package com.gt.union.h5.brokerage.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.member.entity.UnionMember;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 售卡佣金
 *
 * @author linweicong
 * @version 2017-12-01 14:37:42
 */
@ApiModel(value = "售卡佣金")
public class CardBrokerageVO {
    @ApiModelProperty(value = "联盟")
    private UnionMain union;

    @ApiModelProperty(value = "盟员")
    private UnionMember member;

    @ApiModelProperty(value = "售卡佣金收入")
    private UnionBrokerageIncome brokerageIncome;

    public UnionMain getUnion() {
        return union;
    }

    public void setUnion(UnionMain union) {
        this.union = union;
    }

    public UnionMember getMember() {
        return member;
    }

    public void setMember(UnionMember member) {
        this.member = member;
    }

    public UnionBrokerageIncome getBrokerageIncome() {
        return brokerageIncome;
    }

    public void setBrokerageIncome(UnionBrokerageIncome brokerageIncome) {
        this.brokerageIncome = brokerageIncome;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
    }
}
