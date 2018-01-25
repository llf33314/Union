package com.gt.union.api.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.amqp.entity.TemplateSmsMessage;
import com.gt.union.api.amqp.sender.PhoneMessageSender;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.api.client.sms.constant.SmsConstant;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.service.IUnionCardFanService;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.card.main.vo.CardApplyVO;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.constant.SmsCodeConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private Logger logger = LoggerFactory.getLogger(SmsApiController.class);

	@Autowired
	private PhoneMessageSender sender;

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Autowired
	private SmsService smsService;

	@Autowired
	private IUnionCardService unionCardService;

	@Autowired
	private IUnionCardFanService unionCardFanService;

	@ApiOperation(value = "发送短信验证码", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/{type}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public GtJsonResult sendMsg(HttpServletRequest request,
						@ApiParam(value = "功能模块类型 1：联盟卡登录 2：办理联盟卡 3：佣金平台登录 4：联盟卡绑定 5：佣金平台管理员", name = "type")
						@PathVariable(value = "type") Integer type,
						@ApiParam(value = "手机号", name = "phone")
						@RequestParam(value = "phone", required = true) String phone) throws Exception {
		BusUser user = SessionUtils.getLoginUser(request);

		String code = RandomKit.getRandomString(6, 0);
		Long templateId = 0L;
		switch (type){
			case SmsCodeConstant.UNION_CARD_LOGIN_TYPE:
				break;
			case SmsCodeConstant.APPLY_UNION_CARD_TYPE:
				templateId = SmsConstant.APPLY_CARD_TEMPLATE_ID;
				break;
			case SmsCodeConstant.BROKERAGE_LOGIN_TYPE:
				templateId = SmsConstant.BROKERAGE_LOGIN_CODE_TEMPLATE_ID;
				break;
			case SmsCodeConstant.UNION_CARD_PHONE_BIND_TYPE:
				templateId = SmsConstant.BINDING_CARD_CODE_TEMPLATE_ID;
				break;
			case SmsCodeConstant.UNION_VERIFIER_TYPE:
				templateId = SmsConstant.BROKERAGE_VERIFIER_CODE_TEMPLATE_ID;
				break;
			default:
				break;
		}
		if(SmsCodeConstant.APPLY_UNION_CARD_TYPE == type){
			//办理联盟卡
			Integer busId = user.getId();
			if (user.getPid() != null && user.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
				busId = user.getPid();
			}
			try{
				UnionCardFan fan = unionCardFanService.getOrSaveByPhone(phone);
				// TODO
//				CardApplyVO vo = unionCardService.getCardApplyVOByBusIdAndFanId(busId, fan.getId(), null);
//				if(vo == null){
//					return GtJsonResult.instanceErrorMsg("您已办理联盟卡");
//				}
			}catch (Exception e){
				logger.error("办理联盟卡发送短信校验是否办卡校验出错", e);
			}

		}
		TemplateSmsMessage msg = new TemplateSmsMessage();
		msg.setBusId(CommonUtil.isNotEmpty(user) ? user.getId() : PropertiesUtil.getDuofenBusId());
		msg.setMobile(phone);
		msg.setTmplId(templateId);
		msg.setParamsStr(code);
		msg.setModel(ConfigConstant.SMS_UNION_MODEL);
		sender.sendMsg(msg);

		redisCacheUtil.set("phoneCode:" + type + ":" + phone, code, 300L);
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
