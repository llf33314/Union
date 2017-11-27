package com.gt.union.api.client.member.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.bean.session.Member;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.member.MemberService;
import com.gt.union.common.util.ApiResultHandlerUtil;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 * Created by Administrator on 2017/11/25 0018.
 */
@Service
public class MemberServiceImpl implements MemberService {

	private Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

	@Override
	public List<Map> listByBusIdAndMemberIds(Integer busId, String memberIds) {
		logger.info("根据商家id：{}和用户ids：{}查询列表信息", busId, memberIds);
		String url = PropertiesUtil.getMemberUrl() + "/memberAPI/member/findMemberByIds";
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("busId",busId);
		param.put("ids",memberIds);
		try {
			String data = SignHttpUtils.WxmppostByHttp(url,param, PropertiesUtil.getMemberSignKey());
			return ApiResultHandlerUtil.listDataObject(data,Map.class);
		}catch (Exception e){
			return null;
		}
	}

	@Override
	public Member getById(Integer memberId) {
		logger.info("根据粉丝用户id获取用户信息，粉丝id：{}", memberId);
		String url = PropertiesUtil.getMemberUrl() + "/memberAPI/member/findByMemberId";
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("memberId",memberId);
		try {
			String data = SignHttpUtils.WxmppostByHttp(url,param, PropertiesUtil.getMemberSignKey());
			return ApiResultHandlerUtil.getDataObject(data,Member.class);
		}catch (Exception e){
			return null;
		}
	}

	@Override
	public Member findByPhoneAndBusId(String phone, Integer busId) {
		logger.info("根据手机号：{}和商家id：{}获取用户信息", phone, busId);
		String url = PropertiesUtil.getMemberUrl() + "/memberAPI/member/findMemberByPhone";
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("phone",phone);
		param.put("busId",busId);
		try {
			String data = SignHttpUtils.WxmppostByHttp(url,param,PropertiesUtil.getMemberSignKey());
			Member member = ApiResultHandlerUtil.getDataObject(data,Member.class);
			return member;
		}catch (Exception e){
			return null;
		}
	}

	@Override
	public int bindMemberPhone(Integer busId, Integer memberId, String phone) {
		String url = PropertiesUtil.getMemberUrl() + "/memberAPI/member/updateMemberPhoneByMemberId";
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("phone",phone);
		param.put("busId",busId);
		param.put("memberId",memberId);
		logger.info("绑定粉丝用户手机号，请求参数：{}", JSON.toJSONString(param));
		return httpRequestMemberApi(param,url);
	}

	@Override
	public int loginMemberByPhone(String phone, Integer busId) {
		String url = PropertiesUtil.getMemberUrl() + "/memberAPI/member/loginMemberByPhone";
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("phone",phone);
		param.put("busId",busId);
		logger.info("粉丝用户手机号登录，请求参数：{}", JSON.toJSONString(param));
		return httpRequestMemberApi(param,url);
	}

	/**
	 * 请求粉丝会员接口，返回参数
	 * @param param
	 * @param url
	 * @return
	 */
	private int httpRequestMemberApi(Map param, String url){
		try {
			String data = SignHttpUtils.WxmppostByHttp(url,param,PropertiesUtil.getMemberSignKey());
			if(StringUtil.isEmpty(data)){
				return 0;
			}
			Map map = JSONObject.parseObject(data,Map.class);
			if(!("0".equals(map.get("code").toString()))){
				return 0;
			}
		}catch (Exception e){
			return 0;
		}
		return 1;
	}
}
