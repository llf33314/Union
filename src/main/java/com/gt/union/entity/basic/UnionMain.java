package com.gt.union.entity.basic;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 联盟主表
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@TableName("t_union_main")
public class UnionMain extends Model<UnionMain> {

    private static final long serialVersionUID = 1L;

    /**
     * 主表
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
     * 联盟名称
     */
    @NotNull(message = "联盟名称内容不能为空")
	@TableField("union_name")
	private String unionName;
    /**
     * 盟主id
     */
	@TableField("bus_id")
	private Integer busId;
    /**
     * 联盟图标
     */
    @NotNull(message = "请设置联盟图标")
	@TableField("union_img")
	private String unionImg;
    /**
     * 加盟方式（1：推荐 2：申请、推荐）
     */
    @NotNull( message = "请选择加入方式" )
	@TableField("join_type")
	private Integer joinType;
    /**
     * 负责人电话
     */
	@TableField("director_phone")
	private String directorPhone;
    /**
     * 联盟说明
     */
	@NotNull(message = "联盟说明内容不能为空")
	@TableField("union_illustration")
	private String unionIllustration;
    /**
     * 联盟群二维码
     */
	@TableField("union_wx_group_img")
	private String unionWxGroupImg;
    /**
     * 联盟标识
     */
	@TableField("union_sign")
	private String unionSign;
    /**
     * 联盟成员总数
     */
	@TableField("union_total_member")
	private Integer unionTotalMember;
    /**
     * 现有联盟成员数
     */
	@TableField("union_member_num")
	private Integer unionMemberNum;
    /**
     * 联盟等级
     */
	@TableField("union_level")
	private Integer unionLevel;
    /**
     * 创建联盟审核状态（0：未审核 1：审核通过 2：审核不通过 ）
     */
	@TableField("union_verify_status")
	private Integer unionVerifyStatus;
    /**
     * 联盟是否开启积分（0：否 1：是）
     */
    @NotNull(message = "请选择是否开启积分")
	@TableField("is_integral")
	private Integer isIntegral;
    /**
     * 旧联盟会员是否要收费（0：否 1：是）
     */
    @NotNull(message = "请选择老会员升级是否收费")
	@TableField("old_member_charge")
	private Integer oldMemberCharge;
    /**
     * 黑卡是否收费（0：否 1：是）
     */
	@NotNull(message = "请选择黑卡是否收费")
	@TableField("black_card_charge")
	private Integer blackCardCharge;
    /**
     * 黑卡收费价格
     */
	@TableField("black_card_price")
	private Double blackCardPrice;
    /**
     * 黑卡有效期限
     */
	@TableField("black_card_term")
	private Integer blackCardTerm;
    /**
     * 是否开启红卡（0：否 1：是）
     */
	@NotNull(message = "请选择红卡是否收费")
	@TableField("red_card_opend")
	private Integer redCardOpend;
    /**
     * 红卡价格
     */
	@TableField("red_card_price")
	private Double redCardPrice;
    /**
     * 红卡有效期限
     */
	@TableField("red_card_term")
	private Integer redCardTerm;
    /**
     * 黑卡说明
     */
	@TableField("black_card_illustration")
	private String blackCardIllustration;
    /**
     * 红卡说明
     */
	@TableField("red_card_illustration")
	private String redCardIllustration;
    /**
     * 联盟有效期
     */
	@TableField("union_validity")
	private Date unionValidity;

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

	public String getUnionName() {
		return unionName;
	}

	public void setUnionName(String unionName) {
		this.unionName = unionName;
	}

	public Integer getBusId() {
		return busId;
	}

	public void setBusId(Integer busId) {
		this.busId = busId;
	}

	public String getUnionImg() {
		return unionImg;
	}

	public void setUnionImg(String unionImg) {
		this.unionImg = unionImg;
	}

	public Integer getJoinType() {
		return joinType;
	}

	public void setJoinType(Integer joinType) {
		this.joinType = joinType;
	}

	public String getDirectorPhone() {
		return directorPhone;
	}

	public void setDirectorPhone(String directorPhone) {
		this.directorPhone = directorPhone;
	}

	public String getUnionIllustration() {
		return unionIllustration;
	}

	public void setUnionIllustration(String unionIllustration) {
		this.unionIllustration = unionIllustration;
	}

	public String getUnionWxGroupImg() {
		return unionWxGroupImg;
	}

	public void setUnionWxGroupImg(String unionWxGroupImg) {
		this.unionWxGroupImg = unionWxGroupImg;
	}

	public String getUnionSign() {
		return unionSign;
	}

	public void setUnionSign(String unionSign) {
		this.unionSign = unionSign;
	}

	public Integer getUnionTotalMember() {
		return unionTotalMember;
	}

	public void setUnionTotalMember(Integer unionTotalMember) {
		this.unionTotalMember = unionTotalMember;
	}

	public Integer getUnionMemberNum() {
		return unionMemberNum;
	}

	public void setUnionMemberNum(Integer unionMemberNum) {
		this.unionMemberNum = unionMemberNum;
	}

	public Integer getUnionLevel() {
		return unionLevel;
	}

	public void setUnionLevel(Integer unionLevel) {
		this.unionLevel = unionLevel;
	}

	public Integer getUnionVerifyStatus() {
		return unionVerifyStatus;
	}

	public void setUnionVerifyStatus(Integer unionVerifyStatus) {
		this.unionVerifyStatus = unionVerifyStatus;
	}

	public Integer getIsIntegral() {
		return isIntegral;
	}

	public void setIsIntegral(Integer isIntegral) {
		this.isIntegral = isIntegral;
	}

	public Integer getOldMemberCharge() {
		return oldMemberCharge;
	}

	public void setOldMemberCharge(Integer oldMemberCharge) {
		this.oldMemberCharge = oldMemberCharge;
	}

	public Integer getBlackCardCharge() {
		return blackCardCharge;
	}

	public void setBlackCardCharge(Integer blackCardCharge) {
		this.blackCardCharge = blackCardCharge;
	}

	public Double getBlackCardPrice() {
		return blackCardPrice;
	}

	public void setBlackCardPrice(Double blackCardPrice) {
		this.blackCardPrice = blackCardPrice;
	}

	public Integer getBlackCardTerm() {
		return blackCardTerm;
	}

	public void setBlackCardTerm(Integer blackCardTerm) {
		this.blackCardTerm = blackCardTerm;
	}

	public Integer getRedCardOpend() {
		return redCardOpend;
	}

	public void setRedCardOpend(Integer redCardOpend) {
		this.redCardOpend = redCardOpend;
	}

	public Double getRedCardPrice() {
		return redCardPrice;
	}

	public void setRedCardPrice(Double redCardPrice) {
		this.redCardPrice = redCardPrice;
	}

	public Integer getRedCardTerm() {
		return redCardTerm;
	}

	public void setRedCardTerm(Integer redCardTerm) {
		this.redCardTerm = redCardTerm;
	}

	public String getBlackCardIllustration() {
		return blackCardIllustration;
	}

	public void setBlackCardIllustration(String blackCardIllustration) {
		this.blackCardIllustration = blackCardIllustration;
	}

	public String getRedCardIllustration() {
		return redCardIllustration;
	}

	public void setRedCardIllustration(String redCardIllustration) {
		this.redCardIllustration = redCardIllustration;
	}

	public Date getUnionValidity() {
		return unionValidity;
	}

	public void setUnionValidity(Date unionValidity) {
		this.unionValidity = unionValidity;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

}
