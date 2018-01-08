package com.gt.union.api.erp.jxc.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.gt.union.api.erp.jxc.service.JxcAuthorityService;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.StringUtil;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HttpClientUtil {
	private static PoolingHttpClientConnectionManager cm;
	private static String EMPTY_STR = "";
	private static String UTF_8 = "UTF-8";


	private static JxcAuthorityService jxcAuthorityService;

	@Autowired
	public HttpClientUtil(JxcAuthorityService jxcAuthorityService) {
		HttpClientUtil.jxcAuthorityService = jxcAuthorityService;
	}

	static {
		cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(50);
		cm.setDefaultMaxPerRoute(5);
	}

	private static CloseableHttpClient getHttpClient() {
		return HttpClients.custom().setConnectionManager(cm).build();
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public static String httpGetRequest(String url) {
		HttpGet httpGet = new HttpGet(url);
		RequestConfig config = RequestConfig.custom().setConnectTimeout(1000).setConnectionRequestTimeout(500).setSocketTimeout(10000).setStaleConnectionCheckEnabled(true).build();
		httpGet.setConfig(config);
		return getResult(httpGet);
	}

	public static String httpGetRequest(String url, Map<String, Object> params) throws URISyntaxException {
		URIBuilder ub = new URIBuilder(url);

		ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
		if(ListUtil.isNotEmpty(pairs)){
			ub.setParameters(pairs);
		}

		HttpGet httpGet = new HttpGet(ub.build());
		return getResult(httpGet);
	}

	public static String httpGetRequest(String url, Map<String, Object> params, String token)
			throws URISyntaxException {
		URIBuilder ub = new URIBuilder(url);
		if(StringUtil.isEmpty(token)){
			token = jxcAuthorityService.getJxcAuthority();
		}
		ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
		if(ListUtil.isNotEmpty(pairs)){
			ub.setParameters(pairs);
		}

		HttpGet httpGet = new HttpGet(ub.build());
		RequestConfig config = RequestConfig.custom().setConnectTimeout(1000).setConnectionRequestTimeout(500).setSocketTimeout(10000).setStaleConnectionCheckEnabled(true).build();
		httpGet.setConfig(config);
		httpGet.setHeader("token", token);
		return getResult(httpGet);
	}


	public static String httpPostRequest(String url) {
		HttpPost httpPost = new HttpPost(url);
		return getResult(httpPost);
	}

	public static String httpPostRequest(String url, Map<String, Object> params, String token)
			throws UnsupportedEncodingException {
		HttpPost httpPost = new HttpPost(url);
		if(StringUtil.isEmpty(token)){
			token = jxcAuthorityService.getJxcAuthority();
		}
		ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
		httpPost.addHeader("token", token);
		httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
		RequestConfig config = RequestConfig.custom().setConnectTimeout(1000).setConnectionRequestTimeout(500).setSocketTimeout(10000).setStaleConnectionCheckEnabled(true).build();
		httpPost.setConfig(config);
		return getResult(httpPost);
	}

	public static String httpPostRequest(String url, Map<String, Object> params) throws UnsupportedEncodingException {
		HttpPost httpPost = new HttpPost(url);
		ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
		httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
		return getResult(httpPost);
	}


	private static ArrayList<NameValuePair> covertParams2NVPS(Map<String, Object> params) {
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		if(CommonUtil.isNotEmpty(params)){
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if(CommonUtil.isNotEmpty(param.getValue())){
					pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
				}
			}
		}
		return pairs;
	}


	private static String getResult(HttpRequestBase request) {
		CloseableHttpClient httpClient = getHttpClient();
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String result = EntityUtils.toString(entity);
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try{
				if(response != null){
					response.close();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return EMPTY_STR;
	}

}
