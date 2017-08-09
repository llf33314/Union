package com.gt.union.entity.basic;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * <p>
 * 联盟成员申请信息
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@TableName("t_union_apply_info")
public class UnionApplyInfo extends Model<UnionApplyInfo> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 申请id
     */
	@TableField("union_apply_id")
	private Integer unionApplyId;
    /**
     * 申请理由
     */
	@TableField("apply_reason")
	private String applyReason;
    /**
     * 企业名称
     */
	@TableField("enterprise_name")
	private String enterpriseName;
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
     * 商家地址
     */
	@TableField("bus_address")
	private String busAddress;
    /**
     * 商机推荐短信通知
     */
	@TableField("notify_phone")
	private String notifyPhone;
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
     * 地址省份id
     */
	@TableField("address_provience_id")
	private Integer addressProvienceId;
    /**
     * 地址城市id
     */
	@TableField("address_city_id")
	private Integer addressCityId;
    /**
     * 地址区id
     */
	@TableField("address_district_id")
	private Integer addressDistrictId;
    /**
     * 积分兑换率
     */
	@TableField("integral_proportion")
	private Double integralProportion;
    /**
     * 盟员退出是否短信通知开关设置（0：关闭 1：开启）
     */
	@TableField("is_member_out_advice")
	private Integer isMemberOutAdvice;
    /**
     * 售卡分成比例
     */
	@TableField("sell_divide_proportion")
	private Double sellDivideProportion;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUnionApplyId() {
		return unionApplyId;
	}

	public void setUnionApplyId(Integer unionApplyId) {
		this.unionApplyId = unionApplyId;
	}

	public String getApplyReason() {
		return applyReason;
	}

	public void setApplyReason(String applyReason) {
		this.applyReason = applyReason;
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

	public String getBusAddress() {
		return busAddress;
	}

	public void setBusAddress(String busAddress) {
		this.busAddress = busAddress;
	}

	public String getNotifyPhone() {
		return notifyPhone;
	}

	public void setNotifyPhone(String notifyPhone) {
		this.notifyPhone = notifyPhone;
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

	public Integer getAddressProvienceId() {
		return addressProvienceId;
	}

	public void setAddressProvienceId(Integer addressProvienceId) {
		this.addressProvienceId = addressProvienceId;
	}

	public Integer getAddressCityId() {
		return addressCityId;
	}

	public void setAddressCityId(Integer addressCityId) {
		this.addressCityId = addressCityId;
	}

	public Integer getAddressDistrictId() {
		return addressDistrictId;
	}

	public void setAddressDistrictId(Integer addressDistrictId) {
		this.addressDistrictId = addressDistrictId;
	}

	public Double getIntegralProportion() {
		return integralProportion;
	}

	public void setIntegralProportion(Double integralProportion) {
		this.integralProportion = integralProportion;
	}

	public Integer getIsMemberOutAdvice() {
		return isMemberOutAdvice;
	}

	public void setIsMemberOutAdvice(Integer isMemberOutAdvice) {
		this.isMemberOutAdvice = isMemberOutAdvice;
	}

	public Double getSellDivideProportion() {
		return sellDivideProportion;
	}

	public void setSellDivideProportion(Double sellDivideProportion) {
		this.sellDivideProportion = sellDivideProportion;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
