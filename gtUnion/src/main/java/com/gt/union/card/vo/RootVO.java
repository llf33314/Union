package com.gt.union.card.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.main.entity.UnionMain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 根
 *
 * @author linweicong
 * @version 2017-11-25 10:23:29
 */
@ApiModel(value = "根")
public class RootVO {
    @ApiModelProperty(value = "卡号")
    private String number;

    @ApiModelProperty(value = "积分")
    private Double integral;

    @ApiModelProperty(value = "联盟列表")
    private List<UnionMain> unionList;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Double getIntegral() {
        return integral;
    }

    public void setIntegral(Double integral) {
        this.integral = integral;
    }

    public List<UnionMain> getUnionList() {
        return unionList;
    }

    public void setUnionList(List<UnionMain> unionList) {
        this.unionList = unionList;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
