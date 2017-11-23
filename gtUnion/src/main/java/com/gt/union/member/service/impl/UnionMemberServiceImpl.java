package com.gt.union.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.mapper.UnionMemberMapper;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.member.util.UnionMemberCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 盟员 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 10:28:35
 */
@Service
public class UnionMemberServiceImpl extends ServiceImpl<UnionMemberMapper, UnionMember> implements IUnionMemberService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;
    
    //***************************************** Domain Driven Design - get *********************************************

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Object As a Service - get **********************************************

    private UnionMember getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMember result;
        // (1)cache
        String idKey = UnionMemberCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionMember.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionMember> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> result;
        // (1)cache
        String unionIdKey = UnionMemberCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMember.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionMemberCacheUtil.TYPE_UNION_ID);
        return result;
    }
    
    public List<UnionMember> listByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> result;
        // (1)cache
        String busIdKey = UnionMemberCacheUtil.getBusIdKey(busId);
        if (redisCacheUtil.exists(busIdKey)) {
            String tempStr = redisCacheUtil.get(busIdKey);
            result = JSONArray.parseArray(tempStr, UnionMember.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("bus_id", busId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, busId, UnionMemberCacheUtil.TYPE_BUS_ID);
        return result;
    }
    
    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMember newUnionMember) throws Exception {
        if (newUnionMember == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionMember);
        removeCache(newUnionMember);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMember> newUnionMemberList) throws Exception {
        if (newUnionMemberList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionMemberList);
        removeCache(newUnionMemberList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionMember unionMember = getById(id);
        removeCache(unionMember);
        // (2)remove in db logically
        UnionMember removeUnionMember = new UnionMember();
        removeUnionMember.setId(id);
        removeUnionMember.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionMember);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMember> unionMemberList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMember unionMember = getById(id);
            unionMemberList.add(unionMember);
        }
        removeCache(unionMemberList);
        // (2)remove in db logically
        List<UnionMember> removeUnionMemberList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMember removeUnionMember = new UnionMember();
            removeUnionMember.setId(id);
            removeUnionMember.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMemberList.add(removeUnionMember);
        }
        updateBatchById(removeUnionMemberList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMember updateUnionMember) throws Exception {
        if (updateUnionMember == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer unionMemberId = updateUnionMember.getId();
        UnionMember unionMember = getById(unionMemberId);
        removeCache(unionMember);
        // (2)update db
        updateById(updateUnionMember);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMember> updateUnionMemberList) throws Exception {
        if (updateUnionMemberList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> unionMemberIdList = new ArrayList<>();
        for (UnionMember updateUnionMember : updateUnionMemberList) {
            unionMemberIdList.add(updateUnionMember.getId());
        }
        List<UnionMember> unionMemberList = new ArrayList<>();
        for (Integer unionMemberId : unionMemberIdList) {
            UnionMember unionMember = getById(unionMemberId);
            unionMemberList.add(unionMember);
        }
        removeCache(unionMemberList);
        // (2)update db
        updateBatchById(updateUnionMemberList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionMember newUnionMember, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String unionMemberIdKey = UnionMemberCacheUtil.getIdKey(id);
        redisCacheUtil.set(unionMemberIdKey, newUnionMember);
    }

    private void setCache(List<UnionMember> newUnionMemberList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return; 
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionMemberCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionMemberCacheUtil.getUnionIdKey(foreignId);
                break;
            case UnionMemberCacheUtil.TYPE_BUS_ID:
                foreignIdKey = UnionMemberCacheUtil.getBusIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMemberList);
        }
    }

    private void removeCache(UnionMember unionMember) {
        if (unionMember == null) {
            return;
        }
        Integer id = unionMember.getId();
        String idKey = UnionMemberCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);
        
        Integer unionId = unionMember.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionMemberCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }
        
        Integer busId = unionMember.getBusId();
        if (busId != null) {
            String busIdKey = UnionMemberCacheUtil.getBusIdKey(busId);
            redisCacheUtil.remove(busIdKey);
        }
    }

    private void removeCache(List<UnionMember> unionMemberList) {
        if (ListUtil.isEmpty(unionMemberList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionMember unionMember : unionMemberList) {
            idList.add(unionMember.getId());
        }
        List<String> idKeyList = UnionMemberCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);
        
        List<String> unionIdKeyList = getForeignIdKeyList(unionMemberList, UnionMemberCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }
        
        List<String> busIdKeyList = getForeignIdKeyList(unionMemberList, UnionMemberCacheUtil.TYPE_BUS_ID);
        if (ListUtil.isNotEmpty(busIdKeyList)) {
            redisCacheUtil.remove(busIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionMember> unionMemberList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionMemberCacheUtil.TYPE_UNION_ID:
                for (UnionMember unionMember : unionMemberList) {
                    Integer unionId = unionMember.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionMemberCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            case UnionMemberCacheUtil.TYPE_BUS_ID:
                for (UnionMember unionMember : unionMemberList) {
                    Integer busId = unionMember.getBusId();
                    if (busId != null) {
                        String busIdKey = UnionMemberCacheUtil.getBusIdKey(busId);
                        result.add(busIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}