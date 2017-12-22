package com.gt.union.api.server.card.controller;

import com.gt.api.dto.ResponseUtils;
import com.gt.union.api.entity.param.UnionCardDiscountParam;
import com.gt.union.api.entity.result.UnionDiscountResult;
import com.gt.union.api.server.card.service.IUnionCardApiService;
import com.gt.union.api.server.constant.UnionDiscountConstant;
import com.gt.union.api.server.entity.param.RequestApiParam;
import com.gt.union.api.server.ApiBaseController;
import com.gt.union.common.exception.BaseException;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 联盟卡对外服务接口
 * Created by Administrator on 2017/8/23 0023.
 */
@RestController
@RequestMapping("/api/card/8A5DA52E")
public class UnionCardApiController extends ApiBaseController {

    private Logger logger = Logger.getLogger(UnionCardApiController.class);

	@Autowired
    private IUnionCardApiService unionCardApiService;

	@ApiOperation(value = "线上--根据商家和粉丝获取联盟卡折扣，session的member不能为空", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/consumeUnionDiscount", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public ResponseUtils<UnionDiscountResult> getConsumeUnionDiscount(HttpServletRequest request, HttpServletResponse response, @RequestBody RequestApiParam<UnionCardDiscountParam> requestApiParam){
		try {
			UnionCardDiscountParam param = requestApiParam.getReqdata();
			boolean verification=super.verification(request, response, requestApiParam);
			UnionDiscountResult data = unionCardApiService.getConsumeUnionCardDiscount(param.getPhone(), param.getBusId());
			return ResponseUtils.createBySuccess(data);
		} catch (BaseException e) {
			logger.error("", e);
			UnionDiscountResult data = new UnionDiscountResult();
			data.setCode(UnionDiscountConstant.UNION_DISCOUNT_CODE_NON);
			return ResponseUtils.createByErrorMessage(e.getErrorMsg());
		}catch (Exception e) {
			logger.error("", e);
			UnionDiscountResult data = new UnionDiscountResult();
			data.setCode(UnionDiscountConstant.UNION_DISCOUNT_CODE_NON);
			return ResponseUtils.createByError();
		}
	}





}
