package com.gt.union.main.controller;

import com.gt.union.common.response.GTJsonResult;
import com.gt.union.main.entity.UnionMainDict;
import com.gt.union.main.service.IUnionMainDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 联盟设置申请填写信息 前端控制器
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
@Api(description = "联盟设置申请填写信息")
@RestController
@RequestMapping("/unionMainDict")
public class UnionMainDictController {
    @Autowired
    private IUnionMainDictService unionMainDictService;

    @ApiOperation(value = "根据联盟id，获取入盟申请必填信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listByUnionId(@ApiParam(name = "unionId", value = "联盟id", required = true)
                                @PathVariable("unionId") Integer unionId) throws Exception {
        List<UnionMainDict> result = this.unionMainDictService.listByUnionId(unionId);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }
}
