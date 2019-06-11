package com.chorifa.secondkillproject.dao;

import com.chorifa.secondkillproject.dataobject.UserDataObject;

public interface UserDataObjectMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserDataObject record);

    int insertSelective(UserDataObject record);

    UserDataObject selectByPrimaryKey(Integer id);

    UserDataObject selectByPhone(String phone);

    int updateByPrimaryKeySelective(UserDataObject record);

    int updateByPrimaryKey(UserDataObject record);
}