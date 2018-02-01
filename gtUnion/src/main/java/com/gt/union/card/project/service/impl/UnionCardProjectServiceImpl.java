package com.gt.union.card.project.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.api.client.erp.ErpService;
import com.gt.union.card.activity.constant.ActivityConstant;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.activity.service.IUnionCardActivityService;
import com.gt.union.card.project.constant.ProjectConstant;
import com.gt.union.card.project.dao.IUnionCardProjectDao;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.entity.UnionCardProjectFlow;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.card.project.service.IUnionCardProjectFlowService;
import com.gt.union.card.project.service.IUnionCardProjectItemService;
import com.gt.union.card.project.service.IUnionCardProjectService;
import com.gt.union.card.project.vo.CardProjectCheckUpdateVO;
import com.gt.union.card.project.vo.CardProjectCheckVO;
import com.gt.union.card.project.vo.CardProjectJoinMemberVO;
import com.gt.union.card.project.vo.CardProjectVO;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
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
public class UnionCardProjectServiceImpl implements IUnionCardProjectService {
    @Autowired
    private IUnionCardProjectDao unionCardProjectDao;

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

    //********************************************* Base On Business - get *********************************************

    @Override
    public UnionCardProject getValidByUnionIdAndMemberIdAndActivityId(Integer unionId, Integer memberId, Integer activityId) throws Exception {
        if (unionId == null || memberId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("member_id", memberId)
                .eq("activity_id", activityId);

        return unionCardProjectDao.selectOne(entityWrapper);
    }

    @Override
    public UnionCardProject getValidByUnionIdAndMemberIdAndActivityIdAndStatus(Integer unionId, Integer memberId, Integer activityId, Integer status) throws Exception {
        if (unionId == null || memberId == null || activityId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("member_id", memberId)
                .eq("activity_id", activityId)
                .eq("status", status);

        return unionCardProjectDao.selectOne(entityWrapper);
    }

    @Override
    public UnionCardProject getValidByIdAndUnionIdAndActivityId(Integer projectId, Integer unionId, Integer activityId) throws Exception {
        if (projectId == null || unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", projectId)
                .eq("union_id", unionId)
                .eq("activity_id", activityId);

        return unionCardProjectDao.selectOne(entityWrapper);
    }

    @Override
    public CardProjectVO getProjectVOByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception {
        if (activityId == null || unionId == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // 判断union有效性
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        // 判断member读权限
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // 判断activityId有效性
        UnionCardActivity activity = unionCardActivityService.getValidByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动信息");
        }
        // 获取是否erp信息（调接口），并获取相应的服务内容
        CardProjectVO result = new CardProjectVO();
        result.setMember(member);
        result.setActivity(activity);
        result.setActivityStatus(unionCardActivityService.getStatus(activity));
        boolean isErp = erpService.userHasErpAuthority(busId);
        result.setIsErp(isErp ? CommonConstant.COMMON_YES : CommonConstant.COMMON_NO);
        UnionCardProject project = getValidByUnionIdAndMemberIdAndActivityId(unionId, member.getId(), activityId);
        if (project != null) {
            result.setProject(project);
            Integer projectId = project.getId();

            List<UnionCardProjectItem> erpTextList = unionCardProjectItemService.listValidByProjectIdAndType(projectId, ProjectConstant.TYPE_ERP_TEXT);
            result.setErpTextList(erpTextList);

            List<UnionCardProjectItem> erpGoodsList = unionCardProjectItemService.listValidByProjectIdAndType(projectId, ProjectConstant.TYPE_ERP_GOODS);
            result.setErpGoodsList(erpGoodsList);

            List<UnionCardProjectItem> textList = unionCardProjectItemService.listValidByProjectIdAndType(projectId, ProjectConstant.TYPE_TEXT);
            result.setNonErpTextList(textList);
        } else {
            result.setErpTextList(new ArrayList<UnionCardProjectItem>());

            result.setErpGoodsList(new ArrayList<UnionCardProjectItem>());

            result.setNonErpTextList(new ArrayList<UnionCardProjectItem>());
        }

        return result;
    }

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<UnionCardProject> listValidByUnionIdAndMemberIdAndStatus(Integer unionId, Integer memberId, Integer status) throws Exception {
        if (unionId == null || memberId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("member_id", memberId)
                .eq("status", status);

        return unionCardProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProject> listValidByUnionIdAndActivityId(Integer unionId, Integer activityId) throws Exception {
        if (unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("activity_id", activityId);

        return unionCardProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProject> listValidByUnionIdAndActivityIdAndStatus(Integer unionId, Integer activityId, Integer status) throws Exception {
        if (unionId == null || activityId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("activity_id", activityId)
                .eq("status", status);

        return unionCardProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProject> listValidWithoutExpiredMemberByUnionIdAndActivityIdAndStatus(Integer unionId, Integer activityId, Integer status) throws Exception {
        if (unionId == null || activityId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("activity_id", activityId)
                .eq("status", status)
                .exists(" SELECT m.id FROM t_union_member m" +
                        " WHERE m.id=t_union_card_project.member_id" +
                        " AND m.del_status=" + CommonConstant.DEL_STATUS_NO);

        return unionCardProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProject> listValidByUnionIdAndActivityIdAndStatus(Integer unionId, Integer activityId, Integer status, String orderBy, boolean isAsc) throws Exception {
        if (unionId == null || activityId == null || status == null || orderBy == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("activity_id", activityId)
                .eq("status", status)
                .orderBy(orderBy, isAsc);

        return unionCardProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<CardProjectJoinMemberVO> listJoinMemberByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception {
        if (busId == null || unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // 判断union有效性
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        // 判断member读权限
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // 判断activityId有效性
        UnionCardActivity activity = unionCardActivityService.getValidByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动信息");
        }
        // 要求报名项目已审核通过
        List<CardProjectJoinMemberVO> result = new ArrayList<>();
        List<UnionCardProject> projectList = listValidByUnionIdAndActivityIdAndStatus(unionId, activityId, ProjectConstant.STATUS_ACCEPT);
        if (ListUtil.isNotEmpty(projectList)) {
            for (UnionCardProject project : projectList) {
                CardProjectJoinMemberVO vo = new CardProjectJoinMemberVO();
                vo.setProject(project);

                UnionMember projectMember = unionMemberService.getValidReadByIdAndUnionId(project.getMemberId(), unionId);
                if (projectMember == null) {
                    continue;
                }
                vo.setMember(projectMember);

                List<UnionCardProjectItem> itemList = unionCardProjectItemService.listValidByProjectId(project.getId());
                vo.setItemList(itemList);

                result.add(vo);
            }
        }
        // 按时间顺序排序 
        Collections.sort(result, new Comparator<CardProjectJoinMemberVO>() {
            @Override
            public int compare(CardProjectJoinMemberVO o1, CardProjectJoinMemberVO o2) {
                return o1.getProject().getCreateTime().compareTo(o2.getProject().getCreateTime());
            }
        });

        return result;
    }

    @Override
    public List<CardProjectCheckVO> listProjectCheckByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception {
        if (busId == null || activityId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // 判断union有效性
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        // 判断member读权限
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // 判断盟主权限
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_OWNER_ERROR);
        }
        // 判断activityId有效性
        UnionCardActivity activity = unionCardActivityService.getValidByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动信息");
        }
        // 要求活动在报名中状态
        Integer activityStatus = unionCardActivityService.getStatus(activity);
        if (ActivityConstant.STATUS_APPLYING != activityStatus && ActivityConstant.STATUS_BEFORE_SELL != activityStatus) {
            throw new BusinessException("活动卡不在可审核状态");
        }
        // 要求报名项目是审核中状态
        List<CardProjectCheckVO> result = new ArrayList<>();
        List<UnionCardProject> projectList = listValidByUnionIdAndActivityIdAndStatus(unionId, activityId, ProjectConstant.STATUS_COMMITTED);
        if (ListUtil.isNotEmpty(projectList)) {
            for (UnionCardProject project : projectList) {
                CardProjectCheckVO vo = new CardProjectCheckVO();
                vo.setProject(project);

                UnionMember projectMember = unionMemberService.getValidReadByIdAndUnionId(project.getMemberId(), unionId);
                if (projectMember == null) {
                    continue;
                }
                vo.setMember(projectMember);

                List<UnionCardProjectItem> itemList = unionCardProjectItemService.listValidByProjectId(project.getId());
                vo.setItemList(itemList);

                result.add(vo);
            }
        }
        // 按时间顺序排序
        Collections.sort(result, new Comparator<CardProjectCheckVO>() {
            @Override
            public int compare(CardProjectCheckVO o1, CardProjectCheckVO o2) {
                return o1.getProject().getCreateTime().compareTo(o2.getProject().getCreateTime());
            }
        });

        return result;
    }

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId, Integer isPass, CardProjectCheckUpdateVO vo) throws Exception {
        if (busId == null || unionId == null || activityId == null || isPass == null || vo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // 判断union有效性
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        // 判断member读权限
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // 判断盟主权限
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_OWNER_ERROR);
        }
        // 判断activityId有效性
        UnionCardActivity activity = unionCardActivityService.getValidByIdAndUnionId(activityId, unionId);
        if (activity == null) {
            throw new BusinessException("找不到活动信息");
        }
        // 要求活动在报名中状态
        Integer activityStatus = unionCardActivityService.getStatus(activity);
        if (ActivityConstant.STATUS_APPLYING != activityStatus && ActivityConstant.STATUS_BEFORE_SELL != activityStatus) {
            throw new BusinessException("活动卡状态不在报名中或售卡前");
        }
        // 不通过时要求理由不能为空
        if (CommonConstant.COMMON_YES != isPass && StringUtil.isEmpty(vo.getRejectReason())) {
            throw new BusinessException("不通过时理由不能为空");
        }
        // 要求报名项目在审核中状态
        List<UnionCardProject> updateProjectList = new ArrayList<>();
        List<UnionCardProjectFlow> saveFlowList = new ArrayList<>();
        Date currentDate = DateUtil.getCurrentDate();
        if (ListUtil.isNotEmpty(vo.getProjectIdList())) {
            for (Integer projectId : vo.getProjectIdList()) {
                UnionCardProject project = getValidByIdAndUnionIdAndActivityId(projectId, unionId, activityId);
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
                saveFlow.setIllustration(CommonConstant.COMMON_YES == isPass ? "提交审核通过" : "提交审核不通过：" + vo.getRejectReason());
                saveFlowList.add(saveFlow);
            }
        }

        // 事务操作
        if (ListUtil.isNotEmpty(updateProjectList)) {
            updateBatch(updateProjectList);
        }
        if (ListUtil.isNotEmpty(saveFlowList)) {
            unionCardProjectFlowService.saveBatch(saveFlowList);
        }

    }

    //********************************************* Base On Business - other *******************************************

    @Override
    public Integer countValidByUnionIdAndActivityIdAndStatus(Integer unionId, Integer activityId, Integer status) throws Exception {
        if (unionId == null || activityId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("activity_id", activityId)
                .eq("status", status);

        return unionCardProjectDao.selectCount(entityWrapper);
    }

    @Override
    public boolean existValidByUnionIdAndMemberIdAndActivityIdAndStatus(Integer unionId, Integer memberId, Integer activityId, Integer status) throws Exception {
        if (unionId == null || memberId == null || activityId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("member_id", memberId)
                .eq("activity_id", activityId)
                .eq("status", status);

        return unionCardProjectDao.selectCount(entityWrapper) > 0;
    }

    @Override
    public boolean existUnionOwnerId(List<UnionCardProject> projectList) throws Exception {
        if (projectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        if (ListUtil.isNotEmpty(projectList)) {
            for (UnionCardProject project : projectList) {
                UnionMember member = unionMemberService.getValidReadById(project.getMemberId());
                if (member != null && MemberConstant.IS_UNION_OWNER_YES == member.getIsUnionOwner()) {
                    return true;
                }
            }
        }

        return false;
    }

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionCardProject> filterByDelStatus(List<UnionCardProject> unionCardProjectList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProject> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardProjectList)) {
            for (UnionCardProject unionCardProject : unionCardProjectList) {
                if (delStatus.equals(unionCardProject.getDelStatus())) {
                    result.add(unionCardProject);
                }
            }
        }

        return result;
    }

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
    public List<UnionCardProject> filterInvalidMemberId(List<UnionCardProject> projectList) throws Exception {
        if (projectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProject> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(projectList)) {
            for (UnionCardProject project : projectList) {
                if (unionMemberService.existValidReadById(project.getMemberId())) {
                    result.add(project);
                }
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

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionCardProject getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardProjectDao.selectById(id);
    }

    @Override
    public UnionCardProject getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", id);

        return unionCardProjectDao.selectOne(entityWrapper);
    }

    @Override
    public UnionCardProject getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("id", id);

        return unionCardProjectDao.selectOne(entityWrapper);
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionCardProject> unionCardProjectList) throws Exception {
        if (unionCardProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardProjectList)) {
            for (UnionCardProject unionCardProject : unionCardProjectList) {
                result.add(unionCardProject.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionCardProject> listByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_id", activityId);

        return unionCardProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProject> listValidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("activity_id", activityId);

        return unionCardProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProject> listInvalidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("activity_id", activityId);

        return unionCardProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProject> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId);

        return unionCardProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProject> listValidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("member_id", memberId);

        return unionCardProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProject> listInvalidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("member_id", memberId);

        return unionCardProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProject> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);

        return unionCardProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProject> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);

        return unionCardProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProject> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);

        return unionCardProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProject> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", idList).eq(ListUtil.isEmpty(idList), "id", null);

        return unionCardProjectDao.selectList(entityWrapper);
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionCardProject> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardProjectDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardProject newUnionCardProject) throws Exception {
        if (newUnionCardProject == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardProjectDao.insert(newUnionCardProject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardProject> newUnionCardProjectList) throws Exception {
        if (newUnionCardProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardProjectDao.insertBatch(newUnionCardProjectList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionCardProject removeUnionCardProject = new UnionCardProject();
        removeUnionCardProject.setId(id);
        removeUnionCardProject.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionCardProjectDao.updateById(removeUnionCardProject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProject> removeUnionCardProjectList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProject removeUnionCardProject = new UnionCardProject();
            removeUnionCardProject.setId(id);
            removeUnionCardProject.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardProjectList.add(removeUnionCardProject);
        }
        unionCardProjectDao.updateBatchById(removeUnionCardProjectList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardProject updateUnionCardProject) throws Exception {
        if (updateUnionCardProject == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardProjectDao.updateById(updateUnionCardProject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardProject> updateUnionCardProjectList) throws Exception {
        if (updateUnionCardProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardProjectDao.updateBatchById(updateUnionCardProjectList);
    }

}