package com.gt.union.main.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.log.service.IUnionLogErrorService;
import com.gt.union.main.service.IUnionMainTransferService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 联盟转移 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionMainTransfer")
public class UnionMainTransferController {
    private Logger logger = LoggerFactory.getLogger(UnionMainTransferController.class);

    @Autowired
    private IUnionLogErrorService unionLogErrorService;

    @Autowired
    private IUnionMainTransferService unionMainTransferService;

    //-------------------------------------------------- get ----------------------------------------------------------

    @ApiOperation(value = "分页查询盟主服务转移申请信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/pageMap/memberId/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageMapByFromMemberId(HttpServletRequest request, Page page
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                        @PathVariable("memberId") Integer memberId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer busId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                busId = busUser.getPid();
            }
            Page result = this.unionMainTransferService.pageMapByBusIdAndFromMemberId(page, busId, memberId);
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

    @ApiOperation(value = "转移盟主服务权限", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/memberId/{memberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String saveByFromMemberIdAndToMemberId(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                                  @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "tgtMemberId", value = "被转移的盟员身份id", required = true)
                                                  @RequestParam(value = "tgtMemberId") Integer tgtMemberId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionMainTransferService.saveByBusIdAndFromMemberIdAndToMemberId(busUser.getId(), memberId, tgtMemberId);
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

    @ApiOperation(value = "撤回盟主权限转移申请", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{transferId}/memberId/{memberId}/revoke", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String revokeByIdAndFromMemberId(HttpServletRequest request
            , @ApiParam(name = "transferId", value = "权限转移申请id", required = true)
                                            @PathVariable("transferId") Integer transferId
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                            @PathVariable("memberId") Integer memberId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionMainTransferService.revokeByIdAndBusIdAndFromMemberId(transferId, busUser.getId(), memberId);
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

    @ApiOperation(value = "接受或拒绝盟主权限转移", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{transferId}/memberId/{memberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateStatusByTransferIdAndToMemberId(HttpServletRequest request
            , @ApiParam(name = "transferId", value = "权限转移申请id", required = true)
                                                        @PathVariable("transferId") Integer transferId
            , @ApiParam(name = "memberId", value = "操作人的盟员身份id", required = true)
                                                        @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "isOK", value = "是否接受，1为是，0为否", required = true)
                                                        @RequestParam(value = "isOK") Integer isOK) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionMainTransferService.updateStatusByTransferIdAndBusIdAndToMemberId(transferId, busUser.getId(), memberId, isOK);
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
