package com.chorifa.secondkillproject.service;

import com.chorifa.secondkillproject.controller.viewobject.UserVO;
import com.chorifa.secondkillproject.error.BusinessException;
import com.chorifa.secondkillproject.service.model.UserModel;

public interface UserService {

    UserModel getUserById(Integer id);

    UserModel register(UserModel userModel) throws BusinessException;

    /**
     * 验证加密后的密码
     * @param phone
     * @param encrptPassword // 加密的密码
     * @throws BusinessException
     */
    UserModel validateLogin(String phone, String encrptPassword) throws BusinessException;

    UserVO createVO(UserModel userModel);

}
