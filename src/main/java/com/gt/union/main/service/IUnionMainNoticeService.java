package com.gt.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.main.entity.UnionMainNotice;

/**
 * <p>
 * 联盟公告 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionMainNoticeService extends IService<UnionMainNotice> {
	/**
	 * 获取联盟公告
	 * @param unionId
	 * @return
	 */
	UnionMainNotice getByUnionId(Integer unionId);

	/**
	 *	保存联盟公告
	 * @param unionId
	 * @param busId
	 * @param content
	 * @return
	 */
	UnionMainNotice saveByUnionId(Integer unionId, Integer busId, String content) throws Exception;
}
