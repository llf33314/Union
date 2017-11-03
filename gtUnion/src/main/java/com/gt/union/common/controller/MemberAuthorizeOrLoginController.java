package com.gt.union.common.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.bean.session.Member;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.redis.RedisService;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 授权 或 登录 统一调用接口
 * Created by Administrator on 2017/9/19 0019.
 */
public class MemberAuthorizeOrLoginController {

	private Logger logger = LoggerFactory.getLogger(MemberAuthorizeOrLoginController.class);

	@Autowired
	private RedisService redisService;

	/**
	 *
	 * @param request
	 * @param busId		登录的商家id
	 * @param uclogin	uc登录  null：不用在uc登录 not null：在uc登录
	 * @param reqUrl	请求的url，即授权登录后重定向的链接
	 * @return
	 * @throws Exception
	 */
	protected String authorizeMember(HttpServletRequest request, Integer busId, Integer uclogin, String reqUrl) throws Exception {
		logger.debug("进入--联盟手机端授权方法！");
		Integer browser = CommonUtil.judgeBrowser(request);//判断浏览器
		Map<String, Object> getWxPublicMap = new HashMap<>();
		getWxPublicMap.put("busId", busId);//商家账号
		//判断商家信息 1是否过期 2公众号是否变更过
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/busUserApi/getWxPulbicMsg.do";
		String wxpublic = SignHttpUtils.WxmppostByHttp(url, getWxPublicMap, PropertiesUtil.getWxmpSignKey());
		if(StringUtil.isEmpty(wxpublic)){
			throw new BusinessException("登录错误");
		}
		JSONObject json = JSONObject.parseObject(wxpublic);
		Integer code = Integer.parseInt(json.get("code").toString());
		if (code.equals(0)) {
			if(CommonUtil.isNotEmpty(json.get("data"))){
				JSONObject obj = JSONObject.parseObject(json.get("data").toString());
				if (CommonUtil.isNotEmpty(obj.get("guoqi"))) {//商家已过期
					return obj.get("guoqiUrl").toString();//跳到过期页面
				}
				if(CommonUtil.isNotEmpty(obj.get("remoteUcLogin"))){
					return PropertiesUtil.getUnionUrl() + "/cardPhone/#/" + "toUnionLogin?busId="+busId;
				}
			}
		}else {
			throw new BusinessException("登录错误");
		}
		String otherRedisKey = PropertiesUtil.redisNamePrefix() + "authority:"+System.currentTimeMillis();
		redisService.setValue(otherRedisKey, reqUrl, 300);
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("otherRedisKey", otherRedisKey);
		queryMap.put("browser", browser);
		queryMap.put("busId", busId);
		queryMap.put("uclogin", uclogin);
		logger.info("queryMap=" + JSON.toJSONString(queryMap));
		String params = URLEncoder.encode(JSON.toJSONString(queryMap), "utf-8");
		return PropertiesUtil.getWxmpUrl() + "/remoteUserAuthoriPhoneController/79B4DE7C/authorizeMember.do?queryBody=" + params;
	}



	protected String authorizeMemberWx(HttpServletRequest request, String reqUrl) throws Exception {
		logger.debug("进入--联盟手机端微信授权方法！");
		Integer browser = CommonUtil.judgeBrowser(request);//判断浏览器
		if(browser != 1){
			throw new BusinessException("请使用微信登录");
		}
		Map<String, Object> getWxPublicMap = new HashMap<>();
		getWxPublicMap.put("busId", PropertiesUtil.getDuofenBusId());//多粉
		//判断商家信息 1是否过期 2公众号是否变更过
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/busUserApi/getWxPulbicMsg.do";
		String wxpublic = SignHttpUtils.WxmppostByHttp(url, getWxPublicMap, PropertiesUtil.getWxmpSignKey());
		if(StringUtil.isEmpty(wxpublic)){
			throw new BusinessException("登录错误");
		}
		JSONObject json = JSONObject.parseObject(wxpublic);
		Integer code = Integer.parseInt(json.get("code").toString());
		if (code.equals(0)) {
			if(CommonUtil.isNotEmpty(json.get("data"))){
				JSONObject obj = JSONObject.parseObject(json.get("data").toString());
				if (CommonUtil.isNotEmpty(obj.get("guoqi"))) {//商家已过期
					return obj.get("guoqiUrl").toString();//跳到过期页面
				}
				if(CommonUtil.isNotEmpty(obj.get("remoteUcLogin"))){
					throw new BusinessException("登录错误");
				}
			}
		}else {
			throw new BusinessException("登录错误");
		}
		String otherRedisKey = PropertiesUtil.redisNamePrefix() + "authority:"+System.currentTimeMillis();
		redisService.setValue(otherRedisKey, reqUrl, 300);
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("otherRedisKey", otherRedisKey);
		queryMap.put("browser", 1);
		queryMap.put("busId", PropertiesUtil.getDuofenBusId());
		logger.info("queryMap=" + JSON.toJSONString(queryMap));
		String params = URLEncoder.encode(JSON.toJSONString(queryMap), "utf-8");
		return PropertiesUtil.getWxmpUrl() + "/remoteUserAuthoriPhoneController/79B4DE7C/authorizeMember.do?queryBody=" + params;
	}


	/**
	 * 获取是否需要登录
	 * @param member
	 * @param request
	 * @param busId
	 * @param url
	 * @return
	 * @throws Exception
	 */
	protected String getCardH5LoginReturnUrl(Member member, HttpServletRequest request, Integer busId, String url) throws Exception{
		if(CommonUtil.isEmpty(member)){
			if(CommonUtil.judgeBrowser(request) == 1){//微信
				String redirectUrl = authorizeMember(request, busId, 1, PropertiesUtil.getUnionUrl() + "/cardPhone/#/" + url);
				return GTJsonResult.instanceSuccessMsg(null,redirectUrl).toString();
			}else {//其他浏览器
				return GTJsonResult.instanceSuccessMsg("登录授权",PropertiesUtil.getUnionUrl() + "/cardPhone/#/" + "toUnionLogin?busId="+busId).toString();
			}
		}
		if(!member.getBusid().equals(busId)){
			if(CommonUtil.judgeBrowser(request) == 1){//微信
				String redirectUrl = authorizeMember(request, busId, 1, PropertiesUtil.getUnionUrl() + "/cardPhone/#/" + url);
				return GTJsonResult.instanceSuccessMsg(null,redirectUrl).toString();
			}else {//其他浏览器
				return GTJsonResult.instanceSuccessMsg("登录授权",PropertiesUtil.getUnionUrl() + "/cardPhone/#/" + "toUnionLogin?busId="+busId).toString();
			}
		}
		return null;
	}
}
