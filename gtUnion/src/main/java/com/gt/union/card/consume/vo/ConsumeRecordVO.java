package com.gt.union.card.consume.vo;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.card.consume.entity.UnionConsume;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.union.main.entity.UnionMain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 消费核销记录
 *
 * @author linweicong
 * @version 2017-11-25 11:21:28
 */
@ApiModel(value = "消费核销记录VO")
public class ConsumeRecordVO {
    @ApiModelProperty(value = "联盟")
    private UnionMain union;

    @ApiModelProperty(value = "门店名称")
    private String shopName;

    @ApiModelProperty(value = "联盟卡粉丝信息")
    private UnionCardFan fan;

    @ApiModelProperty(value = "消费核销信息")
    private UnionConsume consume;

    @ApiModelProperty(value = "非ERP文本项目优惠列表")
    private List<UnionCardProjectItem> nonErpTextList;

    @ApiModelProperty(value = "ERP系统的文本项目优惠列表")
    private List<UnionCardProjectItem> erpTextList;

    @ApiModelProperty(value = "ERP系统的商品项目优惠列表")
    private List<UnionCardProjectItem> erpGoodsList;

    public UnionMain getUnion() {
        return union;
    }

    public void setUnion(UnionMain union) {
        this.union = union;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public UnionCardFan getFan() {
        return fan;
    }

    public void setFan(UnionCardFan fan) {
        this.fan = fan;
    }

    public UnionConsume getConsume() {
        return consume;
    }

    public void setConsume(UnionConsume consume) {
        this.consume = consume;
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
