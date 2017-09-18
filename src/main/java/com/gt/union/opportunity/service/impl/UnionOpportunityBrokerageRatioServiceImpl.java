package com.gt.union.opportunity.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.brokerage.vo.UnionBrokerageRatioVO;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.DoubleUtil;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.opportunity.entity.UnionOpportunityBrokerageRatio;
import com.gt.union.opportunity.mapper.UnionOpportunityBrokerageRatioMapper;
import com.gt.union.opportunity.service.IUnionOpportunityBrokerageRatioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
    //------------------------------------------------- update --------------------------------------------------------
    //------------------------------------------------- save ----------------------------------------------------------
    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------


    @Override
    public Page pageBrokerageRatio(Page page, Integer busId, Integer unionId) throws Exception {
        if (page == null || busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMember member = unionMemberService.getByBusIdAndUnionId(busId, unionId);
        //List<UnionBrokerageRatioVO> list = unionBrokerageRatioMapper.pageBrokerageRatio(page, member.getId(), unionId);
        List<UnionBrokerageRatioVO> list = null;
        for (UnionBrokerageRatioVO vo : list) {
            vo.setFromMemberId(member.getId());
        }
        page.setRecords(list);
        return page;
    }

    @Override
    public void saveOrUpdateBrokerageRatio(Integer fromMemberId, Integer busId, Integer toMemberId, Double ratio) throws Exception {
        if (fromMemberId == null || busId == null || toMemberId == null || ratio == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //判断盟员身份
        /*if (!this.unionMemberService.isUnionMemberValid(busId, fromMemberId)) {
            throw new BusinessException("不具有盟员身份或已过期");
		}*/
        if (ratio <= 0D || ratio >= 100D) {
            throw new ParamException("商机佣金比必须大于0%，且小于100%");
        }
        if (!DoubleUtil.checkDecimalPrecision(ratio, 2)) {
            throw new ParamException("商机佣金比最多保留两位小数");
        }
        UnionOpportunityBrokerageRatio unionBrokerageRatio = this.getByFromMemberIdAndToMemberId(fromMemberId, toMemberId);
        UnionOpportunityBrokerageRatio brokerageRatio = new UnionOpportunityBrokerageRatio();
        if (unionBrokerageRatio == null) {
            brokerageRatio.setCreatetime(new Date());
            brokerageRatio.setModifytime(new Date());
            brokerageRatio.setDelStatus(CommonConstant.DEL_STATUS_NO);
            brokerageRatio.setFromMemberId(fromMemberId);
            brokerageRatio.setToMemberId(toMemberId);
            brokerageRatio.setRatio(ratio);
            this.insert(brokerageRatio);
        } else {
            brokerageRatio.setModifytime(new Date());
            brokerageRatio.setId(unionBrokerageRatio.getId());
            brokerageRatio.setRatio(ratio);
            this.updateById(brokerageRatio);
        }
    }
}
