package com.gt.union.card.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import java.util.Date;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 联盟绑定表
 * </p>
 *
 * @author linweicong
 * @since 2017-09-11
 */
@TableName("t_union_card_binding")
public class UnionCardBinding extends Model<UnionCardBinding> {

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
     * 第三方用户id
     */
	@TableField("third_member_id")
	private Integer thirdMemberId;


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

	public Integer getThirdMemberId() {
		return thirdMemberId;
	}

	public void setThirdMemberId(Integer thirdMemberId) {
		this.thirdMemberId = thirdMemberId;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
