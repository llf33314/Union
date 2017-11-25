package com.gt.union.card.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.entity.UnionCardSharingRatio;
import com.gt.union.card.mapper.UnionCardSharingRatioMapper;
import com.gt.union.card.service.IUnionCardSharingRatioService;
import com.gt.union.card.util.UnionCardSharingRatioCacheUtil;
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
 * 联盟卡售卡分成比例 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Service
public class UnionCardSharingRatioServiceImpl extends ServiceImpl<UnionCardSharingRatioMapper, UnionCardSharingRatio> implements IUnionCardSharingRatioService {
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

    public UnionCardSharingRatio getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardSharingRatio result;
        // (1)cache
        String idKey = UnionCardSharingRatioCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionCardSharingRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionCardSharingRatio> listByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRatio> result;
        // (1)cache
        String activityIdKey = UnionCardSharingRatioCacheUtil.getActivityIdKey(activityId);
        if (redisCacheUtil.exists(activityIdKey)) {
            String tempStr = redisCacheUtil.get(activityIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_id", activityId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, activityId, UnionCardSharingRatioCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    public List<UnionCardSharingRatio> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRatio> result;
        // (1)cache
        String memberIdKey = UnionCardSharingRatioCacheUtil.getMemberIdKey(memberId);
        if (redisCacheUtil.exists(memberIdKey)) {
            String tempStr = redisCacheUtil.get(memberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, memberId, UnionCardSharingRatioCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    public List<UnionCardSharingRatio> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRatio> result;
        // (1)cache
        String unionIdKey = UnionCardSharingRatioCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionCardSharingRatioCacheUtil.TYPE_UNION_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardSharingRatio newUnionCardSharingRatio) throws Exception {
        if (newUnionCardSharingRatio == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionCardSharingRatio);
        removeCache(newUnionCardSharingRatio);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardSharingRatio> newUnionCardSharingRatioList) throws Exception {
        if (newUnionCardSharingRatioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionCardSharingRatioList);
        removeCache(newUnionCardSharingRatioList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionCardSharingRatio unionCardSharingRatio = getById(id);
        removeCache(unionCardSharingRatio);
        // (2)remove in db logically
        UnionCardSharingRatio removeUnionCardSharingRatio = new UnionCardSharingRatio();
        removeUnionCardSharingRatio.setId(id);
        removeUnionCardSharingRatio.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionCardSharingRatio);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardSharingRatio> unionCardSharingRatioList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardSharingRatio unionCardSharingRatio = getById(id);
            unionCardSharingRatioList.add(unionCardSharingRatio);
        }
        removeCache(unionCardSharingRatioList);
        // (2)remove in db logically
        List<UnionCardSharingRatio> removeUnionCardSharingRatioList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardSharingRatio removeUnionCardSharingRatio = new UnionCardSharingRatio();
            removeUnionCardSharingRatio.setId(id);
            removeUnionCardSharingRatio.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardSharingRatioList.add(removeUnionCardSharingRatio);
        }
        updateBatchById(removeUnionCardSharingRatioList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardSharingRatio updateUnionCardSharingRatio) throws Exception {
        if (updateUnionCardSharingRatio == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCardSharingRatio.getId();
        UnionCardSharingRatio unionCardSharingRatio = getById(id);
        removeCache(unionCardSharingRatio);
        // (2)update db
        updateById(updateUnionCardSharingRatio);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardSharingRatio> updateUnionCardSharingRatioList) throws Exception {
        if (updateUnionCardSharingRatioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionCardSharingRatio updateUnionCardSharingRatio : updateUnionCardSharingRatioList) {
            idList.add(updateUnionCardSharingRatio.getId());
        }
        List<UnionCardSharingRatio> unionCardSharingRatioList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardSharingRatio unionCardSharingRatio = getById(id);
            unionCardSharingRatioList.add(unionCardSharingRatio);
        }
        removeCache(unionCardSharingRatioList);
        // (2)update db
        updateBatchById(updateUnionCardSharingRatioList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionCardSharingRatio newUnionCardSharingRatio, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardSharingRatioCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCardSharingRatio);
    }

    private void setCache(List<UnionCardSharingRatio> newUnionCardSharingRatioList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionCardSharingRatioCacheUtil.TYPE_ACTIVITY_ID:
                foreignIdKey = UnionCardSharingRatioCacheUtil.getActivityIdKey(foreignId);
                break;
            case UnionCardSharingRatioCacheUtil.TYPE_MEMBER_ID:
                foreignIdKey = UnionCardSharingRatioCacheUtil.getMemberIdKey(foreignId);
                break;
            case UnionCardSharingRatioCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionCardSharingRatioCacheUtil.getUnionIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionCardSharingRatioList);
        }
    }

