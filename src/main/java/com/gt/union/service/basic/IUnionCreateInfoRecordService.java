package com.gt.union.service.basic;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionCreateInfoRecord;

/**
 * <p>
 * 创建联盟服务记录 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-31
 */
public interface IUnionCreateInfoRecordService extends IService<UnionCreateInfoRecord> {

	/**
	 * 根据商家id 获取创建盟主服务的信息
	 * @param busId	商家id
	 * @param unionId	联盟id
	 * @return  	返回为空：则联盟盟主服务已过，或没有创建  如果不为空，则可能创建了联盟，也可以没有创建联盟
	 */
	public UnionCreateInfoRecord getBusUnion(Integer busId, Integer unionId);
}
