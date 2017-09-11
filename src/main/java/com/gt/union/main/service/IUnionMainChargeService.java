package com.gt.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.main.entity.UnionMainCharge;

/**
 * <p>
 * 联盟升级收费 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionMainChargeService extends IService<UnionMainCharge> {

    /**
     * 根据联盟id和联盟卡类型获取联盟卡信息
     *
     * @param unionId {not null} 联盟卡
     * @param type    {not null} 联盟卡类型
     * @return
     */
    UnionMainCharge getByUnionIdAndType(Integer unionId, int type);

    /**
     * 根据联盟id、红黑卡类型和是否启用，获取联盟升级收费信息
     *
     * @param unionId     {not null} 联盟id
     * @param type        {not null} 红黑卡类型
     * @param isAvailable {not null} 是否启用
     * @return
     * @throws Exception
     */
    UnionMainCharge getByUnionIdAndTypeAndIsAvailable(Integer unionId, Integer type, Integer isAvailable) throws Exception;
}
