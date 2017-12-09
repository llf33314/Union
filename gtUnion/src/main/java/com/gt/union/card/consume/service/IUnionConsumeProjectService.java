package com.gt.union.card.consume.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.consume.entity.UnionConsumeProject;

import java.util.List;

/**
 * 消费核销项目优惠 服务接口
 *
 * @author linweicong
 * @version 2017-11-27 10:27:29
 */
public interface IUnionConsumeProjectService extends IService<UnionConsumeProject> {
    //***************************************** Domain Driven Design - get *********************************************

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 根据消费核销id，获取消费核销优惠项目信息
     *
     * @param consumeId 消费核销id
     * @return List<UnionConsumeProject>
     * @throws Exception 统一处理异常
     */
    List<UnionConsumeProject> listByConsumeId(Integer consumeId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    /**
     * 批量保存
     *
     * @param newUnionConsumeProjectList 数据
     * @throws Exception 统一处理异常
     */
    void saveBatch(List<UnionConsumeProject> newUnionConsumeProjectList) throws Exception;

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    /**
     * 根据项目id和项目优惠id，统计个数
     *
     * @param projectId     项目id
     * @param projectItemId 项目优惠id
     * @return Integer
     * @throws Exception 统一处理异常
     */
    Integer countByProjectIdAndProjectItemId(Integer projectId, Integer projectItemId) throws Exception;

    //***************************************** Domain Driven Design - boolean *****************************************

}