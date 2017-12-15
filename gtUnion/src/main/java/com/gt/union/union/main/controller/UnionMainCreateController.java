package com.gt.union.union.main.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.union.main.service.IUnionMainCreateService;
import com.gt.union.union.main.vo.UnionCreateVO;
import com.gt.union.union.main.vo.UnionPermitCheckVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private IUnionMainCreateService unionMainCreateService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "创建联盟-检查联盟许可", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/checkPermit", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getPermitCheckVO(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        // mock
        UnionPermitCheckVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(UnionPermitCheckVO.class);
        } else {
            result = unionMainCreateService.getPermitCheckVOByBusId(busId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "创建联盟-保存", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String saveUnionCreateVO(
            HttpServletRequest request,
            @ApiParam(value = "表单信息", name = "unionCreateVO", required = true)
            @RequestBody UnionCreateVO unionCreateVO) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            unionMainCreateService.saveUnionCreateVOByBusId(busId, unionCreateVO);
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }
}