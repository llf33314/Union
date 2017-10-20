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
import com.gt.union.main.entity.UnionMainDict;
import com.gt.union.main.mapper.UnionMainDictMapper;
import com.gt.union.main.service.IUnionMainDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟设置申请填写信息 服务实现类
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
@Service
public class UnionMainDictServiceImpl extends ServiceImpl<UnionMainDictMapper, UnionMainDict> implements IUnionMainDictService {

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainDict> dictList = this.listByUnionId(unionId);
        if (ListUtil.isNotEmpty(dictList)) {
            List<Integer> dictIdList = new ArrayList<>();
            for (UnionMainDict dict : dictList) {
                dictIdList.add(dict.getId());
            }
            this.removeBatchById(dictIdList);
        }
    }

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    //******************************************* Object As a Service - get ********************************************

    @Override
    public UnionMainDict getById(Integer dictId) throws Exception {
        if (dictId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainDict result;
        //(1)cache
        String dictIdKey = RedisKeyUtil.getDictIdKey(dictId);
        if (this.redisCacheUtil.exists(dictIdKey)) {
            String tempStr = this.redisCacheUtil.get(dictIdKey);
            result = JSONArray.parseObject(tempStr, UnionMainDict.class);
            return result;
        }
        //(2)db
        EntityWrapper<UnionMainDict> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", dictId);
        result = this.selectOne(entityWrapper);
        setCache(result, dictId);
        return result;
    }

    //******************************************* Object As a Service - list *******************************************

    @Override
    public List<UnionMainDict> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainDict> result;
        //(1)get in cache
        String unionIdKey = RedisKeyUtil.getDictUnionIdKey(unionId);
        if (this.redisCacheUtil.exists(unionIdKey)) {
            String tempStr = this.redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainDict.class);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionMainDict> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);
        result = this.selectList(entityWrapper);
        setCache(result, unionId, MainConstant.REDIS_KEY_DICT_UNION_ID);
        return result;
    }

    //******************************************* Object As a Service - save *******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMainDict newDict) throws Exception {
        if (newDict == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insert(newDict);
        this.removeCache(newDict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMainDict> newDictList) throws Exception {
        if (newDictList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insertBatch(newDictList);
        this.removeCache(newDictList);
    }

    //******************************************* Object As a Service - remove *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer dictId) throws Exception {
        if (dictId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        UnionMainDict dict = this.getById(dictId);
        removeCache(dict);
        //(2)remove in db physically, special...
        EntityWrapper<UnionMainDict> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", dictId);
        this.delete(entityWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> dictIdList) throws Exception {
        if (dictIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<UnionMainDict> dictList = new ArrayList<>();
        for (Integer dictId : dictIdList) {
            UnionMainDict dict = this.getById(dictId);
            dictList.add(dict);
        }
        removeCache(dictList);
        //(2)remove in db physically, special...
        EntityWrapper<UnionMainDict> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", dictIdList);
        this.delete(entityWrapper);
    }

    //******************************************* Object As a Service - update *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMainDict updateDict) throws Exception {
        if (updateDict == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        Integer dictId = updateDict.getId();
        UnionMainDict dict = this.getById(dictId);
        removeCache(dict);
        //(2)update db
        this.updateById(updateDict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMainDict> updateDictList) throws Exception {
        if (updateDictList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<Integer> dictIdList = new ArrayList<>();
        for (UnionMainDict updateDict : updateDictList) {
            dictIdList.add(updateDict.getId());
        }
        List<UnionMainDict> dictList = new ArrayList<>();
        for (Integer dictId : dictIdList) {
            UnionMainDict dict = this.getById(dictId);
            dictList.add(dict);
        }
        removeCache(dictList);
        //(2)update db
        this.updateBatchById(updateDictList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionMainDict newDict, Integer dictId) {
        if (dictId == null) {
            return; //do nothing,just in case
        }
        String dictIdKey = RedisKeyUtil.getDictIdKey(dictId);
        this.redisCacheUtil.set(dictIdKey, newDict);
    }

    private void setCache(List<UnionMainDict> newDictList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            return; //do nothing,just in case
        }
        String foreignIdKey;
        switch (foreignIdType) {
            case MainConstant.REDIS_KEY_DICT_UNION_ID:
                foreignIdKey = RedisKeyUtil.getDictUnionIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newDictList);
                break;
            default:
                break;
        }
    }

    private void removeCache(UnionMainDict dict) {
        if (dict == null) {
            return;
        }
        Integer dictId = dict.getId();
        String dictIdKey = RedisKeyUtil.getDictIdKey(dictId);
        this.redisCacheUtil.remove(dictIdKey);
        Integer unionId = dict.getUnionId();
        if (unionId != null) {
            String unionIdKey = RedisKeyUtil.getDictUnionIdKey(unionId);
            this.redisCacheUtil.remove(unionIdKey);
        }
    }

    private void removeCache(List<UnionMainDict> dictList) {
        if (ListUtil.isEmpty(dictList)) {
            return;
        }
        List<Integer> dictIdList = new ArrayList<>();
        for (UnionMainDict dict : dictList) {
            dictIdList.add(dict.getId());
        }
        List<String> dictIdKeyList = RedisKeyUtil.getDictIdKey(dictIdList);
        this.redisCacheUtil.remove(dictIdKeyList);
        List<String> unionIdKeyList = getForeignIdKeyList(dictList, MainConstant.REDIS_KEY_DICT_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            this.redisCacheUtil.remove(unionIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionMainDict> dictList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case MainConstant.REDIS_KEY_DICT_UNION_ID:
                for (UnionMainDict dict : dictList) {
                    Integer unionId = dict.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = RedisKeyUtil.getDictUnionIdKey(unionId);
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
