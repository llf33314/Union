package com.gt.union.opportunity.main.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gt.union.union.main.entity.UnionMain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author linweicong
 * @version 2018-01-25 10:15:29
 */
@ApiModel(value = "商机佣金按联盟统计详情")
public class OpportunityStatisticsDetail {
    @ApiModelProperty(value = "商机佣金所属联盟")
    private UnionMain union;

    @ApiModelProperty(value = "商机佣金总和")
    private Double moneySum;

    public UnionMain getUnion() {
        return union;
    }

    public void setUnion(UnionMain union) {
        this.union = union;
    }

    public Double getMoneySum() {
        return moneySum;
    }

    public void setMoneySum(Double moneySum) {
        this.moneySum = moneySum;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
