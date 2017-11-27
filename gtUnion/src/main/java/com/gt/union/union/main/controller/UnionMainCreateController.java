package com.gt.union.union.main.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.union.main.vo.UnionPermitCheckVO;
import com.gt.union.union.main.vo.UnionCreateVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 联盟创建 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
 */
@Api(description = "联盟创建")
@RestController
@RequestMapping("/unionMainCreate")
public class UnionMainCreateController {

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "检查许可", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/checkPermit", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String checkPermit(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        UnionPermitCheckVO result = MockUtil.get(UnionPermitCheckVO.class);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "创建联盟", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String save(HttpServletRequest request,
                       @ApiParam(value = "表单信息", name = "unionCreateVO", required = true)
                       @RequestBody UnionCreateVO unionCreateVO) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }
}