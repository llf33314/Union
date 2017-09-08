package com.gt.union.index.service;

import java.util.Map;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
public interface IIndexService {
    /**
     * 商家联盟首页
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    Map<String, Object> index(Integer busId) throws Exception;
}
