package com.gt.union.card.sharing.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.service.IUnionCardFanService;
import com.gt.union.card.sharing.dao.IUnionCardSharingRecordDao;
import com.gt.union.card.sharing.entity.UnionCardSharingRecord;
import com.gt.union.card.sharing.service.IUnionCardSharingRecordService;
import com.gt.union.card.sharing.util.UnionCardSharingRecordCacheUtil;
import com.gt.union.card.sharing.vo.CardSharingRecordVO;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 联盟卡售卡分成记录 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Service
public class UnionCardSharingRecordServiceImpl implements IUnionCardSharingRecordService {
    @Autowired
    private IUnionCardSharingRecordDao unionCardSharingRecordDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionCardFanService unionCardFanService;

    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************


    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionCardSharingRecord> filterByDelStatus(List<UnionCardSharingRecord> unionCardSharingRecordList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardSharingRecord> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardSharingRecordList)) {
            for (UnionCardSharingRecord unionCardSharingRecord : unionCardSharingRecordList) {
                if (delStatus.equals(unionCardSharingRecord.getDelStatus())) {
                    result.add(unionCardSharingRecord);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
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
        result = unionCardSharingRecordDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionCardSharingRecord getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardSharingRecord result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionCardSharingRecord getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardSharingRecord result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionCardSharingRecord> unionCardSharingRecordList) throws Exception {
        if (unionCardSharingRecordList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardSharingRecordList)) {
            for (UnionCardSharingRecord unionCardSharingRecord : unionCardSharingRecordList) {
                result.add(unionCardSharingRecord.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionCardSharingRecord> listBySharingMemberId(Integer sharingMemberId) throws Exception {
        if (sharingMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String sharingMemberIdKey = UnionCardSharingRecordCacheUtil.getSharingMemberIdKey(sharingMemberId);
        if (redisCacheUtil.exists(sharingMemberIdKey)) {
            String tempStr = redisCacheUtil.get(sharingMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("sharing_member_id", sharingMemberId);
        result = unionCardSharingRecordDao.selectList(entityWrapper);
        setCache(result, sharingMemberId, UnionCardSharingRecordCacheUtil.TYPE_SHARING_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRecord> listValidBySharingMemberId(Integer sharingMemberId) throws Exception {
        if (sharingMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String validSharingMemberIdKey = UnionCardSharingRecordCacheUtil.getValidSharingMemberIdKey(sharingMemberId);
        if (redisCacheUtil.exists(validSharingMemberIdKey)) {
            String tempStr = redisCacheUtil.get(validSharingMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("sharing_member_id", sharingMemberId);
        result = unionCardSharingRecordDao.selectList(entityWrapper);
        setValidCache(result, sharingMemberId, UnionCardSharingRecordCacheUtil.TYPE_SHARING_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRecord> listInvalidBySharingMemberId(Integer sharingMemberId) throws Exception {
        if (sharingMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String invalidSharingMemberIdKey = UnionCardSharingRecordCacheUtil.getInvalidSharingMemberIdKey(sharingMemberId);
        if (redisCacheUtil.exists(invalidSharingMemberIdKey)) {
            String tempStr = redisCacheUtil.get(invalidSharingMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("sharing_member_id", sharingMemberId);
        result = unionCardSharingRecordDao.selectList(entityWrapper);
        setInvalidCache(result, sharingMemberId, UnionCardSharingRecordCacheUtil.TYPE_SHARING_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRecord> listByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String fromMemberIdKey = UnionCardSharingRecordCacheUtil.getFromMemberIdKey(fromMemberId);
        if (redisCacheUtil.exists(fromMemberIdKey)) {
            String tempStr = redisCacheUtil.get(fromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("from_member_id", fromMemberId);
        result = unionCardSharingRecordDao.selectList(entityWrapper);
        setCache(result, fromMemberId, UnionCardSharingRecordCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRecord> listValidByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String validFromMemberIdKey = UnionCardSharingRecordCacheUtil.getValidFromMemberIdKey(fromMemberId);
        if (redisCacheUtil.exists(validFromMemberIdKey)) {
            String tempStr = redisCacheUtil.get(validFromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_member_id", fromMemberId);
        result = unionCardSharingRecordDao.selectList(entityWrapper);
        setValidCache(result, fromMemberId, UnionCardSharingRecordCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRecord> listInvalidByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String invalidFromMemberIdKey = UnionCardSharingRecordCacheUtil.getInvalidFromMemberIdKey(fromMemberId);
        if (redisCacheUtil.exists(invalidFromMemberIdKey)) {
            String tempStr = redisCacheUtil.get(invalidFromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("from_member_id", fromMemberId);
        result = unionCardSharingRecordDao.selectList(entityWrapper);
        setInvalidCache(result, fromMemberId, UnionCardSharingRecordCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("union_id", unionId);
        result = unionCardSharingRecordDao.selectList(entityWrapper);
        setCache(result, unionId, UnionCardSharingRecordCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRecord> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String validUnionIdKey = UnionCardSharingRecordCacheUtil.getValidUnionIdKey(unionId);
        if (redisCacheUtil.exists(validUnionIdKey)) {
            String tempStr = redisCacheUtil.get(validUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);
        result = unionCardSharingRecordDao.selectList(entityWrapper);
        setValidCache(result, unionId, UnionCardSharingRecordCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRecord> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String invalidUnionIdKey = UnionCardSharingRecordCacheUtil.getInvalidUnionIdKey(unionId);
        if (redisCacheUtil.exists(invalidUnionIdKey)) {
            String tempStr = redisCacheUtil.get(invalidUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);
        result = unionCardSharingRecordDao.selectList(entityWrapper);
        setInvalidCache(result, unionId, UnionCardSharingRecordCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("activity_id", activityId);
        result = unionCardSharingRecordDao.selectList(entityWrapper);
        setCache(result, activityId, UnionCardSharingRecordCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRecord> listValidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String validActivityIdKey = UnionCardSharingRecordCacheUtil.getValidActivityIdKey(activityId);
        if (redisCacheUtil.exists(validActivityIdKey)) {
            String tempStr = redisCacheUtil.get(validActivityIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("activity_id", activityId);
        result = unionCardSharingRecordDao.selectList(entityWrapper);
        setValidCache(result, activityId, UnionCardSharingRecordCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRecord> listInvalidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String invalidActivityIdKey = UnionCardSharingRecordCacheUtil.getInvalidActivityIdKey(activityId);
        if (redisCacheUtil.exists(invalidActivityIdKey)) {
            String tempStr = redisCacheUtil.get(invalidActivityIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("activity_id", activityId);
        result = unionCardSharingRecordDao.selectList(entityWrapper);
        setInvalidCache(result, activityId, UnionCardSharingRecordCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("card_id", cardId);
        result = unionCardSharingRecordDao.selectList(entityWrapper);
        setCache(result, cardId, UnionCardSharingRecordCacheUtil.TYPE_CARD_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRecord> listValidByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String validCardIdKey = UnionCardSharingRecordCacheUtil.getValidCardIdKey(cardId);
        if (redisCacheUtil.exists(validCardIdKey)) {
            String tempStr = redisCacheUtil.get(validCardIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("card_id", cardId);
        result = unionCardSharingRecordDao.selectList(entityWrapper);
        setValidCache(result, cardId, UnionCardSharingRecordCacheUtil.TYPE_CARD_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRecord> listInvalidByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String invalidCardIdKey = UnionCardSharingRecordCacheUtil.getInvalidCardIdKey(cardId);
        if (redisCacheUtil.exists(invalidCardIdKey)) {
            String tempStr = redisCacheUtil.get(invalidCardIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("card_id", cardId);
        result = unionCardSharingRecordDao.selectList(entityWrapper);
        setInvalidCache(result, cardId, UnionCardSharingRecordCacheUtil.TYPE_CARD_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("fan_id", fanId);
        result = unionCardSharingRecordDao.selectList(entityWrapper);
        setCache(result, fanId, UnionCardSharingRecordCacheUtil.TYPE_FAN_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRecord> listValidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String validFanIdKey = UnionCardSharingRecordCacheUtil.getValidFanIdKey(fanId);
        if (redisCacheUtil.exists(validFanIdKey)) {
            String tempStr = redisCacheUtil.get(validFanIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("fan_id", fanId);
        result = unionCardSharingRecordDao.selectList(entityWrapper);
        setValidCache(result, fanId, UnionCardSharingRecordCacheUtil.TYPE_FAN_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRecord> listInvalidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRecord> result;
        // (1)cache
        String invalidFanIdKey = UnionCardSharingRecordCacheUtil.getInvalidFanIdKey(fanId);
        if (redisCacheUtil.exists(invalidFanIdKey)) {
            String tempStr = redisCacheUtil.get(invalidFanIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRecord.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("fan_id", fanId);
        result = unionCardSharingRecordDao.selectList(entityWrapper);
        setInvalidCache(result, fanId, UnionCardSharingRecordCacheUtil.TYPE_FAN_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRecord> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardSharingRecord> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page<UnionCardSharingRecord> pageSupport(Page page, EntityWrapper<UnionCardSharingRecord> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardSharingRecordDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardSharingRecord newUnionCardSharingRecord) throws Exception {
        if (newUnionCardSharingRecord == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionCardSharingRecordDao.insert(newUnionCardSharingRecord);
        removeCache(newUnionCardSharingRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardSharingRecord> newUnionCardSharingRecordList) throws Exception {
        if (newUnionCardSharingRecordList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionCardSharingRecordDao.insertBatch(newUnionCardSharingRecordList);
        removeCache(newUnionCardSharingRecordList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
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
        unionCardSharingRecordDao.updateById(removeUnionCardSharingRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardSharingRecord> unionCardSharingRecordList = listByIdList(idList);
        removeCache(unionCardSharingRecordList);
        // (2)remove in db logically
        List<UnionCardSharingRecord> removeUnionCardSharingRecordList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardSharingRecord removeUnionCardSharingRecord = new UnionCardSharingRecord();
            removeUnionCardSharingRecord.setId(id);
            removeUnionCardSharingRecord.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardSharingRecordList.add(removeUnionCardSharingRecord);
        }
        unionCardSharingRecordDao.updateBatchById(removeUnionCardSharingRecordList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
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
        unionCardSharingRecordDao.updateById(updateUnionCardSharingRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardSharingRecord> updateUnionCardSharingRecordList) throws Exception {
        if (updateUnionCardSharingRecordList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionCardSharingRecordList);
        List<UnionCardSharingRecord> unionCardSharingRecordList = listByIdList(idList);
        removeCache(unionCardSharingRecordList);
        // (2)update db
        unionCardSharingRecordDao.updateBatchById(updateUnionCardSharingRecordList);
    }

    //********************************************* Object As a Service - cache support ********************************

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
            case UnionCardSharingRecordCacheUtil.TYPE_SHARING_MEMBER_ID:
                foreignIdKey = UnionCardSharingRecordCacheUtil.getSharingMemberIdKey(foreignId);
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_FROM_MEMBER_ID:
                foreignIdKey = UnionCardSharingRecordCacheUtil.getFromMemberIdKey(foreignId);
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

    private void setValidCache(List<UnionCardSharingRecord> newUnionCardSharingRecordList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionCardSharingRecordCacheUtil.TYPE_SHARING_MEMBER_ID:
                validForeignIdKey = UnionCardSharingRecordCacheUtil.getValidSharingMemberIdKey(foreignId);
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_FROM_MEMBER_ID:
                validForeignIdKey = UnionCardSharingRecordCacheUtil.getValidFromMemberIdKey(foreignId);
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_UNION_ID:
                validForeignIdKey = UnionCardSharingRecordCacheUtil.getValidUnionIdKey(foreignId);
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_ACTIVITY_ID:
                validForeignIdKey = UnionCardSharingRecordCacheUtil.getValidActivityIdKey(foreignId);
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_CARD_ID:
                validForeignIdKey = UnionCardSharingRecordCacheUtil.getValidCardIdKey(foreignId);
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_FAN_ID:
                validForeignIdKey = UnionCardSharingRecordCacheUtil.getValidFanIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionCardSharingRecordList);
        }
    }

    private void setInvalidCache(List<UnionCardSharingRecord> newUnionCardSharingRecordList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionCardSharingRecordCacheUtil.TYPE_SHARING_MEMBER_ID:
                invalidForeignIdKey = UnionCardSharingRecordCacheUtil.getInvalidSharingMemberIdKey(foreignId);
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_FROM_MEMBER_ID:
                invalidForeignIdKey = UnionCardSharingRecordCacheUtil.getInvalidFromMemberIdKey(foreignId);
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_UNION_ID:
                invalidForeignIdKey = UnionCardSharingRecordCacheUtil.getInvalidUnionIdKey(foreignId);
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_ACTIVITY_ID:
                invalidForeignIdKey = UnionCardSharingRecordCacheUtil.getInvalidActivityIdKey(foreignId);
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_CARD_ID:
                invalidForeignIdKey = UnionCardSharingRecordCacheUtil.getInvalidCardIdKey(foreignId);
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_FAN_ID:
                invalidForeignIdKey = UnionCardSharingRecordCacheUtil.getInvalidFanIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionCardSharingRecordList);
        }
    }

    private void removeCache(UnionCardSharingRecord unionCardSharingRecord) {
        if (unionCardSharingRecord == null) {
            return;
        }
        Integer id = unionCardSharingRecord.getId();
        String idKey = UnionCardSharingRecordCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer sharingMemberId = unionCardSharingRecord.getSharingMemberId();
        if (sharingMemberId != null) {
            String sharingMemberIdKey = UnionCardSharingRecordCacheUtil.getSharingMemberIdKey(sharingMemberId);
            redisCacheUtil.remove(sharingMemberIdKey);

            String validSharingMemberIdKey = UnionCardSharingRecordCacheUtil.getValidSharingMemberIdKey(sharingMemberId);
            redisCacheUtil.remove(validSharingMemberIdKey);

            String invalidSharingMemberIdKey = UnionCardSharingRecordCacheUtil.getInvalidSharingMemberIdKey(sharingMemberId);
            redisCacheUtil.remove(invalidSharingMemberIdKey);
        }

        Integer fromMemberId = unionCardSharingRecord.getFromMemberId();
        if (fromMemberId != null) {
            String fromMemberIdKey = UnionCardSharingRecordCacheUtil.getFromMemberIdKey(fromMemberId);
            redisCacheUtil.remove(fromMemberIdKey);

            String validFromMemberIdKey = UnionCardSharingRecordCacheUtil.getValidFromMemberIdKey(fromMemberId);
            redisCacheUtil.remove(validFromMemberIdKey);

            String invalidFromMemberIdKey = UnionCardSharingRecordCacheUtil.getInvalidFromMemberIdKey(fromMemberId);
            redisCacheUtil.remove(invalidFromMemberIdKey);
        }

        Integer unionId = unionCardSharingRecord.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionCardSharingRecordCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);

            String validUnionIdKey = UnionCardSharingRecordCacheUtil.getValidUnionIdKey(unionId);
            redisCacheUtil.remove(validUnionIdKey);

            String invalidUnionIdKey = UnionCardSharingRecordCacheUtil.getInvalidUnionIdKey(unionId);
            redisCacheUtil.remove(invalidUnionIdKey);
        }

        Integer activityId = unionCardSharingRecord.getActivityId();
        if (activityId != null) {
            String activityIdKey = UnionCardSharingRecordCacheUtil.getActivityIdKey(activityId);
            redisCacheUtil.remove(activityIdKey);

            String validActivityIdKey = UnionCardSharingRecordCacheUtil.getValidActivityIdKey(activityId);
            redisCacheUtil.remove(validActivityIdKey);

            String invalidActivityIdKey = UnionCardSharingRecordCacheUtil.getInvalidActivityIdKey(activityId);
            redisCacheUtil.remove(invalidActivityIdKey);
        }

        Integer cardId = unionCardSharingRecord.getCardId();
        if (cardId != null) {
            String cardIdKey = UnionCardSharingRecordCacheUtil.getCardIdKey(cardId);
            redisCacheUtil.remove(cardIdKey);

            String validCardIdKey = UnionCardSharingRecordCacheUtil.getValidCardIdKey(cardId);
            redisCacheUtil.remove(validCardIdKey);

            String invalidCardIdKey = UnionCardSharingRecordCacheUtil.getInvalidCardIdKey(cardId);
            redisCacheUtil.remove(invalidCardIdKey);
        }

        Integer fanId = unionCardSharingRecord.getFanId();
        if (fanId != null) {
            String fanIdKey = UnionCardSharingRecordCacheUtil.getFanIdKey(fanId);
            redisCacheUtil.remove(fanIdKey);

            String validFanIdKey = UnionCardSharingRecordCacheUtil.getValidFanIdKey(fanId);
            redisCacheUtil.remove(validFanIdKey);

            String invalidFanIdKey = UnionCardSharingRecordCacheUtil.getInvalidFanIdKey(fanId);
            redisCacheUtil.remove(invalidFanIdKey);
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

        List<String> sharingMemberIdKeyList = getForeignIdKeyList(unionCardSharingRecordList, UnionCardSharingRecordCacheUtil.TYPE_SHARING_MEMBER_ID);
        if (ListUtil.isNotEmpty(sharingMemberIdKeyList)) {
            redisCacheUtil.remove(sharingMemberIdKeyList);
        }

        List<String> fromMemberIdKeyList = getForeignIdKeyList(unionCardSharingRecordList, UnionCardSharingRecordCacheUtil.TYPE_FROM_MEMBER_ID);
        if (ListUtil.isNotEmpty(fromMemberIdKeyList)) {
            redisCacheUtil.remove(fromMemberIdKeyList);
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
            case UnionCardSharingRecordCacheUtil.TYPE_SHARING_MEMBER_ID:
                for (UnionCardSharingRecord unionCardSharingRecord : unionCardSharingRecordList) {
                    Integer sharingMemberId = unionCardSharingRecord.getSharingMemberId();
                    if (sharingMemberId != null) {
                        String sharingMemberIdKey = UnionCardSharingRecordCacheUtil.getSharingMemberIdKey(sharingMemberId);
                        result.add(sharingMemberIdKey);

                        String validSharingMemberIdKey = UnionCardSharingRecordCacheUtil.getValidSharingMemberIdKey(sharingMemberId);
                        result.add(validSharingMemberIdKey);

                        String invalidSharingMemberIdKey = UnionCardSharingRecordCacheUtil.getInvalidSharingMemberIdKey(sharingMemberId);
                        result.add(invalidSharingMemberIdKey);
                    }
                }
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_FROM_MEMBER_ID:
                for (UnionCardSharingRecord unionCardSharingRecord : unionCardSharingRecordList) {
                    Integer fromMemberId = unionCardSharingRecord.getFromMemberId();
                    if (fromMemberId != null) {
                        String fromMemberIdKey = UnionCardSharingRecordCacheUtil.getFromMemberIdKey(fromMemberId);
                        result.add(fromMemberIdKey);

                        String validFromMemberIdKey = UnionCardSharingRecordCacheUtil.getValidFromMemberIdKey(fromMemberId);
                        result.add(validFromMemberIdKey);

                        String invalidFromMemberIdKey = UnionCardSharingRecordCacheUtil.getInvalidFromMemberIdKey(fromMemberId);
                        result.add(invalidFromMemberIdKey);
                    }
                }
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_UNION_ID:
                for (UnionCardSharingRecord unionCardSharingRecord : unionCardSharingRecordList) {
                    Integer unionId = unionCardSharingRecord.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionCardSharingRecordCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);

                        String validUnionIdKey = UnionCardSharingRecordCacheUtil.getValidUnionIdKey(unionId);
                        result.add(validUnionIdKey);

                        String invalidUnionIdKey = UnionCardSharingRecordCacheUtil.getInvalidUnionIdKey(unionId);
                        result.add(invalidUnionIdKey);
                    }
                }
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_ACTIVITY_ID:
                for (UnionCardSharingRecord unionCardSharingRecord : unionCardSharingRecordList) {
                    Integer activityId = unionCardSharingRecord.getActivityId();
                    if (activityId != null) {
                        String activityIdKey = UnionCardSharingRecordCacheUtil.getActivityIdKey(activityId);
                        result.add(activityIdKey);

                        String validActivityIdKey = UnionCardSharingRecordCacheUtil.getValidActivityIdKey(activityId);
                        result.add(validActivityIdKey);

                        String invalidActivityIdKey = UnionCardSharingRecordCacheUtil.getInvalidActivityIdKey(activityId);
                        result.add(invalidActivityIdKey);
                    }
                }
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_CARD_ID:
                for (UnionCardSharingRecord unionCardSharingRecord : unionCardSharingRecordList) {
                    Integer cardId = unionCardSharingRecord.getCardId();
                    if (cardId != null) {
                        String cardIdKey = UnionCardSharingRecordCacheUtil.getCardIdKey(cardId);
                        result.add(cardIdKey);

                        String validCardIdKey = UnionCardSharingRecordCacheUtil.getValidCardIdKey(cardId);
                        result.add(validCardIdKey);

                        String invalidCardIdKey = UnionCardSharingRecordCacheUtil.getInvalidCardIdKey(cardId);
                        result.add(invalidCardIdKey);
                    }
                }
                break;
            case UnionCardSharingRecordCacheUtil.TYPE_FAN_ID:
                for (UnionCardSharingRecord unionCardSharingRecord : unionCardSharingRecordList) {
                    Integer fanId = unionCardSharingRecord.getFanId();
                    if (fanId != null) {
                        String fanIdKey = UnionCardSharingRecordCacheUtil.getFanIdKey(fanId);
                        result.add(fanIdKey);

                        String validFanIdKey = UnionCardSharingRecordCacheUtil.getValidFanIdKey(fanId);
                        result.add(validFanIdKey);

                        String invalidFanIdKey = UnionCardSharingRecordCacheUtil.getInvalidFanIdKey(fanId);
                        result.add(invalidFanIdKey);
                    }
                }
                break;

            default:
                break;
        }
        return result;
    }

    //***************************************** Domain Driven Design - get *********************************************

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<CardSharingRecordVO> listCardSharingRecordVOByBusIdAndUnionId(
            Integer busId, Integer unionId, String optCardNumber, Date optBeginTime, Date optEndTime) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_READ_REJECT);
        }

        // （2）	按时间倒序排序
        List<CardSharingRecordVO> result = new ArrayList<>();
        List<UnionCardSharingRecord> recordList = listByUnionIdAndSharingMemberId(unionId, member.getId());
        if (ListUtil.isNotEmpty(recordList)) {
            for (UnionCardSharingRecord record : recordList) {
                if (optBeginTime != null && optBeginTime.compareTo(record.getCreateTime()) > 0) {
                    continue;
                }
                if (optEndTime != null && optEndTime.compareTo(record.getCreateTime()) < 0) {
                    continue;
                }
                Integer fanId = record.getFanId();
                UnionCardFan fan = null;
                if (StringUtil.isNotEmpty(optCardNumber)) {
                    if (fanId == null) {
                        continue;
                    }
                    fan = unionCardFanService.getById(fanId);
                    if (fan == null || StringUtil.isEmpty(fan.getNumber()) || !fan.getNumber().contains(optCardNumber)) {
                        continue;
                    }
                }
                CardSharingRecordVO vo = new CardSharingRecordVO();
                vo.setFan(fan);
                vo.setSharingRecord(record);
                UnionMember fromMember = unionMemberService.getReadByIdAndUnionId(record.getFromMemberId(), unionId);
                vo.setMember(fromMember);
                result.add(vo);
            }
        }
        Collections.sort(result, new Comparator<CardSharingRecordVO>() {
            @Override
            public int compare(CardSharingRecordVO o1, CardSharingRecordVO o2) {
                return o2.getSharingRecord().getCreateTime().compareTo(o1.getSharingRecord().getCreateTime());
            }
        });

        return result;
    }

    @Override
    public List<UnionCardSharingRecord> listByUnionIdAndSharingMemberId(Integer unionId, Integer sharingMemberId) throws Exception {
        if (unionId == null || sharingMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardSharingRecord> result = listBySharingMemberId(sharingMemberId);
        result = filterByUnionId(result, unionId);

        return result;
    }


    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    @Override
    public List<UnionCardSharingRecord> filterByUnionId(List<UnionCardSharingRecord> recordList, Integer unionId) throws Exception {
        if (recordList == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardSharingRecord> result = new ArrayList<>();
        for (UnionCardSharingRecord record : recordList) {
            if (unionId.equals(record.getUnionId())) {
                result.add(record);
            }
        }

        return result;
    }


}