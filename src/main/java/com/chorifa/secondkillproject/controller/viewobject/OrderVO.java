package com.chorifa.secondkillproject.controller.viewobject;

import java.math.BigDecimal;

public class OrderVO {

    private String id;

    private Integer userId;

    private Integer itemId;

    // 下单时商品单价
    private BigDecimal itemPrice;

    // 购买数量
    private Integer amount;

    // 订单金额
    private BigDecimal orderPrice;

    public String getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public Integer getAmount() {
        return amount;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }
}
