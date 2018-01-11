package com.gt.union.wxapp.card.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.Member;
import com.gt.union.api.amqp.entity.PhoneMessage;
import com.gt.union.api.amqp.sender.PhoneMessageSender;
import com.gt.union.api.client.member.MemberService;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.pay.entity.PayParam;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.SmsCodeConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.wxapp.card.service.ITokenApiService;
import com.gt.union.wxapp.card.service.IWxAppCardService;
import com.gt.union.wxapp.card.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hongjiye
 * @time 2017-12-29 15:07
 **/
@Api(description = "微信小程序联盟卡")
@RestController
@RequestMapping("/union/wxAppCard/{version}")
public class WxAppCardController {

	@Autowired
	private IWxAppCardService wxAppCardService;

	@Autowired
	private PhoneMessageSender sender;

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Autowired
	private MemberService memberService;

	//-------------------------------------------------- get ----------------------------------------------------------

	@ApiOperation(value = "联盟卡-首页", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/index/{busId}/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String getIndexVO(HttpServletRequest request, Page page,
							 @ApiParam(value = "版本号", name = "version", required = true)
							 @PathVariable("version") String version,
							 @ApiParam(value = "商家id", name = "busId", required = true)
							 @PathVariable("busId") Integer busId,
							 @ApiParam(value = "粉丝用户id", name = "memberId", required = true) @PathVariable("memberId") Integer memberId) throws Exception {
		Member member = memberService.getById(memberId);
		IndexVO indexVO = wxAppCardService.getIndexVO(member == null ? null : member.getPhone(), busId);
		Page<UnionCardVO> result = (Page<UnionCardVO>) page;
		result = PageUtil.setRecord(result, indexVO.getCardList());
		return GtJsonResult.instanceSuccessMsg(result).toString();
	}

	@ApiOperation(value = "联盟卡-详情", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/cardDetail/{busId}/{unionId}/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String cardDetail(HttpServletRequest request,
							 @ApiParam(value = "版本号", name = "version", required = true)
							 @PathVariable("version") String version,
							 @ApiParam(value = "商家id", name = "busId", required = true)
							 @PathVariable("busId") Integer busId,
							 @ApiParam(value = "活动卡id，如果没有，则是折扣卡", name = "activityId", required = false)
							 @RequestParam(name = "activityId", required = false) Integer activityId,
							 @ApiParam(value = "联盟id", name = "unionId", required = true)
							 @PathVariable("unionId") Integer unionId, @ApiParam(value = "粉丝用户id", name = "memberId", required = true) @PathVariable("memberId") Integer memberId) throws Exception {
		Member member = memberService.getById(memberId);
		CardDetailVO result = wxAppCardService.getCardDetail(member == null ? null : member.getPhone(), busId, unionId, activityId);
		return GtJsonResult.instanceSuccessMsg(result).toString();
	}


	@ApiOperation(value = "联盟卡-详情-分页获取列表信息", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/cardDetail/list/{busId}/{unionId}/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String cardDetailList(HttpServletRequest request,
							 @ApiParam(value = "版本号", name = "version", required = true)
							 @PathVariable("version") String version,
							 @ApiParam(value = "商家id", name = "busId", required = true)
							 @PathVariable("busId") Integer busId,
							 @ApiParam(value = "活动卡id，如果没有，则是折扣卡", name = "activityId", required = false)
							 @RequestParam(name = "activityId", required = false) Integer activityId,
							 @ApiParam(value = "联盟id", name = "unionId", required = true)
							 @PathVariable("unionId") Integer unionId, @ApiParam(value = "粉丝用户id", name = "memberId", required = true) @PathVariable("memberId") Integer memberId, Page page) throws Exception {
		Member member = memberService.getById(memberId);
		Page<List<CardDetailListVO>> result = wxAppCardService.listCardDetailPage(member == null ? null : member.getPhone(), busId, unionId, activityId, page);
		return GtJsonResult.instanceSuccessMsg(result).toString();
	}


	@ApiOperation(value = "联盟卡-我的详情", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/37FD66FE/myCardDetail/{busId}/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String myCardDetail(HttpServletRequest request,
							   @ApiParam(value = "版本号", name = "version", required = true)
							   @PathVariable("version") String version,
							   @ApiParam(value = "商家id", name = "busId", required = true)
							   @PathVariable("busId") Integer busId, @ApiParam(value = "粉丝用户id", name = "memberId", required = true) @PathVariable("memberId") Integer memberId) throws Exception {
		Member member = memberService.getById(memberId);
		MyCardDetailVO myCardDetailVO = wxAppCardService.myCardDetail(member.getPhone());
		myCardDetailVO.setNickName(StringUtil.isEmpty(member.getNickname()) ? "未知用户" : member.getNickname());
		myCardDetailVO.setHeardImg(member.getHeadimgurl());
		return GtJsonResult.instanceSuccessMsg(myCardDetailVO).toString();
	}

	@ApiOperation(value = "联盟卡-我的详情-分页获取列表信息", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/37FD66FE/myCardDetail/list/{busId}/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String myCardDetail(HttpServletRequest request,
							   @ApiParam(value = "版本号", name = "version", required = true)
							   @PathVariable("version") String version,
							   @ApiParam(value = "商家id", name = "busId", required = true)
							   @PathVariable("busId") Integer busId,@ApiParam(value = "粉丝用户id", name = "memberId", required = true) @PathVariable("memberId") Integer memberId,
							   Page page) throws Exception {
		Member member = memberService.getById(memberId);
		Page result =  wxAppCardService.listMyCardPage(member.getPhone(), page);
		return GtJsonResult.instanceSuccessMsg(result).toString();
	}

	@ApiOperation(value = "联盟卡-消费记录", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/37FD66FE/myCardConsume/{busId}/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String myCardConsume(HttpServletRequest request,
								@ApiParam(value = "版本号", name = "version", required = true)
								@PathVariable("version") String version,
								@ApiParam(value = "商家id", name = "busId", required = true)
								@PathVariable("busId") Integer busId, @ApiParam(value = "粉丝用户id", name = "memberId", required = true) @PathVariable("memberId") Integer memberId,
								Page page) throws Exception {
		Member member = memberService.getById(memberId);
		Page result = wxAppCardService.pageConsumeByPhone(page, member.getPhone());
		return GtJsonResult.instanceSuccessMsg(result).toString();
	}


	@ApiOperation(value = "获取联盟卡二维码", notes = "获取联盟卡二维码", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/qr/cardNo", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
	public void qrCardNo(HttpServletRequest request, HttpServletResponse response,
						 @ApiParam(value = "版本号", name = "version", required = true)
						 @PathVariable("version") String version,
						 @ApiParam(name="cardNo", value = "联盟卡号", required = true)
						 @RequestParam("cardNo") String cardNo) throws UnsupportedEncodingException {
		QRcodeKit.buildQRcode(cardNo, 250, 250, response);
	}


	@ApiOperation(value = "发送短信验证码", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/37FD66FE/{busId}/sms/{memberId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String sendMsg(HttpServletRequest request,
								@ApiParam(value = "版本号", name = "version", required = true)
								@PathVariable("version") String version,
								@ApiParam(value = "手机号", name = "phone")
								@RequestParam(value = "phone", required = true) String phone,
						  		@ApiParam(value = "商家id", name = "busId", required = true)
							    @PathVariable("busId") Integer busId, @ApiParam(value = "粉丝用户id", name = "memberId", required = true) @PathVariable("memberId") Integer memberId) throws Exception {
		Member member = memberService.getById(memberId);
		String code = RandomKit.getRandomString(6, 0);
		String content = SmsCodeConstant.UNION_CARD_PHONE_BIND_MSG;
		sender.sendMsg(new PhoneMessage(busId, phone, content + code));
		redisCacheUtil.set(SmsCodeConstant.UNION_CARD_PHONE_BIND_TYPE + ":" + phone, code, 300L);
		return GtJsonResult.instanceSuccessMsg().toString();
	}

	//-------------------------------------------------- post ----------------------------------------------------------


	@ApiOperation(value = "小程序授权成功后登录", notes = "小程序授权成功后登录", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/{busId}/{memberId}/login", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
	public String login(HttpServletRequest request, HttpServletResponse response
			,@ApiParam(value = "版本号", name = "version", required = true) @PathVariable("version") String version
			,@ApiParam(name="busId", value = "商家id", required = true) @PathVariable("busId") Integer busId
			,@ApiParam(name="memberId", value = "用户id", required = true) @PathVariable("memberId") Integer memberId) throws Exception{
		//登录成功后获取token
		String data = wxAppCardService.login(busId, memberId);
		return GtJsonResult.instanceSuccessMsg(data).toString();
	}


	@ApiOperation(value = "绑定手机号", notes = "绑定手机号", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/37FD66FE/{busId}/bind/{memberId}", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
	public String bindCardPhone(HttpServletRequest request, HttpServletResponse response
			,@ApiParam(value = "版本号", name = "version", required = true) @PathVariable("version") String version
			, @ApiParam(name="phone", value = "手机号", required = true) @RequestParam("phone") String phone
			, @ApiParam(name="busId", value = "商家id", required = true) @PathVariable("busId") Integer busId
			, @ApiParam(name = "code", value = "验证码" ,required = true) @RequestParam(value = "code") String code, @ApiParam(value = "粉丝用户id", name = "memberId", required = true) @PathVariable("memberId") Integer memberId) throws Exception{
		Member member = memberService.getById(memberId);
		wxAppCardService.bindCardPhone(member, busId, phone, code);
		return GtJsonResult.instanceSuccessMsg().toString();
	}

	@ApiOperation(value = "办理联盟卡", notes = "办理联盟卡", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/37FD66FE/transaction/{busId}/{unionId}/{memberId}", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
	public String cardTransaction(HttpServletRequest request, HttpServletResponse response
			,@ApiParam(value = "版本号", name = "version", required = true) @PathVariable("version") String version
			,@ApiParam(name="busId", value = "商家id", required = true) @PathVariable("busId") Integer busId
			,@ApiParam(name="activityId", value = "活动卡id，如果没有，则是折扣卡", required = false) @RequestParam(value = "activityId", required = false) Integer activityId
			,@ApiParam(value = "联盟id", name = "unionId", required = true) @PathVariable("unionId") Integer unionId, @ApiParam(value = "粉丝用户id", name = "memberId", required = true) @PathVariable("memberId") Integer memberId) throws Exception{
		Member member = memberService.getById(memberId);
		return wxAppCardService.cardTransaction(member.getPhone(), busId, activityId, unionId);
	}

	@ApiOperation(value = "获取支付参数", notes = "获取支付参数", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/37FD66FE/payParam/{duoFenMemberId}/{memberId}", produces = "application/json;charset=UTF-8",method = RequestMethod.GET)
	public String payParam(HttpServletRequest request, HttpServletResponse response
			,@ApiParam(value = "版本号", name = "version", required = true) @PathVariable("version") String version,
			 @ApiParam(value = "多粉粉丝用户id", name = "duoFenMemberId", required = true) @PathVariable("duoFenMemberId") Integer duoFenMemberId,
			 @ApiParam(value = "支付粉丝用户id", name = "memberId", required = true) @PathVariable("memberId") Integer memberId,
			@ApiParam(value = "订单号", name = "orderNo", required = true) @RequestParam(name = "orderNo", required = true) String orderNo) throws Exception{
		Member member = memberService.getById(memberId);
		return wxAppCardService.getPayParam(duoFenMemberId, orderNo, member.getPhone());
	}




}
