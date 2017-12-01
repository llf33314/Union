package com.gt.union.card.main.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.card.consume.entity.UnionConsume;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 消费核销记录
 *
 * @author linweicong
 * @version 2017-11-29 14:18:17
 */
@ApiModel(value = "消费核销记录VO")
public class CardApplyPayVO {
    @ApiModelProperty(value = "门店id")
    private Integer shopId;

    @ApiModelProperty(value = "消费核销信息")
    private UnionConsume consume;

    @ApiModelProperty(value = "联盟卡活动id")
    private Integer activityId;

    @ApiModelProperty(value = "非ERP文本项目优惠列表")
    private List<UnionCardProjectItem> nonErpTextList;

    @ApiModelProperty(value = "ERP系统的文本项目优惠列表")
    private List<UnionCardProjectItem> erpTextList;

    @ApiModelProperty(value = "ERP系统的商品项目优惠列表")
    private List<UnionCardProjectItem> erpGoodsList;

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public UnionConsume getConsume() {
        return consume;
    }

    public void setConsume(UnionConsume consume) {
        this.consume = consume;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
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
