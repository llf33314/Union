package com.gt.union.controller.basic;

import com.gt.union.entity.basic.UnionMain;
import com.gt.union.service.basic.IUnionMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 联盟主表 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@RestController
@RequestMapping("/unionMain")
public class UnionMainController {
    @Autowired
    private IUnionMainService unionMainService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public UnionMain selectById(@PathVariable(name = "id") String id) {
        UnionMain unionMain = unionMainService.selectById(id);
        return unionMain;
    }
	
}
