package com.gt.union.common.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hongjiye
 * @time 2017-12-06 18:04
 **/
@ConfigurationProperties(prefix = "redisson")
public class RedissonPropertiesUtil {

    private int timeout = 5000;

    private String password;

    private String[] sentinelAddresses;

    private String masterName;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String[] getSentinelAddresses() {
        return sentinelAddresses;
    }

    public void setSentinelAddresses(String sentinelAddresses) {
        this.sentinelAddresses = sentinelAddresses.split(",");
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
