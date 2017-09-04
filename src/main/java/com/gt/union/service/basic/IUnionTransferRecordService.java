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

    /**
     * 根据联盟id、转移商家id和被转移商家id判断是否存在指定审核状态的转移记录
     * @param unionId
     * @param toBusId
     * @param confirmStatus
     * @return
     * @throws Exception
     */
	public boolean existByUnionIdAndBusIdAndConfirmStatus(Integer unionId, Integer toBusId, Integer confirmStatus) throws Exception;

    /**
     * 新增盟主权限转移记录
     * @param unionTransferRecord
     * @throws Exception
     */
	public void save(UnionTransferRecord unionTransferRecord) throws Exception;

	/**
	 * 批量更新转移信息
	 * @param unionId	联盟id
	 */
	void updateBatch(Integer unionId);
}
