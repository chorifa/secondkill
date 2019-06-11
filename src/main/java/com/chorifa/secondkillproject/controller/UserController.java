package com.chorifa.secondkillproject.controller;

import com.chorifa.secondkillproject.controller.viewobject.UserVO;
import com.chorifa.secondkillproject.error.BusinessException;
import com.chorifa.secondkillproject.error.EmBusinessError;
import com.chorifa.secondkillproject.response.CommonReturnType;
import com.chorifa.secondkillproject.service.UserService;
import com.chorifa.secondkillproject.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@RestController("user")
@RequestMapping("/user")
// DEFAULT_ALLOWED_HEADERS:允许跨域传输所有的header参数，将用于使用token放入header域做session共享的跨域请求
// DEFAULT_ALLOW_CREDENTIALS = true:需配合前端设置xhrFields授信后使得跨域session共享
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*") // 本地文件(前端) -> localhost的跨域请求
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    /**
     * 虽然Bean是单例的，但是这里HttpServletRequest的本质是一个proxy，内部拥有Tread Local的Map
     * 所以这个HttpServletRequest允许用户在自己的Thread中处理自己的Response
     */
    @Autowired
    private HttpServletRequest httpServletRequest;

    @PostMapping(value = "/login", consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType login(@RequestParam(name = "phone") String phone,
                                  @RequestParam(value = "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // 入参校验
        if(StringUtils.isAnyEmpty(phone,password))
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号或密码为空");
        // 用户登录校验，检验密码是否正确
        UserModel userModel = userService.validateLogin(phone, EncodeByMD5(password));
        // 将登录凭证加入到用户登录成功的session(单点，非分布式)
        httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel.getId());
        return CommonReturnType.create(userService.createVO(userModel));
    }


    /**
     * 用户注册，检查参数，并调用UserServiceImpl的register函数
     * @param phone
     * @param otpCode
     * @param name
     * @param gender
     * @param age
     * @return
     */
    @PostMapping(value = "/register", consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType register(@RequestParam(name="phone") String phone,
                                     @RequestParam(name="otpCode") String otpCode,
                                     @RequestParam(name="name") String name,
                                     @RequestParam(name="gender") Byte gender,
                                     @RequestParam(name="age") Integer age,
                                     @RequestParam(name="password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // 检查optCode和手机号是否一致
        String waitedCheckOptCode = (String)this.httpServletRequest.getSession().getAttribute(phone);
            // 这个包下的equals方法内部已经进行了判空处理
        if(!com.alibaba.druid.util.StringUtils.equals(otpCode,waitedCheckOptCode))
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码无效");
        // 进行注册业务
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(gender);
        userModel.setAge(age);
        userModel.setPhone(phone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(EncodeByMD5(password));
        // 注意phone实际需要是唯一的，这里数据库user_info表中将phone字段设为unique索引，重复insert会抛出异常
        userModel = userService.register(userModel);
        return CommonReturnType.create(userService.createVO(userModel));
    }

    /**
     * 获取OTP短信验证码，并与手机号绑定
     * @param phone
     * @return
     */
    @PostMapping(value = "/getotp", consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType getOtp(@RequestParam(value = "phone") String phone){
        // 生成6位随机验证码
        Random random = new Random();
        int randomInt = 100000 + random.nextInt(900000); // bound is exclusive
        String otpCode = String.valueOf(randomInt);

        // 绑定手机，应当采用redis方式，这里将OTP与phone设置为session属性对
        httpServletRequest.getSession().setAttribute(phone,otpCode);

        // http pos 给用户手机 省略。在控制台输出，但实际不能让该敏感信息被公司后台获取
        System.out.println("phone: " + phone + "  OTPCode: " + otpCode);

        return CommonReturnType.create(null);
    }

    private String EncodeByMD5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // 确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        // 加密
        String newStr = base64Encoder.encode(md5.digest(str.getBytes(StandardCharsets.UTF_8)));
        return newStr;
    }

    /**
     * 调用service服务获取对应id的用户并返回给前端
     * @param id
     */
    @GetMapping("/get")
    public CommonReturnType getUser(@RequestParam(value = "id") Integer id) throws Exception {
        UserModel userModel = userService.getUserById(id);
        // 不存在就throw Exception 会被BaseController中的exception handler捕获
        if(userModel == null)
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        UserVO userVO = userService.createVO(userModel);
        return CommonReturnType.create(userVO);
    }

}
