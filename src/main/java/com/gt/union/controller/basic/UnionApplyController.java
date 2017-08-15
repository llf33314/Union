package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionApplyService;
import com.gt.union.vo.basic.UnionApplyVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    private static final String LIST_UNIONID_APPLYSTATUS = "UnionApplyController.listByUnionIdAndApplyStatus()";
    private static final String UPDATE_UNIONID_APPLYSTATUS = "UnionApplyController.updateByUnionIdAndApplyStatus()";
    private static final String SAVE = "UnionApplyController.save()";

    private Logger logger = LoggerFactory.getLogger(UnionApplyController.class);

    @Autowired
    private IUnionApplyService unionApplyService;


    @ApiOperation(value = "查询入盟申请相关信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/applyStatus/{applyStatus}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listByUnionIdAndApplyStatus(Page page
            , @ApiParam(name = "unionId", value = "联盟id",required = true)
             @PathVariable("unionId") Integer unionId
            , @ApiParam(name = "applyStatus", value = "审核状态，可选值0=未审核，1=审核通过，2=审核不通过", required = true)
             @PathVariable("applyStatus") Integer applyStatus
            , @ApiParam(name = "enterpriseName", value = "申请企业,模糊匹配")
             @RequestParam(name = "enterpriseName", required = false) String enterpriseName
            , @ApiParam(name = "directorPhone", value = "联系电话,模糊匹配")
             @RequestParam(name = "directorPhone", required = false) String directorPhone){
        try {
            Page result = this.unionApplyService.listByUnionIdAndApplyStatus(page, unionId, applyStatus, enterpriseName, directorPhone);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_UNIONID_APPLYSTATUS, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "审批入盟申请", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{id}/unionId/{unionId}/applyStatus/{applyStatus}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(description = "通过或不通过入盟申请", op_function = "1")
    public String updateByUnionIdAndApplyStatus(HttpServletRequest request
            , @ApiParam(name = "id", value = "入盟申请id", required = true) @PathVariable("id")Integer id
            , @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId
            , @ApiParam(name = "applyStatus", value = "审批状态，可选值1=通过，2=不通过", required = true) @PathVariable("applyStatus") Integer applyStatus) {
        try{
            BusUser busUser = SessionUtils.getLoginUser(request);
            if(CommonUtil.isNotEmpty(busUser.getPid()) && busUser.getPid() != 0){
                throw new BusinessException(UPDATE_UNIONID_APPLYSTATUS, "", CommonConstant.UNION_BUS_PARENT_MSG);
            }
            this.unionApplyService.updateByUnionIdAndApplyStatus(busUser.getId(), id, unionId, applyStatus);
            return GTJsonResult.instanceSuccessMsg().toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(UPDATE_UNIONID_APPLYSTATUS, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "新建申请入盟或推荐入盟", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(description = "新建申请入盟或推荐入盟", op_function = "1")
    public String save(HttpServletRequest request
            , @ApiParam(name = "unionApplyVO", value = "新建的数据模型", required = true) @RequestBody UnionApplyVO unionApplyVO){
        try{
            BusUser busUser = SessionUtils.getLoginUser(request);
            if(CommonUtil.isNotEmpty(busUser.getPid()) && busUser.getPid() != 0){
                throw new BusinessException(SAVE, "",CommonConstant.UNION_BUS_PARENT_MSG);
            }
            Map<String,Object> data = this.unionApplyService.save(busUser.getId(), unionApplyVO);
            return GTJsonResult.instanceSuccessMsg(data).toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("",e);
            return GTJsonResult.instanceErrorMsg(SAVE, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }
}
