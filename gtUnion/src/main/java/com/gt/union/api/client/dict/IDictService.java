package com.gt.union.api.client.dict;

import java.util.List;
import java.util.Map;

/**
 * 字典服务api
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0022.
 */
public interface IDictService {

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

	/**
	 * 获取创建联盟套餐
	 * @return
	 */
	public List<Map> getUnionCreatePackage();

	/**
	 * 根据商家等级获取商家等级名称
	 * @param level
	 * @return
	 */
	public String getBusUserLevel(Integer level);


}
