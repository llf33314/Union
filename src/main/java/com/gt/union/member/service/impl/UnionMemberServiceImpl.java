package com.gt.union.member.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.StringUtil;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.mapper.UnionMemberMapper;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.stereotype.Service;

import java.util.List;

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
                .andNew()
                .eq("status", MemberConstant.STATUS_IN)
                .or()
                .eq("status", MemberConstant.STATUS_APPLY_OUT);
        UnionMember unionMember = this.selectOne(entityWrapper);
        return unionMember != null ? true : false;
    }

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
     * 根据商家id、是否盟主、盟员状态，获取商家的盟员身份列表信息
     *
     * @param busId        {not null} 商家id
     * @param isUnionOwner {not null} 是否盟主
     * @param status       {not null} 盟员状态
     * @param orStatus     或操作，盟员状态，为空时不参与查询
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionMember> listByBusIdAndIsUnionOwnerAndStatus(Integer busId, Integer isUnionOwner
            , Integer status, Integer orStatus) throws Exception {
        if (busId == null || isUnionOwner == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .eq("is_union_owner", isUnionOwner);
        if (orStatus != null) {
            entityWrapper.andNew()
                    .eq("status", status)
                    .or()
                    .eq("status", orStatus);
        } else {
            entityWrapper.eq("status", status);
        }
        entityWrapper.orderBy("id", true);

        return this.selectList(entityWrapper);
    }

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
     * 根据盟员id，分页获取所有与盟员同属一个联盟的盟员信息
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
        final UnionMember unionMember = this.getByMemberIdAndBusId(memberId, busId);
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
     * 根据盟员id和商家id，获取盟员信息
     *
     * @param memberId {not null} 盟员id
     * @param busId    {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public UnionMember getByMemberIdAndBusId(Integer memberId, Integer busId) throws Exception {
        if (memberId == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", memberId)
                .eq("bus_id", busId);
        return this.selectOne(entityWrapper);
    }

	@Override
	public List<UnionMember> listByUnionId(Integer unionId) throws Exception{
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<UnionMember>();
        entityWrapper.eq("union_id", unionId);
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
		return this.selectList(entityWrapper);
	}

	@Override
	public List<UnionMember> listValidByBusId(Integer busId) throws Exception{
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

	@Override
	public UnionMember getById(Integer memberId) {
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<UnionMember>();
        entityWrapper.eq("id", memberId);
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        return this.selectOne(entityWrapper);
	}
}
