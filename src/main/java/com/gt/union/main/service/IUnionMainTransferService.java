package com.gt.union.main.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.main.entity.UnionMainTransfer;
import com.gt.union.main.entity.UnionMainTransfer;

import java.util.List;

/**
 * <p>
 * 联盟转移 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionMainTransferService extends IService<UnionMainTransfer> {
    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - get *********************************************
     ******************************************************************************************************************/

    /**
     * 根据联盟id、转移盟主权限的盟员身份id、目标盟员身份id和确认状态，获取盟主权限转移记录
     *
     * @param unionId       {not null} 联盟id
     * @param fromMemberId  {not null} 转移盟主权限的盟员身份id
     * @param toMemberId    {not null} 目标盟员身份id
     * @param confirmStatus {not null} 确认状态
     * @return
     * @throws Exception
     */
    UnionMainTransfer getByUnionIdAndFromMemberIdAndToMemberIdAndConfirmStatus(Integer unionId, Integer fromMemberId
            , Integer toMemberId, Integer confirmStatus) throws Exception;


    /**
     * 根据转移申请id、接受者盟员身份id和确认状态，获取转移申请信息
     *
     * @param transferId    {not null} 转移申请id
     * @param toMemberId    {not null} 接受者盟员身份id
     * @param confirmStatus {not null} 确认状态
     * @return
     * @throws Exception
     */
    UnionMainTransfer getByIdAndToMemberIdAndConfirmStatus(Integer transferId, Integer toMemberId, Integer confirmStatus) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - list ********************************************
     ******************************************************************************************************************/

    /**
     * 根据商家id和转移者盟员身份id，分页获取盟主服务转移申请列表
     *
     * @param page         {not null} 分页对象
     * @param busId        {not null} 商家id
     * @param fromMemberId {not null} 转移者盟员身份id
     * @return
     * @throws Exception
     */
    Page pageMapByBusIdAndFromMemberId(Page page, Integer busId, Integer fromMemberId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - save ********************************************
     ******************************************************************************************************************/

    /**
     * 根据商家id、转移盟主权限的盟员身份id和目标盟员身份id，保存盟主权限转移信息
     *
     * @param busId        {not null} 商家id
     * @param fromMemberId {not null} 转移盟主权限的盟员身份id
     * @param toMemberId   {not null} 目标盟员身份id
     * @throws Exception
     */
    void saveByBusIdAndFromMemberIdAndToMemberId(Integer busId, Integer fromMemberId, Integer toMemberId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - remove ******************************************
     ******************************************************************************************************************/
    
    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - update ******************************************
     ******************************************************************************************************************/

    /**
     * 根据转移申请id、商家id、接受者盟员身份id和是否同意，更新盟主服务权限转移信息
     *
     * @param transferId {not null} 转移申请id
     * @param busId      {not null} 商家id
     * @param toMemberId {not null} 接受者盟员身份id
     * @param isOK       {not null} 是否同意，1为是，0为否
     * @throws Exception
     */
    void updateStatusByTransferIdAndBusIdAndToMemberId(Integer transferId, Integer busId, Integer toMemberId, Integer isOK) throws Exception;

    /**
     * 根据转移申请id、商家id和转移者的盟员身份id， 撤回盟主服务转移申请
     *
     * @param transferId   {not null} 转移申请id
     * @param busId        {not null} 商家id
     * @param fromMemberId {not null} 转移者的盟员身份id
     * @throws Exception
     */
    void revokeByIdAndBusIdAndFromMemberId(Integer transferId, Integer busId, Integer fromMemberId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - count *******************************************
     ******************************************************************************************************************/
    
    /*******************************************************************************************************************
     ****************************************** Domain Driven Design - boolean *****************************************
     ******************************************************************************************************************/

    /*******************************************************************************************************************
     ****************************************** Object As a Service - get **********************************************
     ******************************************************************************************************************/

    UnionMainTransfer getById(Integer transferId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - list *********************************************
     ******************************************************************************************************************/

    List<UnionMainTransfer> listByUnionId(Integer unionId) throws Exception;

    List<UnionMainTransfer> listByFromMemberId(Integer fromMemberId) throws Exception;

    List<UnionMainTransfer> listByToMemberId(Integer toMemberId) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - save *********************************************
     ******************************************************************************************************************/

    void save(UnionMainTransfer newTransfer) throws Exception;

    void saveBatch(List<UnionMainTransfer> newTransferList) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - remove *******************************************
     ******************************************************************************************************************/

    void removeById(Integer transferId) throws Exception;

    void removeBatchById(List<Integer> transferIdList) throws Exception;

    /*******************************************************************************************************************
     ****************************************** Object As a Service - update *******************************************
     ******************************************************************************************************************/

    void update(UnionMainTransfer updateTransfer) throws Exception;

    void updateBatch(List<UnionMainTransfer> updateTransferList) throws Exception;

}
