package com.gt.union.card.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.main.entity.UnionCardRecord;
import com.gt.union.card.main.mapper.UnionCardRecordMapper;
import com.gt.union.card.main.service.IUnionCardRecordService;
import com.gt.union.card.main.util.UnionCardRecordCacheUtil;
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
public class UnionCardRecordServiceImpl extends ServiceImpl<UnionCardRecordMapper, UnionCardRecord> implements IUnionCardRecordService {
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

    public UnionCardRecord getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardRecord result;
        // (1)cache
        String idKey = UnionCardRecordCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionCardRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionCardRecord> listByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardRecord> result;
        // (1)cache
        String cardIdKey = UnionCardRecordCacheUtil.getCardIdKey(cardId);
        if (redisCacheUtil.exists(cardIdKey)) {
            String tempStr = redisCacheUtil.get(cardIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("card_id", cardId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, cardId, UnionCardRecordCacheUtil.TYPE_CARD_ID);
        return result;
    }

    public List<UnionCardRecord> listByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardRecord> result;
        // (1)cache
        String fanIdKey = UnionCardRecordCacheUtil.getFanIdKey(fanId);
        if (redisCacheUtil.exists(fanIdKey)) {
            String tempStr = redisCacheUtil.get(fanIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("fan_id", fanId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, fanId, UnionCardRecordCacheUtil.TYPE_FAN_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardRecord newUnionCardApply) throws Exception {
        if (newUnionCardApply == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionCardApply);
        removeCache(newUnionCardApply);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardRecord> newUnionCardApplyList) throws Exception {
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
        UnionCardRecord unionCardApply = getById(id);
        removeCache(unionCardApply);
        // (2)remove in db logically
        UnionCardRecord removeUnionCardApply = new UnionCardRecord();
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
        List<UnionCardRecord> unionCardApplyList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardRecord unionCardApply = getById(id);
            unionCardApplyList.add(unionCardApply);
        }
        removeCache(unionCardApplyList);
        // (2)remove in db logically
        List<UnionCardRecord> removeUnionCardApplyList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardRecord removeUnionCardApply = new UnionCardRecord();
            removeUnionCardApply.setId(id);
            removeUnionCardApply.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardApplyList.add(removeUnionCardApply);
        }
        updateBatchById(removeUnionCardApplyList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardRecord updateUnionCardApply) throws Exception {
        if (updateUnionCardApply == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCardApply.getId();
        UnionCardRecord unionCardApply = getById(id);
        removeCache(unionCardApply);
        // (2)update db
        updateById(updateUnionCardApply);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardRecord> updateUnionCardApplyList) throws Exception {
        if (updateUnionCardApplyList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionCardRecord updateUnionCardApply : updateUnionCardApplyList) {
            idList.add(updateUnionCardApply.getId());
        }
        List<UnionCardRecord> unionCardApplyList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardRecord unionCardApply = getById(id);
            unionCardApplyList.add(unionCardApply);
        }
        removeCache(unionCardApplyList);
        // (2)update db
        updateBatchById(updateUnionCardApplyList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionCardRecord newUnionCardApply, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardRecordCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCardApply);
    }

    private void setCache(List<UnionCardRecord> newUnionCardApplyList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionCardRecordCacheUtil.TYPE_CARD_ID:
                foreignIdKey = UnionCardRecordCacheUtil.getCardIdKey(foreignId);
                break;
            case UnionCardRecordCacheUtil.TYPE_FAN_ID:
                foreignIdKey = UnionCardRecordCacheUtil.getFanIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionCardApplyList);
        }
    }

    private void removeCache(UnionCardRecord unionCardApply) {
        if (unionCardApply == null) {
            return;
        }
        Integer id = unionCardApply.getId();
        String idKey = UnionCardRecordCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer cardId = unionCardApply.getCardId();
        if (cardId != null) {
            String cardIdKey = UnionCardRecordCacheUtil.getCardIdKey(cardId);
            redisCacheUtil.remove(cardIdKey);
        }

        Integer fanId = unionCardApply.getFanId();
        if (fanId != null) {
            String fanIdKey = UnionCardRecordCacheUtil.getFanIdKey(fanId);
            redisCacheUtil.remove(fanIdKey);
        }
    }

    private void removeCache(List<UnionCardRecord> unionCardApplyList) {
        if (ListUtil.isEmpty(unionCardApplyList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCardRecord unionCardApply : unionCardApplyList) {
            idList.add(unionCardApply.getId());
        }
        List<String> idKeyList = UnionCardRecordCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> cardIdKeyList = getForeignIdKeyList(unionCardApplyList, UnionCardRecordCacheUtil.TYPE_CARD_ID);
        if (ListUtil.isNotEmpty(cardIdKeyList)) {
            redisCacheUtil.remove(cardIdKeyList);
        }

        List<String> fanIdKeyList = getForeignIdKeyList(unionCardApplyList, UnionCardRecordCacheUtil.TYPE_FAN_ID);
        if (ListUtil.isNotEmpty(fanIdKeyList)) {
            redisCacheUtil.remove(fanIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionCardRecord> unionCardApplyList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionCardRecordCacheUtil.TYPE_CARD_ID:
                for (UnionCardRecord unionCardApply : unionCardApplyList) {
                    Integer cardId = unionCardApply.getCardId();
                    if (cardId != null) {
                        String cardIdKey = UnionCardRecordCacheUtil.getCardIdKey(cardId);
                        result.add(cardIdKey);
                    }
                }
                break;
            case UnionCardRecordCacheUtil.TYPE_FAN_ID:
                for (UnionCardRecord unionCardApply : unionCardApplyList) {
                    Integer fanId = unionCardApply.getFanId();
                    if (fanId != null) {
                        String fanIdKey = UnionCardRecordCacheUtil.getFanIdKey(fanId);
                        result.add(fanIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}