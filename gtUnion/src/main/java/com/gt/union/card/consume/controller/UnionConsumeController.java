package com.gt.union.card.consume.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.consume.constant.ConsumeConstant;
import com.gt.union.card.consume.service.IUnionConsumeService;
import com.gt.union.card.consume.vo.ConsumePostVO;
import com.gt.union.card.consume.vo.ConsumeRecordVO;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.union.main.vo.UnionPayVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 消费核销 前端控制器
 *
 * @author linweicong
 * @version 2017-11-25 10:51:42
 */
@Api(description = "消费核销")
@RestController
@RequestMapping("/unionConsume")
public class UnionConsumeController {

    @Autowired
    private IUnionConsumeService unionConsumeService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页：前台-消费核销记录", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/record/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageConsumeVO(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId,
            @ApiParam(value = "门店id", name = "shopId")
            @RequestParam(value = "shopId", required = false) Integer shopId,
            @ApiParam(value = "卡号", name = "cardNumber")
            @RequestParam(value = "cardNumber", required = false) String cardNumber,
            @ApiParam(value = "手机号", name = "phone")
            @RequestParam(value = "phone", required = false) String phone,
            @ApiParam(value = "开始时间", name = "beginTime")
            @RequestParam(value = "beginTime", required = false) Long beginTime,
            @ApiParam(value = "结束时间", name = "endTime")
            @RequestParam(value = "endTime", required = false) Long endTime) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<ConsumeRecordVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(ConsumeRecordVO.class, page.getSize());
            for (int i = 0; i < voList.size(); i++) {
                List<UnionCardProjectItem> nonErpTextList = MockUtil.list(UnionCardProjectItem.class, 3);
                voList.get(i).setNonErpTextList(nonErpTextList);

                List<UnionCardProjectItem> erpTextList = MockUtil.list(UnionCardProjectItem.class, 3);
                voList.get(i).setErpTextList(erpTextList);

                List<UnionCardProjectItem> erpGoodsList = MockUtil.list(UnionCardProjectItem.class, 3);
                voList.get(i).setErpGoodsList(erpGoodsList);
            }
        } else {
            Date begin = beginTime != null ? (new Date(beginTime)) : null;
            Date end = endTime != null ? (new Date(endTime)) : null;
            voList = unionConsumeService.listConsumeRecordVOByBusId(busId, unionId, shopId, cardNumber, phone, begin, end);
        }
        Page<ConsumeRecordVO> result = (Page<ConsumeRecordVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "导出：前台-消费核销记录", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/record/export", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void exportConsumeVO(
            HttpServletRequest request,
            HttpServletResponse response,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId,
            @ApiParam(value = "门店id", name = "shopId")
            @RequestParam(value = "shopId", required = false) Integer shopId,
            @ApiParam(value = "卡号", name = "cardNumber")
            @RequestParam(value = "cardNumber", required = false) String cardNumber,
            @ApiParam(value = "手机号", name = "phone")
            @RequestParam(value = "phone", required = false) String phone,
            @ApiParam(value = "开始时间", name = "beginTime")
            @RequestParam(value = "beginTime", required = false) Long beginTime,
            @ApiParam(value = "结束时间", name = "endTime")
            @RequestParam(value = "endTime", required = false) Long endTime) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<ConsumeRecordVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(ConsumeRecordVO.class, 20);
            for (int i = 0; i < voList.size(); i++) {
                List<UnionCardProjectItem> nonErpTextList = MockUtil.list(UnionCardProjectItem.class, 3);
                voList.get(i).setNonErpTextList(nonErpTextList);

                List<UnionCardProjectItem> erpTextList = MockUtil.list(UnionCardProjectItem.class, 3);
                voList.get(i).setErpTextList(erpTextList);

                List<UnionCardProjectItem> erpGoodsList = MockUtil.list(UnionCardProjectItem.class, 3);
                voList.get(i).setErpGoodsList(erpGoodsList);
            }
        } else {
            Date begin = beginTime != null ? (new Date(beginTime)) : null;
            Date end = endTime != null ? (new Date(endTime)) : null;
            voList = unionConsumeService.listConsumeRecordVOByBusId(busId, unionId, shopId, cardNumber, phone, begin, end);
        }
        String[] titles = new String[]{"所属联盟", "消费门店", "联盟卡号", "手机号", "消费金额(元)", "实收金额(元)", "优惠项目", "支付状态", "消费时间"};
        HSSFWorkbook workbook = ExportUtil.newHSSFWorkbook(titles);
        HSSFSheet sheet = workbook.getSheetAt(0);
        if (ListUtil.isNotEmpty(voList)) {
            int rowIndex = 1;
            HSSFCellStyle centerCellStyle = ExportUtil.newHSSFCellStyle(workbook, HSSFCellStyle.ALIGN_CENTER);
            for (ConsumeRecordVO vo : voList) {
                HSSFRow row = sheet.createRow(rowIndex++);
                int cellIndex = 0;
                // 所属联盟
                HSSFCell unionNameCell = row.createCell(cellIndex++);
                unionNameCell.setCellValue(vo.getUnion().getName());
                unionNameCell.setCellStyle(centerCellStyle);
                // 消费门店
                HSSFCell shopNameCell = row.createCell(cellIndex++);
                shopNameCell.setCellValue(vo.getShopName());
                shopNameCell.setCellStyle(centerCellStyle);
                // 联盟卡号
                HSSFCell cardNumberCell = row.createCell(cellIndex++);
                cardNumberCell.setCellValue(vo.getFan().getNumber());
                cardNumberCell.setCellStyle(centerCellStyle);
                // 手机号
                HSSFCell phoneCell = row.createCell(cellIndex++);
                phoneCell.setCellValue(vo.getFan().getPhone());
                phoneCell.setCellStyle(centerCellStyle);
                // 消费金额(元)
                HSSFCell consumeMoneyCell = row.createCell(cellIndex++);
                consumeMoneyCell.setCellValue(vo.getConsume().getConsumeMoney());
                consumeMoneyCell.setCellStyle(centerCellStyle);
                // 实收金额(元)
                HSSFCell payMoneyCell = row.createCell(cellIndex++);
                payMoneyCell.setCellValue(vo.getConsume().getPayMoney());
                payMoneyCell.setCellStyle(centerCellStyle);
                // 优惠项目
                HSSFCell itemListCell = row.createCell(cellIndex++);
                String itemListValue = "";
                if (ListUtil.isNotEmpty(vo.getNonErpTextList())) {
                    itemListValue = JSONObject.toJSONString(vo.getNonErpTextList());
                }
                if (ListUtil.isNotEmpty(vo.getErpTextList())) {
                    if (StringUtil.isNotEmpty(itemListValue)) {
                        itemListValue += ",";
                    }
                    itemListValue += JSONObject.toJSONString(vo.getErpTextList());
                }
                if (ListUtil.isNotEmpty(vo.getErpGoodsList())) {
                    if (StringUtil.isNotEmpty(itemListValue)) {
                        itemListValue += ",";
                    }
                    itemListValue += JSONObject.toJSONString(vo.getErpGoodsList());
                }
                itemListCell.setCellValue(itemListValue);
                itemListCell.setCellStyle(centerCellStyle);
                // 支付状态
                HSSFCell payStatusCell = row.createCell(cellIndex++);
                Integer payStatus = vo.getConsume().getPayStatus();
                payStatusCell.setCellValue(ConsumeConstant.PAY_STATUS_PAYING == payStatus ? "支付中"
                        : ConsumeConstant.PAY_STATUS_SUCCESS == payStatus ? "已支付"
                        : ConsumeConstant.PAY_STATUS_FAIL == payStatus ? "已退款" : "");
                payStatusCell.setCellStyle(centerCellStyle);
                // 消费时间
                HSSFCell consumeTimeCell = row.createCell(cellIndex);
                consumeTimeCell.setCellValue(vo.getConsume().getCreateTime());
                consumeTimeCell.setCellStyle(centerCellStyle);

            }
        }
        String fileName = "消费核销记录表";
        ExportUtil.responseExport(response, workbook, fileName);
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "前台-联盟卡消费核销-支付", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/fanId/{fanId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String saveConsumePayVOByUnionIdAndFanId(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "联盟卡粉丝id", name = "fanId", required = true)
            @PathVariable("fanId") Integer fanId,
            @ApiParam(value = "表单信息", name = "postVO", required = true)
            @RequestBody ConsumePostVO postVO) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        UnionPayVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(UnionPayVO.class);
        } else {
            result = unionConsumeService.saveConsumePostVOByBusIdAndUnionIdAndFanId(busId, unionId, fanId, postVO);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

}