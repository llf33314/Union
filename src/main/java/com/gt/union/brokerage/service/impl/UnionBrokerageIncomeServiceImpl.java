package com.gt.union.brokerage.service.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.brokerage.constant.BrokerageConstant;
import com.gt.union.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.brokerage.entity.UnionBrokerageWithdrawal;
import com.gt.union.brokerage.mapper.UnionBrokerageIncomeMapper;
import com.gt.union.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.brokerage.service.IUnionBrokerageWithdrawalService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Autowired
	private UnionBrokerageIncomeMapper unionBrokerageIncomeMapper;

	@Autowired
	private IUnionBrokerageWithdrawalService unionBrokerageWithdrawalService;


	@Override
	public UnionBrokerageIncome getByUnionOpportunityId(Integer id) {
		EntityWrapper wrapper = new EntityWrapper();
		wrapper.eq("opportunity_id", id);
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
     * @return
     * @throws Exception
     */
    @Override
    public Page pageCardMapByBusIdAndMemberId(Page page, final Integer busId, Integer memberId, final Integer optionCardType
            , final String optionCardNumber) throws Exception {
        if (page == null || busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        final UnionMember unionMember = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionMember.getUnionId());
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
                sbSqlSegment.append(" ORDER BY bi.createtime ASC");
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" bi.id incomeId") //佣金收入id
                .append(", DATE_FORMAT(bi.createtime, '%Y-%m-%d %T') incomeCreatetime") //佣金收入时间
                .append(", cr.number cardNumber") //卡号
                .append(", c.type cardType") //卡类型(红、黑卡)
                .append(", bi.money incomeMoney") //佣金金额
                .append(", m.enterprise_name srcEnterpriseName"); //售卡出处
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }

	@Override
	public Map<String, Object> listAbleToWithdrawalBrokerage(Integer busId) throws Exception{
    	Map<String,Object> data = new HashMap<String,Object>();
		if (busId == null) {
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		Double sum = 0d;
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		/*List<UnionMember> members = unionMemberService.listByBusId(busId);
		if(ListUtil.isEmpty(members)){
			data.put("sum",sum);
			data.put("list",list);
			return null;
		}
		List<Map<String,Object>> incomes = unionBrokerageIncomeMapper.listIncomeByMemberIds(members);
		if(ListUtil.isEmpty(incomes)){
			data.put("list",list);
			data.put("sum",sum);
			return data;
		}
		List<UnionBrokerageWithdrawal> withdrawals = unionBrokerageWithdrawalService.listWithdrawalByMemberIds(members);
		if(ListUtil.isEmpty(withdrawals)){
			for(Map<String,Object> map : incomes){
				list.add(map);
				sum = BigDecimalUtil.add(Double.valueOf(map.get("money").toString()),sum).doubleValue();
			}
		}else {
			//有提现
			for(Map<String,Object> map : incomes){
				for (UnionBrokerageWithdrawal withdrawal : withdrawals){
					if(map.get("member_id").equals(withdrawal.getMemberId())){
						map.put("money",BigDecimalUtil.subtract(Double.valueOf(map.get("money").toString()),withdrawal.getMoney()).doubleValue());
						break;
					}
				}
				sum = BigDecimalUtil.add(Double.valueOf(map.get("money").toString()),sum).doubleValue();
				list.add(map);
			}
		}*/
		data.put("list",list);
		data.put("sum",sum);
		return data;
	}

	@Override
	public Double withdrawalSum(Integer busId) throws Exception{
    	if(busId == null){
			throw new ParamException(CommonConstant.PARAM_ERROR);
		}
		Double sum = 0d;
		/*List<UnionMember> members = unionMemberService.listAllByBusId(busId);
		if(ListUtil.isEmpty(members)){
			return sum;
		}
		Double incomeSum = this.withdrawalSumByMemberIds(members);//总收入
		Double withdrawalSum = unionBrokerageWithdrawalService.withdrawalSumByMemberIds(members);
		sum = BigDecimalUtil.subtract(incomeSum,withdrawalSum).doubleValue();*/
		return sum;
	}

	@Override
	public Double withdrawalSumByMemberIds(List<UnionMember> members) {
		List<Integer> ids = new ArrayList<Integer>();
		for(UnionMember member : members){
			ids.add(member.getId());
		}
    	EntityWrapper wrapper = new EntityWrapper<>();
    	wrapper.in("member_id", ids.toArray());
    	wrapper.setSqlSelect("IFNULL(SUM(money), 0) money");
    	Map<String,Object> data = this.selectMap(wrapper);
    	if(data == null){
    		return 0d;
		}
		return Double.valueOf(data.get("money").toString());
	}

	@Override
	public double getSumInComeUnionBrokerage(Integer busId) {
		EntityWrapper wrapper = new EntityWrapper<>();
		wrapper.eq("bus_id", busId);
		wrapper.setSqlSelect("IFNULL(SUM(money), 0) money");
		Map<String,Object> data = this.selectMap(wrapper);
		if(CommonUtil.isEmpty(data)){
			return 0;
		}
		return CommonUtil.toDouble(data.get("money"));
	}

	@Override
	public double getSumInComeUnionBrokerageByType(Integer busId, int type) {
		EntityWrapper wrapper = new EntityWrapper<>();
		wrapper.eq("bus_id", busId);
		wrapper.eq("type", type);
		wrapper.setSqlSelect("IFNULL(SUM(money), 0) money");
		Map<String,Object> data = this.selectMap(wrapper);
		if(CommonUtil.isEmpty(data)){
			return 0;
		}
		return CommonUtil.toDouble(data.get("money"));
	}
}

