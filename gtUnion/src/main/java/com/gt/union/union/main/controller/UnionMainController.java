package com.gt.union.union.main.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.common.util.PageUtil;
import com.gt.union.union.main.entity.UnionMainDict;
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

    @ApiOperation(value = "获取联盟信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<UnionVO> getUnionMainVOById(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        UnionVO result = MockUtil.get(UnionVO.class);
        List<UnionMainDict> itemList = MockUtil.list(UnionMainDict.class, 3);
        result.setItemList(itemList);
//        UnionVO result = unionMainService.getUnionMainVOByIdAndBusId(unionId, busId);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "分页：获取其他有效的联盟", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/other/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<UnionVO>> pageOther(
            HttpServletRequest request,
            Page page) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<UnionVO> voList = MockUtil.list(UnionVO.class, page.getSize());
        for (UnionVO vo : voList) {
            List<UnionMainDict> itemList = MockUtil.list(UnionMainDict.class, 3);
            vo.setItemList(itemList);
        }
//        List<UnionVO> voList = unionMainService.listOtherValidByBusId(busId);
        Page<UnionVO> result = (Page<UnionVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "分页：获取我的有效的联盟", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/my/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<UnionVO>> pageMy(
            HttpServletRequest request,
            Page page) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<UnionVO> voList = MockUtil.list(UnionVO.class, page.getSize());
        for (UnionVO vo : voList) {
            List<UnionMainDict> itemList = MockUtil.list(UnionMainDict.class, 3);
            vo.setItemList(itemList);
        }
        Page<UnionVO> result = (Page<UnionVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "获取我的有效的联盟", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/my", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<List<UnionVO>> listMy(HttpServletRequest request) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<UnionVO> result = MockUtil.list(UnionVO.class, 3);
        for (UnionVO vo : result) {
            List<UnionMainDict> itemList = MockUtil.list(UnionMainDict.class, 3);
            vo.setItemList(itemList);
        }
//        List<UnionVO> result = unionMainService.listMyValidByBusId(busId);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "更新联盟信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{unionId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> updateUnionMainVOById(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "表单信息", name = "unionMainVO", required = true)
            @RequestBody UnionVO unionMainVO) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
//        unionMainService.updateUnionMainVOByIdAndBusId(unionId, busId, unionMainVO);
        return GtJsonResult.instanceSuccessMsg();
    }

    //-------------------------------------------------- post ----------------------------------------------------------

}