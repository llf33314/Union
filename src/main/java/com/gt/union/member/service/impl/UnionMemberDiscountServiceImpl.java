package com.gt.union.member.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.common.util.DoubleUtil;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.entity.UnionMemberDiscount;
import com.gt.union.member.mapper.UnionMemberDiscountMapper;
import com.gt.union.member.service.IUnionMemberDiscountService;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Autowired
    private IUnionMainService unionMainService;

    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 查询最低折扣
     *
     * @param members    我的盟员身份列表
     * @param memberList 和我有盟员关系的其他盟员列表
     * @return
     */
    @Override
    public UnionMemberDiscount getMinDiscountByMemberList(List<Integer> members, List<Integer> memberList) {
        EntityWrapper wrapper = new EntityWrapper<UnionMemberDiscount>();
        wrapper.in("from_member_id", members.toArray());
        wrapper.in("to_member_id", memberList.toArray());
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        wrapper.orderBy("to_member_id", true);
        wrapper.setSqlSelect("id, DATE_FORMAT(createtime, '%Y-%m-%d %T') createtime, del_status, from_member_id, to_member_id, min(discount) as discount, DATE_FORMAT(modifytime, '%Y-%m-%d %T') modifytime ");
        return this.selectOne(wrapper);
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

    //------------------------------------------ list(include page) ---------------------------------------------------
    //------------------------------------------------- update --------------------------------------------------------

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
        //(1)判断是否具有盟员权限
        UnionMember unionMember = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionMember.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)校验更新内容
        if (discount < 0D || discount > 10D) {
            throw new ParamException("折扣必须大于0折，且小于10折");
        }
        if (!DoubleUtil.checkDecimalPrecision(discount, 2)) {
            throw new ParamException("折扣最多保留两位小数");
        }
        //(5)更新操作
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

    //------------------------------------------------- save ----------------------------------------------------------
    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------
}
