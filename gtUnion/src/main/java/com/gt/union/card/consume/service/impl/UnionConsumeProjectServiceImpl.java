package com.gt.union.card.consume.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.consume.dao.IUnionConsumeProjectDao;
import com.gt.union.card.consume.entity.UnionConsumeProject;
import com.gt.union.card.consume.service.IUnionConsumeProjectService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 消费核销项目优惠 服务实现类
 *
 * @author linweicong
 * @version 2017-11-27 10:27:29
 */
@Service
public class UnionConsumeProjectServiceImpl implements IUnionConsumeProjectService {
    @Autowired
    private IUnionConsumeProjectDao unionConsumeProjectDao;

    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************


    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    @Override
    public Integer countValidByProjectIdAndProjectItemId(Integer projectId, Integer projectItemId) throws Exception {
        if (projectId == null || projectItemId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .eq("project_id", projectId)
                .eq("project_item_id", projectItemId);

        return unionConsumeProjectDao.selectCount(entityWrapper);
    }

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionConsumeProject> filterByDelStatus(List<UnionConsumeProject> unionConsumeProjectList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionConsumeProject> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionConsumeProjectList)) {
            for (UnionConsumeProject unionConsumeProject : unionConsumeProjectList) {
                if (delStatus.equals(unionConsumeProject.getDelStatus())) {
                    result.add(unionConsumeProject);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionConsumeProject getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        return unionConsumeProjectDao.selectById(id);
    }

    @Override
    public UnionConsumeProject getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", id);

        return unionConsumeProjectDao.selectOne(entityWrapper);
    }

    @Override
    public UnionConsumeProject getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("id", id);

        return unionConsumeProjectDao.selectOne(entityWrapper);
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionConsumeProject> unionConsumeProjectList) throws Exception {
        if (unionConsumeProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionConsumeProjectList)) {
            for (UnionConsumeProject unionConsumeProject : unionConsumeProjectList) {
                result.add(unionConsumeProject.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionConsumeProject> listByConsumeId(Integer consumeId) throws Exception {
        if (consumeId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("consume_id", consumeId);

        return unionConsumeProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsumeProject> listValidByConsumeId(Integer consumeId) throws Exception {
        if (consumeId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("consume_id", consumeId);

        return unionConsumeProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsumeProject> listInvalidByConsumeId(Integer consumeId) throws Exception {
        if (consumeId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("consume_id", consumeId);

        return unionConsumeProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsumeProject> listByProjectItemId(Integer projectItemId) throws Exception {
        if (projectItemId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("project_item_id", projectItemId);

        return unionConsumeProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsumeProject> listValidByProjectItemId(Integer projectItemId) throws Exception {
        if (projectItemId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("project_item_id", projectItemId);

        return unionConsumeProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsumeProject> listInvalidByProjectItemId(Integer projectItemId) throws Exception {
        if (projectItemId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("project_item_id", projectItemId);

        return unionConsumeProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsumeProject> listByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("project_id", projectId);

        return unionConsumeProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsumeProject> listValidByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("project_id", projectId);

        return unionConsumeProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsumeProject> listInvalidByProjectId(Integer projectId) throws Exception {
        if (projectId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("project_id", projectId);

        return unionConsumeProjectDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsumeProject> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsumeProject> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", idList).eq(ListUtil.isEmpty(idList), "id", null);

        return unionConsumeProjectDao.selectList(entityWrapper);
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionConsumeProject> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionConsumeProjectDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionConsumeProject newUnionConsumeProject) throws Exception {
        if (newUnionConsumeProject == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionConsumeProjectDao.insert(newUnionConsumeProject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionConsumeProject> newUnionConsumeProjectList) throws Exception {
        if (newUnionConsumeProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionConsumeProjectDao.insertBatch(newUnionConsumeProjectList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionConsumeProject removeUnionConsumeProject = new UnionConsumeProject();
        removeUnionConsumeProject.setId(id);
        removeUnionConsumeProject.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionConsumeProjectDao.updateById(removeUnionConsumeProject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionConsumeProject> removeUnionConsumeProjectList = new ArrayList<>();
        for (Integer id : idList) {
            UnionConsumeProject removeUnionConsumeProject = new UnionConsumeProject();
            removeUnionConsumeProject.setId(id);
            removeUnionConsumeProject.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionConsumeProjectList.add(removeUnionConsumeProject);
        }
        unionConsumeProjectDao.updateBatchById(removeUnionConsumeProjectList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionConsumeProject updateUnionConsumeProject) throws Exception {
        if (updateUnionConsumeProject == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionConsumeProjectDao.updateById(updateUnionConsumeProject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionConsumeProject> updateUnionConsumeProjectList) throws Exception {
        if (updateUnionConsumeProjectList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionConsumeProjectDao.updateBatchById(updateUnionConsumeProjectList);
    }

}