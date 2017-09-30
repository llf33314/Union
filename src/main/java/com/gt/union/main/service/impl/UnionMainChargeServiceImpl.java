package com.gt.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.RedisKeyUtil;
import com.gt.union.main.constant.MainConstant;
import com.gt.union.main.entity.UnionMainCharge;
import com.gt.union.main.mapper.UnionMainChargeMapper;
import com.gt.union.main.service.IUnionMainChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 联盟升级收费 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionMainChargeServiceImpl extends ServiceImpl<UnionMainChargeMapper, UnionMainCharge> implements IUnionMainChargeService {

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - get *********************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - list ********************************************
     ******************************************************************************************************************/

    @Override
    public List<UnionMainCharge> listByUnionIdAndType(Integer unionId, Integer type) throws Exception {
        if (unionId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainCharge> result = new ArrayList<>();
        List<UnionMainCharge> chargeList = this.listByUnionId(unionId);
        if (ListUtil.isNotEmpty(chargeList)) {
            for (UnionMainCharge charge : chargeList) {
                if (type.equals(charge.getType())) {
                    result.add(charge);
                }
            }
        }
        return result;
    }

    @Override
    public List<UnionMainCharge> listByUnionIdAndTypeAndIsAvailable(Integer unionId, Integer type, Integer isAvailable) throws Exception {
        if (unionId == null || type == null || isAvailable == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainCharge> result = new ArrayList<>();
        List<UnionMainCharge> chargeList = this.listByUnionId(unionId);
        if (ListUtil.isNotEmpty(chargeList)) {
            for (UnionMainCharge charge : chargeList) {
                if (type.equals(charge.getType()) && isAvailable.equals(charge.getIsAvailable())) {
                    result.add(charge);
                }
            }
        }
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - save ********************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - remove ******************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - update ******************************************
     ******************************************************************************************************************/

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
    public UnionMainCharge getById(Integer chargeId) throws Exception {
        if (chargeId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainCharge result;
        //(1)cache
        String chargeIdKey = RedisKeyUtil.getChargeIdKey(chargeId);
        if (this.redisCacheUtil.exists(chargeIdKey)) {
            String tempStr = this.redisCacheUtil.get(chargeIdKey);
            result = JSONArray.parseObject(tempStr, UnionMainCharge.class);
            return result;
        }
        //(2)db
        EntityWrapper<UnionMainCharge> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", chargeId);
        result = this.selectOne(entityWrapper);
        setCache(result, chargeId);
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - list *********************************************
     ******************************************************************************************************************/

    @Override
    public List<UnionMainCharge> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainCharge> result;
        //(1)get in cache
        String unionIdKey = RedisKeyUtil.getChargeUnionIdKey(unionId);
        if (this.redisCacheUtil.exists(unionIdKey)) {
            String tempStr = this.redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainCharge.class);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionMainCharge> entityWrapper = new EntityWrapper();
        entityWrapper.eq("union_id", unionId);
        result = this.selectList(entityWrapper);
        setCache(result, unionId, MainConstant.REDIS_KEY_CHARGE_UNION_ID);
        return result;
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - save *********************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void save(UnionMainCharge newCharge) throws Exception {
        if (newCharge == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insert(newCharge);
        this.removeCache(newCharge);
    }

    @Override
    @Transactional
    public void saveBatch(List<UnionMainCharge> newChargeList) throws Exception {
        if (newChargeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insertBatch(newChargeList);
        this.removeCache(newChargeList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - remove *******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void removeById(Integer chargeId) throws Exception {
        if (chargeId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        UnionMainCharge charge = this.getById(chargeId);
        removeCache(charge);
        //(2)remove in db logically
        UnionMainCharge removeCharge = new UnionMainCharge();
        removeCharge.setId(chargeId);
        removeCharge.setDelStatus(CommonConstant.DEL_STATUS_YES);
        this.updateById(removeCharge);
    }

    @Override
    @Transactional
    public void removeBatchById(List<Integer> chargeIdList) throws Exception {
        if (chargeIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<UnionMainCharge> chargeList = new ArrayList<>();
        for (Integer chargeId : chargeIdList) {
            UnionMainCharge charge = this.getById(chargeId);
            chargeList.add(charge);
        }
        removeCache(chargeList);
        //(2)remove in db logically
        List<UnionMainCharge> removeChargeList = new ArrayList<>();
        for (Integer chargeId : chargeIdList) {
            UnionMainCharge removeCharge = new UnionMainCharge();
            removeCharge.setId(chargeId);
            removeCharge.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeChargeList.add(removeCharge);
        }
        this.updateBatchById(removeChargeList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - update *******************************************
     ******************************************************************************************************************/

    @Override
    @Transactional
    public void update(UnionMainCharge updateCharge) throws Exception {
        if (updateCharge == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        Integer chargeId = updateCharge.getId();
        UnionMainCharge charge = this.getById(chargeId);
        removeCache(charge);
        //(2)update db
        this.updateById(updateCharge);
    }

    @Override
    @Transactional
    public void updateBatch(List<UnionMainCharge> updateChargeList) throws Exception {
        if (updateChargeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<Integer> chargeIdList = new ArrayList<>();
        for (UnionMainCharge updateCharge : updateChargeList) {
            chargeIdList.add(updateCharge.getId());
        }
        List<UnionMainCharge> chargeList = new ArrayList<>();
        for (Integer chargeId : chargeIdList) {
            UnionMainCharge charge = this.getById(chargeId);
            chargeList.add(charge);
        }
        removeCache(chargeList);
        //(2)update db
        this.updateBatchById(updateChargeList);
    }

    /*******************************************************************************************************************
     ****************************************** Object As a Service - cache support ************************************
     ******************************************************************************************************************/

    private void setCache(UnionMainCharge newCharge, Integer chargeId) {
        if (chargeId == null) {
            return; //do nothing,just in case
        }
        String chargeIdKey = RedisKeyUtil.getChargeIdKey(chargeId);
        this.redisCacheUtil.set(chargeIdKey, newCharge);
    }

    private void setCache(List<UnionMainCharge> newChargeList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            return; //do nothing,just in case
        }
        String foreignIdKey;
        switch (foreignIdType) {
            case MainConstant.REDIS_KEY_CHARGE_UNION_ID:
                foreignIdKey = RedisKeyUtil.getChargeUnionIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newChargeList);
                break;
            default:
                break;
        }
    }

    private void removeCache(UnionMainCharge charge) {
        if (charge == null) {
            return;
        }
        Integer chargeId = charge.getId();
        String chargeIdKey = RedisKeyUtil.getChargeIdKey(chargeId);
        this.redisCacheUtil.remove(chargeIdKey);
        Integer unionId = charge.getUnionId();
        if (unionId != null) {
            String unionIdKey = RedisKeyUtil.getChargeUnionIdKey(unionId);
            this.redisCacheUtil.remove(unionIdKey);
        }
    }

    private void removeCache(List<UnionMainCharge> chargeList) {
        if (ListUtil.isEmpty(chargeList)) {
            return;
        }
        List<Integer> chargeIdList = new ArrayList<>();
        for (UnionMainCharge charge : chargeList) {
            chargeIdList.add(charge.getId());
        }
        List<String> chargeIdKeyList = RedisKeyUtil.getChargeIdKey(chargeIdList);
        this.redisCacheUtil.remove(chargeIdKeyList);
        List<String> unionIdKeyList = getForeignIdKeyList(chargeList, MainConstant.REDIS_KEY_CHARGE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            this.redisCacheUtil.remove(unionIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionMainCharge> chargeList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case MainConstant.REDIS_KEY_CHARGE_UNION_ID:
                for (UnionMainCharge charge : chargeList) {
                    Integer unionId = charge.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = RedisKeyUtil.getChargeUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }

}
