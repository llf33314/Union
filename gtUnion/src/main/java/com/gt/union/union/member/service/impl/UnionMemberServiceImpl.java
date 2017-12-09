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
import com.gt.union.common.util.StringUtil;
import com.gt.union.union.main.entity.UnionMain;
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
    public UnionMember getByIdAndUnionIdAndBusId(Integer memberId, Integer unionId, Integer busId) throws Exception {
        if (memberId == null || unionId == null || busId == null) {
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
        return getReadByIdAndUnionId(memberId, unionId);
    }

    @Override
    public UnionMember getReadByIdAndUnionId(Integer memberId, Integer unionId) throws Exception {
        if (memberId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMember result = getById(memberId);
        if (result != null) {
            Integer status = result.getStatus();
            boolean isReadValid = MemberConstant.STATUS_IN == status || MemberConstant.STATUS_APPLY_OUT == status || MemberConstant.STATUS_OUT_PERIOD == status;
            if (unionId.equals(result.getUnionId()) && isReadValid) {
                return result;
            }
        }

        return null;
    }

    @Override
    public UnionMember getWriteByIdAndUnionId(Integer memberId, Integer unionId) throws Exception {
        if (memberId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMember result = getById(memberId);
        if (result != null) {
            Integer status = result.getStatus();
            boolean isWriteValid = MemberConstant.STATUS_IN == status || MemberConstant.STATUS_APPLY_OUT == status;
            if (unionId.equals(result.getUnionId()) && isWriteValid) {
                return result;
            }
        }

        return null;
    }

    @Override
    public UnionMember getByIdAndUnionIdAndStatus(Integer memberId, Integer unionId, Integer status) throws Exception {
        if (memberId == null || unionId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMember result = getById(memberId);
        if (result != null) {
            return unionId.equals(result.getUnionId()) && status.equals(result.getStatus()) ? result : null;
        }

        return null;
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

        return filterByStatusList(busMemberList, statusList);
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

        return filterByStatusList(busMemberList, statusList);
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

        return filterByStatusList(unionMemberList, statusList);
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

        return filterByStatusList(unionMemberList, statusList);
    }

    @Override
    public List<UnionMember> listWriteByBusIdAndUnionId(Integer busId, Integer unionId, String optMemberName) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）判断union有效性和member读权限
        if (!unionMainService.isUnionValid(unionId)) {
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

    @Override
    public List<UnionMember> listByUnionIdAndStatus(Integer unionId, Integer status) throws Exception {
        if (unionId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = listByUnionId(unionId);
        result = filterByStatus(result, status);
        return null;
    }

    @Override
    public List<UnionMember> listOtherWriteByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        // （2）获取商家在union的member
        UnionMember member = getReadByBusIdAndUnionId(busId, unionId);
        // （3）获取union下writeMember，过滤掉member
        List<UnionMember> result = new ArrayList<>();
        List<UnionMember> writeMemberList = listWriteByUnionId(unionId);
        if (member == null) {
            result = writeMemberList;
        } else if (ListUtil.isNotEmpty(writeMemberList)) {
            Integer memberId = member.getId();
            for (UnionMember writeMember : writeMemberList) {
                if (!memberId.equals(writeMember.getId())) {
                    result.add(writeMember);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionMember> listOtherReadByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        // （2）获取商家在union的member
        UnionMember member = getReadByBusIdAndUnionId(busId, unionId);
        // （3）获取union下writeMember，过滤掉member
        List<UnionMember> result = new ArrayList<>();
        List<UnionMember> readMemberList = listReadByUnionId(unionId);
        if (member == null) {
            result= readMemberList;
        } else if (ListUtil.isNotEmpty(readMemberList)) {
            Integer memberId = member.getId();
            for (UnionMember readMember : readMemberList) {
                if (!memberId.equals(readMember.getId())) {
                    result.add(readMember);
                }
            }
        }

        return result;
    }

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    @Override
    public void updateByIdAndUnionIdAndBusId(Integer memberId, Integer unionId, Integer busId, UnionMember vo) throws Exception {
        if (memberId == null || unionId == null || busId == null || vo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        // （2）	判断memberId有效性
        if (!member.getId().equals(memberId)) {
            throw new BusinessException("参数memberId无效");
        }
        // （3）	校验表单
        UnionMember updateMember = new UnionMember();
        updateMember.setId(memberId);
        updateMember.setModifyTime(DateUtil.getCurrentDate());
        // （3-1）企业名称
        String memberEnterpriseName = vo.getEnterpriseName();
        if (StringUtil.isEmpty(memberEnterpriseName)) {
            throw new BusinessException("企业名称不能为空");
        }
        if (StringUtil.getStringLength(memberEnterpriseName) > 10) {
            throw new BusinessException("企业名称字数不能大于10");
        }
        updateMember.setEnterpriseName(memberEnterpriseName);
        // （3-2）企业地址
        String memberEnterpriseAddress = vo.getEnterpriseAddress();
        if (StringUtil.isEmpty(memberEnterpriseAddress)) {
            throw new BusinessException("企业地址不能为空");
        }
        updateMember.setEnterpriseAddress(memberEnterpriseAddress);
        // （3-3）负责人名称
        String memberDirectorName = vo.getDirectorName();
        if (StringUtil.isEmpty(memberDirectorName)) {
            throw new BusinessException("负责人名称不能为空");
        }
        if (StringUtil.getStringLength(memberDirectorName) > 10) {
            throw new BusinessException("负责人名称字数不能大于10");
        }
        updateMember.setDirectorName(memberDirectorName);
        // （3-4）负责人联系电话
        String memberDirectorPhone = vo.getDirectorPhone();
        if (StringUtil.isEmpty(memberDirectorPhone)) {
            throw new BusinessException("负责人联系电话不能为空");
        }
        if (!StringUtil.isPhone(memberDirectorPhone)) {
            throw new BusinessException("负责人联系电话参数值有误");
        }
        updateMember.setDirectorPhone(memberDirectorPhone);
        // （3-5）负责人邮箱
        String memberDirectorEmail = vo.getDirectorEmail();
        if (StringUtil.isEmpty(memberDirectorEmail)) {
            throw new BusinessException("负责人联系邮箱不能为空");
        }
        if (!StringUtil.isEmail(memberDirectorEmail)) {
            throw new BusinessException("负责人联系邮箱参数值有误");
        }
        updateMember.setDirectorEmail(memberDirectorEmail);
        // （3-6）地址经纬度
        String memberAddressLongitude = vo.getAddressLongitude();
        String memberAddressLatitude = vo.getAddressLatitude();
        if (StringUtil.isEmpty(memberAddressLongitude) || StringUtil.isEmpty(memberAddressLatitude)) {
            throw new BusinessException("地址经纬度不能为空");
        }
        updateMember.setAddressLongitude(memberAddressLongitude);
        updateMember.setAddressLatitude(memberAddressLatitude);
        // （3-7）短信通知手机
        String memberNotifyPhone = vo.getNotifyPhone();
        if (StringUtil.isEmpty(memberNotifyPhone)) {
            throw new BusinessException("短信通知手机不能为空");
        }
        if (StringUtil.isPhone(memberNotifyPhone)) {
            throw new BusinessException("短信通知手机参数值有误");
        }
        updateMember.setNotifyPhone(memberNotifyPhone);

        // （3-8）积分抵扣率
        UnionMain union = unionMainService.getById(unionId);
        if (CommonConstant.COMMON_YES == union.getIsIntegral()) {
            Double memberIntegralExchangeRatio = vo.getIntegralExchangeRatio();
            if (memberIntegralExchangeRatio == null || memberIntegralExchangeRatio <= 0 || memberIntegralExchangeRatio > 30) {
                throw new BusinessException("积分抵扣率不能小于等于0，且不能大于30");
            }
            updateMember.setIntegralExchangeRatio(memberIntegralExchangeRatio);
        }

        update(updateMember);
    }

    @Override
    public void updateDiscountByIdAndUnionIdAndBusId(Integer memberId, Integer unionId, Integer busId, Double discount) throws Exception {
        if (memberId == null || unionId == null || busId == null || discount == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        // （2）	判断memberId有效性
        if (!member.getId().equals(memberId)) {
            throw new BusinessException("参数memberId无效");
        }
        // （3）	要求折扣在(0,10)
        if (discount <= 0 || discount >= 10) {
            throw new BusinessException("折扣必须大于0且小于10");
        }
        UnionMember updateMember = new UnionMember();
        updateMember.setId(memberId);
        updateMember.setDiscount(discount);
        update(updateMember);
    }

    //***************************************** Domain Driven Design - count *******************************************

    @Override
    public Integer countReadByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> memberList = listReadByBusId(busId);
        return ListUtil.isNotEmpty(memberList) ? memberList.size() : 0;
    }

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
    public List<UnionMember> filterByStatusList(List<UnionMember> memberList, List<Integer> statusList) throws Exception {
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
    public List<UnionMember> filterByStatus(List<UnionMember> memberList, Integer status) throws Exception {
        if (memberList == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = new ArrayList<>();
        for (UnionMember member : memberList) {
            if (status.equals(member.getStatus())) {
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

    @Override
    public UnionMember getById(Integer id) throws Exception {
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

    @Override
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