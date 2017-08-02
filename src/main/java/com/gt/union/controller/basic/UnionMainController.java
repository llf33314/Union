package com.gt.union.controller.basic;

import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.basic.UnionInfoDict;
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
import java.util.HashMap;
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

    private Logger logger = Logger.getLogger(UnionMainController.class);

    @Autowired
    private IUnionMainService unionMainService;

    /**
     * 查询我的联盟信息
     * @param request
     * @return
     */
    @ApiOperation(value = "首页查询我的联盟信息", notes = "初始进入我的联盟时调取该方法", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "1", description = "首页查询我的联盟信息")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String unionMainIndex(HttpServletRequest request) {
        BusUser user = SessionUtils.getLoginUser(request);
        Map<String,Object> data = new HashMap<String,Object>();
        try {
            Integer busId = user.getId();
            if(user.getPid() != null && user.getPid() != 0){
                busId = user.getPid();
            }
            data = unionMainService.getUnionMainMemberInfo(busId);
            return GTJsonResult.instanceSuccessMsg(data,null,"首页查询我的联盟信息成功").toString();
        }catch (Exception e){
            logger.error("首页查询我的联盟信息错误",e);
            return GTJsonResult.instanceErrorMsg("首页查询我的联盟信息失败").toString();
        }
    }

    @ApiOperation(value = "查询我的联盟信息", notes = "选择某个联盟时调取该方法", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "1", description = "查询我的联盟信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String unionMain(HttpServletRequest request, @PathVariable("id") Integer id,
                            @ApiParam(name="unionId", value = "联盟id", required = true) @RequestParam(name = "unionId", required = true) Integer unionId) {
        BusUser user = SessionUtils.getLoginUser(request);
        Map<String,Object> data = new HashMap<String,Object>();
        try {
            Integer busId = user.getId();
            if(user.getPid() != null && user.getPid() != 0){
                busId = user.getPid();
            }
            data = unionMainService.getUnionMainMemberInfo(busId, id);
            return GTJsonResult.instanceSuccessMsg(data,null,"查询我的联盟信息成功").toString();
        }catch (Exception e){
            logger.error("查询我的联盟信息错误", e);
            return GTJsonResult.instanceErrorMsg("查询我的联盟信息失败").toString();
        }
    }



    @ApiOperation(value = "查询商家加入的联盟列表", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "1", description = "查询商家加入的联盟列表")
    @RequestMapping(value = "/unionList", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String unionList(HttpServletRequest request) {
        BusUser user = SessionUtils.getLoginUser(request);
        try {
            Integer busId = user.getId();
            if(user.getPid() != null && user.getPid() != 0){
                busId = user.getPid();
            }
            List<UnionMain> list = unionMainService.getMemberUnionList(busId);
            return GTJsonResult.instanceSuccessMsg(list,null,"获取联盟列表成功").toString();
        }catch (Exception e){
            logger.error("查询商家加入的联盟列表错误", e);
            return GTJsonResult.instanceErrorMsg("获取联盟列表失败").toString();
        }
    }


    /**
     * 更新联盟信息
     * @param request
     * @param id    联盟id
     * @param unionId   联盟id
     * @param unionMainInfo  联盟信息
     * @return
     */
    @ApiOperation(value = "盟主更新联盟信息", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "3", description = "盟主更新联盟信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateUnionMain(HttpServletRequest request,
                                  @ApiParam(name="id", value = "联盟id", required = true) @PathVariable Integer id,
                                  @ApiParam(name="unionId", value = "联盟id", required = true) @RequestParam(name="unionId", required = true) Integer unionId,
                                  @ApiParam(name="unionMain", value = "联盟更新信息", required = true) @RequestBody UnionMainInfoVO unionMainInfo ){
        BusUser user = SessionUtils.getLoginUser(request);
        try{
            if(CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0){
                throw new BusinessException("请使用主账号操作");
            }
            unionMainInfo.getUnionMain().setId(id);
            unionMainService.updateUnionMain(unionMainInfo, user.getId());
            return GTJsonResult.instanceSuccessMsg(null,null,"更新成功").toString();
        }catch (BaseException e){
            logger.error("盟主更新联盟信息错误", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }catch (Exception e){
            logger.error("盟主更新联盟信息错误", e);
            return GTJsonResult.instanceErrorMsg("更新失败").toString();
        }
    }


    /**
     * 获取创建联盟信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/unionCreateInfo", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getUnionCreateInfo(HttpServletRequest request){
        BusUser user = SessionUtils.getLoginUser(request);
        try{
            if(CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0){
                throw new BusinessException("请使用主账号操作");
            }
            return unionMainService.getCreateUnionInfo(user);
        }catch (BaseException e){
            logger.error("获取创建联盟信息错误", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }catch (Exception e){
            logger.error("获取创建联盟信息错误", e);
            return GTJsonResult.instanceErrorMsg("创建失败").toString();
        }
    }



    /**
     * 保存创建联盟的信息
     * @param request
     * @param unionMainInfo  联盟信息
     * @return
     */
    @ApiOperation(value = "保存创建联盟的信息", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "3", description = "保存创建联盟的信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String saveUnionMain(HttpServletRequest request,
                                  @ApiParam(name="unionMain", value = "保存创建联盟的信息", required = true) @RequestBody UnionMainInfoVO unionMainInfo ){
        BusUser user = SessionUtils.getLoginUser(request);
        try{
            if(CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0){
                throw new BusinessException("请使用主账号操作");
            }
            unionMainService.saveUnionMainInfo(unionMainInfo, user);
            return GTJsonResult.instanceSuccessMsg(null,null,"创建成功").toString();
        }catch (BaseException e){
            logger.error("保存创建联盟的信息错误", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }catch (Exception e){
            logger.error("保存创建联盟的信息错误", e);
            return GTJsonResult.instanceErrorMsg("创建失败").toString();
        }
    }

}
