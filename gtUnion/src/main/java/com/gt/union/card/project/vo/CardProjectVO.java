package com.gt.union.card.project.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 活动卡设置-我的活动项目
 *
 * @author linweicong
 * @version 2017-11-27 14:42:43
 */
@ApiModel(value = "活动卡设置-我的活动项目VO")
public class CardProjectVO {
    @ApiModelProperty(value = "项目所属盟员")
    private UnionMember member;

    @ApiModelProperty(value = "活动项目")
    private UnionCardProject project;
    
    @ApiModelProperty(value = "活动")
    private UnionCardActivity activity;

    @ApiModelProperty(value = "是否ERP(0:否 1:是)")
    private Integer isErp;

    @ApiModelProperty(value = "非ERP文本项目优惠列表")
    private List<UnionCardProjectItem> nonErpTextList;

    @ApiModelProperty(value = "ERP文本项目优惠列表")
    private List<UnionCardProjectItem> erpTextList;

    @ApiModelProperty(value = "ERP商品项目优惠列表")
    private List<UnionCardProjectItem> erpGoodsList;

    public UnionMember getMember() {
        return member;
    }

    public void setMember(UnionMember member) {
        this.member = member;
    }

    public UnionCardProject getProject() {
        return project;
    }

    public void setProject(UnionCardProject project) {
        this.project = project;
    }

    public UnionCardActivity getActivity() {
        return activity;
    }

    public void setActivity(UnionCardActivity activity) {
        this.activity = activity;
    }

    public Integer getIsErp() {
        return isErp;
    }

    public void setIsErp(Integer isErp) {
        this.isErp = isErp;
    }

    public List<UnionCardProjectItem> getNonErpTextList() {
        return nonErpTextList;
    }

    public void setNonErpTextList(List<UnionCardProjectItem> nonErpTextList) {
        this.nonErpTextList = nonErpTextList;
    }

    public List<UnionCardProjectItem> getErpTextList() {
        return erpTextList;
    }

    public void setErpTextList(List<UnionCardProjectItem> erpTextList) {
        this.erpTextList = erpTextList;
    }

    public List<UnionCardProjectItem> getErpGoodsList() {
        return erpGoodsList;
    }

    public void setErpGoodsList(List<UnionCardProjectItem> erpGoodsList) {
        this.erpGoodsList = erpGoodsList;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
