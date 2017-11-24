package com.gt.union.card.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.entity.UnionActivityFlow;
import com.gt.union.card.entity.UnionCardActivity;
import com.gt.union.card.mapper.UnionActivityFlowMapper;
import com.gt.union.card.service.IUnionActivityFlowService;
import com.gt.union.card.util.UnionActivityFlowCacheUtil;
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
 * 活动项目流程 服务实现类
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
@Service
public class UnionActivityFlowServiceImpl extends ServiceImpl<UnionActivityFlowMapper, UnionActivityFlow> implements IUnionActivityFlowService {
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

    public UnionActivityFlow getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionActivityFlow result;
        // (1)cache
        String idKey = UnionActivityFlowCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionActivityFlow.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionActivityFlow> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionActivityFlow> listByActivityProjectId(Integer activityProjectId) throws Exception {
        if (activityProjectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionActivityFlow> result;
        // (1)cache
        String activityProjectIdKey = UnionActivityFlowCacheUtil.getActivityProjectIdKey(activityProjectId);
        if (redisCacheUtil.exists(activityProjectIdKey)) {
            String tempStr = redisCacheUtil.get(activityProjectIdKey);
            result = JSONArray.parseArray(tempStr, UnionActivityFlow.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionActivityFlow> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_project_id", activityProjectId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, activityProjectId, UnionActivityFlowCacheUtil.TYPE_ACTIVITY_PROJECT_ID);
        return result;
    }
    
    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionActivityFlow newUnionActivityFlow) throws Exception {
        if (newUnionActivityFlow == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionActivityFlow);
        removeCache(newUnionActivityFlow);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionActivityFlow> newUnionActivityFlowList) throws Exception {
        if (newUnionActivityFlowList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionActivityFlowList);
        removeCache(newUnionActivityFlowList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionActivityFlow unionActivityFlow = getById(id);
        removeCache(unionActivityFlow);
        // (2)remove in db logically
        UnionActivityFlow removeUnionActivityFlow = new UnionActivityFlow();
        removeUnionActivityFlow.setId(id);
        removeUnionActivityFlow.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionActivityFlow);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionActivityFlow> unionActivityFlowList = new ArrayList<>();
        for (Integer id : idList) {
            UnionActivityFlow unionActivityFlow = getById(id);
            unionActivityFlowList.add(unionActivityFlow);
        }
        removeCache(unionActivityFlowList);
        // (2)remove in db logically
        List<UnionActivityFlow> removeUnionActivityFlowList = new ArrayList<>();
        for (Integer id : idList) {
            UnionActivityFlow removeUnionActivityFlow = new UnionActivityFlow();
            removeUnionActivityFlow.setId(id);
            removeUnionActivityFlow.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionActivityFlowList.add(removeUnionActivityFlow);
        }
        updateBatchById(removeUnionActivityFlowList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionActivityFlow updateUnionActivityFlow) throws Exception {
        if (updateUnionActivityFlow == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionActivityFlow.getId();
        UnionActivityFlow unionActivityFlow = getById(id);
        removeCache(unionActivityFlow);
        // (2)update db
        updateById(updateUnionActivityFlow);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionActivityFlow> updateUnionActivityFlowList) throws Exception {
        if (updateUnionActivityFlowList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionActivityFlow updateUnionActivityFlow : updateUnionActivityFlowList) {
            idList.add(updateUnionActivityFlow.getId());
        }
        List<UnionActivityFlow> unionActivityFlowList = new ArrayList<>();
        for (Integer id : idList) {
            UnionActivityFlow unionActivityFlow = getById(id);
            unionActivityFlowList.add(unionActivityFlow);
        }
        removeCache(unionActivityFlowList);
        // (2)update db
        updateBatchById(updateUnionActivityFlowList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionActivityFlow newUnionActivityFlow, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionActivityFlowCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionActivityFlow);
    }

    private void setCache(List<UnionActivityFlow> newUnionActivityFlowList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionActivityFlowCacheUtil.TYPE_ACTIVITY_PROJECT_ID:
                foreignIdKey = UnionActivityFlowCacheUtil.getActivityProjectIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionActivityFlowList);
        }
    }

    private void removeCache(UnionActivityFlow unionActivityFlow) {
        if (unionActivityFlow == null) {
            return;
        }
        Integer id = unionActivityFlow.getId();
        String idKey = UnionActivityFlowCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);
        
        Integer activityProjectId = unionActivityFlow.getActivityProjectId();
        if (activityProjectId != null) {
            String activityProjectIdKey = UnionActivityFlowCacheUtil.getActivityProjectIdKey(activityProjectId);
            redisCacheUtil.remove(activityProjectIdKey);
        }
    }

    private void removeCache(List<UnionActivityFlow> unionActivityFlowList) {
        if (ListUtil.isEmpty(unionActivityFlowList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionActivityFlow unionActivityFlow : unionActivityFlowList) {
            idList.add(unionActivityFlow.getId());
        }
        List<String> idKeyList = UnionActivityFlowCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);
        
        List<String> activityProjectIdKeyList = getForeignIdKeyList(unionActivityFlowList, UnionActivityFlowCacheUtil.TYPE_ACTIVITY_PROJECT_ID);
        if (ListUtil.isNotEmpty(activityProjectIdKeyList)) {
            redisCacheUtil.remove(activityProjectIdKeyList);   
        }
    }

    private List<String> getForeignIdKeyList(List<UnionActivityFlow> unionActivityFlowList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionActivityFlowCacheUtil.TYPE_ACTIVITY_PROJECT_ID:
                for (UnionActivityFlow unionActivityFlow : unionActivityFlowList) {
                    Integer activityProjectId = unionActivityFlow.getActivityProjectId();
                    if (activityProjectId != null) {
                        String activityProjectIdKey = UnionActivityFlowCacheUtil.getActivityProjectIdKey(activityProjectId);
                        result.add(activityProjectIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}