package com.gt.union.opportunity.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 商机
 *
 * @author linweicong
 * @version 2017-11-23 16:56:17
 */
@TableName("t_union_opportunity")
public class UnionOpportunity extends Model<UnionOpportunity> {
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
     * 修改时间
     */
    @TableField("modify_time")
    private Date modifyTime;

    /**
     * 推荐类型(1:线上 2:线下)
     */
    @TableField("type")
    private Integer type;

    /**
     * 推荐盟员id
     */
    @TableField("from_member_id")
    private Integer fromMemberId;

    /**
     * 接收盟员id
     */
    @TableField("to_member_id")
    private Integer toMemberId;

    /**
     * 联盟id
     */
    @TableField("union_id")
    private Integer unionId;

    /**
     * 受理状态(1:受理中 2:已接受 3:已拒绝)
     */
    @TableField("accept_status")
    private Integer acceptStatus;

    /**
     * 受理金额
     */
    @TableField("accept_price")
    private Double acceptPrice;

    /**
     * 佣金金额
     */
    @TableField("brokerage_money")
    private Double brokerageMoney;

    /**
     * 客户姓名
     */
    @TableField("client_name")
    private String clientName;

    /**
     * 客户电话
     */
    @TableField("client_phone")
    private String clientPhone;

    /**
     * 业务备注
     */
    @TableField("business_msg")
    private String businessMsg;

    /**
     * 是否需要催促佣金(0:否 1:是)
     */
    @TableField("is_urge_brokerage")
    private Integer isUrgeBrokerage;


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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Integer getAcceptStatus() {
        return acceptStatus;
    }

    public void setAcceptStatus(Integer acceptStatus) {
        this.acceptStatus = acceptStatus;
    }

    public Double getAcceptPrice() {
        return acceptPrice;
    }

    public void setAcceptPrice(Double acceptPrice) {
        this.acceptPrice = acceptPrice;
    }

    public Double getBrokerageMoney() {
        return brokerageMoney;
    }

    public void setBrokerageMoney(Double brokerageMoney) {
        this.brokerageMoney = brokerageMoney;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getBusinessMsg() {
        return businessMsg;
    }

    public void setBusinessMsg(String businessMsg) {
        this.businessMsg = businessMsg;
    }

    public Integer getIsUrgeBrokerage() {
        return isUrgeBrokerage;
    }

    public void setIsUrgeBrokerage(Integer isUrgeBrokerage) {
        this.isUrgeBrokerage = isUrgeBrokerage;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}