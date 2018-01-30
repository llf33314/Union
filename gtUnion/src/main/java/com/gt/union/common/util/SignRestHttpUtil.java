package com.gt.union.common.util;

import com.alibaba.fastjson.JSONObject;
import com.gt.api.bean.sign.SignBean;
import com.gt.api.exception.SignException;
import com.gt.api.util.HttpClienUtils;
import com.gt.api.util.sign.SignUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * API REST请求工具
 * @author hongjiye
 * @time 2018-01-30 11:43
 **/
@Component
public class SignRestHttpUtil {

	private static Logger logger = LoggerFactory.getLogger(SignRestHttpUtil.class);

	private static RestTemplate restTemplate;

	@Autowired
	private RestTemplate initRestTemplate;

	@PostConstruct
	public void init() {
		restTemplate = this.initRestTemplate;
	}

	/**
	 *
	 * @param url		请求地址
	 * @param jsonObject	请求参数
	 * @param signKey	请求签名
	 * @return
	 * @throws SignException
	 */
	public static String wxmpPostByHttp(String url, String jsonObject, String signKey) throws SignException {
		if(StringUtil.isEmpty(signKey)) {
			throw new SignException("signKey could not be null");
		} else {
			SignBean signBean = SignUtils.sign(signKey, null);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
			httpHeaders.add("Accept", MediaType.APPLICATION_JSON_UTF8.toString());
			httpHeaders.add("sign", JSONObject.toJSONString(signBean));
			HttpEntity<String> formEntity = new HttpEntity(jsonObject, httpHeaders);
			String result = restTemplate.postForObject(url, formEntity, String.class);
			return result;
		}
	}

	/**
	 *
	 * @param url		请求地址
	 * @param jsonObject	请求参数
	 * @param signKey	请求签名
	 * @return
	 * @throws Exception
	 */
	public static String reqPostUTF8(String url, String jsonObject, String signKey) throws Exception{
		if(StringUtil.isEmpty(signKey)) {
			throw new SignException("signKey could not be null");
		} else {
			SignBean signBean = HttpClienUtils.sign(signKey, jsonObject);
			HttpHeaders headers = new HttpHeaders();
			headers.add("Accept", MediaType.APPLICATION_JSON_UTF8.toString());
			headers.add("sign", JSONObject.toJSONString(signBean));
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<String> httpEntity = new HttpEntity<String>(jsonObject, headers);
			String result = restTemplate.postForObject(url, httpEntity, String.class);
			return result;
		}
	}


	/**
	 *
	 * @param url		请求地址
	 * @param jsonObject	请求参数
	 * @param signKey	请求签名
	 * @return
	 * @throws Exception
	 */
	public static String reqGetUTF8(String url, String jsonObject, String signKey) throws Exception{
		if(StringUtil.isEmpty(signKey)) {
			throw new SignException("signKey could not be null");
		} else {
			SignBean signBean = HttpClienUtils.sign(signKey, jsonObject);
			HttpHeaders headers = new HttpHeaders();
			headers.add("Accept", MediaType.APPLICATION_JSON_UTF8.toString());
			headers.add("sign", JSONObject.toJSONString(signBean));
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<String> httpEntity = new HttpEntity<String>(jsonObject, headers);
			String result = restTemplate.getForObject(url, String.class, httpEntity);
			return result;
		}
	}

	public static String reqGetUTF8(String url, String jsonObject) throws Exception{
		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", MediaType.APPLICATION_JSON_UTF8.toString());
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> httpEntity = new HttpEntity<String>(jsonObject, headers);
		String result = restTemplate.getForObject(url, String.class, httpEntity);
		return result;
	}
}
