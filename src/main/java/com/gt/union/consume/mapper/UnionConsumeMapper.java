package com.gt.union.consume.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.consume.entity.UnionConsume;
import com.gt.union.consume.vo.UnionConsumeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
  * 消费 Mapper 接口
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface UnionConsumeMapper extends BaseMapper<UnionConsume> {

	/**
	 * 查询我店消费记录列表
	 * @param page
	 * @param unionId
	 * @param busId
	 * @param memberId
	 * @param cardNo
	 * @param phone
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<UnionConsumeVO> listMy(Page page, @Param("unionId") Integer unionId, @Param("busId") Integer busId, @Param("memberId") Integer memberId, @Param("cardNo") String cardNo, @Param("phone") String phone, @Param("beginTime") String beginTime, @Param("endTime") String endTime);

	/**
	 * 查询他店消费记录列表
	 * @param page
	 * @param unionId
	 * @param busId
	 * @param memberId
	 * @param cardNo
	 * @param phone
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<UnionConsumeVO> listOther(Page page, @Param("unionId") Integer unionId, @Param("busId") Integer busId, @Param("memberId") Integer memberId, @Param("cardNo") String cardNo, @Param("phone") String phone, @Param("beginTime") String beginTime, @Param("endTime") String endTime);

	/**
	 * 本店消费记录列表
	 * @param unionId
	 * @param busId
	 * @param memberId
	 * @param cardNo
	 * @param phone
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<Map<String,Object>> listMyByUnionId(@Param("unionId") Integer unionId, @Param("busId") Integer busId, @Param("memberId") Integer memberId, @Param("cardNo") String cardNo, @Param("phone") String phone, @Param("beginTime") String beginTime, @Param("endTime") String endTime);

	/**
	 * 他店消费记录列表
	 * @param unionId
	 * @param busId
	 * @param memberId
	 * @param cardNo
	 * @param phone
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<Map<String,Object>> listOtherByUnionId(@Param("unionId") Integer unionId, @Param("busId") Integer busId, @Param("memberId") Integer memberId, @Param("cardNo") String cardNo, @Param("phone") String phone, @Param("beginTime") String beginTime, @Param("endTime") String endTime);

	/**
	 * 计算本店消费记录总数
	 * @param unionId
	 * @param busId
	 * @param memberId
	 * @param cardNo
	 * @param phone
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	Integer listMyCount(@Param("unionId") Integer unionId, @Param("busId") Integer busId, @Param("memberId") Integer memberId, @Param("cardNo") String cardNo, @Param("phone") String phone, @Param("beginTime") String beginTime, @Param("endTime") String endTime);

	/**
	 * 计算他说店消费记录总数
	 * @param unionId
	 * @param busId
	 * @param memberId
	 * @param cardNo
	 * @param phone
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	Integer listOtherCount(@Param("unionId") Integer unionId, @Param("busId") Integer busId, @Param("memberId") Integer memberId, @Param("cardNo") String cardNo, @Param("phone") String phone, @Param("beginTime") String beginTime, @Param("endTime") String endTime);
}