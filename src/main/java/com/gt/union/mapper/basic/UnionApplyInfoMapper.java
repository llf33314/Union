package com.gt.union.mapper.basic;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.gt.union.entity.basic.UnionApplyInfo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
  * 联盟成员申请信息 Mapper 接口
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
public interface UnionApplyInfoMapper extends BaseMapper<UnionApplyInfo> {

	/**
	 * 根据主账号商家id和联盟id查询盟员信息
	 * @param busId	主账号商家id
	 * @param unionId	联盟id
	 * @return
	 */
	UnionApplyInfo selectUnionApplyInfo(@Param("busId") Integer busId, @Param("unionId") Integer unionId);
}