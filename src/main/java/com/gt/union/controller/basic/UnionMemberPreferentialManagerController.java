package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.UnionMemberPreferentialServiceConstant;
import com.gt.union.common.exception.BaseException;
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
    private static final String LIST_UNIONID_VERIFYSTATUS = "UnionMemberPreferentialManagerController.listByUnionIdAndVerifyStatus()";
    private static final String LIST_UNIONID = "UnionMemberPreferentialManagerController.listMyByUnionId()";
    private static final String GET_ID_VERIFYSTATUS = "UnionMemberPreferentialManagerController.getByIdAndVerifyStatus()";
    private Logger logger = LoggerFactory.getLogger(UnionMemberPreferentialManagerController.class);

    @Autowired
    private IUnionMemberPreferentialManagerService unionMemberPreferentialManagerService;

    @ApiOperation(value = "优惠项目审核", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/verifyStatus/{verifyStatus}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listByUnionIdAndVerifyStatus(HttpServletRequest request, Page page
        , @ApiParam(name = "unionId", value = "联盟id", required = true)
          @RequestParam(name = "unionId", required = true) Integer unionId
        , @ApiParam(name = "verifyStatus", value = "审核状态，1代表未审核，2代表审核通过，3代表审核不通过")
          @RequestParam(name = "verifyStatus") Integer verifyStatus) {
        try {
            Page pageData = this.unionMemberPreferentialManagerService.listByUnionIdAndVerifyStatus(page, unionId, verifyStatus);
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
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_UNIONID_VERIFYSTATUS, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "我的优惠项目", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listMyByUnionId(HttpServletRequest request, Page page
            , @ApiParam(name = "unionId", value = "联盟id", required = true) @RequestParam(name = "unionId", required = true) Integer unionId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Page result = this.unionMemberPreferentialManagerService.listMyByUnionId(page, unionId, busUser.getId());
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_UNIONID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "优惠项目审核详情", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{id}/verifyStatus/{verifyStatus}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public String getByIdAndVerifyStatus(Page page
        , @ApiParam(name = "id", value = "优惠项目审核id", required = true)
        @PathVariable("id") Integer id
        , @ApiParam(name = "verifyStatus", value = "审核状态：1代表未审核，2代表审核通过，3代表审核不通过", required = true)
        @RequestParam(name = "verifyStatus") Integer verifyStatus) {
        try {
            Map<String, Object> result = this.unionMemberPreferentialManagerService.getByIdAndVerifyStatus(page, id, verifyStatus);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(GET_ID_VERIFYSTATUS, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

}
