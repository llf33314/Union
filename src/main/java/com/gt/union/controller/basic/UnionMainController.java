package com.gt.union.controller.basic;

import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionMainService;
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

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String unionList(HttpServletRequest request) {
        BusUser user = SessionUtils.getLoginUser(request);
        Map<String,Object> data = new HashMap<String,Object>();
        try {
            Integer busId = user.getId();
            if(user.getPid() != null && user.getPid() != 0){
                busId = user.getPid();
            }
            List<UnionMain> list = unionMainService.getMemberUnionList(busId);
            data.put("unionList",list);
            if(list.size() > 0){
                UnionMain main = list.get(0);
                data.put("main",main);//第一个联盟
            }
        }catch (Exception e){
            logger.error("获取联盟列表失败");
            return GTJsonResult.instanceSuccessMsg(data,null,"获取联盟信息成功").toString();
        }
        return GTJsonResult.instanceSuccessMsg(data,null,"获取联盟信息成功").toString();
    }




    /**
     * 获取联盟信息
     * @param unionId
     * @return
     */
    @RequestMapping(value = "/{unionId}", method = RequestMethod.GET)
    public String getUnionMainInfo(HttpServletRequest request, @PathVariable("unionId") Integer unionId){
        BusUser user = SessionUtils.getLoginUser(request);
        Map<String,Object> data = new HashMap<String,Object>();
        Integer busId = user.getId();
        try{
            if(user.getPid() != null && user.getPid() != 0){
                busId = user.getPid();
            }
            data = unionMainService.getUnionMainMemberInfo(busId,unionId);
            return GTJsonResult.instanceSuccessMsg(data,null,"获取联盟信息成功").toString();
        }catch (Exception e){
            logger.error("获取联盟信息错误");
            return GTJsonResult.instanceErrorMsg("获取联盟信息失败").toString();
        }
    }


    @RequestMapping(value = "/getUnionMainCardInfo", method = RequestMethod.GET)
    public String getUnionMainCardInfo(HttpServletRequest request, @RequestParam(value = "unionId", required = true) Integer unionId){
        BusUser user = SessionUtils.getLoginUser(request);
        Map<String,Object> data = null;
        try{
            Integer busId = user.getId();
            if(user.getPid() != null && user.getPid() != 0){
                busId = user.getPid();
            }
            data = unionMainService.getUnionMainMemberInfo(busId,unionId);
            return GTJsonResult.instanceSuccessMsg(data,null,"获取联盟信息成功").toString();
        }catch (Exception e){
            logger.error("获取联盟信息错误");
            return GTJsonResult.instanceErrorMsg("获取联盟信息失败").toString();
        }
    }


}
