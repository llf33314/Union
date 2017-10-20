package com.gt.union.main.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.main.service.IIndexService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 首页 前端控制器
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
@RestController
@RequestMapping("/union")
public class IndexController {

    @Autowired
    private IIndexService indexService;

    //-------------------------------------------------- get ----------------------------------------------------------

    @ApiOperation(value = "商家联盟首页-默认未选定盟员身份", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/index", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String index(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        //默认是主帐号
        Integer accountType = BusUserConstant.ACCOUNT_TYPE_MASTER;
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
            //存在父帐号，则说明是子帐号
            accountType = BusUserConstant.ACCOUNT_TYPE_SUB;
        }

        Map<String, Object> result = this.indexService.index(busId);
        result.put("busId", busId);
        result.put("accountType", accountType);

        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "商家联盟首页-选定盟员身份", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/index/memberId/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String indexByUnionId(HttpServletRequest request,
                                 @ApiParam(name = "memberId", value = "选定的盟员身份id", required = true)
                                 @PathVariable("memberId") Integer memberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        //默认是主帐号
        Integer accountType = BusUserConstant.ACCOUNT_TYPE_MASTER;
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
            //存在父帐号，则说明是子帐号
            accountType = BusUserConstant.ACCOUNT_TYPE_SUB;
        }

        Map<String, Object> result = this.indexService.indexByMemberId(memberId, busId);
        result.put("busId", busId);
        result.put("accountType", accountType);

        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put ----------------------------------------------------------
    //------------------------------------------------- post ----------------------------------------------------------
}
