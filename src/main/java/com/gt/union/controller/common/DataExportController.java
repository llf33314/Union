package com.gt.union.controller.common;

import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.DataExportException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.ExportUtil;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionMainService;
import com.gt.union.service.basic.IUnionMemberService;
import com.gt.union.service.business.IUnionBusinessRecommendService;
import com.gt.union.service.card.IUnionBusMemberCardService;
import com.gt.union.service.common.ExportService;
import com.gt.union.service.consume.IUnionConsumeRecordService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

	private static final String EXPORT_BUSMEMBERCARD_UNIONID = "DataExportController.exportBusMemberCardByUnionId()";
	private static final String EXPORT_UNIONMEMBER_UNIONID = "DataExportController.exportUnionMemberByUnionId()";
	private static final String EXPORT_CARDDIVIDE_UNIONID = "DataExportController.exportCardDivideByUnionId()";
	private static final String EXPORT_BROKERAGEDETAIL_UNIONID = "DataExportController.exportBrokerageDetailByUnionId()";
	private static final String EXPORT_RECOMMEND_BROKERAGE_DETAIL = "DataExportController.exportRecommendBrokerageDetail()";
	private static final String EXPORT_CONSUME_FROM_DETAIL = "DataExportController.exportConsumeFromDetail()";
	private static final String EXPORT_CONSUME_TO_DETAIL = "DataExportController.exportConsumeToDetail()";

	private Logger logger = LoggerFactory.getLogger(DataExportController.class);

	@Autowired
	private ExportService exportService;


	@Autowired
	private IUnionBusMemberCardService unionBusMemberCardService;

	@Autowired
	private IUnionMemberService unionMemberService;

	@Autowired
	private IUnionMainService unionMainService;

	@Autowired
	private IUnionConsumeRecordService unionConsumeRecordService;

	@Autowired
	private IUnionBusinessRecommendService unionBusinessRecommendService;


	@ApiOperation(value = "导出盟员的联盟卡列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/busMemberCard/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public void exportBusMemberCardByUnionId(HttpServletRequest request, HttpServletResponse response
			, @ApiParam(name="unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId
			,@ApiParam(name="phone", value = "电话号码", required = false) @RequestParam(name = "phone", required = false) String phone
			,@ApiParam(name="cardNo", value = "联盟卡号", required = false) @RequestParam(name = "cardNo", required = false) String cardNo) throws IOException {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			Integer busId = busUser.getId();
			if(busUser.getPid() != null && busUser.getPid() != 0){
				busId = busUser.getPid();
			}
			List<Map<String,Object>> list = unionBusMemberCardService.selectUnionBusMemberCardList(unionId, busId, cardNo, phone);
			String[] titles = new String[]{"联盟卡号", "联盟卡类型", "手机号","联盟积分","升级时间","有效期"};
			String[] contentName = new String[]{"cardNo", "card_type", "phone", "integral", "updatetime", "cardTermTime"};
			String filename = "联盟卡列表";
			HSSFWorkbook wb = exportService.exportBusMemberCar(titles, contentName, list);
			ExportUtil.responseExport(response, wb, filename);
		} catch (BaseException e){
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(EXPORT_BUSMEMBERCARD_UNIONID,e.getMessage(),"导出失败").toString();
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
			String result = GTJsonResult.instanceErrorMsg(EXPORT_BUSMEMBERCARD_UNIONID,e.getMessage(),"导出失败").toString();
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
	@RequestMapping(value = "/unionMember/unionId/{unionId}", method = RequestMethod.GET)
	public void exportUnionMemberByUnionId(HttpServletRequest request, HttpServletResponse response, @ApiParam(name="unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId
							,@ApiParam(name = "enterpriseName", value = "盟员名称", required = false) @RequestParam(name = "enterpriseName", required = false) String enterpriseName) throws IOException {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			List<Map<String,Object>> list = unionMemberService.listByUnionIdList(unionId, enterpriseName);
			String[] titles = new String[]{"企业名称", "加入时间", "我给TA的折扣（折）","TA给我的折扣（折）", "售卡分成比例"};
			String[] contentName = new String[]{"enterpriseName", "createtime", "fromDiscount", "toDiscount", "sellDivideProportion"};
			UnionMain main = unionMainService.getById(unionId);
			String filename = main.getUnionName() + "的盟员列表";
			HSSFWorkbook wb = exportService.exportUnionMember(titles,contentName,list);
			ExportUtil.responseExport(response, wb, filename);
		} catch (BaseException e){
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(EXPORT_UNIONMEMBER_UNIONID,e.getMessage(),"导出失败").toString();
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
			String result = GTJsonResult.instanceErrorMsg(EXPORT_UNIONMEMBER_UNIONID,e.getMessage(),"导出失败").toString();
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
	@RequestMapping(value = "/cardDivide/unionId/{unionId}", method = RequestMethod.GET)
	public void exportCardDivideByUnionId(HttpServletRequest request, HttpServletResponse response, @ApiParam(name="unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId
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
			String result = GTJsonResult.instanceErrorMsg(EXPORT_CARDDIVIDE_UNIONID,e.getMessage(),"导出失败").toString();
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
			String result = GTJsonResult.instanceErrorMsg(EXPORT_CARDDIVIDE_UNIONID,e.getMessage(),"导出失败").toString();
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
	 * @param toBusId		佣金来往商家id
	 * @throws IOException
	 */
	@ApiOperation(value = "导出佣金明细详情", notes = "导出佣金明细详情列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/brokerageDetail/unionId/{unionId}", method = RequestMethod.GET)
	public void exportBrokerageDetailByUnionId(HttpServletRequest request, HttpServletResponse response, @ApiParam(name="unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId
			,@ApiParam(name = "toBusId", value = "佣金来往商家id", required = true) @RequestParam(name = "toBusId", required = true) Integer toBusId) throws IOException {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			Integer fromBusId = busUser.getId();
			if (busUser.getPid() != null && busUser.getPid() != 0) {
				fromBusId = busUser.getPid();
			}
			List<Map<String,Object>> list = unionBusinessRecommendService.listPayDetailParticularByUnionIdAndFromBusIdAndToBusId(unionId, fromBusId, toBusId);
			String[] titles = new String[]{"时间", "顾客姓名", "电话","佣金（元）"};
			String[] contentName = new String[]{"roomNum", "ownerName", "ownerPhone", "ownerAccount"};
			String filename = "佣金明细详情";
			HSSFWorkbook wb = exportService.exportBrokerageDetail(titles,contentName,list);
			wb.getSheet("sheet");
			ExportUtil.responseExport(response, wb, filename);
		} catch (BaseException e){
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(EXPORT_BROKERAGEDETAIL_UNIONID,e.getMessage(),"导出失败").toString();
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
			String result = GTJsonResult.instanceErrorMsg(EXPORT_BROKERAGEDETAIL_UNIONID,e.getMessage(),"导出失败").toString();
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
	public void exportRecommendBrokerageDetail(HttpServletRequest request, HttpServletResponse response,
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
			String result = GTJsonResult.instanceErrorMsg(EXPORT_RECOMMEND_BROKERAGE_DETAIL,e.getMessage(),"导出失败").toString();
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
			String result = GTJsonResult.instanceErrorMsg(EXPORT_RECOMMEND_BROKERAGE_DETAIL,e.getMessage(),"导出失败").toString();
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
	 * @param fromBusId
	 * @param beginTime
	 * @param endTime
	 * @throws IOException
	 */
	@ApiOperation(value = "导出本店消费记录列表", notes = "导出本店消费记录列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/consumeFromDetail", method = RequestMethod.GET)
	public void exportConsumeFromDetail(HttpServletRequest request, HttpServletResponse response,
							 @ApiParam(name="unionId", value = "联盟id", required = true) @RequestParam(name = "unionId", required = true) Integer unionId
							,@ApiParam(name = "cardNo", value = "联盟卡号", required = false) @RequestParam(name = "cardNo", required = false) String cardNo
							,@ApiParam(name = "phone", value = "手机号", required = false) @RequestParam(name = "phone", required = false) String phone
							,@ApiParam(name = "fromBusId", value = "来往的商家id", required = false) @RequestParam(name = "fromBusId", required = false) Integer fromBusId
							,@ApiParam(name = "beginTime", value = "开始时间", required = false) @RequestParam(name = "beginTime", required = false) String beginTime
							,@ApiParam(name = "endTime", value = "结束时间", required = false) @RequestParam(name = "endTime", required = false) String endTime) throws IOException {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			Integer busId = busUser.getId();
			if(busUser.getPid() != null && busUser.getPid() != 0){
				busId = busUser.getPid();
			}
			List<Map<String,Object>> list = unionConsumeRecordService.listMyByUnionId(unionId, busId, fromBusId, cardNo, phone, beginTime, endTime);
			String[] titles = new String[]{"来源", "联盟卡号","手机号", "消费金额（元）", "实收金额（元）", "优惠项目", "创建时间"};
			String[] contentName = new String[]{"enterpriseName", "cardNo", "phone", "totalMoney", "payMoney", "serviceNames", "createTime"};
			UnionMain main = unionMainService.getById(unionId);
			String filename = main.getUnionName() + "的本店消费记录";
			HSSFWorkbook wb = exportService.exportConsumeFromDetail(titles,contentName,list);
			ExportUtil.responseExport(response, wb, filename);
		} catch (BaseException e){
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(EXPORT_CONSUME_FROM_DETAIL,e.getMessage(),"导出失败").toString();
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
			String result = GTJsonResult.instanceErrorMsg(EXPORT_CONSUME_FROM_DETAIL,e.getMessage(),"导出失败").toString();
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
	 * @param toBusId
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
								,@ApiParam(name = "toBusId", value = "来往的商家id", required = false) @RequestParam(name = "toBusId", required = false) Integer toBusId
								,@ApiParam(name = "beginTime", value = "开始时间", required = false) @RequestParam(name = "beginTime", required = false) String beginTime
								,@ApiParam(name = "endTime", value = "结束时间", required = false) @RequestParam(name = "endTime", required = false) String endTime) throws IOException {
		try {
			BusUser busUser = SessionUtils.getLoginUser(request);
			Integer busId = busUser.getId();
			if(busUser.getPid() != null && busUser.getPid() != 0){
				busId = busUser.getPid();
			}
			List<Map<String,Object>> list = unionConsumeRecordService.listOtherByUnionId(unionId, busId, toBusId, cardNo, phone, beginTime, endTime);
			String[] titles = new String[]{"来源", "联盟卡号","手机号", "消费金额（元）", "实收金额（元）", "优惠项目", "创建时间"};
			String[] contentName = new String[]{"enterpriseName", "cardNo", "phone", "totalMoney", "payMoney", "serviceNames", "createTime"};
			UnionMain main = unionMainService.getById(unionId);
			String filename = main.getUnionName() + "的他店消费记录";
			HSSFWorkbook wb = exportService.exportConsumeToDetail(titles,contentName,list);
			ExportUtil.responseExport(response, wb, filename);
		} catch (BaseException e){
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(EXPORT_CONSUME_TO_DETAIL,e.getMessage(),"导出失败").toString();
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
			String result = GTJsonResult.instanceErrorMsg(EXPORT_CONSUME_TO_DETAIL,e.getMessage(),"导出失败").toString();
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			logger.error("",e);
		}
	}
}
