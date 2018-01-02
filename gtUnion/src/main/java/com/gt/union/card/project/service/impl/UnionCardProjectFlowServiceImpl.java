package com.gt.union.card.project.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.project.dao.IUnionCardProjectFlowDao;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.entity.UnionCardProjectFlow;
import com.gt.union.card.project.service.IUnionCardProjectFlowService;
import com.gt.union.card.project.service.IUnionCardProjectService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目流程 服务实现类
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
@Service
public class UnionCardProjectFlowServiceImpl implements IUnionCardProjectFlowService {
    @Autowired
    private IUnionCardProjectFlowDao unionCardProjectFlowDao;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionCardProjectService unionCardProjectService;

    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<UnionCardProjectFlow> listValidByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception {
        if (busId == null || unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // （3）	按时间顺序排序
        List<UnionCardProjectFlow> result = new ArrayList<>();
        UnionCardProject project = unionCardProjectService.getValidByUnionIdAndMemberIdAndActivityId(unionId, member.getId(), activityId);
        if (project != null) {
            EntityWrapper<UnionCardProjectFlow> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                    .eq("project_id", project.getId())
                    .orderBy("create_time", true);

            result = unionCardProjectFlowDao.selectList(entityWrapper);
        }

        return result;
    }

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionCardProjectFlow> filterByDelStatus(List<UnionCardProjectFlow> unionCardProjectFlowList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProjectFlow> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardProjectFlowList)) {
            for (UnionCardProjectFlow unionCardProjectFlow : unionCardProjectFlowList) {
                if (delStatus.equals(unionCardProjectFlow.getDelStatus())) {
                    result.add(unionCardProjectFlow);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionCardProjectFlow getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardProjectFlowDao.selectById(id);
    }

    @Override
    public UnionCardProjectFlow getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProjectFlow> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", id);

        return unionCardProjectFlowDao.selectOne(entityWrapper);
    }

    @Override
    public UnionCardProjectFlow getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProjectFlow> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("id", id);

        return unionCardProjectFlowDao.selectOne(entityWrapper);
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionCardProjectFlow> unionCardProjectFlowList) throws Exception {
        if (unionCardProjectFlowList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardProjectFlowList)) {
            for (UnionCardProjectFlow unionCardProjectFlow : unionCardProjectFlowList) {
                result.add(unionCardProjectFlow.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionCardProjectFlow> listByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProjectFlow> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("project_id", projectId);

        return unionCardProjectFlowDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProjectFlow> listValidByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProjectFlow> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("project_id", projectId);

        return unionCardProjectFlowDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProjectFlow> listInvalidByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProjectFlow> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("project_id", projectId);

        return unionCardProjectFlowDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCardProjectFlow> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardProjectFlow> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", idList);

        return unionCardProjectFlowDao.selectList(entityWrapper);
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionCardProjectFlow> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardProjectFlowDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardProjectFlow newUnionCardProjectFlow) throws Exception {
        if (newUnionCardProjectFlow == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardProjectFlowDao.insert(newUnionCardProjectFlow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardProjectFlow> newUnionCardProjectFlowList) throws Exception {
        if (newUnionCardProjectFlowList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardProjectFlowDao.insertBatch(newUnionCardProjectFlowList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionCardProjectFlow removeUnionCardProjectFlow = new UnionCardProjectFlow();
        removeUnionCardProjectFlow.setId(id);
        removeUnionCardProjectFlow.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionCardProjectFlowDao.updateById(removeUnionCardProjectFlow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardProjectFlow> removeUnionCardProjectFlowList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardProjectFlow removeUnionCardProjectFlow = new UnionCardProjectFlow();
            removeUnionCardProjectFlow.setId(id);
            removeUnionCardProjectFlow.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardProjectFlowList.add(removeUnionCardProjectFlow);
        }
        unionCardProjectFlowDao.updateBatchById(removeUnionCardProjectFlowList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardProjectFlow updateUnionCardProjectFlow) throws Exception {
        if (updateUnionCardProjectFlow == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardProjectFlowDao.updateById(updateUnionCardProjectFlow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardProjectFlow> updateUnionCardProjectFlowList) throws Exception {
        if (updateUnionCardProjectFlowList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardProjectFlowDao.updateBatchById(updateUnionCardProjectFlowList);
    }


}