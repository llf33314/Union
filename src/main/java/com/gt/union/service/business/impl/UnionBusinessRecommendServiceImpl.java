package com.gt.union.service.business.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
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
import com.gt.union.common.util.*;
import com.gt.union.entity.basic.UnionMember;
import com.gt.union.entity.business.UnionBrokerage;
import com.gt.union.entity.business.UnionBusinessRecommend;
import com.gt.union.entity.business.UnionBusinessRecommendInfo;
import com.gt.union.entity.business.vo.UnionBusinessRecommendVO;
import com.gt.union.mapper.business.UnionBusinessRecommendMapper;
import com.gt.union.service.basic.IUnionMemberService;
import com.gt.union.service.business.IUnionBrokerageService;
import com.gt.union.service.business.IUnionBusinessRecommendInfoService;
import com.gt.union.service.business.IUnionBusinessRecommendService;
import com.gt.union.service.common.IUnionRootService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

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
	private static final String ACCEPT_RECOMMEND = "UnionBusinessRecommendServiceImpl.acceptRecommend()";
	private static final String SAVE = "UnionBusinessRecommendServiceImpl.save()";
	private static final String LIST_TOBUSID = "UnionBusinessRecommendServiceImpl.listByToBusId()";
	private static final String LIST_FROMBUSID = "UnionBusinessRecommendServiceImpl.listByFromBusId()";
	private static final String LIST_BROKERAGE_FROMBUSID = "UnionBusinessRecommendServiceImpl.listBrokerageByFromBusId()";
	private static final String LIST_BROKERAGE_TOBUSID = "UnionBusinessRecommendServiceImpl.listBrokerageByToBusId()";
    private static final String LIST_PAYDETAILPARTICULAR_UNIONID_FROMBUSID_TOBUSID = "UnionBusinessRecommendServiceImpl.listPayDetailParticularByUnionIdAndFromBusIdAndToBusId()";
    private static final String LIST_PAYDETAIL_FROMBUSID = "UnionBusinessRecommendServiceImpl.listPayDetailByFromBusId()";
    private static final String GET_STATISTIC_DATA = "UnionBusinessRecommendServiceImpl.getStatisticData()";
    private static final String RECOMMEND_MSG = "UnionBusinessRecommendServiceImpl.getRecommendMsgInfo()";

	@Autowired
	private IUnionBusinessRecommendInfoService unionBusinessRecommendInfoService;

	@Autowired
	private IUnionRootService unionRootService;

	@Autowired
	private IUnionMemberService unionMemberService;

	@Autowired
    private IUnionBrokerageService unionBrokerageService;

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Override
    @Transactional(propagation = Propagation.REQUIRED)
	public void save(UnionBusinessRecommendVO vo) throws Exception{
		//判断联盟是否有效
		if (!this.unionRootService.checkUnionMainValid(vo.getUnionId())) {
			throw new BusinessException(SAVE, "", "联盟已无效");
		}
		UnionMember fromUnionMember = this.unionMemberService.getByUnionIdAndBusId(vo.getUnionId(), vo.getBusId());
		if (fromUnionMember == null) {
		    throw new BusinessException(SAVE, "unionId=" + vo.getUnionId() + ";busId=" + vo.getBusId(), "未找到推荐商家信息");
        }
		//判断推荐商家是否是联盟的有效成员
		if (!this.unionRootService.hasUnionMemberAuthority(vo.getUnionId(), vo.getBusId())) {
		    throw new BusinessException(SAVE, "", "推荐商家不是该联盟的有效成员");
        }
        //判断被推荐商家是否是联盟的有效成员
		UnionMember toUnionMember = this.unionMemberService.selectById(vo.getToMemberId());
		if(toUnionMember == null || this.unionRootService.hasUnionMemberAuthority(toUnionMember)
                || this.unionRootService.checkBusUserValid(toUnionMember.getBusId())){
			throw new BusinessException(SAVE, "", "您推荐的盟员是无效盟员");
		}
		//保存推荐主表信息
		UnionBusinessRecommend recommend = new UnionBusinessRecommend();
		recommend.setCreatetime(new Date());
		recommend.setDelStatus(UnionBusinessRecommendConstant.DEL_STATUS_NO);
		recommend.setIsAcceptance(UnionBusinessRecommendConstant.IS_ACCEPTANCE_UNCHECK);
		recommend.setIsConfirm(UnionBusinessRecommendConstant.IS_CONFIRM_UNCHECK);
		recommend.setFromBusId(vo.getBusId());
		recommend.setRecommendType(UnionBusinessRecommendConstant.RECOMMEND_TYPE_OFFLINE);
		recommend.setUnionId(vo.getUnionId());
		recommend.setToBusId(toUnionMember.getBusId());
		recommend.setFromMemberId(fromUnionMember.getId());
		recommend.setToMemberId(vo.getToMemberId());
		this.insert(recommend);
		//保存推荐辅表信息
		UnionBusinessRecommendInfo info = new UnionBusinessRecommendInfo();
		info.setBusinessMsg(vo.getBusinessMsg());
		info.setRecommendId(recommend.getId());
		info.setUserName(vo.getUserName());
		info.setUserPhone(vo.getUserPhone());
		this.unionBusinessRecommendInfoService.insert(info);
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateByIdAndIsAcceptance(Integer busId, Integer id, Integer isAcceptance, Double acceptancePrice) throws Exception{
        if (busId == null) {
            throw new ParamException(UPDATE_ID_ISACCEPTANCE, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (id == null) {
            throw new ParamException(UPDATE_ID_ISACCEPTANCE, "参数id为空", ExceptionConstant.PARAM_ERROR);
        }
        if (isAcceptance == null) {
            throw new ParamException(UPDATE_ID_ISACCEPTANCE, "参数isAcceptance为空", ExceptionConstant.PARAM_ERROR);
        }
        if (acceptancePrice == null) {
            throw new ParamException(UPDATE_ID_ISACCEPTANCE, "参数acceptancePrice为空", ExceptionConstant.PARAM_ERROR);
        }
        // （1）判断审核的商机是否存在
        UnionBusinessRecommend unionBusinessRecommend = this.selectById(id);
        if (unionBusinessRecommend == null) {
            throw new ParamException(UPDATE_ID_ISACCEPTANCE, "id=" + id, ExceptionConstant.PARAM_ERROR);
        }
        // （2）判断当前商家是否是商机的接收者
        if (unionBusinessRecommend.getToBusId() != busId) {
            throw new BusinessException(UPDATE_ID_ISACCEPTANCE, "", "当前登录的商家帐号不是该商机的接收者");
        }
        // （3）判断商机是否是未处理状态
        if (unionBusinessRecommend.getIsAcceptance() != UnionBusinessRecommendConstant.IS_ACCEPTANCE_UNCHECK) {
            throw new BusinessException(UPDATE_ID_ISACCEPTANCE, "",  "当前商机已处理");
        }
        switch (isAcceptance) {
            case UnionBusinessRecommendConstant.IS_ACCEPTANCE_ACCEPT:
                // （4-1）接受商机
                unionBusinessRecommend.setAcceptancePrice(acceptancePrice);
                this.acceptRecommend(unionBusinessRecommend);
                break;
            case UnionBusinessRecommendConstant.IS_ACCEPTANCE_REFUSE:
                // （4-2）拒绝商机
                this.refuseRecommend(unionBusinessRecommend);
                break;
            default:
                throw new ParamException(UPDATE_ID_ISACCEPTANCE, "isAcceptance=" + isAcceptance, ExceptionConstant.PARAM_ERROR);
        }
    }

    /**
     * 接受商机
     * @param unionBusinessRecommend
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    private void acceptRecommend(UnionBusinessRecommend unionBusinessRecommend) throws Exception {
        if (unionBusinessRecommend != null) {
            // （1）判断该商机关联的联盟是否有效
            if (!this.unionRootService.checkUnionMainValid(unionBusinessRecommend.getUnionId())) {
                throw new BusinessException(ACCEPT_RECOMMEND, "", "该商机所关联的联盟信息已无效");
            }
            // （2）判断该商机关联的推荐方是否有效
            Integer fromBusId = unionBusinessRecommend.getFromBusId();
            if (!this.unionRootService.checkBusUserValid(fromBusId)) {
                throw new BusinessException(ACCEPT_RECOMMEND, "", "该商机的推荐方信息已无效");
            }
            // （3）判断该商机的接收方是否对推荐方设置了商机佣金比
            UnionBrokerage unionBrokerage = this.unionBrokerageService.getByUnionIdAndFromBusIdAndToBusId(unionBusinessRecommend.getUnionId()
                , unionBusinessRecommend.getFromBusId(), unionBusinessRecommend.getToBusId());
            if (unionBrokerage == null) {
                throw new BusinessException(ACCEPT_RECOMMEND, "", "未对该商机的推荐方设置商机佣金比");
            }

            // （4）保存更新信息
            UnionBusinessRecommend updateRecommend = new UnionBusinessRecommend();
            updateRecommend.setId(unionBusinessRecommend.getId());
            updateRecommend.setIsAcceptance(UnionBusinessRecommendConstant.IS_ACCEPTANCE_ACCEPT);
            updateRecommend.setBusinessPrice(BigDecimalUtil.multiply(unionBusinessRecommend.getAcceptancePrice(), unionBrokerage.getBrokerageRatio()).doubleValue());
            this.updateById(updateRecommend);

        }
    }

    /**
     * 拒绝商机
     * @param unionBusinessRecommend
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    private void refuseRecommend(UnionBusinessRecommend unionBusinessRecommend) throws Exception {
	    if (unionBusinessRecommend != null) {
	        UnionBusinessRecommend updateRecommend = new UnionBusinessRecommend();
	        updateRecommend.setId(unionBusinessRecommend.getId());
	        updateRecommend.setIsAcceptance(UnionBusinessRecommendConstant.IS_ACCEPTANCE_REFUSE);
	        this.updateById(updateRecommend);
        }
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

    @Override
    public Map<String, Object> getStatisticData(Integer unionId, Integer busId) throws Exception {
        if (unionId == null) {
            throw new ParamException(GET_STATISTIC_DATA, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(GET_STATISTIC_DATA, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        Map<String, Object> resultMap = new HashMap<>();
        //未结算佣金收入
        Double uncheckBrokerageIncome = getBrokerageIncome(unionId, busId, UnionBusinessRecommendConstant.IS_CONFIRM_CONFIRM);
        resultMap.put("uncheckBrokerageIncome", uncheckBrokerageIncome);
        //已结算佣金收入
        Double confirmBrokerageIncome = getBrokerageIncome(unionId, busId, UnionBusinessRecommendConstant.IS_CONFIRM_CONFIRM);
        resultMap.put("confirmBrokerageIncome", confirmBrokerageIncome);
        //未结算佣金收入占总佣金收入的百分比
        BigDecimal bdUncheckBrokerageIncomePercent = BigDecimalUtil.divide(uncheckBrokerageIncome, BigDecimalUtil.add(uncheckBrokerageIncome, confirmBrokerageIncome), 4);
        String uncheckBrokerageIncomePercent = DoubleUtil.formatPercent(bdUncheckBrokerageIncomePercent.doubleValue());
        resultMap.put("uncheckBrokerageIncomePercent", uncheckBrokerageIncomePercent);
        //已结算佣金收入占总佣金收入的百分比
        BigDecimal bdConfirmBrokerageIncomePercent = BigDecimalUtil.subtract(Double.valueOf(1.00), bdUncheckBrokerageIncomePercent, 4);
        String confirmBrokerageIncomePercent = DoubleUtil.formatPercent(bdConfirmBrokerageIncomePercent.doubleValue());
        resultMap.put("confirmBrokerageIncomePercent", confirmBrokerageIncomePercent);

        //未结算支出佣金
        Double uncheckBrokerageExpense = getBrokerageExpense(unionId, busId, UnionBusinessRecommendConstant.IS_CONFIRM_UNCHECK);
        resultMap.put("uncheckBrokerageExpense", uncheckBrokerageExpense);
        //已结算支出佣金
        Double confirmBrokerageExpense = getBrokerageExpense(unionId, busId, UnionBusinessRecommendConstant.IS_CONFIRM_CONFIRM);
        resultMap.put("confirmBrokerageExpense", confirmBrokerageExpense);
        //未结算支出佣金占总支出佣金的百分比
        BigDecimal bdUncheckBrokerageExpensePercent = BigDecimalUtil.divide(uncheckBrokerageExpense, BigDecimalUtil.add(uncheckBrokerageExpense, confirmBrokerageExpense), 4);
        String uncheckBrokerageExpensePercent = DoubleUtil.formatPercent(bdUncheckBrokerageExpensePercent.doubleValue());
        resultMap.put("uncheckBrokerageExpensePercent", uncheckBrokerageExpensePercent);
        //已结算支出佣金占总支出佣金的百分比
        BigDecimal bdConfirmBrokerageExpensePercent = BigDecimalUtil.subtract(Double.valueOf(1.00), bdUncheckBrokerageExpensePercent, 4);
        String confirmBrokerageExpensePercent = DoubleUtil.formatPercent(bdConfirmBrokerageExpensePercent.doubleValue());
        resultMap.put("confirmBrokerageExpensePercent", confirmBrokerageExpensePercent);

        //获取一周统计数据
        List<Map<String, Map<String, Double>>> brokerageInWeek = getBrokerageInWeek(unionId, busId);
        resultMap.put("brokerageInWeek", brokerageInWeek);

        return resultMap;
    }

	@Override
	public Map<String, Object> getRecommendMsgInfo(String redisKey) throws Exception{
		if(StringUtil.isEmpty(redisKey)){
			throw new ParamException(RECOMMEND_MSG, "参数redisKey为空", "参数redisKey为空");
		}
		Object data = redisCacheUtil.get(redisKey);
		if(data == null){
			throw new ParamException(RECOMMEND_MSG, "", "redis失效");
		}
		Map<String,Object> result = JSON.parseObject(data.toString(), Map.class);
		return null;
	}

	//根据联盟id、推荐商家id和是否给予佣金状态isConfirm获取总佣金收入信息
    private Double getBrokerageIncome(Integer unionId, Integer busId, Integer isConfirm) {
        if (unionId != null && busId != null && isConfirm != null) {
            EntityWrapper entityWrapper = new EntityWrapper();
            entityWrapper.eq("del_status", UnionBusinessRecommendConstant.DEL_STATUS_NO)
                    .eq("is_acceptance", UnionBusinessRecommendConstant.IS_ACCEPTANCE_ACCEPT)
                    .eq("union_id", unionId)
                    .eq("from_bus_id", busId)
                    .eq("is_confirm", isConfirm);
            entityWrapper.setSqlSelect(" SUM(business_price) businessPriceSum ");

            Map<String, Object> map = this.selectMap(entityWrapper);
            if (map == null || map.get("businessPriceSum") == null) {
                return Double.valueOf(0.0);
            }
            return Double.valueOf(map.get("businessPriceSum").toString());
        }
        return null;
    }

    //根据联盟id、推荐商家id和是否给予佣金状态isConfirm获取总支出佣金信息
    private Double getBrokerageExpense(Integer unionId, Integer busId, Integer isConfirm) {
        if (unionId != null && busId != null && isConfirm != null) {
            EntityWrapper entityWrapper = new EntityWrapper();
            entityWrapper.eq("del_status", UnionBusinessRecommendConstant.DEL_STATUS_NO)
                    .eq("is_acceptance", UnionBusinessRecommendConstant.IS_ACCEPTANCE_ACCEPT)
                    .eq("union_id", unionId)
                    .eq("to_bus_id", busId)
                    .eq("is_confirm", isConfirm);
            entityWrapper.setSqlSelect(" SUM(business_price) businessPriceSum ");

            Map<String, Object> map = this.selectMap(entityWrapper);
            if (map == null || map.get("businessPriceSum") == null) {
                return Double.valueOf(0.0);
            }
            return Double.valueOf(map.get("businessPriceSum").toString());
        }
        return null;
    }

    //获取一周佣金信息
    private List<Map<String, Map<String, Double>>> getBrokerageInWeek(Integer unionId, Integer busId) {
        List<Map<String, Map<String, Double>>> resultList = new ArrayList<>();
        Date currentDate = DateUtil.getCurrentDate();
        for (int i = 0; i > -7; i--) {
            Date date = DateUtil.addDays(currentDate, i);
            String strDate = DateUtil.getDateString(date, DateUtil.DATE_PATTERN);
            String strDateBegin = strDate + " 00:00:00";
            String strDateEnd = strDate + " 23:59:59";
            Map<String, Double> brokerageInDayMap = getBrokerageInDay(unionId, busId, strDateBegin, strDateEnd);
            String strWeek = DateUtil.getWeek(date);
            Map<String, Map<String, Double>> weekMap = new HashMap<>();
            weekMap.put(strWeek, brokerageInDayMap);
            resultList.add(weekMap);
        }
        return resultList;
    }

    //获取一天的佣金信息
    private Map<String,Double> getBrokerageInDay(Integer unionId, Integer busId, String strDateBegin, String strDateEnd) {
        if (unionId != null && busId != null && StringUtil.isNotEmpty(strDateBegin) && StringUtil.isNotEmpty(strDateEnd)) {
            Map<String, Double> resultMap = new HashMap<>();

            //已结算的佣金收入
            EntityWrapper incomeWrapper = new EntityWrapper();
            incomeWrapper.eq("del_status", UnionBusinessRecommendConstant.DEL_STATUS_NO)
                    .eq("is_acceptance", UnionBusinessRecommendConstant.IS_ACCEPTANCE_ACCEPT)
                    .eq("union_id", unionId)
                    .eq("from_bus_id", busId)
                    .eq("is_confirm", UnionBusinessRecommendConstant.IS_CONFIRM_CONFIRM)
                    .between("confirm_time", strDateBegin, strDateEnd);
            incomeWrapper.setSqlSelect(" SUM(business_price) businessPriceSum ");
            Map<String, Object> incomeMap = this.selectMap(incomeWrapper);
            if (incomeMap == null || incomeMap.get("businessPriceSum") == null) {
                resultMap.put("incomeBrokerage", Double.valueOf(0.0));
            } else {
                resultMap.put("incomeBrokerage", Double.valueOf(incomeMap.get("businessPriceSum").toString()));
            }

            //已结算的支出佣金
            EntityWrapper expanseWrapper = new EntityWrapper();
            expanseWrapper.eq("del_status", UnionBusinessRecommendConstant.DEL_STATUS_NO)
                    .eq("is_acceptance", UnionBusinessRecommendConstant.IS_ACCEPTANCE_ACCEPT)
                    .eq("union_id", unionId)
                    .eq("to_bus_id", busId)
                    .eq("is_confirm", UnionBusinessRecommendConstant.IS_CONFIRM_CONFIRM)
                    .between("confirm_time", strDateBegin, strDateEnd);
            expanseWrapper.setSqlSelect(" SUM(business_price) businessPriceSum ");
            Map<String, Object> expanseMap = this.selectMap(expanseWrapper);
            if (expanseMap == null || expanseMap.get("businessPriceSum") == null) {
                resultMap.put("expanseBrokerage", Double.valueOf(0.0));
            } else {
                resultMap.put("expanseBrokerage", Double.valueOf(expanseMap.get("expanseBrokerage").toString()));
            }

            return resultMap;
        }
        return null;
    }
}
