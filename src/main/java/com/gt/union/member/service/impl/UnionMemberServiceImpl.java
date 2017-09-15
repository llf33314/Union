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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    public UnionMember getUnionOwnerByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("is_union_owner", MemberConstant.IS_UNION_OWNER_YES)
                .eq("status", MemberConstant.STATUS_APPLY_IN)
                .eq("union_id", unionId);
        return this.selectOne(entityWrapper);
    }

    /**
     * 根据商家id和联盟id查询盟员信息
     *
     * @param unionId
     * @param busId
     * @return
     */
    @Override
    public UnionMember getByUnionIdAndBusId(Integer unionId, Integer busId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<UnionMember>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("bus_id", busId);
        UnionMember unionMember = this.selectOne(entityWrapper);
        return unionMember;
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
     * @param memberId
     * @return
     */
    @Override
    public UnionMember getById(Integer memberId) {
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<UnionMember>();
        entityWrapper.eq("id", memberId)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        return this.selectOne(entityWrapper);
    }

    //------------------------------------------ list(include page) ---------------------------------------------------

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
    public Page pageMapByMemberIdAndBusId(Page page, final Integer memberId, Integer busId, final String optionEnterpriseName) throws Exception {
        if (page == null || memberId == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        final UnionMember unionMember = this.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException("没有盟员权限");
        }

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
                        .append("  AND m.union_id = ").append(unionMember.getUnionId())
                        .append("  AND mdFromMe.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND mdToMe.del_status = ").append(CommonConstant.DEL_STATUS_NO);
                if (StringUtil.isNotEmpty(optionEnterpriseName)) {
                    sbSqlSegment.append(" AND m.enterprise_name LIKE %").append(optionEnterpriseName).append("%");
                }
                sbSqlSegment.append(" ORDER BY m.is_union_owner DESC, m.id ASC");
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" m.id memberId") //盟员id
                .append(", m.is_union_owner isUnionOwner") //是否盟主
                .append(", m.enterprise_name enterpriseName") //盟员名称
                .append(", DATE_FORMAT(m.createtime, '%Y-%m-%d %T') createtime") //创建时间
                .append(", mdFromMe.discount discountFromMe") //我给他的折扣
                .append(", mdToMe.discount discountToMe") //他给我的折扣
                .append(", m.card_divide_percent cardDividePercent"); //售卡分成比例
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }

    /**
     * 根据商家id、是否盟主、盟员状态，获取商家的盟员身份列表信息
     *
     * @param busId        {not null} 商家id
     * @param isUnionOwner {not null} 是否盟主
     * @param statusArray  {not null} 盟员状态
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionMember> listByBusIdAndIsUnionOwnerAndStatus(Integer busId, Integer isUnionOwner, Object[] statusArray) throws Exception {
        if (busId == null || isUnionOwner == null || statusArray == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .eq("is_union_owner", isUnionOwner)
                .in("status", statusArray)
                .orderBy("id", true);

        return this.selectList(entityWrapper);
    }

    /**
     * 根据盟员id和商家id，获取所有与该盟员通属一个联盟的盟员信息
     *
     * @param memberId {not null} 盟员id
     * @param busId    {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> listMapByMemberIdAndBusId(final Integer memberId, final Integer busId) throws Exception {
        if (memberId == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        final UnionMember unionMember = this.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException("没有盟员权限");
        }

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
                        .append("  AND m.union_id = ").append(unionMember.getUnionId())
                        .append("  AND mdFromMe.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND mdToMe.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append(" ORDER BY m.is_union_owner DESC, m.id ASC");
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" m.id memberId") //盟员id
                .append(", m.is_union_owner isUnionOwner") //是否盟主
                .append(", m.enterprise_name enterpriseName") //盟员名称
                .append(", DATE_FORMAT(m.createtime, '%Y-%m-%d %T') createtime") //创建时间
                .append(", mdFromMe.discount discountFromMe") //我给他的折扣
                .append(", mdToMe.discount discountToMe") //他给我的折扣
                .append(", m.card_divide_percent cardDividePercent"); //售卡分成比例
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMaps(wrapper);
    }

    /**
     * 根据联盟id查询盟员列表
     *
     * @param unionId
     * @return
     */
    @Override
    public List<UnionMember> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<UnionMember>();
        entityWrapper.eq("union_id", unionId);
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        return this.selectList(entityWrapper);
    }

    /**
     * 根据商家id查询商家加入的有效盟员信息列表
     *
     * @param busId
     * @return
     */
    @Override
    public List<UnionMember> listValidByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<UnionMember>();
        entityWrapper.eq("bus_id", busId);
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        entityWrapper.andNew()
                .eq("status", MemberConstant.STATUS_IN)
                .or()
                .eq("status", MemberConstant.STATUS_APPLY_OUT);
        return this.selectList(entityWrapper);
    }

    /**
     * 根据联盟ids和盟员ids查询匹配的盟员列表
     *
     * @param unionIds  联盟ids
     * @param memberIds 盟员ids
     * @return
     */
    @Override
    public List<UnionMember> listByUnionIdsAndUnionMemberIds(List<Integer> unionIds, List<Integer> memberIds) {
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<UnionMember>();
        entityWrapper.in("union_id", unionIds.toArray());
        entityWrapper.in("id", memberIds.toArray());
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        entityWrapper.andNew()
                .eq("status", MemberConstant.STATUS_IN)
                .or()
                .eq("status", MemberConstant.STATUS_APPLY_OUT);
        return this.selectList(entityWrapper);
    }

    /**
     * 根据联盟id和盟员状态，获取盟员身份列表信息
     *
     * @param unionId {not null} 联盟id
     * @param status  {not null} 盟员状态
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionMember> listByUnionIdAndStatus(Integer unionId, Object[] status) throws Exception {
        if (unionId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .in("status", status);
        return this.selectList(entityWrapper);
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
        if (!this.isUnionMemberValid(busId, memberId)) {
            throw new BusinessException("不具有盟员身份或已过期");
        }
        UnionMember unionMember = this.getByIdAndBusId(memberId, busId);
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
                throw new BusinessException("联盟已开启积分功能，积分兑换率必填");
            }
            if (integralExchangePercent < 0D || integralExchangePercent > 30D) {
                throw new BusinessException("积分兑换率有误，请重新设置");
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
        // (1)判断操作人身份权限
        if (!this.isUnionMemberValid(busId, memberId)) {
            throw new BusinessException("不具有盟员身份或已过期");
        }
        UnionMember unionOwner = this.getByIdAndBusId(memberId, busId);
        if (unionOwner.getIsUnionOwner() != MemberConstant.IS_UNION_OWNER_YES) {
            throw new BusinessException("非盟主身份无法操作");
        }
        // (2)判断操作信息是否过时
        List<UnionMember> unionMemberList = this.listByUnionIdAndStatus(unionOwner.getUnionId(), new Object[]{MemberConstant.STATUS_IN_APPLYOUT_OUTING});
        if (ListUtil.isEmpty(unionMemberList) || unionMemberList.size() != cardDividePercentVOList.size()) {
            throw new BusinessException("操作信息已过时，请刷新后重试");
        }
        // (3)判断操作信息是否正确
        List<UnionMember> updateUnionMemberList = new ArrayList<>();
        BigDecimal cardDividePercentSum = BigDecimal.valueOf(0);
        Integer unionId = unionOwner.getUnionId();
        for (CardDividePercentVO vo : cardDividePercentVOList) {
            UnionMember unionMember = this.getById(vo.getMemberId());
            if (unionMember == null) {
                throw new BusinessException("操作不存在或已过期的盟员信息");
            }
            if (!unionMember.getUnionId().equals(unionId)) {
                throw new BusinessException("无法操作不在该联盟下的盟员信息");
            }
            Double cardDividePercent = vo.getCardDividePercent();
            if (cardDividePercent < 0D || cardDividePercent > 100D) {
                throw new BusinessException("售卡分成比例不能小于0，且不能大于100");
            }
            UnionMember updateUnionMember = new UnionMember();
            updateUnionMember.setId(unionMember.getId());
            updateUnionMember.setCardDividePercent(cardDividePercent);
            updateUnionMemberList.add(updateUnionMember);
            cardDividePercentSum = BigDecimalUtil.add(cardDividePercentSum, cardDividePercent);
        }
        if (cardDividePercentSum.doubleValue() != 100D) {
            throw new BusinessException("售卡分成比例之和必须等于100");
        }
        // (4)批量更新
        this.updateBatchById(updateUnionMemberList);
    }

    //------------------------------------------------- save ----------------------------------------------------------
    //------------------------------------------------- count ---------------------------------------------------------

    @Override
    public Integer countByUnionIdAndStatus(Integer unionId, Integer status) throws Exception {
        if (unionId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("status", status);
        return this.selectCount(entityWrapper);
    }

    /**
     * 根据联盟id统计有效盟员数，即已加入的、申请退出的和退盟过渡期的
     *
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    @Override
    public Integer countValidMemberByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .in("status", new Object[]{MemberConstant.STATUS_IN, MemberConstant.STATUS_APPLY_OUT, MemberConstant.STATUS_OUTING});
        return this.selectCount(entityWrapper);
    }

    /**
     * 根据商家id，统计盟员数
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public Integer countValidMemberByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .in("status", new Object[]{MemberConstant.STATUS_IN, MemberConstant.STATUS_APPLY_OUT, MemberConstant.STATUS_OUTING});
        return this.selectCount(entityWrapper);
    }

    //------------------------------------------------ boolean --------------------------------------------------------

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

    @Override
    public boolean isUnionOwner(Integer unionId, Integer busId) throws Exception {
        if (unionId == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("is_union_owner", MemberConstant.IS_UNION_OWNER_YES)
                .eq("union_id", unionId)
                .eq("bus_id", busId);
        UnionMember unionMember = this.selectOne(entityWrapper);
        return unionMember != null ? true : false;
    }

    @Override
    public boolean hasUnionMemberAuthority(Integer unionId, Integer busId) throws Exception {
        if (unionId == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("bus_id", busId)
                .andNew()
                .eq("status", MemberConstant.STATUS_IN)
                .or()
                .eq("status", MemberConstant.STATUS_APPLY_OUT);
        UnionMember unionMember = this.selectOne(entityWrapper);
        return unionMember != null ? true : false;
    }

    @Override
    public boolean hasUnionMemberAuthority(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", memberId)
                .in("status", new Object[]{MemberConstant.STATUS_IN, MemberConstant.STATUS_APPLY_OUT});
        UnionMember unionMember = this.selectOne(entityWrapper);
        return unionMember != null ? true : false;
    }

    /**
     * 根据商家id和盟员id，检查商家信息和盟员信息是否匹配
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员id
     * @throws Exception
     */
    @Override
    public boolean isUnionMemberValid(Integer busId, Integer memberId) throws Exception {
        if (busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", memberId)
                .eq("bus_id", busId)
                .in("status", new Object[]{MemberConstant.STATUS_IN, MemberConstant.STATUS_APPLY_OUT, MemberConstant.STATUS_OUTING});
        UnionMember unionMember = this.selectOne(entityWrapper);
        return unionMember != null ? true : false;
    }

    @Override
    public boolean hasUnionMemberAuthority(UnionMember unionMember) {
        if(unionMember == null){
            return false;
        }
        if(unionMember.getDelStatus().equals(CommonConstant.DEL_STATUS_YES)){
            return false;
        }
        if(!(unionMember.getStatus().equals(MemberConstant.STATUS_IN) || unionMember.getStatus().equals(MemberConstant.STATUS_APPLY_OUT))){
            return false;
        }
        return true;
    }

    @Override
    public List<UnionMember> listByBusId(Integer busId) {
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<UnionMember>();
        entityWrapper.eq("bus_id", busId);
        entityWrapper.eq("del_status",CommonConstant.DEL_STATUS_NO);
        return this.selectList(entityWrapper);
    }

	@Override
	public List<UnionMember> listAllByBusId(Integer busId) {
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<UnionMember>();
        entityWrapper.eq("bus_id", busId);
        return this.selectList(entityWrapper);
	}

    @Override
    public List<UnionMember> getByUnionIdAndBusIdWidthOutDelStatus(Integer unionId, Integer busId) {
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<UnionMember>();
        entityWrapper.eq("bus_id", busId);
        entityWrapper.eq("union_id", unionId);
        return this.selectList(entityWrapper);
    }
}
