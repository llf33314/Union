package com.gt.union.service.common;

import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.common.BusUser;

/**
 * 商家联盟权限统一管理类
 * Created by Administrator on 2017/7/25 0025.
 */
public interface UnionRootService {

	/**
	 * 判断该商家是否是该联盟盟主
	 * @param busId	商家id
	 * @param main	联盟
	 * @return 0：不是该盟盟主 1：是该盟盟主 -1：参数错误 -2：内部异常
	 */
	int isUnionMainOwner(Integer busId, UnionMain main);

	/**
	 * 当对某个商家进行操作时，需判断商家账号是否过期
	 * @param user  商家
	 * @return
	 */
	int isBusUserOverdue(BusUser user);


	/**
	 * 判断联盟服务是否过期
	 * @param main  联盟
	 * @return
	 */
	int isUnionMainOverdue(UnionMain main);

	//int unionMainOwnerServiceValidity(UnionMain main);


	/**
	 * 判断联盟是否可用
	 * @param unionId	联盟id
	 * return
	 */
	public int unionRoot(Integer unionId);

	/**
	 * 判断联盟是否可用
	 * @param main	联盟
	 * return
	 */
	public int unionRoot(UnionMain main);
}
