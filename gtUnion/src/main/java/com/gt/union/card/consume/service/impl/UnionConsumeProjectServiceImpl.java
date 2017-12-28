package com.gt.union.card.consume.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.consume.dao.IUnionConsumeProjectDao;
import com.gt.union.card.consume.entity.UnionConsumeProject;
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
public class UnionConsumeProjectServiceImpl implements IUnionConsumeProjectService {
    @Autowired
    private IUnionConsumeProjectDao unionConsumeProjectDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************


    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    @Override
    public Integer countValidByProjectIdAndProjectItemId(Integer projectId, Integer projectItemId) throws Exception {
        if (projectId == null || projectItemId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .eq("project_id", projectId)
                .eq("project_item_id", projectItemId);

        return unionConsumeProjectDao.selectCount(entityWrapper);
    }

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionConsumeProject> filterByDelStatus(List<UnionConsumeProject> unionConsumeProjectList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionConsumeProject> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionConsumeProjectList)) {
            for (UnionConsumeProject unionConsumeProject : unionConsumeProjectList) {
                if (delStatus.equals(unionConsumeProject.getDelStatus())) {
                    result.add(unionConsumeProject);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
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
        result = unionConsumeProjectDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionConsumeProject getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionConsumeProject result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionConsumeProject getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionConsumeProject result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionConsumeProject> unionConsumeProjectList) throws Exception {
        if (unionConsumeProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionConsumeProjectList)) {
            for (UnionConsumeProject unionConsumeProject : unionConsumeProjectList) {
                result.add(unionConsumeProject.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionConsumeProject> listByConsumeId(Integer consumeId) throws Exception {
        if (consumeId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsumeProject> result;
        // (1)cache
        String consumeIdKey = UnionConsumeProjectCacheUtil.getConsumeIdKey(consumeId);
        if (redisCacheUtil.exists(consumeIdKey)) {
            String tempStr = redisCacheUtil.get(consumeIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsumeProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("consume_id", consumeId);
        result = unionConsumeProjectDao.selectList(entityWrapper);
        setCache(result, consumeId, UnionConsumeProjectCacheUtil.TYPE_CONSUME_ID);
        return result;
    }

    @Override
    public List<UnionConsumeProject> listValidByConsumeId(Integer consumeId) throws Exception {
        if (consumeId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsumeProject> result;
        // (1)cache
        String validConsumeIdKey = UnionConsumeProjectCacheUtil.getValidConsumeIdKey(consumeId);
        if (redisCacheUtil.exists(validConsumeIdKey)) {
            String tempStr = redisCacheUtil.get(validConsumeIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsumeProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("consume_id", consumeId);
        result = unionConsumeProjectDao.selectList(entityWrapper);
        setValidCache(result, consumeId, UnionConsumeProjectCacheUtil.TYPE_CONSUME_ID);
        return result;
    }

    @Override
    public List<UnionConsumeProject> listInvalidByConsumeId(Integer consumeId) throws Exception {
        if (consumeId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsumeProject> result;
        // (1)cache
        String invalidConsumeIdKey = UnionConsumeProjectCacheUtil.getInvalidConsumeIdKey(consumeId);
        if (redisCacheUtil.exists(invalidConsumeIdKey)) {
            String tempStr = redisCacheUtil.get(invalidConsumeIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsumeProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("consume_id", consumeId);
        result = unionConsumeProjectDao.selectList(entityWrapper);
        setInvalidCache(result, consumeId, UnionConsumeProjectCacheUtil.TYPE_CONSUME_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("project_item_id", projectItemId);
        result = unionConsumeProjectDao.selectList(entityWrapper);
        setCache(result, projectItemId, UnionConsumeProjectCacheUtil.TYPE_PROJECT_ITEM_ID);
        return result;
    }

    @Override
    public List<UnionConsumeProject> listValidByProjectItemId(Integer projectItemId) throws Exception {
        if (projectItemId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsumeProject> result;
        // (1)cache
        String validProjectItemIdKey = UnionConsumeProjectCacheUtil.getValidProjectItemIdKey(projectItemId);
        if (redisCacheUtil.exists(validProjectItemIdKey)) {
            String tempStr = redisCacheUtil.get(validProjectItemIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsumeProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("project_item_id", projectItemId);
        result = unionConsumeProjectDao.selectList(entityWrapper);
        setValidCache(result, projectItemId, UnionConsumeProjectCacheUtil.TYPE_PROJECT_ITEM_ID);
        return result;
    }

    @Override
    public List<UnionConsumeProject> listInvalidByProjectItemId(Integer projectItemId) throws Exception {
        if (projectItemId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsumeProject> result;
        // (1)cache
        String invalidProjectItemIdKey = UnionConsumeProjectCacheUtil.getInvalidProjectItemIdKey(projectItemId);
        if (redisCacheUtil.exists(invalidProjectItemIdKey)) {
            String tempStr = redisCacheUtil.get(invalidProjectItemIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsumeProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("project_item_id", projectItemId);
        result = unionConsumeProjectDao.selectList(entityWrapper);
        setInvalidCache(result, projectItemId, UnionConsumeProjectCacheUtil.TYPE_PROJECT_ITEM_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("project_id", projectId);
        result = unionConsumeProjectDao.selectList(entityWrapper);
        setCache(result, projectId, UnionConsumeProjectCacheUtil.TYPE_PROJECT_ID);
        return result;
    }

    @Override
    public List<UnionConsumeProject> listValidByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsumeProject> result;
        // (1)cache
        String validProjectIdKey = UnionConsumeProjectCacheUtil.getValidProjectIdKey(projectId);
        if (redisCacheUtil.exists(validProjectIdKey)) {
            String tempStr = redisCacheUtil.get(validProjectIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsumeProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("project_id", projectId);
        result = unionConsumeProjectDao.selectList(entityWrapper);
        setValidCache(result, projectId, UnionConsumeProjectCacheUtil.TYPE_PROJECT_ID);
        return result;
    }

    @Override
    public List<UnionConsumeProject> listInvalidByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsumeProject> result;
        // (1)cache
        String invalidProjectIdKey = UnionConsumeProjectCacheUtil.getInvalidProjectIdKey(projectId);
        if (redisCacheUtil.exists(invalidProjectIdKey)) {
            String tempStr = redisCacheUtil.get(invalidProjectIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsumeProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("project_id", projectId);
        result = unionConsumeProjectDao.selectList(entityWrapper);
        setInvalidCache(result, projectId, UnionConsumeProjectCacheUtil.TYPE_PROJECT_ID);
        return result;
    }

    @Override
    public List<UnionConsumeProject> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionConsumeProject> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionConsumeProject> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionConsumeProjectDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionConsumeProject newUnionConsumeProject) throws Exception {
        if (newUnionConsumeProject == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionConsumeProjectDao.insert(newUnionConsumeProject);
        removeCache(newUnionConsumeProject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionConsumeProject> newUnionConsumeProjectList) throws Exception {
        if (newUnionConsumeProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionConsumeProjectDao.insertBatch(newUnionConsumeProjectList);
        removeCache(newUnionConsumeProjectList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
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
        unionConsumeProjectDao.updateById(removeUnionConsumeProject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionConsumeProject> unionConsumeProjectList = listByIdList(idList);
        removeCache(unionConsumeProjectList);
        // (2)remove in db logically
        List<UnionConsumeProject> removeUnionConsumeProjectList = new ArrayList<>();
        for (Integer id : idList) {
            UnionConsumeProject removeUnionConsumeProject = new UnionConsumeProject();
            removeUnionConsumeProject.setId(id);
            removeUnionConsumeProject.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionConsumeProjectList.add(removeUnionConsumeProject);
        }
        unionConsumeProjectDao.updateBatchById(removeUnionConsumeProjectList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
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
        unionConsumeProjectDao.updateById(updateUnionConsumeProject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionConsumeProject> updateUnionConsumeProjectList) throws Exception {
        if (updateUnionConsumeProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionConsumeProjectList);
        List<UnionConsumeProject> unionConsumeProjectList = listByIdList(idList);
        removeCache(unionConsumeProjectList);
        // (2)update db
        unionConsumeProjectDao.updateBatchById(updateUnionConsumeProjectList);
    }

    //********************************************* Object As a Service - cache support ********************************

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
            case UnionConsumeProjectCacheUtil.TYPE_CONSUME_ID:
                foreignIdKey = UnionConsumeProjectCacheUtil.getConsumeIdKey(foreignId);
                break;
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

    private void setValidCache(List<UnionConsumeProject> newUnionConsumeProjectList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionConsumeProjectCacheUtil.TYPE_CONSUME_ID:
                validForeignIdKey = UnionConsumeProjectCacheUtil.getValidConsumeIdKey(foreignId);
                break;
            case UnionConsumeProjectCacheUtil.TYPE_PROJECT_ITEM_ID:
                validForeignIdKey = UnionConsumeProjectCacheUtil.getValidProjectItemIdKey(foreignId);
                break;
            case UnionConsumeProjectCacheUtil.TYPE_PROJECT_ID:
                validForeignIdKey = UnionConsumeProjectCacheUtil.getValidProjectIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionConsumeProjectList);
        }
    }

    private void setInvalidCache(List<UnionConsumeProject> newUnionConsumeProjectList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionConsumeProjectCacheUtil.TYPE_CONSUME_ID:
                invalidForeignIdKey = UnionConsumeProjectCacheUtil.getInvalidConsumeIdKey(foreignId);
                break;
            case UnionConsumeProjectCacheUtil.TYPE_PROJECT_ITEM_ID:
                invalidForeignIdKey = UnionConsumeProjectCacheUtil.getInvalidProjectItemIdKey(foreignId);
                break;
            case UnionConsumeProjectCacheUtil.TYPE_PROJECT_ID:
                invalidForeignIdKey = UnionConsumeProjectCacheUtil.getInvalidProjectIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionConsumeProjectList);
        }
    }

    private void removeCache(UnionConsumeProject unionConsumeProject) {
        if (unionConsumeProject == null) {
            return;
        }
        Integer id = unionConsumeProject.getId();
        String idKey = UnionConsumeProjectCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer consumeId = unionConsumeProject.getConsumeId();
        if (consumeId != null) {
            String consumeIdKey = UnionConsumeProjectCacheUtil.getConsumeIdKey(consumeId);
            redisCacheUtil.remove(consumeIdKey);

            String validConsumeIdKey = UnionConsumeProjectCacheUtil.getValidConsumeIdKey(consumeId);
            redisCacheUtil.remove(validConsumeIdKey);

            String invalidConsumeIdKey = UnionConsumeProjectCacheUtil.getInvalidConsumeIdKey(consumeId);
            redisCacheUtil.remove(invalidConsumeIdKey);
        }

        Integer projectItemId = unionConsumeProject.getProjectItemId();
        if (projectItemId != null) {
            String projectItemIdKey = UnionConsumeProjectCacheUtil.getProjectItemIdKey(projectItemId);
            redisCacheUtil.remove(projectItemIdKey);

            String validProjectItemIdKey = UnionConsumeProjectCacheUtil.getValidProjectItemIdKey(projectItemId);
            redisCacheUtil.remove(validProjectItemIdKey);

            String invalidProjectItemIdKey = UnionConsumeProjectCacheUtil.getInvalidProjectItemIdKey(projectItemId);
            redisCacheUtil.remove(invalidProjectItemIdKey);
        }

        Integer projectId = unionConsumeProject.getProjectId();
        if (projectId != null) {
            String projectIdKey = UnionConsumeProjectCacheUtil.getProjectIdKey(projectId);
            redisCacheUtil.remove(projectIdKey);

            String validProjectIdKey = UnionConsumeProjectCacheUtil.getValidProjectIdKey(projectId);
            redisCacheUtil.remove(validProjectIdKey);

            String invalidProjectIdKey = UnionConsumeProjectCacheUtil.getInvalidProjectIdKey(projectId);
            redisCacheUtil.remove(invalidProjectIdKey);
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

        List<String> consumeIdKeyList = getForeignIdKeyList(unionConsumeProjectList, UnionConsumeProjectCacheUtil.TYPE_CONSUME_ID);
        if (ListUtil.isNotEmpty(consumeIdKeyList)) {
            redisCacheUtil.remove(consumeIdKeyList);
        }

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
            case UnionConsumeProjectCacheUtil.TYPE_CONSUME_ID:
                for (UnionConsumeProject unionConsumeProject : unionConsumeProjectList) {
                    Integer consumeId = unionConsumeProject.getConsumeId();
                    if (consumeId != null) {
                        String consumeIdKey = UnionConsumeProjectCacheUtil.getConsumeIdKey(consumeId);
                        result.add(consumeIdKey);

                        String validConsumeIdKey = UnionConsumeProjectCacheUtil.getValidConsumeIdKey(consumeId);
                        result.add(validConsumeIdKey);

                        String invalidConsumeIdKey = UnionConsumeProjectCacheUtil.getInvalidConsumeIdKey(consumeId);
                        result.add(invalidConsumeIdKey);
                    }
                }
                break;
            case UnionConsumeProjectCacheUtil.TYPE_PROJECT_ITEM_ID:
                for (UnionConsumeProject unionConsumeProject : unionConsumeProjectList) {
                    Integer projectItemId = unionConsumeProject.getProjectItemId();
                    if (projectItemId != null) {
                        String projectItemIdKey = UnionConsumeProjectCacheUtil.getProjectItemIdKey(projectItemId);
                        result.add(projectItemIdKey);

                        String validProjectItemIdKey = UnionConsumeProjectCacheUtil.getValidProjectItemIdKey(projectItemId);
                        result.add(validProjectItemIdKey);

                        String invalidProjectItemIdKey = UnionConsumeProjectCacheUtil.getInvalidProjectItemIdKey(projectItemId);
                        result.add(invalidProjectItemIdKey);
                    }
                }
                break;
            case UnionConsumeProjectCacheUtil.TYPE_PROJECT_ID:
                for (UnionConsumeProject unionConsumeProject : unionConsumeProjectList) {
                    Integer projectId = unionConsumeProject.getProjectId();
                    if (projectId != null) {
                        String projectIdKey = UnionConsumeProjectCacheUtil.getProjectIdKey(projectId);
                        result.add(projectIdKey);

                        String validProjectIdKey = UnionConsumeProjectCacheUtil.getValidProjectIdKey(projectId);
                        result.add(validProjectIdKey);

                        String invalidProjectIdKey = UnionConsumeProjectCacheUtil.getInvalidProjectIdKey(projectId);
                        result.add(invalidProjectIdKey);
                    }
                }
                break;

            default:
                break;
        }
        return result;
    }

}