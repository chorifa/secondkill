package com.chorifa.secondkillproject.controller;

import com.chorifa.secondkillproject.error.BusinessException;
import com.chorifa.secondkillproject.error.EmBusinessError;
import com.chorifa.secondkillproject.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BaseController {

    static final String CONTENT_TYPE_FORMED = "application/x-www-form-urlencoded";

    // 定义exceptionhandle 解决没有被contrller层解决的异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody // 加上json序列化
    public CommonReturnType handlerException(HttpServletRequest request, Exception ex){
        Map<String,Object> responseData = new HashMap<>();
        if(ex instanceof BusinessException){
                BusinessException businessException = (BusinessException)ex;
                responseData.put("errorCode",businessException.getErrorCode());
                responseData.put("errorMsg",businessException.getErrorMsg());
        }else {
                responseData.put("errorCode", EmBusinessError.UNKNOWN_ERROR.getErrorCode());
                responseData.put("errorMsg", EmBusinessError.UNKNOWN_ERROR.getErrorMsg());
        }
        return CommonReturnType.create(responseData,"fail");
    }
}
