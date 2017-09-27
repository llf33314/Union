package com.gt.union.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 非wxmp内容项目 token 校验
 * @author pengjiangli
 *
 */
public class TokenUitl {
	
	private static final Logger LOG=Logger.getLogger(TokenUitl.class);

	public static Map<String, Object> tokenMap;
	
	private static String userName="duofriend";
	private static String pwd="123456";
	
	
	public static String getToken(){
		try {
			Long tokenStart=Long.parseLong(CommonUtil.toString(tokenMap.get("tokenStart")));
			Long validTime=Long.parseLong(CommonUtil.toString(tokenMap.get("validTime")));
			Long currentDate=new Date().getTime();
			//token有效时间小于当前时间
			if(tokenStart+validTime>=currentDate){
				return CommonUtil.toString(tokenMap.get("token"));
			}
			
			String url=PropertiesUtil.getWxmpUrl()+"/tokenController/79B4DE7C/getToken.do";
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("userName", userName);
			map.put("pwd", pwd);
			JSONObject json=HttpClienUtil.httpPost(url,  JSONObject.parseObject(JSON.toJSONString(map)), false);
			if("1".equals(json.get("code"))){
				tokenMap=(Map<String, Object>) json.get("tokenMap");
				return CommonUtil.toString(tokenMap.get("token"));
			}
		} catch (Exception e) {
			LOG.error("校验token异常", e);
			return null;
		}
		return null;
	}
	
	
	/**
	 * 如果其他项目访问当前项目 请远程校验token
	 * @return
	 */
	public static boolean checkToken(){
		try {
			String url=PropertiesUtil.getWxmpUrl()+"/tokenController/79B4DE7C/checkedToken.do";
			JSONObject json=HttpClienUtil.httpPost(url, JSONObject.parseObject(JSON.toJSONString(tokenMap)), false);
			return json.getBoolean("code");
		} catch (Exception e) {
			LOG.error("校验token异常", e);
			return false;
		}
	}
}
