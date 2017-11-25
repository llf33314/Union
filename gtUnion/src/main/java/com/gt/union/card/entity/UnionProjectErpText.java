package com.gt.union.card.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * ERP文本项目
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@TableName("t_union_project_erp_text")
public class UnionProjectErpText extends Model<UnionProjectErpText> {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 是否删除(0:否 1:是)
     */
    @TableField("del_status")
    private Integer delStatus;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("modify_time")
    private Date modifyTime;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 数量
     */
    @TableField("number")
    private Integer number;

    /**
     * 剩余有效数量
     */
    @TableField("surplus_number")
    private Integer surplusNumber;

    /**
     * ERP类型(1:车小算 2:样子)
     */
    @TableField("erp_type")
    private Integer erpType;

    /**
     * 门店id
     */
    @TableField("shop_id")
    private Integer shopId;

    /**
     * ERP系统的文本项目id
     */
    @TableField("erp_text_id")
    private Integer erpTextId;

    /**
     * 活动项目id
     */
    @TableField("activity_project_id")
    private Integer activityProjectId;


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

    public Integer getSurplusNumber() {
        return surplusNumber;
    }

    public void setSurplusNumber(Integer surplusNumber) {
        this.surplusNumber = surplusNumber;
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

    public Integer getActivityProjectId() {
        return activityProjectId;
    }

    public void setActivityProjectId(Integer activityProjectId) {
        this.activityProjectId = activityProjectId;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}