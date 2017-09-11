package com.gt.union.index.controller;

import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.entity.BusUser;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.index.service.IIndexService;
import com.gt.union.log.service.IUnionLogErrorService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
@RestController
public class IndexController {
    private Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private IUnionLogErrorService unionLogErrorService;

    @Autowired
    private IIndexService indexService;

    @ApiOperation(value = "商家联盟首页-默认未选定盟员身份", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/index", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String index(HttpServletRequest request) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer busId = busUser.getId();
            Integer accountType = BusUserConstant.ACCOUNT_TYPE_MASTER;//默认是主帐号
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                busId = busUser.getPid();
                accountType = BusUserConstant.ACCOUNT_TYPE_SUB;//存在父帐号，则说明是子帐号
            }

            Map<String, Object> result = this.indexService.index(busId);
            result.put("busId", busId);
            result.put("accountType", accountType);

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

    @ApiOperation(value = "商家联盟首页-选定盟员身份", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/index/memberId/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String indexByUnionId(HttpServletRequest request
            , @ApiParam(name = "memberId", value = "盟员id", required = true)@PathVariable("memberId") Integer memberId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer busId = busUser.getId();
            Integer accountType = BusUserConstant.ACCOUNT_TYPE_MASTER;//默认是主帐号
            if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
                busId = busUser.getPid();
                accountType = BusUserConstant.ACCOUNT_TYPE_SUB;//存在父帐号，则说明是子帐号
            }

            Map<String, Object> result = this.indexService.indexByMemberId(memberId, busId);
            result.put("busId", busId);
            result.put("accountType", accountType);

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
}
