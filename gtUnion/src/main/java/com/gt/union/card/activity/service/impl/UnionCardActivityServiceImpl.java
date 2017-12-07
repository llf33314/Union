package com.gt.union.card.activity.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.activity.mapper.UnionCardActivityMapper;
import com.gt.union.card.activity.service.IUnionCardActivityService;
import com.gt.union.card.activity.util.UnionCardActivityCacheUtil;
import com.gt.union.card.activity.vo.CardActivityStatusVO;
import com.gt.union.card.activity.vo.CardActivityVO;
import com.gt.union.card.constant.CardConstant;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.service.IUnionCardProjectService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
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

import java.util.*;

/**
 * 活动 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Service
public class UnionCardActivityServiceImpl extends ServiceImpl<UnionCardActivityMapper, UnionCardActivity> implements IUnionCardActivityService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionCardProjectService unionCardProjectService;

    @Autowired
    private IUnionCardService unionCardService;

    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public UnionCardActivity getByIdAndUnionId(Integer activityId, Integer unionId) throws Exception {
        if (activityId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionCardActivity result = getById(activityId);

        return result != null && unionId.equals(result.getUnionId()) ? result : null;
    }

    @Override
    public Integer getStatus(UnionCardActivity activity) throws Exception {
        if (activity == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        Date currentDate = DateUtil.getCurrentDate();
        if (currentDate.compareTo(activity.getSellEndTime()) > 0) {
            return CardConstant.ACTIVITY_STATUS_END;
        } else if (currentDate.compareTo(activity.getSellBeginTime()) > 0) {
            return CardConstant.ACTIVITY_STATUS_SELLING;
        } else if (currentDate.compareTo(activity.getApplyEndTime()) > 0) {
            return CardConstant.ACTIVITY_STATUS_BEFORE_SELL;
        } else if (currentDate.compareTo(activity.getApplyBeginTime()) > 0) {
            return CardConstant.ACTIVITY_STATUS_APPLYING;
        } else {
            return CardConstant.ACTIVITY_STATUS_BEFORE_APPLY;
        }
    }

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<CardActivityStatusVO> listCardActivityStatusVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
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
        // （2）	如果是盟主，则全部显示
        // 否则要求盟员已报名活动且项目已通过
        List<CardActivityStatusVO> result = new ArrayList<>();
        List<UnionCardActivity> activityList = null;
        if (MemberConstant.IS_UNION_OWNER_YES == member.getIsUnionOwner()) {
            activityList = listByUnionId(unionId);
        } else {
            List<UnionCardProject> projectList = unionCardProjectService.listByMemberIdAndUnionIdAndStatus(member.getId(), unionId, CardConstant.PROJECT_STATUS_ACCEPT);
            if (ListUtil.isNotEmpty(projectList)) {
                activityList = new ArrayList<>();
                for (UnionCardProject project : projectList) {
                    UnionCardActivity activity = getByIdAndUnionId(project.getActivityId(), project.getUnionId());
                    if (activity != null) {
                        activityList.add(activity);
                    }
                }
            }
        }
        if (ListUtil.isNotEmpty(activityList)) {
            for (UnionCardActivity activity : activityList) {
                CardActivityStatusVO vo = new CardActivityStatusVO();
                vo.setActivity(activity);
                Integer activityStatus = getStatus(activity);
                vo.setActivityStatus(activityStatus);
                result.add(vo);
            }
        }
        // （3）	按时间倒序排序
        Collections.sort(result, new Comparator<CardActivityStatusVO>() {
            @Override
            public int compare(CardActivityStatusVO o1, CardActivityStatusVO o2) {
                return o1.getActivity().getCreateTime().compareTo(o2.getActivity().getCreateTime());
            }
        });

        return result;
    }

    @Override
    public List<CardActivityVO> listCardActivityVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
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
        // （2）	参与盟员数要求活动项目已报名且已通过
        List<CardActivityVO> result = new ArrayList<>();
        List<UnionCardActivity> activityList = listByUnionId(unionId);
        if (ListUtil.isNotEmpty(activityList)) {
            Date currentDate = DateUtil.getCurrentDate();
            for (UnionCardActivity activity : activityList) {
                CardActivityVO vo = new CardActivityVO();
                vo.setActivity(activity);
                Integer activityStatus = getStatus(activity);
                vo.setActivityStatus(activityStatus);


                if (currentDate.compareTo(activity.getApplyBeginTime()) > 0) {
                    Integer joinMemberCount = unionCardProjectService.countByActivityIdAndUnionIdAndStatus(activity.getId(), unionId, CardConstant.PROJECT_STATUS_ACCEPT);
                    vo.setJoinMemberCount(joinMemberCount);

                    UnionCardProject project = unionCardProjectService.getByActivityIdAndMemberIdAndUnionId(activity.getId(), member.getId(), unionId);
                    vo.setProject(project);
                }

                //（3）	如果在报名中，则有待审核数量
                if (CardConstant.ACTIVITY_STATUS_APPLYING == activityStatus) {
                    Integer projectCheckCount = unionCardProjectService.countByActivityIdAndUnionIdAndStatus(activity.getId(), unionId, CardConstant.PROJECT_STATUS_COMMITTED);
                    vo.setProjectCheckCount(projectCheckCount);
                }

                // （4）如果在售卖开始后，则有已售活动卡数量
                if (currentDate.compareTo(activity.getSellBeginTime()) > 0) {
                    Integer cardSellCount = unionCardService.countByActivityIdAndUnionId(activity.getId(), unionId);
                    vo.setCardSellCount(cardSellCount);
                }
                result.add(vo);
            }
        }

        // （5）	按时间倒序排序
        Collections.sort(result, new Comparator<CardActivityVO>() {
            @Override
            public int compare(CardActivityVO o1, CardActivityVO o2) {
                return o1.getActivity().getCreateTime().compareTo(o2.getActivity().getCreateTime());
            }
        });
        return result;
    }

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Object As a Service - get **********************************************

    public UnionCardActivity getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardActivity result;
        // (1)cache
        String idKey = UnionCardActivityCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionCardActivity.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardActivity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionCardActivity> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardActivity> result;
        // (1)cache
        String unionIdKey = UnionCardActivityCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardActivity.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardActivity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionCardActivityCacheUtil.TYPE_UNION_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardActivity newUnionCardActivity) throws Exception {
        if (newUnionCardActivity == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionCardActivity);
        removeCache(newUnionCardActivity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardActivity> newUnionCardActivityList) throws Exception {
        if (newUnionCardActivityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionCardActivityList);
        removeCache(newUnionCardActivityList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionCardActivity unionCardActivity = getById(id);
        removeCache(unionCardActivity);
        // (2)remove in db logically
        UnionCardActivity removeUnionCardActivity = new UnionCardActivity();
        removeUnionCardActivity.setId(id);
        removeUnionCardActivity.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionCardActivity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardActivity> unionCardActivityList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardActivity unionCardActivity = getById(id);
            unionCardActivityList.add(unionCardActivity);
        }
        removeCache(unionCardActivityList);
        // (2)remove in db logically
        List<UnionCardActivity> removeUnionCardActivityList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardActivity removeUnionCardActivity = new UnionCardActivity();
            removeUnionCardActivity.setId(id);
            removeUnionCardActivity.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardActivityList.add(removeUnionCardActivity);
        }
        updateBatchById(removeUnionCardActivityList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardActivity updateUnionCardActivity) throws Exception {
        if (updateUnionCardActivity == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCardActivity.getId();
        UnionCardActivity unionCardActivity = getById(id);
        removeCache(unionCardActivity);
        // (2)update db
        updateById(updateUnionCardActivity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardActivity> updateUnionCardActivityList) throws Exception {
        if (updateUnionCardActivityList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionCardActivity updateUnionCardActivity : updateUnionCardActivityList) {
            idList.add(updateUnionCardActivity.getId());
        }
        List<UnionCardActivity> unionCardActivityList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardActivity unionCardActivity = getById(id);
            unionCardActivityList.add(unionCardActivity);
        }
        removeCache(unionCardActivityList);
        // (2)update db
        updateBatchById(updateUnionCardActivityList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionCardActivity newUnionCardActivity, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardActivityCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCardActivity);
    }

    private void setCache(List<UnionCardActivity> newUnionCardActivityList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionCardActivityCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionCardActivityCacheUtil.getUnionIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionCardActivityList);
        }
    }

    private void removeCache(UnionCardActivity unionCardActivity) {
        if (unionCardActivity == null) {
            return;
        }
        Integer id = unionCardActivity.getId();
        String idKey = UnionCardActivityCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer unionId = unionCardActivity.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionCardActivityCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }
    }

    private void removeCache(List<UnionCardActivity> unionCardActivityList) {
        if (ListUtil.isEmpty(unionCardActivityList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCardActivity unionCardActivity : unionCardActivityList) {
            idList.add(unionCardActivity.getId());
        }
        List<String> idKeyList = UnionCardActivityCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> unionIdKeyList = getForeignIdKeyList(unionCardActivityList, UnionCardActivityCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionCardActivity> unionCardActivityList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionCardActivityCacheUtil.TYPE_UNION_ID:
                for (UnionCardActivity unionCardActivity : unionCardActivityList) {
                    Integer unionId = unionCardActivity.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionCardActivityCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
            default:
                break;
        }
        return result;
    }

}