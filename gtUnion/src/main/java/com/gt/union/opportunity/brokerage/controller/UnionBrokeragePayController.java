package com.gt.union.opportunity.brokerage.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.opportunity.brokerage.service.IUnionBrokeragePayService;
import com.gt.union.opportunity.brokerage.service.IUnionBrokeragePayStrategyService;
import com.gt.union.opportunity.brokerage.vo.BrokerageOpportunityVO;
import com.gt.union.opportunity.brokerage.vo.BrokeragePayVO;
import com.gt.union.opportunity.main.entity.UnionOpportunity;
import com.gt.union.union.main.vo.UnionPayVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 佣金支出 前端控制器
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
@Api(description = "佣金支出")
@RestController
@RequestMapping("/unionBrokeragePay")
public class UnionBrokeragePayController {

    @Autowired
    private IUnionBrokeragePayService unionBrokeragePayService;

    @Resource(name = "unionBackBrokeragePayService")
    private IUnionBrokeragePayStrategyService unionBrokeragePayStrategyService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页：商机-佣金结算-我需支付的佣金", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/opportunity/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageBrokerageOpportunityVO(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId,
            @ApiParam(value = "推荐商家盟员id", name = "fromMemberId")
            @RequestParam(value = "fromMemberId", required = false) Integer fromMemberId,
            @ApiParam(value = "是否已结算(0:否 1:是)", name = "isClose")
            @RequestParam(value = "isClose", required = false) Integer isClose,
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
        List<BrokerageOpportunityVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(BrokerageOpportunityVO.class, page.getSize());
            Page<BrokerageOpportunityVO> result = (Page<BrokerageOpportunityVO>) page;
            result = PageUtil.setRecord(result, voList);
            return GtJsonResult.instanceSuccessMsg(result).toString();
        } else {
            Page result = unionBrokeragePayService.pageBrokerageOpportunityVOByBusId(page, busId, unionId, fromMemberId, isClose, clientName, clientPhone);
            return GtJsonResult.instanceSuccessMsg(result).toString();
        }
    }

    @ApiOperation(value = "分页：商机-佣金结算-支付明细", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/detail/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pagePayVo(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            List<BrokeragePayVO> voList = MockUtil.list(BrokeragePayVO.class, page.getSize());
            for (int i = 0; i < voList.size(); i++) {
                List<UnionOpportunity> opportunityList = MockUtil.list(UnionOpportunity.class, 20);
                voList.get(i).setOpportunityList(opportunityList);
            }
            Page<BrokeragePayVO> result = (Page<BrokeragePayVO>) page;
            result = PageUtil.setRecord(result, voList);
            return GtJsonResult.instanceSuccessMsg(result).toString();
        } else {
            Page result = unionBrokeragePayService.pageBrokeragePayVOByBusId(page, busId, unionId);
            return GtJsonResult.instanceSuccessMsg(result).toString();
        }
    }

    @ApiOperation(value = "导出：商机-佣金结算-支付明细", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/detail/export", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void exportBrokeragePayDetail(
            HttpServletRequest request,
            HttpServletResponse response,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        List<BrokeragePayVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(BrokeragePayVO.class, 20);
            for (int i = 0; i < voList.size(); i++) {
                List<UnionOpportunity> opportunityList = MockUtil.list(UnionOpportunity.class, 20);
                voList.get(i).setOpportunityList(opportunityList);
            }
        } else {
            voList = unionBrokeragePayService.listBrokeragePayVOByBusId(busId, unionId);
        }
        String[] titles = new String[]{"所属联盟", "盟员名称", "商机往来金额(元)"};
        HSSFWorkbook workbook = ExportUtil.newHSSFWorkbook(titles);
        HSSFSheet sheet = workbook.getSheetAt(0);
        if (ListUtil.isNotEmpty(voList)) {
            int rowIndex = 1;
            HSSFCellStyle centerCellStyle = ExportUtil.newHSSFCellStyle(workbook, HSSFCellStyle.ALIGN_CENTER);
            for (BrokeragePayVO vo : voList) {
                HSSFRow row = sheet.createRow(rowIndex++);
                int cellIndex = 0;
                // 所属联盟
                HSSFCell unionNameCell = row.createCell(cellIndex++);
                unionNameCell.setCellValue(vo.getUnion().getName());
                unionNameCell.setCellStyle(centerCellStyle);
                // 盟员名称
                HSSFCell memberNameCell = row.createCell(cellIndex++);
                memberNameCell.setCellValue(vo.getMember().getEnterpriseName());
                memberNameCell.setCellStyle(centerCellStyle);
                // 商机往来金额(元)
                HSSFCell contactMoneyCell = row.createCell(cellIndex);
                contactMoneyCell.setCellValue(vo.getContactMoney());
                contactMoneyCell.setCellStyle(centerCellStyle);
            }
        }
        String fileName = "商机佣金结算支付明细表";
        ExportUtil.responseExport(response, workbook, fileName);
    }

    @ApiOperation(value = "商机-佣金结算-支付明细-详情", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/detail", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getPayVo(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "盟员id", name = "memberId", required = true)
            @RequestParam(value = "memberId") Integer memberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        BrokeragePayVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(BrokeragePayVO.class);
        } else {
            result = unionBrokeragePayService.getBrokeragePayVOByBusIdAndUnionIdAndMemberId(busId, unionId, memberId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "导出：商机佣金结算-支付明细-详情", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/detail/export", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void exportDetail(
            HttpServletRequest request,
            HttpServletResponse response,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "盟员id", name = "memberId", required = true)
            @RequestParam(value = "memberId") Integer memberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        BrokeragePayVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(BrokeragePayVO.class);
        } else {
            result = unionBrokeragePayService.getBrokeragePayVOByBusIdAndUnionIdAndMemberId(busId, unionId, memberId);
        }
        List<UnionOpportunity> opportunityList = result.getOpportunityList();
        String[] titles = new String[]{"时间", "顾客姓名", "电话", "佣金(元)"};
        HSSFWorkbook workbook = ExportUtil.newHSSFWorkbook(titles);
        HSSFSheet sheet = workbook.getSheetAt(0);
        if (ListUtil.isNotEmpty(opportunityList)) {
            int rowIndex = 1;
            HSSFCellStyle centerCellStyle = ExportUtil.newHSSFCellStyle(workbook, HSSFCellStyle.ALIGN_CENTER);
            for (UnionOpportunity opportunity : opportunityList) {
                HSSFRow row = sheet.createRow(rowIndex++);
                int cellIndex = 0;
                // 时间
                HSSFCell createTimeCell = row.createCell(cellIndex++);
                createTimeCell.setCellValue(DateUtil.getDateString(opportunity.getCreateTime(), DateUtil.DATETIME_PATTERN));
                createTimeCell.setCellStyle(centerCellStyle);
                // 顾客姓名
                HSSFCell clientNameCell = row.createCell(cellIndex++);
                clientNameCell.setCellValue(opportunity.getClientName());
                clientNameCell.setCellStyle(centerCellStyle);
                // 电话
                HSSFCell clientPhoneCell = row.createCell(cellIndex++);
                clientPhoneCell.setCellValue(opportunity.getClientPhone());
                clientPhoneCell.setCellStyle(centerCellStyle);
                // 佣金(元)
                HSSFCell brokerageMoneyCell = row.createCell(cellIndex);
                brokerageMoneyCell.setCellValue(opportunity.getBrokerageMoney());
                brokerageMoneyCell.setCellStyle(centerCellStyle);
            }
        }
        String fileName = result.getUnion().getName() + "_" + result.getMember().getEnterpriseName() + "_商机佣金明细表";
        ExportUtil.responseExport(response, workbook, fileName);
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "商机-佣金结算-我需支付的佣金-批量支付", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/opportunity", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String batchPay(
            HttpServletRequest request,
            @ApiParam(value = "商机id列表", name = "opportunityIdList", required = true)
            @RequestBody List<Integer> opportunityIdList) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.BUS_PARENT_TIP);
        }
        // mock
        UnionPayVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(UnionPayVO.class);
        } else {
            result = unionBrokeragePayService.batchPayByBusId(busId, opportunityIdList, null, unionBrokeragePayStrategyService);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

}