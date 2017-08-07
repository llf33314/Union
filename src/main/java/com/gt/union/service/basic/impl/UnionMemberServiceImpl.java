package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.basic.UnionApplyConstant;
import com.gt.union.common.constant.basic.UnionDiscountConstant;
import com.gt.union.common.constant.basic.UnionMemberConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParameterException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.basic.UnionMember;
import com.gt.union.mapper.basic.UnionMemberMapper;
import com.gt.union.service.basic.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟成员列表 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@Service
public class UnionMemberServiceImpl extends ServiceImpl<UnionMemberMapper, UnionMember> implements IUnionMemberService {

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Override
    public Page listMember(Page page, final Integer unionId, final String enterpriseName) throws Exception {
        if (page == null) {
            throw new Exception("UnionMemberServiceImpl.listUnionMember():参数page不能为空!");
        }
        if (unionId == null) {
            throw new Exception("UnionMemberServiceImpl.listUnionMember():参数unionId不能为空!");
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" m");
                sbSqlSegment.append(" LEFT JOIN t_union_discount d4 on d4.from_bus_id = m.bus_id ")
                        .append(" LEFT JOIN t_union_discount d2 on d2.to_bus_id = m.bus_id ")
                        .append(" LEFT JOIN t_union_apply a on a.union_member_id = m.id ")
                        .append(" LEFT JOIN t_union_apply_info i on i.union_apply_id = a.id ")
                        .append(" WHERE m.del_status=").append(UnionMemberConstant.DEL_STATUS_NO)
                        .append("    AND d4.del_status = ").append(UnionDiscountConstant.DEL_STATUS_NO)
                        .append("    AND d2.del_status = ").append(UnionMemberConstant.DEL_STATUS_NO)
                        .append("    AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("    AND m.union_id = ").append(unionId);
                if (StringUtil.isNotEmpty(enterpriseName)) {
                    sbSqlSegment.append(" AND i.enterprise_name LIKE '%").append(enterpriseName).append("%' ");
                }
                sbSqlSegment.append(" ORDER BY m.is_nuion_owner DESC,m.createtime DESC ");
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append(" m.is_nuion_owner isUnionOwner ")
            .append(", m.id id ")
            .append(", m.bus_id busId ")
            .append(", DATE_FORMAT(m.createtime, '%Y-%m-%d %T') createtime ")
            .append(", d4.discount fromDiscount ")
            .append(", d2.discount toDiscount ")
            .append(", i.sell_divide_proportion sellDivideProportion ")
            .append(", i.enterprise_name enterpriseName ");
        wrapper.setSqlSelect(sbSqlSelect.toString());

        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public Page listOut(Page page, final Integer unionId, final Integer outStatus) throws Exception {
        if (page == null) {
            throw new Exception("UnionMemberServiceImpl.listUnionMember():参数page不能为空!");
        }
        if (unionId == null) {
            throw new Exception("UnionMemberServiceImpl.listUnionMember():参数unionId不能为空!");
        }
        if (outStatus == null) {
            throw new Exception("UnionMemberServiceImpl.listUnionMember():参数outStatus不能为空!");
        }

        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" m");
                sbSqlSegment.append(" LEFT JOIN t_union_apply a on a.union_member_id = m.id ")
                        .append(" LEFT JOIN t_union_apply_info i on i.union_apply_id = a.id ")
                        .append(" WHERE m.del_status=").append(UnionMemberConstant.DEL_STATUS_NO)
                        .append("    AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("    AND m.union_id = ").append(unionId)
                        .append("    AND m.out_staus = ").append(outStatus);
                sbSqlSegment.append(" ORDER BY m.apply_out_time DESC ");
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append(" DATE_FORMAT(m.apply_out_time, '%Y-%m-%d %T') applyOutTime ")
                .append(", m.out_reason outReason ")
                .append(", DATE_FORMAT(m.confirm_out_time, '%Y-%m-%d %T') confirmOutTime ")
                .append(", DATEDIFF(now(), m.confirm_out_time) remainOutTime ")
                .append(", i.enterprise_name enterpriseName ");
        wrapper.setSqlSelect(sbSqlSelect.toString());

        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public Page listTransfer(Page page, final Integer unionId, final Integer busId) throws Exception {
        if (page == null) {
            throw new Exception("UnionMemberServiceImpl.listUnionMember():参数page不能为空!");
        }
        if (unionId == null) {
            throw new Exception("UnionMemberServiceImpl.listUnionMember():参数unionId不能为空!");
        }
        if (busId == null) {
            throw new Exception("UnionMemberServiceImpl.listUnionMember():参数busId不能为空!");
        } else {
            if (!this.isUnionOwner(unionId, busId)) {
                throw new Exception("UnionMemberServiceImpl.listUnionMember():当前用户不能是盟主，无法查询!");
            }
        }

        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" m");
                sbSqlSegment.append(" LEFT JOIN t_union_apply a on a.union_member_id = m.id ")
                        .append(" LEFT JOIN t_union_apply_info i on i.union_apply_id = a.id ")
                        .append(" WHERE m.del_status = ").append(UnionMemberConstant.DEL_STATUS_NO)
                        .append("    AND m.is_nuion_owner = ").append(UnionMemberConstant.IS_UNION_OWNER_NO)
                        .append("    AND m.out_staus = ").append(UnionMemberConstant.OUT_STATUS_NORMAL)
                        .append("    AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("    AND m.union_id = ").append(unionId);
                sbSqlSegment.append(" ORDER BY m.apply_out_time DESC ");
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append(" DATE_FORMAT(m.createtime, '%Y-%m-%d %T') createtime ")
                .append(", m.id id ")
                .append(", i.enterprise_name enterpriseName ");
        wrapper.setSqlSelect(sbSqlSelect.toString());

        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public Map<String, Object> getDetail(final Integer unionMemberId) throws Exception {
        if (unionMemberId == null) {
            throw new Exception("UnionMemberServiceImpl.getDetail():参数unionMemberId不能为空!");
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" m");
                sbSqlSegment.append(" LEFT JOIN t_union_apply a on a.union_member_id = m.id ")
                        .append(" LEFT JOIN t_union_apply_info i on i.union_apply_id = a.id ")
                        .append(" WHERE m.del_status=").append(UnionMemberConstant.DEL_STATUS_NO)
                        .append("    AND m.id = ").append(unionMemberId)
                        .append("    AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS);
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append(" DATE_FORMAT(m.createtime, '%Y-%m-%d %T') createtime ")
                .append(", i.enterprise_name enterpriseName ")
                .append(", i.director_name directorName ")
                .append(", i.director_phone directorPhone ")
                .append(", i.director_email directorEmail ")
                .append(", i.bus_address busAddress ");
        wrapper.setSqlSelect(sbSqlSelect.toString());

        return this.selectMap(wrapper);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateIsUnionMember(Integer unionMemberId, Integer unionId, Integer busId, Integer isUnionOwner) throws Exception {
        if (unionMemberId == null) {
            throw new Exception("UnionMemberServiceImpl.updateIsUnionMember():参数unionMemberId不能为空!");
        }
        if (unionId == null) {
            throw new Exception("UnionMemberServiceImpl.updateIsUnionMember():参数unionId不能为空!");
        }
        if (busId == null) {
            throw new Exception("UnionMemberServiceImpl.updateIsUnionMember():参数busId不能为空!");
        }
        if (isUnionOwner == null) {
            throw new Exception("UnionMemberServiceImpl.updateIsUnionMember():参数isUnionOwner不能为空!");
        } else {
            switch (isUnionOwner) {
                case UnionMemberConstant.IS_UNION_OWNER_YES:
                    break;
                /*case UnionMemberConstant.IS_UNION_OWNER_NO:
                    throw new Exception("UnionMemberServiceImpl.updateIsUnionMember():不支持的参数值(isUnionOwner=" + isUnionOwner + ")!");*/
                default:
                    throw new Exception("UnionMemberServiceImpl.updateIsUnionMember():不支持的参数值(isUnionOwner=" + isUnionOwner + ")!");
            }
        }

        //判断盟员是否未申请退盟
        EntityWrapper newOwnerWrapper = new EntityWrapper();
        newOwnerWrapper.eq("id", unionMemberId)
                .eq("del_status", UnionMemberConstant.DEL_STATUS_NO)
                .eq("out_staus", UnionMemberConstant.OUT_STATUS_NORMAL);
        if (!this.isExistUnionMember(newOwnerWrapper)) {
            throw new Exception("UnionMemberServiceImpl.updateIsUnionMember():盟员信息不存在，或已申请退盟，或正在退盟过渡期!");
        }

        //判断当前用户是否用户盟主的身份
        EntityWrapper oldOwnerWrapper = new EntityWrapper();
        oldOwnerWrapper.eq("del_status", UnionMemberConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("bus_id", busId)
                .eq("is_nuion_owner", UnionMemberConstant.IS_UNION_OWNER_YES);
        if (!this.isExistUnionMember(oldOwnerWrapper)) {
            throw new Exception("UnionMemberServiceImpl.updateIsUnionMember():当前用户不是盟主，没有转移盟主身份的权限!");
        }

        //盟主取消身份
        UnionMember oldOwner = new UnionMember();
        oldOwner.setIsNuionOwner(UnionMemberConstant.IS_UNION_OWNER_NO);
        this.update(oldOwner, oldOwnerWrapper);

        //盟员增加盟主身份
        UnionMember newOwner = new UnionMember();
        newOwner.setIsNuionOwner(UnionMemberConstant.IS_UNION_OWNER_YES);
        this.update(newOwner, newOwnerWrapper);
    }

    @Override
    public boolean isUnionOwner(Integer unionId, Integer busId) throws Exception {
        if (unionId == null) {
            throw new Exception("UnionMemberServiceImpl.isUnionOwner():参数unionId不能为空!");
        }
        if (busId == null) {
            throw new Exception("UnionMemberServiceImpl.isUnionOwner():参数busId不能为空!");
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", UnionMemberConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("bus_id", busId)
                .eq("is_nuion_owner", UnionMemberConstant.IS_UNION_OWNER_YES)
                .eq("out_staus", UnionMemberConstant.OUT_STATUS_NORMAL);

        return this.isExistUnionMember(entityWrapper);
    }

    @Override
    public boolean isExistUnionMember(Wrapper wrapper) throws Exception {
        if (wrapper == null) {
            throw new Exception("UnionMemberServiceImpl.isExistUnionMember():参数wrapper不能为空!");
        }

        return this.selectCount(wrapper) > 0 ? true : false;
    }

    @Override
    public Page selectUnionBrokerageList(Page page, final Integer unionId, final Integer busId) throws Exception {
        if (page == null) {
            throw new ParameterException("参数错误");
        }
        if (unionId == null) {
            throw new ParameterException("参数错误");
        }
        if (busId == null) {
            throw new ParameterException("参数错误");
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" t1");
                sbSqlSegment.append(" LEFT JOIN t_union_brokerage t2 on t1.bus_id = t2.to_bus_id")
                        .append(" and t2.from_bus_id = ").append(busId)
                        .append(" and t2.union_id = ").append(unionId)
                        .append(" and t2.del_status = ").append(0)
                        .append(" LEFT JOIN t_union_brokerage t3 on t1.bus_id = t3.from_bus_id")
                        .append(" and t3.to_bus_id = ").append(busId)
                        .append(" and t3.union_id = ").append(unionId)
                        .append(" and t3.del_status = ").append(0)
                        .append(" LEFT JOIN t_union_apply t4 on t4.union_member_id = t1.id")
                        .append(" LEFT JOIN t_union_apply_info t5 on t5.union_apply_id = t4.id")
                        .append(" where t1.union_id = ").append(unionId)
                        .append(" and t1.del_status = ").append(0);
                sbSqlSegment.append(" ORDER BY t1.id asc ");
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append("t1.id, t1.bus_id, t2.id as from_id, t2.brokerage_ratio as from_brokerage, t3.id as to_id, t3.brokerage_ratio as to_brokerage, t6.enterprise_name");
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public void isMemberValid(UnionMember unionMember) throws Exception {
        if(unionMember == null){
            throw new BusinessException("未加入联盟");
        }
        if(unionMember.getOutStaus() == UnionMemberConstant.OUT_STATUS_PERIOD){
            throw new BusinessException("已退出联盟");
        }
    }

    @Override
    public UnionMember getUnionMember(Integer busId, Integer unionId) {
        UnionMember unionMember = null;
        if ( redisCacheUtil.exists( "unionMember:" + unionId + ":" + busId) ) {
            // 1.1 存在则从redis 读取
            unionMember = (UnionMember) redisCacheUtil.get("unionMember:" + unionId + ":" + busId );
        } else {
            // 2. 不存在则从数据库查询
            EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<UnionMember>();
            entityWrapper.eq("union_id",unionId);
            entityWrapper.eq("bus_id",busId);
            entityWrapper.eq("del_status",0);
            unionMember = this.selectOne(entityWrapper);
            // 写入 Redis 操作
            if(CommonUtil.isNotEmpty(unionMember)){
                redisCacheUtil.set( "unionMember:" + unionId + ":" + busId, unionMember );
            }
        }
        return unionMember;
    }

    @Override
    public void isMemberValid(Integer busId, Integer unionId) throws Exception {
        UnionMember unionMember = getUnionMember(busId,unionId);
        isMemberValid(unionMember);
    }

    @Override
    public List<Map<String, Object>> getUnionMemberList(final Integer unionId) {
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" m");
                sbSqlSegment.append(" LEFT JOIN t_union_apply a on a.union_member_id = m.id ")
                        .append(" LEFT JOIN t_union_apply_info i on i.union_apply_id = a.id ")
                        .append(" WHERE m.union_id = ").append(unionId)
                        .append("    AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND m.del_status = ").append(UnionMemberConstant.DEL_STATUS_NO);
                sbSqlSegment.append(" ORDER BY m.apply_out_time DESC ");
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append(", m.id id ")
                .append(", i.enterprise_name enterpriseName ");
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return null;
    }

    @Override
    public int getUnionMemberCount(Integer applyBusId) {
        EntityWrapper entityWrapper = new EntityWrapper<UnionMember>();
        entityWrapper.eq("bus_id",applyBusId);
        entityWrapper.eq("del_status",0);
        return this.selectCount(entityWrapper);
    }
}
