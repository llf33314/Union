package com.gt.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.main.entity.UnionMainNotice;

import java.util.List;

/**
 * 联盟公告 服务接口
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
public interface IUnionMainNoticeService extends IService<UnionMainNotice> {
    //------------------------------------------ Domain Driven Design - get --------------------------------------------

    //------------------------------------------ Domain Driven Design - list -------------------------------------------

    //------------------------------------------ Domain Driven Design - save -------------------------------------------

    //------------------------------------------ Domain Driven Design - remove -----------------------------------------

    //------------------------------------------ Domain Driven Design - update -----------------------------------------

    /**
     * 根据商家id、盟员身份id和联盟公告内容，更新保存联盟公告信息
     *
     * @param busId    {not null} 商家id
     * @param memberId {not null} 盟员身份id
     * @param content  {not null} 联盟公告内容
     * @return UnionMainNotice
     * @throws Exception 全局处理异常
     */
    void updateOrSaveByBusIdAndMemberId(Integer busId, Integer memberId, String content) throws Exception;

    //------------------------------------------ Domain Driven Design - count ------------------------------------------

    //------------------------------------------ Domain Driven Design - boolean ----------------------------------------

    //******************************************* Object As a Service - get ********************************************

    /**
     * 根据id查询对象
     *
     * @param noticeId 对象id
     * @return UnionMainCharge
     * @throws Exception 全局处理异常
     */
    UnionMainNotice getById(Integer noticeId) throws Exception;

    //******************************************* Object As a Service - list *******************************************

    /**
     * 根据联盟id查询对象列表
     *
     * @param unionId 联盟id
     * @return List<UnionMainNotice>
     * @throws Exception 全局处理异常
     */
    List<UnionMainNotice> listByUnionId(Integer unionId) throws Exception;

    //******************************************* Object As a Service - save *******************************************

    /**
     * 新增对象
     *
     * @param newNotice 新增的对象
     * @throws Exception 全局处理异常
     */
    void save(UnionMainNotice newNotice) throws Exception;

    /**
     * 批量新增对象
     *
     * @param newNoticeList 批量新增的对象列表
     * @throws Exception 全局处理异常
     */
    void saveBatch(List<UnionMainNotice> newNoticeList) throws Exception;

    //******************************************* Object As a Service - remove *****************************************

    /**
     * 根据id删除对象
     *
     * @param noticeId 对象id
     * @throws Exception 全局处理异常
     */
    void removeById(Integer noticeId) throws Exception;

    /**
     * 根据id列表批量删除对象
     *
     * @param noticeIdList 对象id列表
     * @throws Exception 全局处理异常
     */
    void removeBatchById(List<Integer> noticeIdList) throws Exception;

    //******************************************* Object As a Service - update *****************************************

    /**
     * 修改对象
     *
     * @param updateNotice 修改的对象
     * @throws Exception 全局处理异常
     */
    void update(UnionMainNotice updateNotice) throws Exception;

    /**
     * 批量修改对象
     *
     * @param updateNoticeList 批量修改的对象列表
     * @throws Exception 全局处理异常
     */
    void updateBatch(List<UnionMainNotice> updateNoticeList) throws Exception;
}
