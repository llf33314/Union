package com.gt.union.h5.card.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.Member;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.client.member.MemberService;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.constant.SmsCodeConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.h5.card.service.IH5CardService;
import com.gt.union.h5.card.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author hongjiye
 * @time 2017-12-19 11:37
 **/
@Api(description = "H5联盟卡")
@RestController
@RequestMapping("/h5Card/79B4DE7C")
public class H5CardController {

	@Autowired
	private IH5CardService h5CardService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private SmsService smsService;

	//-------------------------------------------------- get ----------------------------------------------------------

	@ApiOperation(value = "联盟卡-首页", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/index/{busId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String getIndexVO(HttpServletRequest request, Page page,
							 @ApiParam(value = "商家id", name = "busId", required = true)
							 @PathVariable("busId") Integer busId) throws Exception {
		Member member = SessionUtils.getLoginMember(request, busId);
		IndexVO indexVO;
		if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
			indexVO = MockUtil.get(IndexVO.class);
		} else {
			indexVO = h5CardService.getIndexVO(member == null ? null : member.getPhone(), busId);
		}
		Page<UnionCardVO> result = (Page<UnionCardVO>) page;
		result = PageUtil.setRecord(result, indexVO.getCardList());
		return GtJsonResult.instanceSuccessMsg(result).toString();
	}

	@ApiOperation(value = "联盟卡-详情", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/cardDetail/{busId}/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String cardDetail(HttpServletRequest request,
					 @ApiParam(value = "商家id", name = "busId", required = true)
					 @PathVariable("busId") Integer busId,
					 @ApiParam(value = "活动卡id，如果没有，则是折扣卡", name = "activityId", required = false)
					 @RequestParam(name = "activityId", required = false) Integer activityId,
					 @ApiParam(value = "联盟id", name = "unionId", required = true)
					 @PathVariable("unionId") Integer unionId) throws Exception {
		Member member = SessionUtils.getLoginMember(request, busId);
		CardDetailVO result;
		if (CommonConstant.COMMON_YES == ConfigConstant.IS_MOCK) {
			result = MockUtil.get(CardDetailVO.class);
		} else {
			result = h5CardService.getCardDetail(member == null ? null : member.getPhone(), busId, unionId, activityId);
		}
		return GtJsonResult.instanceSuccessMsg(result).toString();
	}

	@ApiOperation(value = "联盟卡-我的详情", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/myCardDetail/{busId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String myCardDetail(HttpServletRequest request,
							 @ApiParam(value = "商家id", name = "busId", required = true)
							 @PathVariable("busId") Integer busId,
							 @ApiParam(name = "url", value = "回调的url", required = true)
							 @RequestParam(value = "url") String url, Page page) throws Exception {
		Member member = SessionUtils.getLoginMember(request, busId);
		url = url + "/" + busId;
		if(CommonUtil.isEmpty(member)){
			return memberService.authorizeMember(request, busId, true, ConfigConstant.CARD_PHONE_BASE_URL + url, null).toString();
		}
		MyCardDetailVO myCardDetailVO = h5CardService.myCardDetail(member.getPhone());
		myCardDetailVO.setNickName(StringUtil.isEmpty(member.getNickname()) ? "未知用户" : member.getNickname());
		myCardDetailVO.setHeardImg(member.getHeadimgurl());
		Page<MyUnionCardDetailVO> result = (Page<MyUnionCardDetailVO>) page;
		result = PageUtil.setRecord(result, myCardDetailVO.getCardList());
		return GtJsonResult.instanceSuccessMsg(result).toString();
	}

	@ApiOperation(value = "联盟卡-消费记录", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/myCardConsume/{busId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String myCardConsume(HttpServletRequest request,
				   @ApiParam(value = "商家id", name = "busId", required = true)
				   @PathVariable("busId") Integer busId,
				   @ApiParam(name = "url", value = "回调的url", required = true)
				   @RequestParam(value = "url") String url, Page page) throws Exception {
		Member member = SessionUtils.getLoginMember(request, busId);
		url = url + "/" + busId;
		if(CommonUtil.isEmpty(member)){
			return memberService.authorizeMember(request, busId, true, ConfigConstant.CARD_PHONE_BASE_URL + url, ConfigConstant.CARD_PHONE_BASE_URL + "toUnionLogin").toString();
		}
		List<MyCardConsumeVO> list = h5CardService.listConsumeByPhone(member.getPhone(), page);
		page.setRecords(list);
		return GtJsonResult.instanceSuccessMsg(page).toString();
	}


	@ApiOperation(value = "获取联盟卡二维码", notes = "获取联盟卡二维码", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/qr/cardNo", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
	public void qrCardNo(HttpServletRequest request,
						  HttpServletResponse response, @ApiParam(name="cardNo", value = "联盟卡号", required = true) @RequestParam("cardNo") String cardNo) throws UnsupportedEncodingException {
		String encrypt = EncryptUtil.encrypt(PropertiesUtil.getEncryptKey(), cardNo);//加密后参数
		encrypt = URLEncoder.encode(encrypt,"UTF-8");
		QRcodeKit.buildQRcode(encrypt, 250, 250, response);
	}


	//-------------------------------------------------- post ----------------------------------------------------------


/*	@ApiOperation(value = "手机号、验证码登录", notes = "手机号、验证码登录", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/login", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
	public GtJsonResult loginPhone(HttpServletRequest request, HttpServletResponse response
			, @ApiParam(name="phone", value = "手机号", required = true) @RequestParam("phone") String phone
			, @ApiParam(name="code", value = "验证码", required = true) @RequestParam("code") String code
			, @ApiParam(name="busId", value = "商家id", required = true) @RequestParam("busId") Integer busId) throws Exception{
		if(!smsService.checkPhoneCode(SmsCodeConstant.UNION_CARD_LOGIN_TYPE, code, phone)){
			return GtJsonResult.instanceErrorMsg(CommonConstant.CODE_ERROR_MSG);
		}
		Member member = memberService.getByPhoneAndBusId(phone, busId);
		if(member == null){
		}
		if(memberService.loginMemberByPhone(phone,busId)){
			throw new BusinessException("登录失败");
		}
		return GtJsonResult.instanceSuccessMsg();
	}*/


	@ApiOperation(value = "绑定手机号", notes = "绑定手机号", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/{busId}/bind", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
	public GtJsonResult bindCardPhone(HttpServletRequest request, HttpServletResponse response
			,@ApiParam(name="phone", value = "手机号", required = true) @RequestParam("phone") String phone
			,@ApiParam(name="busId", value = "商家id", required = true) @PathVariable("busId") Integer busId
			,@ApiParam(name = "url", value = "回调的url", required = true) @RequestParam(value = "url") String url
			,@ApiParam(name = "code", value = "验证码" ,required = true) @RequestParam(value = "code") String code) throws Exception{
		Member member = SessionUtils.getLoginMember(request,busId);
		url = url + "/" + busId;
		if(CommonUtil.isEmpty(member)){
			return memberService.authorizeMember(request, busId, true, ConfigConstant.CARD_PHONE_BASE_URL + url, null);
		}
		h5CardService.bindCardPhone(member, busId, phone, code);
		return GtJsonResult.instanceSuccessMsg();
	}

	@ApiOperation(value = "办理联盟卡", notes = "办理联盟卡", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/transaction/{busId}/{unionId}", produces = "application/json;charset=UTF-8",method = RequestMethod.POST)
	public String cardTransaction(HttpServletRequest request, HttpServletResponse response
			,@ApiParam(name="busId", value = "商家id", required = true) @PathVariable("busId") Integer busId
			,@ApiParam(name="activityId", value = "活动卡id，如果没有，则是折扣卡", required = false) @RequestParam(value = "activityId", required = false) Integer activityId
			,@ApiParam(name = "url", value = "回调的url", required = true) @RequestParam(value = "url") String url
			,@ApiParam(value = "联盟id", name = "unionId", required = true) @PathVariable("unionId") Integer unionId) throws Exception{
		Member member = SessionUtils.getLoginMember(request,busId);
		url = url + "/" + busId;
		if(CommonUtil.isEmpty(member)){
			return memberService.authorizeMember(request, busId, true, ConfigConstant.CARD_PHONE_BASE_URL + url, null).toString();
		}
		return h5CardService.cardTransaction(member.getPhone(), busId, activityId, unionId);
	}


	//-------------------------------------------------- put ----------------------------------------------------------


}
