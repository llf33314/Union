package com.gt.union.service.basic;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.common.exception.ParameterException;
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
	 * 保存联盟公告
	 * @param notice
	 * @return
	 */
	UnionNotice saveNotice(UnionNotice notice) throws Exception;
}
