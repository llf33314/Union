package com.gt.union.service.card.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.entity.result.UnionBindCardResult;
import com.gt.union.api.entity.result.UnionDiscountResult;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.UnionMemberConstant;
import com.gt.union.common.constant.card.UnionBusMemberCardConstant;
import com.gt.union.common.constant.card.UnionCardInfoConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.card.UnionBusMemberCard;
import com.gt.union.entity.card.UnionMemberCard;
import com.gt.union.mapper.card.UnionBusMemberCardMapper;
import com.gt.union.service.basic.IUnionMainService;
import com.gt.union.service.card.IUnionBusMemberCardService;
import com.gt.union.service.card.IUnionMemberCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <p>
 * 联盟商家升级会员联盟卡 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@Service
public class UnionBusMemberCardServiceImpl extends ServiceImpl<UnionBusMemberCardMapper, UnionBusMemberCard> implements IUnionBusMemberCardService {
    private static final String GET_UNION_MEMBER_INTEGRAL = "UnionBusMemberCardServiceImpl.getUnionMemberIntegral()";
    private static final String SELECT_UNION_BUS_MEMBER_CARD_LIST = "UnionBusMemberCardServiceImpl.selectUnionBusMemberCardList()";
    private static final String BING_UNION_CARD = "UnionBusMemberCardServiceImpl.bindUnionCard()";
    private static final String GET_UNION_CARD_INFO = "UnionBusMemberCardServiceImpl.getUnionCardInfo()";

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberCardService unionMemberCardService;

	@Autowired
    private RedisCacheUtil redisCacheUtil;



	@Override
	public double getUnionMemberIntegral(final Integer unionId) {
		Wrapper wrapper = new Wrapper() {
			@Override
			public String getSqlSegment() {
				StringBuilder sbSqlSegment = new StringBuilder();
				sbSqlSegment.append(" WHERE bus_id IN (SELECT bus_id from t_union_member WHERE union_id = ")
						.append(unionId)
						.append(" AND del_status = ").append(UnionMemberConstant.DEL_STATUS_NO).append(")")
						.append(" AND del_status = ").append(UnionBusMemberCardConstant.DEL_STATUS_NO);
				return sbSqlSegment.toString();
			};
		};
		StringBuilder sbSqlSelect = new StringBuilder("");
		sbSqlSelect.append("IFNULL(SUM(integral),0)AS integral");
		wrapper.setSqlSelect(sbSqlSelect.toString());
		Map<String,Object> map = this.selectMap(wrapper);
		return CommonUtil.toDouble(map.get("integral"));
	}

