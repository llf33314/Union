package com.gt.union.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.union.main.entity.UnionMainTransfer;
import com.gt.union.union.main.mapper.UnionMainTransferMapper;
import com.gt.union.union.main.service.IUnionMainTransferService;
import com.gt.union.union.main.util.UnionMainTransferCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟转移 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
 */
@Service
public class UnionMainTransferServiceImpl extends ServiceImpl<UnionMainTransferMapper, UnionMainTransfer> implements IUnionMainTransferService {
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

    public UnionMainTransfer getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainTransfer result;
        // (1)cache
        String idKey = UnionMainTransferCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionMainTransfer.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionMainTransfer> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainTransfer> result;
        // (1)cache
        String unionIdKey = UnionMainTransferCacheUtil.getUnionInKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainTransfer.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionMainTransferCacheUtil.TYPE_UNION_ID);
        return result;
    }

    public List<UnionMainTransfer> listByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainTransfer> result;
        // (1)cache
        String fromMemberIdKey = UnionMainTransferCacheUtil.getFromMemberIdKey(fromMemberId);
        if (redisCacheUtil.exists(fromMemberIdKey)) {
            String tempStr = redisCacheUtil.get(fromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainTransfer.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("from_member_id", fromMemberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, fromMemberId, UnionMainTransferCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

    public List<UnionMainTransfer> listByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainTransfer> result;
        // (1)cache
        String toMemberIdKey = UnionMainTransferCacheUtil.getToMemberIdKey(toMemberId);
        if (redisCacheUtil.exists(toMemberIdKey)) {
            String tempStr = redisCacheUtil.get(toMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainTransfer.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("to_member_id", toMemberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, toMemberId, UnionMainTransferCacheUtil.TYPE_TO_MEMBER_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMainTransfer newUnionMainTransfer) throws Exception {
        if (newUnionMainTransfer == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionMainTransfer);
        removeCache(newUnionMainTransfer);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMainTransfer> newUnionMainTransferList) throws Exception {
        if (newUnionMainTransferList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionMainTransferList);
        removeCache(newUnionMainTransferList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionMainTransfer unionMainTransfer = getById(id);
        removeCache(unionMainTransfer);
        // (2)remove in db logically
        UnionMainTransfer removeUnionMainTransfer = new UnionMainTransfer();
        removeUnionMainTransfer.setId(id);
        removeUnionMainTransfer.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionMainTransfer);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMainTransfer> unionMainTransferList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainTransfer unionMainTransfer = getById(id);
            unionMainTransferList.add(unionMainTransfer);
        }
        removeCache(unionMainTransferList);
        // (2)remove in db logically
        List<UnionMainTransfer> removeUnionMainTransferList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainTransfer removeUnionMainTransfer = new UnionMainTransfer();
            removeUnionMainTransfer.setId(id);
            removeUnionMainTransfer.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMainTransferList.add(removeUnionMainTransfer);
        }
        updateBatchById(removeUnionMainTransferList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMainTransfer updateUnionMainTransfer) throws Exception {
        if (updateUnionMainTransfer == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionMainTransfer.getId();
        UnionMainTransfer unionMainTransfer = getById(id);
        removeCache(unionMainTransfer);
        // (2)update db
        updateById(updateUnionMainTransfer);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMainTransfer> updateUnionMainTransferList) throws Exception {
        if (updateUnionMainTransferList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionMainTransfer updateUnionMainTransfer : updateUnionMainTransferList) {
            idList.add(updateUnionMainTransfer.getId());
        }
        List<UnionMainTransfer> unionMainTransferList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainTransfer unionMainTransfer = getById(id);
            unionMainTransferList.add(unionMainTransfer);
        }
        removeCache(unionMainTransferList);
        // (2)update db
        updateBatchById(updateUnionMainTransferList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionMainTransfer newUnionMainTransfer, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionMainTransferCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionMainTransfer);
    }

    private void setCache(List<UnionMainTransfer> newUnionMainTransferList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionMainTransferCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionMainTransferCacheUtil.getUnionInKey(foreignId);
                break;
            case UnionMainTransferCacheUtil.TYPE_FROM_MEMBER_ID:
                foreignIdKey = UnionMainTransferCacheUtil.getFromMemberIdKey(foreignId);
                break;
            case UnionMainTransferCacheUtil.TYPE_TO_MEMBER_ID:
                foreignIdKey = UnionMainTransferCacheUtil.getToMemberIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMainTransferList);
        }
    }

    private void removeCache(UnionMainTransfer unionMainTransfer) {
        if (unionMainTransfer == null) {
            return;
        }
        Integer id = unionMainTransfer.getId();
        String idKey = UnionMainTransferCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer unionId = unionMainTransfer.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionMainTransferCacheUtil.getUnionInKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }

        Integer fromMemberId = unionMainTransfer.getFromMemberId();
        if (fromMemberId != null) {
            String fromMemberIdKey = UnionMainTransferCacheUtil.getFromMemberIdKey(fromMemberId);
            redisCacheUtil.remove(fromMemberIdKey);
        }

        Integer toMemberId = unionMainTransfer.getToMemberId();
        if (toMemberId != null) {
            String toMemberIdKey = UnionMainTransferCacheUtil.getToMemberIdKey(toMemberId);
            redisCacheUtil.remove(toMemberIdKey);
        }
    }

    private void removeCache(List<UnionMainTransfer> unionMainTransferList) {
        if (ListUtil.isEmpty(unionMainTransferList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionMainTransfer unionMainTransfer : unionMainTransferList) {
            idList.add(unionMainTransfer.getId());
        }
        List<String> idKeyList = UnionMainTransferCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> unionIdKeyList = getForeignIdKeyList(unionMainTransferList, UnionMainTransferCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

        List<String> fromMemberIdKeyList = getForeignIdKeyList(unionMainTransferList, UnionMainTransferCacheUtil.TYPE_FROM_MEMBER_ID);
        if (ListUtil.isNotEmpty(fromMemberIdKeyList)) {
            redisCacheUtil.remove(fromMemberIdKeyList);
        }

        List<String> toMemberIdKeyList = getForeignIdKeyList(unionMainTransferList, UnionMainTransferCacheUtil.TYPE_TO_MEMBER_ID);
        if (ListUtil.isNotEmpty(toMemberIdKeyList)) {
            redisCacheUtil.remove(toMemberIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionMainTransfer> unionMainTransferList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionMainTransferCacheUtil.TYPE_UNION_ID:
                for (UnionMainTransfer unionMainTransfer : unionMainTransferList) {
                    Integer unionId = unionMainTransfer.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionMainTransferCacheUtil.getUnionInKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            case UnionMainTransferCacheUtil.TYPE_FROM_MEMBER_ID:
                for (UnionMainTransfer unionMainTransfer : unionMainTransferList) {
                    Integer fromMemberId = unionMainTransfer.getFromMemberId();
                    if (fromMemberId != null) {
                        String fromMemberIdKey = UnionMainTransferCacheUtil.getFromMemberIdKey(fromMemberId);
                        result.add(fromMemberIdKey);
                    }
                }
                break;
            case UnionMainTransferCacheUtil.TYPE_TO_MEMBER_ID:
                for (UnionMainTransfer unionMainTransfer : unionMainTransferList) {
                    Integer toMemberId = unionMainTransfer.getToMemberId();
                    if (toMemberId != null) {
                        String toMemberIdKey = UnionMainTransferCacheUtil.getToMemberIdKey(toMemberId);
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