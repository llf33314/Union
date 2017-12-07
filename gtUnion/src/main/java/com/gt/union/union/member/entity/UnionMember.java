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
 * 盟员
 *
 * @author linweicong
 * @version 2017-11-30 15:29:33
 */
@ApiModel(value = "盟员")
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("t_union_member")
public class UnionMember extends Model<UnionMember> {
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
     * 联盟id
     */
    @ApiModelProperty(value = "联盟id")
    @TableField("union_id")
    private Integer unionId;

    /**
     * 商家id
     */
    @ApiModelProperty(value = "商家id")
    @TableField("bus_id")
    private Integer busId;

    /**
     * 是否盟主(0:否 1:是)
     */
    @ApiModelProperty(value = "是否盟主(0:否 1:是)")
    @TableField("is_union_owner")
    private Integer isUnionOwner;

    /**
     * 盟员状态(1:申请入盟 2:已入盟 3:申请退盟 4:退盟过渡期)
     */
    @ApiModelProperty(value = "盟员状态(1:申请入盟 2:已入盟 3:申请退盟 4:退盟过渡期)")
    @TableField("status")
    private Integer status;

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称")
    @TableField("enterprise_name")
    private String enterpriseName;

    /**
     * 企业地址
     */
    @ApiModelProperty(value = "企业地址")
    @TableField("enterprise_address")
    private String enterpriseAddress;

    /**
     * 负责人名称
     */
    @ApiModelProperty(value = "负责人名称")
    @TableField("director_name")
    private String directorName;

    /**
     * 负责人电话
     */
    @ApiModelProperty(value = "负责人电话")
    @TableField("director_phone")
    private String directorPhone;

    /**
     * 负责人邮箱
     */
    @ApiModelProperty(value = "负责人邮箱")
    @TableField("director_email")
    private String directorEmail;

    /**
     * 地址经度
     */
    @ApiModelProperty(value = "地址经度")
    @TableField("address_longitude")
    private String addressLongitude;

    /**
     * 地址维度
     */
    @ApiModelProperty(value = "地址维度")
    @TableField("address_latitude")
    private String addressLatitude;

    /**
     * 盟员退出是否短信通知(0:否 1:是)
     */
    @ApiModelProperty(value = "盟员退出是否短信通知(0:否 1:是)")
    @TableField("is_member_out_notify")
    private Integer isMemberOutNotify;

    /**
     * 短信通知手机号
     */
    @ApiModelProperty(value = "短信通知手机号")
    @TableField("notify_phone")
    private String notifyPhone;

    /**
     * 积分兑换率(百分比)
     */
    @ApiModelProperty(value = "积分兑换率(百分比)")
    @TableField("integral_exchange_ratio")
    private Double integralExchangeRatio;

    /**
     * 统一折扣(折)
     */
    @ApiModelProperty(value = "统一折扣(折)")
    @TableField("discount")
    private Double discount;


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

    public Integer getUnionId() {
        return unionId;
    }

    public void setUnionId(Integer unionId) {
        this.unionId = unionId;
    }

    public Integer getBusId() {
        return busId;
    }

    public void setBusId(Integer busId) {
        this.busId = busId;
    }

    public Integer getIsUnionOwner() {
        return isUnionOwner;
    }

    public void setIsUnionOwner(Integer isUnionOwner) {
        this.isUnionOwner = isUnionOwner;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getEnterpriseAddress() {
        return enterpriseAddress;
    }

    public void setEnterpriseAddress(String enterpriseAddress) {
        this.enterpriseAddress = enterpriseAddress;
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

    public String getAddressLongitude() {
        return addressLongitude;
    }

    public void setAddressLongitude(String addressLongitude) {
        this.addressLongitude = addressLongitude;
    }

    public String getAddressLatitude() {
        return addressLatitude;
    }

    public void setAddressLatitude(String addressLatitude) {
        this.addressLatitude = addressLatitude;
    }

    public Integer getIsMemberOutNotify() {
        return isMemberOutNotify;
    }

    public void setIsMemberOutNotify(Integer isMemberOutNotify) {
        this.isMemberOutNotify = isMemberOutNotify;
    }

    public String getNotifyPhone() {
        return notifyPhone;
    }

    public void setNotifyPhone(String notifyPhone) {
        this.notifyPhone = notifyPhone;
    }

    public Double getIntegralExchangeRatio() {
        return integralExchangeRatio;
    }

    public void setIntegralExchangeRatio(Double integralExchangeRatio) {
        this.integralExchangeRatio = integralExchangeRatio;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}