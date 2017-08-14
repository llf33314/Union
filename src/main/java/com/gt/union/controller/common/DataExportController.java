package com.gt.union.controller.common;

import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.ExportUtil;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.common.ExportService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 导出数据控制器
 * Created by Administrator on 2017/8/10 0010.
 */
@RestController
@RequestMapping("/dataExport")
public class DataExportController {

	private static final String BUS_MEMBER_CARD = "DataExportController.busMemberCard()";
	private static final String UNION_MEMBER = "DataExportController.unionMember()";
	private static final String CARD_DIVIDE = "DataExportController.cardDivide()";
	private static final String BROKERAGE_DETAIL = "DataExportController.brokerageDetail()";
	private static final String RECOMMEND_BROKERAGE_DETAIL = "DataExportController.recommendBrokerageDetail()";
	private static final String CONSUME_FROM_DETAIL = "DataExportController.consumeFromDetail()";
	private static final String CONSUME_TO_DETAIL = "DataExportController.consumeToDetail()";

    //TODO 错误返回提醒
    private static final String EXPORT_BUSMEMBERCARD_UNIONID = "DataExportController.exportBusMemberCarByUnionId()";
	private Logger logger = LoggerFactory.getLogger(DataExportController.class);

	@Autowired
	private ExportService exportService;

	@ApiOperation(value = "导出盟员的联盟卡列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/busMemberCar/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public void exportBusMemberCarByUnionId(HttpServletRequest request, HttpServletResponse response
			, @ApiParam(name="unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId
			,@ApiParam(name="phone", value = "电话号码", required = false) @RequestParam(name = "phone", required = false) String phone
			,@ApiParam(name="cardNo", value = "联盟卡号", required = false) @RequestParam(name = "cardNo", required = false) String cardNo) throws IOException {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			String[] titles = new String[]{"房号", "业主姓名", "联系电话","余额（元）"};
			String[] contentName = new String[]{"roomNum", "ownerName", "ownerPhone", "ownerAccount"};
			String filename = "联盟卡列表";
			HSSFWorkbook wb = exportService.exportBusMemberCar(titles, contentName, list);
			ExportUtil.responseExport(response, wb, filename);
		} catch (BaseException e){
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(BUS_MEMBER_CARD,e.getMessage(),"导出失败").toString();
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			logger.error("",e);
		} catch (RuntimeException e){
			logger.error("",e);
		} catch (Exception e) {
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(BUS_MEMBER_CARD,e.getMessage(),"导出失败").toString();
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			logger.error("",e);
		}
	}

	/**
	 * 导出盟员列表
	 * @param request
	 * @param response
	 * @param enterpriseName	盟员名称
	 * @throws IOException
	 */
	@ApiOperation(value = "导出盟员列表", notes = "导出盟员列表，可模糊搜索盟员名称关键字", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionMember", method = RequestMethod.GET)
	public void unionMember(HttpServletRequest request, HttpServletResponse response, @ApiParam(name="unionId", value = "联盟id", required = true) @RequestParam(name = "unionId", required = true) Integer unionId
							,@ApiParam(name = "enterpriseName", value = "盟员名称", required = false) @RequestParam(name = "enterpriseName", required = false) String enterpriseName) throws IOException {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			String[] titles = new String[]{"房号", "业主姓名", "联系电话","余额（元）"};
			String[] contentName = new String[]{"roomNum", "ownerName", "ownerPhone", "ownerAccount"};
			String filename = "盟员列表";
			HSSFWorkbook wb = exportService.exportUnionMember(titles,contentName,list);
			ExportUtil.responseExport(response, wb, filename);
		} catch (BaseException e){
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(UNION_MEMBER,e.getMessage(),"导出失败").toString();
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			logger.error("",e);
		} catch (RuntimeException e){
			logger.error("",e);
		} catch (Exception e) {
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(UNION_MEMBER,e.getMessage(),"导出失败").toString();
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			logger.error("",e);
		}
	}

