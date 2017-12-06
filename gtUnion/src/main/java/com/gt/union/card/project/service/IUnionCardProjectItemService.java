package com.gt.union.card.project.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.card.project.entity.UnionCardProjectItem;

import java.util.List;

/**
 * 项目优惠 服务接口
 *
 * @author linweicong
 * @version 2017-11-27 09:55:47
 */
public interface IUnionCardProjectItemService extends IService<UnionCardProjectItem> {
    //***************************************** Domain Driven Design - get *********************************************

    //***************************************** Domain Driven Design - list ********************************************

    /**
     * 根据项目id，获取名称来自接口的优惠信息
     *
     * @param projectId 项目id
     * @return List<UnionCardProjectItem>
     * @throws Exception 统一处理异常
     */
    List<UnionCardProjectItem> listItemByProjectId(Integer projectId) throws Exception;

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

}