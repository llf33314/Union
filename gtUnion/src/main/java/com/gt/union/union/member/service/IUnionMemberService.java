package com.gt.union.union.member.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.vo.MemberVO;

import java.util.List;

/**
 * 盟员 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 10:28:35
 */
public interface IUnionMemberService {
    //********************************************* Base On Business - get *********************************************

    /**
     * 获取未删除的联盟盟员信息
     *
     * @param memberId id
     * @param unionId  联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getValidByIdAndUnionId(Integer memberId, Integer unionId) throws Exception;

    /**
     * 获取未删除的联盟盟员信息
     *
     * @param memberId 盟员id
     * @param status   盟员状态
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getValidByIdAndStatus(Integer memberId, Integer status) throws Exception;

    /**
     * 获取未删除的具有读权限的联盟盟员信息
     *
     * @param memberId 盟员id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getValidReadById(Integer memberId) throws Exception;

    /**
     * 获取未删除的具有读权限的联盟盟员信息
     *
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getValidReadByIdAndUnionId(Integer memberId, Integer unionId) throws Exception;

    /**
     * 获取未删除的具有写权限的联盟盟员信息
     *
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getValidWriteByIdAndUnionId(Integer memberId, Integer unionId) throws Exception;

    /**
     * 获取未删除的联盟盟主信息
     *
     * @param unionId 联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getValidOwnerByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的商家盟主信息
     *
     * @param busId 商家id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getValidOwnerByBusId(Integer busId) throws Exception;

    /**
     * 获取未删除的具有读权限的商家盟员信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getValidReadByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 获取未删除的具有写权限的商家盟员信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getValidWriteByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 获取未删除的联盟盟员信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @param status  盟员状态
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getValidByBusIdAndUnionIdAndStatus(Integer busId, Integer unionId, Integer status) throws Exception;

    /**
     * 获取商家盟员信息
     *
     * @param busId    商家id
     * @param memberId 盟员id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getByBusIdAndId(Integer busId, Integer memberId) throws Exception;

    /**
     * 获取商家盟员信息
     *
     * @param memberId 盟员id
     * @param unionId    商家id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getByIdAndUnionId(Integer memberId, Integer unionId) throws Exception;

    /**
     * 获取商家盟员信息
     *
     * @param busId    商家id
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getByBusIdAndIdAndUnionId(Integer busId, Integer memberId, Integer unionId) throws Exception;

    /**
     * 我的联盟-首页-盟员列表-分页数据-详情
     *
     * @param busId    商家id
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getValidByBusIdAndIdAndUnionId(Integer busId, Integer memberId, Integer unionId) throws Exception;

    /**
     * 我的联盟-联盟设置-基础设置；我的联盟-联盟卡设置-折扣卡设置-折扣设置
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    MemberVO getMemberVOByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    //********************************************* Base On Business - list ********************************************

    /**
     * 获取未删除的联盟盟员列表信息
     *
     * @param status 盟员状态
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listValidByStatus(Integer status) throws Exception;

    /**
     * 获取未删除的具有读权限的联盟盟员列表信息
     *
     * @param unionId 联盟id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listValidReadByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的联盟盟员列表信息
     *
     * @param unionId 联盟id
     * @param status  盟员状态
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listValidByUnionIdAndStatus(Integer unionId, Integer status) throws Exception;

    /**
     * 获取未删除的具有写权限的联盟盟员列表信息
     *
     * @param unionId 联盟id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listValidWriteByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的具有读权限的商家盟员列表信息
     *
     * @param busId 商家id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listValidReadByBusId(Integer busId) throws Exception;

    /**
     * 获取未删除的具有写权限的商家盟员列表信息
     *
     * @param busId 商家id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listValidWriteByBusId(Integer busId) throws Exception;

    /**
     * 获取商家盟员列表信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 获取其他未删除的具有读权限的联盟盟员列表信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listOtherValidReadByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 获取其他未删除的具有写权限的联盟盟员列表信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listOtherValidWriteByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 分页：我的联盟-首页-盟员列表；导出：我的联盟-首页-盟员列表；分页：我的联盟-联盟卡设置-折扣卡设置
     *
     * @param busId         商家id
     * @param unionId       联盟id
     * @param optMemberName 盟员名称
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listValidReadByBusIdAndUnionId(Integer busId, Integer unionId, String optMemberName) throws Exception;

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

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

    //********************************************* Base On Business - other *******************************************

    /**
     * 统计未删除的具有读权限的商家盟员数量
     *
     * @param busId 商家id
     * @return Integer
     * @throws Exception 统一处理异常
     */
    Integer countValidReadByBusId(Integer busId) throws Exception;

