package com.gt.union.opportunity.main.entity;

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
 * 商机佣金比率
 *
 * @author linweicong
 * @version 2017-11-30 15:26:46
 */
@ApiModel(value = "商机佣金比率")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("t_union_opportunity_ratio")
public class UnionOpportunityRatio extends Model<UnionOpportunityRatio> {
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
     * 设置佣金比率盟员id
     */
    @ApiModelProperty(value = "设置佣金比率盟员id")
    @TableField("from_member_id")
    private Integer fromMemberId;

    /**
     * 受惠佣金比率盟员id
     */
    @ApiModelProperty(value = "受惠佣金比率盟员id")
    @TableField("to_member_id")
    private Integer toMemberId;

    /**
     * 联盟id
     */
    @ApiModelProperty(value = "联盟id")
    @TableField("union_id")
    private Integer unionId;

    /**
     * 佣金比率（百分比）
     */
    @ApiModelProperty(value = "佣金比率（百分比）")
    @TableField("ratio")
    private Double ratio;


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

    public Integer getFromMemberId() {
        return fromMemberId;
    }

    public void setFromMemberId(Integer fromMemberId) {
        this.fromMemberId = fromMemberId;
    }

    public Integer getToMemberId() {
        return toMemberId;
    }

    public void setToMemberId(Integer toMemberId) {
        this.toMemberId = toMemberId;
    }

    public Integer getUnionId() {
        return unionId;
    }

    public void setUnionId(Integer unionId) {
        this.unionId = unionId;
    }

    public Double getRatio() {
        return ratio;
    }

    public void setRatio(Double ratio) {
        this.ratio = ratio;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}