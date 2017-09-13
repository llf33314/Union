package com.gt.union.api.server.consume;

import com.gt.api.dto.ResponseUtils;
import com.gt.union.api.entity.param.RequestApiParam;
import com.gt.union.api.entity.param.UnionConsumeParam;
import com.gt.union.api.entity.param.UnionRefundParam;
import com.gt.union.api.entity.result.UnionConsumeResult;
import com.gt.union.api.entity.result.UnionRefundResult;
import com.gt.union.api.server.ApiBaseController;
import com.gt.union.common.exception.BaseException;
import com.gt.union.consume.service.IUnionConsumeService;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2017/9/4 0004.
 */
@RestController
@RequestMapping("/api/consume/8A5DA52E")
public class UnionConsumeApiController extends ApiBaseController{

	private Logger logger = Logger.getLogger(UnionConsumeApiController.class);

	@Autowired
	private IUnionConsumeService unionConsumeService;

	@ApiOperation( value = "使用联盟卡核销", produces = "application/json;charset=UTF-8" )
	@RequestMapping(value = "/unionConsume", method= RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public ResponseUtils consumeByUnionCard(HttpServletRequest request, HttpServletResponse response, @RequestBody RequestApiParam<UnionConsumeParam> requestApiParam) throws IOException {
		try {
			boolean verification=super.verification(request, response, requestApiParam);
			UnionConsumeResult data = unionConsumeService.consumeByUnionCard(requestApiParam.getReqdata());
			return ResponseUtils.createBySuccess();
		} catch (BaseException e) {
			logger.error("", e);
			return ResponseUtils.createByErrorMessage(e.getErrorMsg());
		}catch (Exception e) {
			logger.error("", e);
			return ResponseUtils.createByError();
		}
	}

	@ApiOperation( value = "使用联盟卡核销后退款", produces = "application/json;charset=UTF-8" )
	@RequestMapping(value = "/unionRefund", method= RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public ResponseUtils unionRefund(HttpServletRequest request, HttpServletResponse response, @RequestBody RequestApiParam<UnionRefundParam> requestApiParam) throws IOException {
		try {
			boolean verification=super.verification(request, response, requestApiParam);
			UnionRefundResult data = unionConsumeService.unionRefund(requestApiParam.getReqdata());
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
