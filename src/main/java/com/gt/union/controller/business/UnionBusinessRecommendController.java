package com.gt.union.controller.business;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.business.UnionBusinessRecommendConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.business.UnionBusinessRecommend;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.business.IUnionBusinessRecommendService;
import com.gt.union.vo.business.UnionBusinessRecommendFormVo;
import com.gt.union.vo.business.UnionBusinessRecommendVo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 联盟商家商机推荐 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@Controller
@RequestMapping("/unionBusinessRecommend")
public class UnionBusinessRecommendController {

	private Logger logger = Logger.getLogger(UnionBusinessRecommendController.class);

	@Autowired
	private IUnionBusinessRecommendService unionBusinessRecommendService;

	/**
	 * 获取盟员的联盟卡列表  TODO 多状态查询 排序
	 * @param page
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
	public String unionBusinessRecommend(Page page, HttpServletRequest request, @RequestParam(name = "unionId", required = true) Integer unionId
			, @RequestParam(name = "phone", required = false) String phone
			, @RequestParam(name = "isAcceptance", required = false) Integer isAcceptance){
		BusUser user = SessionUtils.getLoginUser(request);
		try{
			Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			UnionBusinessRecommendVo vo = new UnionBusinessRecommendVo();
			vo.setUnionId(unionId);
			vo.setBusId(busId);
			vo.setPhone(phone);
			vo.setIsAcceptance(isAcceptance);
			page = unionBusinessRecommendService.selectUnionBusinessRecommendList(page,vo);
		}catch (BaseException e){
			logger.error("获取盟员联盟卡列表错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
		}catch (Exception e){
			logger.error("获取盟员联盟卡列表错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg("获取盟员联盟卡列表错误").toString();
		}
		return GTJsonResult.instanceSuccessMsg(page,null,"获取商家的佣金平台管理员成功").toString();
	}


	/**
	 * 审核
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	public String verifyUnionBusinessRecommend(HttpServletRequest request,@PathVariable("id") Integer id, @RequestBody Integer isAcceptance){
		BusUser user = SessionUtils.getLoginUser(request);
		try{
			UnionBusinessRecommend recommend = new UnionBusinessRecommend();
			recommend.setId(id);
			recommend.setIsAcceptance(isAcceptance);
			unionBusinessRecommendService.updateVerifyRecommend(recommend);
			if(isAcceptance == 1){
				//接受
				return GTJsonResult.instanceSuccessMsg(null,null, UnionBusinessRecommendConstant.UNION_BUSINESS_RECOMMEND_ACCEPT_SUCCESS).toString();
			}else if(isAcceptance == 2){
				//拒绝
				return GTJsonResult.instanceSuccessMsg(null,null,UnionBusinessRecommendConstant.UNION_BUSINESS_RECOMMEND_REFUND_SUCCESS).toString();
			}
			return GTJsonResult.instanceErrorMsg("审核失败").toString();
		}catch (BaseException e){
			logger.error("审核商机消息错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
		}catch (Exception e){
			logger.error("审核商机消息错误"+e.getMessage());
			if(isAcceptance == 1){
				//接受
				return GTJsonResult.instanceErrorMsg(UnionBusinessRecommendConstant.UNION_BUSINESS_RECOMMEND_ACCEPT_FAILED).toString();
			}else if(isAcceptance == 2){
				//拒绝
				return GTJsonResult.instanceErrorMsg(UnionBusinessRecommendConstant.UNION_BUSINESS_RECOMMEND_REFUND_FAILED).toString();
			}
			return GTJsonResult.instanceErrorMsg("审核失败").toString();
		}
	}

	/**
	 * 添加商机推荐
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String saveUnionBusinessRecommend(HttpServletRequest request, @RequestBody UnionBusinessRecommendFormVo vo){
		BusUser user = SessionUtils.getLoginUser(request);
		try{
			Integer busId = user.getId();
			vo.setBusId(busId);
			unionBusinessRecommendService.saveUnionBusinessRecommend(vo);
		}catch (BaseException e){
			logger.error("添加商机推荐错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
		}catch (Exception e){
			logger.error("添加商机推荐错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg("添加失败").toString();
		}
		return GTJsonResult.instanceSuccessMsg(null,null,"添加成功").toString();
	}

}
