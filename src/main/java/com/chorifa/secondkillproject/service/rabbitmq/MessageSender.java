package com.chorifa.secondkillproject.service.rabbitmq;

import com.chorifa.secondkillproject.service.redis.RedisService;
import org.springframework.amqp.core.AmqpTemplate;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageSender {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private RedisService redisService;

    public void sendSeckillMsg(Object message){
        String strMsg = redisService.objectToString(message);
        amqpTemplate.convertAndSend(MQConfig.SECKILL_MSG_QUEUE,strMsg);
    }

//    public void send(Object message){
//        // 这里的Object只支持String或byte[]或者实现了Serializable方法的类
//        String strMsg = redisService.objectToString(message);
//        amqpTemplate.convertAndSend(MQConfig.QUEUE,strMsg);
//    }
//
//    public void sendTopic(Object message){
//        String strMsg = redisService.objectToString(message);
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key1",strMsg+" with key1");
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.other.key",strMsg+" with other key");
//    }
//
//    /**
//     * 注意Fannout下convertAndSend仍然需要给出key参数，只是没有用
//     * @param message
//     */
//    public void sendFanout(Object message){
//        String strMsg = redisService.objectToString(message);
//        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",strMsg);
//    }
//
//    public void sendHeaders(Object message){
//        String strMsg = redisService.objectToString(message);
//        MessageProperties properties = new MessageProperties();
//        properties.setHeader("header1","value1");
//        properties.setHeader("header2","value2");
//        Message obj = new Message(strMsg.getBytes(),properties);
//        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE,"",obj);
//    }

}
