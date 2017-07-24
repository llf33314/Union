package com.gt.union.entity.business;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * <p>
 * 联盟商家推荐关联信息
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@TableName("t_union_business_recommend_info")
public class UnionBusinessRecommendInfo extends Model<UnionBusinessRecommendInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
	@TableId(value="id", type= IdType.AUTO)
	private Integer id;
    /**
     * 推荐id
     */
	@TableField("recommend_id")
	private Integer recommendId;
    /**
     * 用户姓名
     */
	@TableField("user_name")
	private String userName;
    /**
     * 用户电话
     */
	@TableField("user_phone")
	private String userPhone;
    /**
     * 业务备注
     */
	@TableField("business_msg")
	private String businessMsg;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRecommendId() {
		return recommendId;
	}

	public void setRecommendId(Integer recommendId) {
		this.recommendId = recommendId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getBusinessMsg() {
		return businessMsg;
	}

	public void setBusinessMsg(String businessMsg) {
		this.businessMsg = businessMsg;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
