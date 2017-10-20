package com.gt.union.main.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 联盟升级收费
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@TableName("t_union_main_charge")
public class UnionMainCharge extends Model<UnionMainCharge> {

    private static final long serialVersionUID = 1L;

    /**
     * 主表
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
     * 类型（1：黑卡 2：红卡）
     */
	private Integer type;
    /**
     * 是否启用（0：否 1：是）
     */
	@TableField("is_available")
	private Integer isAvailable;
    /**
     * 旧联盟会员是否要收费（0：否 1：是）
     */
	@TableField("is_old_charge")
	private Integer isOldCharge;
    /**
     * 是否收费（0：否 1：是）
     */
	@TableField("is_charge")
	private Integer isCharge;
    /**
     * 收费价格
     */
	@TableField("charge_price")
	private Double chargePrice;
    /**
     * 有效期限
     */
	@TableField("validity_day")
	private Integer validityDay;
    /**
     * 说明
     */
	private String illustration;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Integer getDelStatus() {
		return delStatus;
	}

	public void setDelStatus(Integer delStatus) {
		this.delStatus = delStatus;
	}

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Integer isAvailable) {
		this.isAvailable = isAvailable;
	}

	public Integer getIsOldCharge() {
		return isOldCharge;
	}

	public void setIsOldCharge(Integer isOldCharge) {
		this.isOldCharge = isOldCharge;
	}

	public Integer getIsCharge() {
		return isCharge;
	}

	public void setIsCharge(Integer isCharge) {
		this.isCharge = isCharge;
	}

	public Double getChargePrice() {
		return chargePrice;
	}

	public void setChargePrice(Double chargePrice) {
		this.chargePrice = chargePrice;
	}

	public Integer getValidityDay() {
		return validityDay;
	}

	public void setValidityDay(Integer validityDay) {
		this.validityDay = validityDay;
	}

	public String getIllustration() {
		return illustration;
	}

	public void setIllustration(String illustration) {
		this.illustration = illustration;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
