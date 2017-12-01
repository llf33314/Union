package com.gt.union.union.member.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.common.util.PageUtil;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 盟员 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 10:22:05
 */
@Api(description = "盟员")
@RestController
@RequestMapping("/unionMember")
public class UnionMemberController {

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页：获取入盟和申请退盟状态的盟员信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/write/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<UnionMember>> pageWriteByUnionId(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "盟员名称", name = "memberName")
            @RequestParam(value = "memberName", required = false) String memberName) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<UnionMember> memberList = MockUtil.list(UnionMember.class, page.getSize());
        Page<UnionMember> result = (Page<UnionMember>) page;
        result = PageUtil.setRecord(result, memberList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "导出：入盟和申请退盟状态的盟员信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/write/export", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> exportByUnionId(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg("TODO");
    }

    @ApiOperation(value = "获取盟员信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{memberId}/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<UnionMember> getByIdAndUnionId(
            HttpServletRequest request,
            @ApiParam(value = "盟员id", name = "memberId", required = true)
            @PathVariable(value = "memberId") Integer memberId,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        UnionMember result = MockUtil.get(UnionMember.class);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "获取商家的盟员信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/busUser", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<UnionMember> getByUnionId(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        UnionMember result = MockUtil.get(UnionMember.class);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "更新盟员信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{memberId}/unionId/{unionId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> updateByUnionId(
            HttpServletRequest request,
            @ApiParam(value = "盟员id", name = "memberId", required = true)
            @PathVariable("memberId") Integer memberId,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "盟员", name = "member", required = true)
            @RequestBody UnionMember member) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg();
    }

    @ApiOperation(value = "更新盟员折扣信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{memberId}/unionId/{unionId}/discount", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> updateDiscountByIdAndUnionId(
            HttpServletRequest request,
            @ApiParam(value = "盟员id", name = "memberId", required = true)
            @PathVariable("memberId") Integer memberId,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "折扣", name = "discount", required = true)
            @RequestBody Double discount) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg();
    }

    //-------------------------------------------------- post ----------------------------------------------------------
}