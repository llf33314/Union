package com.gt.union.main.service;

import java.util.Map;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
public interface IIndexService {
    //-------------------------------------------------- get ----------------------------------------------------------

    /**
     * 商家联盟首页-默认未选定盟员身份
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    Map<String, Object> index(Integer busId) throws Exception;

    /**
     * 商家联盟首页-选定盟员身份
     *
     * @param memberId {not null} 盟员id
     * @param busId    {not null} 商家id
     * @return
     * @throws Exception
     */
    Map<String, Object> indexByMemberId(Integer memberId, Integer busId) throws Exception;

    //------------------------------------------ list(include page) ---------------------------------------------------
    //------------------------------------------------- update --------------------------------------------------------
    //------------------------------------------------- save ----------------------------------------------------------
    //------------------------------------------------- count ---------------------------------------------------------
    //------------------------------------------------ boolean --------------------------------------------------------

}
