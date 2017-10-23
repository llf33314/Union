package com.gt.union.member.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.main.constant.MainConstant;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.mapper.UnionMemberMapper;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.member.vo.CardDividePercentVO;
import com.gt.union.member.vo.UnionMemberVO;
import com.gt.union.preferential.constant.PreferentialConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 联盟成员 服务实现类
 *
 * @author linweicong
 * @version 2017-10-23 08:34:54
 */
@Service
public class UnionMemberServiceImpl extends ServiceImpl<UnionMemberMapper, UnionMember> implements IUnionMemberService {
    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    @Override
    public UnionMember getOwnerByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> memberList = this.listByUnionId(unionId);
        if (ListUtil.isNotEmpty(memberList)) {
            for (UnionMember member : memberList) {
                if (member.getIsUnionOwner() == MemberConstant.IS_UNION_OWNER_YES) {
                    return member;
                }
            }
        }
        return null;
    }

    @Override
    public UnionMember getOwnerByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> memberList = this.listByBusId(busId);
        if (ListUtil.isNotEmpty(memberList)) {
            for (UnionMember member : memberList) {
                if (member.getIsUnionOwner() == MemberConstant.IS_UNION_OWNER_YES) {
                    return member;
                }
            }
        }
        return null;
    }

    @Override
    public UnionMember getByIdAndBusId(Integer memberId, Integer busId) throws Exception {
        if (memberId == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMember member = this.getById(memberId);
        if (member != null && busId.equals(member.getBusId())) {
            return member;
        }
        return null;
    }

    @Override
    public UnionMember getByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> memberList = this.listByBusId(busId);
        if (ListUtil.isNotEmpty(memberList)) {
            for (UnionMember member : memberList) {
                if (unionId.equals(member.getUnionId())) {
                    return member;
                }
            }
        }
        return null;
    }

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    @Override
    public Page pageByIds(Page<UnionMember> page, List<Integer> memberIdList) throws Exception {
        if (page == null || memberIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (ListUtil.isEmpty(memberIdList)) {
            page.setRecords(new ArrayList<UnionMember>());
            return page;
        }
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("id", memberIdList)
                .orderBy("is_union_owner DESC,union_id ASC, id", true);
        return this.selectPage(page, entityWrapper);
    }

    @Override
    public Page pageMapByIdAndBusId(Page page, final Integer memberId, Integer busId, final String optionEnterpriseName) throws Exception {
        if (page == null || memberId == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        final UnionMember unionMember = this.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(unionMember.getUnionId());
        //(3)判断是否具有读权限
        if (!this.hasReadAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)查询操作
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" m")
                        .append(" LEFT JOIN t_union_member_discount mdFromMe ON mdFromMe.from_member_id = ").append(memberId)
                        .append("  AND mdFromMe.to_member_id = m.id")
                        .append(" LEFT JOIN t_union_member_discount mdToMe ON mdToMe.from_member_id = m.id")
                        .append("  AND mdToMe.to_member_id = ").append(memberId)
                        .append(" WHERE m.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND m.status != ").append(MemberConstant.STATUS_APPLY_IN)
                        .append("  AND m.union_id = ").append(unionMember.getUnionId());
                if (StringUtil.isNotEmpty(optionEnterpriseName)) {
                    sbSqlSegment.append(" AND m.enterprise_name LIKE '%").append(optionEnterpriseName).append("%'");
                }
                sbSqlSegment.append(" ORDER BY m.is_union_owner DESC, m.id ASC");
                return sbSqlSegment.toString();
            }
        };
        //盟员id
        String sqlSelect = " m.id memberId"
                //是否盟主
                + ", m.is_union_owner isUnionOwner"
                //盟员名称
                + ", m.enterprise_name enterpriseName"
                //创建时间
                + ", DATE_FORMAT(m.createtime, '%Y-%m-%d %T') createTime"
                //我给他的折扣
                + ", mdFromMe.discount discountFromMe"
                //他给我的折扣
                + ", mdToMe.discount discountToMe"
                //售卡分成比例
                + ", m.card_divide_percent cardDividePercent";
        wrapper.setSqlSelect(sqlSelect);
        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public Page pageOpportunityRatioMapByMember(Page page, final UnionMember unionMember) throws Exception {
        if (page == null || unionMember == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                return " toM"
                        + " LEFT JOIN t_union_opportunity_ratio rFromMe"
                        + "  ON rFromMe.from_member_id = " + unionMember.getId()
                        + "  AND rFromMe.to_member_id=toM.id"
                        + " LEFT JOIN t_union_opportunity_ratio rToMe"
                        + "  ON rToMe.to_member_id =" + unionMember.getId()
                        + "  AND rToMe.from_member_id = toM.id"
                        + " WHERE toM.del_status = " + CommonConstant.DEL_STATUS_NO
                        + "  AND toM.union_id = " + unionMember.getUnionId()
                        + "  AND toM.status in (" + MemberConstant.STATUS_IN
                        + "  AND toM.id != " + unionMember.getId()
                        + "    ," + MemberConstant.STATUS_APPLY_OUT + ")";
            }
        };
        //受惠方盟员身份id
        String sqlSelect = " toM.id toMemberId"
                //受惠方盟员身份名称
                + ", toM.enterprise_name toEnterpriseName"
                //我给TA的商机佣金比例
                + ", rFromMe.ratio ratioFromMe"
                //TA给我的商机佣金比例
                + ", rToMe.ratio ratioToMe";
        wrapper.setSqlSelect(sqlSelect);
        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public Page pagePreferentialUnCommitByUnionOwner(Page<UnionMember> page, UnionMember ownerMember) throws Exception {
        if (page == null || ownerMember == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (!ownerMember.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .ne("status", MemberConstant.STATUS_APPLY_IN)
                .eq("union_id", ownerMember.getUnionId())
                .ne("id", ownerMember.getId())
                .notExists(" SELECT pi.id FROM t_union_preferential_item pi"
                        + " WHERE pi.del_status = " + CommonConstant.DEL_STATUS_NO
                        + "  AND pi.status = " + PreferentialConstant.STATUS_PASS
                        + "  AND exists("
                        + "    SELECT pp.id FROM t_union_preferential_project pp"
                        + "    WHERE pp.del_status = " + CommonConstant.DEL_STATUS_NO
                        + "      AND pp.id = pi.project_id"
                        + "      AND pp.member_id = t_union_member.id"
                        + "  )");
        return this.selectPage(page, entityWrapper);
    }

    @Override
    public Page pageTransferMapByUnionOwner(Page page, final UnionMember ownerMember) throws Exception {
        if (page == null || ownerMember == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                return " m"
                        + " LEFT JOIN t_union_main_transfer mt ON mt.to_member_id = m.id"
                        + "  AND (mt.id IS NULL "
                        + "    OR ("
                        + "      mt.id IS NOT NULL"
                        + "      AND mt.del_status = " + CommonConstant.DEL_STATUS_NO
                        + "      AND mt.confirm_status = " + MainConstant.TRANSFER_CONFIRM_STATUS_HANDLING
                        + "    )"
                        + "  )"
                        + " WHERE m.del_status = " + CommonConstant.DEL_STATUS_NO
                        + "  AND m.status != " + MemberConstant.STATUS_APPLY_IN
                        + "  AND m.id != " + ownerMember.getId()
                        + "  AND m.union_id = " + ownerMember.getUnionId()
                        + " ORDER BY mt.id ASC";
            }
        };
        //盟员身份id
        String sqlSelect = " m.id memberId"
                //盟员名称
                + ", m.enterprise_name enterpriseName"
                //加入时间
                + ", m.createtime createTime"
                //盟主服务转移申请id
                + ", mt.id transferId";
        wrapper.setSqlSelect(sqlSelect);
        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public List<Map<String, Object>> listMapByIdAndBusId(final Integer memberId, final Integer busId
            , final String optionEnterpriseName) throws Exception {
        if (memberId == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        final UnionMember unionMember = this.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(unionMember.getUnionId());
        //(3)判断是否具有读权限
        if (!this.hasReadAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)查询操作
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" m2")
                        .append(" LEFT JOIN t_union_member_discount mdFromMe ON mdFromMe.from_member_id = ").append(memberId)
                        .append("  AND mdFromMe.to_member_id = m2.id")
                        .append(" LEFT JOIN t_union_member_discount mdToMe ON mdToMe.from_member_id = m2.id")
                        .append("  AND mdToMe.to_member_id = ").append(memberId)
                        .append(" WHERE m2.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND m2.status != ").append(MemberConstant.STATUS_APPLY_IN)
                        .append("  AND m2.union_id = ").append(unionMember.getUnionId());
                if (StringUtil.isNotEmpty(optionEnterpriseName)) {
                    sbSqlSegment.append(" AND m2.enterprise_name LIKE '%").append(optionEnterpriseName).append("%'");
                }
                sbSqlSegment.append(" ORDER BY m2.is_union_owner DESC, m2.id ASC");
                return sbSqlSegment.toString();
            }
        };
        //盟员id
        String sqlSelect = " m2.id memberId"
                //是否盟主
                + ", m2.is_union_owner isUnionOwner"
                //盟员名称
                + ", m2.enterprise_name enterpriseName"
                //创建时间
                + ", DATE_FORMAT(m2.createtime, '%Y-%m-%d %T') createTime"
                //我给他的折扣
                + ", mdFromMe.discount discountFromMe"
                //他给我的折扣
                + ", mdToMe.discount discountToMe"
                //售卡分成比例
                + ", m2.card_divide_percent cardDividePercent";
        wrapper.setSqlSelect(sqlSelect);
        return this.selectMaps(wrapper);
    }

    @Override
    public List<Map<String, Object>> listMapById(final Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        final UnionMember member = this.getById(memberId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        if (!this.hasWriteAuthority(member)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                return " m"
                        + " LEFT JOIN t_union_member_discount d ON m.id = d.to_member_id AND d.from_member_id = " + memberId
                        + " WHERE m.union_id = " + member.getUnionId()
                        + "  AND m.status in (" + MemberConstant.STATUS_IN + "," + MemberConstant.STATUS_APPLY_OUT + " )"
                        + "  AND m.del_status = " + CommonConstant.DEL_STATUS_NO
                        + " ORDER BY m.is_union_owner DESC, m.id ASC";
            }
        };
        //盟员id
        String sqlSelect = " m.id memberId"
                //是否盟主
                + ", m.is_union_owner isUnionOwner"
                //盟员名称
                + ", m.enterprise_name enterpriseName"
                //创建时间
                + ", DATE_FORMAT(m.createtime, '%Y-%m-%d %T') createTime"
                //企业地址
                + ", m.enterprise_address enterpriseAddress"
                //负责人电话
                + ", m.director_phone directorPhone"
                //地址经度
                + ", m.address_longitude addressLongitude"
                //地址维度
                + ", m.address_latitude addressLatitude"
                //我给他的折扣
                + ", d.discount discount";
        wrapper.setSqlSelect(sqlSelect);
        return this.selectMaps(wrapper);
    }

    @Override
    public List<UnionMember> listReadByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> result = new ArrayList<>();
        List<UnionMember> memberList = this.listByBusId(busId);
        if (ListUtil.isNotEmpty(memberList)) {
            result = filterWithReadAuthority(memberList);
            sortByIsUnionOwnerAndId(result);
        }
        return result;
    }

    @Override
    public List<UnionMember> listWriteByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> result = new ArrayList<>();
        List<UnionMember> memberList = this.listByBusId(busId);
        if (ListUtil.isNotEmpty(memberList)) {
            result = filterWithWriteAuthority(memberList);
            sortByIsUnionOwnerAndId(result);
        }
        return result;
    }

    @Override
    public List<UnionMember> listReadByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> result = new ArrayList<>();
        List<UnionMember> memberList = this.listByUnionId(unionId);
        if (ListUtil.isNotEmpty(memberList)) {
            result = filterWithReadAuthority(memberList);
            sortByIsUnionOwnerAndId(result);
        }
        return result;
    }

    @Override
    public List<UnionMember> listWriteByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> result = new ArrayList<>();
        List<UnionMember> memberList = this.listByUnionId(unionId);
        if (ListUtil.isNotEmpty(memberList)) {
            result = filterWithWriteAuthority(memberList);
            sortByIsUnionOwnerAndId(result);
        }
        return result;
    }

    @Override
    public List<UnionMember> listWriteWithValidUnionByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> result = new ArrayList<>();
        List<UnionMember> memberList = this.listWriteByBusId(busId);
        if (ListUtil.isNotEmpty(memberList)) {
            for (UnionMember member : memberList) {
                if (this.unionMainService.isUnionValid(member.getUnionId())) {
                    result.add(member);
                }
            }
        }
        return result;
    }

    @Override
    public List<UnionMember> listNotOwnerReadByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> result = new ArrayList<>();
        List<UnionMember> memberList = this.listReadByBusId(busId);
        if (ListUtil.isNotEmpty(memberList)) {
            for (UnionMember member : memberList) {
                if (member.getIsUnionOwner() == MemberConstant.IS_UNION_OWNER_NO) {
                    result.add(member);
                }
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> listReadWithUnionByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> memberList = this.listReadByBusId(busId);
        return listReadWithUnionByMemberList(memberList);
    }

    private List<Map<String, Object>> listReadWithUnionByMemberList(List<UnionMember> memberList) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(memberList)) {
            for (UnionMember member : memberList) {
                UnionMain unionMain = this.unionMainService.getById(member.getUnionId());
                Map<String, Object> map = new HashMap<>(16);
                map.put("unionMember", member);
                map.put("unionMain", unionMain);
                result.add(map);
            }
        }
        return result;
    }

    @Override
    public List<UnionMember> listByIds(List<Integer> memberIdList) throws Exception {
        List<UnionMember> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(memberIdList)) {
            for (Integer memberId : memberIdList) {
                UnionMember member = this.getById(memberId);
                if (member != null) {
                    result.add(member);
                }
            }
            sortByIsUnionOwnerAndId(result);
        }
        return result;
    }

    @Override
    public List<UnionMember> listExpired() throws Exception {
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("status", MemberConstant.STATUS_OUTING)
                .exists("SELECT o.id FROM t_union_member_out o"
                        + " WHERE o.del_status = " + CommonConstant.DEL_STATUS_NO
                        + "  AND o.apply_member_id = t_union_member.id"
                        + "  AND o.actual_out_time < '" + DateUtil.getCurrentDateString() + "'");
        return this.selectList(entityWrapper);
    }

    private List<UnionMember> filterWithReadAuthority(List<UnionMember> memberList) {
        List<UnionMember> result = new ArrayList<>();
        Integer status;
        for (UnionMember member : memberList) {
            status = member.getStatus();
            if (status == MemberConstant.STATUS_IN || status == MemberConstant.STATUS_APPLY_OUT || status == MemberConstant.STATUS_OUTING) {
                result.add(member);
            }
        }
        return result;
    }

    private List<UnionMember> filterWithWriteAuthority(List<UnionMember> memberList) {
        List<UnionMember> result = new ArrayList<>();
        Integer status;
        for (UnionMember member : memberList) {
            status = member.getStatus();
            if (status == MemberConstant.STATUS_IN || status == MemberConstant.STATUS_APPLY_OUT) {
                result.add(member);
            }
        }
        return result;
    }

    /**
     * order by is_union_owner desc, id asc
     */
    private void sortByIsUnionOwnerAndId(List<UnionMember> memberList) {
        Collections.sort(memberList, new Comparator<UnionMember>() {
            @Override
            public int compare(UnionMember o1, UnionMember o2) {
                if (o1.getIsUnionOwner() == MemberConstant.IS_UNION_OWNER_YES) {
                    if (o2.getIsUnionOwner() == MemberConstant.IS_UNION_OWNER_YES) {
                        return o2.getId().compareTo(o1.getId());
                    }
                    return 1;
                }
                if (o2.getIsUnionOwner() == MemberConstant.IS_UNION_OWNER_YES) {
                    return -1;
                }
                return o2.getId().compareTo(o1.getId());
            }
        });
    }

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByIdAndBusId(Integer memberId, Integer busId, UnionMemberVO memberVO) throws Exception {
        if (memberId == null || busId == null || memberVO == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember member = this.getByIdAndBusId(memberId, busId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(member.getUnionId());
        //(3)判断是否具有写权限
        if (!this.hasWriteAuthority(member)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)更新操作
        UnionMain unionMain = this.unionMainService.getById(member.getUnionId());
        UnionMember updateMember = new UnionMember();
        //盟员身份id
        updateMember.setId(memberId);
        //企业名称
        updateMember.setEnterpriseName(memberVO.getEnterpriseName());
        //企业地址
        updateMember.setEnterpriseAddress(memberVO.getEnterpriseAddress());
        //负责人名称
        updateMember.setDirectorName(memberVO.getDirectorName());
        //负责人联系电话
        updateMember.setDirectorPhone(memberVO.getDirectorPhone());
        //负责人邮箱
        updateMember.setDirectorEmail(memberVO.getDirectorEmail());
        //地址经度
        updateMember.setAddressLongitude(memberVO.getAddressLongitude());
        //地址纬度
        updateMember.setAddressLatitude(memberVO.getAddressLatitude());
        //短信通知手机号
        updateMember.setNotifyPhone(memberVO.getNotifyPhone());
        //地址省份编码
        updateMember.setAddressProvinceCode(memberVO.getAddressProvinceCode());
        //地址城市编码
        updateMember.setAddressCityCode(memberVO.getAddressCityCode());
        //地址区编码
        updateMember.setAddressDistrictCode(memberVO.getAddressDistrictCode());
        if (unionMain.getIsIntegral() == MainConstant.IS_INTEGRAL_YES) {
            //开启积分后，积分兑换率必填
            Double integralExchangePercent = memberVO.getIntegralExchangePercent();
            if (integralExchangePercent == null) {
                throw new BusinessException("联盟已开启积分功能，积分兑换率不能为空");
            }
            if (integralExchangePercent < 0.0 || integralExchangePercent > 30.0) {
                throw new BusinessException("积分兑换率有误(应在0-30之间)，请重新设置");
            }
            updateMember.setIntegralExchangePercent(integralExchangePercent);
        }
        this.update(updateMember);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCardDividePercentByIdAndBusId(Integer memberId, Integer busId, List<CardDividePercentVO> voList) throws Exception {
        if (memberId == null || busId == null || voList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionOwner = this.getByIdAndBusId(memberId, busId);
        if (unionOwner == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionValid(unionOwner.getUnionId());
        //(3)判断是否具有写权限
        if (!this.hasWriteAuthority(unionOwner)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断是否是盟主身份
        if (!unionOwner.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        //(5)判断操作信息是否过期
        List<UnionMember> memberList = this.listReadByUnionId(unionOwner.getUnionId());
        if (ListUtil.isEmpty(memberList) || memberList.size() != voList.size()) {
            throw new BusinessException("操作信息已过期，请刷新后重试");
        }
        //(6)判断操作信息是否正确
        List<UnionMember> updateMemberList = new ArrayList<>();
        BigDecimal cardDividePercentSum = BigDecimal.valueOf(0);
        Integer unionId = unionOwner.getUnionId();
        for (CardDividePercentVO vo : voList) {
            UnionMember member = this.getById(vo.getMemberId());
            if (member == null) {
                throw new BusinessException("无法操作不存在或已过期的盟员信息");
            }
            if (!member.getUnionId().equals(unionId)) {
                throw new BusinessException("无法操作不在该联盟下的盟员信息");
            }
            Double cardDividePercent = vo.getCardDividePercent();
            if (cardDividePercent < 0D || cardDividePercent > 100D) {
                throw new BusinessException("售卡分成比例不能小于0，且不能大于100");
            }
            UnionMember updateMember = new UnionMember();
            //盟员id
            updateMember.setId(member.getId());
            //盟员售卡分成比例
            updateMember.setCardDividePercent(cardDividePercent);
            //更新列表
            updateMemberList.add(updateMember);
            //累积售卡积分比例之和
            cardDividePercentSum = BigDecimalUtil.add(cardDividePercentSum, cardDividePercent);
        }
        if (cardDividePercentSum.doubleValue() != 100.0) {
            throw new BusinessException("售卡分成比例之和必须等于100");
        }
        //(7)批量更新
        this.updateBatch(updateMemberList);
    }

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    @Override
    public Integer countReadByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> memberList = this.listReadByUnionId(unionId);
        return memberList.size();
    }

    @Override
    public Integer countWriteByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> memberList = this.listWriteByUnionId(unionId);
        return memberList.size();
    }

    @Override
    public Integer countReadByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> memberList = this.listReadByBusId(busId);
        return memberList.size();
    }

    @Override
    public Integer countPreferentialUnCommitByUnionOwner(UnionMember ownerMember) throws Exception {
        if (ownerMember == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (!ownerMember.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .ne("status", MemberConstant.STATUS_APPLY_IN)
                .eq("union_id", ownerMember.getUnionId())
                .notExists(" SELECT pi.id FROM t_union_preferential_item pi"
                        + " WHERE pi.del_status = " + CommonConstant.DEL_STATUS_NO
                        + "  AND pi.status = " + PreferentialConstant.STATUS_PASS
                        + "  AND exists("
                        + "    SELECT pp.id FROM t_union_preferential_project pp"
                        + "    WHERE pp.del_status = " + CommonConstant.DEL_STATUS_NO
                        + "      AND pp.id = pi.project_id"
                        + "      AND pp.member_id = t_union_member.id"
                        + "  )");
        return this.selectCount(entityWrapper);
    }

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    @Override
    public boolean isUnionOwner(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> memberList = this.listByBusId(busId);
        if (ListUtil.isNotEmpty(memberList)) {
            for (UnionMember member : memberList) {
                if (member.getIsUnionOwner() == MemberConstant.IS_UNION_OWNER_YES) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasReadAuthority(UnionMember unionMember) throws Exception {
        if (unionMember == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        switch (unionMember.getStatus()) {
            case MemberConstant.STATUS_APPLY_IN:
                return false;
            case MemberConstant.STATUS_IN:
                return true;
            case MemberConstant.STATUS_APPLY_OUT:
                return true;
            case MemberConstant.STATUS_OUTING:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean hasWriteAuthority(UnionMember unionMember) throws Exception {
        if (unionMember == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        switch (unionMember.getStatus()) {
            case MemberConstant.STATUS_APPLY_IN:
                return false;
            case MemberConstant.STATUS_IN:
                return true;
            case MemberConstant.STATUS_APPLY_OUT:
                return true;
            case MemberConstant.STATUS_OUTING:
                return false;
            default:
                return false;
        }
    }

    //******************************************* Object As a Service - get ********************************************


    @Override
    public UnionMember getById(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMember result;
        //(1)cache
        String memberIdKey = RedisKeyUtil.getMemberIdKey(memberId);
        if (this.redisCacheUtil.exists(memberIdKey)) {
            String tempStr = this.redisCacheUtil.get(memberIdKey);
            result = JSONArray.parseObject(tempStr, UnionMember.class);
            return result;
        }
        //(2)db
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", memberId)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = this.selectOne(entityWrapper);
        setCache(result, memberId);
        return result;
    }

    //******************************************* Object As a Service - list *******************************************

    @Override
    public List<UnionMember> listByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> result;
        //(1)get in cache
        String busIdKey = RedisKeyUtil.getMemberBusIdKey(busId);
        if (this.redisCacheUtil.exists(busIdKey)) {
            String tempStr = this.redisCacheUtil.get(busIdKey);
            result = JSONArray.parseArray(tempStr, UnionMember.class);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);
        result = this.selectList(entityWrapper);
        setCache(result, busId, MemberConstant.REDIS_KEY_MEMBER_BUS_ID);
        return result;
    }

    @Override
    public List<UnionMember> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> result;
        //(1)get in cache
        String unionIdKey = RedisKeyUtil.getMemberUnionIdKey(unionId);
        if (this.redisCacheUtil.exists(unionIdKey)) {
            String tempStr = this.redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionMember.class);
            return result;
        }
        //(2)get in db
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);
        result = this.selectList(entityWrapper);
        setCache(result, unionId, MemberConstant.REDIS_KEY_MEMBER_UNION_ID);
        return result;
    }

    //******************************************* Object As a Service - save *******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionMember newMember) throws Exception {
        if (newMember == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insert(newMember);
        this.removeCache(newMember);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionMember> newMemberList) throws Exception {
        if (newMemberList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        this.insertBatch(newMemberList);
        this.removeCache(newMemberList);
    }

    //******************************************* Object As a Service - remove *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        UnionMember member = this.getById(memberId);
        removeCache(member);
        //(2)remove in db logically
        UnionMember removeMember = new UnionMember();
        removeMember.setId(memberId);
        removeMember.setDelStatus(CommonConstant.DEL_STATUS_YES);
        this.updateById(removeMember);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> memberIdList) throws Exception {
        if (memberIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<UnionMember> memberList = new ArrayList<>();
        for (Integer memberId : memberIdList) {
            UnionMember member = this.getById(memberId);
            memberList.add(member);
        }
        removeCache(memberList);
        //(2)remove in db logically
        List<UnionMember> removeMemberList = new ArrayList<>();
        for (Integer memberId : memberIdList) {
            UnionMember removeMember = new UnionMember();
            removeMember.setId(memberId);
            removeMember.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeMemberList.add(removeMember);
        }
        this.updateBatchById(removeMemberList);
    }

    //******************************************* Object As a Service - update *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionMember updateMember) throws Exception {
        if (updateMember == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        Integer memberId = updateMember.getId();
        UnionMember member = this.getById(memberId);
        removeCache(member);
        //(2)update db
        this.updateById(updateMember);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionMember> updateMemberList) throws Exception {
        if (updateMemberList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)remove cache
        List<Integer> memberIdList = new ArrayList<>();
        for (UnionMember updateMember : updateMemberList) {
            memberIdList.add(updateMember.getId());
        }
        List<UnionMember> memberList = new ArrayList<>();
        for (Integer memberId : memberIdList) {
            UnionMember member = this.getById(memberId);
            memberList.add(member);
        }
        removeCache(memberList);
        //(2)update db
        this.updateBatchById(updateMemberList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionMember newMember, Integer memberId) {
        if (memberId == null) {
            return; //do nothing,just in case
        }
        String memberIdKey = RedisKeyUtil.getMemberIdKey(memberId);
        this.redisCacheUtil.set(memberIdKey, newMember);
    }

    private void setCache(List<UnionMember> newMemberList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            return; //do nothing,just in case
        }
        String foreignIdKey;
        switch (foreignIdType) {
            case MemberConstant.REDIS_KEY_MEMBER_BUS_ID:
                foreignIdKey = RedisKeyUtil.getMemberBusIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newMemberList);
                break;
            case MemberConstant.REDIS_KEY_MEMBER_UNION_ID:
                foreignIdKey = RedisKeyUtil.getMemberUnionIdKey(foreignId);
                this.redisCacheUtil.set(foreignIdKey, newMemberList);
                break;
            default:
                break;
        }
    }

    private void removeCache(UnionMember member) {
        if (member == null) {
            return;
        }
        Integer memberId = member.getId();
        String memberIdKey = RedisKeyUtil.getMemberIdKey(memberId);
        this.redisCacheUtil.remove(memberIdKey);
        Integer busId = member.getBusId();
        if (busId != null) {
            String busIdKey = RedisKeyUtil.getMemberBusIdKey(busId);
            this.redisCacheUtil.remove(busIdKey);
        }
        Integer unionId = member.getUnionId();
        if (unionId != null) {
            String unionIdKey = RedisKeyUtil.getMemberUnionIdKey(unionId);
            this.redisCacheUtil.remove(unionIdKey);
        }
    }

    private void removeCache(List<UnionMember> memberList) {
        if (ListUtil.isEmpty(memberList)) {
            return;
        }
        List<Integer> memberIdList = new ArrayList<>();
        for (UnionMember member : memberList) {
            memberIdList.add(member.getId());
        }
        List<String> memberIdKeyList = RedisKeyUtil.getMemberIdKey(memberIdList);
        this.redisCacheUtil.remove(memberIdKeyList);
        List<String> busIdKeyList = getForeignIdKeyList(memberList, MemberConstant.REDIS_KEY_MEMBER_BUS_ID);
        if (ListUtil.isNotEmpty(busIdKeyList)) {
            this.redisCacheUtil.remove(busIdKeyList);
        }
        List<String> unionIdKeyList = getForeignIdKeyList(memberList, MemberConstant.REDIS_KEY_MEMBER_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            this.redisCacheUtil.remove(unionIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionMember> memberList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case MemberConstant.REDIS_KEY_MEMBER_BUS_ID:
                for (UnionMember member : memberList) {
                    Integer busId = member.getBusId();
                    if (busId != null) {
                        String busIdKey = RedisKeyUtil.getMemberBusIdKey(busId);
                        result.add(busIdKey);
                    }
                }
                break;
            case MemberConstant.REDIS_KEY_MEMBER_UNION_ID:
                for (UnionMember member : memberList) {
                    Integer unionId = member.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = RedisKeyUtil.getMemberUnionIdKey(unionId);
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
