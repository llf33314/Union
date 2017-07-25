package com.gt.union.service.common.impl;

import com.gt.union.service.common.UnionRootService;

/**
 *
 * 商家联盟权限统一管理类
 * Created by Administrator on 2017/7/25 0025.
 */
public class UnionRootServiceImpl implements UnionRootService{

	@Override
	public boolean isUnionMainOwner() {
		return false;
	}
}
