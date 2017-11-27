package com.gt.union.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.union.main.entity.UnionMainCreate;
import com.gt.union.union.main.mapper.UnionMainCreateMapper;
import com.gt.union.union.main.service.IUnionMainCreateService;
import com.gt.union.union.main.util.UnionMainCreateCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟创建 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
 */
@Service
public class UnionMainCreateServiceImpl extends ServiceImpl<UnionMainCreateMapper, UnionMainCreate> implements IUnionMainCreateService {
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

    public UnionMainCreate getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainCreate result;
        // (1)cache
        String idKey = UnionMainCreateCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionMainCreate.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainCreate> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionMainCreate> listByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainCreate> result;
        // (1)cache
        String busIdKey = UnionMainCreateCacheUtil.getBusIdKey(busId);
        if (redisCacheUtil.exists(busIdKey)) {
            String tempStr = redisCacheUtil.get(busIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainCreate.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainCreate> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("bus_id", busId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, busId, UnionMainCreateCacheUtil.TYPE_BUS_ID);
        return result;
    }

    public List<UnionMainCreate> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainCreate> result;
        // (1)cache
        String unionIdKey = UnionMainCreateCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainCreate.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainCreate> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionMainCreateCacheUtil.TYPE_UNION_ID);
        return result;
    }

    public List<UnionMainCreate> listByPermitId(Integer permitId) throws Exception {
        if (permitId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainCreate> result;
        // (1)cache
        String permitIdKey = UnionMainCreateCacheUtil.getPermitIdKey(permitId);
        if (redisCacheUtil.exists(permitIdKey)) {
            String tempStr = redisCacheUtil.get(permitIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainCreate.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainCreate> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("permit_id", permitId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, permitId, UnionMainCreateCacheUtil.TYPE_PERMIT_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMainCreate newUnionMainCreate) throws Exception {
        if (newUnionMainCreate == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionMainCreate);
        removeCache(newUnionMainCreate);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMainCreate> newUnionMainCreateList) throws Exception {
        if (newUnionMainCreateList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionMainCreateList);
        removeCache(newUnionMainCreateList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionMainCreate unionMainCreate = getById(id);
        removeCache(unionMainCreate);
        // (2)remove in db logically
        UnionMainCreate removeUnionMainCreate = new UnionMainCreate();
        removeUnionMainCreate.setId(id);
        removeUnionMainCreate.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionMainCreate);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMainCreate> unionMainCreateList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainCreate unionMainCreate = getById(id);
            unionMainCreateList.add(unionMainCreate);
        }
        removeCache(unionMainCreateList);
        // (2)remove in db logically
        List<UnionMainCreate> removeUnionMainCreateList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainCreate removeUnionMainCreate = new UnionMainCreate();
            removeUnionMainCreate.setId(id);
            removeUnionMainCreate.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMainCreateList.add(removeUnionMainCreate);
        }
        updateBatchById(removeUnionMainCreateList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMainCreate updateUnionMainCreate) throws Exception {
        if (updateUnionMainCreate == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionMainCreate.getId();
        UnionMainCreate unionMainCreate = getById(id);
        removeCache(unionMainCreate);
        // (2)update db
        updateById(updateUnionMainCreate);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMainCreate> updateUnionMainCreateList) throws Exception {
        if (updateUnionMainCreateList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionMainCreate updateUnionMainCreate : updateUnionMainCreateList) {
            idList.add(updateUnionMainCreate.getId());
        }
        List<UnionMainCreate> unionMainCreateList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainCreate unionMainCreate = getById(id);
            unionMainCreateList.add(unionMainCreate);
        }
        removeCache(unionMainCreateList);
        // (2)update db
        updateBatchById(updateUnionMainCreateList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionMainCreate newUnionMainCreate, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionMainCreateCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionMainCreate);
    }

    private void setCache(List<UnionMainCreate> newUnionMainCreateList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionMainCreateCacheUtil.TYPE_BUS_ID:
                foreignIdKey = UnionMainCreateCacheUtil.getBusIdKey(foreignId);
                break;
            case UnionMainCreateCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionMainCreateCacheUtil.getUnionIdKey(foreignId);
                break;
            case UnionMainCreateCacheUtil.TYPE_PERMIT_ID:
                foreignIdKey = UnionMainCreateCacheUtil.getPermitIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMainCreateList);
        }
    }

    private void removeCache(UnionMainCreate unionMainCreate) {
        if (unionMainCreate == null) {
            return;
        }
        Integer id = unionMainCreate.getId();
        String idKey = UnionMainCreateCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer busId = unionMainCreate.getBusId();
        if (busId != null) {
            String busIdKey = UnionMainCreateCacheUtil.getBusIdKey(busId);
            redisCacheUtil.remove(busIdKey);
        }

        Integer unionId = unionMainCreate.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionMainCreateCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }

        Integer permitId = unionMainCreate.getPermitId();
        if (permitId != null) {
            String permitIdKey = UnionMainCreateCacheUtil.getPermitIdKey(permitId);
            redisCacheUtil.remove(permitIdKey);
        }
    }

    private void removeCache(List<UnionMainCreate> unionMainCreateList) {
        if (ListUtil.isEmpty(unionMainCreateList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionMainCreate unionMainCreate : unionMainCreateList) {
            idList.add(unionMainCreate.getId());
        }
        List<String> idKeyList = UnionMainCreateCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> busIdKeyList = getForeignIdKeyList(unionMainCreateList, UnionMainCreateCacheUtil.TYPE_BUS_ID);
        if (ListUtil.isNotEmpty(busIdKeyList)) {
            redisCacheUtil.remove(busIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionMainCreateList, UnionMainCreateCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

        List<String> permitIdKeyList = getForeignIdKeyList(unionMainCreateList, UnionMainCreateCacheUtil.TYPE_PERMIT_ID);
        if (ListUtil.isNotEmpty(permitIdKeyList)) {
            redisCacheUtil.remove(permitIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionMainCreate> unionMainCreateList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionMainCreateCacheUtil.TYPE_BUS_ID:
                for (UnionMainCreate unionMainCreate : unionMainCreateList) {
                    Integer busId = unionMainCreate.getBusId();
                    if (busId != null) {
                        String busIdKey = UnionMainCreateCacheUtil.getBusIdKey(busId);
                        result.add(busIdKey);
                    }
                }
                break;
            case UnionMainCreateCacheUtil.TYPE_UNION_ID:
                for (UnionMainCreate unionMainCreate : unionMainCreateList) {
                    Integer unionId = unionMainCreate.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionMainCreateCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            case UnionMainCreateCacheUtil.TYPE_PERMIT_ID:
                for (UnionMainCreate unionMainCreate : unionMainCreateList) {
                    Integer permitId = unionMainCreate.getPermitId();
                    if (permitId != null) {
                        String permitIdKey = UnionMainCreateCacheUtil.getPermitIdKey(permitId);
                        result.add(permitIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}