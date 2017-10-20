package com.gt.union.consume.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 消费服务
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@TableName("t_union_consume_item")
public class UnionConsumeItem extends Model<UnionConsumeItem> {

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
     * 创建时间
     */
	private Date createtime;
    /**
     * 消费id
     */
	@TableField("consume_id")
	private Integer consumeId;
    /**
     * 优惠服务项id
     */
	@TableField("preferential_item_id")
	private Integer preferentialItemId;


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

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Integer getConsumeId() {
		return consumeId;
	}

	public void setConsumeId(Integer consumeId) {
		this.consumeId = consumeId;
	}

	public Integer getPreferentialItemId() {
		return preferentialItemId;
	}

	public void setPreferentialItemId(Integer preferentialItemId) {
		this.preferentialItemId = preferentialItemId;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
