package com.gt.union.card.consume.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.consume.vo.ConsumePayVO;
import com.gt.union.card.consume.vo.ConsumeRecordVO;
import com.gt.union.card.consume.vo.ConsumeVO;
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
 * 消费核销 前端控制器
 *
 * @author linweicong
 * @version 2017-11-25 10:51:42
 */
@Api(description = "消费核销")
@RestController
@RequestMapping("/unionConsume")
public class UnionConsumeController {

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页获取消费核销记录", produces = "application/json;charset=UTF-8")
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
        Page<ConsumeVO> result = (Page<ConsumeVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "导出消费核销记录", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/record/export", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> exportConsumeVO(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg("TODO");
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "消费核销支付", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/fanId/{fanId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public GtJsonResult<ConsumePayVO> savePayVOByUnionIdAndFanId(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "联盟卡粉丝id", name = "fanId", required = true)
            @PathVariable("fanId") Integer fanId,
            @ApiParam(value = "表单信息", name = "recordVO", required = true)
            @RequestBody ConsumeRecordVO recordVO) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        ConsumePayVO result = MockUtil.get(ConsumePayVO.class);
        return GtJsonResult.instanceSuccessMsg(result);
    }

}