package com.gt.union.member.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 退盟过渡期
 *
 * @author linweicong
 * @version 2017-11-24 18:29:23
 */
@ApiModel(value = "退盟过渡期")
public class OutPeriodVO {
    @ApiModelProperty(value = "是否盟主(0:否 1:是)")
    private Integer isUnionOwner;

    @ApiModelProperty(value = "退盟过渡期盟员列表")
    private List<OutVO> periodMemberList;

    public Integer getIsUnionOwner() {
        return isUnionOwner;
    }

    public void setIsUnionOwner(Integer isUnionOwner) {
        this.isUnionOwner = isUnionOwner;
    }

    public List<OutVO> getPeriodMemberList() {
        return periodMemberList;
    }

    public void setPeriodMemberList(List<OutVO> periodMemberList) {
        this.periodMemberList = periodMemberList;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
