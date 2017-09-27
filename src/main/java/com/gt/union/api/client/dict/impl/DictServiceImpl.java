package com.gt.union.api.client.dict.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/22 0022.
 */
@Service
public class DictServiceImpl implements IDictService {

	@Autowired
	private RedisCacheUtil redisCacheUtil;


	/**
	 * 联盟收集资料信息参数
	 */
	public static final String UNION_INFO_DICT = "1144";


	/**
	 * 创建联盟权限属性
	 */
	private static String CREATE_UNION_TYPE = "1183";

	/**
	 * 联盟积分兑换规则 消费1元赠送多少积分
	 */
	private static String GIVE_INTEGRAL_TYPE = "1184";


	/**
	 * 联盟积分抵扣规则 多少积分抵扣多少元
	 */
	private static String EXCHANGE_INTEGRAL_TYPE = "1185";


	/**
	 * 联盟积分最多可抵扣价格的百分比
	 */
	private static String MAX_EXCHANGE_TYPE = "1186";

	/**
	 * 联盟默认折扣
	 */
	private static String DEFAULT_DISCOUNT_TYPE = "1187";

	/**
	 * 创建联盟套餐
	 */
	private static String UNION_CREATE_PACKAGE_TYPE = "E001";

	@Value("${wxmp.url}")
	private String wxmpUrl;


	public Double getDefaultDiscount(){
		return getItemDoubleValue(DEFAULT_DISCOUNT_TYPE);
	}

	@Override
	public Double getDefaultMaxExchangePercent() {
		return getItemDoubleValue(MAX_EXCHANGE_TYPE);
	}

	@Override
	public Double getExchangeIntegral() {
		return getItemDoubleValue(EXCHANGE_INTEGRAL_TYPE);
	}

	@Override
	public Double getGiveIntegral() {
		return getItemDoubleValue(GIVE_INTEGRAL_TYPE);
	}

	@Override
	public List<Map> getUnionApplyInfoDict() {
		return getItemList(UNION_INFO_DICT);
	}

	@Override
	public List<Map> getCreateUnionDict() {
		return getItemList(CREATE_UNION_TYPE);
	}

	@Override
	public List<Map> getUnionCreatePackage() {
		return getItemList(UNION_CREATE_PACKAGE_TYPE);
	}
	/**
	 * 获取单个值
	 * @param itemKey
	 * @return
	 */
	private Double getItemDoubleValue(String itemKey){
		String key = RedisKeyUtil.getDictTypeKey(itemKey);
		if(redisCacheUtil.exists(key)){
			Object obj = redisCacheUtil.get(key);
			if(CommonUtil.isNotEmpty(obj)){
				return JSON.parseObject(obj.toString(),Double.class);
			}
		}
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("style",itemKey);
		Double exchange = null;
		String url = wxmpUrl + "/8A5DA52E/dictApi/getDictApi.do";
		try{
			String result = SignHttpUtils.WxmppostByHttp(url, param, ConfigConstant.WXMP_SIGN_KEY);
			if(StringUtil.isEmpty(result)){
				return null;
			}
			Map<String,Object> data= JSON.parseObject(result,Map.class);
			if(CommonUtil.isEmpty(data.get("data"))){
				return null;
			}
			Map item = JSON.parseObject(data.get("data").toString(),Map.class);
			List<Map> dict = JSONArray.parseArray(item.get("dictJSON").toString(),Map.class);
			if(ListUtil.isEmpty(dict)){
				return null;
			}
			exchange = CommonUtil.toDouble(dict.get(0).get("item_value"));
			if(CommonUtil.isNotEmpty(exchange)){
				redisCacheUtil.set(key,JSON.toJSONString(exchange));
			}
		}catch (Exception e){
			return null;
		}
		return exchange;
	}

	/**
	 * 获取列表
	 * @param itemKey
	 * @return
	 */
	private List<Map> getItemList(String itemKey){
		String key = RedisKeyUtil.getDictTypeKey(itemKey);
		if(redisCacheUtil.exists(key)){
			Object obj = redisCacheUtil.get(key);
			if(CommonUtil.isNotEmpty(obj)){
				return JSON.parseObject(obj.toString(),List.class);
			}
		}
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("style",itemKey);
		String url = wxmpUrl + "/8A5DA52E/dictApi/getDictApi.do";
		try{
			String result = SignHttpUtils.WxmppostByHttp(url, param, ConfigConstant.WXMP_SIGN_KEY);
			if(StringUtil.isEmpty(result)){
				return null;
			}
			Map<String,Object> data= JSON.parseObject(result,Map.class);
			if(CommonUtil.isEmpty(data.get("data"))){
				return null;
			}
			Map item = JSON.parseObject(data.get("data").toString(),Map.class);
			List list = JSONArray.parseArray(item.get("dictJSON").toString(),Map.class);
			if(ListUtil.isNotEmpty(list)){
				redisCacheUtil.set(key, JSON.toJSONString(list));
			}
			return list;
		}catch (Exception e){
			return null;
		}
	}

}
