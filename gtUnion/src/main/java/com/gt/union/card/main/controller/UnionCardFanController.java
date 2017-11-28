package com.gt.union.card.main.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.card.main.vo.CardFanDetailVO;
import com.gt.union.card.main.vo.CardFanSearchVO;
import com.gt.union.card.main.vo.CardFanVO;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 联盟卡粉丝信息 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 17:39:13
 */
@Api(description = "联盟卡粉丝信息")
@RestController
@RequestMapping("/unionCardFan")
public class UnionCardFanController {

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "导出联盟卡列表信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/export", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String exportFanVOByUnionId(HttpServletRequest request,
                                       @ApiParam(value = "联盟id", name = "unionId", required = true)
                                       @RequestParam(value = "unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg("TODO").toString();
    }

    @ApiOperation(value = "分页获取联盟卡列表信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageFanVOByUnionId(HttpServletRequest request,
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
        List<CardFanVO> rootList = MockUtil.list(CardFanVO.class, page.getSize());
        page.setRecords(rootList);
        return GtJsonResult.instanceSuccessMsg(page).toString();
    }

    @ApiOperation(value = "获取联盟卡详情", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{fanId}/detail", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getFanDetailVOByFanIdAndUnionId(HttpServletRequest request,
                                                  @ApiParam(value = "联盟卡粉丝信息id", name = "fanId", required = true)
                                                  @PathVariable("fanId") Integer fanId,
                                                  @ApiParam(value = "联盟id", name = "unionId", required = true)
                                                  @RequestParam(value = "unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        CardFanDetailVO result = MockUtil.get(CardFanDetailVO.class);
        List<UnionCard> activityCardList = MockUtil.list(UnionCard.class, 20);
        result.setActivityCardList(activityCardList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "根据卡号搜索", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/number/{number}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String searchByNumber(HttpServletRequest request,
                                 @ApiParam(value = "卡号", name = "number", required = true)
                                 @PathVariable("number") String number,
                                 @ApiParam(value = "联盟id", name = "unionId")
                                 @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        CardFanSearchVO result = MockUtil.get(CardFanSearchVO.class);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

}