package com.gt.union.card.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.entity.UnionProjectErpText;
import com.gt.union.card.mapper.UnionProjectErpTextMapper;
import com.gt.union.card.service.IUnionProjectErpTextService;
import com.gt.union.card.util.UnionProjectErpTextCacheUtil;
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
 * ERP文本项目 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Service
public class UnionProjectErpTextServiceImpl extends ServiceImpl<UnionProjectErpTextMapper, UnionProjectErpText> implements IUnionProjectErpTextService {
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

    public UnionProjectErpText getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionProjectErpText result;
        // (1)cache
        String idKey = UnionProjectErpTextCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionProjectErpText.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionProjectErpText> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionProjectErpText> listByActivityProjectId(Integer activityProjectId) throws Exception {
        if (activityProjectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionProjectErpText> result;
        // (1)cache
        String activityProjectIdKey = UnionProjectErpTextCacheUtil.getActivityProjectId(activityProjectId);
        if (redisCacheUtil.exists(activityProjectIdKey)) {
            String tempStr = redisCacheUtil.get(activityProjectIdKey);
            result = JSONArray.parseArray(tempStr, UnionProjectErpText.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionProjectErpText> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_project_id", activityProjectId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, activityProjectId, UnionProjectErpTextCacheUtil.TYPE_ACTIVITY_PROJECT_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionProjectErpText newUnionProjectErpText) throws Exception {
        if (newUnionProjectErpText == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionProjectErpText);
        removeCache(newUnionProjectErpText);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionProjectErpText> newUnionProjectErpTextList) throws Exception {
        if (newUnionProjectErpTextList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionProjectErpTextList);
        removeCache(newUnionProjectErpTextList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionProjectErpText unionProjectErpText = getById(id);
        removeCache(unionProjectErpText);
        // (2)remove in db logically
        UnionProjectErpText removeUnionProjectErpText = new UnionProjectErpText();
        removeUnionProjectErpText.setId(id);
        removeUnionProjectErpText.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionProjectErpText);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionProjectErpText> unionProjectErpTextList = new ArrayList<>();
        for (Integer id : idList) {
            UnionProjectErpText unionProjectErpText = getById(id);
            unionProjectErpTextList.add(unionProjectErpText);
        }
        removeCache(unionProjectErpTextList);
        // (2)remove in db logically
        List<UnionProjectErpText> removeUnionProjectErpTextList = new ArrayList<>();
        for (Integer id : idList) {
            UnionProjectErpText removeUnionProjectErpText = new UnionProjectErpText();
            removeUnionProjectErpText.setId(id);
            removeUnionProjectErpText.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionProjectErpTextList.add(removeUnionProjectErpText);
        }
        updateBatchById(removeUnionProjectErpTextList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionProjectErpText updateUnionProjectErpText) throws Exception {
        if (updateUnionProjectErpText == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionProjectErpText.getId();
        UnionProjectErpText unionProjectErpText = getById(id);
        removeCache(unionProjectErpText);
        // (2)update db
        updateById(updateUnionProjectErpText);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionProjectErpText> updateUnionProjectErpTextList) throws Exception {
        if (updateUnionProjectErpTextList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionProjectErpText updateUnionProjectErpText : updateUnionProjectErpTextList) {
            idList.add(updateUnionProjectErpText.getId());
        }
        List<UnionProjectErpText> unionProjectErpTextList = new ArrayList<>();
        for (Integer id : idList) {
            UnionProjectErpText unionProjectErpText = getById(id);
            unionProjectErpTextList.add(unionProjectErpText);
        }
        removeCache(unionProjectErpTextList);
        // (2)update db
        updateBatchById(updateUnionProjectErpTextList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionProjectErpText newUnionProjectErpText, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionProjectErpTextCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionProjectErpText);
    }

    private void setCache(List<UnionProjectErpText> newUnionProjectErpTextList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionProjectErpTextCacheUtil.TYPE_ACTIVITY_PROJECT_ID:
                foreignIdKey = UnionProjectErpTextCacheUtil.getActivityProjectId(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionProjectErpTextList);
        }
    }

    private void removeCache(UnionProjectErpText unionProjectErpText) {
        if (unionProjectErpText == null) {
            return;
        }
        Integer id = unionProjectErpText.getId();
        String idKey = UnionProjectErpTextCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer activityProjectId = unionProjectErpText.getActivityProjectId();
        if (activityProjectId != null) {
            String activityProjectIdKey = UnionProjectErpTextCacheUtil.getActivityProjectId(activityProjectId);
            redisCacheUtil.remove(activityProjectIdKey);
        }
    }

    private void removeCache(List<UnionProjectErpText> unionProjectErpTextList) {
        if (ListUtil.isEmpty(unionProjectErpTextList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionProjectErpText unionProjectErpText : unionProjectErpTextList) {
            idList.add(unionProjectErpText.getId());
        }
        List<String> idKeyList = UnionProjectErpTextCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> activityProjectIdKeyList = getForeignIdKeyList(unionProjectErpTextList, UnionProjectErpTextCacheUtil.TYPE_ACTIVITY_PROJECT_ID);
        if (ListUtil.isNotEmpty(activityProjectIdKeyList)) {
            redisCacheUtil.remove(activityProjectIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionProjectErpText> unionProjectErpTextList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionProjectErpTextCacheUtil.TYPE_ACTIVITY_PROJECT_ID:
                for (UnionProjectErpText unionProjectErpText : unionProjectErpTextList) {
                    Integer activityProjectId = unionProjectErpText.getActivityProjectId();
                    if (activityProjectId != null) {
                        String activityProjectIdKey = UnionProjectErpTextCacheUtil.getActivityProjectId(activityProjectId);
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