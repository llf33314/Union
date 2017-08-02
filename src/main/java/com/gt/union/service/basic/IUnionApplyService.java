package com.gt.union.service.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionApply;
import com.gt.union.entity.basic.UnionApplyInfo;

/**
 * <p>
 * 联盟成员申请推荐 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
public interface IUnionApplyService extends IService<UnionApply> {
	/**
	 * 根据联盟id获取入盟申请，并根据enterpriseName/directorPhone进行模糊匹配
	 * @param page
	 * @param unionId
	 * @param enterpriseName
	 * @param directorPhone
	 * @return
	 * @throws Exception
	 */
	Page listUncheckedApply(Page page, final Integer unionId, final String enterpriseName, final String directorPhone) throws Exception;

	/**
	 * 获取盟员信息
	 * @param busId	主账号商家id
	 * @param unionId	联盟id
	 * @return
	 */
	UnionApplyInfo getUnionApplyInfo(Integer busId, Integer unionId) throws Exception;

	/**
	 * 获取商家申请加盟
	 * @param busId	商家id
	 * @param unionId	联盟id
	 * @return
	 */
	int getUnionApply(Integer busId, Integer unionId);
}
