package com.gt.union.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.pay.entity.PayParam;
import com.gt.union.api.client.socket.SocketService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.union.main.constant.UnionConstant;
import com.gt.union.union.main.entity.UnionMainPackage;
import com.gt.union.union.main.entity.UnionMainPermit;
import com.gt.union.union.main.mapper.UnionMainPermitMapper;
import com.gt.union.union.main.service.IUnionMainPackageService;
import com.gt.union.union.main.service.IUnionMainPermitService;
import com.gt.union.union.main.util.UnionMainPermitCacheUtil;
import com.gt.union.union.main.vo.UnionPayVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 联盟许可 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 15:26:19
 */
@Service
public class UnionMainPermitServiceImpl extends ServiceImpl<UnionMainPermitMapper, UnionMainPermit> implements IUnionMainPermitService {

    private Logger logger = Logger.getLogger(UnionMainPermitServiceImpl.class);

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainPackageService unionMainPackageService;

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private SocketService socketService;

    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public UnionMainPermit getValidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionMainPermit> entityWrapper = new EntityWrapper<>();
        entityWrapper.ge("validity", DateUtil.getCurrentDate())
                .eq("bus_id", busId)
                .eq("order_status", UnionConstant.PERMIT_ORDER_STATUS_SUCCESS)
                .eq("del_status", CommonConstant.COMMON_NO);

