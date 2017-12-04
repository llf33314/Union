package com.gt.union.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.union.main.entity.UnionMainNotice;

/**
 * 联盟公告 服务接口
 *
 * @author linweicong
 * @version 2017-11-23 15:18:52
 */
public interface IUnionMainNoticeService extends IService<UnionMainNotice> {
    //***************************************** Domain Driven Design - get *********************************************

    /**
     * 根据商家id和联盟id，获取联盟公告信息
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @return UnionMainNotice
     * @throws Exception 统一处理异常
     */
    UnionMainNotice getByBusIdAndUnionId(Integer busId, Integer unionId) throws Exception;

    //***************************************** Domain Driven Design - list ********************************************

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    /**
     * 更新联盟公告
     *
     * @param busId   商家id
     * @param unionId 联盟id
     * @param content 公告内容
     * @throws Exception 统一处理异常
     */
    void updateContentByBusIdAndUnionId(Integer busId, Integer unionId, String content) throws Exception;

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Domain Driven Design - filter ******************************************

}