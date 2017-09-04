package com.gt.union.entity.common;

import java.io.Serializable;
import java.util.Date;

public class BusUser implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column bus_user.id
     *
     * @mbggenerated Tue Jun 30 11:03:44 CST 2015
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column bus_user.name
     *
     * @mbggenerated Tue Jun 30 11:03:44 CST 2015
     */
    private String name;

    /**
     * 会员级别
     */
    private Integer level;

    /**
     * 会员开始时间
     */
    private Date startTime;
    
    
    /**
     * 会员结束时间
     */
    private Date endTime;

	/**
	 * 父id
	 */
	private Integer pid;

	/**
	 * 用户状态  不等于0表示冻结了
	 */
	private Integer status;

	/**
	 * 是否绑定公众号
	 */
	private Boolean is_binding;



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Boolean getIs_binding() {
		return is_binding;
	}

	public void setIs_binding(Boolean is_binding) {
		this.is_binding = is_binding;
	}
}