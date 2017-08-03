package com.gt.union.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis缓存工具类  联盟使用的 所有的key已加 Union:前缀
 *
 * Created by Administrator on 2017/8/2 0002.
 */
@Component
public class RedisCacheUtil {

	@Autowired
	@Qualifier( "redisTemplate" )
	private RedisTemplate redisTemplate;

	private static String redisNamePrefix = PropertiesUtil.redisNamePrefix();

	/**
	 * 批量删除对应的value
	 *
	 * @param keys 数组Key
	 */
	public void remove( final String... keys ) {
		for ( String key : keys ) {
			remove( redisNamePrefix + key );
		}
	}

	/**
	 * 批量删除key
	 *
	 * @param pattern pattern
	 */
	public void removePattern( final String pattern ) {
		Set< Serializable > keys = redisTemplate.keys( pattern );
		if ( keys.size() > 0 ) redisTemplate.delete( redisNamePrefix + keys );
	}

	/**
	 * 删除对应的value
	 *
	 * @param key key
	 */
	public void remove( final String key ) {
		if ( exists( key ) ) {
			redisTemplate.delete( redisNamePrefix +key );
		}
	}

	/**
	 * 判断缓存中是否有对应的value
	 *
	 * @param key key
	 *
	 * @return boolean
	 */
	public boolean exists( final String key ) {
		return redisTemplate.hasKey( redisNamePrefix + key );
	}

	/**
	 * 读取缓存
	 *
	 * @param key key
	 *
	 * @return Object
	 */
	public Object get( final String key ) {
		Object result = null;
		ValueOperations< Serializable,Object > operations = redisTemplate.opsForValue();
		result = operations.get( redisNamePrefix + key );
		return result;
	}

	/**
	 * 写入缓存
	 *
	 * @param key   key
	 * @param value value
	 *
	 * @return boolean
	 */
	public boolean set( final String key, Object value ) {
		boolean result = false;
		try {
			ValueOperations<Serializable,Object > operations = redisTemplate.opsForValue();
			operations.set( redisNamePrefix + key, value );
			result = true;
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 写入缓存
	 *
	 * @param key   key
	 * @param value value
	 *
	 * @return boolean
	 */
	public boolean set( final String key, Object value, Long expireTime ) {
		boolean result = false;
		try {
			ValueOperations< Serializable,Object > operations = redisTemplate.opsForValue();
			operations.set( key, value );
			redisTemplate.expire( redisNamePrefix + key, expireTime, TimeUnit.SECONDS );
			result = true;
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return result;
	}
}
