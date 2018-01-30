package com.gt.union.card.sharing.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.sharing.service.IUnionCardSharingRecordService;
import com.gt.union.card.sharing.vo.CardSharingRecordVO;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 联盟卡售卡分成记录 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Api(description = "联盟卡售卡分成记录")
@RestController
@RequestMapping("/unionCardSharingRecord")
public class UnionCardSharingRecordController {

    @Autowired
    private IUnionCardSharingRecordService unionCardSharingRecordService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页：我的联盟-售卡佣金分成管理-售卡分成记录", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageSharingRecordVOByUnionId(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable(value = "unionId") Integer unionId,
            @ApiParam(value = "联盟卡号", name = "cardNumber")
            @RequestParam(value = "cardNumber", required = false) String cardNumber,
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
        List<CardSharingRecordVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(CardSharingRecordVO.class, page.getSize());
        } else {
            Date begin = beginTime != null ? (new Date(beginTime)) : null;
            Date end = endTime != null ? (new Date(endTime)) : null;
            voList = unionCardSharingRecordService.listCardSharingRecordVOByBusIdAndUnionId(busId, unionId, cardNumber, begin, end);
        }
        Page<CardSharingRecordVO> result = (Page<CardSharingRecordVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "导出：我的联盟-售卡佣金分成管理-售卡分成记录", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/export", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void exportSharingRecordVOByUnionId(
            HttpServletRequest request,
            HttpServletResponse response,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable(value = "unionId") Integer unionId,
            @ApiParam(value = "联盟卡号", name = "cardNumber")
            @RequestParam(value = "cardNumber", required = false) String cardNumber,
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
        List<CardSharingRecordVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(CardSharingRecordVO.class, 20);
        } else {
            Date begin = beginTime != null ? (new Date(beginTime)) : null;
            Date end = endTime != null ? (new Date(endTime)) : null;
            voList = unionCardSharingRecordService.listCardSharingRecordVOByBusIdAndUnionId(busId, unionId, cardNumber, begin, end);
        }
        String[] titles = new String[]{"时间", "联盟卡号", "售卡金额(元)", "售卡佣金(元)", "售卡出处"};
        SXSSFWorkbook workbook = ExportUtil.newHSSFWorkbook(titles);
        Sheet sheet = workbook.getSheetAt(0);
        if (ListUtil.isNotEmpty(voList)) {
            int rowIndex = 1;
            CellStyle centerCellStyle = ExportUtil.newHSSFCellStyle(workbook, HSSFCellStyle.ALIGN_CENTER);
            for (CardSharingRecordVO vo : voList) {
                Row row = sheet.createRow(rowIndex++);
                int cellIndex = 0;
                // 时间
                Cell createTimeCell = row.createCell(cellIndex++);
                createTimeCell.setCellValue(DateUtil.getDateString(vo.getSharingRecord().getCreateTime(), DateUtil.DATETIME_PATTERN));
                createTimeCell.setCellStyle(centerCellStyle);
                // 联盟卡号
                Cell cardNumberCell = row.createCell(cellIndex++);
                cardNumberCell.setCellValue(vo.getFan().getNumber());
                cardNumberCell.setCellStyle(centerCellStyle);
                // 售卡金额(元)
                Cell sellPriceCell = row.createCell(cellIndex++);
                sellPriceCell.setCellValue(vo.getSharingRecord().getSellPrice());
                sellPriceCell.setCellStyle(centerCellStyle);
                // 售卡佣金(元)
                Cell sharingMoneyCell = row.createCell(cellIndex++);
                sharingMoneyCell.setCellValue(vo.getSharingRecord().getSharingMoney());
                sharingMoneyCell.setCellStyle(centerCellStyle);
                // 售卡出处
                Cell fromMemberCell = row.createCell(cellIndex);
                fromMemberCell.setCellValue(vo.getMember().getEnterpriseName());
                fromMemberCell.setCellStyle(centerCellStyle);
            }
        }
        String fileName = "售卡佣金分成记录表";
        ExportUtil.responseExport(response, workbook, fileName);
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

}