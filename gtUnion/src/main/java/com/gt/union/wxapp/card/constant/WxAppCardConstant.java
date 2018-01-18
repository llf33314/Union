package com.gt.union.wxapp.card.constant;

/**
 * @author hongjiye
 * @time 2017-12-29 16:42
 **/
public interface WxAppCardConstant {

	String UNION_TOKEN_KEY = "GTUNION";

	String REDIS_MEMBER_LINK = "_LINK";

	/**
	 * 购买了折扣卡
	 */
	int CARD_DISCOUNT_APPLY = 1;

	/**
	 * 购买活动卡
	 */
	int CARD_ACTIVITY_APPLY = 2;

	/**
	 * 已售罄
	 */
	int CARD_SELL_OUT = 3;

}
