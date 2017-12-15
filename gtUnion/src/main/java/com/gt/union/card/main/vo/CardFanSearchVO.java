package com.gt.union.card.main.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 联盟卡卡号搜索
 *
 * @author linweicong
 * @version 2017-11-27 16:44:02
 */
@ApiModel(value = "联盟卡卡号搜索VO")
public class CardFanSearchVO {
    @ApiModelProperty(value = "联盟卡粉丝信息")
    private UnionCardFan fan;

    @ApiModelProperty(value = "联盟积分")
    private Double integral;

    @ApiModelProperty(value = "消耗多少积分可抵扣1元")
    private Double exchangeIntegral;

    @ApiModelProperty(value = "可选联盟列表")
    private List<UnionMain> unionList;

    @ApiModelProperty(value = "当前选中的联盟")
    private UnionMain currentUnion;

    @ApiModelProperty(value = "当前选中联盟下的盟员身份")
    private UnionMember currentMember;

    @ApiModelProperty(value = "优惠项目是否可用(0:否 1:是)")
    private Integer isProjectAvailable;

    public UnionCardFan getFan() {
        return fan;
    }

    public void setFan(UnionCardFan fan) {
        this.fan = fan;
    }

    public Double getIntegral() {
        return integral;
    }

    public void setIntegral(Double integral) {
        this.integral = integral;
    }

    public Double getExchangeIntegral() {
        return exchangeIntegral;
    }

    public void setExchangeIntegral(Double exchangeIntegral) {
        this.exchangeIntegral = exchangeIntegral;
    }

    public List<UnionMain> getUnionList() {
        return unionList;
    }

    public void setUnionList(List<UnionMain> unionList) {
        this.unionList = unionList;
    }

    public UnionMain getCurrentUnion() {
        return currentUnion;
    }

    public void setCurrentUnion(UnionMain currentUnion) {
        this.currentUnion = currentUnion;
    }

    public UnionMember getCurrentMember() {
        return currentMember;
    }

    public void setCurrentMember(UnionMember currentMember) {
        this.currentMember = currentMember;
    }

    public Integer getIsProjectAvailable() {
        return isProjectAvailable;
    }

    public void setIsProjectAvailable(Integer isProjectAvailable) {
        this.isProjectAvailable = isProjectAvailable;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
