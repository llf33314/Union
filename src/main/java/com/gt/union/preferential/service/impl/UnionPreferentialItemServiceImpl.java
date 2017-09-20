package com.gt.union.preferential.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.main.service.IUnionMainChargeService;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.preferential.constant.PreferentialConstant;
import com.gt.union.preferential.entity.UnionPreferentialItem;
import com.gt.union.preferential.entity.UnionPreferentialProject;
import com.gt.union.preferential.mapper.UnionPreferentialItemMapper;
import com.gt.union.preferential.service.IUnionPreferentialItemService;
import com.gt.union.preferential.service.IUnionPreferentialProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠服务项 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionPreferentialItemServiceImpl extends ServiceImpl<UnionPreferentialItemMapper, UnionPreferentialItem> implements IUnionPreferentialItemService {

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionPreferentialProjectService unionPreferentialProjectService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMainChargeService unionMainChargeService;


    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据优惠服务项id获取对象
     *
     * @param itemId {not null} 优惠服务项id
     * @return
     * @throws Exception
     */
    @Override
    public UnionPreferentialItem getById(Integer itemId) throws Exception {
        if (itemId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", itemId);
        return this.selectOne(entityWrapper);
    }

    //------------------------------------------ list(include page) ---------------------------------------------------

    /**
     * 查询我的优惠服务
     *
     * @param page
     * @param unionId 联盟id
     * @param busId   商家id
     * @return
     */
    @Override
    public Page listByUnionId(Page page, final Integer unionId, final Integer busId) throws Exception {
        UnionMember member = unionMemberService.getByBusIdAndUnionId(busId, unionId);
        final UnionPreferentialProject project = unionPreferentialProjectService.getByMemberId(member.getId());
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder("");
                sbSqlSegment.append(" WHERE project_id = ").append(project.getId())
                        .append(" ORDER BY id DESC ");
                return sbSqlSegment.toString();
            }

            ;

        };
        wrapper.setSqlSelect(" id, name, DATE_FORMAT(createtime, '%Y-%m-%d %T') createtime, verify_status verifyStatus ");
        return this.selectMapsPage(page, wrapper);
    }

    /**
     * 根据优惠项目id和优惠服务状态，获取优惠服务项列表信息
     *
     * @param projectId {not null} 优惠项目id
     * @param status    {not null} 优惠服务状态
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionPreferentialItem> listByProjectIdAndStatus(Integer projectId, Integer status) throws Exception {
        if (projectId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("project_id", projectId)
                .eq("status", status);
        return this.selectList(entityWrapper);
    }

    /**
     * 通过managerId和verifyStatus查询对应的优惠服务项信息
     *
     * @param projectId    项目id
     * @param verifyStatus 审核状态
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> listByProjectIdWidthStatus(Integer projectId, Integer verifyStatus) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (verifyStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("project_id", projectId)
                .eq("verify_status", verifyStatus);
        entityWrapper.setSqlSelect(" id, name, DATE_FORMAT(createtime, '%Y-%m-%d %T') createtime, verify_status verifyStatus ");
        return this.selectMaps(entityWrapper);
    }

    //------------------------------------------------- update --------------------------------------------------------

    /**
     * 根据优惠服务项id、商家id和盟员身份id，提交优惠服务项
     *
     * @param itemId   {not null} 优惠服务项id
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @throws Exception
     */
    @Override
    public void submitByIdAndBusIdAndMemberId(Integer itemId, Integer busId, Integer memberId) throws Exception {
        if (itemId == null || busId == null || memberId == null) {
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
        //(4)检查优惠服务项信息是否过期
        UnionPreferentialItem item = this.getById(itemId);
        if (item == null) {
            throw new BusinessException("优惠服务项不存在");
        }
        UnionPreferentialProject project = this.unionPreferentialProjectService.getById(item.getProjectId());
        if (project == null) {
            throw new BusinessException("找不到对应的优惠项目");
        }
        if (!project.getMemberId().equals(memberId)) {
            throw new BusinessException("没有该优惠服务项的权限");
        }
        if (item.getStatus() != PreferentialConstant.STATUS_UNCOMMITTED) {
            throw new BusinessException("优惠服务项已处理");
        }
        //(5)更新操作
        UnionPreferentialItem updateItem = new UnionPreferentialItem();
        updateItem.setId(item.getId()); //优惠服务项id
        updateItem.setStatus(PreferentialConstant.STATUS_VERIFYING); //优惠服务项状态变成审核中
        updateItem.setModifytime(DateUtil.getCurrentDate()); //修改时间
        this.updateById(updateItem);
    }

    /**
     * 根据优惠服务项id、商家id和盟员身份id，移除优惠服务项
     *
     * @param itemId   {not null} 优惠服务项id
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @throws Exception
     */
    @Override
    public void removeByIdAndBusIdAndMemberId(Integer itemId, Integer busId, Integer memberId) throws Exception {
        if (itemId == null || busId == null || memberId == null) {
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
        //(4)检查优惠服务项信息是否过期
        UnionPreferentialItem item = this.getById(itemId);
        if (item == null) {
            throw new BusinessException("优惠服务项不存在");
        }
        UnionPreferentialProject project = this.unionPreferentialProjectService.getById(item.getProjectId());
        if (project == null) {
            throw new BusinessException("找不到对应的优惠项目");
        }
        if (!project.getMemberId().equals(memberId)) {
            throw new BusinessException("没有该优惠服务项的权限");
        }
        //(5)更新操作
        UnionPreferentialItem updateItem = new UnionPreferentialItem();
        updateItem.setId(item.getId()); //优惠服务项id
        updateItem.setDelStatus(CommonConstant.DEL_STATUS_YES); //删除状态变成已删除
        updateItem.setModifytime(DateUtil.getCurrentDate()); //修改时间
        this.updateById(updateItem);
    }

    /**
     * 根据商家id、盟员身份id、批量操作的优惠服务项id和是否审核通过，批量审核优惠服务项
     *
     * @param busId      {not null} 商家id
     * @param memberId   {not null} 盟员身份id
     * @param itemIdList {not null} 优惠服务项id列表
     * @param isOK       {not null} 是否审核通过
     * @throws Exception
     */
    @Override
    public void updateBatchByBusIdAndMemberId(Integer busId, Integer memberId, List<Integer> itemIdList, Integer isOK) throws Exception {
        if (busId == null || memberId == null || itemIdList == null || isOK == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionOwner = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionOwner == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionOwner.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(unionOwner)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断盟主权限
        if (unionOwner.getIsUnionOwner() != MemberConstant.IS_UNION_OWNER_YES) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        //(5)检查优惠服务项信息是否过期
        Integer unionId = unionOwner.getUnionId();
        List<UnionPreferentialItem> updateItemList = new ArrayList<>();
        for (Integer itemId : itemIdList) {
            UnionPreferentialItem item = this.getById(itemId);
            if (item == null) {
                throw new BusinessException("存在无效的优惠服务项id");
            }
            UnionPreferentialProject project = this.unionPreferentialProjectService.getById(item.getProjectId());
            if (project == null) {
                throw new BusinessException("找不到对应的优惠项目");
            }
            UnionMember unionMember = this.unionMemberService.getById(project.getMemberId());
            if (unionMember == null) {
                throw new BusinessException("找不到优惠项目对应的盟员信息");
            }
            if (!unionId.equals(unionMember.getUnionId())) {
                throw new BusinessException("无法操作他人联盟的优惠服务项");
            }
            //(6)更新操作
            UnionPreferentialItem updateItem = new UnionPreferentialItem();
            updateItem.setId(itemId); //优惠服务项id
            updateItem.setStatus(isOK == CommonConstant.COMMON_YES ? PreferentialConstant.STATUS_PASS : PreferentialConstant.STATUS_FAIL); //审核状态
            updateItem.setModifytime(DateUtil.getCurrentDate()); //修改时间
            updateItemList.add(updateItem);
        }
        this.updateBatchById(updateItemList);
    }

    /**
     * 删除优惠服务项目
     *
     * @param unionId 联盟id
     * @param busId   商家id
     * @param ids     服务项目ids
     * @throws Exception
     */
    @Override
    public void delete(Integer unionId, Integer busId, String ids) throws Exception {

    }

    /**
     * 审核优惠服务项目
     *
     * @param unionId      联盟id
     * @param busId        商家id
     * @param ids          服务项目ids
     * @param verifyStatus 审核状态 2：通过 3：不通过
     */
    @Override
    public void verify(Integer unionId, Integer busId, String ids, Integer verifyStatus) throws Exception {

    }

    //------------------------------------------------- save ----------------------------------------------------------

    /**
     * 根据商家id、盟员身份id和新增的优惠服务名称，保存新增信息
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @param itemName {not null} 优惠服务名称
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveByBusIdAndMemberIdAndName(Integer busId, Integer memberId, String itemName) throws Exception {
        if (busId == null || memberId == null || itemName == null) {
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
        //(4)更新或新增操作
        UnionPreferentialProject project = this.unionPreferentialProjectService.getByMemberId(memberId);
        Integer projectId = null;
        if (project != null) {
            projectId = project.getId();
        } else {
            UnionPreferentialProject saveProject = new UnionPreferentialProject();
            saveProject.setCreatetime(DateUtil.getCurrentDate()); //创建时间
            saveProject.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
            saveProject.setMemberId(memberId); //盟员身份id
            saveProject.setIllustration(""); //优惠项目说明
            this.unionPreferentialProjectService.insert(saveProject);
            projectId = saveProject.getId();
        }
        UnionPreferentialItem saveItem = new UnionPreferentialItem();
        saveItem.setCreatetime(DateUtil.getCurrentDate()); //创建时间
        saveItem.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
        saveItem.setProjectId(projectId); //优惠项目id
        saveItem.setName(itemName); //优惠服务名称
        saveItem.setStatus(PreferentialConstant.STATUS_UNCOMMITTED); //状态
        saveItem.setModifytime(DateUtil.getCurrentDate()); //默认修改时间
        this.insert(saveItem);
    }

    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------
}
