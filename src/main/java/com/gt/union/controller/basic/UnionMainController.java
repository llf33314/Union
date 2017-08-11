package com.gt.union.controller.basic;

import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionMainService;
import com.gt.union.vo.basic.UnionMainInfoVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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
    private static final String INDEX = "UnionMainController.index()";
    private static final String INDEX_ID = "UnionMainController.indexById()";
    private static final String LIST_MY_UNION = "UnionMainController.listMyUnion()";
    private static final String LIST = "UnionMainController.list()";
    private static final String UPDATE_ID = "UnionMainController.updateById()";
    private static final String GET_ID = "UnionMainController.getById()";
    private static final String GET_INSTANCE_STEP = "UnionMainController.getInstanceStep()";
    private static final String SAVE = "UnionMainController.save()";
    private Logger logger = Logger.getLogger(UnionMainController.class);

    @Autowired
    private IUnionMainService unionMainService;

    @ApiOperation(value = "首页查询我的联盟信息", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "1", description = "首页查询我的联盟信息")
    @RequestMapping(value = "/index", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String index(HttpServletRequest request) {
        try {
            BusUser user = SessionUtils.getLoginUser(request);
            Integer busId = user.getId();
            if (user.getPid() != null && user.getPid() != 0) {
                busId = user.getPid();
            }
            Map<String, Object> data = unionMainService.indexByBusId(busId);
            return GTJsonResult.instanceSuccessMsg(data).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(INDEX, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "选择联盟时查询我的联盟信息", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "1", description = "查询我的联盟信息")
    @RequestMapping(value = "/index/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String indexById(HttpServletRequest request
            , @ApiParam(name="id", value = "联盟id", required = true)  @PathVariable("id") Integer id) {
        try {
            Map<String, Object> data = unionMainService.indexById(id);
            return GTJsonResult.instanceSuccessMsg(data).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(INDEX_ID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "查询商家创建及加入的所有联盟列表", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "1", description = "查询商家创建及加入的所有联盟列表")
    @RequestMapping(value = "/myUnion", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listMyUnion(HttpServletRequest request) {
        try {
            BusUser user = SessionUtils.getLoginUser(request);
            Integer busId = user.getId();
            if (user.getPid() != null && user.getPid() != 0) {
                busId = user.getPid();
            }
            List<UnionMain> list = unionMainService.listMyUnion(busId);
            return GTJsonResult.instanceSuccessMsg(list).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_MY_UNION, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "查询所有联盟列表", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "1", description = "查询所有联盟列表")
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String list(HttpServletRequest request) {
        try {
            List<UnionMain> list = unionMainService.list();
            return GTJsonResult.instanceSuccessMsg(list).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "更新联盟信息，要求盟主权限", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "3", description = "更新联盟信息，要求盟主权限")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateById(HttpServletRequest request
          , @ApiParam(name="id", value = "联盟id", required = true) @PathVariable Integer id
          , @ApiParam(name="unionMain", value = "联盟更新信息", required = true) @RequestBody UnionMainInfoVO unionMainInfo ){
        try{
            BusUser user = SessionUtils.getLoginUser(request);
            if(CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0){
                throw new BusinessException(UPDATE_ID, "","请使用主账号权限");
            }
            unionMainInfo.getUnionMain().setId(id);
            unionMainService.updateById(id, user.getId(), unionMainInfo);
            return GTJsonResult.instanceSuccessMsg().toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(UPDATE_ID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "获取联盟信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getById(HttpServletRequest request, @ApiParam(name="id", value = "联盟id", required = true) @PathVariable("id") Integer id){
        try{
            BusUser user = SessionUtils.getLoginUser(request);
            if(CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0){
                throw new BusinessException(GET_ID, "", "请使用主账号权限");
            }
            Map<String,Object> data = unionMainService.getById(id);
            return GTJsonResult.instanceSuccessMsg(data).toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(GET_ID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "获取创建联盟步骤信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/instance/step", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getInstanceStep(HttpServletRequest request){
        try{
            BusUser user = SessionUtils.getLoginUser(request);
            if(CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0){
                throw new BusinessException(GET_INSTANCE_STEP, "", "请使用主账号权限");
            }
            Map<String,Object> data = unionMainService.getInstanceStep(user.getId());
            return GTJsonResult.instanceSuccessMsg(data).toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(GET_INSTANCE_STEP, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "保存创建联盟的信息", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "3", description = "保存创建联盟的信息")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String save(HttpServletRequest request
        , @ApiParam(name="unionMain", value = "保存创建联盟的信息", required = true) @RequestBody List<UnionMainInfoVO> unionMainInfoVOList ){
        try{
            BusUser user = SessionUtils.getLoginUser(request); //TODO 没有判断user是否为空
            if(CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0){
                throw new BusinessException(SAVE, "", "请使用主账号权限");
            }
            unionMainService.save(user.getId(), unionMainInfoVOList);
            return GTJsonResult.instanceSuccessMsg().toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(SAVE, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

}
