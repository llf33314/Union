package com.gt.union.h5.card.service.impl;

import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.service.IUnionCardFanService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.h5.card.service.IH5CardService;
import com.gt.union.h5.card.vo.CardDetailVO;
import com.gt.union.h5.card.vo.IndexVO;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hongjiye
 * @time 2017-12-19 11:38
 **/
@Service
public class H5CardServiceImpl implements IH5CardService {

	@Autowired
	private IUnionMainService unionMainService;

	@Autowired
	private IUnionMemberService unionMemberService;

	private IUnionCardFanService unionCardFanService;

	@Override
	public IndexVO getIndexVO(String phone, Integer busId) throws Exception {
		if (CommonUtil.isEmpty(busId)) {
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		if(CommonUtil.isEmpty(phone)){
			//没有手机号
		}else {
			//有手机号
			UnionCardFan fan = unionCardFanService.getByPhone(phone);
		}
		return null;
	}

	@Override
	public CardDetailVO getCardDetail(String phone, Integer busId, Integer unionId, Integer activityId) {
		return null;
	}
}
