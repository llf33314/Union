package com.gt.union.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.union.main.entity.UnionMainTransfer;

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
     * 根据联盟id、目标盟员id和确认状态，获取盟主权限转移信息
     *
     * @param unionId       联盟id
     * @param toMemberId    目标盟员id
     * @param confirmStatus 确认状态
     * @return UnionMainTransfer
     * @throws Exception 统一处理异常
     */
    UnionMainTransfer getByUnionIdAndToMemberIdAndConfirmStatus(Integer unionId, Integer toMemberId, Integer confirmStatus) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

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