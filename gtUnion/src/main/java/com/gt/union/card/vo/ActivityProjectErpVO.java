package com.gt.union.card.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 跟erp有关的活动项目
 *
 * @author linweicong
 * @version 2017-11-24 17:55:32
 */
@ApiModel(value = "跟erp有关的活动项目")
public class ActivityProjectErpVO {
    @ApiModelProperty(value = "活动项目id")
    private Integer activityProjectId;

    @ApiModelProperty(value = "项目状态(1:未提交 2:审核中 3:已通过 4:不通过)")
    private Integer activityProjectStatus;

    @ApiModelProperty(value = "剩余有效天数")
    private Integer activitySurplusValidDay;

    @ApiModelProperty(value = "是否ERP项目")
    private Integer isErp;

    @ApiModelProperty(value = "非ERP文本项目列表")
    private List<Project> projectTextList;

    @ApiModelProperty(value = "ERP文本项目列表")
    private List<Project> projectErpTextList;

    @ApiModelProperty(value = "ERP商品项目列表")
    private List<Project> projectErpGoodsList;

    public Integer getActivityProjectId() {
        return activityProjectId;
    }

    public void setActivityProjectId(Integer activityProjectId) {
        this.activityProjectId = activityProjectId;
    }

    public Integer getActivityProjectStatus() {
        return activityProjectStatus;
    }

    public void setActivityProjectStatus(Integer activityProjectStatus) {
        this.activityProjectStatus = activityProjectStatus;
    }

    public Integer getActivitySurplusValidDay() {
        return activitySurplusValidDay;
    }

    public void setActivitySurplusValidDay(Integer activitySurplusValidDay) {
        this.activitySurplusValidDay = activitySurplusValidDay;
    }

    public Integer getIsErp() {
        return isErp;
    }

    public void setIsErp(Integer isErp) {
        this.isErp = isErp;
    }

    public List<Project> getProjectTextList() {
        return projectTextList;
    }

    public void setProjectTextList(List<Project> projectTextList) {
        this.projectTextList = projectTextList;
    }

    public List<Project> getProjectErpTextList() {
        return projectErpTextList;
    }

    public void setProjectErpTextList(List<Project> projectErpTextList) {
        this.projectErpTextList = projectErpTextList;
    }

    public List<Project> getProjectErpGoodsList() {
        return projectErpGoodsList;
    }

    public void setProjectErpGoodsList(List<Project> projectErpGoodsList) {
        this.projectErpGoodsList = projectErpGoodsList;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
