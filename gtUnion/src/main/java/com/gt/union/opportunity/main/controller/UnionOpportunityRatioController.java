package com.gt.union.opportunity.main.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.opportunity.main.vo.OpportunityRatioVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 商机佣金比率 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 16:56:20
 */
@Api(description = "商机佣金比率")
@RestController
@RequestMapping("/unionOpportunityRatio")
public class UnionOpportunityRatioController {

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "分页获取商机佣金比例信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageRatioVOByUnionId(HttpServletRequest request,
                                       Page page,
                                       @ApiParam(value = "联盟id", name = "unionId", required = true)
                                       @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<OpportunityRatioVO> voList = MockUtil.list(OpportunityRatioVO.class, page.getSize());
        page.setRecords(voList);
        return GtJsonResult.instanceSuccessMsg(page).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "设置佣金比例", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/toMemberId/{toMemberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateRatioByUnionIdAndToMemberId(HttpServletRequest request,
                                                    @ApiParam(value = "联盟id", name = "unionId", required = true)
                                                    @PathVariable("unionId") Integer unionId,
                                                    @ApiParam(value = "目标盟员id", name = "toMemberId", required = true)
                                                    @PathVariable("toMemberId") Integer toMemberId,
                                                    @ApiParam(value = "佣金比例", name = "ratio", required = true)
                                                    @RequestParam(value = "ratio") Double ratio) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //-------------------------------------------------- post ----------------------------------------------------------

}