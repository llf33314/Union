package com.gt.union.controller.card;

import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.SessionUtils;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.common.BusUser;
import com.gt.union.service.card.IUnionCardDivideRecordService;
import com.gt.union.vo.card.UnionCardDivideRecordVo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商家售卡分成记录 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@Controller
@RequestMapping("/unionCardDivideRecord")
public class UnionCardDivideRecordController {

	private Logger logger = Logger.getLogger(UnionCardDivideRecordController.class);

	@Autowired
	private IUnionCardDivideRecordService unionCardDivideRecordService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String unionCardDivideRecordList(Page page, HttpServletRequest request, @RequestBody UnionCardDivideRecordVo vo) {
		BusUser user = SessionUtils.getLoginUser(request);
		try {
			page = unionCardDivideRecordService.getUnionCardDivideRecordList(page,vo);
		}catch (Exception e){
			logger.error("获取联盟列表失败");
			return GTJsonResult.instanceSuccessMsg(page,null,"获取联盟信息成功").toString();
		}
		return GTJsonResult.instanceSuccessMsg(page,null,"获取联盟信息成功").toString();
	}
}
