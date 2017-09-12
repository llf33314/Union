package com.gt.union.member.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.log.service.IUnionLogErrorService;
import com.gt.union.main.controller.IndexController;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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

}
