package com.gt.union.controller.business;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.business.IUnionBusinessRecommendService;
import com.gt.union.service.common.IUnionValidateService;
import com.gt.union.vo.business.UnionBusinessRecommendVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟商家商机推荐 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@RestController
@RequestMapping("/unionBusinessRecommend")
public class UnionBusinessRecommendController {
	private static final String LIST_TOBUSID = "UnionBusinessRecommendController.listByToBusId()";
	private static final String LIST_FROMBUSID = "UnionBusinessRecommendController.listByFromBusId()";
	private static final String LIST_BROKERAGE_FROMBUSID = "UnionBusinessRecommendController.listBrokerageByFromBusId()";
	private static final String LIST_BROKERAGE_TOBUSID = "UnionBusinessRecommendController.listBrokerageByToBusId()";
	private static final String LIST_PAYDETAIL_FROMBUSID = "UnionBusinessRecommendController.listPayDetailByFromBusId()";
	private static final String LIST_PAYDETAILPARTICULAR_UNIONID_FROMBUSID_TOBUSID = "UnionBusinessRecommendController.listPayDetailParticularByUnionIdAndFromBusIdAndToBusId()";
	private static final String GET_STATISTIC_DATA = "UnionBusinessRecommendController.getStatisticData()";
	private static final String UPDATE_ID_ISACCEPTANCE = "UnionBusinessRecommendController.updateByIdAndIsAcceptance()";
	private static final String SAVE = "UnionBusinessRecommendController.save()";

	private Logger logger = Logger.getLogger(UnionBusinessRecommendController.class);

	@Autowired
	private IUnionBusinessRecommendService unionBusinessRecommendService;

	@Autowired
    private IUnionValidateService unionValidateService;

