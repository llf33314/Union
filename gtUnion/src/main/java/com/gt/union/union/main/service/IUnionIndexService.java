package com.gt.union.union.main.service;

import com.gt.union.union.main.vo.IndexVO;

/**
 * 首页 服务接口
 *
 * @author linweicong
 * @version 2017-12-04 08:37:37
 */
public interface IUnionIndexService {
    /**
     * 我的联盟-首页
     *
     * @param busId      商家id
     * @param optUnionId 联盟id
     * @return IndexVO
     * @throws Exception 统一处理异常
     */
    IndexVO getIndexVOByBusId(Integer busId, Integer optUnionId) throws Exception;
}
