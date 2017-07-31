package com.gt.union.controller.business;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.business.IUnionBrokerageService;
import com.gt.union.vo.business.UnionBusinessRecommendVo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
			page = unionBrokerageService.selectUnionBrokerageList(page,unionId);
		}catch (BaseException e){
			logger.error("获取盟员联盟卡列表错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
		}catch (Exception e){
			logger.error("获取盟员联盟卡列表错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg("获取盟员联盟卡列表错误").toString();
		}
		return GTJsonResult.instanceSuccessMsg(page,null,"获取商家的佣金平台管理员成功").toString();
	}
}
