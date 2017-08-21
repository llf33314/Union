package com.gt.union.vo.basic;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.gt.union.common.annotation.valid.StringLengthValid;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.entity.basic.UnionInfoDict;
import com.gt.union.entity.basic.UnionMain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/8/14 0014.
 */
@ApiModel( value = "UnionMainInfoVO", description = "编辑联盟信息保存实体" )
@Data
public class UnionMainInfoVO{


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
}
