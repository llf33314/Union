package com.gt.union.card.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.entity.UnionCardActivity;
import com.gt.union.card.mapper.UnionCardActivityMapper;
import com.gt.union.card.service.IUnionCardActivityService;
import com.gt.union.card.util.UnionCardActivityCacheUtil;
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
 * 联盟卡活动 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Service
public class UnionCardActivityServiceImpl extends ServiceImpl<UnionCardActivityMapper, UnionCardActivity> implements IUnionCardActivityService {
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

    public UnionCardActivity getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardActivity result;
        // (1)cache
        String idKey = UnionCardActivityCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionCardActivity.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardActivity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionCardActivity> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardActivity> result;
        // (1)cache
        String unionIdKey = UnionCardActivityCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardActivity.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardActivity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionCardActivityCacheUtil.TYPE_UNION_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardActivity newUnionCardActivity) throws Exception {
        if (newUnionCardActivity == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionCardActivity);
        removeCache(newUnionCardActivity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardActivity> newUnionCardActivityList) throws Exception {
        if (newUnionCardActivityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionCardActivityList);
        removeCache(newUnionCardActivityList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionCardActivity unionCardActivity = getById(id);
        removeCache(unionCardActivity);
        // (2)remove in db logically
        UnionCardActivity removeUnionCardActivity = new UnionCardActivity();
        removeUnionCardActivity.setId(id);
        removeUnionCardActivity.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionCardActivity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardActivity> unionCardActivityList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardActivity unionCardActivity = getById(id);
            unionCardActivityList.add(unionCardActivity);
        }
        removeCache(unionCardActivityList);
        // (2)remove in db logically
        List<UnionCardActivity> removeUnionCardActivityList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardActivity removeUnionCardActivity = new UnionCardActivity();
            removeUnionCardActivity.setId(id);
            removeUnionCardActivity.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardActivityList.add(removeUnionCardActivity);
        }
        updateBatchById(removeUnionCardActivityList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardActivity updateUnionCardActivity) throws Exception {
        if (updateUnionCardActivity == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCardActivity.getId();
        UnionCardActivity unionCardActivity = getById(id);
        removeCache(unionCardActivity);
        // (2)update db
        updateById(updateUnionCardActivity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardActivity> updateUnionCardActivityList) throws Exception {
        if (updateUnionCardActivityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionCardActivity updateUnionCardActivity : updateUnionCardActivityList) {
            idList.add(updateUnionCardActivity.getId());
        }
        List<UnionCardActivity> unionCardActivityList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardActivity unionCardActivity = getById(id);
            unionCardActivityList.add(unionCardActivity);
        }
        removeCache(unionCardActivityList);
        // (2)update db
        updateBatchById(updateUnionCardActivityList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionCardActivity newUnionCardActivity, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardActivityCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCardActivity);
    }

    private void setCache(List<UnionCardActivity> newUnionCardActivityList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionCardActivityCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionCardActivityCacheUtil.getUnionIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionCardActivityList);
        }
    }

    private void removeCache(UnionCardActivity unionCardActivity) {
        if (unionCardActivity == null) {
            return;
        }
        Integer id = unionCardActivity.getId();
        String idKey = UnionCardActivityCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer unionId = unionCardActivity.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionCardActivityCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }
    }

    private void removeCache(List<UnionCardActivity> unionCardActivityList) {
        if (ListUtil.isEmpty(unionCardActivityList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCardActivity unionCardActivity : unionCardActivityList) {
            idList.add(unionCardActivity.getId());
        }
        List<String> idKeyList = UnionCardActivityCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> unionIdKeyList = getForeignIdKeyList(unionCardActivityList, UnionCardActivityCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionCardActivity> unionCardActivityList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionCardActivityCacheUtil.TYPE_UNION_ID:
                for (UnionCardActivity unionCardActivity : unionCardActivityList) {
                    Integer unionId = unionCardActivity.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionCardActivityCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
            default:
                break;
        }
        return result;
    }
}