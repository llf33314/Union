package com.gt.union.api.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.bean.sign.SignBean;
import com.gt.api.bean.sign.SignEnum;
import com.gt.api.util.KeysUtil;
import com.gt.union.api.server.entity.param.RequestApiParam;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.StringUtil;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hongjiye
 * Created by Administrator on 2017/9/4 0004.
 */
public class ApiBaseController {

    private Logger logger = Logger.getLogger(this.getClass());

    public boolean verification(HttpServletRequest request, HttpServletResponse response, RequestApiParam<?> requestApiParam) throws Exception {
        logger.info("*******************************************签名验证开启*******************************************");
        // 设置返回编码和类型
        String signKey = PropertiesUtil.getUnionSignKey();
        //获取header中的签名
        String signStr = request.getHeader("sign");
        if (StringUtil.isEmpty(signStr)) {
            throw new BusinessException("签名sign不能为空");
        }
        // 解析签名
        SignBean signBean = JSON.parseObject(signStr, SignBean.class);
        // 返回签名验证信息
        String param = JSONObject.toJSONString(requestApiParam);
        String code = decSign(signKey, signBean, param);
        if (SignEnum.TIME_OUT.getCode().equals(code)) {
            // 超过指定时间
            throw new BusinessException("请求超时");
        } else if (SignEnum.SIGN_ERROR.getCode().equals(code)) {
            logger.error(String.format("验证结果:%s", JSONObject.toJSONString(requestApiParam)));
            // 签名验证错误
            throw new BusinessException("签名验证错误，请检查签名信息");
        } else if (SignEnum.SERVER_ERROR.getCode().equals(code)) {
            logger.error(String.format("验证结果:%s", JSONObject.toJSONString(requestApiParam)));
            throw new BusinessException("系统错误，请检查传入参数");
        }
        logger.info("*******************************************签名验证结束*******************************************");
        return true;
    }

    /**
     * 签名验证
     *
     * @param signKey  签名密钥
     * @param signBean 签名Bean
     * @return 结果code（对应SignEnum里面的code）
     */
    public static String decSign(String signKey, SignBean signBean, String param) {
        String reqTime = signBean.getTimeStamp();
        // 判断时间是否在10分钟内
        boolean timeOut = contrastTimeNow(Long.parseLong(reqTime)) > 10;
        if (timeOut) {
            return SignEnum.TIME_OUT.getCode();
        }
        // 根据key+时间撮+随机数-->MD5加密
        String reqSign = signBean.getSign();
        try {
            String sign = KeysUtil.getEncString(signKey + reqTime + signBean.getRandNum() + param);
            boolean signFail = !sign.equals(reqSign);
            if (signFail) {
                return SignEnum.SIGN_ERROR.getCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return SignEnum.SIGN_ERROR.getCode();
        }

        return SignEnum.SUCCESS.getCode();
    }

    private static long contrastTimeNow(Long paramTime) {
        return contrastTime(System.currentTimeMillis(), paramTime);
    }

    /**
     * 对比两个时间戳相差的分钟数
     *
     * @param bigTime   较大的时间
     * @param smallTime 较小的时间
     * @return
     */
    private static long contrastTime(Long bigTime, Long smallTime) {
        return (bigTime - smallTime) / (1000 * 60);
    }
}
