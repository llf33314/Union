package com.gt.union.common.exception;

import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.response.GTJsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一全局异常处理类
 *
 * @author linweicong
 * @version 2017-10-23 08:34:54
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public String defaultErrorHandler(Exception e) throws Exception {
        //日志文件记录
        this.logger.error("", e);
        if (e instanceof BaseException) {
            return GTJsonResult.instanceErrorMsg(((BaseException) e).getErrorMsg()).toString();
        }
        return GTJsonResult.instanceErrorMsg(CommonConstant.SYS_ERROR).toString();
    }

}
