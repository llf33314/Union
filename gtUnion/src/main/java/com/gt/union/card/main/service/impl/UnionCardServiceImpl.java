package com.gt.union.card.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.card.main.mapper.UnionCardMapper;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.card.main.util.UnionCardCacheUtil;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 联盟卡 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Service
public class UnionCardServiceImpl extends ServiceImpl<UnionCardMapper, UnionCard> implements IUnionCardService {
    @Autowired
    public RedisCacheUtil redisCacheUtil;

    //***************************************** Domain Driven Design - get *********************************************

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<UnionCard> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.ge("validity", DateUtil.getCurrentDate())
                .eq("union_id", unionId)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);

        return selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidByFanIdAndUnionId(Integer fanId, Integer unionId) throws Exception {
        if (fanId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.ge("validity", DateUtil.getCurrentDate())
                .eq("fan_id", fanId)
                .eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);

        return selectList(entityWrapper);
    }

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    @Override
    public Double countIntegralByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> cardList = listValidByUnionId(unionId);
        BigDecimal bigDecimal = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(cardList)) {
            for (UnionCard card : cardList) {
                bigDecimal = BigDecimalUtil.add(bigDecimal, card.getIntegral());
            }
        }

        return bigDecimal.doubleValue();
    }

    @Override
    public Double countIntegralByFanIdAndUnionId(Integer fanId, Integer unionId) throws Exception {
        if (fanId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> cardList = listValidByFanIdAndUnionId(fanId, unionId);
        BigDecimal bigDecimal = BigDecimal.ZERO;
        if (ListUtil.isNotEmpty(cardList)) {
            for (UnionCard card : cardList) {
                bigDecimal = BigDecimalUtil.add(bigDecimal, card.getIntegral());
            }
        }

        return bigDecimal.doubleValue();
    }

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    @Override
    public List<UnionCard> filterByType(List<UnionCard> cardList, Integer type) throws Exception {
        if (cardList == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = new ArrayList<>();
        for (UnionCard card : cardList) {
            if (type.equals(card.getType())) {
                result.add(card);
            }
        }

        return result;
    }

    //***************************************** Object As a Service - get **********************************************

    public UnionCard getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCard result;
        // (1)cache
        String idKey = UnionCardCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionCard.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionCard> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCard> result;
        // (1)cache
        String memberIdKey = UnionCardCacheUtil.getMemberIdKey(memberId);
        if (redisCacheUtil.exists(memberIdKey)) {
            String tempStr = redisCacheUtil.get(memberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCard.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, memberId, UnionCardCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    public List<UnionCard> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCard> result;
        // (1)cache
        String unionIdKey = UnionCardCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCard.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionCardCacheUtil.TYPE_UNION_ID);
        return result;
    }

    public List<UnionCard> listByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCard> result;
        // (1)cache
        String fanIdKey = UnionCardCacheUtil.getFanIdKey(fanId);
        if (redisCacheUtil.exists(fanIdKey)) {
            String tempStr = redisCacheUtil.get(fanIdKey);
            result = JSONArray.parseArray(tempStr, UnionCard.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("fan_id", fanId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, fanId, UnionCardCacheUtil.TYPE_FAN_ID);
        return result;
    }

    public List<UnionCard> listByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCard> result;
        // (1)cache
        String activityIdKey = UnionCardCacheUtil.getActivityIdKey(activityId);
        if (redisCacheUtil.exists(activityIdKey)) {
            String tempStr = redisCacheUtil.get(activityIdKey);
            result = JSONArray.parseArray(tempStr, UnionCard.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_id", activityId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, activityId, UnionCardCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCard newUnionCard) throws Exception {
        if (newUnionCard == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionCard);
        removeCache(newUnionCard);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCard> newUnionCardList) throws Exception {
        if (newUnionCardList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionCardList);
        removeCache(newUnionCardList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionCard unionCard = getById(id);
        removeCache(unionCard);
        // (2)remove in db logically
        UnionCard removeUnionCard = new UnionCard();
        removeUnionCard.setId(id);
        removeUnionCard.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionCard);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCard> unionCardList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCard unionCard = getById(id);
            unionCardList.add(unionCard);
        }
        removeCache(unionCardList);
        // (2)remove in db logically
        List<UnionCard> removeUnionCardList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCard removeUnionCard = new UnionCard();
            removeUnionCard.setId(id);
            removeUnionCard.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardList.add(removeUnionCard);
        }
        updateBatchById(removeUnionCardList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCard updateUnionCard) throws Exception {
        if (updateUnionCard == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCard.getId();
        UnionCard unionCard = getById(id);
        removeCache(unionCard);
        // (2)update db
        updateById(updateUnionCard);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCard> updateUnionCardList) throws Exception {
        if (updateUnionCardList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionCard updateUnionCard : updateUnionCardList) {
            idList.add(updateUnionCard.getId());
        }
        List<UnionCard> unionCardList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCard unionCard = getById(id);
            unionCardList.add(unionCard);
        }
        removeCache(unionCardList);
        // (2)update db
        updateBatchById(updateUnionCardList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionCard newUnionCard, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCard);
    }

    private void setCache(List<UnionCard> newUnionCardList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionCardCacheUtil.TYPE_MEMBER_ID:
                foreignIdKey = UnionCardCacheUtil.getMemberIdKey(foreignId);
                break;
            case UnionCardCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionCardCacheUtil.getUnionIdKey(foreignId);
                break;
            case UnionCardCacheUtil.TYPE_FAN_ID:
                foreignIdKey = UnionCardCacheUtil.getFanIdKey(foreignId);
                break;
            case UnionCardCacheUtil.TYPE_ACTIVITY_ID:
                foreignIdKey = UnionCardCacheUtil.getActivityIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionCardList);
        }
    }

    private void removeCache(UnionCard unionCard) {
        if (unionCard == null) {
            return;
        }
        Integer id = unionCard.getId();
        String idKey = UnionCardCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer memberId = unionCard.getMemberId();
        if (memberId != null) {
            String memberIdKey = UnionCardCacheUtil.getMemberIdKey(memberId);
            redisCacheUtil.remove(memberIdKey);
        }

        Integer unionId = unionCard.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionCardCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }

        Integer fanId = unionCard.getFanId();
        if (fanId != null) {
            String fanIdKey = UnionCardCacheUtil.getFanIdKey(fanId);
            redisCacheUtil.remove(fanIdKey);
        }

        Integer activityId = unionCard.getActivityId();
        if (activityId != null) {
            String activityIdKey = UnionCardCacheUtil.getActivityIdKey(activityId);
            redisCacheUtil.remove(activityIdKey);
        }
    }

    private void removeCache(List<UnionCard> unionCardList) {
        if (ListUtil.isEmpty(unionCardList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCard unionCard : unionCardList) {
            idList.add(unionCard.getId());
        }
        List<String> idKeyList = UnionCardCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> memberIdKeyList = getForeignIdKeyList(unionCardList, UnionCardCacheUtil.TYPE_MEMBER_ID);
        if (ListUtil.isNotEmpty(memberIdKeyList)) {
            redisCacheUtil.remove(memberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionCardList, UnionCardCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

        List<String> fanIdKeyList = getForeignIdKeyList(unionCardList, UnionCardCacheUtil.TYPE_FAN_ID);
        if (ListUtil.isNotEmpty(fanIdKeyList)) {
            redisCacheUtil.remove(fanIdKeyList);
        }

        List<String> activityIdKeyList = getForeignIdKeyList(unionCardList, UnionCardCacheUtil.TYPE_ACTIVITY_ID);
        if (ListUtil.isNotEmpty(activityIdKeyList)) {
            redisCacheUtil.remove(activityIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionCard> unionCardList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionCardCacheUtil.TYPE_MEMBER_ID:
                for (UnionCard unionCard : unionCardList) {
                    Integer memberId = unionCard.getMemberId();
                    if (memberId != null) {
                        String memberIdKey = UnionCardCacheUtil.getMemberIdKey(memberId);
                        result.add(memberIdKey);
                    }
                }
                break;
            case UnionCardCacheUtil.TYPE_UNION_ID:
                for (UnionCard unionCard : unionCardList) {
                    Integer unionId = unionCard.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionCardCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            case UnionCardCacheUtil.TYPE_FAN_ID:
                for (UnionCard unionCard : unionCardList) {
                    Integer fanId = unionCard.getFanId();
                    if (fanId != null) {
                        String fanIdKey = UnionCardCacheUtil.getFanIdKey(fanId);
                        result.add(fanIdKey);
                    }
                }
                break;
            case UnionCardCacheUtil.TYPE_ACTIVITY_ID:
                for (UnionCard unionCard : unionCardList) {
                    Integer activityId = unionCard.getActivityId();
                    if (activityId != null) {
                        String activityIdKey = UnionCardCacheUtil.getActivityIdKey(activityId);
                        result.add(activityIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}