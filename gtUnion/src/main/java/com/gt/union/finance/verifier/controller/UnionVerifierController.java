package com.gt.union.finance.verifier.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.common.util.PageUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.QRcodeKit;
import com.gt.union.finance.verifier.entity.UnionVerifier;
import com.gt.union.finance.verifier.service.IUnionVerifierService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    @Autowired
    private IUnionVerifierService unionVerifierService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "佣金平台-提现", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/h5Code", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void h5Code(HttpServletResponse response) throws Exception {
        String url = PropertiesUtil.getUnionUrl() + "/brokeragePhone/#/" + "index";
        Integer width = 250;
        Integer height = 250;
        QRcodeKit.buildQRcode(url, width, height, response);
    }

    @ApiOperation(value = "分页：获取平台管理者", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageVerifier(HttpServletRequest request, Page page) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<UnionVerifier> verifierList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            verifierList = MockUtil.list(UnionVerifier.class, page.getSize());
        } else {
            verifierList = unionVerifierService.listFinanceByBusId(busId);
        }
        Page<UnionVerifier> result = (Page<UnionVerifier>) page;
        result = PageUtil.setRecord(result, verifierList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------


    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "新增平台管理者", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String save(
            HttpServletRequest request,
            @ApiParam(value = "验证码", name = "code", required = true)
            @RequestParam(value = "code") String code,
            @ApiParam(value = "表单信息", name = "verifier", required = true)
            @RequestBody UnionVerifier verifier) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            unionVerifierService.saveByBusId(busId, code, verifier);
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //------------------------------------------------- delete ---------------------------------------------------------

    @ApiOperation(value = "删除平台管理者", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{verifierId}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
    public String removeById(
            HttpServletRequest request,
            @ApiParam(value = "平台管理者id", name = "verifierId", required = true)
            @PathVariable("verifierId") Integer verifierId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            unionVerifierService.removeByBusIdAndId(busId, verifierId);
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

}