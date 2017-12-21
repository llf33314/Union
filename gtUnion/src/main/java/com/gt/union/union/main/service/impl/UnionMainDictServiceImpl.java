package com.gt.union.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.union.main.entity.UnionMainDict;
import com.gt.union.union.main.mapper.UnionMainDictMapper;
import com.gt.union.union.main.service.IUnionMainDictService;
import com.gt.union.union.main.util.UnionMainDictCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟入盟申请必填信息 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
 */
@Service
public class UnionMainDictServiceImpl extends ServiceImpl<UnionMainDictMapper, UnionMainDict> implements IUnionMainDictService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    //***************************************** Domain Driven Design - get *********************************************

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<String> listItemKeyByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<String> result = new ArrayList<>();
        List<UnionMainDict> itemList = listByUnionId(unionId);
        if (ListUtil.isNotEmpty(itemList)) {
            for (UnionMainDict item : itemList) {
                result.add(item.getItemKey());
            }
        }
        return result;
    }

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Object As a Service - get **********************************************

    public UnionMainDict getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainDict result;
        // (1)cache
        String idKey = UnionMainDictCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionMainDict.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainDict> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    @Override
    public List<UnionMainDict> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainDict> result;
        // (1)cache
        String unionIdKey = UnionMainDictCacheUtil.getUnionInKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainDict.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainDict> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionMainDictCacheUtil.TYPE_UNION_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMainDict newUnionMainDict) throws Exception {
        if (newUnionMainDict == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionMainDict);
        removeCache(newUnionMainDict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMainDict> newUnionMainDictList) throws Exception {
        if (newUnionMainDictList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionMainDictList);
        removeCache(newUnionMainDictList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionMainDict unionMainDict = getById(id);
        removeCache(unionMainDict);
        // (2)remove in db physical
        deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMainDict> unionMainDictList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainDict unionMainDict = getById(id);
            unionMainDictList.add(unionMainDict);
        }
        removeCache(unionMainDictList);
        // (2)remove in db physical
        deleteBatchIds(idList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMainDict updateUnionMainDict) throws Exception {
        if (updateUnionMainDict == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionMainDict.getId();
        UnionMainDict unionMainDict = getById(id);
        removeCache(unionMainDict);
        // (2)update db
        updateById(updateUnionMainDict);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMainDict> updateUnionMainDictList) throws Exception {
        if (updateUnionMainDictList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionMainDict updateUnionMainDict : updateUnionMainDictList) {
            idList.add(updateUnionMainDict.getId());
        }
        List<UnionMainDict> unionMainDictList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainDict unionMainDict = getById(id);
            unionMainDictList.add(unionMainDict);
        }
        removeCache(unionMainDictList);
        // (2)update db
        updateBatchById(updateUnionMainDictList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionMainDict newUnionMainDict, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionMainDictCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionMainDict);
    }

    private void setCache(List<UnionMainDict> newUnionMainDictList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionMainDictCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionMainDictCacheUtil.getUnionInKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMainDictList);
        }
    }

    private void removeCache(UnionMainDict unionMainDict) {
        if (unionMainDict == null) {
            return;
        }
        Integer id = unionMainDict.getId();
        String idKey = UnionMainDictCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer unionId = unionMainDict.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionMainDictCacheUtil.getUnionInKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }

    }

    private void removeCache(List<UnionMainDict> unionMainDictList) {
        if (ListUtil.isEmpty(unionMainDictList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionMainDict unionMainDict : unionMainDictList) {
            idList.add(unionMainDict.getId());
        }
        List<String> idKeyList = UnionMainDictCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> unionIdKeyList = getForeignIdKeyList(unionMainDictList, UnionMainDictCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionMainDict> unionMainDictList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionMainDictCacheUtil.TYPE_UNION_ID:
                for (UnionMainDict unionMainDict : unionMainDictList) {
                    Integer unionId = unionMainDict.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionMainDictCacheUtil.getUnionInKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}