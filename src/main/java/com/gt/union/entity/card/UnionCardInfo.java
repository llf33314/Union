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
 * 联盟卡信息
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@TableName("t_union_card_info")
public class UnionCardInfo extends Model<UnionCardInfo> {

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
	 * 更新时间
	 */
	private Date updatetime;
	/**
	 * 删除状态（0：未删除 1：删除）
	 */
	@TableField("del_status")
	private Integer delStatus;
	/**
	 * 联盟卡id
	 */
	@TableField("union_card_id")
	private Integer unionCardId;
	/**
	 * 联盟id
	 */
	@TableField("union_id")
	private Integer unionId;
	/**
	 * 联盟卡有效期
	 */
	@TableField("card_term_time")
	private Date cardTermTime;
	/**
	 * 联盟卡类型（1：黑卡 2：红卡）
	 */
	@TableField("card_type")
	private Integer cardType;
	/**
	 * 卡的版本
	 */
	@TableField("card_version")
	private Integer cardVersion;
	/**
	 * 商家id
	 */
	@TableField("bus_id")
	private Integer busId;


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

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public Integer getDelStatus() {
		return delStatus;
	}

	public void setDelStatus(Integer delStatus) {
		this.delStatus = delStatus;
	}

	public Integer getUnionCardId() {
		return unionCardId;
	}

	public void setUnionCardId(Integer unionCardId) {
		this.unionCardId = unionCardId;
	}

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}

	public Date getCardTermTime() {
		return cardTermTime;
	}

	public void setCardTermTime(Date cardTermTime) {
		this.cardTermTime = cardTermTime;
	}

	public Integer getCardType() {
		return cardType;
	}

	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}

	public Integer getCardVersion() {
		return cardVersion;
	}

	public void setCardVersion(Integer cardVersion) {
		this.cardVersion = cardVersion;
	}

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
