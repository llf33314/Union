package com.gt.union.service.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionMemberPreferentialService;

/**
 * <p>
 * 盟员优惠服务项 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
public interface IUnionMemberPreferentialServiceService extends IService<UnionMemberPreferentialService> {
    /**
     * 通过managerId和verifyStatus查询对应的优惠服务项信息
     * @param page
     * @param managerId
     * @param verifyStatus
     * @return
     * @throws Exception
     */
	Page pagePreferentialServiceByManagerId(Page page, Integer managerId, Integer verifyStatus) throws Exception;
}
