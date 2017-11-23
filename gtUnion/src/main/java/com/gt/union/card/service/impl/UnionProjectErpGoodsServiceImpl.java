package com.gt.union.card.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.entity.UnionProjectErpGoods;
import com.gt.union.card.mapper.UnionProjectErpGoodsMapper;
import com.gt.union.card.service.IUnionProjectErpGoodsService;
import com.gt.union.card.util.UnionProjectErpGoodsCacheUtil;
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
 * ERP商品项目 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Service
public class UnionProjectErpGoodsServiceImpl extends ServiceImpl<UnionProjectErpGoodsMapper, UnionProjectErpGoods> implements IUnionProjectErpGoodsService {
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

    public UnionProjectErpGoods getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionProjectErpGoods result;
        // (1)cache
        String idKey = UnionProjectErpGoodsCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionProjectErpGoods.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionProjectErpGoods> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionProjectErpGoods> listByActivityProjectId(Integer activityProjectId) throws Exception {
        if (activityProjectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionProjectErpGoods> result;
        // (1)cache
        String activityProjectIdKey = UnionProjectErpGoodsCacheUtil.getActivityProjectId(activityProjectId);
        if (redisCacheUtil.exists(activityProjectIdKey)) {
            String tempStr = redisCacheUtil.get(activityProjectIdKey);
            result = JSONArray.parseArray(tempStr, UnionProjectErpGoods.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionProjectErpGoods> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_project_id", activityProjectId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, activityProjectId, UnionProjectErpGoodsCacheUtil.TYPE_ACTIVITY_PROJECT_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionProjectErpGoods newUnionProjectErpGoods) throws Exception {
        if (newUnionProjectErpGoods == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionProjectErpGoods);
        removeCache(newUnionProjectErpGoods);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionProjectErpGoods> newUnionProjectErpGoodsList) throws Exception {
        if (newUnionProjectErpGoodsList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionProjectErpGoodsList);
        removeCache(newUnionProjectErpGoodsList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionProjectErpGoods unionProjectErpGoods = getById(id);
        removeCache(unionProjectErpGoods);
        // (2)remove in db logically
        UnionProjectErpGoods removeUnionProjectErpGoods = new UnionProjectErpGoods();
        removeUnionProjectErpGoods.setId(id);
        removeUnionProjectErpGoods.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionProjectErpGoods);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionProjectErpGoods> unionProjectErpGoodsList = new ArrayList<>();
        for (Integer id : idList) {
            UnionProjectErpGoods unionProjectErpGoods = getById(id);
            unionProjectErpGoodsList.add(unionProjectErpGoods);
        }
        removeCache(unionProjectErpGoodsList);
        // (2)remove in db logically
        List<UnionProjectErpGoods> removeUnionProjectErpGoodsList = new ArrayList<>();
        for (Integer id : idList) {
            UnionProjectErpGoods removeUnionProjectErpGoods = new UnionProjectErpGoods();
            removeUnionProjectErpGoods.setId(id);
            removeUnionProjectErpGoods.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionProjectErpGoodsList.add(removeUnionProjectErpGoods);
        }
        updateBatchById(removeUnionProjectErpGoodsList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionProjectErpGoods updateUnionProjectErpGoods) throws Exception {
        if (updateUnionProjectErpGoods == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionProjectErpGoods.getId();
        UnionProjectErpGoods unionProjectErpGoods = getById(id);
        removeCache(unionProjectErpGoods);
        // (2)update db
        updateById(updateUnionProjectErpGoods);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionProjectErpGoods> updateUnionProjectErpGoodsList) throws Exception {
        if (updateUnionProjectErpGoodsList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionProjectErpGoods updateUnionProjectErpGoods : updateUnionProjectErpGoodsList) {
            idList.add(updateUnionProjectErpGoods.getId());
        }
        List<UnionProjectErpGoods> unionProjectErpGoodsList = new ArrayList<>();
        for (Integer id : idList) {
            UnionProjectErpGoods unionProjectErpGoods = getById(id);
            unionProjectErpGoodsList.add(unionProjectErpGoods);
        }
        removeCache(unionProjectErpGoodsList);
        // (2)update db
        updateBatchById(updateUnionProjectErpGoodsList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionProjectErpGoods newUnionProjectErpGoods, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionProjectErpGoodsCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionProjectErpGoods);
    }

    private void setCache(List<UnionProjectErpGoods> newUnionProjectErpGoodsList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionProjectErpGoodsCacheUtil.TYPE_ACTIVITY_PROJECT_ID:
                foreignIdKey = UnionProjectErpGoodsCacheUtil.getActivityProjectId(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionProjectErpGoodsList);
        }
    }

    private void removeCache(UnionProjectErpGoods unionProjectErpGoods) {
        if (unionProjectErpGoods == null) {
            return;
        }
        Integer id = unionProjectErpGoods.getId();
        String idKey = UnionProjectErpGoodsCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer activityProjectId = unionProjectErpGoods.getActivityProjectId();
        if (activityProjectId != null) {
            String activityProjectIdKey = UnionProjectErpGoodsCacheUtil.getActivityProjectId(activityProjectId);
            redisCacheUtil.remove(activityProjectIdKey);
        }
    }

    private void removeCache(List<UnionProjectErpGoods> unionProjectErpGoodsList) {
        if (ListUtil.isEmpty(unionProjectErpGoodsList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionProjectErpGoods unionProjectErpGoods : unionProjectErpGoodsList) {
            idList.add(unionProjectErpGoods.getId());
        }
        List<String> idKeyList = UnionProjectErpGoodsCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> activityProjectIdKeyList = getForeignIdKeyList(unionProjectErpGoodsList, UnionProjectErpGoodsCacheUtil.TYPE_ACTIVITY_PROJECT_ID);
        if (ListUtil.isNotEmpty(activityProjectIdKeyList)) {
            redisCacheUtil.remove(activityProjectIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionProjectErpGoods> unionProjectErpGoodsList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionProjectErpGoodsCacheUtil.TYPE_ACTIVITY_PROJECT_ID:
                for (UnionProjectErpGoods unionProjectErpGoods : unionProjectErpGoodsList) {
                    Integer activityProjectId = unionProjectErpGoods.getActivityProjectId();
                    if (activityProjectId != null) {
                        String activityProjectIdKey = UnionProjectErpGoodsCacheUtil.getActivityProjectId(activityProjectId);
                        result.add(activityProjectIdKey);
                    }
                }
            default:
                break;
        }
        return result;
    }
}