package com.gt.union.opportunity.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.opportunity.entity.UnionOpportunity;
import com.gt.union.opportunity.vo.UnionOpportunityBrokerageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
  * 商机推荐 Mapper 接口
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface UnionOpportunityMapper extends BaseMapper<UnionOpportunity> {

	/**
	 * 查询我的佣金收入
	 * @param members        我的盟员列表
	 * @param toMemberId    被推荐的盟员id
	 * @param unionId        联盟id
	 * @param incomeStatus        结算状态
	 * @param clientName    客户名称
	 * @param clientPhone    客户电话
	 * @param page
	 * @return
	 */
	List<UnionOpportunityBrokerageVO> listBrokerageFromMy(@Param("members") List<UnionMember> members, @Param("toMemberId") Integer toMemberId, @Param("unionId") Integer unionId, @Param("incomeStatus") Integer incomeStatus, @Param("clientName") String clientName, @Param("clientPhone") String clientPhone, Page page);

	/**
	 * 查询我的佣金收入
	 * @param members        我的盟员列表
	 * @param fromMemberId   推荐的盟员id
	 * @param unionId        联盟id
	 * @param incomeStatus        结算状态
	 * @param clientName    客户名称
	 * @param clientPhone    客户电话
	 * @param page
	 * @return
	 */
	List<UnionOpportunityBrokerageVO> listBrokerageToMy(@Param("members") List<UnionMember> members, @Param("fromMemberId") Integer fromMemberId, @Param("unionId") Integer unionId, @Param("incomeStatus") Integer incomeStatus, @Param("clientName") String clientName, @Param("clientPhone") String clientPhone, Page page);

	/**
	 * 查询佣金支付明细
	 * @param members	我所加入的所有盟员列表
	 * @param unionId	联盟id
	 * @param page
	 * @return
	 */
	List<Map<String,Object>> listPayDetail(@Param("members") List<UnionMember> members, @Param("unionId") Integer unionId, Page page);

	/**
	 * 查询在联盟下我与某盟员的佣金来往明细
	 * @param memberId    联盟id
	 * @param myMemberId    我的盟员id
	 * @return
	 */
	List<Map<String,Object>> listPayDetailParticularByUnionIdAndMemberId(@Param("memberId") Integer memberId, @Param("myMemberId") Integer myMemberId);

	/**
	 * 获取时间范围内盟员收入的佣金之和
	 * @param memberId		盟员id
	 * @param strDateBegin	起始时间
	 * @param strDateEnd	结束时间
	 * @return
	 */
	Double getBrokerageComeInDay(@Param("memberId") Integer memberId, @Param("strDateBegin") String strDateBegin, @Param("strDateEnd") String strDateEnd);

	/**
	 * 获取时间范围内盟员支出的佣金之和
	 * @param memberId		盟员id
	 * @param strDateBegin	起始时间
	 * @param strDateEnd	结束时间
	 * @return
	 */
	Double getBrokerageExpanseInDay(@Param("memberId") Integer memberId, @Param("strDateBegin") String strDateBegin, @Param("strDateEnd") String strDateEnd);
}