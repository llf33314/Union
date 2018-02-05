package com.gt.union.api.client.erp.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.util.sign.SignHttpUtils;
import com.gt.union.api.client.erp.ErpService;
import com.gt.union.api.client.erp.vo.ErpTypeVO;
import com.gt.union.api.client.erp.vo.ErpServerVO;
import com.gt.union.api.client.shop.ShopService;
import com.gt.union.api.client.shop.vo.ShopVO;
import com.gt.union.api.erp.car.service.CarErpService;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.util.ApiResultHandlerUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.PropertiesUtil;
import com.gt.union.common.util.SignRestHttpUtil;
import com.gt.util.entity.result.shop.WsWxShopInfoExtend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hongjiye
 * @time 2017-12-04 15:27
 **/
@Service
public class ErpServiceImpl implements ErpService {

    private Logger logger = LoggerFactory.getLogger(ErpServiceImpl.class);

    @Autowired
    private CarErpService carErpService;

    @Autowired
    private ShopService shopService;

    @Override
    public List<ErpTypeVO> listErpByBusId(Integer busId) {
        logger.info("根据商家id获取erp列表，busId:{}", busId);
        String url = PropertiesUtil.getWxmpUrl() + "/8A5DA52E/ErploginApi/getErpListApi.do";
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userId", busId);
        param.put("loginStyle", 1);
        param.put("loginUc", 0);
        List<ErpTypeVO> dataList = new ArrayList<ErpTypeVO>();
        try {
            String result = SignRestHttpUtil.wxmpPostByHttp(url, JSONObject.toJSONString(param), PropertiesUtil.getWxmpSignKey());
            logger.info("根据商家id获取erp列表，结果：{}", result);
            Map data = ApiResultHandlerUtil.getDataObject(result, Map.class);
            List<ErpTypeVO> list = JSON.parseArray(data.get("menusLevelList").toString(), ErpTypeVO.class);
            if (ListUtil.isNotEmpty(list)) {
                for (ErpTypeVO vo : list) {
                    for (int erpType : ConfigConstant.UNION_USER_ERP_TYPE) {
                        if (erpType == vo.getErpModel()) {
                            vo.setErpType(vo.getErpModel());
                            dataList.add(vo);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("根据商家id获取erp列表错误", e);
            return null;
        }
        return dataList;
    }

    @Override
    public List<ErpServerVO> listErpServer(Integer shopId, Integer erpModel, String search, Page page, Integer busId) {
        List<ErpServerVO> list = null;
        switch (erpModel) {
            case 2:
                //车小算
                list = carErpService.listErpServer(shopId, search, busId, page);
                if (ListUtil.isNotEmpty(list)) {
                    for (ErpServerVO vo : list) {
                        vo.setErpType(erpModel);
                        vo.setShopId(shopId);
                    }
                }
                break;
            default:
                break;
        }
        return list;
    }

    @Override
    public Boolean userHasErpAuthority(Integer busId) {
        List<ErpTypeVO> list = listErpByBusId(busId);
        if (ListUtil.isEmpty(list)) {
            return false;
        }
        List<WsWxShopInfoExtend> shops = shopService.listByBusId(busId);
        if (ListUtil.isEmpty(shops)) {
            return false;
        }
        return true;
    }


}
