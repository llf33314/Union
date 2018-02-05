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
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.main.vo.UnionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 联盟 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 15:26:16
 */
@Api(description = "联盟")
@RestController
@RequestMapping("/unionMain")
public class UnionMainController {

    @Autowired
    private IUnionMainService unionMainService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "我的联盟-联盟设置-联盟基本信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getUnionMainVOById(
        HttpServletRequest request,
        @ApiParam(value = "联盟id", name = "unionId", required = true)
        @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        UnionVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(UnionVO.class);
        } else {
            result = unionMainService.getUnionVOByBusIdAndId(busId, unionId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "分页：我的联盟-加入联盟-选择联盟", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/join/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageOther(
        HttpServletRequest request,
        Page page) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<UnionVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(UnionVO.class, 20);
        } else {
            voList = unionMainService.listValidJoinByBusId(busId);
        }
        Page<UnionVO> result = (Page<UnionVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "辅助-列表：获取我参与过的联盟", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/busUser", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listByBusUser(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<UnionMain> result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.list(UnionMain.class, 20);
        } else {
            result = unionMainService.listReadByBusId(busId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "辅助-列表：获取我的当前有效的联盟", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/busUser/valid", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listValidByBusUser(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<UnionMain> result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.list(UnionMain.class, 3);
        } else {
            result = unionMainService.listValidReadByBusId(busId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "我的联盟-联盟设置-联盟基本信息-保存", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{unionId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateUnionMainVOById(
        HttpServletRequest request,
        @ApiParam(value = "联盟id", name = "unionId", required = true)
        @PathVariable("unionId") Integer unionId,
        @ApiParam(value = "表单信息", name = "unionMainVO", required = true)
        @RequestBody UnionVO unionMainVO) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.BUS_PARENT_TIP);
        }
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            unionMainService.updateUnionVOByBusIdAndId(busId, unionId, unionMainVO);
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //-------------------------------------------------- post ----------------------------------------------------------

}
