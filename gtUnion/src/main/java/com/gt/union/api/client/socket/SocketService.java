package com.gt.union.api.client.socket;

/**
 * socket服务api
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0021.
 */
public interface SocketService {

	/**
	 * socket推送信息
	 * @param pushName		推送的客户端key
	 * @param message		推送内容		JSON转换后的数据
	 * @param pushStyle		推送类型
	 * @return	0：失败 1：成功
	 */
	int socketSendMessage(String pushName,String message,String pushStyle);
}
