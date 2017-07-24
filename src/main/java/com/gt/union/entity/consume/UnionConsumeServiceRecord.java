package com.gt.union.entity.consume;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * <p>
 * 核销服务记录
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@TableName("t_union_consume_service_record")
public class UnionConsumeServiceRecord extends Model<UnionConsumeServiceRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 消费记录id
     */
	@TableField("recore_id")
	private Integer recoreId;
    /**
     * 服务id
     */
	@TableField("service_id")
	private Integer serviceId;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRecoreId() {
		return recoreId;
	}

	public void setRecoreId(Integer recoreId) {
		this.recoreId = recoreId;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
