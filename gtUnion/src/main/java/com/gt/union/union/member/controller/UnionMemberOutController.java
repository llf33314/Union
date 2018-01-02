package com.gt.union.union.member.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.common.util.PageUtil;
import com.gt.union.union.member.service.IUnionMemberOutService;
import com.gt.union.union.member.vo.MemberOutPeriodVO;
import com.gt.union.union.member.vo.MemberOutVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 退盟申请 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 10:22:05
 */
@Api(description = "退盟申请")
@RestController
@RequestMapping("/unionMemberOut")
public class UnionMemberOutController {

    @Autowired
    private IUnionMemberOutService unionMemberOutService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页：我的联盟-退盟管理-退盟审核", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageOutVOByUnionId(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<MemberOutVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(MemberOutVO.class, page.getSize());
        } else {
            voList = unionMemberOutService.listMemberOutVOByBusIdAndUnionId(busId, unionId);
        }
        Page<MemberOutVO> result = (Page<MemberOutVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "分页：我的联盟-退盟管理-退盟过渡期", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/period/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pagePeriodByUnionId(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<MemberOutPeriodVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(MemberOutPeriodVO.class, page.getSize());
        } else {
            voList = unionMemberOutService.listMemberOutPeriodVOByBusIdAndUnionId(busId, unionId);
        }
        Page<MemberOutPeriodVO> result = (Page<MemberOutPeriodVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "我的联盟-退盟管理-退盟审核-分页数据-同意或拒绝", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{outId}/unionId/{unionId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateStatusByIdAndUnionId(
            HttpServletRequest request,
            @ApiParam(value = "退盟id", name = "outId", required = true)
            @PathVariable("outId") Integer outId,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "是否通过(0:否 1:是)", name = "isPass", required = true)
            @RequestParam(value = "isPass") Integer isPass) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.BUS_PARENT_TIP);
        }
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            unionMemberOutService.updateByBusIdAndIdAndUnionId(busId, outId, unionId, isPass);
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "我的联盟-退盟管理-退盟过渡期-退盟申请", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String saveByUnionId(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "退盟理由", name = "reason", required = true) String reason) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.BUS_PARENT_TIP);
        }
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            unionMemberOutService.saveByBusIdAndUnionId(busId, unionId, reason);
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    @ApiOperation(value = "我的联盟-首页-盟员列表-分页数据-移出", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/applyMemberId/{applyMemberId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String saveByUnionIdAndApplyMemberId(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "盟员id", name = "applyMemberId", required = true)
            @PathVariable("applyMemberId") Integer applyMemberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.BUS_PARENT_TIP);
        }

        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            unionMemberOutService.saveByBusIdAndUnionIdAndApplyMemberId(busId, unionId, applyMemberId);
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //-------------------------------------------------- delete --------------------------------------------------------

    @ApiOperation(value = "我的联盟-首页-盟员列表-分页数据-撤消移出", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{outId}/unionId/{unionId}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
    public String removeByIdAndUnionId(
            HttpServletRequest request,
            @ApiParam(value = "退盟申请id", name = "outId")
            @PathVariable("outId") Integer outId,
            @ApiParam(value = "联盟id", name = "unionId")
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.BUS_PARENT_TIP);
        }
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            unionMemberOutService.removeByBusIdAndIdAndUnionId(busId, outId, unionId);
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }
}