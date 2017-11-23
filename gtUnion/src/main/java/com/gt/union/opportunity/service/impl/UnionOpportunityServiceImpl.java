package com.gt.union.opportunity.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.opportunity.entity.UnionOpportunity;
import com.gt.union.opportunity.mapper.UnionOpportunityMapper;
import com.gt.union.opportunity.service.IUnionOpportunityService;
import com.gt.union.opportunity.util.UnionOpportunityCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 商机 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 16:56:17
 */
@Service
public class UnionOpportunityServiceImpl extends ServiceImpl<UnionOpportunityMapper, UnionOpportunity> implements IUnionOpportunityService {
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

    public UnionOpportunity getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionOpportunity result;
        // (1)cache
        String idKey = UnionOpportunityCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionOpportunity.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionOpportunity> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunity> result;
        // (1)cache
        String unionIdKey = UnionOpportunityCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunity.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionOpportunityCacheUtil.TYPE_UNION_ID);
        return result;
    }

    public List<UnionOpportunity> listByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunity> result;
        // (1)cache
        String fromMemberIdKey = UnionOpportunityCacheUtil.getFromMemberIdKey(fromMemberId);
        if (redisCacheUtil.exists(fromMemberIdKey)) {
            String tempStr = redisCacheUtil.get(fromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunity.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("from_member_id", fromMemberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, fromMemberId, UnionOpportunityCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

    public List<UnionOpportunity> listByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunity> result;
        // (1)cache
        String toMemberIdKey = UnionOpportunityCacheUtil.getToMemberIdKey(toMemberId);
        if (redisCacheUtil.exists(toMemberIdKey)) {
            String tempStr = redisCacheUtil.get(toMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunity.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("to_member_id", toMemberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, toMemberId, UnionOpportunityCacheUtil.TYPE_TO_MEMBER_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionOpportunity newUnionOpportunity) throws Exception {
        if (newUnionOpportunity == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionOpportunity);
        removeCache(newUnionOpportunity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionOpportunity> newUnionOpportunityList) throws Exception {
        if (newUnionOpportunityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionOpportunityList);
        removeCache(newUnionOpportunityList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionOpportunity unionOpportunity = getById(id);
        removeCache(unionOpportunity);
        // (2)remove in db logically
        UnionOpportunity removeUnionOpportunity = new UnionOpportunity();
        removeUnionOpportunity.setId(id);
        removeUnionOpportunity.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionOpportunity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionOpportunity> unionOpportunityList = new ArrayList<>();
        for (Integer id : idList) {
            UnionOpportunity unionOpportunity = getById(id);
            unionOpportunityList.add(unionOpportunity);
        }
        removeCache(unionOpportunityList);
        // (2)remove in db logically
        List<UnionOpportunity> removeUnionOpportunityList = new ArrayList<>();
        for (Integer id : idList) {
            UnionOpportunity removeUnionOpportunity = new UnionOpportunity();
            removeUnionOpportunity.setId(id);
            removeUnionOpportunity.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionOpportunityList.add(removeUnionOpportunity);
        }
        updateBatchById(removeUnionOpportunityList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionOpportunity updateUnionOpportunity) throws Exception {
        if (updateUnionOpportunity == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionOpportunity.getId();
        UnionOpportunity unionOpportunity = getById(id);
        removeCache(unionOpportunity);
        // (2)update db
        updateById(updateUnionOpportunity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionOpportunity> updateUnionOpportunityList) throws Exception {
        if (updateUnionOpportunityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionOpportunity updateUnionOpportunity : updateUnionOpportunityList) {
            idList.add(updateUnionOpportunity.getId());
        }
        List<UnionOpportunity> unionOpportunityList = new ArrayList<>();
        for (Integer id : idList) {
            UnionOpportunity unionOpportunity = getById(id);
            unionOpportunityList.add(unionOpportunity);
        }
        removeCache(unionOpportunityList);
        // (2)update db
        updateBatchById(updateUnionOpportunityList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionOpportunity newUnionOpportunity, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionOpportunityCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionOpportunity);
    }

    private void setCache(List<UnionOpportunity> newUnionOpportunityList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionOpportunityCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionOpportunityCacheUtil.getUnionIdKey(foreignId);
                break;
            case UnionOpportunityCacheUtil.TYPE_FROM_MEMBER_ID:
                foreignIdKey = UnionOpportunityCacheUtil.getFromMemberIdKey(foreignId);
                break;
            case UnionOpportunityCacheUtil.TYPE_TO_MEMBER_ID:
                foreignIdKey = UnionOpportunityCacheUtil.getToMemberIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionOpportunityList);
        }
    }

    private void removeCache(UnionOpportunity unionOpportunity) {
        if (unionOpportunity == null) {
            return;
        }
        Integer id = unionOpportunity.getId();
        String idKey = UnionOpportunityCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer unionId = unionOpportunity.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionOpportunityCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }

        Integer fromMemberId = unionOpportunity.getFromMemberId();
        if (fromMemberId != null) {
            String fromMemberIdKey = UnionOpportunityCacheUtil.getFromMemberIdKey(fromMemberId);
            redisCacheUtil.remove(fromMemberIdKey);
        }

        Integer toMemberId = unionOpportunity.getToMemberId();
        if (toMemberId != null) {
            String toMemberIdKey = UnionOpportunityCacheUtil.getToMemberIdKey(toMemberId);
            redisCacheUtil.remove(toMemberIdKey);
        }
    }

    private void removeCache(List<UnionOpportunity> unionOpportunityList) {
        if (ListUtil.isEmpty(unionOpportunityList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionOpportunity unionOpportunity : unionOpportunityList) {
            idList.add(unionOpportunity.getId());
        }
        List<String> idKeyList = UnionOpportunityCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> unionIdKeyList = getForeignIdKeyList(unionOpportunityList, UnionOpportunityCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

        List<String> fromMemberIdKeyList = getForeignIdKeyList(unionOpportunityList, UnionOpportunityCacheUtil.TYPE_FROM_MEMBER_ID);
        if (ListUtil.isNotEmpty(fromMemberIdKeyList)) {
            redisCacheUtil.remove(fromMemberIdKeyList);
        }

        List<String> toMemberIdKeyList = getForeignIdKeyList(unionOpportunityList, UnionOpportunityCacheUtil.TYPE_TO_MEMBER_ID);
        if (ListUtil.isNotEmpty(toMemberIdKeyList)) {
            redisCacheUtil.remove(toMemberIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionOpportunity> unionOpportunityList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionOpportunityCacheUtil.TYPE_UNION_ID:
                for (UnionOpportunity unionOpportunity : unionOpportunityList) {
                    Integer unionId = unionOpportunity.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionOpportunityCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            case UnionOpportunityCacheUtil.TYPE_FROM_MEMBER_ID:
                for (UnionOpportunity unionOpportunity : unionOpportunityList) {
                    Integer fromMemberId = unionOpportunity.getFromMemberId();
                    if (fromMemberId != null) {
                        String fromMemberIdKey = UnionOpportunityCacheUtil.getFromMemberIdKey(fromMemberId);
                        result.add(fromMemberIdKey);
                    }
                }
                break;
            case UnionOpportunityCacheUtil.TYPE_TO_MEMBER_ID:
                for (UnionOpportunity unionOpportunity : unionOpportunityList) {
                    Integer toMemberId = unionOpportunity.getToMemberId();
                    if (toMemberId != null) {
                        String toMemberIdKey = UnionOpportunityCacheUtil.getToMemberIdKey(toMemberId);
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