package com.gt.union.union.main.controller;

import com.alibaba.fastjson.JSONObject;
import com.gt.union.card.consume.service.IUnionConsumeService;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.util.StringUtil;
import com.gt.union.opportunity.brokerage.service.IUnionBrokeragePayService;
import com.gt.union.union.main.service.IUnionMainPermitService;
import io.swagger.annotations.Api;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
@ApiIgnore
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

        Object objOrderNo = param.get("out_trade_no");
        String orderNo = objOrderNo != null ? objOrderNo.toString() : null;
        if (StringUtil.isEmpty(orderNo)) {
            Map<String, Object> result = new HashMap<>(2);
            result.put("code", -1);
            result.put("msg", "out_trade_no参数无效");
            return JSONObject.toJSONString(result);
        }

        String payOrderNo;
        Integer isSuccess;
        if ("0".equals(payType)) {
            // 微信支付
            Object objPayOrderNo = param.get("transaction_id");
            payOrderNo = objPayOrderNo != null ? objPayOrderNo.toString().trim() : "";

            Object objResultCode = param.get("result_code");
            String resultCode = objResultCode != null ? objResultCode.toString().trim() : "";
            Object objReturnCode = param.get("return_code");
            String returnCode = objReturnCode != null ? objReturnCode.toString().trim() : "";
            isSuccess = "SUCCESS".equals(resultCode.toUpperCase()) && "SUCCESS".equals(returnCode.toUpperCase())
                    ? CommonConstant.COMMON_YES : CommonConstant.COMMON_NO;
        } else {
            // 支付宝支付
            Object objPayOrderNo = param.get("trade_no");
            payOrderNo = objPayOrderNo != null ? objPayOrderNo.toString().trim() : "";

            Object objTradeStatus = param.get("trade_status");
            String tradeStatus = objTradeStatus != null ? objTradeStatus.toString().trim() : "";
            isSuccess = "TRADE_SUCCESS".equals(tradeStatus.toUpperCase()) ? CommonConstant.COMMON_YES : CommonConstant.COMMON_NO;
        }

        switch (model.toLowerCase()) {
            case "permit":
                return unionMainPermitService.updateCallbackByOrderNo(orderNo, socketKey, payType, payOrderNo, isSuccess);
            case "opportunity":
                return unionBrokeragePayService.updateCallbackByOrderNo(orderNo, socketKey, payType, payOrderNo, isSuccess);
            case "consume":
                return unionConsumeService.updateCallbackByOrderNo(orderNo, socketKey, payType, payOrderNo, isSuccess);
            case "card":
                return unionCardService.updateCallbackByOrderNo(orderNo, socketKey, payType, payOrderNo, isSuccess);
            default:
                Map<String, Object> result = new HashMap<>(2);
                result.put("code", -1);
                result.put("msg", "未定义的model类型");
                return JSONObject.toJSONString(result);
        }
    }
}
