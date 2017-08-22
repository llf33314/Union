package com.gt.union.vo.business;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2017/8/22 0022.
 */
@ApiModel(value = "UnionBrokerageVO", description = "商机佣金比例设置参数实体：id为空时，为新增；否则，为更新")
public class UnionBrokerageVO {
    @ApiModelProperty(value = "id" ,required = false)
    private Integer id;

    @ApiModelProperty(value = "联盟id", required = true)
    @NotNull(message = "联盟id不能为空")
    private Integer unionId;

    @ApiModelProperty(value = "商机佣金比例被设置方", required = true)
    @NotNull(message = "商机佣金比例被设置方不能为空")
    private Integer toBusId;

    @ApiModelProperty(value = "商机佣金比例", required = true)
    @NotNull(message = "商机佣金比例不能为空")
    @DecimalMax(value = "100", message = "商机佣金比例不能大于100")
    @DecimalMin(value = "0", message = "商机佣金比例不能小于0")
    private Double brokerageRatio;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUnionId() {
        return unionId;
    }

    public void setUnionId(Integer unionId) {
        this.unionId = unionId;
    }

    public Integer getToBusId() {
        return toBusId;
    }

    public void setToBusId(Integer toBusId) {
        this.toBusId = toBusId;
    }

    public Double getBrokerageRatio() {
        return brokerageRatio;
    }

    public void setBrokerageRatio(Double brokerageRatio) {
        this.brokerageRatio = brokerageRatio;
    }
}
