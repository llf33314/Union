package com.gt.union.card.consume.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.consume.entity.UnionConsume;
import com.gt.union.card.consume.mapper.UnionConsumeMapper;
import com.gt.union.card.consume.service.IUnionConsumeService;
import com.gt.union.card.consume.util.UnionConsumeCacheUtil;
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
 * 消费核销 服务实现类
 *
 * @author linweicong
 * @version 2017-11-25 10:51:42
 */
@Service
public class UnionConsumeServiceImpl extends ServiceImpl<UnionConsumeMapper, UnionConsume> implements IUnionConsumeService {
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

    public UnionConsume getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionConsume result;
        // (1)cache
        String idKey = UnionConsumeCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionConsume.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionConsume> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsume> result;
        // (1)cache
        String memberIdKey = UnionConsumeCacheUtil.getMemberIdKey(memberId);
        if (redisCacheUtil.exists(memberIdKey)) {
            String tempStr = redisCacheUtil.get(memberIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsume.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, memberId, UnionConsumeCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    public List<UnionConsume> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsume> result;
        // (1)cache
        String unionIdKey = UnionConsumeCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsume.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionConsumeCacheUtil.TYPE_UNION_ID);
        return result;
    }

    public List<UnionConsume> listByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsume> result;
        // (1)cache
        String cardIdKey = UnionConsumeCacheUtil.getCardIdKey(cardId);
        if (redisCacheUtil.exists(cardIdKey)) {
            String tempStr = redisCacheUtil.get(cardIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsume.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("card_id", cardId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, cardId, UnionConsumeCacheUtil.TYPE_CARD_ID);
        return result;
    }

    public List<UnionConsume> listByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsume> result;
        // (1)cache
        String fanIdKey = UnionConsumeCacheUtil.getFanIdKey(fanId);
        if (redisCacheUtil.exists(fanIdKey)) {
            String tempStr = redisCacheUtil.get(fanIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsume.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("fan_id", fanId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, fanId, UnionConsumeCacheUtil.TYPE_FAN_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionConsume newUnionConsume) throws Exception {
        if (newUnionConsume == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionConsume);
        removeCache(newUnionConsume);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionConsume> newUnionConsumeList) throws Exception {
        if (newUnionConsumeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionConsumeList);
        removeCache(newUnionConsumeList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionConsume unionConsume = getById(id);
        removeCache(unionConsume);
        // (2)remove in db logically
        UnionConsume removeUnionConsume = new UnionConsume();
        removeUnionConsume.setId(id);
        removeUnionConsume.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionConsume);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionConsume> unionConsumeList = new ArrayList<>();
        for (Integer id : idList) {
            UnionConsume unionConsume = getById(id);
            unionConsumeList.add(unionConsume);
        }
        removeCache(unionConsumeList);
        // (2)remove in db logically
        List<UnionConsume> removeUnionConsumeList = new ArrayList<>();
        for (Integer id : idList) {
            UnionConsume removeUnionConsume = new UnionConsume();
            removeUnionConsume.setId(id);
            removeUnionConsume.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionConsumeList.add(removeUnionConsume);
        }
        updateBatchById(removeUnionConsumeList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionConsume updateUnionConsume) throws Exception {
        if (updateUnionConsume == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionConsume.getId();
        UnionConsume unionConsume = getById(id);
        removeCache(unionConsume);
        // (2)update db
        updateById(updateUnionConsume);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionConsume> updateUnionConsumeList) throws Exception {
        if (updateUnionConsumeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionConsume updateUnionConsume : updateUnionConsumeList) {
            idList.add(updateUnionConsume.getId());
        }
        List<UnionConsume> unionConsumeList = new ArrayList<>();
        for (Integer id : idList) {
            UnionConsume unionConsume = getById(id);
            unionConsumeList.add(unionConsume);
        }
        removeCache(unionConsumeList);
        // (2)update db
        updateBatchById(updateUnionConsumeList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionConsume newUnionConsume, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionConsumeCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionConsume);
    }

    private void setCache(List<UnionConsume> newUnionConsumeList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionConsumeCacheUtil.TYPE_MEMBER_ID:
                foreignIdKey = UnionConsumeCacheUtil.getMemberIdKey(foreignId);
                break;
            case UnionConsumeCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionConsumeCacheUtil.getUnionIdKey(foreignId);
                break;
            case UnionConsumeCacheUtil.TYPE_CARD_ID:
                foreignIdKey = UnionConsumeCacheUtil.getCardIdKey(foreignId);
                break;
            case UnionConsumeCacheUtil.TYPE_FAN_ID:
                foreignIdKey = UnionConsumeCacheUtil.getFanIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionConsumeList);
        }
    }

    private void removeCache(UnionConsume unionConsume) {
        if (unionConsume == null) {
            return;
        }
        Integer id = unionConsume.getId();
        String idKey = UnionConsumeCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer memberId = unionConsume.getMemberId();
        if (memberId != null) {
            String memberIdKey = UnionConsumeCacheUtil.getMemberIdKey(memberId);
            redisCacheUtil.remove(memberIdKey);
        }

        Integer unionId = unionConsume.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionConsumeCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }

        Integer cardId = unionConsume.getCardId();
        if (cardId != null) {
            String cardIdKey = UnionConsumeCacheUtil.getCardIdKey(cardId);
            redisCacheUtil.remove(cardIdKey);
        }

        Integer fanId = unionConsume.getFanId();
        if (fanId != null) {
            String fanIdKey = UnionConsumeCacheUtil.getFanIdKey(fanId);
            redisCacheUtil.remove(fanIdKey);
        }
    }

    private void removeCache(List<UnionConsume> unionConsumeList) {
        if (ListUtil.isEmpty(unionConsumeList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionConsume unionConsume : unionConsumeList) {
            idList.add(unionConsume.getId());
        }
        List<String> idKeyList = UnionConsumeCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> memberIdKeyList = getForeignIdKeyList(unionConsumeList, UnionConsumeCacheUtil.TYPE_MEMBER_ID);
        if (ListUtil.isNotEmpty(memberIdKeyList)) {
            redisCacheUtil.remove(memberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionConsumeList, UnionConsumeCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

        List<String> cardIdKeyList = getForeignIdKeyList(unionConsumeList, UnionConsumeCacheUtil.TYPE_CARD_ID);
        if (ListUtil.isNotEmpty(cardIdKeyList)) {
            redisCacheUtil.remove(cardIdKeyList);
        }

        List<String> fanIdKeyList = getForeignIdKeyList(unionConsumeList, UnionConsumeCacheUtil.TYPE_FAN_ID);
        if (ListUtil.isNotEmpty(fanIdKeyList)) {
            redisCacheUtil.remove(fanIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionConsume> unionConsumeList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionConsumeCacheUtil.TYPE_MEMBER_ID:
                for (UnionConsume unionConsume : unionConsumeList) {
                    Integer memberId = unionConsume.getMemberId();
                    if (memberId != null) {
                        String memberIdKey = UnionConsumeCacheUtil.getMemberIdKey(memberId);
                        result.add(memberIdKey);
                    }
                }
                break;
            case UnionConsumeCacheUtil.TYPE_UNION_ID:
                for (UnionConsume unionConsume : unionConsumeList) {
                    Integer unionId = unionConsume.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionConsumeCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            case UnionConsumeCacheUtil.TYPE_CARD_ID:
                for (UnionConsume unionConsume : unionConsumeList) {
                    Integer cardId = unionConsume.getCardId();
                    if (cardId != null) {
                        String cardIdKey = UnionConsumeCacheUtil.getCardIdKey(cardId);
                        result.add(cardIdKey);
                    }
                }
                break;
            case UnionConsumeCacheUtil.TYPE_FAN_ID:
                for (UnionConsume unionConsume : unionConsumeList) {
                    Integer fanId = unionConsume.getFanId();
                    if (fanId != null) {
                        String fanIdKey = UnionConsumeCacheUtil.getFanIdKey(fanId);
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