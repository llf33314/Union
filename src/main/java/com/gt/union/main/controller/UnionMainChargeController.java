package com.gt.union.main.controller;

import com.gt.union.common.response.GTJsonResult;
import com.gt.union.main.entity.UnionMainCharge;
import com.gt.union.main.service.IUnionMainChargeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 联盟升级收费 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionMainCharge")
public class UnionMainChargeController {
    @Autowired
    private IUnionMainChargeService unionMainChargeService;

    @ApiOperation(value = "根据联盟id和红黑卡类型(1:黑卡，2:红卡)，获取联盟红黑卡收费设置", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/type/{type}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getByUnionIdAndType(@PathVariable("unionId") Integer unionId
            , @PathVariable("type") Integer type) throws Exception {
        UnionMainCharge result = this.unionMainChargeService.getByUnionIdAndType(unionId, type);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }
}
