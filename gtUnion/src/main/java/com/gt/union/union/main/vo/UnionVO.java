package com.gt.union.union.main.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.entity.UnionMainDict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 联盟基础信息
 *
 * @author linweicong
 * @version 2017-11-25 16:51:15
 */
@ApiModel(value = "联盟基础信息VO")
public class UnionVO {
    @ApiModelProperty(value = "联盟主信息")
    private UnionMain union;

    @ApiModelProperty(value = "入盟必填字段列表")
    private List<UnionMainDict> itemList;

    public UnionMain getUnion() {
        return union;
    }

    public void setUnion(UnionMain union) {
        this.union = union;
    }

    public List<UnionMainDict> getItemList() {
        return itemList;
    }

    public void setItemList(List<UnionMainDict> itemList) {
        this.itemList = itemList;
    }

    @Override
    public String toString() {
        return JSONArray.toJSONString(this);
    }
}
