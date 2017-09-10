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
     * @param unionId   联盟卡
     * @param redCardType   联盟卡类型
     * @return
     */
    UnionMainCharge getByUnionIdAndType(Integer unionId, int redCardType);
}
