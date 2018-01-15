package com.gt.union.api.client.member.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.bean.session.Member;
import com.gt.api.util.KeysUtil;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.member.MemberService;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.ApiResultHandlerUtil;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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
			logger.info("根据商家id和用户ids查询列表信息，结果：{}", data);
			return ApiResultHandlerUtil.listDataObject(data,Map.class);
		}catch (Exception e){
			logger.error("根据商家id和用户ids查询列表信息错误",e);
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
			logger.info("根据粉丝用户id获取用户信息，结果：{}", data);
			return ApiResultHandlerUtil.getDataObject(data,Member.class);
		}catch (Exception e){
			logger.error("根据粉丝用户id获取用户信息错误",e);
			return null;
		}
	}

	@Override
	public Member getByPhoneAndBusId(String phone, Integer busId) {
		logger.info("根据手机号：{}和商家id：{}获取用户信息", phone, busId);
		String url = PropertiesUtil.getMemberUrl() + "/memberAPI/member/findMemberByPhone";
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("phone",phone);
		param.put("busId",busId);
		try {
			String data = SignHttpUtils.WxmppostByHttp(url,param,PropertiesUtil.getMemberSignKey());
			logger.info("根据手机号和商家id获取用户信息，结果：{}", data);
			Member member = ApiResultHandlerUtil.getDataObject(data,Member.class);
			return member;
		}catch (Exception e){
			logger.error("根据手机号和商家id获取用户信息错误",e);
			return null;
		}
	}

	@Override
	public boolean bindMemberPhone(Integer busId, Integer memberId, String phone) throws Exception{
		String url = PropertiesUtil.getMemberUrl() + "/memberAPI/member/updateMemberPhoneByMemberId";
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("phone",phone);
		param.put("busId",busId);
		param.put("memberId",memberId);
		logger.info("绑定粉丝用户手机号，请求参数：{}", JSON.toJSONString(param));
		return httpRequestMemberApi(param,url);
	}

	@Override
	public boolean bindMemberPhoneApp(Integer busId, Integer memberId, String phone) throws Exception {
		String url = PropertiesUtil.getMemberUrl() + "/memberAPI/member/bingdingPhone";
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("phone",phone);
		param.put("busId",busId);
		param.put("memberId",memberId);
		logger.info("小程序粉丝用户手机号登录，请求参数：{}", JSON.toJSONString(param));
		return httpRequestMemberApi(param,url);
	}

	@Override
	public boolean loginMemberByPhone(String phone, Integer busId) throws Exception{
		String url = PropertiesUtil.getMemberUrl() + "/memberAPI/member/bingdingPhoneH5";
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("phone",phone);
		param.put("busId",busId);
		logger.info("H5粉丝用户手机号登录，请求参数：{}", JSON.toJSONString(param));
		return httpRequestMemberApi(param,url);
	}

	@Override
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

	@Override
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

	/**
	 * 请求粉丝会员接口，返回参数
	 * @param param
	 * @param url
	 * @return
	 */
	private boolean httpRequestMemberApi(Map param, String url) throws Exception{
		try {
			String data = SignHttpUtils.WxmppostByHttp(url,param,PropertiesUtil.getMemberSignKey());
			if(StringUtil.isEmpty(data)){
				return false;
			}
			Map map = JSONObject.parseObject(data,Map.class);
			if(!("0".equals(map.get("code").toString()))){
				throw new BusinessException(CommonUtil.isNotEmpty(map.get("msg")) ? map.get("msg").toString() : "请求失败");
			}
		}catch (BaseException e){
			logger.error("调取会员接口转换数据错误", e);
			throw new BaseException(e.getMessage());
		}catch (Exception e){
			logger.error("调取会员接口转换数据错误", e);
			throw new Exception(e.getMessage());
		}
		return true;
	}
}
