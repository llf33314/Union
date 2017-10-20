package com.gt.union.main.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.service.IUnionValidateService;
import com.gt.union.main.service.IUnionMainCreateService;
import com.gt.union.main.vo.UnionMainCreateVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * 创建联盟 前端控制器
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
@RestController
@RequestMapping("/unionMainCreate")
public class UnionMainCreateController {

    @Autowired
    private IUnionMainCreateService unionMainCreateService;

    @Autowired
    private IUnionValidateService unionValidateService;

    //-------------------------------------------------- get ----------------------------------------------------------

    @ApiOperation(value = "请求创建联盟，创建联盟两步走的第一步", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/instance", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String instance(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        Map<String, Object> result = this.unionMainCreateService.instanceByBusId(busUser.getId());
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put ----------------------------------------------------------
    //------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "保存创建联盟，创建联盟两步走的第二步", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/instance", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String saveInstance(HttpServletRequest request,
                               @ApiParam(name = "unionMainCreateVO", value = "创建联盟实体", required = true)
                               @RequestBody @Valid UnionMainCreateVO unionMainCreateVO, BindingResult bindingResult) throws Exception {
        this.unionValidateService.checkBindingResult(bindingResult);
        BusUser busUser = SessionUtils.getLoginUser(request);
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        this.unionMainCreateService.saveInstanceByBusIdAndVo(busUser.getId(), unionMainCreateVO);
        return GTJsonResult.instanceSuccessMsg().toString();
    }
}
