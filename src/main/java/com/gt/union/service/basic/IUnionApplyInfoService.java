package com.gt.union.service.basic;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionApplyInfo;

/**
 * <p>
 * 联盟成员申请信息 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
public interface IUnionApplyInfoService extends IService<UnionApplyInfo> {

	/**
	 * 更新盟员信息
	 * @param unionApplyInfo
	 * @param id
	 * @param unionId
	 */
	void updateUnionApplyInfo(UnionApplyInfo unionApplyInfo, Integer id, Integer unionId) throws Exception;
}
