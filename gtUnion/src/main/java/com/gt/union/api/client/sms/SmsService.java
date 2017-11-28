package com.gt.union.api.client.sms;

import com.gt.union.api.amqp.entity.PhoneMessage;


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
}
