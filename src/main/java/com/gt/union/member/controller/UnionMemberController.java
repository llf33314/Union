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
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.member.vo.CardDividePercentVO;
import com.gt.union.member.vo.UnionMemberVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟成员 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionMember")
public class UnionMemberController {
    private Logger logger = LoggerFactory.getLogger(UnionMemberController.class);

    @Autowired
    private IUnionLogErrorService unionLogErrorService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionValidateService unionValidateService;

    //-------------------------------------------------- get ----------------------------------------------------------

    @ApiOperation(value = "根据我的盟员身份id分页获取所有与我同属一个联盟的盟员相关信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pageMap/memberId/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageMapByMemberId(HttpServletRequest request, Page page
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                    @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "enterpriseName", value = "盟员名称，模糊匹配")
                                    @RequestParam(value = "enterpriseName", required = false) String enterpriseName) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer busId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                busId = busUser.getPid();
            }
            Page result = this.unionMemberService.pageMapByIdAndBusId(page, memberId, busId, enterpriseName);
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

    @ApiOperation(value = "根据我的盟员身份id获取所有与我同属一个联盟的盟员相关信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/listMap/memberId/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listMapByMemberId(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                    @PathVariable("memberId") Integer memberId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer busId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                busId = busUser.getPid();
            }
            List<Map<String, Object>> result = this.unionMemberService.listMapByIdAndBusId(memberId, busId);
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

    @ApiOperation(value = "根据我的盟员身份id获取对应的盟员信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getById(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "盟员身份id", required = true)
                          @PathVariable("memberId") Integer memberId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer busId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                busId = busUser.getPid();
            }
            UnionMember result = this.unionMemberService.getByIdAndBusId(memberId, busId);
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

    @ApiOperation(value = "获取我的所有盟员身份，以及盟员身份所在的联盟信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/listMap", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listReadMap(HttpServletRequest request) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer busId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                busId = busUser.getPid();
            }
            List<Map<String, Object>> result = this.unionMemberService.listReadMapByBusId(busId);
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

    //-------------------------------------------------- put ----------------------------------------------------------

    @ApiOperation(value = "更新盟员信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{memberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateById(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "盟员身份id", required = true)
                             @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "unionMemberVO", value = "更新内容实体", required = true)
                             @RequestBody @Valid UnionMemberVO unionMemberVO, BindingResult bindingResult) {
        try {
            this.unionValidateService.checkBindingResult(bindingResult);
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionMemberService.updateByIdAndBusId(memberId, busUser.getId(), unionMemberVO);
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

    @ApiOperation(value = "批量更新售卡分成比例", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/cardDividePercent/memberId/{memberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateCardDividePercentById(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                              @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "cardDividePercentVO", value = "更新内容实体", required = true)
                                              @RequestBody @Valid List<CardDividePercentVO> cardDividePercentVOList, BindingResult bindingResult) {
        try {
            this.unionValidateService.checkBindingResult(bindingResult);
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionMemberService.updateCardDividePercentByIdAndBusId(memberId, busUser.getId(), cardDividePercentVOList);
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

    //------------------------------------------------- post ----------------------------------------------------------
}
