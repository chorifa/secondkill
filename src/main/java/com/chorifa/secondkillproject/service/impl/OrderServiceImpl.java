package com.chorifa.secondkillproject.service.impl;

import com.chorifa.secondkillproject.controller.viewobject.OrderVO;
import com.chorifa.secondkillproject.dao.OrderDataObjectMapper;
//import com.chorifa.secondkillproject.dao.SequenceDataObjectMapper;
import com.chorifa.secondkillproject.dataobject.OrderDataObject;
//import com.chorifa.secondkillproject.dataobject.SequenceDataObject;
import com.chorifa.secondkillproject.error.BusinessException;
import com.chorifa.secondkillproject.error.EmBusinessError;
import com.chorifa.secondkillproject.service.ItemService;
import com.chorifa.secondkillproject.service.OrderService;
import com.chorifa.secondkillproject.service.SequenceService;
import com.chorifa.secondkillproject.service.UserService;
import com.chorifa.secondkillproject.service.model.*;
import com.chorifa.secondkillproject.service.rabbitmq.MessageSender;
import com.chorifa.secondkillproject.service.redis.RedisService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDataObjectMapper orderDataObjectMapper;

//    @Autowired
//    private SequenceDataObjectMapper sequenceDataObjectMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private SequenceService sequenceService;

    @Override
    public void createOrderPre(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException{
        ItemCacheModel itemCacheModel = itemService.getItemByIdInCache(itemId);
        if(itemCacheModel == null)
            throw new BusinessException(EmBusinessError.ITEM_NOT_EXIST);
        // TODO 检查userId合法性
        if(promoId != null){
            // 非空，比较是否同一个活动
            if(!promoId.equals(itemCacheModel.getPromoId()))
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"不存在该活动");
                // 活动是否在进行中
            else if(itemCacheModel.getStatus() != 2)
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动不在进行中");
        }

        // 减库存和下单
        // 返回减完之后的数值
        // 空了
        if(itemService.itemEmpty.get(itemId))
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        Long stock = redisService.decr(itemService.ITEM_STOCK_KEY+itemCacheModel.getId());
        if(stock < 0) {
            itemService.itemEmpty.put(itemId,true);
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }
        //TODO 判断重复秒杀

        // 发送消息
        SecondkillMessageModel secondkillMessageModel = new SecondkillMessageModel();
        secondkillMessageModel.setItemId(itemCacheModel.getId());
        secondkillMessageModel.setUserId(userId);
        secondkillMessageModel.setAmount(amount);
        secondkillMessageModel.setPromoId(promoId);
        secondkillMessageModel.setPrice(itemCacheModel.getPrice());
        messageSender.sendSeckillMsg(secondkillMessageModel);
    }

    @Override
    @Transactional
    public OrderModel createOrderPos(SecondkillMessageModel messageModel) throws BusinessException {
        // 获取自增订单号
        String orderId = sequenceService.generateOrderIdWithRedis();

        // 生成模型
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(messageModel.getUserId());
        orderModel.setItemId(messageModel.getItemId());
        orderModel.setAmount(messageModel.getAmount());
        orderModel.setPromoId(messageModel.getPromoId());
        orderModel.setItemPrice(messageModel.getPrice());
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(BigDecimal.valueOf(messageModel.getAmount())));
        orderModel.setId(orderId);
        OrderDataObject orderDataObject = convertOrderDataFromOrderModel(orderModel);

        // 减库存
        boolean isDecreased = itemService.decreaseStock(messageModel.getItemId(),messageModel.getAmount());
        if(!isDecreased) {
            // redis标记
            redisService.put(itemService.ITEM_FLAG_KEY+messageModel.getItemId(),0);
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }
        // 下单
        orderDataObjectMapper.insertSelective(orderDataObject);
        // 增加销量
        itemService.increaseSales(messageModel.getItemId(),messageModel.getAmount());
        // 返回Controller
        return orderModel;
    }

    @Override
    public Object getResult(Integer userId, Integer itemId) {
        // 查redis
        String pattern = ORDER_INFO_KEY+userId+"."+itemId;
        // 查key
        Set<String> keySet = redisService.getKeySet(pattern+"*");
        if(keySet == null || keySet.size() == 0){
            // 拉去标志
            if(redisService.exists(itemService.ITEM_FLAG_KEY+itemId) && 0==redisService.get(itemService.ITEM_FLAG_KEY+itemId,Integer.class))
                return "已经售罄";
            else return "排队处理中";
        }else{
            // 结果放在list中
            List<OrderModel> orderList = new ArrayList<>(keySet.size());
            keySet.forEach(x->orderList.add(redisService.get(x,OrderModel.class)));
            return orderList;
        }
    }

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException {
        // 校验状态：用户是否存在，商品是否存在，数量是否合法,活动是否合法
        // 在getItemById方法中创建itemModel时就已经判断了服务器时间活动状态
        ItemModel itemModel = itemService.getItemById(itemId);
        if(itemModel == null)
            throw new BusinessException(EmBusinessError.ITEM_NOT_EXIST);

        UserModel userModel = userService.getUserById(userId);
        if(userModel == null)
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);

        if(amount <=0 || amount >99)
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"购买数量过大");

        if(promoId != null){
            // 非空，比较是否同一个活动
            if(!promoId.equals(itemModel.getPromoModel().getId()))
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"不存在该活动");
            // 活动是否在进行中
            else if(itemModel.getPromoModel().getStatus() != 2)
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动不在进行中");
        }

        // 减库存的两种方式：下单减库存和支付减库存
        // 下单减库存是指，当提交订单时查询是否有剩余可买的商品，如果有就将商品“加锁”(数量减一)，并完成下单，再去支付
        // 支付减库存是指，当提交订单时仅仅查询是否有剩余可买的商品，不对其加锁，直到支付完成才减库存。
        // 下单减库存能确保下单后一定能获得商品。支付减库存在并发下存在超卖商品风险
        // 某些商家库存120件，表明的是100件，采用下单减库存可以承受20件的超卖风险，同时能营造出 火爆和售罄的紧张感
        // 采用下单减库存方式
        boolean isDecreased = itemService.decreaseStock(itemId,amount);
        if(!isDecreased)
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);

        // 订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        orderModel.setPromoId(promoId);
        if(promoId == null)
            orderModel.setItemPrice(itemModel.getPrice());
        else orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(BigDecimal.valueOf(amount)));
        // 生成交易流水号
        orderModel.setId(sequenceService.generateOrderId());

        OrderDataObject orderDataObject = convertOrderDataFromOrderModel(orderModel);
        orderDataObjectMapper.insertSelective(orderDataObject);
        // 销量增加，暂时不考虑支付金额的情况
        itemService.increaseSales(itemId,amount);
        // 返回Controller
        return orderModel;
    }

    @Override
    public OrderVO createVO(OrderModel orderModel) {
        if(orderModel == null) return null;
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orderModel,orderVO);
        return orderVO;
    }

    private OrderDataObject convertOrderDataFromOrderModel(OrderModel orderModel){
        if(orderModel == null)
            return null;
        OrderDataObject orderDataObject = new OrderDataObject();
        BeanUtils.copyProperties(orderModel,orderDataObject);
        return orderDataObject;
    }
}
