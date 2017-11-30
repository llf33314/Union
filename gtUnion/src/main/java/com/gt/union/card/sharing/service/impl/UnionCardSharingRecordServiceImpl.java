package com.gt.union.card.sharing.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.sharing.entity.UnionCardSharingRecord;
import com.gt.union.card.sharing.mapper.UnionCardSharingRecordMapper;
import com.gt.union.card.sharing.service.IUnionCardSharingRecordService;
import com.gt.union.card.sharing.util.UnionCardSharingRecordCacheUtil;
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
 * 联盟卡售卡分成记录 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Service
public class UnionCardSharingRecordServiceImpl extends ServiceImpl<UnionCardSharingRecordMapper, UnionCardSharingRecord> implements IUnionCardSharingRecordService {
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

    public UnionCardSharingRecord getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardSharingRecord result;
        // (1)cache
        String idKey = UnionCardSharingRecordCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionCardSharingRecord> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String memberIdKey = UnionCardSharingRecordCacheUtil.getMemberIdKey(memberId);
        if (redisCacheUtil.exists(memberIdKey)) {
            String tempStr = redisCacheUtil.get(memberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, memberId, UnionCardSharingRecordCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    public List<UnionCardSharingRecord> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String unionIdKey = UnionCardSharingRecordCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionCardSharingRecordCacheUtil.TYPE_UNION_ID);
        return result;
    }

