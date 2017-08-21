package com.gt.union.service.common.impl;

import com.gt.union.service.common.DictService;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/8/18 0018.
 */
@Service
public class DictServiceImpl implements DictService {


	/**
	 * 联盟收集资料信息参数
	 */
	public static final String UNION_INFO_DICT = "1144";


	/**
	 * 创建联盟的等级相应成员数、金额、名称
	 */
	public static final String UNION_CREATE_INFO = "1145";


	/**
	 * 创建联盟权限属性
	 */
	private static String CREATE_UNION_TYPE = "1183";

	/**
	 * 联盟积分兑换规则 消费1元抵扣多少积分
	 */
	private static String GIVE_INTEGRAL_TYPE = "1184";


	/**
	 * 联盟积分抵扣规则 多少积分抵扣多少元
	 */
	private static String EXCHANGE_INTEGARL_TYPE = "1185";


	/**
	 * 联盟积分最多可抵扣价格的百分比
	 */
	private static String MAX_EXCHANGE_TYPE = "1186";

	/**
	 * 联盟默认折扣
	 */
	private static String DEFAULT_DISCOUNT_TYPE = "1187";
}
