package com.gt.union.union.member.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.api.client.dict.IDictService;
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
import com.gt.union.union.member.dao.IUnionMemberDao;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.entity.UnionMemberOut;
import com.gt.union.union.member.service.IUnionMemberOutService;
import com.gt.union.union.member.service.IUnionMemberService;
import com.gt.union.union.member.util.UnionMemberCacheUtil;
import com.gt.union.union.member.vo.MemberOutVO;
import com.gt.union.union.member.vo.MemberVO;
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
public class UnionMemberServiceImpl implements IUnionMemberService {
    @Autowired
    private IUnionMemberDao unionMemberDao;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IDictService dictService;

    @Autowired
    private IUnionMemberOutService unionMemberOutService;

    //********************************************* Base On Business - get *********************************************

    @Override
    public UnionMember getValidByIdAndUnionId(Integer memberId, Integer unionId) throws Exception {
        if (memberId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMember result = getValidById(memberId);

        return result != null && unionId.equals(result.getUnionId()) ? result : null;
    }

    @Override
    public UnionMember getValidByIdAndStatus(Integer memberId, Integer status) throws Exception {
        if (memberId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMember result = getValidById(memberId);

        return result != null && status.equals(result.getStatus()) ? result : null;
    }

    @Override
    public UnionMember getValidReadById(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMember result = getValidById(memberId);
        Integer status = result != null ? result.getStatus() : null;

        return status != null && (MemberConstant.STATUS_IN == status || MemberConstant.STATUS_APPLY_OUT == status || MemberConstant.STATUS_OUT_PERIOD == status) ? result : null;
    }

    @Override
    public UnionMember getValidReadByIdAndUnionId(Integer memberId, Integer unionId) throws Exception {
        if (memberId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMember result = getValidReadById(memberId);

        return result != null && unionId.equals(result.getUnionId()) ? result : null;
    }

    @Override
    public UnionMember getValidWriteByIdAndUnionId(Integer memberId, Integer unionId) throws Exception {
        if (memberId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMember result = getValidByIdAndUnionId(memberId, unionId);

        if (result == null) {
            return null;
        } else {
            Integer status = result.getStatus();
            return MemberConstant.STATUS_IN == status || MemberConstant.STATUS_APPLY_OUT == status ? result : null;
        }
    }

    @Override
    public UnionMember getValidOwnerByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = listValidByUnionId(unionId);
        result = filterByIsUnionOwner(result, MemberConstant.IS_UNION_OWNER_YES);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    @Override
    public UnionMember getValidOwnerByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = listValidByBusId(busId);
        result = filterByIsUnionOwner(result, MemberConstant.IS_UNION_OWNER_YES);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    @Override
    public UnionMember getValidReadByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }

        List<UnionMember> result = listValidReadByBusId(busId);
        result = filterByUnionId(result, unionId);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    @Override
    public UnionMember getValidWriteByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }

        List<UnionMember> result = listValidWriteByBusId(busId);
        result = filterByUnionId(result, unionId);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    @Override
    public UnionMember getValidByBusIdAndUnionIdAndStatus(Integer busId, Integer unionId, Integer status) throws Exception {
        if (busId == null || unionId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = listValidByBusId(busId);
        result = filterByUnionId(result, unionId);
        result = filterByStatus(result, status);

        return ListUtil.isNotEmpty(result) ? result.get(0) : null;
    }

    @Override
    public UnionMember getByBusIdAndId(Integer busId, Integer memberId) throws Exception {
        if (busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMember result = getById(memberId);

        return result != null && busId.equals(result.getBusId()) ? result : null;
    }

    @Override
    public UnionMember getByIdAndUnionId(Integer memberId, Integer unionId) throws Exception {
        if (memberId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMember result = getById(memberId);

        return result != null && unionId.equals(result.getUnionId()) ? result : null;
    }

    @Override
    public UnionMember getByBusIdAndIdAndUnionId(Integer busId, Integer memberId, Integer unionId) throws Exception {
        if (busId == null || memberId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMember result = getByBusIdAndId(busId, memberId);

        return result != null && unionId.equals(result.getUnionId()) ? result : null;
    }

    @Override
    public UnionMember getValidByBusIdAndIdAndUnionId(Integer busId, Integer memberId, Integer unionId) throws Exception {
        if (busId == null || memberId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）	判断union有效性和member读权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }

        return getValidReadByIdAndUnionId(memberId, unionId);
    }

    @Override
    public MemberVO getMemberVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限
        UnionMain union = unionMainService.getValidById(unionId);
        if (!unionMainService.isUnionValid(union)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }

        MemberVO result = new MemberVO();
        result.setUnion(union);
        result.setMember(member);

        return result;
    }

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<UnionMember> listValidByStatus(Integer status) throws Exception {
        if (status == null) {
            throw new BusinessException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("status", status);

        return unionMemberDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionMember> listValidReadByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = listValidByUnionId(unionId);
        List<Integer> statusList = new ArrayList<>();
        statusList.add(MemberConstant.STATUS_IN);
        statusList.add(MemberConstant.STATUS_APPLY_OUT);
        statusList.add(MemberConstant.STATUS_OUT_PERIOD);
        result = filterByStatusList(result, statusList);

        return result;
    }

    @Override
    public List<UnionMember> listValidByUnionIdAndStatus(Integer unionId, Integer status) throws Exception {
        if (unionId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = listValidByUnionId(unionId);
        result = filterByStatus(result, status);

        return result;
    }

    @Override
    public List<UnionMember> listValidWriteByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = listValidByUnionId(unionId);
        List<Integer> statusList = new ArrayList<>();
        statusList.add(MemberConstant.STATUS_IN);
        statusList.add(MemberConstant.STATUS_APPLY_OUT);
        result = filterByStatusList(result, statusList);

        return result;
    }

    @Override
    public List<UnionMember> listReadByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = listByBusId(busId);
        List<Integer> statusList = new ArrayList<>();
        statusList.add(MemberConstant.STATUS_IN);
        statusList.add(MemberConstant.STATUS_APPLY_OUT);
        statusList.add(MemberConstant.STATUS_OUT_PERIOD);
        result = filterByStatusList(result, statusList);

        return result;
    }

    @Override
    public List<UnionMember> listValidReadByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> result = listReadByBusId(busId);
        result = filterByDelStatus(result, CommonConstant.DEL_STATUS_NO);

        return result;
    }

    @Override
    public List<UnionMember> listValidWriteByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = listValidByBusId(busId);
        List<Integer> statusList = new ArrayList<>();
        statusList.add(MemberConstant.STATUS_IN);
        statusList.add(MemberConstant.STATUS_APPLY_OUT);
        result = filterByStatusList(result, statusList);

        return result;
    }

    @Override
    public List<UnionMember> listByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = listByBusId(busId);

        return filterByUnionId(result, unionId);
    }

    @Override
    public List<UnionMember> listOtherValidReadByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        // （2）获取商家在union的member
        UnionMember member = getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // （3）获取union下readMember，过滤掉member
        List<UnionMember> readMemberList = listValidReadByUnionId(unionId);

        return getOtherMemberList(readMemberList, member);
    }

    @Override
    public List<UnionMember> listOtherValidWriteByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        // （2）获取商家在union的member
        UnionMember member = getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // （3）获取union下writeMember，过滤掉member
        List<UnionMember> writeMemberList = listValidWriteByUnionId(unionId);

        return getOtherMemberList(writeMemberList, member);
    }

