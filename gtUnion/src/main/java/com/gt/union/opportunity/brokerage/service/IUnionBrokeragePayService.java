package com.gt.union.opportunity.brokerage.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.opportunity.brokerage.entity.UnionBrokeragePay;
import com.gt.union.opportunity.brokerage.vo.BrokerageOpportunityVO;
import com.gt.union.opportunity.brokerage.vo.BrokeragePayVO;
import com.gt.union.opportunity.brokerage.vo.OpportunityBrokeragePayVO;

import java.util.List;

/**
 * 佣金支出 服务接口
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
public interface IUnionBrokeragePayService extends IService<UnionBrokeragePay> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 获取商机佣金支付明细详情信息
     *
     * @param busId    商家id
     * @param unionId  联盟id
     * @param memberId 盟员id
     * @return BrokeragePayVO
     * @throws Exception 统一处理异常
     */
    BrokeragePayVO getBrokeragePayVOByBusIdAndUnionIdAndMemberId(Integer busId, Integer unionId, Integer memberId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 分页：获取我需支付的商机佣金信息
     *
     * @param busId           商家id
     * @param optUnionId      联盟id
     * @param optFromMemberId 商机推荐盟员id
     * @param optIsClose      是否已结算(0:否 1:是)
     * @param optClientName   客户名称
     * @param optClientPhone  客户电话
     * @return List<BrokerageOpportunityVO>
     * @throws Exception 统一处理异常
     */
    List<BrokerageOpportunityVO> listBrokerageOpportunityVOByBusId(Integer busId, Integer optUnionId, Integer optFromMemberId,
                                                                   Integer optIsClose, String optClientName, String optClientPhone) throws Exception;

    /**
     * 分页：获取商机佣金支付明细信息
     *
     * @param busId      商家id
     * @param optUnionId 联盟id
     * @return List<BrokeragePayVO>
     * @throws Exception 统一处理异常
     */
    List<BrokeragePayVO> listBrokeragePayVOByBusId(Integer busId, Integer optUnionId) throws Exception;

    /**
     * 获取与商家具有商机支付往来的盟员id列表
     *
     * @param unionIdList        联盟id列表
     * @param status             支付状态
     * @param fromBusIdOrToBusId 商家id
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<Integer> listMemberIdByUnionIdListAndStatus(List<Integer> unionIdList, Integer status, Integer fromBusIdOrToBusId) throws Exception;

    /**
     * 根据支付商家id、收款商家id和支付状态，获取商机佣金支付信息
     *
     * @param fromBusId 支付商家id
     * @param toBusId   收款商家id
     * @param status    支付状态
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listByFromBusIdAndToBusIdAndStatus(Integer fromBusId, Integer toBusId, Integer status) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 批量支付：商机佣金
     *
     * @param busId             商家id
     * @param opportunityIdList 商机id列表
     * @return OpportunityBrokeragePayVO
     * @throws Exception 统一处理异常
     */
    OpportunityBrokeragePayVO batchPayByBusId(Integer busId, List<Integer> opportunityIdList) throws Exception;

    /**
     * 商机佣金支付成功后的回调
     *
     * @param payIds    商机佣金支付ids
     * @param socketKey socket关键字
     * @param payType   支付类型
     * @param orderNo   订单号
     * @param isSuccess 是否成功
     * @return String
     */
    String updateCallbackByIds(String payIds, String socketKey, String payType, String orderNo, Integer isSuccess);

    //***************************************** Domain Driven Design - count *******************************************

    /**
     * 根据支付商家id、收款商家id和支付状态，统计商机佣金支付总额
     *
     * @param fromBusId 支付商家id
     * @param toBusId   收款商家id
     * @param status    支付状态
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double sumMoneyByFromBusIdAndToBusIdAndStatus(Integer fromBusId, Integer toBusId, Integer status) throws Exception;

    //***************************************** Domain Driven Design - boolean *****************************************

    /**
     * 根据商机id和联盟id，判断是否存在
     *
     * @param opportunityId 商机id
     * @param unionId       联盟id
     * @param status        支付状态
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existByOpportunityIdAndUnionIdAndStatus(Integer opportunityId, Integer unionId, Integer status) throws Exception;

    //***************************************** Domain Driven Design - filter ******************************************

    /**
     * 根据联盟id进行过滤
     *
     * @param payList 数据源
     * @param unionId 联盟id
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> filterByUnionId(List<UnionBrokeragePay> payList, Integer unionId) throws Exception;

}