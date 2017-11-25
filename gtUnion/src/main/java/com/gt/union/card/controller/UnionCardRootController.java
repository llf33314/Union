package com.gt.union.card.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.entity.UnionCardRoot;
import com.gt.union.card.vo.ActivityCard;
import com.gt.union.card.vo.RootDetailVO;
import com.gt.union.card.vo.RootVO;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.main.entity.UnionMain;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 联盟卡根信息 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 17:39:13
 */
@Api(description = "联盟卡根信息")
@RestController
@RequestMapping("/unionCardRoot")
public class UnionCardRootController {

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "首页导出联盟卡列表信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/export", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String exportCardListInIndex(HttpServletRequest request,
                                        @ApiParam(value = "联盟id", name = "unionId", required = true)
                                        @RequestParam(value = "unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg("TODO").toString();
    }

    @ApiOperation(value = "首页分页获取联盟卡列表信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageCardListInIndex(HttpServletRequest request,
                                Page page,
                                @ApiParam(value = "联盟id", name = "unionId", required = true)
                                @RequestParam(value = "unionId") Integer unionId,
                                @ApiParam(value = "联盟卡号", name = "number")
                                @RequestParam(value = "number", required = false) String number,
                                @ApiParam(value = "手机号", name = "phone")
                                @RequestParam(value = "phone", required = false) String phone) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<UnionCardRoot> rootList = MockUtil.list(UnionCardRoot.class, page.getSize());
        page.setRecords(rootList);
        return GtJsonResult.instanceSuccessMsg(page).toString();
    }

    @ApiOperation(value = "获取联盟卡详情", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{rootId}/detail", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getCardDetailInIndex(HttpServletRequest request,
                                          @ApiParam(value = "根id", name = "rootId", required = true)
                                          @PathVariable("rootId") Integer rootId,
                                          @ApiParam(value = "联盟id", name = "unionId", required = true)
                                          @RequestParam(value = "unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        RootDetailVO result = MockUtil.get(RootDetailVO.class);
        List<ActivityCard> activityCardList = MockUtil.list(ActivityCard.class, 5);
        result.setActivityCardList(activityCardList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "根据卡号搜索", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/number/{number}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String searchByNumber(HttpServletRequest request,
                                 @ApiParam(value = "卡号", name = "number", required = true)
                                 @PathVariable("number") String number) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        RootVO result = MockUtil.get(RootVO.class);
        List<UnionMain> unionList = MockUtil.list(UnionMain.class, 20);
        result.setUnionList(unionList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

}