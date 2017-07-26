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
    public String listUncheckedApply(Page page, @RequestParam(name = "unionId", required = true) Integer unionId
            , @RequestParam(name = "enterpriseName", required = false) String enterpriseName
            , @RequestParam(name = "directorPhone", required = false) String directorPhone){
        Page result = null;
        try {
            result = this.unionApplyService.listUncheckedApply(page, unionId, enterpriseName, directorPhone);
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(description = "通过或不通过入盟申请", op_function = "1")
    public String updateApplyStatus(@PathVariable("id")Integer id, @RequestParam(name = "unionId", required = true) Integer unionId
            , @RequestParam(name = "applyStatus", required = true) Integer applyStatus) {
        //TODO
        return GTJsonResult.instanceSuccessMsg().toString();
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(description = "申请入盟或推荐入盟", op_function = "1")
    public String saveApply(@RequestParam(name = "unionId", required = true) Integer unionId
            , @RequestParam(name = "applyType", required = true) Integer applyType , String unionName, UnionApplyInfo unionApplyInfo){
        //TODO
        return GTJsonResult.instanceSuccessMsg().toString();
    }
}