    public List<UnionCardSharingRecord> listByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String activityIdKey = UnionCardSharingRecordCacheUtil.getActivityIdKey(activityId);
        if (redisCacheUtil.exists(activityIdKey)) {
            String tempStr = redisCacheUtil.get(activityIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_id", activityId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, activityId, UnionCardSharingRecordCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    public List<UnionCardSharingRecord> listByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String cardIdKey = UnionCardSharingRecordCacheUtil.getCardIdKey(cardId);
        if (redisCacheUtil.exists(cardIdKey)) {
            String tempStr = redisCacheUtil.get(cardIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("card_id", cardId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, cardId, UnionCardSharingRecordCacheUtil.TYPE_CARD_ID);
        return result;
    }

    public List<UnionCardSharingRecord> listByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String fanIdKey = UnionCardSharingRecordCacheUtil.getFanIdKey(fanId);
        if (redisCacheUtil.exists(fanIdKey)) {
            String tempStr = redisCacheUtil.get(fanIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("fan_id", fanId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, fanId, UnionCardSharingRecordCacheUtil.TYPE_FAN_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardSharingRecord newUnionCardSharingRecord) throws Exception {
        if (newUnionCardSharingRecord == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionCardSharingRecord);
        removeCache(newUnionCardSharingRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardSharingRecord> newUnionCardSharingRecordList) throws Exception {
        if (newUnionCardSharingRecordList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionCardSharingRecordList);
        removeCache(newUnionCardSharingRecordList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionCardSharingRecord unionCardSharingRecord = getById(id);
        removeCache(unionCardSharingRecord);
        // (2)remove in db logically
        UnionCardSharingRecord removeUnionCardSharingRecord = new UnionCardSharingRecord();
        removeUnionCardSharingRecord.setId(id);
        removeUnionCardSharingRecord.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionCardSharingRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardSharingRecord> unionCardSharingRecordList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardSharingRecord unionCardSharingRecord = getById(id);
            unionCardSharingRecordList.add(unionCardSharingRecord);
        }
        removeCache(unionCardSharingRecordList);
        // (2)remove in db logically
        List<UnionCardSharingRecord> removeUnionCardSharingRecordList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardSharingRecord removeUnionCardSharingRecord = new UnionCardSharingRecord();
            removeUnionCardSharingRecord.setId(id);
            removeUnionCardSharingRecord.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardSharingRecordList.add(removeUnionCardSharingRecord);
        }
        updateBatchById(removeUnionCardSharingRecordList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardSharingRecord updateUnionCardSharingRecord) throws Exception {
        if (updateUnionCardSharingRecord == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCardSharingRecord.getId();
        UnionCardSharingRecord unionCardSharingRecord = getById(id);
        removeCache(unionCardSharingRecord);
        // (2)update db
        updateById(updateUnionCardSharingRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardSharingRecord> updateUnionCardSharingRecordList) throws Exception {
        if (updateUnionCardSharingRecordList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionCardSharingRecord updateUnionCardSharingRecord : updateUnionCardSharingRecordList) {
            idList.add(updateUnionCardSharingRecord.getId());
        }
        List<UnionCardSharingRecord> unionCardSharingRecordList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardSharingRecord unionCardSharingRecord = getById(id);
            unionCardSharingRecordList.add(unionCardSharingRecord);
        }
        removeCache(unionCardSharingRecordList);
        // (2)update db
        updateBatchById(updateUnionCardSharingRecordList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionCardSharingRecord newUnionCardSharingRecord, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardSharingRecordCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCardSharingRecord);
    }

    private void setCache(List<UnionCardSharingRecord> newUnionCardSharingRecordList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionCardSharingRecordCacheUtil.TYPE_MEMBER_ID:
                foreignIdKey = UnionCardSharingRecordCacheUtil.getMemberIdKey(foreignId);
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionCardSharingRecordCacheUtil.getUnionIdKey(foreignId);
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_ACTIVITY_ID:
                foreignIdKey = UnionCardSharingRecordCacheUtil.getActivityIdKey(foreignId);
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_CARD_ID:
                foreignIdKey = UnionCardSharingRecordCacheUtil.getCardIdKey(foreignId);
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_FAN_ID:
                foreignIdKey = UnionCardSharingRecordCacheUtil.getFanIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionCardSharingRecordList);
        }
    }

    private void removeCache(UnionCardSharingRecord unionCardSharingRecord) {
        if (unionCardSharingRecord == null) {
            return;
        }
        Integer id = unionCardSharingRecord.getId();
        String idKey = UnionCardSharingRecordCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer memberId = unionCardSharingRecord.getMemberId();
        if (memberId != null) {
            String memberIdKey = UnionCardSharingRecordCacheUtil.getMemberIdKey(memberId);
            redisCacheUtil.remove(memberIdKey);
        }

        Integer unionId = unionCardSharingRecord.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionCardSharingRecordCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }

        Integer activityId = unionCardSharingRecord.getActivityId();
        if (activityId != null) {
            String activityIdKey = UnionCardSharingRecordCacheUtil.getActivityIdKey(activityId);
            redisCacheUtil.remove(activityIdKey);
        }

        Integer cardId = unionCardSharingRecord.getCardId();
        if (cardId != null) {
            String cardIdKey = UnionCardSharingRecordCacheUtil.getCardIdKey(cardId);
            redisCacheUtil.remove(cardIdKey);
        }

        Integer fanId = unionCardSharingRecord.getFanId();
        if (fanId != null) {
            String fanIdKey = UnionCardSharingRecordCacheUtil.getFanIdKey(fanId);
            redisCacheUtil.remove(fanIdKey);
        }
    }

    private void removeCache(List<UnionCardSharingRecord> unionCardSharingRecordList) {
        if (ListUtil.isEmpty(unionCardSharingRecordList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCardSharingRecord unionCardSharingRecord : unionCardSharingRecordList) {
            idList.add(unionCardSharingRecord.getId());
        }
        List<String> idKeyList = UnionCardSharingRecordCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> memberIdKeyList = getForeignIdKeyList(unionCardSharingRecordList, UnionCardSharingRecordCacheUtil.TYPE_MEMBER_ID);
        if (ListUtil.isNotEmpty(memberIdKeyList)) {
            redisCacheUtil.remove(memberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionCardSharingRecordList, UnionCardSharingRecordCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

        List<String> activityIdKeyList = getForeignIdKeyList(unionCardSharingRecordList, UnionCardSharingRecordCacheUtil.TYPE_ACTIVITY_ID);
        if (ListUtil.isNotEmpty(activityIdKeyList)) {
            redisCacheUtil.remove(activityIdKeyList);
        }

        List<String> cardIdKeyList = getForeignIdKeyList(unionCardSharingRecordList, UnionCardSharingRecordCacheUtil.TYPE_CARD_ID);
        if (ListUtil.isNotEmpty(cardIdKeyList)) {
            redisCacheUtil.remove(cardIdKeyList);
        }

        List<String> fanIdKeyList = getForeignIdKeyList(unionCardSharingRecordList, UnionCardSharingRecordCacheUtil.TYPE_FAN_ID);
        if (ListUtil.isNotEmpty(fanIdKeyList)) {
            redisCacheUtil.remove(fanIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionCardSharingRecord> unionCardSharingRecordList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionCardSharingRecordCacheUtil.TYPE_MEMBER_ID:
                for (UnionCardSharingRecord unionCardSharingRecord : unionCardSharingRecordList) {
                    Integer memberId = unionCardSharingRecord.getMemberId();
                    if (memberId != null) {
                        String memberIdKey = UnionCardSharingRecordCacheUtil.getMemberIdKey(memberId);
                        result.add(memberIdKey);
                    }
                }
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_UNION_ID:
                for (UnionCardSharingRecord unionCardSharingRecord : unionCardSharingRecordList) {
                    Integer unionId = unionCardSharingRecord.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionCardSharingRecordCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_ACTIVITY_ID:
                for (UnionCardSharingRecord unionCardSharingRecord : unionCardSharingRecordList) {
                    Integer activityId = unionCardSharingRecord.getActivityId();
                    if (activityId != null) {
                        String activityIdKey = UnionCardSharingRecordCacheUtil.getActivityIdKey(activityId);
                        result.add(activityIdKey);
                    }
                }
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_CARD_ID:
                for (UnionCardSharingRecord unionCardSharingRecord : unionCardSharingRecordList) {
                    Integer cardId = unionCardSharingRecord.getCardId();
                    if (cardId != null) {
                        String cardIdKey = UnionCardSharingRecordCacheUtil.getCardIdKey(cardId);
                        result.add(cardIdKey);
                    }
                }
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_FAN_ID:
                for (UnionCardSharingRecord unionCardSharingRecord : unionCardSharingRecordList) {
                    Integer fanId = unionCardSharingRecord.getFanId();
                    if (fanId != null) {
                        String fanIdKey = UnionCardSharingRecordCacheUtil.getFanIdKey(fanId);
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