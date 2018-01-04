package com.gt.union.union.member.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
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
import com.gt.union.union.member.dao.IUnionMemberOutDao;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.entity.UnionMemberOut;
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
public class UnionMemberOutServiceImpl implements IUnionMemberOutService {
    @Autowired
    private IUnionMemberOutDao unionMemberOutDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private PhoneMessageSender phoneMessageSender;

    //********************************************* Base On Business - get *********************************************

    @Override
    public UnionMemberOut getValidByIdAndUnionId(Integer outId, Integer unionId) throws Exception {
        if (outId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMemberOut result = getValidById(outId);

        return result != null && unionId.equals(result.getUnionId()) ? result : null;
    }

    @Override
    public UnionMemberOut getValidByUnionIdAndApplyMemberId(Integer unionId, Integer applyMemberId) throws Exception {
        if (unionId == null || applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMemberOut> result = listValidByUnionId(unionId);
        result = filterByApplyMemberId(result, applyMemberId);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<MemberOutVO> listMemberOutVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_OWNER_ERROR);
        }
        // （2）	获取union下所有退盟申请状态的member，并获取对应的out
        List<MemberOutVO> result = new ArrayList<>();
        List<UnionMember> outMemberList = unionMemberService.listValidByUnionIdAndStatus(unionId, MemberConstant.STATUS_APPLY_OUT);
        if (ListUtil.isNotEmpty(outMemberList)) {
            for (UnionMember outMember : outMemberList) {
                MemberOutVO vo = new MemberOutVO();
                vo.setMember(outMember);

                UnionMemberOut out = getValidById(outMember.getId());
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
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // （2）	获取union下所有退盟过渡期状态的member，并获取对应的out
        List<MemberOutPeriodVO> result = new ArrayList<>();
        Date currentDate = DateUtil.getCurrentDate();
        List<UnionMember> periodMemberList = unionMemberService.listValidByUnionIdAndStatus(unionId, MemberConstant.STATUS_OUT_PERIOD);
        if (ListUtil.isNotEmpty(periodMemberList)) {
            for (UnionMember periodMember : periodMemberList) {
                MemberOutPeriodVO vo = new MemberOutPeriodVO();
                vo.setMember(periodMember);

                UnionMemberOut out = getValidByUnionIdAndApplyMemberId(unionId, periodMember.getId());
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

    //********************************************* Base On Business - save ********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveByBusIdAndUnionIdAndApplyMemberId(Integer busId, Integer unionId, Integer applyMemberId) throws Exception {
        if (busId == null || unionId == null || applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）	判断union有效性和member读权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_OWNER_ERROR);
        }
        // （2）	判断applyMemberId有效性
        UnionMember applyMember = unionMemberService.getValidReadByIdAndUnionId(applyMemberId, unionId);
        if (applyMember == null) {
            throw new BusinessException(CommonConstant.MEMBER_NOT_FOUND);
        }
        // （3）	要求applyMember是入盟状态
        if (MemberConstant.STATUS_IN != applyMember.getStatus()) {
            throw new BusinessException("盟员不在可移出状态");
        }
        Date currentDate = DateUtil.getCurrentDate();
        UnionMemberOut saveOut = new UnionMemberOut();
        saveOut.setCreateTime(currentDate);
        saveOut.setDelStatus(CommonConstant.DEL_STATUS_NO);
        saveOut.setUnionId(unionId);
        saveOut.setApplyMemberId(applyMemberId);
        saveOut.setType(MemberConstant.OUT_TYPE_REMOVE);
        saveOut.setApplyOutReason("盟主移出");
        saveOut.setConfirmOutTime(currentDate);
        saveOut.setActualOutTime(DateUtil.addDays(currentDate, 15));

        UnionMember updateMember = new UnionMember();
        updateMember.setId(applyMemberId);
        updateMember.setModifyTime(currentDate);
        updateMember.setStatus(MemberConstant.STATUS_OUT_PERIOD);
        // 事务操作
        save(saveOut);
        unionMemberService.update(updateMember);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveByBusIdAndUnionId(Integer busId, Integer unionId, String reason) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限
        UnionMain union = unionMainService.getById(unionId);
        if (!unionMainService.isUnionValid(union)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // （2）	要求不能是盟主
        if (MemberConstant.IS_UNION_OWNER_YES == member.getIsUnionOwner()) {
            throw new BusinessException("盟主不能申请退盟");
        }
        // （3）判断是否重复退盟
        if (existValidByUnionIdAndApplyMemberId(unionId, member.getId())) {
            throw new BusinessException("已存在退盟申请");
        }
        // （3）	要求member是入盟状态
        if (MemberConstant.STATUS_IN != member.getStatus()) {
            throw new BusinessException("不在可退盟状态");
        }
        if (StringUtil.isEmpty(reason)) {
            throw new BusinessException("退盟理由不能为空");
        }
        Date currentDate = DateUtil.getCurrentDate();

        UnionMember updateMember = new UnionMember();
        updateMember.setId(member.getId());
        updateMember.setModifyTime(currentDate);
        updateMember.setStatus(MemberConstant.STATUS_APPLY_OUT);

        UnionMemberOut saveOut = new UnionMemberOut();
        saveOut.setDelStatus(CommonConstant.COMMON_NO);
        saveOut.setCreateTime(currentDate);
        saveOut.setType(MemberConstant.OUT_TYPE_APPLY);
        saveOut.setUnionId(unionId);
        saveOut.setApplyMemberId(member.getId());
        saveOut.setApplyOutReason(reason);

        // （4）事务操作
        unionMemberService.update(updateMember);
        save(saveOut);

        // （5）发送短信通知
        UnionMember ownerMember = unionMemberService.getValidOwnerByUnionId(unionId);
        String phone = StringUtil.isNotEmpty(ownerMember.getNotifyPhone()) ? ownerMember.getNotifyPhone() : ownerMember.getDirectorPhone();
        String content = "\""
                + member.getEnterpriseName()
                + "\"申请退出\""
                + union.getName()
                + "\",请到退盟审核处查看并处理";
        phoneMessageSender.sendMsg(new PhoneMessage(ownerMember.getBusId(), phone, content));
    }

    //********************************************* Base On Business - remove ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByBusIdAndIdAndUnionId(Integer busId, Integer outId, Integer unionId) throws Exception {
        if (busId == null || outId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）判断union有效性和member读权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // （2）判断outId有效性
        UnionMemberOut out = getValidByIdAndUnionId(outId, unionId);
        if (out == null) {
            throw new BusinessException("找不到退盟申请信息");
        }
        // （3）判断out类型
        if (MemberConstant.OUT_TYPE_REMOVE == out.getType()) {
            if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
                throw new BusinessException(CommonConstant.UNION_OWNER_ERROR);
            }
        } else {
            if (!member.getId().equals(out.getApplyMemberId())) {
                throw new BusinessException("无法取消他人的退盟申请");
            }
        }
        // （4）要求applyMember状态是申请退盟状态或退盟过渡期状态
        UnionMember outMember = unionMemberService.getValidByIdAndUnionId(out.getApplyMemberId(), unionId);
        if (outMember == null) {
            throw new BusinessException(CommonConstant.MEMBER_NOT_FOUND);
        }
        if (MemberConstant.STATUS_APPLY_OUT != outMember.getStatus() && MemberConstant.STATUS_OUT_PERIOD != outMember.getStatus()) {
            throw new BusinessException("盟员不在退盟申请或退盟过渡期状态");
        }
        UnionMember updateMember = new UnionMember();
        updateMember.setId(outMember.getId());
        updateMember.setModifyTime(DateUtil.getCurrentDate());
        updateMember.setStatus(MemberConstant.STATUS_IN);
        // 事务
        unionMemberService.update(updateMember);
        removeById(outId);
    }

    //********************************************* Base On Business - update ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByBusIdAndIdAndUnionId(Integer busId, Integer outId, Integer unionId, Integer isPass) throws Exception {
        if (busId == null || outId == null || unionId == null || isPass == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限、盟主权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        if (MemberConstant.IS_UNION_OWNER_YES != member.getIsUnionOwner()) {
            throw new BusinessException(CommonConstant.UNION_OWNER_ERROR);
        }
        // （2）	判断outId有效性
        UnionMemberOut out = getValidByIdAndUnionId(outId, unionId);
        if (out == null) {
            throw new BusinessException("找不到退盟申请信息");
        }
        UnionMember outMember = unionMemberService.getValidByIdAndStatus(out.getApplyMemberId(), MemberConstant.STATUS_APPLY_OUT);
        if (outMember == null) {
            throw new BusinessException("找不到退盟盟员信息)");
        }

        if (CommonConstant.COMMON_YES == isPass) {
            UnionMember updateMember = new UnionMember();
            updateMember.setId(outMember.getId());
            updateMember.setModifyTime(DateUtil.getCurrentDate());
            updateMember.setStatus(MemberConstant.STATUS_OUT_PERIOD);

            UnionMemberOut updateOut = new UnionMemberOut();
            updateOut.setId(outId);
            updateOut.setConfirmOutTime(DateUtil.getCurrentDate());

            unionMemberService.update(updateMember);
            update(updateOut);
        } else {
            UnionMember updateMember = new UnionMember();
            updateMember.setId(outMember.getId());
            updateMember.setStatus(MemberConstant.STATUS_IN);

            unionMemberService.update(updateMember);
            removeById(outId);
        }

    }

    //********************************************* Base On Business - other *******************************************

    @Override
    public boolean existValidByUnionIdAndApplyMemberId(Integer unionId, Integer applyMemberId) throws Exception {
        if (unionId == null || applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return getValidByUnionIdAndApplyMemberId(unionId, applyMemberId) != null;
    }

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionMemberOut> filterByDelStatus(List<UnionMemberOut> unionMemberOutList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMemberOut> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMemberOutList)) {
            for (UnionMemberOut unionMemberOut : unionMemberOutList) {
                if (delStatus.equals(unionMemberOut.getDelStatus())) {
                    result.add(unionMemberOut);
                }
            }
        }

        return result;
    }

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

    //********************************************* Object As a Service - get ******************************************

    @Override
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
        result = unionMemberOutDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionMemberOut getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMemberOut result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionMemberOut getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMemberOut result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionMemberOut> unionMemberOutList) throws Exception {
        if (unionMemberOutList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMemberOutList)) {
            for (UnionMemberOut unionMemberOut : unionMemberOutList) {
                result.add(unionMemberOut.getId());
            }
        }

        return result;
    }

