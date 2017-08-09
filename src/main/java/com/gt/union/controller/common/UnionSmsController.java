package com.gt.union.controller.common;

import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
 * 联盟短信发送控制器
 * Created by Administrator on 2017/8/9 0009.
 */
@RequestMapping("/unionSms")
@RestController
public class UnionSmsController {


	@ApiOperation(value = "盟主审核确认退盟发送短信"
			, notes = "盟主审核确认退盟发送短信"
			, produces = "application/json;charset=UTF-8")
	@SysLogAnnotation(description = "盟主审核确认退盟发送短信", op_function = "2")
	@RequestMapping(value = "/memberOut", method= RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String confirmOutSendMsg(HttpServletRequest request, HttpServletResponse response,
									@ApiParam(name = "redisKey", value = "审核确认退盟后返回的数据", required = true)
									@RequestParam(name = "redisKey", required = true) String redisKey) throws IOException {
		try {

		} catch (Exception e) {

		}
		return GTJsonResult.instanceSuccessMsg().toString();
	}

}
