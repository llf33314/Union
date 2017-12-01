package com.gt.union.union.main.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.union.main.vo.UnionPermitPayVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 联盟许可 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 15:26:19
 */
@Api(description = "联盟许可")
@RestController
@RequestMapping("/unionMainPermit")
public class UnionMainPermitController {

    //-------------------------------------------------- get -----------------------------------------------------------

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "购买联盟盟主服务-支付", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/packageId/{packageId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public GtJsonResult<UnionPermitPayVO> saveByPackageId(
            HttpServletRequest request,
            @ApiParam(value = "套餐id", name = "packageId", required = true)
            @PathVariable("packageId") Integer packageId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        UnionPermitPayVO payVO = MockUtil.get(UnionPermitPayVO.class);
        return GtJsonResult.instanceSuccessMsg(payVO);
    }

    @ApiOperation(value = "购买联盟盟主服务-支付-回调", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{permitId}/pay/callback", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public GtJsonResult updateCallbackByPermitId(
            HttpServletRequest request,
            @ApiParam(value = "联盟许可id", name = "permitId", required = true)
            @PathVariable("permitId") Integer permitId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg();
    }

}