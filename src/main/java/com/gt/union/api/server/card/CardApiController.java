package com.gt.union.api.server.card;

import com.alibaba.fastjson.JSON;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.api.entity.UnionBindCardResult;
import com.gt.union.api.entity.UnionDiscountResult;
import com.gt.union.api.entity.UnionPhoneCodeResult;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.entity.common.Member;
import com.gt.union.service.card.IUnionBusMemberCardService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 联盟卡对外服务接口
 * Created by Administrator on 2017/8/23 0023.
 */
@RestController
@RequestMapping("/api")
public class CardApiController {

	private static final String GET_CONSUME_UNION_DISCOUNT = "UnionApplyInfoController.getConsumeUnionDiscount()";
	private static final String GET_PHONE_CODE = "UnionApplyInfoController.getPhoneCode()";
	private static final String BIND_UNION_CARD = "UnionApplyInfoController.bindUnionCard()";

	private Logger logger = Logger.getLogger(CardApiController.class);

	@Autowired
	private IUnionBusMemberCardService unionBusMemberCardService;

	@Value("${wxmp.company}")
	private String company;

	@Autowired
	private SmsService smsService;

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@ApiOperation(value = "获取联盟卡折扣", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/consumeUnionDiscount", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public GTJsonResult<UnionDiscountResult> getConsumeUnionDiscount(HttpServletRequest request,
					@ApiParam(name="memberId", value = "用户id", required = true) @RequestParam(name = "memberId", required = true) Integer memberId
					,@ApiParam(name="busId", value = "商家id", required = true) @RequestParam(name = "busId", required = true) Integer busId ){
		try {
			UnionDiscountResult data = unionBusMemberCardService.getConsumeUnionDiscount(memberId, busId);
			return GTJsonResult.instanceSuccessMsg(data);
		} catch (BaseException e) {
			logger.error("", e);
			UnionDiscountResult data = new UnionDiscountResult(UnionDiscountResult.UNION_DISCOUNT_CODE_NON);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), data);
		}catch (Exception e) {
			logger.error("", e);
			UnionDiscountResult data = new UnionDiscountResult(UnionDiscountResult.UNION_DISCOUNT_CODE_NON);
			return GTJsonResult.instanceErrorMsg(GET_CONSUME_UNION_DISCOUNT, e.getMessage(), data);
		}
	}


	/**
	 * 绑定联盟卡，获取手机验证码 5分钟内有效
	 * @param request
	 * @param response
	 * @param phone	电话号码
	 * @return
	 */
	@ApiOperation(value = "绑定联盟卡，获取手机验证码", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/79B4DE7C/phoneCode", method=RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public GTJsonResult<UnionPhoneCodeResult> getPhoneCode(HttpServletRequest request, HttpServletResponse response,
					   @ApiParam(name="phone", value = "电话号码", required = true) @RequestParam(name = "phone", required = true) String phone) {
		try {
			Member member = SessionUtils.getLoginMember(request);
			//生成验证码
			String code = RandomKit.getRandomString(6, 0);
			HashMap<String, Object> smsParams = new HashMap<String,Object>();
			smsParams.put("mobiles", phone);
			smsParams.put("content", "绑定联盟卡，验证码:" + code);
			smsParams.put("company", company);
			//smsParams.put("busId", member.getBusid());
			smsParams.put("busId", 33);
			smsParams.put("model", CommonConstant.SMS_UNION_MODEL);
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("reqdata",smsParams);
			if(smsService.sendSms(param) == 1){
				//String phoneKey = RedisKeyUtil.getMemberPhoneCodeKey(member.getId());
				String phoneKey = RedisKeyUtil.getMemberPhoneCodeKey(123);
				redisCacheUtil.set(phoneKey,code,300l);//5分钟
				UnionPhoneCodeResult result = new UnionPhoneCodeResult(true, "发送成功");
				return GTJsonResult.instanceSuccessMsg(result);
			} else {
				UnionPhoneCodeResult result = new UnionPhoneCodeResult(true, "发送失败");
				return GTJsonResult.instanceErrorMsg(GET_PHONE_CODE,"发送短信验证码失败", result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			UnionPhoneCodeResult result = new UnionPhoneCodeResult(true, "发送失败");
			return GTJsonResult.instanceErrorMsg(GET_PHONE_CODE,e.getMessage(), result);
		}
	}


	/**
	 * 根据手机号获取联盟卡,并绑定联盟卡
	 * @param request
	 * @param response
	 * @param phone	电话
	 * @param code	验证码
	 * @throws IOException
	 */
	@RequestMapping(value = "/79B4DE7C/bindUnionCard", method=RequestMethod.POST)
	public GTJsonResult<UnionBindCardResult> bindUnionCard(HttpServletRequest request, HttpServletResponse response,
							   @ApiParam(name="phone", value = "电话号码", required = true) @RequestParam(name = "phone", required = true) String phone
							  , @ApiParam(name="code", value = "验证码", required = true) @RequestParam(name = "code", required = true) String code) throws IOException {
		try {
			// 获取会员信息
			Member member = SessionUtils.getLoginMember(request);
			UnionBindCardResult data = unionBusMemberCardService.bindUnionCard(member.getBusid(), member.getId(), phone, code);
			return GTJsonResult.instanceSuccessMsg(data);
		} catch (BaseException e) {
			logger.error("", e);
			UnionBindCardResult data = new UnionBindCardResult();
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), data);
		}catch (Exception e) {
			logger.error("", e);
			UnionBindCardResult data = new UnionBindCardResult();
			return GTJsonResult.instanceErrorMsg(BIND_UNION_CARD, e.getMessage(), data);
		}
	}
}
