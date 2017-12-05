package com.gt.union.api.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.amqp.entity.PhoneMessage;
import com.gt.union.api.amqp.sender.PhoneMessageSender;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.SmsCodeConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.RandomKit;
import com.gt.union.common.util.RedisCacheUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hongjiye
 * @time 2017-12-05 14:05
 **/
@Api(description = "短信服务RestApi")
@RestController
@RequestMapping("/api/sms")
public class SmsApiController {

	@Autowired
	private PhoneMessageSender sender;

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Autowired
	private SmsService smsService;

	@ApiOperation(value = "发送短信验证码", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/{type}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public GtJsonResult sendMsg(HttpServletRequest request,
						@ApiParam(value = "功能模块类型 1：联盟卡登录 2：办理联盟卡 3：佣金平台登录 4：联盟卡绑定 5：佣金平台管理员", name = "type")
						@PathVariable(value = "type") Integer type,
						@ApiParam(value = "手机号", name = "phone")
						@RequestParam(value = "phone", required = true) String phone) throws Exception {
		BusUser user = SessionUtils.getLoginUser(request);
		String code = RandomKit.getRandomString(6, 0);
		String content = "";
		switch (type){
			case SmsCodeConstant.UNION_CARD_LOGIN_TYPE:
				content = SmsCodeConstant.UNION_CARD_LOGIN_MSG;
				break;
			case SmsCodeConstant.APPLY_UNION_CARD_TYPE:
				content = SmsCodeConstant.APPLY_UNION_CARD_MSG;
				break;
			case SmsCodeConstant.BROKERAGE_LOGIN_TYPE:
				content = SmsCodeConstant.BROKERAGE_LOGIN_MSG;
				break;
			case SmsCodeConstant.UNION_CARD_PHONE_BIND_TYPE:
				content = SmsCodeConstant.UNION_CARD_PHONE_BIND_MSG;
				break;
			case SmsCodeConstant.UNION_VERIFIER_TYPE:
				content = SmsCodeConstant.UNION_VERIFIER_MSG;
				break;
			default:
				break;
		}
		sender.sendMsg(new PhoneMessage(CommonUtil.isNotEmpty(user) ? user.getId() : null,phone, content + code));
		redisCacheUtil.set(type + ":" + phone, code, 300L);
		return GtJsonResult.instanceSuccessMsg();
	}

	@ApiOperation(value = "前端校验短信验证码", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/{type}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public GtJsonResult checkPhoneCode(HttpServletRequest request,
								@ApiParam(value = "功能模块类型 2：办理联盟卡", name = "type")
								@PathVariable(value = "type") Integer type,
								@ApiParam(value = "手机号", name = "phone")
								@RequestParam(value = "phone", required = true) String phone,
							   @ApiParam(value = "验证码", name = "code")
							   @RequestParam(value = "code", required = true) String code) throws Exception {
		if(!smsService.checkPhoneCode(type,code,phone)){
			return GtJsonResult.instanceErrorMsg(CommonConstant.CODE_ERROR_MSG);
		}
		return GtJsonResult.instanceSuccessMsg();
	}
}
