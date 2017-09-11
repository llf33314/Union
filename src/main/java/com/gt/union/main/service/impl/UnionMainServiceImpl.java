package com.gt.union.main.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.RedisKeyUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.main.constant.MainConstant;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.entity.UnionMainCharge;
import com.gt.union.main.entity.UnionMainDict;
import com.gt.union.main.mapper.UnionMainMapper;
import com.gt.union.main.service.IUnionMainChargeService;
import com.gt.union.main.service.IUnionMainDictService;
import com.gt.union.main.service.IUnionMainPermitService;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.main.vo.UnionMainChargeVO;
import com.gt.union.main.vo.UnionMainVO;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private IUnionMainChargeService unionMainChargeService;

    @Autowired
    private IUnionMainDictService unionMainDictService;

    @Override
    public void checkUnionMainValid(Integer id) throws Exception {
        UnionMain unionMain = this.getById(id);
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
    public UnionMain getById(Integer id) throws Exception {
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
    public List<UnionMain> listByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .exists(new StringBuilder("SELECT m.id FROM t_union_member m")
                        .append(" WHERE m.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND m.status != ").append(MemberConstant.STATUS_APPLY_IN)
                        .append("  AND m.bus_id = ").append(busId)
                        .toString());
        return this.selectList(entityWrapper);
    }

    /**
     * 根据商家id，分页获取具有盟员身份的联盟信息，即创建的联盟+加入的联盟
     *
     * @param page  {not null} 分页对象
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    @Override
    public Page<UnionMain> PageByBusId(Page page, Integer busId) throws Exception {
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

    /**
     * 根据盟员id、商家id和VO对象，更新联盟信息
     *
     * @param memberId    {not null} 盟员id
     * @param busId       {not null} 商家id
     * @param unionMainVO {not null} 更新信息
     * @throws Exception
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateByMemberIdAndBusIdAndVO(Integer memberId, Integer busId, UnionMainVO unionMainVO) throws Exception {
        if (memberId == null || busId == null || unionMainVO == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)判断盟员身份
        UnionMember unionMember = this.unionMemberService.getByMemberIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException("没有盟员权限");
        }
        // (2)判断联盟有效性
        Integer unionId = unionMember.getUnionId();
        this.checkUnionMainValid(unionId);
        // (3)判断是否具有盟主权限
        if (unionMember.getIsUnionOwner() != MemberConstant.IS_UNION_OWNER_YES) {
            throw new BusinessException("没有盟主权限");
        }
        // (4)联盟积分开启后不能关闭
        UnionMain unionMain = this.getById(unionId);
        if (unionMain == null) {
            throw new BusinessException("联盟不存在");
        }
        if (MainConstant.IS_INTEGRAL_YES == unionMain.getIsIntegral() && MainConstant.IS_INTEGRAL_NO == unionMainVO.getIsIntegral()) {
            throw new BusinessException("积分开启后不可关闭");
        }
        // (5)联盟更新信息
        UnionMain updateUnionMain = new UnionMain();
        updateUnionMain.setId(unionId); //联盟id
        updateUnionMain.setName(unionMainVO.getUnionName()); //联盟名称
        String unionImg = unionMainVO.getUnionImg();
        if (StringUtil.isNotEmpty(unionImg) && unionImg.indexOf("/upload/") > -1) {
            updateUnionMain.setImg(unionImg.split("/upload/")[1]); //联盟图标
        }
        updateUnionMain.setJoinType(unionMainVO.getJoinType()); //加盟方式
        updateUnionMain.setIllustration(unionMainVO.getUnionIllustration()); //联盟说明
        updateUnionMain.setIsIntegral(unionMainVO.getIsIntegral()); //是否开启积分

        // （6）联盟收费实体-黑卡
        UnionMainCharge blackUnionMainCharge = this.unionMainChargeService.getByUnionIdAndType(unionId, MainConstant.CHARGE_TYPE_BLACK);
        if (blackUnionMainCharge == null) {
            throw new BusinessException("联盟黑卡设置不存在");
        }
        UnionMainChargeVO unionMainChargeVO = unionMainVO.getUnionMainChargeVO();
        UnionMainCharge updateBlackUnionMainCharge = new UnionMainCharge();
        updateBlackUnionMainCharge.setId(blackUnionMainCharge.getId());
        Integer blackIsAvailable = unionMainChargeVO.getBlackIsAvailable();
        updateBlackUnionMainCharge.setIsAvailable(blackIsAvailable); //黑卡是否启用
        Integer blackIsCharge = unionMainChargeVO.getBlackIsCharge();
        updateBlackUnionMainCharge.setIsCharge(blackIsCharge); //黑卡是否收费
        if (MainConstant.CHARGE_IS_AVAILABLE_YES == blackIsAvailable && MainConstant.CHARGE_IS_CHARGE_YES == blackIsCharge) {
            Double blackChargePrice = unionMainChargeVO.getBlackChargePrice();
            if (blackChargePrice < 0) {
                throw new BusinessException("黑卡价格不能小于0");
            }
            updateBlackUnionMainCharge.setChargePrice(blackChargePrice); //黑卡收费价格
            Integer blackValidityDay = unionMainChargeVO.getBlackValidityDay();
            if (blackValidityDay < 0) {
                throw new BusinessException("黑卡有效期不能小于0");
            }
            updateBlackUnionMainCharge.setValidityDay(blackValidityDay); //黑卡有效期
            updateBlackUnionMainCharge.setIllustration(unionMainChargeVO.getBlackIllustration()); //黑卡说明
        } else {
            updateBlackUnionMainCharge.setChargePrice(0D); //黑卡收费价格
            updateBlackUnionMainCharge.setValidityDay(0); //黑卡有效期
            updateBlackUnionMainCharge.setIllustration(""); //黑卡说明
        }

        // (7)联盟收费实体-红卡
        UnionMainCharge redUnionMainCharge = this.unionMainChargeService.getByUnionIdAndType(unionId, MainConstant.CHARGE_TYPE_RED);
        if (redUnionMainCharge == null) {
            throw new BusinessException("联盟红卡设置不存在");
        }
        UnionMainCharge updateRedUnionMainCharge = new UnionMainCharge();
        updateRedUnionMainCharge.setId(redUnionMainCharge.getId());
        Integer redIsAvailable = unionMainChargeVO.getRedIsAvailable();
        updateRedUnionMainCharge.setIsAvailable(redIsAvailable); //红卡是否启用
        Integer redIsCharge = unionMainChargeVO.getRedIsCharge();
        updateRedUnionMainCharge.setIsCharge(redIsCharge); //红卡是否收费
        if (MainConstant.CHARGE_IS_AVAILABLE_YES == redIsAvailable && MainConstant.CHARGE_IS_CHARGE_YES == redIsCharge) {
            Double redChargePrice = unionMainChargeVO.getRedChargePrice();
            if (redChargePrice < 0) {
                throw new BusinessException("红卡价格不能小于0");
            }
            updateRedUnionMainCharge.setChargePrice(redChargePrice); //红卡收费价格
            Integer redValidityDay = unionMainChargeVO.getRedValidityDay();
            if (redValidityDay < 0) {
                throw new BusinessException("红卡有效期不能小于0");
            }
            updateRedUnionMainCharge.setValidityDay(redValidityDay); //红卡有效期
            updateRedUnionMainCharge.setIllustration(unionMainChargeVO.getBlackIllustration()); //红卡说明
        } else {
            updateRedUnionMainCharge.setChargePrice(0D); //红卡收费价格
            updateRedUnionMainCharge.setValidityDay(0); //红卡有效期
            updateRedUnionMainCharge.setIllustration(""); //红卡说明
        }
        // (8)黑卡价格不能高于红卡价格
        if (MainConstant.CHARGE_IS_AVAILABLE_YES == blackIsAvailable && MainConstant.CHARGE_IS_CHARGE_YES == blackIsCharge
                && MainConstant.CHARGE_IS_AVAILABLE_YES == redIsAvailable && MainConstant.CHARGE_IS_CHARGE_YES == redIsCharge) {
            if (unionMainChargeVO.getBlackChargePrice() > unionMainChargeVO.getRedChargePrice()) {
                throw new BusinessException("黑卡价格不能高于红卡价格");
            }
        }

        // (9)事务处理
        this.updateById(unionMain);
        this.unionMainChargeService.updateById(updateBlackUnionMainCharge);
        this.unionMainChargeService.updateById(updateRedUnionMainCharge);
        this.unionMainDictService.deleteByUnionId(unionId);
        List<UnionMainDict> unionMainDictList = unionMainVO.getUnionMainDictList();
        if (ListUtil.isNotEmpty(unionMainDictList)) {
            this.unionMainDictService.insertBatch(unionMainDictList);
        }
    }

}
