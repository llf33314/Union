package com.gt.union.service.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionMemberPreferentialManager;

import java.util.Map;

/**
 * <p>
 * 盟员优惠项目管理 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
public interface IUnionMemberPreferentialManagerService extends IService<UnionMemberPreferentialManager> {
    /**
     * 根据联盟id获取优惠项目审核列表，并根据优惠服务项审核状态verifyStatus进行分类查询
     * @param page
     * @param unionId
     * @param verifyStatus
     * @return
     * @throws Exception
     */
	Page listByUnionIdAndVerifyStatus(Page page, Integer unionId, Integer verifyStatus) throws Exception;

    /**
     * 根据联盟id和盟员id查询该盟员的优惠项目
     * @param page
     * @param unionId	联盟id
     * @param busId		商家id
     * @return
     * @throws Exception
     */
	Page listMyByUnionId(Page page, Integer unionId, Integer busId) throws Exception;

    /**
     * 根据联盟id和优惠服务项审核状态verifyStatus查询数量
     * @param unionId
     * @param verifyStatus
     * @return
     * @throws Exception
     */
	int countPreferentialManager(Integer unionId, Integer verifyStatus) throws Exception;

    /**
     * 根据managerId和verifyStatus获取对应详情信息，page封装优惠服务项列表信息
     * @param page
     * @param id
     * @param verifyStatus
     * @return
     * @throws Exception
     */
	Map<String, Object> getByIdAndVerifyStatus(Page page, Integer id, Integer verifyStatus) throws Exception;
}
