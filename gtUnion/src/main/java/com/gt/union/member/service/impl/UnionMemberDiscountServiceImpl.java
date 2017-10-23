package com.gt.union.member.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.entity.UnionMemberDiscount;
import com.gt.union.member.mapper.UnionMemberDiscountMapper;
import com.gt.union.member.service.IUnionMemberDiscountService;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 盟员折扣 服务实现类
 *
 * @author linweicong
 * @version 2017-10-23 08:34:54
 */
@Service
public class UnionMemberDiscountServiceImpl extends ServiceImpl<UnionMemberDiscountMapper, UnionMemberDiscount> implements IUnionMemberDiscountService {
    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    @Override
    public List<UnionMemberDiscount> listByFromMemberIdAndToMemberId(Integer fromMemberId, Integer toMemberId) throws Exception {
        if (fromMemberId == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberDiscount> result = new ArrayList<>();
        List<UnionMemberDiscount> discountList = this.listByFromMemberId(fromMemberId);
        if (ListUtil.isNotEmpty(discountList)) {
            for (UnionMemberDiscount discount : discountList) {
                if (toMemberId.equals(discount.getToMemberId())) {
                    result.add(discount);
                }
            }
        }
        return result;
    }

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrSaveDiscountByBusIdAndMemberId(Integer busId, Integer memberId, Integer tgtMemberId, Double dDiscount) throws Exception {
        if (busId == null || memberId == null || tgtMemberId == null || dDiscount == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember member = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(member.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(member)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)校验更新内容
        if (dDiscount < 0.0 || dDiscount > 10.0) {
            throw new ParamException("折扣必须大于0折，且小于10折");
        }
        if (!DoubleUtil.checkDecimalPrecision(dDiscount, 2)) {
            throw new ParamException("折扣最多保留两位小数");
        }
        //(5)更新操作
        UnionMemberDiscount discount = null;
        List<UnionMemberDiscount> discountList = this.listByFromMemberIdAndToMemberId(memberId, tgtMemberId);
        if (ListUtil.isNotEmpty(discountList)) {
            discount = discountList.get(0);
        }
        if (discount != null) {
            //更新
            //修改时间
            discount.setModifytime(DateUtil.getCurrentDate());
            //折扣
            discount.setDiscount(dDiscount);
            this.update(discount);
        } else {
            //新增
            UnionMemberDiscount saveDiscount = new UnionMemberDiscount();
            //删除状态
            saveDiscount.setDelStatus(CommonConstant.DEL_STATUS_NO);
            //创建时间
            saveDiscount.setCreatetime(DateUtil.getCurrentDate());
            //修改时间
            saveDiscount.setModifytime(DateUtil.getCurrentDate());
            //设置折扣的盟员身份id
            saveDiscount.setFromMemberId(memberId);
            //受惠折扣的盟员身份id
            saveDiscount.setToMemberId(tgtMemberId);
            //折扣
            saveDiscount.setDiscount(dDiscount);
            this.save(saveDiscount);
        }
    }

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    //******************************************* Object As a Service - get ********************************************

    @Override
    public UnionMemberDiscount getById(Integer discountId) throws Exception {
        if (discountId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMemberDiscount result;
        //(1)cache
        String discountIdKey = RedisKeyUtil.getMemberDiscountIdKey(discountId);
        if (this.redisCacheUtil.exists(discountIdKey)) {
            String tempStr = this.redisCacheUtil.get(discountIdKey);
            result = JSONArray.parseObject(tempStr, UnionMemberDiscount.class);
            return result;
        }
        //(2)db
        EntityWrapper<UnionMemberDiscount> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", discountId)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = this.selectOne(entityWrapper);
        setCache(result, discountId);
        return result;
    }

    //******************************************* Object As a Service - list *******************************************

    @Override
    public List<UnionMemberDiscount> listByFromMemberId(Integer fromMemberId) throws Exception {
        if (fromMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberDiscount> result;
        //(1)get in cache
        String fromMemberIdKey = RedisKeyUtil.getMemberDiscountFromMemberIdKey(fromMemberId);
        if (this.redisCacheUtil.exists(fromMemberIdKey)) {
            String tempStr = this.redisCacheUtil.get(fromMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberDiscount.class);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionMemberDiscount> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_member_id", fromMemberId);
        result = this.selectList(entityWrapper);
        setCache(result, fromMemberId, MemberConstant.REDIS_KEY_DISCOUNT_FROM_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionMemberDiscount> listByToMemberId(Integer toMemberId) throws Exception {
        if (toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberDiscount> result;
        //(1)get in cache
        String toMemberIdKey = RedisKeyUtil.getMemberDiscountToMemberIdKey(toMemberId);
        if (this.redisCacheUtil.exists(toMemberIdKey)) {
            String tempStr = this.redisCacheUtil.get(toMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberDiscount.class);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionMemberDiscount> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("to_member_id", toMemberId);
        result = this.selectList(entityWrapper);
        setCache(result, toMemberId, MemberConstant.REDIS_KEY_DISCOUNT_TO_MEMBER_ID);
        return result;
    }

    //******************************************* Object As a Service - save *******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMemberDiscount newDiscount) throws Exception {
        if (newDiscount == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insert(newDiscount);
        this.removeCache(newDiscount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMemberDiscount> newDiscountList) throws Exception {
        if (newDiscountList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insertBatch(newDiscountList);
        this.removeCache(newDiscountList);
    }

    //******************************************* Object As a Service - remove *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer discountId) throws Exception {
        if (discountId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        UnionMemberDiscount discount = this.getById(discountId);
        removeCache(discount);
        //(2)remove in db logically
        UnionMemberDiscount removeDiscount = new UnionMemberDiscount();
        removeDiscount.setId(discountId);
        removeDiscount.setDelStatus(CommonConstant.DEL_STATUS_YES);
        this.updateById(removeDiscount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> discountIdList) throws Exception {
        if (discountIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<UnionMemberDiscount> discountList = new ArrayList<>();
        for (Integer discountId : discountIdList) {
            UnionMemberDiscount discount = this.getById(discountId);
            discountList.add(discount);
        }
        removeCache(discountList);
        //(2)remove in db logically
        List<UnionMemberDiscount> removeDiscountList = new ArrayList<>();
        for (Integer discountId : discountIdList) {
            UnionMemberDiscount removeDiscount = new UnionMemberDiscount();
            removeDiscount.setId(discountId);
            removeDiscount.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeDiscountList.add(removeDiscount);
        }
        this.updateBatchById(removeDiscountList);
    }

    //******************************************* Object As a Service - update *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMemberDiscount updateDiscount) throws Exception {
        if (updateDiscount == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        Integer discountId = updateDiscount.getId();
        UnionMemberDiscount discount = this.getById(discountId);
        removeCache(discount);
        //(2)update db
        this.updateById(updateDiscount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMemberDiscount> updateDiscountList) throws Exception {
        if (updateDiscountList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<Integer> discountIdList = new ArrayList<>();
        for (UnionMemberDiscount updateDiscount : updateDiscountList) {
            discountIdList.add(updateDiscount.getId());
        }
        List<UnionMemberDiscount> discountList = new ArrayList<>();
        for (Integer discountId : discountIdList) {
            UnionMemberDiscount discount = this.getById(discountId);
            discountList.add(discount);
        }
        removeCache(discountList);
        //(2)update db
        this.updateBatchById(updateDiscountList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - cache support ************************************
     ******************************************************************************************************************/

    private void setCache(UnionMemberDiscount newDiscount, Integer discountId) {
        if (discountId == null) {
            return; //do nothing,just in case
        }
        String discountIdKey = RedisKeyUtil.getMemberDiscountIdKey(discountId);
        this.redisCacheUtil.set(discountIdKey, newDiscount);
    }

    private void setCache(List<UnionMemberDiscount> newDiscountList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            return; //do nothing,just in case
        }
        String foreignIdKey;
        switch (foreignIdType) {
            case MemberConstant.REDIS_KEY_DISCOUNT_FROM_MEMBER_ID:
                foreignIdKey = RedisKeyUtil.getMemberDiscountFromMemberIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newDiscountList);
                break;
            case MemberConstant.REDIS_KEY_DISCOUNT_TO_MEMBER_ID:
                foreignIdKey = RedisKeyUtil.getMemberDiscountToMemberIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newDiscountList);
                break;
            default:
                break;
        }
    }

    private void removeCache(UnionMemberDiscount discount) {
        if (discount == null) {
            return;
        }
        Integer discountId = discount.getId();
        String discountIdKey = RedisKeyUtil.getMemberDiscountIdKey(discountId);
        this.redisCacheUtil.remove(discountIdKey);
        Integer fromMemberId = discount.getFromMemberId();
        if (fromMemberId != null) {
            String fromMemberIdKey = RedisKeyUtil.getMemberDiscountFromMemberIdKey(fromMemberId);
            this.redisCacheUtil.remove(fromMemberIdKey);
        }
        Integer toMemberId = discount.getToMemberId();
        if (toMemberId != null) {
            String toMemberIdKey = RedisKeyUtil.getMemberDiscountToMemberIdKey(toMemberId);
            this.redisCacheUtil.remove(toMemberIdKey);
        }
    }

    private void removeCache(List<UnionMemberDiscount> discountList) {
        if (ListUtil.isEmpty(discountList)) {
            return;
        }
        List<Integer> discountIdList = new ArrayList<>();
        for (UnionMemberDiscount discount : discountList) {
            discountIdList.add(discount.getId());
        }
        List<String> discountIdKeyList = RedisKeyUtil.getMemberDiscountIdKey(discountIdList);
        this.redisCacheUtil.remove(discountIdKeyList);
        List<String> fromMemberIdKeyList = getForeignIdKeyList(discountList, MemberConstant.REDIS_KEY_DISCOUNT_FROM_MEMBER_ID);
        if (ListUtil.isNotEmpty(fromMemberIdKeyList)) {
            this.redisCacheUtil.remove(fromMemberIdKeyList);
        }
        List<String> toMemberIdKeyList = getForeignIdKeyList(discountList, MemberConstant.REDIS_KEY_DISCOUNT_TO_MEMBER_ID);
        if (ListUtil.isNotEmpty(toMemberIdKeyList)) {
            this.redisCacheUtil.remove(toMemberIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionMemberDiscount> discountList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case MemberConstant.REDIS_KEY_DISCOUNT_FROM_MEMBER_ID:
                for (UnionMemberDiscount discount : discountList) {
                    Integer fromMemberId = discount.getFromMemberId();
                    if (fromMemberId != null) {
                        String fromMemberIdKey = RedisKeyUtil.getMemberDiscountFromMemberIdKey(fromMemberId);
                        result.add(fromMemberIdKey);
                    }
                }
                break;
            case MemberConstant.REDIS_KEY_DISCOUNT_TO_MEMBER_ID:
                for (UnionMemberDiscount discount : discountList) {
                    Integer toMemberId = discount.getToMemberId();
                    if (toMemberId != null) {
                        String toMemberIdKey = RedisKeyUtil.getMemberDiscountToMemberIdKey(toMemberId);
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
