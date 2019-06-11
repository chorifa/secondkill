package com.chorifa.secondkillproject.error;

public interface CommonError {

    public int getErrorCode();

    public String getErrorMsg();

    public CommonError setErrorMsg(String errorMsg);

}
