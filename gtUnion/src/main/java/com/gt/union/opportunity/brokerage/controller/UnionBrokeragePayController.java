package com.gt.union.opportunity.brokerage.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.ExportUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.PageUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.opportunity.brokerage.service.IUnionBrokeragePayService;
import com.gt.union.opportunity.brokerage.vo.BrokerageOpportunityVO;
import com.gt.union.opportunity.brokerage.vo.BrokeragePayVO;
import com.gt.union.opportunity.brokerage.vo.OpportunityBrokeragePayVO;
import com.gt.union.opportunity.main.entity.UnionOpportunity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Logger logger = Logger.getLogger(UnionBrokeragePayController.class);

    @Autowired
    private IUnionBrokeragePayService unionBrokeragePayService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页：获取我需支付的商机佣金信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/opportunity/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<BrokerageOpportunityVO>> pageBrokerageOpportunityVO(
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
//        List<BrokerageOpportunityVO> voList = MockUtil.list(BrokerageOpportunityVO.class, page.getSize());
        List<BrokerageOpportunityVO> voList = unionBrokeragePayService.listBrokerageOpportunityVOByBusId(busId, unionId, fromMemberId, isClose, clientName, clientPhone);
        Page<BrokerageOpportunityVO> result = (Page<BrokerageOpportunityVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "分页：获取商机佣金支付明细信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/detail/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<BrokeragePayVO>> pagePayVo(
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
//        List<BrokeragePayVO> voList = MockUtil.list(BrokeragePayVO.class, page.getSize());
//        for (int i = 0; i < voList.size(); i++) {
//            List<UnionOpportunity> opportunityList = MockUtil.list(UnionOpportunity.class, 20);
//            voList.get(i).setOpportunityList(opportunityList);
//        }
        List<BrokeragePayVO> voList = unionBrokeragePayService.listBrokeragePayVOByBusId(busId, unionId);
        Page<BrokeragePayVO> result = (Page<BrokeragePayVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "导出：商机佣金支付明细信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/detail/export", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void exportBrokeragePayDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        List<BrokeragePayVO> voList = unionBrokeragePayService.listBrokeragePayVOByBusId(busId, null);
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

    @ApiOperation(value = "获取商机佣金支付明细详情信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/detail", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<BrokeragePayVO> getPayVo(
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
//        BrokeragePayVO result = MockUtil.get(BrokeragePayVO.class);
        BrokeragePayVO result = unionBrokeragePayService.getBrokeragePayVOByBusIdAndUnionIdAndMemberId(busId, unionId, memberId);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "导出：商机佣金支付明细详情信息", produces = "application/json;charset=UTF-8")
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
        BrokeragePayVO result = unionBrokeragePayService.getBrokeragePayVOByBusIdAndUnionIdAndMemberId(busId, unionId, memberId);
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
                createTimeCell.setCellValue(opportunity.getCreateTime());
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

    @ApiOperation(value = "批量支付：商机佣金", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/opportunity", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public GtJsonResult<OpportunityBrokeragePayVO> batchPay(
            HttpServletRequest request,
            @ApiParam(value = "商机id列表", name = "opportunityIdList", required = true)
            @RequestParam(value = "opportunityIdList") List<Integer> opportunityIdList) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        OpportunityBrokeragePayVO result = unionBrokeragePayService.batchPayByBusId(busId, opportunityIdList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "批量支付：商机佣金-回调", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/79B4DE7C/opportunity/callback", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String updateCallback(
            @ApiParam(value = "socket关键字", name = "socketKey", required = true)
            @RequestParam(value = "socketKey") String socketKey,
            @ApiParam(value = "商机佣金支付ids", name = "payIds", required = true)
            @RequestParam(value = "payIds") String payIds,
            @RequestBody Map<String, Object> param) throws Exception {
        // debug
        logger.debug(JSONObject.toJSONString(param));

        Object objPayType = param.get("payType");
        String payType = objPayType != null ? objPayType.toString().trim() : "";
        boolean isPayTypeValid = StringUtil.isNotEmpty(payType) && ("0".equals(payType) || "1".equals(payType));
        if (!isPayTypeValid) {
            Map<String, Object> result = new HashMap<>(2);
            result.put("code", -1);
            result.put("msg", "payType参数无效");
            return JSONObject.toJSONString(result);
        }

        String orderNo;
        Integer isSuccess;
        if ("0".equals(payType)) {
            // 微信支付
            Object objOrderNo = param.get("transaction_id");
            orderNo = objOrderNo != null ? objOrderNo.toString().trim() : "";

            Object objResultCode = param.get("result_code");
            String resultCode = objResultCode != null ? objResultCode.toString().trim() : "";
            Object objReturnCode = param.get("return_code");
            String returnCode = objReturnCode != null ? objReturnCode.toString().trim() : "";
            isSuccess = "SUCCESS".equals(resultCode.toUpperCase()) && "SUCCESS".equals(returnCode.toUpperCase())
                    ? CommonConstant.COMMON_YES : CommonConstant.COMMON_NO;
        } else {
            // 支付宝支付
            Object objOrderNo = param.get("trade_no");
            orderNo = objOrderNo != null ? objOrderNo.toString().trim() : "";

            Object objTradeStatus = param.get("trade_status");
            String tradeStatus = objTradeStatus != null ? objTradeStatus.toString().trim() : "";
            isSuccess = "TRADE_SUCCESS".equals(tradeStatus.toUpperCase()) ? CommonConstant.COMMON_YES : CommonConstant.COMMON_NO;
        }

        return unionBrokeragePayService.updateCallbackByIds(payIds, socketKey, payType, orderNo, isSuccess);
    }

}