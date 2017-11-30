package com.gt.union.union.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.union.main.entity.UnionMainNotice;
import com.gt.union.union.main.mapper.UnionMainNoticeMapper;
import com.gt.union.union.main.service.IUnionMainNoticeService;
import com.gt.union.union.main.util.UnionMainNoticeCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟公告 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 15:18:52
 */
@Service
public class UnionMainNoticeServiceImpl extends ServiceImpl<UnionMainNoticeMapper, UnionMainNotice> implements IUnionMainNoticeService {
    @Autowired
    public RedisCacheUtil redisCacheUtil;

    //***************************************** Domain Driven Design - get *********************************************

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Object As a Service - get **********************************************

    public UnionMainNotice getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMainNotice result;
        // (1)cache
        String idKey = UnionMainNoticeCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionMainNotice.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainNotice> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionMainNotice> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMainNotice> result;
        // (1)cache
        String unionIdKey = UnionMainNoticeCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMainNotice.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMainNotice> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionMainNoticeCacheUtil.TYPE_UNION_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMainNotice newUnionMainNotice) throws Exception {
        if (newUnionMainNotice == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionMainNotice);
        removeCache(newUnionMainNotice);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMainNotice> newUnionMainNoticeList) throws Exception {
        if (newUnionMainNoticeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionMainNoticeList);
        removeCache(newUnionMainNoticeList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionMainNotice unionMainNotice = getById(id);
        removeCache(unionMainNotice);
        // (2)remove in db logically
        UnionMainNotice removeUnionMainNotice = new UnionMainNotice();
        removeUnionMainNotice.setId(id);
        removeUnionMainNotice.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionMainNotice);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMainNotice> unionMainNoticeList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainNotice unionMainNotice = getById(id);
            unionMainNoticeList.add(unionMainNotice);
        }
        removeCache(unionMainNoticeList);
        // (2)remove in db logically
        List<UnionMainNotice> removeUnionMainNoticeList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainNotice removeUnionMainNotice = new UnionMainNotice();
            removeUnionMainNotice.setId(id);
            removeUnionMainNotice.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMainNoticeList.add(removeUnionMainNotice);
        }
        updateBatchById(removeUnionMainNoticeList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMainNotice updateUnionMainNotice) throws Exception {
        if (updateUnionMainNotice == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionMainNotice.getId();
        UnionMainNotice unionMainNotice = getById(id);
        removeCache(unionMainNotice);
        // (2)update db
        updateById(updateUnionMainNotice);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMainNotice> updateUnionMainNoticeList) throws Exception {
        if (updateUnionMainNoticeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionMainNotice updateUnionMainNotice : updateUnionMainNoticeList) {
            idList.add(updateUnionMainNotice.getId());
        }
        List<UnionMainNotice> unionMainNoticeList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMainNotice unionMainNotice = getById(id);
            unionMainNoticeList.add(unionMainNotice);
        }
        removeCache(unionMainNoticeList);
        // (2)update db
        updateBatchById(updateUnionMainNoticeList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionMainNotice newUnionMainNotice, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionMainNoticeCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionMainNotice);
    }

    private void setCache(List<UnionMainNotice> newUnionMainNoticeList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionMainNoticeCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionMainNoticeCacheUtil.getUnionIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMainNoticeList);
        }
    }

    private void removeCache(UnionMainNotice unionMainNotice) {
        if (unionMainNotice == null) {
            return;
        }
        Integer id = unionMainNotice.getId();
        String idKey = UnionMainNoticeCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer unionId = unionMainNotice.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionMainNoticeCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }
    }

    private void removeCache(List<UnionMainNotice> unionMainNoticeList) {
        if (ListUtil.isEmpty(unionMainNoticeList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionMainNotice unionMainNotice : unionMainNoticeList) {
            idList.add(unionMainNotice.getId());
        }
        List<String> idKeyList = UnionMainNoticeCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> unionIdKeyList = getForeignIdKeyList(unionMainNoticeList, UnionMainNoticeCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionMainNotice> unionMainNoticeList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionMainNoticeCacheUtil.TYPE_UNION_ID:
                for (UnionMainNotice unionMainNotice : unionMainNoticeList) {
                    Integer unionId = unionMainNotice.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionMainNoticeCacheUtil.getUnionIdKey(unionId);
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