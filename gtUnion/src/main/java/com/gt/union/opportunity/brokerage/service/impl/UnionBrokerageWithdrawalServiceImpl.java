package com.gt.union.opportunity.brokerage.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.opportunity.brokerage.dao.IUnionBrokerageWithdrawalDao;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageWithdrawal;
import com.gt.union.opportunity.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.opportunity.brokerage.service.IUnionBrokerageWithdrawalService;
import com.gt.union.opportunity.brokerage.util.UnionBrokerageWithdrawalCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 佣金提现 服务实现类
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
@Service
public class UnionBrokerageWithdrawalServiceImpl implements IUnionBrokerageWithdrawalService {
    @Autowired
    private IUnionBrokerageWithdrawalDao unionBrokerageWithdrawalDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionBrokerageIncomeService unionBrokerageIncomeService;

    //********************************************* Base On Business - get *********************************************

    @Override
    public Double getValidAvailableMoneyByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）收入
        Double incomeSum = unionBrokerageIncomeService.sumValidMoneyByBusId(busId);
        // （2）已提现
        Double withdrawalSum = sumValidMoneyByBusId(busId);

        return BigDecimalUtil.subtract(incomeSum, withdrawalSum).doubleValue();
    }

    //********************************************* Base On Business - list ********************************************


    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    @Override
    public Double sumValidMoneyByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);
        List<UnionBrokerageWithdrawal> withdrawalList = unionBrokerageWithdrawalDao.selectList(entityWrapper);

        BigDecimal result = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(withdrawalList)) {
            for (UnionBrokerageWithdrawal withdrawal : withdrawalList) {
                result = BigDecimalUtil.add(result, withdrawal.getMoney());
            }
        }

        return result.doubleValue();
    }

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionBrokerageWithdrawal> filterByDelStatus(List<UnionBrokerageWithdrawal> unionBrokerageWithdrawalList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionBrokerageWithdrawal> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionBrokerageWithdrawalList)) {
            for (UnionBrokerageWithdrawal unionBrokerageWithdrawal : unionBrokerageWithdrawalList) {
                if (delStatus.equals(unionBrokerageWithdrawal.getDelStatus())) {
                    result.add(unionBrokerageWithdrawal);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionBrokerageWithdrawal getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionBrokerageWithdrawal result;
        // (1)cache
        String idKey = UnionBrokerageWithdrawalCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionBrokerageWithdrawal.class);
            return result;
        }
        // (2)db
        result = unionBrokerageWithdrawalDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionBrokerageWithdrawal getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionBrokerageWithdrawal result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionBrokerageWithdrawal getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionBrokerageWithdrawal result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionBrokerageWithdrawal> unionBrokerageWithdrawalList) throws Exception {
        if (unionBrokerageWithdrawalList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionBrokerageWithdrawalList)) {
            for (UnionBrokerageWithdrawal unionBrokerageWithdrawal : unionBrokerageWithdrawalList) {
                result.add(unionBrokerageWithdrawal.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionBrokerageWithdrawal> listByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageWithdrawal> result;
        // (1)cache
        String busIdKey = UnionBrokerageWithdrawalCacheUtil.getBusIdKey(busId);
        if (redisCacheUtil.exists(busIdKey)) {
            String tempStr = redisCacheUtil.get(busIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageWithdrawal.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("bus_id", busId);
        result = unionBrokerageWithdrawalDao.selectList(entityWrapper);
        setCache(result, busId, UnionBrokerageWithdrawalCacheUtil.TYPE_BUS_ID);
        return result;
    }

    @Override
    public List<UnionBrokerageWithdrawal> listValidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageWithdrawal> result;
        // (1)cache
        String validBusIdKey = UnionBrokerageWithdrawalCacheUtil.getValidBusIdKey(busId);
        if (redisCacheUtil.exists(validBusIdKey)) {
            String tempStr = redisCacheUtil.get(validBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageWithdrawal.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);
        result = unionBrokerageWithdrawalDao.selectList(entityWrapper);
        setValidCache(result, busId, UnionBrokerageWithdrawalCacheUtil.TYPE_BUS_ID);
        return result;
    }

    @Override
    public List<UnionBrokerageWithdrawal> listInvalidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageWithdrawal> result;
        // (1)cache
        String invalidBusIdKey = UnionBrokerageWithdrawalCacheUtil.getInvalidBusIdKey(busId);
        if (redisCacheUtil.exists(invalidBusIdKey)) {
            String tempStr = redisCacheUtil.get(invalidBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageWithdrawal.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("bus_id", busId);
        result = unionBrokerageWithdrawalDao.selectList(entityWrapper);
        setInvalidCache(result, busId, UnionBrokerageWithdrawalCacheUtil.TYPE_BUS_ID);
        return result;
    }

    @Override
    public List<UnionBrokerageWithdrawal> listByVerifierId(Integer verifierId) throws Exception {
        if (verifierId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageWithdrawal> result;
        // (1)cache
        String verifierIdKey = UnionBrokerageWithdrawalCacheUtil.getVerifierIdKey(verifierId);
        if (redisCacheUtil.exists(verifierIdKey)) {
            String tempStr = redisCacheUtil.get(verifierIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageWithdrawal.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("verifier_id", verifierId);
        result = unionBrokerageWithdrawalDao.selectList(entityWrapper);
        setCache(result, verifierId, UnionBrokerageWithdrawalCacheUtil.TYPE_VERIFIER_ID);
        return result;
    }

    @Override
    public List<UnionBrokerageWithdrawal> listValidByVerifierId(Integer verifierId) throws Exception {
        if (verifierId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageWithdrawal> result;
        // (1)cache
        String validVerifierIdKey = UnionBrokerageWithdrawalCacheUtil.getValidVerifierIdKey(verifierId);
        if (redisCacheUtil.exists(validVerifierIdKey)) {
            String tempStr = redisCacheUtil.get(validVerifierIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageWithdrawal.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("verifier_id", verifierId);
        result = unionBrokerageWithdrawalDao.selectList(entityWrapper);
        setValidCache(result, verifierId, UnionBrokerageWithdrawalCacheUtil.TYPE_VERIFIER_ID);
        return result;
    }

    @Override
    public List<UnionBrokerageWithdrawal> listInvalidByVerifierId(Integer verifierId) throws Exception {
        if (verifierId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokerageWithdrawal> result;
        // (1)cache
        String invalidVerifierIdKey = UnionBrokerageWithdrawalCacheUtil.getInvalidVerifierIdKey(verifierId);
        if (redisCacheUtil.exists(invalidVerifierIdKey)) {
            String tempStr = redisCacheUtil.get(invalidVerifierIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokerageWithdrawal.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("verifier_id", verifierId);
        result = unionBrokerageWithdrawalDao.selectList(entityWrapper);
        setInvalidCache(result, verifierId, UnionBrokerageWithdrawalCacheUtil.TYPE_VERIFIER_ID);
        return result;
    }

    @Override
    public List<UnionBrokerageWithdrawal> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionBrokerageWithdrawal> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionBrokerageWithdrawal> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionBrokerageWithdrawalDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionBrokerageWithdrawal newUnionBrokerageWithdrawal) throws Exception {
        if (newUnionBrokerageWithdrawal == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionBrokerageWithdrawalDao.insert(newUnionBrokerageWithdrawal);
        removeCache(newUnionBrokerageWithdrawal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionBrokerageWithdrawal> newUnionBrokerageWithdrawalList) throws Exception {
        if (newUnionBrokerageWithdrawalList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionBrokerageWithdrawalDao.insertBatch(newUnionBrokerageWithdrawalList);
        removeCache(newUnionBrokerageWithdrawalList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionBrokerageWithdrawal unionBrokerageWithdrawal = getById(id);
        removeCache(unionBrokerageWithdrawal);
        // (2)remove in db logically
        UnionBrokerageWithdrawal removeUnionBrokerageWithdrawal = new UnionBrokerageWithdrawal();
        removeUnionBrokerageWithdrawal.setId(id);
        removeUnionBrokerageWithdrawal.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionBrokerageWithdrawalDao.updateById(removeUnionBrokerageWithdrawal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionBrokerageWithdrawal> unionBrokerageWithdrawalList = listByIdList(idList);
        removeCache(unionBrokerageWithdrawalList);
        // (2)remove in db logically
        List<UnionBrokerageWithdrawal> removeUnionBrokerageWithdrawalList = new ArrayList<>();
        for (Integer id : idList) {
            UnionBrokerageWithdrawal removeUnionBrokerageWithdrawal = new UnionBrokerageWithdrawal();
            removeUnionBrokerageWithdrawal.setId(id);
            removeUnionBrokerageWithdrawal.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionBrokerageWithdrawalList.add(removeUnionBrokerageWithdrawal);
        }
        unionBrokerageWithdrawalDao.updateBatchById(removeUnionBrokerageWithdrawalList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionBrokerageWithdrawal updateUnionBrokerageWithdrawal) throws Exception {
        if (updateUnionBrokerageWithdrawal == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionBrokerageWithdrawal.getId();
        UnionBrokerageWithdrawal unionBrokerageWithdrawal = getById(id);
        removeCache(unionBrokerageWithdrawal);
        // (2)update db
        unionBrokerageWithdrawalDao.updateById(updateUnionBrokerageWithdrawal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionBrokerageWithdrawal> updateUnionBrokerageWithdrawalList) throws Exception {
        if (updateUnionBrokerageWithdrawalList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionBrokerageWithdrawalList);
        List<UnionBrokerageWithdrawal> unionBrokerageWithdrawalList = listByIdList(idList);
        removeCache(unionBrokerageWithdrawalList);
        // (2)update db
        unionBrokerageWithdrawalDao.updateBatchById(updateUnionBrokerageWithdrawalList);
    }

    //********************************************* Object As a Service - cache support ********************************

    private void setCache(UnionBrokerageWithdrawal newUnionBrokerageWithdrawal, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionBrokerageWithdrawalCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionBrokerageWithdrawal);
    }

    private void setCache(List<UnionBrokerageWithdrawal> newUnionBrokerageWithdrawalList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionBrokerageWithdrawalCacheUtil.TYPE_BUS_ID:
                foreignIdKey = UnionBrokerageWithdrawalCacheUtil.getBusIdKey(foreignId);
                break;
            case UnionBrokerageWithdrawalCacheUtil.TYPE_VERIFIER_ID:
                foreignIdKey = UnionBrokerageWithdrawalCacheUtil.getVerifierIdKey(foreignId);
                break;

            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionBrokerageWithdrawalList);
        }
    }

    private void setValidCache(List<UnionBrokerageWithdrawal> newUnionBrokerageWithdrawalList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionBrokerageWithdrawalCacheUtil.TYPE_BUS_ID:
                validForeignIdKey = UnionBrokerageWithdrawalCacheUtil.getValidBusIdKey(foreignId);
                break;
            case UnionBrokerageWithdrawalCacheUtil.TYPE_VERIFIER_ID:
                validForeignIdKey = UnionBrokerageWithdrawalCacheUtil.getValidVerifierIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionBrokerageWithdrawalList);
        }
    }

    private void setInvalidCache(List<UnionBrokerageWithdrawal> newUnionBrokerageWithdrawalList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionBrokerageWithdrawalCacheUtil.TYPE_BUS_ID:
                invalidForeignIdKey = UnionBrokerageWithdrawalCacheUtil.getInvalidBusIdKey(foreignId);
                break;
            case UnionBrokerageWithdrawalCacheUtil.TYPE_VERIFIER_ID:
                invalidForeignIdKey = UnionBrokerageWithdrawalCacheUtil.getInvalidVerifierIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionBrokerageWithdrawalList);
        }
    }

    private void removeCache(UnionBrokerageWithdrawal unionBrokerageWithdrawal) {
        if (unionBrokerageWithdrawal == null) {
            return;
        }
        Integer id = unionBrokerageWithdrawal.getId();
        String idKey = UnionBrokerageWithdrawalCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer busId = unionBrokerageWithdrawal.getBusId();
        if (busId != null) {
            String busIdKey = UnionBrokerageWithdrawalCacheUtil.getBusIdKey(busId);
            redisCacheUtil.remove(busIdKey);

            String validBusIdKey = UnionBrokerageWithdrawalCacheUtil.getValidBusIdKey(busId);
            redisCacheUtil.remove(validBusIdKey);

            String invalidBusIdKey = UnionBrokerageWithdrawalCacheUtil.getInvalidBusIdKey(busId);
            redisCacheUtil.remove(invalidBusIdKey);
        }

        Integer verifierId = unionBrokerageWithdrawal.getVerifierId();
        if (verifierId != null) {
            String verifierIdKey = UnionBrokerageWithdrawalCacheUtil.getVerifierIdKey(verifierId);
            redisCacheUtil.remove(verifierIdKey);

            String validVerifierIdKey = UnionBrokerageWithdrawalCacheUtil.getValidVerifierIdKey(verifierId);
            redisCacheUtil.remove(validVerifierIdKey);

            String invalidVerifierIdKey = UnionBrokerageWithdrawalCacheUtil.getInvalidVerifierIdKey(verifierId);
            redisCacheUtil.remove(invalidVerifierIdKey);
        }

    }

    private void removeCache(List<UnionBrokerageWithdrawal> unionBrokerageWithdrawalList) {
        if (ListUtil.isEmpty(unionBrokerageWithdrawalList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionBrokerageWithdrawal unionBrokerageWithdrawal : unionBrokerageWithdrawalList) {
            idList.add(unionBrokerageWithdrawal.getId());
        }
        List<String> idKeyList = UnionBrokerageWithdrawalCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> busIdKeyList = getForeignIdKeyList(unionBrokerageWithdrawalList, UnionBrokerageWithdrawalCacheUtil.TYPE_BUS_ID);
        if (ListUtil.isNotEmpty(busIdKeyList)) {
            redisCacheUtil.remove(busIdKeyList);
        }

        List<String> verifierIdKeyList = getForeignIdKeyList(unionBrokerageWithdrawalList, UnionBrokerageWithdrawalCacheUtil.TYPE_VERIFIER_ID);
        if (ListUtil.isNotEmpty(verifierIdKeyList)) {
            redisCacheUtil.remove(verifierIdKeyList);
        }

    }

    private List<String> getForeignIdKeyList(List<UnionBrokerageWithdrawal> unionBrokerageWithdrawalList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionBrokerageWithdrawalCacheUtil.TYPE_BUS_ID:
                for (UnionBrokerageWithdrawal unionBrokerageWithdrawal : unionBrokerageWithdrawalList) {
                    Integer busId = unionBrokerageWithdrawal.getBusId();
                    if (busId != null) {
                        String busIdKey = UnionBrokerageWithdrawalCacheUtil.getBusIdKey(busId);
                        result.add(busIdKey);

                        String validBusIdKey = UnionBrokerageWithdrawalCacheUtil.getValidBusIdKey(busId);
                        result.add(validBusIdKey);

                        String invalidBusIdKey = UnionBrokerageWithdrawalCacheUtil.getInvalidBusIdKey(busId);
                        result.add(invalidBusIdKey);
                    }
                }
                break;
            case UnionBrokerageWithdrawalCacheUtil.TYPE_VERIFIER_ID:
                for (UnionBrokerageWithdrawal unionBrokerageWithdrawal : unionBrokerageWithdrawalList) {
                    Integer verifierId = unionBrokerageWithdrawal.getVerifierId();
                    if (verifierId != null) {
                        String verifierIdKey = UnionBrokerageWithdrawalCacheUtil.getVerifierIdKey(verifierId);
                        result.add(verifierIdKey);

                        String validVerifierIdKey = UnionBrokerageWithdrawalCacheUtil.getValidVerifierIdKey(verifierId);
                        result.add(validVerifierIdKey);

                        String invalidVerifierIdKey = UnionBrokerageWithdrawalCacheUtil.getInvalidVerifierIdKey(verifierId);
                        result.add(invalidVerifierIdKey);
                    }
                }
                break;

            default:
                break;
        }
        return result;
    }

}