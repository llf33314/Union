package com.gt.union.card.project.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.card.project.service.IUnionCardProjectService;
import com.gt.union.card.project.vo.CardProjectCheckUpdateVO;
import com.gt.union.card.project.vo.CardProjectCheckVO;
import com.gt.union.card.project.vo.CardProjectJoinMemberVO;
import com.gt.union.card.project.vo.CardProjectVO;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 活动项目 前端控制器
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
@Api(description = "活动项目")
@RestController
@RequestMapping("/unionCardProject")
public class UnionCardProjectController {

    @Autowired
    private IUnionCardProjectService unionCardProjectService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "我的联盟-联盟卡设置-活动卡设置-分页数据-参与盟员数", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityId/{activityId}/unionId/{unionId}/joinMember", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listJoinMemberByActivityIdAndUnionId(
            HttpServletRequest request,
            @ApiParam(value = "联盟卡活动id", name = "activityId", required = true)
            @PathVariable("activityId") Integer activityId,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<CardProjectJoinMemberVO> result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                CardProjectJoinMemberVO vo = MockUtil.get(CardProjectJoinMemberVO.class);
                List<UnionCardProjectItem> itemList = MockUtil.list(UnionCardProjectItem.class, 3);
                vo.setItemList(itemList);
                result.add(vo);
            }
        } else {
            result = unionCardProjectService.listJoinMemberByBusIdAndUnionIdAndActivityId(busId, unionId, activityId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "我的联盟-联盟卡设置-活动卡设置-分页数据-待审核", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityId/{activityId}/unionId/{unionId}/projectCheck", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listProjectCheckByActivityIdAndUnionId(
            HttpServletRequest request,
            @ApiParam(value = "联盟卡活动id", name = "activityId", required = true)
            @PathVariable("activityId") Integer activityId,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.BUS_PARENT_TIP);
        }
        // mock
        List<CardProjectCheckVO> result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                CardProjectCheckVO vo = MockUtil.get(CardProjectCheckVO.class);
                List<UnionCardProjectItem> itemList = MockUtil.list(UnionCardProjectItem.class, 3);
                vo.setItemList(itemList);
                result.add(vo);
            }
        } else {
            result = unionCardProjectService.listProjectCheckByBusIdAndUnionIdAndActivityId(busId, unionId, activityId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "我的联盟-联盟卡设置-活动卡设置-分页数据-我的活动项目", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityId/{activityId}/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getProjectVOByIdAndUnionId(
            HttpServletRequest request,
            @ApiParam(value = "活动id", name = "activityId", required = true)
            @PathVariable("activityId") Integer activityId,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        CardProjectVO result;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            result = MockUtil.get(CardProjectVO.class);
            List<UnionCardProjectItem> nonErpTextList = MockUtil.list(UnionCardProjectItem.class, 10);
            result.setNonErpTextList(nonErpTextList);
            List<UnionCardProjectItem> erpTextList = MockUtil.list(UnionCardProjectItem.class, 10);
            result.setErpTextList(erpTextList);
            List<UnionCardProjectItem> erpGoodsList = MockUtil.list(UnionCardProjectItem.class, 10);
            result.setErpGoodsList(erpGoodsList);
        } else {
            result = unionCardProjectService.getProjectVOByBusIdAndUnionIdAndActivityId(busId, unionId, activityId);
        }
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "我的联盟-联盟卡设置-活动卡设置-分页数据-审核项目-通过或不通过", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/activityId/{activityId}/unionId/{unionId}/projectCheck", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateProjectCheckByActivityIdAndUnionId(
            HttpServletRequest request,
            @ApiParam(value = "联盟卡活动id", name = "activityId", required = true)
            @PathVariable("activityId") Integer activityId,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "是否通过(0:否 1:是)", name = "isPass", required = true)
            @RequestParam(value = "isPass") Integer isPass,
            @ApiParam(value = "表单内容", name = "updateVO", required = true)
            @RequestBody CardProjectCheckUpdateVO updateVO) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.BUS_PARENT_TIP);
        }
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            unionCardProjectService.updateByBusIdAndUnionIdAndActivityId(busId, unionId, activityId, isPass, updateVO);
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //-------------------------------------------------- post ----------------------------------------------------------

}