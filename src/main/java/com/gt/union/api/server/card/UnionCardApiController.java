package com.gt.union.api.server.card;

import com.gt.api.bean.session.Member;
import com.gt.api.dto.ResponseUtils;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.api.entity.param.BindCardParam;
import com.gt.union.api.entity.param.RequestApiParam;
import com.gt.union.api.entity.param.UnionCardDiscountParam;
import com.gt.union.api.entity.param.UnionPhoneCodeParam;
import com.gt.union.api.entity.result.UnionBindCardResult;
import com.gt.union.api.entity.result.UnionDiscountResult;
import com.gt.union.api.server.ApiBaseController;
import com.gt.union.card.service.IUnionCardBindingService;
import com.gt.union.card.service.IUnionCardService;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.util.RandomKit;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.RedisKeyUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
@RequestMapping("/api/card/8A5DA52E")
public class UnionCardApiController extends ApiBaseController {

	private Logger logger = Logger.getLogger(UnionCardApiController.class);


	@Value("${wxmp.company}")
	private String company;

	@Autowired
	private SmsService smsService;

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Autowired
	private IUnionCardBindingService unionCardBindingService;

	@Autowired
	private IUnionCardService unionCardService;

	@ApiOperation(value = "线上--根据商家和粉丝获取联盟卡折扣，session的member不能为空", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/consumeUnionDiscount", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public ResponseUtils<UnionDiscountResult> getConsumeUnionDiscount(HttpServletRequest request, HttpServletResponse response, @RequestBody RequestApiParam<UnionCardDiscountParam> requestApiParam){
		try {
			UnionCardDiscountParam param = requestApiParam.getReqdata();
			boolean verification=super.verification(request, response, requestApiParam);
			UnionDiscountResult data = unionCardService.getConsumeUnionDiscount(param.getMemberId(), param.getPhone(), param.getBusId());
			return ResponseUtils.createBySuccess(data);
		} catch (BaseException e) {
			logger.error("", e);
			UnionDiscountResult data = new UnionDiscountResult(UnionDiscountResult.UNION_DISCOUNT_CODE_NON);
			return ResponseUtils.createByErrorMessage(e.getErrorMsg());
		}catch (Exception e) {
			logger.error("", e);
			UnionDiscountResult data = new UnionDiscountResult(UnionDiscountResult.UNION_DISCOUNT_CODE_NON);
			return ResponseUtils.createByError();
		}
	}


	/**
	 * 绑定联盟卡，获取手机验证码 5分钟内有效
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation(value = "绑定联盟卡，获取手机验证码，session的member不能为空", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/phoneCode", method=RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public ResponseUtils getPhoneCode(HttpServletRequest request, HttpServletResponse response, @RequestBody RequestApiParam<UnionPhoneCodeParam> requestApiParam) {
		try {
			boolean verification=super.verification(request, response, requestApiParam);
			//生成验证码
			String code = RandomKit.getRandomString(6, 0);
			HashMap<String, Object> smsParams = new HashMap<String,Object>();
			smsParams.put("mobiles", requestApiParam.getReqdata().getPhone());
			smsParams.put("content", "绑定联盟卡，验证码:" + code);
			smsParams.put("company", company);
			smsParams.put("busId", requestApiParam.getReqdata().getBusId());
			smsParams.put("model", ConfigConstant.SMS_UNION_MODEL);
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("reqdata",smsParams);
			if(smsService.sendSms(param) == 1){
				String phoneKey = RedisKeyUtil.getMemberPhoneCodeKey(requestApiParam.getReqdata().getMemberId());
				redisCacheUtil.set(phoneKey,code,300l);//5分钟
				return ResponseUtils.createBySuccess();
			} else {
				return ResponseUtils.createByErrorMessage("发送失败");
			}
		} catch (BusinessException e) {
			e.printStackTrace();
			return ResponseUtils.createByErrorMessage(e.getErrorMsg());
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.createByError();
		}
	}


	/**
	 * 根据手机号获取联盟卡,并绑定联盟卡
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@ApiOperation( value = "根据手机号获取联盟卡,并绑定联盟卡，session的member不能为空", produces = "application/json;charset=UTF-8" )
	@RequestMapping(value = "/uionCardBind", method=RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public ResponseUtils bindUnionCard(HttpServletRequest request, HttpServletResponse response, @RequestBody RequestApiParam<BindCardParam> requestApiParam) throws IOException {
		try {
			boolean verification=super.verification(request, response, requestApiParam);
			// 获取会员信息
			UnionBindCardResult data = unionCardBindingService.bindUnionCard(requestApiParam.getReqdata().getBusId(), requestApiParam.getReqdata().getMemberId(), requestApiParam.getReqdata().getPhone(), requestApiParam.getReqdata().getCode());
			return ResponseUtils.createBySuccess();
		} catch (BaseException e) {
			logger.error("", e);
			return ResponseUtils.createByErrorMessage(e.getErrorMsg());
		}catch (Exception e) {
			logger.error("", e);
			return ResponseUtils.createByError();
		}
	}




}
