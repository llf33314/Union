package com.gt.union.consume.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.DataExportException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.ExportUtil;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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

	@ApiOperation(value = "查询本店消费核销记录", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/my", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String listMyByUnionId(HttpServletRequest request, Page page
			, @ApiParam(name = "unionId", value = "联盟id", required = false)
								  @PathVariable("unionId") Integer unionId
			, @ApiParam(name = "memberId", value = "来源", required = false)
								  @RequestParam(name = "memberId", required = false) Integer memberId
			, @ApiParam(name = "cardNo", value = "联盟卡号，模糊查询", required = false)
								  @RequestParam(name = "cardNo", required = false) String cardNo
			, @ApiParam(name = "phone", value = "手机号，模糊查询", required = false)
								  @RequestParam(name = "phone", required = false) String phone
			, @ApiParam(name = "beginTime", value = "开始时间", required = false)
								  @RequestParam(name = "beginTime", required = false) String beginTime
			, @ApiParam(name = "endTime", value = "结束时间", required = false)
								  @RequestParam(name = "endTime", required = false) String endTime){
		try {
			BusUser user = SessionUtils.getLoginUser(request);
			Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			Page result = this.unionConsumeService.listMy(page, unionId, busId, memberId, cardNo, phone, beginTime, endTime);
			return GTJsonResult.instanceSuccessMsg(result).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "查询它店消费核销记录", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/other", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String listOtherByUnionId(HttpServletRequest request, Page page
			, @ApiParam(name = "unionId", value = "联盟id", required = false)
									 @PathVariable("unionId") Integer unionId
			, @ApiParam(name = "memberId", value = "来源", required = false)
									 @RequestParam(name = "memberId", required = false) Integer memberId
			, @ApiParam(name = "cardNo", value = "联盟卡号，模糊查询", required = false)
									 @RequestParam(name = "cardNo", required = false) String cardNo
			, @ApiParam(name = "phone", value = "手机号，模糊查询", required = false)
									 @RequestParam(name = "phone", required = false) String phone
			, @ApiParam(name = "beginTime", value = "开始时间", required = false)
									 @RequestParam(name = "beginTime", required = false) String beginTime
			, @ApiParam(name = "endTime", value = "结束时间", required = false)
									 @RequestParam(name = "endTime", required = false) String endTime){
		try {
			BusUser user = SessionUtils.getLoginUser(request);
			Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			Page result = this.unionConsumeService.listOther(page, unionId, busId, memberId, cardNo, phone, beginTime, endTime);
			return GTJsonResult.instanceSuccessMsg(result).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg( e.getErrorMsg()).toString();
		} catch (Exception e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
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
										@ApiParam(name="unionId", value = "联盟id", required = true) @RequestParam(name = "unionId", required = true) Integer unionId
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


	@ApiOperation(value = "根据联盟卡核销", produces = "application/json;charset=UTF-8")
	@SysLogAnnotation(op_function = "3", description = "根据联盟卡核销")
	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String consumeByCard(HttpServletRequest request
			,@ApiParam(name="unionConsumeParamVO", value = "联盟卡核销参数", required = true) @RequestBody UnionConsumeParamVO vo ){
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			unionConsumeService.consumeByCard(busId, vo);
			return GTJsonResult.instanceSuccessMsg().toString();
		}catch (BaseException e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		}catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

}
