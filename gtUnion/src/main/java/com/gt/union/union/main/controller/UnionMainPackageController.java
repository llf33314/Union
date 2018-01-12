package com.gt.union.union.main.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.union.main.service.IUnionMainPackageService;
import com.gt.union.union.main.vo.UnionPackageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 盟主服务套餐 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 15:26:16
 */
@Api(description = "盟主服务套餐")
@RestController
@RequestMapping("/unionMainPackage")
public class UnionMainPackageController {

    @Autowired
    private IUnionMainPackageService unionMainPackageService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "我的联盟-创建联盟-购买盟主服务", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/option", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getUnionPackageVO(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        UnionPackageVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(UnionPackageVO.class);
        } else {
            result = unionMainPackageService.getUnionPackageVOByBusId(busId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

}