package com.gt.union.controller.basic;

import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionDiscountService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 联盟商家折扣 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@RestController
@RequestMapping("/unionDiscount")
public class UnionDiscountController {
    private static final String UPDATE_UNIONID_TOBUSID_DISCOUNT = "UnionDiscountController.updateByUnionIdAndToBusIdAndDiscount()";
    private Logger logger = LoggerFactory.getLogger(UnionDiscountController.class);

    @Autowired
    private IUnionDiscountService unionDiscountService;

    /**
     * 根据session中busId和请求参数中的busId、discount设置折扣信息
     * @param request
     * @param toBusId
     * @param discount
     * @return
     */
    @ApiOperation(value = "设置盟员折扣", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{unionId}/{toBusId}/{discount}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateByUnionIdAndToBusIdAndDiscount(HttpServletRequest request
            , @ApiParam(name="unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId
            , @ApiParam(name="toBusId", value = "被设置的盟员商家id", required = true) @PathVariable("toBusId") Integer toBusId
            , @ApiParam(name="discount", value = "设置的折扣", required = true) @PathVariable("discount") Double discount) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser == null) {
                throw new BusinessException(UPDATE_UNIONID_TOBUSID_DISCOUNT, "", "无法通过session获取用户的信息");
            }
            this.unionDiscountService.updateByUnionIdAndToBusIdAndDiscount(unionId, busUser.getId(), toBusId, discount);
            return GTJsonResult.instanceSuccessMsg().toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(UPDATE_UNIONID_TOBUSID_DISCOUNT, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }
	
}
