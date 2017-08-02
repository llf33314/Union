package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.basic.UnionMemberPreferentialManagerConstant;
import com.gt.union.common.constant.basic.UnionMemberPreferentialServiceConstant;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionMemberPreferentialManagerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 盟员优惠项目管理 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@RestController
@RequestMapping("/unionMemberPreferentialManager")
public class UnionMemberPreferentialManagerController {
    private Logger logger = LoggerFactory.getLogger(UnionMemberPreferentialManagerController.class);

    @Autowired
    private IUnionMemberPreferentialManagerService unionMemberPreferentialManagerService;

    /**
     * 优惠项目信息查询
     * @param request
     * @param page
     * @param unionId
     * @param listType
     * @param verifyStatus
     * @return
     */
    @ApiOperation(value = "优惠项目信息查询"
            , notes = "优惠项目信息查询，根据listType进行分类查询"
            , produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listPreferentialManager(HttpServletRequest request, Page page
            , @ApiParam(name = "unionId", value = "联盟id", required = true)
              @RequestParam(name = "unionId", required = true) Integer unionId
            , @ApiParam(name = "listType", value = "查询类型：1代表优惠项目审核，2代表我的优惠项目", required = true)
              @RequestParam(name = "listType", required = true) Integer listType
            , @ApiParam(name = "verifyStatus", value = "审核状态，在listType=1时必填,1代表未审核，2代表审核通过，3代表审核不通过")
              @RequestParam(name = "verifyStatus") Integer verifyStatus) {
        try {
            switch (listType) {
                case UnionMemberPreferentialManagerConstant.LIST_TYPE_CHECK:
                    Page pageData = this.unionMemberPreferentialManagerService.listPreferentialManager(page, unionId, verifyStatus);
                    int unCommitCount = this.unionMemberPreferentialManagerService.countPreferentialManager(unionId, UnionMemberPreferentialServiceConstant.VERIFY_STATUS_UNCOMMIT);
                    int unCheckCount = this.unionMemberPreferentialManagerService.countPreferentialManager(unionId, UnionMemberPreferentialServiceConstant.VERIFY_STATUS_UNCHECK);
                    int passCount = this.unionMemberPreferentialManagerService.countPreferentialManager(unionId, UnionMemberPreferentialServiceConstant.VERIFY_STATUS_PASS);
                    int failCount = this.unionMemberPreferentialManagerService.countPreferentialManager(unionId, UnionMemberPreferentialServiceConstant.VERIFY_STATUS_FAIL);
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("page", pageData);
                    resultMap.put("unCommitCount", unCommitCount);
                    resultMap.put("unCheckCount", unCheckCount);
                    resultMap.put("passCount", passCount);
                    resultMap.put("failCount", failCount);
                    return GTJsonResult.instanceSuccessMsg(resultMap).toString();
                case UnionMemberPreferentialManagerConstant.LIST_TYPE_MY:
                    BusUser busUser = SessionUtils.getLoginUser(request);
                    if (busUser == null) {
                        throw new Exception("UnionMemberPreferentialManagerController.listPreferentialManager():无法通过session获取用户的信息!");
                    }
                    Page result = this.unionMemberPreferentialManagerService.listMyPreferentialManager(page, unionId, busUser.getId());
                    return GTJsonResult.instanceSuccessMsg(result).toString();
                default:
                    throw new Exception("UnionMemberPreferentialManagerController.listPreferentialManager():不支持的查询类型listType(value=" + listType + ")!");
            }
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }
    }

    /**
     * 优惠项目审核详情
     * @param page
     * @param id
     * @param verifyStatus
     * @return
     */
    @ApiOperation(value = "优惠项目审核详情"
            , notes = "根据id和审核状态verifyStatus查询优惠项目审核详情"
            , produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public String detailPreferentialManager(Page page
            , @ApiParam(name = "id", value = "优惠项目审核id", required = true)
                @PathVariable("id") Integer id
            , @ApiParam(name = "verifyStatus", value = "审核状态：1代表未审核，2代表审核通过，3代表审核不通过", required = true)
                @RequestParam(name = "verifyStatus") Integer verifyStatus) {
        Map<String, Object> result = null;
        try {
            result = this.unionMemberPreferentialManagerService.detailPreferentialManager(page, id, verifyStatus);
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

}
