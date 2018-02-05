package com.gt.union.h5.card.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author hongjiye
 * @time 2017-12-20 17:34
 **/
@ApiModel(value = "我的联盟卡详情")
public class MyCardDetailVO {

	@ApiModelProperty(value = "用户昵称")
	private String nickName;

	@ApiModelProperty(value = "用户头像")
	private String heardImg;

	@ApiModelProperty(value = "联盟卡号")
	private String cardNo;

	@ApiModelProperty(value = "联盟卡二维码")
	private String cardImg;

	@ApiModelProperty(value = "联盟积分")
	private Double integral;

	@ApiModelProperty(value = "消费记录数")
	private Integer consumeCount;

	@ApiModelProperty(value = "联盟卡列表详情")
	private List<MyUnionCardDetailVO> cardList;

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getHeardImg() {
		return heardImg;
	}

	public void setHeardImg(String heardImg) {
		this.heardImg = heardImg;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getCardImg() {
		return cardImg;
	}

	public void setCardImg(String cardImg) {
		this.cardImg = cardImg;
	}

	public Double getIntegral() {
		return integral;
	}

	public void setIntegral(Double integral) {
		this.integral = integral;
	}

	public Integer getConsumeCount() {
		return consumeCount;
	}

	public void setConsumeCount(Integer consumeCount) {
		this.consumeCount = consumeCount;
	}

	public List<MyUnionCardDetailVO> getCardList() {
		return cardList;
	}

	public void setCardList(List<MyUnionCardDetailVO> cardList) {
		this.cardList = cardList;
	}

    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
    }
}
