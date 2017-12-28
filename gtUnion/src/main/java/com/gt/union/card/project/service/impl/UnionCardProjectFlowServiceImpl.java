package com.gt.union.card.project.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.project.dao.IUnionCardProjectFlowDao;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.entity.UnionCardProjectFlow;
import com.gt.union.card.project.service.IUnionCardProjectFlowService;
import com.gt.union.card.project.service.IUnionCardProjectService;
import com.gt.union.card.project.util.UnionCardProjectFlowCacheUtil;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 项目流程 服务实现类
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
@Service
public class UnionCardProjectFlowServiceImpl implements IUnionCardProjectFlowService {
    @Autowired
    private IUnionCardProjectFlowDao unionCardProjectFlowDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionCardProjectService unionCardProjectService;

    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<UnionCardProjectFlow> listValidByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception {
        if (busId == null || unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // （2）	判断project是否存在
        UnionCardProject project = unionCardProjectService.getValidByUnionIdAndMemberIdAndActivityId(unionId, member.getId(), activityId);

        // （3）	按时间顺序排序
        List<UnionCardProjectFlow> result = new ArrayList<>();
        if (project != null) {
            result = listValidByProjectId(project.getId());

            Collections.sort(result, new Comparator<UnionCardProjectFlow>() {
                @Override
                public int compare(UnionCardProjectFlow o1, UnionCardProjectFlow o2) {
                    return o1.getCreateTime().compareTo(o2.getCreateTime());
                }
            });
        }

        return result;
    }

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionCardProjectFlow> filterByDelStatus(List<UnionCardProjectFlow> unionCardProjectFlowList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProjectFlow> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardProjectFlowList)) {
            for (UnionCardProjectFlow unionCardProjectFlow : unionCardProjectFlowList) {
                if (delStatus.equals(unionCardProjectFlow.getDelStatus())) {
                    result.add(unionCardProjectFlow);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
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
        result = unionCardProjectFlowDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionCardProjectFlow getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardProjectFlow result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionCardProjectFlow getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardProjectFlow result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionCardProjectFlow> unionCardProjectFlowList) throws Exception {
        if (unionCardProjectFlowList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardProjectFlowList)) {
            for (UnionCardProjectFlow unionCardProjectFlow : unionCardProjectFlowList) {
                result.add(unionCardProjectFlow.getId());
            }
        }

        return result;
    }

    @Override
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
        entityWrapper.eq("project_id", projectId);
        result = unionCardProjectFlowDao.selectList(entityWrapper);
        setCache(result, projectId, UnionCardProjectFlowCacheUtil.TYPE_PROJECT_ID);
        return result;
    }

    @Override
    public List<UnionCardProjectFlow> listValidByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardProjectFlow> result;
        // (1)cache
        String validProjectIdKey = UnionCardProjectFlowCacheUtil.getValidProjectIdKey(projectId);
        if (redisCacheUtil.exists(validProjectIdKey)) {
            String tempStr = redisCacheUtil.get(validProjectIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardProjectFlow.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProjectFlow> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("project_id", projectId);
        result = unionCardProjectFlowDao.selectList(entityWrapper);
        setValidCache(result, projectId, UnionCardProjectFlowCacheUtil.TYPE_PROJECT_ID);
        return result;
    }

    @Override
    public List<UnionCardProjectFlow> listInvalidByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardProjectFlow> result;
        // (1)cache
        String invalidProjectIdKey = UnionCardProjectFlowCacheUtil.getInvalidProjectIdKey(projectId);
        if (redisCacheUtil.exists(invalidProjectIdKey)) {
            String tempStr = redisCacheUtil.get(invalidProjectIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardProjectFlow.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProjectFlow> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("project_id", projectId);
        result = unionCardProjectFlowDao.selectList(entityWrapper);
        setInvalidCache(result, projectId, UnionCardProjectFlowCacheUtil.TYPE_PROJECT_ID);
        return result;
    }

    @Override
    public List<UnionCardProjectFlow> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProjectFlow> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionCardProjectFlow> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardProjectFlowDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardProjectFlow newUnionCardProjectFlow) throws Exception {
        if (newUnionCardProjectFlow == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionCardProjectFlowDao.insert(newUnionCardProjectFlow);
        removeCache(newUnionCardProjectFlow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardProjectFlow> newUnionCardProjectFlowList) throws Exception {
        if (newUnionCardProjectFlowList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionCardProjectFlowDao.insertBatch(newUnionCardProjectFlowList);
        removeCache(newUnionCardProjectFlowList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
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
        unionCardProjectFlowDao.updateById(removeUnionCardProjectFlow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardProjectFlow> unionCardProjectFlowList = listByIdList(idList);
        removeCache(unionCardProjectFlowList);
        // (2)remove in db logically
        List<UnionCardProjectFlow> removeUnionCardProjectFlowList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProjectFlow removeUnionCardProjectFlow = new UnionCardProjectFlow();
            removeUnionCardProjectFlow.setId(id);
            removeUnionCardProjectFlow.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardProjectFlowList.add(removeUnionCardProjectFlow);
        }
        unionCardProjectFlowDao.updateBatchById(removeUnionCardProjectFlowList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
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
        unionCardProjectFlowDao.updateById(updateUnionCardProjectFlow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardProjectFlow> updateUnionCardProjectFlowList) throws Exception {
        if (updateUnionCardProjectFlowList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionCardProjectFlowList);
        List<UnionCardProjectFlow> unionCardProjectFlowList = listByIdList(idList);
        removeCache(unionCardProjectFlowList);
        // (2)update db
        unionCardProjectFlowDao.updateBatchById(updateUnionCardProjectFlowList);
    }

    //********************************************* Object As a Service - cache support ********************************

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

    private void setValidCache(List<UnionCardProjectFlow> newUnionCardProjectFlowList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionCardProjectFlowCacheUtil.TYPE_PROJECT_ID:
                validForeignIdKey = UnionCardProjectFlowCacheUtil.getValidProjectIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionCardProjectFlowList);
        }
    }

    private void setInvalidCache(List<UnionCardProjectFlow> newUnionCardProjectFlowList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionCardProjectFlowCacheUtil.TYPE_PROJECT_ID:
                invalidForeignIdKey = UnionCardProjectFlowCacheUtil.getInvalidProjectIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionCardProjectFlowList);
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

            String validProjectIdKey = UnionCardProjectFlowCacheUtil.getValidProjectIdKey(projectId);
            redisCacheUtil.remove(validProjectIdKey);

            String invalidProjectIdKey = UnionCardProjectFlowCacheUtil.getInvalidProjectIdKey(projectId);
            redisCacheUtil.remove(invalidProjectIdKey);
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

                        String validProjectIdKey = UnionCardProjectFlowCacheUtil.getValidProjectIdKey(projectId);
                        result.add(validProjectIdKey);

                        String invalidProjectIdKey = UnionCardProjectFlowCacheUtil.getInvalidProjectIdKey(projectId);
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