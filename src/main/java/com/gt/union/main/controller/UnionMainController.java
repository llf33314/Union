package com.gt.union.main.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.service.IUnionValidateService;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.main.vo.UnionMainVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 联盟主表 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionMain")
public class UnionMainController {

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionValidateService unionValidateService;

    //-------------------------------------------------- get ----------------------------------------------------------

    @ApiOperation(value = "根据联盟id，获取联盟对象信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String get(@PathVariable("unionId") Integer unionId) throws Exception {
        UnionMain result = this.unionMainService.getById(unionId);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "分页获取我尚未加入的联盟列表信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/page/otherUnion", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageOther(HttpServletRequest request, Page page) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        Page<UnionMain> result = this.unionMainService.pageOtherUnionByBusId(page, busId);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "获取我的所有联盟的信息，包括我创建的、以及我加入的", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/list/myUnion", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listByBusId(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        List<UnionMain> result = this.unionMainService.listReadByBusId(busId);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "分页获取我的所有联盟的信息，包括我创建的、以及我加入的", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/page/myUnion", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String PageByBusId(HttpServletRequest request, Page page) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        Page result = this.unionMainService.pageReadByBusId(page, busId);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put ----------------------------------------------------------

    @ApiOperation(value = "更新联盟信息，要求盟主权限", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "3", description = "更新联盟信息，要求盟主权限")
    @RequestMapping(value = "/memberId/{memberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String method(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                         @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "unionMainVO", value = "更新信息实体", required = true)
                         @RequestBody @Valid UnionMainVO unionMainVO, BindingResult bindingResult) throws Exception {
        this.unionValidateService.checkBindingResult(bindingResult);
        BusUser busUser = SessionUtils.getLoginUser(request);
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        this.unionMainService.updateByMemberIdAndBusIdAndVO(memberId, busUser.getId(), unionMainVO);
        return GTJsonResult.instanceSuccessMsg().toString();
    }

    //------------------------------------------------- post ----------------------------------------------------------
}
