package com.gt.union.service.basic;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionNotice;

/**
 * <p>
 * 联盟公告 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
public interface IUnionNoticeService extends IService<UnionNotice> {

	/**
	 * 获取联盟公告
	 * @param unionId
	 * @return
	 */
	UnionNotice getByUnionId(Integer unionId);

	/**
	 *	保存联盟公告
	 * @param unionId
	 * @param busId
	 * @param noticeContent
	 * @return
	 */
	UnionNotice saveByUnionId(Integer unionId, Integer busId, String noticeContent) throws Exception;
}
