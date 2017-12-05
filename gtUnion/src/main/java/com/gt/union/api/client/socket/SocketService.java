package com.gt.union.api.client.socket;

/**
 * socket服务api
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0021.
 */
public interface SocketService {

	/**
	 * socket通用推送信息
	 * @param socketKey		推送的客户端socketKey
	 * @param message		推送内容		JSON转换后的数据
	 * @param pushStyle		推送属性
	 * @return	false：失败 true：成功
	 */
	boolean socketCommonSendMessage(String socketKey,String message,String pushStyle);

	/**
	 * socket支付推送推送消息
	 * @param socketKey		推送的客户端socketKey
	 * @param status		支付结果状态
	 * @param pushStyle		推送属性
	 * @return	false：失败 true：成功
	 */
	boolean socketPaySendMessage(String socketKey, Integer status,String pushStyle);



}
