package com.chorifa.secondkillproject.dao;

import com.chorifa.secondkillproject.dataobject.ItemDataObject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemDataObjectMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ItemDataObject record);

    int insertSelective(ItemDataObject record);

    ItemDataObject selectByPrimaryKey(Integer id);

    // 自己添加的
    List<ItemDataObject> listItem();

    // 自己添加的
    int increaseSales(@Param("id") Integer id, @Param("amount") Integer amount);

    int updateByPrimaryKeySelective(ItemDataObject record);

    int updateByPrimaryKey(ItemDataObject record);
}