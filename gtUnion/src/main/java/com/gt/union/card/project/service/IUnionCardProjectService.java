package com.gt.union.card.project.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.vo.CardProjectCheckUpdateVO;
import com.gt.union.card.project.vo.CardProjectCheckVO;
import com.gt.union.card.project.vo.CardProjectJoinMemberVO;
import com.gt.union.card.project.vo.CardProjectVO;

import java.util.List;

/**
 * 活动项目 服务接口
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
public interface IUnionCardProjectService {
    //********************************************* Base On Business - get *********************************************

    /**
     * 获取未删除的项目信息
     *
     * @param unionId    联盟id
     * @param memberId   盟员id
     * @param activityId 活动id
     * @return UnionCardProject
     * @throws Exception 统一处理异常
     */
    UnionCardProject getValidByUnionIdAndMemberIdAndActivityId(Integer unionId, Integer memberId, Integer activityId) throws Exception;

    /**
     * 获取未删除的项目信息
     *
     * @param unionId    联盟id
     * @param memberId   盟员id
     * @param activityId 活动id
     * @param status     项目状态
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    UnionCardProject getValidByUnionIdAndMemberIdAndActivityIdAndStatus(Integer unionId, Integer memberId, Integer activityId, Integer status) throws Exception;

    /**
     * 获取未删除的项目信息
     *
     * @param projectId  项目id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return UnionCardProject
     * @throws Exception 统一处理异常
     */
    UnionCardProject getValidByIdAndUnionIdAndActivityId(Integer projectId, Integer unionId, Integer activityId) throws Exception;

    /**
     * 我的联盟-联盟卡设置-活动卡设置-分页数据-我的活动项目
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return CardProjectVO
     * @throws Exception 统一处理异常
     */
    CardProjectVO getProjectVOByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception;

    //********************************************* Base On Business - list ********************************************

    /**
     * 获取未删除的项目列表信息
     *
     * @param unionId  联盟id
     * @param memberId 盟员id
     * @param status   项目状态
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listValidByUnionIdAndMemberIdAndStatus(Integer unionId, Integer memberId, Integer status) throws Exception;

    /**
     * 获取未删除的项目列表信息
     *
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listValidByUnionIdAndActivityId(Integer unionId, Integer activityId) throws Exception;

    /**
     * 获取未删除的项目列表信息
     *
     * @param unionId    联盟id
     * @param activityId 活动id
     * @param status     项目状态
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listValidByUnionIdAndActivityIdAndStatus(Integer unionId, Integer activityId, Integer status) throws Exception;

    /**
     * 获取未删除的项目列表信息，自动过滤已退盟的盟员
     *
     * @param unionId    联盟id
     * @param activityId 活动id
     * @param status     项目状态
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listValidWithoutExpiredMemberByUnionIdAndActivityIdAndStatus(Integer unionId, Integer activityId, Integer status) throws Exception;

    /**
     * 获取未删除的项目列表信息
     *
     * @param unionId    联盟id
     * @param activityId 活动id
     * @param status     项目状态
     * @param orderBy    排序字段
     * @param isAsc      是否升序
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listValidByUnionIdAndActivityIdAndStatus(Integer unionId, Integer activityId, Integer status, String orderBy, boolean isAsc) throws Exception;

    /**
     * 我的联盟-联盟卡设置-活动卡设置-分页数据-参与盟员数
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return List<CardProjectJoinMemberVO>
     * @throws Exception 统一处理异常
     */
    List<CardProjectJoinMemberVO> listJoinMemberByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception;

    /**
     * 我的联盟-联盟卡设置-活动卡设置-分页数据-待审核
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return List<CardProjectCheckVO>
     * @throws Exception 统一处理异常
     */
    List<CardProjectCheckVO> listProjectCheckByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception;

    //********************************************* Base On Business - save ********************************************

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    /**
     * 我的联盟-联盟卡设置-活动卡设置-分页数据-审核项目-通过或不通过
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @param vo         表单内容
     * @param isPass     是否通过(0：否 1：是)
     * @throws Exception 统一处理异常
     */
    void updateByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId, Integer isPass, CardProjectCheckUpdateVO vo) throws Exception;

    //********************************************* Base On Business - other *******************************************

    /**
     * 统计未删除的项目个数
     *
     * @param activityId 活动id
     * @param unionId    联盟id
     * @param status     项目状态
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    Integer countValidByUnionIdAndActivityIdAndStatus(Integer unionId, Integer activityId, Integer status) throws Exception;

    /**
     * 是否存在未删除的项目信息
     *
     * @param unionId    联盟id
     * @param memberId   盟员id
     * @param activityId 活动id
     * @param status     项目状态
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existValidByUnionIdAndMemberIdAndActivityIdAndStatus(Integer unionId, Integer memberId, Integer activityId, Integer status) throws Exception;

    /**
     * 是否存在活动项目所有者是盟主
     *
     * @param projectList 活动项目列表
     * @return boolean
     * @throws Exception 统一处理异常
     */
    boolean existUnionOwnerId(List<UnionCardProject> projectList) throws Exception;

    //********************************************* Base On Business - filter ******************************************

    /**
     * 根据删除状态进行过滤(by myBatisGenerator)
     *
     * @param unionCardProjectList 数据源
     * @param delStatus            删除状态
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> filterByDelStatus(List<UnionCardProject> unionCardProjectList, Integer delStatus) throws Exception;

    /**
     * 根据盟员id进行过滤
     *
     * @param projectList 数据源
     * @param memberId    盟员id
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> filterByMemberId(List<UnionCardProject> projectList, Integer memberId) throws Exception;

    /**
     * 过滤掉盟员已退盟的数据
     *
     * @param projectList 数据源
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> filterInvalidMemberId(List<UnionCardProject> projectList) throws Exception;

    /**
     * 根据联盟id进行过滤
     *
     * @param projectList 数据源
     * @param unionId     联盟id
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> filterByUnionId(List<UnionCardProject> projectList, Integer unionId) throws Exception;

    /**
     * 根据项目状态进行过滤
     *
     * @param projectList 数据源
     * @param status      项目状态
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> filterByStatus(List<UnionCardProject> projectList, Integer status) throws Exception;

    //****************************************** Object As a Service - get *********************************************

    /**
     * 获取项目信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardProject
     * @throws Exception 统一处理异常
     */
    UnionCardProject getById(Integer id) throws Exception;

    /**
     * 获取未删除的项目信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardProject
     * @throws Exception 统一处理异常
     */
    UnionCardProject getValidById(Integer id) throws Exception;

    /**
     * 获取已删除的项目信息(by myBatisGenerator)
     *
     * @param id id
     * @return UnionCardProject
     * @throws Exception 统一处理异常
     */
    UnionCardProject getInvalidById(Integer id) throws Exception;

    //****************************************** Object As a Service - list ********************************************

    /**
     * 获取对象集对应的的主键集(by myBatisGenerator)
     *
     * @param unionCardProjectList 对象集
     * @return List<Id>
     * @throws Exception 统一处理异常
     */
    List<Integer> getIdList(List<UnionCardProject> unionCardProjectList) throws Exception;


    /**
     * 获取项目列表信息(by myBatisGenerator)
     *
     * @param activityId activityId
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listByActivityId(Integer activityId) throws Exception;

    /**
     * 获取未删除的项目列表信息(by myBatisGenerator)
     *
     * @param activityId activityId
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listValidByActivityId(Integer activityId) throws Exception;

    /**
     * 获取已删除的项目列表信息(by myBatisGenerator)
     *
     * @param activityId activityId
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listInvalidByActivityId(Integer activityId) throws Exception;

    /**
     * 获取项目列表信息(by myBatisGenerator)
     *
     * @param memberId memberId
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listByMemberId(Integer memberId) throws Exception;

    /**
     * 获取未删除的项目列表信息(by myBatisGenerator)
     *
     * @param memberId memberId
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listValidByMemberId(Integer memberId) throws Exception;

    /**
     * 获取已删除的项目列表信息(by myBatisGenerator)
     *
     * @param memberId memberId
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listInvalidByMemberId(Integer memberId) throws Exception;

    /**
     * 获取项目列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listByUnionId(Integer unionId) throws Exception;

    /**
     * 获取未删除的项目列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listValidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取已删除的项目列表信息(by myBatisGenerator)
     *
     * @param unionId unionId
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listInvalidByUnionId(Integer unionId) throws Exception;

    /**
     * 获取主键集对应的对象集
     *
     * @param idList 主键集
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listByIdList(List<Integer> idList) throws Exception;

    /**
     * 分页支持
     *
     * @param page          分页对象
     * @param entityWrapper 条件
     * @return Page
     * @throws Exception 统一处理异常
     */
    Page pageSupport(Page page, EntityWrapper<UnionCardProject> entityWrapper) throws Exception;

    //****************************************** Object As a Service - save ********************************************

    /**
     * 保存(by myBatisGenerator)
     *
     * @param newUnionCardProject 保存内容
     * @throws Exception 统一处理异常
     */
    void save(UnionCardProject newUnionCardProject) throws Exception;

    /**
     * 批量保存(by myBatisGenerator)
     *
     * @param newUnionCardProjectList 保存内容
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionCardProject> newUnionCardProjectList) throws Exception;

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
     * @param updateUnionCardProject 更新内容
     * @throws Exception 统一处理异常
     */
    void update(UnionCardProject updateUnionCardProject) throws Exception;

    /**
     * 批量更新(by myBatisGenerator)
     *
     * @param updateUnionCardProjectList 更新内容
     * @throws Exception 统一处理异常
     */
    void updateBatch(List<UnionCardProject> updateUnionCardProjectList) throws Exception;

}