package com.gt.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.main.entity.UnionMainNotice;

/**
 * <p>
 * 联盟公告 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionMainNoticeService extends IService<UnionMainNotice> {
    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 根据联盟id，获取联盟公告
     *
     * @param unionId {not null} 联盟id
     * @return
     */
    UnionMainNotice getByUnionId(Integer unionId);

    //------------------------------------------ list(include page) ---------------------------------------------------
    //------------------------------------------------- update --------------------------------------------------------

    /**
     * 根据商家id、盟员身份id和联盟公告内容，更新保存联盟公告信息
     *
     * @param busId {not null} 商家id
     * @param memberId   {not null} 盟员身份id
     * @param content {not null} 联盟公告内容
     * @return UnionMainNotice
     */
    void updateOrSaveByBusIdAndMemberId(Integer busId, Integer memberId, String content) throws Exception;

    //------------------------------------------------- save ----------------------------------------------------------
    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------
}
