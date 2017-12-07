package com.gt.union.union.member.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.union.member.entity.UnionMemberOut;
import com.gt.union.union.member.vo.MemberOutPeriodVO;
import com.gt.union.union.member.vo.MemberOutVO;

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

    /**
     * 根据退盟申请id和联盟id，获取退盟申请信息
     *
     * @param outId   退盟申请id
     * @param unionId 联盟id
     * @return UnionMemberOut
     * @throws Exception 统一处理异常
     */
    UnionMemberOut getByIdAndUnionId(Integer outId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 分页：获取退盟申请信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<MemberOutVO>
     * @throws Exception 统一处理异常
     */
    List<MemberOutVO> listMemberOutVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 分页：获取退盟过渡期信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<MemberOutPeriodVO>
     * @throws Exception 统一处理异常
     */
    List<MemberOutPeriodVO> listMemberOutPeriodVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

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

    /**
     * 申请退盟
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @param reason  退盟理由
     * @throws Exception 统一处理异常
     */
    void saveByBusIdAndUnionId(Integer busId, Integer unionId, String reason) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 审核退盟申请
     *
     * @param outId   退盟申请id
     * @param unionId 联盟id
     * @param busId   商家id
     * @param isPass  是否通过(0：否 1：是)
     * @throws Exception 统一处理异常
     */
    void updateStatusByIdAndUnionIdAndBusId(Integer outId, Integer unionId, Integer busId, Integer isPass) throws Exception;

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