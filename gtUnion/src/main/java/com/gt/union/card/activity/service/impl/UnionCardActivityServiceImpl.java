package com.gt.union.card.activity.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.activity.constant.ActivityConstant;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.activity.mapper.UnionCardActivityMapper;
import com.gt.union.card.activity.service.IUnionCardActivityService;
import com.gt.union.card.activity.util.UnionCardActivityCacheUtil;
import com.gt.union.card.activity.vo.*;
import com.gt.union.card.main.constant.CardConstant;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.service.IUnionCardFanService;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.card.project.constant.ProjectConstant;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.entity.UnionCardProjectFlow;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.card.project.service.IUnionCardProjectFlowService;
import com.gt.union.card.project.service.IUnionCardProjectItemService;
import com.gt.union.card.project.service.IUnionCardProjectService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
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

    @Autowired
    private IUnionCardProjectItemService unionCardProjectItemService;

    @Autowired
    private IUnionCardProjectFlowService unionCardProjectFlowService;

    @Autowired
    private IUnionCardFanService unionCardFanService;

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
            return ActivityConstant.STATUS_END;
        } else if (currentDate.compareTo(activity.getSellBeginTime()) > 0) {
            return ActivityConstant.STATUS_SELLING;
        } else if (currentDate.compareTo(activity.getApplyEndTime()) > 0) {
            return ActivityConstant.STATUS_BEFORE_SELL;
        } else if (currentDate.compareTo(activity.getApplyBeginTime()) > 0) {
            return ActivityConstant.STATUS_APPLYING;
        } else {
            return ActivityConstant.STATUS_BEFORE_APPLY;
        }
    }

    @Override
    public CardActivityApplyVO getCardActivityApplyVOByBusIdAndIdAndUnionId(Integer busId, Integer activityId, Integer unionId) throws Exception {
        if (busId == null || activityId == null || unionId == null) {
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
        // （2）	判断activityId有效性
        UnionCardActivity activity = getByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动卡信息");
        }
        // （3）	统计服务项目数
        CardActivityApplyVO result = new CardActivityApplyVO();
        result.setActivity(activity);
        result.setItemCount(unionCardProjectItemService.countCommittedByUnionIdAndActivityId(unionId, activityId));

        return result;
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
        // （2）	判断是否是盟主：
        //   （2-1）如果是，则显示全部；
        //   （2-2）如果不是，则要求是报名项目已审核通过的
        List<CardActivityStatusVO> result = new ArrayList<>();
        List<UnionCardActivity> activityList = null;
        if (MemberConstant.IS_UNION_OWNER_YES == member.getIsUnionOwner()) {
            activityList = listByUnionId(unionId);
        } else {
            List<UnionCardProject> projectList = unionCardProjectService.listByUnionIdAndMemberIdAndStatus(unionId, member.getId(), ProjectConstant.STATUS_ACCEPT);
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
                return o2.getActivity().getCreateTime().compareTo(o1.getActivity().getCreateTime());
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

        List<CardActivityVO> result = new ArrayList<>();
        List<UnionCardActivity> activityList = listByUnionId(unionId);
        if (ListUtil.isNotEmpty(activityList)) {
            Date currentDate = DateUtil.getCurrentDate();
            for (UnionCardActivity activity : activityList) {
                CardActivityVO vo = new CardActivityVO();
                vo.setActivity(activity);

                Integer activityStatus = getStatus(activity);
                vo.setActivityStatus(activityStatus);

                // （2）报名开始后，才有参与盟员数，且	参与盟员数要求报名项目已审核通过
                if (currentDate.compareTo(activity.getApplyBeginTime()) > 0) {
                    Integer joinMemberCount = unionCardProjectService.countByUnionIdAndActivityIdAndStatus(unionId, activity.getId(), ProjectConstant.STATUS_ACCEPT);
                    vo.setJoinMemberCount(joinMemberCount);

                    UnionCardProject project = unionCardProjectService.getByUnionIdAndMemberIdAndActivityId(unionId, member.getId(), activity.getId());
                    vo.setProject(project);
                }

                //（3）在报名中，才有待审核数量
                if (ActivityConstant.STATUS_APPLYING == activityStatus) {
                    Integer projectCheckCount = unionCardProjectService.countByUnionIdAndActivityIdAndStatus(unionId, activity.getId(), ProjectConstant.STATUS_COMMITTED);
                    vo.setProjectCheckCount(projectCheckCount);
                }

                // （4）在售卡开始后，才有已售活动卡数量
                if (currentDate.compareTo(activity.getSellBeginTime()) > 0) {
                    Integer cardSellCount = unionCardService.countByUnionIdAndActivityId(unionId, activity.getId());
                    vo.setCardSellCount(cardSellCount);
                }
                result.add(vo);
            }
        }

        // （5）	按时间倒序排序
        Collections.sort(result, new Comparator<CardActivityVO>() {
            @Override
            public int compare(CardActivityVO o1, CardActivityVO o2) {
                return o2.getActivity().getCreateTime().compareTo(o1.getActivity().getCreateTime());
            }
        });
        return result;
    }

    @Override
    public List<CardActivityConsumeVO> listCardActivityConsumeVOByBusIdAndUnionIdAndFanId(Integer busId, Integer unionId, Integer fanId) throws Exception {
        if (busId == null || unionId == null || fanId == null) {
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
        // （2）	判断fanId有效性
        UnionCardFan fan = unionCardFanService.getById(fanId);
        if (fan == null) {
            throw new BusinessException("找不到粉丝信息");
        }
        // （3）	获取粉丝所有有效的活动卡信息
        List<CardActivityConsumeVO> result = new ArrayList<>();
        List<UnionCard> validActivityCardList = unionCardService.listValidByUnionIdAndFanIdAndType(unionId, fanId, CardConstant.TYPE_ACTIVITY);
        if (ListUtil.isNotEmpty(validActivityCardList)) {
            for (UnionCard validActivityCard : validActivityCardList) {
                CardActivityConsumeVO vo = new CardActivityConsumeVO();
                vo.setActivityCard(validActivityCard);

                UnionCardActivity validActivity = getByIdAndUnionId(validActivityCard.getActivityId(), unionId);
                vo.setActivity(validActivity);

                result.add(vo);
            }
        }

        return result;
    }

    @Override
    public List<UnionCardActivity> listByUnionIdAndStatus(Integer unionId, Integer status) throws Exception {
        if (unionId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardActivity> result = new ArrayList<>();
        List<UnionCardActivity> activityList = listByUnionId(unionId);
        if (ListUtil.isNotEmpty(activityList)) {
            for (UnionCardActivity activity : activityList) {
                if (status.equals(getStatus(activity))) {
                    result.add(activity);
                }
            }
        }

        return result;
    }

    @Override
    public List<CardActivityApplyItemVO> listCardActivityApplyItemVOByBusIdAndIdAndUnionId(Integer busId, Integer activityId, Integer unionId) throws Exception {
        if (busId == null || activityId == null || unionId == null) {
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
        // （2）	判断activityId有效性
        UnionCardActivity activity = getByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动信息");
        }
        // （3）	获取已提交通过的活动项目
        // （4）	按时间顺序排序
        List<CardActivityApplyItemVO> result = new ArrayList<>();
        List<UnionCardProject> projectList = unionCardProjectService.listByUnionIdAndActivityIdAndStatus(unionId, activityId, ProjectConstant.STATUS_ACCEPT);
        if (ListUtil.isNotEmpty(projectList)) {
            Collections.sort(projectList, new Comparator<UnionCardProject>() {
                @Override
                public int compare(UnionCardProject o1, UnionCardProject o2) {
                    return o1.getCreateTime().compareTo(o2.getCreateTime());
                }
            });
            for (UnionCardProject project : projectList) {
                CardActivityApplyItemVO vo = new CardActivityApplyItemVO();
                UnionMember projectMember = unionMemberService.getReadByIdAndUnionId(project.getMemberId(), project.getUnionId());
                vo.setMember(projectMember);

                List<UnionCardProjectItem> itemList = unionCardProjectItemService.listByProjectId(project.getId());
                vo.setItemList(itemList);

                result.add(vo);
            }
        }

        return result;
    }

    //***************************************** Domain Driven Design - save ********************************************

    @Override
    public void saveByBusIdAndUnionId(Integer busId, Integer unionId, UnionCardActivity vo) throws Exception {
        if (busId == null || unionId == null || vo == null) {
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
        // （2）	校验表单
        UnionCardActivity saveActivity = new UnionCardActivity();
        Date currentDate = DateUtil.getCurrentDate();
        saveActivity.setCreateTime(currentDate);
        saveActivity.setDelStatus(CommonConstant.COMMON_NO);
        saveActivity.setUnionId(unionId);
        // （2-1）名称
        String name = vo.getName();
        if (StringUtil.isEmpty(name)) {
            throw new BusinessException("名称不能为空");
        }
        if (StringUtil.getStringLength(name) > 10) {
            throw new BusinessException("名称字数不能大于10");
        }
        saveActivity.setName(name);
        // （2-2）价格
        Double price = vo.getPrice();
        if (price == null || price <= 0) {
            throw new BusinessException("价格不能为空，且不能小于0");
        }
        saveActivity.setPrice(price);
        // （2-3）发行量
        Integer amount = vo.getAmount();
        if (amount == null || amount <= 0) {
            throw new BusinessException("发行量不能为空，且不能小于0");
        }
        saveActivity.setAmount(amount);
        // （2-4）有效天数
        Integer validityDay = vo.getValidityDay();
        if (validityDay == null || validityDay <= 0) {
            throw new BusinessException("有效天数不能为空，且不能小于0");
        }
        saveActivity.setValidityDay(validityDay);
        // （2-5）报名开始时间
        Date applyBeginTime = vo.getApplyBeginTime();
        if (applyBeginTime == null || applyBeginTime.compareTo(currentDate) < 0) {
            throw new BusinessException("报名开始时间不能为空，且必须不小于当前时间");
        }
        saveActivity.setApplyBeginTime(applyBeginTime);
        // （2-6）报名结束时间
        Date applyEndTime = vo.getApplyEndTime();
        if (applyEndTime == null || applyEndTime.compareTo(applyBeginTime) < 0) {
            throw new BusinessException("报名结束时间不能为空，且必须不小于报名开始时间");
        }
        saveActivity.setApplyEndTime(applyEndTime);
        // （2-7）售卡开始时间
        Date sellBeginTime = vo.getSellBeginTime();
        if (sellBeginTime == null || sellBeginTime.compareTo(applyEndTime) < 0) {
            throw new BusinessException("售卡开始时间不能为空，且必须不小于报名结束时间");
        }
        saveActivity.setSellBeginTime(sellBeginTime);
        // （2-8）售卡结束时间
        Date sellEndTime = vo.getSellEndTime();
        if (sellEndTime == null || sellEndTime.compareTo(sellBeginTime) < 0) {
            throw new BusinessException("售卡结束时间不能为空，且必须不小于售卡开始时间");
        }
        saveActivity.setSellEndTime(sellEndTime);
        // （2-9）颜色
        String color = vo.getColor();
        if (StringUtil.isEmpty(color)) {
            throw new BusinessException("颜色不能为空");
        }
        saveActivity.setColor(color);
        // （2-10）说明
        String illustration = vo.getIllustration();
        if (StringUtil.isEmpty(illustration)) {
            throw new BusinessException("说明不能为空");
        }
        saveActivity.setIllustration(illustration);
        // （2-11）项目是否需要审核
        Integer isProjectCheck = vo.getIsProjectCheck();
        if (isProjectCheck == null) {
            throw new BusinessException("项目是否需要审核不能为空");
        }
        saveActivity.setIsProjectCheck(CommonConstant.COMMON_YES == isProjectCheck ? CommonConstant.COMMON_YES : CommonConstant.COMMON_NO);

        save(saveActivity);
    }

    //***************************************** Domain Driven Design - remove ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByBusIdAndIdAndUnionId(Integer busId, Integer activityId, Integer unionId) throws Exception {
        if (busId == null || activityId == null || unionId == null) {
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
        UnionCardActivity activity = getByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动卡信息");
        }
        // （3）	要求活动在售卡开始之前
        if (DateUtil.getCurrentDate().compareTo(activity.getSellBeginTime()) >= 0) {
            throw new BusinessException("售卡开始后活动不能删除");
        }

        List<Integer> removeProjectIdtList = new ArrayList<>();
        List<Integer> removeProjectItemIdList = new ArrayList<>();
        List<Integer> removeProjectFlowIdList = new ArrayList<>();
        List<UnionCardProject> projectList = unionCardProjectService.listByUnionIdAndActivityId(unionId, activityId);
        if (ListUtil.isNotEmpty(projectList)) {
            for (UnionCardProject project : projectList) {
                removeProjectIdtList.add(project.getId());

                List<UnionCardProjectItem> projectItemList = unionCardProjectItemService.listByProjectId(project.getId());
                if (ListUtil.isNotEmpty(projectItemList)) {
                    for (UnionCardProjectItem projectItem : projectItemList) {
                        removeProjectItemIdList.add(projectItem.getId());
                    }
                }

                List<UnionCardProjectFlow> projectFlowList = unionCardProjectFlowService.listByProjectId(project.getId());
                if (ListUtil.isNotEmpty(projectFlowList)) {
                    for (UnionCardProjectFlow projectFlow : projectFlowList) {
                        removeProjectFlowIdList.add(projectFlow.getId());
                    }
                }
            }
        }

        // （4）事务处理
        removeById(activityId);
        unionCardProjectService.removeBatchById(removeProjectIdtList);
        unionCardProjectItemService.removeBatchById(removeProjectItemIdList);
        unionCardProjectFlowService.removeBatchById(removeProjectFlowIdList);
    }

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