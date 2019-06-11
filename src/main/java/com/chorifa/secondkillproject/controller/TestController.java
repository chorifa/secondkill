package com.chorifa.secondkillproject.controller;

import com.chorifa.secondkillproject.response.CommonReturnType;
import com.chorifa.secondkillproject.service.redis.RedisService;
import com.chorifa.secondkillproject.service.rabbitmq.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("test")
@RequestMapping("/test")
public class TestController extends BaseController{
    /*
    @Autowired
    private RedisService redisService;

    @Autowired
    private MessageSender messageSender;

    @GetMapping(value = "/sendHeaders")
    public CommonReturnType sendHeaders(){
        messageSender.sendHeaders("this is a test for RabbitMQ in Headers mode");
        return CommonReturnType.create(null);
    }

    @GetMapping(value = "/sendFanout")
    public CommonReturnType sendFanout(){
        messageSender.sendFanout("this is a test for RabbitMQ in fanout mode");
        return CommonReturnType.create(null);
    }

    @GetMapping(value = "/sendTopic")
    public CommonReturnType sendTopic(){
        messageSender.sendTopic("this is a test for RabbitMQ in topic mode");
        return CommonReturnType.create(null);
    }

    @GetMapping(value = "/send")
    public CommonReturnType send(){
        messageSender.send("this is a test for RabbitMQ");
        return CommonReturnType.create(null);
    }


    @GetMapping(value = "/get")
    public CommonReturnType get(){
        String value = redisService.get("key1", String.class);
        return CommonReturnType.create(value);
    }

    @GetMapping(value = "put")
    public CommonReturnType put(){
        redisService.put("key1", "123456");
        System.out.println(redisService.get("key1", String.class));
        return CommonReturnType.create(null);
    }
    */


}
