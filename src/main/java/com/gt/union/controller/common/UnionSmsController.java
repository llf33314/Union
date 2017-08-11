package com.gt.union.controller.common;

import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.RedisCacheUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 联盟短信发送控制器
 * Created by Administrator on 2017/8/9 0009.
 */
@RestController
@RequestMapping("/unionSms")
public class UnionSmsController {

	private Logger logger = LoggerFactory.getLogger(UnionSmsController.class);

	@Autowired
	private RedisCacheUtil redisCacheUtil;



	@ApiOperation(value = "盟主审核确认退盟发送短信"
			, notes = "盟主审核确认退盟发送短信"
			, produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/memberOut", method= RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String confirmOutSendMsg(HttpServletRequest request, HttpServletResponse response,
									@ApiParam(name = "redisKey", value = "审核确认退盟后返回的数据", required = true)
									@RequestParam(name = "redisKey", required = true) String redisKey) throws IOException {
		try {
			Object data = redisCacheUtil.get(redisKey);
			if(data == null){
				throw new BusinessException("redis失效");
			}
		} catch (Exception e) {
			return GTJsonResult.instanceErrorMsg().toString();
		}
		return GTJsonResult.instanceSuccessMsg().toString();
	}

	@ApiOperation(value = "盟员退盟申请发送短信" , notes = "盟员退盟申请发送短信" , produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/memberOutApply", method= RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String memberOutApply(HttpServletRequest request, HttpServletResponse response,
									@ApiParam(name = "redisKey", value = "盟员退盟申请后返回的数据", required = true)
									@RequestParam(name = "redisKey", required = true) String redisKey) throws IOException {
		try {

		} catch (Exception e) {

		}
		return GTJsonResult.instanceSuccessMsg().toString();
	}


	@ApiOperation(value = "商机审核后发送短信" , notes = "商机审核后发送短信" , produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/businessVerify", method= RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String businessVerify(HttpServletRequest request, HttpServletResponse response,
								 @ApiParam(name = "redisKey", value = "商机审核后返回的数据", required = true)
								 @RequestParam(name = "redisKey", required = true) String redisKey) throws IOException {
		try {

		} catch (Exception e) {

		}
		return GTJsonResult.instanceSuccessMsg().toString();
	}



	@ApiOperation(value = "推荐商机后发送短信" , notes = "推荐商机后发送短信" , produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/businessRecommend", method= RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String businessRecommend(HttpServletRequest request, HttpServletResponse response,
								 @ApiParam(name = "redisKey", value = "推荐商机后返回的数据", required = true)
								 @RequestParam(name = "redisKey", required = true) String redisKey) throws IOException {
		try {

		} catch (Exception e) {

		}
		return GTJsonResult.instanceSuccessMsg().toString();
	}



	@ApiOperation(value = "申请加盟成功后发送短信" , notes = "申请加盟成功后发送短信" , produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionApply", method= RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String unionApply(HttpServletRequest request, HttpServletResponse response,
									@ApiParam(name = "redisKey", value = "申请加盟成功后返回的数据", required = true)
									@RequestParam(name = "redisKey", required = true) String redisKey) throws IOException {
		try {

		} catch (Exception e) {

		}
		return GTJsonResult.instanceSuccessMsg().toString();
	}

}
