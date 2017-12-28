package com.gt.union.union.main.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.union.main.entity.UnionMainCreate;
import com.gt.union.union.main.vo.UnionCreateVO;
import com.gt.union.union.main.vo.UnionPermitCheckVO;

import java.util.List;

/**
 * 联盟创建 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
 */
public interface IUnionMainCreateService {
    //********************************************* Base On Business - get *********************************************

    /**
     * 我的联盟-创建联盟-检查联盟许可
     *
     * @param busId 商家id
     * @return UnionPermitCheckVO
     * @throws Exception 统一处理异常
     */
    UnionPermitCheckVO getPermitCheckVOByBusId(Integer busId) throws Exception;

    //********************************************* Base On Business - list ********************************************

    //********************************************* Base On Business - save ********************************************

    /**
     * 我的联盟-创建联盟-保存
     *
     * @param busId 商家id
     * @param vo    表单信息
     * @throws Exception 统一处理异常
     */
    void saveUnionCreateVOByBusId(Integer busId, UnionCreateVO vo) throws Exception;

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionMainCreateList 数据源
     * @param delStatus           删除状态
     * @return List<UnionMainCreate>
     * @throws Exception 统一处理异常
     */
    List<UnionMainCreate> filterByDelStatus(List<UnionMainCreate> unionMainCreateList, Integer delStatus) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取联盟创建信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMainCreate
     * @throws Exception 统一处理异常
     */
    UnionMainCreate getById(Integer id) throws Exception;

    /**
     * 获取未删除的联盟创建信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMainCreate
     * @throws Exception 统一处理异常
     */
    UnionMainCreate getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的联盟创建信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMainCreate
     * @throws Exception 统一处理异常
     */
    UnionMainCreate getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionMainCreateList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionMainCreate> unionMainCreateList) throws Exception;


    /**
     * 获取联盟创建列表信息(by myBatisGenerator)
     *
     * @param busId busId
     * @return List<UnionMainCreate>
     * @throws Exception 统一处理异常
     */
    List<UnionMainCreate> listByBusId(Integer busId) throws Exception;

    /**
     * 获取未删除的联盟创建列表信息(by myBatisGenerator)
     *
     * @param busId busId
     * @return List<UnionMainCreate>
     * @throws Exception 统一处理异常
     */
    List<UnionMainCreate> listValidByBusId(Integer busId) throws Exception;

    /**
     * 获取已删除的联盟创建列表信息(by myBatisGenerator)
     *
     * @param busId busId
     * @return List<UnionMainCreate>
     * @throws Exception 统一处理异常
     */
    List<UnionMainCreate> listInvalidByBusId(Integer busId) throws Exception;

    /**
     * 获取联盟创建列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMainCreate>
     * @throws Exception 统一处理异常
     */
    List<UnionMainCreate> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的联盟创建列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMainCreate>
     * @throws Exception 统一处理异常
     */
    List<UnionMainCreate> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的联盟创建列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMainCreate>
     * @throws Exception 统一处理异常
     */
    List<UnionMainCreate> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取联盟创建列表信息(by myBatisGenerator)
     *
     * @param permitId permitId
     * @return List<UnionMainCreate>
     * @throws Exception 统一处理异常
     */
    List<UnionMainCreate> listByPermitId(Integer permitId) throws Exception;

    /**
     * 获取未删除的联盟创建列表信息(by myBatisGenerator)
     *
     * @param permitId permitId
     * @return List<UnionMainCreate>
     * @throws Exception 统一处理异常
     */
    List<UnionMainCreate> listValidByPermitId(Integer permitId) throws Exception;

    /**
     * 获取已删除的联盟创建列表信息(by myBatisGenerator)
     *
     * @param permitId permitId
     * @return List<UnionMainCreate>
     * @throws Exception 统一处理异常
     */
    List<UnionMainCreate> listInvalidByPermitId(Integer permitId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionMainCreate>
     * @throws Exception 统一处理异常
     */
    List<UnionMainCreate> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionMainCreate> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionMainCreate 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionMainCreate newUnionMainCreate) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionMainCreateList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionMainCreate> newUnionMainCreateList) throws Exception;

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
     * @param updateUnionMainCreate 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionMainCreate updateUnionMainCreate) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionMainCreateList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionMainCreate> updateUnionMainCreateList) throws Exception;

}