package com.gt.union.api.client.dict;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/22 0022.
 */
public interface DictService {

	/**
	 * 获取默认折扣
	 * @return
	 */
	public Double getDefaultDiscount();

	/**
	 * 获取最大可抵扣价格的百分比
	 * @return
	 */
	public Double getDefaultMaxExchangePercent();

	/**
	 * 获取100元可抵扣多少积分
	 * @return
	 */
	public Double getExchangeIntegral();

	/**
	 * 获取消费1元赠送多少积分
	 * @return
	 */
	public Double getGiveIntegral();

	/**
	 * 获取盟员信息需要收集的选项
	 * @return
	 */
	public List<Map> getUnionApplyInfoDict();

	/**
	 * 获取创建联盟权限属性
	 * @return
	 */
	public List<Map> getCreateUnionDict();


}
