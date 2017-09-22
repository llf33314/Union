package com.gt.union.member.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.StringUtil;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟成员 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionMemberServiceImpl extends ServiceImpl<UnionMemberMapper, UnionMember> implements IUnionMemberService {
    @Autowired
    private IUnionMainService unionMainService;

    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据联盟id获取该联盟盟主的盟员信息
     *
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    @Override
    public UnionMember getOwnerByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("is_union_owner", MemberConstant.IS_UNION_OWNER_YES)
                .eq("union_id", unionId);
        return this.selectOne(entityWrapper);
    }

    /**
     * 根据商家id，获取具有盟主权限的盟员身份信息
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public UnionMember getOwnerByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .eq("is_union_owner", MemberConstant.IS_UNION_OWNER_YES)
                .in("status", new Object[]{MemberConstant.STATUS_IN
                        , MemberConstant.STATUS_APPLY_OUT
                        , MemberConstant.STATUS_OUTING});
        return this.selectOne(entityWrapper);
    }

    /**
     * 根据盟员id和商家id，获取盟员信息
     *
     * @param memberId {not null} 盟员id
     * @param busId    {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public UnionMember getByIdAndBusId(Integer memberId, Integer busId) throws Exception {
        if (memberId == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", memberId)
                .eq("bus_id", busId);
        return this.selectOne(entityWrapper);
    }

    /**
     * 根据id获取盟员信息
     *
     * @param memberId {not null} 盟员id
     * @return
     */
    @Override
    public UnionMember getById(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<UnionMember>();
        entityWrapper.eq("id", memberId)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        return this.selectOne(entityWrapper);
    }

    /**
     * 根据商家id和联盟id，获取商家在该联盟的盟员身份
     *
     * @param busId   {not null} 商家id
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    @Override
    public UnionMember getByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("bus_id", busId);
        return this.selectOne(entityWrapper);
    }

    //------------------------------------------ list(include page) ---------------------------------------------------

    /**
     * 根据盟员身份id列表，分页获取盟员身份列表信息
     *
     * @param page         {not null} 分页对象
     * @param memberIdList {not null} 盟员身份id列表
     * @return
     * @throws Exception
     */
    @Override
    public Page pageByIds(Page page, List<Integer> memberIdList) throws Exception {
        if (page == null || memberIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (ListUtil.isEmpty(memberIdList)) {
            page.setRecords(new ArrayList());
            return page;
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("id", memberIdList)
                .orderBy("is_union_owner DESC,union_id ASC, id", true);
        return this.selectPage(page, entityWrapper);
    }

    /**
     * 根据盟员id和商家id，分页获取所有与该盟员同属一个联盟的盟员信息
     *
     * @param page                 {not null} 分页对象
     * @param memberId             {not null} 盟员id
     * @param busId                {not null} 商家id
     * @param optionEnterpriseName 可选项，盟员名称，模糊匹配
     * @return
     * @throws Exception
     */
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
        this.unionMainService.checkUnionMainValid(unionMember.getUnionId());
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
        StringBuilder sbSqlSelect = new StringBuilder(" m.id memberId") //盟员id
                .append(", m.is_union_owner isUnionOwner") //是否盟主
                .append(", m.enterprise_name enterpriseName") //盟员名称
                .append(", DATE_FORMAT(m.createtime, '%Y-%m-%d %T') createTime") //创建时间
                .append(", mdFromMe.discount discountFromMe") //我给他的折扣
                .append(", mdToMe.discount discountToMe") //他给我的折扣
                .append(", m.card_divide_percent cardDividePercent"); //售卡分成比例
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }

    /**
     * 根据盟员身份对象，分页获取与盟员之间的商机佣金比例设置列表信息
     *
     * @param page        {not null} 分页对象
     * @param unionMember {not null} 盟员身份对象
     * @return
     * @throws Exception
     */
    @Override
    public Page pageOpportunityBrokerageRatioMapByMember(Page page, final UnionMember unionMember) throws Exception {
        if (page == null || unionMember == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" toM")
                        .append(" LEFT JOIN t_union_opportunity_brokerage_ratio rFromMe")
                        .append("  ON rFromMe.from_member_id = ").append(unionMember.getId())
                        .append("  AND rFromMe.to_member_id=toM.id")
                        .append(" LEFT JOIN t_union_opportunity_brokerage_ratio rToMe")
                        .append("  ON rToMe.to_member_id =").append(unionMember.getId())
                        .append("  AND rToMe.from_member_id = toM.id")
                        .append(" WHERE toM.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND toM.union_id = ").append(unionMember.getUnionId())
                        .append("  AND toM.status in (").append(MemberConstant.STATUS_IN)
                        .append("    ,").append(MemberConstant.STATUS_APPLY_OUT).append(")");
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" toM.id toMemberId") //受惠方盟员身份id
                .append(", toM.enterprise_name toEnterpriseName") //受惠方盟员身份名称
                .append(", rFromMe.ratio ratioFromMe") //我给TA的商机佣金比例
                .append(", rToMe.ratio ratioToMe"); //TA给我的商机佣金比例
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }

    /**
     * 盟主分页获取本联盟中所有未提交优惠项目的盟员信息，未提交是指：不存在被审核通过的优惠服务项
     *
     * @param page        {not null} 分页对象
     * @param ownerMember {not null} 盟主身份
     * @return
     * @throws Exception
     */
    @Override
    public Page pagePreferentialUnCommitByUnionOwner(Page page, UnionMember ownerMember) throws Exception {
        if (page == null || ownerMember == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (!ownerMember.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .ne("status", MemberConstant.STATUS_APPLY_IN)
                .eq("union_id", ownerMember.getUnionId())
                .notExists(new StringBuilder(" SELECT pi.id FROM t_union_preferential_item pi")
                        .append(" WHERE pi.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND pi.status = ").append(PreferentialConstant.STATUS_PASS)
                        .append("  AND exists(")
                        .append("    SELECT pp.id FROM t_union_preferential_project pp")
                        .append("    WHERE pp.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("      AND pp.id = pi.project_id")
                        .append("      AND pp.member_id = t_union_member.id")
                        .append("  )")
                        .toString());
        return this.selectPage(page, entityWrapper);
    }

    /**
     * 根据盟主信息，分页获取本联盟中所有其他盟员是否具有盟主权限转移的相关记录
     *
     * @param page        {not null} 分页对象
     * @param ownerMember {not null} 盟主身份
     * @return
     * @throws Exception
     */
    @Override
    public Page pageTransferMapByUnionOwner(Page page, final UnionMember ownerMember) throws Exception {
        if (page == null || ownerMember == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" m")
                        .append(" LEFT JOIN t_union_main_transfer mt ON mt.to_member_id = m.id")
                        .append("  AND (mt.id IS NULL ")
                        .append("    OR (")
                        .append("      mt.id IS NOT NULL")
                        .append("      AND mt.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("      AND mt.confirm_status = ").append(MainConstant.TRANSFER_CONFIRM_STATUS_HANDLING)
                        .append("    )")
                        .append("  )")
                        .append(" WHERE m.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND m.status != ").append(MemberConstant.STATUS_APPLY_IN)
                        .append("  AND m.id != ").append(ownerMember.getId())
                        .append("  AND m.union_id = ").append(ownerMember.getUnionId())
                        .append(" ORDER BY mt.id ASC");
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" m.id memberId") //盟员身份id
                .append(", m.enterprise_name enterpriseName") //盟员名称
                .append(", m.createtime createTime") //加入时间
                .append(", mt.id transferId"); //盟主服务转移申请id
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }

    /**
     * 根据盟员id和商家id，获取所有与该盟员通属一个联盟的盟员信息
     *
     * @param memberId             {not null} 盟员id
     * @param busId                {not null} 商家id
     * @param optionEnterpriseName 可选项 盟员名称
     * @return
     * @throws Exception
     */
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
        this.unionMainService.checkUnionMainValid(unionMember.getUnionId());
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
        StringBuilder sbSqlSelect = new StringBuilder(" m2.id memberId") //盟员id
                .append(", m2.is_union_owner isUnionOwner") //是否盟主
                .append(", m2.enterprise_name enterpriseName") //盟员名称
                .append(", DATE_FORMAT(m2.createtime, '%Y-%m-%d %T') createTime") //创建时间
                .append(", mdFromMe.discount discountFromMe") //我给他的折扣
                .append(", mdToMe.discount discountToMe") //他给我的折扣
                .append(", m2.card_divide_percent cardDividePercent"); //售卡分成比例
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMaps(wrapper);
    }

    /**
     * 根据商家id，获取商家所有具有读权限的盟员身份列表信息
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionMember> listReadByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .in("status", new Object[]{MemberConstant.STATUS_IN
                        , MemberConstant.STATUS_APPLY_OUT
                        , MemberConstant.STATUS_OUTING})
                .orderBy("is_union_owner DESC,id", true);
        return this.selectList(entityWrapper);
    }

    /**
     * 根据商家id，获取商家所有具有写权限的盟员身份列表信息
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionMember> listWriteByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .in("status", new Object[]{MemberConstant.STATUS_IN
                        , MemberConstant.STATUS_APPLY_OUT})
                .orderBy("is_union_owner DESC,id", true);
        return this.selectList(entityWrapper);
    }

    /**
     * 根据商家id，获取所有具有写权限，且所在联盟是有效的盟员身份列表信息
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionMember> listWriteWithValidUnionByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> result = new ArrayList<>();
        //(1)获取所有具有写权限的盟员身份列表信息
        List<UnionMember> writeMemberList = this.listWriteByBusId(busId);
        if (ListUtil.isEmpty(writeMemberList)) {
            return null;
        }
        //(2)判断盟员身份所在的联盟是否有效
        for (int i = 0, size = writeMemberList.size(); i < size; i++) {
            UnionMember writeMember = writeMemberList.get(i);
            if (this.unionMainService.isUnionMainValid(writeMember.getUnionId())) {
                result.add(writeMember);
            }
        }
        return result;
    }

    /**
     * 根据商家id，获取商家所有具有读权限、且不是盟主的盟员身份列表信息
     *
     * @param busId {not null}
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionMember> listNotOwnerReadByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .eq("is_union_owner", MemberConstant.IS_UNION_OWNER_NO)
                .in("status", new Object[]{MemberConstant.STATUS_IN
                        , MemberConstant.STATUS_APPLY_OUT
                        , MemberConstant.STATUS_OUTING})
                .orderBy("id", true);
        return this.selectList(entityWrapper);
    }

    /**
     * 根据商家id，获取商家所有具有读权限的盟员身份列表信息，以及对应所在的联盟信息
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> listReadMapByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> unionMemberList = this.listReadByBusId(busId);
        return listMapByMemberList(unionMemberList);
    }

    /**
     * 根据盟员列表，匹配对应所在的联盟信息
     *
     * @param unionMemberList 盟员列表
     * @return
     * @throws Exception
     */
    private List<Map<String, Object>> listMapByMemberList(List<UnionMember> unionMemberList) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionMemberList)) {
            for (UnionMember unionMember : unionMemberList) {
                UnionMain unionMain = this.unionMainService.getById(unionMember.getUnionId());
                Map<String, Object> map = new HashMap<>();
                map.put("unionMember", unionMember);
                map.put("unionMain", unionMain);
                result.add(map);
            }
        }
        return result;
    }

    /**
     * 根据联盟id，获取该联盟下所有具有读权限的盟员身份列表信息
     *
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionMember> listReadByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .in("status", new Object[]{MemberConstant.STATUS_IN
                        , MemberConstant.STATUS_APPLY_OUT
                        , MemberConstant.STATUS_OUTING})
                .orderBy("id", true);
        return this.selectList(entityWrapper);
    }

    /**
     * 根据联盟id，获取该联盟下所有具有写权限的盟员身份列表信息
     *
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionMember> listWriteByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .in("status", new Object[]{MemberConstant.STATUS_IN
                        , MemberConstant.STATUS_APPLY_OUT})
                .orderBy("id", true);
        return this.selectList(entityWrapper);
    }

    /**
     * 根据盟员id列表，获取盟员列表信息
     *
     * @param memberIdList 盟员id列表
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionMember> listByIds(List<Integer> memberIdList) throws Exception {
        List<UnionMember> result = new ArrayList<>();
        if (ListUtil.isEmpty(memberIdList)) {
            return result;
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("id", memberIdList)
                .orderBy("is_union_owner DESC,id", true);
        result = this.selectList(entityWrapper);
        return result;
    }

    //------------------------------------------------- update --------------------------------------------------------

    /**
     * 根据盟员身份id、商家id和更新内容实体，更新盟员信息
     *
     * @param memberId      {not null} 盟员身份id
     * @param busId         {not null} 商家id
     * @param unionMemberVO {not null} 更新内容实体
     * @throws Exception
     */
    @Override
    public void updateByIdAndBusId(Integer memberId, Integer busId, UnionMemberVO unionMemberVO) throws Exception {
        if (memberId == null || busId == null || unionMemberVO == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionMember = this.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionMember.getUnionId());
        //(3)判断是否具有写权限
        if (!this.hasWriteAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)更新操作
        UnionMain unionMain = this.unionMainService.getById(unionMember.getUnionId());
        UnionMember updateUnionMember = new UnionMember();
        updateUnionMember.setId(memberId); //盟员身份id
        updateUnionMember.setEnterpriseName(unionMemberVO.getEnterpriseName()); //企业名称
        updateUnionMember.setEnterpriseAddress(unionMemberVO.getEnterpriseAddress()); //企业地址
        updateUnionMember.setDirectorName(unionMemberVO.getDirectorName()); //负责人名称
        updateUnionMember.setDirectorPhone(unionMemberVO.getDirectorPhone()); //负责人联系电话
        updateUnionMember.setDirectorEmail(unionMemberVO.getDirectorEmail()); //负责人邮箱
        updateUnionMember.setAddressLongitude(unionMemberVO.getAddressLongitude()); //地址经度
        updateUnionMember.setAddressLatitude(unionMemberVO.getAddressLatitude()); //地址纬度
        updateUnionMember.setNotifyPhone(unionMemberVO.getNotifyPhone()); //短信通知手机号
        updateUnionMember.setAddressProvinceCode(unionMemberVO.getAddressProvinceCode()); //地址省份编码
        updateUnionMember.setAddressCityCode(unionMemberVO.getAddressCityCode()); //地址城市编码
        updateUnionMember.setAddressDistrictCode(unionMemberVO.getAddressDistrictCode()); //地址区编码
        if (unionMain.getIsIntegral() == MainConstant.IS_INTEGRAL_YES) { //开启积分后，积分兑换率必填
            Double integralExchangePercent = unionMemberVO.getIntegralExchangePercent();
            if (integralExchangePercent == null) {
                throw new BusinessException("联盟已开启积分功能，积分兑换率不能为空");
            }
            if (integralExchangePercent < 0D || integralExchangePercent > 30D) {
                throw new BusinessException("积分兑换率有误(应在0-30之间)，请重新设置");
            }
            updateUnionMember.setIntegralExchangePercent(integralExchangePercent);
        }
        this.updateById(updateUnionMember);
    }

    /**
     * 根据盟员身份id、商家id和更新内容实体，更新盟员售卡分成信息
     *
     * @param memberId                {not null} 盟员身份id
     * @param busId                   {not null} 商家id
     * @param cardDividePercentVOList {not null} 更新内容实体
     * @throws Exception
     */
    @Override
    public void updateCardDividePercentByIdAndBusId(Integer memberId, Integer busId, List<CardDividePercentVO> cardDividePercentVOList) throws Exception {
        if (memberId == null || busId == null || cardDividePercentVOList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember unionOwner = this.getByIdAndBusId(memberId, busId);
        if (unionOwner == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionOwner.getUnionId());
        //(3)判断是否具有写权限
        if (!this.hasWriteAuthority(unionOwner)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断是否是盟主身份
        if (!unionOwner.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        //(5)判断操作信息是否过期
        List<UnionMember> unionMemberList = this.listReadByUnionId(unionOwner.getUnionId());
        if (ListUtil.isEmpty(unionMemberList) || unionMemberList.size() != cardDividePercentVOList.size()) {
            throw new BusinessException("操作信息已过期，请刷新后重试");
        }
        //(6)判断操作信息是否正确
        List<UnionMember> updateUnionMemberList = new ArrayList<>();
        BigDecimal cardDividePercentSum = BigDecimal.valueOf(0);
        Integer unionId = unionOwner.getUnionId();
        for (CardDividePercentVO vo : cardDividePercentVOList) {
            UnionMember unionMember = this.getById(vo.getMemberId());
            if (unionMember == null) {
                throw new BusinessException("无法操作不存在或已过期的盟员信息");
            }
            if (!unionMember.getUnionId().equals(unionId)) {
                throw new BusinessException("无法操作不在该联盟下的盟员信息");
            }
            Double cardDividePercent = vo.getCardDividePercent();
            if (cardDividePercent < 0D || cardDividePercent > 100D) {
                throw new BusinessException("售卡分成比例不能小于0，且不能大于100");
            }
            UnionMember updateUnionMember = new UnionMember();
            updateUnionMember.setId(unionMember.getId()); //盟员id
            updateUnionMember.setCardDividePercent(cardDividePercent); //盟员售卡分成比例
            updateUnionMemberList.add(updateUnionMember); //更新列表
            cardDividePercentSum = BigDecimalUtil.add(cardDividePercentSum, cardDividePercent); //累积售卡积分比例之和
        }
        if (cardDividePercentSum.doubleValue() != 100D) {
            throw new BusinessException("售卡分成比例之和必须等于100");
        }
        //(7)批量更新
        this.updateBatchById(updateUnionMemberList);
    }

    //------------------------------------------------- save ----------------------------------------------------------
    //------------------------------------------------- count ---------------------------------------------------------

    /**
     * 根据联盟id，统计具有读权限的盟员数
     *
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    @Override
    public Integer countReadByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .in("status", new Object[]{MemberConstant.STATUS_IN
                        , MemberConstant.STATUS_APPLY_OUT
                        , MemberConstant.STATUS_OUTING});
        return this.selectCount(entityWrapper);
    }

    /**
     * 根据联盟id，统计具有写权限的盟员数
     *
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    @Override
    public Integer countWriteByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .in("status", new Object[]{MemberConstant.STATUS_IN
                        , MemberConstant.STATUS_APPLY_OUT});
        return this.selectCount(entityWrapper);
    }


    /**
     * 根据商家id，统计具有读权限的盟员身份数
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public Integer countReadByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("busId", busId)
                .in("status", new Object[]{MemberConstant.STATUS_IN
                        , MemberConstant.STATUS_APPLY_OUT
                        , MemberConstant.STATUS_OUTING});
        return this.selectCount(entityWrapper);
    }

    /**
     * 根据盟主身份，统计本联盟中所有未提交优惠项目的盟员数，未提交是指：不存在被审核通过的优惠服务项
     *
     * @param ownerMember {not null} 盟主身份
     * @return
     * @throws Exception
     */
    @Override
    public Integer countPreferentialUnCommitByUnionOwner(UnionMember ownerMember) throws Exception {
        if (ownerMember == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (!ownerMember.getIsUnionOwner().equals(MemberConstant.IS_UNION_OWNER_YES)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_NEED_OWNER);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .ne("status", MemberConstant.STATUS_APPLY_IN)
                .eq("union_id", ownerMember.getUnionId())
                .notExists(new StringBuilder(" SELECT pi.id FROM t_union_preferential_item pi")
                        .append(" WHERE pi.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND pi.status = ").append(PreferentialConstant.STATUS_PASS)
                        .append("  AND exists(")
                        .append("    SELECT pp.id FROM t_union_preferential_project pp")
                        .append("    WHERE pp.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("      AND pp.id = pi.project_id")
                        .append("      AND pp.member_id = t_union_member.id")
                        .append("  )")
                        .toString());
        return this.selectCount(entityWrapper);
    }

    //------------------------------------------------ boolean --------------------------------------------------------

    /**
     * 根据商家id判断该商家是否是某联盟的盟主
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public boolean isUnionOwner(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("is_union_owner", MemberConstant.IS_UNION_OWNER_YES)
                .eq("bus_id", busId);
        UnionMember unionMember = this.selectOne(entityWrapper);
        return unionMember != null ? true : false;
    }

    /**
     * 根据盟员对象，判断是否具有读权限，只有已加盟、申请退盟状态和退盟过渡期才有，未加盟、申请加盟和已退盟不具有
     *
     * @param unionMember {not null} 盟员对象
     * @return
     * @throws Exception
     */
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

    /**
     * 根据盟员对象，判断是否具有写权限，只有已加盟和申请退盟状态才有，未加盟、申请加盟、退盟过渡期和已退盟不具有
     *
     * @param unionMember {not null} 盟员对象
     * @return
     * @throws Exception
     */
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
}
