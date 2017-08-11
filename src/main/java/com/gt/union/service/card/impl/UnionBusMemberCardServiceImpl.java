package com.gt.union.service.card.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.card.UnionBusMemberCard;
import com.gt.union.mapper.card.UnionBusMemberCardMapper;
import com.gt.union.service.card.IUnionBusMemberCardService;
import com.gt.union.vo.card.UnionBusMemberCardVO;
import org.springframework.stereotype.Service;

import java.util.Map;

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
	public Page selectUnionBusMemberCardList(Page page, final UnionBusMemberCardVO vo) throws Exception{
		if (vo.getUnionId() == null) {
			throw new ParamException(SELECT_UNION_BUS_MEMBER_CARD_LIST, "参数错误", ExceptionConstant.PARAM_ERROR);
		}
		Wrapper wrapper = new Wrapper() {
			@Override
			public String getSqlSegment() {
				StringBuilder sbSqlSegment = new StringBuilder();
				sbSqlSegment.append(" t1 LEFT JOIN t_union_card_info t2 ON t1.id = t2.union_card_id")
						.append(" WHERE")
						.append(" t1.bus_id = ").append(vo.getBusId())
						.append(" AND t2.union_id = ").append(vo.getUnionId())
						.append(" AND t1.del_status = ").append(0)
						.append(" AND t2.del_status = ").append(0);
				if (StringUtil.isNotEmpty(vo.getPhone())) {
					sbSqlSegment.append(" AND t1.phone LIKE '%").append(vo.getPhone().trim()).append("%' ");
				}
				if (StringUtil.isNotEmpty(vo.getCardNo())) {
					sbSqlSegment.append(" AND i.cardNo LIKE '%").append(vo.getCardNo().trim()).append("%' ");
				}
				sbSqlSegment.append(" ORDER BY t1.id DESC ");
				return sbSqlSegment.toString();
			};

		};
		StringBuilder sbSqlSelect = new StringBuilder("");
		sbSqlSelect.append(" t1.id, t1.cardNo, t1.phone, t1.integral, DATE_FORMAT(t1.createtime, '%Y-%m-%d %T') createtime, t2.card_type, DATE_FORMAT(t2.card_term_time, '%Y-%m-%d %T') cardTermTime");
		wrapper.setSqlSelect(sbSqlSelect.toString());
		return this.selectMapsPage(page, wrapper);
	}
}
