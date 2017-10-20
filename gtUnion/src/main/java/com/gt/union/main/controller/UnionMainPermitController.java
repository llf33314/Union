package com.gt.union.main.controller;

import com.alibaba.fastjson.JSON;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.api.client.socket.SocketService;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.RedisKeyUtil;
import com.gt.union.main.service.IUnionMainPermitService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 联盟许可，盟主服务 前端控制器
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
@RestController
@RequestMapping("/unionMainPermit")
public class UnionMainPermitController {
    private Logger logger = Logger.getLogger(UnionMainController.class);

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainPermitService unionMainPermitService;

    @Autowired
    private SocketService socketService;


    @ApiOperation(value = "获取升级套餐链接", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "/feeTradeUrl", method = RequestMethod.GET)
    public String feeTradeUrl() {
        String url = PropertiesUtil.getWxmpUrl() + "/trading/index.do?setType=trading";
        return GTJsonResult.instanceSuccessMsg(url).toString();
    }

    @ApiOperation(value = "创建联盟支付成功回调地址")
    @RequestMapping(value = "/79B4DE7C/paymentSuccess/{only}", method = RequestMethod.POST)
    public String payCreateUnionSuccess(@PathVariable(name = "only") String only, @RequestBody Map<String, Object> param) {
        Map<String, Object> data = new HashMap<>(16);
        try {
            logger.info("创建联盟支付成功回调参数" + JSON.toJSONString(param));
            boolean isOK = (CommonUtil.isNotEmpty(param.get("trade_status")) && "TRADE_SUCCESS".equals(param.get("trade_status"))) ||
                    (CommonUtil.isNotEmpty(param.get("result_code")) && "SUCCESS".equals(param.get("result_code")) &&
                            CommonUtil.isNotEmpty(param.get("return_code")) && "SUCCESS".equals(param.get("return_code")));
            if (isOK) {
                String orderNo = param.get("out_trade_no").toString();
                logger.info("创建联盟支付成功，订单orderNo------------------" + orderNo);
                logger.info("创建联盟支付成功，only------------------" + only);
                String paramKey = RedisKeyUtil.getCreateUnionPayParamKey(only);
                String statusKey = RedisKeyUtil.getCreateUnionPayStatusKey(only);
                String paramData = redisCacheUtil.get(paramKey);
                Map map = JSON.parseObject(paramData, Map.class);
                unionMainPermitService.payCreateUnionSuccess(orderNo, only, CommonUtil.toInteger(param.get("payType")));
                String status = redisCacheUtil.get(statusKey);
                if (CommonUtil.isEmpty(status)) {
                    //订单超时
                    status = ConfigConstant.USER_ORDER_STATUS_004;
                } else {
                    status = JSON.parseObject(status, String.class);
                }
                if (ConfigConstant.USER_ORDER_STATUS_003.equals(status)) {
                    //订单支付成功
                    redisCacheUtil.remove(statusKey);
                }

                if (ConfigConstant.USER_ORDER_STATUS_005.equals(status)) {
                    //订单支付失败
                    redisCacheUtil.remove(statusKey);
                }
                Map<String, Object> result = new HashMap<>(16);
                result.put("status", status);
                result.put("only", only);
                logger.info("创建联盟扫码支付成功回调----------" + JSON.toJSONString(map));
                socketService.socketSendMessage(PropertiesUtil.getSocketKey() + CommonUtil.toInteger(map.get("payBusId")), JSON.toJSONString(result), "");
                data.put("code", 0);
                data.put("msg", "成功");
                return JSON.toJSONString(data);
            } else {
                throw new BusinessException("支付失败");
            }
        } catch (BaseException e) {
            logger.error("创建联盟支付成功后，产生错误：" + e);
            data.put("code", -1);
            data.put("msg", e.getErrorMsg());
            return JSON.toJSONString(data);
        } catch (Exception e) {
            logger.error("创建联盟支付成功后，产生错误：" + e);
            data.put("code", -1);
            data.put("msg", "失败");
            return JSON.toJSONString(data);
        }
    }

    @ApiOperation(value = "生成创建联盟支付订单二维码", produces = "application/json;charset=UTF-8")
    @SysLogAnnotation(op_function = "2", description = "生成创建联盟支付订单")
    @RequestMapping(value = "/qrCode/{id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String createUnionQRCode(HttpServletRequest request,
                                    @ApiParam(name = "id", value = "套餐列表中的id", required = true)
                                    @PathVariable("id") Integer id) {
        try {
            BusUser user = SessionUtils.getLoginUser(request);
            if (CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0) {
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            Map<String, Object> data = unionMainPermitService.createUnionQRCode(user, id);
            String str = "?totalFee=" + data.get("totalFee")
                    + "&model=" + data.get("model")
                    + "&busId=" + data.get("busId")
                    + "&appidType=" + data.get("appidType")
                    + "&appid=" + data.get("appid")
                    + "&orderNum=" + data.get("orderNum")
                    + "&desc=" + data.get("desc")
                    + "&isreturn=" + data.get("isreturn")
                    + "&notifyUrl=" + data.get("notifyUrl")
                    + "&isSendMessage=" + data.get("isSendMessage")
                    + "&payWay=" + data.get("payWay")
                    + "&sourceType=" + data.get("sourceType");
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("url", PropertiesUtil.getWxmpUrl() + "/pay/B02A45A5/79B4DE7C/createPayQR.do" + str);
            result.put("only", data.get("only"));
            result.put("userId", PropertiesUtil.getSocketKey() + user.getId());
            return GTJsonResult.instanceSuccessMsg(result).toString();
        } catch (Exception e) {
            logger.error("生成购买联盟服务支付二维码错误：" + e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.OPERATE_ERROR).toString();
        }
    }

    @ApiOperation(value = "获取创建联盟支付状态，用定时器请求，004：订单超时 003：支付成功 005：支付失败", produces = "application/json;charset=UTF-8")
    @RequestMapping(value = "status/{only}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getStatus(@PathVariable("only") String only) {
        logger.info("获取创建联盟支付订单状态：" + only);
        try {
            String statusKey = RedisKeyUtil.getCreateUnionPayStatusKey(only);
            String paramKey = RedisKeyUtil.getCreateUnionPayParamKey(only);
            String status = redisCacheUtil.get(statusKey);
            if (CommonUtil.isEmpty(status)) {
                //订单超时
                status = ConfigConstant.USER_ORDER_STATUS_004;
            } else {
                status = JSON.parseObject(status, String.class);
            }
            if (ConfigConstant.USER_ORDER_STATUS_003.equals(status)) {
                //订单支付成功
                redisCacheUtil.remove(statusKey);
                redisCacheUtil.remove(paramKey);
            }

            if (ConfigConstant.USER_ORDER_STATUS_005.equals(status)) {
                //订单支付失败
                redisCacheUtil.remove(statusKey);
                redisCacheUtil.remove(paramKey);
            }
            return GTJsonResult.instanceSuccessMsg(status).toString();
        } catch (Exception e) {
            logger.error("获取创建联盟支付订单状态错误：" + e);
            return GTJsonResult.instanceErrorMsg(CommonConstant.SYS_ERROR).toString();
        }
    }
}
