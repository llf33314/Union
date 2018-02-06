package com.gt.union.api.client.address.impl;

import com.alibaba.fastjson.JSONObject;
import com.gt.union.api.client.address.AddressService;
import com.gt.union.common.util.ApiResultHandlerUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.SignRestHttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hongjiye
 * @time 2017/1/25 0022.
 */
@Service
public class AddressServiceImpl implements AddressService {

    private Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

    @Override
    public List<Map> listByIds(String ids) {
        logger.info("根据ids获取地址列表，请求参数：{}", JSONObject.toJSONString(ids));
        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/shopapi/6F6D9AD2/79B4DE7C/queryBasisCityIds.do";
        List<Map> list = new ArrayList();
        try {
            Map<String, Object> param = new HashMap();
            param.put("reqdata", ids);
            String result = SignRestHttpUtil.reqPostUTF8(url, JSONObject.toJSONString(param), PropertiesUtil.getWxmpSignKey());
            logger.info("根据ids获取地址列表，结果：{}", result);
            list = ApiResultHandlerUtil.listDataObject(result, Map.class);
        } catch (Exception e) {
            logger.error("根据ids获取地址列表错误", e);
        }
        return list;
    }

    @Override
    public List<Map> listByCityCode(String city_codes) {
        logger.info("根据city_code列表获取地址列表，请求参数:{}", JSONObject.toJSONString(city_codes));
        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/shopapi/6F6D9AD2/79B4DE7C/queryBasisByCodes.do";
        List<Map> list = new ArrayList();
        try {
            Map<String, Object> param = new HashMap();
            param.put("reqdata", city_codes);
            String result = SignRestHttpUtil.reqPostUTF8(url, JSONObject.toJSONString(param), PropertiesUtil.getWxmpSignKey());
            logger.info("根据city_code列表获取地址列表，结果：{}", result);
            list = ApiResultHandlerUtil.listDataObject(result, Map.class);
        } catch (Exception e) {
            logger.error("根据city_code列表获取地址列表错误", e);
        }
        return list;
    }


}
