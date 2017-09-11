package com.gt.union.opportunity.service.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.StringUtil;
import com.gt.union.opportunity.entity.UnionOpportunity;
import com.gt.union.opportunity.mapper.UnionOpportunityMapper;
import com.gt.union.opportunity.service.IUnionOpportunityService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商机推荐 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionOpportunityServiceImpl extends ServiceImpl<UnionOpportunityMapper, UnionOpportunity> implements IUnionOpportunityService {

	@Override
	public Page listToMy(Page page, final Integer busId, final Integer unionId, final String isAccept, final String clientName, final String clientPhone) throws Exception{
		if (page == null || busId == null) {
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		Wrapper wrapper = new Wrapper() {
			@Override
			public String getSqlSegment() {
				StringBuilder sbSqlSegment = new StringBuilder(" o");
				sbSqlSegment.append(" LEFT JOIN t_union_member m ON o.from_member_id = m.id ")
						.append(" LEFT JOIN t_union_main um ON um.id = m.union_id ")
						.append(" WHERE m.bus_id = ").append(busId)
						.append("    AND o.del_status = ").append(CommonConstant.DEL_STATUS_NO);
				if (unionId != null) {
					sbSqlSegment.append(" AND m.union_id = ").append(unionId);
				}
				if (StringUtil.isNotEmpty(isAccept)) {
					String[] isAcceptArray = isAccept.split(";");
					sbSqlSegment.append(" AND (");
					for (int i = 0, length = isAcceptArray.length; i < length; i++) {
						sbSqlSegment.append(i == 0 ? "" : " OR ").append(" o.is_accept = ").append(isAcceptArray[i]);
					}
					sbSqlSegment.append(" ) ");
				}
				if (StringUtil.isNotEmpty(clientName)) {
					sbSqlSegment.append(" AND o.client_name LIKE '%").append(clientName).append("%' ");
				}
				if (StringUtil.isNotEmpty(clientPhone)) {
					sbSqlSegment.append(" AND o.client_phone LIKE '%").append(clientPhone).append("%' ");
				}
				sbSqlSegment.append(" ORDER BY o.is_accept ASC, o.id DESC ");
				return sbSqlSegment.toString();
			}
		};
		StringBuilder sbSqlSelect = new StringBuilder(" o.id opportunityId ")
				.append(" , ri.client_name clientName ")
				.append(" , ri.client_phone clientPhone ")
				.append(" , ri.business_msg businessMsg ")
				.append(" , m.enterprise_name enterpriseName ")
				.append(" , um.id unionId ")
				.append(" , um.name name ")
				.append(" , o.is_accept isAccept ");
		wrapper.setSqlSelect(sbSqlSelect.toString());

		return this.selectMapsPage(page, wrapper);
	}

	@Override
	public Page listFromMy(Page page, final Integer busId, final Integer unionId, final String isAccept, final String clientName, final String clientPhone) throws Exception{
		if (page == null || busId == null) {
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		Wrapper wrapper = new Wrapper() {
			@Override
			public String getSqlSegment() {
				StringBuilder sbSqlSegment = new StringBuilder(" o");
				sbSqlSegment.append(" LEFT JOIN t_union_member m ON o.to_member_id = m.id ")
						.append(" LEFT JOIN t_union_main um ON um.id = m.union_id ")
						.append(" WHERE m.bus_id = ").append(busId)
						.append("    AND o.del_status = ").append(CommonConstant.DEL_STATUS_NO);
				if (unionId != null) {
					sbSqlSegment.append(" AND m.union_id = ").append(unionId);
				}
				if (StringUtil.isNotEmpty(isAccept)) {
					String[] isAcceptArray = isAccept.split(";");
					sbSqlSegment.append(" AND (");
					for (int i = 0, length = isAcceptArray.length; i < length; i++) {
						sbSqlSegment.append(i == 0 ? "" : " OR ").append(" o.is_accept = ").append(isAcceptArray[i]);
					}
					sbSqlSegment.append(" ) ");
				}
				if (StringUtil.isNotEmpty(clientName)) {
					sbSqlSegment.append(" AND o.client_name LIKE '%").append(clientName).append("%' ");
				}
				if (StringUtil.isNotEmpty(clientPhone)) {
					sbSqlSegment.append(" AND o.client_phone LIKE '%").append(clientPhone).append("%' ");
				}
				sbSqlSegment.append(" ORDER BY o.is_accept ASC, o.id DESC ");
				return sbSqlSegment.toString();
			}
		};
		StringBuilder sbSqlSelect = new StringBuilder(" o.id opportunityId ")
				.append(" , ri.client_name clientName ")
				.append(" , ri.client_phone clientPhone ")
				.append(" , ri.business_msg businessMsg ")
				.append(" , m.enterprise_name enterpriseName ")
				.append(" , um.id unionId ")
				.append(" , um.name name ")
				.append(" , o.is_accept isAccept ");
		wrapper.setSqlSelect(sbSqlSelect.toString());

		return this.selectMapsPage(page, wrapper);
	}

	@Override
	public void updateByIdAndIsAccept(Integer busId, Integer id, Integer isAccept, Double acceptPrice) throws Exception{
		if (busId == null || id == null || isAccept == null || acceptPrice == null) {
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		// （1）判断审核的商机是否存在
		UnionOpportunity unionOpportunity = this.selectById(id);
		if(unionOpportunity == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		// （2）判断当前商家是否是商机的接收者

	}
}
