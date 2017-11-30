package com.gt.union.union.member.entity;

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
 * 退盟申请
 *
 * @author linweicong
 * @version 2017-11-30 15:29:33
 */
@ApiModel(value = "退盟申请")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("t_union_member_out")
public class UnionMemberOut extends Model<UnionMemberOut> {
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
     * 退盟类型(1:自己申请 2:盟主移出)
     */
    @ApiModelProperty(value = "退盟类型(1:自己申请 2:盟主移出)")
    @TableField("type")
    private Integer type;

    /**
     * 退盟盟员id
     */
    @ApiModelProperty(value = "退盟盟员id")
    @TableField("apply_member_id")
    private Integer applyMemberId;

    /**
     * 退盟理由
     */
    @ApiModelProperty(value = "退盟理由")
    @TableField("apply_out_reason")
    private String applyOutReason;

    /**
     * 盟主审核退盟时间
     */
    @ApiModelProperty(value = "盟主审核退盟时间")
    @TableField("confirm_out_time")
    private Date confirmOutTime;

    /**
     * 实际退盟时间
     */
    @ApiModelProperty(value = "实际退盟时间")
    @TableField("actual_out_time")
    private Date actualOutTime;

    /**
     * 联盟id
     */
    @ApiModelProperty(value = "联盟id")
    @TableField("union_id")
    private Integer unionId;


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

    public Integer getApplyMemberId() {
        return applyMemberId;
    }

    public void setApplyMemberId(Integer applyMemberId) {
        this.applyMemberId = applyMemberId;
    }

    public String getApplyOutReason() {
        return applyOutReason;
    }

    public void setApplyOutReason(String applyOutReason) {
        this.applyOutReason = applyOutReason;
    }

    public Date getConfirmOutTime() {
        return confirmOutTime;
    }

    public void setConfirmOutTime(Date confirmOutTime) {
        this.confirmOutTime = confirmOutTime;
    }

    public Date getActualOutTime() {
        return actualOutTime;
    }

    public void setActualOutTime(Date actualOutTime) {
        this.actualOutTime = actualOutTime;
    }

    public Integer getUnionId() {
        return unionId;
    }

    public void setUnionId(Integer unionId) {
        this.unionId = unionId;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}