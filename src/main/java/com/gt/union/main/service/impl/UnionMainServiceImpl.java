package com.gt.union.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
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
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）如果缓存中已存在，说明有效，直接返回
        String unionMainValidKey = RedisKeyUtil.getUnionMainValidKey(id);
        if (this.redisCacheUtil.exists(unionMainValidKey)) {
            return;
        }
        // （2）检查联盟有效性
        UnionMain unionMain = this.getUnionMainById(id);
        if (unionMain == null) {
            throw new BusinessException("联盟不存在");
        }
        // （3）检查盟主有效性
        UnionMember unionOwner = this.unionMemberService.getUnionOwnerByUnionId(id);
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
}
