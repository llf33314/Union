package com.gt.union.common.config;

import com.gt.union.common.service.DistributedLocker;
import com.gt.union.common.service.impl.RedissonDistributedLocker;
import com.gt.union.common.util.RedissonLockUtil;
import com.gt.union.common.util.RedissonPropertiesUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redisson分布式锁配置
 * @author hongjiye
 * @time 2017-12-06 17:57
 **/
@Configuration
@ConditionalOnClass(Config.class)
@EnableConfigurationProperties(RedissonPropertiesUtil.class)
public class RedissonConfig {

	@Autowired
	private RedissonPropertiesUtil redssionProperties;

	/**
	 * 哨兵模式自动装配
	 * @return
	 */
	@Bean
	RedissonClient redissonSentinel() {
		Config config = new Config();
		config.useSentinelServers()
				.setMasterName(redssionProperties.getMasterName())
				.addSentinelAddress(redssionProperties.getSentinelAddresses())
				.setPassword(redssionProperties.getPassword())
				.setTimeout(redssionProperties.getTimeout());
		return Redisson.create(config);
	}



	/**
	 * 装配locker类，并将实例注入到RedissonLockUtil中
	 * @return
	 */
	@Bean
	DistributedLocker distributedLocker(RedissonClient redissonClient) {
		DistributedLocker locker = new RedissonDistributedLocker();
		locker.setRedissonClient(redissonClient);
		RedissonLockUtil.setLocker(locker);
		return locker;
	}
}
