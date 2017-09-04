package com.gt.union.api.server.consume;

import com.gt.union.api.entity.param.RequestApiParam;
import com.gt.union.api.entity.param.UnionConsumeParam;
import com.gt.union.api.entity.result.UnionConsumeResult;
import com.gt.union.api.entity.param.UnionRefundParam;
import com.gt.union.api.entity.result.UnionRefundResult;
import com.gt.union.api.server.ApiBaseController;
import com.gt.union.api.server.card.UnionCardApiController;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.service.consume.IUnionConsumeServiceRecordService;
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

	private static final String CONSUME_BY_UNIONCARD = "UnionApplyInfoController.consumeByUnionCard()";
	private static final String UNION_REFUND = "UnionApplyInfoController.unionRefund()";

	private Logger logger = Logger.getLogger(UnionCardApiController.class);

	@Autowired
	private IUnionConsumeServiceRecordService unionConsumeServiceRecordService;

	@ApiOperation( value = "使用联盟卡核销", produces = "application/json;charset=UTF-8" )
	@RequestMapping(value = "/unionConsume", method= RequestMethod.POST)
	public GTJsonResult<UnionConsumeResult> consumeByUnionCard(HttpServletRequest request, HttpServletResponse response, @RequestBody RequestApiParam<UnionConsumeParam> requestApiParam) throws IOException {
		try {
			boolean verification=super.verification(request, response, requestApiParam);
			UnionConsumeResult data = unionConsumeServiceRecordService.consumeByUnionCard(requestApiParam.getReqdata());
			return GTJsonResult.instanceSuccessMsg(data);
		} catch (BaseException e) {
			logger.error("", e);
			UnionConsumeResult data = new UnionConsumeResult(false,e.getErrorMsg());
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), data);
		}catch (Exception e) {
			logger.error("", e);
			UnionConsumeResult data = new UnionConsumeResult();
			return GTJsonResult.instanceErrorMsg(CONSUME_BY_UNIONCARD, e.getMessage(), data);
		}
	}

	@ApiOperation( value = "使用联盟卡核销后退款", produces = "application/json;charset=UTF-8" )
	@RequestMapping(value = "/unionRefund", method= RequestMethod.POST)
	public GTJsonResult<UnionRefundResult> unionRefund(HttpServletRequest request, HttpServletResponse response, @RequestBody RequestApiParam<UnionRefundParam> requestApiParam) throws IOException {
		try {
			boolean verification=super.verification(request, response, requestApiParam);
			UnionRefundResult data = unionConsumeServiceRecordService.unionRefund(requestApiParam.getReqdata());
			return GTJsonResult.instanceSuccessMsg(data);
		} catch (BaseException e) {
			logger.error("", e);
			UnionRefundResult data = new UnionRefundResult(false,e.getErrorMsg());
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), data);
		}catch (Exception e) {
			logger.error("", e);
			UnionRefundResult data = new UnionRefundResult(false ,"失败");
			return GTJsonResult.instanceErrorMsg(UNION_REFUND, e.getMessage(), data);
		}
	}

}
