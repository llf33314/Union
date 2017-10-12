package com.gt.union.consume.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.DataExportException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.consume.service.IUnionConsumeService;
import com.gt.union.consume.vo.UnionConsumeParamVO;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.service.IUnionMainService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 消费 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionConsume")
public class UnionConsumeController {

	private Logger logger = LoggerFactory.getLogger(UnionConsumeController.class);

	@Autowired
	private IUnionMainService unionMainService;

	@Autowired
	private IUnionConsumeService unionConsumeService;

	@Autowired
	private RedisCacheUtil redisCacheUtil;


	@ApiOperation(value = "查询本店消费核销记录", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/my", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String listMyByUnionId(HttpServletRequest request, Page page
			, @ApiParam(name = "unionId", value = "联盟id", required = false)
								  @RequestParam(name = "unionId",required = false) Integer unionId
			, @ApiParam(name = "memberId", value = "来源", required = false)
								  @RequestParam(name = "memberId", required = false) Integer memberId
			, @ApiParam(name = "cardNo", value = "联盟卡号，模糊查询", required = false)
								  @RequestParam(name = "cardNo", required = false) String cardNo
			, @ApiParam(name = "phone", value = "手机号，模糊查询", required = false)
								  @RequestParam(name = "phone", required = false) String phone
			, @ApiParam(name = "beginTime", value = "开始时间", required = false)
								  @RequestParam(name = "beginTime", required = false) String beginTime
			, @ApiParam(name = "endTime", value = "结束时间", required = false)
								  @RequestParam(name = "endTime", required = false) String endTime) throws Exception{
		BusUser user = SessionUtils.getLoginUser(request);
		Integer busId = user.getId();
		if(user.getPid() != null && user.getPid() != 0){
			busId = user.getPid();
		}
		Page result = this.unionConsumeService.listMy(page, unionId, busId, memberId, cardNo, phone, beginTime, endTime);
		return GTJsonResult.instanceSuccessMsg(result).toString();
	}

	@ApiOperation(value = "查询它店消费核销记录", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/other", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String listOtherByUnionId(HttpServletRequest request, Page page
			, @ApiParam(name = "unionId", value = "联盟id", required = false)
									 @RequestParam(name = "unionId",required = false) Integer unionId
			, @ApiParam(name = "memberId", value = "来源", required = false)
									 @RequestParam(name = "memberId", required = false) Integer memberId
			, @ApiParam(name = "cardNo", value = "联盟卡号，模糊查询", required = false)
									 @RequestParam(name = "cardNo", required = false) String cardNo
			, @ApiParam(name = "phone", value = "手机号，模糊查询", required = false)
									 @RequestParam(name = "phone", required = false) String phone
			, @ApiParam(name = "beginTime", value = "开始时间", required = false)
									 @RequestParam(name = "beginTime", required = false) String beginTime
			, @ApiParam(name = "endTime", value = "结束时间", required = false)
									 @RequestParam(name = "endTime", required = false) String endTime) throws Exception{
		BusUser user = SessionUtils.getLoginUser(request);
		Integer busId = user.getId();
		if(user.getPid() != null && user.getPid() != 0){
			busId = user.getPid();
		}
		Page result = this.unionConsumeService.listOther(page, unionId, busId, memberId, cardNo, phone, beginTime, endTime);
		return GTJsonResult.instanceSuccessMsg(result).toString();
	}

