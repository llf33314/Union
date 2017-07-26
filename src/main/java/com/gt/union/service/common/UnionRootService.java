package com.gt.union.service.common;

import com.gt.union.common.exception.ParameterException;
import com.gt.union.entity.basic.UnionMain;

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


	//int unionMainOwnerServiceValidity(UnionMain main);


}