	@ApiOperation(value = "查询我的商机信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/toBusId", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String listByToBusId(HttpServletRequest request, Page page
            , @ApiParam(name = "unionId", value = "所属联盟id", required = false)
            @RequestParam(name = "unionId", required = false) Integer unionId
            , @ApiParam(name = "isAcceptance", value = "商机状态，当勾选多个时用英文字符的分号拼接，如=1;2", required = false)
            @RequestParam(name = "isAcceptance", required = false) String isAcceptance
            , @ApiParam(name = "userName", value = "顾客姓名，模糊查询", required = false)
            @RequestParam(name = "userName", required = false) String userName
            , @ApiParam(name = "userPhone", value = "顾客电话，模糊查询", required = false)
            @RequestParam(name = "userPhone", required = false) String userPhone ) {
	    try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Page result = this.unionBusinessRecommendService.listByToBusId(page, busUser.getId(), unionId, isAcceptance, userName, userPhone);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
	        logger.error("", e);
	        return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
	        logger.error("", e);
	        return GTJsonResult.instanceErrorMsg(LIST_TOBUSID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "查询我推荐的商机信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/fromBusId", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listByFromBusId(HttpServletRequest request, Page page
            , @ApiParam(name = "unionId", value = "所属联盟id", required = false)
            @RequestParam(name = "unionId", required = false) Integer unionId
            , @ApiParam(name = "isAcceptance", value = "商机状态，当勾选多个时用英文字符的分号拼接，如=1;2", required = false)
            @RequestParam(name = "isAcceptance", required = false) String isAcceptance
            , @ApiParam(name = "userName", value = "顾客姓名，模糊查询", required = false)
            @RequestParam(name = "userName", required = false) String userName
            , @ApiParam(name = "userPhone", value = "顾客电话，模糊查询", required = false)
            @RequestParam(name = "userPhone", required = false) String userPhone ) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Page result = this.unionBusinessRecommendService.listByFromBusId(page, busUser.getId(), unionId, isAcceptance, userName, userPhone);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_FROMBUSID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "查询我的佣金收入信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/brokerage/fromBusId", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listBrokerageByFromBusId(HttpServletRequest request, Page page
            , @ApiParam(name = "toBusId", value = "佣金消费盟员id", required = false)
            @RequestParam(name = "toBusId", required = false) Integer toBusId
            , @ApiParam(name = "unionId", value = "所属联盟id", required = false)
            @RequestParam(name = "unionId", required = false) Integer unionId
            , @ApiParam(name = "isConfirm", value = "佣金结算状态，当勾选多个时用英文字符的分号拼接，如=1;2", required = false)
            @RequestParam(name = "isConfirm", required = false) String isConfirm
            , @ApiParam(name = "userName", value = "顾客姓名，模糊查询", required = false)
            @RequestParam(name = "userName", required = false) String userName
            , @ApiParam(name = "userPhone", value = "顾客电话，模糊查询", required = false)
            @RequestParam(name = "userPhone", required = false) String userPhone ) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer fromBusId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != 0) {
                fromBusId = busUser.getPid();
            }
            Page result = this.unionBusinessRecommendService.listBrokerageByFromBusId(page, fromBusId, toBusId, unionId, isConfirm, userName, userPhone);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_BROKERAGE_FROMBUSID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "查询我需支付的佣金信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/brokerage/toBusId", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listBrokerageByToBusId(HttpServletRequest request, Page page
            , @ApiParam(name = "fromBusId", value = "佣金消费盟员id", required = false)
            @RequestParam(name = "fromBusId", required = false) Integer fromBusId
            , @ApiParam(name = "unionId", value = "所属联盟id", required = false)
            @RequestParam(name = "unionId", required = false) Integer unionId
            , @ApiParam(name = "isConfirm", value = "佣金结算状态，当勾选多个时用英文字符的分号拼接，如=1;2", required = false)
            @RequestParam(name = "isConfirm", required = false) String isConfirm
            , @ApiParam(name = "userName", value = "顾客姓名，模糊查询", required = false)
            @RequestParam(name = "userName", required = false) String userName
            , @ApiParam(name = "userPhone", value = "顾客电话，模糊查询", required = false)
            @RequestParam(name = "userPhone", required = false) String userPhone ) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer toBusId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != 0) {
                toBusId = busUser.getPid();
            }
            Page result = this.unionBusinessRecommendService.listBrokerageByToBusId(page, toBusId, fromBusId, unionId, isConfirm, userName, userPhone);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_BROKERAGE_TOBUSID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "查询支付明细信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/payDetail", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listPayDetailByFromBusId(HttpServletRequest request, Page page
            , @ApiParam(name = "unionId", value = "联盟id", required = false) @RequestParam(name = "unionId",  required = false) Integer unionId) {
	    try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer fromBusId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != 0) {
                fromBusId = busUser.getPid();
            }
            Page result = this.unionBusinessRecommendService.listPayDetailByFromBusId(page, fromBusId, unionId);
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
	        logger.error("", e);
	        return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
	        logger.error("", e);
	        return GTJsonResult.instanceErrorMsg(LIST_PAYDETAIL_FROMBUSID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "查询支付明细详情信息", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/payDetailParticular/toBusId/{toBusId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String listPayDetailParticularByUnionIdAndFromBusIdAndToBusId(HttpServletRequest request
            , @ApiParam(name = "toBusId", value = "佣金消费盟员id", required = true) @PathVariable("toBusId") Integer toBusId
            , @ApiParam(name = "unionId", value = "联盟id", required = true) @RequestParam(name = "unionId", required = true) Integer unionId) {
        try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Integer fromBusId = busUser.getId();
            if (busUser.getPid() != null && busUser.getPid() != 0) {
                fromBusId = busUser.getPid();
            }
            List<Map<String, Object>> dataList = this.unionBusinessRecommendService.listPayDetailParticularByUnionIdAndFromBusIdAndToBusId(unionId, fromBusId, toBusId);
            BigDecimal businessPriceSum = BigDecimal.valueOf(0.0);
            if (ListUtil.isNotEmpty(dataList)) {
                for (int i = 0, size = dataList.size(); i < size && dataList.get(i) != null; i++) {
                    Object businessPriceObj = dataList.get(i).get("businessPrice");
                    Double businessPrice = businessPriceObj == null ? Double.valueOf(0.0) : Double.valueOf(businessPriceObj.toString());
                    businessPriceSum = BigDecimalUtil.add(businessPriceSum, businessPrice);
                }
            }
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", dataList);
            resultMap.put("businessPriceSum", businessPriceSum);
            return GTJsonResult.instanceSuccessMsg(resultMap).toString();
        } catch (BaseException e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
            logger.error("", e);
            return GTJsonResult.instanceErrorMsg(LIST_PAYDETAILPARTICULAR_UNIONID_FROMBUSID_TOBUSID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

    @ApiOperation(value = "获取商机统计数据", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/unionId/{unionId}/statisticData", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getStatisticData(HttpServletRequest request
        , @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId) {
	    try {
            BusUser busUser = SessionUtils.getLoginUser(request);
            Map<String, Object> result = this.unionBusinessRecommendService.getStatisticData(unionId, busUser.getId());
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (BaseException e) {
	        logger.error("", e);
	        return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
        } catch (Exception e) {
	        logger.error("", e);
	        return GTJsonResult.instanceErrorMsg(GET_STATISTIC_DATA, "", ExceptionConstant.OPERATE_FAIL).toString();
        }
    }

	@ApiOperation(value = "商机审核", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/{id}/isAcceptance/{isAcceptance}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public String updateByIdAndIsAcceptance(HttpServletRequest request
			, @ApiParam(name = "id", value = "商机id", required = true) @PathVariable("id") Integer id
			, @ApiParam(name = "isAcceptance", value = "审核状态 1：接受 2：拒绝", required = true) @PathVariable Integer isAcceptance
            , @ApiParam(name = "acceptancePrice", value = "受理价格，拒绝时可传0", required = true) @RequestParam Double acceptancePrice){
		try{
            BusUser user = SessionUtils.getLoginUser(request);
			unionBusinessRecommendService.updateByIdAndIsAcceptance(user.getId(), id, isAcceptance, acceptancePrice);
			return GTJsonResult.instanceSuccessMsg().toString();
		}catch (BaseException e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		}catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(UPDATE_ID_ISACCEPTANCE, e.getMessage(),ExceptionConstant.OPERATE_FAIL).toString();
		}
	}

	@ApiOperation(value = "添加商机推荐", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String save(HttpServletRequest request
        , @ApiParam(name = "unionBusinessRecommendFormVO", value = "推荐的商机信息", required = true)
        @RequestBody @Valid UnionBusinessRecommendVO vo, BindingResult bindingResult){
		try{
		    this.unionValidateService.checkBindingResult(bindingResult);
            BusUser user = SessionUtils.getLoginUser(request);
            Integer busId = user.getId();
            if(CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0){//子账号
            	busId = user.getPid();
			}
			vo.setBusId(busId);
			unionBusinessRecommendService.save(vo);
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
