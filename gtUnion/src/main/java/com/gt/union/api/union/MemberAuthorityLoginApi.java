package com.gt.union.api.union;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.bean.session.Member;
import com.gt.api.util.KeysUtil;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 手机端粉丝登录授权API
 * @author hongjiye
 * @time 2017-11-24 17:39
 *
 */
@Component
public class MemberAuthorityLoginApi {

	private Logger logger = LoggerFactory.getLogger(MemberAuthorityLoginApi.class);

	/**
	 *	uc登录或微信授权登录
	 * @param request
	 * @param busId		登录授权的商家id
	 * @param ucLogin	是否可以在uc登录  false：不能在uc登录 true：可以在uc登录
	 * @param reqUrl	请求的url，即授权登录后重定向的链接
	 * @param ucLoginUrl	自定义uc登录的地址 如果没有自定义的，可以不填
	 * @return
	 * @throws Exception
	 */
	public GtJsonResult authorizeMember(HttpServletRequest request, Integer busId, boolean ucLogin, String reqUrl, String ucLoginUrl) {
		Map param = new HashMap<>();
		param.put("busId",busId);
		param.put("ucLogin",ucLogin);
		param.put("reqUrl",reqUrl);
		param.put("ucLoginUrl",ucLoginUrl);
		logger.info("进入--联盟手机端用户授权，请求参数：{}",JSON.toJSONString(param));
		//判断浏览器  1：微信  99：其他浏览器
		Integer browser = CommonUtil.judgeBrowser(request);
		Map<String, Object> getWxPublicMap = new HashMap<>();
		getWxPublicMap.put("busId", busId);
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/busUserApi/getWxPulbicMsg.do";
		try{
			String wxpublic = SignHttpUtils.WxmppostByHttp(url, getWxPublicMap, PropertiesUtil.getWxmpSignKey());
			if(StringUtil.isEmpty(wxpublic)){
				return GtJsonResult.instanceErrorMsg("登录错误");
			}
			JSONObject json = JSONObject.parseObject(wxpublic);
			if ("0".equals(json.get("code").toString())) {
				if(CommonUtil.isNotEmpty(json.get("data"))){
					JSONObject obj = JSONObject.parseObject(json.get("data").toString());
					if (CommonUtil.isNotEmpty(obj.get("guoqi"))) {
						//商家已过期 跳到过期页面
						return GtJsonResult.instanceSuccessMsg(null,obj.get("guoqiUrl").toString());
					}
					//uc 登录
					if(ucLogin && CommonUtil.isNotEmpty(obj.get("remoteUcLogin"))){
						if(StringUtil.isNotEmpty(ucLoginUrl)){
							return GtJsonResult.instanceSuccessMsg(null,ucLoginUrl);
						}
					}
				}
			}else {
				return GtJsonResult.instanceErrorMsg("登录错误");
			}
			reqUrl = KeysUtil.getEncString(reqUrl);
		}catch (Exception e){
			logger.error("uc登录或微信授权登录错误=======>", e);
			return GtJsonResult.instanceErrorMsg("登录错误");
		}

		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("returnUrl", reqUrl);
		queryMap.put("browser", browser);
		queryMap.put("busId", busId);
		queryMap.put("uclogin", ucLogin ? null : 1);
		logger.info("手机端登录授权queryMap：{}", JSON.toJSONString(queryMap));
		return GtJsonResult.instanceSuccessMsg(null,PropertiesUtil.getWxmpUrl() + "/remoteUserAuthoriPhoneController/79B4DE7C/authorizeMemberNew.do?queryBody=" + JSON.toJSONString(queryMap));
	}


	/**
	 * 微信授权（多粉账号）登录
	 * @param request
	 * @param reqUrl	授权收重定向的地址
	 * @return
	 * @throws Exception
	 */
	public GtJsonResult authorizeMemberWx(HttpServletRequest request, String reqUrl) {
		logger.info("进入--联盟手机端微信授权方法，重定向地址：{}", reqUrl);
		//判断浏览器
		Integer browser = CommonUtil.judgeBrowser(request);
		if(browser != 1){
			return GtJsonResult.instanceErrorMsg("请使用微信登录");
		}
		Map<String, Object> getWxPublicMap = new HashMap<>();
		getWxPublicMap.put("busId", PropertiesUtil.getDuofenBusId());
		//判断商家信息 1是否过期 2公众号是否变更过
		String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/busUserApi/getWxPulbicMsg.do";
		try{
			String wxpublic = SignHttpUtils.WxmppostByHttp(url, getWxPublicMap, PropertiesUtil.getWxmpSignKey());
			if(StringUtil.isEmpty(wxpublic)){
				return GtJsonResult.instanceErrorMsg("登录错误");
			}
			JSONObject json = JSONObject.parseObject(wxpublic);
			if ("0".equals(json.get("code").toString())) {
				if(CommonUtil.isNotEmpty(json.get("data"))){
					JSONObject obj = JSONObject.parseObject(json.get("data").toString());
					if (CommonUtil.isNotEmpty(obj.get("guoqi"))) {
						//商家已过期
						return GtJsonResult.instanceSuccessMsg(null, obj.get("guoqiUrl").toString());
					}
					if(CommonUtil.isNotEmpty(obj.get("remoteUcLogin"))){
						return GtJsonResult.instanceErrorMsg("登录错误");
					}
				}
			}else {
				return GtJsonResult.instanceErrorMsg("登录错误");
			}
			reqUrl = KeysUtil.getEncString( reqUrl );
		}catch (Exception e){
			logger.error("微信授权（多粉账号）登录错误=======>", e);
			return GtJsonResult.instanceErrorMsg("登录错误");
		}
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("returnUrl", reqUrl);
		queryMap.put("browser", browser);
		queryMap.put("uclogin", null);
		queryMap.put("busId", PropertiesUtil.getDuofenBusId());
		logger.info("手机端微信登录授权queryMap：{}" + JSON.toJSONString(queryMap));
		return GtJsonResult.instanceSuccessMsg(null,PropertiesUtil.getWxmpUrl() + "/remoteUserAuthoriPhoneController/79B4DE7C/authorizeMemberNew.do?queryBody=" + JSON.toJSONString(queryMap));
	}

}
