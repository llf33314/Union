package com.gt.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.main.entity.UnionMainPermit;
import com.gt.union.main.mapper.UnionMainPermitMapper;
import com.gt.union.main.service.IUnionMainPermitService;
import com.gt.union.main.util.UnionMainPermitCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟许可 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 15:26:19
 */
@Service
public class UnionMainPermitServiceImpl extends ServiceImpl<UnionMainPermitMapper, UnionMainPermit> implements IUnionMainPermitService {
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