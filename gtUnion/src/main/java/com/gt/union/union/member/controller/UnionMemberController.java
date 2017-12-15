package com.gt.union.union.member.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.ExportUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.MockUtil;
import com.gt.union.common.util.PageUtil;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import com.gt.union.union.member.vo.MemberVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainService unionMainService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "首页-盟员列表-分页；联盟卡设置-折扣卡设置-分页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/write/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageWriteByUnionId(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "盟员名称", name = "memberName")
            @RequestParam(value = "memberName", required = false) String optMemberName) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<UnionMember> memberList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            memberList = MockUtil.list(UnionMember.class, page.getSize());
        } else {
            memberList = unionMemberService.listWriteByBusIdAndUnionId(busId, unionId, optMemberName);
        }

        Page<UnionMember> result = (Page<UnionMember>) page;
        result = PageUtil.setRecord(result, memberList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "导出：入盟和申请退盟状态的盟员信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/write/export", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void exportWriteByUnionId(
            HttpServletRequest request,
            HttpServletResponse response,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }

        List<UnionMember> memberList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            memberList = MockUtil.list(UnionMember.class, 20);
        } else {
            memberList = unionMemberService.listWriteByBusIdAndUnionId(busId, unionId, null);
        }
        String[] titles = new String[]{"盟员名称", "负责人", "联系电话", "加入时间"};
        HSSFWorkbook workbook = ExportUtil.newHSSFWorkbook(titles);
        HSSFSheet sheet = workbook.getSheetAt(0);
        if (ListUtil.isNotEmpty(memberList)) {
            int rowIndex = 1;
            HSSFCellStyle centerCellStyle = ExportUtil.newHSSFCellStyle(workbook, HSSFCellStyle.ALIGN_CENTER);
            for (UnionMember member : memberList) {
                HSSFRow row = sheet.createRow(rowIndex++);
                int cellIndex = 0;
                // 盟员名称
                HSSFCell memberNameCell = row.createCell(cellIndex++);
                memberNameCell.setCellValue(member.getEnterpriseName());
                memberNameCell.setCellStyle(centerCellStyle);
                // 负责人
                HSSFCell directorCell = row.createCell(cellIndex++);
                directorCell.setCellValue(member.getDirectorName());
                directorCell.setCellStyle(centerCellStyle);
                // 联系电话
                HSSFCell phoneCell = row.createCell(cellIndex++);
                phoneCell.setCellValue(member.getDirectorPhone());
                phoneCell.setCellStyle(centerCellStyle);
                // 加入时间
                HSSFCell createTimeCell = row.createCell(cellIndex);
                createTimeCell.setCellValue(member.getCreateTime());
                createTimeCell.setCellStyle(centerCellStyle);
            }
        }
        UnionMain union = unionMainService.getById(unionId);
        String fileName = union.getName() + "的盟员列表";
        ExportUtil.responseExport(response, workbook, fileName);
    }

    @ApiOperation(value = "首页-盟员列表-分页-详情", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{memberId}/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getByIdAndUnionId(
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
        UnionMember result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(UnionMember.class);
        } else {
            result = unionMemberService.getByBusIdAndIdAndUnionId(busId, memberId, unionId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "联盟设置-基础设置；联盟卡设置-折扣卡设置-折扣设置", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/busUser", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getByUnionId(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        MemberVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(MemberVO.class);
        } else {
            result = unionMemberService.getMemberVOByBusIdAndUnionId(busId, unionId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "辅助接口：获取指定联盟下具有写权限的但不包括我的盟员列表", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/write/other", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listOtherWriteByUnionId(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        List<UnionMember> result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.list(UnionMember.class, 30);
        } else {
            result = unionMemberService.listOtherWriteByBusIdAndUnionId(busId, unionId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "联盟设置-基础设置-更新", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{memberId}/unionId/{unionId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateByUnionId(
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
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            unionMemberService.updateByBusIdAndIdAndUnionId(busId, memberId, unionId, member);
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    @ApiOperation(value = "联盟卡设置-折扣卡设置-折扣设置-更新", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{memberId}/unionId/{unionId}/discount", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateDiscountByIdAndUnionId(
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
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            unionMemberService.updateDiscountByBusIdAndIdAndUnionId(busId, memberId, unionId, discount);
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //-------------------------------------------------- post ----------------------------------------------------------
}