package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
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
    private static final String LIST_MAP_UNIONID_PAGE = "UnionMemberController.listMapByUnionIdInPage()";
    private static final String LIST_MAP_UNIONID_LIST = "UnionMemberController.listMapByUnionIdInList()";
    private static final String LIST_UNIONID_OUTSTATUS = "UnionMemberController.listByUnionIdAndOutStatus()";
    private static final String LIST_UNIONID_OUTSTATUS_ISNUIONOWNER = "UnionMemberController.listByUnionIdAndOutStatusAndIsNuionOwner()";
    private static final String GET_ID = "UnionMemberController.getById()";
    private static final String UPDATE_ISNUIONOWNER_ID = "UnionMemberController.updateIsNuionOwnerById()";
    private static final String APPLY_OUT_UNION = "UnionMemberController.applyOutUnion()";
    private Logger logger = LoggerFactory.getLogger(UnionMemberController.class);

    @Autowired
    private IUnionMemberService unionMemberService;

    @ApiOperation(value = "根据联盟id获取盟员列表信息，并支持根据盟员名称enterpriseName进行模糊查询", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listMapByUnionIdInPage(Page page
        , @ApiParam(name = "unionId", value = "联盟id", required = true)
        @PathVariable("unionId") Integer unionId
        , @ApiParam(name = "enterpriseName", value = "企业名称，模糊匹配", required = false)
        @RequestParam(name = "enterpriseName", required = false) String enterpriseName) {
        try {
            Page result = this.unionMemberService.listMapByUnionIdInPage(page, unionId, enterpriseName);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_MAP_UNIONID_PAGE, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "根据联盟id获取盟员列表信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listMapByUnionIdInList(@ApiParam(name = "unionId", value = "盟员id", required = true) @PathVariable("unionId") Integer unionId) {
        try {
            List<Map<String, Object>> result = this.unionMemberService.listMapByUnionIdInList(unionId);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_MAP_UNIONID_LIST, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "根据联盟id和退盟状态outStatus获取退盟申请列表信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/outStatus/{outStatus}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listByUnionIdAndOutStatus(Page page
        , @ApiParam(name = "unionId", value = "联盟id", required = true)
        @PathVariable("unionId") Integer unionId
        , @ApiParam(name = "outStatus", value = "退盟状态，0代表正常，1代表未处理，2代表过渡期", required = true)
        @PathVariable("outStatus") Integer outStatus) {
        try {
            Page result = this.unionMemberService.listByUnionIdAndOutStatus(page, unionId, outStatus);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_UNIONID_OUTSTATUS, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "根据联盟id、退盟状态outStatus和是否盟主isNuionOwner获取盟员列表信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/outStatus/{outStatus}/isNuionOwner/{isNuionOwner}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listByUnionIdAndOutStatusAndIsNuionOwner(HttpServletRequest request, Page page
            , @ApiParam(name = "unionId", value = "联盟id", required = true)
            @PathVariable("unionId") Integer unionId
            , @ApiParam(name = "outStatus", value = "退盟状态，0代表正常，1代表未处理，2代表过渡期", required = true)
            @PathVariable("outStatus") Integer outStatus
            , @ApiParam(name = "isNuionOwner", value = "是否盟主，0代表不是，1代表是", required = true)
            @PathVariable("isNuionOwner") Integer isNuionOwner) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Page result = this.unionMemberService.listByUnionIdAndOutStatusAndIsNuionOwner(page, unionId, busUser.getId(), outStatus, isNuionOwner);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_UNIONID_OUTSTATUS_ISNUIONOWNER, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "根据盟员id获取盟员信息详情", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getById(@ApiParam(name = "id", value = "盟员id", required = true) @PathVariable("id") Integer id) {
        try {
            Map<String, Object> result = this.unionMemberService.getMapById(id);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(GET_ID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "接受盟主权限转移", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/isNuionOwner/accept", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateIsNuionOwnerById(HttpServletRequest request
        , @ApiParam(name = "unionId", value = "联盟id", required = true) @RequestParam(value = "unionId", required = true) Integer unionId){
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (CommonUtil.isNotEmpty(busUser.getPid()) && busUser.getPid() != 0) {
                throw new BusinessException(UPDATE_ISNUIONOWNER_ID, "", CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionMemberService.acceptUnionOwner(busUser.getId(), unionId);
            return GTJsonResult.instanceSuccessMsg().toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(UPDATE_ISNUIONOWNER_ID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "转移盟主权限", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{id}/isNuionOwner/transfer", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String transferUnionOwner(HttpServletRequest request
        , @ApiParam(name = "id", value = "盟员id", required = true) @PathVariable("id") Integer id
        , @ApiParam(name = "unionId", value = "联盟id", required = true) @RequestParam(value = "unionId", required = true) Integer unionId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (CommonUtil.isNotEmpty(busUser.getPid()) && busUser.getPid() != 0) {
                throw new BusinessException(UPDATE_ISNUIONOWNER_ID, "", CommonConstant.UNION_BUS_PARENT_MSG);
            }
            //TODO  盟主权限转移
            return GTJsonResult.instanceSuccessMsg().toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(UPDATE_ISNUIONOWNER_ID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "盟主撤销权限转移", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{id}/isNuionOwner/cancel", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String cancelUnionOwner(HttpServletRequest request
            , @ApiParam(name = "id", value = "盟员id", required = true) @PathVariable("id") Integer id
            , @ApiParam(name = "unionId", value = "联盟id", required = true) @RequestParam(value = "unionId", required = true) Integer unionId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (CommonUtil.isNotEmpty(busUser.getPid()) && busUser.getPid() != 0) {
                throw new BusinessException(UPDATE_ISNUIONOWNER_ID, "", CommonConstant.UNION_BUS_PARENT_MSG);
            }
            //TODO  盟主撤销权限转移
            return GTJsonResult.instanceSuccessMsg().toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(UPDATE_ISNUIONOWNER_ID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "盟员申请退盟", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/outUnion", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String applyOutUnion(HttpServletRequest request
            , @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable(value = "unionId", required = true) Integer unionId
            , @ApiParam(name = "outReason", value = "退盟理由", required = true) @RequestParam(name = "outReason", required = true) String outReason){
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (CommonUtil.isNotEmpty(busUser.getPid()) && busUser.getPid() != 0) {
                throw new BusinessException(APPLY_OUT_UNION, "", CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionMemberService.applyOutUnion(unionId, busUser.getId(), outReason);
            return GTJsonResult.instanceSuccessMsg().toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(APPLY_OUT_UNION, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "盟主审核退盟成员", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{id}/outStatus/outStatus", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateOutStatusById(HttpServletRequest request
            , @ApiParam(name = "id", value = "盟员id", required = true) @PathVariable("id") Integer id
            , @ApiParam(name = "unionId", value = "联盟id", required = true) @RequestParam(value = "unionId", required = true) Integer unionId
            , @ApiParam(name = "verifyStatus", value = "1为同意，2为拒绝", required = true) @RequestParam(name = "verifyStatus", required = true) Integer verifyStatus){
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (CommonUtil.isNotEmpty(busUser.getPid()) && busUser.getPid() != 0) {
                throw new BusinessException(UPDATE_ISNUIONOWNER_ID, "", CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionMemberService.updateOutStatusById(id, unionId, busUser.getId(), verifyStatus);
            return GTJsonResult.instanceSuccessMsg().toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(UPDATE_ISNUIONOWNER_ID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

}
