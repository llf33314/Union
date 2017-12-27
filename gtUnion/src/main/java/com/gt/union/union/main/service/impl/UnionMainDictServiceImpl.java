package com.gt.union.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.union.main.dao.IUnionMainDictDao;
import com.gt.union.union.main.entity.UnionMainDict;
import com.gt.union.union.main.service.IUnionMainDictService;
import com.gt.union.union.main.util.UnionMainDictCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟入盟申请必填信息 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
 */
@Service
public class UnionMainDictServiceImpl implements IUnionMainDictService {
    @Autowired
    private IUnionMainDictDao unionMainDictDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<String> listItemKeyByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<String> result = new ArrayList<>();
        List<UnionMainDict> itemList = listValidByUnionId(unionId);
        if (ListUtil.isNotEmpty(itemList)) {
            for (UnionMainDict item : itemList) {
                result.add(item.getItemKey());
            }
        }

        return result;
    }

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionMainDict> filterByDelStatus(List<UnionMainDict> unionMainDictList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainDict> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMainDictList)) {
            for (UnionMainDict unionMainDict : unionMainDictList) {
                if (delStatus.equals(unionMainDict.getDelStatus())) {
                    result.add(unionMainDict);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionMainDict getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainDict result;
        // (1)cache
        String idKey = UnionMainDictCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionMainDict.class);
            return result;
        }
        // (2)db
        result = unionMainDictDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionMainDict getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainDict result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionMainDict getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainDict result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionMainDict> unionMainDictList) throws Exception {
        if (unionMainDictList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMainDictList)) {
            for (UnionMainDict unionMainDict : unionMainDictList) {
                result.add(unionMainDict.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionMainDict> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainDict> result;
        // (1)cache
        String unionIdKey = UnionMainDictCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainDict.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainDict> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);
        result = unionMainDictDao.selectList(entityWrapper);
        setCache(result, unionId, UnionMainDictCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMainDict> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainDict> result;
        // (1)cache
        String validUnionIdKey = UnionMainDictCacheUtil.getValidUnionIdKey(unionId);
        if (redisCacheUtil.exists(validUnionIdKey)) {
            String tempStr = redisCacheUtil.get(validUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainDict.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainDict> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);
        result = unionMainDictDao.selectList(entityWrapper);
        setValidCache(result, unionId, UnionMainDictCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMainDict> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainDict> result;
        // (1)cache
        String invalidUnionIdKey = UnionMainDictCacheUtil.getInvalidUnionIdKey(unionId);
        if (redisCacheUtil.exists(invalidUnionIdKey)) {
            String tempStr = redisCacheUtil.get(invalidUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainDict.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainDict> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);
        result = unionMainDictDao.selectList(entityWrapper);
        setInvalidCache(result, unionId, UnionMainDictCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMainDict> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMainDict> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page<UnionMainDict> pageSupport(Page page, EntityWrapper<UnionMainDict> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionMainDictDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMainDict newUnionMainDict) throws Exception {
        if (newUnionMainDict == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionMainDictDao.insert(newUnionMainDict);
        removeCache(newUnionMainDict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMainDict> newUnionMainDictList) throws Exception {
        if (newUnionMainDictList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionMainDictDao.insertBatch(newUnionMainDictList);
        removeCache(newUnionMainDictList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionMainDict unionMainDict = getById(id);
        removeCache(unionMainDict);
        // (2)remove in db logically
        UnionMainDict removeUnionMainDict = new UnionMainDict();
        removeUnionMainDict.setId(id);
        removeUnionMainDict.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionMainDictDao.updateById(removeUnionMainDict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMainDict> unionMainDictList = listByIdList(idList);
        removeCache(unionMainDictList);
        // (2)remove in db logically
        List<UnionMainDict> removeUnionMainDictList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainDict removeUnionMainDict = new UnionMainDict();
            removeUnionMainDict.setId(id);
            removeUnionMainDict.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMainDictList.add(removeUnionMainDict);
        }
        unionMainDictDao.updateBatchById(removeUnionMainDictList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMainDict updateUnionMainDict) throws Exception {
        if (updateUnionMainDict == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionMainDict.getId();
        UnionMainDict unionMainDict = getById(id);
        removeCache(unionMainDict);
        // (2)update db
        unionMainDictDao.updateById(updateUnionMainDict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMainDict> updateUnionMainDictList) throws Exception {
        if (updateUnionMainDictList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionMainDictList);
        List<UnionMainDict> unionMainDictList = listByIdList(idList);
        removeCache(unionMainDictList);
        // (2)update db
        unionMainDictDao.updateBatchById(updateUnionMainDictList);
    }

    //********************************************* Object As a Service - cache support ********************************

    private void setCache(UnionMainDict newUnionMainDict, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionMainDictCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionMainDict);
    }

    private void setCache(List<UnionMainDict> newUnionMainDictList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionMainDictCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionMainDictCacheUtil.getUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMainDictList);
        }
    }

    private void setValidCache(List<UnionMainDict> newUnionMainDictList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionMainDictCacheUtil.TYPE_UNION_ID:
                validForeignIdKey = UnionMainDictCacheUtil.getValidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionMainDictList);
        }
    }

    private void setInvalidCache(List<UnionMainDict> newUnionMainDictList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionMainDictCacheUtil.TYPE_UNION_ID:
                invalidForeignIdKey = UnionMainDictCacheUtil.getInvalidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionMainDictList);
        }
    }

    private void removeCache(UnionMainDict unionMainDict) {
        if (unionMainDict == null) {
            return;
        }
        Integer id = unionMainDict.getId();
        String idKey = UnionMainDictCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer unionId = unionMainDict.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionMainDictCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);

            String validUnionIdKey = UnionMainDictCacheUtil.getValidUnionIdKey(unionId);
            redisCacheUtil.remove(validUnionIdKey);

            String invalidUnionIdKey = UnionMainDictCacheUtil.getInvalidUnionIdKey(unionId);
            redisCacheUtil.remove(invalidUnionIdKey);
        }

    }

    private void removeCache(List<UnionMainDict> unionMainDictList) {
        if (ListUtil.isEmpty(unionMainDictList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionMainDict unionMainDict : unionMainDictList) {
            idList.add(unionMainDict.getId());
        }
        List<String> idKeyList = UnionMainDictCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> unionIdKeyList = getForeignIdKeyList(unionMainDictList, UnionMainDictCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

    }

    private List<String> getForeignIdKeyList(List<UnionMainDict> unionMainDictList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionMainDictCacheUtil.TYPE_UNION_ID:
                for (UnionMainDict unionMainDict : unionMainDictList) {
                    Integer unionId = unionMainDict.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionMainDictCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);

                        String validUnionIdKey = UnionMainDictCacheUtil.getValidUnionIdKey(unionId);
                        result.add(validUnionIdKey);

                        String invalidUnionIdKey = UnionMainDictCacheUtil.getInvalidUnionIdKey(unionId);
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