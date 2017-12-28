package com.gt.union.card.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.main.dao.IUnionCardIntegralDao;
import com.gt.union.card.main.entity.UnionCardIntegral;
import com.gt.union.card.main.service.IUnionCardIntegralService;
import com.gt.union.card.main.util.UnionCardIntegralCacheUtil;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 联盟积分 服务实现类
 *
 * @author linweicong
 * @version 2017-09-01 11:34:16
 */
@Service
public class UnionCardIntegralServiceImpl implements IUnionCardIntegralService {
    @Autowired
    private IUnionCardIntegralDao unionCardIntegralDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************


    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionCardIntegral> filterByDelStatus(List<UnionCardIntegral> unionCardIntegralList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardIntegral> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardIntegralList)) {
            for (UnionCardIntegral unionCardIntegral : unionCardIntegralList) {
                if (delStatus.equals(unionCardIntegral.getDelStatus())) {
                    result.add(unionCardIntegral);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionCardIntegral getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardIntegral result;
        // (1)cache
        String idKey = UnionCardIntegralCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionCardIntegral.class);
            return result;
        }
        // (2)db
        result = unionCardIntegralDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionCardIntegral getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardIntegral result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionCardIntegral getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardIntegral result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionCardIntegral> unionCardIntegralList) throws Exception {
        if (unionCardIntegralList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardIntegralList)) {
            for (UnionCardIntegral unionCardIntegral : unionCardIntegralList) {
                result.add(unionCardIntegral.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionCardIntegral> listByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardIntegral> result;
        // (1)cache
        String fanIdKey = UnionCardIntegralCacheUtil.getFanIdKey(fanId);
        if (redisCacheUtil.exists(fanIdKey)) {
            String tempStr = redisCacheUtil.get(fanIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardIntegral.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardIntegral> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("fan_id", fanId);
        result = unionCardIntegralDao.selectList(entityWrapper);
        setCache(result, fanId, UnionCardIntegralCacheUtil.TYPE_FAN_ID);
        return result;
    }

    @Override
    public List<UnionCardIntegral> listValidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardIntegral> result;
        // (1)cache
        String validFanIdKey = UnionCardIntegralCacheUtil.getValidFanIdKey(fanId);
        if (redisCacheUtil.exists(validFanIdKey)) {
            String tempStr = redisCacheUtil.get(validFanIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardIntegral.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardIntegral> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("fan_id", fanId);
        result = unionCardIntegralDao.selectList(entityWrapper);
        setValidCache(result, fanId, UnionCardIntegralCacheUtil.TYPE_FAN_ID);
        return result;
    }

    @Override
    public List<UnionCardIntegral> listInvalidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardIntegral> result;
        // (1)cache
        String invalidFanIdKey = UnionCardIntegralCacheUtil.getInvalidFanIdKey(fanId);
        if (redisCacheUtil.exists(invalidFanIdKey)) {
            String tempStr = redisCacheUtil.get(invalidFanIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardIntegral.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardIntegral> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("fan_id", fanId);
        result = unionCardIntegralDao.selectList(entityWrapper);
        setInvalidCache(result, fanId, UnionCardIntegralCacheUtil.TYPE_FAN_ID);
        return result;
    }

    @Override
    public List<UnionCardIntegral> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardIntegral> result;
        // (1)cache
        String unionIdKey = UnionCardIntegralCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardIntegral.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardIntegral> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);
        result = unionCardIntegralDao.selectList(entityWrapper);
        setCache(result, unionId, UnionCardIntegralCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionCardIntegral> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardIntegral> result;
        // (1)cache
        String validUnionIdKey = UnionCardIntegralCacheUtil.getValidUnionIdKey(unionId);
        if (redisCacheUtil.exists(validUnionIdKey)) {
            String tempStr = redisCacheUtil.get(validUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardIntegral.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardIntegral> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);
        result = unionCardIntegralDao.selectList(entityWrapper);
        setValidCache(result, unionId, UnionCardIntegralCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionCardIntegral> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCardIntegral> result;
        // (1)cache
        String invalidUnionIdKey = UnionCardIntegralCacheUtil.getInvalidUnionIdKey(unionId);
        if (redisCacheUtil.exists(invalidUnionIdKey)) {
            String tempStr = redisCacheUtil.get(invalidUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCardIntegral.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardIntegral> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);
        result = unionCardIntegralDao.selectList(entityWrapper);
        setInvalidCache(result, unionId, UnionCardIntegralCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionCardIntegral> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardIntegral> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionCardIntegral> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardIntegralDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardIntegral newUnionCardIntegral) throws Exception {
        if (newUnionCardIntegral == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionCardIntegralDao.insert(newUnionCardIntegral);
        removeCache(newUnionCardIntegral);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardIntegral> newUnionCardIntegralList) throws Exception {
        if (newUnionCardIntegralList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionCardIntegralDao.insertBatch(newUnionCardIntegralList);
        removeCache(newUnionCardIntegralList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionCardIntegral unionCardIntegral = getById(id);
        removeCache(unionCardIntegral);
        // (2)remove in db logically
        UnionCardIntegral removeUnionCardIntegral = new UnionCardIntegral();
        removeUnionCardIntegral.setId(id);
        removeUnionCardIntegral.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionCardIntegralDao.updateById(removeUnionCardIntegral);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardIntegral> unionCardIntegralList = listByIdList(idList);
        removeCache(unionCardIntegralList);
        // (2)remove in db logically
        List<UnionCardIntegral> removeUnionCardIntegralList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardIntegral removeUnionCardIntegral = new UnionCardIntegral();
            removeUnionCardIntegral.setId(id);
            removeUnionCardIntegral.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardIntegralList.add(removeUnionCardIntegral);
        }
        unionCardIntegralDao.updateBatchById(removeUnionCardIntegralList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardIntegral updateUnionCardIntegral) throws Exception {
        if (updateUnionCardIntegral == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCardIntegral.getId();
        UnionCardIntegral unionCardIntegral = getById(id);
        removeCache(unionCardIntegral);
        // (2)update db
        unionCardIntegralDao.updateById(updateUnionCardIntegral);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardIntegral> updateUnionCardIntegralList) throws Exception {
        if (updateUnionCardIntegralList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionCardIntegralList);
        List<UnionCardIntegral> unionCardIntegralList = listByIdList(idList);
        removeCache(unionCardIntegralList);
        // (2)update db
        unionCardIntegralDao.updateBatchById(updateUnionCardIntegralList);
    }

    //********************************************* Object As a Service - cache support ********************************

    private void setCache(UnionCardIntegral newUnionCardIntegral, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardIntegralCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCardIntegral);
    }

    private void setCache(List<UnionCardIntegral> newUnionCardIntegralList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionCardIntegralCacheUtil.TYPE_FAN_ID:
                foreignIdKey = UnionCardIntegralCacheUtil.getFanIdKey(foreignId);
                break;
            case UnionCardIntegralCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionCardIntegralCacheUtil.getUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionCardIntegralList);
        }
    }

    private void setValidCache(List<UnionCardIntegral> newUnionCardIntegralList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionCardIntegralCacheUtil.TYPE_FAN_ID:
                validForeignIdKey = UnionCardIntegralCacheUtil.getValidFanIdKey(foreignId);
                break;
            case UnionCardIntegralCacheUtil.TYPE_UNION_ID:
                validForeignIdKey = UnionCardIntegralCacheUtil.getValidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionCardIntegralList);
        }
    }

    private void setInvalidCache(List<UnionCardIntegral> newUnionCardIntegralList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionCardIntegralCacheUtil.TYPE_FAN_ID:
                invalidForeignIdKey = UnionCardIntegralCacheUtil.getInvalidFanIdKey(foreignId);
                break;
            case UnionCardIntegralCacheUtil.TYPE_UNION_ID:
                invalidForeignIdKey = UnionCardIntegralCacheUtil.getInvalidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionCardIntegralList);
        }
    }

    private void removeCache(UnionCardIntegral unionCardIntegral) {
        if (unionCardIntegral == null) {
            return;
        }
        Integer id = unionCardIntegral.getId();
        String idKey = UnionCardIntegralCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer fanId = unionCardIntegral.getFanId();
        if (fanId != null) {
            String fanIdKey = UnionCardIntegralCacheUtil.getFanIdKey(fanId);
            redisCacheUtil.remove(fanIdKey);

            String validFanIdKey = UnionCardIntegralCacheUtil.getValidFanIdKey(fanId);
            redisCacheUtil.remove(validFanIdKey);

            String invalidFanIdKey = UnionCardIntegralCacheUtil.getInvalidFanIdKey(fanId);
            redisCacheUtil.remove(invalidFanIdKey);
        }

        Integer unionId = unionCardIntegral.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionCardIntegralCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);

            String validUnionIdKey = UnionCardIntegralCacheUtil.getValidUnionIdKey(unionId);
            redisCacheUtil.remove(validUnionIdKey);

            String invalidUnionIdKey = UnionCardIntegralCacheUtil.getInvalidUnionIdKey(unionId);
            redisCacheUtil.remove(invalidUnionIdKey);
        }

    }

    private void removeCache(List<UnionCardIntegral> unionCardIntegralList) {
        if (ListUtil.isEmpty(unionCardIntegralList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCardIntegral unionCardIntegral : unionCardIntegralList) {
            idList.add(unionCardIntegral.getId());
        }
        List<String> idKeyList = UnionCardIntegralCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> fanIdKeyList = getForeignIdKeyList(unionCardIntegralList, UnionCardIntegralCacheUtil.TYPE_FAN_ID);
        if (ListUtil.isNotEmpty(fanIdKeyList)) {
            redisCacheUtil.remove(fanIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionCardIntegralList, UnionCardIntegralCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

    }

    private List<String> getForeignIdKeyList(List<UnionCardIntegral> unionCardIntegralList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionCardIntegralCacheUtil.TYPE_FAN_ID:
                for (UnionCardIntegral unionCardIntegral : unionCardIntegralList) {
                    Integer fanId = unionCardIntegral.getFanId();
                    if (fanId != null) {
                        String fanIdKey = UnionCardIntegralCacheUtil.getFanIdKey(fanId);
                        result.add(fanIdKey);

                        String validFanIdKey = UnionCardIntegralCacheUtil.getValidFanIdKey(fanId);
                        result.add(validFanIdKey);

                        String invalidFanIdKey = UnionCardIntegralCacheUtil.getInvalidFanIdKey(fanId);
                        result.add(invalidFanIdKey);
                    }
                }
                break;
            case UnionCardIntegralCacheUtil.TYPE_UNION_ID:
                for (UnionCardIntegral unionCardIntegral : unionCardIntegralList) {
                    Integer unionId = unionCardIntegral.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionCardIntegralCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);

                        String validUnionIdKey = UnionCardIntegralCacheUtil.getValidUnionIdKey(unionId);
                        result.add(validUnionIdKey);

                        String invalidUnionIdKey = UnionCardIntegralCacheUtil.getInvalidUnionIdKey(unionId);
                        result.add(invalidUnionIdKey);
                    }
                }
                break;

            default:
                break;
        }
        return result;
    }

    // TODO

    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public UnionCardIntegral getByUnionIdAndFanId(Integer unionId, Integer fanId) throws Exception {
        if (unionId == null || fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardIntegral> result = listByUnionIdAndFanId(unionId, fanId);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<UnionCardIntegral> listByUnionIdAndFanId(Integer unionId, Integer fanId) throws Exception {
        if (unionId == null || fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardIntegral> result = listByFanId(fanId);
        result = filterByUnionId(result, unionId);

        return result;
    }

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    @Override
    public Double countIntegralByUnionIdAndFanId(Integer unionId, Integer fanId) throws Exception {
        if (unionId == null || fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        BigDecimal result = BigDecimal.ZERO;
        List<UnionCardIntegral> integralList = listByUnionIdAndFanId(unionId, fanId);
        if (ListUtil.isNotEmpty(integralList)) {
            for (UnionCardIntegral integral : integralList) {
                result = BigDecimalUtil.add(result, integral.getIntegral());
            }
        }

        return result.doubleValue();
    }

    @Override
    public Double sumIntegralByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        BigDecimal result = BigDecimal.ZERO;
        List<UnionCardIntegral> integralList = listByFanId(fanId);
        if (ListUtil.isNotEmpty(integralList)) {
            for (UnionCardIntegral integral : integralList) {
                result = BigDecimalUtil.add(result, integral.getIntegral());
            }
        }

        return result.doubleValue();
    }

    @Override
    public Double sumIntegralByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        BigDecimal result = BigDecimal.ZERO;
        List<UnionCardIntegral> integralList = listByUnionId(unionId);
        if (ListUtil.isNotEmpty(integralList)) {
            for (UnionCardIntegral integral : integralList) {
                result = BigDecimalUtil.add(result, integral.getIntegral());
            }
        }

        return result.doubleValue();
    }


    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    @Override
    public List<UnionCardIntegral> filterByUnionId(List<UnionCardIntegral> integralList, Integer unionId) throws Exception {
        if (integralList == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardIntegral> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(integralList)) {
            for (UnionCardIntegral integral : integralList) {
                if (unionId.equals(integral.getUnionId())) {
                    result.add(integral);
                }
            }
        }

        return result;
    }

}