package com.gt.union.opportunity.main.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.opportunity.main.entity.UnionOpportunityRatio;
import com.gt.union.opportunity.main.vo.OpportunityRatioVO;

import java.util.List;

/**
 * 商机佣金比率 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 16:56:20
 */
public interface IUnionOpportunityRatioService {
    //********************************************* Base On Business - get *********************************************

    //********************************************* Base On Business - list ********************************************

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionOpportunityRatioList 数据源
     * @param delStatus                 删除状态
     * @return List<UnionOpportunityRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunityRatio> filterByDelStatus(List<UnionOpportunityRatio> unionOpportunityRatioList, Integer delStatus) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取商机佣金比率信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionOpportunityRatio
     * @throws Exception 统一处理异常
     */
    UnionOpportunityRatio getById(Integer id) throws Exception;

    /**
     * 获取未删除的商机佣金比率信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionOpportunityRatio
     * @throws Exception 统一处理异常
     */
    UnionOpportunityRatio getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的商机佣金比率信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionOpportunityRatio
     * @throws Exception 统一处理异常
     */
    UnionOpportunityRatio getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionOpportunityRatioList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionOpportunityRatio> unionOpportunityRatioList) throws Exception;


    /**
     * 获取商机佣金比率列表信息(by myBatisGenerator)
     *
     * @param fromMemberId fromMemberId
     * @return List<UnionOpportunityRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunityRatio> listByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 获取未删除的商机佣金比率列表信息(by myBatisGenerator)
     *
     * @param fromMemberId fromMemberId
     * @return List<UnionOpportunityRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunityRatio> listValidByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 获取已删除的商机佣金比率列表信息(by myBatisGenerator)
     *
     * @param fromMemberId fromMemberId
     * @return List<UnionOpportunityRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunityRatio> listInvalidByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 获取商机佣金比率列表信息(by myBatisGenerator)
     *
     * @param toMemberId toMemberId
     * @return List<UnionOpportunityRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunityRatio> listByToMemberId(Integer toMemberId) throws Exception;

    /**
     * 获取未删除的商机佣金比率列表信息(by myBatisGenerator)
     *
     * @param toMemberId toMemberId
     * @return List<UnionOpportunityRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunityRatio> listValidByToMemberId(Integer toMemberId) throws Exception;

    /**
     * 获取已删除的商机佣金比率列表信息(by myBatisGenerator)
     *
     * @param toMemberId toMemberId
     * @return List<UnionOpportunityRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunityRatio> listInvalidByToMemberId(Integer toMemberId) throws Exception;

    /**
     * 获取商机佣金比率列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionOpportunityRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunityRatio> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的商机佣金比率列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionOpportunityRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunityRatio> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的商机佣金比率列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionOpportunityRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunityRatio> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionOpportunityRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunityRatio> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page<UnionOpportunityRatio>
     * @throws Exception 统一处理异常
     */
    Page<UnionOpportunityRatio> pageSupport(Page page, EntityWrapper<UnionOpportunityRatio> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionOpportunityRatio 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionOpportunityRatio newUnionOpportunityRatio) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionOpportunityRatioList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionOpportunityRatio> newUnionOpportunityRatioList) throws Exception;

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
     * @param updateUnionOpportunityRatio 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionOpportunityRatio updateUnionOpportunityRatio) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionOpportunityRatioList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionOpportunityRatio> updateUnionOpportunityRatioList) throws Exception;

    // TODO

    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 获取商机佣金比例信息
     *
     * @param unionId      联盟id
     * @param fromMemberId 设置佣金比率盟员id
     * @param toMemberId   受惠佣金比率盟员id
     * @return UnionOpportunityRatio
     * @throws Exception 统一处理异常
     */
    UnionOpportunityRatio getByUnionIdAndFromMemberIdAndToMemberId(Integer unionId, Integer fromMemberId, Integer toMemberId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 分页：商机-商机佣金比设置
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<OpportunityRatioVO>
     * @throws Exception 统一处理异常
     */
    List<OpportunityRatioVO> listOpportunityRatioVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 商机-商机佣金比例设置-设置佣金比例-更新
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param toMemberId 受惠佣金比率盟员id
     * @param ratio      佣金比例
     * @throws Exception 统一处理异常
     */
    void updateRatioByBusIdAndUnionIdAndToMemberId(Integer busId, Integer unionId, Integer toMemberId, Double ratio) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    /**
     * 根据设置佣金比率盟员id进行过滤
     *
     * @param ratioList    数据源
     * @param fromMemberId 设置佣金比率盟员id
     * @return List<UnionOpportunityRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunityRatio> filterByFromMemberId(List<UnionOpportunityRatio> ratioList, Integer fromMemberId) throws Exception;

    /**
     * 根据受惠佣金比率盟员id进行过滤
     *
     * @param ratioList  数据源
     * @param toMemberId 设置佣金比率盟员id
     * @return List<UnionOpportunityRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunityRatio> filterByToMemberId(List<UnionOpportunityRatio> ratioList, Integer toMemberId) throws Exception;

    /**
     * 根据联盟id进行过滤
     *
     * @param ratioList 数据源
     * @param unionId   联盟id
     * @return List<UnionOpportunityRatio>
     * @throws Exception 统一处理异常
     */
    List<UnionOpportunityRatio> filterByUnionId(List<UnionOpportunityRatio> ratioList, Integer unionId) throws Exception;

}