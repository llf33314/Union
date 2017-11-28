package com.gt.union.finance.verifier.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.finance.verifier.entity.UnionVerifier;
import com.gt.union.finance.verifier.mapper.UnionVerifierMapper;
import com.gt.union.finance.verifier.service.IUnionVerifierService;
import com.gt.union.finance.verifier.util.UnionVerifierCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 平台管理者 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 14:54:27
 */
@Service
public class UnionVerifierServiceImpl extends ServiceImpl<UnionVerifierMapper, UnionVerifier> implements IUnionVerifierService {
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

    public UnionVerifier getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionVerifier result;
        // (1)cache
        String idKey = UnionVerifierCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionVerifier.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionVerifier> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionVerifier> listByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionVerifier> result;
        // (1)cache
        String busIdKey = UnionVerifierCacheUtil.getBusIdKey(busId);
        if (redisCacheUtil.exists(busIdKey)) {
            String tempStr = redisCacheUtil.get(busIdKey);
            result = JSONArray.parseArray(tempStr, UnionVerifier.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionVerifier> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("bus_id", busId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, busId, UnionVerifierCacheUtil.TYPE_BUS_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionVerifier newUnionVerifier) throws Exception {
        if (newUnionVerifier == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionVerifier);
        removeCache(newUnionVerifier);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionVerifier> newUnionVerifierList) throws Exception {
        if (newUnionVerifierList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionVerifierList);
        removeCache(newUnionVerifierList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionVerifier unionVerifier = getById(id);
        removeCache(unionVerifier);
        // (2)remove in db logically
        UnionVerifier removeUnionVerifier = new UnionVerifier();
        removeUnionVerifier.setId(id);
        removeUnionVerifier.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionVerifier);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionVerifier> unionVerifierList = new ArrayList<>();
        for (Integer id : idList) {
            UnionVerifier unionVerifier = getById(id);
            unionVerifierList.add(unionVerifier);
        }
        removeCache(unionVerifierList);
        // (2)remove in db logically
        List<UnionVerifier> removeUnionVerifierList = new ArrayList<>();
        for (Integer id : idList) {
            UnionVerifier removeUnionVerifier = new UnionVerifier();
            removeUnionVerifier.setId(id);
            removeUnionVerifier.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionVerifierList.add(removeUnionVerifier);
        }
        updateBatchById(removeUnionVerifierList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionVerifier updateUnionVerifier) throws Exception {
        if (updateUnionVerifier == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionVerifier.getId();
        UnionVerifier unionVerifier = getById(id);
        removeCache(unionVerifier);
        // (2)update db
        updateById(updateUnionVerifier);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionVerifier> updateUnionVerifierList) throws Exception {
        if (updateUnionVerifierList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionVerifier updateUnionVerifier : updateUnionVerifierList) {
            idList.add(updateUnionVerifier.getId());
        }
        List<UnionVerifier> unionVerifierList = new ArrayList<>();
        for (Integer id : idList) {
            UnionVerifier unionVerifier = getById(id);
            unionVerifierList.add(unionVerifier);
        }

        removeCache(unionVerifierList);

        // (2)update db
        updateBatchById(updateUnionVerifierList);

    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionVerifier newUnionVerifier, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionVerifierCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionVerifier);
    }

    private void setCache(List<UnionVerifier> newUnionVerifierList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionVerifierCacheUtil.TYPE_BUS_ID:
                foreignIdKey = UnionVerifierCacheUtil.getBusIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionVerifierList);
        }
    }

    private void removeCache(UnionVerifier unionVerifier) {
        if (unionVerifier == null) {
            return;
        }
        Integer id = unionVerifier.getId();
        String idKey = UnionVerifierCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer busId = unionVerifier.getBusId();
        if (busId != null) {
            String busIdKey = UnionVerifierCacheUtil.getBusIdKey(busId);
            redisCacheUtil.remove(busIdKey);
        }
    }

    private void removeCache(List<UnionVerifier> unionVerifierList) {
        if (ListUtil.isEmpty(unionVerifierList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionVerifier unionVerifier : unionVerifierList) {
            idList.add(unionVerifier.getId());
        }
        List<String> idKeyList = UnionVerifierCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> busIdKeyList = getForeignIdKeyList(unionVerifierList, UnionVerifierCacheUtil.TYPE_BUS_ID);
        if (ListUtil.isNotEmpty(busIdKeyList)) {
            redisCacheUtil.remove(busIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionVerifier> unionVerifierList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionVerifierCacheUtil.TYPE_BUS_ID:
                for (UnionVerifier unionVerifier : unionVerifierList) {
                    Integer busId = unionVerifier.getBusId();
                    if (busId != null) {
                        String busIdKey = UnionVerifierCacheUtil.getBusIdKey(busId);
                        result.add(busIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}