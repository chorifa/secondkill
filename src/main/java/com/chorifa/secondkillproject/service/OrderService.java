package com.chorifa.secondkillproject.service;

import com.chorifa.secondkillproject.controller.viewobject.OrderVO;
import com.chorifa.secondkillproject.error.BusinessException;
import com.chorifa.secondkillproject.service.model.OrderModel;
import com.chorifa.secondkillproject.service.model.SecondkillMessageModel;

public interface OrderService {

    String ORDER_INFO_KEY = "Order.info:";

    // 前端需要传送promoId用来校验
    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException;

    void createOrderPre(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException;

    OrderModel createOrderPos(SecondkillMessageModel messageModel) throws BusinessException;

    Object getResult(Integer userId, Integer itemId);

    OrderVO createVO(OrderModel orderModel);
}
