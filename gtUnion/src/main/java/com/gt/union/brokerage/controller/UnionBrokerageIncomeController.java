package com.gt.union.brokerage.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.brokerage.service.IUnionBrokerageWithdrawalService;
import com.gt.union.card.constant.CardConstant;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.service.IUnionMainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * 佣金收入 前端控制器
 *
 * @author linweicong
 * @version 2017-10-23 15:28:54
 */
@Api(description = "佣金收入")
@RestController
@RequestMapping("/unionBrokerageIncome")
public class UnionBrokerageIncomeController {
    private Logger logger = LoggerFactory.getLogger(UnionBrokerageIncomeController.class);

    @Autowired
    private IUnionBrokerageIncomeService unionBrokerageIncomeService;

    @Autowired
    private IUnionBrokerageWithdrawalService unionBrokerageWithdrawalService;

    @Autowired
    private IUnionMainService unionMainService;

    @ApiOperation(value = "分页获取售卡佣金分成列表信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/card/memberId/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageCardMapByMemberId(HttpServletRequest request, Page page,
                                        @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                        @PathVariable("memberId") Integer memberId,
                                        @ApiParam(name = "cardType", value = "联盟卡类型，1为黑卡，2为红卡")
                                        @RequestParam(value = "cardType", required = false) Integer cardType,
                                        @ApiParam(name = "cardNumber", value = "联盟卡号，模糊匹配")
                                        @RequestParam(value = "cardNumber", required = false) String cardNumber,
                                        @ApiParam(name = "beginDate", value = "开始日期，大于或等于开始日期")
                                        @RequestParam(value = "beginDate", required = false) String beginDate,
                                        @ApiParam(name = "endDate", value = "结束日期，小于开始日期")
                                        @RequestParam(value = "endDate", required = false) String endDate) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        Page result = this.unionBrokerageIncomeService.pageCardMapByBusIdAndMemberId(page, busId, memberId,
                cardType, cardNumber, beginDate, endDate);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "导出：获取同一联盟下所有售卡佣金分成列表信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/exportCard/memberId/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void exportCardMapByMemberId(HttpServletRequest request,
                                        HttpServletResponse response,
                                        @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                        @PathVariable("memberId") Integer memberId,
                                        @ApiParam(name = "cardType", value = "联盟卡类型，1为黑卡，2为红卡")
                                        @RequestParam(value = "cardType", required = false) Integer cardType,
                                        @ApiParam(name = "cardNumber", value = "联盟卡号，模糊匹配")
                                        @RequestParam(value = "cardNumber", required = false) String cardNumber,
                                        @ApiParam(name = "beginDate", value = "开始日期，大于或等于开始日期")
                                        @RequestParam(value = "beginDate", required = false) String beginDate,
                                        @ApiParam(name = "endDate", value = "结束日期，小于开始日期")
                                        @RequestParam(value = "endDate", required = false) String endDate) throws Exception {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer busId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                busId = busUser.getPid();
            }
            List<Map<String, Object>> resultList = this.unionBrokerageIncomeService.listCardMapByBusIdAndMemberId(busId
                    , memberId, cardType, cardNumber, beginDate, endDate);
            String[] titles = new String[]{"时间", "联盟卡号", "售卡类型", "佣金金额", "售卡出处"};
            HSSFWorkbook wb = ExportUtil.newHSSFWorkbook(titles);
            HSSFSheet sheet = wb.getSheetAt(0);
            if (ListUtil.isNotEmpty(resultList)) {
                int rowIndex = 1;
                HSSFCellStyle centerCellStyle = ExportUtil.newHSSFCellStyle(wb, HSSFCellStyle.ALIGN_CENTER);
                for (Map<String, Object> resultMap : resultList) {
                    HSSFRow row = sheet.createRow(rowIndex++);
                    int cellIndex = 0;
                    HSSFCell incomeCreateTimeCell = row.createCell(cellIndex++);
                    //时间
                    String tgtIncomeCreateTime = resultMap.get("incomeCreateTime") != null ? resultMap.get("incomeCreateTime").toString() : "";
                    incomeCreateTimeCell.setCellValue(tgtIncomeCreateTime);
                    incomeCreateTimeCell.setCellStyle(centerCellStyle);
                    //联盟卡号
                    HSSFCell cardNumberCell = row.createCell(cellIndex++);
                    String tgtCardNumber = resultMap.get("cardNumber") != null ? resultMap.get("cardNumber").toString() : "";
                    cardNumberCell.setCellValue(tgtCardNumber);
                    cardNumberCell.setCellStyle(centerCellStyle);
                    //售卡类型
                    HSSFCell cardTypeCell = row.createCell(cellIndex++);
                    String tgtCardType = resultMap.get("cardType") != null ? resultMap.get("cardType").toString() : "";
                    tgtCardType = tgtCardType.equals(String.valueOf(CardConstant.TYPE_RED)) ? "红卡" : "黑卡";
                    cardTypeCell.setCellValue(tgtCardType);
                    cardTypeCell.setCellStyle(centerCellStyle);
                    //售卡佣金
                    HSSFCell incomeMoneyMeCell = row.createCell(cellIndex++);
                    String tgtIncomeMoney = resultMap.get("incomeMoney") != null ? resultMap.get("incomeMoney").toString() : "";
                    incomeMoneyMeCell.setCellValue(tgtIncomeMoney);
                    incomeMoneyMeCell.setCellStyle(centerCellStyle);
                    //售卡出处
                    HSSFCell srcEnterpriseNameCell = row.createCell(cellIndex);
                    String tgtSrcEnterpriseName = resultMap.get("srcEnterpriseName") != null ? resultMap.get("srcEnterpriseName").toString() : "";
                    srcEnterpriseNameCell.setCellValue(tgtSrcEnterpriseName);
                    srcEnterpriseNameCell.setCellStyle(centerCellStyle);
                }
            }
            UnionMain unionMain = this.unionMainService.getByBusIdAndMemberId(busId, memberId);
            String filename = unionMain.getName() + "的售卡佣金分成记录";
            ExportUtil.responseExport(response, wb, filename);
        } catch (Exception e) {
            ExportUtil.responseExportError(response);
        }
    }

    /************************************************佣金平台************************************************************************/
    @ApiOperation(value = "佣金平台可提现总额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/withdrawalSum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String withdrawalSum(HttpServletRequest request) {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        //收入的佣金总和
        double sumPay = unionBrokerageIncomeService.getSumInComeUnionBrokerage(busId);
        //已提现的佣金总和
        double sumWithdrawals = unionBrokerageWithdrawalService.getSumWithdrawalsUnionBrokerage(busId);
        //可提现
        double ableGet = BigDecimalUtil.subtract(sumPay, sumWithdrawals).doubleValue();
        return GTJsonResult.instanceSuccessMsg(ableGet).toString();
    }


    @ApiOperation(value = "获取佣金平台二维码图片链接", notes = "获取佣金平台二维码图片链接", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/indexQRUrl", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    public String indexQRUrl() throws UnsupportedEncodingException {
        String url = PropertiesUtil.getUnionUrl() + "/unionBrokerageIncome/indexQR";
        return GTJsonResult.instanceSuccessMsg(url).toString();
    }

    @ApiOperation(value = "获取佣金平台二维码图片", notes = "获取佣金平台二维码图片", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/indexQR", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
    public void indexQR(HttpServletResponse response) throws UnsupportedEncodingException {
        String url = PropertiesUtil.getUnionUrl() + "/brokeragePhone/#/" + "index";
        QRcodeKit.buildQRcode(url, 250, 250, response);
    }
}
