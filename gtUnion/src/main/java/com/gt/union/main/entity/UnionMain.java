package com.gt.union.main.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 联盟
 *
 * @author linweicong
 * @version 2017-11-23 15:26:16
 */
@TableName("t_union_main")
public class UnionMain extends Model<UnionMain> {
    private static final long serialVersionUID = 1L;

    /**
     * 主表
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
     * 图标
     */
    @TableField("img")
    private String img;

    /**
     * 加盟方式(1:推荐 2:申请、推荐)
     */
    @TableField("join_type")
    private Integer joinType;

    /**
     * 说明
     */
    @TableField("illustration")
    private String illustration;

    /**
     * 成员总数上限
     */
    @TableField("member_limit")
    private Integer memberLimit;

    /**
     * 是否开启积分(0:否 1:是)
     */
    @TableField("is_integral")
    private Integer isIntegral;

    /**
     * 有效期
     */
    @TableField("validity")
    private Date validity;


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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getJoinType() {
        return joinType;
    }

    public void setJoinType(Integer joinType) {
        this.joinType = joinType;
    }

    public String getIllustration() {
        return illustration;
    }

    public void setIllustration(String illustration) {
        this.illustration = illustration;
    }

    public Integer getMemberLimit() {
        return memberLimit;
    }

    public void setMemberLimit(Integer memberLimit) {
        this.memberLimit = memberLimit;
    }

    public Integer getIsIntegral() {
        return isIntegral;
    }

    public void setIsIntegral(Integer isIntegral) {
        this.isIntegral = isIntegral;
    }

    public Date getValidity() {
        return validity;
    }

    public void setValidity(Date validity) {
        this.validity = validity;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}