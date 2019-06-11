package com.chorifa.secondkillproject.error;

public enum EmBusinessError implements CommonError {
    // 通用错误类型，一类错误，通过setErrMsg修改具体的错误信息
    PARAMETER_VALIDATION_ERROR(10001, "参数不合法"),
    UNKNOWN_ERROR(10002,"未知错误"),
    // 特定错误
    USER_NOT_EXIST(20001, "用户不存在"),
    USER_LOGIN_FAIL(20002,"用户登录失败，用户名或密码不正确"),
    USER_NOT_LOGIN(20003,"用户尚未登录"),

    ITEM_NOT_EXIST(30001,"商品不存在"),
    STOCK_NOT_ENOUGH(30002,"库存不足");

    private int errorCode;
    private String errorMsg;

    private EmBusinessError(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

    @Override
    public CommonError setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }
}