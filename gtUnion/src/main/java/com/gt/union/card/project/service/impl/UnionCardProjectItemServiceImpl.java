package com.gt.union.card.project.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.card.project.mapper.UnionCardProjectItemMapper;
import com.gt.union.card.project.service.IUnionCardProjectItemService;
import com.gt.union.card.project.util.UnionCardProjectItemCacheUtil;
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
 * 项目优惠 服务实现类
 *
 * @author linweicong
 * @version 2017-11-27 09:55:47
 */
@Service
public class UnionCardProjectItemServiceImpl extends ServiceImpl<UnionCardProjectItemMapper, UnionCardProjectItem> implements IUnionCardProjectItemService {
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

    public UnionCardProjectItem getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardProjectItem result;
        // (1)cache
        String idKey = UnionCardProjectItemCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionCardProjectItem.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProjectItem> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionCardProjectItem> listByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardProjectItem> result;
        // cache
        String projectIdKey = UnionCardProjectItemCacheUtil.getProjectIdKey(projectId);
        if (redisCacheUtil.exists(projectIdKey)) {
            String tempStr = redisCacheUtil.get(projectIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardProjectItem.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProjectItem> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("project_id", projectId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, projectId, UnionCardProjectItemCacheUtil.TYPE_PROJECT_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardProjectItem newUnionCardProjectItem) throws Exception {
        if (newUnionCardProjectItem == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionCardProjectItem);
        removeCache(newUnionCardProjectItem);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardProjectItem> newUnionCardProjectItemList) throws Exception {
        if (newUnionCardProjectItemList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionCardProjectItemList);
        removeCache(newUnionCardProjectItemList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionCardProjectItem unionCardProjectItem = getById(id);
        removeCache(unionCardProjectItem);
        // (2)remove in db logically
        UnionCardProjectItem removeUnionCardProjectItem = new UnionCardProjectItem();
        removeUnionCardProjectItem.setId(id);
        removeUnionCardProjectItem.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionCardProjectItem);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardProjectItem> unionCardProjectItemList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProjectItem unionCardProjectItem = getById(id);
            unionCardProjectItemList.add(unionCardProjectItem);
        }
        removeCache(unionCardProjectItemList);
        // (2)remove in db logically
        List<UnionCardProjectItem> removeUnionCardProjectItemList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProjectItem removeUnionCardProjectItem = new UnionCardProjectItem();
            removeUnionCardProjectItem.setId(id);
            removeUnionCardProjectItem.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardProjectItemList.add(removeUnionCardProjectItem);
        }
        updateBatchById(removeUnionCardProjectItemList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardProjectItem updateUnionCardProjectItem) throws Exception {
        if (updateUnionCardProjectItem == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCardProjectItem.getId();
        UnionCardProjectItem unionCardProjectItem = getById(id);
        removeCache(unionCardProjectItem);
        // (2)update db
        updateById(updateUnionCardProjectItem);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardProjectItem> updateUnionCardProjectItemList) throws Exception {
        if (updateUnionCardProjectItemList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionCardProjectItem updateUnionCardProjectItem : updateUnionCardProjectItemList) {
            idList.add(updateUnionCardProjectItem.getId());
        }
        List<UnionCardProjectItem> unionCardProjectItemList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProjectItem unionCardProjectItem = getById(id);
            unionCardProjectItemList.add(unionCardProjectItem);
        }
        removeCache(unionCardProjectItemList);
        // (2)update db
        updateBatchById(updateUnionCardProjectItemList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionCardProjectItem newUnionCardProjectItem, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardProjectItemCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCardProjectItem);
    }

    private void setCache(List<UnionCardProjectItem> newUnionCardProjectItemList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionCardProjectItemCacheUtil.TYPE_PROJECT_ID:
                foreignIdKey = UnionCardProjectItemCacheUtil.getProjectIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionCardProjectItemList);
        }
    }

    private void removeCache(UnionCardProjectItem unionCardProjectItem) {
        if (unionCardProjectItem == null) {
            return;
        }
        Integer id = unionCardProjectItem.getId();
        String idKey = UnionCardProjectItemCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer projectId = unionCardProjectItem.getProjectId();
        if (projectId != null) {
            String projectIdKey = UnionCardProjectItemCacheUtil.getProjectIdKey(projectId);
            redisCacheUtil.remove(projectIdKey);
        }
    }

    private void removeCache(List<UnionCardProjectItem> unionCardProjectItemList) {
        if (ListUtil.isEmpty(unionCardProjectItemList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCardProjectItem unionCardProjectItem : unionCardProjectItemList) {
            idList.add(unionCardProjectItem.getId());
        }
        List<String> idKeyList = UnionCardProjectItemCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> projectIdKeyList = getForeignIdKeyList(unionCardProjectItemList, UnionCardProjectItemCacheUtil.TYPE_PROJECT_ID);
        if (ListUtil.isNotEmpty(projectIdKeyList)) {
            redisCacheUtil.remove(projectIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionCardProjectItem> unionCardProjectItemList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionCardProjectItemCacheUtil.TYPE_PROJECT_ID:
                for (UnionCardProjectItem unionCardProjectItem : unionCardProjectItemList) {
                    Integer projectId = unionCardProjectItem.getProjectId();
                    if (projectId != null) {
                        String projectIdKey = UnionCardProjectItemCacheUtil.getProjectIdKey(projectId);
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