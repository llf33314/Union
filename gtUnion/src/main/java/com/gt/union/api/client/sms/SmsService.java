package com.gt.union.api.client.sms;

import com.gt.union.api.amqp.entity.PhoneMessage;


/**
 * 短信服务api
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0022.
 */
public interface SmsService {

	/**
	 * 1：成功 0：失败
	 * @param phoneMessage
	 * @return
	 */
	int sendSms(PhoneMessage phoneMessage);
}
