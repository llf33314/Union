package com.gt.union.card.project.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 活动卡设置-审核项目-通过或不通过
 *
 * @author linweicong
 * @version 2017-12-07 09:19:15
 */
@ApiModel(value = "活动卡设置-审核项目-通过或不通过VO")
public class CardProjectCheckUpdateVO {
    @ApiModelProperty(value = "不通过理由")
    private String rejectReason;

    @ApiModelProperty(value = "项目id列表")
    private List<Integer> projectIdList;

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public List<Integer> getProjectIdList() {
        return projectIdList;
    }

    public void setProjectIdList(List<Integer> projectIdList) {
        this.projectIdList = projectIdList;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
