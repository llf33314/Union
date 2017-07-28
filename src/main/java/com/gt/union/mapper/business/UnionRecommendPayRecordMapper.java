package com.gt.union.mapper.business;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.gt.union.entity.business.UnionRecommendPayRecord;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
  * 联盟商机佣金支付记录 Mapper 接口
 * </p>
 *
 * @author linweicong
 * @since 2017-07-27
 */
public interface UnionRecommendPayRecordMapper extends BaseMapper<UnionRecommendPayRecord> {

	/**
	 * 获取商家获得的佣金总和
	 * @param busId
	 * @param unionId
	 * @return
	 */
	double selectRecommendPaySum(@Param("busId") Integer busId, @Param("unionId") Integer unionId);
}