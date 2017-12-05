package com.gt.union.union.member.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.constant.MemberConstant;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.entity.UnionMemberOut;
import com.gt.union.union.member.mapper.UnionMemberOutMapper;
import com.gt.union.union.member.service.IUnionMemberOutService;
import com.gt.union.union.member.service.IUnionMemberService;
import com.gt.union.union.member.util.UnionMemberOutCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 退盟申请 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 11:45:12
 */
@Service
public class UnionMemberOutServiceImpl extends ServiceImpl<UnionMemberOutMapper, UnionMemberOut> implements IUnionMemberOutService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public UnionMemberOut getByApplyMemberIdAndUnionId(Integer applyMemberId, Integer unionId) throws Exception {
        if (applyMemberId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMemberOut> result = listByApplyMemberId(applyMemberId);
        result = filterByUnionId(result, unionId);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveByBusIdAndUnionIdAndApplyMemberId(Integer busId, Integer unionId, Integer applyMemberId) throws Exception {
        if (busId == null || unionId == null || applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）	判断union有效性和member写权限、盟主权限
        if (unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        if (member.getIsUnionOwner() != MemberConstant.IS_UNION_OWNER_YES) {
            throw new BusinessException(CommonConstant.UNION_NEED_OWNER);
        }

        // （2）	判断applyMemberId有效性
        UnionMember applyMember = unionMemberService.getByIdAndUnionId(applyMemberId, unionId);
        if (applyMember == null) {
            throw new BusinessException("要移出的盟员不存在");
        }

        // （3）	判断applyMember状态，如果是入盟状态，则新增退盟申请并通过，并更新applyMember为退盟过渡期状态；
        // 如果是申请退盟状态，则判断退盟申请是否存在，若不存在则新增，并自动通过退盟申请，且更新applyMember为退盟过渡期状态；
        // 如果是退盟过渡期状态，则直接返回成功；
        // 如果是其他状态，报错
        boolean isSave = false;
        boolean isUpdate = false;
        UnionMemberOut existOut = null;
        switch (applyMember.getStatus()) {
            case MemberConstant.STATUS_IN:
                isSave = true;
                break;
            case MemberConstant.STATUS_APPLY_OUT:
                existOut = getByApplyMemberIdAndUnionId(applyMemberId, unionId);
                if (existOut == null) {
                    isSave = true;
                } else {
                    isUpdate = true;
                }
                break;
            case MemberConstant.STATUS_OUT_PERIOD:
                break;
            default:
                throw new BusinessException("盟员状态异常");
        }

        if (isSave) {
            UnionMemberOut saveOut = new UnionMemberOut();
            saveOut.setUnionId(unionId);
            saveOut.setApplyMemberId(applyMemberId);
            saveOut.setType(MemberConstant.OUT_TYPE_REMOVE);
            saveOut.setApplyOutReason("盟主移出");
            Date currentDate = DateUtil.getCurrentDate();
            saveOut.setCreateTime(currentDate);
            saveOut.setConfirmOutTime(currentDate);
            saveOut.setActualOutTime(DateUtil.addDays(currentDate, 15));
            saveOut.setDelStatus(CommonConstant.DEL_STATUS_NO);
            save(saveOut);

            UnionMember updateMember = new UnionMember();
            updateMember.setId(applyMemberId);
            updateMember.setStatus(MemberConstant.STATUS_OUT_PERIOD);
            unionMemberService.update(updateMember);
        } else if (isUpdate) {
            UnionMemberOut updateOut = new UnionMemberOut();
            updateOut.setId(existOut.getId());
            Date currentDate = DateUtil.getCurrentDate();
            updateOut.setConfirmOutTime(currentDate);
            updateOut.setActualOutTime(DateUtil.addDays(currentDate, 15));
            update(updateOut);

            UnionMember updateMember = new UnionMember();
            updateMember.setId(applyMemberId);
            updateMember.setStatus(MemberConstant.STATUS_OUT_PERIOD);
            unionMemberService.update(updateMember);
        }


        UnionMemberOut saveOut2 = new UnionMemberOut();
        saveOut2.setUnionId(unionId);
        saveOut2.setApplyMemberId(applyMemberId);
        saveOut2.setType(MemberConstant.OUT_TYPE_REMOVE);
        saveOut2.setApplyOutReason("盟主移出");
        Date currentDate2 = DateUtil.getCurrentDate();
        saveOut2.setCreateTime(currentDate2);
        saveOut2.setConfirmOutTime(currentDate2);
        saveOut2.setActualOutTime(DateUtil.addDays(currentDate2, 15));
        saveOut2.setDelStatus(CommonConstant.DEL_STATUS_NO);
        save(saveOut2);

        UnionMember updateMember2 = new UnionMember();
        updateMember2.setId(applyMemberId);
        updateMember2.setStatus(MemberConstant.STATUS_OUT_PERIOD);
        unionMemberService.update(updateMember2);
    }

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    @Override
    public List<UnionMemberOut> filterByUnionId(List<UnionMemberOut> outList, Integer unionId) throws Exception {
        if (outList == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMemberOut> result = new ArrayList<>();
        for (UnionMemberOut out : outList) {
            if (unionId.equals(out.getUnionId())) {
                result.add(out);
            }
        }

        return result;
    }

    //***************************************** Object As a Service - get **********************************************

    public UnionMemberOut getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMemberOut result;
        // (1)cache
        String idKey = UnionMemberOutCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionMemberOut.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberOut> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionMemberOut> listByApplyMemberId(Integer applyMemberId) throws Exception {
        if (applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberOut> result;
        // (1)cache
        String applyMemberIdKey = UnionMemberOutCacheUtil.getApplyMemberIdKey(applyMemberId);
        if (redisCacheUtil.exists(applyMemberIdKey)) {
            String tempStr = redisCacheUtil.get(applyMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberOut.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberOut> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("apply_member_id", applyMemberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, applyMemberId, UnionMemberOutCacheUtil.TYPE_APPLY_MEMBER_ID);
        return result;
    }

    public List<UnionMemberOut> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberOut> result;
        // (1)cache
        String unionIdKey = UnionMemberOutCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberOut.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberOut> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionMemberOutCacheUtil.TYPE_UNION_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMemberOut newUnionMemberOut) throws Exception {
        if (newUnionMemberOut == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionMemberOut);
        removeCache(newUnionMemberOut);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMemberOut> newUnionMemberOutList) throws Exception {
        if (newUnionMemberOutList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionMemberOutList);
        removeCache(newUnionMemberOutList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionMemberOut unionMemberOut = getById(id);
        removeCache(unionMemberOut);
        // (2)remove in db logically
        UnionMemberOut removeUnionMemberOut = new UnionMemberOut();
        removeUnionMemberOut.setId(id);
        removeUnionMemberOut.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionMemberOut);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMemberOut> unionMemberOutList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMemberOut unionMemberOut = getById(id);
            unionMemberOutList.add(unionMemberOut);
        }
        removeCache(unionMemberOutList);
        // (2)remove in db logically
        List<UnionMemberOut> removeUnionMemberOutList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMemberOut removeUnionMemberOut = new UnionMemberOut();
            removeUnionMemberOut.setId(id);
            removeUnionMemberOut.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMemberOutList.add(removeUnionMemberOut);
        }
        updateBatchById(removeUnionMemberOutList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMemberOut updateUnionMemberOut) throws Exception {
        if (updateUnionMemberOut == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer unionMemberOutId = updateUnionMemberOut.getId();
        UnionMemberOut unionMemberOut = getById(unionMemberOutId);
        removeCache(unionMemberOut);
        // (2)update db
        updateById(updateUnionMemberOut);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMemberOut> updateUnionMemberOutList) throws Exception {
        if (updateUnionMemberOutList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> unionMemberOutIdList = new ArrayList<>();
        for (UnionMemberOut updateUnionMemberOut : updateUnionMemberOutList) {
            unionMemberOutIdList.add(updateUnionMemberOut.getId());
        }
        List<UnionMemberOut> unionMemberOutList = new ArrayList<>();
        for (Integer unionMemberOutId : unionMemberOutIdList) {
            UnionMemberOut unionMemberOut = getById(unionMemberOutId);
            unionMemberOutList.add(unionMemberOut);
        }
        removeCache(unionMemberOutList);
        // (2)update db
        updateBatchById(updateUnionMemberOutList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionMemberOut newUnionMemberOut, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionMemberOutCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionMemberOut);
    }

    private void setCache(List<UnionMemberOut> newUnionMemberOutList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionMemberOutCacheUtil.TYPE_APPLY_MEMBER_ID:
                foreignIdKey = UnionMemberOutCacheUtil.getApplyMemberIdKey(foreignId);
                break;
            case UnionMemberOutCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionMemberOutCacheUtil.getUnionIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMemberOutList);
        }
    }

    private void removeCache(UnionMemberOut unionMemberOut) {
        if (unionMemberOut == null) {
            return;
        }
        Integer id = unionMemberOut.getId();
        String idKey = UnionMemberOutCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer applyMemberId = unionMemberOut.getApplyMemberId();
        if (applyMemberId != null) {
            String applyMemberIdKey = UnionMemberOutCacheUtil.getApplyMemberIdKey(applyMemberId);
            redisCacheUtil.remove(applyMemberIdKey);
        }

        Integer unionId = unionMemberOut.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionMemberOutCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }
    }

    private void removeCache(List<UnionMemberOut> unionMemberOutList) {
        if (ListUtil.isEmpty(unionMemberOutList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionMemberOut unionMemberOut : unionMemberOutList) {
            idList.add(unionMemberOut.getId());
        }
        List<String> idKeyList = UnionMemberOutCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> applyMemberIdKeyList = getForeignIdKeyList(unionMemberOutList, UnionMemberOutCacheUtil.TYPE_APPLY_MEMBER_ID);
        if (ListUtil.isNotEmpty(applyMemberIdKeyList)) {
            redisCacheUtil.remove(applyMemberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionMemberOutList, UnionMemberOutCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionMemberOut> unionMemberOutList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionMemberOutCacheUtil.TYPE_APPLY_MEMBER_ID:
                for (UnionMemberOut unionMemberOut : unionMemberOutList) {
                    Integer applyMemberId = unionMemberOut.getApplyMemberId();
                    if (applyMemberId != null) {
                        String applyMemberIdKey = UnionMemberOutCacheUtil.getApplyMemberIdKey(applyMemberId);
                        result.add(applyMemberIdKey);
                    }
                }
                break;
            case UnionMemberOutCacheUtil.TYPE_UNION_ID:
                for (UnionMemberOut unionMemberOut : unionMemberOutList) {
                    Integer unionId = unionMemberOut.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionMemberOutCacheUtil.getUnionIdKey(unionId);
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