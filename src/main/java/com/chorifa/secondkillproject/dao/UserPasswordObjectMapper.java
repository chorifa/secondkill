package com.chorifa.secondkillproject.dao;

import com.chorifa.secondkillproject.dataobject.UserPasswordObject;

public interface UserPasswordObjectMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserPasswordObject record);

    int insertSelective(UserPasswordObject record);

    UserPasswordObject selectByPrimaryKey(Integer id);

    //自己添加的
    UserPasswordObject selectByUserId(Integer userId);

    int updateByPrimaryKeySelective(UserPasswordObject record);

    int updateByPrimaryKey(UserPasswordObject record);
}