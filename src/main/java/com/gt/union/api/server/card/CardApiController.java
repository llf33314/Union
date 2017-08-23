package com.gt.union.api.server.card;

import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.service.card.IUnionBusMemberCardService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 联盟卡对外服务接口
 * Created by Administrator on 2017/8/23 0023.
 */
@RestController
@RequestMapping("/api")
public class CardApiController {

	private static final String GET_CONSUME_UNION_DISCOUNT = "UnionApplyInfoController.getConsumeUnionDiscount()";

	private Logger logger = Logger.getLogger(CardApiController.class);

	@Autowired
	private IUnionBusMemberCardService unionBusMemberCardService;

	@ApiOperation(value = "获取联盟卡折扣", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/consumeUnionDiscount", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String getConsumeUnionDiscount(HttpServletRequest request,
					@ApiParam(name="memberId", value = "用户id", required = true) @RequestParam(name = "memberId", required = true) Integer memberId
					,@ApiParam(name="busId", value = "商家id", required = true) @RequestParam(name = "busId", required = true) Integer busId ){
		try {
			Map data = unionBusMemberCardService.getConsumeUnionDiscount(memberId, busId);
			return GTJsonResult.instanceSuccessMsg(data).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		}catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(GET_CONSUME_UNION_DISCOUNT, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
		}
	}
}
