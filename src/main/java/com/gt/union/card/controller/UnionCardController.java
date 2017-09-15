package com.gt.union.card.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.card.service.IUnionCardService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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


	@ApiOperation(value = "获取盟员的联盟卡列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String listByUnionId(Page page, HttpServletRequest request
			, @ApiParam(name="unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId
			, @ApiParam(name="phone", value = "电话号码，模糊匹配", required = false) @RequestParam(name = "phone", required = false) String phone
			, @ApiParam(name="cardNo", value = "联盟卡号，模糊匹配", required = false) @RequestParam(name = "cardNo", required = false) String cardNo){
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			Page result = unionCardService.selectListByUnionId(page, unionId, busId, cardNo, phone);
			return GTJsonResult.instanceSuccessMsg(result).toString();
		}catch (BaseException e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		}catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}

	@ApiOperation(value = "根据联盟卡号、手机号、扫码枪扫出的号码", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionCardInfo", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String unionCardInfo(HttpServletRequest request
			,@ApiParam(name="no", value = "联盟卡号、手机号、扫码枪扫出的号码", required = true) @RequestParam("no") String no
			,@ApiParam(name="unionId", value = "联盟卡号、手机号、扫码枪扫出的号码", required = false) @RequestParam("unionId") Integer unionId){
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			Map<String,Object> data = unionCardService.getUnionCardInfo(no, user.getId());//解码后的no
			return GTJsonResult.instanceSuccessMsg(data).toString();
		}catch (BaseException e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorMsg()).toString();
		}catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
		}
	}
}
