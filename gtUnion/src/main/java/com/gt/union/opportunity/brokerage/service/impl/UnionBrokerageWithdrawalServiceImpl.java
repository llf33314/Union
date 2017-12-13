package com.gt.union.opportunity.brokerage.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageWithdrawal;
import com.gt.union.opportunity.brokerage.mapper.UnionBrokerageWithdrawalMapper;
import com.gt.union.opportunity.brokerage.service.IUnionBrokerageWithdrawalService;
import com.gt.union.opportunity.brokerage.util.UnionBrokerageWithdrawalCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 佣金提现 服务实现类
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
@Service
public class UnionBrokerageWithdrawalServiceImpl extends ServiceImpl<UnionBrokerageWithdrawalMapper, UnionBrokerageWithdrawal> implements IUnionBrokerageWithdrawalService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    //***************************************** Domain Driven Design - get *********************************************

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Object As a Service - get **********************************************

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
        EntityWrapper<UnionBrokerageWithdrawal> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

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
        entityWrapper.eq("bus_id", busId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, busId, UnionBrokerageWithdrawalCacheUtil.TYPE_BUS_ID);
        return result;
    }

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
        entityWrapper.eq("verifier_id", verifierId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, verifierId, UnionBrokerageWithdrawalCacheUtil.TYPE_VERIFIER_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionBrokerageWithdrawal newUnionBrokerageWithdrawal) throws Exception {
        if (newUnionBrokerageWithdrawal == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionBrokerageWithdrawal);
        removeCache(newUnionBrokerageWithdrawal);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionBrokerageWithdrawal> newUnionBrokerageWithdrawalList) throws Exception {
        if (newUnionBrokerageWithdrawalList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionBrokerageWithdrawalList);
        removeCache(newUnionBrokerageWithdrawalList);
    }

    //***************************************** Object As a Service - remove *******************************************

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
        updateById(removeUnionBrokerageWithdrawal);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionBrokerageWithdrawal> unionBrokerageWithdrawalList = new ArrayList<>();
        for (Integer id : idList) {
            UnionBrokerageWithdrawal unionBrokerageWithdrawal = getById(id);
            unionBrokerageWithdrawalList.add(unionBrokerageWithdrawal);
        }
        removeCache(unionBrokerageWithdrawalList);
        // (2)remove in db logically
        List<UnionBrokerageWithdrawal> removeUnionBrokerageWithdrawalList = new ArrayList<>();
        for (Integer id : idList) {
            UnionBrokerageWithdrawal removeUnionBrokerageWithdrawal = new UnionBrokerageWithdrawal();
            removeUnionBrokerageWithdrawal.setId(id);
            removeUnionBrokerageWithdrawal.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionBrokerageWithdrawalList.add(removeUnionBrokerageWithdrawal);
        }
        updateBatchById(removeUnionBrokerageWithdrawalList);
    }

    //***************************************** Object As a Service - update *******************************************

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
        updateById(updateUnionBrokerageWithdrawal);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionBrokerageWithdrawal> updateUnionBrokerageWithdrawalList) throws Exception {
        if (updateUnionBrokerageWithdrawalList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionBrokerageWithdrawal updateUnionBrokerageWithdrawal : updateUnionBrokerageWithdrawalList) {
            idList.add(updateUnionBrokerageWithdrawal.getId());
        }
        List<UnionBrokerageWithdrawal> unionBrokerageWithdrawalList = new ArrayList<>();
        for (Integer id : idList) {
            UnionBrokerageWithdrawal unionBrokerageWithdrawal = getById(id);
            unionBrokerageWithdrawalList.add(unionBrokerageWithdrawal);
        }
        removeCache(unionBrokerageWithdrawalList);
        // (2)update db
        updateBatchById(updateUnionBrokerageWithdrawalList);
    }

    //***************************************** Object As a Service - cache support ************************************

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
        }

        Integer verifierId = unionBrokerageWithdrawal.getVerifierId();
        if (verifierId != null) {
            String verifierIdKey = UnionBrokerageWithdrawalCacheUtil.getVerifierIdKey(verifierId);
            redisCacheUtil.remove(verifierIdKey);
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
                    }
                }
                break;
            case UnionBrokerageWithdrawalCacheUtil.TYPE_VERIFIER_ID:
                for (UnionBrokerageWithdrawal unionBrokerageWithdrawal : unionBrokerageWithdrawalList) {
                    Integer verifierId = unionBrokerageWithdrawal.getVerifierId();
                    if (verifierId != null) {
                        String verifierIdKey = UnionBrokerageWithdrawalCacheUtil.getVerifierIdKey(verifierId);
                        result.add(verifierIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}