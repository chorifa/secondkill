package com.chorifa.secondkillproject.service.redis;

import java.util.Set;

public interface RedisService {

    public <T> T get(String key, Class<T> clazz);

    public <T> boolean put(String key, T value);

    public <T> boolean put(String key, T value, int timeout);

    public Long incr(String key);

    public Long incr(String key, long step);

    public Long decr(String key);

    public Long decr(String key, long step);

    public Set<String> getKeySet(String pattern);

    public boolean exists(String key);

    public <T> String objectToString(T value);

    public <T> T stringToObject(String value, Class<T> clazz);

}
