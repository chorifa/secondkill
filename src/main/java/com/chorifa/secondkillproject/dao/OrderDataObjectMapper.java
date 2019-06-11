package com.chorifa.secondkillproject.dao;

import com.chorifa.secondkillproject.dataobject.OrderDataObject;

public interface OrderDataObjectMapper {
    int deleteByPrimaryKey(String id);

    int insert(OrderDataObject record);

    int insertSelective(OrderDataObject record);

    OrderDataObject selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OrderDataObject record);

    int updateByPrimaryKey(OrderDataObject record);
}