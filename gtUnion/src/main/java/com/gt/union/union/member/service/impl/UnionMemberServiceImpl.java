package com.gt.union.union.member.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.constant.MemberConstant;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.mapper.UnionMemberMapper;
import com.gt.union.union.member.service.IUnionMemberService;
import com.gt.union.union.member.util.UnionMemberCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    @Autowired
    private IUnionMainService unionMainService;

    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public UnionMember getReadByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = listReadByBusId(busId);
        result = filterByUnionId(result, unionId);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    @Override
    public UnionMember getWriteByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = listWriteByBusId(busId);
        result = filterByUnionId(result, unionId);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    @Override
    public UnionMember getOwnerByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = listByUnionId(unionId);
        result = filterByIsUnionOwner(result, MemberConstant.IS_UNION_OWNER_YES);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    @Override
    public UnionMember getOwnerByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = listByBusId(busId);
        result = filterByIsUnionOwner(result, MemberConstant.IS_UNION_OWNER_YES);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    @Override
    public UnionMember getByIdAndBusIdAndUnionId(Integer memberId, Integer busId, Integer unionId) throws Exception {
        if (busId == null || memberId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）	判断union有效性和member读权限
        if (unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember busMember = getReadByBusIdAndUnionId(busId, unionId);
        if (busMember == null) {
            throw new BusinessException(CommonConstant.UNION_READ_REJECT);
        }

        // （2）	判断memberId有效性
        return getByIdAndUnionId(memberId, unionId);
    }

    @Override
    public UnionMember getByIdAndUnionId(Integer memberId, Integer unionId) throws Exception {
        if (memberId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMember result = getById(memberId);

        return unionId.equals(result.getUnionId()) ? result : null;
    }

    //***************************************** Domain Driven Design - list ********************************************


    @Override
    public List<UnionMember> listReadByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> busMemberList = listByBusId(busId);
        List<Integer> statusList = new ArrayList<>();
        statusList.add(MemberConstant.STATUS_IN);
        statusList.add(MemberConstant.STATUS_APPLY_OUT);
        statusList.add(MemberConstant.STATUS_OUT_PERIOD);

        return filterByStatus(busMemberList, statusList);
    }

    @Override
    public List<UnionMember> listWriteByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> busMemberList = listByBusId(busId);
        List<Integer> statusList = new ArrayList<>();
        statusList.add(MemberConstant.STATUS_IN);
        statusList.add(MemberConstant.STATUS_APPLY_OUT);

        return filterByStatus(busMemberList, statusList);
    }

    @Override
    public List<UnionMember> listReadByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> unionMemberList = listByUnionId(unionId);
        List<Integer> statusList = new ArrayList<>();
        statusList.add(MemberConstant.STATUS_IN);
        statusList.add(MemberConstant.STATUS_APPLY_OUT);
        statusList.add(MemberConstant.STATUS_OUT_PERIOD);

        return filterByStatus(unionMemberList, statusList);
    }

    @Override
    public List<UnionMember> listWriteByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> unionMemberList = listByUnionId(unionId);
        List<Integer> statusList = new ArrayList<>();
        statusList.add(MemberConstant.STATUS_IN);
        statusList.add(MemberConstant.STATUS_APPLY_OUT);

        return filterByStatus(unionMemberList, statusList);
    }

    @Override
    public List<UnionMember> listWriteByBusIdAndUnionId(Integer busId, Integer unionId, String optMemberName) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）判断union有效性和member读权限
        if (unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        final UnionMember member = getReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_READ_REJECT);
        }

        // （2）	获取union下所有入盟状态和退盟申请状态的member
        List<UnionMember> result = listWriteByUnionId(unionId);

        // （3）	如果memberName不为空，则过滤member
        if (StringUtil.isNotEmpty(optMemberName)) {
            List<UnionMember> tempMemberList = new ArrayList<>();
            for (UnionMember tempMember : result) {
                if (tempMember.getEnterpriseName().contains(optMemberName)) {
                    tempMemberList.add(tempMember);
                }
            }
            result = tempMemberList;
        }

        // （4）	按盟主>商家盟员>其他盟员，其他盟员按时间顺序排序
        Collections.sort(result, new Comparator<UnionMember>() {
            @Override
            public int compare(UnionMember o1, UnionMember o2) {
                if (o1.getIsUnionOwner() == MemberConstant.IS_UNION_OWNER_YES) {
                    return 1;
                }
                if (o2.getIsUnionOwner() == MemberConstant.IS_UNION_OWNER_YES) {
                    return -1;
                }
                if (o1.getId().equals(member.getId())) {
                    return 1;
                }
                if (o2.getId().equals(member.getId())) {
                    return -1;
                }
                return o2.getCreateTime().compareTo(o1.getCreateTime());
            }
        });

        return result;
    }

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    @Override
    public List<UnionMember> filterByIsUnionOwner(List<UnionMember> memberList, Integer isUnionOwner) throws Exception {
        if (memberList == null || isUnionOwner == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = new ArrayList<>();
        for (UnionMember member : memberList) {
            if (isUnionOwner.equals(member.getIsUnionOwner())) {
                result.add(member);
            }
        }

        return result;
    }

    @Override
    public List<UnionMember> filterByStatus(List<UnionMember> memberList, List<Integer> statusList) throws Exception {
        if (memberList == null || statusList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = new ArrayList<>();
        for (UnionMember member : memberList) {
            if (statusList.contains(member.getStatus())) {
                result.add(member);
            }
        }

        return result;
    }

    @Override
    public List<UnionMember> filterByUnionId(List<UnionMember> memberList, Integer unionId) throws Exception {
        if (memberList == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = new ArrayList<>();
        for (UnionMember member : memberList) {
            if (unionId.equals(member.getUnionId())) {
                result.add(member);
            }
        }

        return result;
    }

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
    @Override
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