package com.gt.union.service.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionApply;

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
	 * 根据联盟id获取入盟申请，并根据enterpriseName/directorPhone进行模块匹配
	 * @param page
	 * @param unionId
	 * @param enterpriseName
	 * @param directorPhone
	 * @return
	 * @throws Exception
	 */
	Page listUnionApplyVO(Page page, final Integer unionId, final String enterpriseName, final String directorPhone) throws Exception;
}
