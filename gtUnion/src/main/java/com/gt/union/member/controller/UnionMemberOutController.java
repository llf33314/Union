package com.gt.union.member.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.member.vo.OutPeriodVO;
import com.gt.union.member.vo.OutVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 退盟申请 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 10:22:05
 */
@Api(description = "退盟申请")
@RestController
@RequestMapping("/unionMemberOut")
public class UnionMemberOutController {

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "根据联盟id，分页获取退盟信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageByUnionId(HttpServletRequest request,
                                Page page,
                                @ApiParam(value = "联盟id", name = "unionId", required = true)
                                @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<OutVO> voList = MockUtil.list(OutVO.class, page.getSize());
        page.setRecords(voList);
        return GtJsonResult.instanceSuccessMsg(page).toString();
    }

    @ApiOperation(value = "根据联盟id，分页获取退盟过渡期信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/period/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pagePeriodByUnionId(HttpServletRequest request,
                                      Page page,
                                      @ApiParam(value = "联盟id", name = "unionId", required = true)
                                      @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        OutPeriodVO result = MockUtil.get(OutPeriodVO.class);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "根据退盟类型和退盟盟员id，更新退盟信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/type/{type}/applyMemberId/{applyMemberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String removeByTypeAndApplyMemberId(HttpServletRequest request,
                                               @ApiParam(value = "退盟类型(1:自己申请 2:盟主移出)", name = "type", required = true)
                                               @PathVariable("type") Integer type,
                                               @ApiParam(value = "退盟盟员id", name = "applyMemberId", required = true)
                                               @PathVariable("applyMemberId") Integer applyMemberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    @ApiOperation(value = "根据退盟id和联盟id，审核退盟申请", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{outId}/unionId/{unionId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String checkByIdAndUnionId(HttpServletRequest request,
                                      @ApiParam(value = "退盟id", name = "outId", required = true)
                                      @PathVariable("outId") Integer outId,
                                      @ApiParam(value = "联盟id", name = "unionId", required = true)
                                      @PathVariable("unionId") Integer unionId,
                                      @ApiParam(value = "是否通过(0:否 1:是)", name = "isPass", required = true)
                                      @RequestParam(value = "isPass") Integer isPass) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "根据联盟id和表单信息，保存退盟申请", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String save(HttpServletRequest request,
                       @ApiParam(value = "联盟id", name = "unionId", required = true)
                       @PathVariable("unionId") Integer unionId,
                       @ApiParam(value = "退盟理由", name = "reason", required = true) String reason) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }
}