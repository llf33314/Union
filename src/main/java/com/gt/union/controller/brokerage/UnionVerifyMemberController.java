package com.gt.union.controller.brokerage;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.ParameterException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.entity.brokerage.UnionBrokerageWithdrawalsRecord;
import com.gt.union.entity.brokerage.UnionVerifyMember;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.brokerage.IUnionVerifyMemberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟佣金平台管理人员 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@Controller
@RequestMapping("/unionVerifyMember")
public class UnionVerifyMemberController {

	private Logger logger = Logger.getLogger(UnionVerifyMemberController.class);

	@Autowired
	private IUnionVerifyMemberService unionVerifyMemberService;



	/**
	 * 获取商家的佣金平台管理员列表
	 * @param page
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "获取商家的佣金平台管理员列表", notes = "获取商家的佣金平台管理员列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String unionVerifyMember(Page<UnionVerifyMember> page, HttpServletRequest request){
		BusUser user = SessionUtils.getLoginUser(request);
		try{
			Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			EntityWrapper<UnionVerifyMember> wrapper = new EntityWrapper<UnionVerifyMember>();
			wrapper.eq("bus_id", busId);
			wrapper.eq("del_status", 0);
			page = unionVerifyMemberService.selectPage(page,wrapper);
		}catch (Exception e){
			logger.error("获取商家的佣金平台管理员错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg("获取商家的佣金平台管理员错误").toString();
		}
		return GTJsonResult.instanceSuccessMsg(page,null,"获取商家的佣金平台管理员成功").toString();
	}

	/**
	 * 删除佣金平台管理员
	 * @param request
	 * @param id
	 * @return
	 */
	@SysLogAnnotation(op_function = "4", description = "删除佣金平台管理员")
	@ApiOperation(value = "删除佣金平台管理员", notes = "删除佣金平台管理员", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
	public String delUnionVerifyMember(HttpServletRequest request, @ApiParam(name="id", value = "管理员id", required = true) @PathVariable("id") Integer id){
		try{
			unionVerifyMemberService.delUnionVerifyMember(id);
		}catch (Exception e){
			logger.error("删除商家的佣金平台管理员错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg("删除失败").toString();
		}
		return GTJsonResult.instanceSuccessMsg(null,null,"删除成功").toString();
	}


	/**
	 * 保存佣金平台管理员
	 * @param request
	 * @param unionVerifyMember
	 * @return
	 */
	@SysLogAnnotation(op_function = "2", description = "保存佣金平台管理员")
	@ApiOperation(value = "保存佣金平台管理员", notes = "保存佣金平台管理员", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String saveUnionVerifyMember(HttpServletRequest request, @ApiParam(name="unionVerifyMember", value = "平台管理员信息", required = true) @RequestBody UnionVerifyMember unionVerifyMember){
		BusUser user = SessionUtils.getLoginUser(request);
		try{
			Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			unionVerifyMember.setBusId(busId);
			unionVerifyMember.setCreatetime(new Date());
			unionVerifyMember.setDelStatus(0);
			unionVerifyMemberService.saveUnionVerifyMember(unionVerifyMember);
		}catch (BaseException e){
			logger.error("保存商家的佣金平台管理员错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
		}catch (Exception e){
			logger.error("保存商家的佣金平台管理员错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg("保存失败").toString();
		}
		return GTJsonResult.instanceSuccessMsg(null,null,"保存成功").toString();
	}

	/**
	 *	获取佣金平台管理员验证码
	 * @param request
	 * @param response
	 * @param phone
	 */
	@SysLogAnnotation(op_function = "1", description = "获取佣金平台管理员验证码")
	@ApiOperation(value = "获取佣金平台管理员验证码", notes = "获取佣金平台管理员验证码", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/phoneCode", produces = "application/json;charset=UTF-8")
	public String getPhoneCode(HttpServletRequest request, HttpServletResponse response,
							   @ApiParam(name="phone", value = "手机号", required = true) @RequestParam String phone) {
		BusUser user = SessionUtils.getLoginUser(request);
		//生成验证码
		String code = RandomKit.getRandomString(6, 0);
		try {
			Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			if (CommonUtil.isNotEmpty(phone)) {
				//TODO 发送短信接口
				SmsUtil.sendMsg(phone,33,busId, "佣金平台管理员验证码:" + code.toString(), user.getName());
			}
		} catch (Exception e) {
			logger.error("发送佣金平台管理员验证码错误");
			return GTJsonResult.instanceErrorMsg("发送失败").toString();
		}
		return GTJsonResult.instanceSuccessMsg(code,null,"发送成功").toString();
	}


}
