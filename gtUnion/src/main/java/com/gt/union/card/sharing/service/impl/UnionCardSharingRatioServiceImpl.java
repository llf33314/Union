package com.gt.union.card.sharing.service.impl;

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
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.constant.MemberConstant;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.omg.CORBA.TIMEOUT;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionCardActivityService unionCardActivityService;

    @Autowired
    private IUnionCardProjectService unionCardProjectService;

    @Autowired
    private RedissonClient redissonClient;

    //********************************************* Base On Business - get *********************************************

    @Override
    public UnionCardSharingRatio getValidByUnionIdAndMemberIdAndActivityId(Integer unionId, Integer memberId, Integer activityId) throws Exception {
        if (unionId == null || memberId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("member_id", memberId)
                .eq("activity_id", activityId);

        return unionCardSharingRatioDao.selectOne(entityWrapper);
    }

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<UnionCardSharingRatio> listValidByUnionIdAndActivityId(Integer unionId, Integer activityId, String orderBy, boolean isAsc) throws Exception {
        if (unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("activity_id", activityId)
                .orderBy(orderBy, isAsc);

        return unionCardSharingRatioDao.selectList(entityWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CardSharingRatioVO> listCardSharingRatioVOByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception {
        if (busId == null || unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // 判断union有效性
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        // 判断member读权限
        final UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // 判断activityId有效性
        UnionCardActivity activity = unionCardActivityService.getValidByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动信息");
        }
        // 获取已审核通过的报名项目，并获取对应的分成比例设置
        List<CardSharingRatioVO> result = new ArrayList<>();
        List<UnionCardProject> projectList = unionCardProjectService.listValidByUnionIdAndActivityIdAndStatus(unionId, activityId, ProjectConstant.STATUS_ACCEPT, "create_time", true);
        if (ListUtil.isNotEmpty(projectList)) {
            if (activity.getSellBeginTime().compareTo(DateUtil.getCurrentDate()) < 0) {
                if (!existValidByUnionIdAndActivityId(unionId, activityId)) {
                    autoEqualDivisionRatio(projectList);
                }
            } else {
                projectList = unionCardProjectService.filterInvalidMemberId(projectList);
            }
            for (UnionCardProject project : projectList) {
                CardSharingRatioVO vo = new CardSharingRatioVO();

                UnionMember ratioMember = unionMemberService.getById(project.getMemberId());
                vo.setMember(ratioMember);

                UnionCardSharingRatio ratio = getValidByUnionIdAndMemberIdAndActivityId(unionId, project.getMemberId(), activityId);
                vo.setSharingRatio(ratio);

                result.add(vo);
            }
        }
        // 按盟主>当前盟员>其他盟员，其他盟员按时间顺序排序
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

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId, List<CardSharingRatioVO> voList) throws Exception {
        if (busId == null || unionId == null || activityId == null || voList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // 判断union有效性和member读权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_OWNER_ERROR);
        }
        // 判断activityId有效性
        UnionCardActivity activity = unionCardActivityService.getValidByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动信息");
        }
        // 要求activity是在售卡开始之前
        if (DateUtil.getCurrentDate().compareTo(activity.getSellBeginTime()) > 0) {
            throw new BusinessException("只能在活动卡售卖开始前更改");
        }
        // 要求售卡分成之和为100%
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
            if (!unionMemberService.existValidReadByIdAndUnionId(memberId, unionId)) {
                throw new BusinessException("无法对不存在或已退盟的盟员设置售卡分成比例");
            }
            UnionCardSharingRatio dbRatio = getValidByUnionIdAndMemberIdAndActivityId(unionId, memberId, activityId);
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

        // 事务操作
        if (ListUtil.isNotEmpty(updateRatioList)) {
            updateBatch(updateRatioList);
        }
        if (ListUtil.isNotEmpty(saveRatioList)) {
            saveBatch(saveRatioList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoEqualDivisionRatio(List<UnionCardProject> projectList) throws Exception {
        if (projectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionCardProject project = projectList.get(0);
        Integer activityId = project.getActivityId();
        Integer unionId = project.getUnionId();
        UnionCardActivity activity = unionCardActivityService.getValidByIdAndUnionId(activityId, unionId);
        String autoEqualDivisionRatioLockKey = UnionCardSharingRatioCacheUtil.getAutoEqualDivisionRatioLockKey();
        RLock rLock = redissonClient.getLock(autoEqualDivisionRatioLockKey);
        try {
            rLock.lock(5, TimeUnit.SECONDS);
            if (!existValidByUnionIdAndActivityId(unionId, activityId) && activity.getSellBeginTime().compareTo(DateUtil.getCurrentDate()) < 0) {
                Date currentDate = DateUtil.getCurrentDate();
                boolean isIncludeUnionOwnerId = unionCardProjectService.existUnionOwnerId(projectList);
                projectList = unionCardProjectService.filterInvalidMemberId(projectList);

                List<UnionCardSharingRatio> saveSharingRatioList = new ArrayList<>();
                if (ListUtil.isNotEmpty(projectList)) {
                    int size = projectList.size();
                    BigDecimal averageSharingRatio = BigDecimalUtil.divide(1.0, Double.valueOf(size));
                    BigDecimal sharedRatioSum = BigDecimal.ZERO;
                    for (int i = size - 1; i >= 0; i--) {
                        UnionCardProject tempProject = projectList.get(i);

                        UnionCardSharingRatio saveSharingRatio = new UnionCardSharingRatio();
                        saveSharingRatio.setDelStatus(CommonConstant.DEL_STATUS_NO);
                        saveSharingRatio.setCreateTime(currentDate);
                        saveSharingRatio.setUnionId(unionId);
                        saveSharingRatio.setMemberId(tempProject.getMemberId());
                        saveSharingRatio.setActivityId(activityId);
                        saveSharingRatio.setRatio(BigDecimalUtil.toDouble(averageSharingRatio));
                        saveSharingRatioList.add(saveSharingRatio);

                        sharedRatioSum = BigDecimalUtil.add(sharedRatioSum, averageSharingRatio);
                    }
                    BigDecimal surplusRatio = BigDecimalUtil.subtract(1.0, sharedRatioSum);
                    if (surplusRatio.doubleValue() > 0) {
                        if (isIncludeUnionOwnerId) {
                            for (UnionCardSharingRatio sharingRatio : saveSharingRatioList) {
                                UnionMember tempMember = unionMemberService.getValidReadById(sharingRatio.getMemberId());
                                if (tempMember != null && MemberConstant.IS_UNION_OWNER_YES == tempMember.getIsUnionOwner()) {
                                    BigDecimal ratio = BigDecimalUtil.add(sharingRatio.getRatio(), surplusRatio);
                                    sharingRatio.setRatio(BigDecimalUtil.toDouble(ratio));
                                    break;
                                }
                            }
                        } else {
                            BigDecimal ratio = BigDecimalUtil.add(saveSharingRatioList.get(0).getRatio(), surplusRatio);
                            saveSharingRatioList.get(0).setRatio(BigDecimalUtil.toDouble(ratio));
                        }
                    }
                } else {
                    UnionCardSharingRatio saveSharingRatio = new UnionCardSharingRatio();
                    saveSharingRatio.setDelStatus(CommonConstant.DEL_STATUS_NO);
                    saveSharingRatio.setCreateTime(currentDate);
                    saveSharingRatio.setUnionId(unionId);
                    saveSharingRatio.setMemberId(unionMemberService.getValidOwnerByUnionId(unionId).getId());
                    saveSharingRatio.setActivityId(activityId);
                    saveSharingRatio.setRatio(1.0);
                    saveSharingRatioList.add(saveSharingRatio);
                }

                saveBatch(saveSharingRatioList);
            }
        } finally {
            rLock.unlock();
        }

    }

    //********************************************* Base On Business - other *******************************************

    @Override
    public boolean existValidByUnionIdAndActivityId(Integer unionId, Integer activityId) throws Exception {
        if (unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("activity_id", activityId);

        return unionCardSharingRatioDao.selectCount(entityWrapper) > 0;
    }

    @Override
    public boolean existInvalidMemberId(List<UnionCardSharingRatio> ratioList) throws Exception {
        if (ratioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        if (ListUtil.isNotEmpty(ratioList)) {
            for (UnionCardSharingRatio ratio : ratioList) {
                if (!unionMemberService.existValidReadById(ratio.getMemberId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean existUnionOwnerId(List<UnionCardSharingRatio> ratioList) throws Exception {
        if (ratioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        if (ListUtil.isNotEmpty(ratioList)) {
            for (UnionCardSharingRatio ratio : ratioList) {
                UnionMember member = unionMemberService.getValidReadById(ratio.getMemberId());
                if (member != null && MemberConstant.IS_UNION_OWNER_YES == member.getIsUnionOwner()) {
                    return true;
                }
            }
        }

        return false;
    }

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
    public List<UnionCardSharingRatio> filterInvalidMemberId(List<UnionCardSharingRatio> ratioList) throws Exception {
        if (ratioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardSharingRatio> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(ratioList)) {
            for (UnionCardSharingRatio ratio : ratioList) {
                if (unionMemberService.existValidReadById(ratio.getMemberId())) {
                    result.add(ratio);
                }
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

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionCardSharingRatio getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardSharingRatioDao.selectById(id);
    }

    @Override
    public UnionCardSharingRatio getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", id);

        return unionCardSharingRatioDao.selectOne(entityWrapper);
    }

    @Override
    public UnionCardSharingRatio getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("id", id);

        return unionCardSharingRatioDao.selectOne(entityWrapper);
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

        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_id", activityId);

        return unionCardSharingRatioDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRatio> listValidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("activity_id", activityId);

        return unionCardSharingRatioDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRatio> listInvalidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("activity_id", activityId);

        return unionCardSharingRatioDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRatio> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId);

        return unionCardSharingRatioDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRatio> listValidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("member_id", memberId);

        return unionCardSharingRatioDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRatio> listInvalidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("member_id", memberId);

        return unionCardSharingRatioDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRatio> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);

        return unionCardSharingRatioDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRatio> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);

        return unionCardSharingRatioDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRatio> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);

        return unionCardSharingRatioDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardSharingRatio> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardSharingRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", idList).eq(ListUtil.isEmpty(idList), "id", null);

        return unionCardSharingRatioDao.selectList(entityWrapper);
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionCardSharingRatio> entityWrapper) throws Exception {
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
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardSharingRatio> newUnionCardSharingRatioList) throws Exception {
        if (newUnionCardSharingRatioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardSharingRatioDao.insertBatch(newUnionCardSharingRatioList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

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

        unionCardSharingRatioDao.updateById(updateUnionCardSharingRatio);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardSharingRatio> updateUnionCardSharingRatioList) throws Exception {
        if (updateUnionCardSharingRatioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardSharingRatioDao.updateBatchById(updateUnionCardSharingRatioList);
    }

}