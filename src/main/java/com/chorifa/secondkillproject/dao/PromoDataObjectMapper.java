package com.chorifa.secondkillproject.dao;

import com.chorifa.secondkillproject.dataobject.PromoDataObject;

public interface PromoDataObjectMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PromoDataObject record);

    int insertSelective(PromoDataObject record);

    PromoDataObject selectByPrimaryKey(Integer id);

    PromoDataObject selectByItemId(Integer itemId);

    int updateByPrimaryKeySelective(PromoDataObject record);

    int updateByPrimaryKey(PromoDataObject record);
}