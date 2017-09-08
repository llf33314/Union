package com.gt.union.log.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 请求日志表
 * </p>
 *
 * @author linweicong
 * @since 2017-09-08
 */
@TableName("t_union_log_request")
public class UnionLogRequest extends Model<UnionLogRequest> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 创建时间
     */
	private Date createtime;
    /**
     * 用户id
     */
	@TableField("bus_id")
	private Integer busId;
    /**
     * 请求地址
     */
	private String url;
    /**
     * 请求参数
     */
	private byte[] param;
    /**
     * 请求方式
     */
	private String method;
    /**
     * 请求描述
     */
	private String desc;


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

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public byte[] getParam() {
		return param;
	}

	public void setParam(byte[] param) {
		this.param = param;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
