package com.gt.union.api.server.consume.service.impl;

import com.alibaba.fastjson.JSON;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.shop.ShopService;
import com.gt.union.api.entity.param.UnionConsumeParam;
import com.gt.union.api.server.consume.service.IUnionConsumeApiService;
import com.gt.union.api.server.entity.result.UnionRefundResult;
import com.gt.union.card.consume.constant.ConsumeConstant;
import com.gt.union.card.consume.entity.UnionConsume;
import com.gt.union.card.consume.service.IUnionConsumeService;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.card.main.entity.UnionCardIntegral;
import com.gt.union.card.main.service.IUnionCardIntegralService;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import com.gt.util.entity.result.shop.WsWxShopInfoExtend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author hongjiye
 * @time 2017-12-22 10:56
 **/
@Service
public class UnionConsumeApiServiceImpl implements IUnionConsumeApiService {

	private Logger logger = LoggerFactory.getLogger(UnionConsumeApiServiceImpl.class);

	@Autowired
	private IUnionCardService unionCardService;

	@Autowired
	private IUnionConsumeService unionConsumeService;

	@Autowired
	private IUnionCardIntegralService unionCardIntegralService;

	@Autowired
	private IUnionMainService unionMainService;

	@Autowired
	private IDictService dictService;

	@Autowired
	private IUnionMemberService unionMemberService;

	@Autowired
	private ShopService shopService;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void consumeByUnionCard(UnionConsumeParam unionConsumeParam) throws Exception{
		logger.info("API联盟卡核销，参数：{}", JSON.toJSONString(unionConsumeParam));
		if(unionConsumeParam.getUnionCardId() == null){
			throw new ParamException("unionCardId为空");
		}
		if(unionConsumeParam.getBusId() == null){
			throw new ParamException("busId为空");
		}
		if(unionConsumeParam.getModel() == null){
			throw new ParamException("model为空");
		}
		if(unionConsumeParam.getOrderNo() == null){
			throw new ParamException("orderNo为空");
		}
		if(unionConsumeParam.getPayMoney() == null){
			throw new ParamException("payMoney为空");
		}
		if(unionConsumeParam.getTotalMoney() == null){
			throw new ParamException("totalMoney为空");
		}
		if(unionConsumeParam.getOrderType() == null){
			throw new ParamException("orderType为空");
		}
		UnionCard card = unionCardService.getById(unionConsumeParam.getUnionCardId());
		if(card == null){
			throw new BusinessException("联盟卡不存在");
		}

		UnionConsume consume = unionConsumeService.getValidByOrderNoAndModel(unionConsumeParam.getOrderNo(), unionConsumeParam.getModel());
		if (consume != null) {
			throw new BusinessException("订单已存在");
		}
		UnionConsume unionConsume = new UnionConsume();
		unionConsume.setCreateTime(new Date());
		unionConsume.setDelStatus(CommonConstant.DEL_STATUS_NO);
		unionConsume.setBusinessType(unionConsumeParam.getModel());//行业模型
		unionConsume.setBusinessDesc(unionConsumeParam.getModelDesc());//行业描述
		unionConsume.setSysOrderNo(unionConsumeParam.getOrderNo());//订单号
		unionConsume.setPayType(unionConsumeParam.getOrderType() == 4 ? Integer.valueOf(0) : unionConsumeParam.getOrderType());//支付类型 (0：现金 1：微信 2：支付宝)
		unionConsume.setType(1);//线上支付
		unionConsume.setPayStatus(unionConsumeParam.getStatus());//支付状态
		unionConsume.setConsumeMoney(unionConsumeParam.getTotalMoney());//联盟折扣打折前价格
		unionConsume.setPayMoney(unionConsumeParam.getPayMoney());//折后支付价格
		unionConsume.setCardId(unionConsumeParam.getUnionCardId());//联盟卡id
		unionConsume.setFanId(card.getFanId());
		unionConsume.setMemberId(card.getMemberId());
		unionConsume.setUnionId(card.getUnionId());
		unionConsume.setDiscountMoney(unionConsumeParam.getTotalMoney() - unionConsumeParam.getPayMoney());
		unionConsume.setMemberId(card.getMemberId());
		UnionMember unionMember = unionMemberService.getById(card.getMemberId());
		unionConsume.setDiscount(CommonUtil.isNotEmpty(unionMember.getDiscount()) ? unionMember.getDiscount() : 0);
		if(CommonUtil.isNotEmpty(unionConsumeParam.getShopId())){
			WsWxShopInfoExtend shop = shopService.getById(unionConsumeParam.getShopId());
			if(shop != null){
				unionConsume.setShopId(unionConsumeParam.getShopId());
				unionConsume.setShopName(shop.getBusinessName());
			}
		}
		//线上积分赠送积分
		if (CommonUtil.isNotEmpty(unionConsumeParam.getGiveIntegralNow()) && unionConsumeParam.getGiveIntegralNow()) {//立即赠送
			UnionMain main = unionMainService.getById(card.getUnionId());
			if(unionMainService.isUnionValid(main)){
				//开启积分
				if (main.getIsIntegral() != null && main.getIsIntegral() == 1) {
					UnionCardIntegral cardIntegral = unionCardIntegralService.getValidByUnionIdAndFanId(card.getUnionId(), card.getFanId());
					double integral = dictService.getGiveIntegral();
					//获得的积分
					double getIntegral = BigDecimalUtil.toDouble(BigDecimalUtil.multiply(unionConsume.getPayMoney(), integral));
					if(cardIntegral != null){
						UnionCardIntegral uci = new UnionCardIntegral();
						uci.setId(cardIntegral.getId());
						uci.setIntegral(CommonUtil.isNotEmpty(cardIntegral.getIntegral()) ? (cardIntegral.getIntegral() + getIntegral) : getIntegral);
						unionCardIntegralService.update(uci);
					}else {
						UnionCardIntegral uci = new UnionCardIntegral();
						uci.setIntegral(getIntegral);
						uci.setCreateTime(new Date());
						uci.setUnionId(card.getUnionId());
						uci.setDelStatus(CommonConstant.DEL_STATUS_NO);
						uci.setFanId(card.getFanId());
						unionCardIntegralService.save(uci);
					}
					unionConsume.setGiveIntegral(getIntegral);
				}
			}
		}
		unionConsumeService.save(unionConsume);
	}

	@Override
	public void unionConsumeRefund(String orderNo, Integer model) throws Exception{
		UnionConsume unionConsume = unionConsumeService.getValidByOrderNoAndModel(orderNo, model);
		if (unionConsume == null) {
			throw new BusinessException("订单不存在");
		}
		if (unionConsume.getPayStatus() == ConsumeConstant.PAY_STATUS_PAYING) {
			throw new BusinessException("未支付，不可退款");
		} else if (unionConsume.getPayStatus() == ConsumeConstant.PAY_STATUS_SUCCESS) {//已支付
			UnionConsume consume = new UnionConsume();
			consume.setId(unionConsume.getId());
			consume.setPayStatus(ConsumeConstant.PAY_STATUS_FAIL);
			unionConsumeService.update(consume);
		} else if (unionConsume.getPayStatus() == ConsumeConstant.PAY_STATUS_FAIL) {//已退款
			throw new BusinessException("已退款");
		}

	}
}