	/**
	 * 导出售卡分成
	 * @param request
	 * @param response
	 * @param unionId	联盟id
	 * @param cardType	售卡类型
	 * @param cardNo	联盟卡号
	 * @param startTime	开始日期
	 * @param endTime	结束日期
	 * @throws IOException
	 */
	@ApiOperation(value = "导出售卡分成", notes = "导出售卡分成列表，可模糊搜索联盟卡号关键字", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/cardDivide", method = RequestMethod.GET)
	public void cardDivide(HttpServletRequest request, HttpServletResponse response, @ApiParam(name="unionId", value = "联盟id", required = true) @RequestParam(name = "unionId", required = true) Integer unionId
			,@ApiParam(name = "cardType", value = "售卡类型", required = false) @RequestParam(name = "cardType", required = false) Integer cardType
			,@ApiParam(name = "cardNo", value = "联盟卡号", required = false) @RequestParam(name = "cardNo", required = false) String cardNo
			,@ApiParam(name = "startTime", value = "开始时间", required = false) @RequestParam(name = "startTime", required = false) String startTime
			,@ApiParam(name = "endTime", value = "结束时间", required = false) @RequestParam(name = "endTime", required = false) String endTime) throws IOException {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			String[] titles = new String[]{"房号", "业主姓名", "联系电话","余额（元）"};
			String[] contentName = new String[]{"roomNum", "ownerName", "ownerPhone", "ownerAccount"};
			String filename = "盟员列表";
			HSSFWorkbook wb = exportService.exportCardDivide(titles,contentName,list);
			ExportUtil.responseExport(response, wb, filename);
		} catch (BaseException e){
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(CARD_DIVIDE,e.getMessage(),"导出失败").toString();
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			logger.error("",e);
		} catch (RuntimeException e){
			logger.error("",e);
		} catch (Exception e) {
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(CARD_DIVIDE,e.getMessage(),"导出失败").toString();
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			logger.error("",e);
		}
	}