        return selectOne(entityWrapper);
    }

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UnionPayVO saveByBusIdAndPackageId(Integer busId, Integer packageId) throws Exception {
        if (busId == null || packageId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）	判断packageId有效性
        UnionMainPackage unionPackage = unionMainPackageService.getById(packageId);
        if (unionPackage == null) {
            throw new BusinessException("找不到套餐信息");
        }

        // （2）	生成未支付状态的盟主服务记录
        UnionMainPermit savePermit = new UnionMainPermit();
        savePermit.setDelStatus(CommonConstant.COMMON_NO);
        Date currentDate = DateUtil.getCurrentDate();
        savePermit.setCreateTime(currentDate);
        int validMonth = BigDecimalUtil.multiply(Double.valueOf(12), unionPackage.getYear()).intValue();
        savePermit.setValidity(DateUtil.addMonths(currentDate, validMonth));
        savePermit.setBusId(busId);
        savePermit.setPackageId(packageId);
        savePermit.setOrderMoney(unionPackage.getPrice());
        savePermit.setOrderStatus(UnionConstant.PERMIT_ORDER_STATUS_PAYING);
        String orderNo = "LM_P_" + DateUtil.getSerialNumber();
        savePermit.setSysOrderNo(orderNo);
        save(savePermit);

        // （3）	调用接口，返回支付链接
        UnionPayVO result = new UnionPayVO();
        String socketKey = PropertiesUtil.getSocketKey() + orderNo;
        String notifyUrl = PropertiesUtil.getUnionUrl() + "/callBack/79B4DE7C/permit?socketKey=" + socketKey + "&ids=" + savePermit.getId();

        PayParam payParam = new PayParam();
        payParam.setTotalFee(savePermit.getOrderMoney());
        payParam.setOrderNum(savePermit.getSysOrderNo());
        payParam.setIsreturn(CommonConstant.COMMON_NO);
        payParam.setNotifyUrl(notifyUrl);
        payParam.setIsSendMessage(CommonConstant.COMMON_NO);
        payParam.setPayWay(0);
        String payUrl = wxPayService.qrCodePay(payParam);

        result.setPayUrl(payUrl);
        result.setSocketKey(socketKey);

        return result;
    }

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    @Override
    public String updateCallbackById(String permitId, String socketKey, String payType, String orderNo, Integer isSuccess) {
        Map<String, Object> result = new HashMap<>(2);
        if (permitId == null || socketKey == null || payType == null || orderNo == null || isSuccess == null) {
            result.put("code", -1);
            result.put("msg", "参数缺少");
            return JSONObject.toJSONString(result);
        }

        // （1）	判断permitId有效性
        UnionMainPermit permit;
        try {
            permit = getById(Integer.valueOf(permitId));
        } catch (Exception e) {
            logger.error("", e);
            result.put("code", -1);
            result.put("msg", e.getMessage());
            return JSONObject.toJSONString(result);
        }
        if (permit == null) {
            result.put("code", -1);
            result.put("msg", "找不到联盟许可对象");
            return JSONObject.toJSONString(result);
        }
        // （2）	如果permit不是未支付状态，则socket通知，并返回处理成功
        // （3）	否则，更新permit为支付成功状态，且socket通知，并返回处理成功
        Integer orderStatus = permit.getOrderStatus();
        if (orderStatus == UnionConstant.PERMIT_ORDER_STATUS_SUCCESS || orderStatus == UnionConstant.PERMIT_ORDER_STATUS_FAIL) {
            // socket通知
            socketService.socketPaySendMessage(socketKey, isSuccess, null);
            result.put("code", 0);
            result.put("msg", "重复处理");
            return JSONObject.toJSONString(result);
        } else {
            UnionMainPermit updatePermit = new UnionMainPermit();
            updatePermit.setId(permit.getId());
            updatePermit.setOrderStatus(isSuccess == CommonConstant.COMMON_YES ? UnionConstant.PERMIT_ORDER_STATUS_SUCCESS : UnionConstant.PERMIT_ORDER_STATUS_FAIL);
            if (payType.equals("0")) {
                updatePermit.setWxOrderNo(orderNo);
            } else {
                updatePermit.setAlipayOrderNo(orderNo);
            }

            try {
                update(updatePermit);
            } catch (Exception e) {
                logger.error("", e);
                result.put("code", -1);
                result.put("msg", e.getMessage());
                return JSONObject.toJSONString(result);
            }

            // socket通知
            socketService.socketPaySendMessage(socketKey, isSuccess, null);
            result.put("code", 0);
            result.put("msg", "成功");
            return JSONObject.toJSONString(result);
        }
    }

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Object As a Service - get **********************************************

    public UnionMainPermit getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainPermit result;
        // (1)cache
        String idKey = UnionMainPermitCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionMainPermit.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainPermit> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionMainPermit> listByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainPermit> result;
        // (1)cache
        String busIdKey = UnionMainPermitCacheUtil.getBusId(busId);
        if (redisCacheUtil.exists(busIdKey)) {
            String tempStr = redisCacheUtil.get(busIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainPermit.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainPermit> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("bus_id", busId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, busId, UnionMainPermitCacheUtil.TYPE_BUS_ID);
        return result;
    }

    public List<UnionMainPermit> listByPackageId(Integer packageId) throws Exception {
        if (packageId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainPermit> result;
        // (1)cache
        String packageIdKey = UnionMainPermitCacheUtil.getPackageId(packageId);
        if (redisCacheUtil.exists(packageIdKey)) {
            String tempStr = redisCacheUtil.get(packageIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainPermit.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainPermit> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("package_id", packageId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, packageId, UnionMainPermitCacheUtil.TYPE_PACKAGE_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMainPermit newUnionMainPermit) throws Exception {
        if (newUnionMainPermit == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionMainPermit);
        removeCache(newUnionMainPermit);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMainPermit> newUnionMainPermitList) throws Exception {
        if (newUnionMainPermitList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionMainPermitList);
        removeCache(newUnionMainPermitList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionMainPermit unionMainPermit = getById(id);
        removeCache(unionMainPermit);
        // (2)remove in db logically
        UnionMainPermit removeUnionMainPermit = new UnionMainPermit();
        removeUnionMainPermit.setId(id);
        removeUnionMainPermit.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionMainPermit);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMainPermit> unionMainPermitList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainPermit unionMainPermit = getById(id);
            unionMainPermitList.add(unionMainPermit);
        }
        removeCache(unionMainPermitList);
        // (2)remove in db logically
        List<UnionMainPermit> removeUnionMainPermitList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainPermit removeUnionMainPermit = new UnionMainPermit();
            removeUnionMainPermit.setId(id);
            removeUnionMainPermit.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMainPermitList.add(removeUnionMainPermit);
        }
        updateBatchById(removeUnionMainPermitList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMainPermit updateUnionMainPermit) throws Exception {
        if (updateUnionMainPermit == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionMainPermit.getId();
        UnionMainPermit unionMainPermit = getById(id);
        removeCache(unionMainPermit);
        // (2)update db
        updateById(updateUnionMainPermit);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMainPermit> updateUnionMainPermitList) throws Exception {
        if (updateUnionMainPermitList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionMainPermit updateUnionMainPermit : updateUnionMainPermitList) {
            idList.add(updateUnionMainPermit.getId());
        }
        List<UnionMainPermit> unionMainPermitList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainPermit unionMainPermit = getById(id);
            unionMainPermitList.add(unionMainPermit);
        }
        removeCache(unionMainPermitList);
        // (2)update db
        updateBatchById(updateUnionMainPermitList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionMainPermit newUnionMainPermit, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionMainPermitCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionMainPermit);
    }

    private void setCache(List<UnionMainPermit> newUnionMainPermitList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionMainPermitCacheUtil.TYPE_BUS_ID:
                foreignIdKey = UnionMainPermitCacheUtil.getBusId(foreignId);
                break;
            case UnionMainPermitCacheUtil.TYPE_PACKAGE_ID:
                foreignIdKey = UnionMainPermitCacheUtil.getPackageId(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMainPermitList);
        }
    }

    private void removeCache(UnionMainPermit unionMainPermit) {
        if (unionMainPermit == null) {
            return;
        }
        Integer id = unionMainPermit.getId();
        String idKey = UnionMainPermitCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer busId = unionMainPermit.getBusId();
        if (busId != null) {
            String busIdKey = UnionMainPermitCacheUtil.getBusId(busId);
            redisCacheUtil.remove(busIdKey);
        }

        Integer packageId = unionMainPermit.getPackageId();
        if (busId != null) {
            String packageIdKey = UnionMainPermitCacheUtil.getPackageId(packageId);
            redisCacheUtil.remove(packageIdKey);
        }
    }

    private void removeCache(List<UnionMainPermit> unionMainPermitList) {
        if (ListUtil.isEmpty(unionMainPermitList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionMainPermit unionMainPermit : unionMainPermitList) {
            idList.add(unionMainPermit.getId());
        }
        List<String> idKeyList = UnionMainPermitCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> busIdKeyList = getForeignIdKeyList(unionMainPermitList, UnionMainPermitCacheUtil.TYPE_BUS_ID);
        if (ListUtil.isNotEmpty(busIdKeyList)) {
            redisCacheUtil.remove(busIdKeyList);
        }

        List<String> packageIdKeyList = getForeignIdKeyList(unionMainPermitList, UnionMainPermitCacheUtil.TYPE_PACKAGE_ID);
        if (ListUtil.isNotEmpty(packageIdKeyList)) {
            redisCacheUtil.remove(packageIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionMainPermit> unionMainPermitList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionMainPermitCacheUtil.TYPE_BUS_ID:
                for (UnionMainPermit unionMainPermit : unionMainPermitList) {
                    Integer busId = unionMainPermit.getBusId();
                    if (busId != null) {
                        String busIdKey = UnionMainPermitCacheUtil.getBusId(busId);
                        result.add(busIdKey);
                    }
                }
                break;
            case UnionMainPermitCacheUtil.TYPE_PACKAGE_ID:
                for (UnionMainPermit unionMainPermit : unionMainPermitList) {
                    Integer packageId = unionMainPermit.getPackageId();
                    if (packageId != null) {
                        String packageIdKey = UnionMainPermitCacheUtil.getPackageId(packageId);
                        result.add(packageIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }

}