package com.gt.union.common.util;

/**
 * @author hongjiye
 * @time 2017-12-29 9:58
 **/
public class RedissonKeyUtil {

	/**
	 * 获取联盟卡粉丝手机号的分布式锁建
	 * @param phone
	 * @return
	 */
	public static final String getUnionCardFanByPhoneKey(String phone) {
		return PropertiesUtil.redisNamePrefix() + "fan:" + phone;
	}

	/**
	 * 获取提现商家分布式锁建
	 * @param busId
	 * @return
	 */
	public static final String getWithdrawalByBusIdKey(Integer busId) {
		return PropertiesUtil.redisNamePrefix() + "withdrawal:" + busId;
	}

	/**
	 * 获取粉丝折扣卡锁键
	 * @param fanId	粉丝联盟卡id
	 * @return
	 */
	public static final String getUnionCardByFanId(Integer fanId){
		return PropertiesUtil.redisNamePrefix() + "fanId:" + fanId;
	}

}
