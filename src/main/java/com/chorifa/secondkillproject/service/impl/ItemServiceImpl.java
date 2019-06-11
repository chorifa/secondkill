package com.chorifa.secondkillproject.service.impl;

import com.chorifa.secondkillproject.controller.viewobject.ItemVO;
import com.chorifa.secondkillproject.dao.ItemDataObjectMapper;
import com.chorifa.secondkillproject.dao.ItemStockObjectMapper;
import com.chorifa.secondkillproject.dataobject.ItemDataObject;
import com.chorifa.secondkillproject.dataobject.ItemStockObject;
import com.chorifa.secondkillproject.error.BusinessException;
import com.chorifa.secondkillproject.error.EmBusinessError;
import com.chorifa.secondkillproject.service.ItemService;
import com.chorifa.secondkillproject.service.PromoService;
import com.chorifa.secondkillproject.service.model.ItemCacheModel;
import com.chorifa.secondkillproject.service.model.ItemModel;
import com.chorifa.secondkillproject.service.model.PromoModel;
import com.chorifa.secondkillproject.service.redis.RedisService;
import com.chorifa.secondkillproject.validator.ValidationResult;
import com.chorifa.secondkillproject.validator.ValidatorImpl;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService, InitializingBean {

    private static ReentrantLock lock = new ReentrantLock();

    @Autowired
    private ItemDataObjectMapper itemDataObjectMapper;

    @Autowired
    private ItemStockObjectMapper itemStockObjectMapper;

    @Autowired
    private PromoService promoService;

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private RedisService redisService;

    @Override
    @Transactional // 注意事务包装
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        // 参数验证
        if(itemModel == null)
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商品模型不能为空");
        ValidationResult validationResult = validator.validate(itemModel);
        if(validationResult.isHasError())
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, validationResult.getErrorMsg());
        validationResult = null;

        // 转换为对应的DO 并入库
        ItemDataObject itemDataObject = convertItemDataFromItemModel(itemModel); // 此时id值尚未赋值
        itemDataObjectMapper.insertSelective(itemDataObject); // 此时id值已被更新赋值

        itemModel.setId(itemDataObject.getId());
        ItemStockObject itemStockObject = convertItemStockFromItemModel(itemModel);
        itemStockObjectMapper.insertSelective(itemStockObject);

        return itemModel;
    }

    @Override
    public List<ItemModel> listItem(){
        List<ItemDataObject> itemDataObjectList = itemDataObjectMapper.listItem();
        // 这里使用stream进行map操作。考虑到原来的list是按照销量排序的，因此这里不用parallelStream
        // 注意有空验证一下parallel stream的map是否是按原先顺序的。
        // 同时，这里的库存查询时通过在流中一个个查询的方式，效率较差。考虑一次性预先取出?
        List<ItemModel> itemModelList = itemDataObjectList.stream().map(itemDataObject -> {
            ItemStockObject itemStockObject = itemStockObjectMapper.selectByItemId(itemDataObject.getId());
            return convertFromDataObject(itemDataObject,itemStockObject);
        }).collect(Collectors.toList());
        return itemModelList;
    }

    @Override
    public List<ItemModel> listItemWithPromo() {
        List<ItemModel> list = listItem();
        if(list != null){
            for(ItemModel itemModel : list){
                PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
                if(promoModel != null && promoModel.getStatus() != 3)
                    itemModel.setPromoModel(promoModel);
            }
            return list;
        }
        return null;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        if(id == null)
            return null;
        ItemDataObject itemDataObject = itemDataObjectMapper.selectByPrimaryKey(id);
        if(itemDataObject == null)
            return null;
        ItemStockObject itemStockObject = itemStockObjectMapper.selectByItemId(id);
        ItemModel itemModel = convertFromDataObject(itemDataObject, itemStockObject);
        // 获取活动信息，里边会根据客户端时间计算活动状态
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
        // 有活动且状态为未开始和进行中，将promoModel内聚于itemModel内
        if(promoModel != null && promoModel.getStatus() != 3)
            itemModel.setPromoModel(promoModel);
        return itemModel;
    }

    @Override
    public ItemCacheModel getItemByIdInCache(Integer id) {
        if(id == null) return null;
        ItemCacheModel itemCacheModel = redisService.get(ITEM_INFO_KEY+id, ItemCacheModel.class);
        if(itemCacheModel == null){ // 缓存里没有,只允许添加一次或者商品不存在
            ItemModel itemModel = getItemById(id);
            if(itemModel != null) { // 数据库里有，缓存里没有
                try {
                    lock.lock();
                    System.out.println("获取Item时发现没有，把Item放在Cache时加了锁");
                    // 再获取一次
                    itemCacheModel = redisService.get(ITEM_INFO_KEY + id, ItemCacheModel.class);
                    if (itemCacheModel == null) { // 第一次
                        redisService.put(ITEM_STOCK_KEY + itemModel.getId(), itemModel.getStock()); // 销量
                        return putItemInCache(itemModel); // Info
                    }
                } finally {
                    if (lock.isHeldByCurrentThread())
                        lock.unlock();
                }
            }
        }
        // 更新status
        if(itemCacheModel != null){
            // 判断活动时间是否合适
            // 没开始
            if(itemCacheModel.getPromoId() == null)
                itemCacheModel.setStatus(-1);
            else if(new DateTime(itemCacheModel.getStartDate()).isAfterNow())
                itemCacheModel.setStatus(1);
                // 已结束
            else if(new DateTime(itemCacheModel.getEndDate()).isBeforeNow())
                itemCacheModel.setStatus(3);
                // 进行中
            else itemCacheModel.setStatus(2);
        }
        return itemCacheModel;
    }

    @Override
    public ItemCacheModel putItemInCache(ItemModel itemModel) {
        ItemCacheModel itemCacheModel = convertCacheFromModel(itemModel);
        if(itemCacheModel != null)
            redisService.put(ITEM_INFO_KEY+itemCacheModel.getId(),itemCacheModel);
        return itemCacheModel;
    }

    @Override
    public ItemVO createVO(ItemModel itemModel){
        if(itemModel == null)
            return null;
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);
        PromoModel promoModel = itemModel.getPromoModel();
        if(promoModel == null)
            itemVO.setPromoStatus(0);
        else{
            itemVO.setPromoStatus(promoModel.getStatus());
            itemVO.setPromoId(promoModel.getId());
            itemVO.setPromoPrice(promoModel.getPromoItemPrice());
            itemVO.setStartDate(promoModel.getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
        }
        return itemVO;
    }

    /**
     * 使用itemStockObjectMapper.decreaseStock语句，一次sql，不用先查询再更新，提升性能
     * @param itemId
     * @param amount
     * @return
     * @throws BusinessException
     */
    @Override
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
        // affectedRow为经过这次减库存操作，数据有变动的行数
        int affectedRow = itemStockObjectMapper.decreaseStock(itemId, amount);
        return affectedRow > 0;
    }

    @Override
    public void increaseSales(Integer itemId, Integer amount) throws BusinessException {
        itemDataObjectMapper.increaseSales(itemId,amount);
    }

    private ItemDataObject convertItemDataFromItemModel(ItemModel itemModel){
        if(itemModel == null)
            return null;
        ItemDataObject itemDataObject = new ItemDataObject();
        BeanUtils.copyProperties(itemModel,itemDataObject);
        return itemDataObject;
    }

    private ItemStockObject convertItemStockFromItemModel(ItemModel itemModel){
        if(itemModel == null)
            return null;
        ItemStockObject itemStockObject = new ItemStockObject();
        itemStockObject.setItemId(itemModel.getId());
        itemStockObject.setStock(itemModel.getStock());
        return itemStockObject;
    }

    private ItemModel convertFromDataObject(ItemDataObject itemDataObject, ItemStockObject itemStockObject){
        if(itemDataObject == null)
            return null;
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDataObject, itemModel);
        if(itemStockObject != null)
            itemModel.setStock(itemStockObject.getStock());
        return itemModel;
    }

    private ItemCacheModel convertCacheFromModel(ItemModel itemModel){
        if(itemModel == null) return null;
        ItemCacheModel itemCacheModel = new ItemCacheModel();
        PromoModel promoModel = itemModel.getPromoModel();
        itemCacheModel.setId(itemModel.getId());
        itemCacheModel.setStock(itemModel.getStock());
        if(promoModel == null){ // 没有活动
            itemCacheModel.setStatus(-1);
            itemCacheModel.setPrice(itemModel.getPrice());
        }else{
            itemCacheModel.setPrice(promoModel.getPromoItemPrice());
            itemCacheModel.setPromoId(promoModel.getId());
            itemCacheModel.setStartDate(promoModel.getStartDate().toDate());
            itemCacheModel.setEndDate(promoModel.getEndDate().toDate());
            itemCacheModel.setStatus(promoModel.getStatus());
        }
        return itemCacheModel;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<ItemModel> list = listItemWithPromo();
        // 将信息和销量放在cache中
        list.forEach(x->{
            putItemInCache(x);
            redisService.put(ITEM_STOCK_KEY+x.getId(),x.getStock());
            itemEmpty.put(x.getId(),false);
        });

    }
}
