package com.gt.union.consume.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.entity.param.UnionConsumeParam;
import com.gt.union.api.entity.result.UnionConsumeResult;
import com.gt.union.api.entity.result.UnionRefundResult;
import com.gt.union.card.constant.CardConstant;
import com.gt.union.card.entity.UnionCardIntegral;
import com.gt.union.card.service.IUnionCardIntegralService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.consume.constant.ConsumeConstant;
import com.gt.union.consume.entity.UnionConsume;
import com.gt.union.consume.mapper.UnionConsumeMapper;
import com.gt.union.consume.service.IUnionConsumeService;
import com.gt.union.consume.vo.UnionConsumeVO;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

	@Autowired
	private IUnionCardIntegralService unionCardIntegralService;

	@Autowired
	private UnionConsumeMapper unionConsumeMapper;

	@Override
	public UnionConsumeResult consumeByUnionCard(UnionConsumeParam unionConsumeParam) throws Exception{
		if(unionConsumeParam.getUnionCardId() == null){
			throw new ParamException("unionCardId为空");
		}
		if(unionConsumeParam.getBusId() == null){
			throw new ParamException("busId为空");
		}
		if(unionConsumeParam.getUnionId() == null){
			throw new ParamException("unionId为空");
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
		this.insert(unionConsume);
		//线上积分赠送积分
		if(CommonUtil.isEmpty(unionConsumeParam.getGiveIntegralNow()) || unionConsumeParam.getGiveIntegralNow()){//立即正式
			UnionMain main = unionMainService.getById(unionConsumeParam.getUnionId());
			if(main.getIsIntegral() != null && main.getIsIntegral() == 1){//开启积分
				double integral = dictService.getGiveIntegral();
				double getIntegral = BigDecimalUtil.multiply(unionConsume.getPayMoney(), integral, 2).doubleValue();//获得的积分
				UnionCardIntegral unionCardIntegral = new UnionCardIntegral();
				unionCardIntegral.setCardId(unionConsumeParam.getUnionCardId());
				unionCardIntegral.setCreatetime(new Date());
				unionCardIntegral.setDelStatus(CommonConstant.DEL_STATUS_NO);
				unionCardIntegral.setIntegral(getIntegral);
				unionCardIntegral.setStatus(CardConstant.INTEGRAL_STATUS_IN);
				unionCardIntegral.setType(CardConstant.INTEGRAL_TYPE_GIVE);
				unionCardIntegralService.insert(unionCardIntegral);
			}
		}
		UnionConsumeResult result = new UnionConsumeResult();
		result.setMessage("核销成功");
		result.setSuccess(true);
		return result;
	}

	@Override
	public UnionRefundResult unionRefund(String orderNo, Integer model) throws Exception{
		UnionConsume unionConsume = this.getByOrderNoAndModel(orderNo,model);
		UnionRefundResult result = new UnionRefundResult();
		if(unionConsume == null){
			throw new BusinessException("没有该订单信息的联盟消费记录");
		}
		if(unionConsume.getStatus() == ConsumeConstant.PAY_STATUS_NON){
			throw new BusinessException("该订单未支付");
		}else if(unionConsume.getStatus() == ConsumeConstant.PAY_STATUS_YES){//已支付
			UnionConsume consume = new UnionConsume();
			consume.setId(unionConsume.getId());
			consume.setStatus(2);
			this.updateById(consume);
			result.setSuccess(true);
		}else if(unionConsume.getStatus() == ConsumeConstant.PAY_STATUS_REFUND){//已退款
			throw new BusinessException("该订单已退款");
		}
		return result;
	}

	@Override
	public UnionConsume getByOrderNoAndModel(String orderNo, Integer model) {
		EntityWrapper wrapper = new EntityWrapper<>();
		wrapper.eq("order_no",orderNo);
		wrapper.eq("model",model);
		wrapper.eq("del_status",CommonConstant.DEL_STATUS_NO);
		return this.selectOne(wrapper);
	}

	@Override
	public Page listMy(Page page, Integer unionId, Integer busId, Integer memberId, String cardNo, String phone, String beginTime, String endTime) throws Exception{
		if(page == null || busId == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		List<UnionConsumeVO> list = unionConsumeMapper.listMy(page, unionId, busId, memberId, cardNo, phone, beginTime, endTime);
		page.setRecords(list);
		return page;
	}

	@Override
	public Page listOther(Page page, Integer unionId, Integer busId, Integer memberId, String cardNo, String phone, String beginTime, String endTime) throws Exception{
		if(page == null || busId == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		List<UnionConsumeVO> list = unionConsumeMapper.listOther(page, unionId, busId, memberId, cardNo, phone, beginTime, endTime);
		page.setRecords(list);
		return page;
	}

}
