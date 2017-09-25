package com.gt.union.card.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.bean.session.Member;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.card.service.IUnionCardService;
import com.gt.union.common.amqp.entity.PhoneMessage;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.controller.MemberAuthorizeOrLoginController;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.RandomKit;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.RedisKeyUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
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

	@Value("${union.url}")
	private String unionUrl;

	@ApiOperation(value = "联盟卡首页", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/index/{busId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String indexList(HttpServletRequest request, @ApiParam(name = "busId", value = "商家id", required = true) @PathVariable("busId") Integer busId
						,@ApiParam(name = "url", value = "回调的url" ,required = true) @RequestParam(value = "url", required = true) String url) {
		try {
			Member member = SessionUtils.getLoginMember(request);
			if(CommonUtil.isEmpty(member)){
				String redirectUrl = this.authorizeMemberWx(request,unionUrl + url);
				return GTJsonResult.instanceErrorMsg("登录授权",redirectUrl).toString();
			}
			if(!member.getBusid().equals(busId)){
				String redirectUrl = this.authorizeMemberWx(request,unionUrl + url);
				return GTJsonResult.instanceErrorMsg("登录授权",redirectUrl).toString();
			}
			Map<String,Object> data = this.unionCardService.getUnionCardIndex(busId, member);
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
	@RequestMapping(value = "/79B4DE7C/login/{phone}", produces = "application/json;charset=UTF-8",method = RequestMethod.GET)
	public String getCodeByLoginPhone(HttpServletRequest request, HttpServletResponse response
			, @ApiParam(name="phone", value = "手机号", required = true) @PathVariable String phone) {
		try {
			BusUser user = SessionUtils.getLoginUser(request);
			//生成验证码
			String code = RandomKit.getRandomString(6, 0);
			Integer busId = user.getId();
			if (user.getPid() != null && user.getPid() != 0) {
				busId = user.getPid();
			}
			if (CommonUtil.isNotEmpty(phone)) {
				PhoneMessage phoneMessage = new PhoneMessage(busId,phone,"联盟卡手机登录验证码:" + code);
				Map param = new HashMap<String,Object>();
				param.put("reqdata",phoneMessage);
				if(smsService.sendSms(param) == 0){
					return GTJsonResult.instanceErrorMsg("发送失败").toString();
				}
				String phoneKey = RedisKeyUtil.getCardH5LoginPhoneKey(phone);
				redisCacheUtil.set(phoneKey , code, 300l);
				return GTJsonResult.instanceSuccessMsg(code).toString();
			}
			return GTJsonResult.instanceErrorMsg("手机号不能为空").toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "获取绑定手机号验证码", notes = "获取绑定手机号验证码", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/bind/{phone}", produces = "application/json;charset=UTF-8",method = RequestMethod.GET)
	public String getCodeByBindPhone(HttpServletRequest request, HttpServletResponse response
			, @ApiParam(name="phone", value = "手机号", required = true) @PathVariable String phone) {
		try {
			BusUser user = SessionUtils.getLoginUser(request);
			//生成验证码
			String code = RandomKit.getRandomString(6, 0);
			Integer busId = user.getId();
			if (user.getPid() != null && user.getPid() != 0) {
				busId = user.getPid();
			}
			if (CommonUtil.isNotEmpty(phone)) {
				PhoneMessage phoneMessage = new PhoneMessage(busId,phone,"联盟卡手机登录验证码:" + code);
				Map param = new HashMap<String,Object>();
				param.put("reqdata",phoneMessage);
				if(smsService.sendSms(param) == 0){
					return GTJsonResult.instanceErrorMsg("发送失败").toString();
				}
				String phoneKey = RedisKeyUtil.getCardH5BindPhoneKey(phone);
				redisCacheUtil.set(phoneKey , code, 300l);
				return GTJsonResult.instanceSuccessMsg(code).toString();
			}
			return GTJsonResult.instanceErrorMsg("手机号不能为空").toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}
}
