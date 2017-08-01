package com.gt.union.vo.basic;

import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.entity.basic.UnionMain;

/**
 * Created by Administrator on 2017/7/31 0031.
 */
public class UnionMainInfoVo {

	/**
	 * 联盟信息
	 */
	private UnionMain unionMain;

	/**
	 * 盟员信息
	 */
	private UnionApplyInfo unionApplyInfo;

	public UnionMain getUnionMain() {
		return unionMain;
	}

	public void setUnionMain(UnionMain unionMain) {
		this.unionMain = unionMain;
	}

	public UnionApplyInfo getUnionApplyInfo() {
		return unionApplyInfo;
	}

	public void setUnionApplyInfo(UnionApplyInfo unionApplyInfo) {
		this.unionApplyInfo = unionApplyInfo;
	}
}
