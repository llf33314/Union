package com.gt.union.opportunity.brokerage.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.opportunity.brokerage.entity.UnionBrokeragePay;
import com.gt.union.opportunity.brokerage.vo.BrokerageOpportunityVO;
import com.gt.union.opportunity.brokerage.vo.BrokeragePayVO;
import com.gt.union.union.main.vo.UnionPayVO;

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
     * 商机-佣金结算-支付明细-详情；导出：商机佣金结算-支付明细-详情
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
     * 分页：商机-佣金结算-我需支付的佣金
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
     * 分页：商机-佣金结算-支付明细；导出：商机-佣金结算-支付明细
     *
     * @param busId      商家id
     * @param optUnionId 联盟id
     * @return List<BrokeragePayVO>
     * @throws Exception 统一处理异常
     */
    List<BrokeragePayVO> listBrokeragePayVOByBusId(Integer busId, Integer optUnionId) throws Exception;

    /**
     * 获取商机佣金支付列表信息
     *
     * @param fromBusId 支付商家id
     * @param toBusId   收款商家id
     * @param status    支付状态
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listByFromBusIdAndToBusIdAndStatus(Integer fromBusId, Integer toBusId, Integer status) throws Exception;

    /**
     * 获取商机佣金支付列表信息
     *
     * @param orderNo 订单号
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listByOrderNo(String orderNo) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 保存
     *
     * @param newUnionBrokeragePay 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionBrokeragePay newUnionBrokeragePay) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 商机-佣金结算-我需支付的佣金-批量支付
     *
     * @param busId             商家id
     * @param opportunityIdList 商机id列表
     * @param verifierId        佣金平台管理人员id
     * @param unionBrokeragePayStrategyService
	 * @return UnionPayVO
     * @throws Exception 统一处理异常
     */
    UnionPayVO batchPayByBusId(Integer busId, List<Integer> opportunityIdList, Integer verifierId, IUnionBrokeragePayStrategyService unionBrokeragePayStrategyService) throws Exception;

    /**
     * 佣金结算-我需支付的佣金-批量支付-回调
     *
     * @param orderNo    订单号
     * @param socketKey  socket关键字
     * @param payType    支付类型
     * @param payOrderNo 支付订单号
     * @param isSuccess  是否成功
     * @return String 返回结果
     */
    String updateCallbackByOrderNo(String orderNo, String socketKey, String payType, String payOrderNo, Integer isSuccess);

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
     * 判断商机佣金支付是否存在
     *
     * @param unionId       联盟id
     * @param opportunityId 商机id
     * @param status        支付状态
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existByUnionIdAndOpportunityIdAndStatus(Integer unionId, Integer opportunityId, Integer status) throws Exception;

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

    /**
     * 根据商机id进行过滤
     *
     * @param payList       数据源
     * @param opportunityId 商机id
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> filterByOpportunityId(List<UnionBrokeragePay> payList, Integer opportunityId) throws Exception;

    /**
     * 根据支付状态进行过滤
     *
     * @param payList 数据源
     * @param status  支付状态
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> filterByStatus(List<UnionBrokeragePay> payList, Integer status) throws Exception;

    /**
     * 根据收款商家id进行过滤
     *
     * @param payList 数据源
     * @param toBusId 收款商家id
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> filterByToBusId(List<UnionBrokeragePay> payList, Integer toBusId) throws Exception;

}