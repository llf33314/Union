package com.gt.union.member.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.member.entity.UnionMemberOut;
import com.gt.union.member.mapper.UnionMemberOutMapper;
import com.gt.union.member.service.IUnionMemberOutService;
import com.gt.union.member.util.UnionMemberOutCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 退盟申请 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 11:45:12
 */
@Service
public class UnionMemberOutServiceImpl extends ServiceImpl<UnionMemberOutMapper, UnionMemberOut> implements IUnionMemberOutService {
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

    public UnionMemberOut getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMemberOut result;
        // (1)cache
        String idKey = UnionMemberOutCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionMemberOut.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberOut> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionMemberOut> listByApplyMemberId(Integer applyMemberId) throws Exception {
        if (applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberOut> result;
        // (1)cache
        String applyMemberIdKey = UnionMemberOutCacheUtil.getApplyMemberIdKey(applyMemberId);
        if (redisCacheUtil.exists(applyMemberIdKey)) {
            String tempStr = redisCacheUtil.get(applyMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberOut.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberOut> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("apply_member_id", applyMemberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, applyMemberId, UnionMemberOutCacheUtil.TYPE_APPLY_MEMBER_ID);
        return result;
    }

    public List<UnionMemberOut> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberOut> result;
        // (1)cache
        String unionIdKey = UnionMemberOutCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberOut.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberOut> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionMemberOutCacheUtil.TYPE_UNION_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMemberOut newUnionMemberOut) throws Exception {
        if (newUnionMemberOut == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionMemberOut);
        removeCache(newUnionMemberOut);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMemberOut> newUnionMemberOutList) throws Exception {
        if (newUnionMemberOutList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionMemberOutList);
        removeCache(newUnionMemberOutList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionMemberOut unionMemberOut = getById(id);
        removeCache(unionMemberOut);
        // (2)remove in db logically
        UnionMemberOut removeUnionMemberOut = new UnionMemberOut();
        removeUnionMemberOut.setId(id);
        removeUnionMemberOut.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionMemberOut);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMemberOut> unionMemberOutList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMemberOut unionMemberOut = getById(id);
            unionMemberOutList.add(unionMemberOut);
        }
        removeCache(unionMemberOutList);
        // (2)remove in db logically
        List<UnionMemberOut> removeUnionMemberOutList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMemberOut removeUnionMemberOut = new UnionMemberOut();
            removeUnionMemberOut.setId(id);
            removeUnionMemberOut.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMemberOutList.add(removeUnionMemberOut);
        }
        updateBatchById(removeUnionMemberOutList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMemberOut updateUnionMemberOut) throws Exception {
        if (updateUnionMemberOut == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer unionMemberOutId = updateUnionMemberOut.getId();
        UnionMemberOut unionMemberOut = getById(unionMemberOutId);
        removeCache(unionMemberOut);
        // (2)update db
        updateById(updateUnionMemberOut);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMemberOut> updateUnionMemberOutList) throws Exception {
        if (updateUnionMemberOutList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> unionMemberOutIdList = new ArrayList<>();
        for (UnionMemberOut updateUnionMemberOut : updateUnionMemberOutList) {
            unionMemberOutIdList.add(updateUnionMemberOut.getId());
        }
        List<UnionMemberOut> unionMemberOutList = new ArrayList<>();
        for (Integer unionMemberOutId : unionMemberOutIdList) {
            UnionMemberOut unionMemberOut = getById(unionMemberOutId);
            unionMemberOutList.add(unionMemberOut);
        }
        removeCache(unionMemberOutList);
        // (2)update db
        updateBatchById(updateUnionMemberOutList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionMemberOut newUnionMemberOut, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionMemberOutCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionMemberOut);
    }

    private void setCache(List<UnionMemberOut> newUnionMemberOutList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionMemberOutCacheUtil.TYPE_APPLY_MEMBER_ID:
                foreignIdKey = UnionMemberOutCacheUtil.getApplyMemberIdKey(foreignId);
                break;
            case UnionMemberOutCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionMemberOutCacheUtil.getUnionIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMemberOutList);
        }
    }

    private void removeCache(UnionMemberOut unionMemberOut) {
        if (unionMemberOut == null) {
            return;
        }
        Integer id = unionMemberOut.getId();
        String idKey = UnionMemberOutCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer applyMemberId = unionMemberOut.getApplyMemberId();
        if (applyMemberId != null) {
            String applyMemberIdKey = UnionMemberOutCacheUtil.getApplyMemberIdKey(applyMemberId);
            redisCacheUtil.remove(applyMemberIdKey);
        }

        Integer unionId = unionMemberOut.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionMemberOutCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }
    }

    private void removeCache(List<UnionMemberOut> unionMemberOutList) {
        if (ListUtil.isEmpty(unionMemberOutList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionMemberOut unionMemberOut : unionMemberOutList) {
            idList.add(unionMemberOut.getId());
        }
        List<String> idKeyList = UnionMemberOutCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> applyMemberIdKeyList = getForeignIdKeyList(unionMemberOutList, UnionMemberOutCacheUtil.TYPE_APPLY_MEMBER_ID);
        if (ListUtil.isNotEmpty(applyMemberIdKeyList)) {
            redisCacheUtil.remove(applyMemberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionMemberOutList, UnionMemberOutCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionMemberOut> unionMemberOutList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionMemberOutCacheUtil.TYPE_APPLY_MEMBER_ID:
                for (UnionMemberOut unionMemberOut : unionMemberOutList) {
                    Integer applyMemberId = unionMemberOut.getApplyMemberId();
                    if (applyMemberId != null) {
                        String applyMemberIdKey = UnionMemberOutCacheUtil.getApplyMemberIdKey(applyMemberId);
                        result.add(applyMemberIdKey);
                    }
                }
                break;
            case UnionMemberOutCacheUtil.TYPE_UNION_ID:
                for (UnionMemberOut unionMemberOut : unionMemberOutList) {
                    Integer unionId = unionMemberOut.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionMemberOutCacheUtil.getUnionIdKey(unionId);
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