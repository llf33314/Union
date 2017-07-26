package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.entity.basic.UnionApply;
import com.gt.union.service.basic.IUnionApplyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 联盟成员申请推荐 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@RestController
@RequestMapping("/unionApply")
public class UnionApplyController {
    private Logger logger = LoggerFactory.getLogger(UnionApplyController.class);
    @Autowired
    private IUnionApplyService unionApplyService;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(description = "查询联盟成员申请推荐", op_function = "1")
    public String listUnionApplyVO(Page page, @RequestParam(name = "unionId", required = true) Integer unionId
            , @RequestParam(name = "enterpriseName", required = false) String enterpriseName
            , @RequestParam(name = "directorPhone", required = false) String directorPhone){
        Page result = null;
        try {
            result = this.unionApplyService.listUnionApplyVO(page, unionId, enterpriseName, directorPhone);
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }
}
