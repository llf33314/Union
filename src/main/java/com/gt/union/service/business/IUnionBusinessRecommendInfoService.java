package com.gt.union.service.business;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.business.UnionBusinessRecommendInfo;

/**
 * <p>
 * 联盟商家推荐关联信息 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
public interface IUnionBusinessRecommendInfoService extends IService<UnionBusinessRecommendInfo> {
    /**
     * 根据推荐商机id获取该推荐商机的相关信息
     * @param recommendId
     * @return
     * @throws Exception
     */
	UnionBusinessRecommendInfo getByRecommendId(Integer recommendId) throws Exception;
}
