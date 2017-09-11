package com.gt.union.card.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.entity.result.UnionDiscountResult;
import com.gt.union.card.entity.UnionCard;
import com.gt.union.card.mapper.UnionCardMapper;
import com.gt.union.card.service.IUnionCardIntegralService;
import com.gt.union.card.service.IUnionCardService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>
 * 联盟卡信息 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionCardServiceImpl extends ServiceImpl<UnionCardMapper, UnionCard> implements IUnionCardService {

	@Autowired
	private IUnionMemberService unionMemberService;

	@Autowired
	private IUnionCardIntegralService unionCardIntegralService;

	@Value("${union.encryptKey}")
	private String encryptKey;

	@Override
	public Page selectListByUnionId(Page page, final Integer unionId, final Integer busId, final String cardNo, final String phone) throws Exception{
		if (unionId == null) {
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		if (busId == null) {
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		if (page == null) {
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		UnionMember unionMember = unionMemberService.getByUnionIdAndBusId(unionId,busId);
		final int memberId = unionMember.getId();
		Wrapper wrapper = new Wrapper() {
			@Override
			public String getSqlSegment() {
				StringBuilder sbSqlSegment = new StringBuilder();
				sbSqlSegment.append(" t1 LEFT JOIN t_union_card_root t2 ON t1.root_id = t2.id")
						.append(" WHERE")
						.append(" t1.member_id = ").append(memberId)
						.append(" AND t1.del_status = ").append(CommonConstant.DEL_STATUS_NO);
				if (StringUtil.isNotEmpty(phone)) {
					sbSqlSegment.append(" AND t2.phone LIKE '%").append(phone.trim()).append("%' ");
				}
				if (StringUtil.isNotEmpty(cardNo)) {
					sbSqlSegment.append(" AND t2.number LIKE '%").append(cardNo.trim()).append("%' ");
				}
				sbSqlSegment.append(" ORDER BY t1.id DESC ");
				return sbSqlSegment.toString();
			};

		};
		wrapper.setSqlSelect(" t1.id, DATE_FORMAT(t1.createtime, '%Y-%m-%d %T') createtime, t1.type, DATE_FORMAT(t1.validity, '%Y-%m-%d %T') validity, t2.phone, t2.number as cardNo");
		Page result = this.selectMapsPage(page, wrapper);
		List<Map<String,Object>> list = result.getRecords();
		List<Integer> cardIds = new ArrayList<Integer>();
		for(Map map : list){
			cardIds.add(CommonUtil.toInteger(map.get("id")));
		}
		List<Map<String,Object>> incomes = unionCardIntegralService.sumByCardIdsAndStatus(cardIds, 1);
		List<Map<String,Object>> expenditures = unionCardIntegralService.sumByCardIdsAndStatus(cardIds, 2);
		for(Map<String,Object> map : list){
			map.put("integral", 0);
			for(Map<String,Object> income : incomes){
				if(map.get("id").equals(income.get("cardId"))){
					map.put("integral",CommonUtil.toDouble(income.get("integral")));
				}
			}
			for(Map<String,Object> expenditure : expenditures){
				if(map.get("id").equals(expenditure.get("cardId"))){
					map.put("integral",new BigDecimal(CommonUtil.toDouble(map.get("integral"))).subtract(new BigDecimal(CommonUtil.toDouble(expenditure.get("integral")))).doubleValue());
				}
			}
		}
		page.setRecords(list);
		return result;
	}

	@Override
	public Map<String, Object> getUnionCardInfo(String no, Integer busId) throws Exception{
		if(StringUtil.isEmpty(no)){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		if(busId == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		Pattern p = Pattern.compile("[a-zA-z]");
		if(p.matcher(no).find()){//包含字母--扫码枪扫码所得
			//解密
			try{
				no = EncryptUtil.decrypt(encryptKey, no);//解码后得到联盟卡号
			}catch (Exception e){
				throw new ParamException(CommonConstant.PARAM_ERROR);
			}
			return getUnionCardInfoByCardNo(no,busId);
		}else {
			//手机号或联盟卡号
			//卡号使用8位
			if(no.length() == 8){//卡号
				return getUnionCardInfoByCardNo(no,busId);
			} else{//手机号
				return getUnionCardInfoByPhone(no,busId);
			}
		}
	}

	@Override
	public Map<String, Object> getUnionCardInfoByCardNo(String cardNo, Integer busId) {
		return null;
	}

	@Override
	public Map<String, Object> getUnionCardInfoByPhone(String phone, Integer busId) {
		return null;
	}

	@Override
	public UnionDiscountResult getConsumeUnionDiscount(Integer memberId, Integer busId) throws Exception{
		UnionDiscountResult result = new UnionDiscountResult();
		if(CommonUtil.isEmpty(memberId) || CommonUtil.isEmpty(busId)){
			result.setCode(-2);
			return result;
		}
		/*List<UnionMain> list = unionMainService.listMyValidUnion(busId);//查询我加入的有效联盟
		if(ListUtil.isEmpty(list)){
			result.setCode(-1);
			return result;
		}
		//绑定
		UnionMemberCard memberCard = unionMemberCardService.getUnionMemberCard(memberId, busId);
		if(memberCard == null){
			result.setCode(0);
			return result;
		}
		EntityWrapper entityWrapper = new EntityWrapper<UnionBusMemberCard>();
		entityWrapper.eq("del_status",UnionBusMemberCardConstant.DEL_STATUS_NO);
		entityWrapper.eq("id", memberCard.getUnionMemberCardId());
		UnionBusMemberCard card = this.selectOne(entityWrapper);
		List<UnionMain> sameUnions = unionMainService.getSameUnionBus(list, memberCard.getBusId());
		if(ListUtil.isEmpty(sameUnions)){
			result.setCode(-1);
			return result;
		}*/
		result.setCardId(10);
		result.setCode(1);
		result.setIfDefault(false);
		result.setDiscount(8d);
		return result;
	}
}
