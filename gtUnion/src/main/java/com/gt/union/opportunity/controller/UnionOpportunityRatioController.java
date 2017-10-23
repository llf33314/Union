package com.gt.union.opportunity.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.opportunity.service.IUnionOpportunityRatioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 * 商机佣金比率 前端控制器
 *
 * @author linweicong
 * @version 2017-10-23 11:17:59
 */
@Api(description = "商机佣金比率")
@RestController
@RequestMapping("/unionOpportunityRatio")
public class UnionOpportunityRatioController {

    @Autowired
    private IUnionOpportunityRatioService unionRatioService;

    //-------------------------------------------------- get ----------------------------------------------------------

    @ApiOperation(value = "根据盟员身份id，分页获取与盟友之间的商机佣金比例设置列表信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pageMap/memberId/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageRatio(HttpServletRequest request, Page page,
                            @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                            @PathVariable("memberId") Integer memberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        Page result = this.unionRatioService.pageMapByBusIdAndMemberId(page, busId, memberId);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put ----------------------------------------------------------

    @ApiOperation(value = "根据盟员身份id，以及受惠方的盟员身份id，设置商机佣金比例", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/memberId/{memberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String saveOrUpdateRatio(HttpServletRequest request,
                                    @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                    @PathVariable("memberId") Integer memberId,
                                    @ApiParam(name = "toMemberId", value = "受惠方的盟员身份id", required = true)
                                    @RequestParam(name = "toMemberId") Integer toMemberId,
                                    @ApiParam(name = "ratio", value = "商机佣金比例", required = true)
                                    @RequestParam(name = "ratio") @NotNull Double ratio) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        if (busUser.getPid() != null && busUser.getPid() != 0) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        this.unionRatioService.updateOrSaveByBusIdAndFromMemberIdAndToMemberIdAndRatio(busUser.getId(), memberId, toMemberId, ratio);
        return GTJsonResult.instanceSuccessMsg().toString();
    }

    //------------------------------------------------- post ----------------------------------------------------------

}
