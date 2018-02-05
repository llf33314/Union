package com.gt.union.api.client.shop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 门店信息VO
 *
 * @author hongjiye
 * @time 2017-11-30 11:37
 **/
@ApiModel(value = "门店信息")
public class ShopVO {

    @ApiModelProperty(value = "门店id")
    private Integer id;

    @ApiModelProperty(value = "门店名称")
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ShopVO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
