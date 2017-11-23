package com.gt.union.common.config;

import com.gt.union.common.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * redis session配置类
 * maxInactiveIntervalInSeconds为SpringSession的过期时间（单位：秒）
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
public class SessionConfig {

    @Value("${redisSession.cookieName}")
    private String cookieName;
    @Value("${redisSession.cookiePath}")
    private String cookiePath;
    @Value("${redisSession.domainName}")
    private String domainName;

    @Value("${spring.redis2.host}")
    private String host;
    @Value("${spring.redis2.port}")
    private int port;
    @Value("${spring.redis2.timeout}")
    private int timeout;
    @Value("${spring.redis2.database}")
    private int database;
    @Value("${spring.redis2.password}")
    private String password;

    @Bean
    public DefaultCookieSerializer defaultCookieSerializer() {
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        if (!"dev".equals(PropertiesUtil.getProfiles())) {
            defaultCookieSerializer.setDomainName(domainName);
            defaultCookieSerializer.setCookieName(cookieName);
            defaultCookieSerializer.setCookiePath(cookiePath);
        }
        return defaultCookieSerializer;
    }

    @Bean
    public JedisConnectionFactory connectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(host);
        factory.setPort(port);
        factory.setDatabase(database);
        //设置连接超时时间
        factory.setTimeout(timeout);
        factory.setPassword(password);
        return factory;
    }
}