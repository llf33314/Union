package com.gt.union.controller.card;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.card.vo.UnionBusMemberCardVO;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.card.IUnionBusMemberCardService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
	private static final String LIST_UNIONID = "UnionBusMemberCardController.listByUnionId()";
	private static final String UNION_CARD_INFO = "UnionBusMemberCardController.unionCardInfo()";


	private Logger logger = Logger.getLogger(UnionBusMemberCardController.class);

	@Autowired
	private IUnionBusMemberCardService unionBusMemberCardService;

	@ApiOperation(value = "获取盟员的联盟卡列表", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionId/{unionId}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String listByUnionId(Page page, HttpServletRequest request
			,@ApiParam(name="unionId", value = "联盟id", required = true) @PathVariable("unionId") Integer unionId
			,@ApiParam(name="phone", value = "电话号码，模糊匹配", required = false) @RequestParam(name = "phone", required = false) String phone
			,@ApiParam(name="cardNo", value = "联盟卡号，模糊匹配", required = false) @RequestParam(name = "cardNo", required = false) String cardNo){
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			Integer busId = user.getId();
            if(user.getPid() != null && user.getPid() != 0){
                busId = user.getPid();
            }
            UnionBusMemberCardVO vo = new UnionBusMemberCardVO();
            vo.setUnionId(unionId);
            vo.setBusId(busId);
            vo.setCardNo(cardNo);
            vo.setPhone(phone);
            Page result = unionBusMemberCardService.selectUnionBusMemberCardList(page, unionId, busId, cardNo, phone);
            return GTJsonResult.instanceSuccessMsg(result).toString();
		}catch (BaseException e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		}catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(LIST_UNIONID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
		}
	}

	@ApiOperation(value = "根据联盟卡号、手机号、扫码枪扫出的号码", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/unionCardInfo", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String unionCardInfo(HttpServletRequest request
			,@ApiParam(name="no", value = "联盟卡号、手机号、扫码枪扫出的号码", required = true) @RequestParam("no") String no){
		try{
			BusUser user = SessionUtils.getLoginUser(request);
			Map<String,Object> data = unionBusMemberCardService.getUnionCardInfo(no, user.getId());
			return GTJsonResult.instanceSuccessMsg(data).toString();
		}catch (BaseException e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		}catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(LIST_UNIONID, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
		}
	}

}
