package com.gt.union.opportunity.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.KeysUtil;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.client.socket.SocketService;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.DataExportException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.service.IUnionValidateService;
import com.gt.union.common.util.*;
import com.gt.union.opportunity.service.IUnionOpportunityService;
import com.gt.union.opportunity.vo.UnionOpportunityVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商机推荐 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionOpportunity")
public class UnionOpportunityController {

    private Logger logger = LoggerFactory.getLogger(UnionOpportunityController.class);

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionOpportunityService unionOpportunityService;

    @Autowired
    private IUnionValidateService unionValidateService;

    @Autowired
    private SocketService socketService;

    //-------------------------------------------------- get ----------------------------------------------------------

    @ApiOperation(value = "查询我推荐的商机信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/fromMe", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listFromMy(HttpServletRequest request, Page page
            , @ApiParam(name = "unionId", value = "联盟id")
                             @RequestParam(name = "unionId", required = false) Integer unionId
            , @ApiParam(name = "isAccept", value = "商机状态，1为未处理，2为受理，3为拒绝，当勾选多个时用英文字符的逗号拼接，如=1,2")
                             @RequestParam(name = "isAccept", required = false) String isAccept
            , @ApiParam(name = "clientName", value = "顾客姓名，模糊查询")
                             @RequestParam(name = "clientName", required = false) String clientName
            , @ApiParam(name = "clientPhone", value = "顾客电话，模糊查询")
                             @RequestParam(name = "clientPhone", required = false) String clientPhone) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != 0) {
            busId = busUser.getPid();
        }
        Page result = this.unionOpportunityService.pageFromMeMapByBusId(page, busId, unionId, isAccept, clientName, clientPhone);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "查询推荐给我的商机信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/toMe", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listToMy(HttpServletRequest request, Page page
            , @ApiParam(name = "unionId", value = "所属联盟id")
                           @RequestParam(name = "unionId", required = false) Integer unionId
            , @ApiParam(name = "isAccept", value = "商机状态，1为未处理，2为受理，3为拒绝，当勾选多个时用英文字符的分号拼接，如=1,2")
                           @RequestParam(name = "isAccept", required = false) String isAccept
            , @ApiParam(name = "clientNname", value = "顾客姓名，模糊查询")
                           @RequestParam(name = "clientName", required = false) String clientName
            , @ApiParam(name = "clientPhone", value = "顾客电话，模糊查询")
                           @RequestParam(name = "clientPhone", required = false) String clientPhone) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != 0) {
            busId = busUser.getPid();
        }
        Page result = this.unionOpportunityService.pageToMeMapByBusId(page, busId, unionId, isAccept, clientName, clientPhone);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "查询我的佣金收入信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/income", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageIncome(HttpServletRequest request, Page page
            , @ApiParam(name = "unionId", value = "所属联盟id")
                             @RequestParam(name = "unionId", required = false) Integer unionId
            , @ApiParam(name = "toMemberId", value = "商机接收方的盟员身份id")
                             @RequestParam(name = "toMemberId", required = false) Integer toMemberId
            , @ApiParam(name = "isClose", value = "佣金结算状态，1：已结算 0：未结算，不传代表都包含")
                             @RequestParam(name = "isClose", required = false) Integer isClose
            , @ApiParam(name = "clientName", value = "顾客姓名，模糊查询")
                             @RequestParam(name = "clientName", required = false) String clientName
            , @ApiParam(name = "clientPhone", value = "顾客电话，模糊查询")
                             @RequestParam(name = "clientPhone", required = false) String clientPhone) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        Page result = this.unionOpportunityService.pageIncomeByBusId(page, busId, unionId, toMemberId, isClose, clientName, clientPhone);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "查询我的佣金支出信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/expense", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageExpense(HttpServletRequest request, Page page
            , @ApiParam(name = "unionId", value = "所属联盟id")
                              @RequestParam(name = "unionId", required = false) Integer unionId
            , @ApiParam(name = "fromMemberId", value = "商机推荐方的盟员身份id")
                              @RequestParam(name = "fromMemberId", required = false) Integer fromMemberId
            , @ApiParam(name = "isClose", value = "佣金结算状态，1：已结算 0：未结算，不传代表都包含")
                              @RequestParam(name = "isClose", required = false) Integer isClose
            , @ApiParam(name = "clientName", value = "顾客姓名，模糊查询")
                              @RequestParam(name = "clientName", required = false) String clientName
            , @ApiParam(name = "clientPhone", value = "顾客电话，模糊查询")
                              @RequestParam(name = "clientPhone", required = false) String clientPhone) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        Page result = this.unionOpportunityService.pageExpenseByBusId(page, busId, unionId, fromMemberId, isClose, clientName, clientPhone);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "分页查询商机佣金支付往来列表信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/contact/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageContact(HttpServletRequest request, Page page
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id")
                              @RequestParam(name = "memberId", required = false) Integer memberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        Page result = this.unionOpportunityService.pageContactByBusId(page, busId, memberId);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "导出：商机佣金支付往来列表信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/contact/export", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void exportContactByTgtMemberId(HttpServletRequest request, HttpServletResponse response
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id")
                                           @RequestParam(name = "memberId", required = false) Integer memberId) throws Exception {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer busId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                busId = busUser.getPid();
            }
            List<Map<String, Object>> resultList = this.unionOpportunityService.listContactByBusId(busId, memberId);
            String[] titles = new String[]{"所属联盟", "盟员名称", "商机往来金额（元）"};
            HSSFWorkbook wb = ExportUtil.newHSSFWorkbook(titles);
            HSSFSheet sheet = wb.getSheetAt(0);
            if (ListUtil.isNotEmpty(resultList)) {
                int rowIndex = 1;
                HSSFCellStyle centerCellStyle = ExportUtil.newHSSFCellStyle(wb, HSSFCellStyle.ALIGN_CENTER);
                for (Map<String, Object> map : resultList) {
                    int cellIndex = 0;
                    HSSFRow row = sheet.createRow(rowIndex++);
                    HSSFCell tgtUnionNameCell = row.createCell(cellIndex++);
                    //所属联盟
                    String tgtUnionName = map.get("tgtUnionName") != null ? map.get("tgtUnionName").toString() : "";
                    tgtUnionNameCell.setCellValue(tgtUnionName);
                    tgtUnionNameCell.setCellStyle(centerCellStyle);
                    //盟员名称
                    HSSFCell tgtMemberEnterpriseNameCell = row.createCell(cellIndex++);
                    String tgtMemberEnterpriseName = map.get("tgtMemberEnterpriseName") != null ? map.get("tgtMemberEnterpriseName").toString() : "";
                    tgtMemberEnterpriseNameCell.setCellValue(tgtMemberEnterpriseName);
                    tgtMemberEnterpriseNameCell.setCellStyle(centerCellStyle);
                    //商机往来金额（元）
                    HSSFCell contactMoneyCell = row.createCell(cellIndex++);
                    String tgtContactMoney = map.get("contactMoney") != null ? map.get("contactMoney").toString() : "";
                    contactMoneyCell.setCellValue(tgtContactMoney);
                    contactMoneyCell.setCellStyle(centerCellStyle);
                }
            }
            String filename = "佣金往来明细";
            ExportUtil.responseExport(response, wb, filename);
        }catch (BaseException e){
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            response.setCharacterEncoding("UTF-8");
            String result = "<script>alert('导出失败')</script>";
            PrintWriter writer = response.getWriter();
            writer.print(result);
            writer.close();
        } catch (DataExportException e){

        } catch (Exception e) {
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            response.setCharacterEncoding("UTF-8");
            String result = "<script>alert('导出失败')</script>";
            PrintWriter writer = response.getWriter();
            writer.print(result);
            writer.close();
        }

    }

    @ApiOperation(value = "查询商机佣金支付往来详情信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/contact/detail", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getContactDetailByTgtMemberId(HttpServletRequest request
            , @ApiParam(name = "tgtMemberId", value = "与操作人有商机佣金来往的盟员身份id", required = true)
                                                @RequestParam(name = "tgtMemberId") Integer tgtMemberId
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id")
                                                @RequestParam(name = "memberId", required = false) Integer memberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        Map<String, Object> result = this.unionOpportunityService.getContactDetailByBusIdAndTgtMemberId(busId, tgtMemberId, memberId);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "导出：商机佣金支付往来详情信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/contact/exportDetail", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void exportContactDetailByTgtMemberId(HttpServletRequest request, HttpServletResponse response
            , @ApiParam(name = "tgtMemberId", value = "与操作人有商机佣金来往的盟员身份id", required = true)
                                                 @RequestParam(name = "tgtMemberId") Integer tgtMemberId
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id")
                                                 @RequestParam(name = "memberId", required = false) Integer memberId) throws Exception {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer busId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                busId = busUser.getPid();
            }
            Map<String, Object> resultMap = this.unionOpportunityService.getContactDetailByBusIdAndTgtMemberId(busId, tgtMemberId, memberId);
            String[] titles = new String[]{"时间", "顾客姓名", "电话", "佣金（元）"};
            HSSFWorkbook wb = ExportUtil.newHSSFWorkbook(titles);
            HSSFSheet sheet = wb.getSheetAt(0);
            if (resultMap != null && resultMap.get("contactList") != null) {
                List<Map<String, Object>> contactList = (List<Map<String, Object>>) resultMap.get("contactList");
                if (ListUtil.isNotEmpty(contactList)) {
                    int rowIndex = 1;
                    HSSFCellStyle centerCellStyle = ExportUtil.newHSSFCellStyle(wb, HSSFCellStyle.ALIGN_CENTER);
                    for (Map<String, Object> map : contactList) {
                        int cellIndex = 0;
                        HSSFRow row = sheet.createRow(rowIndex++);
                        HSSFCell lastModifyTimeCell = row.createCell(cellIndex++);
                        //时间
                        String tgtLastModifyTime = map.get("lastModifyTime") != null ? map.get("lastModifyTime").toString() : "";
                        lastModifyTimeCell.setCellValue(tgtLastModifyTime);
                        lastModifyTimeCell.setCellStyle(centerCellStyle);
                        //顾客姓名
                        HSSFCell clientNameCell = row.createCell(cellIndex++);
                        String tgtClientName = map.get("clientName") != null ? map.get("clientName").toString() : "";
                        clientNameCell.setCellValue(tgtClientName);
                        clientNameCell.setCellStyle(centerCellStyle);
                        //电话
                        HSSFCell clientPhoneCell = row.createCell(cellIndex++);
                        String tgtClientPhone = map.get("clientPhone") != null ? map.get("clientPhone").toString() : "";
                        clientPhoneCell.setCellValue(tgtClientPhone);
                        clientPhoneCell.setCellStyle(centerCellStyle);
                        //佣金（元）
                        HSSFCell brokeragePriceCell = row.createCell(cellIndex++);
                        String tgtBrokeragePrice = map.get("brokeragePrice") != null ? map.get("brokeragePrice").toString() : "";
                        brokeragePriceCell.setCellValue(tgtBrokeragePrice);
                        brokeragePriceCell.setCellStyle(centerCellStyle);
                    }
                    //统计佣金往来总额
                    HSSFRow rowSum = sheet.createRow(rowIndex++);
                    //标题
                    HSSFCell contactMoneySumTitleCell = rowSum.createCell(0);
                    contactMoneySumTitleCell.setCellValue("合计：");
                    contactMoneySumTitleCell.setCellStyle(centerCellStyle);
                    //内容
                    HSSFCell contactMoneySumCell = rowSum.createCell(1);
                    String tgtContactMoneySum = resultMap.get("contactMoneySum") != null ? resultMap.get("contactMoneySum").toString() : "";
                    contactMoneySumCell.setCellValue(tgtContactMoneySum);
                    contactMoneySumCell.setCellStyle(centerCellStyle);
                }
            }
            String filename = "佣金明细详情";
            ExportUtil.responseExport(response, wb, filename);
        }catch (BaseException e){
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            response.setCharacterEncoding("UTF-8");
            String result = "<script>alert('导出失败')</script>";
            PrintWriter writer = response.getWriter();
            writer.print(result);
            writer.close();
        } catch (DataExportException e){

        } catch (Exception e) {
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            response.setCharacterEncoding("UTF-8");
            String result = "<script>alert('导出失败')</script>";
            PrintWriter writer = response.getWriter();
            writer.print(result);
            writer.close();
        }
    }

    @ApiOperation(value = "获取商机统计数据", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/memberId/{memberId}/statistics", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getStatisticData(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                   @PathVariable("memberId") Integer memberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        Map<String, Object> result = this.unionOpportunityService.getStatisticsByBusIdAndMemberId(busId, memberId);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put ----------------------------------------------------------

    @ApiOperation(value = "接受商家推荐", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{opportunityId}/isAccept/2", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateAcceptYesById(HttpServletRequest request
            , @ApiParam(name = "opportunityId", value = "商机推荐id", required = true)
                                      @PathVariable("opportunityId") Integer opportunityId
            , @ApiParam(name = "acceptPrice", value = "受理金额", required = true)
                                      @RequestBody @NotNull Double acceptPrice) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        if (busUser.getPid() != null && busUser.getPid() != 0) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        this.unionOpportunityService.updateAcceptYesByIdAndBusId(opportunityId, busUser.getId(), acceptPrice);
        return GTJsonResult.instanceSuccessMsg().toString();
    }

    @ApiOperation(value = "拒绝商家推荐", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{opportunityId}/isAccept/3", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateAcceptNoById(HttpServletRequest request
            , @ApiParam(name = "opportunityId", value = "商机推荐id", required = true)
                                     @PathVariable("opportunityId") Integer opportunityId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        if (busUser.getPid() != null && busUser.getPid() != 0) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        this.unionOpportunityService.updateAcceptNoByIdAndBusId(opportunityId, busUser.getId());
        return GTJsonResult.instanceSuccessMsg().toString();
    }

    //------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "添加商机推荐", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/memberId/{memberId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String save(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                       @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "unionOpportunityVO", value = "推荐的商机信息", required = true)
                       @RequestBody @Valid UnionOpportunityVO vo, BindingResult bindingResult) throws Exception {
        this.unionValidateService.checkBindingResult(bindingResult);
        BusUser user = SessionUtils.getLoginUser(request);
        Integer busId = user.getId();
        if (CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {//子账号
            busId = user.getPid();
        }
        this.unionOpportunityService.saveByBusIdAndMemberId(busId, memberId, vo);
        return GTJsonResult.instanceSuccessMsg().toString();
    }

    //------------------------------------------------- money ----------------------------------------------------------
    @RequestMapping(value = "/79B4DE7C/paymentSuccess/{only}",method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String payOpportunitySuccess(HttpServletRequest request, HttpServletResponse response
            , @PathVariable(name = "only", required = true) String only
            , @RequestBody Map<String,Object> param) throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        String statusKey = RedisKeyUtil.getRecommendPayStatusKey(only);
        try {
            if(param.get("result_code").equals("SUCCESS") && param.get("result_code").equals("SUCCESS")){
                logger.info("商机佣金支付成功，param------------------" + JSON.toJSONString(param));
                logger.info("商机佣金支付成功，only------------------" + only);
                String paramKey = RedisKeyUtil.getRecommendPayParamKey(only);
                String paramData = redisCacheUtil.get(paramKey);
                Map map = JSON.parseObject(paramData,Map.class);
                unionOpportunityService.payOpportunitySuccess(param.get("out_trade_no").toString(), only);
                String status = redisCacheUtil.get(statusKey);
                if (CommonUtil.isEmpty(status)) {//订单超时
                    status = ConfigConstant.USER_ORDER_STATUS_004;
                }else {
                    status = JSON.parseObject(status,String.class);
                }
                if (ConfigConstant.USER_ORDER_STATUS_003.equals(status)) {//订单支付成功
                    redisCacheUtil.remove(statusKey);
                }

                if (ConfigConstant.USER_ORDER_STATUS_005.equals(status)) {//订单支付失败
                    redisCacheUtil.remove(statusKey);
                }
                Map<String,Object> result = new HashMap<String,Object>();
                result.put("status",status);
                result.put("only",only);
                logger.info("商机佣金扫码支付成功回调----------" + JSON.toJSONString(result));
                socketService.socketSendMessage(PropertiesUtil.getSocketKey() + CommonUtil.toInteger(map.get("payBusId")), JSON.toJSONString(data),"");
                data.put("code", 0);
                data.put("msg", "成功");
                return JSON.toJSONString(data);
            }else {
                throw new BusinessException("支付失败");
            }
        } catch (BaseException e) {
            redisCacheUtil.set(statusKey,ConfigConstant.USER_ORDER_STATUS_005);
            logger.error("商机佣金支付成功后，产生错误：" + e);
            data.put("code", -1);
            data.put("msg", e.getErrorMsg());
            return JSON.toJSONString(data);
        } catch (Exception e) {
            redisCacheUtil.set(statusKey,ConfigConstant.USER_ORDER_STATUS_005);
            logger.error("商机佣金支付成功后，产生错误：" + e);
            data.put("code", -1);
            data.put("msg", "失败");
            return JSON.toJSONString(data);
        }
    }



    @ApiOperation(value = "生成商机推荐支付订单二维码", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "2", description = "生成商机推荐支付二维码")
    @RequestMapping(value = "/qrCode/{ids}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String payOpportunityQRCode(HttpServletRequest request
            , @ApiParam(name = "ids", value = "商机ids，使用“,”隔开", required = true)
                                       @PathVariable("ids") String ids) {
        try {
            BusUser user = SessionUtils.getLoginUser(request);
            if (CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0) {
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            Map<String, Object> data = unionOpportunityService.payOpportunityQRCode(user.getId(), ids);
            StringBuilder sb = new StringBuilder("?");
            sb.append("totalFee=" + data.get("totalFee"));
            sb.append("&model=" + data.get("model"));
            sb.append("&busId=" + data.get("busId"));
            sb.append("&appidType=" + data.get("appidType"));
            sb.append("&appid=" + data.get("appid"));
            sb.append("&orderNum=" + data.get("orderNum"));
            sb.append("&desc=" + data.get("desc"));
            sb.append("&isreturn=" + data.get("isreturn"));
            sb.append("&notifyUrl=" + data.get("notifyUrl"));
            sb.append("&isSendMessage=" + data.get("isSendMessage"));
            sb.append("&payWay=" + data.get("payWay"));
            sb.append("&sourceType=" + data.get("sourceType"));
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("url", PropertiesUtil.getWxmpUrl() + "/pay/B02A45A5/79B4DE7C/createPayQR.do" + sb.toString());
            result.put("only", data.get("only"));
            result.put("userId",PropertiesUtil.getSocketKey() + user.getId());
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("生成商机推荐支付订单二维码错误：" + e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
        }
    }

    @ApiOperation(value = "获取商机佣金支付状态，用定时器请求，004：订单超时 003：支付成功 005：支付失败", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "status/{only}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getStatus(@PathVariable("only") String only) throws Exception {
        logger.info("获取商机佣金支付状态：" + only);
        try {
            String paramKey = RedisKeyUtil.getRecommendPayParamKey(only);
            String statusKey = RedisKeyUtil.getRecommendPayStatusKey(only);
            String status = redisCacheUtil.get(statusKey);
            if (CommonUtil.isEmpty(status)) {//订单超时
                status = ConfigConstant.USER_ORDER_STATUS_004;
            }else {
                status = JSON.parseObject(status,String.class);
            }
            if (ConfigConstant.USER_ORDER_STATUS_003.equals(status)) {//订单支付成功
                redisCacheUtil.remove(statusKey);
                redisCacheUtil.remove(paramKey);
            }

            if (ConfigConstant.USER_ORDER_STATUS_005.equals(status)) {//订单支付失败
                redisCacheUtil.remove(statusKey);
                redisCacheUtil.remove(paramKey);
            }
            return GTJsonResult.instanceSuccessMsg(status).toString();
        } catch (Exception e) {
            logger.error("获取商机佣金支付错误：" + e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.SYS_ERROR).toString();
        }
    }
}