    private void removeCache(UnionCardSharingRatio unionCardSharingRatio) {
        if (unionCardSharingRatio == null) {
            return;
        }
        Integer id = unionCardSharingRatio.getId();
        String idKey = UnionCardSharingRatioCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer activityId = unionCardSharingRatio.getActivityId();
        if (activityId != null) {
            String activityIdKey = UnionCardSharingRatioCacheUtil.getActivityIdKey(activityId);
            redisCacheUtil.remove(activityIdKey);
        }

        Integer memberId = unionCardSharingRatio.getMemberId();
        if (memberId != null) {
            String memberIdKey = UnionCardSharingRatioCacheUtil.getMemberIdKey(memberId);
            redisCacheUtil.remove(memberIdKey);
        }

        Integer unionId = unionCardSharingRatio.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionCardSharingRatioCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }
    }

    private void removeCache(List<UnionCardSharingRatio> unionCardSharingRatioList) {
        if (ListUtil.isEmpty(unionCardSharingRatioList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCardSharingRatio unionCardSharingRatio : unionCardSharingRatioList) {
            idList.add(unionCardSharingRatio.getId());
        }
        List<String> idKeyList = UnionCardSharingRatioCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> activityIdKeyList = getForeignIdKeyList(unionCardSharingRatioList, UnionCardSharingRatioCacheUtil.TYPE_ACTIVITY_ID);
        if (ListUtil.isNotEmpty(activityIdKeyList)) {
            redisCacheUtil.remove(activityIdKeyList);
        }

        List<String> memberIdKeyList = getForeignIdKeyList(unionCardSharingRatioList, UnionCardSharingRatioCacheUtil.TYPE_MEMBER_ID);
        if (ListUtil.isNotEmpty(memberIdKeyList)) {
            redisCacheUtil.remove(memberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionCardSharingRatioList, UnionCardSharingRatioCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionCardSharingRatio> unionCardSharingRatioList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionCardSharingRatioCacheUtil.TYPE_ACTIVITY_ID:
                for (UnionCardSharingRatio unionCardSharingRatio : unionCardSharingRatioList) {
                    Integer activityId = unionCardSharingRatio.getActivityId();
                    if (activityId != null) {
                        String activityIdKey = UnionCardSharingRatioCacheUtil.getActivityIdKey(activityId);
                        result.add(activityIdKey);
                    }
                }
                break;
            case UnionCardSharingRatioCacheUtil.TYPE_MEMBER_ID:
                for (UnionCardSharingRatio unionCardSharingRatio : unionCardSharingRatioList) {
                    Integer memberId = unionCardSharingRatio.getMemberId();
                    if (memberId != null) {
                        String memberIdKey = UnionCardSharingRatioCacheUtil.getMemberIdKey(memberId);
                        result.add(memberIdKey);
                    }
                }
                break;
            case UnionCardSharingRatioCacheUtil.TYPE_UNION_ID:
                for (UnionCardSharingRatio unionCardSharingRatio : unionCardSharingRatioList) {
                    Integer unionId = unionCardSharingRatio.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionCardSharingRatioCacheUtil.getUnionIdKey(unionId);
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