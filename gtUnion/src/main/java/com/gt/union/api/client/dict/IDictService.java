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
	 * 获取最大可抵扣价格的百分比
	 * @return
	 */
	Double getMaxExchangePercent();

	/**
	 * 获取消耗多少积分可以抵扣1元
	 * @return
	 */
	Double getExchangeIntegral();

	/**
	 * 获取消费1元赠送多少积分
	 * @return
	 */
	Double getGiveIntegral();

	/**
	 * 获取盟员信息需要收集的选项
	 * @return
	 */
	List<Map> listMemberApplyInfoDict();

	/**
	 * 获取创建联盟权限属性
	 * @return
	 */
	List<Map> listCreateUnionDict();

	/**
	 * 获取erp属性值
	 * @return
	 */
	List<Map> listErpStyle();

	/**
	 * 根据商家等级获取商家等级名称
	 * @param level	商家等级
	 * @return
	 */
	String getBusUserLevel(Integer level);


}
