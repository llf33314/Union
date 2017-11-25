package com.gt.union.card.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.entity.UnionActivityProject;
import com.gt.union.card.mapper.UnionActivityProjectMapper;
import com.gt.union.card.service.IUnionActivityProjectService;
import com.gt.union.card.util.UnionActivityProjectCacheUtil;
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
 * 活动项目 服务实现类
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
@Service
public class UnionActivityProjectServiceImpl extends ServiceImpl<UnionActivityProjectMapper, UnionActivityProject> implements IUnionActivityProjectService {
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

    public UnionActivityProject getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionActivityProject result;
        // (1)cache
        String idKey = UnionActivityProjectCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionActivityProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionActivityProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionActivityProject> listByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionActivityProject> result;
        // (1)cache
        String activityIdKey = UnionActivityProjectCacheUtil.getActivityIdKey(activityId);
        if (redisCacheUtil.exists(activityIdKey)) {
            String tempStr = redisCacheUtil.get(activityIdKey);
            result = JSONArray.parseArray(tempStr, UnionActivityProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionActivityProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_id", activityId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, activityId, UnionActivityProjectCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    public List<UnionActivityProject> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionActivityProject> result;
        // (1)cache
        String memberIdKey = UnionActivityProjectCacheUtil.getMemberIdKey(memberId);
        if (redisCacheUtil.exists(memberIdKey)) {
            String tempStr = redisCacheUtil.get(memberIdKey);
            result = JSONArray.parseArray(tempStr, UnionActivityProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionActivityProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, memberId, UnionActivityProjectCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    public List<UnionActivityProject> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionActivityProject> result;
        // (1)cache
        String unionIdKey = UnionActivityProjectCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionActivityProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionActivityProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionActivityProjectCacheUtil.TYPE_UNION_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionActivityProject newUnionActivityProject) throws Exception {
        if (newUnionActivityProject == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionActivityProject);
        removeCache(newUnionActivityProject);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionActivityProject> newUnionActivityProjectList) throws Exception {
        if (newUnionActivityProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionActivityProjectList);
        removeCache(newUnionActivityProjectList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionActivityProject unionActivityProject = getById(id);
        removeCache(unionActivityProject);
        // (2)remove in db logically
        UnionActivityProject removeUnionActivityProject = new UnionActivityProject();
        removeUnionActivityProject.setId(id);
        removeUnionActivityProject.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionActivityProject);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionActivityProject> unionActivityProjectList = new ArrayList<>();
        for (Integer id : idList) {
            UnionActivityProject unionActivityProject = getById(id);
            unionActivityProjectList.add(unionActivityProject);
        }
        removeCache(unionActivityProjectList);
        // (2)remove in db logically
        List<UnionActivityProject> removeUnionActivityProjectList = new ArrayList<>();
        for (Integer id : idList) {
            UnionActivityProject removeUnionActivityProject = new UnionActivityProject();
            removeUnionActivityProject.setId(id);
            removeUnionActivityProject.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionActivityProjectList.add(removeUnionActivityProject);
        }
        updateBatchById(removeUnionActivityProjectList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionActivityProject updateUnionActivityProject) throws Exception {
        if (updateUnionActivityProject == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionActivityProject.getId();
        UnionActivityProject unionActivityProject = getById(id);
        removeCache(unionActivityProject);
        // (2)update db
        updateById(updateUnionActivityProject);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionActivityProject> updateUnionActivityProjectList) throws Exception {
        if (updateUnionActivityProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionActivityProject updateUnionActivityProject : updateUnionActivityProjectList) {
            idList.add(updateUnionActivityProject.getId());
        }
        List<UnionActivityProject> unionActivityProjectList = new ArrayList<>();
        for (Integer id : idList) {
            UnionActivityProject unionActivityProject = getById(id);
            unionActivityProjectList.add(unionActivityProject);
        }
        removeCache(unionActivityProjectList);
        // (2)update db
        updateBatchById(updateUnionActivityProjectList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionActivityProject newUnionActivityProject, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionActivityProjectCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionActivityProject);
    }

    private void setCache(List<UnionActivityProject> newUnionActivityProjectList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionActivityProjectCacheUtil.TYPE_ACTIVITY_ID:
                foreignIdKey = UnionActivityProjectCacheUtil.getActivityIdKey(foreignId);
                break;
            case UnionActivityProjectCacheUtil.TYPE_MEMBER_ID:
                foreignIdKey = UnionActivityProjectCacheUtil.getMemberIdKey(foreignId);
                break;
            case UnionActivityProjectCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionActivityProjectCacheUtil.getUnionIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionActivityProjectList);
        }
    }

    private void removeCache(UnionActivityProject unionActivityProject) {
        if (unionActivityProject == null) {
            return;
        }
        Integer id = unionActivityProject.getId();
        String idKey = UnionActivityProjectCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer activityId = unionActivityProject.getActivityId();
        if (activityId != null) {
            String activityIdKey = UnionActivityProjectCacheUtil.getActivityIdKey(activityId);
            redisCacheUtil.remove(activityIdKey);
        }

        Integer memberId = unionActivityProject.getMemberId();
        if (memberId != null) {
            String memberIdKey = UnionActivityProjectCacheUtil.getMemberIdKey(memberId);
            redisCacheUtil.remove(memberIdKey);
        }

        Integer unionId = unionActivityProject.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionActivityProjectCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }
    }

    private void removeCache(List<UnionActivityProject> unionActivityProjectList) {
        if (ListUtil.isEmpty(unionActivityProjectList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionActivityProject unionActivityProject : unionActivityProjectList) {
            idList.add(unionActivityProject.getId());
        }
        List<String> idKeyList = UnionActivityProjectCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> activityIdKeyList = getForeignIdKeyList(unionActivityProjectList, UnionActivityProjectCacheUtil.TYPE_ACTIVITY_ID);
        if (ListUtil.isNotEmpty(activityIdKeyList)) {
            redisCacheUtil.remove(activityIdKeyList);
        }

        List<String> memberIdKeyList = getForeignIdKeyList(unionActivityProjectList, UnionActivityProjectCacheUtil.TYPE_MEMBER_ID);
        if (ListUtil.isNotEmpty(memberIdKeyList)) {
            redisCacheUtil.remove(memberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionActivityProjectList, UnionActivityProjectCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionActivityProject> unionActivityProjectList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionActivityProjectCacheUtil.TYPE_ACTIVITY_ID:
                for (UnionActivityProject unionActivityProject : unionActivityProjectList) {
                    Integer activityId = unionActivityProject.getActivityId();
                    if (activityId != null) {
                        String activityIdKey = UnionActivityProjectCacheUtil.getActivityIdKey(activityId);
                        result.add(activityIdKey);
                    }
                }
                break;
            case UnionActivityProjectCacheUtil.TYPE_MEMBER_ID:
                for (UnionActivityProject unionActivityProject : unionActivityProjectList) {
                    Integer memberId = unionActivityProject.getMemberId();
                    if (memberId != null) {
                        String memberIdKey = UnionActivityProjectCacheUtil.getMemberIdKey(memberId);
                        result.add(memberIdKey);
                    }
                }
                break;
            case UnionActivityProjectCacheUtil.TYPE_UNION_ID:
                for (UnionActivityProject unionActivityProject : unionActivityProjectList) {
                    Integer unionId = unionActivityProject.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionActivityProjectCacheUtil.getUnionIdKey(unionId);
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