package com.gt.union.union.member.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.union.member.entity.UnionMember;

import java.util.List;

/**
 * 盟员 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 10:28:35
 */
public interface IUnionMemberService extends IService<UnionMember> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 根据商家id和联盟id，获取商家在联盟具有读权限的盟员信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getReadByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 根据商家id和联盟id，获取商家在联盟具有写权限的盟员信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getWriteByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 根据联盟id，获取盟主信息
     *
     * @param unionId 联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getOwnerByUnionId(Integer unionId) throws Exception;

    /**
     * 根据商家id，获取盟主信息
     *
     * @param busId 商家id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getOwnerByBusId(Integer busId) throws Exception;

    /**
     * controller专用：商家获取指定盟员信息
     *
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @param busId    商家id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getByIdAndUnionIdAndBusId(Integer memberId, Integer unionId, Integer busId) throws Exception;

    /**
     * 根据盟员id和联盟id，获取读权限盟员信息
     *
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getReadByIdAndUnionId(Integer memberId, Integer unionId) throws Exception;

    /**
     * 根据盟员id和联盟id，获取写权限盟员信息
     *
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getWriteByIdAndUnionId(Integer memberId, Integer unionId) throws Exception;

    /**
     * 根据盟员id和联盟id，获取申请状态中的盟员信息
     *
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getApplyByIdAndUnionId(Integer memberId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 获取商家所有具有读权限的盟员身份信息
     *
     * @param busId 商家id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listReadByBusId(Integer busId) throws Exception;

    /**
     * 获取商家所有具有写权限的盟员身份信息
     *
     * @param busId 商家id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listWriteByBusId(Integer busId) throws Exception;

    /**
     * 获取联盟中所有具有读权限的盟员身份信息
     *
     * @param unionId 联盟id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listReadByUnionId(Integer unionId) throws Exception;

    /**
     * 获取联盟中所有具有写权限的盟员身份信息
     *
     * @param unionId 联盟id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listWriteByUnionId(Integer unionId) throws Exception;

    /**
     * controller专用：获取联盟中所有具有写权限的盟员身份信息，并按盟主>商家盟员>其他盟员，其他盟员按时间顺序排序
     *
     * @param busId         商家id
     * @param unionId       联盟id
     * @param optMemberName 盟员名称
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listWriteByBusIdAndUnionId(Integer busId, Integer unionId, String optMemberName) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 保存新增盟员
     *
     * @param saveMember 新增盟员
     * @throws Exception Exception
     */
    void save(UnionMember saveMember) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 更新盟员
     *
     * @param unionMember 盟员
     * @throws Exception 统一处理异常
     */
    void update(UnionMember unionMember) throws Exception;

    /**
     * 更新盟员
     *
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @param busId    商家id
     * @param vo       更新内容
     * @throws Exception 统一处理异常
     */
    void updateByIdAndUnionIdAndBusId(Integer memberId, Integer unionId, Integer busId, UnionMember vo) throws Exception;

    /**
     * 更新盟员折扣
     *
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @param busId    商家id
     * @param discount 折扣
     * @throws Exception
     */
    void updateDiscountByIdAndUnionIdAndBusId(Integer memberId, Integer unionId, Integer busId, Double discount) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    /**
     * 统计商家已入盟或创建的联盟盟员数
     *
     * @param busId 商家id
     * @return Integer
     * @throws Exception 统一处理异常
     */
    Integer countReadByBusId(Integer busId) throws Exception;

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

    /**
     * 根据是否盟主字段进行过滤
     *
     * @param memberList   盟员列表
     * @param isUnionOwner 是否盟主
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> filterByIsUnionOwner(List<UnionMember> memberList, Integer isUnionOwner) throws Exception;

    /**
     * 根据盟员状态进行过滤
     *
     * @param memberList 盟员列表
     * @param statusList 状态列表
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> filterByStatus(List<UnionMember> memberList, List<Integer> statusList) throws Exception;

    /**
     * 根据联盟id进行过滤
     *
     * @param memberList 盟员列表
     * @param unionId    联盟id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> filterByUnionId(List<UnionMember> memberList, Integer unionId) throws Exception;

}