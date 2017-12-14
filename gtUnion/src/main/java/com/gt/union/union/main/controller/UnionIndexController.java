package com.gt.union.union.main.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.MockUtil;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionIndexService;
import com.gt.union.union.main.vo.IndexVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 首页 前端控制器
 *
 * @author linweicong
 * @version 2017-11-24 11:05:10
 */

@Api(description = "首页")
@RestController
@RequestMapping("")
public class UnionIndexController {

    @Autowired
    private IUnionIndexService unionIndexService;

    //-------------------------------------------------- get -----------------------------------------------------------

    @ApiOperation(value = "首页", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/index", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public GtJsonResult<IndexVO> getIndexVO(
            HttpServletRequest request,
            @ApiParam(value = "联盟id", name = "unionId")
            @RequestParam(value = "unionId", required = false) Integer unionId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            busId = busUser.getPid();
        }
        // mock
//        IndexVO result = MockUtil.get(IndexVO.class);
//        List<UnionMain> myJoinUnionList = MockUtil.list(UnionMain.class, 2);
//        result.setMyJoinUnionList(myJoinUnionList);

        IndexVO result = unionIndexService.getIndexVOByBusId(busId, unionId);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------
}
