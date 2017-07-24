package com.gt.union.entity.consume;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 联盟卡核销优惠项目记录
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@TableName("t_union_card_consume_preferential_record")
public class UnionCardConsumePreferentialRecord extends Model<UnionCardConsumePreferentialRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 核销时间
     */
	private Date createtime;
    /**
     * 联盟卡id
     */
	@TableField("union_card_id")
	private Integer unionCardId;
    /**
     * 优惠项目id
     */
	@TableField("preferential_service_id")
	private Integer preferentialServiceId;


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

	public Integer getUnionCardId() {
		return unionCardId;
	}

	public void setUnionCardId(Integer unionCardId) {
		this.unionCardId = unionCardId;
	}

	public Integer getPreferentialServiceId() {
		return preferentialServiceId;
	}

	public void setPreferentialServiceId(Integer preferentialServiceId) {
		this.preferentialServiceId = preferentialServiceId;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
