package com.gt.union.opportunity.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.log.service.IUnionLogErrorService;
import com.gt.union.opportunity.service.IUnionOpportunityBrokerageRatioService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 商机佣金比率 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionOpportunityBrokerageRatio")
public class UnionOpportunityBrokerageRatioController {

    private Logger logger = Logger.getLogger(UnionOpportunityBrokerageRatioController.class);

    @Autowired
    private IUnionOpportunityBrokerageRatioService unionBrokerageRatioService;

    @Autowired
    private IUnionLogErrorService unionLogErrorService;

    //-------------------------------------------------- get ----------------------------------------------------------

    @ApiOperation(value = "根据盟员身份id，分页获取与盟友之间的商机佣金比例设置列表信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pageMap/memberId/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageBrokerageRatio(HttpServletRequest request, Page page
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                     @PathVariable("memberId") Integer memberId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer busId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                busId = busUser.getPid();
            }
            Page result = this.unionBrokerageRatioService.pageMapByBusIdAndMemberId(page, busId, memberId);
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

    @ApiOperation(value = "根据盟员身份id，以及受惠方的盟员身份id，设置商机佣金比例", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/memberId/{memberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String saveOrUpdateBrokerageRatio(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                             @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "toMemberId", value = "受惠方的盟员身份id", required = true)
                                             @RequestParam(name = "toMemberId") Integer toMemberId
            , @ApiParam(name = "ratio", value = "商机佣金比例", required = true)
                                             @RequestParam(name = "ratio") @NotNull Double ratio) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser.getPid() != null && busUser.getPid() != 0) {
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionBrokerageRatioService.updateOrSaveByBusIdAndFromMemberIdAndToMemberIdAndRatio(busUser.getId(), memberId, toMemberId, ratio);
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
