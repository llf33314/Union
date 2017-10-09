package com.gt.union.api.client.member.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.bean.session.Member;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.member.MemberService;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
@Service
public class MemberServiceImpl implements MemberService {


	@Override
	public List<Map> listByBusIdAndMemberIds(Integer busId, String memberIds) {
		String url = ConfigConstant.MEMBER_ROOT_URL + "/memberAPI/member/findMemberByids";
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("busId",busId);
		param.put("ids",memberIds);
		try {
			String data = SignHttpUtils.WxmppostByHttp(url,param, ConfigConstant.MEMBER_SIGNKEY);
			if(StringUtil.isEmpty(data)){
				return null;
			}
			Map map = JSONObject.parseObject(data,Map.class);
			if(CommonUtil.isNotEmpty(map.get("data"))){
				return JSONArray.parseArray(map.get("data").toString(),Map.class);
			}else {
				return null;
			}
		}catch (Exception e){
			return null;
		}
	}

	@Override
	public Member getById(Integer memberId) {
		String url = ConfigConstant.MEMBER_ROOT_URL + "/memberAPI/member/findByMemberId";
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("memberId",memberId);
		try {
			String data = SignHttpUtils.WxmppostByHttp(url,param,ConfigConstant.MEMBER_SIGNKEY);
			if(StringUtil.isEmpty(data)){
				return null;
			}
			Map map = JSONObject.parseObject(data,Map.class);
			if(CommonUtil.isNotEmpty(map.get("data"))){
				return JSONObject.parseObject(map.get("data").toString(),Member.class);
			}else {
				return null;
			}
		}catch (Exception e){
			return null;
		}
	}

	@Override
	public Member findByPhoneAndBusId(String phone, Integer busId) {
		String url = ConfigConstant.MEMBER_ROOT_URL + "/memberAPI/member/findMemberByPhone";
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("phone",phone);
		param.put("busId",busId);
		try {
			String data = SignHttpUtils.WxmppostByHttp(url,param,ConfigConstant.MEMBER_SIGNKEY);
			if(StringUtil.isEmpty(data)){
				return null;
			}
			Map map = JSONObject.parseObject(data,Map.class);
			if(CommonUtil.isNotEmpty(map.get("data"))){
				List<Member> list = JSONArray.parseArray(map.get("data").toString(),Member.class);
				return list.get(0);
			}else {
				return null;
			}
		}catch (Exception e){
			return null;
		}
	}

	@Override
	public int bindMemberPhone(Integer busId, Integer memberId, String phone) {
		String url = ConfigConstant.MEMBER_ROOT_URL + "/memberAPI/member/updateMemberPhoneByMemberId";
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("phone",phone);
		param.put("busId",busId);
		param.put("memberId",memberId);
		try {
			String data = SignHttpUtils.WxmppostByHttp(url,param,ConfigConstant.MEMBER_SIGNKEY);
			if(StringUtil.isEmpty(data)){
				return 0;
			}
			Map map = JSONObject.parseObject(data,Map.class);
			if(CommonUtil.isEmpty(map.get("data"))){
				return 0;
			}
		}catch (Exception e){
			return 0;
		}
		return 1;
	}
}
