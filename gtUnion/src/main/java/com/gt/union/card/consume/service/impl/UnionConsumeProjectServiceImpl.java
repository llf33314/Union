package com.gt.union.card.consume.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.consume.entity.UnionConsumeProject;
import com.gt.union.card.consume.mapper.UnionConsumeProjectMapper;
import com.gt.union.card.consume.service.IUnionConsumeProjectService;
import com.gt.union.card.consume.util.UnionConsumeProjectCacheUtil;
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
 * 消费核销项目优惠 服务实现类
 *
 * @author linweicong
 * @version 2017-11-27 10:27:29
 */
@Service
public class UnionConsumeProjectServiceImpl extends ServiceImpl<UnionConsumeProjectMapper, UnionConsumeProject> implements IUnionConsumeProjectService {
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

    public UnionConsumeProject getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionConsumeProject result;
        // (1)cache
        String idKey = UnionConsumeProjectCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionConsumeProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionConsumeProject> listByProjectItemId(Integer projectItemId) throws Exception {
        if (projectItemId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsumeProject> result;
        // (1)cache
        String projectItemIdKey = UnionConsumeProjectCacheUtil.getProjectItemIdKey(projectItemId);
        if (redisCacheUtil.exists(projectItemIdKey)) {
            String tempStr = redisCacheUtil.get(projectItemIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsumeProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("project_item_id", projectItemId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, projectItemId, UnionConsumeProjectCacheUtil.TYPE_PROJECT_ITEM_ID);
        return result;
    }

    public List<UnionConsumeProject> listByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsumeProject> result;
        // (1)cache
        String projectIdKey = UnionConsumeProjectCacheUtil.getProjectIdKey(projectId);
        if (redisCacheUtil.exists(projectIdKey)) {
            String tempStr = redisCacheUtil.get(projectIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsumeProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("project_id", projectId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, projectId, UnionConsumeProjectCacheUtil.TYPE_PROJECT_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionConsumeProject newUnionConsumeProject) throws Exception {
        if (newUnionConsumeProject == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionConsumeProject);
        removeCache(newUnionConsumeProject);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionConsumeProject> newUnionConsumeProjectList) throws Exception {
        if (newUnionConsumeProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionConsumeProjectList);
        removeCache(newUnionConsumeProjectList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionConsumeProject unionConsumeProject = getById(id);
        removeCache(unionConsumeProject);
        // (2)remove in db logically
        UnionConsumeProject removeUnionConsumeProject = new UnionConsumeProject();
        removeUnionConsumeProject.setId(id);
        removeUnionConsumeProject.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionConsumeProject);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionConsumeProject> unionConsumeProjectList = new ArrayList<>();
        for (Integer id : idList) {
            UnionConsumeProject unionConsumeProject = getById(id);
            unionConsumeProjectList.add(unionConsumeProject);
        }
        removeCache(unionConsumeProjectList);
        // (2)remove in db logically
        List<UnionConsumeProject> removeUnionConsumeProjectList = new ArrayList<>();
        for (Integer id : idList) {
            UnionConsumeProject removeUnionConsumeProject = new UnionConsumeProject();
            removeUnionConsumeProject.setId(id);
            removeUnionConsumeProject.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionConsumeProjectList.add(removeUnionConsumeProject);
        }
        updateBatchById(removeUnionConsumeProjectList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionConsumeProject updateUnionConsumeProject) throws Exception {
        if (updateUnionConsumeProject == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionConsumeProject.getId();
        UnionConsumeProject unionConsumeProject = getById(id);
        removeCache(unionConsumeProject);
        // (2)update db
        updateById(updateUnionConsumeProject);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionConsumeProject> updateUnionConsumeProjectList) throws Exception {
        if (updateUnionConsumeProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionConsumeProject updateUnionConsumeProject : updateUnionConsumeProjectList) {
            idList.add(updateUnionConsumeProject.getId());
        }
        List<UnionConsumeProject> unionConsumeProjectList = new ArrayList<>();
        for (Integer id : idList) {
            UnionConsumeProject unionConsumeProject = getById(id);
            unionConsumeProjectList.add(unionConsumeProject);
        }
        removeCache(unionConsumeProjectList);
        // (2)update db
        updateBatchById(updateUnionConsumeProjectList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionConsumeProject newUnionConsumeProject, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionConsumeProjectCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionConsumeProject);
    }

    private void setCache(List<UnionConsumeProject> newUnionConsumeProjectList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionConsumeProjectCacheUtil.TYPE_PROJECT_ITEM_ID:
                foreignIdKey = UnionConsumeProjectCacheUtil.getProjectItemIdKey(foreignId);
                break;
            case UnionConsumeProjectCacheUtil.TYPE_PROJECT_ID:
                foreignIdKey = UnionConsumeProjectCacheUtil.getProjectIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionConsumeProjectList);
        }
    }

    private void removeCache(UnionConsumeProject unionConsumeProject) {
        if (unionConsumeProject == null) {
            return;
        }
        Integer id = unionConsumeProject.getId();
        String idKey = UnionConsumeProjectCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer projectItemId = unionConsumeProject.getProjectItemId();
        if (projectItemId != null) {
            String projectItemIdKey = UnionConsumeProjectCacheUtil.getProjectItemIdKey(projectItemId);
            redisCacheUtil.remove(projectItemIdKey);
        }

        Integer projectId = unionConsumeProject.getProjectId();
        if (projectId != null) {
            String projectIdKey = UnionConsumeProjectCacheUtil.getProjectIdKey(projectId);
            redisCacheUtil.remove(projectIdKey);
        }
    }

    private void removeCache(List<UnionConsumeProject> unionConsumeProjectList) {
        if (ListUtil.isEmpty(unionConsumeProjectList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionConsumeProject unionConsumeProject : unionConsumeProjectList) {
            idList.add(unionConsumeProject.getId());
        }
        List<String> idKeyList = UnionConsumeProjectCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> projectItemIdKeyList = getForeignIdKeyList(unionConsumeProjectList, UnionConsumeProjectCacheUtil.TYPE_PROJECT_ITEM_ID);
        if (ListUtil.isNotEmpty(projectItemIdKeyList)) {
            redisCacheUtil.remove(projectItemIdKeyList);
        }

        List<String> projectIdKeyList = getForeignIdKeyList(unionConsumeProjectList, UnionConsumeProjectCacheUtil.TYPE_PROJECT_ID);
        if (ListUtil.isNotEmpty(projectIdKeyList)) {
            redisCacheUtil.remove(projectIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionConsumeProject> unionConsumeProjectList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionConsumeProjectCacheUtil.TYPE_PROJECT_ITEM_ID:
                for (UnionConsumeProject unionConsumeProject : unionConsumeProjectList) {
                    Integer projectItemId = unionConsumeProject.getProjectItemId();
                    if (projectItemId != null) {
                        String projectItemIdKey = UnionConsumeProjectCacheUtil.getProjectItemIdKey(projectItemId);
                        result.add(projectItemIdKey);
                    }
                }
                break;
            case UnionConsumeProjectCacheUtil.TYPE_PROJECT_ID:
                for (UnionConsumeProject unionConsumeProject : unionConsumeProjectList) {
                    Integer projectId = unionConsumeProject.getProjectId();
                    if (projectId != null) {
                        String projectIdKey = UnionConsumeProjectCacheUtil.getProjectIdKey(projectId);
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