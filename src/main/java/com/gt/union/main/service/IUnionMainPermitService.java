package com.gt.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.main.entity.UnionMainPermit;

/**
 * <p>
 * 联盟许可，盟主服务 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionMainPermitService extends IService<UnionMainPermit> {
    /**
     * 根据商家id判断是否拥有盟主服务许可
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    boolean hasUnionMainPermit(Integer busId) throws Exception;

    /**
     * 根据商家id获取联盟服务许可
     *
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
    UnionMainPermit getByBusId(Integer busId) throws Exception;
}
