package com.gt.union.card.main.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author linweicong
 * @version 2018-01-25 09:16:02
 */
@ApiModel(value = "活动卡优惠项目")
public class ActivityCardProject {
    @ApiModelProperty(value = "优惠项目对象")
    private UnionCardProject project;

    @ApiModelProperty(value = "优惠项目所属盟员")
    private UnionMember member;

    @ApiModelProperty(value = "优惠服务项对象列表")
    private List<UnionCardProjectItem> projectItemList;

    public UnionCardProject getProject() {
        return project;
    }

    public void setProject(UnionCardProject project) {
        this.project = project;
    }

    public UnionMember getMember() {
        return member;
    }

    public void setMember(UnionMember member) {
        this.member = member;
    }

    public List<UnionCardProjectItem> getProjectItemList() {
        return projectItemList;
    }

    public void setProjectItemList(List<UnionCardProjectItem> projectItemList) {
        this.projectItemList = projectItemList;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
