package com.gt.union.wxapp.card.service;

import com.gt.api.bean.session.Member;

/**
 * @author hongjiye
 * @time 2017-12-29 16:07
 **/
public interface ITokenApiService {

	/**
	 * 根据token获取粉丝信息
	 * @param token		token
	 * @param busId		商家id
	 * @return
	 * @throws Exception
	 */
	Member getMemberByToken(String token, Integer busId) throws Exception;
}
