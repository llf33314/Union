package com.gt.union.service.business.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.UnionApplyConstant;
import com.gt.union.common.constant.basic.UnionMainConstant;
import com.gt.union.common.constant.basic.UnionMemberConstant;
import com.gt.union.common.constant.business.UnionBusinessRecommendConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.basic.UnionMember;
import com.gt.union.entity.business.UnionBusinessRecommend;
import com.gt.union.entity.business.UnionBusinessRecommendInfo;
import com.gt.union.mapper.business.UnionBusinessRecommendMapper;
import com.gt.union.service.basic.IUnionMainService;
import com.gt.union.service.basic.IUnionMemberService;
import com.gt.union.service.business.IUnionBusinessRecommendInfoService;
import com.gt.union.service.business.IUnionBusinessRecommendService;
import com.gt.union.vo.business.UnionBusinessRecommendVO;
import com.gt.union.service.common.IUnionRootService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟商家商机推荐 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@Service
public class UnionBusinessRecommendServiceImpl extends ServiceImpl<UnionBusinessRecommendMapper, UnionBusinessRecommend> implements IUnionBusinessRecommendService {
	private static final String UPDATE_ID_ISACCEPTANCE = "UnionBusinessRecommendServiceImpl.updateByIdAndIsAcceptance()";
	private static final String SAVE = "UnionBusinessRecommendServiceImpl.save()";
	private static final String LIST_TOBUSID = "UnionBusinessRecommendServiceImpl.listByToBusId()";
	private static final String LIST_FROMBUSID = "UnionBusinessRecommendServiceImpl.listByFromBusId()";
	private static final String LIST_BROKERAGE_FROMBUSID = "UnionBusinessRecommendServiceImpl.listBrokerageByFromBusId()";
	private static final String LIST_BROKERAGE_TOBUSID = "UnionBusinessRecommendServiceImpl.listBrokerageByToBusId()";
	private static final String LIST_PAYDETAIL_FROMBUSID = "UnionBusinessRecommendServiceImpl.listPayDetailByFromBusId()";
	private static final String LIST_PAYDETAILPARTICULAR_UNIONID_FROMBUSID_TOBUSID = "UnionBusinessRecommendServiceImpl.listPayDetailParticularByUnionIdAndFromBusIdAndToBusId()";

	@Autowired
	private IUnionBusinessRecommendInfoService unionBusinessRecommendInfoService;

	@Autowired
	private IUnionRootService unionRootService;

	@Autowired
	private IUnionMemberService unionMemberService;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateByIdAndIsAcceptance(UnionBusinessRecommend recommend) throws Exception{
		//TODO 判断推荐的商家是否有效
		if(CommonUtil.isEmpty(recommend.getId())){
			throw new ParamException(UPDATE_ID_ISACCEPTANCE, "", ExceptionConstant.PARAM_ERROR);
		}
		if(CommonUtil.isEmpty(recommend.getIsAcceptance()) || !(recommend.getIsAcceptance() == 1 || recommend.getIsAcceptance() == 2 )){
			throw new ParamException(UPDATE_ID_ISACCEPTANCE, "", ExceptionConstant.PARAM_ERROR);
		}
		UnionBusinessRecommend businessRecommend = this.selectById(recommend.getId());
		if(CommonUtil.isEmpty(businessRecommend) || businessRecommend.getDelStatus() == 1){
			throw new BusinessException(UPDATE_ID_ISACCEPTANCE, "", "该商机不存在，请刷新后重试");
		}
		if(businessRecommend.getIsAcceptance() != 0){
			throw new BusinessException(UPDATE_ID_ISACCEPTANCE, "", "该商机已处理，请刷新后重试");
		}
		this.updateById(recommend);
	}

