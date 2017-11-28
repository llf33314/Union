package com.gt.union.card.project.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 活动项目消费优惠
 *
 * @author linweicong
 * @version 2017-11-27 14:42:43
 */
@ApiModel(value = "活动项目消费优惠VO")
public class CardProjectItemConsumeVO {
    @ApiModelProperty(value = "项目优惠")
    private UnionCardProjectItem item;
    
    @ApiModelProperty(value = "项目优惠可使用数量")
    private Integer availableCount;

    public UnionCardProjectItem getItem() {
        return item;
    }

    public void setItem(UnionCardProjectItem item) {
        this.item = item;
    }

    public Integer getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(Integer availableCount) {
        this.availableCount = availableCount;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
