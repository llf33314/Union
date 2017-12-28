package com.gt.union.opportunity.main.service.impl;

import com.alibaba.fastjson.JSONArray;
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
import com.gt.union.opportunity.main.util.UnionOpportunityRatioCacheUtil;
import com.gt.union.opportunity.main.vo.OpportunityRatioVO;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        List<UnionOpportunityRatio> result = listValidByUnionId(unionId);
        result = filterByFromMemberId(result, fromMemberId);
        result = filterByToMemberId(result, toMemberId);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<OpportunityRatioVO> listOpportunityRatioVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
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
        List<OpportunityRatioVO> result = new ArrayList<>();
        List<UnionMember> otherMemberList = unionMemberService.listOtherValidReadByBusIdAndUnionId(busId, unionId);
        if (ListUtil.isNotEmpty(otherMemberList)) {
            for (UnionMember otherMember : otherMemberList) {
                OpportunityRatioVO vo = new OpportunityRatioVO();
                vo.setMember(otherMember);

                UnionOpportunityRatio ratioFromMe = getValidByUnionIdAndFromMemberIdAndToMemberId(unionId, member.getId(), otherMember.getId());
                vo.setRatioFromMe(ratioFromMe != null ? ratioFromMe.getRatio() : null);

                UnionOpportunityRatio ratioToMe = getValidByUnionIdAndFromMemberIdAndToMemberId(unionId, otherMember.getId(), member.getId());
                vo.setRatioToMe(ratioToMe != null ? ratioToMe.getRatio() : null);

                result.add(vo);
            }
        }
        // （3）	按时间顺序排序
        Collections.sort(result, new Comparator<OpportunityRatioVO>() {
            @Override
            public int compare(OpportunityRatioVO o1, OpportunityRatioVO o2) {
                return o1.getMember().getCreateTime().compareTo(o2.getMember().getCreateTime());
            }
        });
        return result;
    }

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRatioByBusIdAndUnionIdAndToMemberId(Integer busId, Integer unionId, Integer toMemberId, Double ratio) throws Exception {
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
        UnionOpportunityRatio result;
        // (1)cache
        String idKey = UnionOpportunityRatioCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionOpportunityRatio.class);
            return result;
        }
        // (2)db
        result = unionOpportunityRatioDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionOpportunityRatio getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionOpportunityRatio result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionOpportunityRatio getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionOpportunityRatio result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
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
        List<UnionOpportunityRatio> result;
        // (1)cache
        String fromMemberIdKey = UnionOpportunityRatioCacheUtil.getFromMemberIdKey(fromMemberId);
        if (redisCacheUtil.exists(fromMemberIdKey)) {
            String tempStr = redisCacheUtil.get(fromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunityRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("from_member_id", fromMemberId);
        result = unionOpportunityRatioDao.selectList(entityWrapper);
        setCache(result, fromMemberId, UnionOpportunityRatioCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionOpportunityRatio> listValidByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunityRatio> result;
        // (1)cache
        String validFromMemberIdKey = UnionOpportunityRatioCacheUtil.getValidFromMemberIdKey(fromMemberId);
        if (redisCacheUtil.exists(validFromMemberIdKey)) {
            String tempStr = redisCacheUtil.get(validFromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunityRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_member_id", fromMemberId);
        result = unionOpportunityRatioDao.selectList(entityWrapper);
        setValidCache(result, fromMemberId, UnionOpportunityRatioCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionOpportunityRatio> listInvalidByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunityRatio> result;
        // (1)cache
        String invalidFromMemberIdKey = UnionOpportunityRatioCacheUtil.getInvalidFromMemberIdKey(fromMemberId);
        if (redisCacheUtil.exists(invalidFromMemberIdKey)) {
            String tempStr = redisCacheUtil.get(invalidFromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunityRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("from_member_id", fromMemberId);
        result = unionOpportunityRatioDao.selectList(entityWrapper);
        setInvalidCache(result, fromMemberId, UnionOpportunityRatioCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionOpportunityRatio> listByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunityRatio> result;
        // (1)cache
        String toMemberIdKey = UnionOpportunityRatioCacheUtil.getToMemberIdKey(toMemberId);
        if (redisCacheUtil.exists(toMemberIdKey)) {
            String tempStr = redisCacheUtil.get(toMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunityRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("to_member_id", toMemberId);
        result = unionOpportunityRatioDao.selectList(entityWrapper);
        setCache(result, toMemberId, UnionOpportunityRatioCacheUtil.TYPE_TO_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionOpportunityRatio> listValidByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunityRatio> result;
        // (1)cache
        String validToMemberIdKey = UnionOpportunityRatioCacheUtil.getValidToMemberIdKey(toMemberId);
        if (redisCacheUtil.exists(validToMemberIdKey)) {
            String tempStr = redisCacheUtil.get(validToMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunityRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("to_member_id", toMemberId);
        result = unionOpportunityRatioDao.selectList(entityWrapper);
        setValidCache(result, toMemberId, UnionOpportunityRatioCacheUtil.TYPE_TO_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionOpportunityRatio> listInvalidByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunityRatio> result;
        // (1)cache
        String invalidToMemberIdKey = UnionOpportunityRatioCacheUtil.getInvalidToMemberIdKey(toMemberId);
        if (redisCacheUtil.exists(invalidToMemberIdKey)) {
            String tempStr = redisCacheUtil.get(invalidToMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunityRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("to_member_id", toMemberId);
        result = unionOpportunityRatioDao.selectList(entityWrapper);
        setInvalidCache(result, toMemberId, UnionOpportunityRatioCacheUtil.TYPE_TO_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionOpportunityRatio> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunityRatio> result;
        // (1)cache
        String unionIdKey = UnionOpportunityRatioCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunityRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);
        result = unionOpportunityRatioDao.selectList(entityWrapper);
        setCache(result, unionId, UnionOpportunityRatioCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionOpportunityRatio> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunityRatio> result;
        // (1)cache
        String validUnionIdKey = UnionOpportunityRatioCacheUtil.getValidUnionIdKey(unionId);
        if (redisCacheUtil.exists(validUnionIdKey)) {
            String tempStr = redisCacheUtil.get(validUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunityRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);
        result = unionOpportunityRatioDao.selectList(entityWrapper);
        setValidCache(result, unionId, UnionOpportunityRatioCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionOpportunityRatio> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunityRatio> result;
        // (1)cache
        String invalidUnionIdKey = UnionOpportunityRatioCacheUtil.getInvalidUnionIdKey(unionId);
        if (redisCacheUtil.exists(invalidUnionIdKey)) {
            String tempStr = redisCacheUtil.get(invalidUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunityRatio.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);
        result = unionOpportunityRatioDao.selectList(entityWrapper);
        setInvalidCache(result, unionId, UnionOpportunityRatioCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionOpportunityRatio> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunityRatio> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
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
        removeCache(newUnionOpportunityRatio);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionOpportunityRatio> newUnionOpportunityRatioList) throws Exception {
        if (newUnionOpportunityRatioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionOpportunityRatioDao.insertBatch(newUnionOpportunityRatioList);
        removeCache(newUnionOpportunityRatioList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionOpportunityRatio unionOpportunityRatio = getById(id);
        removeCache(unionOpportunityRatio);
        // (2)remove in db logically
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
        // (1)remove cache
        List<UnionOpportunityRatio> unionOpportunityRatioList = listByIdList(idList);
        removeCache(unionOpportunityRatioList);
        // (2)remove in db logically
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
        // (1)remove cache
        Integer id = updateUnionOpportunityRatio.getId();
        UnionOpportunityRatio unionOpportunityRatio = getById(id);
        removeCache(unionOpportunityRatio);
        // (2)update db
        unionOpportunityRatioDao.updateById(updateUnionOpportunityRatio);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionOpportunityRatio> updateUnionOpportunityRatioList) throws Exception {
        if (updateUnionOpportunityRatioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionOpportunityRatioList);
        List<UnionOpportunityRatio> unionOpportunityRatioList = listByIdList(idList);
        removeCache(unionOpportunityRatioList);
        // (2)update db
        unionOpportunityRatioDao.updateBatchById(updateUnionOpportunityRatioList);
    }

    //********************************************* Object As a Service - cache support ********************************

    private void setCache(UnionOpportunityRatio newUnionOpportunityRatio, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionOpportunityRatioCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionOpportunityRatio);
    }

    private void setCache(List<UnionOpportunityRatio> newUnionOpportunityRatioList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionOpportunityRatioCacheUtil.TYPE_FROM_MEMBER_ID:
                foreignIdKey = UnionOpportunityRatioCacheUtil.getFromMemberIdKey(foreignId);
                break;
            case UnionOpportunityRatioCacheUtil.TYPE_TO_MEMBER_ID:
                foreignIdKey = UnionOpportunityRatioCacheUtil.getToMemberIdKey(foreignId);
                break;
            case UnionOpportunityRatioCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionOpportunityRatioCacheUtil.getUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionOpportunityRatioList);
        }
    }

    private void setValidCache(List<UnionOpportunityRatio> newUnionOpportunityRatioList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionOpportunityRatioCacheUtil.TYPE_FROM_MEMBER_ID:
                validForeignIdKey = UnionOpportunityRatioCacheUtil.getValidFromMemberIdKey(foreignId);
                break;
            case UnionOpportunityRatioCacheUtil.TYPE_TO_MEMBER_ID:
                validForeignIdKey = UnionOpportunityRatioCacheUtil.getValidToMemberIdKey(foreignId);
                break;
            case UnionOpportunityRatioCacheUtil.TYPE_UNION_ID:
                validForeignIdKey = UnionOpportunityRatioCacheUtil.getValidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionOpportunityRatioList);
        }
    }

    private void setInvalidCache(List<UnionOpportunityRatio> newUnionOpportunityRatioList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionOpportunityRatioCacheUtil.TYPE_FROM_MEMBER_ID:
                invalidForeignIdKey = UnionOpportunityRatioCacheUtil.getInvalidFromMemberIdKey(foreignId);
                break;
            case UnionOpportunityRatioCacheUtil.TYPE_TO_MEMBER_ID:
                invalidForeignIdKey = UnionOpportunityRatioCacheUtil.getInvalidToMemberIdKey(foreignId);
                break;
            case UnionOpportunityRatioCacheUtil.TYPE_UNION_ID:
                invalidForeignIdKey = UnionOpportunityRatioCacheUtil.getInvalidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionOpportunityRatioList);
        }
    }

    private void removeCache(UnionOpportunityRatio unionOpportunityRatio) {
        if (unionOpportunityRatio == null) {
            return;
        }
        Integer id = unionOpportunityRatio.getId();
        String idKey = UnionOpportunityRatioCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer fromMemberId = unionOpportunityRatio.getFromMemberId();
        if (fromMemberId != null) {
            String fromMemberIdKey = UnionOpportunityRatioCacheUtil.getFromMemberIdKey(fromMemberId);
            redisCacheUtil.remove(fromMemberIdKey);

            String validFromMemberIdKey = UnionOpportunityRatioCacheUtil.getValidFromMemberIdKey(fromMemberId);
            redisCacheUtil.remove(validFromMemberIdKey);

            String invalidFromMemberIdKey = UnionOpportunityRatioCacheUtil.getInvalidFromMemberIdKey(fromMemberId);
            redisCacheUtil.remove(invalidFromMemberIdKey);
        }

        Integer toMemberId = unionOpportunityRatio.getToMemberId();
        if (toMemberId != null) {
            String toMemberIdKey = UnionOpportunityRatioCacheUtil.getToMemberIdKey(toMemberId);
            redisCacheUtil.remove(toMemberIdKey);

            String validToMemberIdKey = UnionOpportunityRatioCacheUtil.getValidToMemberIdKey(toMemberId);
            redisCacheUtil.remove(validToMemberIdKey);

            String invalidToMemberIdKey = UnionOpportunityRatioCacheUtil.getInvalidToMemberIdKey(toMemberId);
            redisCacheUtil.remove(invalidToMemberIdKey);
        }

        Integer unionId = unionOpportunityRatio.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionOpportunityRatioCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);

            String validUnionIdKey = UnionOpportunityRatioCacheUtil.getValidUnionIdKey(unionId);
            redisCacheUtil.remove(validUnionIdKey);

            String invalidUnionIdKey = UnionOpportunityRatioCacheUtil.getInvalidUnionIdKey(unionId);
            redisCacheUtil.remove(invalidUnionIdKey);
        }

    }

    private void removeCache(List<UnionOpportunityRatio> unionOpportunityRatioList) {
        if (ListUtil.isEmpty(unionOpportunityRatioList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionOpportunityRatio unionOpportunityRatio : unionOpportunityRatioList) {
            idList.add(unionOpportunityRatio.getId());
        }
        List<String> idKeyList = UnionOpportunityRatioCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> fromMemberIdKeyList = getForeignIdKeyList(unionOpportunityRatioList, UnionOpportunityRatioCacheUtil.TYPE_FROM_MEMBER_ID);
        if (ListUtil.isNotEmpty(fromMemberIdKeyList)) {
            redisCacheUtil.remove(fromMemberIdKeyList);
        }

        List<String> toMemberIdKeyList = getForeignIdKeyList(unionOpportunityRatioList, UnionOpportunityRatioCacheUtil.TYPE_TO_MEMBER_ID);
        if (ListUtil.isNotEmpty(toMemberIdKeyList)) {
            redisCacheUtil.remove(toMemberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionOpportunityRatioList, UnionOpportunityRatioCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

    }

    private List<String> getForeignIdKeyList(List<UnionOpportunityRatio> unionOpportunityRatioList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionOpportunityRatioCacheUtil.TYPE_FROM_MEMBER_ID:
                for (UnionOpportunityRatio unionOpportunityRatio : unionOpportunityRatioList) {
                    Integer fromMemberId = unionOpportunityRatio.getFromMemberId();
                    if (fromMemberId != null) {
                        String fromMemberIdKey = UnionOpportunityRatioCacheUtil.getFromMemberIdKey(fromMemberId);
                        result.add(fromMemberIdKey);

                        String validFromMemberIdKey = UnionOpportunityRatioCacheUtil.getValidFromMemberIdKey(fromMemberId);
                        result.add(validFromMemberIdKey);

                        String invalidFromMemberIdKey = UnionOpportunityRatioCacheUtil.getInvalidFromMemberIdKey(fromMemberId);
                        result.add(invalidFromMemberIdKey);
                    }
                }
                break;
            case UnionOpportunityRatioCacheUtil.TYPE_TO_MEMBER_ID:
                for (UnionOpportunityRatio unionOpportunityRatio : unionOpportunityRatioList) {
                    Integer toMemberId = unionOpportunityRatio.getToMemberId();
                    if (toMemberId != null) {
                        String toMemberIdKey = UnionOpportunityRatioCacheUtil.getToMemberIdKey(toMemberId);
                        result.add(toMemberIdKey);

                        String validToMemberIdKey = UnionOpportunityRatioCacheUtil.getValidToMemberIdKey(toMemberId);
                        result.add(validToMemberIdKey);

                        String invalidToMemberIdKey = UnionOpportunityRatioCacheUtil.getInvalidToMemberIdKey(toMemberId);
                        result.add(invalidToMemberIdKey);
                    }
                }
                break;
            case UnionOpportunityRatioCacheUtil.TYPE_UNION_ID:
                for (UnionOpportunityRatio unionOpportunityRatio : unionOpportunityRatioList) {
                    Integer unionId = unionOpportunityRatio.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionOpportunityRatioCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);

                        String validUnionIdKey = UnionOpportunityRatioCacheUtil.getValidUnionIdKey(unionId);
                        result.add(validUnionIdKey);

                        String invalidUnionIdKey = UnionOpportunityRatioCacheUtil.getInvalidUnionIdKey(unionId);
                        result.add(invalidUnionIdKey);
                    }
                }
                break;

            default:
                break;
        }
        return result;
    }

}