package com.gt.union.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.union.main.entity.UnionMainPackage;
import com.gt.union.union.main.mapper.UnionMainPackageMapper;
import com.gt.union.union.main.service.IUnionMainPackageService;
import com.gt.union.union.main.util.UnionMainPackageCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 盟主服务套餐 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 15:26:16
 */
@Service
public class UnionMainPackageServiceImpl extends ServiceImpl<UnionMainPackageMapper, UnionMainPackage> implements IUnionMainPackageService {
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

    public UnionMainPackage getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainPackage result;
        // (1)cache
        String idKey = UnionMainPackageCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionMainPackage.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainPackage> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMainPackage newUnionMainPackage) throws Exception {
        if (newUnionMainPackage == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionMainPackage);
        removeCache(newUnionMainPackage);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMainPackage> newUnionMainPackageList) throws Exception {
        if (newUnionMainPackageList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionMainPackageList);
        removeCache(newUnionMainPackageList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionMainPackage unionMainPackage = getById(id);
        removeCache(unionMainPackage);
        // (2)remove in db logically
        UnionMainPackage removeUnionMainPackage = new UnionMainPackage();
        removeUnionMainPackage.setId(id);
        removeUnionMainPackage.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionMainPackage);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMainPackage> unionMainPackageList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainPackage unionMainPackage = getById(id);
            unionMainPackageList.add(unionMainPackage);
        }
        removeCache(unionMainPackageList);
        // (2)remove in db logically
        List<UnionMainPackage> removeUnionMainPackageList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainPackage removeUnionMainPackage = new UnionMainPackage();
            removeUnionMainPackage.setId(id);
            removeUnionMainPackage.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMainPackageList.add(removeUnionMainPackage);
        }
        updateBatchById(removeUnionMainPackageList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMainPackage updateUnionMainPackage) throws Exception {
        if (updateUnionMainPackage == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionMainPackage.getId();
        UnionMainPackage unionMainPackage = getById(id);
        removeCache(unionMainPackage);
        // (2)update db
        updateById(updateUnionMainPackage);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMainPackage> updateUnionMainPackageList) throws Exception {
        if (updateUnionMainPackageList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionMainPackage updateUnionMainPackage : updateUnionMainPackageList) {
            idList.add(updateUnionMainPackage.getId());
        }
        List<UnionMainPackage> unionMainPackageList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainPackage unionMainPackage = getById(id);
            unionMainPackageList.add(unionMainPackage);
        }
        removeCache(unionMainPackageList);
        // (2)update db
        updateBatchById(updateUnionMainPackageList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionMainPackage newUnionMainPackage, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionMainPackageCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionMainPackage);
    }

    private void setCache(List<UnionMainPackage> newUnionMainPackageList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMainPackageList);
        }
    }

    private void removeCache(UnionMainPackage unionMainPackage) {
        if (unionMainPackage == null) {
            return;
        }
        Integer id = unionMainPackage.getId();
        String idKey = UnionMainPackageCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);
    }

    private void removeCache(List<UnionMainPackage> unionMainPackageList) {
        if (ListUtil.isEmpty(unionMainPackageList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionMainPackage unionMainPackage : unionMainPackageList) {
            idList.add(unionMainPackage.getId());
        }
        List<String> idKeyList = UnionMainPackageCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);
    }

    private List<String> getForeignIdKeyList(List<UnionMainPackage> unionMainPackageList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            default:
                break;
        }
        return result;
    }
}