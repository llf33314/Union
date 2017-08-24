package com.gt.union.entity.basic.vo;

import com.gt.union.common.annotation.valid.StringLengthValid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Created by Administrator on 2017/8/18 0018.
 */
@ApiModel( value = "UnionApplyInfoVO", description = "编辑盟员信息实体" )
@Data
public class UnionApplyInfoVO {

	/**
	 * 信息id
	 */
	private Integer id;

	/**
	 * 联盟id
	 */
	@ApiModelProperty( value = "联盟id", required = true)
	@NotNull(message = "联盟不存在")
	private Integer unionId;

	/**
	 * 商家id
	 */
	private Integer busId;

	/**
	 * 企业名称
	 */
	@ApiModelProperty( value = "企业名称", required = true)
	@NotBlank(message = "企业名称内容不能为空")
	@StringLengthValid(length = 10, message = "企业名称不可超过10个字")
	private String enterpriseName;

	/**
	 * 负责人
	 */
	@ApiModelProperty( value = "负责人")
	@StringLengthValid(length = 5, message = "负责人不可超过5个字")
	private String directorName;

	/**
	 * 联系电话
	 */
	@ApiModelProperty( value = "联系电话", required = true)
	@NotBlank(message = "联系电话内容不能为空")
	@Pattern(regexp = "^1[3|4|5|6|7|8][0-9][0-9]{8}$", message = "联系电话有误")
	private String directorPhone;

	/**
	 * 邮箱
	 */
	@ApiModelProperty( value = "邮箱")
	@Email(message = "邮箱有误", regexp = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")
	private String directorEmail;

	/**
	 * 短信通知手机号
	 */
	@ApiModelProperty( value = "手机短信通知", required = true)
	@NotBlank(message = "短信通知手机号不能为空")
	@Pattern(regexp = "^1[3|4|5|6|7|8][0-9][0-9]{8}$", message = "短信通知手机号有误")
	private String notifyPhone;


	/**
	 * 积分抵扣率
	 */
	@ApiModelProperty( value = "手机短信通知", required = true)
	private Double integralProportion;


	/**
	 * 我的地址
	 */
	@ApiModelProperty( value = "手机短信通知", required = true)
	@NotBlank(message = "短信通知手机号不能为空")
	private String busAddress;

	/**
	 * 地址经度
	 */
	@NotBlank(message = "短信通知手机号不能为空")
	private String addressLongitude;

	/**
	 * 地址纬度
	 */
	@NotBlank(message = "短信通知手机号不能为空")
	private String addressLatitude;

	/**
	 * 省代码
	 */
	@NotBlank(message = "请选择省")
	private String provienceCode;

	/**
	 * 市代码
	 */
	@NotBlank(message = "请选择市")
	private String cityCode;

	/**
	 * 区代码
	 */
	@NotBlank(message = "请选择区")
	private String districtCode;

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

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
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

	public String getNotifyPhone() {
		return notifyPhone;
	}

	public void setNotifyPhone(String notifyPhone) {
		this.notifyPhone = notifyPhone;
	}

	public Double getIntegralProportion() {
		return integralProportion;
	}

	public void setIntegralProportion(Double integralProportion) {
		this.integralProportion = integralProportion;
	}

	public String getBusAddress() {
		return busAddress;
	}

	public void setBusAddress(String busAddress) {
		this.busAddress = busAddress;
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

	public String getProvienceCode() {
		return provienceCode;
	}

	public void setProvienceCode(String provienceCode) {
		this.provienceCode = provienceCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}
}
