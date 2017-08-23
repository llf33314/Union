package com.gt.union.service.card.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.card.UnionBusMemberCard;
import com.gt.union.mapper.card.UnionBusMemberCardMapper;
import com.gt.union.service.basic.IUnionMainService;
import com.gt.union.service.card.IUnionBusMemberCardService;
import com.gt.union.service.common.IUnionRootService;
import com.gt.union.vo.card.UnionBusMemberCardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionRootService unionRootService;

	@Override
	public double getUnionMemberIntegral(final Integer unionId) {
		Wrapper wrapper = new Wrapper() {
			@Override
			public String getSqlSegment() {
				StringBuilder sbSqlSegment = new StringBuilder();
				sbSqlSegment.append(" WHERE bus_id IN (SELECT bus_id from t_union_member WHERE union_id = ")
						.append(unionId)
						.append(" AND del_status = ").append(0).append(")")
						.append(" AND del_status = ").append(0);
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
						.append(" AND t1.del_status = ").append(0)
						.append(" AND t2.del_status = ").append(0);
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
						.append(" AND t1.del_status = ").append(0)
						.append(" AND t2.del_status = ").append(0);
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
	public Map getConsumeUnionDiscount(Integer memberId, Integer busId) throws Exception{
		Map<String, Object> discount_map = new HashMap<>();
		List<UnionMain> list = unionMainService.listMyUnion(busId);//查询我加入的联盟
		if(ListUtil.isEmpty(list)){
			return discount_map;
		}
		List<UnionMain> myUnions = new ArrayList();
		Iterator<UnionMain> it = list.iterator();
		while (it.hasNext()){
			UnionMain main = it.next();
			if(unionRootService.checkUnionMainValid(main)){
				myUnions.add(main);
			}
		}
		//得到有效的联盟
		if(ListUtil.isEmpty(myUnions)){
			return discount_map;
		}
		EntityWrapper entityWrapper = new EntityWrapper<UnionBusMemberCard>();
		entityWrapper.eq("del_status", 0 );
		entityWrapper.eq("member_id",memberId);
		entityWrapper.eq("bus_id",busId);
		UnionBusMemberCard card = this.selectOne(entityWrapper);
		if(card == null){
			discount_map.put("")
		}
		return discount_map;
	}
}
