package com.gt.union.opportunity.brokerage.service.impl;

import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.pay.entity.PayParam;
import com.gt.union.api.client.socket.constant.SocketKeyConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.opportunity.brokerage.service.IUnionBrokeragePayStrategyService;
import com.gt.union.union.main.vo.UnionPayVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hongjiye
 * @time 2017-12-21 11:02
 **/
@Service("unionBackBrokeragePayService")
public class UnionBackBrokeragePayServiceImpl implements IUnionBrokeragePayStrategyService {

    @Autowired
    private WxPayService wxPayService;

    @Override
    public UnionPayVO unionBrokerageApply(String orderNo, Double payMoneySum, Integer memberId, Integer busId) {
        UnionPayVO result = new UnionPayVO();
        String socketKey = PropertiesUtil.getSocketKey() + SocketKeyConstant.BROKERAGE + busId;
        String notifyUrl = PropertiesUtil.getUnionUrl() + "/callBack/79B4DE7C/opportunity?socketKey=" + socketKey;

        PayParam payParam = new PayParam();
        payParam.setTotalFee(payMoneySum);
        payParam.setOrderNum(orderNo);
        payParam.setIsreturn(CommonConstant.COMMON_NO);
        payParam.setNotifyUrl(notifyUrl);
        payParam.setIsSendMessage(CommonConstant.COMMON_NO);
        payParam.setPayWay(1);
        payParam.setDesc("商机佣金");
        payParam.setPayDuoFen(true);
        String payUrl = wxPayService.qrCodePay(payParam);

        result.setPayUrl(payUrl);
        result.setOrderNo(orderNo);
        result.setSocketKey(socketKey);
        return result;
    }
}
