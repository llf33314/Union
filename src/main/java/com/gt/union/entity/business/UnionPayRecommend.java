package com.gt.union.entity.business;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * <p>
 * 联盟推荐支付
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@TableName("t_union_pay_recommend")
public class UnionPayRecommend extends Model<UnionPayRecommend> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 支付记录id
     */
	@TableField("pay_record_id")
	private Integer payRecordId;
    /**
     * 商机推荐id
     */
	@TableField("recommend_id")
	private Integer recommendId;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPayRecordId() {
		return payRecordId;
	}

	public void setPayRecordId(Integer payRecordId) {
		this.payRecordId = payRecordId;
	}

	public Integer getRecommendId() {
		return recommendId;
	}

	public void setRecommendId(Integer recommendId) {
		this.recommendId = recommendId;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
