package com.gt.union.controller.card;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.card.IUnionCardDivideRecordService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 商家售卡分成记录 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@RestController
@RequestMapping("/unionCardDivideRecord")
public class UnionCardDivideRecordController {
	private static final String PAGE_MAP_BUSID = "UnionCardDivideRecordController.pageMapByBusId()";
	private static final String PAGE_MAP_UNIONID_BUSID = "UnionCardDivideRecordController.pageMapByUnionIdAndBusId()";
	private static final String SUM_PRICE_BUSID = "UnionCardDivideRecordController.sumPriceByBusId()";
	private static final String SUM_PRICE_UNIONID_BUSID = "UnionCardDivideRecordController.sumPriceByUnionIdAndBusId()";

	private Logger logger = Logger.getLogger(UnionCardDivideRecordController.class);

	@Autowired
	private IUnionCardDivideRecordService unionCardDivideRecordService;

	@ApiOperation(value = "分页获取我的售卡佣金分成信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageMapByBusId(Page page, HttpServletRequest request) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Page result = unionCardDivideRecordService.pageMapByBusId(page, busUser.getId());
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(PAGE_MAP_BUSID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "分页获取我在某联盟的售卡佣金分成信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String pageMapByUnionIdAndBusId(Page page, HttpServletRequest request
        , @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Page result = unionCardDivideRecordService.pageMapByUnionIdAndBusId(page, unionId, busUser.getId());
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(PAGE_MAP_UNIONID_BUSID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "统计我的售卡佣金总额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/sum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String sumPriceByBusId(HttpServletRequest request) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Double priceSum = unionCardDivideRecordService.sumPriceByBusId(busUser.getId());
            return GTJsonResult.instanceSuccessMsg(priceSum).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(SUM_PRICE_BUSID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "统计我在某联盟的售卡佣金总额", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/sum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String sumPriceByUnionIdAndBusId(HttpServletRequest request
        , @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Double priceSum = unionCardDivideRecordService.sumPriceByUnionIdAndBusId(unionId, busUser.getId());
            return GTJsonResult.instanceSuccessMsg(priceSum).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(SUM_PRICE_UNIONID_BUSID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }
}
