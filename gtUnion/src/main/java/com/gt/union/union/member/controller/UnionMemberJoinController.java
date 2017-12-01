package com.gt.union.union.member.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.PageUtil;
import com.gt.union.union.member.vo.MemberJoinCreateVO;
import com.gt.union.union.member.vo.MemberJoinVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页：获取入盟申请信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<Page<MemberJoinVO>> pageJoinVOByUnionId(
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
        List<MemberJoinVO> voList = new ArrayList<>();
        Page<MemberJoinVO> result = (Page<MemberJoinVO>) page;
        result = PageUtil.setRecord(result, voList);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "通过或不通过入盟申请", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{joinId}/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> updateIsPassByIdAndUnionId(
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
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg();
    }

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "申请入盟或推荐入盟", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/type/{type}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public GtJsonResult<String> saveJoinCreateVOByUnionIdAndType(
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
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg();
    }

}