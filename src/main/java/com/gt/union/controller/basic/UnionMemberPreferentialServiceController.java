package com.gt.union.controller.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.basic.IUnionMemberPreferentialServiceService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 盟员优惠服务项 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@Controller
@RequestMapping("/unionMemberPreferentialService")
public class UnionMemberPreferentialServiceController {

	private Logger logger = Logger.getLogger(UnionMemberPreferentialServiceController.class);

	@Autowired
	private IUnionMemberPreferentialServiceService unionMemberPreferentialServiceService;

	/**
	 * 联盟优惠服务
	 * @param page
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String unionMemberPreferentialServiceList(Page page, HttpServletRequest request) {
		BusUser user = SessionUtils.getLoginUser(request);
		try {

		}catch (Exception e){
			logger.error("获取联盟列表失败");
			return GTJsonResult.instanceSuccessMsg(page,null,"获取联盟信息成功").toString();
		}
		return GTJsonResult.instanceSuccessMsg(page,null,"获取联盟信息成功").toString();
	}
}
