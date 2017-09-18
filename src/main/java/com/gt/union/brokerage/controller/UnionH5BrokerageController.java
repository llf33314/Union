package com.gt.union.brokerage.controller;

import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.brokerage.service.IUnionH5BrokerageService;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.IPKit;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/18 0018.
 */
@RestController
@RequestMapping("/unionH5Brokerage")
public class UnionH5BrokerageController {

	private Logger logger = Logger.getLogger(UnionH5BrokerageController.class);

	@Autowired
	private IUnionH5BrokerageService unionH5BrokerageService;

	/**
	 * 佣金平台登录页面
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView toLogin(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("merchants/trade/union/mobile/brokerage/login");
		return mv;
	}

	/**
	 * 佣金平台登录
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@ApiOperation(value = "佣金平台登录", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/login/type/{type}", method = RequestMethod.POST)
	public String login(HttpServletRequest request, HttpServletResponse response, @ApiParam(name="type", value = "登录类型 1：商家账号 2：手机和验证码", required = true) @PathVariable("type") Integer type
									,@ApiParam(name="username", value = "商家账号", required = false) @RequestParam(name = "username", required = false) String username
									,@ApiParam(name="userpwd", value = "商家账号密码", required = false) @RequestParam(name = "userpwd", required = false) String userpwd
									,@ApiParam(name="phone", value = "手机号", required = false) @RequestParam(name = "phone", required = false) String phone
									,@ApiParam(name="code", value = "手机验证码", required = false) @RequestParam(name = "code", required = false) String code) {
		logger.info("进入登录,用户名：" + username);
		logger.info("进入登录,用手机号：" + phone);
		try {
			String url = "/index";
			unionH5BrokerageService.checkLogin(type, username, userpwd, phone, code, request);
			return GTJsonResult.instanceSuccessMsg(null, url).toString();
		} catch (BaseException e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg().toString();
		}
	}

	/**
	 * 佣金平台首页
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/{busId}/index")
	public String index(HttpServletRequest request, HttpServletResponse response, @ApiParam(name="busId", value = "商家id", required = true) @PathVariable("busId") Integer busId) {
		/*BusUser busUser = SessionUtils.getLoginUser(request);
		if()
		unionH5BrokerageService.getSumPayUnionBrokerage(busUser.getId());
		params.put("busId", busUser.getId());
		double sumPay = unionBrokerageService.querySumPayUnionBrokerage(params);//已支付给我的佣金总和
		double getPay = unionBrokerageService.getWithdrawalsBrokerage(params);//我已提取佣金总和
		//params.put("delStatus", 0);//未退盟 未支付给我的
		//double unPay = unionBrokerageService.queryUnPaySumBrokerage(params);//未退盟未支付给我的佣金总和
		double unPay = unionBrokerageService.queryMyUnPaySumBrokerage(params);//我未支付金额总和
		params.put("delStatus", 1);//已退盟 未支付给我的（坏账）
		double badPay = unionBrokerageService.queryUnPaySumBrokerage(params);//已退盟未支付给我的佣金总和（坏账）
		double dividePay = unionBrokerageService.queryUserSumDivide(params);//售卡佣金之和
		BigDecimal bd1 = new BigDecimal(Double.toString(sumPay));
		BigDecimal bd2 = new BigDecimal(Double.toString(getPay));
		BigDecimal bd3 = new BigDecimal(Double.toString(dividePay));
		double ableGet = bd1.add(bd3).subtract(bd2).doubleValue();//可提取佣金总和
		sumPay = bd1.add(bd3).doubleValue();//推荐佣金+售卡佣金
		mv.addObject("sumPay", sumPay);
		mv.addObject("getPay", getPay);
		mv.addObject("ableGet", ableGet);
		mv.addObject("unPay", unPay);
		mv.addObject("badPay", badPay);*/
		return "";
	}

}
