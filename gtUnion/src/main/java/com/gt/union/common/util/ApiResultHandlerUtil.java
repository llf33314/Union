package com.gt.union.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.bean.session.Member;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.Map;

/**
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0025.
 */
public class ApiResultHandlerUtil {

	/**
	 * 获取单个对象的数据
	 * @param data		返回结果数据
	 * @param clazz		需要转换的结果对象
	 * @return
	 */
	public static <T> T getDataObject(String data, Class<T> clazz){
		if(StringUtil.isEmpty(data)){
			return null;
		}
		Map map = JSONObject.parseObject(data,Map.class);
		if(CommonUtil.isNotEmpty(map.get("data"))){
			return JSONArray.parseObject(map.get("data").toString(),clazz);
		}else {
			return null;
		}
	}

	/**
	 * 获取列表对象的数据
	 * @param data		返回结果数据
	 * @param clazz		需要转换的结果对象
	 * @return
	 */
	public static <T> List<T> listDataObject(String data, Class<T> clazz){
		if(StringUtil.isEmpty(data)){
			return null;
		}
		Map map = JSONObject.parseObject(data,Map.class);
		if(CommonUtil.isNotEmpty(map.get("data"))){
			List<T> list = JSONArray.parseArray(map.get("data").toString(),clazz);
			return list;
		}else {
			return null;
		}
	}

}
