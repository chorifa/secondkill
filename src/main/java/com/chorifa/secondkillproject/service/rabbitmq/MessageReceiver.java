package com.chorifa.secondkillproject.service.rabbitmq;

import com.chorifa.secondkillproject.error.BusinessException;
import com.chorifa.secondkillproject.service.OrderService;
import com.chorifa.secondkillproject.service.model.OrderModel;
import com.chorifa.secondkillproject.service.model.SecondkillMessageModel;
import com.chorifa.secondkillproject.service.redis.RedisService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MessageReceiver {

    @Autowired
    private RedisService redisService;

    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = MQConfig.SECKILL_MSG_QUEUE)
    public void receive(String message) throws BusinessException {
        //System.out.println("消息接收者接收的信息: "+message);
        // recover
        SecondkillMessageModel secondkillMessageModel = redisService.stringToObject(message, SecondkillMessageModel.class);
        // 下单
        OrderModel orderModel = orderService.createOrderPos(secondkillMessageModel);
        // redis里暂存
        String pattern = orderService.ORDER_INFO_KEY+orderModel.getUserId()+"."+orderModel.getItemId();
        Set<String> keySet = redisService.getKeySet(pattern+"*");
        redisService.put(pattern+"."+ (keySet==null?0:keySet.size()),orderModel);
    }

//    /**
//     * Direct 模式
//     * @param message
//     */
//    @RabbitListener(queues = MQConfig.QUEUE)
//    public void receive(String message){
//        System.out.println("消息接收者接收的信息: "+message);
//    }
//
//    /**
//     * Topic/Fanout 模式
//     */
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
//    public void receiveTopic1(String message){
//        System.out.println("Topic队列1收到的消息: "+message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
//    public void receiveTopic2(String message){
//        System.out.println("Topic队列2收到的消息: "+message);
//    }
//
//    /**
//     * Headers 模式
//     */
//    @RabbitListener(queues = MQConfig.HEADERS_QUEUE1)
//    public void receiveHeader1(byte[] bytes){
//        System.out.println("Headers队列1接收到的消息: " + new String(bytes));
//    }
//
//    @RabbitListener(queues = MQConfig.HEADERS_QUEUE2)
//    public void receiveHeader2(byte[] bytes){
//        System.out.println("Headers队列2接收到的消息: " + new String(bytes));
//    }
}
