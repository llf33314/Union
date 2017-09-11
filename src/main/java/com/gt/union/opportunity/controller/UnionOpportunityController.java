package com.gt.union.opportunity.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.entity.BusUser;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.opportunity.service.IUnionOpportunityService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商机推荐 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionOpportunity")
public class UnionOpportunityController {

	private Logger logger = Logger.getLogger(UnionOpportunityController.class);

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Value("${wxmp.url}")
	private String wxmpUrl;

	@Autowired
	private IUnionOpportunityService unionOpportunityService;

	@ApiOperation(value = "查询我的商机信息", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/toMy", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String listToMy(HttpServletRequest request, Page page
			, @ApiParam(name = "unionId", value = "所属联盟id", required = false)
								@RequestParam(name = "unionId", required = false) Integer unionId
			, @ApiParam(name = "isAccept", value = "商机状态，1为未处理，2为受理，3为拒绝，当勾选多个时用英文字符的分号拼接，如=1;2", required = false)
								@RequestParam(name = "isAccept", required = false) String isAccept
			, @ApiParam(name = "clientNname", value = "顾客姓名，模糊查询", required = false)
								@RequestParam(name = "clientName", required = false) String clientName
			, @ApiParam(name = "clientPhone", value = "顾客电话，模糊查询", required = false)
								@RequestParam(name = "clientPhone", required = false) String clientPhone ) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			Integer busId = busUser.getId();
			if(busUser.getPid() != null && busUser.getPid() != 0){
				busId = busUser.getPid();
			}
			Page result = this.unionOpportunityService.listToMy(page, busId, unionId, isAccept, clientName, clientPhone);
			return GTJsonResult.instanceSuccessMsg(result).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "查询我推荐的商机信息", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/fromMy", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String listFromMy(HttpServletRequest request, Page page
			, @ApiParam(name = "unionId", value = "所属联盟id", required = false)
								  @RequestParam(name = "unionId", required = false) Integer unionId
			, @ApiParam(name = "isAccept", value = "商机状态，1为未处理，2为受理，3为拒绝，当勾选多个时用英文字符的分号拼接，如=1;2", required = false)
								 @RequestParam(name = "isAccept", required = false) String isAccept
			, @ApiParam(name = "clientNname", value = "顾客姓名，模糊查询", required = false)
								 @RequestParam(name = "clientName", required = false) String clientName
			, @ApiParam(name = "clientPhone", value = "顾客电话，模糊查询", required = false)
								 @RequestParam(name = "clientPhone", required = false) String clientPhone ) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			Integer busId = busUser.getId();
			if(busUser.getPid() != null && busUser.getPid() != 0){
				busId = busUser.getPid();
			}
			Page result = this.unionOpportunityService.listFromMy(page, busId, unionId, isAccept, clientName, clientPhone);
			return GTJsonResult.instanceSuccessMsg(result).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "查询我的佣金收入信息", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/brokerage/fromBusId", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String listBrokerageByFromBusId(HttpServletRequest request, Page page
			, @ApiParam(name = "toBusId", value = "佣金消费盟员id", required = false)
										   @RequestParam(name = "toBusId", required = false) Integer toBusId
			, @ApiParam(name = "unionId", value = "所属联盟id", required = false)
										   @RequestParam(name = "unionId", required = false) Integer unionId
			, @ApiParam(name = "isConfirm", value = "佣金结算状态，0为未处理，1为受理，2为拒绝，当勾选多个时用英文字符的分号拼接，如=1;2", required = false)
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
			return GTJsonResult.instanceErrorMsg(LIST_BROKERAGE_FROMBUSID, e.getMessage(), CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "查询我需支付的佣金信息", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/brokerage/toBusId", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String listBrokerageByToBusId(HttpServletRequest request, Page page
			, @ApiParam(name = "fromBusId", value = "佣金消费盟员id", required = false)
										 @RequestParam(name = "fromBusId", required = false) Integer fromBusId
			, @ApiParam(name = "unionId", value = "所属联盟id", required = false)
										 @RequestParam(name = "unionId", required = false) Integer unionId
			, @ApiParam(name = "isConfirm", value = "佣金结算状态，0为未处理，1为受理，2为拒绝，当勾选多个时用英文字符的分号拼接，如=1;2", required = false)
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
			return GTJsonResult.instanceErrorMsg(LIST_BROKERAGE_TOBUSID, e.getMessage(), CommonConstant.OPERATE_ERROR).toString();
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
			return GTJsonResult.instanceErrorMsg(LIST_PAYDETAIL_FROMBUSID, e.getMessage(), CommonConstant.OPERATE_ERROR).toString();
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
			return GTJsonResult.instanceErrorMsg(LIST_PAYDETAILPARTICULAR_UNIONID_FROMBUSID_TOBUSID, e.getMessage(), CommonConstant.OPERATE_ERROR).toString();
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
			return GTJsonResult.instanceErrorMsg(GET_STATISTIC_DATA, "", CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "商机审核", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/{id}/isAcceptance/{isAcceptance}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public String updateByIdAndIsAcceptance(HttpServletRequest request
			, @ApiParam(name = "id", value = "商机id", required = true) @PathVariable("id") Integer id
			, @ApiParam(name = "isAccept", value = "审核状态 1：接受 2：拒绝", required = true) @PathVariable Integer isAccept
			, @ApiParam(name = "acceptPrice", value = "受理价格，拒绝时可传0", required = true) @RequestParam Double acceptPrice){
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			unionOpportunityService.updateByIdAndIsAccept(busId, id, isAccept, acceptPrice);
			return GTJsonResult.instanceSuccessMsg().toString();
		}catch (BaseException e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		}catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
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
			return GTJsonResult.instanceErrorMsg(SAVE, e.getMessage(), CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "统计我的所有已被接受的，目前未支付或已支付的商机佣金总额", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/isConfirm/{isConfirm}/sum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String sumBusinessPriceByFromBusIdAndIsConfirm(HttpServletRequest request
			, @ApiParam(name = "isConfirm", value = "是否已支付：0未支付，1已支付", required = true) @PathVariable("isConfirm") Integer isConfirm) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			Double businessPriceSum = this.unionBusinessRecommendService.sumBusinessPriceByFromBusIdAndIsConfirm(busUser.getId(), isConfirm);
			return GTJsonResult.instanceSuccessMsg(businessPriceSum).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(SUM_BUSINESSPRICE_FROMBUSID_ISCONFIRM, e.getMessage(), CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "统计我的所有已被接受的，目前未支付或已支付的，指定联盟的商机佣金总额", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionId/{unionId}/isConfirm/{isConfirm}/sum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String sumBusinessPriceByUnionIdAndFromBusIdAndIsConfirm(HttpServletRequest request
			, @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId
			, @ApiParam(name = "isConfirm", value = "是否已支付：0未支付，1已支付", required = true) @PathVariable("isConfirm") Integer isConfirm) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			Double businessPriceSum = this.unionBusinessRecommendService.sumBusinessPriceByUnionIdAndFromBusIdAndIsConfirm(unionId, busUser.getId(), isConfirm);
			return GTJsonResult.instanceSuccessMsg(businessPriceSum).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(SUM_BUSINESSPRICE_UNIONID_FROMBUSID_ISCONFIRM, e.getMessage(), CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "统计我的所有已被接受的，但由于接收方退盟造成的坏账状态下的商机佣金总额", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/badDebt/sum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String sumBusinessPriceByFromBusIdInBadDebt(HttpServletRequest request) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			Double businessPriceSum = this.unionBusinessRecommendService.sumBusinessPriceByFromBusIdInBadDebt(busUser.getId());
			return GTJsonResult.instanceSuccessMsg(businessPriceSum).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(SUM_BUSINESSPRICE_FROMBUSID_BADDEBT, e.getMessage(), CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "分页获取我的所有坏账明细", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/badDebt", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String pageMapByFromBusIdInBadDebt(HttpServletRequest request, Page page) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			Page result = this.unionBusinessRecommendService.pageMapByFromBusIdInBadDebt(page, busUser.getId());
			return GTJsonResult.instanceSuccessMsg(result).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(PAGE_MAP_FROMBUSID_BADDEBT, e.getMessage(), CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "分页获取我在某联盟的所有坏账明细", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionId/{unionId}/badDebt", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String pageMapByUnionIdAndFromBusIdInBadDebt(HttpServletRequest request, Page page
			, @ApiParam(name = "unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			Page result = this.unionBusinessRecommendService.pageMapByUnionIdAndFromBusIdInBadDebt(page, unionId, busUser.getId());
			return GTJsonResult.instanceSuccessMsg(result).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(PAGE_MAP_UNIONID_FROMBUSID_BADDEBT, e.getMessage(), CommonConstant.OPERATE_ERROR).toString();
		}
	}



	@RequestMapping(value = "/79B4DE7C/paymentSuccess/{recommendEncrypt}/{only}")
	public void payUnionBusinessRecommendSuccess(HttpServletRequest request, HttpServletResponse response,@PathVariable(name = "recommendEncrypt", required = true) String recommendEncrypt, @PathVariable(name = "only", required = true) String only) {
		try {
			logger.info("商机佣金支付成功，订单recommendEncrypt------------------"+recommendEncrypt);
			logger.info("商机佣金支付成功，only------------------"+only);
			unionBusinessRecommendService.payUnionBusinessRecommendSuccess(recommendEncrypt, only);
		} catch (Exception e) {
			logger.error("商机佣金支付成功后，产生错误：" + e);
		}
	}

	@ApiOperation(value = "生成商机推荐支付订单二维码", produces = "application/json;charset=UTF-8")
	@SysLogAnnotation(op_function = "2", description = "生成商机推荐支付二维码")
	@RequestMapping(value = "/qrCode/{ids}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String payBusinessRecommendQRCode(HttpServletRequest request, HttpServletResponse response, @ApiParam(name="ids", value = "商机ids", required = true) @PathVariable("ids") String ids) {
		try {
			BusUser user = SessionUtils.getLoginUser(request);
			if(CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0){
				throw new BusinessException(PAY_BUSINESS_RECOMMEND_QRCODE, "", CommonConstant.UNION_BUS_PARENT_MSG);
			}
			Map<String,Object> data = unionBusinessRecommendService.payBusinessRecommendQRCode(user.getId(), ids);
			StringBuilder sb = new StringBuilder("?");
			sb.append("totalFee="+data.get("totalFee"));
			sb.append("&model="+data.get("model"));
			sb.append("&busId="+data.get("busId"));
			sb.append("&appidType="+data.get("appidType"));
			sb.append("&appid=" + data.get("appid"));
			sb.append("&orderNum="+data.get("orderNum"));
			sb.append("&desc="+data.get("desc"));
			sb.append("&isreturn="+data.get("isreturn"));
			sb.append("&notifyUrl="+data.get("notifyUrl"));
			sb.append("&isSendMessage="+data.get("isSendMessage"));
			sb.append("&payWay="+data.get("payWay"));
			sb.append("&sourceType="+data.get("sourceType"));
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("url",wxmpUrl + "/pay/B02A45A5/79B4DE7C/createPayQR.do" + sb.toString());
			result.put("only",data.get("only"));
			return GTJsonResult.instanceSuccessMsg(result).toString();
		} catch (Exception e) {
			logger.error("生成商机推荐支付订单二维码错误：" + e);
			return GTJsonResult.instanceErrorMsg(PAY_BUSINESS_RECOMMEND_QRCODE, e.getMessage(), CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "获取商机佣金支付状态", produces = "application/json;charset=UTF-8")
	@RequestMapping(value="status/{only}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String getStatus(HttpServletRequest request, HttpServletResponse response, @PathVariable("only")String only) throws Exception{
		logger.info("获取商机佣金支付状态：" + only);
		try {
			String statusKey = RedisKeyUtil.getCreateUnionPayStatusKey(only);
			String paramKey = RedisKeyUtil.getCreateUnionPayParamKey(only);
			Object status = redisCacheUtil.get(statusKey);
			if(CommonUtil.isEmpty(status)){//订单超时
				status = CommonConstant.USER_ORDER_STATUS_004;
			}
			if(CommonConstant.USER_ORDER_STATUS_003.equals(status)){//订单支付成功
				redisCacheUtil.remove(statusKey);
				redisCacheUtil.remove(paramKey);
			}

			if(CommonConstant.USER_ORDER_STATUS_005.equals(status)){//订单支付失败
				redisCacheUtil.remove(statusKey);
				redisCacheUtil.remove(paramKey);
			}
			return GTJsonResult.instanceSuccessMsg(status).toString();
		} catch (Exception e) {
			logger.error("获取商机佣金支付错误：" + e);
			return GTJsonResult.instanceErrorMsg(GET_STATUS, e.getMessage(), ExceptionConstant.SYS_ERROR).toString();
		}
	}
}
