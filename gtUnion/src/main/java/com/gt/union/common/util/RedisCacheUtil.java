package com.gt.union.common.util;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis缓存工具类
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
@Component
public class RedisCacheUtil {
    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    private String getRedisNamePrefix() {
        return PropertiesUtil.redisNamePrefix();
    }

    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 通过key获取对应的缓存内容
     *
     * @param key 键
     * @return Object 值
     */
    public String get(String key) {
        String result = null;
        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            String tgtKey = this.getRedisNamePrefix() + key;
            result = operations.get(tgtKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //-------------------------------------------------- set ----------------------------------------------------------

    /**
     * 通过key和value设置缓存，默认2小时
     *
     * @param key   键
     * @param value 值
     * @return boolean 是否设置成功
     */
    public boolean set(String key, Object value) {
        boolean result = false;
        try {
            set(key, value, 7200L);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 通过key、value和过期时间设置缓存
     *
     * @param key        键
     * @param value      值
     * @param expireTime 过期时间
     * @return boolean 是否设置成功
     */
    public boolean set(String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            String tgtKey = this.getRedisNamePrefix() + key;
            String tgtValue = JSON.toJSONString(value);
            operations.set(tgtKey, tgtValue);
            redisTemplate.expire(tgtKey, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //------------------------------------------------ remove ---------------------------------------------------------

    /**
     * 通过key移除对应缓存内容
     *
     * @param key 键
     * @return boolean 是否移除成功
     */
    public boolean remove(String key) {
        boolean result = false;
        try {
            String tgtKey = this.getRedisNamePrefix() + key;
            redisTemplate.delete(tgtKey);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 通过key集合，移除对应缓存内容
     *
     * @param keys 键集合
     * @return boolean 是否移除成功
     */
    public boolean remove(List<String> keys) {
        Set<String> tgtKeys = new HashSet<>();
        if (ListUtil.isNotEmpty(keys)) {
            for (String key : keys) {
                String tgtKey = this.getRedisNamePrefix() + key;
                tgtKeys.add(tgtKey);
            }
        }
        return this.remove(tgtKeys);
    }

    /**
     * 私有方法: 根据目标key集合，移除对应缓存对象
     *
     * @param tgtKeys 目标键集合
     * @return boolean 是否移除成功
     */
    private boolean remove(Set<String> tgtKeys) {
        boolean result = false;
        try {
            if (tgtKeys.size() > 0) {
                redisTemplate.delete(tgtKeys);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //------------------------------------------------ exists ---------------------------------------------------------

    /**
     * 根据key，判断是否存在对应的缓存内容
     *
     * @param key 键
     * @return boolean 是否存在
     */
    public boolean exists(String key) {
        boolean result = false;
        try {
            String tgtKey = this.getRedisNamePrefix() + key;
            result = redisTemplate.hasKey(tgtKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
