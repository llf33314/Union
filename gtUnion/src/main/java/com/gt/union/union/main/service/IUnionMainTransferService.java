package com.gt.union.union.main.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.union.main.entity.UnionMainTransfer;
import com.gt.union.union.main.vo.UnionTransferVO;

import java.util.List;

/**
 * 联盟转移 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
 */
public interface IUnionMainTransferService {
    //********************************************* Base On Business - get *********************************************

    /**
     * 获取未删除的盟主权限转移信息
     *
     * @param unionId       联盟id
     * @param toMemberId    目标盟员id
     * @param confirmStatus 确认状态
     * @return UnionMainTransfer
     * @throws Exception 统一处理异常
     */
    UnionMainTransfer getValidByUnionIdAndToMemberIdAndConfirmStatus(Integer unionId, Integer toMemberId, Integer confirmStatus) throws Exception;

    /**
     * 获取未删除的盟主权限转移信息
     *
     * @param unionId       联盟id
     * @param confirmStatus 确认状态
     * @return UnionMainTransfer
     * @throws Exception 统一处理异常
     */
    UnionMainTransfer getValidByUnionIdAndConfirmStatus(Integer unionId, Integer confirmStatus) throws Exception;

    /**
     * 获取未删除的盟主权限转移信息
     *
     * @param transferId    联盟转移id
     * @param unionId       联盟id
     * @param confirmStatus 确认状态
     * @return UnionMainTransfer
     * @throws Exception 统一处理异常
     */
    UnionMainTransfer getValidByIdAndUnionIdAndConfirmStatus(Integer transferId, Integer unionId, Integer confirmStatus) throws Exception;

    //********************************************* Base On Business - list ********************************************

    /**
     * 获取未删除的联盟转移列表信息
     *
     * @param unionId       联盟id
     * @param confirmStatus 确认状态
     * @return List<UnionMainTransfer>
     * @throws Exception 统一处理异常
     */
    List<UnionMainTransfer> listValidByUnionIdAndConfirmStatus(Integer unionId, Integer confirmStatus) throws Exception;

    /**
     * 分页：我的联盟-联盟设置-盟主权限转移
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<UnionTransferVO>
     * @throws Exception 统一处理异常
     */
    List<UnionTransferVO> listUnionTransferVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    //********************************************* Base On Business - save ********************************************

    /**
     * 我的联盟-联盟设置-盟主权限转移-分页-转移
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param toMemberId 目标盟员id
     * @throws Exception 统一处理异常
     */
    void saveByBusIdAndUnionIdAndToMemberId(Integer busId, Integer unionId, Integer toMemberId) throws Exception;

    //********************************************* Base On Business - remove ******************************************

    /**
     * 我的联盟-联盟设置-盟主权限转移-分页数据-撤销
     *
     * @param busId      商家id
     * @param transferId 联盟权限转移id
     * @param unionId    联盟id
     * @throws Exception 统一处理异常
     */
    void removeByBusIdAndIdAndUnionId(Integer busId, Integer transferId, Integer unionId) throws Exception;

    //********************************************* Base On Business - update ******************************************

    /**
     * 我的联盟-盟主权限转移-接受或拒绝
     *
     * @param busId      商家id
     * @param transferId 盟主权限转移申请id
     * @param unionId    联盟id
     * @param isAccept   是否接受(0：否 1：是)
     * @throws Exception 统一处理异常
     */
    void updateByBusIdAndIdAndUnionId(Integer busId, Integer transferId, Integer unionId, Integer isAccept) throws Exception;

    //********************************************* Base On Business - other *******************************************

    /**
     * 判断是否存在未删除的联盟转移信息
     *
     * @param unionId       联盟id
     * @param confirmStatus 确认状态
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existValidByUnionIdAndConfirmStatus(Integer unionId, Integer confirmStatus) throws Exception;

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionMainTransferList 数据源
     * @param delStatus             删除状态
     * @return List<UnionMainTransfer>
     * @throws Exception 统一处理异常
     */
    List<UnionMainTransfer> filterByDelStatus(List<UnionMainTransfer> unionMainTransferList, Integer delStatus) throws Exception;

