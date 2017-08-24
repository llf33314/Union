package com.gt.union.entity.basic.vo;

import com.gt.union.common.annotation.valid.StringLengthValid;
import com.gt.union.entity.basic.UnionInfoDict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * Created by Administrator on 2017/7/31 0031.
 */
@ApiModel( value = "UnionMainCreateInfoVO", description = "创建联盟信息保存实体" )
@Data
public class UnionMainCreateInfoVO {


	/**
	 * 联盟名称
	 */
	@ApiModelProperty( value = "联盟名称，不可超过5字" ,required = true )
	@NotNull(message = "联盟名称内容不能为空")
	@StringLengthValid(length = 5, message = "联盟名称内容不可超过5字")
	private String unionName;

	/**
	 * 联盟图标
	 */
	@NotBlank(message = "请设置联盟图标")
	@ApiModelProperty( value = "联盟图标，从素材库选择" ,required = true)
	private String unionImg;
	/**
	 * 加盟方式（1：推荐 2：申请、推荐）
	 */
	@ApiModelProperty( value = "加盟方式（1：推荐 2：申请、推荐）" ,required = true )
	@NotNull( message = "请选择加入方式" )
	private Integer joinType;

	/**
	 * 联盟说明
	 */
	@NotBlank(message = "联盟说明内容不能为空")
	@ApiModelProperty( value = "联盟说明内容，不可超过20字" ,required = true)
	@StringLengthValid(length = 20, message = "联盟说明内容不可超过20字")
	private String unionIllustration;

	/**
	 * 联盟群二维码
	 */
	@ApiModelProperty( value = "联盟群二维码，从素材库选择" )
	private String unionWxGroupImg;

	/**
	 * 联盟是否开启积分（0：否 1：是）
	 */
	@NotNull(message = "请选择是否开启积分")
	@ApiModelProperty( value = "联盟是否开启积分（0：否 1：是）" ,required = true)
	private Integer isIntegral;

	/**
	 * 旧联盟会员是否要收费（0：否 1：是）
	 */
	@NotNull(message = "请选择老会员升级是否收费")
	@ApiModelProperty( value = "旧联盟会员是否要收费（0：否 1：是）" ,required = true)
	private Integer oldMemberCharge;
	/**
	 * 黑卡是否收费（0：否 1：是）
	 */
	@ApiModelProperty( value = "黑卡是否收费（0：否 1：是）" ,required = true)
	@NotNull(message = "请选择黑卡是否收费")
	private Integer blackCardCharge;
	/**
	 * 黑卡收费价格
	 */
	@ApiModelProperty( value = "黑卡收费价格，设置黑卡收费时必填")
	private Double blackCardPrice;
	/**
	 * 黑卡有效期限
	 */
	@ApiModelProperty( value = "黑卡有效期限，设置黑卡收费时必填")
	private Integer blackCardTerm;
	/**
	 * 是否开启红卡（0：否 1：是）
	 */
	@NotNull(message = "请选择红卡是否收费")
	@ApiModelProperty( value = "是否开启红卡（0：否 1：是）" ,required = true)
	private Integer redCardOpend;

	/**
	 * 红卡价格
	 */
	@ApiModelProperty( value = "红卡价格，设置红卡收费时必填")
	private Double redCardPrice;
	/**
	 * 红卡有效期限
	 */
	@ApiModelProperty( value = "红卡有效期限，设置红卡收费时必填")
	private Integer redCardTerm;
	/**
	 * 黑卡说明
	 */
	@ApiModelProperty( value = "黑卡说明，不可超过50字")
	@StringLengthValid(length = 50, message = "黑卡说明内容不可超过50字")
	private String blackCardIllustration;
	/**
	 * 红卡说明
	 */
	@ApiModelProperty( value = "红卡说明，不可超过50字")
	@StringLengthValid(length = 50, message = "红卡说明内容不可超过50字")
	private String redCardIllustration;

	/**
	 * 联盟申请/推荐收集信息列表
	 */
	@NotNull(message = "请设置收集信息")
	@ApiModelProperty( value = "联盟申请/推荐收集信息列表", required = true)
	private List<UnionInfoDict> infos;


	/**
	 * 盟员信息的数据
	 */

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
	private String directorPphone;

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
	@NotBlank(message = "短信通知手机号不能为空")
	private String provienceCode;

	/**
	 * 市代码
	 */
	@NotBlank(message = "短信通知手机号不能为空")
	private String cityCode;

	/**
	 * 区代码
	 */
	@NotBlank(message = "请选择区")
	private String districtCode;





	public String getUnionName() {
		return unionName;
	}

	public void setUnionName(String unionName) {
		this.unionName = unionName;
	}

	public String getUnionImg() {
		return unionImg;
	}

	public void setUnionImg(String unionImg) {
		this.unionImg = unionImg;
	}

	public Integer getJoinType() {
		return joinType;
	}

	public void setJoinType(Integer joinType) {
		this.joinType = joinType;
	}

	public String getUnionIllustration() {
		return unionIllustration;
	}

	public void setUnionIllustration(String unionIllustration) {
		this.unionIllustration = unionIllustration;
	}

	public String getUnionWxGroupImg() {
		return unionWxGroupImg;
	}

	public void setUnionWxGroupImg(String unionWxGroupImg) {
		this.unionWxGroupImg = unionWxGroupImg;
	}

	public Integer getIsIntegral() {
		return isIntegral;
	}

	public void setIsIntegral(Integer isIntegral) {
		this.isIntegral = isIntegral;
	}

	public Integer getOldMemberCharge() {
		return oldMemberCharge;
	}

	public void setOldMemberCharge(Integer oldMemberCharge) {
		this.oldMemberCharge = oldMemberCharge;
	}

	public Integer getBlackCardCharge() {
		return blackCardCharge;
	}

	public void setBlackCardCharge(Integer blackCardCharge) {
		this.blackCardCharge = blackCardCharge;
	}

	public Double getBlackCardPrice() {
		return blackCardPrice;
	}

	public void setBlackCardPrice(Double blackCardPrice) {
		this.blackCardPrice = blackCardPrice;
	}

	public Integer getBlackCardTerm() {
		return blackCardTerm;
	}

	public void setBlackCardTerm(Integer blackCardTerm) {
		this.blackCardTerm = blackCardTerm;
	}

	public Integer getRedCardOpend() {
		return redCardOpend;
	}

	public void setRedCardOpend(Integer redCardOpend) {
		this.redCardOpend = redCardOpend;
	}

	public Double getRedCardPrice() {
		return redCardPrice;
	}

	public void setRedCardPrice(Double redCardPrice) {
		this.redCardPrice = redCardPrice;
	}

	public Integer getRedCardTerm() {
		return redCardTerm;
	}

	public void setRedCardTerm(Integer redCardTerm) {
		this.redCardTerm = redCardTerm;
	}

	public String getBlackCardIllustration() {
		return blackCardIllustration;
	}

	public void setBlackCardIllustration(String blackCardIllustration) {
		this.blackCardIllustration = blackCardIllustration;
	}

	public String getRedCardIllustration() {
		return redCardIllustration;
	}

	public void setRedCardIllustration(String redCardIllustration) {
		this.redCardIllustration = redCardIllustration;
	}

	public List<UnionInfoDict> getInfos() {
		return infos;
	}

	public void setInfos(List<UnionInfoDict> infos) {
		this.infos = infos;
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

	public String getDirectorPphone() {
		return directorPphone;
	}

	public void setDirectorPphone(String directorPphone) {
		this.directorPphone = directorPphone;
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
