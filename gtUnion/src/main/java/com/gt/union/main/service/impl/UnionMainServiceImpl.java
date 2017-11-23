package com.gt.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.mapper.UnionMainMapper;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.main.util.UnionMainCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 15:26:16
 */
@Service
public class UnionMainServiceImpl extends ServiceImpl<UnionMainMapper, UnionMain> implements IUnionMainService {
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

    public UnionMain getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMain result;
        // (1)cache
        String idKey = UnionMainCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionMain.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMain> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMain newUnionMain) throws Exception {
        if (newUnionMain == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionMain);
        removeCache(newUnionMain);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMain> newUnionMainList) throws Exception {
        if (newUnionMainList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionMainList);
        removeCache(newUnionMainList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionMain unionMain = getById(id);
        removeCache(unionMain);
        // (2)remove in db logically
        UnionMain removeUnionMain = new UnionMain();
        removeUnionMain.setId(id);
        removeUnionMain.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionMain);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMain> unionMainList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMain unionMain = getById(id);
            unionMainList.add(unionMain);
        }
        removeCache(unionMainList);
        // (2)remove in db logically
        List<UnionMain> removeUnionMainList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMain removeUnionMain = new UnionMain();
            removeUnionMain.setId(id);
            removeUnionMain.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMainList.add(removeUnionMain);
        }
        updateBatchById(removeUnionMainList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMain updateUnionMain) throws Exception {
        if (updateUnionMain == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionMain.getId();
        UnionMain unionMain = getById(id);
        removeCache(unionMain);
        // (2)update db
        updateById(updateUnionMain);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMain> updateUnionMainList) throws Exception {
        if (updateUnionMainList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionMain updateUnionMain : updateUnionMainList) {
            idList.add(updateUnionMain.getId());
        }
        List<UnionMain> unionMainList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMain unionMain = getById(id);
            unionMainList.add(unionMain);
        }
        removeCache(unionMainList);
        // (2)update db
        updateBatchById(updateUnionMainList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionMain newUnionMain, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionMainCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionMain);
    }

    private void setCache(List<UnionMain> newUnionMainList, Integer foreignId, int foreignIdType) {
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
            redisCacheUtil.set(foreignIdKey, newUnionMainList);
        }
    }

    private void removeCache(UnionMain unionMain) {
        if (unionMain == null) {
            return;
        }
        Integer id = unionMain.getId();
        String idKey = UnionMainCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);
    }

    private void removeCache(List<UnionMain> unionMainList) {
        if (ListUtil.isEmpty(unionMainList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionMain unionMain : unionMainList) {
            idList.add(unionMain.getId());
        }
        List<String> idKeyList = UnionMainCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);
    }

    private List<String> getForeignIdKeyList(List<UnionMain> unionMainList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            default:
                break;
        }
        return result;
    }
}