	@Override
	public void save(UnionBusinessRecommendVO vo) throws Exception{
		//联盟是否有效
		this.unionRootService.checkUnionMainValid(vo.getUnionId());
		UnionMember fromUnionMember = unionMemberService.getUnionMember(vo.getBusId(),vo.getUnionId());
		unionRootService.hasUnionMemberAuthority(vo.getUnionId(), vo.getBusId());
		UnionMember toUnionMeber = unionMemberService.selectById(vo.getToMemberId());
		if(toUnionMeber == null){
			throw new BusinessException(SAVE,"盟员不存在","您推荐的盟员不存在");
		}
		if(toUnionMeber.getDelStatus() == UnionMemberConstant.DEL_STATUS_YES){
			throw new BusinessException(SAVE,"已退盟","您推荐的盟员已退盟");
		}
		if(toUnionMeber.getOutStaus() == UnionMemberConstant.OUT_STATUS_PERIOD){
			throw new BusinessException(SAVE,"已退盟","您推荐的盟员处于退盟过渡期");
		}
		if(!unionRootService.checkBusUserValid(toUnionMeber.getBusId())){
			throw new BusinessException(SAVE,"盟员账号过期","盟员账号已过期");
		}
		UnionBusinessRecommend recommend = new UnionBusinessRecommend();
		recommend.setCreatetime(new Date());
		recommend.setDelStatus(0);
		recommend.setIsAcceptance(0);
		recommend.setIsConfirm(0);
		recommend.setFromBusId(vo.getBusId());
		recommend.setRecommendType(2);
		recommend.setUnionId(vo.getUnionId());
		recommend.setToBusId(toUnionMeber.getBusId());
		recommend.setFromMemberId(fromUnionMember.getId());
		recommend.setToMemberId(vo.getToMemberId());
		this.insert(recommend);
		UnionBusinessRecommendInfo info = new UnionBusinessRecommendInfo();
		info.setBusinessMsg(vo.getBusinessMsg());
		info.setRecommendId(recommend.getId());
		info.setUserName(vo.getUserName());
		info.setUserPhone(vo.getUserPhone());
		unionBusinessRecommendInfoService.insert(info);
	}

	@Override
	public Page listByToBusId(Page page, final Integer busId, final Integer unionId
            , final String isAcceptance, final String userName, final String userPhone) throws Exception {
		if (page == null) {
			throw new ParamException(LIST_TOBUSID, "参数page为空", ExceptionConstant.PARAM_ERROR);
		}
        if (busId == null) {
            throw new ParamException(LIST_TOBUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" r");
                sbSqlSegment.append(" LEFT JOIN t_union_business_recommend_info ri ON ri.recommend_id = r.id ")
                        .append(" LEFT JOIN t_union_apply a ON a.union_member_id = r.from_member_id AND a.union_id = r.union_id ")
                        .append(" LEFT JOIN t_union_apply_info ai ON ai.union_apply_id = a.id ")
                        .append(" LEFT JOIN t_union_main m ON m.id = r.union_id ")
                        .append(" WHERE r.del_status = ").append(UnionBusinessRecommendConstant.DEL_STATUS_NO)
                        .append("    AND r.to_bus_id = ").append(busId)
                        .append("    AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("    AND m.del_status = ").append(UnionMainConstant.DEL_STATUS_NO);
                if (unionId != null) {
                    sbSqlSegment.append(" AND r.union_id = ").append(unionId);
                }
                if (StringUtil.isNotEmpty(isAcceptance)) {
                    String[] isAcceptanceArray = isAcceptance.split(",");
                    sbSqlSegment.append(" AND (");
                    for (int i = 0, length = isAcceptanceArray.length; i < length; i++) {
                        sbSqlSegment.append(i == 0 ? "" : " OR ").append(" r.is_acceptance = ").append(isAcceptanceArray[i]);
                    }
                    sbSqlSegment.append(" ) ");
                }
                if (StringUtil.isNotEmpty(userName)) {
                    sbSqlSegment.append(" AND ri.user_name LIKE '%").append(userName).append("%' ");
                }
                if (StringUtil.isNotEmpty(userPhone)) {
                    sbSqlSegment.append(" AND ri.user_phone LIKE '%").append(userPhone).append("%' ");
                }
                sbSqlSegment.append(" ORDER BY r.is_acceptance ASC,r.id DESC ");
                return sbSqlSegment.toString();
            }
        };
		StringBuilder sbSqlSelect = new StringBuilder(" r.id recommendId ")
                .append(" , ri.user_name userName ")
                .append(" , ri.user_phone userPhone ")
                .append(" , ri.business_msg businessMsg ")
                .append(" , ai.enterprise_name enterpriseName ")
                .append(" , m.id unionId ")
                .append(" , m.union_name unionName ")
                .append(" , r.is_acceptance isAcceptance ");
		wrapper.setSqlSelect(sbSqlSelect.toString());

		return this.selectMapsPage(page, wrapper);
	}

