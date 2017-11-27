package com.gt.union.api.client.user.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.bean.session.BusUser;
import com.gt.api.bean.session.WxPublicUsers;
import com.gt.api.util.HttpClienUtils;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 商家信息 服务实现类
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0022.
 */
@Service
public class BusUserServiceImpl implements IBusUserService {

    @Autowired
    private RedisCacheUtil redisCacheUtil;


    @Override
    public BusUser getBusUserById(Integer busId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userId", busId);
        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/busUserApi/getBusUserApi.do";
        try {
            String result = SignHttpUtils.WxmppostByHttp(url, param, PropertiesUtil.getWxmpSignKey());
            return ApiResultHandlerUtil.getDataObject(result, BusUser.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public BusUser getBusUserByName(String name) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name", name);
        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/busUserApi/getBusUserApi.do";
        try {
            String result = SignHttpUtils.WxmppostByHttp(url, param, PropertiesUtil.getWxmpSignKey());
            return ApiResultHandlerUtil.getDataObject(result, BusUser.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public WxPublicUsers getWxPublicUserByBusId(Integer busId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("reqdata", busId);
        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/wxpublicapi/6F6D9AD2/79B4DE7C/selectByUserId.do";
        try{
            String result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(param), url, String.class, PropertiesUtil.getWxmpSignKey());
            return ApiResultHandlerUtil.getDataObject(result, WxPublicUsers.class);
        }catch (Exception e){
            return null;
        }
    }

	@Override
	public String getWxPublicUserQRCode(Integer publicId, Integer busId) {
        String codeKey = RedisKeyUtil.getWxPublicUserQRCodeKey(publicId, busId);
        if (this.redisCacheUtil.exists(codeKey)) {//（1）通过busId获取缓存中的busUser对象，如果存在，则直接返回
            String obj = this.redisCacheUtil.get(codeKey);
            if(CommonUtil.isNotEmpty(obj)){
                return JSON.parseObject(obj, String.class);
            }
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("publicId", publicId);
        data.put("model", ConfigConstant.WXPUBLIC_QRCODE_MODEL);
        data.put("externalId", busId);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("reqdata", data);
        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/wxpublicapi/6F6D9AD2/79B4DE7C/newqrcodeCreateFinal.do";
        String result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(param), url, String.class, PropertiesUtil.getWxmpSignKey());
        String qrurl = ApiResultHandlerUtil.getDataObject(result, String.class);
        if (CommonUtil.isNotEmpty(qrurl)) {
            redisCacheUtil.set(codeKey, qrurl);
        }
		return qrurl;
	}


}
