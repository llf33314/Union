package com.gt.union.controller.consume;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.consume.IUnionConsumeRecordService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 会员消费 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@RestController
@RequestMapping("/unionConsumeRecord")
public class UnionConsumeRecordController {
    private static final String LIST_MY_UNIONID = "UnionConsumeRecordController.listMyByUnionId()";
    private static final String LIST_OTHER_UNIONID = "UnionConsumeRecordController.listOtherByUnionId()";
    private Logger logger = LoggerFactory.getLogger(UnionConsumeRecordController.class);

    @Autowired
    private IUnionConsumeRecordService unionConsumeRecordService;

    @ApiOperation(value = "查询本店消费核销记录", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/my/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listMyByUnionId(HttpServletRequest request, Page page
            , @ApiParam(name = "unionId", value = "联盟id", required = true)
            @PathVariable("unionId") Integer unionId
            , @ApiParam(name = "srcBusId", value = "来源", required = false)
            @RequestParam(name = "busId", required = false) Integer srcBusId
            , @ApiParam(name = "cardNo", value = "联盟卡号，模糊查询", required = false)
            @RequestParam(name = "cardNo", required = false) String cardNo
            , @ApiParam(name = "phone", value = "手机号，模糊查询", required = false)
            @RequestParam(name = "phone", required = false) String phone
            , @ApiParam(name = "beginTime", value = "开始时间", required = false)
            @RequestParam(name = "beginTime", required = false) String beginTime
            , @ApiParam(name = "endTime", value = "结束时间", required = false)
            @RequestParam(name = "endTime", required = false) String endTIme){
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Page result = this.unionConsumeRecordService.listMyByUnionId(page, unionId, busUser.getId(), srcBusId, cardNo, phone, beginTime, endTIme);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_MY_UNIONID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "查询它店消费核销记录", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/other/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listOtherByUnionId(HttpServletRequest request, Page page
            , @ApiParam(name = "unionId", value = "联盟id", required = true)
            @PathVariable("unionId") Integer unionId
            , @ApiParam(name = "srcBusId", value = "来源", required = false)
            @RequestParam(name = "busId", required = false) Integer srcBusId
            , @ApiParam(name = "cardNo", value = "联盟卡号，模糊查询", required = false)
            @RequestParam(name = "cardNo", required = false) String cardNo
            , @ApiParam(name = "phone", value = "手机号，模糊查询", required = false)
            @RequestParam(name = "phone", required = false) String phone
            , @ApiParam(name = "beginTime", value = "开始时间", required = false)
            @RequestParam(name = "beginTime", required = false) String beginTime
            , @ApiParam(name = "endTime", value = "结束时间", required = false)
            @RequestParam(name = "endTime", required = false) String endTIme){
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Page result = this.unionConsumeRecordService.listOtherByUnionId(page, unionId, busUser.getId(), srcBusId, cardNo, phone, beginTime, endTIme);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_OTHER_UNIONID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }
}
