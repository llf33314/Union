package com.gt.union.opportunity.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.opportunity.main.entity.UnionOpportunityRatio;
import com.gt.union.opportunity.main.mapper.UnionOpportunityRatioMapper;
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
public class UnionOpportunityRatioServiceImpl extends ServiceImpl<UnionOpportunityRatioMapper, UnionOpportunityRatio> implements IUnionOpportunityRatioService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public UnionOpportunityRatio getByFromMemberIdAndToMemberIdAndUnionId(Integer fromMemberId, Integer toMemberId, Integer unionId) throws Exception {
        if (fromMemberId == null || toMemberId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionOpportunityRatio> result = listByToMemberId(toMemberId);
        result = filterByFromMemberId(result, fromMemberId);
        result = filterByUnionId(result, unionId);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<OpportunityRatioVO> listOpportunityRatioVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
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
        // （2）	获取所有，但不包括自己的member列表
        List<OpportunityRatioVO> result = new ArrayList<>();
        List<UnionMember> otherMemberList = unionMemberService.listOtherReadByBusIdAndUnionId(busId, unionId);
        if (ListUtil.isNotEmpty(otherMemberList)) {
            for (UnionMember otherMember : otherMemberList) {
                OpportunityRatioVO vo = new OpportunityRatioVO();
                vo.setMember(otherMember);

                UnionOpportunityRatio ratioFromMe = getByFromMemberIdAndToMemberIdAndUnionId(member.getId(), otherMember.getId(), unionId);
                vo.setRatioFromMe(ratioFromMe != null ? ratioFromMe.getRatio() : null);

                UnionOpportunityRatio ratioToMe = getByFromMemberIdAndToMemberIdAndUnionId(otherMember.getId(), member.getId(), unionId);
                vo.setRatioToMe(ratioToMe != null ? ratioToMe.getRatio() : null);
                result.add(vo);
            }
        }
        // （3）	按时间顺序排序
        Collections.sort(result, new Comparator<OpportunityRatioVO>() {
            @Override
            public int compare(OpportunityRatioVO o1, OpportunityRatioVO o2) {
                return o2.getMember().getCreateTime().compareTo(o1.getMember().getCreateTime());
            }
        });
        return result;
    }


    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    @Override
    public void updateRatioByBusIdAndUnionIdAndToMemberId(Integer busId, Integer unionId, Integer toMemberId, Double ratio) throws Exception {
        if (busId == null || unionId == null || toMemberId == null || ratio == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        // （2）	判断toMemberId有效性
        UnionMember toMember = unionMemberService.getReadByIdAndUnionId(toMemberId, unionId);
        if (toMember == null) {
            throw new BusinessException("找不到盟员信息");
        }
        // （3）	要求ratio在(0, 100)
        if (ratio <= 0 || ratio >= 100) {
            throw new BusinessException("比例必须在0至100之间");
        }
        // （4）	如果存在原设置，则更新，否则新增
        UnionOpportunityRatio oldRatio = getByFromMemberIdAndToMemberIdAndUnionId(member.getId(), toMemberId, unionId);
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

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

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

    //***************************************** Object As a Service - get **********************************************

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
        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

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
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.PARAM_ERROR);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionOpportunityRatioCacheUtil.TYPE_UNION_ID);
        return result;
    }

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
        entityWrapper.eq("from_member_id", fromMemberId)
                .eq("del_status", CommonConstant.PARAM_ERROR);
        result = selectList(entityWrapper);
        setCache(result, fromMemberId, UnionOpportunityRatioCacheUtil.TYPE_FROM_MEMBER_ID);
        return result;
    }

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
        entityWrapper.eq("to_member_id", toMemberId)
                .eq("del_status", CommonConstant.PARAM_ERROR);
        result = selectList(entityWrapper);
        setCache(result, toMemberId, UnionOpportunityRatioCacheUtil.TYPE_TO_MEMBER_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionOpportunityRatio newUnionOpportunityRatio) throws Exception {
        if (newUnionOpportunityRatio == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionOpportunityRatio);
        removeCache(newUnionOpportunityRatio);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionOpportunityRatio> newUnionOpportunityRatioList) throws Exception {
        if (newUnionOpportunityRatioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionOpportunityRatioList);
        removeCache(newUnionOpportunityRatioList);
    }

    //***************************************** Object As a Service - remove *******************************************

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
        updateById(removeUnionOpportunityRatio);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionOpportunityRatio> unionOpportunityRatioList = new ArrayList<>();
        for (Integer id : idList) {
            UnionOpportunityRatio unionOpportunityRatio = getById(id);
            unionOpportunityRatioList.add(unionOpportunityRatio);
        }
        removeCache(unionOpportunityRatioList);
        // (2)remove in db logically
        List<UnionOpportunityRatio> removeUnionOpportunityRatioList = new ArrayList<>();
        for (Integer id : idList) {
            UnionOpportunityRatio removeUnionOpportunityRatio = new UnionOpportunityRatio();
            removeUnionOpportunityRatio.setId(id);
            removeUnionOpportunityRatio.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionOpportunityRatioList.add(removeUnionOpportunityRatio);
        }
        updateBatchById(removeUnionOpportunityRatioList);
    }

    //***************************************** Object As a Service - update *******************************************

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
        updateById(updateUnionOpportunityRatio);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionOpportunityRatio> updateUnionOpportunityRatioList) throws Exception {
        if (updateUnionOpportunityRatioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionOpportunityRatio updateUnionOpportunityRatio : updateUnionOpportunityRatioList) {
            idList.add(updateUnionOpportunityRatio.getId());
        }
        List<UnionOpportunityRatio> unionOpportunityRatioList = new ArrayList<>();
        for (Integer id : idList) {
            UnionOpportunityRatio unionOpportunityRatio = getById(id);
            unionOpportunityRatioList.add(unionOpportunityRatio);
        }
        removeCache(unionOpportunityRatioList);
        // (2)update db
        updateBatchById(updateUnionOpportunityRatioList);
    }

    //***************************************** Object As a Service - cache support ************************************

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
            case UnionOpportunityRatioCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionOpportunityRatioCacheUtil.getUnionIdKey(foreignId);
                break;
            case UnionOpportunityRatioCacheUtil.TYPE_FROM_MEMBER_ID:
                foreignIdKey = UnionOpportunityRatioCacheUtil.getFromMemberIdKey(foreignId);
                break;
            case UnionOpportunityRatioCacheUtil.TYPE_TO_MEMBER_ID:
                foreignIdKey = UnionOpportunityRatioCacheUtil.getToMemberIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionOpportunityRatioList);
        }
    }

    private void removeCache(UnionOpportunityRatio unionOpportunityRatio) {
        if (unionOpportunityRatio == null) {
            return;
        }
        Integer id = unionOpportunityRatio.getId();
        String idKey = UnionOpportunityRatioCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer unionId = unionOpportunityRatio.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionOpportunityRatioCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }

        Integer fromMemberId = unionOpportunityRatio.getFromMemberId();
        if (fromMemberId != null) {
            String fromMemberIdKey = UnionOpportunityRatioCacheUtil.getFromMemberIdKey(fromMemberId);
            redisCacheUtil.remove(fromMemberIdKey);
        }

        Integer toMemberId = unionOpportunityRatio.getToMemberId();
        if (toMemberId != null) {
            String toMemberIdKey = UnionOpportunityRatioCacheUtil.getToMemberIdKey(toMemberId);
            redisCacheUtil.remove(toMemberIdKey);
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

        List<String> unionIdKeyList = getForeignIdKeyList(unionOpportunityRatioList, UnionOpportunityRatioCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

        List<String> fromMemberIdKeyList = getForeignIdKeyList(unionOpportunityRatioList, UnionOpportunityRatioCacheUtil.TYPE_FROM_MEMBER_ID);
        if (ListUtil.isNotEmpty(fromMemberIdKeyList)) {
            redisCacheUtil.remove(fromMemberIdKeyList);
        }

        List<String> toMemberIdKeyList = getForeignIdKeyList(unionOpportunityRatioList, UnionOpportunityRatioCacheUtil.TYPE_TO_MEMBER_ID);
        if (ListUtil.isNotEmpty(toMemberIdKeyList)) {
            redisCacheUtil.remove(toMemberIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionOpportunityRatio> unionOpportunityRatioList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionOpportunityRatioCacheUtil.TYPE_UNION_ID:
                for (UnionOpportunityRatio unionOpportunityRatio : unionOpportunityRatioList) {
                    Integer unionId = unionOpportunityRatio.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionOpportunityRatioCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            case UnionOpportunityRatioCacheUtil.TYPE_FROM_MEMBER_ID:
                for (UnionOpportunityRatio unionOpportunityRatio : unionOpportunityRatioList) {
                    Integer fromMemberId = unionOpportunityRatio.getFromMemberId();
                    if (fromMemberId != null) {
                        String fromMemberIdKey = UnionOpportunityRatioCacheUtil.getFromMemberIdKey(fromMemberId);
                        result.add(fromMemberIdKey);
                    }
                }
                break;
            case UnionOpportunityRatioCacheUtil.TYPE_TO_MEMBER_ID:
                for (UnionOpportunityRatio unionOpportunityRatio : unionOpportunityRatioList) {
                    Integer toMemberId = unionOpportunityRatio.getToMemberId();
                    if (toMemberId != null) {
                        String toMemberIdKey = UnionOpportunityRatioCacheUtil.getToMemberIdKey(toMemberId);
                        result.add(toMemberIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }

}