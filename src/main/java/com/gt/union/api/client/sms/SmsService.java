package com.gt.union.api.client.sms;

import com.gt.api.dto.ResponseUtils;

import java.util.Map;

/**
 * Created by Administrator on 2017/8/22 0022.
 */
public interface SmsService {

	/**
	 * 1：成功 0：失败
	 * @param param
	 * @return
	 */
	public int sendSms(Map<String,Object> param);
}
