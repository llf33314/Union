package com.gt.union.card.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.bean.session.BusUser;
import com.gt.api.bean.session.Member;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.client.member.MemberService;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.card.service.IUnionCardService;
import com.gt.union.card.vo.UnionCardBindParamVO;
import com.gt.union.common.amqp.entity.PhoneMessage;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.controller.MemberAuthorizeOrLoginController;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/23 0023.
 */
@RestController
@RequestMapping("/cardH5/79B4DE7C")
public class UnionCardH5Controller extends MemberAuthorizeOrLoginController{

	private Logger logger = LoggerFactory.getLogger(UnionCardH5Controller.class);

	@Autowired
	private IUnionCardService unionCardService;

	@Autowired
	private SmsService smsService;

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Autowired
	private WxPayService wxPayService;

	@Autowired
	private MemberService memberService;

	@ApiOperation(value = "联盟卡首页", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/index/{busId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String indexList(HttpServletRequest request, @ApiParam(name = "busId", value = "商家id", required = true) @PathVariable("busId") Integer busId
						,@ApiParam(name = "url", value = "回调的url" ,required = true) @RequestParam(value = "url", required = true) String url) {
		try {
			Member member = SessionUtils.getLoginMember(request);
			String returnLoginUrl = this.getCardH5LoginReturnUrl(member,request,busId,url);
			if(StringUtil.isNotEmpty(returnLoginUrl)){
				return returnLoginUrl;
			}
			Map<String,Object> data = this.unionCardService.getUnionCardIndex(busId, member);
			data.put("phone",member.getPhone());
			return GTJsonResult.instanceSuccessMsg(data).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "加载联盟下的盟员和联盟卡信息", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/info/{busId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String unionInfoCardList(HttpServletRequest request, @ApiParam(name = "busId", value = "商家id", required = true) @PathVariable("busId") Integer busId
			,@ApiParam(name = "url", value = "回调的url" ,required = true) @RequestParam(value = "url", required = true) String url
			,@ApiParam(name = "unionId", value = "联盟id", required = true) @RequestParam("unionId") Integer unionId
			,@ApiParam(name = "memberId", value = "联盟id", required = true) @RequestParam("memberId") Integer memberId) {
		try {
			Member member = SessionUtils.getLoginMember(request);
			member = memberService.getById(998);
			String returnLoginUrl = this.getCardH5LoginReturnUrl(member,request,busId,url);
			if(StringUtil.isNotEmpty(returnLoginUrl)){
				return returnLoginUrl;
			}
			Map<String,Object> data = this.unionCardService.getUnionInfoCardList(busId, member, unionId, memberId);
			return GTJsonResult.instanceSuccessMsg(data).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "获取联盟卡手机登录验证码", notes = "获取联盟卡手机登录验证码", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/login/{phone}", produces = "application/json;charset=UTF-8",method = RequestMethod.GET)
	public String getCodeByLoginPhone(HttpServletRequest request, HttpServletResponse response
			, @ApiParam(name="phone", value = "手机号", required = true) @PathVariable String phone
			, @ApiParam(name="busId", value = "商家id", required = true) @RequestParam("busId") Integer busId) {
		try {
			//生成验证码
			String code = RandomKit.getRandomString(6, 0);
			if (CommonUtil.isNotEmpty(phone)) {
				PhoneMessage phoneMessage = new PhoneMessage(busId,phone,"联盟卡手机登录验证码:" + code);
				Map param = new HashMap<String,Object>();
				param.put("reqdata",phoneMessage);
				if(smsService.sendSms(param) == 0){
					return GTJsonResult.instanceErrorMsg("发送失败").toString();
				}
				String phoneKey = RedisKeyUtil.getCardH5LoginPhoneKey(phone);
				redisCacheUtil.set(phoneKey , code, 300l);
				return GTJsonResult.instanceSuccessMsg().toString();
			}
			return GTJsonResult.instanceErrorMsg("手机号不能为空").toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "获取绑定手机号验证码", notes = "获取绑定手机号验证码", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/code/{phone}", produces = "application/json;charset=UTF-8",method = RequestMethod.GET)
	public String getCodeByBindPhone(HttpServletRequest request, HttpServletResponse response
			, @ApiParam(name="phone", value = "手机号", required = true) @PathVariable String phone
			, @ApiParam(name="busId", value = "商家id", required = true) @RequestParam("busId") Integer busId) {
		try {
			//生成验证码
			String code = RandomKit.getRandomString(6, 0);
			if (CommonUtil.isNotEmpty(phone)) {
				PhoneMessage phoneMessage = new PhoneMessage(busId,phone,"联盟卡手机绑定验证码:" + code);
				Map param = new HashMap<String,Object>();
				param.put("reqdata",phoneMessage);
				if(smsService.sendSms(param) == 0){
					return GTJsonResult.instanceErrorMsg("发送失败").toString();
				}
				String phoneKey = RedisKeyUtil.getCardH5BindPhoneKey(phone);
				redisCacheUtil.set(phoneKey , code, 300l);
				return GTJsonResult.instanceSuccessMsg().toString();
			}
			return GTJsonResult.instanceErrorMsg("手机号不能为空").toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "手机号、验证码登录", notes = "手机号、验证码登录", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/login/{phone}", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
	public String loginPhone(HttpServletRequest request, HttpServletResponse response
			, @ApiParam(name="phone", value = "手机号", required = true) @PathVariable String phone
			, @ApiParam(name="code", value = "验证码", required = true) @RequestParam("code") String code
			, @ApiParam(name="busId", value = "商家id", required = true) @RequestParam("busId") Integer busId) {
		try {
			Member member = memberService.findByPhoneAndBusId(phone,busId);
			if(member == null){
				throw new BusinessException("用户不存在");
			}
			request.getSession().setAttribute(SessionUtils.SESSION_MEMBER, JSONObject.toJSONString(member));
			return GTJsonResult.instanceErrorMsg().toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}


	@ApiOperation(value = "绑定手机号", notes = "绑定手机号", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/bind/{phone}", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
	public String bindCardPhone(HttpServletRequest request, HttpServletResponse response
			,@ApiParam(name="phone", value = "手机号", required = true) @PathVariable String phone
			,@ApiParam(name="busId", value = "商家id", required = true) @RequestParam("busId") Integer busId
			,@ApiParam(name = "url", value = "回调的url" ,required = true) @RequestParam(value = "url", required = true) String url) {
		try {
			Member member = SessionUtils.getLoginMember(request);
			member = memberService.getById(998);
			String returnLoginUrl = this.getCardH5LoginReturnUrl(member,request,busId,url);
			if(StringUtil.isNotEmpty(returnLoginUrl)){
				return returnLoginUrl;
			}
			unionCardService.bindCardPhone(member,busId,phone);
			return GTJsonResult.instanceSuccessMsg().toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "获取二维码图片链接", notes = "获取二维码图片链接", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/79B4DE7C/cardNoImg", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
	public void cardNoImges(HttpServletRequest request,
							HttpServletResponse response, @ApiParam(name="cardNo", value = "联盟卡号", required = true) @RequestParam("cardNo") String cardNo) throws UnsupportedEncodingException {
		String encrypt = EncryptUtil.encrypt(ConfigConstant.UNION_ENCRYPTKEY, cardNo);//加密后参数
		encrypt = URLEncoder.encode(encrypt,"UTF-8");
		QRcodeKit.buildQRcode(encrypt, 250, 250, response);
	}


	@ApiOperation(value = "办理联盟卡", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method=RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String bindCard(HttpServletRequest request, HttpServletResponse response, @ApiParam(name = "url", value = "回调的url" ,required = true) @RequestParam(value = "url", required = true) String url
			, @ApiParam(name="unionCardBindParamVO", value = "办理联盟卡参数", required = true) @RequestBody @Valid UnionCardBindParamVO vo, BindingResult bindingResult ) {
		try {
			Member member = SessionUtils.getLoginMember(request);
			member = memberService.getById(998);
			Integer busId = vo.getBusId();
			String returnLoginUrl = this.getCardH5LoginReturnUrl(member,request,busId,url);
			if(StringUtil.isNotEmpty(returnLoginUrl)){
				return returnLoginUrl;
			}
			if(CommonUtil.isEmpty(member.getPhone())){
				throw new BusinessException("请绑定手机号");
			}
			Map<String,Object> data = unionCardService.bindCard(vo);
			if(CommonUtil.isNotEmpty(data.get("qrurl"))){
				String returnUrl = ConfigConstant.UNION_PHONE_ROOT_URL + url;
				Map<String,Object> qrCodeData = unionCardService.createQRCode(busId, vo.getPhone(), member.getId(),vo.getUnionId(), vo.getCardType(), 1, returnUrl);
				Map<String,Object> param = new HashMap<String,Object>();
				param.put("totalFee", qrCodeData.get("totalFee"));
				param.put("model", qrCodeData.get("model"));
				param.put("busId", qrCodeData.get("busId"));
				param.put("appidType", qrCodeData.get("appidType"));
				param.put("appid", qrCodeData.get("appid"));
				param.put("orderNum", qrCodeData.get("orderNum"));
				param.put("memberId", member.getId());
				param.put("desc", qrCodeData.get("desc"));
				param.put("isreturn", qrCodeData.get("isreturn"));
				param.put("returnUrl", qrCodeData.get("returnUrl"));
				param.put("notifyUrl", qrCodeData.get("notifyUrl"));
				param.put("isSendMessage", qrCodeData.get("isSendMessage"));
				param.put("payWay", qrCodeData.get("payWay"));
				param.put("sourceType", qrCodeData.get("sourceType"));
				data.put("qrurl",wxPayService.wxPay(param));
			}
			return GTJsonResult.instanceSuccessMsg(data).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}


}
