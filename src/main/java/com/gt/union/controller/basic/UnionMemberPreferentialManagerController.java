package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.basic.UnionMemberPreferentialManagerConstant;
import com.gt.union.common.constant.basic.UnionMemberPreferentialServiceConstant;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionMemberPreferentialManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 盟员优惠项目管理 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@RestController
@RequestMapping("/unionMemberPreferentialManager")
public class UnionMemberPreferentialManagerController {
    private Logger logger = LoggerFactory.getLogger(UnionMemberPreferentialManagerController.class);

    @Autowired
    private IUnionMemberPreferentialManagerService unionMemberPreferentialManagerService;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listPreferentialManager(HttpServletRequest request, Page page
            , @RequestParam(name = "unionId", required = true) Integer unionId
            , @RequestParam(name = "listType", required = true) Integer listType
            , @RequestParam(name = "verifyStatus") Integer verifyStatus) {
        try {
            switch (listType) {
                case UnionMemberPreferentialManagerConstant.LIST_TYPE_CHECK:
                    Page pageData = this.unionMemberPreferentialManagerService.listPreferentialManager(page, unionId, verifyStatus);
                    int unCommitCount = this.unionMemberPreferentialManagerService.countPreferentialManager(unionId, UnionMemberPreferentialServiceConstant.VERIFY_STATUS_UNCOMMIT);
                    int unCheckCount = this.unionMemberPreferentialManagerService.countPreferentialManager(unionId, UnionMemberPreferentialServiceConstant.VERIFY_STATUS_UNCHECK);
                    int passCount = this.unionMemberPreferentialManagerService.countPreferentialManager(unionId, UnionMemberPreferentialServiceConstant.VERIFY_STATUS_PASS);
                    int failCount = this.unionMemberPreferentialManagerService.countPreferentialManager(unionId, UnionMemberPreferentialServiceConstant.VERIFY_STATUS_FAIL);
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("page", pageData);
                    resultMap.put("unCommitCount", unCommitCount);
                    resultMap.put("unCheckCount", unCheckCount);
                    resultMap.put("passCount", passCount);
                    resultMap.put("failCount", failCount);
                    return GTJsonResult.instanceSuccessMsg(resultMap).toString();
                case UnionMemberPreferentialManagerConstant.LIST_TYPE_MY:
                    BusUser busUser = SessionUtils.getLoginUser(request);
                    if (busUser == null) {
                        throw new Exception("UnionMemberPreferentialManagerController.listPreferentialManager():无法通过session获取用户的信息!");
                    }
                    Page result = this.unionMemberPreferentialManagerService.listMyPreferentialManager(page, unionId, busUser.getId());
                    return GTJsonResult.instanceSuccessMsg(result).toString();
                default:
                    throw new Exception("UnionMemberPreferentialManagerController.listPreferentialManager():不支持的查询类型listType(value=" + listType + ")!");
            }
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public String detailPreferentialManager(Page page, @PathVariable("id") Integer id
            , @RequestParam(name = "verifyStatus") Integer verifyStatus) {
        Map<String, Object> result = null;
        try {
            result = this.unionMemberPreferentialManagerService.detailPreferentialManager(page, id, verifyStatus);
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
        }
        return GTJsonResult.instanceSuccessMsg(result).toString();
    }

}
