package com.gt.union.controller.brokerage;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.ParameterException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.entity.brokerage.UnionBrokerageWithdrawalsRecord;
import com.gt.union.entity.brokerage.UnionVerifyMember;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.brokerage.IUnionVerifyMemberService;
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
	 * 获取商家的佣金平台管理员
	 * @param page
	 * @param request
	 * @return
	 */
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
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
	public String delUnionVerifyMember(HttpServletRequest request,@PathVariable("id") Integer id){
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
	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String saveUnionVerifyMember(HttpServletRequest request, @RequestBody UnionVerifyMember unionVerifyMember){
		BusUser user = SessionUtils.getLoginUser(request);
		try{
			unionVerifyMember.setBusId(user.getId());
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
	@RequestMapping(value = "/getPhoneCode", produces = "application/json;charset=UTF-8")
	public String getPhoneCode(HttpServletRequest request, HttpServletResponse response, @RequestBody String phone) {
		BusUser user = SessionUtils.getLoginUser(request);
		String code = RandomKit.getRandomString(6, 0);
		try {
			if (phone != null) {
				//生成验证码
				Map<String,Object> smsMap = new HashMap<>();
				smsMap.put("mobiles",phone);
				smsMap.put("content","佣金平台管理员验证码:" + code.toString());
				smsMap.put("model",33);
				smsMap.put("busId",user.getId());
				smsMap.put("company",user.getName());
				//TODO 发送短信接口

			}
		} catch (Exception e) {
			logger.error("发送佣金平台管理员验证码错误");
			return GTJsonResult.instanceErrorMsg("发送失败").toString();
		}
		return GTJsonResult.instanceSuccessMsg(code,null,"发送成功").toString();
	}


}
