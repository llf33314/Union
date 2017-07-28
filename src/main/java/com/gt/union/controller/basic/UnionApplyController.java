package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.entity.basic.UnionApply;
import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.service.basic.IUnionApplyService;
import com.gt.union.vo.basic.UnionApplyVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
            , @RequestParam(name = "applyType", required = true) Integer applyType , @RequestBody UnionApplyVO unionApplyVO){
        //TODO
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("unionId", unionId);
        resultMap.put("applyType", applyType);
        if (unionApplyVO != null) {
            resultMap.put("userName", unionApplyVO.getUserName());
            resultMap.put("unionApply", unionApplyVO.getUnionApply());
            resultMap.put("unionApplyInfo", unionApplyVO.getUnionApplyInfo());
        }
        return GTJsonResult.instanceSuccessMsg(resultMap).toString();
    }
}
