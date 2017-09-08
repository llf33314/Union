package com.gt.union.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.main.entity.UnionMainCreate;

/**
 * <p>
 * 创建联盟 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionMainCreateService extends IService<UnionMainCreate> {
    /**
     * 根据商家id判断是否创建过联盟
     * @param busId {not null} 商家id
     * @return
     * @throws Exception
     */
	boolean hasCreateUnionMain(Integer busId) throws Exception;
}
