package com.gt.union.union.member.controller;

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
import com.gt.union.union.member.service.IUnionMemberJoinService;
import com.gt.union.union.member.vo.MemberJoinCreateVO;
import com.gt.union.union.member.vo.MemberJoinVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 入盟申请 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 10:22:05
 */
@Api(description = "入盟申请")
@RestController
@RequestMapping("/unionMemberJoin")
public class UnionMemberJoinController {

    @Autowired
    private IUnionMemberJoinService unionMemberJoinService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "入盟审核-分页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageJoinVOByUnionId(
            HttpServletRequest request,
            Page page,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "盟员名称", name = "memberName")
            @RequestParam(value = "memberName", required = false) String memberName,
            @ApiParam(value = "联系电话", name = "phone")
            @RequestParam(value = "phone", required = false) String phone) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<MemberJoinVO> voList;
        if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
            voList = MockUtil.list(MemberJoinVO.class, page.getSize());
        } else {
            voList = unionMemberJoinService.listMemberJoinVOByBusIdAndUnionId(busId, unionId, memberName, phone);
        }
        Page<MemberJoinVO> result = (Page<MemberJoinVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "入盟审核-分页-通过或不通过", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{joinId}/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String updateStatusByIdAndUnionId(
            HttpServletRequest request,
            @ApiParam(value = "入盟申请id", name = "joinId", required = true)
            @PathVariable("joinId") Integer joinId,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "是否通过", name = "isPass", required = true)
            @RequestParam(value = "isPass") Integer isPass) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            unionMemberJoinService.updateStatusByBusIdAndIdAndUnionId(busId, joinId, unionId, isPass);
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "加入联盟-保存；推荐入盟", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/type/{type}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String saveJoinCreateVOByUnionIdAndType(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId", required = true)
            @PathVariable("unionId") Integer unionId,
            @ApiParam(value = "入盟类型(1:申请 2:推荐)", name = "type", required = true)
            @PathVariable("type") Integer type,
            @ApiParam(value = "joinCreateVO", name = "表单信息", required = true)
            @RequestBody MemberJoinCreateVO joinCreateVO) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        if (CommonConstant.COMMON_YES != ConfigConstant.IS_MOCK) {
            unionMemberJoinService.saveJoinCreateVOByBusIdAndUnionIdAndType(busId, unionId, type, joinCreateVO);
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

}