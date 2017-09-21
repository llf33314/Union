package com.gt.union.member.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 联盟成员退盟申请
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@TableName("t_union_member_out")
public class UnionMemberOut extends Model<UnionMemberOut> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 创建时间
     */
    private Date createtime;
    /**
     * 删除状态（0：未删除 1：删除）
     */
    @TableField("del_status")
    private Integer delStatus;
    /**
     * 退盟类型（1：自己申请退盟 2：盟主移出退盟）
     */
    private Integer type;
    /**
     * 申请退盟的盟员id
     */
    @TableId(value = "apply_member_id")
    private Integer applyMemberId;
    /**
     * 退盟理由
     */
    @TableField("apply_out_reason")
    private String applyOutReason;
    /**
     * 盟主审核退盟时间
     */
    @TableField("confirm_out_time")
    private Date confirmOutTime;
    /**
     * 实际退盟时间
     */
    @TableField("actual_out_time")
    private Date actualOutTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getApplyMemberId() {
        return applyMemberId;
    }

    public void setApplyMemberId(Integer applyMemberId) {
        this.applyMemberId = applyMemberId;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(Integer delStatus) {
        this.delStatus = delStatus;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
