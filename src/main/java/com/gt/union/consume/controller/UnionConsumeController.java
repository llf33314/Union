package com.gt.union.consume.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.consume.service.IUnionConsumeService;
import com.gt.union.consume.vo.UnionConsumeParamVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
	 *
	 * @param request
	 * @param vo
	 * @return
	 */
	@ApiOperation(value = "根据联盟卡核销", produces = "application/json;charset=UTF-8")
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
