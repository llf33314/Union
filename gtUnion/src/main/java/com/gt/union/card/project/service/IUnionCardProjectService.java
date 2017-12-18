package com.gt.union.card.project.service;

import com.baomidou.mybatisplus.service.IService;
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
public interface IUnionCardProjectService extends IService<UnionCardProject> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 获取项目信息
     *
     * @param unionId    联盟id
     * @param memberId   盟员id
     * @param activityId 活动id
     * @return UnionCardProject
     * @throws Exception 统一处理异常
     */
    UnionCardProject getByUnionIdAndMemberIdAndActivityId(Integer unionId, Integer memberId, Integer activityId) throws Exception;

    /**
     * 获取项目信息
     *
     * @param projectId  项目id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return UnionCardProject
     * @throws Exception 统一处理异常
     */
    UnionCardProject getByIdAndUnionIdAndActivityId(Integer projectId, Integer unionId, Integer activityId) throws Exception;

    /**
     * 联盟卡设置-活动卡设置-分页-我的活动项目-ERP和非ERP
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return CardProjectVO
     * @throws Exception 统一处理异常
     */
    CardProjectVO getProjectVOByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 获取项目信息
     *
     * @param unionId  联盟id
     * @param memberId 盟员id
     * @param status   项目状态
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listByUnionIdAndMemberIdAndStatus(Integer unionId, Integer memberId, Integer status) throws Exception;

    /**
     * 获取项目列表信息
     *
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listByUnionIdAndActivityId(Integer unionId, Integer activityId) throws Exception;

    /**
     * 获取项目列表信息
     *
     * @param unionId    联盟id
     * @param activityId 活动id
     * @param status     项目状态
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProject> listByUnionIdAndActivityIdAndStatus(Integer unionId, Integer activityId, Integer status) throws Exception;

    /**
     * 联盟卡设置-活动卡设置-分页-参与盟员数
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return List<CardProjectJoinMemberVO>
     * @throws Exception 统一处理异常
     */
    List<CardProjectJoinMemberVO> listJoinMemberByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception;

    /**
     * 联盟卡设置-活动卡设置-分页-审核项目
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @return List<CardProjectCheckVO>
     * @throws Exception 统一处理异常
     */
    List<CardProjectCheckVO> listProjectCheckByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 保存项目
     *
     * @param newUnionCardProject 项目
     * @throws Exception 统一处理异常
     */
    void save(UnionCardProject newUnionCardProject) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    /**
     * 批量删除
     *
     * @param idList id列表
     * @throws Exception 统一处理异常
     */
    void removeBatchById(List<Integer> idList) throws Exception;

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 联盟卡设置-活动卡设置-分页-审核项目-通过或不通过
     *
     * @param busId      商家id
     * @param unionId    联盟id
     * @param activityId 活动id
     * @param vo         表单内容
     * @param isPass     是否通过(0：否 1：是)
     * @throws Exception 统一处理异常
     */
    void updateProjectCheckByBusIdAndUnionIdAndActivityId(Integer busId, Integer unionId, Integer activityId, Integer isPass, CardProjectCheckUpdateVO vo) throws Exception;

    /**
     * 更新项目
     *
     * @param updateUnionCardProject 项目
     * @throws Exception 统一处理异常
     */
    void update(UnionCardProject updateUnionCardProject) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    /**
     * 统计项目个数
     *
     * @param activityId 活动id
     * @param unionId    联盟id
     * @param status     项目状态
     * @return List<UnionCardProject>
     * @throws Exception 统一处理异常
     */
    Integer countByUnionIdAndActivityIdAndStatus(Integer unionId, Integer activityId, Integer status) throws Exception;

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