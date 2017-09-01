package com.gt.union.controller.common;

import com.alibaba.fastjson.JSONObject;
import com.gt.api.bean.sign.SignBean;
import com.gt.api.util.HttpClienUtils;
import com.gt.api.util.sign.HttpUtils;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.api.util.sign.SignUtils;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.StringUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/30 0030.
 */
@RestController
@RequestMapping("/login")
public class LoginController {

	private Logger logger = LoggerFactory.getLogger(LoginController.class);

	private static final String SIGN = "LoginController.getSign()";

	@Value("${wxmp.signkey}")
	private String wxmpKey;

	@Value(("${wxmp.url}"))
	private String wxmpUrl;

	@ApiOperation(value = "获取商家账号登录密钥", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "/sign", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public String getSign(HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "userName", required = true) String userName, @RequestParam(name = "password", required = true) String password){
		logger.info("获取登录密钥---" + "userName：" + userName + "----" + "password：" + password);
		try{
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("login_name",userName);
			param.put("password",password);
			SignBean sign = SignUtils.sign(wxmpKey , JSONObject.toJSONString(param));
			return GTJsonResult.instanceSuccessMsg(sign).toString();
		}catch (Exception e){
			return GTJsonResult.instanceErrorMsg(SIGN, "获取登录信息错误").toString();
		}
	}

	@ApiOperation(value = "登录", produces = "application/json;charset=UTF-8")
	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String login(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = true) String sign ) {
		logger.info("登录信息：" + sign.toString());
		try {
			JSONObject obj = JSONObject.parseObject(sign);
			String url = wxmpUrl + "/ErpMenus/79B4DE7C/UnionErplogin.do";
			String result = SignHttpUtils.WxmppostByHttp(url,obj,wxmpKey);
			if(StringUtil.isEmpty(result)){
				return GTJsonResult.instanceErrorMsg(SIGN, "登录错误", "登录错误").toString();
			}
			JSONObject data = JSONObject.parseObject(result);
			if(data.get("code").equals("1")){//验证错误
				return GTJsonResult.instanceErrorMsg(SIGN, "登录错误", "登录错误").toString();
			}
			if(data.get("code").equals("2")){//参数错误
				return GTJsonResult.instanceErrorMsg(SIGN, "登录错误", data.getString("msg")).toString();
			}
			return GTJsonResult.instanceSuccessMsg().toString();
		} catch (Exception e) {
			logger.error("佣金平台商家账号登录错误", e);
			return GTJsonResult.instanceErrorMsg(SIGN, "登录错误", "登录错误").toString();
		}
	}
}
