package com.gt.union.member.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.member.service.IUnionMemberOutService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 联盟成员退盟申请 前端控制器
 *
 * @author linweicong
 * @version 2017-10-23 08:34:54
 */
@Api(description = "联盟成员退盟申请")
@RestController
@RequestMapping("/unionMemberOut")
public class UnionMemberOutController {

    @Autowired
    private IUnionMemberOutService unionMemberOutService;

    //-------------------------------------------------- get ----------------------------------------------------------

    @ApiOperation(value = "分页获取退盟申请列表信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/page/applyOut/memberId/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageApplyOutMapByMemberId(HttpServletRequest request, Page page,
                                            @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                            @PathVariable("memberId") Integer memberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        Page result = this.unionMemberOutService.pageApplyOutMapByBusIdAndMemberId(page, busId, memberId);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "分页获取退盟过渡期列表信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/page/outing/memberId/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageOutingMapByMemberId(HttpServletRequest request, Page page,
                                          @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                          @PathVariable("memberId") Integer memberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        Page result = this.unionMemberOutService.pageOutingMapByBusIdAndMemberId(page, busId, memberId);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put ----------------------------------------------------------

    @ApiOperation(value = "审核退盟申请", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{outId}/memberId/{memberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateByMemberIdAndOutId(HttpServletRequest request,
                                           @ApiParam(name = "outId", value = "退盟申请id", required = true)
                                           @PathVariable("outId") Integer outId,
                                           @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                           @PathVariable("memberId") Integer memberId,
                                           @ApiParam(name = "isOK", value = "是否同意退盟，1为是，0为否", required = true)
                                           @RequestParam(value = "isOK") Integer isOK) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        this.unionMemberOutService.updateByBusIdAndMemberIdAndOutId(busUser.getId(), memberId, outId, isOK);
        return GTJsonResult.instanceSuccessMsg().toString();
    }

    @ApiOperation(value = "盟主直接移出盟员", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/memberId/{memberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateByMemberIdAndTgtMemberId(HttpServletRequest request,
                                                 @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                                 @PathVariable("memberId") Integer memberId,
                                                 @ApiParam(name = "tgtMemberId", value = "要移出的盟员身份id", required = true)
                                                 @RequestParam(value = "tgtMemberId") Integer tgtMemberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        this.unionMemberOutService.updateByBusIdAndMemberIdAndTgtMemberId(busUser.getId(), memberId, tgtMemberId);
        return GTJsonResult.instanceSuccessMsg().toString();
    }

    //------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "申请退盟", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/memberId/{memberId}/applyOut", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String method(HttpServletRequest request,
                         @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                         @PathVariable(value = "memberId") Integer memberId,
                         @ApiParam(name = "applyOutReason", value = "退盟理由", required = true)
                         @RequestParam(name = "outReason") String applyOutReason) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        this.unionMemberOutService.saveApplyOutByBusIdAndMemberId(busUser.getId(), memberId, applyOutReason);
        return GTJsonResult.instanceSuccessMsg().toString();
    }
}
