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
 * 联盟会员商家绑定
 * </p>
 *
 * @author linweicong
 * @since 2017-08-23
 */
@TableName("t_union_member_card")
public class UnionMemberCard extends Model<UnionMemberCard> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 删除状态（0：未删除 1：删除）
     */
	@TableField("del_status")
	private Integer delStatus;
    /**
     * 商家id
     */
	@TableField("bus_id")
	private Integer busId;
    /**
     * 用户id
     */
	@TableField("member_id")
	private Integer memberId;
    /**
     * 联盟升级会员卡id
     */
	@TableField("union_member_card_id")
	private Integer unionMemberCardId;
    /**
     * 绑卡时间
     */
	@TableField("band_card_time")
	private Date bandCardTime;


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

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getUnionMemberCardId() {
		return unionMemberCardId;
	}

	public void setUnionMemberCardId(Integer unionMemberCardId) {
		this.unionMemberCardId = unionMemberCardId;
	}

	public Date getBandCardTime() {
		return bandCardTime;
	}

	public void setBandCardTime(Date bandCardTime) {
		this.bandCardTime = bandCardTime;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
