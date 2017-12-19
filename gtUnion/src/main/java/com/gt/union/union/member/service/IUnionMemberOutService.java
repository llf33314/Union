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
     * 获取退盟申请信息
     *
     * @param unionId       联盟id
     * @param applyMemberId 退盟盟员id
     * @return UnionMemberOut
     * @throws Exception 统一处理异常
     */
    UnionMemberOut getByUnionIdAndApplyMemberId(Integer unionId, Integer applyMemberId) throws Exception;

    /**
     * 获取退盟申请信息
     *
     * @param outId   退盟申请id
     * @param unionId 联盟id
     * @return UnionMemberOut
     * @throws Exception 统一处理异常
     */
    UnionMemberOut getByIdAndUnionId(Integer outId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 分页：我的联盟-退盟管理-退盟审核
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<MemberOutVO>
     * @throws Exception 统一处理异常
     */
    List<MemberOutVO> listMemberOutVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 分页：我的联盟-退盟管理-退盟过渡期
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<MemberOutPeriodVO>
     * @throws Exception 统一处理异常
     */
    List<MemberOutPeriodVO> listMemberOutPeriodVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 我的联盟-首页-盟员列表-分页数据-移出
     *
     * @param busId         商家id
     * @param unionId       联盟id
     * @param applyMemberId 被移出的盟员id
     * @throws Exception 统一处理异常
     */
    void saveByBusIdAndUnionIdAndApplyMemberId(Integer busId, Integer unionId, Integer applyMemberId) throws Exception;

    /**
     * 我的联盟-退盟管理-退盟过渡期-退盟申请
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
     * 我的联盟-退盟管理-退盟审核-分页数据-同意或拒绝
     *
     * @param busId   商家id
     * @param outId   退盟申请id
     * @param unionId 联盟id
     * @param isPass  是否通过(0：否 1：是)
     * @throws Exception 统一处理异常
     */
    void updateStatusByBusIdAndIdAndUnionId(Integer busId, Integer outId, Integer unionId, Integer isPass) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    /**
     * 根据退盟盟员id进行过滤
     *
     * @param outList       退盟申请列表
     * @param applyMemberId 退盟盟员id
     * @return List<UnionMemberOut>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberOut> filterByApplyMemberId(List<UnionMemberOut> outList, Integer applyMemberId) throws Exception;

}