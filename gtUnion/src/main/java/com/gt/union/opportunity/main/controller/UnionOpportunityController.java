package com.gt.union.opportunity.main.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.common.util.PageUtil;
import com.gt.union.opportunity.main.vo.OpportunityStatisticsDay;
import com.gt.union.opportunity.main.vo.OpportunityStatisticsVO;
import com.gt.union.opportunity.main.vo.OpportunityVO;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 商机 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 16:56:17
 */
@Api(description = "商机")
@RestController
@RequestMapping("/unionOpportunity")
public class UnionOpportunityController {

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页获取我的商机信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/toMe/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<OpportunityVO>> pageToMe(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId,
            @ApiParam(value = "(1:受理中 2:已接受 3:已拒绝)，多个用英文分号隔离", name = "acceptStatus")
            @RequestParam(value = "acceptStatus", required = false) String acceptStatus,
            @ApiParam(value = "客户名称", name = "clientName")
            @RequestParam(value = "clientName", required = false) String clientName,
            @ApiParam(value = "客户电话", name = "clientPhone")
            @RequestParam(value = "clientPhone", required = false) String clientPhone) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<OpportunityVO> voList = MockUtil.list(OpportunityVO.class, page.getSize());
        for (int i = 0; i < voList.size(); i++) {
            UnionMember fromMember = MockUtil.get(UnionMember.class);
            voList.get(i).setFromMember(fromMember);
        }
        Page<OpportunityVO> result = (Page<OpportunityVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "分页获取我的商机推荐信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/fromMe/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<OpportunityVO>> pageFromMe(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId,
            @ApiParam(value = "(1:受理中 2:已接受 3:已拒绝)，多个用英文分号隔离", name = "acceptStatus")
            @RequestParam(value = "acceptStatus", required = false) String acceptStatus,
            @ApiParam(value = "客户名称", name = "clientName")
            @RequestParam(value = "clientName", required = false) String clientName,
            @ApiParam(value = "客户电话", name = "clientPhone")
            @RequestParam(value = "clientPhone", required = false) String clientPhone) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<OpportunityVO> voList = MockUtil.list(OpportunityVO.class, page.getSize());
        for (int i = 0; i < voList.size(); i++) {
            UnionMember toMember = MockUtil.get(UnionMember.class);
            voList.get(i).setToMember(toMember);
        }
        Page<OpportunityVO> result = (Page<OpportunityVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "获取商机佣金统计数据", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/statistics", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<OpportunityStatisticsVO> getStatisticsByUnionId(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        OpportunityStatisticsVO result = MockUtil.get(OpportunityStatisticsVO.class);
        OpportunityStatisticsDay monday = MockUtil.get(OpportunityStatisticsDay.class);
        result.setMonday(monday);
        OpportunityStatisticsDay tuesday = MockUtil.get(OpportunityStatisticsDay.class);
        result.setTuesday(tuesday);
        OpportunityStatisticsDay wednesday = MockUtil.get(OpportunityStatisticsDay.class);
        result.setWednesday(wednesday);
        OpportunityStatisticsDay thursday = MockUtil.get(OpportunityStatisticsDay.class);
        result.setThursday(thursday);
        OpportunityStatisticsDay friday = MockUtil.get(OpportunityStatisticsDay.class);
        result.setFriday(friday);
        OpportunityStatisticsDay saturday = MockUtil.get(OpportunityStatisticsDay.class);
        result.setSaturday(saturday);
        OpportunityStatisticsDay sunday = MockUtil.get(OpportunityStatisticsDay.class);
        result.setSunday(sunday);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "审核商机", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{opportunityId}/unionId/{unionId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> updateStatusByIdAndUnionId(
            HttpServletRequest request,
            @ApiParam(value = "商机id", name = "opportunityId", required = true)
            @PathVariable("opportunityId") Integer opportunityId,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "是否接受(0:否 1:是)", name = "isPass", required = true)
            @RequestParam(value = "isPass") Integer isPass) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg();
    }

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "推荐商机", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> saveByUnionId(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "表单信息", name = "opportunityVO", required = true)
            @RequestBody OpportunityVO opportunityVO) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg();
    }

}