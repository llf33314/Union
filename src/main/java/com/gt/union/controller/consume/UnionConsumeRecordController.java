package com.gt.union.controller.consume;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.consume.UnionConsumeRecordConstant;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    //TODO RESTFUL
    private static final String LIST_UNION_CONSUME_RECORD = "UnionConsumeRecordController.listUnionConsumeRecord()";
    private Logger logger = LoggerFactory.getLogger(UnionConsumeRecordController.class);

    @Autowired
    private IUnionConsumeRecordService unionConsumeRecordService;

    @RequestMapping(value = ""
            , method = RequestMethod.GET
            , produces = "application/json;charset=UTF-8")
    @ApiOperation(value = "查询消费核销记录"
            , notes = "根据listType进行分类查询，支持本店消费和它店消费的核销记录查询"
            , produces = "application/json;charset=UTF-8")
    public String listUnionConsumeRecord(HttpServletRequest request, Page page
            , @ApiParam(name = "listType", value = "值为1时，查询本店消费记录；值为2时，查询它店消费记录", required = true)
             @RequestParam(name = "listType", required = true) Integer listType
            , @ApiParam(name = "unionId", value = "联盟id", required = true)
             @RequestParam(name = "unionId", required = true) Integer unionId
            , @ApiParam(name = "busId", value = "来源", required = false)
             @RequestParam(name = "busId", required = false) Integer busId
            , @ApiParam(name = "cardNo", value = "联盟卡号，模糊查询", required = false)
             @RequestParam(name = "cardNo", required = false) String cardNo
            , @ApiParam(name = "phone", value = "手机号，模糊查询", required = false)
             @RequestParam(name = "phone", required = false) String phone
            , @ApiParam(name = "beginTime", value = "开始时间", required = false)
             @RequestParam(name = "beginTime", required = false) String beginTime
            , @ApiParam(name = "endTime", value = "结束时间", required = false)
             @RequestParam(name = "endTime", required = false) String endTIme){
        try {
            Page result = null;
            BusUser busUser = SessionUtils.getLoginUser(request);
            if (busUser == null) {
                throw new Exception("UnionConsumeRecordController.listUnionConsumeRecord()：无法通过session获取用户的信息!");
            }
            switch (listType) {
                case UnionConsumeRecordConstant.LIST_TYPE_MY:
                    result = this.unionConsumeRecordService.listMyConsumeRecord(page, unionId, busUser.getId(), busId, cardNo, phone, beginTime, endTIme);
                    break;
                case UnionConsumeRecordConstant.LIST_TYPE_OTHER:
                    result = this.unionConsumeRecordService.listOtherConsumeRecord(page, unionId, busUser.getId(), busId, cardNo, phone, beginTime, endTIme);
                    break;
                default:
                    throw new Exception("UnionConsumeRecordController.listUnionConsumeRecord()：不支持的查询类型(listType=" + listType + ")!");
            }
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_UNION_CONSUME_RECORD, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }
}
