package com.gt.union.union.main.controller;

import com.alibaba.fastjson.JSONObject;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.constant.BusUserConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.StringUtil;
import com.gt.union.union.main.service.IUnionMainPermitService;
import com.gt.union.union.main.vo.UnionPermitPayVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 联盟许可 前端控制器
 *
 * @author linweicong
 * @version 2017-11-23 15:26:19
 */
@Api(description = "联盟许可")
@RestController
@RequestMapping("/unionMainPermit")
public class UnionMainPermitController {

    private Logger logger = Logger.getLogger(UnionMainPermitController.class);

    @Autowired
    private IUnionMainPermitService unionMainPermitService;

    //-------------------------------------------------- get -----------------------------------------------------------

    //-------------------------------------------------- put -----------------------------------------------------------

    //-------------------------------------------------- post ----------------------------------------------------------

    @ApiOperation(value = "购买联盟盟主服务-支付", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/packageId/{packageId}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public GtJsonResult<UnionPermitPayVO> saveByPackageId(
            HttpServletRequest request,
            @ApiParam(value = "套餐id", name = "packageId", required = true)
            @PathVariable("packageId") Integer packageId) throws Exception {
        BusUser busUser = SessionUtils.getLoginUser(request);
        Integer busId = busUser.getId();
        if (busUser.getPid() != null && busUser.getPid() != BusUserConstant.ACCOUNT_TYPE_UNVALID) {
            throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
        }
        // mock
//        UnionPermitPayVO result = MockUtil.get(UnionPermitPayVO.class);
        UnionPermitPayVO result = unionMainPermitService.saveByBusIdAndPackageId(busId, packageId);
        return GtJsonResult.instanceSuccessMsg(result);
    }

    @ApiOperation(value = "购买联盟盟主服务-支付-回调", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/79B4DE7C/{permitId}/pay/callback", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String updateCallbackByPermitId(
            @ApiParam(value = "联盟许可id", name = "permitId", required = true)
            @PathVariable("permitId") Integer permitId,
            @ApiParam(value = "socket关键字", name = "socketKey", required = true)
            @RequestParam(value = "socketKey") String socketKey,
            @RequestBody Map<String, Object> param) throws Exception {
        /// debug
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
        
        return unionMainPermitService.updateCallbackByPermitId(permitId, socketKey, payType, orderNo, isSuccess);
    }

}