    @Override
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
        entityWrapper.eq("apply_member_id", applyMemberId);
        result = unionMemberOutDao.selectList(entityWrapper);
        setCache(result, applyMemberId, UnionMemberOutCacheUtil.TYPE_APPLY_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionMemberOut> listValidByApplyMemberId(Integer applyMemberId) throws Exception {
        if (applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberOut> result;
        // (1)cache
        String validApplyMemberIdKey = UnionMemberOutCacheUtil.getValidApplyMemberIdKey(applyMemberId);
        if (redisCacheUtil.exists(validApplyMemberIdKey)) {
            String tempStr = redisCacheUtil.get(validApplyMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberOut.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberOut> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("apply_member_id", applyMemberId);
        result = unionMemberOutDao.selectList(entityWrapper);
        setValidCache(result, applyMemberId, UnionMemberOutCacheUtil.TYPE_APPLY_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionMemberOut> listInvalidByApplyMemberId(Integer applyMemberId) throws Exception {
        if (applyMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberOut> result;
        // (1)cache
        String invalidApplyMemberIdKey = UnionMemberOutCacheUtil.getInvalidApplyMemberIdKey(applyMemberId);
        if (redisCacheUtil.exists(invalidApplyMemberIdKey)) {
            String tempStr = redisCacheUtil.get(invalidApplyMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberOut.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberOut> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("apply_member_id", applyMemberId);
        result = unionMemberOutDao.selectList(entityWrapper);
        setInvalidCache(result, applyMemberId, UnionMemberOutCacheUtil.TYPE_APPLY_MEMBER_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("union_id", unionId);
        result = unionMemberOutDao.selectList(entityWrapper);
        setCache(result, unionId, UnionMemberOutCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMemberOut> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberOut> result;
        // (1)cache
        String validUnionIdKey = UnionMemberOutCacheUtil.getValidUnionIdKey(unionId);
        if (redisCacheUtil.exists(validUnionIdKey)) {
            String tempStr = redisCacheUtil.get(validUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberOut.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberOut> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);
        result = unionMemberOutDao.selectList(entityWrapper);
        setValidCache(result, unionId, UnionMemberOutCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMemberOut> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMemberOut> result;
        // (1)cache
        String invalidUnionIdKey = UnionMemberOutCacheUtil.getInvalidUnionIdKey(unionId);
        if (redisCacheUtil.exists(invalidUnionIdKey)) {
            String tempStr = redisCacheUtil.get(invalidUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMemberOut.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMemberOut> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);
        result = unionMemberOutDao.selectList(entityWrapper);
        setInvalidCache(result, unionId, UnionMemberOutCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMemberOut> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMemberOut> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionMemberOut> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionMemberOutDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMemberOut newUnionMemberOut) throws Exception {
        if (newUnionMemberOut == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionMemberOutDao.insert(newUnionMemberOut);
        removeCache(newUnionMemberOut);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMemberOut> newUnionMemberOutList) throws Exception {
        if (newUnionMemberOutList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionMemberOutDao.insertBatch(newUnionMemberOutList);
        removeCache(newUnionMemberOutList);
    }

    //********************************************* Object As a Service - remove ***************************************

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
        unionMemberOutDao.updateById(removeUnionMemberOut);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMemberOut> unionMemberOutList = listByIdList(idList);
        removeCache(unionMemberOutList);
        // (2)remove in db logically
        List<UnionMemberOut> removeUnionMemberOutList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMemberOut removeUnionMemberOut = new UnionMemberOut();
            removeUnionMemberOut.setId(id);
            removeUnionMemberOut.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMemberOutList.add(removeUnionMemberOut);
        }
        unionMemberOutDao.updateBatchById(removeUnionMemberOutList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMemberOut updateUnionMemberOut) throws Exception {
        if (updateUnionMemberOut == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionMemberOut.getId();
        UnionMemberOut unionMemberOut = getById(id);
        removeCache(unionMemberOut);
        // (2)update db
        unionMemberOutDao.updateById(updateUnionMemberOut);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMemberOut> updateUnionMemberOutList) throws Exception {
        if (updateUnionMemberOutList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionMemberOutList);
        List<UnionMemberOut> unionMemberOutList = listByIdList(idList);
        removeCache(unionMemberOutList);
        // (2)update db
        unionMemberOutDao.updateBatchById(updateUnionMemberOutList);
    }

    //********************************************* Object As a Service - cache support ********************************

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

    private void setValidCache(List<UnionMemberOut> newUnionMemberOutList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionMemberOutCacheUtil.TYPE_APPLY_MEMBER_ID:
                validForeignIdKey = UnionMemberOutCacheUtil.getValidApplyMemberIdKey(foreignId);
                break;
            case UnionMemberOutCacheUtil.TYPE_UNION_ID:
                validForeignIdKey = UnionMemberOutCacheUtil.getValidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionMemberOutList);
        }
    }

    private void setInvalidCache(List<UnionMemberOut> newUnionMemberOutList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionMemberOutCacheUtil.TYPE_APPLY_MEMBER_ID:
                invalidForeignIdKey = UnionMemberOutCacheUtil.getInvalidApplyMemberIdKey(foreignId);
                break;
            case UnionMemberOutCacheUtil.TYPE_UNION_ID:
                invalidForeignIdKey = UnionMemberOutCacheUtil.getInvalidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionMemberOutList);
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

            String validApplyMemberIdKey = UnionMemberOutCacheUtil.getValidApplyMemberIdKey(applyMemberId);
            redisCacheUtil.remove(validApplyMemberIdKey);

            String invalidApplyMemberIdKey = UnionMemberOutCacheUtil.getInvalidApplyMemberIdKey(applyMemberId);
            redisCacheUtil.remove(invalidApplyMemberIdKey);
        }

        Integer unionId = unionMemberOut.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionMemberOutCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);

            String validUnionIdKey = UnionMemberOutCacheUtil.getValidUnionIdKey(unionId);
            redisCacheUtil.remove(validUnionIdKey);

            String invalidUnionIdKey = UnionMemberOutCacheUtil.getInvalidUnionIdKey(unionId);
            redisCacheUtil.remove(invalidUnionIdKey);
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

                        String validApplyMemberIdKey = UnionMemberOutCacheUtil.getValidApplyMemberIdKey(applyMemberId);
                        result.add(validApplyMemberIdKey);

                        String invalidApplyMemberIdKey = UnionMemberOutCacheUtil.getInvalidApplyMemberIdKey(applyMemberId);
                        result.add(invalidApplyMemberIdKey);
                    }
                }
                break;
            case UnionMemberOutCacheUtil.TYPE_UNION_ID:
                for (UnionMemberOut unionMemberOut : unionMemberOutList) {
                    Integer unionId = unionMemberOut.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionMemberOutCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);

                        String validUnionIdKey = UnionMemberOutCacheUtil.getValidUnionIdKey(unionId);
                        result.add(validUnionIdKey);

                        String invalidUnionIdKey = UnionMemberOutCacheUtil.getInvalidUnionIdKey(unionId);
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