	@Override
	public Page listByFromBusId(Page page, final Integer busId, final Integer unionId
            , final String isAcceptance, final String userName, final String userPhone) throws Exception {
        if (page == null) {
            throw new ParamException(LIST_FROMBUSID, "参数page为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(LIST_FROMBUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" r");
                sbSqlSegment.append(" LEFT JOIN t_union_business_recommend_info ri ON ri.recommend_id = r.id ")
                        .append(" LEFT JOIN t_union_apply a ON a.union_member_id = r.to_member_id AND a.union_id = r.union_id ")
                        .append(" LEFT JOIN t_union_apply_info ai ON ai.union_apply_id = a.id ")
                        .append(" LEFT JOIN t_union_main m ON m.id = r.union_id ")
                        .append(" WHERE r.del_status = ").append(UnionBusinessRecommendConstant.DEL_STATUS_NO)
                        .append("    AND r.from_bus_id = ").append(busId)
                        .append("    AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("    AND m.del_status = ").append(UnionMainConstant.DEL_STATUS_NO);
                if (unionId != null) {
                    sbSqlSegment.append(" AND r.union_id = ").append(unionId);
                }
                if (StringUtil.isNotEmpty(isAcceptance)) {
                    String[] isAcceptanceArray = isAcceptance.split(";");
                    sbSqlSegment.append(" AND (");
                    for (int i = 0, length = isAcceptanceArray.length; i < length; i++) {
                        sbSqlSegment.append(i != 0 ? " OR " : "").append(" r.is_acceptance = ").append(isAcceptanceArray[i]);
                    }
                    sbSqlSegment.append(" ) ");
                }
                if (StringUtil.isNotEmpty(userName)) {
                    sbSqlSegment.append(" AND ri.user_name LIKE '%").append(userName).append("%' ");
                }
                if (StringUtil.isNotEmpty(userPhone)) {
                    sbSqlSegment.append(" AND ri.user_phone LIKE '%").append(userPhone).append("%' ");
                }
                sbSqlSegment.append(" ORDER BY r.is_acceptance ASC,r.id DESC ");
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" r.id recommendId ")
                .append(" , ri.user_name userName ")
                .append(" , ri.user_phone userPhone ")
                .append(" , ri.business_msg businessMsg ")
                .append(" , ai.enterprise_name enterpriseName ")
                .append(" , m.id unionId ")
                .append(" , m.union_name unionName ")
                .append(" , r.is_acceptance isAcceptance ");
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
	}

	@Override
	public Page listBrokerageByFromBusId(Page page, final Integer fromBusId, final Integer toBusId, final Integer unionId
            , final String isConfirm, final String userName, final String userPhone) throws Exception {
		if (page == null) {
			throw new ParamException(LIST_BROKERAGE_FROMBUSID, "参数page为空", ExceptionConstant.PARAM_ERROR);
		}
		if (fromBusId == null) {
			throw new ParamException(LIST_BROKERAGE_FROMBUSID, "参数fromBusId为空", ExceptionConstant.PARAM_ERROR);
		}
		Wrapper wrapper = new Wrapper() {
			@Override
			public String getSqlSegment() {
				StringBuilder sbSqlSegment = new StringBuilder(" r");
				sbSqlSegment.append(" LEFT JOIN t_union_business_recommend_info ri ON ri.recommend_id = r.id ")
						.append(" LEFT JOIN t_union_apply a ON a.union_member_id = r.to_member_id AND a.union_id = r.union_id ")
						.append(" LEFT JOIN t_union_apply_info ai ON ai.union_apply_id = a.id ")
						.append(" LEFT JOIN t_union_main m ON m.id = r.union_id ")
						.append(" WHERE r.del_status = ").append(UnionBusinessRecommendConstant.DEL_STATUS_NO)
						.append("    AND r.from_bus_id = ").append(fromBusId)
						.append("    AND r.is_acceptance = ").append(UnionBusinessRecommendConstant.IS_ACCEPTANCE_ACCEPT)
                        .append("    AND (r.is_confirm = ").append(UnionBusinessRecommendConstant.IS_CONFIRM_UNCHECK)
                        .append("        OR r.is_confirm = ").append(UnionBusinessRecommendConstant.IS_CONFIRM_CONFIRM).append(") ")
						.append("    AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
						.append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
						.append("    AND m.del_status = ").append(UnionMainConstant.DEL_STATUS_NO);
				if (toBusId != null) {
				    sbSqlSegment.append(" AND r.to_bus_id = ").append(toBusId);
                }
				if (unionId != null) {
					sbSqlSegment.append(" AND r.union_id = ").append(unionId);
				}
				if (StringUtil.isNotEmpty(isConfirm)) {
					String[] isConfirmArray = isConfirm.split(";");
					sbSqlSegment.append(" AND (");
					for (int i = 0, length = isConfirmArray.length; i < length; i++) {
						sbSqlSegment.append(i == 0 ? "" : " OR ").append(" r.is_confirm = ").append(isConfirmArray[i]);
					}
					sbSqlSegment.append(" ) ");
				}
				if (StringUtil.isNotEmpty(userName)) {
				    sbSqlSegment.append(" AND ri.user_name LIKE '%").append(userName).append("%' ");
                }
                if (StringUtil.isNotEmpty(userPhone)) {
				    sbSqlSegment.append(" AND ri.user_phone LIKE '%").append(userPhone).append("%' ");
                }
				sbSqlSegment.append(" ORDER BY r.is_confirm ASC,r.id DESC ");
				return sbSqlSegment.toString();
			}
		};
		StringBuilder sbSqlSelect = new StringBuilder(" r.id recommendId ")
				.append(" , ri.user_name userName ")
				.append(" , ri.user_phone userPhone ")
				.append(" , ri.business_msg businessMsg ")
				.append(" , ai.enterprise_name enterpriseName ")
				.append(" , m.id unionId ")
				.append(" , m.union_name unionName ")
				.append(" , r.acceptance_price acceptancePrice ")
		        .append(" , r.business_price businessPrice ")
                .append(" , r.recommend_type recommendType ")
                .append(" , r.is_confirm isConfirm ")
                .append(" , DATE_FORMAT(r.confirm_time, '%Y-%m-%d %T') confirmTime ");
		wrapper.setSqlSelect(sbSqlSelect.toString());

		return this.selectMapsPage(page, wrapper);
	}

	@Override
	public Page listBrokerageByToBusId(Page page, final Integer toBusId, final Integer fromBusId, final Integer unionId
            , final String isConfirm, final String userName, final String userPhone) throws Exception {
        if (page == null) {
            throw new ParamException(LIST_BROKERAGE_TOBUSID, "参数page为空", ExceptionConstant.PARAM_ERROR);
        }
        if (toBusId == null) {
            throw new ParamException(LIST_BROKERAGE_TOBUSID, "参数toBusId为空", ExceptionConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" r");
                sbSqlSegment.append(" LEFT JOIN t_union_business_recommend_info ri ON ri.recommend_id = r.id ")
                        .append(" LEFT JOIN t_union_apply a ON a.union_member_id = r.from_member_id AND a.union_id = r.union_id ")
                        .append(" LEFT JOIN t_union_apply_info ai ON ai.union_apply_id = a.id ")
                        .append(" LEFT JOIN t_union_main m ON m.id = r.union_id ")
                        .append(" WHERE r.del_status = ").append(UnionBusinessRecommendConstant.DEL_STATUS_NO)
                        .append("    AND r.to_bus_id = ").append(toBusId)
                        .append("    AND r.is_acceptance = ").append(UnionBusinessRecommendConstant.IS_ACCEPTANCE_ACCEPT)
                        .append("    AND (r.is_confirm = ").append(UnionBusinessRecommendConstant.IS_CONFIRM_UNCHECK)
                        .append("        OR r.is_confirm = ").append(UnionBusinessRecommendConstant.IS_CONFIRM_CONFIRM).append(") ")
                        .append("    AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("    AND m.del_status = ").append(UnionMainConstant.DEL_STATUS_NO);
                if (fromBusId != null) {
                    sbSqlSegment.append(" AND r.from_bus_id = ").append(fromBusId);
                }
                if (unionId != null) {
                    sbSqlSegment.append(" AND r.union_id = ").append(unionId);
                }
                if (StringUtil.isNotEmpty(isConfirm)) {
                    String[] isConfirmArray = isConfirm.split(";");
                    sbSqlSegment.append(" AND (");
                    for (int i = 0, length = isConfirmArray.length; i < length; i++) {
                        sbSqlSegment.append(i != 0 ? " OR " : "").append(" r.is_confirm = ").append(isConfirmArray[i]);
                    }
                    sbSqlSegment.append(" ) ");
                }
                if (StringUtil.isNotEmpty(userName)) {
                    sbSqlSegment.append(" AND ri.user_name LIKE '%").append(userName).append("%' ");
                }
                if (StringUtil.isNotEmpty(userPhone)) {
                    sbSqlSegment.append(" AND ri.user_phone LIKE '%").append(userPhone).append("%' ");
                }
                sbSqlSegment.append(" ORDER BY r.is_confirm ASC,r.id DESC ");
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" r.id recommendId ")
                .append(" , ri.user_name userName ")
                .append(" , ri.user_phone userPhone ")
                .append(" , ri.business_msg businessMsg ")
                .append(" , ai.enterprise_name enterpriseName ")
                .append(" , m.id unionId ")
                .append(" , m.union_name unionName ")
                .append(" , r.acceptance_price acceptancePrice ")
                .append(" , r.business_price businessPrice ")
                .append(" , r.recommend_type recommendType ")
                .append(" , r.is_confirm isConfirm ")
                .append(" , DATE_FORMAT(r.confirm_time, '%Y-%m-%d %T') confirmTime ");
        wrapper.setSqlSelect(sbSqlSelect.toString());

        return this.selectMapsPage(page, wrapper);
	}

	@Override
	public Page listPayDetailByFromBusId(Page page, final Integer fromBusId, final Integer unionId) throws Exception {
		if (page == null) {
		    throw new ParamException(LIST_PAYDETAIL_FROMBUSID, "参数page为空", ExceptionConstant.PARAM_ERROR);
        }
        if (fromBusId == null) {
		    throw new ParamException(LIST_PAYDETAIL_FROMBUSID, "参数fromBusId为空", ExceptionConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" r");
                sbSqlSegment.append(" LEFT JOIN t_union_business_recommend r2 ON r2.union_id = r.union_id ")
                        .append("        AND r2.from_bus_id = r.to_bus_id AND r2.to_bus_id = r.from_bus_id ")
                        .append("    LEFT JOIN t_union_apply a ON a.union_member_id = r.to_member_id AND a.union_id = r.union_id ")
                        .append("    LEFT JOIN t_union_apply_info ai ON ai.union_apply_id = a.id ")
                        .append("    LEFT JOIN t_union_main m ON m.id = r.union_id ")
                        .append("    LEFT JOIN t_union_member m2 on m2.id = r.from_member_id AND m2.union_id = r.union_id")
                        .append("    WHERE r.del_status = ").append(UnionBusinessRecommendConstant.DEL_STATUS_NO)
                        .append("        AND r.is_acceptance = ").append(UnionBusinessRecommendConstant.IS_ACCEPTANCE_ACCEPT)
                        .append("        AND r.is_confirm = ").append(UnionBusinessRecommendConstant.IS_CONFIRM_CONFIRM)
                        .append("        AND r.from_bus_id = ").append(fromBusId)
                        .append("        AND r2.del_status = ").append(UnionBusinessRecommendConstant.DEL_STATUS_NO)
                        .append("        AND r2.is_acceptance = ").append(UnionBusinessRecommendConstant.IS_ACCEPTANCE_ACCEPT)
                        .append("        AND r2.is_confirm = ").append(UnionBusinessRecommendConstant.IS_CONFIRM_CONFIRM)
                        .append("        AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("        AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("        AND m.del_status = ").append(UnionMainConstant.DEL_STATUS_NO)
                        .append("        AND m2.del_status = ").append(UnionMemberConstant.DEL_STATUS_NO);
                if (unionId != null) {
                    sbSqlSegment.append(" AND r.union_id = ").append(unionId);
                }
                sbSqlSegment.append(" GROUP BY r.union_id, r.from_bus_id, r.to_bus_id, m.union_name, ai.enterprise_name, m2.is_nuion_owner ")
                        .append("    ORDER BY m2.is_nuion_owner DESC,ai.enterprise_name ASC ");
                return sbSqlSegment.toString();
            }
        };
		StringBuilder sbSqlSelect = new StringBuilder(" r.union_id unionId ")
                .append(" , r.from_bus_id fromBusId ")
                .append(" , r.to_bus_id toBusId ")
                .append(" , m.union_name unionName ")
                .append(" , ai.enterprise_name enterpriseName ")
                .append(" , sum(r.business_price-r2.business_price) businessPriceSum ");
		wrapper.setSqlSelect(sbSqlSelect.toString());

		return this.selectMapsPage(page, wrapper);
	}

    @Override
    public List<Map<String, Object>> listPayDetailParticularByUnionIdAndFromBusIdAndToBusId(final Integer unionId
			, final Integer fromBusId, final Integer toBusId) throws Exception {
	    if (unionId == null) {
	        throw new ParamException(LIST_PAYDETAILPARTICULAR_UNIONID_FROMBUSID_TOBUSID, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (fromBusId == null) {
	        throw new ParamException(LIST_PAYDETAILPARTICULAR_UNIONID_FROMBUSID_TOBUSID, "参数fromBusId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (toBusId == null) {
	        throw new ParamException(LIST_PAYDETAILPARTICULAR_UNIONID_FROMBUSID_TOBUSID, "参数toBusId为空", ExceptionConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" r");
                sbSqlSegment.append(" LEFT JOIN t_union_business_recommend_info ri ON ri.recommend_id = r.id ")
                        .append("    LEFT JOIN t_union_apply a ON a.union_member_id = r.to_member_id ")
                        .append("    LEFT JOIN t_union_apply_info ai ON ai.union_apply_id = a.id ")
                        .append("    LEFT JOIN t_union_main m ON m.id = r.union_id ")
                        .append("    WHERE r.del_status = ").append(UnionBusinessRecommendConstant.DEL_STATUS_NO)
                        .append("        AND r.is_acceptance = ").append(UnionBusinessRecommendConstant.IS_ACCEPTANCE_ACCEPT)
                        .append("        AND r.is_confirm = ").append(UnionBusinessRecommendConstant.IS_CONFIRM_CONFIRM)
                        .append("        AND r.union_id = ").append(unionId)
                        .append("        AND r.from_bus_id = ").append(fromBusId)
                        .append("        AND r.to_bus_id = ").append(toBusId)
                        .append("        AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("        AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("        AND m.del_status = ").append(UnionMemberConstant.DEL_STATUS_NO);
                sbSqlSegment.append(" ORDER BY r.createtime DESC ");
                return sbSqlSegment.toString();
            }
        };
	    StringBuilder sbSqlSelect = new StringBuilder(" m.union_name unionName ")
                .append(" , ai.enterprise_name enterpriseName ")
                .append(" , DATE_FORMAT(r.createtime, '%Y-%m-%d %T') createtime ")
                .append(" , ri.user_name userName ")
                .append(" , ri.user_phone userPhone ")
                .append(" , r.business_price businessPrice ");
	    wrapper.setSqlSelect(sbSqlSelect.toString());

        return this.selectMaps(wrapper);
    }

}
