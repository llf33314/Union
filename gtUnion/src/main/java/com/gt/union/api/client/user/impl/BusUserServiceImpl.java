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
 * Created by Administrator on 2017/8/22 0022.
 */
@Service
public class BusUserServiceImpl implements IBusUserService {

    @Autowired
    private RedisCacheUtil redisCacheUtil;


    @Override
    public BusUser getBusUserById(Integer id) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userId", id);
        BusUser busUser = null;
        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/busUserApi/getBusUserApi.do";
        try {
            String result = SignHttpUtils.WxmppostByHttp(url, param, PropertiesUtil.getWxmpSignKey());
            if (StringUtil.isEmpty(result)) {
                return null;
            }
            Map<String, Object> data = JSON.parseObject(result, Map.class);
            if (CommonUtil.isEmpty(data.get("data"))) {
                return null;
            }
            busUser = JSON.parseObject(data.get("data").toString(), BusUser.class);
        } catch (Exception e) {
            return null;
        }
        return busUser;
    }

    @Override
    public BusUser getBusUserByName(String name) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name", name);
        BusUser busUser = null;
        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/busUserApi/getBusUserApi.do";
        try {
            String result = SignHttpUtils.WxmppostByHttp(url, param, PropertiesUtil.getWxmpSignKey());
            if (StringUtil.isEmpty(result)) {
                return null;
            }
            Map<String, Object> data = JSON.parseObject(result, Map.class);
            if (CommonUtil.isEmpty(data.get("data"))) {
                return null;
            }
            busUser = JSON.parseObject(data.get("data").toString(), BusUser.class);
        } catch (Exception e) {
            return null;
        }
        return busUser;
    }

    @Override
    public WxPublicUsers getWxPublicUserByBusId(Integer busId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("reqdata", busId);
        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/wxpublicapi/6F6D9AD2/79B4DE7C/selectByUserId.do";
        WxPublicUsers publicUsers = null;
        try{
            Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(param), url, Map.class, PropertiesUtil.getWxmpSignKey());
            if (CommonUtil.isEmpty(result)) {
                return null;
            }
            if (CommonUtil.toInteger(result.get("code")) != 0) {
                return null;
            }
            publicUsers = JSONObject.parseObject(result.get("data").toString(), WxPublicUsers.class);
        }catch (Exception e){
            return null;
        }
        return publicUsers;
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
        Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(param), url, Map.class, PropertiesUtil.getWxmpSignKey());
        if (CommonUtil.isEmpty(result)) {
            return null;
        }
        if (CommonUtil.toInteger(result.get("code")) != 0) {
            return null;
        }
        String qrurl = result.get("data").toString();
        if (CommonUtil.isNotEmpty(qrurl)) {
            redisCacheUtil.set(codeKey, qrurl);
        }
		return qrurl;
	}


}
