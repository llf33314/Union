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
 * 入盟申请
 *
 * @author linweicong
 * @version 2017-11-30 15:29:33
 */
@ApiModel(value = "入盟申请")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("t_union_member_join")
public class UnionMemberJoin extends Model<UnionMemberJoin> {
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
     * 入盟类型(1:申请 2:推荐)
     */
    @ApiModelProperty(value = "入盟类型(1:申请 2:推荐)")
    @TableField("type")
    private Integer type;

    /**
     * 入盟盟员id
     */
    @ApiModelProperty(value = "入盟盟员id")
    @TableField("apply_member_id")
    private Integer applyMemberId;

    /**
     * 推荐盟员id
     */
    @ApiModelProperty(value = "推荐盟员id")
    @TableField("recommend_member_id")
    private Integer recommendMemberId;

    /**
     * 加入或推荐理由
     */
    @ApiModelProperty(value = "加入或推荐理由")
    @TableField("reason")
    private String reason;

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

    public Integer getRecommendMemberId() {
        return recommendMemberId;
    }

    public void setRecommendMemberId(Integer recommendMemberId) {
        this.recommendMemberId = recommendMemberId;
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