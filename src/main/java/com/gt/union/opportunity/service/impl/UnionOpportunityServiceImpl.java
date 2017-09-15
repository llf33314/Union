package com.gt.union.opportunity.service.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.StringUtil;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.opportunity.constant.OpportunityConstant;
import com.gt.union.opportunity.entity.UnionOpportunity;
import com.gt.union.opportunity.mapper.UnionOpportunityMapper;
import com.gt.union.opportunity.service.IUnionOpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainService unionMainService;

    @Override
    public Page listToMy(Page page, final Integer busId, final Integer unionId, final String isAccept, final String clientName, final String clientPhone) throws Exception {
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
    public Page listFromMy(Page page, final Integer busId, final Integer unionId, final String isAccept, final String clientName, final String clientPhone) throws Exception {
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
    public void updateByIdAndIsAccept(Integer busId, Integer id, Integer isAccept, Double acceptPrice) throws Exception {
        if (busId == null || id == null || isAccept == null || acceptPrice == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）判断审核的商机是否存在
        UnionOpportunity unionOpportunity = this.selectById(id);
        if (unionOpportunity == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （2）判断当前商家是否是商机的接收者
        UnionMember member = unionMemberService.getById(unionOpportunity.getToMemberId());
        if (member == null) {
            throw new BusinessException(CommonConstant.OPERATE_ERROR);
        }
        if (!(member.getStatus().equals(MemberConstant.STATUS_IN) || member.getStatus().equals(MemberConstant.STATUS_APPLY_OUT))) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        if (!member.getBusId().equals(busId)) {
            throw new BusinessException(CommonConstant.OPERATE_ERROR);
        }
        // 判断该商机关联的联盟是否有效
        this.unionMainService.checkUnionMainValid(member.getUnionId());

        // （3）判断商机是否是未处理状态
        if (!unionOpportunity.getIsAccept().equals(OpportunityConstant.ACCEPT_CONFIRM_NON)) {
            throw new BusinessException("当前商机已处理");
        }

        switch (isAccept) {
            case OpportunityConstant.ACCEPT_CONFIRM_YES:
                // （4-1）接受商机
                unionOpportunity.setAcceptPrice(acceptPrice);
                //this.acceptOpportunity(unionOpportunity);
                break;
            case OpportunityConstant.ACCEPT_CONFIRM_NO:
                // （4-2）拒绝商机
                //this.refuseOpportunity(unionOpportunity);
                break;
            default:
                throw new ParamException(CommonConstant.PARAM_ERROR);
        }
    }

    /**
     * 拒绝商机
     * @param unionOpportunity
     *//*
    @Transactional
	private void refuseOpportunity(UnionOpportunity unionOpportunity) throws Exception{
		// （2）判断该商机关联的推荐方是否有效
		Integer fromBusId = unionOpportunity.getFromMemberId();
		if (!this.unionMemberService.hasUnionMemberAuthority(fromBusId)) {
			throw new BusinessException("该商机的推荐方信息已无效");
		}
		// （3）判断该商机的接收方是否对推荐方设置了商机佣金比
		UnionBrokerageRatio unionBrokerageRatio = this.unionBrokerageRatioService.getByFromMemberIdAndToMemberId(unionOpportunity.getFromMemberId(), unionOpportunity.getToMemberId());
		if (unionBrokerageRatio == null) {
			throw new BusinessException("未对该商机的推荐方设置商机佣金比");
		}

		// （4）保存更新信息
		UnionBusinessRecommend updateRecommend = new UnionBusinessRecommend();
		updateRecommend.setId(unionBusinessRecommend.getId());
		updateRecommend.setIsAcceptance(UnionBusinessRecommendConstant.IS_ACCEPTANCE_ACCEPT);
		updateRecommend.setBusinessPrice(BigDecimalUtil.multiply(unionBusinessRecommend.getAcceptancePrice(), unionBrokerage.getBrokerageRatio()).doubleValue());
		this.updateById(updateRecommend);

		// （5）短信通知
		UnionMain unionMain = this.unionMainService.getById(unionBusinessRecommend.getUnionId());
		UnionBusinessRecommendInfo unionBusinessRecommendInfo = this.unionBusinessRecommendInfoService.getByRecommendId(unionBusinessRecommend.getId());
		UnionApplyInfo fromUnionApplyInfo = this.unionApplyInfoService.getByUnionIdAndBusId(unionBusinessRecommend.getUnionId(), unionBusinessRecommend.getFromBusId());
		UnionApplyInfo toUnionApplyInfo = this.unionApplyInfoService.getByUnionIdAndBusId(unionBusinessRecommend.getUnionId(), unionBusinessRecommend.getToBusId());
		String phone = StringUtil.isNotEmpty(fromUnionApplyInfo.getNotifyPhone()) ? fromUnionApplyInfo.getNotifyPhone() : fromUnionApplyInfo.getDirectorPhone();
		StringBuilder sbContent = new StringBuilder("\"").append(unionMain.getUnionName()).append("\"的盟员\"")
				.append(toUnionApplyInfo.getEnterpriseName()).append("\"已接收您推荐的商机消息。客户信息：")
				.append(unionBusinessRecommendInfo.getUserName()).append("，")
				.append(unionBusinessRecommendInfo.getUserPhone()).append("，")
				.append(unionBusinessRecommendInfo.getBusinessMsg());
		PhoneMessage phoneMessage = new PhoneMessage(unionBusinessRecommend.getToBusId(), phone, sbContent.toString());
		this.phoneMessageSender.sendMsg(phoneMessage);
	}


	*//**
     * 接收商机
     * @param unionOpportunity
     *//*
	@Transactional
	private void acceptOpportunity(UnionOpportunity unionOpportunity) throws Exception{
		// （2）判断该商机关联的推荐方是否有效
		Integer fromBusId = unionOpportunity.getFromBusId();
		if (!this.unionRootService.checkBusUserValid(fromBusId)) {
			throw new BusinessException("该商机的推荐方信息已无效");
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

		// （5）短信通知
		UnionMain unionMain = this.unionMainService.getById(unionBusinessRecommend.getUnionId());
		UnionBusinessRecommendInfo unionBusinessRecommendInfo = this.unionBusinessRecommendInfoService.getByRecommendId(unionBusinessRecommend.getId());
		UnionApplyInfo fromUnionApplyInfo = this.unionApplyInfoService.getByUnionIdAndBusId(unionBusinessRecommend.getUnionId(), unionBusinessRecommend.getFromBusId());
		UnionApplyInfo toUnionApplyInfo = this.unionApplyInfoService.getByUnionIdAndBusId(unionBusinessRecommend.getUnionId(), unionBusinessRecommend.getToBusId());
		String phone = StringUtil.isNotEmpty(fromUnionApplyInfo.getNotifyPhone()) ? fromUnionApplyInfo.getNotifyPhone() : fromUnionApplyInfo.getDirectorPhone();
		StringBuilder sbContent = new StringBuilder("\"").append(unionMain.getUnionName()).append("\"的盟员\"")
				.append(toUnionApplyInfo.getEnterpriseName()).append("\"已接收您推荐的商机消息。客户信息：")
				.append(unionBusinessRecommendInfo.getUserName()).append("，")
				.append(unionBusinessRecommendInfo.getUserPhone()).append("，")
				.append(unionBusinessRecommendInfo.getBusinessMsg());
		PhoneMessage phoneMessage = new PhoneMessage(unionBusinessRecommend.getToBusId(), phone, sbContent.toString());
		this.phoneMessageSender.sendMsg(phoneMessage);
	}*/
}
