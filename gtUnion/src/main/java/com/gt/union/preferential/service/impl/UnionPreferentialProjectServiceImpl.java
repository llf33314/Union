package com.gt.union.preferential.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.RedisKeyUtil;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.preferential.constant.PreferentialConstant;
import com.gt.union.preferential.entity.UnionPreferentialItem;
import com.gt.union.preferential.entity.UnionPreferentialProject;
import com.gt.union.preferential.mapper.UnionPreferentialProjectMapper;
import com.gt.union.preferential.service.IUnionPreferentialItemService;
import com.gt.union.preferential.service.IUnionPreferentialProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠项目 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionPreferentialProjectServiceImpl extends ServiceImpl<UnionPreferentialProjectMapper, UnionPreferentialProject> implements IUnionPreferentialProjectService {
    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionPreferentialItemService unionPreferentialItemService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - get *********************************************
     ******************************************************************************************************************/

    @Override
    public UnionPreferentialProject getByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionPreferentialProject> projectList = this.listByMemberId(memberId);
        if (ListUtil.isNotEmpty(projectList)) {
            return projectList.get(0);
        }
        return null;
    }

    @Override
    public Map<String, Object> getDetailByBusIdAndMemberIdAndProjectIdAndItemStatus(Integer busId, Integer memberId
            , Integer projectId, Integer itemStatus) throws Exception {
        if (busId == null || memberId == null || projectId == null || itemStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionMember = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(unionMember.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)检查优惠项目参数，并查询
        UnionPreferentialProject project = this.getById(projectId);
        if (project == null) {
            throw new BusinessException("优惠项目不存在");
        }
        List<UnionPreferentialItem> itemList = this.unionPreferentialItemService.listByProjectIdAndStatus(projectId, itemStatus);
        Map<String, Object> result = new HashMap<>();
        result.put("project", project);
        result.put("itemList", itemList);
        result.put("enterpriseName", unionMember.getEnterpriseName());
        return result;
    }

    @Override
    public UnionPreferentialProject getByBusIdAndMemberId(Integer busId, Integer memberId) throws Exception {
        if (busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionMember = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(unionMember.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)查询操作
        return this.getByMemberId(memberId);
    }

    @Override
    public Map<String, Object> getPageMapByBusIdAndMemberId(Page page, Integer busId, Integer memberId) throws Exception {
        if (page == null || busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Map<String, Object> result = new HashMap<>();
        //(1)判断是否具有盟员权限
        UnionMember unionMember = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(unionMember.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)查询优惠项目
        UnionPreferentialProject project = this.getByMemberId(memberId);
        result.put("project", project);
        //(5)查询优惠服务
        if (project == null) {
            result.put("pageItem", page);
        } else {
            Page pageItem = this.unionPreferentialItemService.pageByProjectId(page, project.getId());
            result.put("pageItem", pageItem);
        }
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - list ********************************************
     ******************************************************************************************************************/

    @Override
    public Page pageMapByBusIdAndMemberIdAndItemStatus(Page page, Integer busId, Integer memberId, final Integer itemStatus) throws Exception {
        if (page == null || busId == null || memberId == null || itemStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        final UnionMember unionOwner = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionOwner == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(unionOwner.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(unionOwner)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)判断盟主权限
        if (!unionOwner.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException("非盟主身份无法操作");
        }
        if (itemStatus != PreferentialConstant.STATUS_UNCOMMITTED) {
            //(5-1)审核中、审核通过、审核不通过
            Wrapper wrapper = new Wrapper() {
                @Override
                public String getSqlSegment() {
                    StringBuilder sbSqlSegment = new StringBuilder(" pp")
                            .append(" LEFT JOIN t_union_member m ON m.id = pp.member_id")
                            .append(" WHERE pp.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                            .append("  AND m.union_id = ").append(unionOwner.getUnionId())
                            .append("  AND exists(")
                            .append("    SELECT pi.id FROM t_union_preferential_item pi")
                            .append("    WHERE pi.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                            .append("      AND pi.status = ").append(itemStatus)
                            .append("  )")
                            .append(" ORDER BY pp.id ASC");
                    return sbSqlSegment.toString();
                }
            };
            StringBuilder sbSqlSelect = new StringBuilder(" pp.id projectId") //优惠项目id
                    .append(", pp.illustration projectIllustration") //优惠项目说明
                    .append(", m.enterprise_name enterpriseName") //优惠项目所属盟员的名称
                    .append(", m.id memberId"); //优惠项目所属盟员身份id
            wrapper.setSqlSelect(sbSqlSelect.toString());
            return this.selectMapsPage(page, wrapper);
        } else {
            //(5-2)未提交
            return this.unionMemberService.pagePreferentialUnCommitByUnionOwner(page, unionOwner);
        }
    }

    @Override
    public List<UnionPreferentialProject> listExpired() throws Exception {
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .notExists(new StringBuilder(" SELECT m.id FROM t_union_member m")
                        .append(" WHERE m.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND m.status != ").append(MemberConstant.STATUS_APPLY_IN)
                        .append("  AND m.id = t_union_preferential_project.member_id")
                        .toString());
        return this.selectList(entityWrapper);
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - save ********************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - remove ******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - update ******************************************
     ******************************************************************************************************************/

    @Override
    public void updateIllustrationByIdAndBusIdAndMemberId(Integer projectId, Integer busId, Integer memberId, String illustration) throws Exception {
        if (projectId == null || busId == null || memberId == null || illustration == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionMember = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(unionMember.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断操作者是否是这个优惠项目的拥有者
        UnionPreferentialProject unionPreferentialProject = this.getById(projectId);
        if (!unionPreferentialProject.getMemberId().equals(memberId)) {
            throw new BusinessException("不是该优惠项目的拥有者，无法操作");
        }
        //(5)更新信息
        UnionPreferentialProject updateProject = new UnionPreferentialProject();
        updateProject.setId(projectId); //优惠项目id
        updateProject.setIllustration(illustration); //优惠项目说明
        updateProject.setModifytime(DateUtil.getCurrentDate()); //优惠项目更新时间
        this.update(updateProject);
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - count *******************************************
     ******************************************************************************************************************/

    @Override
    public Integer countByBusInAndMemberIdAndItemStatus(Integer busId, Integer memberId, final Integer itemStatus) throws Exception {
        if (busId == null || memberId == null || itemStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        final UnionMember unionOwner = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionOwner == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(unionOwner.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(unionOwner)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)判断盟主权限
        if (!unionOwner.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        if (itemStatus != PreferentialConstant.STATUS_UNCOMMITTED) {
            //(5-1)审核中、审核通过、审核不通过
            Wrapper wrapper = new Wrapper() {
                @Override
                public String getSqlSegment() {
                    StringBuilder sbSqlSegment = new StringBuilder(" pp")
                            .append(" WHERE pp.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                            .append(" AND exists(")
                            .append("  SELECT pi.id FROM t_union_preferential_item pi")
                            .append("  WHERE pi.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                            .append("    AND pi.status = ").append(itemStatus)
                            .append("    AND pi.project_id = pp.id")
                            .append(" )")
                            .append(" AND exists(")
                            .append("  SELECT m.id FROM t_union_member m")
                            .append("  WHERE m.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                            .append("    AND m.id = pp.member_id")
                            .append("    AND m.union_id = ").append(unionOwner.getUnionId())
                            .append(" )");
                    return sbSqlSegment.toString();
                }
            };
            StringBuilder sbSqlSelect = new StringBuilder(" pp.id projectId"); //优惠项目id
            wrapper.setSqlSelect(sbSqlSelect.toString());
            return this.selectCount(wrapper);
        } else {
            //(5-2) 未提交
            return this.unionMemberService.countPreferentialUnCommitByUnionOwner(unionOwner);
        }
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - boolean *****************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Object As a Service - get **********************************************
     ******************************************************************************************************************/

    @Override
    public UnionPreferentialProject getById(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionPreferentialProject result;
        //(1)cache
        String projectIdKey = RedisKeyUtil.getProjectIdKey(projectId);
        if (this.redisCacheUtil.exists(projectIdKey)) {
            String tempStr = this.redisCacheUtil.get(projectIdKey);
            result = JSONArray.parseObject(tempStr, UnionPreferentialProject.class);
            return result;
        }
        //(2)db
        EntityWrapper<UnionPreferentialProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", projectId);
        result = this.selectOne(entityWrapper);
        setCache(result, projectId);
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - list *********************************************
     ******************************************************************************************************************/

    @Override
    public List<UnionPreferentialProject> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionPreferentialProject> result;
        //(1)get in cache
        String memberIdKey = RedisKeyUtil.getProjectMemberIdKey(memberId);
        if (this.redisCacheUtil.exists(memberIdKey)) {
            String tempStr = this.redisCacheUtil.get(memberIdKey);
            result = JSONArray.parseArray(tempStr, UnionPreferentialProject.class);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionPreferentialProject> entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("member_id", memberId);
        result = this.selectList(entityWrapper);
        setCache(result, memberId, PreferentialConstant.REDIS_KEY_PROJECT_MEMBER_ID);
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - save *********************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void save(UnionPreferentialProject newProject) throws Exception {
        if (newProject == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insert(newProject);
        this.removeCache(newProject);
    }

    @Override
    @Transactional
    public void saveBatch(List<UnionPreferentialProject> newProjectList) throws Exception {
        if (newProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insertBatch(newProjectList);
        this.removeCache(newProjectList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - remove *******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void removeById(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        UnionPreferentialProject project = this.getById(projectId);
        removeCache(project);
        //(2)remove in db logically
        UnionPreferentialProject removeOpportunity = new UnionPreferentialProject();
        removeOpportunity.setId(projectId);
        removeOpportunity.setDelStatus(CommonConstant.DEL_STATUS_YES);
        this.updateById(removeOpportunity);
    }

    @Override
    @Transactional
    public void removeBatchById(List<Integer> projectIdList) throws Exception {
        if (projectIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<UnionPreferentialProject> projectList = new ArrayList<>();
        for (Integer projectId : projectIdList) {
            UnionPreferentialProject project = this.getById(projectId);
            projectList.add(project);
        }
        removeCache(projectList);
        //(2)remove in db logically
        List<UnionPreferentialProject> removeProjectList = new ArrayList<>();
        for (Integer projectId : projectIdList) {
            UnionPreferentialProject removeProject = new UnionPreferentialProject();
            removeProject.setId(projectId);
            removeProject.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeProjectList.add(removeProject);
        }
        this.updateBatchById(removeProjectList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - update *******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void update(UnionPreferentialProject updateProject) throws Exception {
        if (updateProject == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        Integer projectId = updateProject.getId();
        UnionPreferentialProject project = this.getById(projectId);
        removeCache(project);
        //(2)update db
        this.updateById(updateProject);
    }

    @Override
    @Transactional
    public void updateBatch(List<UnionPreferentialProject> updateProjectList) throws Exception {
        if (updateProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<Integer> projectIdList = new ArrayList<>();
        for (UnionPreferentialProject updateProject : updateProjectList) {
            projectIdList.add(updateProject.getId());
        }
        List<UnionPreferentialProject> projectList = new ArrayList<>();
        for (Integer projectId : projectIdList) {
            UnionPreferentialProject project = this.getById(projectId);
            projectList.add(project);
        }
        removeCache(projectList);
        //(2)update db
        this.updateBatchById(updateProjectList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - cache support ************************************
     ******************************************************************************************************************/

    private void setCache(UnionPreferentialProject newProject, Integer projectId) {
        if (projectId == null) {
            return; //do nothing,just in case
        }
        String projectIdKey = RedisKeyUtil.getProjectIdKey(projectId);
        this.redisCacheUtil.set(projectIdKey, newProject);
    }

    private void setCache(List<UnionPreferentialProject> newProjectList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            return; //do nothing,just in case
        }
        String foreignIdKey;
        switch (foreignIdType) {
            case PreferentialConstant.REDIS_KEY_PROJECT_MEMBER_ID:
                foreignIdKey = RedisKeyUtil.getProjectMemberIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newProjectList);
                break;
            default:
                break;
        }
    }

    private void removeCache(UnionPreferentialProject project) {
        if (project == null) {
            return;
        }
        Integer projectId = project.getId();
        String projectIdKey = RedisKeyUtil.getProjectIdKey(projectId);
        this.redisCacheUtil.remove(projectIdKey);
        Integer memberId = project.getMemberId();
        if (memberId != null) {
            String memberIdKey = RedisKeyUtil.getProjectMemberIdKey(memberId);
            this.redisCacheUtil.remove(memberIdKey);
        }
    }

    private void removeCache(List<UnionPreferentialProject> projectList) {
        if (ListUtil.isEmpty(projectList)) {
            return;
        }
        List<Integer> projectIdList = new ArrayList<>();
        for (UnionPreferentialProject project : projectList) {
            projectIdList.add(project.getId());
        }
        List<String> projectIdKeyList = RedisKeyUtil.getProjectIdKey(projectIdList);
        this.redisCacheUtil.remove(projectIdKeyList);
        List<String> memberIdKeyList = getForeignIdKeyList(projectList, PreferentialConstant.REDIS_KEY_PROJECT_MEMBER_ID);
        if (ListUtil.isNotEmpty(memberIdKeyList)) {
            this.redisCacheUtil.remove(memberIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionPreferentialProject> projectList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case PreferentialConstant.REDIS_KEY_PROJECT_MEMBER_ID:
                for (UnionPreferentialProject project : projectList) {
                    Integer memberId = project.getMemberId();
                    if (memberId != null) {
                        String memberIdKey = RedisKeyUtil.getProjectMemberIdKey(memberId);
                        result.add(memberIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}
