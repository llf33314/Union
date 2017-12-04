package com.gt.union.union.member.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.union.member.entity.UnionMemberOut;

import java.util.List;

/**
 * 退盟申请 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 10:28:35
 */
public interface IUnionMemberOutService extends IService<UnionMemberOut> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 根据退盟盟员id和联盟id，获取退盟申请信息
     *
     * @param applyMemberId 退盟盟员id
     * @param unionId       联盟id
     * @return UnionMemberOut
     * @throws Exception 统一处理异常
     */
    UnionMemberOut getByApplyMemberIdAndUnionId(Integer applyMemberId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 盟主移出盟员
     *
     * @param busId         商家id
     * @param unionId       联盟id
     * @param applyMemberId 被移出的盟员id
     * @throws Exception 统一处理异常
     */
    void saveByBusIdAndUnionIdAndApplyMemberId(Integer busId, Integer unionId, Integer applyMemberId) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    /**
     * 根据联盟id进行过滤
     *
     * @param outList 退盟申请列表
     * @param unionId 联盟id
     * @return List<UnionMemberOut>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberOut> filterByUnionId(List<UnionMemberOut> outList, Integer unionId) throws Exception;

}