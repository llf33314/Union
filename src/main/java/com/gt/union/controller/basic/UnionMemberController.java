package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.basic.UnionMemberConstant;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 联盟成员列表 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@RestController
@RequestMapping("/unionMember")
public class UnionMemberController {
    private Logger logger = LoggerFactory.getLogger(UnionMemberController.class);

    @Autowired
    private IUnionMemberService unionMemberService;

    /**
     * 根据listType进行请求分类：
     * (1)当listType为请求盟员列表时，即值为UnionMemberConstant.LIST_TYPE_MEMBER，根据联盟id和session中的bus_id获取盟员列表信息，并支持根据盟员名称enterpriseName进行模糊查询；
     * (2)当listType为请求退盟申请列表时，即值为UnionMemberConstant.LIST_TYPE_OUT，根据联盟id获取退盟申请列表信息，并根据outStatus进行退盟状态分类查询
     * @param request
     * @param response
     * @param page
     * @param unionId
     * @param listType
     * @param enterpriseName
     * @param outStatus 当listType=UnionMemberConstant.LIST_TYPE_OUT时，不能为空
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listUnionMember(HttpServletRequest request, HttpServletResponse response, Page page
            , @RequestParam(name = "unionId", required = true)Integer unionId
            , @RequestParam(name = "listType", required = true)Integer listType
            , @RequestParam(name = "enterpriseName", required = false) String enterpriseName
            , @RequestParam(name = "outStatus", required = false) Integer outStatus) {
        Page result = null;
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser == null) {
                throw new Exception("UnionMemberController.listUnionMember():无法获取登录用户的信息!");
            }
            switch (listType) {
                case UnionMemberConstant.LIST_TYPE_MEMBER:
                    result = this.unionMemberService.listMember(page, unionId, busUser.getId(), enterpriseName);
                    break;
                case UnionMemberConstant.LIST_TYPE_OUT:
                    result = this.unionMemberService.listOut(page, unionId, outStatus);
                    break;
                default:
                    throw new Exception("不支持的查询类型listType:" + listType);
            }

        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }
}
