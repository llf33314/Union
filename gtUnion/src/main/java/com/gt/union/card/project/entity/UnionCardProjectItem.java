package com.gt.union.card.project.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 项目优惠
 *
 * @author linweicong
 * @version 2017-11-30 15:22:39
 */
@ApiModel(value = "项目优惠")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("t_union_card_project_item")
public class UnionCardProjectItem extends Model<UnionCardProjectItem> {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 是否删除(0:否 1:是)
     */
    @ApiModelProperty(value = "是否删除(0:否 1:是)")
    @TableField("del_status")
    private Integer delStatus;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    @TableField("modify_time")
    private Date modifyTime;

    /**
     * 类型(1:非ERP文本优惠 2:ERP文本优惠 3:ERP商品优惠)
     */
    @ApiModelProperty(value = "类型(1:非ERP文本优惠 2:ERP文本优惠 3:ERP商品优惠)")
    @TableField("type")
    private Integer type;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @TableField("name")
    private String name;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    @TableField("number")
    private Integer number;

    /**
     * 规格
     */
    @ApiModelProperty(value = "规格")
    @TableField("size")
    private String size;

    /**
     * ERP类型(1:车小算 2:样子)
     */
    @ApiModelProperty(value = "ERP类型(1:车小算 2:样子)")
    @TableField("erp_type")
    private Integer erpType;

    /**
     * 门店id
     */
    @ApiModelProperty(value = "门店id")
    @TableField("shop_id")
    private Integer shopId;

    /**
     * ERP系统的文本项目id
     */
    @ApiModelProperty(value = "ERP系统的文本项目id")
    @TableField("erp_text_id")
    private Integer erpTextId;

    /**
     * ERP系统的商品项目id
     */
    @ApiModelProperty(value = "ERP系统的商品项目id")
    @TableField("erp_goods_id")
    private Integer erpGoodsId;

    /**
     * 项目id
     */
    @ApiModelProperty(value = "项目id")
    @TableField("project_id")
    private Integer projectId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(Integer delStatus) {
        this.delStatus = delStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getErpType() {
        return erpType;
    }

    public void setErpType(Integer erpType) {
        this.erpType = erpType;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public Integer getErpTextId() {
        return erpTextId;
    }

    public void setErpTextId(Integer erpTextId) {
        this.erpTextId = erpTextId;
    }

    public Integer getErpGoodsId() {
        return erpGoodsId;
    }

    public void setErpGoodsId(Integer erpGoodsId) {
        this.erpGoodsId = erpGoodsId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
    }
}