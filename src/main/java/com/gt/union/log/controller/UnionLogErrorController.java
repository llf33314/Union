package com.gt.union.log.controller;

import com.gt.union.common.response.GTJsonResult;
import com.gt.union.log.service.IUnionLogErrorService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 错误日志表 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-08
 */
@RestController
@RequestMapping("/unionLogError")
public class UnionLogErrorController {
    private Logger logger = LoggerFactory.getLogger(UnionLogErrorController.class);

    @Autowired
    private IUnionLogErrorService unionLogErrorService;

    @ApiOperation(value = "", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String test(){
	    try {
	        int i = 1;
	        int j = 0;
            System.out.println(i/j);
            return GTJsonResult.instanceSuccessMsg().toString();
	    } catch (Exception e) {
	        logger.error("", e);
	        this.unionLogErrorService.saveIfNotNull(e);
	        return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
	    }
    }
}
