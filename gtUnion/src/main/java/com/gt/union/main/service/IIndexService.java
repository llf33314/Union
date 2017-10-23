package com.gt.union.main.service;

import java.util.Map;

/**
 * 首页 服务接口
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
public interface IIndexService {
    //------------------------------------------ Domain Driven Design - get ------------------------------------------

    /**
     * 商家联盟首页-默认未选定盟员身份
     *
     * @param busId {not null} 商家id
     * @return Map <String, Object>
     * @throws Exception 全局处理异常
     */
    Map<String, Object> index(Integer busId) throws Exception;

    /**
     * 商家联盟首页-选定盟员身份
     *
     * @param memberId {not null} 盟员id
     * @param busId    {not null} 商家id
     * @return Map <String, Object>
     * @throws Exception 全局处理异常
     */
    Map<String, Object> indexByMemberId(Integer memberId, Integer busId) throws Exception;
}