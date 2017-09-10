package com.gt.union.member.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
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

    @Override
    public List<UnionMember> listUnionMemberByBusIdAndIsUnionOwnerAndStatus(Integer busId, Integer isUnionOwner
            , Integer status) throws Exception {
        if (busId == null || isUnionOwner == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("bus_id", busId)
                .eq("is_union_owner", isUnionOwner)
                .eq("status", status)
                .orderBy("id", true);
        return this.selectList(entityWrapper);
    }

    @Override
    public Integer countUnionMemberByUnionIdAndStatus(Integer unionId, Integer status) throws Exception {
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
    public UnionMember getByUnionIdAndBusId(Integer unionId, Integer busId) throws Exception{
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<UnionMember>();
        entityWrapper.eq("del_status",CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("bus_id", busId);
        UnionMember unionMember = this.selectOne(entityWrapper);
        return unionMember;
    }
}
