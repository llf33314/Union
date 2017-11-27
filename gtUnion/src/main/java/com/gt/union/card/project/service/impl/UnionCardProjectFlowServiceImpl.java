package com.gt.union.card.project.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.project.entity.UnionCardProjectFlow;
import com.gt.union.card.project.mapper.UnionCardProjectFlowMapper;
import com.gt.union.card.project.service.IUnionCardProjectFlowService;
import com.gt.union.card.project.util.UnionCardProjectFlowCacheUtil;
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
 * 项目流程 服务实现类
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
@Service
public class UnionCardProjectFlowServiceImpl extends ServiceImpl<UnionCardProjectFlowMapper, UnionCardProjectFlow> implements IUnionCardProjectFlowService {
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

    public UnionCardProjectFlow getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardProjectFlow result;
        // (1)cache
        String idKey = UnionCardProjectFlowCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionCardProjectFlow.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProjectFlow> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionCardProjectFlow> listByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardProjectFlow> result;
        // (1)cache
        String projectIdKey = UnionCardProjectFlowCacheUtil.getProjectIdKey(projectId);
        if (redisCacheUtil.exists(projectIdKey)) {
            String tempStr = redisCacheUtil.get(projectIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardProjectFlow.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProjectFlow> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("project_id", projectId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, projectId, UnionCardProjectFlowCacheUtil.TYPE_PROJECT_ID);
        return result;
    }
    
    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardProjectFlow newUnionCardProjectFlow) throws Exception {
        if (newUnionCardProjectFlow == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionCardProjectFlow);
        removeCache(newUnionCardProjectFlow);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardProjectFlow> newUnionCardProjectFlowList) throws Exception {
        if (newUnionCardProjectFlowList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionCardProjectFlowList);
        removeCache(newUnionCardProjectFlowList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionCardProjectFlow unionCardProjectFlow = getById(id);
        removeCache(unionCardProjectFlow);
        // (2)remove in db logically
        UnionCardProjectFlow removeUnionCardProjectFlow = new UnionCardProjectFlow();
        removeUnionCardProjectFlow.setId(id);
        removeUnionCardProjectFlow.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionCardProjectFlow);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardProjectFlow> unionCardProjectFlowList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProjectFlow unionCardProjectFlow = getById(id);
            unionCardProjectFlowList.add(unionCardProjectFlow);
        }
        removeCache(unionCardProjectFlowList);
        // (2)remove in db logically
        List<UnionCardProjectFlow> removeUnionCardProjectFlowList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProjectFlow removeUnionCardProjectFlow = new UnionCardProjectFlow();
            removeUnionCardProjectFlow.setId(id);
            removeUnionCardProjectFlow.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardProjectFlowList.add(removeUnionCardProjectFlow);
        }
        updateBatchById(removeUnionCardProjectFlowList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardProjectFlow updateUnionCardProjectFlow) throws Exception {
        if (updateUnionCardProjectFlow == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCardProjectFlow.getId();
        UnionCardProjectFlow unionCardProjectFlow = getById(id);
        removeCache(unionCardProjectFlow);
        // (2)update db
        updateById(updateUnionCardProjectFlow);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardProjectFlow> updateUnionCardProjectFlowList) throws Exception {
        if (updateUnionCardProjectFlowList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionCardProjectFlow updateUnionCardProjectFlow : updateUnionCardProjectFlowList) {
            idList.add(updateUnionCardProjectFlow.getId());
        }
        List<UnionCardProjectFlow> unionCardProjectFlowList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProjectFlow unionCardProjectFlow = getById(id);
            unionCardProjectFlowList.add(unionCardProjectFlow);
        }
        removeCache(unionCardProjectFlowList);
        // (2)update db
        updateBatchById(updateUnionCardProjectFlowList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionCardProjectFlow newUnionCardProjectFlow, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardProjectFlowCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCardProjectFlow);
    }

    private void setCache(List<UnionCardProjectFlow> newUnionCardProjectFlowList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionCardProjectFlowCacheUtil.TYPE_PROJECT_ID:
                foreignIdKey = UnionCardProjectFlowCacheUtil.getProjectIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionCardProjectFlowList);
        }
    }

    private void removeCache(UnionCardProjectFlow unionCardProjectFlow) {
        if (unionCardProjectFlow == null) {
            return;
        }
        Integer id = unionCardProjectFlow.getId();
        String idKey = UnionCardProjectFlowCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);
        
        Integer projectId = unionCardProjectFlow.getProjectId();
        if (projectId != null) {
            String projectIdKey = UnionCardProjectFlowCacheUtil.getProjectIdKey(projectId);
            redisCacheUtil.remove(projectIdKey);
        }
    }

    private void removeCache(List<UnionCardProjectFlow> unionCardProjectFlowList) {
        if (ListUtil.isEmpty(unionCardProjectFlowList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCardProjectFlow unionCardProjectFlow : unionCardProjectFlowList) {
            idList.add(unionCardProjectFlow.getId());
        }
        List<String> idKeyList = UnionCardProjectFlowCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);
        
        List<String> projectIdKeyList = getForeignIdKeyList(unionCardProjectFlowList, UnionCardProjectFlowCacheUtil.TYPE_PROJECT_ID);
        if (ListUtil.isNotEmpty(projectIdKeyList)) {
            redisCacheUtil.remove(projectIdKeyList);   
        }
    }

    private List<String> getForeignIdKeyList(List<UnionCardProjectFlow> unionCardProjectFlowList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionCardProjectFlowCacheUtil.TYPE_PROJECT_ID:
                for (UnionCardProjectFlow unionCardProjectFlow : unionCardProjectFlowList) {
                    Integer projectId = unionCardProjectFlow.getProjectId();
                    if (projectId != null) {
                        String projectIdKey = UnionCardProjectFlowCacheUtil.getProjectIdKey(projectId);
                        result.add(projectIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}