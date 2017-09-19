package com.gt.union.opportunity.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DateUtil;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.opportunity.entity.UnionOpportunityBrokerageRatio;
import com.gt.union.opportunity.mapper.UnionOpportunityBrokerageRatioMapper;
import com.gt.union.opportunity.service.IUnionOpportunityBrokerageRatioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商机佣金比率 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionOpportunityBrokerageRatioServiceImpl extends ServiceImpl<UnionOpportunityBrokerageRatioMapper, UnionOpportunityBrokerageRatio> implements IUnionOpportunityBrokerageRatioService {
    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据商机佣金比例的设置者盟员身份id和受惠者盟员身份id，获取商机佣金比例设置信息
     *
     * @param fromMemberId {not null} 设置者盟员身份id
     * @param toMemberId   {not null} 受惠者盟员身份id
     * @return
     * @throws Exception
     */
    @Override
    public UnionOpportunityBrokerageRatio getByFromMemberIdAndToMemberId(Integer fromMemberId, Integer toMemberId) throws Exception {
        if (fromMemberId == null || toMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("from_member_id", fromMemberId)
                .eq("to_member_id", toMemberId);
        return this.selectOne(wrapper);
    }

    //------------------------------------------ list(include page) ---------------------------------------------------

    /**
     * 根据商家id和盟员身份id，分页查询商机佣金比设置列表信息
     *
     * @param page     {not null} 分页对象
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @return
     * @throws Exception
     */
    @Override
    public Page pageMapByBusIdAndMemberId(Page page, Integer busId, final Integer memberId) throws Exception {
        if (busId == null || memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        final UnionMember unionMember = this.unionMemberService.getByIdAndBusId(memberId, busId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(unionMember.getUnionId());
        //(3)判断是否具有读权限
        if (!this.unionMemberService.hasReadAuthority(unionMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //(4)查询操作
        return this.unionMemberService.pageOpportunityBrokerageRatioMapByMember(page, unionMember);
    }

    //------------------------------------------------- update --------------------------------------------------------

    /**
     * 根据商家id、设置方盟员身份id、受惠方盟员身份id和商机佣金比例，更新或保存设置
     *
     * @param busId        {not null} 商家id
     * @param fromMemberId {not null} 设置方盟员身份id
     * @param toMemberId   {not null} 受惠方盟员身份id
     * @param ratio        {not null} 商机佣金比例
     * @throws Exception
     */
    @Override
    public void updateOrSaveByBusIdAndFromMemberIdAndToMemberIdAndRatio(Integer busId, Integer fromMemberId, Integer toMemberId, Double ratio) throws Exception {
        if (busId == null || fromMemberId == null || toMemberId == null || ratio == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //(1)判断是否具有盟员权限
        UnionMember fromMember = this.unionMemberService.getByIdAndBusId(fromMemberId, busId);
        if (fromMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //(2)检查联盟有效期
        this.unionMainService.checkUnionMainValid(fromMember.getUnionId());
        //(3)判断是否具有写权限
        if (!this.unionMemberService.hasWriteAuthority(fromMember)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_WRITE_REJECT);
        }
        //(4)判断受惠方是否有效
        UnionMember toMember = this.unionMemberService.getById(toMemberId);
        if (toMember == null) {
            throw new BusinessException("找不到例受惠方的盟员信息");
        }
        if (!this.unionMemberService.hasWriteAuthority(toMember)) {
            throw new BusinessException("受惠方正在退盟过渡期，无法操作");
        }
        //(5)校验比例
        if (ratio < 0D || ratio > 100D) {
            throw new BusinessException("比例必须大于0，且小于100");
        }
        //(6)查询是否已存在商机佣金比例设置，若有，则更新，否则，新增
        UnionOpportunityBrokerageRatio opportunityBrokerageRatio = this.getByFromMemberIdAndToMemberId(fromMemberId, toMemberId);
        if (opportunityBrokerageRatio != null) {
            UnionOpportunityBrokerageRatio updateRatio = new UnionOpportunityBrokerageRatio();
            updateRatio.setId(opportunityBrokerageRatio.getId()); //商机佣金比例设置id
            updateRatio.setModifytime(DateUtil.getCurrentDate()); //最后更新时间
            updateRatio.setRatio(ratio); //比例
            this.updateById(updateRatio);
        } else {
            UnionOpportunityBrokerageRatio saveRatio = new UnionOpportunityBrokerageRatio();
            saveRatio.setDelStatus(CommonConstant.DEL_STATUS_NO); //删除状态
            saveRatio.setCreatetime(DateUtil.getCurrentDate()); //创建时间
            saveRatio.setFromMemberId(fromMemberId); //设置方盟员身份id
            saveRatio.setToMemberId(toMemberId); //受惠方盟员身份id
            saveRatio.setRatio(ratio); //比例
            this.insert(saveRatio);
        }
    }

    //------------------------------------------------- save ----------------------------------------------------------
    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------

}
