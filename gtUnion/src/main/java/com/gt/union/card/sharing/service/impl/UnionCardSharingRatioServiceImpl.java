package com.gt.union.card.sharing.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.activity.service.IUnionCardActivityService;
import com.gt.union.card.project.constant.ProjectConstant;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.service.IUnionCardProjectService;
import com.gt.union.card.sharing.dao.IUnionCardSharingRatioDao;
import com.gt.union.card.sharing.entity.UnionCardSharingRatio;
import com.gt.union.card.sharing.service.IUnionCardSharingRatioService;
import com.gt.union.card.sharing.util.UnionCardSharingRatioCacheUtil;
import com.gt.union.card.sharing.vo.CardSharingRatioVO;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.constant.MemberConstant;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 联盟卡售卡分成比例 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Service
public class UnionCardSharingRatioServiceImpl implements IUnionCardSharingRatioService {
    @Autowired
    private IUnionCardSharingRatioDao unionCardSharingRatioDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionCardActivityService unionCardActivityService;

    @Autowired
    private IUnionCardProjectService unionCardProjectService;

    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************


    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionCardSharingRatio> filterByDelStatus(List<UnionCardSharingRatio> unionCardSharingRatioList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardSharingRatio> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardSharingRatioList)) {
            for (UnionCardSharingRatio unionCardSharingRatio : unionCardSharingRatioList) {
                if (delStatus.equals(unionCardSharingRatio.getDelStatus())) {
                    result.add(unionCardSharingRatio);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
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
        result = unionCardSharingRatioDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionCardSharingRatio getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardSharingRatio result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionCardSharingRatio getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardSharingRatio result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionCardSharingRatio> unionCardSharingRatioList) throws Exception {
        if (unionCardSharingRatioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardSharingRatioList)) {
            for (UnionCardSharingRatio unionCardSharingRatio : unionCardSharingRatioList) {
                result.add(unionCardSharingRatio.getId());
            }
        }

        return result;
    }

    @Override
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
        entityWrapper.eq("activity_id", activityId);
        result = unionCardSharingRatioDao.selectList(entityWrapper);
        setCache(result, activityId, UnionCardSharingRatioCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRatio> listValidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRatio> result;
        // (1)cache
        String validActivityIdKey = UnionCardSharingRatioCacheUtil.getValidActivityIdKey(activityId);
        if (redisCacheUtil.exists(validActivityIdKey)) {
            String tempStr = redisCacheUtil.get(validActivityIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("activity_id", activityId);
        result = unionCardSharingRatioDao.selectList(entityWrapper);
        setValidCache(result, activityId, UnionCardSharingRatioCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRatio> listInvalidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRatio> result;
        // (1)cache
        String invalidActivityIdKey = UnionCardSharingRatioCacheUtil.getInvalidActivityIdKey(activityId);
        if (redisCacheUtil.exists(invalidActivityIdKey)) {
            String tempStr = redisCacheUtil.get(invalidActivityIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("activity_id", activityId);
        result = unionCardSharingRatioDao.selectList(entityWrapper);
        setInvalidCache(result, activityId, UnionCardSharingRatioCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("member_id", memberId);
        result = unionCardSharingRatioDao.selectList(entityWrapper);
        setCache(result, memberId, UnionCardSharingRatioCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRatio> listValidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRatio> result;
        // (1)cache
        String validMemberIdKey = UnionCardSharingRatioCacheUtil.getValidMemberIdKey(memberId);
        if (redisCacheUtil.exists(validMemberIdKey)) {
            String tempStr = redisCacheUtil.get(validMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("member_id", memberId);
        result = unionCardSharingRatioDao.selectList(entityWrapper);
        setValidCache(result, memberId, UnionCardSharingRatioCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRatio> listInvalidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRatio> result;
        // (1)cache
        String invalidMemberIdKey = UnionCardSharingRatioCacheUtil.getInvalidMemberIdKey(memberId);
        if (redisCacheUtil.exists(invalidMemberIdKey)) {
            String tempStr = redisCacheUtil.get(invalidMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("member_id", memberId);
        result = unionCardSharingRatioDao.selectList(entityWrapper);
        setInvalidCache(result, memberId, UnionCardSharingRatioCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("union_id", unionId);
        result = unionCardSharingRatioDao.selectList(entityWrapper);
        setCache(result, unionId, UnionCardSharingRatioCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRatio> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRatio> result;
        // (1)cache
        String validUnionIdKey = UnionCardSharingRatioCacheUtil.getValidUnionIdKey(unionId);
        if (redisCacheUtil.exists(validUnionIdKey)) {
            String tempStr = redisCacheUtil.get(validUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);
        result = unionCardSharingRatioDao.selectList(entityWrapper);
        setValidCache(result, unionId, UnionCardSharingRatioCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRatio> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardSharingRatio> result;
        // (1)cache
        String invalidUnionIdKey = UnionCardSharingRatioCacheUtil.getInvalidUnionIdKey(unionId);
        if (redisCacheUtil.exists(invalidUnionIdKey)) {
            String tempStr = redisCacheUtil.get(invalidUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardSharingRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);
        result = unionCardSharingRatioDao.selectList(entityWrapper);
        setInvalidCache(result, unionId, UnionCardSharingRatioCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionCardSharingRatio> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardSharingRatio> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page<UnionCardSharingRatio> pageSupport(Page page, EntityWrapper<UnionCardSharingRatio> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardSharingRatioDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardSharingRatio newUnionCardSharingRatio) throws Exception {
        if (newUnionCardSharingRatio == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionCardSharingRatioDao.insert(newUnionCardSharingRatio);
        removeCache(newUnionCardSharingRatio);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardSharingRatio> newUnionCardSharingRatioList) throws Exception {
        if (newUnionCardSharingRatioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionCardSharingRatioDao.insertBatch(newUnionCardSharingRatioList);
        removeCache(newUnionCardSharingRatioList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
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
        unionCardSharingRatioDao.updateById(removeUnionCardSharingRatio);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardSharingRatio> unionCardSharingRatioList = listByIdList(idList);
        removeCache(unionCardSharingRatioList);
        // (2)remove in db logically
        List<UnionCardSharingRatio> removeUnionCardSharingRatioList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardSharingRatio removeUnionCardSharingRatio = new UnionCardSharingRatio();
            removeUnionCardSharingRatio.setId(id);
            removeUnionCardSharingRatio.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardSharingRatioList.add(removeUnionCardSharingRatio);
        }
        unionCardSharingRatioDao.updateBatchById(removeUnionCardSharingRatioList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
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
        unionCardSharingRatioDao.updateById(updateUnionCardSharingRatio);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardSharingRatio> updateUnionCardSharingRatioList) throws Exception {
        if (updateUnionCardSharingRatioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionCardSharingRatioList);
        List<UnionCardSharingRatio> unionCardSharingRatioList = listByIdList(idList);
        removeCache(unionCardSharingRatioList);
        // (2)update db
        unionCardSharingRatioDao.updateBatchById(updateUnionCardSharingRatioList);
    }

    //********************************************* Object As a Service - cache support ********************************

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

    private void setValidCache(List<UnionCardSharingRatio> newUnionCardSharingRatioList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionCardSharingRatioCacheUtil.TYPE_ACTIVITY_ID:
                validForeignIdKey = UnionCardSharingRatioCacheUtil.getValidActivityIdKey(foreignId);
                break;
            case UnionCardSharingRatioCacheUtil.TYPE_MEMBER_ID:
                validForeignIdKey = UnionCardSharingRatioCacheUtil.getValidMemberIdKey(foreignId);
                break;
            case UnionCardSharingRatioCacheUtil.TYPE_UNION_ID:
                validForeignIdKey = UnionCardSharingRatioCacheUtil.getValidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionCardSharingRatioList);
        }
    }

    private void setInvalidCache(List<UnionCardSharingRatio> newUnionCardSharingRatioList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionCardSharingRatioCacheUtil.TYPE_ACTIVITY_ID:
                invalidForeignIdKey = UnionCardSharingRatioCacheUtil.getInvalidActivityIdKey(foreignId);
                break;
            case UnionCardSharingRatioCacheUtil.TYPE_MEMBER_ID:
                invalidForeignIdKey = UnionCardSharingRatioCacheUtil.getInvalidMemberIdKey(foreignId);
                break;
            case UnionCardSharingRatioCacheUtil.TYPE_UNION_ID:
                invalidForeignIdKey = UnionCardSharingRatioCacheUtil.getInvalidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionCardSharingRatioList);
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

            String validActivityIdKey = UnionCardSharingRatioCacheUtil.getValidActivityIdKey(activityId);
            redisCacheUtil.remove(validActivityIdKey);

            String invalidActivityIdKey = UnionCardSharingRatioCacheUtil.getInvalidActivityIdKey(activityId);
            redisCacheUtil.remove(invalidActivityIdKey);
        }

        Integer memberId = unionCardSharingRatio.getMemberId();
        if (memberId != null) {
            String memberIdKey = UnionCardSharingRatioCacheUtil.getMemberIdKey(memberId);
            redisCacheUtil.remove(memberIdKey);

            String validMemberIdKey = UnionCardSharingRatioCacheUtil.getValidMemberIdKey(memberId);
            redisCacheUtil.remove(validMemberIdKey);

            String invalidMemberIdKey = UnionCardSharingRatioCacheUtil.getInvalidMemberIdKey(memberId);
            redisCacheUtil.remove(invalidMemberIdKey);
        }

        Integer unionId = unionCardSharingRatio.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionCardSharingRatioCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);

            String validUnionIdKey = UnionCardSharingRatioCacheUtil.getValidUnionIdKey(unionId);
            redisCacheUtil.remove(validUnionIdKey);

            String invalidUnionIdKey = UnionCardSharingRatioCacheUtil.getInvalidUnionIdKey(unionId);
            redisCacheUtil.remove(invalidUnionIdKey);
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

                        String validActivityIdKey = UnionCardSharingRatioCacheUtil.getValidActivityIdKey(activityId);
                        result.add(validActivityIdKey);

                        String invalidActivityIdKey = UnionCardSharingRatioCacheUtil.getInvalidActivityIdKey(activityId);
                        result.add(invalidActivityIdKey);
                    }
                }
                break;
            case UnionCardSharingRatioCacheUtil.TYPE_MEMBER_ID:
                for (UnionCardSharingRatio unionCardSharingRatio : unionCardSharingRatioList) {
                    Integer memberId = unionCardSharingRatio.getMemberId();
                    if (memberId != null) {
                        String memberIdKey = UnionCardSharingRatioCacheUtil.getMemberIdKey(memberId);
                        result.add(memberIdKey);

                        String validMemberIdKey = UnionCardSharingRatioCacheUtil.getValidMemberIdKey(memberId);
                        result.add(validMemberIdKey);

                        String invalidMemberIdKey = UnionCardSharingRatioCacheUtil.getInvalidMemberIdKey(memberId);
                        result.add(invalidMemberIdKey);
                    }
                }
                break;
            case UnionCardSharingRatioCacheUtil.TYPE_UNION_ID:
                for (UnionCardSharingRatio unionCardSharingRatio : unionCardSharingRatioList) {
                    Integer unionId = unionCardSharingRatio.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionCardSharingRatioCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);

                        String validUnionIdKey = UnionCardSharingRatioCacheUtil.getValidUnionIdKey(unionId);
                        result.add(validUnionIdKey);

                        String invalidUnionIdKey = UnionCardSharingRatioCacheUtil.getInvalidUnionIdKey(unionId);
                        result.add(invalidUnionIdKey);
                    }
                }
                break;

            default:
                break;
        }
        return result;
    }

    // TODO


    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public UnionCardSharingRatio getByUnionIdAndMemberIdAndActivityId(Integer unionId, Integer memberId, Integer activityId) throws Exception {
        if (unionId == null || memberId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardSharingRatio> result = listByActivityId(activityId);
        result = filterByMemberId(result, memberId);
        result = filterByUnionId(result, unionId);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<CardSharingRatioVO> listCardSharingRatioVOByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception {
        if (busId == null || unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        final UnionMember member = unionMemberService.getReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_READ_REJECT);
        }
        // （2）	判断activityId有效性
        UnionCardActivity activity = unionCardActivityService.getByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动信息");
        }
        // （3）	获取已审核通过的报名项目，并获取对应的分成比例设置
        List<CardSharingRatioVO> result = new ArrayList<>();
        List<UnionCardProject> projectList = unionCardProjectService.listByUnionIdAndActivityIdAndStatus(unionId, activityId, ProjectConstant.STATUS_ACCEPT);
        if (ListUtil.isNotEmpty(projectList)) {
            for (UnionCardProject project : projectList) {
                CardSharingRatioVO vo = new CardSharingRatioVO();

                UnionCardSharingRatio ratio = getByUnionIdAndMemberIdAndActivityId(unionId, project.getMemberId(), activityId);
                vo.setSharingRatio(ratio);

                UnionMember ratioMember = unionMemberService.getReadByIdAndUnionId(project.getMemberId(), unionId);
                vo.setMember(ratioMember);

                result.add(vo);
            }
        }
        // （4）按盟主>当前盟员>其他盟员，其他盟员按时间顺序排序
        Collections.sort(result, new Comparator<CardSharingRatioVO>() {
            @Override
            public int compare(CardSharingRatioVO o1, CardSharingRatioVO o2) {
                UnionMember o1Member = o1.getMember();
                if (MemberConstant.IS_UNION_OWNER_YES == o1Member.getIsUnionOwner()) {
                    return -1;
                }
                UnionMember o2Member = o2.getMember();
                if (MemberConstant.IS_UNION_OWNER_YES == o2Member.getIsUnionOwner()) {
                    return 1;
                }
                if (member.getId().equals(o1Member.getId())) {
                    return -1;
                }
                if (member.getId().equals(o2Member.getId())) {
                    return 1;
                }
                return o1.getSharingRatio().getCreateTime().compareTo(o2.getSharingRatio().getCreateTime());
            }
        });
        return result;
    }

    @Override
    public List<UnionCardSharingRatio> listByUnionIdAndActivityId(Integer unionId, Integer activityId) throws Exception {
        if (unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardSharingRatio> result = listByActivityId(activityId);
        result = filterByUnionId(result, unionId);

        return result;
    }

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRatioByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId, List<CardSharingRatioVO> voList) throws Exception {
        if (busId == null || unionId == null || activityId == null || voList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_NEED_OWNER);
        }
        // （2）	判断activityId有效性
        UnionCardActivity activity = unionCardActivityService.getByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动信息");
        }
        // （3）	要求activity是在售卡开始之前
        if (DateUtil.getCurrentDate().compareTo(activity.getSellBeginTime()) > 0) {
            throw new BusinessException("只能在活动卡售卖开始前更改");
        }
        // （4）	要求售卡分成之和为100%
        List<UnionCardSharingRatio> updateRatioList = new ArrayList<>();
        List<UnionCardSharingRatio> saveRatioList = new ArrayList<>();
        BigDecimal bdRatioSum = BigDecimal.ZERO;
        for (CardSharingRatioVO vo : voList) {
            Double ratio = vo.getSharingRatio().getRatio();
            if (ratio == null) {
                throw new BusinessException("售卡分成比例不能为空");
            }
            if (ratio < 0 || ratio > 1) {
                throw new BusinessException("售卡分成比例必须在0和100之间");
            }
            Integer memberId = vo.getMember().getId();
            UnionCardSharingRatio dbRatio = getByUnionIdAndMemberIdAndActivityId(unionId, memberId, activityId);
            if (dbRatio != null) {
                UnionCardSharingRatio updateRatio = new UnionCardSharingRatio();
                updateRatio.setId(dbRatio.getId());
                updateRatio.setModifyTime(DateUtil.getCurrentDate());
                updateRatio.setRatio(ratio);
                updateRatioList.add(updateRatio);
            } else {
                UnionCardSharingRatio saveRatio = new UnionCardSharingRatio();
                saveRatio.setDelStatus(CommonConstant.COMMON_NO);
                saveRatio.setCreateTime(DateUtil.getCurrentDate());
                saveRatio.setActivityId(activityId);
                saveRatio.setMemberId(memberId);
                saveRatio.setUnionId(unionId);
                saveRatio.setRatio(ratio);
                saveRatioList.add(saveRatio);
            }
            bdRatioSum = BigDecimalUtil.add(bdRatioSum, ratio);
        }
        if (bdRatioSum.doubleValue() != 1.0) {
            throw new BusinessException("售卡分成比例之和必须等于100");
        }

        // （5）事务操作
        if (ListUtil.isNotEmpty(updateRatioList)) {
            updateBatch(updateRatioList);
        }
        if (ListUtil.isNotEmpty(saveRatioList)) {
            saveBatch(saveRatioList);
        }
    }

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    @Override
    public List<UnionCardSharingRatio> filterByActivityId(List<UnionCardSharingRatio> ratioList, Integer activityId) throws Exception {
        if (ratioList == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardSharingRatio> result = new ArrayList<>();
        for (UnionCardSharingRatio ratio : ratioList) {
            if (activityId.equals(ratio.getActivityId())) {
                result.add(ratio);
            }
        }

        return result;
    }

    @Override
    public List<UnionCardSharingRatio> filterByMemberId(List<UnionCardSharingRatio> ratioList, Integer memberId) throws Exception {
        if (ratioList == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardSharingRatio> result = new ArrayList<>();
        for (UnionCardSharingRatio ratio : ratioList) {
            if (memberId.equals(ratio.getMemberId())) {
                result.add(ratio);
            }
        }

        return result;
    }

    @Override
    public List<UnionCardSharingRatio> filterByUnionId(List<UnionCardSharingRatio> ratioList, Integer unionId) throws Exception {
        if (ratioList == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardSharingRatio> result = new ArrayList<>();
        for (UnionCardSharingRatio ratio : ratioList) {
            if (unionId.equals(ratio.getUnionId())) {
                result.add(ratio);
            }
        }

        return result;
    }


}