package com.gt.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * IP操作
 */
public class IPKit {
	public static String getRemoteIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}
		return request.getHeader("x-forwarded-for");
	}
	/**
	 * 获取IP 包括手机端获取IP效果   用在NIGIX
	 * @param request
	 * @return
	 */
	public static String getIp(HttpServletRequest request){
		String ip = request.getHeader("X-Real-IP");
	    return ip;
	}
	public static String IpByAddress(String ip) {
		Document doc = null;
		String regex = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
		if (!ip.matches(regex)) {
			return "无效的IP";
		}
		try {
			doc = Jsoup.connect("http://wap.ip138.com/ip_search138.asp?ip=" + ip)
					.get();
		} catch (IOException e) {
			return e.getMessage();
		}
		if (doc != null) {
			Elements eles = doc.select("b");
			if (!eles.isEmpty()) {
				String address = eles.html();
				if (!address.isEmpty()) {
					return address.substring(address.indexOf("：") + 1);
				} else {
					return "查询错误";
				}
			} else {
				return "无效的域名";
			}
		}
		return "连接超时,请手动刷新";
	}
	
	
	 public static String getIpAddr(HttpServletRequest request) {
		    String ip = request.getHeader("x-forwarded-for");
		    if(ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
		        ip = request.getHeader("Proxy-Client-IP");
		    }
		    if(ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
		        ip = request.getHeader("WL-Proxy-Client-IP");
		    }
		    if(ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
		        ip = request.getRemoteAddr();
		    }
		    return ip;
		 }
	 
	 
	 /**
	 * 根据IP获取对应的城市 调用QQ的接口
	 * 
	 * @param strIP
	 * @return
	 */
	public static String getAddressByIP(String strIP) {
		try {
			// 116.25.161.212
			URL url = new URL("http://ip.qq.com/cgi-bin/searchip?searchip1="
					+ strIP);
			URLConnection conn = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "GBK"));
			String line = null;
			StringBuffer result = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			reader.close();
			strIP = result.substring(result.indexOf("该IP所在地为："));
			strIP = strIP.substring(strIP.indexOf("：") + 1);
			String province = strIP.split("&")[0].replace("<span>","");
			if (province.indexOf("目前暂时没有") != -1) {
				province = "未知地区";
				return province;
			} else if (province.indexOf("内网") != -1) {
				province = "未知地区";
				return province;
			}else{
				return province;
			}
		} catch (IOException e) {
			return "未知地区";
		}
	}
}
