package com.gt.union.preferential.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.RedisKeyUtil;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    private RedisCacheUtil redisCacheUtil;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - get *********************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - list ********************************************
     ******************************************************************************************************************/

    @Override
    public Page pageByProjectId(Page page, Integer projectId) throws Exception {
        if (page == null || projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("project_id", projectId)
                .orderBy("status ASC, id", true);
        return this.selectPage(page, entityWrapper);
    }

    @Override
    public List<UnionPreferentialItem> listByProjectIdAndStatus(Integer projectId, Integer status) throws Exception {
        if (projectId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionPreferentialItem> result = new ArrayList<>();
        List<UnionPreferentialItem> itemList = this.listByProjectId(projectId);
        if (ListUtil.isNotEmpty(itemList)) {
            for (UnionPreferentialItem item : itemList) {
                if (status.equals(item.getStatus())) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    @Override
    public List<UnionPreferentialItem> listExpired() throws Exception {
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .notExists(new StringBuilder(" SELECT pp.id FROM t_union_preferential_item pp")
                        .append(" WHERE pp.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND pp.id = t_union_preferential_item.item_id")
                        .toString());
        return this.selectList(entityWrapper);
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - save ********************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
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
        this.unionMainService.checkUnionValid(unionMember.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断优惠服务数
        UnionPreferentialProject project = this.unionPreferentialProjectService.getByMemberId(memberId);
        if (project != null) {
            List<UnionPreferentialItem> itemList = this.listByProjectId(project.getId());
            if (itemList != null && itemList.size() >= ConfigConstant.MAX_PREFERENIAL_COUNT) {
                throw new BusinessException("优惠项目已达上限");
            }
        }
        //(5)更新或新增操作
        Integer projectId;
        if (project != null) {
            projectId = project.getId();
        } else {
            UnionPreferentialProject saveProject = new UnionPreferentialProject();
            saveProject.setCreatetime(DateUtil.getCurrentDate()); //创建时间
            saveProject.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
            saveProject.setMemberId(memberId); //盟员身份id
            saveProject.setIllustration(""); //优惠项目说明
            this.unionPreferentialProjectService.save(saveProject);
            projectId = saveProject.getId();
        }
        UnionPreferentialItem saveItem = new UnionPreferentialItem();
        saveItem.setCreatetime(DateUtil.getCurrentDate()); //创建时间
        saveItem.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
        saveItem.setProjectId(projectId); //优惠项目id
        saveItem.setName(itemName); //优惠服务名称
        saveItem.setStatus(PreferentialConstant.STATUS_UNCOMMITTED); //状态
        saveItem.setModifytime(DateUtil.getCurrentDate()); //默认修改时间
        this.save(saveItem);
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - remove ******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - update ******************************************
     ******************************************************************************************************************/

    @Override
    public void submitBatchByIdsAndBusIdAndMemberId(List<Integer> itemIdList, Integer busId, Integer memberId) throws Exception {
        if (itemIdList == null || busId == null || memberId == null) {
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
        //(4)检查优惠服务项信息是否过期
        List<UnionPreferentialItem> updateItemList = new ArrayList<>();
        for (Integer itemId : itemIdList) {
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
            if (item.getStatus() != PreferentialConstant.STATUS_UNCOMMITTED || item.getStatus() != PreferentialConstant.STATUS_FAIL) {
                throw new BusinessException("优惠服务项已处理");
            }
            //(5)更新对象
            UnionPreferentialItem updateItem = new UnionPreferentialItem();
            updateItem.setId(item.getId()); //优惠服务项id
            updateItem.setStatus(PreferentialConstant.STATUS_VERIFYING); //优惠服务项状态变成审核中
            updateItem.setModifytime(DateUtil.getCurrentDate()); //修改时间
            updateItemList.add(updateItem);
        }
        //更新操作
        this.updateBatch(updateItemList);
    }

    @Override
    public void removeBatchByIdsAndBusIdAndMemberId(List<Integer> itemIdList, Integer busId, Integer memberId) throws Exception {
        if (itemIdList == null || busId == null || memberId == null) {
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
        //(4)检查优惠服务项信息是否过期
        List<UnionPreferentialItem> updateItemList = new ArrayList<>();
        for (Integer itemId : itemIdList) {
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
            //(5)更新对象
            UnionPreferentialItem updateItem = new UnionPreferentialItem();
            updateItem.setId(item.getId()); //优惠服务项id
            updateItem.setDelStatus(CommonConstant.DEL_STATUS_YES); //删除状态变成已删除
            updateItem.setModifytime(DateUtil.getCurrentDate()); //修改时间
            updateItemList.add(updateItem);
        }
        //(6)更新操作
        this.updateBatch(updateItemList);
    }

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
        this.unionMainService.checkUnionValid(unionOwner.getUnionId());
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
        this.updateBatch(updateItemList);
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - count *******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - boolean *****************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Object As a Service - get **********************************************
     ******************************************************************************************************************/

    @Override
    public UnionPreferentialItem getById(Integer itemId) throws Exception {
        if (itemId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionPreferentialItem result;
        //(1)cache
        String itemIdKey = RedisKeyUtil.getItemIdKey(itemId);
        if (this.redisCacheUtil.exists(itemIdKey)) {
            String tempStr = this.redisCacheUtil.get(itemIdKey);
            result = JSONArray.parseObject(tempStr, UnionPreferentialItem.class);
            return result;
        }
        //(2)db
        EntityWrapper<UnionPreferentialItem> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", itemId);
        result = this.selectOne(entityWrapper);
        setCache(result, itemId);
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - list *********************************************
     ******************************************************************************************************************/

    @Override
    public List<UnionPreferentialItem> listByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionPreferentialItem> result;
        //(1)get in cache
        String projectIdKey = RedisKeyUtil.getItemProjectIdKey(projectId);
        if (this.redisCacheUtil.exists(projectIdKey)) {
            String tempStr = this.redisCacheUtil.get(projectIdKey);
            result = JSONArray.parseArray(tempStr, UnionPreferentialItem.class);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionPreferentialItem> entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("project_id", projectId);
        result = this.selectList(entityWrapper);
        setCache(result, projectId, PreferentialConstant.REDIS_KEY_ITEM_PROJECT_ID);
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - save *********************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void save(UnionPreferentialItem newItem) throws Exception {
        if (newItem == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insert(newItem);
        this.removeCache(newItem);
    }

    @Override
    @Transactional
    public void saveBatch(List<UnionPreferentialItem> newItemList) throws Exception {
        if (newItemList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insertBatch(newItemList);
        this.removeCache(newItemList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - remove *******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void removeById(Integer itemId) throws Exception {
        if (itemId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        UnionPreferentialItem item = this.getById(itemId);
        removeCache(item);
        //(2)remove in db logically
        UnionPreferentialItem removeOpportunity = new UnionPreferentialItem();
        removeOpportunity.setId(itemId);
        removeOpportunity.setDelStatus(CommonConstant.DEL_STATUS_YES);
        this.updateById(removeOpportunity);
    }

    @Override
    @Transactional
    public void removeBatchById(List<Integer> itemIdList) throws Exception {
        if (itemIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<UnionPreferentialItem> itemList = new ArrayList<>();
        for (Integer itemId : itemIdList) {
            UnionPreferentialItem item = this.getById(itemId);
            itemList.add(item);
        }
        removeCache(itemList);
        //(2)remove in db logically
        List<UnionPreferentialItem> removeItemList = new ArrayList<>();
        for (Integer itemId : itemIdList) {
            UnionPreferentialItem removeItem = new UnionPreferentialItem();
            removeItem.setId(itemId);
            removeItem.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeItemList.add(removeItem);
        }
        this.updateBatchById(removeItemList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - update *******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void update(UnionPreferentialItem updateItem) throws Exception {
        if (updateItem == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        Integer itemId = updateItem.getId();
        UnionPreferentialItem item = this.getById(itemId);
        removeCache(item);
        //(2)update db
        this.updateById(updateItem);
    }

    @Override
    @Transactional
    public void updateBatch(List<UnionPreferentialItem> updateItemList) throws Exception {
        if (updateItemList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<Integer> itemIdList = new ArrayList<>();
        for (UnionPreferentialItem updateItem : updateItemList) {
            itemIdList.add(updateItem.getId());
        }
        List<UnionPreferentialItem> itemList = new ArrayList<>();
        for (Integer itemId : itemIdList) {
            UnionPreferentialItem item = this.getById(itemId);
            itemList.add(item);
        }
        removeCache(itemList);
        //(2)update db
        this.updateBatchById(updateItemList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - cache support ************************************
     ******************************************************************************************************************/

    private void setCache(UnionPreferentialItem newItem, Integer itemId) {
        if (itemId == null) {
            return; //do nothing,just in case
        }
        String itemIdKey = RedisKeyUtil.getItemIdKey(itemId);
        this.redisCacheUtil.set(itemIdKey, newItem);
    }

    private void setCache(List<UnionPreferentialItem> newItemList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            return; //do nothing,just in case
        }
        String foreignIdKey;
        switch (foreignIdType) {
            case PreferentialConstant.REDIS_KEY_ITEM_PROJECT_ID:
                foreignIdKey = RedisKeyUtil.getItemProjectIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newItemList);
                break;
            default:
                break;
        }
    }

    private void removeCache(UnionPreferentialItem item) {
        if (item == null) {
            return;
        }
        Integer itemId = item.getId();
        String itemIdKey = RedisKeyUtil.getItemIdKey(itemId);
        this.redisCacheUtil.remove(itemIdKey);
        Integer projectId = item.getProjectId();
        if (projectId != null) {
            String projectIdKey = RedisKeyUtil.getItemProjectIdKey(projectId);
            this.redisCacheUtil.remove(projectIdKey);
        }
    }

    private void removeCache(List<UnionPreferentialItem> itemList) {
        if (ListUtil.isEmpty(itemList)) {
            return;
        }
        List<Integer> itemIdList = new ArrayList<>();
        for (UnionPreferentialItem item : itemList) {
            itemIdList.add(item.getId());
        }
        List<String> itemIdKeyList = RedisKeyUtil.getItemIdKey(itemIdList);
        this.redisCacheUtil.remove(itemIdKeyList);
        List<String> projectIdKeyList = getForeignIdKeyList(itemList, PreferentialConstant.REDIS_KEY_ITEM_PROJECT_ID);
        if (ListUtil.isNotEmpty(projectIdKeyList)) {
            this.redisCacheUtil.remove(projectIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionPreferentialItem> itemList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case PreferentialConstant.REDIS_KEY_ITEM_PROJECT_ID:
                for (UnionPreferentialItem item : itemList) {
                    Integer projectId = item.getProjectId();
                    if (projectId != null) {
                        String projectIdKey = RedisKeyUtil.getItemProjectIdKey(projectId);
                        result.add(projectIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}
