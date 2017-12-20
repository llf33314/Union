package com.gt.union.h5.card.vo;

import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author hongjiye
 * @time 2017-12-20 11:10
 **/
@ApiModel(value = "联盟卡详情页-列表信息")
public class CardDetailListVO {

	@ApiModelProperty(value = "盟员信息")
	private UnionMember unionMember;

	@ApiModelProperty(value = "盟员优惠项目信息")
	private List<UnionCardProjectItem> unionCardProjectItems;

	public UnionMember getUnionMember() {
		return unionMember;
	}

	public void setUnionMember(UnionMember unionMember) {
		this.unionMember = unionMember;
	}

	public List<UnionCardProjectItem> getUnionCardProjectItems() {
		return unionCardProjectItems;
	}

	public void setUnionCardProjectItems(List<UnionCardProjectItem> unionCardProjectItems) {
		this.unionCardProjectItems = unionCardProjectItems;
	}
}
