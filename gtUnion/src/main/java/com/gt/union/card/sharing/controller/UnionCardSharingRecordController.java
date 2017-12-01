package com.gt.union.card.sharing.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.sharing.vo.CardSharingRecordVO;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.common.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页获取售卡佣金分成记录信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<CardSharingRecordVO>> pageSharingRecordVOByUnionId(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable(value = "unionId") Integer unionId,
            @ApiParam(value = "联盟卡号", name = "cardNumber")
            @RequestParam(value = "cardNumber", required = false) String cardNumber,
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
        List<CardSharingRecordVO> voList = MockUtil.list(CardSharingRecordVO.class, page.getSize());
        Page<CardSharingRecordVO> result = (Page<CardSharingRecordVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

}