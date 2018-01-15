package com.gt.union.api.client.sms;

import com.gt.union.api.amqp.entity.PhoneMessage;
import com.gt.union.api.amqp.entity.TemplateSmsMessage;


/**
 * 短信服务api
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0022.
 */
public interface SmsService {

	/**
	 * true：成功 false：失败
	 * @param phoneMessage
	 * @return
	 */
	boolean sendSms(PhoneMessage phoneMessage);

	/**
	 * 发送模板短信
	 * @param templateSmsMessage
	 * @return
	 */
	boolean sendTempSms(TemplateSmsMessage templateSmsMessage);

	/**
	 * 校验验证码
	 * @param type	验证码模块类型 1：联盟卡登录 2：办理联盟卡 3：佣金平台登录 4：联盟卡绑定 5：佣金平台管理员
	 * @param code	验证码
	 * @param phone	手机号
	 * @return
	 */
	boolean checkPhoneCode(Integer type, String code, String phone);
}
