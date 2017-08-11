package com.gt.union.service.basic;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionTransferRecord;

/**
 * <p>
 * 联盟盟主转移记录 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
public interface IUnionTransferRecordService extends IService<UnionTransferRecord> {

	/**
	 * 获取转移联盟状态信息
	 * @param unionId
	 * @param busId
	 * @return
	 */
	public UnionTransferRecord get(Integer unionId, Integer busId);
}
