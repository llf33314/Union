package com.gt.union.vo.basic;

import java.util.Date;

/**
 * Created by Administrator on 2017/7/24 0024.
 */
public class UnionApplyVO {
    /**
     * ------------来自UnionApply的字段------------
     */
    /**
     * 主键id
     */
    private Integer id = null;

    /**
     * 申请联盟id
     */
    private Integer unionId = null;

    /**
     * 创建时间
     */
    private Date createtime = null;

    /**
     * 申请商家id
     */
    //private Integer applyBusId = null;

    /**
     * 推荐商家id
     */
    //private Integer recommendBusId = null;

    /**
     * 推荐商家名称
     */
    private String recommendBusName = null;

    /**
     * 审核状态（0：未审核 1：审核通过 2：审核不通过）
     */
    private Integer applyStatus = null;

    /**
     * ------------来自UnionApplyInfo的字段------------
     */
    /**
     * 企业名称
     */
    private String enterpriseName = null;

    /**
     * 负责人名称
     */
    private String directorName = null;

    /**
     * 负责人电话
     */
    private String directorPhone = null;

    /**
     * 负责人邮箱
     */
    private String directorEmail = null;

    /**
     * 申请理由
     */
    private String applyReason = null;


    /**
     * ------------getter && setter------------
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUnionId() {
        return unionId;
    }

    public void setUnionId(Integer unionId) {
        this.unionId = unionId;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    /*public Integer getApplyBusId() {
        return applyBusId;
    }

    public void setApplyBusId(Integer applyBusId) {
        this.applyBusId = applyBusId;
    }

    public Integer getRecommendBusId() {
        return recommendBusId;
    }

    public void setRecommendBusId(Integer recommendBusId) {
        this.recommendBusId = recommendBusId;
    }*/

    public String getRecommendBusName() {
        return recommendBusName;
    }

    public void setRecommendBusName(String recommendBusName) {
        this.recommendBusName = recommendBusName;
    }

    public Integer getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(Integer applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }

    public String getDirectorPhone() {
        return directorPhone;
    }

    public void setDirectorPhone(String directorPhone) {
        this.directorPhone = directorPhone;
    }

    public String getDirectorEmail() {
        return directorEmail;
    }

    public void setDirectorEmail(String directorEmail) {
        this.directorEmail = directorEmail;
    }

    public String getApplyReason() {
        return applyReason;
    }

    public void setApplyReason(String applyReason) {
        this.applyReason = applyReason;
    }
}
