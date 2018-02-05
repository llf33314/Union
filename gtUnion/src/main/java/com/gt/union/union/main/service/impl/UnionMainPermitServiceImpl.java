package com.gt.union.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.pay.entity.PayParam;
import com.gt.union.api.client.socket.SocketService;
import com.gt.union.api.client.socket.constant.SocketKeyConstant;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.union.main.constant.UnionConstant;
import com.gt.union.union.main.dao.IUnionMainPermitDao;
import com.gt.union.union.main.entity.UnionMainPackage;
import com.gt.union.union.main.entity.UnionMainPermit;
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
public class UnionMainPermitServiceImpl implements IUnionMainPermitService {
    private Logger logger = Logger.getLogger(UnionMainPermitServiceImpl.class);

    @Autowired
    private IUnionMainPermitDao unionMainPermitDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainPackageService unionMainPackageService;

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private SocketService socketService;

    //********************************************* Base On Business - get *********************************************

    @Override
    public UnionMainPermit getValidByBusIdAndOrderStatus(Integer busId, Integer orderStatus) throws Exception {
        if (busId == null || orderStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainPermit> result = listValidByBusIdAndOrderStatus(busId, orderStatus);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    @Override
    public UnionMainPermit getValidBySysOrderNo(String sysOrderNo) throws Exception {
        if (sysOrderNo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionMainPermit> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .eq("sys_order_no", sysOrderNo);

        return unionMainPermitDao.selectOne(entityWrapper);
    }


    //********************************************* Base On Business - list ********************************************

    @Override
    public List<UnionMainPermit> listValidByBusIdAndOrderStatus(Integer busId, Integer orderStatus) throws Exception {
        if (busId == null || orderStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainPermit> result = listValidByBusId(busId);
        result = filterByOrderStatus(result, orderStatus);

        return result;
    }

    //********************************************* Base On Business - save ********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UnionPayVO saveByBusIdAndPackageId(Integer busId, Integer packageId) throws Exception {
        if (busId == null || packageId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // 判断packageId有效性
        UnionMainPackage unionPackage = unionMainPackageService.getValidById(packageId);
        if (unionPackage == null) {
            throw new BusinessException("找不到套餐信息");
        }
        // 生成支付中状态的联盟许可记录
        UnionMainPermit savePermit = new UnionMainPermit();
        savePermit.setDelStatus(CommonConstant.COMMON_NO);
        Date currentDate = DateUtil.getCurrentDate();
        savePermit.setCreateTime(currentDate);
        int validMonth = BigDecimalUtil.multiply(12.0, unionPackage.getYear()).intValue();
        savePermit.setValidity(DateUtil.addMonths(currentDate, validMonth));
        savePermit.setBusId(busId);
        savePermit.setPackageId(packageId);
        savePermit.setOrderMoney(unionPackage.getPrice());
        savePermit.setOrderStatus(UnionConstant.PERMIT_ORDER_STATUS_PAYING);
        String orderNo = "LM" + ConfigConstant.PAY_MODEL_PERMIT + DateUtil.getSerialNumber();
        savePermit.setSysOrderNo(orderNo);
        // 调用支付接口，返回支付链接
        UnionPayVO result = new UnionPayVO();
        String socketKey = PropertiesUtil.getSocketKey() + SocketKeyConstant.UNION_PERMIT + busId;
        String notifyUrl = PropertiesUtil.getUnionUrl() + "/callBack/79B4DE7C/permit?socketKey=" + socketKey;

        PayParam payParam = new PayParam()
                .setTotalFee(savePermit.getOrderMoney())
                .setOrderNum(savePermit.getSysOrderNo())
                .setIsreturn(CommonConstant.COMMON_NO)
                .setNotifyUrl(notifyUrl)
                .setIsSendMessage(CommonConstant.COMMON_NO)
                .setPayWay(0)
                .setDesc("购买盟主服务")
                .setPayDuoFen(true);
        String payUrl = wxPayService.qrCodePay(payParam);

        result.setOrderNo(orderNo);
        result.setPayUrl(payUrl);
        result.setSocketKey(socketKey);

        save(savePermit);
        return result;
    }


    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    @Override
    public String updateCallbackByOrderNo(String orderNo, String socketKey, String payType, String payOrderNo, Integer isSuccess) {
        Map<String, Object> result = new HashMap<>(2);
        if (orderNo == null || socketKey == null || payType == null || payOrderNo == null || isSuccess == null) {
            result.put("code", -1);
            result.put("msg", "参数缺少");
            return JSONObject.toJSONString(result);
        }
        // 判断permitId有效性
        UnionMainPermit permit;
        try {
            permit = getValidBySysOrderNo(orderNo);
        } catch (Exception e) {
            logger.error("购买盟主服务，支付回调错误", e);
            result.put("code", -1);
            result.put("msg", "订单不存在");
            return JSONObject.toJSONString(result);
        }
        if (permit == null) {
            result.put("code", -1);
            result.put("msg", "找不到联盟许可对象");
            return JSONObject.toJSONString(result);
        }
        // 判断permit支付状态
        Integer orderStatus = permit.getOrderStatus();
        if (orderStatus == UnionConstant.PERMIT_ORDER_STATUS_SUCCESS || orderStatus == UnionConstant.PERMIT_ORDER_STATUS_FAIL) {
            result.put("code", -1);
            result.put("msg", "订单已重复处理");
            return JSONObject.toJSONString(result);
        } else {
            UnionMainPermit updatePermit = new UnionMainPermit();
            updatePermit.setId(permit.getId());
            updatePermit.setOrderStatus(isSuccess == CommonConstant.COMMON_YES ? UnionConstant.PERMIT_ORDER_STATUS_SUCCESS : UnionConstant.PERMIT_ORDER_STATUS_FAIL);
            if ("0".equals(payType)) {
                updatePermit.setPayType(UnionConstant.PERMIT_PAY_TYPE_WX);
                updatePermit.setWxOrderNo(payOrderNo);
            } else {
                updatePermit.setPayType(UnionConstant.PERMIT_PAY_TYPE_ALIPAY);
                updatePermit.setAlipayOrderNo(payOrderNo);
            }

            try {
                update(updatePermit);
            } catch (Exception e) {
                logger.error("购买盟主服务，支付回调错误", e);
                result.put("code", -1);
                result.put("msg", e.getMessage());
                return JSONObject.toJSONString(result);
            }

            // socket通知
            socketService.socketPaySendMessage(socketKey, isSuccess, null, orderNo);
            result.put("code", 0);
            result.put("msg", "成功");
            return JSONObject.toJSONString(result);
        }
    }

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionMainPermit> filterByDelStatus(List<UnionMainPermit> unionMainPermitList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainPermit> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMainPermitList)) {
            for (UnionMainPermit unionMainPermit : unionMainPermitList) {
                if (delStatus.equals(unionMainPermit.getDelStatus())) {
                    result.add(unionMainPermit);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionMainPermit> filterByOrderStatus(List<UnionMainPermit> unionMainPermitList, Integer orderStatus) throws Exception {
        if (orderStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainPermit> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMainPermitList)) {
            for (UnionMainPermit unionMainPermit : unionMainPermitList) {
                if (orderStatus.equals(unionMainPermit.getOrderStatus())) {
                    result.add(unionMainPermit);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
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
        result = unionMainPermitDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionMainPermit getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainPermit result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionMainPermit getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainPermit result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionMainPermit> unionMainPermitList) throws Exception {
        if (unionMainPermitList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMainPermitList)) {
            for (UnionMainPermit unionMainPermit : unionMainPermitList) {
                result.add(unionMainPermit.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionMainPermit> listByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainPermit> result;
        // (1)cache
        String busIdKey = UnionMainPermitCacheUtil.getBusIdKey(busId);
        if (redisCacheUtil.exists(busIdKey)) {
            String tempStr = redisCacheUtil.get(busIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainPermit.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainPermit> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("bus_id", busId);
        result = unionMainPermitDao.selectList(entityWrapper);
        setCache(result, busId, UnionMainPermitCacheUtil.TYPE_BUS_ID);
        return result;
    }

    @Override
    public List<UnionMainPermit> listValidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainPermit> result;
        // (1)cache
        String validBusIdKey = UnionMainPermitCacheUtil.getValidBusIdKey(busId);
        if (redisCacheUtil.exists(validBusIdKey)) {
            String tempStr = redisCacheUtil.get(validBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainPermit.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainPermit> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);
        result = unionMainPermitDao.selectList(entityWrapper);
        setValidCache(result, busId, UnionMainPermitCacheUtil.TYPE_BUS_ID);
        return result;
    }

    @Override
    public List<UnionMainPermit> listInvalidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainPermit> result;
        // (1)cache
        String invalidBusIdKey = UnionMainPermitCacheUtil.getInvalidBusIdKey(busId);
        if (redisCacheUtil.exists(invalidBusIdKey)) {
            String tempStr = redisCacheUtil.get(invalidBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainPermit.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainPermit> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("bus_id", busId);
        result = unionMainPermitDao.selectList(entityWrapper);
        setInvalidCache(result, busId, UnionMainPermitCacheUtil.TYPE_BUS_ID);
        return result;
    }

    @Override
    public List<UnionMainPermit> listByPackageId(Integer packageId) throws Exception {
        if (packageId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainPermit> result;
        // (1)cache
        String packageIdKey = UnionMainPermitCacheUtil.getPackageIdKey(packageId);
        if (redisCacheUtil.exists(packageIdKey)) {
            String tempStr = redisCacheUtil.get(packageIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainPermit.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainPermit> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("package_id", packageId);
        result = unionMainPermitDao.selectList(entityWrapper);
        setCache(result, packageId, UnionMainPermitCacheUtil.TYPE_PACKAGE_ID);
        return result;
    }

    @Override
    public List<UnionMainPermit> listValidByPackageId(Integer packageId) throws Exception {
        if (packageId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainPermit> result;
        // (1)cache
        String validPackageIdKey = UnionMainPermitCacheUtil.getValidPackageIdKey(packageId);
        if (redisCacheUtil.exists(validPackageIdKey)) {
            String tempStr = redisCacheUtil.get(validPackageIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainPermit.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainPermit> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("package_id", packageId);
        result = unionMainPermitDao.selectList(entityWrapper);
        setValidCache(result, packageId, UnionMainPermitCacheUtil.TYPE_PACKAGE_ID);
        return result;
    }

    @Override
    public List<UnionMainPermit> listInvalidByPackageId(Integer packageId) throws Exception {
        if (packageId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainPermit> result;
        // (1)cache
        String invalidPackageIdKey = UnionMainPermitCacheUtil.getInvalidPackageIdKey(packageId);
        if (redisCacheUtil.exists(invalidPackageIdKey)) {
            String tempStr = redisCacheUtil.get(invalidPackageIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainPermit.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainPermit> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("package_id", packageId);
        result = unionMainPermitDao.selectList(entityWrapper);
        setInvalidCache(result, packageId, UnionMainPermitCacheUtil.TYPE_PACKAGE_ID);
        return result;
    }

    @Override
    public List<UnionMainPermit> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainPermit> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionMainPermit> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionMainPermitDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMainPermit newUnionMainPermit) throws Exception {
        if (newUnionMainPermit == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionMainPermitDao.insert(newUnionMainPermit);
        removeCache(newUnionMainPermit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMainPermit> newUnionMainPermitList) throws Exception {
        if (newUnionMainPermitList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionMainPermitDao.insertBatch(newUnionMainPermitList);
        removeCache(newUnionMainPermitList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
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
        unionMainPermitDao.updateById(removeUnionMainPermit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMainPermit> unionMainPermitList = listByIdList(idList);
        removeCache(unionMainPermitList);
        // (2)remove in db logically
        List<UnionMainPermit> removeUnionMainPermitList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainPermit removeUnionMainPermit = new UnionMainPermit();
            removeUnionMainPermit.setId(id);
            removeUnionMainPermit.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMainPermitList.add(removeUnionMainPermit);
        }
        unionMainPermitDao.updateBatchById(removeUnionMainPermitList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
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
        unionMainPermitDao.updateById(updateUnionMainPermit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMainPermit> updateUnionMainPermitList) throws Exception {
        if (updateUnionMainPermitList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionMainPermitList);
        List<UnionMainPermit> unionMainPermitList = listByIdList(idList);
        removeCache(unionMainPermitList);
        // (2)update db
        unionMainPermitDao.updateBatchById(updateUnionMainPermitList);
    }

    //********************************************* Object As a Service - cache support ********************************

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
                foreignIdKey = UnionMainPermitCacheUtil.getBusIdKey(foreignId);
                break;
            case UnionMainPermitCacheUtil.TYPE_PACKAGE_ID:
                foreignIdKey = UnionMainPermitCacheUtil.getPackageIdKey(foreignId);
                break;

            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMainPermitList);
        }
    }

    private void setValidCache(List<UnionMainPermit> newUnionMainPermitList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionMainPermitCacheUtil.TYPE_BUS_ID:
                validForeignIdKey = UnionMainPermitCacheUtil.getValidBusIdKey(foreignId);
                break;
            case UnionMainPermitCacheUtil.TYPE_PACKAGE_ID:
                validForeignIdKey = UnionMainPermitCacheUtil.getValidPackageIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionMainPermitList);
        }
    }

    private void setInvalidCache(List<UnionMainPermit> newUnionMainPermitList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionMainPermitCacheUtil.TYPE_BUS_ID:
                invalidForeignIdKey = UnionMainPermitCacheUtil.getInvalidBusIdKey(foreignId);
                break;
            case UnionMainPermitCacheUtil.TYPE_PACKAGE_ID:
                invalidForeignIdKey = UnionMainPermitCacheUtil.getInvalidPackageIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionMainPermitList);
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
            String busIdKey = UnionMainPermitCacheUtil.getBusIdKey(busId);
            redisCacheUtil.remove(busIdKey);

            String validBusIdKey = UnionMainPermitCacheUtil.getValidBusIdKey(busId);
            redisCacheUtil.remove(validBusIdKey);

            String invalidBusIdKey = UnionMainPermitCacheUtil.getInvalidBusIdKey(busId);
            redisCacheUtil.remove(invalidBusIdKey);
        }

        Integer packageId = unionMainPermit.getPackageId();
        if (packageId != null) {
            String packageIdKey = UnionMainPermitCacheUtil.getPackageIdKey(packageId);
            redisCacheUtil.remove(packageIdKey);

            String validPackageIdKey = UnionMainPermitCacheUtil.getValidPackageIdKey(packageId);
            redisCacheUtil.remove(validPackageIdKey);

            String invalidPackageIdKey = UnionMainPermitCacheUtil.getInvalidPackageIdKey(packageId);
            redisCacheUtil.remove(invalidPackageIdKey);
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
                        String busIdKey = UnionMainPermitCacheUtil.getBusIdKey(busId);
                        result.add(busIdKey);

                        String validBusIdKey = UnionMainPermitCacheUtil.getValidBusIdKey(busId);
                        result.add(validBusIdKey);

                        String invalidBusIdKey = UnionMainPermitCacheUtil.getInvalidBusIdKey(busId);
                        result.add(invalidBusIdKey);
                    }
                }
                break;
            case UnionMainPermitCacheUtil.TYPE_PACKAGE_ID:
                for (UnionMainPermit unionMainPermit : unionMainPermitList) {
                    Integer packageId = unionMainPermit.getPackageId();
                    if (packageId != null) {
                        String packageIdKey = UnionMainPermitCacheUtil.getPackageIdKey(packageId);
                        result.add(packageIdKey);

                        String validPackageIdKey = UnionMainPermitCacheUtil.getValidPackageIdKey(packageId);
                        result.add(validPackageIdKey);

                        String invalidPackageIdKey = UnionMainPermitCacheUtil.getInvalidPackageIdKey(packageId);
                        result.add(invalidPackageIdKey);
                    }
                }
                break;

            default:
                break;
        }
        return result;
    }

}
