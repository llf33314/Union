package com.gt.union.finance.verifier.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.finance.verifier.entity.UnionVerifier;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 平台管理者 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 14:54:27
 */
@Api(description = "平台管理者")
@RestController
@RequestMapping("/unionVerifier")
public class UnionVerifierController {

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "获取所有平台管理者", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<List<UnionVerifier>> list(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<UnionVerifier> result = MockUtil.list(UnionVerifier.class, 20);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    //-------------------------------------------------- put -----------------------------------------------------------


    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "新增平台管理者", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> save(
            HttpServletRequest request,
            @ApiParam(value = "验证码", name = "code", required = true)
            @RequestParam(value = "code") String code,
            @ApiParam(value = "表单信息", name = "verifier", required = true)
            @RequestBody UnionVerifier verifier) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg();
    }

    //------------------------------------------------- delete ---------------------------------------------------------

    @ApiOperation(value = "删除平台管理者", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{verifierId}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> removeById(
            HttpServletRequest request,
            @ApiParam(value = "平台管理者id", name = "verifierId", required = true)
            @PathVariable("verifierId") Integer verifierId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg();
    }

}