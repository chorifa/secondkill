package com.chorifa.secondkillproject.service.impl;

import com.chorifa.secondkillproject.controller.viewobject.UserVO;
import com.chorifa.secondkillproject.dao.UserDataObjectMapper;
import com.chorifa.secondkillproject.dao.UserPasswordObjectMapper;
import com.chorifa.secondkillproject.dataobject.UserDataObject;
import com.chorifa.secondkillproject.dataobject.UserPasswordObject;
import com.chorifa.secondkillproject.error.BusinessException;
import com.chorifa.secondkillproject.error.EmBusinessError;
import com.chorifa.secondkillproject.service.UserService;
import com.chorifa.secondkillproject.service.model.UserModel;
import com.chorifa.secondkillproject.validator.ValidationResult;
import com.chorifa.secondkillproject.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDataObjectMapper userDataObjectMapper;

    @Autowired
    private UserPasswordObjectMapper userPasswordObjectMapper;

    @Autowired
    private ValidatorImpl validator;
    /**
     * service层不能简单的把DataObject透传给前端，DataObject是和数据库一一映射的
     * 真正操作的模型是自己定义的userModel
     * @param id
     */
    @Override
    public UserModel getUserById(Integer id) {
        UserDataObject userDataObject = userDataObjectMapper.selectByPrimaryKey(id);
        if(userDataObject == null)
            return null;
        // 通过用户id获取对应的用户加密密码
        UserPasswordObject userPasswordObject = userPasswordObjectMapper.selectByUserId(id);
        return convertFromDataObject(userDataObject,userPasswordObject);
    }

    /**
     * 实现用户模型的注册，注意insertSelective函数的使用
     * @param userModel
     * @throws BusinessException
     */
    @Override
    @Transactional // 事务包装，两个数据库插入操作有一个失败就全部失败
    public UserModel register(UserModel userModel) throws BusinessException {
        if(userModel == null)
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户模型为空");
//        if(StringUtils.isAnyEmpty(userModel.getName(), userModel.getPhone())
//            || userModel.getAge() == null || userModel.getGender() == null)
//            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "用户模型部分字段为空");

        // 使用validator进行验证
        ValidationResult validationResult = validator.validate(userModel);
        if(validationResult.isHasError())
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, validationResult.getErrorMsg());

        // 构造UserDataObject
        UserDataObject userDataObject = convertUserDataFromUserModel(userModel);
        // insertSelective函数使用数据库的默认值进行字段赋值。也就是如果某个传入字段为null，那么就不会用null覆盖数据库的字段
        // insert就不管这些，直接覆盖。 在更新中insertSelective非常有用。
        // null对于数据库或者前端而言尽量避免，在数据库中也应当尽量设置不能为null限定
        try{
            userDataObjectMapper.insertSelective(userDataObject);
        }catch (DuplicateKeyException dke){ // 在数据库中将phone字段设为unique索引的，所以可能抛出重复键值异常
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"该手机号已被注册");
        }
        userModel.setId(userDataObject.getId());
        // 构造UserPasswordObject
        UserPasswordObject userPasswordObject = convertUserPasswordFromUserModel(userModel);
        userPasswordObjectMapper.insertSelective(userPasswordObject);

        return userModel;
    }

    /**
     * 通过手机号获得对应UserDataObject，得到ID获取UserPasswordObject。并比较传入的密码是否正确
     * @param phone
     * @param encrptPassword // 加密的密码
     * @return userModel
     * @throws BusinessException
     */
    @Override
    public UserModel validateLogin(String phone, String encrptPassword) throws BusinessException {
        // 通过用户手机获取用户信息
        UserDataObject userDataObject = userDataObjectMapper.selectByPhone(phone);
        if(userDataObject == null)
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        UserPasswordObject userPasswordObject = userPasswordObjectMapper.selectByUserId(userDataObject.getId());
        UserModel userModel = convertFromDataObject(userDataObject, userPasswordObject);
        userDataObject = null; userPasswordObject = null;
        // 比对用户信息中的密码和登录密码是否一致
        if(!com.alibaba.druid.util.StringUtils.equals(encrptPassword,userModel.getEncrptPassword()))
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        return userModel;
    }

    @Override
    // 将核心用户模型转换为前端可显示的viewObject模型
    public UserVO createVO(UserModel userModel) {
        if(userModel == null) return null;
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }

    private UserPasswordObject convertUserPasswordFromUserModel(UserModel userModel){
        if(userModel == null)
            return null;
        UserPasswordObject userPasswordObject = new UserPasswordObject();
        userPasswordObject.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordObject.setUserId(userModel.getId());
        return userPasswordObject;
    }

    private UserDataObject convertUserDataFromUserModel(UserModel userModel){
        if(userModel == null)
            return null;
        UserDataObject userDataObject = new UserDataObject();
        BeanUtils.copyProperties(userModel,userDataObject);
        return userDataObject;
    }

    private UserModel convertFromDataObject(UserDataObject userDataObject, UserPasswordObject userPasswordObject){
        if(userDataObject == null) return null;
        UserModel userModel = new UserModel();
        // 拷贝全部属性(字段名和类型完全一致)
        BeanUtils.copyProperties(userDataObject,userModel);
        // 有重复属性，不能拷贝
        if(userPasswordObject != null)
            userModel.setEncrptPassword(userPasswordObject.getEncrptPassword());

        return userModel;
    }
}
