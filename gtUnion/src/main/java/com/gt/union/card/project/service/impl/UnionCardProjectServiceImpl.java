package com.gt.union.card.project.service.impl;

import com.alibaba.fastjson.JSONArray;
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
public class UnionCardProjectServiceImpl implements IUnionCardProjectService {
    @Autowired
    private IUnionCardProjectDao unionCardProjectDao;

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

    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************


    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

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

    //********************************************* Object As a Service - get ******************************************

    @Override
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
        result = unionCardProjectDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionCardProject getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardProject result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionCardProject getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardProject result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
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
        entityWrapper.eq("activity_id", activityId);
        result = unionCardProjectDao.selectList(entityWrapper);
        setCache(result, activityId, UnionCardProjectCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    @Override
    public List<UnionCardProject> listValidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardProject> result;
        // (1)cache
        String validActivityIdKey = UnionCardProjectCacheUtil.getValidActivityIdKey(activityId);
        if (redisCacheUtil.exists(validActivityIdKey)) {
            String tempStr = redisCacheUtil.get(validActivityIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("activity_id", activityId);
        result = unionCardProjectDao.selectList(entityWrapper);
        setValidCache(result, activityId, UnionCardProjectCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    @Override
    public List<UnionCardProject> listInvalidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardProject> result;
        // (1)cache
        String invalidActivityIdKey = UnionCardProjectCacheUtil.getInvalidActivityIdKey(activityId);
        if (redisCacheUtil.exists(invalidActivityIdKey)) {
            String tempStr = redisCacheUtil.get(invalidActivityIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("activity_id", activityId);
        result = unionCardProjectDao.selectList(entityWrapper);
        setInvalidCache(result, activityId, UnionCardProjectCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("member_id", memberId);
        result = unionCardProjectDao.selectList(entityWrapper);
        setCache(result, memberId, UnionCardProjectCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionCardProject> listValidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardProject> result;
        // (1)cache
        String validMemberIdKey = UnionCardProjectCacheUtil.getValidMemberIdKey(memberId);
        if (redisCacheUtil.exists(validMemberIdKey)) {
            String tempStr = redisCacheUtil.get(validMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("member_id", memberId);
        result = unionCardProjectDao.selectList(entityWrapper);
        setValidCache(result, memberId, UnionCardProjectCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionCardProject> listInvalidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardProject> result;
        // (1)cache
        String invalidMemberIdKey = UnionCardProjectCacheUtil.getInvalidMemberIdKey(memberId);
        if (redisCacheUtil.exists(invalidMemberIdKey)) {
            String tempStr = redisCacheUtil.get(invalidMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("member_id", memberId);
        result = unionCardProjectDao.selectList(entityWrapper);
        setInvalidCache(result, memberId, UnionCardProjectCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("union_id", unionId);
        result = unionCardProjectDao.selectList(entityWrapper);
        setCache(result, unionId, UnionCardProjectCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionCardProject> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardProject> result;
        // (1)cache
        String validUnionIdKey = UnionCardProjectCacheUtil.getValidUnionIdKey(unionId);
        if (redisCacheUtil.exists(validUnionIdKey)) {
            String tempStr = redisCacheUtil.get(validUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);
        result = unionCardProjectDao.selectList(entityWrapper);
        setValidCache(result, unionId, UnionCardProjectCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionCardProject> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardProject> result;
        // (1)cache
        String invalidUnionIdKey = UnionCardProjectCacheUtil.getInvalidUnionIdKey(unionId);
        if (redisCacheUtil.exists(invalidUnionIdKey)) {
            String tempStr = redisCacheUtil.get(invalidUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardProject.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);
        result = unionCardProjectDao.selectList(entityWrapper);
        setInvalidCache(result, unionId, UnionCardProjectCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionCardProject> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProject> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page<UnionCardProject> pageSupport(Page page, EntityWrapper<UnionCardProject> entityWrapper) throws Exception {
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
        removeCache(newUnionCardProject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardProject> newUnionCardProjectList) throws Exception {
        if (newUnionCardProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionCardProjectDao.insertBatch(newUnionCardProjectList);
        removeCache(newUnionCardProjectList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
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
        unionCardProjectDao.updateById(removeUnionCardProject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardProject> unionCardProjectList = listByIdList(idList);
        removeCache(unionCardProjectList);
        // (2)remove in db logically
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
        // (1)remove cache
        Integer id = updateUnionCardProject.getId();
        UnionCardProject unionCardProject = getById(id);
        removeCache(unionCardProject);
        // (2)update db
        unionCardProjectDao.updateById(updateUnionCardProject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardProject> updateUnionCardProjectList) throws Exception {
        if (updateUnionCardProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionCardProjectList);
        List<UnionCardProject> unionCardProjectList = listByIdList(idList);
        removeCache(unionCardProjectList);
        // (2)update db
        unionCardProjectDao.updateBatchById(updateUnionCardProjectList);
    }

    //********************************************* Object As a Service - cache support ********************************

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

    private void setValidCache(List<UnionCardProject> newUnionCardProjectList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionCardProjectCacheUtil.TYPE_ACTIVITY_ID:
                validForeignIdKey = UnionCardProjectCacheUtil.getValidActivityIdKey(foreignId);
                break;
            case UnionCardProjectCacheUtil.TYPE_MEMBER_ID:
                validForeignIdKey = UnionCardProjectCacheUtil.getValidMemberIdKey(foreignId);
                break;
            case UnionCardProjectCacheUtil.TYPE_UNION_ID:
                validForeignIdKey = UnionCardProjectCacheUtil.getValidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionCardProjectList);
        }
    }

    private void setInvalidCache(List<UnionCardProject> newUnionCardProjectList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionCardProjectCacheUtil.TYPE_ACTIVITY_ID:
                invalidForeignIdKey = UnionCardProjectCacheUtil.getInvalidActivityIdKey(foreignId);
                break;
            case UnionCardProjectCacheUtil.TYPE_MEMBER_ID:
                invalidForeignIdKey = UnionCardProjectCacheUtil.getInvalidMemberIdKey(foreignId);
                break;
            case UnionCardProjectCacheUtil.TYPE_UNION_ID:
                invalidForeignIdKey = UnionCardProjectCacheUtil.getInvalidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionCardProjectList);
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

            String validActivityIdKey = UnionCardProjectCacheUtil.getValidActivityIdKey(activityId);
            redisCacheUtil.remove(validActivityIdKey);

            String invalidActivityIdKey = UnionCardProjectCacheUtil.getInvalidActivityIdKey(activityId);
            redisCacheUtil.remove(invalidActivityIdKey);
        }

        Integer memberId = unionCardProject.getMemberId();
        if (memberId != null) {
            String memberIdKey = UnionCardProjectCacheUtil.getMemberIdKey(memberId);
            redisCacheUtil.remove(memberIdKey);

            String validMemberIdKey = UnionCardProjectCacheUtil.getValidMemberIdKey(memberId);
            redisCacheUtil.remove(validMemberIdKey);

            String invalidMemberIdKey = UnionCardProjectCacheUtil.getInvalidMemberIdKey(memberId);
            redisCacheUtil.remove(invalidMemberIdKey);
        }

        Integer unionId = unionCardProject.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionCardProjectCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);

            String validUnionIdKey = UnionCardProjectCacheUtil.getValidUnionIdKey(unionId);
            redisCacheUtil.remove(validUnionIdKey);

            String invalidUnionIdKey = UnionCardProjectCacheUtil.getInvalidUnionIdKey(unionId);
            redisCacheUtil.remove(invalidUnionIdKey);
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

                        String validActivityIdKey = UnionCardProjectCacheUtil.getValidActivityIdKey(activityId);
                        result.add(validActivityIdKey);

                        String invalidActivityIdKey = UnionCardProjectCacheUtil.getInvalidActivityIdKey(activityId);
                        result.add(invalidActivityIdKey);
                    }
                }
                break;
            case UnionCardProjectCacheUtil.TYPE_MEMBER_ID:
                for (UnionCardProject unionCardProject : unionCardProjectList) {
                    Integer memberId = unionCardProject.getMemberId();
                    if (memberId != null) {
                        String memberIdKey = UnionCardProjectCacheUtil.getMemberIdKey(memberId);
                        result.add(memberIdKey);

                        String validMemberIdKey = UnionCardProjectCacheUtil.getValidMemberIdKey(memberId);
                        result.add(validMemberIdKey);

                        String invalidMemberIdKey = UnionCardProjectCacheUtil.getInvalidMemberIdKey(memberId);
                        result.add(invalidMemberIdKey);
                    }
                }
                break;
            case UnionCardProjectCacheUtil.TYPE_UNION_ID:
                for (UnionCardProject unionCardProject : unionCardProjectList) {
                    Integer unionId = unionCardProject.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionCardProjectCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);

                        String validUnionIdKey = UnionCardProjectCacheUtil.getValidUnionIdKey(unionId);
                        result.add(validUnionIdKey);

                        String invalidUnionIdKey = UnionCardProjectCacheUtil.getInvalidUnionIdKey(unionId);
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
    public CardProjectVO getProjectVOByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception {
        if (activityId == null || unionId == null || busId == null) {
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
        // （3）	获取是否erp信息（调接口），并获取相应的服务内容
        CardProjectVO result = new CardProjectVO();
        result.setMember(member);
        result.setActivity(activity);
        result.setActivityStatus(unionCardActivityService.getStatus(activity));
        Integer isErp = ListUtil.isNotEmpty(erpService.listErpByBusId(busId)) ? CommonConstant.COMMON_YES : CommonConstant.COMMON_NO;
        result.setIsErp(isErp);
        UnionCardProject project = getByUnionIdAndMemberIdAndActivityId(unionId, member.getId(), activityId);
        if (project != null) {
            result.setProject(project);
            Integer projectId = project.getId();

            List<UnionCardProjectItem> erpTextList = unionCardProjectItemService.listByProjectIdAndType(projectId, ProjectConstant.TYPE_ERP_TEXT);
            result.setErpTextList(erpTextList);

            List<UnionCardProjectItem> erpGoodsList = unionCardProjectItemService.listByProjectIdAndType(projectId, ProjectConstant.TYPE_ERP_GOODS);
            result.setErpGoodsList(erpGoodsList);

            List<UnionCardProjectItem> textList = unionCardProjectItemService.listByProjectIdAndType(projectId, ProjectConstant.TYPE_TEXT);
            result.setNonErpTextList(textList);
        } else {
            result.setErpTextList(new ArrayList<UnionCardProjectItem>());

            result.setErpGoodsList(new ArrayList<UnionCardProjectItem>());

            result.setNonErpTextList(new ArrayList<UnionCardProjectItem>());
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
        // （3）	要求报名项目已审核通过
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
        // （4）	要求报名项目是审核中状态
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
                return o1.getProject().getCreateTime().compareTo(o2.getProject().getCreateTime());
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
        if (ActivityConstant.STATUS_APPLYING != activityStatus && ActivityConstant.STATUS_BEFORE_SELL != activityStatus) {
            throw new BusinessException("活动卡状态不在报名中或售卡前");
        }
        // （4）	不通过时要求理由不能为空
        if (CommonConstant.COMMON_YES != isPass && StringUtil.isEmpty(vo.getRejectReason())) {
            throw new BusinessException("不通过时理由不能为空");
        }
        // （5）	要求报名项目在审核中状态
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
                saveFlow.setIllustration(CommonConstant.COMMON_YES == isPass ? "提交审核通过" : "提交审核不通过：" + vo.getRejectReason());
                saveFlowList.add(saveFlow);
            }
        }

        // （6）事务操作
        if (ListUtil.isNotEmpty(updateProjectList)) {
            updateBatch(updateProjectList);
        }
        if (ListUtil.isNotEmpty(saveFlowList)) {
            unionCardProjectFlowService.saveBatch(saveFlowList);
        }

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


}