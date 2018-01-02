package com.gt.union.opportunity.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.opportunity.main.dao.IUnionOpportunityRatioDao;
import com.gt.union.opportunity.main.entity.UnionOpportunityRatio;
import com.gt.union.opportunity.main.service.IUnionOpportunityRatioService;
import com.gt.union.opportunity.main.vo.OpportunityRatioVO;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 商机佣金比率 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 16:56:20
 */
@Service
public class UnionOpportunityRatioServiceImpl implements IUnionOpportunityRatioService {
    @Autowired
    private IUnionOpportunityRatioDao unionOpportunityRatioDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    //********************************************* Base On Business - get *********************************************

    @Override
    public UnionOpportunityRatio getValidByUnionIdAndFromMemberIdAndToMemberId(Integer unionId, Integer fromMemberId, Integer toMemberId) throws Exception {
        if (unionId == null || fromMemberId == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("from_member_id", fromMemberId)
                .eq("to_member_id", toMemberId);

        return unionOpportunityRatioDao.selectOne(entityWrapper);
    }

    //********************************************* Base On Business - list ********************************************

    @Override
    public Page pageOpportunityRatioVOByBusIdAndUnionId(Page page, Integer busId, Integer unionId) throws Exception {
        if (page == null || busId == null || unionId == null) {
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
        // （2）	获取所有，但不包括自己的member列表
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .ne("bus_id", busId)
                .orderBy("create_time", true);
        Page result = unionMemberService.pageSupport(page, entityWrapper);
        result.setRecords(ListOpportunityRatioVO(result.getRecords(), member.getId()));

        return result;
    }

    private List<OpportunityRatioVO> ListOpportunityRatioVO(List<UnionMember> memberList, Integer memberId) throws Exception {
        List<OpportunityRatioVO> result = new ArrayList<>();

        if (ListUtil.isNotEmpty(memberList)) {
            for (UnionMember member : memberList) {
                OpportunityRatioVO vo = new OpportunityRatioVO();
                vo.setMember(member);

                UnionOpportunityRatio ratioFromMe = getValidByUnionIdAndFromMemberIdAndToMemberId(member.getUnionId(), memberId, member.getId());
                vo.setRatioFromMe(ratioFromMe != null ? ratioFromMe.getRatio() : null);

                UnionOpportunityRatio ratioToMe = getValidByUnionIdAndFromMemberIdAndToMemberId(member.getUnionId(), member.getId(), memberId);
                vo.setRatioToMe(ratioToMe != null ? ratioToMe.getRatio() : null);

                result.add(vo);
            }
        }

        return result;
    }

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByBusIdAndUnionIdAndToMemberId(Integer busId, Integer unionId, Integer toMemberId, Double ratio) throws Exception {
        if (busId == null || unionId == null || toMemberId == null || ratio == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // （2）	判断toMemberId有效性
        UnionMember toMember = unionMemberService.getValidReadByIdAndUnionId(toMemberId, unionId);
        if (toMember == null) {
            throw new BusinessException(CommonConstant.MEMBER_NOT_FOUND);
        }
        // （3）	要求ratio在(0, 100%)
        if (ratio <= 0 || ratio >= 1) {
            throw new BusinessException("比例必须在0至100之间");
        }
        // （4）	如果存在原设置，则更新，否则新增
        UnionOpportunityRatio oldRatio = getValidByUnionIdAndFromMemberIdAndToMemberId(unionId, member.getId(), toMemberId);
        if (oldRatio != null) {
            UnionOpportunityRatio updateRatio = new UnionOpportunityRatio();
            updateRatio.setId(oldRatio.getId());
            updateRatio.setModifyTime(DateUtil.getCurrentDate());
            updateRatio.setRatio(ratio);

            update(updateRatio);
        } else {
            UnionOpportunityRatio saveRatio = new UnionOpportunityRatio();
            saveRatio.setDelStatus(CommonConstant.COMMON_NO);
            saveRatio.setCreateTime(DateUtil.getCurrentDate());
            saveRatio.setFromMemberId(member.getId());
            saveRatio.setToMemberId(toMemberId);
            saveRatio.setUnionId(unionId);
            saveRatio.setRatio(ratio);

            save(saveRatio);
        }
    }

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionOpportunityRatio> filterByDelStatus(List<UnionOpportunityRatio> unionOpportunityRatioList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunityRatio> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionOpportunityRatioList)) {
            for (UnionOpportunityRatio unionOpportunityRatio : unionOpportunityRatioList) {
                if (delStatus.equals(unionOpportunityRatio.getDelStatus())) {
                    result.add(unionOpportunityRatio);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionOpportunityRatio> filterByFromMemberId(List<UnionOpportunityRatio> ratioList, Integer fromMemberId) throws Exception {
        if (ratioList == null || fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunityRatio> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(ratioList)) {
            for (UnionOpportunityRatio ratio : ratioList) {
                if (fromMemberId.equals(ratio.getFromMemberId())) {
                    result.add(ratio);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionOpportunityRatio> filterByToMemberId(List<UnionOpportunityRatio> ratioList, Integer toMemberId) throws Exception {
        if (ratioList == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunityRatio> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(ratioList)) {
            for (UnionOpportunityRatio ratio : ratioList) {
                if (toMemberId.equals(ratio.getToMemberId())) {
                    result.add(ratio);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionOpportunityRatio> filterByUnionId(List<UnionOpportunityRatio> ratioList, Integer unionId) throws Exception {
        if (ratioList == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunityRatio> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(ratioList)) {
            for (UnionOpportunityRatio ratio : ratioList) {
                if (unionId.equals(ratio.getUnionId())) {
                    result.add(ratio);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionOpportunityRatio getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionOpportunityRatioDao.selectById(id);
    }

    @Override
    public UnionOpportunityRatio getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", id);

        return unionOpportunityRatioDao.selectOne(entityWrapper);
    }

    @Override
    public UnionOpportunityRatio getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("id", id);

        return unionOpportunityRatioDao.selectOne(entityWrapper);
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionOpportunityRatio> unionOpportunityRatioList) throws Exception {
        if (unionOpportunityRatioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionOpportunityRatioList)) {
            for (UnionOpportunityRatio unionOpportunityRatio : unionOpportunityRatioList) {
                result.add(unionOpportunityRatio.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionOpportunityRatio> listByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("from_member_id", fromMemberId);

        return unionOpportunityRatioDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunityRatio> listValidByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_member_id", fromMemberId);

        return unionOpportunityRatioDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunityRatio> listInvalidByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("from_member_id", fromMemberId);

        return unionOpportunityRatioDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunityRatio> listByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("to_member_id", toMemberId);

        return unionOpportunityRatioDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunityRatio> listValidByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("to_member_id", toMemberId);

        return unionOpportunityRatioDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunityRatio> listInvalidByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("to_member_id", toMemberId);

        return unionOpportunityRatioDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunityRatio> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);

        return unionOpportunityRatioDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunityRatio> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);

        return unionOpportunityRatioDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunityRatio> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);

        return unionOpportunityRatioDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionOpportunityRatio> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", idList);

        return unionOpportunityRatioDao.selectList(entityWrapper);
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionOpportunityRatio> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionOpportunityRatioDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionOpportunityRatio newUnionOpportunityRatio) throws Exception {
        if (newUnionOpportunityRatio == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionOpportunityRatioDao.insert(newUnionOpportunityRatio);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionOpportunityRatio> newUnionOpportunityRatioList) throws Exception {
        if (newUnionOpportunityRatioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionOpportunityRatioDao.insertBatch(newUnionOpportunityRatioList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionOpportunityRatio removeUnionOpportunityRatio = new UnionOpportunityRatio();
        removeUnionOpportunityRatio.setId(id);
        removeUnionOpportunityRatio.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionOpportunityRatioDao.updateById(removeUnionOpportunityRatio);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunityRatio> removeUnionOpportunityRatioList = new ArrayList<>();
        for (Integer id : idList) {
            UnionOpportunityRatio removeUnionOpportunityRatio = new UnionOpportunityRatio();
            removeUnionOpportunityRatio.setId(id);
            removeUnionOpportunityRatio.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionOpportunityRatioList.add(removeUnionOpportunityRatio);
        }
        unionOpportunityRatioDao.updateBatchById(removeUnionOpportunityRatioList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionOpportunityRatio updateUnionOpportunityRatio) throws Exception {
        if (updateUnionOpportunityRatio == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionOpportunityRatioDao.updateById(updateUnionOpportunityRatio);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionOpportunityRatio> updateUnionOpportunityRatioList) throws Exception {
        if (updateUnionOpportunityRatioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionOpportunityRatioDao.updateBatchById(updateUnionOpportunityRatioList);
    }

}