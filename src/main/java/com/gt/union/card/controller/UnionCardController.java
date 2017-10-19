package com.gt.union.card.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.bean.session.Member;
import com.gt.api.bean.session.WxPublicUsers;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.client.member.MemberService;
import com.gt.union.api.client.socket.SocketService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.card.service.IUnionCardService;
import com.gt.union.card.vo.UnionCardBindParamVO;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.DataExportException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.service.IUnionMainService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟卡信息 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionCard")
public class UnionCardController {

	private Logger logger = Logger.getLogger(UnionCardController.class);

	@Autowired
	private IUnionCardService unionCardService;

	@Autowired
	private IBusUserService busUserService;

	@Autowired
	private SocketService socketService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private IUnionMainService unionMainService;

	@Autowired
	private RedisCacheUtil redisCacheUtil;


	@ApiOperation(value = "获取盟员的联盟卡列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String listByUnionId(Page page, HttpServletRequest request
			, @ApiParam(name="unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId
			, @ApiParam(name="phone", value = "电话号码，模糊匹配", required = false) @RequestParam(name = "phone", required = false) String phone
			, @ApiParam(name="cardNo", value = "联盟卡号，模糊匹配", required = false) @RequestParam(name = "cardNo", required = false) String cardNo) throws Exception{
		BusUser user = SessionUtils.getLoginUser(request);
		Integer busId = user.getId();
		if(user.getPid() != null && user.getPid() != 0){
			busId = user.getPid();
		}
		Page result = unionCardService.selectListByUnionId(page, unionId, busId, cardNo, phone);
		return GTJsonResult.instanceSuccessMsg(result).toString();
	}

	@ApiOperation(value = "导出盟员的联盟卡列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/export/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public void exportCardList(HttpServletRequest request, HttpServletResponse response
			, @ApiParam(name="unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId
			,@ApiParam(name="phone", value = "电话号码", required = false) @RequestParam(name = "phone", required = false) String phone
			,@ApiParam(name="cardNo", value = "联盟卡号", required = false) @RequestParam(name = "cardNo", required = false) String cardNo) throws IOException {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			Integer busId = busUser.getId();
			if(busUser.getPid() != null && busUser.getPid() != 0){
				busId = busUser.getPid();
			}
			List<Map<String,Object>> list = unionCardService.listByUnionId(unionId, busId, cardNo, phone);
			String[] titles = new String[]{"联盟卡号", "联盟卡类型", "手机号","联盟积分","升级时间","有效期"};
			String[] contentName = new String[]{"cardNo", "type", "phone", "integral", "createtime", "validity"};
			UnionMain main = unionMainService.getById(unionId);
			String filename = main.getName() + "的联盟卡列表";
			HSSFWorkbook wb = unionCardService.exportCardList(titles, contentName, list);
			ExportUtil.responseExport(response, wb, filename);
		} catch (BaseException e){
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = "<script>alert('导出失败')</script>";
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			logger.error("",e);
		} catch (DataExportException e){
			logger.error("",e);
		} catch (Exception e) {
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = "<script>alert('导出失败')</script>";
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			logger.error("",e);
		}
	}

	@ApiOperation(value = "根据联盟卡号、手机号、扫码枪扫出的号码", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionCardInfo", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String unionCardInfo(HttpServletRequest request
			,@ApiParam(name="no", value = "联盟卡号、手机号、扫码枪扫出的号码", required = true) @RequestParam(name = "no") String no
			,@ApiParam(name="unionId", value = "联盟id，选择联盟时传入", required = false) @RequestParam(name = "unionId", required = false) Integer unionId) throws Exception{
		BusUser user = SessionUtils.getLoginUser(request);
		Integer busId = user.getId();
		if(user.getPid() != null && user.getPid() != 0){
			busId = user.getPid();
		}
		Map<String,Object> data = unionCardService.getUnionCardInfo(no, busId, unionId);//decode后的no
		return GTJsonResult.instanceSuccessMsg(data).toString();
	}

	@ApiOperation(value = "根据手机号获取验证码，并判断手机号的信息", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/phoneCode", method=RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String getPhoneCode(HttpServletRequest request, HttpServletResponse response,
							   @ApiParam(name="phone", value = "手机号", required = true) @RequestParam(name = "phone") String phone) throws Exception{
		BusUser user = SessionUtils.getLoginUser(request);
		Integer busId = user.getId();
		if(user.getPid() != null && user.getPid() != 0){
			busId = user.getPid();
		}
		unionCardService.getPhoneCode(busId, phone);
		return GTJsonResult.instanceSuccessMsg().toString();
	}

	@ApiOperation(value = "根据手机号和验证码获取升级的联盟卡信息", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/info", method=RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String getUnionInfoByPhone(HttpServletRequest request, HttpServletResponse response,
								   @ApiParam(name="phone", value = "手机号", required = true) @RequestParam(name = "phone") String phone
								   ,@ApiParam(name="code", value = "验证码", required = true) @RequestParam(name = "code") String code
									,@ApiParam(name="unionId", value = "联盟id", required = false) @RequestParam(name = "unionId", required = false) Integer unionId) throws Exception{
		BusUser user = SessionUtils.getLoginUser(request);
		Integer busId = user.getId();
		if(user.getPid() != null && user.getPid() != 0){
			busId = user.getPid();
		}
		Map<String,Object> data = unionCardService.getUnionInfoByPhone(busId, phone, code, unionId);
		return GTJsonResult.instanceSuccessMsg(data).toString();
	}


	@ApiOperation(value = "办理联盟卡", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method=RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String bindCard(HttpServletRequest request, HttpServletResponse response
			, @ApiParam(name="unionCardBindParamVO", value = "办理联盟卡参数", required = true) @RequestBody @Valid UnionCardBindParamVO vo, BindingResult bindingResult ) throws Exception{
			ParamValidatorUtil.checkBindingResult(bindingResult);
			BusUser user = SessionUtils.getLoginUser(request);
			Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			vo.setBusId(busId);
			Map<String,Object> data = unionCardService.bindCard(vo);
			return GTJsonResult.instanceSuccessMsg(data).toString();
	}


	@ApiOperation(value = "开启关注公众号，获取二维码链接", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "wxUser/QRcode", method=RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String wxUserQRcode(HttpServletRequest request, HttpServletResponse response) throws Exception{
		BusUser user = SessionUtils.getLoginUser(request);
		Integer busId = user.getId();
		if(user.getPid() != null && user.getPid() != 0){
			busId = user.getPid();
		}
		WxPublicUsers wxPublicUsers = busUserService.getWxPublicUserByBusId(busId);
		if(wxPublicUsers == null){
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
		String url = busUserService.getWxPublicUserQRCode(wxPublicUsers.getId(), user.getId());
		if(StringUtil.isEmpty(url)){
			return GTJsonResult.instanceErrorMsg(CommonConstant.SYS_ERROR).toString();
		}
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("qrurl",url);
		data.put("socketurl",PropertiesUtil.getSocketUrl());
		data.put("userId",PropertiesUtil.getSocketKey() + "Follow_" + user.getId());
		return GTJsonResult.instanceSuccessMsg(data).toString();
	}


	/**
	 * 关注公众号回调
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "79B4DE7C/followCallback", produces = "application/json;charset=UTF-8")
	public void followCallback(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String,Object> param) {
		try {
			Member member = memberService.getById(CommonUtil.toInteger(param.get("memberId")));
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("nickName", StringUtil.isEmpty(member.getNickname()) ? "未知用户" : member.getNickname());
			data.put("memberId",member.getId());
			data.put("headurl",member.getHeadimgurl());
			data.put("time",DateTimeKit.format(new Date(),"yyyy-MM-dd HH:mm"));
			logger.info("关注回调----------" + JSON.toJSONString(data));
			socketService.socketSendMessage(PropertiesUtil.getSocketKey() + "Follow_" + CommonUtil.toInteger(param.get("externalId")), JSON.toJSONString(data),"");
		} catch (Exception e) {
			logger.error("", e);
		}
	}


	@ApiOperation(value = "生成办理联盟卡支付订单二维码", produces = "application/json;charset=UTF-8")
	@SysLogAnnotation(op_function = "2", description = "生成办理联盟卡支付订单")
	@RequestMapping(value = "/qrCode", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String createUnionQRCode(HttpServletRequest request, HttpServletResponse response, @ApiParam(name="phone", value = "手机号", required = true) @RequestParam("phone") String phone,
									@ApiParam(name="memberId", value = "关注后的用户id", required = false) @RequestParam( name= "memberId", required = false) Integer memberId,
									@ApiParam(name="unionId", value = "联盟id", required = true) @RequestParam("unionId") Integer unionId,
									@ApiParam(name="cardType", value = "联盟卡类型 1：黑卡 2：红卡", required = true) @RequestParam("cardType") Integer cardType
									) throws Exception{
		BusUser user = SessionUtils.getLoginUser(request);
		Integer busId = user.getId();
		if(user.getPid() != null && user.getPid() != 0){
			busId = user.getPid();
		}
		Integer userId = user.getId();
		Map<String,Object> data = unionCardService.createQRCode(busId, phone, memberId,unionId, cardType, 0, "", userId, 1);
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
		result.put("url",PropertiesUtil.getWxmpUrl() + "/pay/B02A45A5/79B4DE7C/createPayQR.do" + sb.toString());
		result.put("only",data.get("only"));
		result.put("userId",PropertiesUtil.getSocketKey() + user.getId());
		return GTJsonResult.instanceSuccessMsg(result).toString();
	}


	@RequestMapping(value = "/79B4DE7C/paymentSuccess/{only}", method = RequestMethod.POST)
	public String payBindCardSuccess(HttpServletRequest request, HttpServletResponse response, @PathVariable(name = "only", required = true) String only
			,@RequestBody Map<String,Object> param) {
		Map<String,Object> data = new HashMap<String,Object>();
		String statusKey = RedisKeyUtil.getBindCardPayStatusKey(only);
		logger.info("办理联盟卡成功回调参数" + JSON.toJSONString(param));
		try {
			if(param.get("result_code").equals("SUCCESS") && param.get("return_code").equals("SUCCESS")){
				String orderNo = param.get("out_trade_no").toString();
				logger.info("前台办理联盟卡支付成功，only------------------"+only);
				logger.info("办理联盟卡支付成功，orderNo------------------" + orderNo);
				unionCardService.payBindCardSuccess(orderNo, only);
				String paramKey = RedisKeyUtil.getBindCardPayParamKey(only);
				String paramData = redisCacheUtil.get(paramKey);
				Map map = JSON.parseObject(paramData,Map.class);
				String status = redisCacheUtil.get(statusKey);
				if (CommonUtil.isEmpty(status)) {//订单超时
					status = ConfigConstant.USER_ORDER_STATUS_004;
				}else {
					status = JSON.parseObject(status,String.class);
				}
				if (ConfigConstant.USER_ORDER_STATUS_003.equals(status)) {//订单支付成功
					redisCacheUtil.remove(statusKey);
				}

				if (ConfigConstant.USER_ORDER_STATUS_005.equals(status)) {//订单支付失败
					redisCacheUtil.remove(statusKey);
				}
				if(CommonUtil.toInteger(map.get("isSendSocketMessage")) == 1){//推送消息
					Map<String,Object> result = new HashMap<String,Object>();
					result.put("status",status);
					result.put("only",only);
					logger.info("办理联盟卡扫码支付成功回调----------" + JSON.toJSONString(result));
					socketService.socketSendMessage(PropertiesUtil.getSocketKey() + CommonUtil.toInteger(map.get("userId")), JSON.toJSONString(result),"");
				}
				data.put("code",0);
				data.put("msg","成功");
				return JSON.toJSONString(data);
			}else {
				throw new BusinessException("支付失败");
			}
		} catch (BaseException e) {
			redisCacheUtil.set(statusKey,ConfigConstant.USER_ORDER_STATUS_005);
			logger.error("办理联盟卡支付成功后，产生错误：" + e);
			data.put("code",-1);
			data.put("msg",e.getErrorMsg());
			return JSON.toJSONString(data);
		} catch (Exception e) {
			redisCacheUtil.set(statusKey,ConfigConstant.USER_ORDER_STATUS_005);
			logger.error("办理联盟卡支付成功后，产生错误：" + e);
			data.put("code",-1);
			data.put("msg","失败");
			return JSON.toJSONString(data);
		}
	}

	@ApiOperation(value = "获取办理联盟卡支付状态", produces = "application/json;charset=UTF-8")
	@RequestMapping(value="status/{only}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String getStatus(HttpServletRequest request, HttpServletResponse response, @PathVariable("only")String only) throws Exception{
		logger.info("获取办理联盟卡支付订单状态：" + only);
		String statusKey = RedisKeyUtil.getBindCardPayStatusKey(only);
		String paramKey = RedisKeyUtil.getBindCardPayParamKey(only);
		String status = redisCacheUtil.get(statusKey);
		if(CommonUtil.isEmpty(status)){//订单超时
			status = ConfigConstant.USER_ORDER_STATUS_004;
		}else {
			status = JSON.parseObject(status,String.class);
		}
		if(ConfigConstant.USER_ORDER_STATUS_003.equals(status)){//订单支付成功
			redisCacheUtil.remove(statusKey);
			redisCacheUtil.remove(paramKey);
		}

		if(ConfigConstant.USER_ORDER_STATUS_005.equals(status)){//订单支付失败
			redisCacheUtil.remove(statusKey);
			redisCacheUtil.remove(paramKey);
		}
		return GTJsonResult.instanceSuccessMsg(status).toString();
	}

	@ApiOperation(value = "获取手机端二维码图片链接", notes = "获取手机端二维码图片链接", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/phone", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
	public String phoneQRCodeUrl(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        return GTJsonResult.instanceSuccessMsg(PropertiesUtil.getUnionUrl() + "/unionCard/phoneImg").toString();
	}


	@ApiOperation(value = "获取手机端二维码图片", notes = "获取手机端二维码图片", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/phoneImg", produces = "application/json;charset=UTF-8", method = RequestMethod.GET)
	public void phoneUrl(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		BusUser user = SessionUtils.getLoginUser(request);
		Integer busId = user.getId();
		if(user.getPid() != null && user.getPid() != 0){
			busId = user.getPid();
		}
		String url = PropertiesUtil.getUnionUrl() + "/cardPhone/#/" + "toUnionCard?busId=" + busId;
		QRcodeKit.buildQRcode(url, 250, 250, response);
	}

}
