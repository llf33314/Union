package com.gt.union.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis缓存配置
 *
 * Created by Administrator on 2017/8/2 0002.
 */
@Configuration
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport {
	/** 日志 */
	private static final Logger LOG = LoggerFactory.getLogger( RedisCacheConfig.class );

	@Value("${spring.redis.host}")
	private String host;
	@Value("${spring.redis.port}")
	private int port;
	@Value("${spring.redis.timeout}")
	private int timeout;
	@Value("${spring.redis.database}")
	private int database;
	@Value("${spring.redis.password}")
	private String password;

	@Bean
	public RedisTemplate< String,String > redisTemplate() {
//		RedisTemplate< String,String > redisTemplate = new RedisTemplate<>();
//		redisTemplate.setConnectionFactory( cf );
//		Jackson2JsonRedisSerializer< Object > jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>( Object.class );
//		ObjectMapper om = new ObjectMapper();
//		om.setVisibility( PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY );
//		om.enableDefaultTyping( ObjectMapper.DefaultTyping.NON_FINAL );
//		jackson2JsonRedisSerializer.setObjectMapper( om );
//		redisTemplate.setValueSerializer( jackson2JsonRedisSerializer );//如果key是String 需要配置一下StringSerializer,不然key会乱码 /XX/XX
//		redisTemplate.afterPropertiesSet();
		RedisTemplate< String,String > redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory( redisConnectionFactory());
		//字符串序列化配置
		RedisSerializer<String> stringRedisSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(stringRedisSerializer);
		redisTemplate.setHashKeySerializer(stringRedisSerializer);
		redisTemplate.setValueSerializer(stringRedisSerializer);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	@Bean
	public JedisConnectionFactory redisConnectionFactory() {
		JedisConnectionFactory factory = new JedisConnectionFactory();
		factory.setHostName(host);
		factory.setPort(port);
		factory.setTimeout(timeout); //设置连接超时时间
		factory.setDatabase(database);
		factory.setPassword(password);
		return factory;
	}

	/*@Bean
	public CacheManager cacheManager(RedisTemplate redisTemplate ) {
		RedisCacheManager cacheManager = new RedisCacheManager( redisTemplate );
		//默认超时时间,单位秒 两小时
		cacheManager.setDefaultExpiration( 7200L );
		//根据缓存名称设置超时时间,0为不超时
		Map< String,Long > expires = new ConcurrentHashMap<>();
		cacheManager.setExpires( expires );
		return cacheManager;
	}*/
}
