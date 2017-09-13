package com.gt.union.member.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.DoubleUtil;
import com.gt.union.member.entity.UnionMemberDiscount;
import com.gt.union.member.mapper.UnionMemberDiscountMapper;
import com.gt.union.member.service.IUnionMemberDiscountService;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 盟员折扣 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionMemberDiscountServiceImpl extends ServiceImpl<UnionMemberDiscountMapper, UnionMemberDiscount> implements IUnionMemberDiscountService {
    @Autowired
    private IUnionMemberService unionMemberService;

    /**
     * 根据商家id、商家盟员身份id、被设置折扣的盟员身份id以及设置的折扣信息，更新或保存折扣信息
     *
     * @param busId       {not null} 商家id
     * @param memberId    {not null} 操作人的盟员身份id
     * @param tgtMemberId {not null} 被设置的折扣信息
     * @param discount
     * @throws Exception
     */
    @Override
    public void updateOrSaveDiscountByBusIdAndMemberId(Integer busId, Integer memberId, Integer tgtMemberId, Double discount) throws Exception {
        if (busId == null || memberId == null || tgtMemberId == null || discount == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (!this.unionMemberService.isUnionMemberValid(busId, memberId)) {
            throw new BusinessException("不具有盟员身份或已过期");
        }
        if (discount < 0D || discount > 10D) {
            throw new ParamException("折扣必须大于0折，且小于10折");
        }
        if (!DoubleUtil.checkDecimalPrecision(discount, 2)) {
            throw new ParamException("折扣最多保留两位小数");
        }
        UnionMemberDiscount unionMemberDiscount = this.getByFromMemberIdAndToMemberId(memberId, tgtMemberId);
        if (unionMemberDiscount != null) { //更新
            unionMemberDiscount.setModifytime(DateUtil.getCurrentDate()); //修改时间
            unionMemberDiscount.setDiscount(discount); //折扣
            this.updateById(unionMemberDiscount);
        } else { //新增
            UnionMemberDiscount saveUnionMemberDiscount = new UnionMemberDiscount();
            saveUnionMemberDiscount.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
            saveUnionMemberDiscount.setCreatetime(DateUtil.getCurrentDate()); //创建时间
            saveUnionMemberDiscount.setModifytime(DateUtil.getCurrentDate()); //修改时间
            saveUnionMemberDiscount.setFromMemberId(memberId); //设置折扣的盟员身份id
            saveUnionMemberDiscount.setToMemberId(tgtMemberId); //受惠折扣的盟员身份id
            saveUnionMemberDiscount.setDiscount(discount); //折扣
            this.insert(saveUnionMemberDiscount);
        }
    }

    /**
     * 根据设置折扣盟员id和受惠折扣盟员id，获取折扣信息
     *
     * @param fromMemberId {not null} 设置折扣盟员id
     * @param toMemberId   {not null} 受惠折扣盟员id
     * @return
     * @throws Exception
     */
    @Override
    public UnionMemberDiscount getByFromMemberIdAndToMemberId(Integer fromMemberId, Integer toMemberId) throws Exception {
        if (fromMemberId == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_member_id", fromMemberId)
                .eq("to_member_id", toMemberId);
        return this.selectOne(entityWrapper);
    }
}
