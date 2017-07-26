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

	private static String mgUrl;

	private static String mqUser;

	private static String mqPassWord;

	private static String staticSourceFtpIp;

	private static String staticSourceFtpPort;

	private static String staticSourceFtpPwd;

	private static String staticSourceFtpUser;

	private static String redisIp;

	private static Integer redisPort;

	private static String redisPwd;

	private static String redisNamePrefix;

	private static String wxmpUrl;

	private static String articleUrl;

	private static String domain;


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
		return mgUrl;
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
	 * 获取ftp上传ip地址
	 * @return staticSourceFtpIp
	 */
	public static String getStaticSourceFtpIp() {
		return staticSourceFtpIp;
	}

	/**
	 * 获取ftp上传端口
	 * @return staticSourceFtpPort
	 */
	public static String getStaticSourceFtpPort() {
		return staticSourceFtpPort;
	}

	/**
	 * 获取ftp上传密码
	 * @return staticSourceFtpPwd
	 */
	public static String getStaticSourceFtpPwd() {
		return staticSourceFtpPwd;
	}

	/**
	 * 获取ftp上传用户
	 * @return staticSourceFtpUser
	 */
	public static String getStaticSourceFtpUser() {
		return staticSourceFtpUser;
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
	 * 获取本项目访问路径
	 * @return
	 */
	public static String getArticleUrl(){
		return articleUrl;
	}

	/**
	 * 获取多粉域名
	 * @return
	 */
	public static String getDomain(){
		return domain;
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
	public void setMgUrl(String mgUrl) {
		this.mgUrl = mgUrl;
	}

	@Value("${mq.user}")
	public void setMqUser(String mqUser) {
		this.mqUser = mqUser;
	}

	@Value("${mq.password}")
	public void setMqPassWord(String mqPassWord) {
		this.mqPassWord = mqPassWord;
	}

	@Value("${static.source.ftp.ip}")
	public void setStaticSourceFtpIp(String staticSourceFtpIp) {
		this.staticSourceFtpIp = staticSourceFtpIp;
	}

	@Value("${static.source.ftp.port}")
	public void setStaticSourceFtpPort(String staticSourceFtpPort) {
		this.staticSourceFtpPort = staticSourceFtpPort;
	}

	@Value("${static.source.ftp.password}")
	public void setStaticSourceFtpPwd(String staticSourceFtpPwd) {
		this.staticSourceFtpPwd = staticSourceFtpPwd;
	}

	@Value("${static.source.ftp.user}")
	public void setStaticSourceFtpUser(String staticSourceFtpUser) {
		this.staticSourceFtpUser = staticSourceFtpUser;
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
	public void setRedisPort(String redisPwd) {
		this.redisPwd = redisPwd;
	}

	@Value("${redisNamePrefix}")
	public void setRedisNamePrefix(String redisNamePrefix) {
		this.redisNamePrefix = redisNamePrefix;
	}

	@Value("${wxmp.url}")
	public void setWxmpUrl(String wxmpUrl) {
		PropertiesUtil.wxmpUrl = wxmpUrl;
	}

	@Value("${article.url.prefix}")
	public void setArticleUrl(String articleUrl){
		PropertiesUtil.articleUrl = articleUrl;
	}

	@Value("${domain}")
	public void setDomain(String domain){
		PropertiesUtil.domain = domain;
	}
}
