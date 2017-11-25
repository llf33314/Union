package com.gt.union.opportunity.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.opportunity.vo.PayBrokerageDetailVO;
import com.gt.union.opportunity.vo.PayBrokerageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页获取佣金支付明细信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/payDetail/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageBrokeragePayDetail(HttpServletRequest request,
                                         Page page,
                                         @ApiParam(value = "联盟id", name = "unionId")
                                         @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<PayBrokerageVO> voList = MockUtil.list(PayBrokerageVO.class, page.getSize());
        page.setRecords(voList);
        return GtJsonResult.instanceSuccessMsg(page).toString();
    }

    @ApiOperation(value = "导出佣金支付明细信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/payDetail/export", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String exportBrokeragePayDetail(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg("TODO").toString();
    }

    @ApiOperation(value = "获取支付明细详情信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/detail", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getDetail(HttpServletRequest request,
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
        PayBrokerageDetailVO result = MockUtil.get(PayBrokerageDetailVO.class);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "导出支付明细详情信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/detail/export", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String exportDetail(HttpServletRequest request,
                               @ApiParam(value = "联盟id", name = "unionId", required = true)
                               @PathVariable("unionId") Integer unionId,
                               @ApiParam(value = "盟员id", name = "memberId", required = true)
                               @RequestParam(value = "memberId") Integer memberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg("TODO").toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

}