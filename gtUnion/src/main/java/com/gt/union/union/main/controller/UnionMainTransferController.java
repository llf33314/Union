package com.gt.union.union.main.controller;

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
import com.gt.union.union.main.service.IUnionMainTransferService;
import com.gt.union.union.main.vo.UnionTransferVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 联盟转移 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
 */
@Api(description = "联盟转移")
@RestController
@RequestMapping("/unionMainTransfer")
public class UnionMainTransferController {

    @Autowired
    private IUnionMainTransferService unionMainTransferService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页：我的联盟-联盟设置-盟主权限转移", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageTransferVOByUnionId(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<UnionTransferVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(UnionTransferVO.class, page.getSize());
        } else {
            voList = unionMainTransferService.listUnionTransferVOByBusIdAndUnionId(busId, unionId);
        }
        Page<UnionTransferVO> result = (Page<UnionTransferVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "我的联盟-盟主权限转移-接受或拒绝", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{transferId}/unionId/{unionId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateStatusByIdAndUnionId(
            HttpServletRequest request,
            @ApiParam(value = "转移id", name = "transferId", required = true)
            @PathVariable("transferId") Integer transferId,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "是否接受(0:否 1:是)", name = "isAccept", required = true)
            @RequestParam(value = "isAccept") Integer isAccept) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.BUS_PARENT_TIP);
        }
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            unionMainTransferService.updateByBusIdAndIdAndUnionId(busId, transferId, unionId, isAccept);
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "我的联盟-联盟设置-盟主权限转移-分页-转移", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/toMemberId/{toMemberId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String saveByUnionIdAndToMemberId(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "目标盟员id", name = "toMemberId", required = true)
            @PathVariable("toMemberId") Integer toMemberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.BUS_PARENT_TIP);
        }
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            unionMainTransferService.saveByBusIdAndUnionIdAndToMemberId(busId, unionId, toMemberId);
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //-------------------------------------------------- delete --------------------------------------------------------
    
    @ApiOperation(value = "我的联盟-联盟设置-盟主权限转移-分页数据-撤销", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{transferId}/unionId/{unionId}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
    public String removeByIdAndUnionId(
            HttpServletRequest request,
            @ApiParam(value = "转移id", name = "transferId", required = true)
            @PathVariable("transferId") Integer transferId,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.BUS_PARENT_TIP);
        }
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            unionMainTransferService.removeByBusIdAndIdAndUnionId(busId, transferId, unionId);
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }
}