    /**
     * 统计未删除的具有读权限的联盟盟员数量
     *
     * @param unionId 联盟id
     * @return Integer
     * @throws Exception 统一处理异常
     */
    Integer countValidReadByUnionId(Integer unionId) throws Exception;

    /**
     * 是否存在未删除的联盟盟员信息
     *
     * @param memberId 盟员id
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existValidReadById(Integer memberId) throws Exception;

    /**
     * 是否存在未删除的联盟盟员信息
     *
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existValidReadByIdAndUnionId(Integer memberId, Integer unionId) throws Exception;

    /**
     * 是否存在未删除的商家盟主信息
     *
     * @param busId 商家id
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existValidOwnerByBusId(Integer busId) throws Exception;

    /**
     * 是否存在未删除的具有读权限的商家盟员信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existValidReadByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    /**
     * 是否存在未删除的商家盟员信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @param status  盟员状态
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existValidByBusIdAndUnionIdAndStatus(Integer busId, Integer unionId, Integer status) throws Exception;

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionMemberList 数据源
     * @param delStatus       删除状态
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> filterByDelStatus(List<UnionMember> unionMemberList, Integer delStatus) throws Exception;

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
     * 根据盟员状态进行过滤
     *
     * @param memberList 数据源
     * @param status     状态
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> filterByStatus(List<UnionMember> memberList, Integer status) throws Exception;

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
     * 根据联盟id进行过滤
     *
     * @param memberList 数据源
     * @param unionId    联盟id
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> filterByUnionId(List<UnionMember> memberList, Integer unionId) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取盟员信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getById(Integer id) throws Exception;

    /**
     * 获取未删除的盟员信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的盟员信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionMember
     * @throws Exception 统一处理异常
     */
    UnionMember getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionMemberList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionMember> unionMemberList) throws Exception;

    /**
     * 辅助：获取盟员集对应的的联盟id列表
     *
     * @param memberList 盟员列表
     * @return List<Integer>
     * @throws Exception 统一处理异常
     */
    List<Integer> getUnionIdList(List<UnionMember> memberList) throws Exception;

    /**
     * 辅助：获取盟员集对应的的商家id列表
     *
     * @param memberList 盟员列表
     * @return List<Integer>
     * @throws Exception 统一处理异常
     */
    List<Integer> getBusIdList(List<UnionMember> memberList) throws Exception;

    /**
     * 获取盟员列表信息(by myBatisGenerator)
     *
     * @param busId busId
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listByBusId(Integer busId) throws Exception;

    /**
     * 获取未删除的盟员列表信息(by myBatisGenerator)
     *
     * @param busId busId
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listValidByBusId(Integer busId) throws Exception;

    /**
     * 获取已删除的盟员列表信息(by myBatisGenerator)
     *
     * @param busId busId
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listInvalidByBusId(Integer busId) throws Exception;

    /**
     * 获取盟员列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的盟员列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的盟员列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionMember>
     * @throws Exception 统一处理异常
     */
    List<UnionMember> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionMember> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionMember 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionMember newUnionMember) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionMemberList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionMember> newUnionMemberList) throws Exception;

    //****************************************** Object As a Service - remove ******************************************

    /**
     * 移除(by myBatisGenerator)
     *
     * @param id 移除内容
     * @throws Exception 统一处理异常
     */
    void removeById(Integer id) throws Exception;

    /**
     * 批量移除(by myBatisGenerator)
     *
     * @param idList 移除内容
     * @throws Exception 统一处理异常
     */
    void removeBatchById(List<Integer> idList) throws Exception;

    //****************************************** Object As a Service - update ******************************************

    /**
     * 更新(by myBatisGenerator)
     *
     * @param updateUnionMember 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionMember updateUnionMember) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionMemberList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionMember> updateUnionMemberList) throws Exception;

}