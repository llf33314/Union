package com.gt.union.controller.card;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.card.IUnionBusMemberCardService;
import com.gt.union.vo.card.UnionBusMemberCardVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 联盟商家升级会员联盟卡 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@RestController
@RequestMapping("/unionBusMemberCard")
public class UnionBusMemberCardController {

	private Logger logger = Logger.getLogger(UnionBusMemberCardController.class);

	@Autowired
	private IUnionBusMemberCardService unionBusMemberCardService;

	/**
	 * 获取盟员的联盟卡列表
	 * @param page
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "获取盟员的联盟卡列表", notes = "根据unionId获取盟员的联盟卡列表，可模糊搜索电话号码、联盟卡号关键字", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.GET,produces = "application/json;charset=UTF-8")
	public String unionBusMemberCard(Page page, HttpServletRequest request,@ApiParam(name="unionId", value = "联盟id", required = true) @RequestParam(name = "unionId", required = true) Integer unionId
			,@ApiParam(name="phone", value = "电话号码", required = false) @RequestParam(name = "phone", required = false) String phone
			,@ApiParam(name="cardNo", value = "联盟卡号", required = false) @RequestParam(name = "cardNo", required = false) String cardNo){
		BusUser user = SessionUtils.getLoginUser(request);
		Page result = null;
		try{
			Integer busId = user.getId();
			if(user.getPid() != null && user.getPid() != 0){
				busId = user.getPid();
			}
			UnionBusMemberCardVO vo = new UnionBusMemberCardVO();
			vo.setUnionId(unionId);
			vo.setBusId(busId);
			vo.setCardNo(cardNo);
			vo.setPhone(phone);
			result = unionBusMemberCardService.selectUnionBusMemberCardList(page,vo);
		}catch (BaseException e){
			logger.error("获取盟员联盟卡列表错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg(e.getMessage()).toString();
		}catch (Exception e){
			logger.error("获取盟员联盟卡列表错误"+e.getMessage());
			return GTJsonResult.instanceErrorMsg("获取盟员联盟卡列表错误").toString();
		}
		return GTJsonResult.instanceSuccessMsg(result,null,"获取盟员联盟卡列表成功").toString();
	}
}
