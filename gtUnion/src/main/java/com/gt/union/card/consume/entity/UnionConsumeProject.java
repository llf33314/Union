package com.gt.union.card.consume.entity;

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
 * 消费核销项目优惠
 *
 * @author linweicong
 * @version 2017-11-30 15:19:43
 */
@ApiModel(value = "消费核销项目优惠")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("t_union_consume_project")
public class UnionConsumeProject extends Model<UnionConsumeProject> {
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
     * 消费类型(1:线上 2:线下)
     */
    @ApiModelProperty(value = "消费类型(1:线上 2:线下)")
    @TableField("type")
    private Integer type;

    /**
     * 核销消费id
     */
    @ApiModelProperty(value = "核销消费id")
    @TableField("consume_id")
    private Integer consumeId;

    /**
     * 项目优惠id
     */
    @ApiModelProperty(value = "项目优惠id")
    @TableField("project_item_id")
    private Integer projectItemId;

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

    public Integer getProjectItemId() {
        return projectItemId;
    }

    public void setProjectItemId(Integer projectItemId) {
        this.projectItemId = projectItemId;
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
}