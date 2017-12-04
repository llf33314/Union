package com.gt.union.union.member.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.union.member.entity.UnionMemberJoin;
import com.gt.union.union.member.mapper.UnionMemberJoinMapper;
import com.gt.union.union.member.service.IUnionMemberJoinService;
import com.gt.union.union.member.util.UnionMemberJoinCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 入盟申请 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 11:45:12
 */
@Service
public class UnionMemberJoinServiceImpl extends ServiceImpl<UnionMemberJoinMapper, UnionMemberJoin> implements IUnionMemberJoinService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    //***************************************** Domain Driven Design - get *********************************************

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Object As a Service - get **********************************************

    public UnionMemberJoin getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMemberJoin result;
        // (1)cache
        String idKey = UnionMemberJoinCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionMemberJoin.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberJoin> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionMemberJoin> listByApplyMemberId(Integer applyMemberId) throws Exception {
        if (applyMemberId != null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberJoin> result;
        // (1)cache
        String applyMemberIdKey = UnionMemberJoinCacheUtil.getApplyMemberIdKey(applyMemberId);
        if (redisCacheUtil.exists(applyMemberIdKey)) {
            String tempStr = redisCacheUtil.get(applyMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberJoin.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberJoin> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("apply_member_id", applyMemberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, applyMemberId, UnionMemberJoinCacheUtil.TYPE_APPLY_MEMBER_ID);
        return result;
    }

    public List<UnionMemberJoin> listByRecommendMemberId(Integer recommendMemberId) throws Exception {
        if (recommendMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberJoin> result;
        // (1)cache
        String recommendMemberIdKey = UnionMemberJoinCacheUtil.getRecommendMemberIdKey(recommendMemberId);
        if (redisCacheUtil.exists(recommendMemberIdKey)) {
            String tempStr = redisCacheUtil.get(recommendMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberJoin.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberJoin> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("recommend_member_id", recommendMemberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, recommendMemberId, UnionMemberJoinCacheUtil.TYPE_RECOMMEND_MEMBER_ID);
        return result;
    }

    public List<UnionMemberJoin> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberJoin> result;
        // (1)cache
        String unionIdKey = UnionMemberJoinCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberJoin.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberJoin> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionMemberJoinCacheUtil.TYPE_UNION_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMemberJoin newUnionMemberJoin) throws Exception {
        if (newUnionMemberJoin == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionMemberJoin);
        removeCache(newUnionMemberJoin);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMemberJoin> newUnionMemberJoinList) throws Exception {
        if (newUnionMemberJoinList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionMemberJoinList);
        removeCache(newUnionMemberJoinList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionMemberJoin unionMemberJoin = getById(id);
        removeCache(unionMemberJoin);
        // (2)remove in db logically
        UnionMemberJoin removeUnionMemberJoin = new UnionMemberJoin();
        removeUnionMemberJoin.setId(id);
        removeUnionMemberJoin.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionMemberJoin);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMemberJoin> unionMemberJoinList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMemberJoin unionMemberJoin = getById(id);
            unionMemberJoinList.add(unionMemberJoin);
        }
        removeCache(unionMemberJoinList);
        // (2)remove in db logically
        List<UnionMemberJoin> removeUnionMemberJoinList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMemberJoin removeUnionMemberJoin = new UnionMemberJoin();
            removeUnionMemberJoin.setId(id);
            removeUnionMemberJoin.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMemberJoinList.add(removeUnionMemberJoin);
        }
        updateBatchById(removeUnionMemberJoinList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMemberJoin updateUnionMemberJoin) throws Exception {
        if (updateUnionMemberJoin == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionMemberJoin.getId();
        UnionMemberJoin unionMemberJoin = getById(id);
        removeCache(unionMemberJoin);
        // (2)update db
        updateById(updateUnionMemberJoin);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMemberJoin> updateUnionMemberJoinList) throws Exception {
        if (updateUnionMemberJoinList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionMemberJoin updateUnionMemberJoin : updateUnionMemberJoinList) {
            idList.add(updateUnionMemberJoin.getId());
        }
        List<UnionMemberJoin> unionMemberJoinList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMemberJoin unionMemberJoin = getById(id);
            unionMemberJoinList.add(unionMemberJoin);
        }
        removeCache(unionMemberJoinList);
        // (2)update db
        updateBatchById(updateUnionMemberJoinList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionMemberJoin newUnionMemberJoin, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionMemberJoinCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionMemberJoin);
    }

    private void setCache(List<UnionMemberJoin> newUnionMemberJoinList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionMemberJoinCacheUtil.TYPE_APPLY_MEMBER_ID:
                foreignIdKey = UnionMemberJoinCacheUtil.getApplyMemberIdKey(foreignId);
                break;
            case UnionMemberJoinCacheUtil.TYPE_RECOMMEND_MEMBER_ID:
                foreignIdKey = UnionMemberJoinCacheUtil.getRecommendMemberIdKey(foreignId);
                break;
            case UnionMemberJoinCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionMemberJoinCacheUtil.getUnionIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMemberJoinList);
        }
    }

    private void removeCache(UnionMemberJoin unionMemberJoin) {
        if (unionMemberJoin == null) {
            return;
        }
        Integer id = unionMemberJoin.getId();
        String idKey = UnionMemberJoinCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer applyMemberId = unionMemberJoin.getApplyMemberId();
        if (applyMemberId != null) {
            String applyMemberIdKey = UnionMemberJoinCacheUtil.getApplyMemberIdKey(applyMemberId);
            redisCacheUtil.remove(applyMemberIdKey);
        }

        Integer recommendMemberId = unionMemberJoin.getRecommendMemberId();
        if (recommendMemberId != null) {
            String recommendMemberIdKey = UnionMemberJoinCacheUtil.getRecommendMemberIdKey(recommendMemberId);
            redisCacheUtil.remove(recommendMemberIdKey);
        }

        Integer unionId = unionMemberJoin.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionMemberJoinCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }
    }

    private void removeCache(List<UnionMemberJoin> unionMemberJoinList) {
        if (ListUtil.isEmpty(unionMemberJoinList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionMemberJoin unionMemberJoin : unionMemberJoinList) {
            idList.add(unionMemberJoin.getId());
        }
        List<String> idKeyList = UnionMemberJoinCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> applyMemberIdKeyList = getForeignIdKeyList(unionMemberJoinList, UnionMemberJoinCacheUtil.TYPE_APPLY_MEMBER_ID);
        if (ListUtil.isNotEmpty(applyMemberIdKeyList)) {
            redisCacheUtil.remove(applyMemberIdKeyList);
        }

        List<String> recommendMemberIdKeyList = getForeignIdKeyList(unionMemberJoinList, UnionMemberJoinCacheUtil.TYPE_RECOMMEND_MEMBER_ID);
        if (ListUtil.isNotEmpty(recommendMemberIdKeyList)) {
            redisCacheUtil.remove(recommendMemberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionMemberJoinList, UnionMemberJoinCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionMemberJoin> unionMemberJoinList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionMemberJoinCacheUtil.TYPE_APPLY_MEMBER_ID:
                for (UnionMemberJoin unionMemberJoin : unionMemberJoinList) {
                    Integer applyMemberId = unionMemberJoin.getApplyMemberId();
                    if (applyMemberId != null) {
                        String applyMemberIdKey = UnionMemberJoinCacheUtil.getApplyMemberIdKey(applyMemberId);
                        result.add(applyMemberIdKey);
                    }
                }
                break;
            case UnionMemberJoinCacheUtil.TYPE_RECOMMEND_MEMBER_ID:
                for (UnionMemberJoin unionMemberJoin : unionMemberJoinList) {
                    Integer recommendMemberId = unionMemberJoin.getRecommendMemberId();
                    if (recommendMemberId != null) {
                        String recommendMemberIdKey = UnionMemberJoinCacheUtil.getRecommendMemberIdKey(recommendMemberId);
                        result.add(recommendMemberIdKey);
                    }
                }
                break;
            case UnionMemberJoinCacheUtil.TYPE_UNION_ID:
                for (UnionMemberJoin unionMemberJoin : unionMemberJoinList) {
                    Integer unionId = unionMemberJoin.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionMemberJoinCacheUtil.getUnionIdKey(unionId);
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