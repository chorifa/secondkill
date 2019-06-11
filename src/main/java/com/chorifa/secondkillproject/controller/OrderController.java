package com.chorifa.secondkillproject.controller;

import com.chorifa.secondkillproject.error.BusinessException;
import com.chorifa.secondkillproject.error.EmBusinessError;
import com.chorifa.secondkillproject.response.CommonReturnType;
import com.chorifa.secondkillproject.service.OrderService;
import com.chorifa.secondkillproject.service.model.OrderModel;
import com.chorifa.secondkillproject.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController("order")
@RequestMapping("/order")
// DEFAULT_ALLOWED_HEADERS:允许跨域传输所有的header参数，将用于使用token放入header域做session共享的跨域请求
// DEFAULT_ALLOW_CREDENTIALS = true:需配合前端设置xhrFields授信后使得跨域session共享
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*") // 本地文件(前端) -> localhost的跨域请求
public class OrderController extends BaseController{

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    // 处理创建订单请求
    @PostMapping(value = "/create", consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType create(@RequestParam(name = "itemId") Integer itemId,
                                   @RequestParam(name = "promoId", required = false) Integer promoId,
                                   @RequestParam(name = "amount") Integer amount,
                                   @RequestParam(name = "userId") Integer userId) throws BusinessException {
        // 获取用户信息
        /*
        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if(isLogin == null || !isLogin)
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        Integer userId = (Integer) httpServletRequest.getSession().getAttribute("LOGIN_USER");
        */
        OrderModel orderModel = orderService.createOrder(userId,itemId,promoId,amount);

        return CommonReturnType.create(orderService.createVO(orderModel));
    }

    @PostMapping(value = "/createasy", consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType createAsy(@RequestParam(name = "itemId") Integer itemId,
                                      @RequestParam(name = "promoId", required = false) Integer promoId,
                                      @RequestParam(name = "amount") Integer amount,
                                      @RequestParam(name = "userId") Integer userId) throws BusinessException {
        orderService.createOrderPre(userId,itemId,promoId,amount);
        return CommonReturnType.create("排队中");
    }

    // 查询订单结果
    @GetMapping(value = "/result", consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType getResult(@RequestParam(name = "itemId") Integer itemId,
                                      @RequestParam(name = "userId") Integer userId) throws BusinessException {
        Object result = orderService.getResult(userId,itemId);
        if(result instanceof String){
            return CommonReturnType.create((String)result);
        }else return CommonReturnType.create((List<OrderModel>)result);
    }
}
