package com.gt.union.preferential.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
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

    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据盟员id获取优惠项目
     *
     * @param id
     * @return
     */
    @Override
    public UnionPreferentialProject getByMemberId(Integer id) {
        EntityWrapper entityWrapper = new EntityWrapper<UnionPreferentialProject>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        entityWrapper.eq("member_id", id);
        UnionPreferentialProject project = this.selectOne(entityWrapper);
        return project;
    }

    /**
     * 根据优惠项目id获取对象
     *
     * @param projectId {not null} 优惠项目id
     * @return
     * @throws Exception
     */
    @Override
    public UnionPreferentialProject getById(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", projectId);
        return this.selectOne(entityWrapper);
    }

    /**
     * 根据商家id、盟员身份id、优惠项目id和优惠服务状态，获取详情信息
     *
     * @param busId      {not null} 商家id
     * @param memberId   {not null} 盟员身份id
     * @param projectId  {not null} 优惠项目id
     * @param itemStatus {not null} 优惠服务状态
     * @return
     * @throws Exception
     */
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
        this.unionMainService.checkUnionMainValid(unionMember.getUnionId());
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

    /**
     * 根据商家id和盟员身份id，获取优惠项目信息
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return
     * @throws Exception
     */
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
        this.unionMainService.checkUnionMainValid(unionMember.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)查询操作
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("member_id", memberId);
        return this.selectOne(entityWrapper);
    }

    /**
     * 根据商家id和盟员身份id，获取优惠项目信息
     *
     * @param page     {not null} 分页对象
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return
     * @throws Exception
     */
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
        this.unionMainService.checkUnionMainValid(unionMember.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)查询优惠项目
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("member_id", memberId);
        UnionPreferentialProject project = this.selectOne(entityWrapper);
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

    //------------------------------------------ list(include page) ---------------------------------------------------

    /**
     * 根据商家id、盟员身份id和优惠服务项状态，分页查询优惠项目列表信息
     *
     * @param page       {not null} 分页对象
     * @param busId      {not null} 商家id
     * @param memberId   {not null} 盟员身份id
     * @param itemStatus {not null} 优惠服务项状态
     * @return
     * @throws Exception
     */
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
        this.unionMainService.checkUnionMainValid(unionOwner.getUnionId());
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

    /**
     * 获取所有过期的优惠项目列表信息，即项目所属盟员已退盟
     *
     * @return
     * @throws Exception
     */
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

    //------------------------------------------------- update --------------------------------------------------------

    /**
     * 根据优惠项目id、商家id和盟员身份id，更新优惠项目说明
     *
     * @param projectId    {not null} 优惠项目id
     * @param busId        {not null} 商家id
     * @param memberId     {not null} 盟员身份id
     * @param illustration {not null} 优惠项目说明
     * @throws Exception
     */
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
        this.unionMainService.checkUnionMainValid(unionMember.getUnionId());
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
        this.updateById(updateProject);
    }

    //------------------------------------------------- save ----------------------------------------------------------
    //------------------------------------------------- count ---------------------------------------------------------

    /**
     * 根据商家id、盟员身份id和优惠服务项状态，统计优惠服务数
     *
     * @param busId      {not null} 商家id
     * @param memberId   {not null} 盟员身份id
     * @param itemStatus {not null} 优惠服务状态
     * @return
     * @throws Exception
     */
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
        this.unionMainService.checkUnionMainValid(unionOwner.getUnionId());
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

    //------------------------------------------------ boolean --------------------------------------------------------
}
