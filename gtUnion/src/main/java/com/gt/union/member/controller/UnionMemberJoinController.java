package com.gt.union.member.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.member.vo.JoinCreateVO;
import com.gt.union.member.vo.JoinVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "根据联盟id，分页获取入盟申请", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageByUnionId(HttpServletRequest request,
                                Page page,
                                @ApiParam(value = "联盟id", name = "unionId", required = true)
                                @PathVariable("unionId") Integer unionId,
                                @ApiParam(value = "企业名称", name = "enterpriseName")
                                @RequestParam(value = "enterpriseName") String enterpriseName,
                                @ApiParam(value = "负责人电话", name = "directorPhone")
                                @RequestParam(value = "directorPhone") String directorPhone) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<JoinVO> voList = MockUtil.list(JoinVO.class, page.getSize());
        page.setRecords(voList);
        return GtJsonResult.instanceSuccessMsg(page).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "入盟审核", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{joinId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String passById(HttpServletRequest request,
                           @ApiParam(value = "入盟id", name = "joinId", required = true)
                           @PathVariable("joinId") Integer joinId,
                           @ApiParam(value = "是否通过", name = "isPass", required = true)
                           @RequestParam(value = "isPass") Integer isPass) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "根据联盟id和表单信息，保存入盟信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/type/{type}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String method(HttpServletRequest request,
                         @ApiParam(value = "联盟id", name = "unionId", required = true)
                         @PathVariable("unionId") Integer unionId,
                         @ApiParam(value = "入盟类型(1:申请 2:推荐)", name = "type", required = true)
                         @PathVariable("type") Integer type,
                         @ApiParam(value = "joinCreateVO", name = "表单信息", required = true)
                         @RequestBody JoinCreateVO joinCreateVO) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

}