package com.gt.union.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.union.main.entity.UnionMainTransfer;
import com.gt.union.union.main.vo.UnionTransferVO;

import java.util.List;

/**
 * 联盟转移 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 15:26:25
 */
public interface IUnionMainTransferService extends IService<UnionMainTransfer> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 获取盟主权限转移信息
     *
     * @param unionId       联盟id
     * @param toMemberId    目标盟员id
     * @param confirmStatus 确认状态
     * @return UnionMainTransfer
     * @throws Exception 统一处理异常
     */
    UnionMainTransfer getByUnionIdAndToMemberIdAndConfirmStatus(Integer unionId, Integer toMemberId, Integer confirmStatus) throws Exception;

    /**
     * 获取盟主权限转移信息
     *
     * @param unionId       联盟id
     * @param confirmStatus 确认状态
     * @return UnionMainTransfer
     * @throws Exception 统一处理异常
     */
    UnionMainTransfer getByUnionIdAndConfirmStatus(Integer unionId, Integer confirmStatus) throws Exception;

    /**
     * 获取盟主权限转移信息
     *
     * @param transferId    联盟转移id
     * @param unionId       联盟id
     * @param confirmStatus 确认状态
     * @return UnionMainTransfer
     * @throws Exception 统一处理异常
     */
    UnionMainTransfer getByIdAndUnionIdAndConfirmStatus(Integer transferId, Integer unionId, Integer confirmStatus) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 联盟设置-盟主权限转移-分页
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<UnionTransferVO>
     * @throws Exception 统一处理异常
     */
    List<UnionTransferVO> listUnionTransferVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 联盟设置-盟主权限转移-分页-转移
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param toMemberId 目标盟员id
     * @throws Exception 统一处理异常
     */
    void saveByBusIdAndUnionIdAndToMemberId(Integer busId, Integer unionId, Integer toMemberId) throws Exception;


    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 联盟设置-盟主权限转移-分页-撤销
     *
     * @param busId      商家id
     * @param transferId 联盟权限转移id
     * @param unionId    联盟id
     * @throws Exception 统一处理异常
     */
    void revokeByBusIdAndIdAndUnionId(Integer busId, Integer transferId, Integer unionId) throws Exception;

    /**
     * 盟主权限转移-接受或拒绝
     *
     * @param busId      商家id
     * @param transferId 盟主权限转移申请id
     * @param unionId    联盟id
     * @param isAccept   是否接受(0：否 1：是)
     * @throws Exception 统一处理异常
     */
    void updateAcceptByBusIdAndIdAndUnionId(Integer busId, Integer transferId, Integer unionId, Integer isAccept) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

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

}