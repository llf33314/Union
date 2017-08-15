package com.gt.union.controller.basic;

import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionMainService;
import com.gt.union.vo.basic.UnionMainCreateInfoVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
            BusUser user = SessionUtils.getLoginUser(request);
            Integer busId = user.getId();
            if (user.getPid() != null && user.getPid() != 0) {
                busId = user.getPid();
            }
            Map<String, Object> data = unionMainService.indexById(id, busId);
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
        }  catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "更新联盟信息，要求盟主权限", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "3", description = "更新联盟信息，要求盟主权限")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateById(HttpServletRequest request
          , @ApiParam(name="id", value = "联盟id", required = true) @PathVariable Integer id
          , @ApiParam(name="unionMain", value = "联盟更新信息", required = true) @RequestBody @Valid UnionMain unionMain , BindingResult result){
        try{
            InvalidParameter( result );
            BusUser user = SessionUtils.getLoginUser(request);
            if(CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0){
                throw new BusinessException(UPDATE_ID, "", CommonConstant.UNION_BUS_PARENT_MSG);
            }
            unionMain.setId(id);
            unionMainService.updateById(id, user.getId(), unionMain);
            return GTJsonResult.instanceSuccessMsg().toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(UPDATE_ID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    /**
     * 参数校验是否合法
     *
     * @param result BindingResult
     */
    private void InvalidParameter( BindingResult result ) throws BusinessException {
        if ( result.hasErrors() ) {
            List<ObjectError> errorList = result.getAllErrors();
            for ( ObjectError error : errorList ) {
                throw new BusinessException("","",error.getDefaultMessage());
            }
        }
    }

/*    @ApiOperation(value = "获取联盟信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getById(HttpServletRequest request, @ApiParam(name="id", value = "联盟id", required = true) @PathVariable("id") Integer id){
        try{
            BusUser user = SessionUtils.getLoginUser(request);
            if(CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0){
                throw new BusinessException(GET_ID, "", CommonConstant.UNION_BUS_PARENT_MSG);
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
    }*/

    @ApiOperation(value = "获取创建联盟步骤信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/instance/step", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getInstanceStep(HttpServletRequest request){
        try{
            BusUser user = SessionUtils.getLoginUser(request);
            if(CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0){
                throw new BusinessException(GET_INSTANCE_STEP, "", CommonConstant.UNION_BUS_PARENT_MSG);
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
        , @ApiParam(name="unionMain", value = "保存创建联盟的信息", required = true) @RequestBody UnionMainCreateInfoVO unionMainCreateInfoVO ){
        try{
            BusUser user = SessionUtils.getLoginUser(request);
            if(CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0){
                throw new BusinessException(SAVE, "", CommonConstant.UNION_BUS_PARENT_MSG);
            }
            unionMainService.save(user.getId(), unionMainCreateInfoVO);
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
