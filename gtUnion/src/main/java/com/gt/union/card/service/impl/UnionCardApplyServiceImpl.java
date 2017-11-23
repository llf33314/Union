package com.gt.union.card.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.entity.UnionCardApply;
import com.gt.union.card.mapper.UnionCardApplyMapper;
import com.gt.union.card.service.IUnionCardApplyService;
import com.gt.union.card.util.UnionCardApplyCacheUtil;
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
 * 联盟卡购买记录 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Service
public class UnionCardApplyServiceImpl extends ServiceImpl<UnionCardApplyMapper, UnionCardApply> implements IUnionCardApplyService {
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

    public UnionCardApply getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardApply result;
        // (1)cache
        String idKey = UnionCardApplyCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionCardApply.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardApply> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionCardApply> listByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardApply> result;
        // (1)cache
        String cardIdKey = UnionCardApplyCacheUtil.getCardIdKey(cardId);
        if (redisCacheUtil.exists(cardIdKey)) {
            String tempStr = redisCacheUtil.get(cardIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardApply.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardApply> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("card_id", cardId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, cardId, UnionCardApplyCacheUtil.TYPE_CARD_ID);
        return result;
    }

    public List<UnionCardApply> listByRootId(Integer rootId) throws Exception {
        if (rootId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardApply> result;
        // (1)cache
        String rootIdKey = UnionCardApplyCacheUtil.getRootIdKey(rootId);
        if (redisCacheUtil.exists(rootIdKey)) {
            String tempStr = redisCacheUtil.get(rootIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardApply.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardApply> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("root_id", rootId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, rootId, UnionCardApplyCacheUtil.TYPE_ROOT_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardApply newUnionCardApply) throws Exception {
        if (newUnionCardApply == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionCardApply);
        removeCache(newUnionCardApply);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardApply> newUnionCardApplyList) throws Exception {
        if (newUnionCardApplyList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionCardApplyList);
        removeCache(newUnionCardApplyList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionCardApply unionCardApply = getById(id);
        removeCache(unionCardApply);
        // (2)remove in db logically
        UnionCardApply removeUnionCardApply = new UnionCardApply();
        removeUnionCardApply.setId(id);
        removeUnionCardApply.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionCardApply);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardApply> unionCardApplyList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardApply unionCardApply = getById(id);
            unionCardApplyList.add(unionCardApply);
        }
        removeCache(unionCardApplyList);
        // (2)remove in db logically
        List<UnionCardApply> removeUnionCardApplyList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardApply removeUnionCardApply = new UnionCardApply();
            removeUnionCardApply.setId(id);
            removeUnionCardApply.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardApplyList.add(removeUnionCardApply);
        }
        updateBatchById(removeUnionCardApplyList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardApply updateUnionCardApply) throws Exception {
        if (updateUnionCardApply == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCardApply.getId();
        UnionCardApply unionCardApply = getById(id);
        removeCache(unionCardApply);
        // (2)update db
        updateById(updateUnionCardApply);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardApply> updateUnionCardApplyList) throws Exception {
        if (updateUnionCardApplyList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionCardApply updateUnionCardApply : updateUnionCardApplyList) {
            idList.add(updateUnionCardApply.getId());
        }
        List<UnionCardApply> unionCardApplyList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardApply unionCardApply = getById(id);
            unionCardApplyList.add(unionCardApply);
        }
        removeCache(unionCardApplyList);
        // (2)update db
        updateBatchById(updateUnionCardApplyList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionCardApply newUnionCardApply, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardApplyCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCardApply);
    }

    private void setCache(List<UnionCardApply> newUnionCardApplyList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionCardApplyCacheUtil.TYPE_CARD_ID:
                foreignIdKey = UnionCardApplyCacheUtil.getCardIdKey(foreignId);
                break;
            case UnionCardApplyCacheUtil.TYPE_ROOT_ID:
                foreignIdKey = UnionCardApplyCacheUtil.getRootIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionCardApplyList);
        }
    }

    private void removeCache(UnionCardApply unionCardApply) {
        if (unionCardApply == null) {
            return;
        }
        Integer id = unionCardApply.getId();
        String idKey = UnionCardApplyCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer cardId = unionCardApply.getCardId();
        if (cardId != null) {
            String cardIdKey = UnionCardApplyCacheUtil.getCardIdKey(cardId);
            redisCacheUtil.remove(cardIdKey);
        }

        Integer rootId = unionCardApply.getRootId();
        if (rootId != null) {
            String rootIdKey = UnionCardApplyCacheUtil.getRootIdKey(rootId);
            redisCacheUtil.remove(rootIdKey);
        }
    }

    private void removeCache(List<UnionCardApply> unionCardApplyList) {
        if (ListUtil.isEmpty(unionCardApplyList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCardApply unionCardApply : unionCardApplyList) {
            idList.add(unionCardApply.getId());
        }
        List<String> idKeyList = UnionCardApplyCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> cardIdKeyList = getForeignIdKeyList(unionCardApplyList, UnionCardApplyCacheUtil.TYPE_CARD_ID);
        if (ListUtil.isNotEmpty(cardIdKeyList)) {
            redisCacheUtil.remove(cardIdKeyList);
        }

        List<String> rootIdKeyList = getForeignIdKeyList(unionCardApplyList, UnionCardApplyCacheUtil.TYPE_ROOT_ID);
        if (ListUtil.isNotEmpty(rootIdKeyList)) {
            redisCacheUtil.remove(rootIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionCardApply> unionCardApplyList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionCardApplyCacheUtil.TYPE_CARD_ID:
                for (UnionCardApply unionCardApply : unionCardApplyList) {
                    Integer cardId = unionCardApply.getCardId();
                    if (cardId != null) {
                        String cardIdKey = UnionCardApplyCacheUtil.getCardIdKey(cardId);
                        result.add(cardIdKey);
                    }
                }
                break;
            case UnionCardApplyCacheUtil.TYPE_ROOT_ID:
                for (UnionCardApply unionCardApply : unionCardApplyList) {
                    Integer rootId = unionCardApply.getRootId();
                    if (rootId != null) {
                        String rootIdKey = UnionCardApplyCacheUtil.getRootIdKey(rootId);
                        result.add(rootIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}