package com.gt.union.brokerage.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.bean.session.Member;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.brokerage.service.IUnionH5BrokerageService;
import com.gt.union.common.amqp.entity.PhoneMessage;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.controller.MemberAuthorizeOrLoginController;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.verifier.entity.UnionVerifier;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
@RestController
@RequestMapping("/unionH5Brokerage")
public class UnionH5BrokerageController extends MemberAuthorizeOrLoginController{

	private Logger logger = Logger.getLogger(UnionH5BrokerageController.class);

	@Autowired
	private IUnionH5BrokerageService unionH5BrokerageService;

	@Autowired
	private WxPayService wxPayService;

	@Autowired
	private IUnionMainService unionMainService;

	@Autowired
	private SmsService smsService;


	@Value("${wx.duofen.busId}")
	private Integer duofenBusId;

	@Value("${union.url}")
	private String unionUrl;

	@Autowired
	private RedisCacheUtil redisCacheUtil;


	/**
	 * 佣金平台登录
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation(value = "佣金平台登录", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String login(HttpServletRequest request, HttpServletResponse response, @ApiParam(name="type", value = "登录类型 1：商家账号 2：手机和验证码", required = true) @RequestParam("type") Integer type
									,@ApiParam(name="username", value = "商家账号", required = false) @RequestParam(name = "username", required = false) String username
									,@ApiParam(name="userpwd", value = "商家账号密码", required = false) @RequestParam(name = "userpwd", required = false) String userpwd
									,@ApiParam(name="phone", value = "手机号", required = false) @RequestParam(name = "phone", required = false) String phone
									,@ApiParam(name="code", value = "手机验证码", required = false) @RequestParam(name = "code", required = false) String code) {
		logger.info("进入登录,用户名：" + username);
		logger.info("进入登录,用手机号：" + phone);
		try {
			unionH5BrokerageService.checkLogin(type, username, userpwd, phone, code, request);
			return GTJsonResult.instanceSuccessMsg().toString();
		} catch (BaseException e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg().toString();
		}
	}


	@ApiOperation(value = "获取佣金平台手机登录验证码", notes = "获取佣金平台手机登录验证码", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/phone/{phone}", produces = "application/json;charset=UTF-8",method = RequestMethod.GET)
	public String getCodeByPhone(HttpServletRequest request, HttpServletResponse response
			, @ApiParam(name="phone", value = "手机号", required = true) @PathVariable String phone) {
		try {
			BusUser user = SessionUtils.getLoginUser(request);
			//生成验证码
			String code = RandomKit.getRandomString(6, 0);
			Integer busId = user.getId();
			if (user.getPid() != null && user.getPid() != 0) {
				busId = user.getPid();
			}
			if (CommonUtil.isNotEmpty(phone)) {
				PhoneMessage phoneMessage = new PhoneMessage(busId,phone,"佣金平台手机登录验证码:" + code);
				Map param = new HashMap<String,Object>();
				param.put("reqdata",phoneMessage);
				if(smsService.sendSms(param) == 0){
					return GTJsonResult.instanceErrorMsg("发送失败").toString();
				}
				String phoneKey = RedisKeyUtil.getBrokeragePhoneKey(phone);
				redisCacheUtil.set(phoneKey , code, 300l);
				return GTJsonResult.instanceSuccessMsg(code).toString();
			}
			return GTJsonResult.instanceErrorMsg("手机号不能为空").toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

	/**
	 * 佣金平台首页
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation(value = "佣金平台首页", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/index", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String index(HttpServletRequest request, HttpServletResponse response) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);

			double sumPay = unionH5BrokerageService.getSumInComeUnionBrokerage(busUser.getId());//我收入的总佣金（已支付）
			double sumWithdrawals = unionH5BrokerageService.getSumWithdrawalsUnionBrokerage(busUser.getId());//我已提现的总佣金（已支付）
			double ableGet = BigDecimalUtil.subtract(sumPay,sumWithdrawals).doubleValue();//可提现
			double unPay = unionH5BrokerageService.getSumUnPayUnionBrokerage(null,busUser.getId());//联盟中我未支付给别人之和
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("sumPay",sumPay);
			data.put("ableGet",ableGet);
			data.put("unPay",unPay);
			return GTJsonResult.instanceSuccessMsg(data).toString();
		}  catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg().toString();
		}
	}

	/**
	 * 我要提现页
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation(value = "我要提现页", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/withdrawals", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String withdrawals(HttpServletRequest request, HttpServletResponse response) {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			double sumPay = unionH5BrokerageService.getSumInComeUnionBrokerage(busUser.getId());//我收入的总佣金（已支付）
			double divideSum = unionH5BrokerageService.getSumInComeUnionBrokerageByType(busUser.getId(), 1);//售卡所得佣金总和
			double opportunitySum = unionH5BrokerageService.getSumInComeUnionBrokerageByType(busUser.getId(), 2);//商机所得佣金总和
			double sumWithdrawals = unionH5BrokerageService.getSumWithdrawalsUnionBrokerage(busUser.getId());//我已提现的总佣金（已支付）
			double sumUnCome = unionH5BrokerageService.getUnComeUnionBrokerage(busUser.getId(), null);//我未收佣金之和
			double ableGet = BigDecimalUtil.subtract(sumPay,sumWithdrawals).doubleValue();//可提现
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("ableGet",ableGet);//可提现金额
			data.put("divideSum",divideSum);//售卡佣金总和
			data.put("opportunitySum",opportunitySum);//商机佣金总和
			data.put("sumWithdrawals",sumWithdrawals);//已提现金额总和
			data.put("sumUnCome",sumUnCome);//未收的佣金之和
			return GTJsonResult.instanceSuccessMsg(data).toString();
		}  catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg().toString();
		}
	}


	@ApiOperation(value = "获取提现记录列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/withdrawals/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String withdrawalsList(Page page, HttpServletRequest request){
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			Page result = unionH5BrokerageService.listWithdrawals(page, user.getId());
			return GTJsonResult.instanceSuccessMsg(result).toString();
		} catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "佣金提现", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/withdrawals", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String withdrawals(HttpServletRequest request,
				  @ApiParam(name = "fee", value = "提现金额" ,required = true) @RequestParam(value = "fee", required = true) Double fee,
				  @ApiParam(name = "url", value = "登录授权后回调的url" ,required = true) @RequestParam(value = "url", required = true) String url){
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			Member member = SessionUtils.getLoginMember(request);
			if(CommonUtil.isEmpty(member)){
				String redirectUrl = this.authorizeMemberWx(request,unionUrl + url);
				return GTJsonResult.instanceErrorMsg("登录授权",redirectUrl).toString();
			}
			if(!member.getBusid().equals(duofenBusId)){
				String redirectUrl = this.authorizeMemberWx(request,unionUrl + url);
				return GTJsonResult.instanceErrorMsg("登录授权",redirectUrl).toString();
			}
			UnionVerifier verifier = com.gt.union.common.util.SessionUtils.getVerifier(request);
			int result = wxPayService.enterprisePayment(member.getBusid(), member.getId(), member.getOpenid(), fee, verifier == null ? null : verifier.getId());
			if(result == 1){
				return GTJsonResult.instanceSuccessMsg().toString();
			}else {
				return GTJsonResult.instanceErrorMsg("提现失败").toString();
			}
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}


	@ApiOperation(value = "获取未支付给别人的佣金列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unPay/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String unPayList(Page page, HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id，可以为空") @RequestParam(value = "unionId", required = false) Integer unionId){
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			Page result = unionH5BrokerageService.listUnPayUnionBrokerage(page, user.getId(),unionId );
			return GTJsonResult.instanceSuccessMsg(result).toString();
		}  catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "未支付给别人的佣金之和", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unPaySum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String unPaySum(HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id，可以为空") @RequestParam(value = "unionId", required = false) Integer unionId){
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			double unPaySum = unionH5BrokerageService.getSumUnPayUnionBrokerage(unionId, user.getId());
			return GTJsonResult.instanceSuccessMsg(unPaySum).toString();
		}  catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}


	@ApiOperation(value = "获取已支付给别人的佣金列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/pay/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String payList(Page page, HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id，可以为空") @RequestParam(value = "unionId", required = false) Integer unionId){
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			Page result = unionH5BrokerageService.listPayUnionBrokerage(page, user.getId(),unionId );
			return GTJsonResult.instanceSuccessMsg(result).toString();
		}  catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "已支付给别人的佣金之和", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/paySum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String paySum(HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id，可以为空") @RequestParam(value = "unionId", required = false) Integer unionId){
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			double paySum = unionH5BrokerageService.getSumPayUnionBrokerage(unionId, user.getId());
			return GTJsonResult.instanceSuccessMsg(paySum).toString();
		}  catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}


	@ApiOperation(value = "未收取别人的佣金列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unCome/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String unComeList(Page page, HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id，可以为空") @RequestParam(value = "unionId", required = false) Integer unionId){
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			Page result = unionH5BrokerageService.listUnComeUnionBrokerage(page, user.getId(),unionId );
			return GTJsonResult.instanceSuccessMsg(result).toString();
		}  catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "未收取别人的佣金之和", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unComeSum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String unComeSum(HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id，可以为空") @RequestParam(value = "unionId", required = false) Integer unionId){
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			double unComeSum = unionH5BrokerageService.getUnComeUnionBrokerage(user.getId(), unionId);
			return GTJsonResult.instanceSuccessMsg(unComeSum).toString();
		}  catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}


	@ApiOperation(value = "佣金明细推荐佣金列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/opportunity/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String opportunityList(Page page, HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id，可以为空") @RequestParam(value = "unionId", required = false) Integer unionId){
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			Page result = unionH5BrokerageService.listOpportunityPayToMe(page, user.getId(),unionId);
			return GTJsonResult.instanceSuccessMsg(result).toString();
		}  catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}


	@ApiOperation(value = "佣金明细推荐佣金之和", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/opportunitySum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String opportunitySum(HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id，可以为空") @RequestParam(value = "unionId", required = false) Integer unionId){
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			double opportunitySum = unionH5BrokerageService.getOpportunitySumToMe(user.getId(), unionId);
			return GTJsonResult.instanceSuccessMsg(opportunitySum).toString();
		}  catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}


	@ApiOperation(value = "佣金明细售卡佣金列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/cardDivide/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String cardDivideList(Page page, HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id，可以为空") @RequestParam(value = "unionId", required = false) Integer unionId){
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			Page result = unionH5BrokerageService.listCardDivide(page, user.getId(),unionId);
			return GTJsonResult.instanceSuccessMsg(result).toString();
		}  catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}


	@ApiOperation(value = "佣金明细售卡佣金之和", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/cardDivideSum", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String cardDivideSum(HttpServletRequest request, @ApiParam(name = "unionId", value = "联盟id，可以为空") @RequestParam(value = "unionId", required = false) Integer unionId){
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			double cardDivideSum = unionH5BrokerageService.getCardDivideSum(user.getId(), unionId);
			return GTJsonResult.instanceSuccessMsg(cardDivideSum).toString();
		}  catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}


	/**
	 * 催促佣金
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation(value = "催促佣金", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/urge/{id}", method=RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String urge(HttpServletRequest request, HttpServletResponse response, @ApiParam(name = "id", value = "商机id", required = true) @PathVariable(value = "id", required = true) Integer id) {
		try {
			BusUser user = SessionUtils.getLoginUser(request);
			unionH5BrokerageService.urgeOpportunity(user.getId(),id);
			return GTJsonResult.instanceSuccessMsg().toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			e.printStackTrace();
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}


	@ApiOperation(value = "支付佣金", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/onePay/{id}", method=RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String payOne(HttpServletRequest request, HttpServletResponse response, @ApiParam(name = "id", value = "商机id", required = true) @PathVariable(value = "id", required = true) Integer id
							,@ApiParam(name = "url", value = "回调的url" ,required = true) @RequestParam(value = "url", required = true) String url) {
		try {
			BusUser user = SessionUtils.getLoginUser(request);
			Member member = SessionUtils.getLoginMember(request);
			if(CommonUtil.isEmpty(member)){
				String redirectUrl = this.authorizeMemberWx(request,"http://union.duofee.com" + url);
				return GTJsonResult.instanceErrorMsg("登录授权",redirectUrl).toString();
			}
			if(!member.getBusid().equals(duofenBusId)){
				String redirectUrl = this.authorizeMemberWx(request,unionUrl + url);
				return GTJsonResult.instanceErrorMsg("登录授权",redirectUrl).toString();
			}
			String payUrl = unionH5BrokerageService.payOpportunity(user.getId(),id, url, member.getId());
			return GTJsonResult.instanceSuccessMsg("支付地址", payUrl).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			e.printStackTrace();
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}


	@ApiOperation(value = "获取一键支付佣金金额", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/allPay", method=RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String getPayAll(HttpServletRequest request, HttpServletResponse response, @ApiParam(name = "unionId", value = "联盟id", required = false) @RequestParam(value = "unionId", required = false) Integer unionId) {
		try {
			BusUser user = SessionUtils.getLoginUser(request);
			double money = unionH5BrokerageService.getPayAllOpportunitySum(user.getId(), unionId);
			return GTJsonResult.instanceSuccessMsg(money).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			e.printStackTrace();
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "一键支付佣金", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/allPay", method=RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String payAll(HttpServletRequest request, HttpServletResponse response, @ApiParam(name = "unionId", value = "联盟id", required = false) @RequestParam(value = "unionId", required = false) Integer unionId
							,@ApiParam(name = "fee", value = "支付金额", required = true) @RequestParam(value = "fee", required = true) Double fee
							,@ApiParam(name = "url", value = "回调的url" ,required = true) @RequestParam(value = "url", required = true) String url) {
		try {
			BusUser user = SessionUtils.getLoginUser(request);
			Member member = SessionUtils.getLoginMember(request);
			if(CommonUtil.isEmpty(member)){
				String redirectUrl = this.authorizeMemberWx(request,unionUrl + url);
				return GTJsonResult.instanceErrorMsg("登录授权",redirectUrl).toString();
			}
			if(!member.getBusid().equals(duofenBusId)){
				String redirectUrl = this.authorizeMemberWx(request,unionUrl + url);
				return GTJsonResult.instanceErrorMsg("登录授权",redirectUrl).toString();
			}
			String payUrl = unionH5BrokerageService.payAllOpportunity(user.getId(), unionId, fee, url, member.getId());
			return GTJsonResult.instanceSuccessMsg("支付地址",payUrl).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			e.printStackTrace();
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}


	@ApiOperation(value = "获取联盟列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionList", method=RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String payOne(HttpServletRequest request, HttpServletResponse response) {
		try {
			BusUser user = SessionUtils.getLoginUser(request);
			List<UnionMain> list = unionMainService.listWriteByBusId(user.getId());
			return GTJsonResult.instanceSuccessMsg(list).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			e.printStackTrace();
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}


	@RequestMapping(value = "/79B4DE7C/paymentOneSuccess/{Encrypt}", method = RequestMethod.POST)
	public String paymentOneOpportunitySuccess(HttpServletRequest request, HttpServletResponse response
			, @PathVariable(name = "Encrypt", required = true) String encrypt , @RequestBody Map<String,Object> param) {
		Map<String,Object> data = new HashMap<String,Object>();
		try {
			logger.info("商机佣金支付成功，Encrypt------------------" + encrypt);
			logger.info("支付成功回调参数" + JSON.toJSONString(param));
			logger.info("商机佣金支付成功，orderNo------------------" + param.get("out_trade_no"));
			UnionVerifier verifier = com.gt.union.common.util.SessionUtils.getVerifier(request);
			unionH5BrokerageService.paymentOneOpportunitySuccess(encrypt, param.get("out_trade_no").toString(), verifier == null ? null : verifier.getId());
			data.put("code",0);
			data.put("msg","成功");
			return JSON.toJSONString(data);
		} catch (BaseException e) {
			logger.error("商机佣金支付成功后，产生错误：" + e);
			data.put("code",-1);
			data.put("msg",e.getErrorMsg());
			return JSON.toJSONString(data);
		} catch (Exception e) {
			logger.error("商机佣金支付成功后，产生错误：" + e);
			data.put("code",-1);
			data.put("msg","失败");
			return JSON.toJSONString(data);
		}
	}

	@RequestMapping(value = "/79B4DE7C/paymentAllSuccess/{Encrypt}", method = RequestMethod.POST)
	public String payAllOpportunitySuccess(HttpServletRequest request, HttpServletResponse response
			, @PathVariable(name = "Encrypt", required = true) String encrypt, @RequestBody Map<String,Object> param) {
		Map<String,Object> data = new HashMap<String,Object>();
		try {
			logger.info("商机佣金支付成功，Encrypt------------------" + encrypt);
			logger.info("支付成功回调参数" + JSON.toJSONString(param));
			logger.info("商机佣金支付成功，orderNo------------------" + param.get("out_trade_no"));
			UnionVerifier verifier = com.gt.union.common.util.SessionUtils.getVerifier(request);
			unionH5BrokerageService.payAllOpportunitySuccess(encrypt, param.get("out_trade_no").toString(), verifier == null ? null : verifier.getId());
			data.put("code",0);
			data.put("msg","成功");
			return JSON.toJSONString(data);
		} catch (BaseException e) {
			logger.error("商机佣金支付成功后，产生错误：" + e);
			data.put("code",-1);
			data.put("msg",e.getErrorMsg());
			return JSON.toJSONString(data);
		} catch (Exception e) {
			logger.error("商机佣金支付成功后，产生错误：" + e);
			data.put("code",-1);
			data.put("msg","失败");
			return JSON.toJSONString(data);
		}
	}

}
