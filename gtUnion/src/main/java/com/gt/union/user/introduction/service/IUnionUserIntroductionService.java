package com.gt.union.user.introduction.service;

import com.gt.union.user.introduction.entity.UnionUserIntroduction;

/**
 * 联盟商家简介 服务接口
 *
 * @author linweicong
 * @version 2018-01-24 16:24:13
 */
public interface IUnionUserIntroductionService {

    /**
     * 查询有效商家简介
     *
     * @param busId
     * @return
     * @throws Exception 统一处理异常
     */
    UnionUserIntroduction getValidByBusId(Integer busId) throws Exception;

    /**
     * 保存商家简介信息
     *
     * @param busId                 商家id
     * @param unionUserIntroduction 简介信息
     * @throws Exception 统一处理异常
     */
    void saveOrUpdate(Integer busId, UnionUserIntroduction unionUserIntroduction) throws Exception;

}
