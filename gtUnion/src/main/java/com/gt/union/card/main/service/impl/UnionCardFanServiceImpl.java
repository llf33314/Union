package com.gt.union.card.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.mapper.UnionCardFanMapper;
import com.gt.union.card.main.service.IUnionCardFanService;
import com.gt.union.card.main.util.UnionCardFanCacheUtil;
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
public class UnionCardFanServiceImpl extends ServiceImpl<UnionCardFanMapper, UnionCardFan> implements IUnionCardFanService {
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

    public UnionCardFan getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardFan result;
        // (1)cache
        String idKey = UnionCardFanCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionCardFan.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardFan> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardFan newUnionCardRoot) throws Exception {
        if (newUnionCardRoot == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionCardRoot);
        removeCache(newUnionCardRoot);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardFan> newUnionCardRootList) throws Exception {
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
        UnionCardFan unionCardRoot = getById(id);
        removeCache(unionCardRoot);
        // (2)remove in db logically
        UnionCardFan removeUnionCardRoot = new UnionCardFan();
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
        List<UnionCardFan> unionCardRootList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardFan unionCardRoot = getById(id);
            unionCardRootList.add(unionCardRoot);
        }
        removeCache(unionCardRootList);
        // (2)remove in db logically
        List<UnionCardFan> removeUnionCardRootList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardFan removeUnionCardRoot = new UnionCardFan();
            removeUnionCardRoot.setId(id);
            removeUnionCardRoot.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardRootList.add(removeUnionCardRoot);
        }
        updateBatchById(removeUnionCardRootList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardFan updateUnionCardRoot) throws Exception {
        if (updateUnionCardRoot == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCardRoot.getId();
        UnionCardFan unionCardRoot = getById(id);
        removeCache(unionCardRoot);
        // (2)update db
        updateById(updateUnionCardRoot);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardFan> updateUnionCardRootList) throws Exception {
        if (updateUnionCardRootList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionCardFan updateUnionCardRoot : updateUnionCardRootList) {
            idList.add(updateUnionCardRoot.getId());
        }
        List<UnionCardFan> unionCardRootList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardFan unionCardRoot = getById(id);
            unionCardRootList.add(unionCardRoot);
        }
        removeCache(unionCardRootList);
        // (2)update db
        updateBatchById(updateUnionCardRootList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionCardFan newUnionCardRoot, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardFanCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCardRoot);
    }

    private void setCache(List<UnionCardFan> newUnionCardRootList, Integer foreignId, int foreignIdType) {
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

    private void removeCache(UnionCardFan unionCardRoot) {
        if (unionCardRoot == null) {
            return;
        }
        Integer id = unionCardRoot.getId();
        String idKey = UnionCardFanCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);
    }

    private void removeCache(List<UnionCardFan> unionCardRootList) {
        if (ListUtil.isEmpty(unionCardRootList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCardFan unionCardRoot : unionCardRootList) {
            idList.add(unionCardRoot.getId());
        }
        List<String> idKeyList = UnionCardFanCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);
    }

    private List<String> getForeignIdKeyList(List<UnionCardFan> unionCardRootList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            default:
                break;
        }
        return result;
    }
}