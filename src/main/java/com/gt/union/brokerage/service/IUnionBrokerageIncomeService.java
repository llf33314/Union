package com.gt.union.brokerage.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.brokerage.entity.UnionBrokerageIncome;

/**
 * <p>
 * 佣金收入 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionBrokerageIncomeService extends IService<UnionBrokerageIncome> {
    /**
     * 根据商家id和盟员身份id，分页获取同一个联盟下的售卡佣金分成列表信息，并根据售卡类型(精确匹配)、卡号(模糊匹配)进行匹配
     *
     * @param page             {not null} 分页对象
     * @param busId            {not null} 商家id
     * @param memberId         {not null} 盟员身份id
     * @param optionCardType   可选项 售卡类型
     * @param optionCardNumber 可选项 卡号
     * @return
     * @throws Exception
     */
    Page pageCardMapByBusIdAndMemberId(Page page, Integer busId, Integer memberId, Integer optionCardType
            , String optionCardNumber) throws Exception;
}
