package com.gt.union.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Properties 读取工具
 * @author
 *
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

	private static String redisIp;

	private static Integer redisPort;

	private static String redisPwd;

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

	/**
	 * 获取资源访问地址
	 * @return resourceUrl
	 */
	public static String getResourceUrl() {
		return resourceUrl;
	}

	/**
	 * 获取socket地址
	 * @return socketUrl
	 */
	public static String getSocketUrl() {
		return socketUrl;
	}

	/**
	 * 获取mq消息队列地址
	 * @return mgUrl
	 */
	public static String getMqUrl() {
		return mqUrl;
	}

	/**
	 * 获取mq消息队列用户
	 * @return mqUser
	 */
	public static String getMqUser() {
		return mqUser;
	}

	/**
	 * 获取mq消息队列密码
	 * @return mqPassWord
	 */
	public static String getMqPassWord() {
		return mqPassWord;
	}

	/**
	 * 获取mq转换器
	 * @return
	 */
	public static String getMqExchange() {
		return mqExchange;
	}


	/**
	 * 获取mq对列名称
	 * @return
	 */
	public static String getMqQueueName() {
		return mqQueueName;
	}

	/**
	 * 获取redis ip地址
	 * @return redisIp
	 */
	public static String getRedisIp() {
		return redisIp;
	}

	/**
	 * 获取redis密码
	 * @return redisPwd
	 */
	public static String getRedisPwd() {
		return redisPwd;
	}

	/**
	 * 获取redis端口
	 * @return redisPort
	 */
	public static Integer getRedisPort() {
		return redisPort;
	}

	/**
	 * 获取redis命名前缀
	 * @return redisNamePrefix
	 */
	public static String redisNamePrefix() {
		return redisNamePrefix;
	}

	/**
	 * 获取wxmp项目地址
	 * @return wxmpUrl
	 */
	public static String getWxmpUrl() {
		return wxmpUrl;
	}

	/**
	 * 获取多粉域名
	 * @return
	 */
	public static String getDomainDf(){
		return domainDf;
	}

	public static String getWxmpCompany() {
		return wxmpCompany;
	}

	/**
	 * 获取wxmp项目api密钥
	 * @return
	 */
	public static String getWxmpSignKey() {
		return wxmpSignkey;
	}

	@Value("${resource.url.prefix}")
	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	@Value("${socket.url}")
	public void setSocketUrl(String socketUrl) {
		this.socketUrl = socketUrl;
	}

	@Value("${mq.uri}")
	public void setMqUrl(String mqUrl) {
		this.mqUrl = mqUrl;
	}

	@Value("${mq.user}")
	public void setMqUser(String mqUser) {
		this.mqUser = mqUser;
	}

	@Value("${mq.password}")
	public void setMqPassWord(String mqPassWord) {
		this.mqPassWord = mqPassWord;
	}

	@Value("${spring.redis.host}")
	public void setRedisIp(String redisIp) {
		this.redisIp = redisIp;
	}

	@Value("${spring.redis.port}")
	public void setRedisPort(Integer redisPort) {
		this.redisPort = redisPort;
	}

	@Value("${spring.redis.password}")
	public void setRedisPwd(String redisPwd) {
		this.redisPwd = redisPwd;
	}

	@Value("${redisNamePrefix}")
	public void setRedisNamePrefix(String redisNamePrefix) {
		this.redisNamePrefix = redisNamePrefix;
	}

	@Value("${wxmp.url}")
	public void setWxmpUrl(String wxmpUrl) {
		this.wxmpUrl = wxmpUrl;
	}

	@Value("${domain.df}")
	public void setDomain(String domainDf){
		this.domainDf = domainDf;
	}

	@Value("${wxmp.signkey}")
	public void setWxmpSignKey(String wxmpSignKey) {
		this.wxmpSignkey = wxmpSignKey;
	}

	@Value("${wxmp.company}")
	public void setWxmpCompany(String wxmpCompany) {
		this.wxmpCompany = wxmpCompany;
	}

	@Value("${queueName.union.queueName}")
	public void setMqQueueName(String mqQueueName) {
		this.mqQueueName = mqQueueName;
	}

	@Value("${exchange.union.exchange}")
	public void setMqExchange(String mqExchange) {
		this.mqExchange = mqExchange;
	}

	/**
	 * 联盟路径
	 * @return
	 */
	public static String getUnionUrl() {
		return unionUrl;
	}

	@Value("${union.url}")
	public void setUnionUrl(String unionUrl) {
		this.unionUrl = unionUrl;
	}

	/**
	 * 会员请求路径
	 * @return
	 */
	public static String getMemberUrl() {
		return memberUrl;
	}

	@Value("${member.url}")
	public void setMemberUrl(String memberUrl) {
		this.memberUrl = memberUrl;
	}

	/**
	 * 会员请求签名
	 * @return
	 */
	public static String getMemberSignKey() {
		return memberSignKey;
	}

	@Value("${member.signkey}")
	public void setMemberSignKey(String memberSignKey) {
		this.memberSignKey = memberSignKey;
	}

	public static Integer getDuofenBusId() {
		return duofenBusId;
	}

	@Value("${wx.duofen.busId}")
	public void setDuofenBusId(Integer duofenBusId) {
		this.duofenBusId = duofenBusId;
	}

	public static String getEncryptKey() {
		return encryptKey;
	}

	@Value("${union.encryptKey}")
	public void setEncryptKey(String encryptKey) {
		this.encryptKey = encryptKey;
	}

	@Value("${socket.key}")
	public static String getSocketKey() {
		return socketKey;
	}

	public void setSocketKey(String socketKey) {
		this.socketKey = socketKey;
	}

	public static String getUnionSignKey() {
		return unionSignKey;
	}

	@Value("${union.signkey}")
	public void setUnionSignKey(String unionSignKey) {
		this.unionSignKey = unionSignKey;
	}
}
