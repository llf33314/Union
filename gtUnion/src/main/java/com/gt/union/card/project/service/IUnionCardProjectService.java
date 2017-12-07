package com.gt.union.card.project.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.vo.CardProjectJoinMemberVO;

import java.util.List;

/**
 * 活动项目 服务接口
 *
 * @author linweicong
 * @version 2017-11-24 16:48:44
 */
public interface IUnionCardProjectService extends IService<UnionCardProject> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 根据活动id、盟员id和联盟id，获取项目信息
     *
     * @param activityId 活动id
     * @param memberId   盟员id
     * @param unionId    联盟id
     * @return UnionCardProject
     * @throws Exception 统一处理异常
     */
    UnionCardProject getByActivityIdAndMemberIdAndUnionId(Integer activityId, Integer memberId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 根据盟员id、联盟id和项目状态，获取项目信息
     *
     * @param memberId 盟员id
     * @param unionId  联盟id
     * @param status   项目状态
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listByMemberIdAndUnionIdAndStatus(Integer memberId, Integer unionId, Integer status) throws Exception;

    /**
     * 根据活动id、联盟id和项目状态，获取项目信息
     *
     * @param activityId 活动id
     * @param unionId    联盟id
     * @param status     项目状态
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listByActivityIdAndUnionIdAndStatus(Integer activityId, Integer unionId, Integer status) throws Exception;

    /**
     * 列表：联盟卡设置-活动卡设置-参与盟员数
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return List<CardProjectJoinMemberVO>
     * @throws Exception 统一处理异常
     */
    List<CardProjectJoinMemberVO> listJoinMemberByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    /**
     * 根据活动id、联盟id和项目状态，统计项目个数
     *
     * @param activityId 活动id
     * @param unionId    联盟id
     * @param status     项目状态
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    Integer countByActivityIdAndUnionIdAndStatus(Integer activityId, Integer unionId, Integer status) throws Exception;

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

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

}