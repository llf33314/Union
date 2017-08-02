package com.gt.union.controller.basic;

import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionMainService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
    @ApiOperation(value = "查询我的联盟信息", notes = "初始进入我的联盟时调取该方法", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "1", description = "查询我的联盟信息")
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
            return GTJsonResult.instanceSuccessMsg(data,null,"我的联盟信息成功").toString();
        }catch (Exception e){
            logger.error("我的联盟信息失败",e);
            return GTJsonResult.instanceErrorMsg("我的联盟信息失败").toString();
        }
    }

    @ApiOperation(value = "查询我的联盟信息", notes = "选择某个联盟时调取该方法", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "1", description = "查询我的联盟信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String unionMain(HttpServletRequest request, @PathVariable Integer id) {
        BusUser user = SessionUtils.getLoginUser(request);
        Map<String,Object> data = new HashMap<String,Object>();
        try {
            Integer busId = user.getId();
            if(user.getPid() != null && user.getPid() != 0){
                busId = user.getPid();
            }
            data = unionMainService.getUnionMainMemberInfo(busId, id);
            return GTJsonResult.instanceSuccessMsg(data,null,"我的联盟信息成功").toString();
        }catch (Exception e){
            logger.error("我的联盟信息失败", e);
            return GTJsonResult.instanceErrorMsg("我的联盟信息失败").toString();
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
            logger.error("获取联盟列表失败", e);
            return GTJsonResult.instanceErrorMsg("获取联盟列表失败").toString();
        }
    }



    /**
     * 更新联盟信息
     * @param request
     * @param id   联盟id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    public String updateUnionMain(HttpServletRequest request, @PathVariable Integer id, @RequestBody UnionMain main){
        BusUser user = SessionUtils.getLoginUser(request);
        try{
            Integer busId = user.getId();
            if(user.getPid() != null && user.getPid() != 0){
                busId = user.getPid();
            }
            main.setId(id);
            unionMainService.updateUnionMain(main, busId);
            return GTJsonResult.instanceSuccessMsg(null,null,"更新成功").toString();
        }catch (BaseException e){
            logger.error("获取联盟信息错误", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }catch (Exception e){
            logger.error("获取联盟信息错误", e);
            return GTJsonResult.instanceErrorMsg("更新失败").toString();
        }
    }


    /**
     * 获取创建联盟信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/createUnionInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String getCreateUnionInfo(HttpServletRequest request){
        BusUser user = SessionUtils.getLoginUser(request);
        try{
            return unionMainService.getCreateUnionInfo(user);
        }catch (BaseException e){
            logger.error("获取联盟信息错误", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }catch (Exception e){
            logger.error("获取联盟信息错误", e);
            return GTJsonResult.instanceErrorMsg("更新失败").toString();
        }
    }


}
