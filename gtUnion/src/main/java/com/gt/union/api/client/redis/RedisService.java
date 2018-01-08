package com.gt.union.api.client.redis;

/**
 * 公共redis服务api
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0022.
 */
public interface RedisService {

	/**
	 * 设置公共redis键值
	 * @param redisKey		redis键
	 * @param redisValue	redis值
	 * @param second		设置时效(单位：秒)
	 * @return 1：成功 0：失败
	 */
	int setValue(String redisKey, String redisValue, Integer second);

	/**
	 * 获取公共redis值
	 * @param redisKey	redis键
	 * @return
	 */
	String getValue(String redisKey);
}
