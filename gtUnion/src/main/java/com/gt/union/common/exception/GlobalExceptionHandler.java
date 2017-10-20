package com.gt.union.common.exception;

import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.log.service.IUnionLogErrorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一全局异常处理类
 * Created by Administrator on 2017/7/24 0024.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private IUnionLogErrorService unionLogErrorService;

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public String defaultErrorHandler(Exception e) throws Exception {
        this.logger.error("", e); //日志文件记录
        this.unionLogErrorService.saveIfNotNull(e); //日志数据库记录
        if (e instanceof BaseException) {
            return GTJsonResult.instanceErrorMsg(((BaseException) e).getErrorMsg()).toString();
        }
        return GTJsonResult.instanceErrorMsg(CommonConstant.SYS_ERROR).toString();
    }

}
