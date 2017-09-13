package com.gt.union.member.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.service.IUnionValidateService;
import com.gt.union.log.service.IUnionLogErrorService;
import com.gt.union.member.service.IUnionMemberJoinService;
import com.gt.union.member.vo.UnionMemberJoinVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>
 * 联盟成员入盟申请 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionMemberJoin")
public class UnionMemberJoinController {
    private Logger logger = LoggerFactory.getLogger(UnionMemberJoinController.class);

    @Autowired
    private IUnionLogErrorService unionLogErrorService;

    @Autowired
    private IUnionMemberJoinService unionMemberJoinService;

    @Autowired
    private IUnionValidateService unionValidateService;

    @ApiOperation(value = "分页查询入盟申请列表", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/memberId/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageMapByBusIdAndMemberId(HttpServletRequest request, Page page
            , @ApiParam(name = "memberId", value = "当前操作人的盟员身份id", required = true)
                                            @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "enterpriseName", value = "企业名称,模糊匹配")
                                            @RequestParam(name = "enterpriseName", required = false) String enterpriseName
            , @ApiParam(name = "directorPhone", value = "负责人电话,模糊匹配")
                                            @RequestParam(name = "directorPhone", required = false) String directorPhone) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer busId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                busId = busUser.getPid();
            }
            Page result = this.unionMemberJoinService.pageMapByBusIdAndMemberId(page, busId, memberId, enterpriseName, directorPhone);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
        }
    }

    @ApiOperation(value = "入盟审核", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "{id}/memberId/{memberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String method(HttpServletRequest request
            , @ApiParam(name = "id", value = "joinId，申请加入信息id", required = true)
                         @PathVariable("id") Integer id
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                         @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "isOK", value = "是否同意入盟，1为是，0为否", required = true)
                         @RequestParam(value = "isOK") Integer isOK) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            ;
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionMemberJoinService.updateJoinStatus(busUser.getId(), memberId, id, isOK);
            return GTJsonResult.instanceSuccessMsg().toString();
        } catch (BaseException e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
        }
    }

    @ApiOperation(value = "自由申请加入联盟", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String method(HttpServletRequest request
            , @ApiParam(name = "unionId", value = "联盟id", required = true)
                         @PathVariable("unionId") Integer unionId
            , @ApiParam(name = "unionMemberJoinVO", value = "申请入盟信息实体", required = true)
                         @RequestBody @Valid UnionMemberJoinVO unionMemberJoinVO, BindingResult bindingResult) {
        try {
            this.unionValidateService.checkBindingResult(bindingResult);
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionMemberJoinService.saveTypeJoin(busUser.getId(), unionId, unionMemberJoinVO);
            return GTJsonResult.instanceSuccessMsg().toString();
        } catch (BaseException e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
        }
    }

    @ApiOperation(value = "推荐申请加入联盟", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/memberId/{memberId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String method2(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                          @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "unionMemberJoinVO", value = "申请入盟信息实体", required = true)
                          @RequestBody @Valid UnionMemberJoinVO unionMemberJoinVO, BindingResult bindingResult) {
        try {
            this.unionValidateService.checkBindingResult(bindingResult);
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionMemberJoinService.saveTypeRecommend(busUser.getId(), memberId, unionMemberJoinVO);
            return GTJsonResult.instanceSuccessMsg().toString();
        } catch (BaseException e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
        }
    }
}
