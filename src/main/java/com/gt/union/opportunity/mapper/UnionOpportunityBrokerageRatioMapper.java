package com.gt.union.opportunity.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.brokerage.vo.UnionBrokerageRatioVO;
import com.gt.union.opportunity.entity.UnionOpportunityBrokerageRatio;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
  * 商机佣金比率 Mapper 接口
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface UnionOpportunityBrokerageRatioMapper extends BaseMapper<UnionOpportunityBrokerageRatio> {

	/**
	 * 根据盟员id和联盟id查询盟员的佣金比列表
	 * @param page
	 * @param memberId	盟员id
	 * @param unionId	联盟id
	 * @return
	 */
	List<UnionBrokerageRatioVO> pageBrokerageRatio(Page page, @Param("memberId") Integer memberId, @Param("unionId") Integer unionId);
}