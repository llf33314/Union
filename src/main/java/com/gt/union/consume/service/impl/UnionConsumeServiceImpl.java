package com.gt.union.consume.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.entity.param.UnionConsumeParam;
import com.gt.union.api.entity.param.UnionRefundParam;
import com.gt.union.api.entity.result.UnionConsumeResult;
import com.gt.union.api.entity.result.UnionRefundResult;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.consume.entity.UnionConsume;
import com.gt.union.consume.mapper.UnionConsumeMapper;
import com.gt.union.consume.service.IUnionConsumeService;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 消费 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionConsumeServiceImpl extends ServiceImpl<UnionConsumeMapper, UnionConsume> implements IUnionConsumeService {

	@Autowired
	private IUnionMemberService unionMemberService;

	@Autowired
	private IUnionMainService unionMainService;

	@Autowired
	private IDictService dictService;

	@Override
	public UnionConsumeResult consumeByUnionCard(UnionConsumeParam unionConsumeParam) throws Exception{
		UnionMember unionMember = unionMemberService.getByUnionIdAndBusId(unionConsumeParam.getUnionId(), unionConsumeParam.getBusId());

		UnionConsume unionConsume = new UnionConsume();
		unionConsume.setCreatetime(new Date());
		unionConsume.setDelStatus(CommonConstant.DEL_STATUS_NO);
		unionConsume.setModel(unionConsumeParam.getModel());//行业模型
		unionConsume.setModelDesc(unionConsumeParam.getModelDesc());//行业描述
		unionConsume.setOrderNo(unionConsumeParam.getOrderNo());//订单号
		unionConsume.setPayType(unionConsumeParam.getOrderType());//支付类型 (0：现金 1：微信 2：支付宝)
		unionConsume.setType(1);//线上支付
		unionConsume.setStatus(unionConsumeParam.getStatus());//支付状态
		unionConsume.setConsumeMoney(unionConsumeParam.getTotalMoney());//联盟折扣打折前价格
		unionConsume.setPayMoney(unionConsumeParam.getPayMoney());//折后支付价格
		unionConsume.setCardId(unionConsumeParam.getUnionCardId());//联盟卡id
		unionConsume.setIsIntegral(0);//是否赠送积分 0未赠送 1赠送
		unionConsume.setMemberId(unionMember.getId());
		//线上积分赠送积分
		if(CommonUtil.isNotEmpty(unionConsumeParam.getGiveIntegralNow()) && unionConsumeParam.getGiveIntegralNow()){
			UnionMain main = unionMainService.getById(unionConsumeParam.getUnionId());
			/*if(main.getIsIntegral() != null && main.getIsIntegral() == 1){//开启赠送积分
				double integral = dictService.getGiveIntegral();
				BigDecimalUtil.multiply(unionConsume.getPayMoney(), integral).doubleValue();
				integral = unionConsume.getPayMoney().intValue() * integral;
				UnionBusMemberCard card = unionBusMemberCardMapper.selectByPrimaryKey(CommonUtil.toInteger(params.get("cardId")));
				card.setIntegral(CommonUtil.isEmpty(card.getIntegral()) ? integral : card.getIntegral() + integral);
				unionBusMemberCardMapper.updateByPrimaryKeySelective(card);
				unionConsume.setIsGiveIntegral(1);
			}*/
		}
		this.insert(unionConsume);
		UnionConsumeResult result = new UnionConsumeResult();
		result.setMessage("核销成功");
		result.setSuccess(true);
		return result;
	}

	@Override
	public UnionRefundResult unionRefund(UnionRefundParam reqdata) {
		return null;
	}
}
