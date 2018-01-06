package com.gt.union.card.main.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.main.entity.UnionCardIntegral;

import java.util.List;

/**
 * 联盟积分 服务接口
 *
 * @author linweicong
 * @version 2017-09-01 11:34:16
 */
public interface IUnionCardIntegralService {
    //********************************************* Base On Business - get *********************************************

    /**
     * 获取未删除的联盟积分信息
     *
     * @param unionId 联盟id
     * @param fanId   粉丝id
     * @return UnionCardIntegral
     * @throws Exception 统一处理异常
     */
    UnionCardIntegral getValidByUnionIdAndFanId(Integer unionId, Integer fanId) throws Exception;

    //********************************************* Base On Business - list ********************************************

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    /**
     * 统计未删除的联盟积分总和信息
     *
     * @param unionId 联盟id
     * @param fanId   粉丝id
     * @return UnionCardIntegral
     * @throws Exception 统一处理异常
     */
    Double sumValidIntegralByUnionIdAndFanId(Integer unionId, Integer fanId) throws Exception;

    /**
     * 统计未删除的联盟积分总和信息
     *
     * @param fanId 粉丝id
     * @return UnionCardIntegral
     * @throws Exception 统一处理异常
     */
    Double sumValidIntegralByFanId(Integer fanId) throws Exception;

    /**
     * 统计未删除的联盟积分总和信息
     *
     * @param unionId 联盟id
     * @return UnionCardIntegral
     * @throws Exception 统一处理异常
     */
    Double sumValidIntegralByUnionId(Integer unionId) throws Exception;

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionCardIntegralList 数据源
     * @param delStatus             删除状态
     * @return List<UnionCardIntegral>
     * @throws Exception 统一处理异常
     */
    List<UnionCardIntegral> filterByDelStatus(List<UnionCardIntegral> unionCardIntegralList, Integer delStatus) throws Exception;

    /**
     * 根据联盟id进行过滤
     *
     * @param integralList 数据源
     * @param unionId      联盟id
     * @return List<UnionCardIntegral>
     * @throws Exception 统一处理异常
     */
    List<UnionCardIntegral> filterByUnionId(List<UnionCardIntegral> integralList, Integer unionId) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取联盟积分信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardIntegral
     * @throws Exception 统一处理异常
     */
    UnionCardIntegral getById(Integer id) throws Exception;

    /**
     * 获取未删除的联盟积分信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardIntegral
     * @throws Exception 统一处理异常
     */
    UnionCardIntegral getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的联盟积分信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardIntegral
     * @throws Exception 统一处理异常
     */
    UnionCardIntegral getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionCardIntegralList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionCardIntegral> unionCardIntegralList) throws Exception;


    /**
     * 获取联盟积分列表信息(by myBatisGenerator)
     *
     * @param fanId fanId
     * @return List<UnionCardIntegral>
     * @throws Exception 统一处理异常
     */
    List<UnionCardIntegral> listByFanId(Integer fanId) throws Exception;

    /**
     * 获取未删除的联盟积分列表信息(by myBatisGenerator)
     *
     * @param fanId fanId
     * @return List<UnionCardIntegral>
     * @throws Exception 统一处理异常
     */
    List<UnionCardIntegral> listValidByFanId(Integer fanId) throws Exception;

    /**
     * 获取已删除的联盟积分列表信息(by myBatisGenerator)
     *
     * @param fanId fanId
     * @return List<UnionCardIntegral>
     * @throws Exception 统一处理异常
     */
    List<UnionCardIntegral> listInvalidByFanId(Integer fanId) throws Exception;

    /**
     * 获取联盟积分列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCardIntegral>
     * @throws Exception 统一处理异常
     */
    List<UnionCardIntegral> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的联盟积分列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCardIntegral>
     * @throws Exception 统一处理异常
     */
    List<UnionCardIntegral> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的联盟积分列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCardIntegral>
     * @throws Exception 统一处理异常
     */
    List<UnionCardIntegral> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionCardIntegral>
     * @throws Exception 统一处理异常
     */
    List<UnionCardIntegral> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionCardIntegral> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionCardIntegral 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionCardIntegral newUnionCardIntegral) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionCardIntegralList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionCardIntegral> newUnionCardIntegralList) throws Exception;

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
     * @param updateUnionCardIntegral 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionCardIntegral updateUnionCardIntegral) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionCardIntegralList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionCardIntegral> updateUnionCardIntegralList) throws Exception;

}