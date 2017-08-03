package com.gt.union.service.business.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.basic.UnionApplyConstant;
import com.gt.union.common.constant.basic.UnionMainConstant;
import com.gt.union.common.constant.business.UnionBusinessRecommendConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParameterException;
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
import com.gt.union.vo.business.UnionBusinessRecommendFormVO;
import com.gt.union.vo.business.UnionBusinessRecommendVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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


	@Autowired
	private IUnionBusinessRecommendInfoService unionBusinessRecommendInfoService;

	@Autowired
	private IUnionMainService unionMainService;

	@Autowired
	private IUnionMemberService unionMemberService;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateVerifyRecommend(UnionBusinessRecommend recommend) throws Exception{
		//TODO 判断推荐的商家是否有效
		if(CommonUtil.isEmpty(recommend.getId())){
			throw new ParameterException("参数错误");
		}
		if(CommonUtil.isEmpty(recommend.getIsAcceptance()) || !(recommend.getIsAcceptance() == 1 || recommend.getIsAcceptance() == 2 )){
			throw new ParameterException("参数错误");
		}
		UnionBusinessRecommend businessRecommend = this.selectById(recommend.getId());
		if(CommonUtil.isEmpty(businessRecommend) || businessRecommend.getDelStatus() == 1){
			throw new BusinessException("该商机不存在，请刷新后重试");
		}
		if(businessRecommend.getIsAcceptance() != 0){
			throw new BusinessException("该商机已处理，请刷新后重试");
		}
		this.updateById(recommend);
	}

	@Override
	public void saveUnionBusinessRecommend(UnionBusinessRecommendFormVO vo) throws Exception{
		//TODO 判断被推荐的商家是否有效
		//联盟是否有效
		unionMainService.isUnionValid(unionMainService.selectById(vo.getUnionId()));
		if(CommonUtil.isEmpty(vo.getUnionId())){
			throw new ParameterException("请选择联盟");
		}
		if(CommonUtil.isEmpty(vo.getToMemberId())){
			throw new ParameterException("请选择推荐的商家");
		}
		if(CommonUtil.isEmpty(vo.getUnionBusinessRecommendInfo().getUserName())){
			throw new ParameterException("意向客户姓名不能为空，请重新输入");
		}
		if(StringUtil.getStringLength(vo.getUnionBusinessRecommendInfo().getUserName()) > 5){
			throw new ParameterException("意向客户姓名不可超过5个字，请重新输入");
		}
		if(CommonUtil.isEmpty(vo.getUnionBusinessRecommendInfo().getUserPhone())){
			throw new ParameterException("意向客户电话不能为空，请重新输入");
		}
		if(StringUtil.getStringLength(vo.getUnionBusinessRecommendInfo().getBusinessMsg()) > 20){
			throw new ParameterException("业务备注不可超过20个字，请重新输入");
		}
		//TODO 判断商机推荐的手机号是否正确   您输入的意向客户电话有误，请重新输入
		UnionMember fromUnionMember = unionMemberService.getUnionMember(vo.getBusId(),vo.getUnionId());
		UnionBusinessRecommend recommend = new UnionBusinessRecommend();
		recommend.setCreatetime(new Date());
		recommend.setDelStatus(0);
		recommend.setIsAcceptance(0);
		recommend.setIsConfirm(0);
		recommend.setFromBusId(vo.getBusId());
		recommend.setRecommendType(2);
		recommend.setUnionId(vo.getUnionId());
		recommend.setToBusId(vo.getToBusId());
		recommend.setFromMemberId(fromUnionMember.getId());
		recommend.setToMemberId(vo.getToMemberId());
		this.insert(recommend);
		UnionBusinessRecommendInfo info = new UnionBusinessRecommendInfo();
		info.setBusinessMsg(vo.getUnionBusinessRecommendInfo().getBusinessMsg());
		info.setRecommendId(recommend.getId());
		info.setUserName(vo.getUnionBusinessRecommendInfo().getUserName());
		info.setUserPhone(vo.getUnionBusinessRecommendInfo().getUserPhone());
		unionBusinessRecommendInfoService.insert(info);
	}

	@Override
	public Page listUnionBusinessRecommendToMe(Page page, final Integer busId, final Integer unionId
            , final String isAcceptance, final String userName, final String userPhone) throws Exception {
		if (page == null) {
			throw new Exception("UnionBusinessRecommendServiceImpl.listUnionBusinessRecommendToMe():参数page不能为空!");
		}
        if (busId == null) {
            throw new Exception("UnionBusinessRecommendServiceImpl.listUnionBusinessRecommendToMe():参数busId不能为空!");
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
	public Page listUnionBusinessRecommendFromMe(Page page, final Integer busId, final Integer unionId
            , final String isAcceptance, final String userName, final String userPhone) throws Exception {
        if (page == null) {
            throw new Exception("UnionBusinessRecommendServiceImpl.listUnionBusinessRecommedFromMe():参数page不能为空!");
        }
        if (busId == null) {
            throw new Exception("UnionBusinessRecommendServiceImpl.listUnionBusinessRecommedFromMe():参数busId不能为空!");
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
                    String[] isAcceptanceArray = isAcceptance.split(",");
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
	public Page listBrokerageToMe(Page page, final Integer fromBusId, final Integer toBusId, final Integer unionId
            , final String isConfirm, final String userName, final String userPhone) throws Exception {
		if (page == null) {
			throw new Exception("UnionBusinessRecommendServiceImpl.listBrokerageToMe():参数page不能为空!");
		}
		if (fromBusId == null) {
			throw new Exception("UnionBusinessRecommendServiceImpl.listBrokerageToMe():参数fromBusId不能为空!");
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
					String[] isConfirmArray = isConfirm.split(",");
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
	public Page listBrokerageFromMe(Page page, final Integer toBusId, final Integer fromBusId, final Integer unionId
            , final String isConfirm, final String userName, final String userPhone) throws Exception {
        if (page == null) {
            throw new Exception("UnionBusinessRecommendServiceImpl.listBrokerageFromMe():参数page不能为空!");
        }
        if (toBusId == null) {
            throw new Exception("UnionBusinessRecommendServiceImpl.listBrokerageFromMe():参数toBusId不能为空!");
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
                    String[] isConfirmArray = isConfirm.split(",");
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

}
