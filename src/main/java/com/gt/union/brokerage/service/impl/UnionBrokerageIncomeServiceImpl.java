package com.gt.union.brokerage.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.brokerage.constant.BrokerageConstant;
import com.gt.union.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.brokerage.mapper.UnionBrokerageIncomeMapper;
import com.gt.union.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 佣金收入 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionBrokerageIncomeServiceImpl extends ServiceImpl<UnionBrokerageIncomeMapper, UnionBrokerageIncome> implements IUnionBrokerageIncomeService {

    @Autowired
    private IUnionMemberService unionMemberService;

    @Override
    public UnionBrokerageIncome getByUnionOpportunityId(Integer id) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("opportunity_id", id);
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        return this.selectOne(wrapper);
    }

    @Autowired
    private IUnionMainService unionMainService;

    /**
     * 根据商家id和盟员身份id，分页获取同一个联盟下的售卡佣金分成列表信息，并根据售卡类型(精确匹配)、卡号(模糊匹配)进行匹配
     *
     * @param page             {not null} 分页对象
     * @param busId            {not null} 商家id
     * @param memberId         {not null} 盟员身份id
     * @param optionCardType   可选项 售卡类型
     * @param optionCardNumber 可选项 卡号
     * @param optionBeginDate  可选项 开始日期
     * @param optionEndDate    可选项 结束日期
     * @return
     * @throws Exception
     */
    @Override
    public Page pageCardMapByBusIdAndMemberId(Page page, final Integer busId, Integer memberId, final Integer optionCardType
            , final String optionCardNumber, final String optionBeginDate, final String optionEndDate) throws Exception {
        if (page == null || busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        final UnionMember unionMember = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(unionMember.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //查询操作
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" bi")
                        .append(" LEFT JOIN t_union_card c ON c.id = bi.card_id")
                        .append(" LEFT JOIN t_union_card_root cr ON cr.id = c.root_id")
                        .append(" LEFT JOIN t_union_member m ON m.id = c.member_id")
                        .append(" WHERE bi.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND bi.type = ").append(BrokerageConstant.SOURCE_TYPE_CARD)
                        .append("  AND bi.bus_id = ").append(busId)
                        .append("  AND bi.card_id IS NOT NULL")
                        .append("  AND m.union_id = ").append(unionMember.getUnionId());
                if (optionCardType != null) {
                    sbSqlSegment.append(" AND c.type = ").append(optionCardType);
                }
                if (StringUtil.isNotEmpty(optionCardNumber)) {
                    sbSqlSegment.append(" AND cr.number LIKE %").append(optionCardNumber).append("%");
                }
                if (StringUtil.isNotEmpty(optionBeginDate)) {
                    sbSqlSegment.append(" AND bi.createtime >= '").append(optionBeginDate).append("'");
                }
                if (StringUtil.isNotEmpty(optionEndDate)) {
                    sbSqlSegment.append(" AND bi.createtime < '").append(optionEndDate).append("'");
                }
                sbSqlSegment.append(" ORDER BY bi.createtime ASC");
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" bi.id incomeId") //佣金收入id
                .append(", DATE_FORMAT(bi.createtime, '%Y-%m-%d %T') incomeCreateTime") //佣金收入时间
                .append(", cr.number cardNumber") //卡号
                .append(", c.type cardType") //卡类型(红、黑卡)
                .append(", bi.money incomeMoney") //佣金金额
                .append(", m.enterprise_name srcEnterpriseName"); //售卡出处
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }

    /**
     * 根据商家id和盟员身份id，获取同一个联盟下所有的售卡佣金分成列表信息，并根据售卡类型(精确匹配)、卡号(模糊匹配)进行匹配
     *
     * @param busId            {not null} 商家id
     * @param memberId         {not null} 盟员身份id
     * @param optionCardType   可选项 售卡类型
     * @param optionCardNumber 可选项 卡号
     * @param optionBeginDate  可选项 开始日期
     * @param optionEndDate    可选项 结束日期
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> listCardMapByBusIdAndMemberId(final Integer busId, Integer memberId, final Integer optionCardType
            , final String optionCardNumber, final String optionBeginDate, final String optionEndDate) throws Exception {
        if (busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        final UnionMember unionMember = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(unionMember.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //查询操作
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" bi2")
                        .append(" LEFT JOIN t_union_card c ON c.id = bi2.card_id")
                        .append(" LEFT JOIN t_union_card_root cr ON cr.id = c.root_id")
                        .append(" LEFT JOIN t_union_member m ON m.id = c.member_id")
                        .append(" WHERE bi2.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND bi2.type = ").append(BrokerageConstant.SOURCE_TYPE_CARD)
                        .append("  AND bi2.bus_id = ").append(busId)
                        .append("  AND bi2.card_id IS NOT NULL")
                        .append("  AND m.union_id = ").append(unionMember.getUnionId());
                if (optionCardType != null) {
                    sbSqlSegment.append(" AND c.type = ").append(optionCardType);
                }
                if (StringUtil.isNotEmpty(optionCardNumber)) {
                    sbSqlSegment.append(" AND cr.number LIKE %").append(optionCardNumber).append("%");
                }
                if (StringUtil.isNotEmpty(optionBeginDate)) {
                    sbSqlSegment.append(" AND bi2.createtime >= '").append(optionBeginDate).append("'");
                }
                if (StringUtil.isNotEmpty(optionEndDate)) {
                    sbSqlSegment.append(" AND bi2.createtime < '").append(optionEndDate).append("'");
                }
                sbSqlSegment.append(" ORDER BY bi2.createtime ASC");
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" bi2.id incomeId") //佣金收入id
                .append(", DATE_FORMAT(bi2.createtime, '%Y-%m-%d %T') incomeCreateTime") //佣金收入时间
                .append(", cr.number cardNumber") //卡号
                .append(", c.type cardType") //卡类型(红、黑卡)
                .append(", bi2.money incomeMoney") //佣金金额
                .append(", m.enterprise_name srcEnterpriseName"); //售卡出处
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMaps(wrapper);
    }


    @Override
    public double getSumInComeUnionBrokerage(Integer busId) {
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.eq("bus_id", busId);
        wrapper.eq("del_status",CommonConstant.DEL_STATUS_NO);
        wrapper.setSqlSelect("IFNULL(SUM(money), 0) money");
        Map<String, Object> data = this.selectMap(wrapper);
        if (CommonUtil.isEmpty(data)) {
            return 0;
        }
        return CommonUtil.toDouble(data.get("money"));
    }

    @Override
    public double getSumInComeUnionBrokerageByType(Integer busId, int type) {
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.eq("bus_id", busId);
        wrapper.eq("type", type);
        wrapper.eq("del_status",CommonConstant.DEL_STATUS_NO);
        wrapper.setSqlSelect("IFNULL(SUM(money), 0) money");
        Map<String, Object> data = this.selectMap(wrapper);
        if (CommonUtil.isEmpty(data)) {
            return 0;
        }
        return CommonUtil.toDouble(data.get("money"));
    }

    @Override
    public int countByOpportunityIds(List<Integer> ids) {
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.in("id", ids.toArray());
        wrapper.eq("type", BrokerageConstant.SOURCE_TYPE_OPPORTUNITY);
        wrapper.eq("del_status",CommonConstant.DEL_STATUS_NO);
        return this.selectCount(wrapper);
    }
}

