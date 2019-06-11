package com.chorifa.secondkillproject.validator;

//import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
//import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

@Component
public class ValidatorImpl {//implements InitializingBean {

    // 这里改成直接自动注入
    @Autowired
    private Validator validator;

    /**
     * 实现校验方法并返回校验结果
     * @param bean
     * @return
     */
    public ValidationResult validate(Object bean){
        final ValidationResult validationResult = new ValidationResult();
        // 返回一个set
        Set<ConstraintViolation<Object>> constraintViolationSet = validator.validate(bean);
        // 有错误
        if(constraintViolationSet.size() >0){
            validationResult.setHasError(true);
            constraintViolationSet.forEach(constraintViolation->{
                String errorMsg = constraintViolation.getMessage();
                String propertyName = constraintViolation.getPropertyPath().toString();
                validationResult.getErrorMsgMap().put(propertyName,errorMsg);
            });
        }
        return validationResult;
    }

    // 在初始化Bean之后自动调用该方法，对validator进行实例化
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        // 将hibernate validator通过工厂的初始化方式实例化
//        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
//    }
}