	/**
	 * 导出本店消费记录
	 * @param request
	 * @param response
	 * @param unionId
	 * @param cardNo
	 * @param phone
	 * @param memberId
	 * @param beginTime
	 * @param endTime
	 * @throws IOException
	 */
	@ApiOperation(value = "导出本店消费记录列表", notes = "导出本店消费记录列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/consumeFromDetail", method = RequestMethod.GET)
	public void exportConsumeFromDetail(HttpServletRequest request, HttpServletResponse response,
										@ApiParam(name="unionId", value = "联盟id", required = true) @RequestParam(name = "unionId", required = false) Integer unionId
			,@ApiParam(name = "cardNo", value = "联盟卡号", required = false) @RequestParam(name = "cardNo", required = false) String cardNo
			,@ApiParam(name = "phone", value = "手机号", required = false) @RequestParam(name = "phone", required = false) String phone
			,@ApiParam(name = "memberId", value = "来往的商家id", required = false) @RequestParam(name = "memberId", required = false) Integer memberId
			,@ApiParam(name = "beginTime", value = "开始时间", required = false) @RequestParam(name = "beginTime", required = false) String beginTime
			,@ApiParam(name = "endTime", value = "结束时间", required = false) @RequestParam(name = "endTime", required = false) String endTime) throws IOException {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			Integer busId = busUser.getId();
			if(busUser.getPid() != null && busUser.getPid() != 0){
				busId = busUser.getPid();
			}
			List<Map<String,Object>> list = unionConsumeService.listMyByUnionId(unionId, busId, memberId, cardNo, phone, beginTime, endTime);
			String[] titles = new String[]{"来源", "联盟卡号","手机号", "消费金额（元）", "实收金额（元）", "优惠项目", "创建时间"};
			String[] contentName = new String[]{"memberName", "cardNo", "phone", "consumeMoney", "payMoney", "serviceNames", "createtime"};
			UnionMain main = unionMainService.getById(unionId);
			String filename = main.getName() + "的本店消费记录";
			HSSFWorkbook wb = unionConsumeService.exportConsumeFromDetail(titles,contentName,list);
			ExportUtil.responseExport(response, wb, filename);
		} catch (BaseException e){
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg("导出失败").toString();
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
			String result = GTJsonResult.instanceErrorMsg("导出失败").toString();
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			logger.error("",e);
		}
	}


	/**
	 * 导出他店消费记录
	 * @param request
	 * @param response
	 * @param unionId
	 * @param cardNo
	 * @param phone
	 * @param memberId
	 * @param beginTime
	 * @param endTime
	 * @throws IOException
	 */
	@ApiOperation(value = "导出他店消费记录列表", notes = "导出他店消费记录列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/consumeToDetail", method = RequestMethod.GET)
	public void exportConsumeToDetail(HttpServletRequest request, HttpServletResponse response,
									  @ApiParam(name="unionId", value = "联盟id", required = false) @RequestParam(name = "unionId", required = false) Integer unionId
			,@ApiParam(name = "cardNo", value = "联盟卡号", required = false) @RequestParam(name = "cardNo", required = false) String cardNo
			,@ApiParam(name = "phone", value = "手机号", required = false) @RequestParam(name = "phone", required = false) String phone
			,@ApiParam(name = "memberId", value = "来往的商家id", required = false) @RequestParam(name = "memberId", required = false) Integer memberId
			,@ApiParam(name = "beginTime", value = "开始时间", required = false) @RequestParam(name = "beginTime", required = false) String beginTime
			,@ApiParam(name = "endTime", value = "结束时间", required = false) @RequestParam(name = "endTime", required = false) String endTime) throws IOException {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			Integer busId = busUser.getId();
			if(busUser.getPid() != null && busUser.getPid() != 0){
				busId = busUser.getPid();
			}
			List<Map<String,Object>> list = unionConsumeService.listOtherByUnionId(unionId, busId, memberId, cardNo, phone, beginTime, endTime);
			String[] titles = new String[]{"来源", "联盟卡号","手机号", "消费金额（元）", "实收金额（元）", "优惠项目", "创建时间"};
			String[] contentName = new String[]{"memberName", "cardNo", "phone", "consumeMoney", "payMoney", "serviceNames", "createtime"};
			UnionMain main = unionMainService.getById(unionId);
			String filename = main.getName() + "的他店消费记录";
			HSSFWorkbook wb = unionConsumeService.exportConsumeToDetail(titles,contentName,list);
			ExportUtil.responseExport(response, wb, filename);
		} catch (BaseException e){
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg("导出失败").toString();
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
			String result = GTJsonResult.instanceErrorMsg("导出失败").toString();
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			logger.error("",e);
		}
	}


	@ApiOperation(value = "根据联盟卡核销，单核销优惠项目，或者使用现金支付时调取", produces = "application/json;charset=UTF-8")
	@SysLogAnnotation(op_function = "3", description = "根据联盟卡核销")
	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String consumeByCard(HttpServletRequest request
			, @ApiParam(name="unionConsumeParamVO", value = "联盟卡核销参数", required = true) @RequestBody @Valid UnionConsumeParamVO vo , BindingResult bindingResult) throws Exception{
		ParamValidatorUtil.checkBindingResult(bindingResult);
		BusUser user = SessionUtils.getLoginUser(request);
		Integer busId = user.getId();
		if(user.getPid() != null && user.getPid() != 0){
			busId = user.getPid();
		}
		unionConsumeService.consumeByCard(busId, vo);
		return GTJsonResult.instanceSuccessMsg().toString();
	}

	//------------------------------------------------- money ----------------------------------------------------------
	@RequestMapping(value = "/79B4DE7C/paymentSuccess/{Encrypt}/{only}", method = RequestMethod.POST)
	public String payConsumeSuccess(HttpServletRequest request, HttpServletResponse response
			, @PathVariable(name = "Encrypt", required = true) String encrypt
			, @PathVariable(name = "only", required = true) String only) {
		Map<String,Object> data = new HashMap<String,Object>();
		try {
			logger.info("消费核销支付成功，Encrypt------------------" + encrypt);
			logger.info("消费核销支付成功，only------------------" + only);
			unionConsumeService.payConsumeSuccess(encrypt, only);
			data.put("code",0);
			data.put("msg","成功");
			return JSON.toJSONString(data);
		} catch (BaseException e) {
			logger.error("消费核销支付成功后，产生错误：" + e);
			data.put("code",-1);
			data.put("msg",e.getErrorMsg());
			return JSON.toJSONString(data);
		} catch (Exception e) {
			logger.error("消费核销支付成功后，产生错误：" + e);
			data.put("code",-1);
			data.put("msg","失败");
			return JSON.toJSONString(data);
		}
	}

	@ApiOperation(value = "扫码支付时调用，生成消费核销支付订单二维码", produces = "application/json;charset=UTF-8")
	@SysLogAnnotation(op_function = "2", description = "生成消费核销支付二维码")
	@RequestMapping(value = "/qrCode", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String payOpportunityQRCode(HttpServletRequest request, HttpServletResponse response, @ApiParam(name="unionConsumeParamVO", value = "联盟卡核销参数", required = true) @RequestBody @Valid UnionConsumeParamVO vo, BindingResult bindingResult) throws Exception{
		ParamValidatorUtil.checkBindingResult(bindingResult);
		BusUser user = SessionUtils.getLoginUser(request);
		if (CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0) {
			throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
		}
		Map<String, Object> data = unionConsumeService.payConsumeQRCode(user.getId(), vo);
		StringBuilder sb = new StringBuilder("?");
		sb.append("totalFee=" + data.get("totalFee"));
		sb.append("&model=" + data.get("model"));
		sb.append("&busId=" + data.get("busId"));
		sb.append("&appidType=" + data.get("appidType"));
		sb.append("&appid=" + data.get("appid"));
		sb.append("&orderNum=" + data.get("orderNum"));
		sb.append("&desc=" + data.get("desc"));
		sb.append("&isreturn=" + data.get("isreturn"));
		sb.append("&notifyUrl=" + data.get("notifyUrl"));
		sb.append("&isSendMessage=" + data.get("isSendMessage"));
		sb.append("&payWay=" + data.get("payWay"));
		sb.append("&sourceType=" + data.get("sourceType"));
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("url", ConfigConstant.WXMP_ROOT_URL + "/pay/B02A45A5/79B4DE7C/createPayQR.do" + sb.toString());
		result.put("only", data.get("only"));
		return GTJsonResult.instanceSuccessMsg(result).toString();
	}

	@ApiOperation(value = "获取消费核销支付状态，用定时器请求，004：订单超时 003：支付成功 005：支付失败", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "status/{only}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String getStatus(HttpServletRequest request, HttpServletResponse response, @PathVariable("only") String only) throws Exception {
		logger.info("获取消费核销支付状态：" + only);
		String statusKey = RedisKeyUtil.getConsumePayStatusKey(only);
		String paramKey = RedisKeyUtil.getConsumePayParamKey(only);
		String status = redisCacheUtil.get(statusKey);
		if (CommonUtil.isEmpty(status)) {//订单超时
			status = ConfigConstant.USER_ORDER_STATUS_004;
		}else {
			status = JSON.parseObject(status,String.class);
		}
		if (ConfigConstant.USER_ORDER_STATUS_003.equals(status)) {//订单支付成功
			redisCacheUtil.remove(statusKey);
			redisCacheUtil.remove(paramKey);
		}

		if (ConfigConstant.USER_ORDER_STATUS_005.equals(status)) {//订单支付失败
			redisCacheUtil.remove(statusKey);
			redisCacheUtil.remove(paramKey);
		}
		return GTJsonResult.instanceSuccessMsg(status).toString();
	}

}
