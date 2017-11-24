package com.gt.union.card.vo;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 项目
 *
 * @author linweicong
 * @version 2017-11-24 17:30:19
 */
@ApiModel(value = "项目")
public class Project {
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "规格")
    private String size;

    @ApiModelProperty(value = "数量")
    private Integer number;

    @ApiModelProperty(value = "剩余有效数量")
    private Integer surplusNumber;

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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getSurplusNumber() {
        return surplusNumber;
    }

    public void setSurplusNumber(Integer surplusNumber) {
        this.surplusNumber = surplusNumber;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
