package com.gt.union.card.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.entity.UnionCardRoot;
import com.gt.union.card.mapper.UnionCardRootMapper;
import com.gt.union.card.service.IUnionCardRootService;
import com.gt.union.card.util.UnionCardRootCacheUtil;
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
 * 联盟卡根信息 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:13
 */
@Service
public class UnionCardRootServiceImpl extends ServiceImpl<UnionCardRootMapper, UnionCardRoot> implements IUnionCardRootService {
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

    public UnionCardRoot getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardRoot result;
        // (1)cache
        String idKey = UnionCardRootCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionCardRoot.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardRoot> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardRoot newUnionCardRoot) throws Exception {
        if (newUnionCardRoot == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionCardRoot);
        removeCache(newUnionCardRoot);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardRoot> newUnionCardRootList) throws Exception {
        if (newUnionCardRootList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionCardRootList);
        removeCache(newUnionCardRootList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionCardRoot unionCardRoot = getById(id);
        removeCache(unionCardRoot);
        // (2)remove in db logically
        UnionCardRoot removeUnionCardRoot = new UnionCardRoot();
        removeUnionCardRoot.setId(id);
        removeUnionCardRoot.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionCardRoot);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardRoot> unionCardRootList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardRoot unionCardRoot = getById(id);
            unionCardRootList.add(unionCardRoot);
        }
        removeCache(unionCardRootList);
        // (2)remove in db logically
        List<UnionCardRoot> removeUnionCardRootList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardRoot removeUnionCardRoot = new UnionCardRoot();
            removeUnionCardRoot.setId(id);
            removeUnionCardRoot.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardRootList.add(removeUnionCardRoot);
        }
        updateBatchById(removeUnionCardRootList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardRoot updateUnionCardRoot) throws Exception {
        if (updateUnionCardRoot == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCardRoot.getId();
        UnionCardRoot unionCardRoot = getById(id);
        removeCache(unionCardRoot);
        // (2)update db
        updateById(updateUnionCardRoot);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardRoot> updateUnionCardRootList) throws Exception {
        if (updateUnionCardRootList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionCardRoot updateUnionCardRoot : updateUnionCardRootList) {
            idList.add(updateUnionCardRoot.getId());
        }
        List<UnionCardRoot> unionCardRootList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardRoot unionCardRoot = getById(id);
            unionCardRootList.add(unionCardRoot);
        }
        removeCache(unionCardRootList);
        // (2)update db
        updateBatchById(updateUnionCardRootList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionCardRoot newUnionCardRoot, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardRootCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCardRoot);
    }

    private void setCache(List<UnionCardRoot> newUnionCardRootList, Integer foreignId, int foreignIdType) {
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
            redisCacheUtil.set(foreignIdKey, newUnionCardRootList);
        }
    }

    private void removeCache(UnionCardRoot unionCardRoot) {
        if (unionCardRoot == null) {
            return;
        }
        Integer id = unionCardRoot.getId();
        String idKey = UnionCardRootCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);
    }

    private void removeCache(List<UnionCardRoot> unionCardRootList) {
        if (ListUtil.isEmpty(unionCardRootList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCardRoot unionCardRoot : unionCardRootList) {
            idList.add(unionCardRoot.getId());
        }
        List<String> idKeyList = UnionCardRootCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);
    }

    private List<String> getForeignIdKeyList(List<UnionCardRoot> unionCardRootList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            default:
                break;
        }
        return result;
    }
}