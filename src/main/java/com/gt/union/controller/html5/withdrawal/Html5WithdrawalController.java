package com.gt.union.controller.html5.withdrawal;

import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.html5.withdrawal.IHtml5WithdrawalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 手机端H5提现 前端控制器
 * Created by Administrator on 2017/8/28 0028.
 */
@RestController
@RequestMapping(value = "/html5/cash")
public class Html5WithdrawalController {
    private static final String INDEX = "Html5WithdrawalController.index()";

    private Logger logger = LoggerFactory.getLogger(Html5WithdrawalController.class);

    @Autowired
    private IHtml5WithdrawalService html5WithdrawalService;

    public String index(HttpServletRequest request) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Map<String, Object> result = this.html5WithdrawalService.index(busUser.getId());
            return GTJsonResult.instanceSuccessMsg().toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            return GTJsonResult.instanceErrorMsg(INDEX, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }
}
