package com.gt.union.brokerage.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.brokerage.entity.UnionBrokeragePay;
import com.gt.union.brokerage.mapper.UnionBrokeragePayMapper;
import com.gt.union.brokerage.service.IUnionBrokeragePayService;
import com.gt.union.brokerage.util.UnionBrokeragePayCacheUtil;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 佣金支出 服务实现类
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
@Service
public class UnionBrokeragePayServiceImpl extends ServiceImpl<UnionBrokeragePayMapper, UnionBrokeragePay> implements IUnionBrokeragePayService {
    @Autowired
    public RedisCacheUtil redisCacheUtil;

    //***************************************** Domain Driven Design - get *********************************************

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Object As a Service - get **********************************************

    public UnionBrokeragePay getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionBrokeragePay result;
        // (1)cache
        String idKey = UnionBrokeragePayCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    List<UnionBrokeragePay> listByFromBusId(Integer fromBusId) throws Exception {
        if (fromBusId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String fromBusIdKey = UnionBrokeragePayCacheUtil.getFromBusIdKey(fromBusId);
        if (redisCacheUtil.exists(fromBusIdKey)) {
            String tempStr = redisCacheUtil.get(fromBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("from_bus_id", fromBusId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, fromBusId, UnionBrokeragePayCacheUtil.TYPE_FROM_BUS_ID);
        return result;
    }

    List<UnionBrokeragePay> listByToBusId(Integer toBusId) throws Exception {
        if (toBusId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String toBusIdKey = UnionBrokeragePayCacheUtil.getToBusIdKey(toBusId);
        if (redisCacheUtil.exists(toBusIdKey)) {
            String tempStr = redisCacheUtil.get(toBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("to_bus_id", toBusId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, toBusId, UnionBrokeragePayCacheUtil.TYPE_TO_BUS_ID);
        return result;
    }

    List<UnionBrokeragePay> listByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String fromMemberIdKey = UnionBrokeragePayCacheUtil.getFromMemberIdKey(fromMemberId);
        if (redisCacheUtil.exists(fromMemberIdKey)) {
            String tempStr = redisCacheUtil.get(fromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("from_member_id", fromMemberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, fromMemberId, UnionBrokeragePayCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

    List<UnionBrokeragePay> listByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String toMemberIdKey = UnionBrokeragePayCacheUtil.getToMemberIdKey(toMemberId);
        if (redisCacheUtil.exists(toMemberIdKey)) {
            String tempStr = redisCacheUtil.get(toMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("to_member_id", toMemberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, toMemberId, UnionBrokeragePayCacheUtil.TYPE_TO_MEMBER_ID);
        return result;
    }

    List<UnionBrokeragePay> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String unionIdKey = UnionBrokeragePayCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionBrokeragePayCacheUtil.TYPE_UNION_ID);
        return result;
    }

    List<UnionBrokeragePay> listByOpportunityId(Integer opportunityId) throws Exception {
        if (opportunityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String opportunityIdKey = UnionBrokeragePayCacheUtil.getOpportunityIdKey(opportunityId);
        if (redisCacheUtil.exists(opportunityIdKey)) {
            String tempStr = redisCacheUtil.get(opportunityIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("opportunity_id", opportunityId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, opportunityId, UnionBrokeragePayCacheUtil.TYPE_OPPORTUNITY_ID);
        return result;
    }

    List<UnionBrokeragePay> listByVerifierId(Integer verifierId) throws Exception {
        if (verifierId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionBrokeragePay> result;
        // (1)cache
        String verifierIdKey = UnionBrokeragePayCacheUtil.getVerifierIdKey(verifierId);
        if (redisCacheUtil.exists(verifierIdKey)) {
            String tempStr = redisCacheUtil.get(verifierIdKey);
            result = JSONArray.parseArray(tempStr, UnionBrokeragePay.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionBrokeragePay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("verifier_id", verifierId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, verifierId, UnionBrokeragePayCacheUtil.TYPE_VERIFIER_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionBrokeragePay newUnionBrokeragePay) throws Exception {
        if (newUnionBrokeragePay == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionBrokeragePay);
        removeCache(newUnionBrokeragePay);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionBrokeragePay> newUnionBrokeragePayList) throws Exception {
        if (newUnionBrokeragePayList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionBrokeragePayList);
        removeCache(newUnionBrokeragePayList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionBrokeragePay unionBrokeragePay = getById(id);
        removeCache(unionBrokeragePay);
        // (2)remove in db logically
        UnionBrokeragePay removeUnionBrokeragePay = new UnionBrokeragePay();
        removeUnionBrokeragePay.setId(id);
        removeUnionBrokeragePay.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionBrokeragePay);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionBrokeragePay> unionBrokeragePayList = new ArrayList<>();
        for (Integer id : idList) {
            UnionBrokeragePay unionBrokeragePay = getById(id);
            unionBrokeragePayList.add(unionBrokeragePay);
        }
        removeCache(unionBrokeragePayList);
        // (2)remove in db logically
        List<UnionBrokeragePay> removeUnionBrokeragePayList = new ArrayList<>();
        for (Integer id : idList) {
            UnionBrokeragePay removeUnionBrokeragePay = new UnionBrokeragePay();
            removeUnionBrokeragePay.setId(id);
            removeUnionBrokeragePay.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionBrokeragePayList.add(removeUnionBrokeragePay);
        }
        updateBatchById(removeUnionBrokeragePayList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionBrokeragePay updateUnionBrokeragePay) throws Exception {
        if (updateUnionBrokeragePay == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionBrokeragePay.getId();
        UnionBrokeragePay unionBrokeragePay = getById(id);
        removeCache(unionBrokeragePay);
        // (2)update db
        updateById(updateUnionBrokeragePay);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionBrokeragePay> updateUnionBrokeragePayList) throws Exception {
        if (updateUnionBrokeragePayList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionBrokeragePay updateUnionBrokeragePay : updateUnionBrokeragePayList) {
            idList.add(updateUnionBrokeragePay.getId());
        }
        List<UnionBrokeragePay> unionBrokeragePayList = new ArrayList<>();
        for (Integer id : idList) {
            UnionBrokeragePay unionBrokeragePay = getById(id);
            unionBrokeragePayList.add(unionBrokeragePay);
        }
        removeCache(unionBrokeragePayList);
        // (2)update db
        updateBatchById(updateUnionBrokeragePayList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionBrokeragePay newUnionBrokeragePay, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionBrokeragePayCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionBrokeragePay);
    }

    private void setCache(List<UnionBrokeragePay> newUnionBrokeragePayList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionBrokeragePayCacheUtil.TYPE_FROM_BUS_ID:
                foreignIdKey = UnionBrokeragePayCacheUtil.getFromBusIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_TO_BUS_ID:
                foreignIdKey = UnionBrokeragePayCacheUtil.getToBusIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_FROM_MEMBER_ID:
                foreignIdKey = UnionBrokeragePayCacheUtil.getFromMemberIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_TO_MEMBER_ID:
                foreignIdKey = UnionBrokeragePayCacheUtil.getToMemberIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionBrokeragePayCacheUtil.getUnionIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_OPPORTUNITY_ID:
                foreignIdKey = UnionBrokeragePayCacheUtil.getOpportunityIdKey(foreignId);
                break;
            case UnionBrokeragePayCacheUtil.TYPE_VERIFIER_ID:
                foreignIdKey = UnionBrokeragePayCacheUtil.getVerifierIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionBrokeragePayList);
        }
    }

    private void removeCache(UnionBrokeragePay unionBrokeragePay) {
        if (unionBrokeragePay == null) {
            return;
        }
        Integer id = unionBrokeragePay.getId();
        String idKey = UnionBrokeragePayCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);
    }

    private void removeCache(List<UnionBrokeragePay> unionBrokeragePayList) {
        if (ListUtil.isEmpty(unionBrokeragePayList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
            idList.add(unionBrokeragePay.getId());
        }
        List<String> idKeyList = UnionBrokeragePayCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> fromBusIdKeyList = getForeignIdKeyList(unionBrokeragePayList, UnionBrokeragePayCacheUtil.TYPE_FROM_BUS_ID);
        if (ListUtil.isNotEmpty(fromBusIdKeyList)) {
            redisCacheUtil.remove(fromBusIdKeyList);
        }

        List<String> toBusIdKeyList = getForeignIdKeyList(unionBrokeragePayList, UnionBrokeragePayCacheUtil.TYPE_TO_BUS_ID);
        if (ListUtil.isNotEmpty(toBusIdKeyList)) {
            redisCacheUtil.remove(toBusIdKeyList);
        }

        List<String> fromMemberIdKeyList = getForeignIdKeyList(unionBrokeragePayList, UnionBrokeragePayCacheUtil.TYPE_FROM_MEMBER_ID);
        if (ListUtil.isNotEmpty(fromMemberIdKeyList)) {
            redisCacheUtil.remove(fromMemberIdKeyList);
        }

        List<String> toMemberIdKeyList = getForeignIdKeyList(unionBrokeragePayList, UnionBrokeragePayCacheUtil.TYPE_TO_MEMBER_ID);
        if (ListUtil.isNotEmpty(toMemberIdKeyList)) {
            redisCacheUtil.remove(toMemberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionBrokeragePayList, UnionBrokeragePayCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

        List<String> opportunityIdKeyList = getForeignIdKeyList(unionBrokeragePayList, UnionBrokeragePayCacheUtil.TYPE_OPPORTUNITY_ID);
        if (ListUtil.isNotEmpty(opportunityIdKeyList)) {
            redisCacheUtil.remove(opportunityIdKeyList);
        }

        List<String> verifierIdKeyList = getForeignIdKeyList(unionBrokeragePayList, UnionBrokeragePayCacheUtil.TYPE_VERIFIER_ID);
        if (ListUtil.isNotEmpty(verifierIdKeyList)) {
            redisCacheUtil.remove(verifierIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionBrokeragePay> unionBrokeragePayList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionBrokeragePayCacheUtil.TYPE_FROM_BUS_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer fromBusId = unionBrokeragePay.getFromBusId();
                    if (fromBusId != null) {
                        String fromBusIdKey = UnionBrokeragePayCacheUtil.getFromBusIdKey(fromBusId);
                        result.add(fromBusIdKey);
                    }
                }
                break;
            case UnionBrokeragePayCacheUtil.TYPE_TO_BUS_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer toBusId = unionBrokeragePay.getToBusId();
                    if (toBusId != null) {
                        String toBusIdKey = UnionBrokeragePayCacheUtil.getToBusIdKey(toBusId);
                        result.add(toBusIdKey);
                    }
                }
                break;
            case UnionBrokeragePayCacheUtil.TYPE_FROM_MEMBER_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer fromMemberId = unionBrokeragePay.getFromMemberId();
                    if (fromMemberId != null) {
                        String fromMemberIdKey = UnionBrokeragePayCacheUtil.getFromMemberIdKey(fromMemberId);
                        result.add(fromMemberIdKey);
                    }
                }
                break;
            case UnionBrokeragePayCacheUtil.TYPE_TO_MEMBER_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer toMemberId = unionBrokeragePay.getToMemberId();
                    if (toMemberId != null) {
                        String toMemberIdKey = UnionBrokeragePayCacheUtil.getToMemberIdKey(toMemberId);
                        result.add(toMemberIdKey);
                    }
                }
                break;
            case UnionBrokeragePayCacheUtil.TYPE_UNION_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer unionId = unionBrokeragePay.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionBrokeragePayCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            case UnionBrokeragePayCacheUtil.TYPE_OPPORTUNITY_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer opportunityId = unionBrokeragePay.getOpportunityId();
                    if (opportunityId != null) {
                        String opportunityIdKey = UnionBrokeragePayCacheUtil.getOpportunityIdKey(opportunityId);
                        result.add(opportunityIdKey);
                    }
                }
                break;
            case UnionBrokeragePayCacheUtil.TYPE_VERIFIER_ID:
                for (UnionBrokeragePay unionBrokeragePay : unionBrokeragePayList) {
                    Integer verifierId = unionBrokeragePay.getVerifierId();
                    if (verifierId != null) {
                        String verifierIdKey = UnionBrokeragePayCacheUtil.getVerifierIdKey(verifierId);
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