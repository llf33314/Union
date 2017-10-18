package com.gt.union.api.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.bean.sign.SignBean;
import com.gt.api.bean.sign.SignEnum;
import com.gt.api.util.sign.SignUtils;
import com.gt.union.api.entity.param.RequestApiParam;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2017/9/4 0004.
 */
@RestController
@RequestMapping("/")
public class ApiBaseController {

	private Logger logger = Logger.getLogger(this.getClass());

	public boolean  verification(HttpServletRequest request, HttpServletResponse response, RequestApiParam<?> requestApiParam) throws Exception {
		logger.info("*******************************************签名验证开启*******************************************");
		// 设置返回编码和类型
		String signKey = PropertiesUtil.getUnionSignKey();
		//获取header中的签名
		String signStr = request.getHeader("sign");
		if(StringUtil.isEmpty(signStr)){
			throw new BusinessException("签名sign不能为空");
		}
		// 解析签名
		SignBean signBean = JSON.parseObject(signStr, SignBean.class);
		// 返回签名验证信息
		String param = JSONObject.toJSONString(requestApiParam);
		param = new String(param.getBytes("utf-8"));
		boolean result=true;
		String code = SignUtils.decSign(signKey, signBean, param);
		if ( SignEnum.TIME_OUT.getCode().equals( code ) ) {
			// 超过指定时间
			throw new BusinessException("请求超时");
		} else if ( SignEnum.SIGN_ERROR.getCode().equals( code ) ) {
			logger.error(StringUtil.format("验证结果:%s", JSONObject.toJSONString(requestApiParam)));
			// 签名验证错误
			throw new BusinessException("签名验证错误，请检查签名信息");
		} else if ( SignEnum.SERVER_ERROR.getCode().equals( code ) ) {
			logger.error(StringUtil.format("验证结果:%s", JSONObject.toJSONString(requestApiParam)));
			throw new BusinessException("系统错误，请检查传入参数");
		}
		logger.info("*******************************************签名验证结束*******************************************");
		return result;
	}
}
