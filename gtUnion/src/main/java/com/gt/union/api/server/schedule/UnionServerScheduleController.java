package com.gt.union.api.server.schedule;

import com.gt.api.dto.ResponseUtils;
import com.gt.union.api.entity.param.UnionCardDiscountParam;
import com.gt.union.api.entity.result.UnionDiscountResult;
import com.gt.union.api.server.ApiBaseController;
import com.gt.union.api.server.card.service.IUnionCardApiService;
import com.gt.union.api.server.constant.UnionDiscountConstant;
import com.gt.union.api.server.entity.param.RequestApiParam;
import com.gt.union.api.server.schedule.service.IUnionConsumeGiveIntegralService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.util.CommonUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author hongjiye
 * @time 2018-01-03 16:55
 **/
@RestController
@RequestMapping("/schedule/8A5DA52E")
public class UnionServerScheduleController extends ApiBaseController {

	private Logger logger = Logger.getLogger(UnionServerScheduleController.class);

	@Autowired
	private IUnionConsumeGiveIntegralService unionConsumeGiveIntegralService;

	@ApiOperation(value = "定时任务赠送积分", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/giveIntegral", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public ResponseUtils getConsumeUnionDiscount(HttpServletRequest request, HttpServletResponse response, @RequestBody RequestApiParam<Map> requestApiParam){
		try {
			Map param = requestApiParam.getReqdata();
			boolean verification=super.verification(request, response, requestApiParam);
			unionConsumeGiveIntegralService.giveConsumeIntegral(param);
			return ResponseUtils.createBySuccess();
		} catch (BaseException e) {
			logger.error(e.getMessage(), e);
			return ResponseUtils.createByErrorMessage(e.getErrorMsg());
		}catch (Exception e) {
			logger.error(CommonConstant.SYS_ERROR, e);
			UnionDiscountResult data = new UnionDiscountResult();
			data.setCode(UnionDiscountConstant.UNION_DISCOUNT_CODE_NON);
			return ResponseUtils.createByError();
		}
	}

}
