package com.gt.union.member.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.service.IUnionValidateService;
import com.gt.union.common.util.ExportUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.member.vo.CardDividePercentVO;
import com.gt.union.member.vo.UnionMemberVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟成员 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionMember")
public class UnionMemberController {

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionValidateService unionValidateService;

    @Autowired
    private IUnionMainService unionMainService;

    //-------------------------------------------------- get ----------------------------------------------------------

    @ApiOperation(value = "根据我的盟员身份id分页获取所有与我同属一个联盟的盟员相关信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pageMap/memberId/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageMapByMemberId(HttpServletRequest request, Page page
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                    @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "enterpriseName", value = "盟员名称，模糊匹配")
                                    @RequestParam(value = "enterpriseName", required = false) String enterpriseName) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        Page result = this.unionMemberService.pageMapByIdAndBusId(page, memberId, busId, enterpriseName);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "根据我的盟员身份id获取所有与我同属一个联盟的盟员相关信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/listMap/memberId/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listMapByMemberId(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                    @PathVariable("memberId") Integer memberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        List<Map<String, Object>> result = this.unionMemberService.listMapByIdAndBusId(memberId, busId, null);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "根据我的盟员身份id获取对应的盟员信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getById(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "盟员身份id", required = true)
                          @PathVariable("memberId") Integer memberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        UnionMember result = this.unionMemberService.getByIdAndBusId(memberId, busId);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "获取我的所有盟员身份，以及盟员身份所在的联盟信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/listMap", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listReadMap(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        List<Map<String, Object>> result = this.unionMemberService.listReadMapByBusId(busId);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "导出：根据我的盟员身份id分页获取所有与我同属一个联盟的盟员相关信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/exportMap/memberId/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void exportByMemberId(HttpServletRequest request, HttpServletResponse response
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                 @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "enterpriseName", value = "盟员名称，模糊匹配")
                                 @RequestParam(value = "enterpriseName", required = false) String enterpriseName) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        List<Map<String, Object>> resultList = this.unionMemberService.listMapByIdAndBusId(memberId, busId, enterpriseName);
        String[] titles = new String[]{"盟员名称", "加入时间", "我给TA的折扣（折）", "TA给我的折扣（折）", "售卡分成比例"};
        HSSFWorkbook wb = ExportUtil.newHSSFWorkbook(titles);
        HSSFSheet sheet = wb.getSheetAt(0);
        if (ListUtil.isNotEmpty(resultList)) {
            int rowIndex = 1;
            HSSFCellStyle centerCellStyle = ExportUtil.newHSSFCellStyle(wb, HSSFCellStyle.ALIGN_CENTER);
            for (Map<String, Object> resultMap : resultList) {
                HSSFRow row = sheet.createRow(rowIndex++);
                int cellIndex = 0;
                HSSFCell enterpriseNameCell = row.createCell(cellIndex++);
                //盟员名称
                String tgtEnterpriseName = resultMap.get("enterpriseName") != null ? resultMap.get("enterpriseName").toString() : "";
                enterpriseNameCell.setCellValue(tgtEnterpriseName);
                enterpriseNameCell.setCellStyle(centerCellStyle);
                //加入时间
                HSSFCell createTimeCell = row.createCell(cellIndex++);
                String tgtCreateTime = resultMap.get("createTime") != null ? resultMap.get("createTime").toString() : "";
                createTimeCell.setCellValue(tgtCreateTime);
                createTimeCell.setCellStyle(centerCellStyle);
                //我给他的折扣
                HSSFCell discountFromMeCell = row.createCell(cellIndex++);
                String tgtDiscountFromMe = resultMap.get("discountFromMe") != null ? resultMap.get("discountFromMe").toString() : "";
                discountFromMeCell.setCellValue(tgtDiscountFromMe);
                discountFromMeCell.setCellStyle(centerCellStyle);
                //他给我的折扣
                HSSFCell discountToMeCell = row.createCell(cellIndex++);
                String tgtDiscountToMe = resultMap.get("discountToMe") != null ? resultMap.get("discountToMe").toString() : "";
                discountToMeCell.setCellValue(tgtDiscountToMe);
                discountToMeCell.setCellStyle(centerCellStyle);
                //售卡分成比例
                HSSFCell cardDividePercentCell = row.createCell(cellIndex++);
                String tgtCardDividePercent = resultMap.get("cardDividePercent") != null ? resultMap.get("cardDividePercent").toString() + "%" : "";
                cardDividePercentCell.setCellValue(tgtCardDividePercent);
                cardDividePercentCell.setCellStyle(centerCellStyle);
            }
        }
        UnionMain unionMain = this.unionMainService.getByBusIdAndMemberId(busId, memberId);
        String filename = unionMain.getName() + "的盟员列表";
        ExportUtil.responseExport(response, wb, filename);
    }

    //-------------------------------------------------- put ----------------------------------------------------------

    @ApiOperation(value = "更新盟员信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{memberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateById(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "盟员身份id", required = true)
                             @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "unionMemberVO", value = "更新内容实体", required = true)
                             @RequestBody @Valid UnionMemberVO unionMemberVO, BindingResult bindingResult) throws Exception {
        this.unionValidateService.checkBindingResult(bindingResult);
        BusUser busUser = SessionUtils.getLoginUser(request);
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        this.unionMemberService.updateByIdAndBusId(memberId, busUser.getId(), unionMemberVO);
        return GTJsonResult.instanceSuccessMsg().toString();
    }

    @ApiOperation(value = "批量更新售卡分成比例", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/cardDividePercent/memberId/{memberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateCardDividePercentById(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                              @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "cardDividePercentVO", value = "更新内容实体", required = true)
                                              @RequestBody @Valid List<CardDividePercentVO> cardDividePercentVOList, BindingResult bindingResult) throws Exception {
        this.unionValidateService.checkBindingResult(bindingResult);
        BusUser busUser = SessionUtils.getLoginUser(request);
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        this.unionMemberService.updateCardDividePercentByIdAndBusId(memberId, busUser.getId(), cardDividePercentVOList);
        return GTJsonResult.instanceSuccessMsg().toString();
    }

    //------------------------------------------------- post ----------------------------------------------------------

}
