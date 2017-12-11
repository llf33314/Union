package com.gt.union.card.consume.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.CardConstant;
import com.gt.union.card.consume.service.IUnionConsumeService;
import com.gt.union.card.consume.vo.ConsumePayVO;
import com.gt.union.card.consume.vo.ConsumePostVO;
import com.gt.union.card.consume.vo.ConsumeVO;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Logger logger = Logger.getLogger(UnionConsumeController.class);

    @Autowired
    private IUnionConsumeService unionConsumeService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页：获取消费核销记录", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/record/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<ConsumeVO>> pageConsumeVO(
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
            @RequestParam(value = "beginTime", required = false) Date beginTime,
            @ApiParam(value = "结束时间", name = "endTime")
            @RequestParam(value = "endTime", required = false) Date endTime) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<ConsumeVO> voList = MockUtil.list(ConsumeVO.class, page.getSize());
        for (int i = 0; i < voList.size(); i++) {
            List<UnionCardProjectItem> nonErpTextList = MockUtil.list(UnionCardProjectItem.class, 3);
            voList.get(i).setTextList(nonErpTextList);
        }
//        List<ConsumeVO> voList = unionConsumeService.listConsumeVOByBusId(busId, unionId, shopId, cardNumber, phone, beginTime, endTime);
        Page<ConsumeVO> result = (Page<ConsumeVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "导出：消费核销记录", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/record/export", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void exportConsumeVO(HttpServletRequest request, HttpServletResponse response) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        List<ConsumeVO> voList = unionConsumeService.listConsumeVOByBusId(busId, null, null,
                null, null, null, null);
        String[] titles = new String[]{"所属联盟", "消费门店", "联盟卡号", "手机号", "消费金额(元)", "实收金额(元)", "优惠项目", "支付状态", "消费时间"};
        HSSFWorkbook workbook = ExportUtil.newHSSFWorkbook(titles);
        HSSFSheet sheet = workbook.getSheetAt(0);
        if (ListUtil.isNotEmpty(voList)) {
            int rowIndex = 1;
            HSSFCellStyle centerCellStyle = ExportUtil.newHSSFCellStyle(workbook, HSSFCellStyle.ALIGN_CENTER);
            for (ConsumeVO vo : voList) {
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
                itemListCell.setCellValue(JSONObject.toJSONString(vo.getTextList()));
                itemListCell.setCellStyle(centerCellStyle);
                // 支付状态
                HSSFCell payStatusCell = row.createCell(cellIndex++);
                Integer payStatus = vo.getConsume().getPayStatus();
                payStatusCell.setCellValue(CardConstant.CONSUME_PAY_STATUS_PAYING == payStatus ? "支付中"
                        : CardConstant.CONSUME_PAY_STATUS_SUCCESS == payStatus ? "已支付"
                        : CardConstant.CONSUME_PAY_STATUS_FAIL == payStatus ? "已退款" : "");
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
    public GtJsonResult<ConsumePayVO> saveConsumePayVOByUnionIdAndFanId(
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
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        // mock
        ConsumePayVO result = MockUtil.get(ConsumePayVO.class);
//        ConsumePayVO result = unionConsumeService.saveConsumePayVOByBusIdAndUnionIdAndFanId(busId, unionId, fanId, postVO);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "前台-联盟卡消费核销-支付-回调", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/79B4DE7C/{consumeId}/callback", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String updateCallbackById(
            @ApiParam(value = "消费id", name = "consumeId", required = true)
            @PathVariable("consumeId") Integer consumeId,
            @ApiParam(value = "socket关键字", name = "socketKey", required = true)
            @RequestParam(value = "socketKey") String socketKey,
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
        return unionConsumeService.updateCallbackByPermitId(consumeId, socketKey, payType, orderNo, isSuccess);
    }

}