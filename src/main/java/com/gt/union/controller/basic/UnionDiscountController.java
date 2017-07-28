package com.gt.union.controller.basic;

import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionDiscountService;
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
    @RequestMapping(value = "", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateUnionDiscount(HttpServletRequest request, @RequestParam(name = "unionId", required = true) Integer unionId
            , @RequestParam(name = "busId", required = true) Integer busId
            , @RequestParam(name = "discount", required = true) Double discount) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser == null) {
                throw new Exception("UnionDiscountController.updateUnionDiscount():无法通过session获取用户的信息!");
            }
            this.unionDiscountService.updateUnionDiscount(unionId, busUser.getId(), busId, discount);
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }
        return GTJsonResult.instanceSuccessMsg().toString();
    }
	
}