	/**
	 *	导出佣金明细详情
	 * @param request
	 * @param response
	 * @param unionId	联盟id
	 * @param busId		佣金来往商家id
	 * @throws IOException
	 */
	@ApiOperation(value = "导出佣金明细详情", notes = "导出佣金明细详情列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/brokerageDetail", method = RequestMethod.GET)
	public void brokerageDetail(HttpServletRequest request, HttpServletResponse response, @ApiParam(name="unionId", value = "联盟id", required = true) @RequestParam(name = "unionId", required = true) Integer unionId
			,@ApiParam(name = "busId", value = "佣金来往商家id", required = true) @RequestParam(name = "busId", required = true) Integer busId) throws IOException {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			String[] titles = new String[]{"房号", "业主姓名", "联系电话","余额（元）"};
			String[] contentName = new String[]{"roomNum", "ownerName", "ownerPhone", "ownerAccount"};
			String filename = "佣金明细详情";
			HSSFWorkbook wb = exportService.exportBrokerageDetail(titles,contentName,list);
			ExportUtil.responseExport(response, wb, filename);
		} catch (BaseException e){
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(BROKERAGE_DETAIL,e.getMessage(),"导出失败").toString();
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			logger.error("",e);
		} catch (RuntimeException e){
			logger.error("",e);
		} catch (Exception e) {
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(BROKERAGE_DETAIL,e.getMessage(),"导出失败").toString();
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			logger.error("",e);
		}
	}

	/**
	 *	导出佣金支付明细列表
	 * @param request
	 * @param response
	 * @param unionId	联盟id
	 * @throws IOException
	 */
	@ApiOperation(value = "导出佣金支付明细列表", notes = "导出佣金支付明细列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/recommendBrokerageDetail", method = RequestMethod.GET)
	public void recommendBrokerageDetail(HttpServletRequest request, HttpServletResponse response,
								@ApiParam(name="unionId", value = "联盟id", required = false) @RequestParam(name = "unionId", required = false) Integer unionId
								) throws IOException {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			String[] titles = new String[]{"房号", "业主姓名", "联系电话","余额（元）"};
			String[] contentName = new String[]{"roomNum", "ownerName", "ownerPhone", "ownerAccount"};
			String filename = "佣金支付明细";
			HSSFWorkbook wb = exportService.exportRecommendBrokerageDetail(titles,contentName,list);
			ExportUtil.responseExport(response, wb, filename);
		} catch (BaseException e){
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(RECOMMEND_BROKERAGE_DETAIL,e.getMessage(),"导出失败").toString();
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			logger.error("",e);
		} catch (RuntimeException e){
			logger.error("",e);
		} catch (Exception e) {
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(RECOMMEND_BROKERAGE_DETAIL,e.getMessage(),"导出失败").toString();
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			logger.error("",e);
		}
	}

	/**
	 * 导出本店消费记录
	 * @param request
	 * @param response
	 * @param unionId
	 * @param cardNo
	 * @param phone
	 * @param busId
	 * @param beginTime
	 * @param endTIme
	 * @throws IOException
	 */
	@ApiOperation(value = "导出本店消费记录列表", notes = "导出本店消费记录列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/consumeFromDetail", method = RequestMethod.GET)
	public void consumeFromDetail(HttpServletRequest request, HttpServletResponse response,
							 @ApiParam(name="unionId", value = "联盟id", required = false) @RequestParam(name = "unionId", required = false) Integer unionId
							,@ApiParam(name = "cardNo", value = "联盟卡号", required = false) @RequestParam(name = "cardNo", required = false) String cardNo
							,@ApiParam(name = "phone", value = "手机号", required = false) @RequestParam(name = "phone", required = false) String phone
							,@ApiParam(name = "busId", value = "来往的商家id", required = false) @RequestParam(name = "busId", required = false) Integer busId
							,@ApiParam(name = "beginTime", value = "开始时间", required = false) @RequestParam(name = "beginTime", required = false) String beginTime
							,@ApiParam(name = "endTime", value = "结束时间", required = false) @RequestParam(name = "endTime", required = false) String endTIme) throws IOException {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			String[] titles = new String[]{"房号", "业主姓名", "联系电话","余额（元）"};
			String[] contentName = new String[]{"roomNum", "ownerName", "ownerPhone", "ownerAccount"};
			String filename = "本店消费记录";
			HSSFWorkbook wb = exportService.exportConsumeFromDetail(titles,contentName,list);
			ExportUtil.responseExport(response, wb, filename);
		} catch (BaseException e){
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(CONSUME_FROM_DETAIL,e.getMessage(),"导出失败").toString();
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			logger.error("",e);
		} catch (RuntimeException e){
			logger.error("",e);
		} catch (Exception e) {
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(CONSUME_FROM_DETAIL,e.getMessage(),"导出失败").toString();
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
	 * @param busId
	 * @param beginTime
	 * @param endTime
	 * @throws IOException
	 */
	@ApiOperation(value = "导出他店消费记录列表", notes = "导出他店消费记录列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/consumeToDetail", method = RequestMethod.GET)
	public void consumeToDetail(HttpServletRequest request, HttpServletResponse response,
								@ApiParam(name="unionId", value = "联盟id", required = false) @RequestParam(name = "unionId", required = false) Integer unionId
								,@ApiParam(name = "cardNo", value = "联盟卡号", required = false) @RequestParam(name = "cardNo", required = false) String cardNo
								,@ApiParam(name = "phone", value = "手机号", required = false) @RequestParam(name = "phone", required = false) String phone
								,@ApiParam(name = "busId", value = "来往的商家id", required = false) @RequestParam(name = "busId", required = false) Integer busId
								,@ApiParam(name = "beginTime", value = "开始时间", required = false) @RequestParam(name = "beginTime", required = false) String beginTime
								,@ApiParam(name = "endTime", value = "结束时间", required = false) @RequestParam(name = "endTime", required = false) String endTime) throws IOException {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			String[] titles = new String[]{"房号", "业主姓名", "联系电话","余额（元）"};
			String[] contentName = new String[]{"roomNum", "ownerName", "ownerPhone", "ownerAccount"};
			String filename = "他店消费记录";
			HSSFWorkbook wb = exportService.exportConsumeToDetail(titles,contentName,list);
			ExportUtil.responseExport(response, wb, filename);
		} catch (BaseException e){
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(CONSUME_TO_DETAIL,e.getMessage(),"导出失败").toString();
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			logger.error("",e);
		} catch (RuntimeException e){
			logger.error("",e);
		} catch (Exception e) {
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(CONSUME_TO_DETAIL,e.getMessage(),"导出失败").toString();
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			logger.error("",e);
		}
	}
}
