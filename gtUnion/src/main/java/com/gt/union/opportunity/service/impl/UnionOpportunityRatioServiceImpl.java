package com.gt.union.opportunity.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
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
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.opportunity.constant.OpportunityConstant;
import com.gt.union.opportunity.entity.UnionOpportunityRatio;
import com.gt.union.opportunity.mapper.UnionOpportunityRatioMapper;
import com.gt.union.opportunity.service.IUnionOpportunityRatioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 商机佣金比率 服务实现类
 *
 * @author linweicong
 * @version 2017-10-23 11:17:59
 */
@Service
public class UnionOpportunityRatioServiceImpl extends ServiceImpl<UnionOpportunityRatioMapper, UnionOpportunityRatio> implements IUnionOpportunityRatioService {
    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    @Override
    public UnionOpportunityRatio getByFromMemberIdAndToMemberId(Integer fromMemberId, Integer toMemberId) throws Exception {
        if (fromMemberId == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunityRatio> ratioList = this.listByFromMemberId(fromMemberId);
        if (ListUtil.isNotEmpty(ratioList)) {
            for (UnionOpportunityRatio ratio : ratioList) {
                if (toMemberId.equals(ratio.getToMemberId())) {
                    return ratio;
                }
            }
        }
        return null;
    }

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    @Override
    public Page pageMapByBusIdAndMemberId(Page page, Integer busId, final Integer memberId) throws Exception {
        if (busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        final UnionMember member = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(member.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(member)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)查询操作
        return this.unionMemberService.pageOpportunityRatioMapByMember(page, member);
    }

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    @Override
    public void updateOrSaveByBusIdAndFromMemberIdAndToMemberIdAndRatio(Integer busId, Integer fromMemberId, Integer toMemberId, Double dRatio) throws Exception {
        if (busId == null || fromMemberId == null || toMemberId == null || dRatio == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember fromMember = this.unionMemberService.getByIdAndBusId(fromMemberId, busId);
        if (fromMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(fromMember.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(fromMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断受惠方是否有效
        UnionMember toMember = this.unionMemberService.getById(toMemberId);
        if (toMember == null) {
            throw new BusinessException("找不到例受惠方的盟员信息");
        }
        if (!this.unionMemberService.hasWriteAuthority(toMember)) {
            throw new BusinessException("受惠方正在退盟过渡期，无法操作");
        }
        //(5)校验比例
        if (dRatio <= 0.0 || dRatio > 100.0) {
            throw new BusinessException("比例必须大于0，且小于100");
        }
        //(6)查询是否已存在商机佣金比例设置，若有，则更新，否则，新增
        UnionOpportunityRatio ratio = this.getByFromMemberIdAndToMemberId(fromMemberId, toMemberId);
        if (ratio != null) {
            UnionOpportunityRatio updateRatio = new UnionOpportunityRatio();
            //商机佣金比例设置id
            updateRatio.setId(ratio.getId());
            //最后更新时间
            updateRatio.setModifytime(DateUtil.getCurrentDate());
            //比例
            updateRatio.setRatio(dRatio);
            this.update(updateRatio);
        } else {
            UnionOpportunityRatio saveRatio = new UnionOpportunityRatio();
            //删除状态
            saveRatio.setDelStatus(CommonConstant.DEL_STATUS_NO);
            //创建时间
            saveRatio.setCreatetime(DateUtil.getCurrentDate());
            //设置方盟员身份id
            saveRatio.setFromMemberId(fromMemberId);
            //受惠方盟员身份id
            saveRatio.setToMemberId(toMemberId);
            //比例
            saveRatio.setRatio(dRatio);
            this.save(saveRatio);
        }
    }

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    //******************************************* Object As a Service - get ********************************************

    @Override
    public UnionOpportunityRatio getById(Integer ratioId) throws Exception {
        if (ratioId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionOpportunityRatio result;
        //(1)cache
        String ratioIdKey = RedisKeyUtil.getRatioIdKey(ratioId);
        if (this.redisCacheUtil.exists(ratioIdKey)) {
            String tempStr = this.redisCacheUtil.get(ratioIdKey);
            result = JSONArray.parseObject(tempStr, UnionOpportunityRatio.class);
            return result;
        }
        //(2)db
        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", ratioId);
        result = this.selectOne(entityWrapper);
        setCache(result, ratioId);
        return result;
    }

    //******************************************* Object As a Service - list *******************************************

    @Override
    public List<UnionOpportunityRatio> listByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunityRatio> result;
        //(1)get in cache
        String fromMemberIdKey = RedisKeyUtil.getRatioFromMemberIdKey(fromMemberId);
        if (this.redisCacheUtil.exists(fromMemberIdKey)) {
            String tempStr = this.redisCacheUtil.get(fromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunityRatio.class);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_member_id", fromMemberId);
        result = this.selectList(entityWrapper);
        setCache(result, fromMemberId, OpportunityConstant.REDIS_KEY_RATIO_FROM_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionOpportunityRatio> listByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionOpportunityRatio> result;
        //(1)get in cache
        String toMemberIdKey = RedisKeyUtil.getRatioToMemberIdKey(toMemberId);
        if (this.redisCacheUtil.exists(toMemberIdKey)) {
            String tempStr = this.redisCacheUtil.get(toMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionOpportunityRatio.class);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionOpportunityRatio> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("to_member_id", toMemberId);
        result = this.selectList(entityWrapper);
        setCache(result, toMemberId, OpportunityConstant.REDIS_KEY_RATIO_TO_MEMBER_ID);
        return result;
    }

    //******************************************* Object As a Service - save *******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionOpportunityRatio newRatio) throws Exception {
        if (newRatio == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insert(newRatio);
        this.removeCache(newRatio);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionOpportunityRatio> newRatioList) throws Exception {
        if (newRatioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insertBatch(newRatioList);
        this.removeCache(newRatioList);
    }

    //******************************************* Object As a Service - remove *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer ratioId) throws Exception {
        if (ratioId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        UnionOpportunityRatio ratio = this.getById(ratioId);
        removeCache(ratio);
        //(2)remove in db logically
        UnionOpportunityRatio removeRatio = new UnionOpportunityRatio();
        removeRatio.setId(ratioId);
        removeRatio.setDelStatus(CommonConstant.DEL_STATUS_YES);
        this.updateById(removeRatio);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> ratioIdList) throws Exception {
        if (ratioIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<UnionOpportunityRatio> ratioList = new ArrayList<>();
        for (Integer ratioId : ratioIdList) {
            UnionOpportunityRatio ratio = this.getById(ratioId);
            ratioList.add(ratio);
        }
        removeCache(ratioList);
        //(2)remove in db logically
        List<UnionOpportunityRatio> removeRatioList = new ArrayList<>();
        for (Integer ratioId : ratioIdList) {
            UnionOpportunityRatio removeRatio = new UnionOpportunityRatio();
            removeRatio.setId(ratioId);
            removeRatio.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeRatioList.add(removeRatio);
        }
        this.updateBatchById(removeRatioList);
    }

    //******************************************* Object As a Service - update *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionOpportunityRatio updateRatio) throws Exception {
        if (updateRatio == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        Integer ratioId = updateRatio.getId();
        UnionOpportunityRatio ratio = this.getById(ratioId);
        removeCache(ratio);
        //(2)update db
        this.updateById(updateRatio);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionOpportunityRatio> updateRatioList) throws Exception {
        if (updateRatioList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<Integer> ratioIdList = new ArrayList<>();
        for (UnionOpportunityRatio ratio : updateRatioList) {
            ratioIdList.add(ratio.getId());
        }
        List<UnionOpportunityRatio> ratioList = new ArrayList<>();
        for (Integer ratioId : ratioIdList) {
            UnionOpportunityRatio ratio = this.getById(ratioId);
            ratioList.add(ratio);
        }
        removeCache(ratioList);
        //(2)update db
        this.updateBatchById(updateRatioList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionOpportunityRatio newRatio, Integer ratioId) {
        if (ratioId == null) {
            return; //do nothing,just in case
        }
        String ratioIdKey = RedisKeyUtil.getRatioIdKey(ratioId);
        this.redisCacheUtil.set(ratioIdKey, newRatio);
    }

    private void setCache(List<UnionOpportunityRatio> newRatioList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            return; //do nothing,just in case
        }
        String foreignIdKey;
        switch (foreignIdType) {
            case OpportunityConstant.REDIS_KEY_RATIO_FROM_MEMBER_ID:
                foreignIdKey = RedisKeyUtil.getRatioFromMemberIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newRatioList);
                break;
            case OpportunityConstant.REDIS_KEY_RATIO_TO_MEMBER_ID:
                foreignIdKey = RedisKeyUtil.getRatioToMemberIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newRatioList);
                break;
            default:
                break;
        }
    }

    private void removeCache(UnionOpportunityRatio ratio) {
        if (ratio == null) {
            return;
        }
        Integer ratioId = ratio.getId();
        String ratioIdKey = RedisKeyUtil.getRatioIdKey(ratioId);
        this.redisCacheUtil.remove(ratioIdKey);
        Integer fromMemberId = ratio.getFromMemberId();
        if (fromMemberId != null) {
            String fromMemberIdKey = RedisKeyUtil.getRatioFromMemberIdKey(fromMemberId);
            this.redisCacheUtil.remove(fromMemberIdKey);
        }
        Integer toMemberId = ratio.getToMemberId();
        if (toMemberId != null) {
            String toMemberIdKey = RedisKeyUtil.getRatioToMemberIdKey(toMemberId);
            this.redisCacheUtil.remove(toMemberIdKey);
        }
    }

    private void removeCache(List<UnionOpportunityRatio> ratioList) {
        if (ListUtil.isEmpty(ratioList)) {
            return;
        }
        List<Integer> ratioIdList = new ArrayList<>();
        for (UnionOpportunityRatio ratio : ratioList) {
            ratioIdList.add(ratio.getId());
        }
        List<String> ratioIdKeyList = RedisKeyUtil.getRatioIdKey(ratioIdList);
        this.redisCacheUtil.remove(ratioIdKeyList);
        List<String> fromMemberIdKeyList = getForeignIdKeyList(ratioList, OpportunityConstant.REDIS_KEY_RATIO_FROM_MEMBER_ID);
        if (ListUtil.isNotEmpty(fromMemberIdKeyList)) {
            this.redisCacheUtil.remove(fromMemberIdKeyList);
        }
        List<String> toMemberIdKeyList = getForeignIdKeyList(ratioList, OpportunityConstant.REDIS_KEY_RATIO_TO_MEMBER_ID);
        if (ListUtil.isNotEmpty(toMemberIdKeyList)) {
            this.redisCacheUtil.remove(toMemberIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionOpportunityRatio> ratioList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case OpportunityConstant.REDIS_KEY_RATIO_FROM_MEMBER_ID:
                for (UnionOpportunityRatio ratio : ratioList) {
                    Integer fromMemberId = ratio.getFromMemberId();
                    if (fromMemberId != null) {
                        String fromMemberIdKey = RedisKeyUtil.getRatioFromMemberIdKey(fromMemberId);
                        result.add(fromMemberIdKey);
                    }
                }
                break;
            case OpportunityConstant.REDIS_KEY_RATIO_TO_MEMBER_ID:
                for (UnionOpportunityRatio ratio : ratioList) {
                    Integer toMemberId = ratio.getToMemberId();
                    if (toMemberId != null) {
                        String toMemberIdKey = RedisKeyUtil.getRatioToMemberIdKey(toMemberId);
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
