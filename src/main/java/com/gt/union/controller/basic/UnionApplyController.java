package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.annotation.SysLogAnnotation;
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


    @ApiOperation(value = "查询入盟申请列表"
            , notes = "根据联盟id获取入盟申请，并根据enterpriseName/directorPhone进行模糊匹配"
            , produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listUncheckedApply(Page page
            , @ApiParam(name = "unionId", value = "联盟id",required = true)
             @RequestParam(name = "unionId", required = true) Integer unionId
            , @ApiParam(name = "enterpriseName", value = "申请企业")
             @RequestParam(name = "enterpriseName", required = false) String enterpriseName
            , @ApiParam(name = "directorPhone", value = "联系电话")
             @RequestParam(name = "directorPhone", required = false) String directorPhone){
        Page result = null;
        try {
            result = this.unionApplyService.listUncheckedApply(page, unionId, enterpriseName, directorPhone);
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

    @ApiOperation(value = "通过或不通过入盟申请"
            , notes = "根据联盟id及审批状态applyStatus通过或不通过入盟申请"
            , produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(description = "通过或不通过入盟申请", op_function = "1")
    public String updateApplyStatus(HttpServletRequest request, @ApiParam(name = "id", value = "入盟申请id", required = true)
                @PathVariable("id")Integer id
            , @ApiParam(name = "unionId", value = "联盟id", required = true)
                @RequestParam(name = "unionId", required = true) Integer unionId
            , @ApiParam(name = "applyStatus", value = "审批状态，1代表通过，2代表不通过", required = true)
                @RequestParam(name = "applyStatus", required = true) Integer applyStatus) {
        BusUser busUser = SessionUtils.getLoginUser(request);
        try{
            if(CommonUtil.isNotEmpty(busUser.getPid()) && busUser.getPid() != 0){
                throw new BusinessException("请使用主账号权限");
            }
            unionApplyService.updateUnionApplyVerify(busUser.getId(),id,unionId, applyStatus);
        }catch (BaseException e){
            logger.error(e.getMessage(),e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        } catch (Exception e){
            logger.error(e.getMessage(),e);
            return GTJsonResult.instanceErrorMsg("审核失败").toString();
        }
        return GTJsonResult.instanceSuccessMsg(null, null,"审核成功").toString();
    }

    @ApiOperation(value = "新建申请入盟或推荐入盟"
            , notes = "新建申请入盟或推荐入盟"
            , produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(description = "新建申请入盟或推荐入盟", op_function = "1")
    public String saveApply(HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id",required = true)
                @RequestParam(name = "unionId", required = true) Integer unionId
            , @ApiParam(name = "applyType", value = "新建类型，1代表自由申请，2代表推荐申请", required = true)
                @RequestParam(name = "applyType", required = true) Integer applyType
            , @ApiParam(name = "unionApplyVO", value = "新建的数据模型", required = true)
                @RequestBody UnionApplyVO unionApplyVO){
        BusUser busUser = SessionUtils.getLoginUser(request);
        try{
            if(CommonUtil.isNotEmpty(busUser.getPid()) && busUser.getPid() != 0){
                throw new BusinessException("请使用主账号权限");
            }
            Map<String,Object> data = unionApplyService.addUnionApply(busUser.getId(), unionId, unionApplyVO, applyType);
            return GTJsonResult.instanceSuccessMsg(data,null,"添加成功").toString();
        }catch (BaseException e){
            logger.error(e.getMessage(),e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return GTJsonResult.instanceErrorMsg("添加失败").toString();
        }
    }
}
