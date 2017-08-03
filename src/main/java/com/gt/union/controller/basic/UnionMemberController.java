package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.basic.UnionMemberConstant;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionMemberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟成员列表 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@RestController
@RequestMapping("/unionMember")
public class UnionMemberController {
    private Logger logger = LoggerFactory.getLogger(UnionMemberController.class);

    @Autowired
    private IUnionMemberService unionMemberService;

    /**
     * 根据listType进行请求分类：
     * (1)当listType为请求盟员列表时，即值为UnionMemberConstant.LIST_TYPE_MEMBER，根据联盟id和session中的bus_id获取盟员列表信息，并支持根据盟员名称enterpriseName进行模糊查询；
     * (2)当listType为请求退盟申请列表时，即值为UnionMemberConstant.LIST_TYPE_OUT，根据联盟id获取退盟申请列表信息，并根据outStatus进行退盟状态分类查询;
     * (3)当listType为请求盟主权限转移盟员列表时，即值为UnionMemberConstant.LIST_TYPE_TRANSFER，根据联盟id获取非盟主盟员列表信息
     * @param page
     * @param unionId
     * @param listType
     * @param enterpriseName
     * @param outStatus 当listType=UnionMemberConstant.LIST_TYPE_OUT时，不能为空
     * @return
     */
    @ApiOperation(value = "查询盟员信息"
            , notes = "根据listType进行请求分类"
            , produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listUnionMember(HttpServletRequest request, Page page
            , @ApiParam(name = "unionId", value = "联盟id", required = true)
              @RequestParam(name = "unionId", required = true)Integer unionId
            , @ApiParam(name = "listType"
                , value = "查询类型:"
                    + "（1）当值为1时，根据联盟id和session中的bus_id获取盟员列表信息，并支持根据盟员名称enterpriseName进行模糊查询；"
                    + "（2）当值为2时，根据联盟id获取退盟申请列表信息，并根据outStatus进行退盟状态分类查询;"
                    + "（3）当值为3时，根据联盟id获取非盟主盟员列表信息."
                , required = true)
              @RequestParam(name = "listType", required = true)Integer listType
            , @ApiParam(name = "enterpriseName", value = "企业名称", required = false)
              @RequestParam(name = "enterpriseName", required = false) String enterpriseName
            , @ApiParam(name = "outStatus", value = "商家退出状态，当listType=2时必填，0代表正常，1代表未处理，2代表过渡期")
              @RequestParam(name = "outStatus", required = false) Integer outStatus) {
        Page result = null;
        try {
            switch (listType) {
                case UnionMemberConstant.LIST_TYPE_MEMBER:
                    result = this.unionMemberService.listMember(page, unionId, enterpriseName);
                    break;
                case UnionMemberConstant.LIST_TYPE_OUT:
                    result = this.unionMemberService.listOut(page, unionId, outStatus);
                    break;
                case UnionMemberConstant.LIST_TYPE_TRANSFER:
                    BusUser busUser = SessionUtils.getLoginUser(request);
                    if (busUser == null) {
                        throw new Exception("UnionMemberController.listUnionMember():无法通过session获取用户的信息!");
                    }
                    result = this.unionMemberService.listTransfer(page, unionId, busUser.getId());
                    break;
                default:
                    throw new Exception("UnionMemberController.listUnionMember():不支持的查询类型listType-" + listType);
            }

        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    /**
     * 根据盟员id获取盟员信息详情
     * @param unionMemberId
     * @return
     */
    @ApiOperation(value = "盟员信息详情"
            , notes = "根据盟员id获取盟员信息详情"
            , produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getUnionMember(@ApiParam(name = "id", value = "盟员id", required = true)@PathVariable("id") Integer unionMemberId) {
        Map<String, Object> result = null;
        try {
            result = this.unionMemberService.getDetail(unionMemberId);
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    /**
     * 获取盟员列表 不分页
     * @param unionId
     * @return
     */
    @ApiOperation(value = "获取盟员列表 不分页" , notes = "获取联盟内所有的盟员 不分页" , produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/memberList", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getUnionMemberList(@ApiParam(name = "unionId", value = "盟员id", required = true) @RequestParam("unionId") Integer unionId) {
        List<Map<String, Object>> result = null;
        try {
            result = this.unionMemberService.getUnionMemberList(unionId);
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }


    /**
     * 盟主权限转移
     * @param request
     * @param unionMemberId
     * @param unionId
     * @param isUnionOwner
     * @return
     */
    @ApiOperation(value = "盟主权限转移"
            , notes = "盟主权限转移"
            , produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateUnionMember(HttpServletRequest request
            , @ApiParam(name = "id", value = "被授予盟主权限的盟员id", required = true)
                @PathVariable("id") Integer unionMemberId
            , @ApiParam(name = "unionId", value = "联盟id", required = true)
                @RequestParam(name = "unionId", required = true) Integer unionId
            , @ApiParam(name = "isUnionOwner", value = "目标值：1代表成为盟主", required = true)
                @RequestParam(name = "isUnionOwner", required = true) Integer isUnionOwner) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser == null) {
                throw new Exception("UnionMemberController.listUnionMember():无法通过session获取用户的信息!");
            }
            this.unionMemberService.updateIsUnionMember(unionMemberId, unionId, busUser.getId(), isUnionOwner);
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }
        return GTJsonResult.instanceSuccessMsg().toString();
    }
}
