package com.gt.union.user.introduction.vo;

import com.gt.union.user.introduction.entity.UnionUserIntroduction;
import io.swagger.annotations.ApiModel;

import java.util.List;

/**
 * @author hongjiye
 * @time 2018-01-24 16:53
 **/
@ApiModel(value = "商家简介信息保存")
public class UnionUserIntroductionVO {

	private List<UnionUserIntroduction> userIntroductionList;

	public List<UnionUserIntroduction> getUserIntroductionList() {
		return userIntroductionList;
	}

	public void setUserIntroductionList(List<UnionUserIntroduction> userIntroductionList) {
		this.userIntroductionList = userIntroductionList;
	}
}