    private List<UnionMember> getOtherMemberList(List<UnionMember> memberList, UnionMember exceptMember) {
        List<UnionMember> result = new ArrayList<>();

        if (ListUtil.isNotEmpty(memberList)) {
            Integer exceptMemberId = exceptMember.getId();
            for (UnionMember member : memberList) {
                if (!exceptMemberId.equals(member.getId())) {
                    result.add(member);
                }
            }
        }

        return result;
    }

    @Override
    public List<MemberOutVO> listIndexByBusIdAndUnionId(Integer busId, Integer unionId, String optMemberName) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）判断union有效性和member读权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        final UnionMember member = getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // （2）	获取union下所有具有读权限的member
        List<UnionMember> unionMemberList = listValidReadByUnionId(unionId);
        // （3）	根据查询条件进行过滤
        if (StringUtil.isNotEmpty(optMemberName)) {
            unionMemberList = filterByListEnterpriseName(unionMemberList, optMemberName);
        }
        List<MemberOutVO> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMemberList)) {
            for (UnionMember unionMember : unionMemberList) {
                MemberOutVO vo = new MemberOutVO();
                vo.setMember(unionMember);

                UnionMemberOut memberOut = unionMemberOutService.getValidByUnionIdAndApplyMemberId(unionId, unionMember.getId());
                vo.setMemberOut(memberOut);

                result.add(vo);
            }
        }
        // （4）	按盟主>商家盟员>其他盟员，其他盟员按时间顺序排序
        Collections.sort(result, new Comparator<MemberOutVO>() {
            @Override
            public int compare(MemberOutVO o1, MemberOutVO o2) {
                if (MemberConstant.IS_UNION_OWNER_YES == o1.getMember().getIsUnionOwner()) {
                    return -1;
                }
                if (MemberConstant.IS_UNION_OWNER_YES == o2.getMember().getIsUnionOwner()) {
                    return 1;
                }
                if (member.getId().equals(o1.getMember().getId())) {
                    return -1;
                }
                if (member.getId().equals(o2.getMember().getId())) {
                    return 1;
                }
                return o1.getMember().getCreateTime().compareTo(o2.getMember().getCreateTime());
            }
        });
        return result;
    }

    @Override
    public List<UnionMember> listDiscountByBusIdAndUnionId(Integer busId, Integer unionId, String optMemberName) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）判断union有效性和member读权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        final UnionMember member = getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // （2）	获取union下所有具有读权限的member
        List<UnionMember> result = listValidReadByUnionId(unionId);
        // （3）	如果memberName不为空，则进行过滤
        if (StringUtil.isNotEmpty(optMemberName)) {
            result = filterByListEnterpriseName(result, optMemberName);
        }
        // （4）	按盟主>商家盟员>其他盟员，其他盟员按时间顺序排序
        Collections.sort(result, new Comparator<UnionMember>() {
            @Override
            public int compare(UnionMember o1, UnionMember o2) {
                if (MemberConstant.IS_UNION_OWNER_YES == o1.getIsUnionOwner()) {
                    return -1;
                }
                if (MemberConstant.IS_UNION_OWNER_YES == o2.getIsUnionOwner()) {
                    return 1;
                }
                if (member.getId().equals(o1.getId())) {
                    return -1;
                }
                if (member.getId().equals(o2.getId())) {
                    return 1;
                }
                return o1.getCreateTime().compareTo(o2.getCreateTime());
            }
        });

        return result;
    }

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByBusIdAndIdAndUnionId(Integer busId, Integer memberId, Integer unionId, UnionMember vo) throws Exception {
        if (busId == null || memberId == null || unionId == null || vo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member读权限
        UnionMain union = unionMainService.getValidById(unionId);
        if (!unionMainService.isUnionValid(union)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // （2）	判断memberId有效性
        if (!memberId.equals(member.getId())) {
            throw new BusinessException("无法更新他人的盟员信息");
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
        if (StringUtil.getStringLength(memberEnterpriseName) > 30) {
            throw new BusinessException("企业名称字数不能大于30");
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
        if (StringUtil.getStringLength(memberDirectorName) > 20) {
            throw new BusinessException("负责人名称字数不能大于20");
        }
        updateMember.setDirectorName(memberDirectorName);
        // （3-4）负责人联系电话
        String memberDirectorPhone = vo.getDirectorPhone();
        if (StringUtil.isEmpty(memberDirectorPhone)) {
            throw new BusinessException("负责人联系电话不能为空");
        }
        if (!StringUtil.isPhone(memberDirectorPhone)) {
            throw new BusinessException("负责人联系电话格式不对，不是有效的联系电话");
        }
        updateMember.setDirectorPhone(memberDirectorPhone);
        // （3-5）负责人邮箱
        String memberDirectorEmail = vo.getDirectorEmail();
        if (StringUtil.isEmpty(memberDirectorEmail)) {
            throw new BusinessException("负责人联系邮箱不能为空");
        }
        if (!StringUtil.isEmail(memberDirectorEmail)) {
            throw new BusinessException("负责人联系邮箱格式不对，不是有效的联系邮箱");
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
        if (!StringUtil.isPhone(memberNotifyPhone)) {
            throw new BusinessException("短信通知手机格式不对，不是有效的手机号");
        }
        updateMember.setNotifyPhone(memberNotifyPhone);
        // （3-8）如果联盟开启积分，则校验积分抵扣率
        if (CommonConstant.COMMON_YES == union.getIsIntegral()) {
            Double memberIntegralExchangeRatio = vo.getIntegralExchangeRatio();
            Double maxIntegralExchange = dictService.getMaxExchangePercent();
            if (memberIntegralExchangeRatio == null || memberIntegralExchangeRatio <= 0 || memberIntegralExchangeRatio > maxIntegralExchange) {
                throw new BusinessException("积分抵扣率不能小于等于0，且不能大于" + maxIntegralExchange * 100);
            }
            updateMember.setIntegralExchangeRatio(memberIntegralExchangeRatio);
        }

        update(updateMember);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDiscountByBusIdAndIdAndUnionId(Integer busId, Integer memberId, Integer unionId, Double discount) throws Exception {
        if (busId == null || memberId == null || unionId == null || discount == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // （2）	判断memberId有效性
        if (!memberId.equals(member.getId())) {
            throw new BusinessException("无法更新其他盟员的折扣信息");
        }
        // （3）	要求折扣在(0,10)
        if (discount <= 0 || discount >= 1) {
            throw new BusinessException("折扣必须大于0且小于10");
        }

        UnionMember updateMember = new UnionMember();
        updateMember.setId(memberId);
        updateMember.setModifyTime(DateUtil.getCurrentDate());
        updateMember.setDiscount(discount);
        update(updateMember);
    }

    //********************************************* Base On Business - other *******************************************

    @Override
    public Integer countValidReadByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = listValidReadByBusId(busId);

        return ListUtil.isNotEmpty(result) ? result.size() : 0;
    }

    @Override
    public Integer countValidReadByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = listValidReadByUnionId(unionId);

        return ListUtil.isNotEmpty(result) ? result.size() : 0;
    }

    @Override
    public boolean existValidReadById(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return getValidReadById(memberId) != null;
    }

    @Override
    public boolean existValidReadByIdAndUnionId(Integer memberId, Integer unionId) throws Exception {
        if (memberId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return getValidReadByIdAndUnionId(memberId, unionId) != null;
    }

    @Override
    public boolean existValidOwnerByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return getValidOwnerByBusId(busId) != null;
    }

    @Override
    public boolean existValidReadByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return getValidReadByBusIdAndUnionId(busId, unionId) != null;
    }

    @Override
    public boolean existValidByBusIdAndUnionIdAndStatus(Integer busId, Integer unionId, Integer status) throws Exception {
        if (busId == null || unionId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return getValidByBusIdAndUnionIdAndStatus(busId, unionId, status) != null;
    }

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionMember> filterByDelStatus(List<UnionMember> unionMemberList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMemberList)) {
            for (UnionMember unionMember : unionMemberList) {
                if (delStatus.equals(unionMember.getDelStatus())) {
                    result.add(unionMember);
                }
            }
        }

        return result;
    }

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

    @Override
    public List<UnionMember> filterByListEnterpriseName(List<UnionMember> memberList, String likeEnterpriseName) throws Exception {
        if (memberList == null || likeEnterpriseName == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(memberList)) {
            for (UnionMember member : memberList) {
                if (member.getEnterpriseName().contains(likeEnterpriseName)) {
                    result.add(member);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionMember> filterByListDirectorPhone(List<UnionMember> memberList, String likeDirectorPhone) throws Exception {
        if (memberList == null || likeDirectorPhone == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(memberList)) {
            for (UnionMember member : memberList) {
                if (member.getDirectorPhone().contains(likeDirectorPhone)) {
                    result.add(member);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionMember> filterInvalidUnionId(List<UnionMember> memberList) throws Exception {
        if (memberList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(memberList)) {
            for (UnionMember member : memberList) {
                if (unionMainService.isUnionValid(member.getUnionId())) {
                    result.add(member);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

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
        result = unionMemberDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionMember getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMember result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionMember getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMember result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionMember> unionMemberList) throws Exception {
        if (unionMemberList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMemberList)) {
            for (UnionMember unionMember : unionMemberList) {
                result.add(unionMember.getId());
            }
        }

        return result;
    }

    @Override
    public List<Integer> getUnionIdList(List<UnionMember> memberList) throws Exception {
        List<Integer> result = new ArrayList<>();

        if (ListUtil.isNotEmpty(memberList)) {
            for (UnionMember member : memberList) {
                result.add(member.getUnionId());
            }
        }

        return result;
    }

    @Override
    public List<Integer> getBusIdList(List<UnionMember> memberList) throws Exception {
        List<Integer> result = new ArrayList<>();

        if (ListUtil.isNotEmpty(memberList)) {
            for (UnionMember member : memberList) {
                result.add(member.getBusId());
            }
        }

        return result;
    }

    @Override
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
        entityWrapper.eq("bus_id", busId);
        result = unionMemberDao.selectList(entityWrapper);
        setCache(result, busId, UnionMemberCacheUtil.TYPE_BUS_ID);
        return result;
    }

    @Override
    public List<UnionMember> listValidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> result;
        // (1)cache
        String validBusIdKey = UnionMemberCacheUtil.getValidBusIdKey(busId);
        if (redisCacheUtil.exists(validBusIdKey)) {
            String tempStr = redisCacheUtil.get(validBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionMember.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);
        result = unionMemberDao.selectList(entityWrapper);
        setValidCache(result, busId, UnionMemberCacheUtil.TYPE_BUS_ID);
        return result;
    }

    @Override
    public List<UnionMember> listInvalidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> result;
        // (1)cache
        String invalidBusIdKey = UnionMemberCacheUtil.getInvalidBusIdKey(busId);
        if (redisCacheUtil.exists(invalidBusIdKey)) {
            String tempStr = redisCacheUtil.get(invalidBusIdKey);
            result = JSONArray.parseArray(tempStr, UnionMember.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("bus_id", busId);
        result = unionMemberDao.selectList(entityWrapper);
        setInvalidCache(result, busId, UnionMemberCacheUtil.TYPE_BUS_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("union_id", unionId);
        result = unionMemberDao.selectList(entityWrapper);
        setCache(result, unionId, UnionMemberCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMember> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> result;
        // (1)cache
        String validUnionIdKey = UnionMemberCacheUtil.getValidUnionIdKey(unionId);
        if (redisCacheUtil.exists(validUnionIdKey)) {
            String tempStr = redisCacheUtil.get(validUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMember.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);
        result = unionMemberDao.selectList(entityWrapper);
        setValidCache(result, unionId, UnionMemberCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMember> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> result;
        // (1)cache
        String invalidUnionIdKey = UnionMemberCacheUtil.getInvalidUnionIdKey(unionId);
        if (redisCacheUtil.exists(invalidUnionIdKey)) {
            String tempStr = redisCacheUtil.get(invalidUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMember.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);
        result = unionMemberDao.selectList(entityWrapper);
        setInvalidCache(result, unionId, UnionMemberCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionMember> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionMember> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public List<UnionMember> listSupport(EntityWrapper<UnionMember> entityWrapper) throws Exception {
        if (entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionMemberDao.selectList(entityWrapper);
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionMember> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionMemberDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMember newUnionMember) throws Exception {
        if (newUnionMember == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionMemberDao.insert(newUnionMember);
        removeCache(newUnionMember);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMember> newUnionMemberList) throws Exception {
        if (newUnionMemberList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionMemberDao.insertBatch(newUnionMemberList);
        removeCache(newUnionMemberList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
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
        unionMemberDao.updateById(removeUnionMember);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionMember> unionMemberList = listByIdList(idList);
        removeCache(unionMemberList);
        // (2)remove in db logically
        List<UnionMember> removeUnionMemberList = new ArrayList<>();
        for (Integer id : idList) {
            UnionMember removeUnionMember = new UnionMember();
            removeUnionMember.setId(id);
            removeUnionMember.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionMemberList.add(removeUnionMember);
        }
        unionMemberDao.updateBatchById(removeUnionMemberList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMember updateUnionMember) throws Exception {
        if (updateUnionMember == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionMember.getId();
        UnionMember unionMember = getById(id);
        removeCache(unionMember);
        // (2)update db
        unionMemberDao.updateById(updateUnionMember);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMember> updateUnionMemberList) throws Exception {
        if (updateUnionMemberList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionMemberList);
        List<UnionMember> unionMemberList = listByIdList(idList);
        removeCache(unionMemberList);
        // (2)update db
        unionMemberDao.updateBatchById(updateUnionMemberList);
    }

    //********************************************* Object As a Service - cache support ********************************

    private void setCache(UnionMember newUnionMember, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionMemberCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionMember);
    }

    private void setCache(List<UnionMember> newUnionMemberList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionMemberCacheUtil.TYPE_BUS_ID:
                foreignIdKey = UnionMemberCacheUtil.getBusIdKey(foreignId);
                break;
            case UnionMemberCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionMemberCacheUtil.getUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionMemberList);
        }
    }

    private void setValidCache(List<UnionMember> newUnionMemberList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionMemberCacheUtil.TYPE_BUS_ID:
                validForeignIdKey = UnionMemberCacheUtil.getValidBusIdKey(foreignId);
                break;
            case UnionMemberCacheUtil.TYPE_UNION_ID:
                validForeignIdKey = UnionMemberCacheUtil.getValidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionMemberList);
        }
    }

    private void setInvalidCache(List<UnionMember> newUnionMemberList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionMemberCacheUtil.TYPE_BUS_ID:
                invalidForeignIdKey = UnionMemberCacheUtil.getInvalidBusIdKey(foreignId);
                break;
            case UnionMemberCacheUtil.TYPE_UNION_ID:
                invalidForeignIdKey = UnionMemberCacheUtil.getInvalidUnionIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionMemberList);
        }
    }

    private void removeCache(UnionMember unionMember) {
        if (unionMember == null) {
            return;
        }
        Integer id = unionMember.getId();
        String idKey = UnionMemberCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer busId = unionMember.getBusId();
        if (busId != null) {
            String busIdKey = UnionMemberCacheUtil.getBusIdKey(busId);
            redisCacheUtil.remove(busIdKey);

            String validBusIdKey = UnionMemberCacheUtil.getValidBusIdKey(busId);
            redisCacheUtil.remove(validBusIdKey);

            String invalidBusIdKey = UnionMemberCacheUtil.getInvalidBusIdKey(busId);
            redisCacheUtil.remove(invalidBusIdKey);
        }

        Integer unionId = unionMember.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionMemberCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);

            String validUnionIdKey = UnionMemberCacheUtil.getValidUnionIdKey(unionId);
            redisCacheUtil.remove(validUnionIdKey);

            String invalidUnionIdKey = UnionMemberCacheUtil.getInvalidUnionIdKey(unionId);
            redisCacheUtil.remove(invalidUnionIdKey);
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

        List<String> busIdKeyList = getForeignIdKeyList(unionMemberList, UnionMemberCacheUtil.TYPE_BUS_ID);
        if (ListUtil.isNotEmpty(busIdKeyList)) {
            redisCacheUtil.remove(busIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionMemberList, UnionMemberCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

    }

    private List<String> getForeignIdKeyList(List<UnionMember> unionMemberList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionMemberCacheUtil.TYPE_BUS_ID:
                for (UnionMember unionMember : unionMemberList) {
                    Integer busId = unionMember.getBusId();
                    if (busId != null) {
                        String busIdKey = UnionMemberCacheUtil.getBusIdKey(busId);
                        result.add(busIdKey);

                        String validBusIdKey = UnionMemberCacheUtil.getValidBusIdKey(busId);
                        result.add(validBusIdKey);

                        String invalidBusIdKey = UnionMemberCacheUtil.getInvalidBusIdKey(busId);
                        result.add(invalidBusIdKey);
                    }
                }
                break;
            case UnionMemberCacheUtil.TYPE_UNION_ID:
                for (UnionMember unionMember : unionMemberList) {
                    Integer unionId = unionMember.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionMemberCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);

                        String validUnionIdKey = UnionMemberCacheUtil.getValidUnionIdKey(unionId);
                        result.add(validUnionIdKey);

                        String invalidUnionIdKey = UnionMemberCacheUtil.getInvalidUnionIdKey(unionId);
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