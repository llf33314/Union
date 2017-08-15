package com.gt.union.service.common;

import com.gt.union.common.exception.BusinessException;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.common.BusUser;

/**
 * 商家联盟权限统一管理类
 * Created by Administrator on 2017/7/25 0025.
 */
public interface UnionRootService {

	/**
	 * 判断联盟是否可用
	 * @param unionId	联盟id
	 * return
	 */
	public int unionRoot(Integer unionId) throws Exception;

	/**
	 * 判断联盟是否可用
	 * @param main	联盟
	 * return
	 */
	public int unionRoot(UnionMain main) throws BusinessException, Exception;

}
