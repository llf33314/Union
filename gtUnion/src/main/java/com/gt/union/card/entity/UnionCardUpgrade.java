package com.gt.union.card.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 联盟卡升级
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@TableName("t_union_card_upgrade")
public class UnionCardUpgrade extends Model<UnionCardUpgrade> {

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
     * 联盟卡id
     */
	@TableField("card_id")
	private Integer cardId;
    /**
     * 盟员id
     */
	@TableField("member_id")
	private Integer memberId;
    /**
     * 联盟卡有效期
     */
	private Date validity;
    /**
     * 联盟卡类型（1：黑卡 2：红卡）
     */
	private Integer type;
    /**
     * 升级支付id
     */
	@TableField("upgrade_pay_id")
	private Integer upgradePayId;


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

	public Integer getCardId() {
		return cardId;
	}

	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Date getValidity() {
		return validity;
	}

	public void setValidity(Date validity) {
		this.validity = validity;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getUpgradePayId() {
		return upgradePayId;
	}

	public void setUpgradePayId(Integer upgradePayId) {
		this.upgradePayId = upgradePayId;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