    /**
     * 根据目标盟员id进行过滤
     *
     * @param transferList 盟主转移列表
     * @param toMemberId   目标盟员id
     * @return List<UnionMainTransfer>
     * @throws Exception 统一处理异常
     */
    List<UnionMainTransfer> filterByToMemberId(List<UnionMainTransfer> transferList, Integer toMemberId) throws Exception;

    /**
     * 根据确认状态进行过滤
     *
     * @param transferList  盟主转移列表
     * @param confirmStatus 确认状态
     * @return List<UnionMainTransfer>
     * @throws Exception 统一处理异常
     */
    List<UnionMainTransfer> filterByConfirmStatus(List<UnionMainTransfer> transferList, Integer confirmStatus) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取联盟转移信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMainTransfer
     * @throws Exception 统一处理异常
     */
    UnionMainTransfer getById(Integer id) throws Exception;

    /**
     * 获取未删除的联盟转移信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMainTransfer
     * @throws Exception 统一处理异常
     */
    UnionMainTransfer getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的联盟转移信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMainTransfer
     * @throws Exception 统一处理异常
     */
    UnionMainTransfer getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionMainTransferList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionMainTransfer> unionMainTransferList) throws Exception;


    /**
     * 获取联盟转移列表信息(by myBatisGenerator)
     *
     * @param fromMemberId fromMemberId
     * @return List<UnionMainTransfer>
     * @throws Exception 统一处理异常
     */
    List<UnionMainTransfer> listByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 获取未删除的联盟转移列表信息(by myBatisGenerator)
     *
     * @param fromMemberId fromMemberId
     * @return List<UnionMainTransfer>
     * @throws Exception 统一处理异常
     */
    List<UnionMainTransfer> listValidByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 获取已删除的联盟转移列表信息(by myBatisGenerator)
     *
     * @param fromMemberId fromMemberId
     * @return List<UnionMainTransfer>
     * @throws Exception 统一处理异常
     */
    List<UnionMainTransfer> listInvalidByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 获取联盟转移列表信息(by myBatisGenerator)
     *
     * @param toMemberId toMemberId
     * @return List<UnionMainTransfer>
     * @throws Exception 统一处理异常
     */
    List<UnionMainTransfer> listByToMemberId(Integer toMemberId) throws Exception;

    /**
     * 获取未删除的联盟转移列表信息(by myBatisGenerator)
     *
     * @param toMemberId toMemberId
     * @return List<UnionMainTransfer>
     * @throws Exception 统一处理异常
     */
    List<UnionMainTransfer> listValidByToMemberId(Integer toMemberId) throws Exception;

    /**
     * 获取已删除的联盟转移列表信息(by myBatisGenerator)
     *
     * @param toMemberId toMemberId
     * @return List<UnionMainTransfer>
     * @throws Exception 统一处理异常
     */
    List<UnionMainTransfer> listInvalidByToMemberId(Integer toMemberId) throws Exception;

    /**
     * 获取联盟转移列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMainTransfer>
     * @throws Exception 统一处理异常
     */
    List<UnionMainTransfer> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的联盟转移列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMainTransfer>
     * @throws Exception 统一处理异常
     */
    List<UnionMainTransfer> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的联盟转移列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMainTransfer>
     * @throws Exception 统一处理异常
     */
    List<UnionMainTransfer> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionMainTransfer>
     * @throws Exception 统一处理异常
     */
    List<UnionMainTransfer> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionMainTransfer> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionMainTransfer 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionMainTransfer newUnionMainTransfer) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionMainTransferList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionMainTransfer> newUnionMainTransferList) throws Exception;

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
     * @param updateUnionMainTransfer 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionMainTransfer updateUnionMainTransfer) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionMainTransferList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionMainTransfer> updateUnionMainTransferList) throws Exception;

}