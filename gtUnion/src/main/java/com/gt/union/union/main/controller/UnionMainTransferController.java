package com.gt.union.union.main.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.union.main.vo.UnionTransferVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页获取盟主权限转移信息", produces = "application/json;charset=UTF-8")
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
        List<UnionTransferVO> voList = MockUtil.list(UnionTransferVO.class, page.getSize());
        page.setRecords(voList);
        return GtJsonResult.instanceSuccessMsg(page).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "撤回盟主权限转移", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{transferId}/unionId/{unionId}/revoke", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String revokeById(HttpServletRequest request,
                             @ApiParam(value = "转移id", name = "transferId", required = true)
                             @PathVariable("transferId") Integer transferId,
                             @ApiParam(value = "联盟id", name = "unionId", required = true)
                             @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "转移盟主权限", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/toMemberId/{toMemberId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String saveTransfer(HttpServletRequest request,
                               @ApiParam(value = "联盟id", name = "unionId", required = true)
                               @PathVariable("unionId") Integer unionId,
                               @ApiParam(value = "目标盟员id", name = "toMemberId", required = true)
                               @PathVariable("toMemberId") Integer toMemberId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

}