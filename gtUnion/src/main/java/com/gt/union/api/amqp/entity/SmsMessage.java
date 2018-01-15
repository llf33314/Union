package com.gt.union.api.amqp.entity;

import com.alibaba.fastjson.JSON;

/**
 * @author hongjiye
 * @time 2018-01-12 16:42
 **/
public class SmsMessage {

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
