package com.gt.union.card.main.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.card.main.service.IUnionCardFanService;
import com.gt.union.card.main.vo.CardFanDetailVO;
import com.gt.union.card.main.vo.CardFanSearchVO;
import com.gt.union.card.main.vo.CardFanVO;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.ExportUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.MockUtil;
import com.gt.union.common.util.PageUtil;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
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
 * 联盟卡粉丝信息 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 17:39:13
 */
@Api(description = "联盟卡粉丝信息")
@RestController
@RequestMapping("/unionCardFan")
public class UnionCardFanController {

    @Autowired
    private IUnionCardFanService unionCardFanService;

    @Autowired
    private IUnionMainService unionMainService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "首页-联盟卡-分页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageFanVOByUnionId(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @RequestParam(value = "unionId") Integer unionId,
            @ApiParam(value = "联盟卡号", name = "number")
            @RequestParam(value = "number", required = false) String optNumber,
            @ApiParam(value = "手机号", name = "phone")
            @RequestParam(value = "phone", required = false) String optPhone) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<CardFanVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(CardFanVO.class, page.getSize());
        } else {
            voList = unionCardFanService.listCardFanVoByBusIdAndUnionId(busId, unionId, optNumber, optPhone);
        }
        Page<CardFanVO> result = (Page<CardFanVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "首页-联盟卡-导出", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/export", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void exportFanVOByUnionId(
            HttpServletRequest request,
            HttpServletResponse response,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @RequestParam(value = "unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }

        List<CardFanVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(CardFanVO.class, 20);
        } else {
            voList = unionCardFanService.listCardFanVoByBusIdAndUnionId(busId, unionId, null, null);
        }
        String[] titles = new String[]{"盟员卡号", "手机号", "联盟积分"};
        HSSFWorkbook workbook = ExportUtil.newHSSFWorkbook(titles);
        HSSFSheet sheet = workbook.getSheetAt(0);
        if (ListUtil.isNotEmpty(voList)) {
            int rowIndex = 1;
            HSSFCellStyle centerCellStyle = ExportUtil.newHSSFCellStyle(workbook, HSSFCellStyle.ALIGN_CENTER);
            for (CardFanVO vo : voList) {
                HSSFRow row = sheet.createRow(rowIndex++);
                int cellIndex = 0;
                // 盟员卡号
                HSSFCell cardNumberCell = row.createCell(cellIndex++);
                cardNumberCell.setCellValue(vo.getFan().getNumber());
                cardNumberCell.setCellStyle(centerCellStyle);
                // 手机号
                HSSFCell phoneCell = row.createCell(cellIndex++);
                phoneCell.setCellValue(vo.getFan().getPhone());
                phoneCell.setCellStyle(centerCellStyle);
                // 联盟积分
                HSSFCell integralCell = row.createCell(cellIndex);
                integralCell.setCellValue(vo.getIntegral());
                integralCell.setCellStyle(centerCellStyle);
            }
        }
        UnionMain union = unionMainService.getById(unionId);
        String fileName = union.getName() + "的联盟卡列表";
        ExportUtil.responseExport(response, workbook, fileName);
    }

    @ApiOperation(value = "首页-联盟卡-分页-详情", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{fanId}/detail", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getFanDetailVOIdAndUnionId(
            HttpServletRequest request,
            @ApiParam(value = "联盟卡粉丝信息id", name = "fanId", required = true)
            @PathVariable("fanId") Integer fanId,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @RequestParam(value = "unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        CardFanDetailVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(CardFanDetailVO.class);
            List<UnionCard> activityCardList = MockUtil.list(UnionCard.class, 20);
            result.setActivityCardList(activityCardList);
        } else {
            result = unionCardFanService.getFanDetailVOByBusIdAndIdAndUnionId(busId, fanId, unionId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "前台-联盟卡消费核销-搜索", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getSearchVO(
            HttpServletRequest request,
            @ApiParam(value = "联盟卡号或手机号", name = "numberOrPhone", required = true)
            @RequestParam(value = "numberOrPhone") String numberOrPhone,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        CardFanSearchVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(CardFanSearchVO.class);
            List<UnionMain> unionList = MockUtil.list(UnionMain.class, 3);
            result.setUnionList(unionList);
        } else {
            result = unionCardFanService.getCardFanSearchVOByBusId(busId, numberOrPhone, unionId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

}