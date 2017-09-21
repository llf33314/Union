package com.gt.union.api.client.socket.impl;

import com.alibaba.fastjson.JSON;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.socket.SocketService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/21 0021.
 */
@Service
public class SocketServiceImpl implements SocketService{

	@Value("${wxmp.url}")
	private String wxmpUrl;

	@Value("${wxmp.signkey}")
	private String wxmpSignkey;

	@Override
	public int socketSendMessage(String pushName,String message,String pushStyle) {
		try {

			Map<String,Object> params = new HashMap<>();
			if(pushStyle == null){
				params.put("pushStyle","");
			}else {
				params.put("pushStyle",pushStyle);
			}
			params.put("pushMsg", message);
			params.put("pushName",pushName);
			String result = SignHttpUtils.WxmppostByHttp(wxmpUrl+"/8A5DA52E/socket/getSocketApi.do", params, wxmpSignkey);
		}catch (Exception e){
			return 0;
		}
		return 1;
	}
}

