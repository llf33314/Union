package com.gt.union.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Properties 读取工具
 *
 * @author
 */
@Component
public class PropertiesUtil {

    private static String resourceUrl;

    private static String socketUrl;

    private static String socketKey;

    private static String mqUrl;

    private static String mqUser;

    private static String mqPassWord;

    private static String mqExchange;

    private static String mqQueueName;

    private static String redisNamePrefix;

    private static String wxmpUrl;

    private static String domainDf;

    private static String wxmpSignkey;

    private static String wxmpCompany;

    private static String unionUrl;

    private static String unionSignKey;

    private static String memberUrl;

    private static String memberSignKey;

    private static Integer duofenBusId;

    private static String encryptKey;

    private static String profiles;

    private static String duofenAppid;

    private static String jxcUrl;

    private static String jxcAccount;

    private static String jxcPwd;

    private static String carUrl;

    private static String sucUrl;

    private static String tokenKey;

    private static String unionAppId;

    private static String appVersion;

    private static Integer appIndustry;

    /**
     * 获取资源访问地址
     *
     * @return resourceUrl
     */
    public static String getResourceUrl() {
        return resourceUrl;
    }

    /**
     * 获取socket地址
     *
     * @return socketUrl
     */
    public static String getSocketUrl() {
        return socketUrl;
    }

    /**
     * 获取mq消息队列地址
     *
     * @return mgUrl
     */
    public static String getMqUrl() {
        return mqUrl;
    }

    /**
     * 获取mq消息队列用户
     *
     * @return mqUser
     */
    public static String getMqUser() {
        return mqUser;
    }

    /**
     * 获取mq消息队列密码
     *
     * @return mqPassWord
     */
    public static String getMqPassWord() {
        return mqPassWord;
    }

    /**
     * 获取mq转换器
     *
     * @return
     */
    public static String getMqExchange() {
        return mqExchange;
    }

    /**
     * 获取mq对列名称
     *
     * @return
     */
    public static String getMqQueueName() {
        return mqQueueName;
    }

    /**
     * 获取redis命名前缀
     *
     * @return redisNamePrefix
     */
    public static String redisNamePrefix() {
        return redisNamePrefix;
    }

    /**
     * 获取wxmp项目地址
     *
     * @return wxmpUrl
     */
    public static String getWxmpUrl() {
        return wxmpUrl;
    }

    /**
     * 获取多粉域名
     *
     * @return
     */
    public static String getDomainDf() {
        return domainDf;
    }

    /**
     * 获取多粉公司名称
     *
     * @return
     */
    public static String getWxmpCompany() {
        return wxmpCompany;
    }

    /**
     * 获取wxmp项目api密钥
     *
     * @return
     */
    public static String getWxmpSignKey() {
        return wxmpSignkey;
    }

    /**
     * 会员请求路径
     *
     * @return
     */
    public static String getMemberUrl() {
        return memberUrl;
    }

    /**
     * 会员请求签名
     *
     * @return
     */
    public static String getMemberSignKey() {
        return memberSignKey;
    }

    /**
     * 获取多粉商家id
     *
     * @return
     */
    public static Integer getDuofenBusId() {
        return duofenBusId;
    }

    /**
     * 联盟加密密钥
     *
     * @return
     */
    public static String getEncryptKey() {
        return encryptKey;
    }

    /**
     * sokcetkey前缀
     *
     * @return
     */
    public static String getSocketKey() {
        return socketKey;
    }

    /**
     * 调用联盟接口签名key
     *
     * @return
     */
    public static String getUnionSignKey() {
        return unionSignKey;
    }

    /**
     * 获取项目环境配置前缀
     *
     * @return
     */
    public static String getProfiles() {
        return profiles;
    }

    /**
     * 获取wxmp签名key
     *
     * @return
     */
    public static String getWxmpSignkey() {
        return wxmpSignkey;
    }

    /**
     * 获取多粉appid
     *
     * @return
     */
    public static String getDuofenAppid() {
        return duofenAppid;
    }

    /**
     * 获取进销存地址
     *
     * @return
     */
    public static String getJxcUrl() {
        return jxcUrl;
    }

    /**
     * 获取进销存账号
     *
     * @return
     */
    public static String getJxcAccount() {
        return jxcAccount;
    }

    /**
     * 获取进销存密码
     *
     * @return
     */
    public static String getJxcPwd() {
        return jxcPwd;
    }

    /**
     * 联盟路径
     *
     * @return
     */
    public static String getUnionUrl() {
        return unionUrl;
    }

    /**
     * 获取素材库路径
     *
     * @return
     */
    public static String getSucUrl() {
        return sucUrl;
    }

    /**
     * 获取车小算接口路径
     *
     * @return
     */
    public static String getCarUrl() {
        return carUrl;
    }

    /**
     * 获取token的key
     *
     * @return
     */
    public static String getTokenKey() {
        return tokenKey;
    }

    /**
     * 获取小程序appid
     *
     * @return
     */
    public static String getUnionAppId() {
        return unionAppId;
    }

