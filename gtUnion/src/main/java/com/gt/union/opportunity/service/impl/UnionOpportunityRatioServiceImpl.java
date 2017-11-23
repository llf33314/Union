package com.gt.union.opportunity.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.opportunity.entity.UnionOpportunityRatio;
import com.gt.union.opportunity.mapper.UnionOpportunityRatioMapper;
import com.gt.union.opportunity.service.IUnionOpportunityRatioService;
import com.gt.union.opportunity.util.UnionOpportunityRatioCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 商机佣金比率 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 16:56:20
 */
@Service
public class UnionOpportunityRatioServiceImpl extends ServiceImpl<UnionOpportunityRatioMapper, UnionOpportunityRatio> implements IUnionOpportunityRatioService {
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

    public UnionOpportunityRatio getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionOpportunityRatio result;
        // (1)cache
        String idKey = UnionOpportunityRatioCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionOpportunityRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionOpportunityRatio> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunityRatio> result;
        // (1)cache
        String unionIdKey = UnionOpportunityRatioCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunityRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.PARAM_ERROR);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionOpportunityRatioCacheUtil.TYPE_UNION_ID);
        return result;
    }

    public List<UnionOpportunityRatio> listByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunityRatio> result;
        // (1)cache
        String fromMemberIdKey = UnionOpportunityRatioCacheUtil.getFromMemberIdKey(fromMemberId);
        if (redisCacheUtil.exists(fromMemberIdKey)) {
            String tempStr = redisCacheUtil.get(fromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunityRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("from_member_id", fromMemberId)
                .eq("del_status", CommonConstant.PARAM_ERROR);
        result = selectList(entityWrapper);
        setCache(result, fromMemberId, UnionOpportunityRatioCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

    public List<UnionOpportunityRatio> listByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunityRatio> result;
        // (1)cache
        String toMemberIdKey = UnionOpportunityRatioCacheUtil.getToMemberIdKey(toMemberId);
        if (redisCacheUtil.exists(toMemberIdKey)) {
            String tempStr = redisCacheUtil.get(toMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunityRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("to_member_id", toMemberId)
                .eq("del_status", CommonConstant.PARAM_ERROR);
        result = selectList(entityWrapper);
        setCache(result, toMemberId, UnionOpportunityRatioCacheUtil.TYPE_TO_MEMBER_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionOpportunityRatio newUnionOpportunityRatio) throws Exception {
        if (newUnionOpportunityRatio == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionOpportunityRatio);
        removeCache(newUnionOpportunityRatio);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionOpportunityRatio> newUnionOpportunityRatioList) throws Exception {
        if (newUnionOpportunityRatioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionOpportunityRatioList);
        removeCache(newUnionOpportunityRatioList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionOpportunityRatio unionOpportunityRatio = getById(id);
        removeCache(unionOpportunityRatio);
        // (2)remove in db logically
        UnionOpportunityRatio removeUnionOpportunityRatio = new UnionOpportunityRatio();
        removeUnionOpportunityRatio.setId(id);
        removeUnionOpportunityRatio.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionOpportunityRatio);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionOpportunityRatio> unionOpportunityRatioList = new ArrayList<>();
        for (Integer id : idList) {
            UnionOpportunityRatio unionOpportunityRatio = getById(id);
            unionOpportunityRatioList.add(unionOpportunityRatio);
        }
        removeCache(unionOpportunityRatioList);
        // (2)remove in db logically
        List<UnionOpportunityRatio> removeUnionOpportunityRatioList = new ArrayList<>();
        for (Integer id : idList) {
            UnionOpportunityRatio removeUnionOpportunityRatio = new UnionOpportunityRatio();
            removeUnionOpportunityRatio.setId(id);
            removeUnionOpportunityRatio.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionOpportunityRatioList.add(removeUnionOpportunityRatio);
        }
        updateBatchById(removeUnionOpportunityRatioList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionOpportunityRatio updateUnionOpportunityRatio) throws Exception {
        if (updateUnionOpportunityRatio == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionOpportunityRatio.getId();
        UnionOpportunityRatio unionOpportunityRatio = getById(id);
        removeCache(unionOpportunityRatio);
        // (2)update db
        updateById(updateUnionOpportunityRatio);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionOpportunityRatio> updateUnionOpportunityRatioList) throws Exception {
        if (updateUnionOpportunityRatioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionOpportunityRatio updateUnionOpportunityRatio : updateUnionOpportunityRatioList) {
            idList.add(updateUnionOpportunityRatio.getId());
        }
        List<UnionOpportunityRatio> unionOpportunityRatioList = new ArrayList<>();
        for (Integer id : idList) {
            UnionOpportunityRatio unionOpportunityRatio = getById(id);
            unionOpportunityRatioList.add(unionOpportunityRatio);
        }
        removeCache(unionOpportunityRatioList);
        // (2)update db
        updateBatchById(updateUnionOpportunityRatioList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionOpportunityRatio newUnionOpportunityRatio, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionOpportunityRatioCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionOpportunityRatio);
    }

    private void setCache(List<UnionOpportunityRatio> newUnionOpportunityRatioList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionOpportunityRatioCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionOpportunityRatioCacheUtil.getUnionIdKey(foreignId);
                break;
            case UnionOpportunityRatioCacheUtil.TYPE_FROM_MEMBER_ID:
                foreignIdKey = UnionOpportunityRatioCacheUtil.getFromMemberIdKey(foreignId);
                break;
            case UnionOpportunityRatioCacheUtil.TYPE_TO_MEMBER_ID:
                foreignIdKey = UnionOpportunityRatioCacheUtil.getToMemberIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionOpportunityRatioList);
        }
    }

    private void removeCache(UnionOpportunityRatio unionOpportunityRatio) {
        if (unionOpportunityRatio == null) {
            return;
        }
        Integer id = unionOpportunityRatio.getId();
        String idKey = UnionOpportunityRatioCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer unionId = unionOpportunityRatio.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionOpportunityRatioCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }

        Integer fromMemberId = unionOpportunityRatio.getFromMemberId();
        if (fromMemberId != null) {
            String fromMemberIdKey = UnionOpportunityRatioCacheUtil.getFromMemberIdKey(fromMemberId);
            redisCacheUtil.remove(fromMemberIdKey);
        }

        Integer toMemberId = unionOpportunityRatio.getToMemberId();
        if (toMemberId != null) {
            String toMemberIdKey = UnionOpportunityRatioCacheUtil.getToMemberIdKey(toMemberId);
            redisCacheUtil.remove(toMemberIdKey);
        }
    }

    private void removeCache(List<UnionOpportunityRatio> unionOpportunityRatioList) {
        if (ListUtil.isEmpty(unionOpportunityRatioList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionOpportunityRatio unionOpportunityRatio : unionOpportunityRatioList) {
            idList.add(unionOpportunityRatio.getId());
        }
        List<String> idKeyList = UnionOpportunityRatioCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> unionIdKeyList = getForeignIdKeyList(unionOpportunityRatioList, UnionOpportunityRatioCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

        List<String> fromMemberIdKeyList = getForeignIdKeyList(unionOpportunityRatioList, UnionOpportunityRatioCacheUtil.TYPE_FROM_MEMBER_ID);
        if (ListUtil.isNotEmpty(fromMemberIdKeyList)) {
            redisCacheUtil.remove(fromMemberIdKeyList);
        }

        List<String> toMemberIdKeyList = getForeignIdKeyList(unionOpportunityRatioList, UnionOpportunityRatioCacheUtil.TYPE_TO_MEMBER_ID);
        if (ListUtil.isNotEmpty(toMemberIdKeyList)) {
            redisCacheUtil.remove(toMemberIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionOpportunityRatio> unionOpportunityRatioList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionOpportunityRatioCacheUtil.TYPE_UNION_ID:
                for (UnionOpportunityRatio unionOpportunityRatio : unionOpportunityRatioList) {
                    Integer unionId = unionOpportunityRatio.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionOpportunityRatioCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            case UnionOpportunityRatioCacheUtil.TYPE_FROM_MEMBER_ID:
                for (UnionOpportunityRatio unionOpportunityRatio : unionOpportunityRatioList) {
                    Integer fromMemberId = unionOpportunityRatio.getFromMemberId();
                    if (fromMemberId != null) {
                        String fromMemberIdKey = UnionOpportunityRatioCacheUtil.getFromMemberIdKey(fromMemberId);
                        result.add(fromMemberIdKey);
                    }
                }
                break;
            case UnionOpportunityRatioCacheUtil.TYPE_TO_MEMBER_ID:
                for (UnionOpportunityRatio unionOpportunityRatio : unionOpportunityRatioList) {
                    Integer toMemberId = unionOpportunityRatio.getToMemberId();
                    if (toMemberId != null) {
                        String toMemberIdKey = UnionOpportunityRatioCacheUtil.getToMemberIdKey(toMemberId);
                        result.add(toMemberIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}