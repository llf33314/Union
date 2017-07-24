package com.gt.union.entity.card;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 联盟卡信息详情
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@TableName("t_union_card_info_detail")
public class UnionCardInfoDetail extends Model<UnionCardInfoDetail> {

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
     * 联盟卡信息id
     */
	@TableField("card_info_id")
	private Integer cardInfoId;
    /**
     * 联盟卡类型（1：黑卡 2：红卡）
     */
	@TableField("card_type")
	private Integer cardType;
    /**
     * 支付金额
     */
	@TableField("pay_price")
	private Double payPrice;
    /**
     * 联盟卡有效期（天）
     */
	@TableField("card_time_term")
	private Integer cardTimeTerm;
    /**
     * 卡的版本
     */
	@TableField("card_version")
	private Integer cardVersion;


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

	public Integer getCardInfoId() {
		return cardInfoId;
	}

	public void setCardInfoId(Integer cardInfoId) {
		this.cardInfoId = cardInfoId;
	}

	public Integer getCardType() {
		return cardType;
	}

	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}

	public Double getPayPrice() {
		return payPrice;
	}

	public void setPayPrice(Double payPrice) {
		this.payPrice = payPrice;
	}

	public Integer getCardTimeTerm() {
		return cardTimeTerm;
	}

	public void setCardTimeTerm(Integer cardTimeTerm) {
		this.cardTimeTerm = cardTimeTerm;
	}

	public Integer getCardVersion() {
		return cardVersion;
	}

	public void setCardVersion(Integer cardVersion) {
		this.cardVersion = cardVersion;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
