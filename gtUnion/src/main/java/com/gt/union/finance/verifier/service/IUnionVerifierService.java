package com.gt.union.finance.verifier.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.finance.verifier.entity.UnionVerifier;

import java.util.List;

/**
 * 平台管理者 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 14:54:27
 */
public interface IUnionVerifierService {
    //********************************************* Base On Business - get *********************************************

    /**
     * 获取未删除的平台人员信息
     *
     * @param busId      商家id
     * @param verifierId 平台人员id
     * @return UnionVerifier
     * @throws Exception 统一处理异常
     */
    UnionVerifier getValidByBusIdAndId(Integer busId, Integer verifierId) throws Exception;

    //********************************************* Base On Business - list ********************************************

    /**
     * 缓存穿透-获取未删除的平台管理者列表信息
     *
     * @return List<UnionVerifier>
     * @throws Exception 统一处理异常
     */
    List<UnionVerifier> listValid() throws Exception;

    /**
     * 列表：获取未删除的平台管理者
     *
     * @param busId 商家id
     * @return VerifierVO
     * @throws Exception 统一处理异常
     */
    List<UnionVerifier> listValidFinanceByBusId(Integer busId) throws Exception;

    //********************************************* Base On Business - save ********************************************

    /**
     * 新增平台管理者
     *
     * @param busId    商家id
     * @param code     手机验证码
     * @param verifier 表单内容
     * @throws Exception 统一处理异常
     */
    void saveByBusId(Integer busId, String code, UnionVerifier verifier) throws Exception;

    //********************************************* Base On Business - remove ******************************************

    /**
     * 删除平台管理者
     *
     * @param busId      商家id
     * @param verifierId 平台管理人员id
     * @throws Exception 统一处理异常
     */
    void removeByBusIdAndId(Integer busId, Integer verifierId) throws Exception;

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    /**
     * 是否存在未删除的平台人员信息
     *
     * @param busId 商家id
     * @param phone 电话
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existValidByBusIdAndPhone(Integer busId, String phone) throws Exception;

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionVerifierList 数据源
     * @param delStatus         删除状态
     * @return List<UnionVerifier>
     * @throws Exception 统一处理异常
     */
    List<UnionVerifier> filterByDelStatus(List<UnionVerifier> unionVerifierList, Integer delStatus) throws Exception;

    /**
     * 根据手机号进行过滤
     *
     * @param verifierList 数据源
     * @param phone        手机号
     * @return List<UnionVerifier>
     * @throws Exception 统一处理异常
     */
    List<UnionVerifier> filterByPhone(List<UnionVerifier> verifierList, String phone) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取平台管理者信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionVerifier
     * @throws Exception 统一处理异常
     */
    UnionVerifier getById(Integer id) throws Exception;

    /**
     * 获取未删除的平台管理者信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionVerifier
     * @throws Exception 统一处理异常
     */
    UnionVerifier getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的平台管理者信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionVerifier
     * @throws Exception 统一处理异常
     */
    UnionVerifier getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionVerifierList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionVerifier> unionVerifierList) throws Exception;


    /**
     * 获取平台管理者列表信息(by myBatisGenerator)
     *
     * @param busId busId
     * @return List<UnionVerifier>
     * @throws Exception 统一处理异常
     */
    List<UnionVerifier> listByBusId(Integer busId) throws Exception;

    /**
     * 获取未删除的平台管理者列表信息(by myBatisGenerator)
     *
     * @param busId busId
     * @return List<UnionVerifier>
     * @throws Exception 统一处理异常
     */
    List<UnionVerifier> listValidByBusId(Integer busId) throws Exception;

    /**
     * 获取已删除的平台管理者列表信息(by myBatisGenerator)
     *
     * @param busId busId
     * @return List<UnionVerifier>
     * @throws Exception 统一处理异常
     */
    List<UnionVerifier> listInvalidByBusId(Integer busId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionVerifier>
     * @throws Exception 统一处理异常
     */
    List<UnionVerifier> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionVerifier> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionVerifier 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionVerifier newUnionVerifier) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionVerifierList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionVerifier> newUnionVerifierList) throws Exception;

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
     * @param updateUnionVerifier 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionVerifier updateUnionVerifier) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionVerifierList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionVerifier> updateUnionVerifierList) throws Exception;

}