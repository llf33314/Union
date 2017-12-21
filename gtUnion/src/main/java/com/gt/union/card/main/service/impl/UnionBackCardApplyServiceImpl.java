package com.gt.union.card.main.service.impl;

import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.pay.entity.PayParam;
import com.gt.union.card.main.service.IUnionCardApplyService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.union.main.vo.UnionPayVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hongjiye
 * @time 2017-12-21 10:15
 **/
@Service("unionBackCardApplyService")
public class UnionBackCardApplyServiceImpl implements IUnionCardApplyService{

	@Autowired
	private WxPayService wxPayService;

	@Override
	public UnionPayVO unionCardApply(String orderNo, Double payMoneySum, Integer busId) {
		UnionPayVO result = new UnionPayVO();
		String socketKey = PropertiesUtil.getSocketKey() + orderNo;
		String notifyUrl = PropertiesUtil.getUnionUrl() + "/callBack/79B4DE7C/card?socketKey=" + socketKey;

		PayParam payParam = new PayParam();
		payParam.setTotalFee(payMoneySum.doubleValue());
		payParam.setOrderNum(orderNo);
		payParam.setIsreturn(CommonConstant.COMMON_NO);
		payParam.setNotifyUrl(notifyUrl);
		payParam.setIsSendMessage(CommonConstant.COMMON_NO);
		payParam.setPayWay(0);
		payParam.setDesc("办理联盟卡");
		String payUrl = wxPayService.qrCodePay(payParam);

		result.setPayUrl(payUrl);
		result.setSocketKey(socketKey);
		return result;
	}
}
