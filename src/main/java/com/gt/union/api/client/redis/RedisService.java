package com.gt.union.api.client.redis;

/**
 * Created by Administrator on 2017/9/22 0022.
 */
public interface RedisService {

	public void setValue(String redisKey, String redisValue, Integer second);

	public String getValue(String redisKey);
}
