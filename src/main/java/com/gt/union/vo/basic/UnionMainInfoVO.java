package com.gt.union.vo.basic;

import com.gt.union.entity.basic.UnionInfoDict;
import com.gt.union.entity.basic.UnionMain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Administrator on 2017/8/14 0014.
 */
public class UnionMainInfoVO {
	/**
	 * 联盟信息
	 */
	@Valid
	private UnionMain unionMain;

	/**
	 * 联盟申请/推荐收集信息列表
	 */
	@NotNull(message = "请设置收集信息")
	private List<UnionInfoDict> infos;

	public UnionMain getUnionMain() {
		return unionMain;
	}

	public void setUnionMain(UnionMain unionMain) {
		this.unionMain = unionMain;
	}

	public List<UnionInfoDict> getInfos() {
		return infos;
	}

	public void setInfos(List<UnionInfoDict> infos) {
		this.infos = infos;
	}
}
