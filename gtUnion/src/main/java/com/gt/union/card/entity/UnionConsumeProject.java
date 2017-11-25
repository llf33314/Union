package com.gt.union.card.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 消费核销活动项目
 *
 * @author linweicong
 * @version 2017-11-25 10:51:42
 */
@TableName("t_union_consume_project")
public class UnionConsumeProject extends Model<UnionConsumeProject> {
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
     * 消费类型(1:线上 2:线下)
     */
    @TableField("type")
    private Integer type;

    /**
     * 核销消费id
     */
    @TableField("consume_id")
    private Integer consumeId;

    /**
     * 非ERP文本项目id
     */
    @TableField("project_text_id")
    private Integer projectTextId;

    /**
     * ERP文本项目id
     */
    @TableField("project_erp_text_id")
    private Integer projectErpTextId;

    /**
     * ERP商品项目id
     */
    @TableField("project_erp_goods_id")
    private Integer projectErpGoodsId;


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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getConsumeId() {
        return consumeId;
    }

    public void setConsumeId(Integer consumeId) {
        this.consumeId = consumeId;
    }

    public Integer getProjectTextId() {
        return projectTextId;
    }

    public void setProjectTextId(Integer projectTextId) {
        this.projectTextId = projectTextId;
    }

    public Integer getProjectErpTextId() {
        return projectErpTextId;
    }

    public void setProjectErpTextId(Integer projectErpTextId) {
        this.projectErpTextId = projectErpTextId;
    }

    public Integer getProjectErpGoodsId() {
        return projectErpGoodsId;
    }

    public void setProjectErpGoodsId(Integer projectErpGoodsId) {
        this.projectErpGoodsId = projectErpGoodsId;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}