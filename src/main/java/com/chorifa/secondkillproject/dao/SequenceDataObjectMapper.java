package com.chorifa.secondkillproject.dao;

import com.chorifa.secondkillproject.dataobject.SequenceDataObject;

public interface SequenceDataObjectMapper {
    int deleteByPrimaryKey(String name);

    int insert(SequenceDataObject record);

    int insertSelective(SequenceDataObject record);

    SequenceDataObject selectByPrimaryKey(String name);

    // 自己添加的方法，和selectByPrimaryKey相比多了加锁
    SequenceDataObject getSequenceByName(String name);

    int updateByPrimaryKeySelective(SequenceDataObject record);

    int updateByPrimaryKey(SequenceDataObject record);
}