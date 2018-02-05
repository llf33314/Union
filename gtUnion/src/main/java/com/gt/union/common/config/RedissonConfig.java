package com.gt.union.common.config;

import com.gt.union.common.util.RedissonPropertiesUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redisson分布式锁配置
 *
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
     *
     * @return
     */
    @Bean
    RedissonClient redissonClient() {
        Config config = new Config();
        config.useSentinelServers()
            .addSentinelAddress(redssionProperties.getSentinelAddresses())
            .setMasterName(redssionProperties.getMasterName())
            .setPassword(redssionProperties.getPassword())
            .setTimeout(redssionProperties.getTimeout());
        return Redisson.create(config);
    }

}
