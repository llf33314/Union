package com.gt.union.card.activity.entity;

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
 * 活动
 *
 * @author linweicong
 * @version 2017-11-30 15:18:44
 */
@ApiModel(value = "活动")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("t_union_card_activity")
public class UnionCardActivity extends Model<UnionCardActivity> {
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
     * 联盟id
     */
    @ApiModelProperty(value = "联盟id")
    @TableField("union_id")
    private Integer unionId;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @TableField("name")
    private String name;

    /**
     * 价格
     */
    @ApiModelProperty(value = "价格")
    @TableField("price")
    private Double price;

    /**
     * 展示图
     */
    @ApiModelProperty(value = "展示图")
    @TableField("img")
    private String img;

    /**
     * 发行量
     */
    @ApiModelProperty(value = "发行量")
    @TableField("amount")
    private Integer amount;

    /**
     * 有效天数
     */
    @ApiModelProperty(value = "有效天数")
    @TableField("validity_day")
    private Integer validityDay;

    /**
     * 报名开始时间
     */
    @ApiModelProperty(value = "报名开始时间")
    @TableField("apply_begin_time")
    private Date applyBeginTime;

    /**
     * 报名结束时间
     */
    @ApiModelProperty(value = "报名结束时间")
    @TableField("apply_end_time")
    private Date applyEndTime;

    /**
     * 售卡开始时间
     */
    @ApiModelProperty(value = "售卡开始时间")
    @TableField("sell_begin_time")
    private Date sellBeginTime;

    /**
     * 售卡结束时间
     */
    @ApiModelProperty(value = "售卡结束时间")
    @TableField("sell_end_time")
    private Date sellEndTime;

    /**
     * 说明
     */
    @ApiModelProperty(value = "说明")
    @TableField("illustration")
    private String illustration;

    /**
     * 项目是否需要审核(0:否 1:是)
     */
    @ApiModelProperty(value = "项目是否需要审核(0:否 1:是)")
    @TableField("is_project_check")
    private Integer isProjectCheck;


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

    public Integer getUnionId() {
        return unionId;
    }

    public void setUnionId(Integer unionId) {
        this.unionId = unionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getValidityDay() {
        return validityDay;
    }

    public void setValidityDay(Integer validityDay) {
        this.validityDay = validityDay;
    }

    public Date getApplyBeginTime() {
        return applyBeginTime;
    }

    public void setApplyBeginTime(Date applyBeginTime) {
        this.applyBeginTime = applyBeginTime;
    }

    public Date getApplyEndTime() {
        return applyEndTime;
    }

    public void setApplyEndTime(Date applyEndTime) {
        this.applyEndTime = applyEndTime;
    }

    public Date getSellBeginTime() {
        return sellBeginTime;
    }

    public void setSellBeginTime(Date sellBeginTime) {
        this.sellBeginTime = sellBeginTime;
    }

    public Date getSellEndTime() {
        return sellEndTime;
    }

    public void setSellEndTime(Date sellEndTime) {
        this.sellEndTime = sellEndTime;
    }

    public String getIllustration() {
        return illustration;
    }

    public void setIllustration(String illustration) {
        this.illustration = illustration;
    }

    public Integer getIsProjectCheck() {
        return isProjectCheck;
    }

    public void setIsProjectCheck(Integer isProjectCheck) {
        this.isProjectCheck = isProjectCheck;
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