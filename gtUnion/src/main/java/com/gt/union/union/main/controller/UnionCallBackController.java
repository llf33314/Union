package com.gt.union.union.main.controller;

import com.alibaba.fastjson.JSONObject;
import com.gt.union.card.consume.service.IUnionConsumeService;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.StringUtil;
import com.gt.union.opportunity.brokerage.service.IUnionBrokeragePayService;
import com.gt.union.union.main.service.IUnionMainPermitService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 回调 前端控制器
 *
 * @author linweicong
 * @version 2017-12-12 10:33:08
 */
@RestController
@RequestMapping("/callBack")
public class UnionCallBackController {

    private Logger logger = Logger.getLogger(UnionCallBackController.class);

    @Autowired
    private IUnionMainPermitService unionMainPermitService;

    @Autowired
    private IUnionBrokeragePayService unionBrokeragePayService;

    @Autowired
    private IUnionConsumeService unionConsumeService;

    @Autowired
    private IUnionCardService unionCardService;

    //支付后回调
    @RequestMapping(value = "/79B4DE7C/{model}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String updateCallbackByPermitId(
            @PathVariable("model") String model,
            @RequestParam(value = "socketKey") String socketKey,
            @RequestParam(value = "ids") String ids,
            @RequestBody Map<String, Object> param) throws Exception {
        // debug
        logger.debug(JSONObject.toJSONString(param));

        Object objPayType = param.get("payType");
        String payType = objPayType != null ? objPayType.toString().trim() : "";
        boolean isPayTypeValid = StringUtil.isNotEmpty(payType) && ("0".equals(payType) || "1".equals(payType));
        if (!isPayTypeValid) {
            Map<String, Object> result = new HashMap<>(2);
            result.put("code", -1);
            result.put("msg", "payType参数无效");
            return JSONObject.toJSONString(result);
        }

        String orderNo;
        Integer isSuccess;
        if ("0".equals(payType)) {
            // 微信支付
            Object objOrderNo = param.get("transaction_id");
            orderNo = objOrderNo != null ? objOrderNo.toString().trim() : "";

            Object objResultCode = param.get("result_code");
            String resultCode = objResultCode != null ? objResultCode.toString().trim() : "";
            Object objReturnCode = param.get("return_code");
            String returnCode = objReturnCode != null ? objReturnCode.toString().trim() : "";
            isSuccess = "SUCCESS".equals(resultCode.toUpperCase()) && "SUCCESS".equals(returnCode.toUpperCase())
                    ? CommonConstant.COMMON_YES : CommonConstant.COMMON_NO;
        } else {
            // 支付宝支付
            Object objOrderNo = param.get("trade_no");
            orderNo = objOrderNo != null ? objOrderNo.toString().trim() : "";

            Object objTradeStatus = param.get("trade_status");
            String tradeStatus = objTradeStatus != null ? objTradeStatus.toString().trim() : "";
            isSuccess = "TRADE_SUCCESS".equals(tradeStatus.toUpperCase()) ? CommonConstant.COMMON_YES : CommonConstant.COMMON_NO;
        }

        switch (model.toLowerCase()) {
            case "permit":
                return unionMainPermitService.updateCallbackById(ids, socketKey, payType, orderNo, isSuccess);
            case "opportunity":
                return unionBrokeragePayService.updateCallbackByIds(ids, socketKey, payType, orderNo, isSuccess);
            case "consume":
                return unionConsumeService.updateCallbackById(ids, socketKey, payType, orderNo, isSuccess);
            case "card":
                return unionCardService.updateCallbackByIds(ids, socketKey, payType, orderNo, isSuccess);
            default:
                Map<String, Object> result = new HashMap<>(2);
                result.put("code", -1);
                result.put("msg", "未定义的model类型");
                return JSONObject.toJSONString(result);
        }
    }
}
