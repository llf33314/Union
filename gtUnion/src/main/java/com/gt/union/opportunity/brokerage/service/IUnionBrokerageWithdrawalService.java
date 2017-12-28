package com.gt.union.opportunity.brokerage.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageWithdrawal;

import java.util.List;

/**
 * 佣金提现 服务接口
 *
 * @author linweicong
 * @version 2017-11-24 09:21:28
 */
public interface IUnionBrokerageWithdrawalService {
    //********************************************* Base On Business - get *********************************************

    /**
     * 缓存穿透-获取未删除的可提现金额
     *
     * @param busId 商家id
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double getValidAvailableMoneyByBusId(Integer busId) throws Exception;

    //********************************************* Base On Business - list ********************************************

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    /**
     * 缓存穿透-统计未删除的已提现金额
     *
     * @param busId 商家id
     * @return Double
     * @throws Exception 统一处理异常
     */
    Double sumValidMoneyByBusId(Integer busId) throws Exception;

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionBrokerageWithdrawalList 数据源
     * @param delStatus                    删除状态
     * @return List<UnionBrokerageWithdrawal>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageWithdrawal> filterByDelStatus(List<UnionBrokerageWithdrawal> unionBrokerageWithdrawalList, Integer delStatus) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取佣金提现信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionBrokerageWithdrawal
     * @throws Exception 统一处理异常
     */
    UnionBrokerageWithdrawal getById(Integer id) throws Exception;

    /**
     * 获取未删除的佣金提现信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionBrokerageWithdrawal
     * @throws Exception 统一处理异常
     */
    UnionBrokerageWithdrawal getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的佣金提现信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionBrokerageWithdrawal
     * @throws Exception 统一处理异常
     */
    UnionBrokerageWithdrawal getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionBrokerageWithdrawalList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionBrokerageWithdrawal> unionBrokerageWithdrawalList) throws Exception;


    /**
     * 获取佣金提现列表信息(by myBatisGenerator)
     *
     * @param busId busId
     * @return List<UnionBrokerageWithdrawal>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageWithdrawal> listByBusId(Integer busId) throws Exception;

    /**
     * 获取未删除的佣金提现列表信息(by myBatisGenerator)
     *
     * @param busId busId
     * @return List<UnionBrokerageWithdrawal>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageWithdrawal> listValidByBusId(Integer busId) throws Exception;

    /**
     * 获取已删除的佣金提现列表信息(by myBatisGenerator)
     *
     * @param busId busId
     * @return List<UnionBrokerageWithdrawal>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageWithdrawal> listInvalidByBusId(Integer busId) throws Exception;

    /**
     * 获取佣金提现列表信息(by myBatisGenerator)
     *
     * @param verifierId verifierId
     * @return List<UnionBrokerageWithdrawal>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageWithdrawal> listByVerifierId(Integer verifierId) throws Exception;

    /**
     * 获取未删除的佣金提现列表信息(by myBatisGenerator)
     *
     * @param verifierId verifierId
     * @return List<UnionBrokerageWithdrawal>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageWithdrawal> listValidByVerifierId(Integer verifierId) throws Exception;

    /**
     * 获取已删除的佣金提现列表信息(by myBatisGenerator)
     *
     * @param verifierId verifierId
     * @return List<UnionBrokerageWithdrawal>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageWithdrawal> listInvalidByVerifierId(Integer verifierId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionBrokerageWithdrawal>
     * @throws Exception 统一处理异常
     */
    List<UnionBrokerageWithdrawal> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionBrokerageWithdrawal> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionBrokerageWithdrawal 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionBrokerageWithdrawal newUnionBrokerageWithdrawal) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionBrokerageWithdrawalList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionBrokerageWithdrawal> newUnionBrokerageWithdrawalList) throws Exception;

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
     * @param updateUnionBrokerageWithdrawal 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionBrokerageWithdrawal updateUnionBrokerageWithdrawal) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionBrokerageWithdrawalList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionBrokerageWithdrawal> updateUnionBrokerageWithdrawalList) throws Exception;

}