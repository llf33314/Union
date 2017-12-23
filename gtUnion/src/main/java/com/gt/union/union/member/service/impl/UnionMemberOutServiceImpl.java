package com.gt.union.union.member.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.amqp.entity.PhoneMessage;
import com.gt.union.api.amqp.sender.PhoneMessageSender;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.constant.MemberConstant;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.entity.UnionMemberOut;
import com.gt.union.union.member.mapper.UnionMemberOutMapper;
import com.gt.union.union.member.service.IUnionMemberOutService;
import com.gt.union.union.member.service.IUnionMemberService;
import com.gt.union.union.member.util.UnionMemberOutCacheUtil;
import com.gt.union.union.member.vo.MemberOutPeriodVO;
import com.gt.union.union.member.vo.MemberOutVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Autowired
    private PhoneMessageSender phoneMessageSender;

    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public UnionMemberOut getByUnionIdAndApplyMemberId(Integer unionId, Integer applyMemberId) throws Exception {
        if (unionId == null || applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMemberOut> result = listByUnionId(unionId);
        result = filterByApplyMemberId(result, applyMemberId);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    @Override
    public UnionMemberOut getByIdAndUnionId(Integer outId, Integer unionId) throws Exception {
        if (outId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMemberOut result = getById(outId);

        return result != null && unionId.equals(result.getUnionId()) ? result : null;
    }

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<MemberOutVO> listMemberOutVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_NEED_OWNER);
        }
        // （2）	获取union下所有退盟申请状态的member，并获取对应的out
        List<MemberOutVO> result = new ArrayList<>();
        List<UnionMember> outMemberList = unionMemberService.listByUnionIdAndStatus(unionId, MemberConstant.STATUS_APPLY_OUT);
        if (ListUtil.isNotEmpty(outMemberList)) {
            for (UnionMember outMember : outMemberList) {
                MemberOutVO vo = new MemberOutVO();
                vo.setMember(outMember);

                UnionMemberOut out = getByUnionIdAndApplyMemberId(unionId, outMember.getId());
                vo.setMemberOut(out);

                result.add(vo);
            }
        }
        // （3）	按时间倒序排序
        Collections.sort(result, new Comparator<MemberOutVO>() {
            @Override
            public int compare(MemberOutVO o1, MemberOutVO o2) {
                return o2.getMemberOut().getCreateTime().compareTo(o1.getMemberOut().getCreateTime());
            }
        });

        return result;
    }

    @Override
    public List<MemberOutPeriodVO> listMemberOutPeriodVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_READ_REJECT);
        }
        // （2）	获取union下所有退盟过渡期状态的member，并获取对应的out
        List<MemberOutPeriodVO> result = new ArrayList<>();
        Date currentDate = DateUtil.getCurrentDate();
        List<UnionMember> periodMemberList = unionMemberService.listByUnionIdAndStatus(unionId, MemberConstant.STATUS_OUT_PERIOD);
        if (ListUtil.isNotEmpty(periodMemberList)) {
            for (UnionMember periodMember : periodMemberList) {
                MemberOutPeriodVO vo = new MemberOutPeriodVO();
                vo.setMember(periodMember);

                UnionMemberOut out = getByUnionIdAndApplyMemberId(unionId, periodMember.getId());
                if (out == null) {
                    throw new BusinessException("找不到退盟申请信息");
                }
                vo.setMemberOut(out);

                vo.setPeriodDay(DateUtil.getPeriodIntervalDay(currentDate, out.getActualOutTime()));
                result.add(vo);
            }
        }
        // （3）	按确认时间倒序排序
        Collections.sort(result, new Comparator<MemberOutPeriodVO>() {
            @Override
            public int compare(MemberOutPeriodVO o1, MemberOutPeriodVO o2) {
                return o2.getMemberOut().getConfirmOutTime().compareTo(o1.getMemberOut().getConfirmOutTime());
            }
        });

        return result;
    }

    //***************************************** Domain Driven Design - save ********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveByBusIdAndUnionIdAndApplyMemberId(Integer busId, Integer unionId, Integer applyMemberId) throws Exception {
        if (busId == null || unionId == null || applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）	判断union有效性和member写权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_NEED_OWNER);
        }

        // （2）	判断applyMemberId有效性
        UnionMember applyMember = unionMemberService.getReadByIdAndUnionId(applyMemberId, unionId);
        if (applyMember == null) {
            throw new BusinessException("要移出的盟员不存在");
        }

        // （3）	判断applyMember状态：
        //   （3-1）如果是入盟状态，则新增退盟申请并通过，并更新applyMember为退盟过渡期状态；
        //   （3-2）如果是申请退盟状态，则判断退盟申请是否存在：
        //     （3-2-1）如果不存在，则新增，并自动通过退盟申请，更新applyMember为退盟过渡期状态；
        //     （3-2-2）如果存在，则直接返回成功；
        //   （3-3）如果是退盟过渡期状态，则直接返回成功；
        //   （3-4）如果是其他状态，报错
        boolean isSave = false;
        boolean isUpdate = false;
        UnionMemberOut existOut = null;
        switch (applyMember.getStatus()) {
            case MemberConstant.STATUS_IN:
                isSave = true;
                break;
            case MemberConstant.STATUS_APPLY_OUT:
                existOut = getByUnionIdAndApplyMemberId(unionId, applyMemberId);
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

        Date currentDate = DateUtil.getCurrentDate();
        if (isSave) {
            UnionMemberOut saveOut = new UnionMemberOut();
            saveOut.setCreateTime(currentDate);
            saveOut.setDelStatus(CommonConstant.DEL_STATUS_NO);
            saveOut.setUnionId(unionId);
            saveOut.setApplyMemberId(applyMemberId);
            saveOut.setType(MemberConstant.OUT_TYPE_REMOVE);
            saveOut.setApplyOutReason("盟主移出");
            saveOut.setConfirmOutTime(currentDate);
            saveOut.setActualOutTime(DateUtil.addDays(currentDate, 15));
            save(saveOut);

            UnionMember updateMember = new UnionMember();
            updateMember.setId(applyMemberId);
            updateMember.setStatus(MemberConstant.STATUS_OUT_PERIOD);
            unionMemberService.update(updateMember);
        } else if (isUpdate) {
            UnionMemberOut updateOut = new UnionMemberOut();
            updateOut.setId(existOut.getId());
            updateOut.setConfirmOutTime(currentDate);
            updateOut.setActualOutTime(DateUtil.addDays(currentDate, 15));
            update(updateOut);

            UnionMember updateMember = new UnionMember();
            updateMember.setId(applyMemberId);
            updateMember.setStatus(MemberConstant.STATUS_OUT_PERIOD);
            unionMemberService.update(updateMember);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveByBusIdAndUnionId(Integer busId, Integer unionId, String reason) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限、盟主权限
        UnionMain union = unionMainService.getById(unionId);
        if (!unionMainService.isUnionValid(union)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        // （2）	要求不能是盟主
        if (MemberConstant.IS_UNION_OWNER_YES == member.getIsUnionOwner()) {
            throw new BusinessException("盟主不能申请退盟");
        }
        // （3）	判断member状态
        //   （3-1）如果是入盟状态，则更新为退盟申请状态，且新增退盟申请信息；
        //   （3-2）如果是退盟申请状态，判断退盟申请是否存在：
        //     （3-2-1）如果不是，则新增；
        //     （3-2-2）如果是，则报错；
        //   （3-3）如果是其他状态，报错；
        Date currentDate = DateUtil.getCurrentDate();
        UnionMemberOut saveOut;
        switch (member.getStatus()) {
            case MemberConstant.STATUS_IN:
                if (StringUtil.isEmpty(reason)) {
                    throw new BusinessException("退盟理由不能为空");
                }

                UnionMember updateMember = new UnionMember();
                updateMember.setId(member.getId());
                updateMember.setStatus(MemberConstant.STATUS_APPLY_OUT);

                saveOut = new UnionMemberOut();
                saveOut.setDelStatus(CommonConstant.COMMON_NO);
                saveOut.setCreateTime(currentDate);
                saveOut.setType(MemberConstant.OUT_TYPE_APPLY);
                saveOut.setUnionId(unionId);
                saveOut.setApplyMemberId(member.getId());
                saveOut.setApplyOutReason(reason);

                unionMemberService.update(updateMember);
                save(saveOut);
                break;
            case MemberConstant.STATUS_APPLY_OUT:
                UnionMemberOut out = getByUnionIdAndApplyMemberId(unionId, member.getId());
                if (out == null) {
                    if (StringUtil.isEmpty(reason)) {
                        throw new BusinessException("退盟理由不能为空");
                    }

                    saveOut = new UnionMemberOut();
                    saveOut.setDelStatus(CommonConstant.COMMON_NO);
                    saveOut.setCreateTime(currentDate);
                    saveOut.setType(MemberConstant.OUT_TYPE_APPLY);
                    saveOut.setUnionId(unionId);
                    saveOut.setApplyMemberId(member.getId());
                    saveOut.setApplyOutReason(reason);

                    save(saveOut);
                } else {
                    throw new BusinessException("已存在退盟申请");
                }
                break;
            default:
                throw new BusinessException("退盟盟员状态异常");
        }
        // （4）发送短信通知
        UnionMember ownerMember = unionMemberService.getOwnerByUnionId(unionId);
        if (ownerMember != null) {
            String phone = StringUtil.isNotEmpty(ownerMember.getNotifyPhone()) ? ownerMember.getNotifyPhone() : ownerMember.getDirectorPhone();
            String content = "\""
                    + member.getEnterpriseAddress()
                    + "\"申请退出\""
                    + union.getName()
                    + "\",请到退盟审核处查看并处理";
            phoneMessageSender.sendMsg(new PhoneMessage(ownerMember.getBusId(), phone, content));
        }


    }

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatusByBusIdAndIdAndUnionId(Integer busId, Integer outId, Integer unionId, Integer isPass) throws Exception {
        if (busId == null || outId == null || unionId == null || isPass == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_NEED_OWNER);
        }
        // （2）	判断outId有效性
        UnionMemberOut out = getByIdAndUnionId(outId, unionId);
        if (out == null) {
            throw new BusinessException("找不到退盟申请信息");
        }
        UnionMember outMember = unionMemberService.getReadByIdAndUnionId(out.getApplyMemberId(), unionId);
        if (outMember == null) {
            throw new BusinessException("找不到退盟盟员信息)");
        }
        // （3）	判断是否审核通过：
        //   （3-1）如果是，则判断outMember状态：
        //     （3-1-1）如果是退盟申请状态，则更新为退盟过渡期；
        //     （3-1-2）如果是退盟过渡期状态，则直接返回成功；
        //     （3-1-3）如果是其他状态，报错；
        //   （3-2）	如果不是，则判断outMember状态：
        //     （3-2-1）如果是退盟申请状态，则更新为入盟状态，并移除退盟申请；
        //     （3-2-2）如果是入盟状态，则直接返回成功；
        //     （3-2-3）如果是其他状态，报错。
        if (CommonConstant.COMMON_YES == isPass) {
            if (MemberConstant.STATUS_APPLY_OUT == outMember.getStatus()) {
                UnionMember updateMember = new UnionMember();
                updateMember.setId(outMember.getId());
                updateMember.setStatus(MemberConstant.STATUS_OUT_PERIOD);

                UnionMemberOut updateOut = new UnionMemberOut();
                updateOut.setId(outId);
                updateOut.setConfirmOutTime(DateUtil.getCurrentDate());

                unionMemberService.update(updateMember);
                update(updateOut);
            } else if (MemberConstant.STATUS_OUT_PERIOD != outMember.getStatus()) {
                throw new BusinessException("退盟盟员状态异常");
            }
        } else {
            if (MemberConstant.STATUS_APPLY_OUT == outMember.getStatus()) {
                UnionMember updateMember = new UnionMember();
                updateMember.setId(outMember.getId());
                updateMember.setStatus(MemberConstant.STATUS_IN);

                unionMemberService.update(updateMember);
                removeById(outId);
            } else if (MemberConstant.STATUS_IN != outMember.getStatus()) {
                throw new BusinessException("退盟盟员状态异常");
            }
        }

    }

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    @Override
    public List<UnionMemberOut> filterByApplyMemberId(List<UnionMemberOut> outList, Integer applyMemberId) throws Exception {
        if (outList == null || applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMemberOut> result = new ArrayList<>();
        for (UnionMemberOut out : outList) {
            if (applyMemberId.equals(out.getApplyMemberId())) {
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

    @Override
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