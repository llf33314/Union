package com.gt.union.main.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.entity.BusUser;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.service.IUnionValidateService;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.log.service.IUnionLogErrorService;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.main.vo.UnionMainVO;
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

/**
 * <p>
 * 联盟主表 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionMain")
public class UnionMainController {
    private Logger logger = LoggerFactory.getLogger(UnionMainController.class);

    @Autowired
    private IUnionLogErrorService unionLogErrorService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionValidateService unionValidateService;

    @ApiOperation(value = "获取我的所有联盟的信息，包括我创建的、以及我加入的", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listByBusId(HttpServletRequest request) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer busId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                busId = busUser.getPid();
            }
            List<UnionMain> result = this.unionMainService.listByBusId(busId);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        } catch (Exception e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
        }
    }

    @ApiOperation(value = "分页获取我的所有联盟的信息，包括我创建的、以及我加入的", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/page", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String PageByBusId(HttpServletRequest request, Page page) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer busId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                busId = busUser.getPid();
            }
            Page result = this.unionMainService.PageByBusId(page, busId);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        } catch (Exception e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
        }
    }

    @ApiOperation(value = "更新联盟信息，要求盟主权限", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "3", description = "更新联盟信息，要求盟主权限")
    @RequestMapping(value = "/memberId/{memberId}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String method(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "盟员id", required = true) @PathVariable("memberId") Integer memberId
            , @ApiParam(name = "unionMainVO", value = "更新信息实体", required = true) @RequestBody @Valid UnionMainVO unionMainVO, BindingResult bindingResult) {
        try {
            this.unionValidateService.checkBindingResult(bindingResult);
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer busId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                busId = busUser.getPid();
            }
            return GTJsonResult.instanceSuccessMsg().toString();
        } catch (BaseException e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        } catch (Exception e) {
            logger.error("", e);
            this.unionLogErrorService.saveIfNotNull(e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
        }
    }
}
