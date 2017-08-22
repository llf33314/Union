package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PageUtil;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionMainService;
import com.gt.union.service.common.IUnionValidateService;
import com.gt.union.vo.basic.UnionMainCreateInfoVO;
import com.gt.union.vo.basic.UnionMainInfoVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
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
    private static final String INSTANCE = "UnionMainController.instance()";
    private static final String MEMBER = "UnionMainController.member()";
    private static final String SAVE = "UnionMainController.save()";
    private Logger logger = Logger.getLogger(UnionMainController.class);

    @Autowired
    private IUnionMainService unionMainService;

	@Autowired
	private IUnionValidateService unionValidateService;

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
            data.put("userType", (user.getPid() != null && user.getPid() != 0) ? 1 : 2);//1：子账号  2：主账号
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
            data.put("userType", (user.getPid() != null && user.getPid() != 0) ? 1 : 2);//1：子账号  2：主账号
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

    @ApiOperation(value = "分页查询所有可加入的联盟列表", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "1", description = "分页查询所有可加入的联盟列表")
    @RequestMapping(value = "/valid/access", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String list(Page page, HttpServletRequest request) {
        try {
            List<UnionMain> unionMainList = unionMainService.listValidAccess();
            PageUtil pageUtil = new PageUtil();
            pageUtil.setRecords(unionMainList);
            pageUtil.setCurrent(page.getCurrent());
            pageUtil.setSize(page.getSize());
            return GTJsonResult.instanceSuccessMsg(pageUtil.getMapAsPage()).toString();
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
          , @ApiParam(name="vo", value = "联盟更新信息", required = true) @ModelAttribute @Valid UnionMainInfoVO vo , BindingResult result){
        try{
			this.unionValidateService.checkBindingResult(result);
            BusUser user = SessionUtils.getLoginUser(request);
            if(CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0){
                throw new BusinessException(UPDATE_ID, "", CommonConstant.UNION_BUS_PARENT_MSG);
            }
            unionMainService.updateById(id, user.getId(), vo);
            return GTJsonResult.instanceSuccessMsg().toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(UPDATE_ID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "创建联盟", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/instance", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String instance(HttpServletRequest request){
        try{
            BusUser user = SessionUtils.getLoginUser(request);
            if(CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0){
                throw new BusinessException(INSTANCE, "", CommonConstant.UNION_BUS_PARENT_MSG);
            }
            Map<String,Object> data = unionMainService.instance(user.getId());
            return GTJsonResult.instanceSuccessMsg(data).toString();
        }catch (BaseException e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        }catch (Exception e){
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(INSTANCE, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "保存创建联盟的信息", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "3", description = "保存创建联盟的信息")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String save(HttpServletRequest request
        , @ApiParam(name="unionMainCreateInfoVO", value = "保存创建联盟的信息", required = true) @RequestBody UnionMainCreateInfoVO unionMainCreateInfoVO ){
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