    /**
     * 获取小程序版本号
     *
     * @return
     */
    public static String getAppVersion() {
        return appVersion;
    }

    /**
     * 获取小程序行业id
     *
     * @return
     */
    public static Integer getAppIndustry() {
        return appIndustry;
    }

    @Value("${resource.url.prefix}")
    public void setResourceUrl(String resourceUrl) {
        PropertiesUtil.resourceUrl = resourceUrl;
    }

    @Value("${socket.url}")
    public void setSocketUrl(String socketUrl) {
        PropertiesUtil.socketUrl = socketUrl;
    }

    @Value("${mq.uri}")
    public void setMqUrl(String mqUrl) {
        PropertiesUtil.mqUrl = mqUrl;
    }

    @Value("${mq.user}")
    public void setMqUser(String mqUser) {
        PropertiesUtil.mqUser = mqUser;
    }

    @Value("${mq.password}")
    public void setMqPassWord(String mqPassWord) {
        PropertiesUtil.mqPassWord = mqPassWord;
    }

    @Value("${redisNamePrefix}")
    public void setRedisNamePrefix(String redisNamePrefix) {
        PropertiesUtil.redisNamePrefix = redisNamePrefix;
    }

    @Value("${wxmp.url}")
    public void setWxmpUrl(String wxmpUrl) {
        PropertiesUtil.wxmpUrl = wxmpUrl;
    }

    @Value("${domain.df}")
    public void setDomain(String domainDf) {
        PropertiesUtil.domainDf = domainDf;
    }

    @Value("${wxmp.signkey}")
    public void setWxmpSignKey(String wxmpSignKey) {
        PropertiesUtil.wxmpSignkey = wxmpSignKey;
    }

    @Value("${wxmp.company}")
    public void setWxmpCompany(String wxmpCompany) {
        PropertiesUtil.wxmpCompany = wxmpCompany;
    }

    @Value("${queueName.union.queueName}")
    public void setMqQueueName(String mqQueueName) {
        PropertiesUtil.mqQueueName = mqQueueName;
    }

    @Value("${exchange.union.exchange}")
    public void setMqExchange(String mqExchange) {
        PropertiesUtil.mqExchange = mqExchange;
    }

    @Value("${union.url}")
    public void setUnionUrl(String unionUrl) {
        PropertiesUtil.unionUrl = unionUrl;
    }

    @Value("${union.encryptKey}")
    public void setEncryptKey(String encryptKey) {
        PropertiesUtil.encryptKey = encryptKey;
    }

    @Value("${union.signkey}")
    public void setUnionSignKey(String unionSignKey) {
        PropertiesUtil.unionSignKey = unionSignKey;
    }

    @Value("${member.url}")
    public void setMemberUrl(String memberUrl) {
        PropertiesUtil.memberUrl = memberUrl;
    }

    @Value("${member.signkey}")
    public void setMemberSignKey(String memberSignKey) {
        PropertiesUtil.memberSignKey = memberSignKey;
    }

    @Value("${wx.duofen.busId}")
    public void setDuofenBusId(Integer duofenBusId) {
        PropertiesUtil.duofenBusId = duofenBusId;
    }

    @Value("${wx.duofen.appid}")
    public void setDuofenAppid(String duofenAppid) {
        PropertiesUtil.duofenAppid = duofenAppid;
    }

    @Value("${socket.key}")
    public void setSocketKey(String socketKey) {
        PropertiesUtil.socketKey = socketKey;
    }

    @Value("${spring.profiles.active}")
    public void setProfiles(String profiles) {
        PropertiesUtil.profiles = profiles;
    }

    @Value("${jxc.url}")
    public void setJxcUrl(String jxcUrl) {
        PropertiesUtil.jxcUrl = jxcUrl;
    }

    @Value("${jxc.account}")
    public void setJxcAccount(String jxcAccount) {
        PropertiesUtil.jxcAccount = jxcAccount;
    }

    @Value("${jxc.pwd}")
    public void setJxcPwd(String jxcPwd) {
        PropertiesUtil.jxcPwd = jxcPwd;
    }

    @Value("${car.url}")
    public void setCarUrl(String carUrl) {
        PropertiesUtil.carUrl = carUrl;
    }

    @Value("${suc.url}")
    public void setSucUrl(String sucUrl) {
        PropertiesUtil.sucUrl = sucUrl;
    }

    @Value("${wxapp.sso.token.des_key}")
    public void setTokenKey(String tokenKey) {
        PropertiesUtil.tokenKey = tokenKey;
    }

    @Value("${union.appid}")
    public void setUnionAppId(String unionAppId) {
        PropertiesUtil.unionAppId = unionAppId;
    }

    @Value("${union.appVersion}")
    public void setAppVersion(String appVersion) {
        PropertiesUtil.appVersion = appVersion;
    }

    @Value("${union.appIndustry}")
    public void setAppIndustry(Integer appIndustry) {
        PropertiesUtil.appIndustry = appIndustry;
    }
}
