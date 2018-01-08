package com.gt.union.wxapp.card.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author hongjiye
 * @time 2017-12-19 11:42
 **/
@ApiModel(value = "h5联盟卡首页VO")
public class IndexVO {

	@ApiModelProperty(value = "首页联盟卡列表信息")
	List<UnionCardVO> cardList;

	public List<UnionCardVO> getCardList() {
		return cardList;
	}

	public void setCardList(List<UnionCardVO> cardList) {
		this.cardList = cardList;
	}
}
