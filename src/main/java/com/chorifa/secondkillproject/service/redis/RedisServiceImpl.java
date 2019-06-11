package com.chorifa.secondkillproject.service.redis;


import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private JedisPool jedisPool;

    public <T> T get(String key, Class<T> clazz){
        try(Jedis jedis = jedisPool.getResource()){
            String value = jedis.get(key);
            T obj = null;
            if(value != null && value.length() != 0){
                obj = stringToObject(value, clazz);
            }
            return obj;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public <T> boolean put(String key, T value){
        try(Jedis jedis = jedisPool.getResource()){
            if(value != null) {
                String str = objectToString(value);
                jedis.set(key, str);
                return true;
            }else return false;
        }
    }

    public <T> boolean put(String key, T value, int timeout){
        try(Jedis jedis = jedisPool.getResource()){
            if(value != null) {
                String str = objectToString(value);
                jedis.setex(key, timeout ,str);
                return true;
            }else return false;
        }
    }

    public Long incr(String key){
        try(Jedis jedis = jedisPool.getResource()){
            if(key != null) {
                return jedis.incr(key);
            }else return -100L;
        }
    }

    public Long incr(String key, long step){
        try(Jedis jedis = jedisPool.getResource()){
            if(key != null) {
                return jedis.incrBy(key,step);
            }else return -100L;
        }
    }

    public Long decr(String key){
        try(Jedis jedis = jedisPool.getResource()){
            if(key != null) {
                return jedis.decr(key);
            }else return -100L;
        }
    }

    public Long decr(String key, long step){
        try(Jedis jedis = jedisPool.getResource()){
            if(key != null) {
                return jedis.decrBy(key,step);
            }else return -100L;
        }
    }

    public Set<String> getKeySet(String pattern){
        try(Jedis jedis = jedisPool.getResource()){
            return jedis.keys(pattern);
        }
    }

    public boolean exists(String key){
        try(Jedis jedis = jedisPool.getResource()){
            if(key == null) return false;
            else return jedis.exists(key);
        }
    }

    public <T> String objectToString(T value) {
        if(value == null) return null;
        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz == Integer.class){
            return String.valueOf(value);
        }else if(clazz == String.class){
            return (String)value;
        }else if(clazz == long.class || clazz == Long.class){
            return String.valueOf(value);
        }else{
            return JSON.toJSONString(value);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T stringToObject(String value, Class<T> clazz) {
        if(value == null || value.length() == 0) return null;
        if(clazz == int.class || clazz == Integer.class){
            return (T)Integer.valueOf(value);
        }else if(clazz == String.class){
            return (T)value;
        }else if(clazz == long.class || clazz == Long.class){
            return (T)Long.valueOf(value);
        }else{
            return JSON.toJavaObject(JSON.parseObject(value),clazz);
        }
    }

}
