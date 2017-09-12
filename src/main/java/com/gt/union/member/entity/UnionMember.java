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
 * 联盟成员
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@TableName("t_union_member")
public class UnionMember extends Model<UnionMember> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableId(value="id", type= IdType.AUTO)
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
     * 联盟id
     */
	@TableField("union_id")
	private Integer unionId;
    /**
     * 成员id
     */
	@TableField("bus_id")
	private Integer busId;
    /**
     * 是否盟主（0：否 1：是）
     */
	@TableField("is_union_owner")
	private Integer isUnionOwner;
    /**
     * 盟员状态(0：申请入盟；1：已入盟 2：申请退盟 3：退盟过渡期 )
     */
	private Integer status;
    /**
     * 企业名称
     */
	@TableField("enterprise_name")
	private String enterpriseName;
    /**
     * 企业地址
     */
	@TableField("enterprise_address")
	private String enterpriseAddress;
    /**
     * 负责人名称
     */
	@TableField("director_name")
	private String directorName;
    /**
     * 负责人电话
     */
	@TableField("director_phone")
	private String directorPhone;
    /**
     * 负责人邮箱
     */
	@TableField("director_email")
	private String directorEmail;
    /**
     * 地址经度
     */
	@TableField("address_longitude")
	private String addressLongitude;
    /**
     * 地址维度
     */
	@TableField("address_latitude")
	private String addressLatitude;
    /**
     * 地址省份code
     */
	@TableField("address_province_code")
	private String addressProvinceCode;
    /**
     * 地址城市code
     */
	@TableField("address_city_code")
	private String addressCityCode;
    /**
     * 地址区code
     */
	@TableField("address_district_code")
	private String addressDistrictCode;
    /**
     * 盟员退出是否短信通知开关设置（0：关闭 1：开启）
     */
	@TableField("is_member_out_notify")
	private Integer isMemberOutNotify;
    /**
     * 短信通知手机号
     */
	@TableField("notify_phone")
	private String notifyPhone;
    /**
     * 售卡分成比例
     */
	@TableField("card_divide_percent")
	private Double cardDividePercent;
    /**
     * 积分兑换率
     */
	@TableField("integral_exchange_percent")
	private Double integralExchangePercent;


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

	public Date getCreatetime() {
	    return createtime;
    }

    public void setCreatetime(Date createtime) {
	    this.createtime = createtime;
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

	public String getAddressProvinceCode() {
		return addressProvinceCode;
	}

	public void setAddressProvinceCode(String addressProvinceCode) {
		this.addressProvinceCode = addressProvinceCode;
	}

	public String getAddressCityCode() {
		return addressCityCode;
	}

	public void setAddressCityCode(String addressCityCode) {
		this.addressCityCode = addressCityCode;
	}

	public String getAddressDistrictCode() {
		return addressDistrictCode;
	}

	public void setAddressDistrictCode(String addressDistrictCode) {
		this.addressDistrictCode = addressDistrictCode;
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

	public Double getCardDividePercent() {
		return cardDividePercent;
	}

	public void setCardDividePercent(Double cardDividePercent) {
		this.cardDividePercent = cardDividePercent;
	}

	public Double getIntegralExchangePercent() {
		return integralExchangePercent;
	}

	public void setIntegralExchangePercent(Double integralExchangePercent) {
		this.integralExchangePercent = integralExchangePercent;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
