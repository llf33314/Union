package com.gt.union.main.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.entity.UnionMainDict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 更新联盟
 *
 * @author linweicong
 * @version 2017-11-24 15:12:19
 */
@ApiModel(value = "更新联盟")
public class UnionUpdateVO {
    @ApiModelProperty(value = "联盟")
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
