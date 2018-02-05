package com.gt.union.wxapp.card.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author hongjiye
 * @time 2017-12-20 16:28
 **/
@ApiModel(value = "h5联盟卡首页列表详情VO")
public class UnionCardVO {

	@ApiModelProperty(value = "联盟卡类型 1:折扣卡 2:活动卡")
	private Integer cardType;

	@ApiModelProperty(value = "联盟卡名称")
	private String cardName;

	@ApiModelProperty(value = "联盟活动卡颜色1")
	private String color1;

	@ApiModelProperty(value = "联盟活动卡颜色2")
	private String color2;

	@ApiModelProperty(value = "活动卡id")
	private Integer activityId;

	@ApiModelProperty(value = "联盟id")
	private Integer unionId;

	@ApiModelProperty(value = "盟员id")
	private Integer unionMemberId;

	@ApiModelProperty(value = "联盟卡价格")
	private Double cardPrice;

	@ApiModelProperty(value = "说明")
	private String illustration;

	@ApiModelProperty(value = "状态 1：已领取 2：已购买 3：已售罄")
	private Integer status;

	public Integer getCardType() {
		return cardType;
	}

	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getColor1() {
		return color1;
	}

	public void setColor1(String color1) {
		this.color1 = color1;
	}

	public String getColor2() {
		return color2;
	}

	public void setColor2(String color2) {
		this.color2 = color2;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	public Integer getUnionId() {
		return unionId;
	}

	public void setUnionId(Integer unionId) {
		this.unionId = unionId;
	}

	public Integer getUnionMemberId() {
		return unionMemberId;
	}

	public void setUnionMemberId(Integer unionMemberId) {
		this.unionMemberId = unionMemberId;
	}

	public Double getCardPrice() {
		return cardPrice;
	}

	public void setCardPrice(Double cardPrice) {
		this.cardPrice = cardPrice;
	}

	public String getIllustration() {
		return illustration;
	}

	public void setIllustration(String illustration) {
		this.illustration = illustration;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
