package com.gt.union.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
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

	private String getRedisNamePrefix(){
		return PropertiesUtil.redisNamePrefix();
	}

	/**
	 * 批量删除对应的value
	 * @param keys 集合keys
	 */
	public void remove( final List<String> keys) {
		for ( String key : keys ) {
			remove( key );
		}
	}

	/**
	 * 批量删除key
	 *
	 * @param pattern pattern
	 */
	public void removePattern( final String pattern ) {
		try{
			Set< String > keys = redisTemplate.keys( pattern );
			if ( keys.size() > 0 ) redisTemplate.delete( keys );
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 删除对应的value
	 *
	 * @param key key
	 */
	public void remove( final String key ) {
		try{
			if ( exists( key ) ) {
				redisTemplate.delete( this.getRedisNamePrefix() +key );
			}
		}catch (Exception e){
			e.printStackTrace();
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
		try{
			return redisTemplate.hasKey( this.getRedisNamePrefix() + key );
		}catch (Exception e){
			return false;
		}
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
		try {
			ValueOperations< String,String > operations = redisTemplate.opsForValue();
			result = operations.get( this.getRedisNamePrefix() + key );
		}catch (Exception e){
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
	public boolean set( final String key, String value ) {
		boolean result = false;
		try {
			set(key, value, 7200L);
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
	public boolean set( final String key, String value, Long expireTime ) {
		boolean result = false;
		try {
			ValueOperations< String,String > operations = redisTemplate.opsForValue();
			operations.set(this.getRedisNamePrefix() + key, value );
			redisTemplate.expire( this.getRedisNamePrefix() + key, expireTime, TimeUnit.SECONDS );
			result = true;
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return result;
	}
}
