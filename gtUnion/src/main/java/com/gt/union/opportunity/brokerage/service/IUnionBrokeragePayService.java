package com.gt.union.opportunity.brokerage.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.opportunity.brokerage.entity.UnionBrokeragePay;
import com.gt.union.opportunity.brokerage.vo.BrokeragePayVO;
import com.gt.union.union.main.vo.UnionPayVO;

import java.util.List;

/**
 * 佣金支出 服务接口
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
public interface IUnionBrokeragePayService {
    //********************************************* Base On Business - get *********************************************

    /**
     * 缓存穿透-商机-佣金结算-支付明细-详情；导出：商机佣金结算-支付明细-详情
     *
     * @param busId    商家id
     * @param unionId  联盟id
     * @param memberId 盟员id
     * @return BrokeragePayVO
     * @throws Exception 统一处理异常
     */
    BrokeragePayVO getBrokeragePayVOByBusIdAndUnionIdAndMemberId(Integer busId, Integer unionId, Integer memberId) throws Exception;

    //********************************************* Base On Business - list ********************************************

    /**
     * 缓存穿透-获取未删除的商机佣金支付列表信息
     *
     * @param fromBusId 支付商家id
     * @param toBusId   收款商家id
     * @param status    支付状态
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listValidByFromBusIdAndToBusIdAndStatus(Integer fromBusId, Integer toBusId, Integer status) throws Exception;

    /**
     * 缓存穿透-获取未删除的商机佣金支付列表信息
     *
     * @param fromBusId   支付商家id
     * @param toBusIdList 收款商家id列表
     * @param status      支付状态
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listValidByFromBusIdAndToBusIdListAndStatus(Integer fromBusId, List<Integer> toBusIdList, Integer status) throws Exception;

    /**
     * 缓存穿透-获取未删除的商机佣金支付列表信息
     *
     * @param fromBusIdList 支付商家id列表
     * @param toBusId       收款商家id
     * @param status        支付状态
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listValidByFromBusIdListAndToBusIdAndStatus(List<Integer> fromBusIdList, Integer toBusId, Integer status) throws Exception;


    /**
     * 缓存穿透-获取未删除的商机佣金支付列表信息
     *
     * @param orderNo 订单号
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listValidByOrderNo(String orderNo) throws Exception;

    /**
     * 缓存穿透-分页：商机-佣金结算-我需支付的佣金
     *
     * @param page            分页对象
     * @param busId           商家id
     * @param optUnionId      联盟id
     * @param optFromMemberId 商机推荐盟员id
     * @param optIsClose      是否已结算(0:否 1:是)
     * @param optClientName   客户名称
     * @param optClientPhone  客户电话
     * @return List<BrokerageOpportunityVO>
     * @throws Exception 统一处理异常
     */
    Page pageBrokerageOpportunityVOByBusId(
            Page page, Integer busId, Integer optUnionId, Integer optFromMemberId, Integer optIsClose, String optClientName, String optClientPhone) throws Exception;

    /**
     * 商机-佣金结算-支付明细；导出：商机-佣金结算-支付明细
     *
     * @param busId      商家id
     * @param optUnionId 联盟id
     * @return List<BrokeragePayVO>
     * @throws Exception 统一处理异常
     */
    List<BrokeragePayVO> listBrokeragePayVOByBusId(Integer busId, Integer optUnionId) throws Exception;

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    /**
     * 商机-佣金结算-我需支付的佣金-批量支付
     *
     * @param busId                            商家id
     * @param opportunityIdList                商机id列表
     * @param verifierId                       佣金平台管理人员id
     * @param unionBrokeragePayStrategyService 回调策略接口
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


    //********************************************* Base On Business - other *******************************************

    /**
     * 缓存穿透-统计商机佣金支付总额
     *
     * @param fromBusId   支付商家id
     * @param toBusIdList 收款商家id列表
     * @param status      支付状态
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double sumValidMoneyByFromBusIdAndToBusIdListAndStatus(Integer fromBusId, List<Integer> toBusIdList, Integer status) throws Exception;

    /**
     * 缓存穿透-统计商机佣金支付总额
     *
     * @param fromBusIdList 支付商家id列表
     * @param toBusId       收款商家id
     * @param status        支付状态
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double sumValidMoneyByFromBusIdListAndToBusIdAndStatus(List<Integer> fromBusIdList, Integer toBusId, Integer status) throws Exception;

    /**
     * 缓存穿透-判断商机佣金支付是否存在
     *
     * @param opportunityId 商机id
     * @param status        支付状态
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existValidByOpportunityIdAndStatus(Integer opportunityId, Integer status) throws Exception;

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionBrokeragePayList 数据源
     * @param delStatus             删除状态
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> filterByDelStatus(List<UnionBrokeragePay> unionBrokeragePayList, Integer delStatus) throws Exception;

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

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取佣金支出信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionBrokeragePay
     * @throws Exception 统一处理异常
     */
    UnionBrokeragePay getById(Integer id) throws Exception;

    /**
     * 获取未删除的佣金支出信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionBrokeragePay
     * @throws Exception 统一处理异常
     */
    UnionBrokeragePay getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的佣金支出信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionBrokeragePay
     * @throws Exception 统一处理异常
     */
    UnionBrokeragePay getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionBrokeragePayList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionBrokeragePay> unionBrokeragePayList) throws Exception;


    /**
     * 获取佣金支出列表信息(by myBatisGenerator)
     *
     * @param fromBusId fromBusId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listByFromBusId(Integer fromBusId) throws Exception;

    /**
     * 获取未删除的佣金支出列表信息(by myBatisGenerator)
     *
     * @param fromBusId fromBusId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listValidByFromBusId(Integer fromBusId) throws Exception;

    /**
     * 获取已删除的佣金支出列表信息(by myBatisGenerator)
     *
     * @param fromBusId fromBusId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listInvalidByFromBusId(Integer fromBusId) throws Exception;

    /**
     * 获取佣金支出列表信息(by myBatisGenerator)
     *
     * @param toBusId toBusId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listByToBusId(Integer toBusId) throws Exception;

    /**
     * 获取未删除的佣金支出列表信息(by myBatisGenerator)
     *
     * @param toBusId toBusId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listValidByToBusId(Integer toBusId) throws Exception;

    /**
     * 获取已删除的佣金支出列表信息(by myBatisGenerator)
     *
     * @param toBusId toBusId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listInvalidByToBusId(Integer toBusId) throws Exception;

    /**
     * 获取佣金支出列表信息(by myBatisGenerator)
     *
     * @param fromMemberId fromMemberId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 获取未删除的佣金支出列表信息(by myBatisGenerator)
     *
     * @param fromMemberId fromMemberId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listValidByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 获取已删除的佣金支出列表信息(by myBatisGenerator)
     *
     * @param fromMemberId fromMemberId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listInvalidByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 获取佣金支出列表信息(by myBatisGenerator)
     *
     * @param toMemberId toMemberId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listByToMemberId(Integer toMemberId) throws Exception;

    /**
     * 获取未删除的佣金支出列表信息(by myBatisGenerator)
     *
     * @param toMemberId toMemberId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listValidByToMemberId(Integer toMemberId) throws Exception;

    /**
     * 获取已删除的佣金支出列表信息(by myBatisGenerator)
     *
     * @param toMemberId toMemberId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listInvalidByToMemberId(Integer toMemberId) throws Exception;

    /**
     * 获取佣金支出列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的佣金支出列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的佣金支出列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取佣金支出列表信息(by myBatisGenerator)
     *
     * @param opportunityId opportunityId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listByOpportunityId(Integer opportunityId) throws Exception;

    /**
     * 获取未删除的佣金支出列表信息(by myBatisGenerator)
     *
     * @param opportunityId opportunityId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listValidByOpportunityId(Integer opportunityId) throws Exception;

    /**
     * 获取已删除的佣金支出列表信息(by myBatisGenerator)
     *
     * @param opportunityId opportunityId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listInvalidByOpportunityId(Integer opportunityId) throws Exception;

    /**
     * 获取佣金支出列表信息(by myBatisGenerator)
     *
     * @param verifierId verifierId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listByVerifierId(Integer verifierId) throws Exception;

    /**
     * 获取未删除的佣金支出列表信息(by myBatisGenerator)
     *
     * @param verifierId verifierId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listValidByVerifierId(Integer verifierId) throws Exception;

    /**
     * 获取已删除的佣金支出列表信息(by myBatisGenerator)
     *
     * @param verifierId verifierId
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listInvalidByVerifierId(Integer verifierId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionBrokeragePay>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokeragePay> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionBrokeragePay> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionBrokeragePay 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionBrokeragePay newUnionBrokeragePay) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionBrokeragePayList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionBrokeragePay> newUnionBrokeragePayList) throws Exception;

    //****************************************** Object As a Service - remove ******************************************

    /**
     * 移除(by myBatisGenerator)
     *
     * @param id 移除内容
     * @throws Exception 统一处理异常
     */
    void removeById(Integer id) throws Exception;

    /**
     * 批量移除(by myBatisGenerator)
     *
     * @param idList 移除内容
     * @throws Exception 统一处理异常
     */
    void removeBatchById(List<Integer> idList) throws Exception;

    //****************************************** Object As a Service - update ******************************************

    /**
     * 更新(by myBatisGenerator)
     *
     * @param updateUnionBrokeragePay 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionBrokeragePay updateUnionBrokeragePay) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionBrokeragePayList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionBrokeragePay> updateUnionBrokeragePayList) throws Exception;

}