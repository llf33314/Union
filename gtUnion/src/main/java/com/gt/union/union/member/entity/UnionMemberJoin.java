package com.gt.union.union.member.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 入盟申请
 *
 * @author linweicong
 * @version 2017-11-23 10:22:05
 */
@TableName("t_union_member_join")
public class UnionMemberJoin extends Model<UnionMemberJoin> {
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
     * 入盟类型(1:申请 2:推荐)
     */
    @TableField("type")
    private Integer type;

    /**
     * 入盟盟员id
     */
    @TableField("apply_member_id")
    private Integer applyMemberId;

    /**
     * 推荐盟员id
     */
    @TableField("recommend_member_id")
    private Integer recommendMemberId;

    /**
     * 是否同意推荐(0：不同意 1：同意)
     */
    @TableField("is_recommend_agree")
    private Integer isRecommendAgree;

    /**
     * 加入或推荐理由
     */
    @TableField("reason")
    private String reason;

    /**
     * 联盟id
     */
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

    public Integer getRecommendMemberId() {
        return recommendMemberId;
    }

    public void setRecommendMemberId(Integer recommendMemberId) {
        this.recommendMemberId = recommendMemberId;
    }

    public Integer getIsRecommendAgree() {
        return isRecommendAgree;
    }

    public void setIsRecommendAgree(Integer isRecommendAgree) {
        this.isRecommendAgree = isRecommendAgree;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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