package com.gt.union.card.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 活动项目
 *
 * @author linweicong
 * @version 2017-11-24 17:34:31
 */
@ApiModel(value = "活动项目")
public class ActivityProjectVO {
    @ApiModelProperty(value = "活动项目id")
    private Integer activityProjectId;

    @ApiModelProperty(value = "盟员名称")
    private String memberName;

    @ApiModelProperty(value = "活动项目列表")
    List<Project> projectList;

    public Integer getActivityProjectId() {
        return activityProjectId;
    }

    public void setActivityProjectId(Integer activityProjectId) {
        this.activityProjectId = activityProjectId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
