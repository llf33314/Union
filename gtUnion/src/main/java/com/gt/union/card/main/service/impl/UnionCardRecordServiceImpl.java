package com.gt.union.card.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.main.dao.IUnionCardRecordDao;
import com.gt.union.card.main.entity.UnionCardRecord;
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
public class UnionCardRecordServiceImpl implements IUnionCardRecordService {
    @Autowired
    private IUnionCardRecordDao unionCardRecordDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<UnionCardRecord> listValidByOrderNo(String orderNo) throws Exception {
        if (orderNo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .eq("order_no", orderNo);

        return unionCardRecordDao.selectList(entityWrapper);
    }

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionCardRecord> filterByDelStatus(List<UnionCardRecord> unionCardRecordList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardRecord> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardRecordList)) {
            for (UnionCardRecord unionCardRecord : unionCardRecordList) {
                if (delStatus.equals(unionCardRecord.getDelStatus())) {
                    result.add(unionCardRecord);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
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
        result = unionCardRecordDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionCardRecord getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardRecord result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionCardRecord getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardRecord result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionCardRecord> unionCardRecordList) throws Exception {
        if (unionCardRecordList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardRecordList)) {
            for (UnionCardRecord unionCardRecord : unionCardRecordList) {
                result.add(unionCardRecord.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionCardRecord> listByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardRecord> result;
        // (1)cache
        String activityIdKey = UnionCardRecordCacheUtil.getActivityIdKey(activityId);
        if (redisCacheUtil.exists(activityIdKey)) {
            String tempStr = redisCacheUtil.get(activityIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_id", activityId);
        result = unionCardRecordDao.selectList(entityWrapper);
        setCache(result, activityId, UnionCardRecordCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    @Override
    public List<UnionCardRecord> listValidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardRecord> result;
        // (1)cache
        String validActivityIdKey = UnionCardRecordCacheUtil.getValidActivityIdKey(activityId);
        if (redisCacheUtil.exists(validActivityIdKey)) {
            String tempStr = redisCacheUtil.get(validActivityIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("activity_id", activityId);
        result = unionCardRecordDao.selectList(entityWrapper);
        setValidCache(result, activityId, UnionCardRecordCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    @Override
    public List<UnionCardRecord> listInvalidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardRecord> result;
        // (1)cache
        String invalidActivityIdKey = UnionCardRecordCacheUtil.getInvalidActivityIdKey(activityId);
        if (redisCacheUtil.exists(invalidActivityIdKey)) {
            String tempStr = redisCacheUtil.get(invalidActivityIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("activity_id", activityId);
        result = unionCardRecordDao.selectList(entityWrapper);
        setInvalidCache(result, activityId, UnionCardRecordCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("card_id", cardId);
        result = unionCardRecordDao.selectList(entityWrapper);
        setCache(result, cardId, UnionCardRecordCacheUtil.TYPE_CARD_ID);
        return result;
    }

    @Override
    public List<UnionCardRecord> listValidByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardRecord> result;
        // (1)cache
        String validCardIdKey = UnionCardRecordCacheUtil.getValidCardIdKey(cardId);
        if (redisCacheUtil.exists(validCardIdKey)) {
            String tempStr = redisCacheUtil.get(validCardIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("card_id", cardId);
        result = unionCardRecordDao.selectList(entityWrapper);
        setValidCache(result, cardId, UnionCardRecordCacheUtil.TYPE_CARD_ID);
        return result;
    }

    @Override
    public List<UnionCardRecord> listInvalidByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardRecord> result;
        // (1)cache
        String invalidCardIdKey = UnionCardRecordCacheUtil.getInvalidCardIdKey(cardId);
        if (redisCacheUtil.exists(invalidCardIdKey)) {
            String tempStr = redisCacheUtil.get(invalidCardIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("card_id", cardId);
        result = unionCardRecordDao.selectList(entityWrapper);
        setInvalidCache(result, cardId, UnionCardRecordCacheUtil.TYPE_CARD_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("fan_id", fanId);
        result = unionCardRecordDao.selectList(entityWrapper);
        setCache(result, fanId, UnionCardRecordCacheUtil.TYPE_FAN_ID);
        return result;
    }

    @Override
    public List<UnionCardRecord> listValidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardRecord> result;
        // (1)cache
        String validFanIdKey = UnionCardRecordCacheUtil.getValidFanIdKey(fanId);
        if (redisCacheUtil.exists(validFanIdKey)) {
            String tempStr = redisCacheUtil.get(validFanIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("fan_id", fanId);
        result = unionCardRecordDao.selectList(entityWrapper);
        setValidCache(result, fanId, UnionCardRecordCacheUtil.TYPE_FAN_ID);
        return result;
    }

    @Override
    public List<UnionCardRecord> listInvalidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardRecord> result;
        // (1)cache
        String invalidFanIdKey = UnionCardRecordCacheUtil.getInvalidFanIdKey(fanId);
        if (redisCacheUtil.exists(invalidFanIdKey)) {
            String tempStr = redisCacheUtil.get(invalidFanIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("fan_id", fanId);
        result = unionCardRecordDao.selectList(entityWrapper);
        setInvalidCache(result, fanId, UnionCardRecordCacheUtil.TYPE_FAN_ID);
        return result;
    }

    @Override
    public List<UnionCardRecord> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardRecord> result;
        // (1)cache
        String memberIdKey = UnionCardRecordCacheUtil.getMemberIdKey(memberId);
        if (redisCacheUtil.exists(memberIdKey)) {
            String tempStr = redisCacheUtil.get(memberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId);
        result = unionCardRecordDao.selectList(entityWrapper);
        setCache(result, memberId, UnionCardRecordCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionCardRecord> listValidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardRecord> result;
        // (1)cache
        String validMemberIdKey = UnionCardRecordCacheUtil.getValidMemberIdKey(memberId);
        if (redisCacheUtil.exists(validMemberIdKey)) {
            String tempStr = redisCacheUtil.get(validMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("member_id", memberId);
        result = unionCardRecordDao.selectList(entityWrapper);
        setValidCache(result, memberId, UnionCardRecordCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionCardRecord> listInvalidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardRecord> result;
        // (1)cache
        String invalidMemberIdKey = UnionCardRecordCacheUtil.getInvalidMemberIdKey(memberId);
        if (redisCacheUtil.exists(invalidMemberIdKey)) {
            String tempStr = redisCacheUtil.get(invalidMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("member_id", memberId);
        result = unionCardRecordDao.selectList(entityWrapper);
        setInvalidCache(result, memberId, UnionCardRecordCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionCardRecord> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardRecord> result;
        // (1)cache
        String unionIdKey = UnionCardRecordCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);
        result = unionCardRecordDao.selectList(entityWrapper);
        setCache(result, unionId, UnionCardRecordCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionCardRecord> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardRecord> result;
        // (1)cache
        String validUnionIdKey = UnionCardRecordCacheUtil.getValidUnionIdKey(unionId);
        if (redisCacheUtil.exists(validUnionIdKey)) {
            String tempStr = redisCacheUtil.get(validUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);
        result = unionCardRecordDao.selectList(entityWrapper);
        setValidCache(result, unionId, UnionCardRecordCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionCardRecord> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardRecord> result;
        // (1)cache
        String invalidUnionIdKey = UnionCardRecordCacheUtil.getInvalidUnionIdKey(unionId);
        if (redisCacheUtil.exists(invalidUnionIdKey)) {
            String tempStr = redisCacheUtil.get(invalidUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);
        result = unionCardRecordDao.selectList(entityWrapper);
        setInvalidCache(result, unionId, UnionCardRecordCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionCardRecord> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardRecord> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionCardRecord> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardRecordDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardRecord newUnionCardRecord) throws Exception {
        if (newUnionCardRecord == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionCardRecordDao.insert(newUnionCardRecord);
        removeCache(newUnionCardRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardRecord> newUnionCardRecordList) throws Exception {
        if (newUnionCardRecordList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionCardRecordDao.insertBatch(newUnionCardRecordList);
        removeCache(newUnionCardRecordList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionCardRecord unionCardRecord = getById(id);
        removeCache(unionCardRecord);
        // (2)remove in db logically
        UnionCardRecord removeUnionCardRecord = new UnionCardRecord();
        removeUnionCardRecord.setId(id);
        removeUnionCardRecord.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionCardRecordDao.updateById(removeUnionCardRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardRecord> unionCardRecordList = listByIdList(idList);
        removeCache(unionCardRecordList);
        // (2)remove in db logically
        List<UnionCardRecord> removeUnionCardRecordList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardRecord removeUnionCardRecord = new UnionCardRecord();
            removeUnionCardRecord.setId(id);
            removeUnionCardRecord.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardRecordList.add(removeUnionCardRecord);
        }
        unionCardRecordDao.updateBatchById(removeUnionCardRecordList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardRecord updateUnionCardRecord) throws Exception {
        if (updateUnionCardRecord == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCardRecord.getId();
        UnionCardRecord unionCardRecord = getById(id);
        removeCache(unionCardRecord);
        // (2)update db
        unionCardRecordDao.updateById(updateUnionCardRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardRecord> updateUnionCardRecordList) throws Exception {
        if (updateUnionCardRecordList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionCardRecordList);
        List<UnionCardRecord> unionCardRecordList = listByIdList(idList);
        removeCache(unionCardRecordList);
        // (2)update db
        unionCardRecordDao.updateBatchById(updateUnionCardRecordList);
    }

    //********************************************* Object As a Service - cache support ********************************

    private void setCache(UnionCardRecord newUnionCardRecord, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardRecordCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCardRecord);
    }

    private void setCache(List<UnionCardRecord> newUnionCardRecordList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionCardRecordCacheUtil.TYPE_ACTIVITY_ID:
                foreignIdKey = UnionCardRecordCacheUtil.getActivityIdKey(foreignId);
                break;
            case UnionCardRecordCacheUtil.TYPE_CARD_ID:
                foreignIdKey = UnionCardRecordCacheUtil.getCardIdKey(foreignId);
                break;
            case UnionCardRecordCacheUtil.TYPE_FAN_ID:
                foreignIdKey = UnionCardRecordCacheUtil.getFanIdKey(foreignId);
                break;
            case UnionCardRecordCacheUtil.TYPE_MEMBER_ID:
                foreignIdKey = UnionCardRecordCacheUtil.getMemberIdKey(foreignId);
                break;
            case UnionCardRecordCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionCardRecordCacheUtil.getUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionCardRecordList);
        }
    }

    private void setValidCache(List<UnionCardRecord> newUnionCardRecordList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionCardRecordCacheUtil.TYPE_ACTIVITY_ID:
                validForeignIdKey = UnionCardRecordCacheUtil.getValidActivityIdKey(foreignId);
                break;
            case UnionCardRecordCacheUtil.TYPE_CARD_ID:
                validForeignIdKey = UnionCardRecordCacheUtil.getValidCardIdKey(foreignId);
                break;
            case UnionCardRecordCacheUtil.TYPE_FAN_ID:
                validForeignIdKey = UnionCardRecordCacheUtil.getValidFanIdKey(foreignId);
                break;
            case UnionCardRecordCacheUtil.TYPE_MEMBER_ID:
                validForeignIdKey = UnionCardRecordCacheUtil.getValidMemberIdKey(foreignId);
                break;
            case UnionCardRecordCacheUtil.TYPE_UNION_ID:
                validForeignIdKey = UnionCardRecordCacheUtil.getValidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionCardRecordList);
        }
    }

    private void setInvalidCache(List<UnionCardRecord> newUnionCardRecordList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionCardRecordCacheUtil.TYPE_ACTIVITY_ID:
                invalidForeignIdKey = UnionCardRecordCacheUtil.getInvalidActivityIdKey(foreignId);
                break;
            case UnionCardRecordCacheUtil.TYPE_CARD_ID:
                invalidForeignIdKey = UnionCardRecordCacheUtil.getInvalidCardIdKey(foreignId);
                break;
            case UnionCardRecordCacheUtil.TYPE_FAN_ID:
                invalidForeignIdKey = UnionCardRecordCacheUtil.getInvalidFanIdKey(foreignId);
                break;
            case UnionCardRecordCacheUtil.TYPE_MEMBER_ID:
                invalidForeignIdKey = UnionCardRecordCacheUtil.getInvalidMemberIdKey(foreignId);
                break;
            case UnionCardRecordCacheUtil.TYPE_UNION_ID:
                invalidForeignIdKey = UnionCardRecordCacheUtil.getInvalidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionCardRecordList);
        }
    }

    private void removeCache(UnionCardRecord unionCardRecord) {
        if (unionCardRecord == null) {
            return;
        }
        Integer id = unionCardRecord.getId();
        String idKey = UnionCardRecordCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer activityId = unionCardRecord.getActivityId();
        if (activityId != null) {
            String activityIdKey = UnionCardRecordCacheUtil.getActivityIdKey(activityId);
            redisCacheUtil.remove(activityIdKey);

            String validActivityIdKey = UnionCardRecordCacheUtil.getValidActivityIdKey(activityId);
            redisCacheUtil.remove(validActivityIdKey);

            String invalidActivityIdKey = UnionCardRecordCacheUtil.getInvalidActivityIdKey(activityId);
            redisCacheUtil.remove(invalidActivityIdKey);
        }

        Integer cardId = unionCardRecord.getCardId();
        if (cardId != null) {
            String cardIdKey = UnionCardRecordCacheUtil.getCardIdKey(cardId);
            redisCacheUtil.remove(cardIdKey);

            String validCardIdKey = UnionCardRecordCacheUtil.getValidCardIdKey(cardId);
            redisCacheUtil.remove(validCardIdKey);

            String invalidCardIdKey = UnionCardRecordCacheUtil.getInvalidCardIdKey(cardId);
            redisCacheUtil.remove(invalidCardIdKey);
        }

        Integer fanId = unionCardRecord.getFanId();
        if (fanId != null) {
            String fanIdKey = UnionCardRecordCacheUtil.getFanIdKey(fanId);
            redisCacheUtil.remove(fanIdKey);

            String validFanIdKey = UnionCardRecordCacheUtil.getValidFanIdKey(fanId);
            redisCacheUtil.remove(validFanIdKey);

            String invalidFanIdKey = UnionCardRecordCacheUtil.getInvalidFanIdKey(fanId);
            redisCacheUtil.remove(invalidFanIdKey);
        }

        Integer memberId = unionCardRecord.getMemberId();
        if (memberId != null) {
            String memberIdKey = UnionCardRecordCacheUtil.getMemberIdKey(memberId);
            redisCacheUtil.remove(memberIdKey);

            String validMemberIdKey = UnionCardRecordCacheUtil.getValidMemberIdKey(memberId);
            redisCacheUtil.remove(validMemberIdKey);

            String invalidMemberIdKey = UnionCardRecordCacheUtil.getInvalidMemberIdKey(memberId);
            redisCacheUtil.remove(invalidMemberIdKey);
        }

        Integer unionId = unionCardRecord.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionCardRecordCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);

            String validUnionIdKey = UnionCardRecordCacheUtil.getValidUnionIdKey(unionId);
            redisCacheUtil.remove(validUnionIdKey);

            String invalidUnionIdKey = UnionCardRecordCacheUtil.getInvalidUnionIdKey(unionId);
            redisCacheUtil.remove(invalidUnionIdKey);
        }

    }

    private void removeCache(List<UnionCardRecord> unionCardRecordList) {
        if (ListUtil.isEmpty(unionCardRecordList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCardRecord unionCardRecord : unionCardRecordList) {
            idList.add(unionCardRecord.getId());
        }
        List<String> idKeyList = UnionCardRecordCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> activityIdKeyList = getForeignIdKeyList(unionCardRecordList, UnionCardRecordCacheUtil.TYPE_ACTIVITY_ID);
        if (ListUtil.isNotEmpty(activityIdKeyList)) {
            redisCacheUtil.remove(activityIdKeyList);
        }

        List<String> cardIdKeyList = getForeignIdKeyList(unionCardRecordList, UnionCardRecordCacheUtil.TYPE_CARD_ID);
        if (ListUtil.isNotEmpty(cardIdKeyList)) {
            redisCacheUtil.remove(cardIdKeyList);
        }

        List<String> fanIdKeyList = getForeignIdKeyList(unionCardRecordList, UnionCardRecordCacheUtil.TYPE_FAN_ID);
        if (ListUtil.isNotEmpty(fanIdKeyList)) {
            redisCacheUtil.remove(fanIdKeyList);
        }

        List<String> memberIdKeyList = getForeignIdKeyList(unionCardRecordList, UnionCardRecordCacheUtil.TYPE_MEMBER_ID);
        if (ListUtil.isNotEmpty(memberIdKeyList)) {
            redisCacheUtil.remove(memberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionCardRecordList, UnionCardRecordCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

    }

    private List<String> getForeignIdKeyList(List<UnionCardRecord> unionCardRecordList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionCardRecordCacheUtil.TYPE_ACTIVITY_ID:
                for (UnionCardRecord unionCardRecord : unionCardRecordList) {
                    Integer activityId = unionCardRecord.getActivityId();
                    if (activityId != null) {
                        String activityIdKey = UnionCardRecordCacheUtil.getActivityIdKey(activityId);
                        result.add(activityIdKey);

                        String validActivityIdKey = UnionCardRecordCacheUtil.getValidActivityIdKey(activityId);
                        result.add(validActivityIdKey);

                        String invalidActivityIdKey = UnionCardRecordCacheUtil.getInvalidActivityIdKey(activityId);
                        result.add(invalidActivityIdKey);
                    }
                }
                break;
            case UnionCardRecordCacheUtil.TYPE_CARD_ID:
                for (UnionCardRecord unionCardRecord : unionCardRecordList) {
                    Integer cardId = unionCardRecord.getCardId();
                    if (cardId != null) {
                        String cardIdKey = UnionCardRecordCacheUtil.getCardIdKey(cardId);
                        result.add(cardIdKey);

                        String validCardIdKey = UnionCardRecordCacheUtil.getValidCardIdKey(cardId);
                        result.add(validCardIdKey);

                        String invalidCardIdKey = UnionCardRecordCacheUtil.getInvalidCardIdKey(cardId);
                        result.add(invalidCardIdKey);
                    }
                }
                break;
            case UnionCardRecordCacheUtil.TYPE_FAN_ID:
                for (UnionCardRecord unionCardRecord : unionCardRecordList) {
                    Integer fanId = unionCardRecord.getFanId();
                    if (fanId != null) {
                        String fanIdKey = UnionCardRecordCacheUtil.getFanIdKey(fanId);
                        result.add(fanIdKey);

                        String validFanIdKey = UnionCardRecordCacheUtil.getValidFanIdKey(fanId);
                        result.add(validFanIdKey);

                        String invalidFanIdKey = UnionCardRecordCacheUtil.getInvalidFanIdKey(fanId);
                        result.add(invalidFanIdKey);
                    }
                }
                break;
            case UnionCardRecordCacheUtil.TYPE_MEMBER_ID:
                for (UnionCardRecord unionCardRecord : unionCardRecordList) {
                    Integer memberId = unionCardRecord.getMemberId();
                    if (memberId != null) {
                        String memberIdKey = UnionCardRecordCacheUtil.getMemberIdKey(memberId);
                        result.add(memberIdKey);

                        String validMemberIdKey = UnionCardRecordCacheUtil.getValidMemberIdKey(memberId);
                        result.add(validMemberIdKey);

                        String invalidMemberIdKey = UnionCardRecordCacheUtil.getInvalidMemberIdKey(memberId);
                        result.add(invalidMemberIdKey);
                    }
                }
                break;
            case UnionCardRecordCacheUtil.TYPE_UNION_ID:
                for (UnionCardRecord unionCardRecord : unionCardRecordList) {
                    Integer unionId = unionCardRecord.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionCardRecordCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);

                        String validUnionIdKey = UnionCardRecordCacheUtil.getValidUnionIdKey(unionId);
                        result.add(validUnionIdKey);

                        String invalidUnionIdKey = UnionCardRecordCacheUtil.getInvalidUnionIdKey(unionId);
                        result.add(invalidUnionIdKey);
                    }
                }
                break;

            default:
                break;
        }
        return result;
    }

}