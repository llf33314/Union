package com.gt.union.main.controller;

import com.alibaba.fastjson.JSON;
import com.gt.api.bean.session.BusUser;
import com.gt.api.util.SessionUtils;
import com.gt.union.common.annotation.SysLogAnnotation;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
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
 * <p>
 * 联盟许可，盟主服务 前端控制器
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@RestController
@RequestMapping("/unionMainPermit")
public class UnionMainPermitController {
    private Logger logger = Logger.getLogger(UnionMainController.class);

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainPermitService unionMainPermitService;


    /**
     * 创建联盟支付成功回调地址
     *
     * @param param
     * @param only
     */
    @RequestMapping(value = "/79B4DE7C/paymentSuccess/{only}", method = RequestMethod.POST)
    public String payCreateUnionSuccess(@PathVariable(name = "only") String only, @RequestBody Map<String,Object> param) {
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            logger.info("创建联盟支付成功回调参数" + JSON.toJSONString(param));
            if(param.get("result_code").equals("SUCCESS") && param.get("result_code").equals("SUCCESS")){
                String orderNo = param.get("out_trade_no").toString();
                logger.info("创建联盟支付成功，订单orderNo------------------" + orderNo);
                logger.info("创建联盟支付成功，only------------------" + only);
                unionMainPermitService.payCreateUnionSuccess(orderNo, only);
                data.put("code", 0);
                data.put("msg", "成功");
                return JSON.toJSONString(data);
            }else {
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
    @RequestMapping(value = "/qrCode/{id}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String createUnionQRCode(HttpServletRequest request
            , @ApiParam(name = "id", value = "套餐列表中的id", required = true) @PathVariable("id") Integer id) {
        try {
            BusUser user = SessionUtils.getLoginUser(request);
            if (CommonUtil.isNotEmpty(user.getPid()) && user.getPid() != 0) {
                throw new BusinessException(CommonConstant.UNION_BUS_PARENT_MSG);
            }
            Map<String, Object> data = unionMainPermitService.createUnionQRCode(user, id);
            StringBuilder sb = new StringBuilder("?");
            sb.append("totalFee=" + data.get("totalFee"));
            sb.append("&model=" + data.get("model"));
            sb.append("&busId=" + data.get("busId"));
            sb.append("&appidType=" + data.get("appidType"));
            sb.append("&appid=" + data.get("appid"));
            sb.append("&orderNum=" + data.get("orderNum"));
            sb.append("&desc=" + data.get("desc"));
            sb.append("&isreturn=" + data.get("isreturn"));
            sb.append("&notifyUrl=" + data.get("notifyUrl"));
            sb.append("&isSendMessage=" + data.get("isSendMessage"));
            sb.append("&payWay=" + data.get("payWay"));
            sb.append("&sourceType=" + data.get("sourceType"));
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("url", ConfigConstant.WXMP_ROOT_URL + "/pay/B02A45A5/79B4DE7C/createPayQR.do" + sb.toString());
            result.put("only", data.get("only"));
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
            Object status = redisCacheUtil.get(statusKey);
            if (CommonUtil.isEmpty(status)) {//订单超时
                status = ConfigConstant.USER_ORDER_STATUS_004;
            }
            if (ConfigConstant.USER_ORDER_STATUS_003.equals(status)) {//订单支付成功
                redisCacheUtil.remove(statusKey);
                redisCacheUtil.remove(paramKey);
            }

            if (ConfigConstant.USER_ORDER_STATUS_005.equals(status)) {//订单支付失败
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
