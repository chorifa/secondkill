package com.chorifa.secondkillproject.service.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {

    public static final String SECKILL_MSG_QUEUE = "seckill.queue";

//    public static final String QUEUE = "queue";
//
//    public static final String TOPIC_QUEUE1 = "topic_queue1";
//
//    public static final String TOPIC_QUEUE2 = "topic_queue2";
//
//    public static final String TOPIC_EXCHANGE = "topic_exchange";
//
//    public static final String TOPIC_KEY1 = "topic.key1";
//
//    public static final String TOPIC_KEY2 = "topic.#";
//
//    public static final String FANOUT_EXCHANGE = "fanout_exchange";
//
//    public static final String HEADERS_EXCHANGE = "headers_exchange";
//
//    public static final String HEADERS_QUEUE1 = "headers_queue1";
//
//    public static final String HEADERS_QUEUE2 = "headers_queue2";
//
//    /**
//     * direct模式
//     * @return
//     */
//    @Bean
//    public Queue queue(){
//        return new Queue(QUEUE,true);
//    }
//
//    /**
//     * Topic模式
//     * @return
//     */
//    @Bean
//    public Queue topicQueue1(){
//        return new Queue(TOPIC_QUEUE1,true);
//    }
//
//    @Bean
//    public Queue topicQueue2(){
//        return new Queue(TOPIC_QUEUE2,true);
//    }
//
//    @Bean
//    public TopicExchange topicExchange(){
//        return new TopicExchange(TOPIC_EXCHANGE);
//    }
//
//    /**
//     * 注意TOPIC中的key支持通配符*和#，分别对应一个词和多个(0)个词，词和词之间用.间隔
//     * 如topic.other.key可以用topic.#来匹配，topic_other_key则不能用topic_#来匹配
//     * @return
//     */
//    @Bean
//    public Binding bindTopic1(){
//        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(TOPIC_KEY1);
//    }
//
//    @Bean
//    public Binding bindTopic2(){
//        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(TOPIC_KEY2);
//    }
//
//    /**
//     * Fanout 模式
//     */
//    @Bean
//    public FanoutExchange fanoutExchange(){
//        return new FanoutExchange(FANOUT_EXCHANGE);
//    }
//
//    @Bean
//    public Binding bindFanout1(){
//        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
//    }
//
//    @Bean
//    public Binding bindFanout2(){
//        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
//    }
//
//    /**
//     * Headers 模式
//     */
//    @Bean
//    public HeadersExchange headersExchange(){
//        return new HeadersExchange(HEADERS_EXCHANGE);
//    }
//
//    @Bean
//    public Queue headersQueue1(){
//        return new Queue(HEADERS_QUEUE1,true);
//    }
//
//    @Bean
//    public Queue headersQueue2(){
//        return new Queue(HEADERS_QUEUE2,true);
//    }
//
//    @Bean
//    public Binding bindHeaders1(){
//        Map<String,Object> map = new HashMap<>();
//        map.put("header1","value1");
//        map.put("header2","value2");
//        return BindingBuilder.bind(headersQueue1()).to(headersExchange()).whereAll(map).match();
//    }
//
//    @Bean
//    public Binding bindHeaders2(){
//        Map<String,Object> map = new HashMap<>();
//        map.put("header1","value1");
//        map.put("header2","value2");
//        map.put("header3","value3");
//        return BindingBuilder.bind(headersQueue2()).to(headersExchange()).whereAny(map).match();
//    }

    @Bean
    public Queue seckillMsgQueue(){
        return new Queue(SECKILL_MSG_QUEUE,true);
    }
}
