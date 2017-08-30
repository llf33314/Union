package com.gt.union.controller.common;

import com.alibaba.fastjson.JSON;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.service.basic.IUnionApplyService;
import com.gt.union.service.basic.IUnionMemberService;
import com.gt.union.service.business.IUnionBusinessRecommendService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 联盟短信发送控制器
 * Created by Administrator on 2017/8/9 0009.
 */
@RestController
@RequestMapping("/unionSms")
public class UnionSmsController {
	private static final String CONFIRM_OUT_CONFIRM_MSG = "UnionSmsController.confirmOutConfirmdMsg()";
	private static final String UNION_APPLY_MSG = "UnionSmsController.unionApplyMsg()";
	private static final String BUSINESS_RECOMMEND_MSG = "UnionSmsController.businessRecommendMsg()";
	private Logger logger = LoggerFactory.getLogger(UnionSmsController.class);

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Autowired
	private IUnionMemberService unionMemberService;

	@Autowired
	private SmsService smsService;

	@Autowired
	private IUnionApplyService unionApplyService;

	@Autowired
	private IUnionBusinessRecommendService unionBusinessRecommendService;

	@ApiOperation(value = "盟主审核确认退盟发送短信", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/memberOutConfirm", method= RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String confirmOutConfirmdMsg(HttpServletRequest request, HttpServletResponse response
		, @ApiParam(name = "redisKey", value = "审核确认退盟后返回的数据", required = true)
		@RequestParam(name = "redisKey", required = true) String redisKey) throws IOException {
		try {
			if(StringUtil.isEmpty(redisKey)){
				throw new ParamException(CONFIRM_OUT_CONFIRM_MSG, "参数redisKey为空", "参数redisKey为空");
			}
			Object data = redisCacheUtil.get(redisKey);
			if(data == null){
				throw new ParamException(CONFIRM_OUT_CONFIRM_MSG, "", "redis失效");
			}
			//发送短信
			return GTJsonResult.instanceSuccessMsg().toString();
        } catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		} catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(CONFIRM_OUT_CONFIRM_MSG, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
	}


	@ApiOperation(value = "商机审核后发送短信", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/businessVerify", method= RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String businessVerify(HttpServletRequest request, HttpServletResponse response
        , @ApiParam(name = "redisKey", value = "商机审核后返回的数据", required = true)
        @RequestParam(name = "redisKey", required = true) String redisKey) throws IOException {
		try {

		} catch (Exception e) {

		}
		return GTJsonResult.instanceSuccessMsg().toString();
	}



}
