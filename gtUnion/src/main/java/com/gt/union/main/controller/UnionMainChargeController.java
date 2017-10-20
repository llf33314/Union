package com.gt.union.main.controller;

import com.gt.union.common.response.GTJsonResult;
import com.gt.union.main.entity.UnionMainCharge;
import com.gt.union.main.service.IUnionMainChargeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 联盟升级收费 前端控制器
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
@RestController
@RequestMapping("/unionMainCharge")
public class UnionMainChargeController {
    @Autowired
    private IUnionMainChargeService unionMainChargeService;

    @ApiOperation(value = "根据联盟id和红黑卡类型，获取联盟红黑卡收费设置", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/type/{type}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getByUnionIdAndType(@ApiParam(name = "unionId", value = "联盟id", required = true)
                                      @PathVariable("unionId") Integer unionId,
                                      @ApiParam(name = "type", value = "红黑卡类型(1:黑卡，2:红卡)", required = true)
                                      @PathVariable("type") Integer type) throws Exception {
        UnionMainCharge result = this.unionMainChargeService.getByUnionIdAndType(unionId, type);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }
}
