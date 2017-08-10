package com.gt.union.controller.basic;

import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionDiscountService;
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
    private Logger logger = LoggerFactory.getLogger(UnionDiscountController.class);

    @Autowired
    private IUnionDiscountService unionDiscountService;

    /**
     * 根据session中busId和请求参数中的busId、discount设置折扣信息
     * @param request
     * @param busId
     * @param discount
     * @return
     */
    @ApiOperation(value = "设置盟员折扣", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(description = "设置盟员折扣", op_function = "2")
    @RequestMapping(value = "", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateUnionDiscount(HttpServletRequest request, @ApiParam(name="unionId", value = "联盟id", required = true) @RequestParam(name = "unionId", required = true) Integer unionId
            , @ApiParam(name="busId", value = "被设置的盟员商家id", required = true) @RequestParam(name = "busId", required = true) Integer busId
            , @ApiParam(name="discount", value = "设置的折扣", required = true) @RequestParam(name = "discount", required = true) Double discount) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser == null) {
                throw new Exception("UnionDiscountController.updateUnionDiscount():无法通过session获取用户的信息!");
            }
            if(CommonUtil.isNotEmpty(busUser.getPid()) && busUser.getPid() != 0){
                throw new BusinessException("请使用主账号权限");
            }
            this.unionDiscountService.updateUnionDiscount(unionId, busUser.getId(), busId, discount);
        } catch (Exception e) {
            logger.error("设置盟员折扣错误", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }
        return GTJsonResult.instanceSuccessMsg().toString();
    }
	
}
