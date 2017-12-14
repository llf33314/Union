package com.gt.union.union.member.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.union.member.entity.UnionMemberJoin;
import com.gt.union.union.member.vo.MemberJoinCreateVO;
import com.gt.union.union.member.vo.MemberJoinVO;

import java.util.List;

/**
 * 入盟申请 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 10:28:35
 */
public interface IUnionMemberJoinService extends IService<UnionMemberJoin> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 获取入盟申请信息
     *
     * @param joinId  入盟申请id
     * @param unionId 联盟id
     * @return UnionMemberJoin
     * @throws Exception 统一处理异常
     */
    UnionMemberJoin getByIdAndUnionId(Integer joinId, Integer unionId) throws Exception;

    /**
     * 获取入盟申请信息
     *
     * @param applyMemberId 申请盟员id
     * @param unionId       联盟id
     * @return UnionMemberJoin
     * @throws Exception 统一处理异常
     */
    UnionMemberJoin getByApplyMemberIdAndUnionId(Integer applyMemberId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 入盟审核-分页
     *
     * @param buId          商家id
     * @param unionId       联盟id
     * @param optMemberName 盟员名称
     * @param optPhone      盟员手机号
     * @return List<MemberJoinVO>
     * @throws Exception 统一处理异常
     */
    List<MemberJoinVO> listMemberJoinVOByBusIdAndUnionId(Integer buId, Integer unionId, String optMemberName, String optPhone) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 加入联盟-保存；推荐入盟
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @param type    入盟类型
     * @param vo      表单信息
     * @throws Exception 统一处理异常
     */
    void saveJoinCreateVOByBusIdAndUnionIdAndType(Integer busId, Integer unionId, Integer type, MemberJoinCreateVO vo) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 通过或不通过入盟申请
     *
     * @param busId   商家id
     * @param joinId  入盟申请id
     * @param unionId 联盟id
     * @param isPass  是否通过(0：否 1：是)
     * @throws Exception 统一处理异常
     */
    void updateStatusByBusIdAndIdAndUnionId(Integer busId, Integer joinId, Integer unionId, Integer isPass) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    /**
     * 根据入盟盟员id进行过滤
     *
     * @param joinList      数据源
     * @param applyMemberId 入盟盟员id
     * @return List<UnionMemberJoin>
     * @throws Exception 统一处理异常
     */
    List<UnionMemberJoin> filterByApplyMemberId(List<UnionMemberJoin> joinList, Integer applyMemberId) throws Exception;

}