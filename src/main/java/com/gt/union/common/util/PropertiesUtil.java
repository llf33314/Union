package com.gt.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;


/**
 * Properties 读取工具
 * @author lfx
 *
 */
public class PropertiesUtil {
	
	private static Properties prop=null;
	
	/**
	 * 微信公众号二维码
	 */
	public static final String IMAGE_FOLDER_TYPE_1="1";
	
	/**
	 * 微官网
	 */
	public static final String IMAGE_FOLDER_TYPE_2="2";
	
	/**
	 * 图文(素材)
	 */
	public static final String IMAGE_FOLDER_TYPE_3="3";
	
	/**
	 * 微场景
	 */
	public static final String IMAGE_FOLDER_TYPE_4="4";
	
	/**
	 * 正文
	 */
	public static final String IMAGE_FOLDER_TYPE_5="5";
	
	/**
	 * 微信门店
	 */
	public static final String IMAGE_FOLDER_TYPE_6="6";
	
	/**
	 * 支付证书
	 */
	public static final String IMAGE_FOLDER_TYPE_7="7";
	
	/**
	 * 音乐
	 */
	public static final String IMAGE_FOLDER_TYPE_8="8";
	
	
	public static final String IMAGE_FOLDER_TYPE_9="9";
	/**
	 * 微预约
	 */
	public static final String IMAGE_FOLDER_TYPE_10="10";
	
	/**
	 * 微餐饮
	 */
	public static final String IMAGE_FOLDER_TYPE_11="11";
	/**
	 * 微预约截图
	 */
	public static final String IMAGE_FOLDER_TYPE_12="12";
	
	/**
	 * 微投票
	 */
	public static final String IMAGE_FOLDER_TYPE_13="13";
	
	/**
	 * 会员信息
	 */
	public static final String IMAGE_FOLDER_TYPE_14="14";
	/**
	 * 微商城
	 */
	public static final String IMAGE_FOLDER_TYPE_15="15";
	
	/**
	 * 微邀请
	 */
	public static final String IMAGE_FOLDER_TYPE_16="16";
	
	/**
	 * 微留言--铃声
	 */
	public static final String IMAGE_FOLDER_TYPE_17="17";
	
	
	/**
	 * 欢迎页--铃声
	 */
	public static final String IMAGE_FOLDER_TYPE_18="18";

	
	/**
	 * 微购票
	 */
	public static final String IMAGE_FOLDER_TYPE_19="19";
	
	/**
	 * h5商城
	 */
	public static final String IMAGE_FOLDER_TYPE_20="20";
	
	/**
	 * 360全景
	 */
	public static final String IMAGE_FOLDER_TYPE_21="21";
	
	
	/**
	 * 个人名片
	 */
	public static final String IMAGE_FOLDER_TYPE_22="22";

	/**
	 * 在线报名
	 */
	public static final String IMAGE_FOLDER_TYPE_23="23";
	
	
	/**
	 * 饭票
	 */
	public static final String IMAGE_FOLDER_TYPE_24="24";
	
	/**
	 * 扫码签到
	 */
	public static final String IMAGE_FOLDER_TYPE_25="25";
	
	/**
	 * 店员酬金
	 */
	public static final String IMAGE_FOLDER_TYPE_26="26";
	
	/**
	 * 微抽奖
	 */
	public static final String IMAGE_FOLDER_TYPE_27="27";

	
	/**
	 * SOCKET推送前缀，运用于总后台的推送
	 */
	public static final String SOCKET_TITLE_TYPE_HT = "HT";
	
	/**
	 * SOCKET推送前缀，运用于多粉打印机的推送
	 */
	public static final String SOCKET_TITLE_TYPE_PR = "PR";
	
	/**
	 * 微商城
	 */
	public static final String IMAGE_FOLDER_TYPE_28 = "28";
	
