package com.gt.union.union.member.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.vo.MemberVO;

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
     * 我的联盟-联盟设置-基础设置；我的联盟-联盟卡设置-折扣卡设置-折扣设置
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    MemberVO getMemberVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 获取具有读权限的盟员信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getReadByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 获取具有写权限的盟员信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getWriteByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 获取盟主信息
     *
     * @param busId 商家id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getOwnerByBusId(Integer busId) throws Exception;

    /**
     * 我的联盟-首页-盟员列表-分页数据-详情
     *
     * @param busId    商家id
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getByBusIdAndIdAndUnionId(Integer busId, Integer memberId, Integer unionId) throws Exception;

    /**
     * 获取盟主信息
     *
     * @param unionId 联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getOwnerByUnionId(Integer unionId) throws Exception;

    /**
     * 获取具有读权限的盟员信息
     *
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getReadByIdAndUnionId(Integer memberId, Integer unionId) throws Exception;

    /**
     * 获取具有写权限的盟员信息
     *
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getWriteByIdAndUnionId(Integer memberId, Integer unionId) throws Exception;

    /**
     * 获取盟员信息
     *
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @param status   盟员状态
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getByIdAndUnionIdAndStatus(Integer memberId, Integer unionId, Integer status) throws Exception;

    /**
     * 获取盟员信息
     *
     * @param memberId id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getById(Integer memberId) throws Exception;

    /**
     * 获取盟员id列表
     *
     * @param memberList 盟员列表
     * @return List<Integer>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionMember> memberList) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 获取具有读权限的盟员列表信息
     *
     * @param busId 商家id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listReadByBusId(Integer busId) throws Exception;

    /**
     * 获取具有写权限的盟员列表信息
     *
     * @param busId 商家id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listWriteByBusId(Integer busId) throws Exception;

    /**
     * 分页：我的联盟-首页-盟员列表；导出：我的联盟-首页-盟员列表；分页：我的联盟-联盟卡设置-折扣卡设置
     *
     * @param busId         商家id
     * @param unionId       联盟id
     * @param optMemberName 盟员名称
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listWriteByBusIdAndUnionId(Integer busId, Integer unionId, String optMemberName) throws Exception;

    /**
     * 辅助接口：获取指定联盟下具有写权限的但不包括我的盟员列表
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listOtherWriteByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 获取其他具有读权限的盟员列表信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listOtherReadByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 获取具有读权限的盟员列表信息
     *
     * @param unionId 联盟id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listReadByUnionId(Integer unionId) throws Exception;

    /**
     * 获取具有写权限的盟员列表信息
     *
     * @param unionId 联盟id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listWriteByUnionId(Integer unionId) throws Exception;

    /**
     * 获取盟员列表信息
     *
     * @param unionId 联盟id
     * @param status  盟员状态
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listByUnionIdAndStatus(Integer unionId, Integer status) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 保存
     *
     * @param saveMember 保存内容
     * @throws Exception Exception
     */
    void save(UnionMember saveMember) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 更新
     *
     * @param unionMember 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionMember unionMember) throws Exception;

    /**
     * 我的联盟-联盟设置-基础设置-保存
     *
     * @param busId    商家id
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @param vo       更新内容
     * @throws Exception 统一处理异常
     */
    void updateByBusIdAndIdAndUnionId(Integer busId, Integer memberId, Integer unionId, UnionMember vo) throws Exception;

    /**
     * 我的联盟-联盟卡设置-折扣卡设置-折扣设置-更新
     *
     * @param busId    商家id
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @param discount 折扣
     * @throws Exception 统一处理异常
     */
    void updateDiscountByBusIdAndIdAndUnionId(Integer busId, Integer memberId, Integer unionId, Double discount) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    /**
     * 统计具有读权限的盟员个数
     *
     * @param busId 商家id
     * @return Integer
     * @throws Exception 统一处理异常
     */
    Integer countReadByBusId(Integer busId) throws Exception;

    /**
     * 统计具有读权限的盟员个数
     *
     * @param unionId 联盟id
     * @return Integer
     * @throws Exception 统一处理异常
     */
    Integer countReadByUnionId(Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - boolean *****************************************

    /**
     * 判断是否存在盟员信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - filter ******************************************

    /**
     * 根据是否盟主字段进行过滤
     *
     * @param memberList   数据源
     * @param isUnionOwner 是否盟主
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> filterByIsUnionOwner(List<UnionMember> memberList, Integer isUnionOwner) throws Exception;

    /**
     * 根据盟员状态列表进行过滤
     *
     * @param memberList 数据源
     * @param statusList 状态列表
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> filterByStatusList(List<UnionMember> memberList, List<Integer> statusList) throws Exception;

    /**
     * 根据盟员状态进行过滤
     *
     * @param memberList 数据源
     * @param status     状态
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> filterByStatus(List<UnionMember> memberList, Integer status) throws Exception;

    /**
     * 根据联盟id进行过滤
     *
     * @param memberList 数据源
     * @param unionId    联盟id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> filterByUnionId(List<UnionMember> memberList, Integer unionId) throws Exception;

}