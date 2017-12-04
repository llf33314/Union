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
     * 商家获取指定盟员信息
     *
     * @param memberId 盟员id
     * @param busId    商家id
     * @param unionId  联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getByIdAndBusIdAndUnionId(Integer memberId, Integer busId, Integer unionId) throws Exception;

    /**
     * 根据盟员id和联盟id，获取盟员信息
     *
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getByIdAndUnionId(Integer memberId, Integer unionId) throws Exception;

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
     * 获取联盟中所有具有写权限的盟员身份信息，并按盟主>商家盟员>其他盟员，其他盟员按时间顺序排序
     *
     * @param busId         商家id
     * @param unionId       联盟id
     * @param optMemberName 盟员名称
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listWriteByBusIdAndUnionId(Integer busId, Integer unionId, String optMemberName) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 自定义更新
     *
     * @param unionMember 盟员
     * @throws Exception 统一处理异常
     */
    void update(UnionMember unionMember) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

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