	@Override
	public Page selectUnionBusMemberCardList(Page page, final Integer unionId, final Integer busId, final String cardNo, final String phone) throws Exception{
		if (unionId == null) {
			throw new ParamException(SELECT_UNION_BUS_MEMBER_CARD_LIST, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
		}
		if (busId == null) {
			throw new ParamException(SELECT_UNION_BUS_MEMBER_CARD_LIST, "参数busId为空", ExceptionConstant.PARAM_ERROR);
		}
		if (page == null) {
			throw new ParamException(SELECT_UNION_BUS_MEMBER_CARD_LIST, "参数page为空", ExceptionConstant.PARAM_ERROR);
		}
		Wrapper wrapper = new Wrapper() {
			@Override
			public String getSqlSegment() {
				StringBuilder sbSqlSegment = new StringBuilder();
				sbSqlSegment.append(" t1 LEFT JOIN t_union_card_info t2 ON t1.id = t2.union_card_id")
						.append(" WHERE")
						.append(" t1.bus_id = ").append(busId)
						.append(" AND t2.union_id = ").append(unionId)
						.append(" AND t1.del_status = ").append(UnionBusMemberCardConstant.DEL_STATUS_NO)
						.append(" AND t2.del_status = ").append(UnionCardInfoConstant.DEL_STATUS_NO);
				if (StringUtil.isNotEmpty(phone)) {
					sbSqlSegment.append(" AND t1.phone LIKE '%").append(phone.trim()).append("%' ");
				}
				if (StringUtil.isNotEmpty(cardNo)) {
					sbSqlSegment.append(" AND t1.cardNo LIKE '%").append(cardNo.trim()).append("%' ");
				}
				sbSqlSegment.append(" ORDER BY t1.id DESC ");
				return sbSqlSegment.toString();
			};

		};
		StringBuilder sbSqlSelect = new StringBuilder("");
		sbSqlSelect.append(" t1.id, t1.cardNo, t1.phone, t1.integral, DATE_FORMAT(t2.updatetime, '%Y-%m-%d %T') updatetime, t2.card_type, DATE_FORMAT(t2.card_term_time, '%Y-%m-%d %T') cardTermTime");
		wrapper.setSqlSelect(sbSqlSelect.toString());
		return this.selectMapsPage(page, wrapper);
	}

	@Override
	public List<Map<String, Object>> selectUnionBusMemberCardList(final Integer unionId, final Integer busId, final String cardNo, final String phone) throws Exception {
		if (unionId == null) {
			throw new ParamException(SELECT_UNION_BUS_MEMBER_CARD_LIST, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
		}
		if (busId == null) {
			throw new ParamException(SELECT_UNION_BUS_MEMBER_CARD_LIST, "参数busId为空", ExceptionConstant.PARAM_ERROR);
		}
		Wrapper wrapper = new Wrapper() {
			@Override
			public String getSqlSegment() {
				StringBuilder sbSqlSegment = new StringBuilder();
				sbSqlSegment.append(" t1 LEFT JOIN t_union_card_info t2 ON t1.id = t2.union_card_id")
						.append(" WHERE")
						.append(" t1.bus_id = ").append(busId)
						.append(" AND t2.union_id = ").append(unionId)
						.append(" AND t1.del_status = ").append(UnionBusMemberCardConstant.DEL_STATUS_NO)
						.append(" AND t2.del_status = ").append(UnionCardInfoConstant.DEL_STATUS_NO);
				if (StringUtil.isNotEmpty(phone)) {
					sbSqlSegment.append(" AND t1.phone LIKE '%").append(phone.trim()).append("%' ");
				}
				if (StringUtil.isNotEmpty(cardNo)) {
					sbSqlSegment.append(" AND t1.cardNo LIKE '%").append(cardNo.trim()).append("%' ");
				}
				sbSqlSegment.append(" ORDER BY t1.id DESC ");
				return sbSqlSegment.toString();
			};

		};
		StringBuilder sbSqlSelect = new StringBuilder("");
		sbSqlSelect.append(" t1.id, t1.cardNo, t1.phone, t1.integral, DATE_FORMAT(t2.updatetime, '%Y-%m-%d %T') updatetime, t2.card_type, DATE_FORMAT(t2.card_term_time, '%Y-%m-%d %T') cardTermTime");
		wrapper.setSqlSelect(sbSqlSelect.toString());
		return this.selectMaps(wrapper);
	}

	@Override
	public UnionDiscountResult getConsumeUnionDiscount(Integer memberId, Integer busId) throws Exception{
		UnionDiscountResult result = new UnionDiscountResult();
		if(CommonUtil.isEmpty(memberId) || CommonUtil.isEmpty(busId)){
			result.setCode(-2);
			return result;
		}
		List<UnionMain> list = unionMainService.listMyValidUnion(busId);//查询我加入的有效联盟
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
		}
		return result;
	}

	@Override
	public UnionBindCardResult bindUnionCard(Integer busid, Integer memberId, String phone, String code) throws Exception{
		if(busid == null){
			throw new ParamException(BING_UNION_CARD, "参数busid为空", ExceptionConstant.PARAM_ERROR);
		}
		if(memberId == null){
			throw new ParamException(BING_UNION_CARD, "参数memberId为空", ExceptionConstant.PARAM_ERROR);
		}
		if(StringUtil.isEmpty(phone)){
			throw new ParamException(BING_UNION_CARD, "参数phone为空", ExceptionConstant.PARAM_ERROR);
		}
		if(StringUtil.isEmpty(code)){
			throw new ParamException(BING_UNION_CARD, "参数code为空", ExceptionConstant.PARAM_ERROR);
		}
		String phoneKey = RedisKeyUtil.getMemberPhoneCodeKey(memberId);
		Object obj = redisCacheUtil.get(phoneKey);
		if(obj == null){
			UnionBindCardResult result = new UnionBindCardResult();
			result.setMessage("验证码已失效");
			result.setSuccess(false);
			return result;
		}
		if(!code.equals(obj.toString())) {
			UnionBindCardResult result = new UnionBindCardResult();
			result.setSuccess(false);
			result.setMessage("验证码有误");
		}
		UnionBindCardResult result = new UnionBindCardResult();
		result.setSuccess(true);
		result.setMessage("绑定成功");
		redisCacheUtil.remove(phoneKey);
		return result;
	}

	@Override
	public Map<String, Object> getUnionCardInfo(String no, Integer busId) throws Exception{
		if(StringUtil.isEmpty(no)){
			throw new ParamException(GET_UNION_CARD_INFO, "参数no为空", ExceptionConstant.PARAM_ERROR);
		}
		if(busId == null){
			throw new ParamException(GET_UNION_CARD_INFO, "参数busid为空", ExceptionConstant.PARAM_ERROR);
		}
		Pattern p = Pattern.compile("[a-zA-z]");
		if(p.matcher(no).find()){//包含字母--扫码枪扫码所得
			//解密
			try{
				checkCardNo = EncryptUtil.decrypt("CFCCBD99C12B62E52952EA90A931A01F", no);
			}catch (Exception e){

			}
			no = URLDecoder.decode(no,"UTF-8");

		}else {

		}
		return null;
	}
}
