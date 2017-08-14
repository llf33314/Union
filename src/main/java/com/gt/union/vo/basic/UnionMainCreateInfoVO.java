package com.gt.union.vo.basic;

import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.entity.basic.UnionInfoDict;
import com.gt.union.entity.basic.UnionMain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Administrator on 2017/7/31 0031.
 */
public class UnionMainCreateInfoVO {

	/**
	 * 联盟信息
	 */
	@Valid
	private UnionMain unionMain;

	/**
	 * 盟员信息
	 */
	@Valid
	private UnionApplyInfo unionApplyInfo;

	/**
	 * 联盟申请/推荐收集信息列表
	 */
	@Valid
	@NotNull(message = "请设置收集信息")
	private List<UnionInfoDict> infos;

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

	public List<UnionInfoDict> getInfos() {
		return infos;
	}

	public void setInfos(List<UnionInfoDict> infos) {
		this.infos = infos;
	}
}
