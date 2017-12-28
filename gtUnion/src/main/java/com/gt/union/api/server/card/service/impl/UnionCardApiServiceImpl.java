package com.gt.union.api.server.card.service.impl;

import com.gt.union.api.server.card.service.IUnionCardApiService;
import com.gt.union.api.entity.result.UnionDiscountResult;
import com.gt.union.api.server.constant.UnionDiscountConstant;
import com.gt.union.card.main.constant.CardConstant;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.service.IUnionCardFanService;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author hongjiye
 * @time 2017-12-22 10:29
 **/
@Service
public class UnionCardApiServiceImpl implements IUnionCardApiService {

	private Logger logger = LoggerFactory.getLogger(UnionCardApiServiceImpl.class);

	@Autowired
	private IUnionMemberService unionMemberService;

	@Autowired
	private IUnionCardFanService unionCardFanService;

	@Autowired
	private IUnionCardService unionCardService;

	@Override
	public UnionDiscountResult getConsumeUnionCardDiscount(String phone, Integer busId) throws Exception {
		logger.info("手机端获取联盟折扣。手机号：{}，商家id：{}", phone, busId);
		UnionDiscountResult result = new UnionDiscountResult();
		if (CommonUtil.isEmpty(phone) || CommonUtil.isEmpty(busId)) {
			result.setCode(UnionDiscountConstant.UNION_DISCOUNT_CODE_NON);
			return result;
		}

		//1、判断商家加入的有效联盟
		List<UnionMember> members = unionMemberService.listValidWriteByBusId(busId);
		if (ListUtil.isEmpty(members)) {
			result.setCode(UnionDiscountConstant.UNION_DISCOUNT_CODE_NON);
			return result;
		}

		//2、判断手机号是否升级过联盟卡
		UnionCardFan fan = unionCardFanService.getValidByPhone(phone);
		if(fan == null){
			result.setCode(UnionDiscountConstant.UNION_DISCOUNT_CODE_NON);
			return result;
		}


		//3、获取最低折扣
		List<UnionDiscountResult> discountList = new ArrayList<UnionDiscountResult>();
		for(UnionMember member : members){
			List<UnionCard> cards = unionCardService.listValidUnexpiredByUnionIdAndFanIdAndType(member.getUnionId(), fan.getId(), CardConstant.TYPE_DISCOUNT);
			if(ListUtil.isNotEmpty(cards)){
				if(CommonUtil.isNotEmpty(member.getDiscount()) && member.getDiscount() > 0 && member.getDiscount() < 1){
					UnionDiscountResult data = new UnionDiscountResult();
					data.setCardId(cards.get(0).getId());
					data.setDiscount(member.getDiscount());
					discountList.add(data);
				}
			}
		}
		if(ListUtil.isNotEmpty(discountList)){
			Collections.sort(discountList, new Comparator<UnionDiscountResult>(){
				@Override
				public int compare(UnionDiscountResult o1, UnionDiscountResult o2) {
					return o1.getDiscount().compareTo(o2.getDiscount());
				}
			});
			result = discountList.get(0);
			result.setDiscount(result.getDiscount() * 10);
			result.setCode(UnionDiscountConstant.UNION_DISCOUNT_CODE_SUCCESS);
			return result;
		}else {
			result.setCode(UnionDiscountConstant.UNION_DISCOUNT_CODE_NON);
			return result;
		}

	}
}
