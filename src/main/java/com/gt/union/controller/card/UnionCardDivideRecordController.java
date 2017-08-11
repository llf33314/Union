package com.gt.union.controller.card;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.card.IUnionCardDivideRecordService;
import com.gt.union.vo.card.UnionCardDivideRecordVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 商家售卡分成记录 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@RestController
@RequestMapping("/unionCardDivideRecord")
public class UnionCardDivideRecordController {
	//TODO RESTFUL
	private static final String UNION_CARD_DIVIDE_RECORD_LIST = "UnionCardDivideRecordController.unionCardDivideRecordList()";
	private Logger logger = Logger.getLogger(UnionCardDivideRecordController.class);

	@Autowired
	private IUnionCardDivideRecordService unionCardDivideRecordService;

	/**
	 * 获取收卡分成列表
	 * @param page
	 * @param request
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String unionCardDivideRecordList(Page page, HttpServletRequest request, @RequestBody UnionCardDivideRecordVO vo) {
		try {
			BusUser user = SessionUtils.getLoginUser(request);
			page = unionCardDivideRecordService.getUnionCardDivideRecordList(page, vo);
			return GTJsonResult.instanceSuccessMsg(page).toString();
		} catch (BaseException e) {
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(e.getErrorLocation(), e.getErrorCausedBy(), e.getErrorMsg()).toString();
		}catch (Exception e){
			logger.error("", e);
			return GTJsonResult.instanceErrorMsg(UNION_CARD_DIVIDE_RECORD_LIST, e.getMessage(), ExceptionConstant.OPERATE_FAIL).toString();
		}
	}
}
