package com.gt.union.card.project.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.client.erp.ErpService;
import com.gt.union.card.activity.constant.ActivityConstant;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.activity.service.IUnionCardActivityService;
import com.gt.union.card.project.constant.ProjectConstant;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.entity.UnionCardProjectFlow;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.card.project.mapper.UnionCardProjectMapper;
import com.gt.union.card.project.service.IUnionCardProjectFlowService;
import com.gt.union.card.project.service.IUnionCardProjectItemService;
import com.gt.union.card.project.service.IUnionCardProjectService;
import com.gt.union.card.project.util.UnionCardProjectCacheUtil;
import com.gt.union.card.project.vo.CardProjectCheckUpdateVO;
import com.gt.union.card.project.vo.CardProjectCheckVO;
import com.gt.union.card.project.vo.CardProjectJoinMemberVO;
import com.gt.union.card.project.vo.CardProjectVO;
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
 * 项目 服务实现类
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
@Service
public class UnionCardProjectServiceImpl extends ServiceImpl<UnionCardProjectMapper, UnionCardProject> implements IUnionCardProjectService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionCardActivityService unionCardActivityService;

    @Autowired
    private IUnionCardProjectItemService unionCardProjectItemService;

    @Autowired
    private IUnionCardProjectFlowService unionCardProjectFlowService;

    @Autowired
    private ErpService erpService;

    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public UnionCardProject getByUnionIdAndMemberIdAndActivityId(Integer unionId, Integer memberId, Integer activityId) throws Exception {
        if (unionId == null || memberId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProject> result = listByActivityId(activityId);
        result = filterByMemberId(result, memberId);
        result = filterByUnionId(result, unionId);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    @Override
    public UnionCardProject getByIdAndUnionIdAndActivityId(Integer projectId, Integer unionId, Integer activityId) throws Exception {
        if (projectId == null || unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionCardProject result = getById(projectId);

        return result != null && activityId.equals(result.getActivityId()) && unionId.equals(result.getUnionId()) ? result : null;
    }

    @Override
    public CardProjectVO getProjectVOByBusIdAndIdAndUnionIdAndActivityId(Integer busId, Integer projectId, Integer unionId, Integer activityId) throws Exception {
        if (projectId == null || activityId == null || unionId == null || busId == null) {
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
        // （2）	判断activityId和projectId有效性
        UnionCardActivity activity = unionCardActivityService.getByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动信息");
        }
        UnionCardProject project = getByIdAndUnionIdAndActivityId(projectId, unionId, activityId);
        if (project == null) {
            throw new BusinessException("找不到活动项目信息");
        }
        // （3）	获取是否erp信息（调接口）
        CardProjectVO result = new CardProjectVO();
        result.setMember(member);
        result.setActivity(activity);
        result.setProject(project);
        Integer isErp = ListUtil.isNotEmpty(erpService.listErpByBusId(busId)) ? CommonConstant.COMMON_YES : CommonConstant.COMMON_NO;
        result.setIsErp(isErp);
        if (CommonConstant.COMMON_YES == isErp) {
            List<UnionCardProjectItem> erpTextList = unionCardProjectItemService.listByProjectIdAndType(projectId, ProjectConstant.TYPE_ERP_TEXT);
            result.setErpTextList(erpTextList);

            List<UnionCardProjectItem> erpGoodsList = unionCardProjectItemService.listByProjectIdAndType(projectId, ProjectConstant.TYPE_ERP_GOODS);
            result.setErpGoodsList(erpGoodsList);
        } else {
            List<UnionCardProjectItem> textList = unionCardProjectItemService.listByProjectIdAndType(projectId, ProjectConstant.TYPE_TEXT);
            result.setNonErpTextList(textList);
        }

        return result;
    }

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<UnionCardProject> listByUnionIdAndMemberIdAndStatus(Integer unionId, Integer memberId, Integer status) throws Exception {
        if (unionId == null || memberId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProject> result = listByMemberId(memberId);
        result = filterByUnionId(result, unionId);
        result = filterByStatus(result, status);

        return result;
    }

    @Override
    public List<UnionCardProject> listByUnionIdAndActivityId(Integer unionId, Integer activityId) throws Exception {
        if (unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProject> result = listByActivityId(activityId);
        result = filterByUnionId(result, unionId);

        return result;
    }

    @Override
    public List<UnionCardProject> listByUnionIdAndActivityIdAndStatus(Integer unionId, Integer activityId, Integer status) throws Exception {
        if (unionId == null || activityId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProject> result = listByActivityId(activityId);
        result = filterByUnionId(result, unionId);
        result = filterByStatus(result, status);

        return result;
    }

    @Override
    public List<CardProjectJoinMemberVO> listJoinMemberByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception {
        if (busId == null || unionId == null || activityId == null) {
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
        UnionCardActivity activity = unionCardActivityService.getByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动信息");
        }
        // （3）	要求已报名活动且项目已通过
        List<CardProjectJoinMemberVO> result = new ArrayList<>();
        List<UnionCardProject> projectList = listByUnionIdAndActivityIdAndStatus(unionId, activityId, ProjectConstant.STATUS_ACCEPT);
        if (ListUtil.isNotEmpty(projectList)) {
            for (UnionCardProject project : projectList) {
                CardProjectJoinMemberVO vo = new CardProjectJoinMemberVO();
                vo.setProject(project);

                UnionMember projectMember = unionMemberService.getReadByIdAndUnionId(project.getMemberId(), unionId);
                vo.setMember(projectMember);

                List<UnionCardProjectItem> itemList = unionCardProjectItemService.listByProjectId(project.getId());
                vo.setItemList(itemList);
                result.add(vo);
            }
        }
        // （4）	按时间顺序排序 
        Collections.sort(result, new Comparator<CardProjectJoinMemberVO>() {
            @Override
            public int compare(CardProjectJoinMemberVO o1, CardProjectJoinMemberVO o2) {
                return o2.getProject().getCreateTime().compareTo(o1.getProject().getCreateTime());
            }
        });

        return result;
    }

    @Override
    public List<CardProjectCheckVO> listProjectCheckByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception {
        if (busId == null || activityId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_READ_REJECT);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_NEED_OWNER);
        }
        // （2）	判断activityId有效性
        UnionCardActivity activity = unionCardActivityService.getByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动信息");
        }
        // （3）	要求活动在报名中状态
        Integer activityStatus = unionCardActivityService.getStatus(activity);
        if (ActivityConstant.STATUS_APPLYING != activityStatus) {
            throw new BusinessException("活动卡不在报名中状态");
        }
        // （4）	要求已报名活动且项目是审核中状态
        List<CardProjectCheckVO> result = new ArrayList<>();
        List<UnionCardProject> projectList = listByUnionIdAndActivityIdAndStatus(unionId, activityId, ProjectConstant.STATUS_COMMITTED);
        if (ListUtil.isNotEmpty(projectList)) {
            for (UnionCardProject project : projectList) {
                CardProjectCheckVO vo = new CardProjectCheckVO();
                vo.setProject(project);

                UnionMember projectMember = unionMemberService.getReadByIdAndUnionId(project.getMemberId(), unionId);
                vo.setMember(projectMember);

                List<UnionCardProjectItem> itemList = unionCardProjectItemService.listByProjectId(project.getId());
                vo.setItemList(itemList);
                result.add(vo);
            }
        }
        // （5）	按时间顺序排序
        Collections.sort(result, new Comparator<CardProjectCheckVO>() {
            @Override
            public int compare(CardProjectCheckVO o1, CardProjectCheckVO o2) {
                return o2.getProject().getCreateTime().compareTo(o1.getProject().getCreateTime());
            }
        });

        return result;
    }

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProjectCheckByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId, Integer isPass, CardProjectCheckUpdateVO vo) throws Exception {
        if (busId == null || unionId == null || activityId == null || isPass == null || vo == null) {
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
        // （3）	要求活动在报名中状态
        Integer activityStatus = unionCardActivityService.getStatus(activity);
        if (ActivityConstant.STATUS_APPLYING != activityStatus) {
            throw new BusinessException("活动卡不在报名中状态");
        }
        // （4）	不通过时要求理由不能为空
        if (CommonConstant.COMMON_YES != isPass && StringUtil.isEmpty(vo.getRejectReason())) {
            throw new BusinessException("不通过时理由不能为空");
        }
        // （5）	要求活动项目在审核中状态
        List<UnionCardProject> updateProjectList = new ArrayList<>();
        List<UnionCardProjectFlow> saveFlowList = new ArrayList<>();
        Date currentDate = DateUtil.getCurrentDate();
        if (ListUtil.isNotEmpty(vo.getProjectIdList())) {
            for (Integer projectId : vo.getProjectIdList()) {
                UnionCardProject project = getByIdAndUnionIdAndActivityId(projectId, unionId, activityId);
                if (project == null) {
                    throw new BusinessException("找不到要审核的项目信息");
                }
                if (ProjectConstant.STATUS_COMMITTED != project.getStatus()) {
                    throw new BusinessException("项目不在可审核状态");
                }

                UnionCardProject updateProject = new UnionCardProject();
                updateProject.setId(projectId);
                updateProject.setStatus(CommonConstant.COMMON_YES == isPass ? ProjectConstant.STATUS_ACCEPT : ProjectConstant.STATUS_REJECT);
                updateProjectList.add(updateProject);

                UnionCardProjectFlow saveFlow = new UnionCardProjectFlow();
                saveFlow.setCreateTime(currentDate);
                saveFlow.setDelStatus(CommonConstant.COMMON_NO);
                saveFlow.setProjectId(projectId);
                saveFlow.setIllustration(CommonConstant.COMMON_YES == isPass ? "通过" : vo.getRejectReason());
                saveFlowList.add(saveFlow);
            }
        }

        // 事务操作
        updateBatch(updateProjectList);
        unionCardProjectFlowService.saveBatch(saveFlowList);

    }

    //***************************************** Domain Driven Design - count *******************************************

    @Override
    public Integer countByUnionIdAndActivityIdAndStatus(Integer unionId, Integer activityId, Integer status) throws Exception {
        if (unionId == null || activityId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProject> projectList = listByUnionIdAndActivityIdAndStatus(unionId, activityId, status);

        return ListUtil.isNotEmpty(projectList) ? projectList.size() : 0;
    }

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    @Override
    public List<UnionCardProject> filterByMemberId(List<UnionCardProject> projectList, Integer memberId) throws Exception {
        if (projectList == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProject> result = new ArrayList<>();
        for (UnionCardProject project : projectList) {
            if (memberId.equals(project.getMemberId())) {
                result.add(project);
            }
        }

        return result;
    }

    @Override
    public List<UnionCardProject> filterByUnionId(List<UnionCardProject> projectList, Integer unionId) throws Exception {
        if (projectList == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProject> result = new ArrayList<>();
        for (UnionCardProject project : projectList) {
            if (unionId.equals(project.getUnionId())) {
                result.add(project);
            }
        }

        return result;
    }

    @Override
    public List<UnionCardProject> filterByStatus(List<UnionCardProject> projectList, Integer status) throws Exception {
        if (projectList == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProject> result = new ArrayList<>();
        for (UnionCardProject project : projectList) {
            if (status.equals(project.getStatus())) {
                result.add(project);
            }
        }

        return result;
    }

    //***************************************** Object As a Service - get **********************************************

    public UnionCardProject getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardProject result;
        // (1)cache
        String idKey = UnionCardProjectCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionCardProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionCardProject> listByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardProject> result;
        // (1)cache
        String activityIdKey = UnionCardProjectCacheUtil.getActivityIdKey(activityId);
        if (redisCacheUtil.exists(activityIdKey)) {
            String tempStr = redisCacheUtil.get(activityIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_id", activityId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, activityId, UnionCardProjectCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    public List<UnionCardProject> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardProject> result;
        // (1)cache
        String memberIdKey = UnionCardProjectCacheUtil.getMemberIdKey(memberId);
        if (redisCacheUtil.exists(memberIdKey)) {
            String tempStr = redisCacheUtil.get(memberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, memberId, UnionCardProjectCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    public List<UnionCardProject> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardProject> result;
        // (1)cache
        String unionIdKey = UnionCardProjectCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionCardProjectCacheUtil.TYPE_UNION_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardProject newUnionCardProject) throws Exception {
        if (newUnionCardProject == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionCardProject);
        removeCache(newUnionCardProject);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardProject> newUnionCardProjectList) throws Exception {
        if (newUnionCardProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionCardProjectList);
        removeCache(newUnionCardProjectList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionCardProject unionCardProject = getById(id);
        removeCache(unionCardProject);
        // (2)remove in db logically
        UnionCardProject removeUnionCardProject = new UnionCardProject();
        removeUnionCardProject.setId(id);
        removeUnionCardProject.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionCardProject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardProject> unionCardProjectList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProject unionCardProject = getById(id);
            unionCardProjectList.add(unionCardProject);
        }
        removeCache(unionCardProjectList);
        // (2)remove in db logically
        List<UnionCardProject> removeUnionCardProjectList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProject removeUnionCardProject = new UnionCardProject();
            removeUnionCardProject.setId(id);
            removeUnionCardProject.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardProjectList.add(removeUnionCardProject);
        }
        updateBatchById(removeUnionCardProjectList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardProject updateUnionCardProject) throws Exception {
        if (updateUnionCardProject == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCardProject.getId();
        UnionCardProject unionCardProject = getById(id);
        removeCache(unionCardProject);
        // (2)update db
        updateById(updateUnionCardProject);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardProject> updateUnionCardProjectList) throws Exception {
        if (updateUnionCardProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionCardProject updateUnionCardProject : updateUnionCardProjectList) {
            idList.add(updateUnionCardProject.getId());
        }
        List<UnionCardProject> unionCardProjectList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProject unionCardProject = getById(id);
            unionCardProjectList.add(unionCardProject);
        }
        removeCache(unionCardProjectList);
        // (2)update db
        updateBatchById(updateUnionCardProjectList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionCardProject newUnionCardProject, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardProjectCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCardProject);
    }

    private void setCache(List<UnionCardProject> newUnionCardProjectList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionCardProjectCacheUtil.TYPE_ACTIVITY_ID:
                foreignIdKey = UnionCardProjectCacheUtil.getActivityIdKey(foreignId);
                break;
            case UnionCardProjectCacheUtil.TYPE_MEMBER_ID:
                foreignIdKey = UnionCardProjectCacheUtil.getMemberIdKey(foreignId);
                break;
            case UnionCardProjectCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionCardProjectCacheUtil.getUnionIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionCardProjectList);
        }
    }

    private void removeCache(UnionCardProject unionCardProject) {
        if (unionCardProject == null) {
            return;
        }
        Integer id = unionCardProject.getId();
        String idKey = UnionCardProjectCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer activityId = unionCardProject.getActivityId();
        if (activityId != null) {
            String activityIdKey = UnionCardProjectCacheUtil.getActivityIdKey(activityId);
            redisCacheUtil.remove(activityIdKey);
        }

        Integer memberId = unionCardProject.getMemberId();
        if (memberId != null) {
            String memberIdKey = UnionCardProjectCacheUtil.getMemberIdKey(memberId);
            redisCacheUtil.remove(memberIdKey);
        }

        Integer unionId = unionCardProject.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionCardProjectCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }
    }

    private void removeCache(List<UnionCardProject> unionCardProjectList) {
        if (ListUtil.isEmpty(unionCardProjectList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCardProject unionCardProject : unionCardProjectList) {
            idList.add(unionCardProject.getId());
        }
        List<String> idKeyList = UnionCardProjectCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> activityIdKeyList = getForeignIdKeyList(unionCardProjectList, UnionCardProjectCacheUtil.TYPE_ACTIVITY_ID);
        if (ListUtil.isNotEmpty(activityIdKeyList)) {
            redisCacheUtil.remove(activityIdKeyList);
        }

        List<String> memberIdKeyList = getForeignIdKeyList(unionCardProjectList, UnionCardProjectCacheUtil.TYPE_MEMBER_ID);
        if (ListUtil.isNotEmpty(memberIdKeyList)) {
            redisCacheUtil.remove(memberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionCardProjectList, UnionCardProjectCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionCardProject> unionCardProjectList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionCardProjectCacheUtil.TYPE_ACTIVITY_ID:
                for (UnionCardProject unionCardProject : unionCardProjectList) {
                    Integer activityId = unionCardProject.getActivityId();
                    if (activityId != null) {
                        String activityIdKey = UnionCardProjectCacheUtil.getActivityIdKey(activityId);
                        result.add(activityIdKey);
                    }
                }
                break;
            case UnionCardProjectCacheUtil.TYPE_MEMBER_ID:
                for (UnionCardProject unionCardProject : unionCardProjectList) {
                    Integer memberId = unionCardProject.getMemberId();
                    if (memberId != null) {
                        String memberIdKey = UnionCardProjectCacheUtil.getMemberIdKey(memberId);
                        result.add(memberIdKey);
                    }
                }
                break;
            case UnionCardProjectCacheUtil.TYPE_UNION_ID:
                for (UnionCardProject unionCardProject : unionCardProjectList) {
                    Integer unionId = unionCardProject.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionCardProjectCacheUtil.getUnionIdKey(unionId);
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