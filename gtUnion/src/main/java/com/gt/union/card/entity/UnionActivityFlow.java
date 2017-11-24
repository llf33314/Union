package com.gt.union.card.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 活动项目流程
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
@TableName("t_union_activity_flow")
public class UnionActivityFlow extends Model<UnionActivityFlow> {
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
     * 说明
     */
    @TableField("illustration")
    private String illustration;

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

    public String getIllustration() {
        return illustration;
    }

    public void setIllustration(String illustration) {
        this.illustration = illustration;
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