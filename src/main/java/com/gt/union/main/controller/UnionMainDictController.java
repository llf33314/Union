package com.gt.union.main.controller;

import com.gt.union.common.response.GTJsonResult;
import com.gt.union.main.entity.UnionMainDict;
import com.gt.union.main.service.IUnionMainDictService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 联盟设置申请填写信息 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionMainDict")
public class UnionMainDictController {
    @Autowired
    private IUnionMainDictService unionMainDictService;

    @ApiOperation(value = "根据联盟id，获取入盟申请必填信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listByUnionId(@PathVariable("unionId") Integer unionId) throws Exception {
        List<UnionMainDict> result = this.unionMainDictService.listByUnionId(unionId);
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }
}
