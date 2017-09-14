package com.gt.union.preferential.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.log.service.IUnionLogErrorService;
import com.gt.union.preferential.service.IUnionPreferentialItemService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 优惠服务项 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionPreferentialItem")
public class UnionPreferentialItemController {
    private Logger logger = LoggerFactory.getLogger(UnionPreferentialItemController.class);

    @Autowired
    private IUnionLogErrorService unionLogErrorService;

    @Autowired
    private IUnionPreferentialItemService unionPreferentialItemService;

    //-------------------------------------------------- get ----------------------------------------------------------
    //-------------------------------------------------- put ----------------------------------------------------------

    @ApiOperation(value = "提交优惠服务项", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{itemId}/status/2/memberId/{memberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String submitByIdAndMemberId(HttpServletRequest request
            , @ApiParam(name = "itemId", value = "优惠服务项id", required = true)
                                        @PathVariable("itemId") Integer itemId
            , @ApiParam(name = "memberId", value = "操作者的盟员身份id", required = true)
                                        @PathVariable("memberId") Integer memberId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionPreferentialItemService.submitByIdAndBusIdAndMemberId(itemId, busUser.getId(), memberId);
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

    @ApiOperation(value = "移除优惠服务项", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{itemId}/delStatus/1/memberId/{memberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String removeByIdAndMemberId(HttpServletRequest request
            , @ApiParam(name = "itemId", value = "优惠服务项id", required = true)
                                        @PathVariable("itemId") Integer itemId
            , @ApiParam(name = "memberId", value = "操作者的盟员身份id", required = true)
                                        @PathVariable("memberId") Integer memberId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionPreferentialItemService.removeByIdAndBusIdAndMemberId(itemId, busUser.getId(), memberId);
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

    @ApiOperation(value = "批量审核优惠服务项", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/batch/memberId/{memberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateBatchByMemberId(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "操作者的盟员身份id", required = true)
                                        @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "itemIdList", value = "批量审核的优惠服务项id列表", required = true)
                                        @RequestBody @NotNull List<Integer> itemIdList
            , @ApiParam(name = "isOK", value = "是否通过，1为通过，0为不通过", required = true)
                                        @RequestParam(value = "isOK") Integer isOK) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionPreferentialItemService.updateBatchByBusIdAndMemberId(busUser.getId(), memberId, itemIdList, isOK);
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

    @ApiOperation(value = "新增优惠服务项", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/memberId/{memberId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String saveByBusIdAndMemberIdAndName(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "操作者的盟员身份id", required = true)
                                                @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "itemName", value = "优惠服务名称", required = true)
                                                @RequestBody @NotNull String itemName) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionPreferentialItemService.saveByBusIdAndMemberIdAndName(busUser.getId(), memberId, itemName);
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
