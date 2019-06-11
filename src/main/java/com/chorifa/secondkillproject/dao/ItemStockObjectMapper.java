package com.chorifa.secondkillproject.dao;

import com.chorifa.secondkillproject.dataobject.ItemStockObject;
import org.apache.ibatis.annotations.Param;

public interface ItemStockObjectMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ItemStockObject record);

    int insertSelective(ItemStockObject record);

    ItemStockObject selectByPrimaryKey(Integer id);

    ItemStockObject selectByItemId(Integer itemId);

    int updateByPrimaryKeySelective(ItemStockObject record);

    // 自己添加的
    int decreaseStock(@Param("itemId") Integer itemId, @Param("amount") Integer amount);

    int updateByPrimaryKey(ItemStockObject record);
}