	/**
	 * 微餐饮
	 */
	public static final String IMAGE_FOLDER_TYPE_33 = "33";
	
	
	/**
	 * 微商城
	 */
	public static final String IMAGE_FOLDER_TYPE_29 = "29";
	static {   
        prop = new Properties();   
        InputStream in = PropertiesUtil.class.getResourceAsStream("/config/properties/system.properties");   
        try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}   
    }   
	
	
	/**
	 * 获取静态资源url
	 * @return
	 */
	public static String getResourceUrl(){
        return prop.getProperty("resource.url.prefix");
	}
	
	/**
	 * 获取新高级编辑器静态资源url  static.pic.url.prefix=http://maint.duofriend.com/upload/
	 * @return
	 */
	public static String getNewEditResourceUrl(){
        return prop.getProperty("static.pic.url.prefix");
	}
	
	/**
	 * 获取访问文章资源URL
	 * @return
	 */
	public static String getArticleUrl(){
        return prop.getProperty("article.url.prefix");
	}
	
	/**
	 * 获取域名
	 * @return
	 */
	public static String getDomain(){
		return prop.getProperty("domain");
	}
	
	/**
	 * 获取访问微网站资源URL
	 * @return
	 */
	public static String getTemplateUrl(Integer templateId){
		String url=prop.getProperty("template.url.prefix");
		return url.replace("{id}", String.valueOf(templateId));
	}
	/**
	 * 获取访问微场景资源URL
	 * @param senceid
	 * @return
	 */
	public static String getSenceUrl(Integer senceid){
		String url=prop.getProperty("sence.url.prefix");
		return url.replace("{id}", String.valueOf(senceid));
	}
	
	/**
	 * 获取访问微网站资源URL
	 * @return
	 */
	public static String getWxcardUrl(Integer id){
		String url=prop.getProperty("wxcard.url.prefix");
		return url.replace("{id}", String.valueOf(id));
	}
	
	/**
	 * 获取访问微预约资源URL
	 * @return
	 */
	public static String getMircodeSpeakUrl(){
		String url=prop.getProperty("mircobespeak.url.prefix");
		return url;
	}
	/**
	 * 获取网站地址
	 * @return
	 */
	public static String getWebHomeUrl(){
		String url=prop.getProperty("web.home.url");
		return url;
	}
	
	
	/**
	 * 获取图片存放路径
	 * @return
	 */
	public static String getResImagePath(){
		String url=prop.getProperty("res.image.path");
		return url;
	}
	
	
	/**
	 * 获取音频存放路径
	 * @return
	 */
	public static String getResVoicePath(){
		String url=prop.getProperty("res.voice.path");
		return url;
	}
	
	
	/**
	 * 获取视频存放路径
	 * @return
	 */
	public static String getResVideoPath(){
		String url=prop.getProperty("res.video.path");
		return url;
	}
	
	
	/**
	 * 获取正文图片存放路径
	 * @return
	 */
	public static String getResEditorPath(){
		String url=prop.getProperty("res.editor.path");
		return url;
	}
	
	
	/**
	 * 微信公众号授权成功后的回调函数
	 * @return
	 */
	public static String getGrantSuccessPath(){
		String url=prop.getProperty("grant.success.url.prefix");
		return url;
	}
	
	
	/**
	 * 多粉服务号的应用号
	 * @return
	 */
	public static String getAppid(){
		String url=prop.getProperty("duofen.appid.prefix");
		return url;
	}
	
	/**
	 * 多粉服务号的应用密码
	 * @return
	 */
	public static String getAppsecrect(){
		String url=prop.getProperty("duofen.appsecrect.prefix");
		return url;
	}
	

	/**
	 * 多粉微信支付账号
	 * @return
	 */
	public static String getMchid(){
		String url=prop.getProperty("duofen.mch_id.prefix");
		return url;
	}
	
	
	
	/**
	 * 服务商公众号appid
	 * @return
	 */
	public static String getServiceUser(){
		String url=prop.getProperty("service.wx.user");
		return url;
	}
	
	/**
	 * 服务商公众号刷新令牌
	 * @return
	 */
	public static String getServiceAuthRefreshToken(){
		String url=prop.getProperty("service.wx.auth_refresh_token");
		return url;
	}
	
	
	/**
	 * 服务商公众号密钥
	 * @return
	 */
	public static String getServicePwd(){
		String url=prop.getProperty("service.wx.pwd");
		return url;
	}
	
	
	/**
	 * 服务商公众号服务商号
	 * @return
	 */
	public static String getServiceMchId(){
		String url=prop.getProperty("service.wx.mch_id");
		return url;
	}
	
	
	/**
	 * 多粉微信支付密匙
	 * @return
	 */
	public static String getApikey(){
		String url=prop.getProperty("duofen.api_key.prefix");
		return url;
	}
	/**
	 * 图文网站长度
	 * @return
	 */
	public static Integer getphoneNum(){
		String num=prop.getProperty("phone.num");
		return Integer.valueOf(num);
		
	}
	/**
	 * 官网网站图片长度
	 * @return
	 */
	public static Integer getwebNum(){
		String num=prop.getProperty("friend.num");
		return Integer.valueOf(num);
		
	}
	
	/**
	 * 一键导航链接
	 * @return
	 */
	public static String getNaviUrl(Integer id,Integer publicid){
		String url=prop.getProperty("navi.url.prefix");
		url=url.replace("{id}", String.valueOf(id));
		url=url.replace("{publicid}", String.valueOf(publicid));
		return url;
	}
	
	/**
	 * 大转盘手机端进入链接
	 * @return
	 */
	public static String getTurUrl(){
		String url=prop.getProperty("tur.url.prefix");
		return url;
	}
	
	/*public static Integer getWxPublicUsersid(){
		String url=prop.getProperty("wxPublicUsersid");
		return Integer.valueOf(url);
		
	}*/
	
	/**
	 * 第三方平台APPID
	 * @return
	 */
	public static String getComponentAppid(){
		return prop.getProperty("component.appid");
	}
	
	
	/**
	 * 第三方平台appsecret
	 * @return
	 */
	public static String getComponentAppsecret(){
		return prop.getProperty("component.appsecret");
	}
	
	
	/**
	 * 公众号消息加解密Key
	 * @return
	 */
	public static String getComponentEncodingaeskey(){
		return prop.getProperty("component.encodingaeskey");
	}
	
	/**
	 * 公众号消息校验Token(创建第三方平台时的Token值)
	 * @return
	 */
	public static String getComponentGrantToken(){
		return prop.getProperty("component.grant.token");
	}
	/*public static Integer getwebpblic(){
		String url=prop.getProperty("wxbreakuserid");
		return Integer.valueOf(url);
	}*/
	
	/**
	 * 会员支付成功接口
	 * @return
	 */
	public static String getMemberNotifyUrl(){
		String url=prop.getProperty("member.notify.url");
		return url;
	}
	
	
	public static String getSocketUrl(){
		String url=prop.getProperty("socket.url");
		return url;
	}
	
	/**
	 * 集团编码
	 * @return
	 */
	public static String getNgecEccode(){
		String url=prop.getProperty("ngec.eccode");
		return url;
	}
	
	/**
	 * 集团用户名
	 * @return
	 */
	public static String getNgecEcusername(){
		String url=prop.getProperty("ngec.ecusername");
		return url;
	}
	
	
	/**
	 * 集团密码
	 * @return
	 */
	public static String getNgecEcuserPwd(){
		String url=prop.getProperty("ngec.ecuserpwd");
		return url;
	}
	
	/**
	 * EC企业代码
	 * @return
	 */
	public static String getNgecMemberShiprequestBodyEccode(){
		String url=prop.getProperty("ngec.membershiprequest.body.eccode");
		return url;
	}
	
	
	/**
	 * 集团产品号码
	 * @return
	 */
	public static String getNgecMemberShiprequestBodyPrdordnum(){
		String url=prop.getProperty("ngec.membershiprequest.body.prdordnum");
		return url;
	}
	
	

	/**
	 * 企业短信通-账户
	 * @return
	 */
	public static String getSmsUser(){
		String url=prop.getProperty("sms.user");
		return url;
	}
	
	/**
	 * 企业短信通-密码
	 * @return
	 */
	public static String getSmsPwd(){
		String url=prop.getProperty("sms.pwd");
		return url;
	}
	
	
	/**
	 * 企业短信通-密码
	 * @return
	 */
	public static String getFollowVisitUrl(){
		String url=prop.getProperty("follow.visit.url");
		return url;
	}
	/**
	 * 读取MQ队列的URL
	 * @return
	 */
	public static String getMqUrl(){
		String url=prop.getProperty("mq.uri");
		return url;
	}
	/**
	 * 读取MQ队列的用户名
	 * @return
	 */
	public static String getMqUser(){
		String url=prop.getProperty("mq.user");
		return url;
	}
	/**
	 * 读取MQ队列的密码
	 * @return
	 */
	public static String getMqPassWord(){
		String url=prop.getProperty("mq.password");
		return url;
	}
	
	
	
	/**
	 * 服务商公众号appid
	 * @return
	 */
	public static String getUserGrantCommonAppid(){
		String url=prop.getProperty("userGrant.common.appid");
		return url;
	}
	
	/**
	 * 服务商公众号刷新令牌
	 * @return
	 */
	public static String getUserGrantCommonAuth_refresh_token(){
		String url=prop.getProperty("userGrant.common.auth_refresh_token");
		return url;
	}
	
	/**
	 * webserivce 访问地址
	 * @return
	 */
	public static String getWebService(){
		String url=prop.getProperty("webserive.url");
		return url;
	}
	
	
	/**
	 * 支付宝异步回调(支付)
	 * @return
	 */
	public static String getAlipayNotifyUrl(){
		String url=prop.getProperty("alipay.notify.url");
		return url;
	}
	
	
	/**
	 * 支付宝异步回调(退款)
	 * @return
	 */
	public static String getAlipayRefundNotifyUrl(){
		String url=prop.getProperty("alipay.refund.notify.url");
		return url;
	}
	
	/**
	 * 支付宝同步跳转
	 * @return
	 */
	public static String getAlipayReturnUrl(){
		String url=prop.getProperty("alipay.return.url");
		return url;
	}
	/**
	 * 客服QQ
	 * @return
	 */
	public static String getUstomerServiceQq(){
		String qq=prop.getProperty("ustomer.service.qq");
		return qq;
	}
	
	/**
	 * 区分项目0代表多粉，1代表翼粉，2代表微站
	 * @return
	 */
	public static String getItemType(){
		String itemtype=prop.getProperty("item.type");
		return itemtype;
		
	}
	
	
	/**
	 * 多粉或翼粉标识符 0代表多粉，1代表翼粉标识符，2代表微站
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String getIdentifier() {
		String itemtype=prop.getProperty("identifier");
		return itemtype;
	}

	/**
	 * 高级编辑器图片路径
	 * @return
	 */
	public static String getNewEditPicUrl(){
		String url=prop.getProperty("new_edit.pic.url");
		return url;
	}
	
	/**
	 * 餐饮推送前缀
	 * @return
	 */
	public static String getEatSocketPrefix(){
		return prop.getProperty("eat.socket.prefix");
	}
	
	
	/**
	 * 易联云打印账户ID
	 * @return
	 */
	public static String getMachinePartner(){
		return prop.getProperty("printmachine.partner");
	}
	
	
	/**
	 * 易联云打印API密钥
	 * @return
	 */
	public static String getMachineApiKey(){
		return prop.getProperty("printmachine.apiKey");
	}
	
	
	/**
	 * 易联云打印登录账户名
	 * @return
	 */
	public static String getMachineUserName(){
		return prop.getProperty("printmachine.userName");
	}
	
	
	/**
	 * 图片资源ftp ip
	 * @return
	 */
	public static String getStaticSourceFtpIp(){
		return prop.getProperty("static.source.ftp.ip");
	}
	
	
	/**
	 * 图片资源ftp 端口
	 * @return
	 */
	public static String getStaticSourceFtpPort(){
		return prop.getProperty("static.source.ftp.port");
	}
	

	/**
	 * 图片资源ftp 密码
	 * @return
	 */
	public static String getStaticSourceFtpPwd(){
		return prop.getProperty("static.source.ftp.password");
	}
	

	/**
	 * 图片资源ftp 用户
	 * @return
	 */
	public static String getStaticSourceFtpUser(){
		return prop.getProperty("static.source.ftp.user");
	}
	
	/**
	 * redis ip
	 * @return
	 */
	public static String getRedisIp(){
		return prop.getProperty("redis.ip");
	}
	
	
	/**
	 * redis port
	 * @return
	 */
	public static String getRedisPort(){
		return prop.getProperty("redis.port");
	}
	
	
	/**
	 * redis pwd
	 * @return
	 */
	public static String getRedisPwd(){
		return prop.getProperty("redis.pwd");
	}
	
	public static String getTelNotice(){
		return prop.getProperty("tel.notice");
	}


	//车小算访问地址
	public static String getCarErpUrl (){
		return prop.getProperty("car.erp.url");
	}

	//车小算erp 公钥
	public static String getCarErpAppid (){
		return prop.getProperty("car.erp.appid");
	}

	//车小算erp 密钥
	public static String getCarErpSecretkey(){
		return prop.getProperty("car.erp.secretkey");
	}

	public static String getXcxAppid(){
		return prop.getProperty("xcx.appid");
	}
	
	public static String getXcxSecret(){
		return prop.getProperty("xcx.secret");
	}
	
	//样子erp手机端 url
	public static String getLookErpMobileUrl (){
		return prop.getProperty("look.erp.mobile.url");
	}
	//样子erp数据 url
	public static String getLookErpurl (){
		return prop.getProperty("look.erp.url");
	}
	//样子erp 公钥
	public static String getLookErpAppid (){
		return prop.getProperty("look.erp.appid");
	}
	//样子erp 密钥
	public static String getLookErpSecretkey(){
		return prop.getProperty("look.erp.secretkey");
	}
	
	/**
	 * 餐饮ERP地址
	 */
	public static String getEatErpWebUrl(){
		return prop.getProperty("eat.erp.web.url");
	}
	
	
	
	/**
	 * 短信发送域名
	 * @return
	 */
	public static String getSmsDomain(){
		return prop.getProperty("sms.domain");
	}
	
	
	//微医疗erp手机端 url
	public static String getHospitalErpMobileUrl (){
		return prop.getProperty("hospital.erp.mobile.url");
	}

	//微医疗erp数据 url
	public static String getHospitalErpurl (){
		return prop.getProperty("hospital.erp.url");
	}
	//微医疗erp 公钥
	public static String getHospitalErpAppid (){
		return prop.getProperty("hospital.erp.appid");
	}

	//微医疗erp 密钥
	public static String getHospitalErpSecretkey(){
		return prop.getProperty("hospital.erp.secretkey");
	}
	
	//小狮erp手机端 url
	public static String getPropertyManageErpMobileUrl() {
		return prop.getProperty("propertyManage.erp.mobile.url");
	}

	// 小狮erp数据 url
	public static String getPropertyManageErpurl() {
		return prop.getProperty("propertyManage.erp.url");
	}

	// 小狮erp 公钥
	public static String getPropertyManageErpAppid() {
		return prop.getProperty("propertyManage.erp.appid");
	}

	// 小狮erp 密钥
	public static String getPropertyManageErpSecretkey() {
		return prop.getProperty("propertyManage.erp.secretkey");
	}
	
	
	/**
	 * 最新流量接口url
	 * @return
	 */
	public static String getFlowUrl(){
		return prop.getProperty("new.flow.url");
	}
	
	/**
	 * 最新流量接口appid
	 * @return
	 */
	public static String getFlowAppid(){
		return prop.getProperty("new.flow.appid");
	}
	
	/**
	 * 最新流量接口key
	 * @return
	 */
	public static String getFlowKey(){
		return prop.getProperty("new.flow.key");
	}
	
	/**
	 * websocket
	 * @return
	 */
	public static String getWebsocketDomain(){
		return prop.getProperty("websocket.domain");
	}
	
	
	/**
	 * ssl域名
	 * @return
	 */
	public static String geSsltDomain(){
		return prop.getProperty("ssl.domain");
	}

	public static String getWebPayBusId(){
        return prop.getProperty("web.pay.id");
	}
	
	
	/**
	 * 进销存数据 url
	 * @return
	 */
	public static String getJxcErpUrl() {
		return prop.getProperty("jxc.erp.mobile.url");
	}

	/**
	 * 进销存用户名
	 * @return
	 */
	public static String getJxcErpAccount() {
		return prop.getProperty("jxc.erp.account");
	}

	/**
	 * 进销存密码
	 * @return
	 */
	public static String getJxcErpPwd() {
		return prop.getProperty("jxc.erp.pwd");
	}
	
	
}
