package com.gt.union.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.RedisKeyUtil;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.mapper.UnionMainMapper;
import com.gt.union.main.service.IUnionMainPermitService;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 联盟主表 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionMainServiceImpl extends ServiceImpl<UnionMainMapper, UnionMain> implements IUnionMainService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IBusUserService busUserService;

    @Autowired
    private IUnionMainPermitService unionMainPermitService;

    @Override
    public void checkUnionMainValid(Integer id) throws Exception {
        UnionMain unionMain = this.getUnionMainById(id);
        checkUnionMainValid(unionMain);
    }

    @Override
    public void checkUnionMainValid(UnionMain unionMain) throws Exception {
        if (unionMain == null) {
            throw new BusinessException("联盟不存在");
        }
        // （1）如果缓存中已存在，说明有效，直接返回
        String unionMainValidKey = RedisKeyUtil.getUnionMainValidKey(unionMain.getId());
        if (this.redisCacheUtil.exists(unionMainValidKey)) {
            return;
        }
        // （3）检查盟主有效性
        UnionMember unionOwner = this.unionMemberService.getUnionOwnerByUnionId(unionMain.getId());
        if (unionOwner == null) {
            throw new BusinessException("联盟没有盟主");
        }
        Integer unionOwnerBusUserId = unionOwner.getBusId();
        if (!this.busUserService.isBusUserValid(unionOwnerBusUserId)) {
            throw new BusinessException("联盟的盟主信息不存在或已过期");
        }
        // （4）检查盟主联盟服务许可
        if (!this.unionMainPermitService.hasUnionMainPermit(unionOwnerBusUserId)) {
            throw new BusinessException("联盟的盟主没有盟主服务或盟主服务已过期");
        }
        // （5）重新存入缓存
        this.redisCacheUtil.set(unionMainValidKey, "1");
    }

    @Override
    public UnionMain getUnionMainById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", id);
        return this.selectOne(entityWrapper);
    }

    /**
     * 根据商家id，获取所有具有盟员身份的联盟信息，即创建的联盟+加入的联盟
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public List<UnionMain> listUnionMainByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .exists(new StringBuilder("SELECT m.id FROM t_union_member m")
                        .append(" WHERE m.bus_id = ").append(busId)
                        .append("  AND m.status != ").append(MemberConstant.STATUS_APPLY_IN)
                        .append("  AND m.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .toString());
        return this.selectList(entityWrapper);
    }

    @Override
    public List<UnionMain> listValidUnionMainByBusId(Integer busId) throws Exception {
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .exists(new StringBuilder("SELECT m.id FROM t_union_member m")
                        .append(" WHERE m.bus_id = ").append(busId)
                        .append("  AND ( m.status = ").append(MemberConstant.STATUS_IN)
                        .append(" OR ").append("m.status = ").append(MemberConstant.STATUS_APPLY_OUT).append(" )")
                        .append("  AND m.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .toString());
        return this.selectList(entityWrapper);
    }

    /**
     * 根据商家id，分页获取具有盟员身份的联盟信息，即创建的联盟+加入的联盟
     *
     * @param page {not null} 分页对象
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public Page<UnionMain> PageUnionMainByBusId(Page page, Integer busId) throws Exception {
        if (page == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .exists(new StringBuilder("SELECT m.id FROM t_union_member m")
                        .append(" WHERE m.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND m.status != ").append(MemberConstant.STATUS_APPLY_IN)
                        .append("  AND m.bus_id = ").append(busId)
                        .toString());
        return this.selectPage(page, entityWrapper);
    }
}
