package com.gt.union.union.main.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.entity.UnionMainDict;
import com.gt.union.union.main.vo.UnionBasicVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "获取联盟基础信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{unionId}/basic", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getBasicVOById(HttpServletRequest request,
                          @ApiParam(value = "联盟id", name = "unionId", required = true)
                          @PathVariable("unionId") Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        UnionBasicVO result = MockUtil.get(UnionBasicVO.class);
        List<UnionMainDict> itemList = MockUtil.list(UnionMainDict.class, 3);
        result.setItemList(itemList);
        return GtJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "分页获取其他联盟", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/other/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageOther(HttpServletRequest request,
                            Page page) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
        List<UnionMain> unionMainList = MockUtil.list(UnionMain.class, page.getSize());
        page.setRecords(unionMainList);
        return GtJsonResult.instanceSuccessMsg(page).toString();
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    @ApiOperation(value = "更新联盟基础信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{unionId}/basic", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateBasicVOById(HttpServletRequest request,
                             @ApiParam(value = "联盟id", name = "unionId", required = true)
                             @PathVariable("unionId") Integer unionId,
                             @ApiParam(value = "表单信息", name = "basicVO", required = true)
                             @RequestBody UnionBasicVO basicVO) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        return GtJsonResult.instanceSuccessMsg().toString();
    }

    //-------------------------------------------------- post ----------------------------------------------------------

}