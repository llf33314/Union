package com.gt.union.card.project.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.mapper.UnionCardProjectMapper;
import com.gt.union.card.project.service.IUnionCardProjectService;
import com.gt.union.card.project.util.UnionCardProjectCacheUtil;
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
 * 项目 服务实现类
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
@Service
public class UnionCardProjectServiceImpl extends ServiceImpl<UnionCardProjectMapper, UnionCardProject> implements IUnionCardProjectService {
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

    public UnionCardProject getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardProject result;
        // (1)cache
        String idKey = UnionCardProjectCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionCardProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionCardProject> listByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardProject> result;
        // (1)cache
        String activityIdKey = UnionCardProjectCacheUtil.getActivityIdKey(activityId);
        if (redisCacheUtil.exists(activityIdKey)) {
            String tempStr = redisCacheUtil.get(activityIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_id", activityId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, activityId, UnionCardProjectCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    public List<UnionCardProject> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardProject> result;
        // (1)cache
        String memberIdKey = UnionCardProjectCacheUtil.getMemberIdKey(memberId);
        if (redisCacheUtil.exists(memberIdKey)) {
            String tempStr = redisCacheUtil.get(memberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, memberId, UnionCardProjectCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    public List<UnionCardProject> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardProject> result;
        // (1)cache
        String unionIdKey = UnionCardProjectCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionCardProjectCacheUtil.TYPE_UNION_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardProject newUnionCardProject) throws Exception {
        if (newUnionCardProject == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionCardProject);
        removeCache(newUnionCardProject);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardProject> newUnionCardProjectList) throws Exception {
        if (newUnionCardProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionCardProjectList);
        removeCache(newUnionCardProjectList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionCardProject unionCardProject = getById(id);
        removeCache(unionCardProject);
        // (2)remove in db logically
        UnionCardProject removeUnionCardProject = new UnionCardProject();
        removeUnionCardProject.setId(id);
        removeUnionCardProject.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionCardProject);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardProject> unionCardProjectList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProject unionCardProject = getById(id);
            unionCardProjectList.add(unionCardProject);
        }
        removeCache(unionCardProjectList);
        // (2)remove in db logically
        List<UnionCardProject> removeUnionCardProjectList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProject removeUnionCardProject = new UnionCardProject();
            removeUnionCardProject.setId(id);
            removeUnionCardProject.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardProjectList.add(removeUnionCardProject);
        }
        updateBatchById(removeUnionCardProjectList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardProject updateUnionCardProject) throws Exception {
        if (updateUnionCardProject == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCardProject.getId();
        UnionCardProject unionCardProject = getById(id);
        removeCache(unionCardProject);
        // (2)update db
        updateById(updateUnionCardProject);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardProject> updateUnionCardProjectList) throws Exception {
        if (updateUnionCardProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionCardProject updateUnionCardProject : updateUnionCardProjectList) {
            idList.add(updateUnionCardProject.getId());
        }
        List<UnionCardProject> unionCardProjectList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProject unionCardProject = getById(id);
            unionCardProjectList.add(unionCardProject);
        }
        removeCache(unionCardProjectList);
        // (2)update db
        updateBatchById(updateUnionCardProjectList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionCardProject newUnionCardProject, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardProjectCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCardProject);
    }

    private void setCache(List<UnionCardProject> newUnionCardProjectList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionCardProjectCacheUtil.TYPE_ACTIVITY_ID:
                foreignIdKey = UnionCardProjectCacheUtil.getActivityIdKey(foreignId);
                break;
            case UnionCardProjectCacheUtil.TYPE_MEMBER_ID:
                foreignIdKey = UnionCardProjectCacheUtil.getMemberIdKey(foreignId);
                break;
            case UnionCardProjectCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionCardProjectCacheUtil.getUnionIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionCardProjectList);
        }
    }

    private void removeCache(UnionCardProject unionCardProject) {
        if (unionCardProject == null) {
            return;
        }
        Integer id = unionCardProject.getId();
        String idKey = UnionCardProjectCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer activityId = unionCardProject.getActivityId();
        if (activityId != null) {
            String activityIdKey = UnionCardProjectCacheUtil.getActivityIdKey(activityId);
            redisCacheUtil.remove(activityIdKey);
        }

        Integer memberId = unionCardProject.getMemberId();
        if (memberId != null) {
            String memberIdKey = UnionCardProjectCacheUtil.getMemberIdKey(memberId);
            redisCacheUtil.remove(memberIdKey);
        }

        Integer unionId = unionCardProject.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionCardProjectCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }
    }

    private void removeCache(List<UnionCardProject> unionCardProjectList) {
        if (ListUtil.isEmpty(unionCardProjectList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCardProject unionCardProject : unionCardProjectList) {
            idList.add(unionCardProject.getId());
        }
        List<String> idKeyList = UnionCardProjectCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> activityIdKeyList = getForeignIdKeyList(unionCardProjectList, UnionCardProjectCacheUtil.TYPE_ACTIVITY_ID);
        if (ListUtil.isNotEmpty(activityIdKeyList)) {
            redisCacheUtil.remove(activityIdKeyList);
        }

        List<String> memberIdKeyList = getForeignIdKeyList(unionCardProjectList, UnionCardProjectCacheUtil.TYPE_MEMBER_ID);
        if (ListUtil.isNotEmpty(memberIdKeyList)) {
            redisCacheUtil.remove(memberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionCardProjectList, UnionCardProjectCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionCardProject> unionCardProjectList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionCardProjectCacheUtil.TYPE_ACTIVITY_ID:
                for (UnionCardProject unionCardProject : unionCardProjectList) {
                    Integer activityId = unionCardProject.getActivityId();
                    if (activityId != null) {
                        String activityIdKey = UnionCardProjectCacheUtil.getActivityIdKey(activityId);
                        result.add(activityIdKey);
                    }
                }
                break;
            case UnionCardProjectCacheUtil.TYPE_MEMBER_ID:
                for (UnionCardProject unionCardProject : unionCardProjectList) {
                    Integer memberId = unionCardProject.getMemberId();
                    if (memberId != null) {
                        String memberIdKey = UnionCardProjectCacheUtil.getMemberIdKey(memberId);
                        result.add(memberIdKey);
                    }
                }
                break;
            case UnionCardProjectCacheUtil.TYPE_UNION_ID:
                for (UnionCardProject unionCardProject : unionCardProjectList) {
                    Integer unionId = unionCardProject.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionCardProjectCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}