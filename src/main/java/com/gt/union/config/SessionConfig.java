package com.gt.union.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.DefaultCookieSerializer;

//这个类用配置redis服务器的连接
//maxInactiveIntervalInSeconds为SpringSession的过期时间（单位：秒）
@EnableRedisHttpSession(maxInactiveIntervalInSeconds= 1800)
public class SessionConfig {

    @Value( "${redisSession.cookieName}" )
    private String cookieName;
    @Value( "${redisSession.cookiePath}" )
    private String cookiePath;
    @Value( "${redisSession.domainName}" )
    private String domainName;

    @Bean
    public DefaultCookieSerializer defaultCookieSerializer(){
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        defaultCookieSerializer.setDomainName(cookieName);
        defaultCookieSerializer.setCookieName(cookieName);
        defaultCookieSerializer.setCookiePath(cookiePath);
        return defaultCookieSerializer;
    }
}