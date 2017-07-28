package com.gt.union.mapper.basic;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.gt.union.entity.basic.UnionMain;

import java.util.List;

/**
 * <p>
  * 联盟主表 Mapper 接口
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
public interface UnionMainMapper extends BaseMapper<UnionMain> {

	/**
	 * 根据商家id获取该商家加入的联盟列表
	 * @param busId
	 * @return
	 */
	List<UnionMain> selectMemberUnionList(Integer busId);
}