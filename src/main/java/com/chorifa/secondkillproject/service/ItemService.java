package com.chorifa.secondkillproject.service;

import com.chorifa.secondkillproject.controller.viewobject.ItemVO;
import com.chorifa.secondkillproject.error.BusinessException;
import com.chorifa.secondkillproject.service.model.ItemCacheModel;
import com.chorifa.secondkillproject.service.model.ItemModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ItemService {

    String ITEM_INFO_KEY = "Item.Info:";

    String ITEM_STOCK_KEY = "Item.stock:";

    String ITEM_FLAG_KEY = "Item.flag:";

    static Map<Integer,Boolean> itemEmpty = new HashMap<>();

    // 创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    // 商品列表浏览
    List<ItemModel> listItem();

    List<ItemModel> listItemWithPromo();

    // 商品详情浏览
    ItemModel getItemById(Integer id);

    // 在redis中获取商品必要详情
    ItemCacheModel getItemByIdInCache(Integer id);

    // 将必要信息放在redis中
    ItemCacheModel putItemInCache(ItemModel itemModel);

    // 将ItemModel转换为ItemVO
    ItemVO createVO(ItemModel itemModel);

    // 减库存操作，提供给OrderService使用
    boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException;

    // 增加销量操作，提供给OrderService使用
    void increaseSales(Integer itemId, Integer amount) throws BusinessException;
}
