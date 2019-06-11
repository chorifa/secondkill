package com.chorifa.secondkillproject.validator;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ValidationResult {
    // 校验结果是否有错
    private boolean hasError = false;

    // 存放错误信息的map
    private Map<String,String> errorMsgMap = new HashMap<>();

    // 实现通用的通过格式化字符串获取错误结果的msg方法
    public  String getErrorMsg(){
        return StringUtils.join(errorMsgMap.values().toArray(),',');
    }

    public boolean isHasError() {
        return hasError;
    }

    void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    Map<String, String> getErrorMsgMap() {
        return errorMsgMap;
    }

    public void setErrorMsgMap(Map<String, String> errorMsgMap) {
        this.errorMsgMap = errorMsgMap;
    }
}
