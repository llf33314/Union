package com.gt.union.main.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.main.entity.UnionMainTransfer;

import java.util.List;

/**
 * 联盟转移 服务接口
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
public interface IUnionMainTransferService extends IService<UnionMainTransfer> {

    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    /**
     * 根据联盟id、转移盟主权限的盟员身份id、目标盟员身份id和确认状态，获取盟主权限转移记录
     *
     * @param unionId       {not null} 联盟id
     * @param fromMemberId  {not null} 转移盟主权限的盟员身份id
     * @param toMemberId    {not null} 目标盟员身份id
     * @param confirmStatus {not null} 确认状态
     * @return UnionMainTransfer
     * @throws Exception 全局处理异常
     */
    UnionMainTransfer getByUnionIdAndFromMemberIdAndToMemberIdAndConfirmStatus(Integer unionId, Integer fromMemberId,
                                                                               Integer toMemberId, Integer confirmStatus) throws Exception;

    /**
     * 根据转移申请id、接受者盟员身份id和确认状态，获取转移申请信息
     *
     * @param transferId    {not null} 转移申请id
     * @param toMemberId    {not null} 接受者盟员身份id
     * @param confirmStatus {not null} 确认状态
     * @return UnionMainTransfer
     * @throws Exception 全局处理异常
     */
    UnionMainTransfer getByIdAndToMemberIdAndConfirmStatus(Integer transferId, Integer toMemberId, Integer confirmStatus) throws Exception;

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    /**
     * 根据商家id和转移者盟员身份id，分页获取盟主服务转移申请列表
     *
     * @param page         {not null} 分页对象
     * @param busId        {not null} 商家id
     * @param fromMemberId {not null} 转移者盟员身份id
     * @return Page
     * @throws Exception 全局处理异常
     */
    Page pageMapByBusIdAndFromMemberId(Page page, Integer busId, Integer fromMemberId) throws Exception;

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    /**
     * 根据商家id、转移盟主权限的盟员身份id和目标盟员身份id，保存盟主权限转移信息
     *
     * @param busId        {not null} 商家id
     * @param fromMemberId {not null} 转移盟主权限的盟员身份id
     * @param toMemberId   {not null} 目标盟员身份id
     * @throws Exception 全局处理异常
     */
    void saveByBusIdAndFromMemberIdAndToMemberId(Integer busId, Integer fromMemberId, Integer toMemberId) throws Exception;

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    /**
     * 根据转移申请id、商家id、接受者盟员身份id和是否同意，更新盟主服务权限转移信息
     *
     * @param transferId {not null} 转移申请id
     * @param busId      {not null} 商家id
     * @param toMemberId {not null} 接受者盟员身份id
     * @param isOK       {not null} 是否同意，1为是，0为否
     * @throws Exception 全局处理异常
     */
    void updateStatusByTransferIdAndBusIdAndToMemberId(Integer transferId, Integer busId, Integer toMemberId, Integer isOK) throws Exception;

    /**
     * 根据转移申请id、商家id和转移者的盟员身份id， 撤回盟主服务转移申请
     *
     * @param transferId   {not null} 转移申请id
     * @param busId        {not null} 商家id
     * @param fromMemberId {not null} 转移者的盟员身份id
     * @throws Exception 全局处理异常
     */
    void revokeByIdAndBusIdAndFromMemberId(Integer transferId, Integer busId, Integer fromMemberId) throws Exception;

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    //******************************************* Object As a Service - get ********************************************

    /**
     * 根据id查询对象
     *
     * @param transferId 对象id
     * @return UnionMainTransfer
     * @throws Exception 全局处理异常
     */
    UnionMainTransfer getById(Integer transferId) throws Exception;

    //******************************************* Object As a Service - list *******************************************

    /**
     * 根据联盟id查询对象列表
     *
     * @param unionId 联盟id
     * @return List<UnionMainTransfer>
     * @throws Exception 全局处理异常
     */
    List<UnionMainTransfer> listByUnionId(Integer unionId) throws Exception;

    /**
     * 根据发起者盟员身份id查询对象列表
     *
     * @param fromMemberId 发起者盟员身份id
     * @return List<UnionMainTransfer>
     * @throws Exception 全局处理异常
     */
    List<UnionMainTransfer> listByFromMemberId(Integer fromMemberId) throws Exception;

    /**
     * 根据目标者盟员身份id查询对象列表
     *
     * @param toMemberId 目标者盟员身份id
     * @return List<UnionMainTransfer>
     * @throws Exception 全局处理异常
     */
    List<UnionMainTransfer> listByToMemberId(Integer toMemberId) throws Exception;

    //******************************************* Object As a Service - save *******************************************

    /**
     * 新增对象
     *
     * @param newTransfer 新增的对象
     * @throws Exception 全局处理异常
     */
    void save(UnionMainTransfer newTransfer) throws Exception;

    /**
     * 批量新增对象
     *
     * @param newTransferList 批量新增的对象列表
     * @throws Exception 全局处理异常
     */
    void saveBatch(List<UnionMainTransfer> newTransferList) throws Exception;

    //******************************************* Object As a Service - remove *****************************************

    /**
     * 根据id删除对象
     *
     * @param transferId 对象id
     * @throws Exception 全局处理异常
     */
    void removeById(Integer transferId) throws Exception;

    /**
     * 根据id列表批量删除对象
     *
     * @param transferIdList 对象id列表
     * @throws Exception 全局处理异常
     */
    void removeBatchById(List<Integer> transferIdList) throws Exception;

    //******************************************* Object As a Service - update *****************************************

    /**
     * 修改对象
     *
     * @param updateTransfer 修改的对象
     * @throws Exception 全局处理异常
     */
    void update(UnionMainTransfer updateTransfer) throws Exception;

    /**
     * 批量修改对象
     *
     * @param updateTransferList 批量修改的对象列表
     * @throws Exception 全局处理异常
     */
    void updateBatch(List<UnionMainTransfer> updateTransferList) throws Exception;
}
