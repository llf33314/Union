package com.gt.union.controller.business;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.business.UnionBrokerage;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionMemberService;
import com.gt.union.service.business.IUnionBrokerageService;
import com.gt.union.vo.business.UnionBusinessRecommendVo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 联盟商家佣金比率 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@Controller
@RequestMapping("/unionBrokerage")
public class UnionBrokerageController {

	private Logger logger = Logger.getLogger(UnionBusinessRecommendController.class);

	@Autowired
	private IUnionMemberService unionMemberService;

	@Autowired
	private IUnionBrokerageService unionBrokerageService;


	/**
	 * 获取联盟佣金比例列表
	 * @param page
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
	public String unionBrokerage(Page page, HttpServletRequest request, @RequestParam(name = "unionId", required = true) Integer unionId){
		BusUser user = SessionUtils.getLoginUser(request);
		try{
			Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			page = unionMemberService.selectUnionBrokerageList(page,unionId,busId);
		}catch (BaseException e){
			logger.error("获取联盟佣金比例列表错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
		}catch (Exception e){
			logger.error("获取联盟佣金比例列表错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg("获取盟员联盟卡列表错误").toString();
		}
		return GTJsonResult.instanceSuccessMsg(page,null,"获取联盟佣金比例列表成功").toString();
	}

	/**
	 *	更新盟员佣金比
	 * @param request
	 * @param id	盟员id
	 * @param unionBrokerage
	 * @return
	 */
	@RequestMapping("/{id}")
	public String updateUnionBrokerage(HttpServletRequest request, @PathVariable Integer id, @RequestBody UnionBrokerage unionBrokerage){
		BusUser user = SessionUtils.getLoginUser(request);
		try{
			Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			unionBrokerage.setFromBusId(busId);
			unionBrokerageService.updateUnionBrokerage(unionBrokerage);
		}catch (BaseException e){
			logger.error("更新盟员佣金比错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
		}catch (Exception e){
			logger.error("更新盟员佣金比错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg("设置失败").toString();
		}
		return GTJsonResult.instanceSuccessMsg(null,null,"设置成功").toString();
	}
}
