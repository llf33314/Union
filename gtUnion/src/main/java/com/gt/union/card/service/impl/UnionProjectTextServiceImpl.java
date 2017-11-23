package com.gt.union.card.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.entity.UnionProjectText;
import com.gt.union.card.mapper.UnionProjectTextMapper;
import com.gt.union.card.service.IUnionProjectTextService;
import com.gt.union.card.util.UnionProjectTextCacheUtil;
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
 * 非ERP文本项目 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Service
public class UnionProjectTextServiceImpl extends ServiceImpl<UnionProjectTextMapper, UnionProjectText> implements IUnionProjectTextService {
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

    public UnionProjectText getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionProjectText result;
        // (1)cache
        String idKey = UnionProjectTextCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionProjectText.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionProjectText> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionProjectText> listByActivityProjectId(Integer activityProjectId) throws Exception {
        if (activityProjectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionProjectText> result;
        // (1)cache
        String activityProjectIdKey = UnionProjectTextCacheUtil.getActivityProjectId(activityProjectId);
        if (redisCacheUtil.exists(activityProjectIdKey)) {
            String tempStr = redisCacheUtil.get(activityProjectIdKey);
            result = JSONArray.parseArray(tempStr, UnionProjectText.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionProjectText> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_project_id", activityProjectId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, activityProjectId, UnionProjectTextCacheUtil.TYPE_ACTIVITY_PROJECT_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionProjectText newUnionProjectText) throws Exception {
        if (newUnionProjectText == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionProjectText);
        removeCache(newUnionProjectText);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionProjectText> newUnionProjectTextList) throws Exception {
        if (newUnionProjectTextList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionProjectTextList);
        removeCache(newUnionProjectTextList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionProjectText unionProjectText = getById(id);
        removeCache(unionProjectText);
        // (2)remove in db logically
        UnionProjectText removeUnionProjectText = new UnionProjectText();
        removeUnionProjectText.setId(id);
        removeUnionProjectText.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionProjectText);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionProjectText> unionProjectTextList = new ArrayList<>();
        for (Integer id : idList) {
            UnionProjectText unionProjectText = getById(id);
            unionProjectTextList.add(unionProjectText);
        }
        removeCache(unionProjectTextList);
        // (2)remove in db logically
        List<UnionProjectText> removeUnionProjectTextList = new ArrayList<>();
        for (Integer id : idList) {
            UnionProjectText removeUnionProjectText = new UnionProjectText();
            removeUnionProjectText.setId(id);
            removeUnionProjectText.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionProjectTextList.add(removeUnionProjectText);
        }
        updateBatchById(removeUnionProjectTextList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionProjectText updateUnionProjectText) throws Exception {
        if (updateUnionProjectText == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionProjectText.getId();
        UnionProjectText unionProjectText = getById(id);
        removeCache(unionProjectText);
        // (2)update db
        updateById(updateUnionProjectText);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionProjectText> updateUnionProjectTextList) throws Exception {
        if (updateUnionProjectTextList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionProjectText updateUnionProjectText : updateUnionProjectTextList) {
            idList.add(updateUnionProjectText.getId());
        }
        List<UnionProjectText> unionProjectTextList = new ArrayList<>();
        for (Integer id : idList) {
            UnionProjectText unionProjectText = getById(id);
            unionProjectTextList.add(unionProjectText);
        }
        removeCache(unionProjectTextList);
        // (2)update db
        updateBatchById(updateUnionProjectTextList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionProjectText newUnionProjectText, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionProjectTextCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionProjectText);
    }

    private void setCache(List<UnionProjectText> newUnionProjectTextList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionProjectTextCacheUtil.TYPE_ACTIVITY_PROJECT_ID:
                foreignIdKey = UnionProjectTextCacheUtil.getActivityProjectId(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionProjectTextList);
        }
    }

    private void removeCache(UnionProjectText unionProjectText) {
        if (unionProjectText == null) {
            return;
        }
        Integer id = unionProjectText.getId();
        String idKey = UnionProjectTextCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer activityProjectId = unionProjectText.getActivityProjectId();
        if (activityProjectId != null) {
            String activityProjectIdKey = UnionProjectTextCacheUtil.getActivityProjectId(activityProjectId);
            redisCacheUtil.remove(activityProjectIdKey);
        }
    }

    private void removeCache(List<UnionProjectText> unionProjectTextList) {
        if (ListUtil.isEmpty(unionProjectTextList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionProjectText unionProjectText : unionProjectTextList) {
            idList.add(unionProjectText.getId());
        }
        List<String> idKeyList = UnionProjectTextCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> activityProjectIdKeyList = getForeignIdKeyList(unionProjectTextList, UnionProjectTextCacheUtil.TYPE_ACTIVITY_PROJECT_ID);
        if (ListUtil.isNotEmpty(activityProjectIdKeyList)) {
            redisCacheUtil.remove(activityProjectIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionProjectText> unionProjectTextList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionProjectTextCacheUtil.TYPE_ACTIVITY_PROJECT_ID:
                for (UnionProjectText unionProjectText : unionProjectTextList) {
                    Integer activityProjectId = unionProjectText.getActivityProjectId();
                    if (activityProjectId != null) {
                        String activityProjectIdKey = UnionProjectTextCacheUtil.getActivityProjectId(activityProjectId);
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