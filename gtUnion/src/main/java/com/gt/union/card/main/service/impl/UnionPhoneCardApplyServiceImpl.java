package com.gt.union.card.main.service.impl;

import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.pay.entity.PayParam;
import com.gt.union.card.main.service.IUnionCardApplyService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.union.main.vo.UnionPayVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hongjiye
 * @time 2017-12-21 10:22
 **/
@Service("unionPhoneCardApplyService")
public class UnionPhoneCardApplyServiceImpl implements IUnionCardApplyService{

	@Autowired
	private WxPayService wxPayService;

	@Override
	public UnionPayVO unionCardApply(String orderNo, Double payMoneySum, Integer busId) {
		UnionPayVO result = new UnionPayVO();
		String notifyUrl = PropertiesUtil.getUnionUrl() + "/callBack/79B4DE7C/card?socketKey=";

		PayParam payParam = new PayParam();
		payParam.setTotalFee(payMoneySum.doubleValue());
		payParam.setOrderNum(orderNo);
		payParam.setIsreturn(CommonConstant.COMMON_YES);
		payParam.setReturnUrl(ConfigConstant.CARD_PHONE_BASE_URL + "toMyInformation/" + busId);//TODO  支付成功后同步地址
		payParam.setNotifyUrl(notifyUrl);
		payParam.setIsSendMessage(CommonConstant.COMMON_NO);
		payParam.setPayWay(1);
		payParam.setDesc("办理联盟卡");
		String payUrl = wxPayService.pay(payParam);

		result.setPayUrl(payUrl);
		return result;
	}
}
