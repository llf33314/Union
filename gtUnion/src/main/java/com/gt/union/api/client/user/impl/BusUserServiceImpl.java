package com.gt.union.api.client.user.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gt.api.bean.session.BusUser;
import com.gt.api.bean.session.WxPublicUsers;
import com.gt.api.util.HttpClienUtils;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商家信息 服务实现类
 * @author hongjiye
 * Created by Administrator on 2017/11/25 0022.
 */
@Service
public class BusUserServiceImpl implements IBusUserService {

    private Logger logger = LoggerFactory.getLogger(BusUserServiceImpl.class);

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IDictService dictService;

    @Override
    public BusUser getBusUserById(Integer busId) {
        logger.info("根据商家id获取商家信息busId：{}", busId);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userId", busId);
        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/busUserApi/getBusUserApi.do";
        try {
            String result = SignHttpUtils.WxmppostByHttp(url, param, PropertiesUtil.getWxmpSignKey());
            logger.info("根据商家id获取商家信息，结果：{}", result);
            return ApiResultHandlerUtil.getDataObject(result, BusUser.class);
        } catch (Exception e) {
            logger.error("根据商家id获取商家信息错误", e);
            return null;
        }
    }

    @Override
    public BusUser getBusUserByName(String name) {
        logger.info("根据账号名称获取商家信息name：{}", name);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name", name);
        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/busUserApi/getBusUserApi.do";
        try {
            String result = SignHttpUtils.WxmppostByHttp(url, param, PropertiesUtil.getWxmpSignKey());
            logger.info("根据账号名称获取商家信息，结果：{}", result);
            return ApiResultHandlerUtil.getDataObject(result, BusUser.class);
        } catch (Exception e) {
            logger.error("根据账号名称获取商家信息错误", e);
            return null;
        }
    }

    @Override
    public WxPublicUsers getWxPublicUserByBusId(Integer busId) {
        logger.info("根据商家id获取公众号信息busId：{}", busId);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("reqdata", busId);
        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/wxpublicapi/6F6D9AD2/79B4DE7C/selectByUserId.do";
        try{
            String result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(param), url, String.class, PropertiesUtil.getWxmpSignKey());
            logger.info("根据商家id获取公众号信息，结果：{}", result);
            return ApiResultHandlerUtil.getDataObject(result, WxPublicUsers.class);
        }catch (Exception e){
            logger.error("根据商家id获取公众号信息错误", e);
            return null;
        }
    }

	@Override
	public String getWxPublicUserQRCode(Integer publicId, Integer busId, Integer extendId) {
        logger.info("获取公众号关注二维码永久链接publicId：{}，busId：{}", publicId, busId);
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
        data.put("externalId", extendId);
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

    @Override
    public Map<String, Object> getUserUnionAuthority(Integer busId) {
        logger.info("获取创建联盟权限商家id：{}",busId);
        BusUser user = this.getBusUserById(busId);
        if(user == null){
            return null;
        }
        //商家版本名称
        String busVersionName = dictService.getBusUserLevel(user.getLevel());
        List<Map> list = dictService.listCreateUnionDict();
        if(CommonUtil.isEmpty(list)){
            return null;
        }
        Map<String, Object> data = new HashMap<String, Object>();
        for(Map map : list){
            //商家等级
            if(user.getLevel().toString().equals(map.get("item_key").toString())){
                String itemValue = map.get("item_value").toString();
                String[] items = itemValue.split(",");
                data.put("authority",CommonUtil.toInteger(items[0]));
                data.put("pay",CommonUtil.toInteger(items[1]));
                data.put("versionName",items[2]);
            }
        }
        data.put("busVersionName",busVersionName);
        return data;
    }

	@Override
	public BusUser getBusUserByPhone(String phone) {
        logger.info("根据手机号获取商家信息phone：{}", phone);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("phone", phone);
        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/busUserApi/getBusUserApi.do";
        try {
            String result = SignHttpUtils.WxmppostByHttp(url, param, PropertiesUtil.getWxmpSignKey());
            logger.info("根据手机号获取商家信息，结果：{}", result);
            return ApiResultHandlerUtil.getDataObject(result, BusUser.class);
        } catch (Exception e) {
            logger.error("根据手机号获取商家信息错误", e);
            return null;
        }
	}


}
