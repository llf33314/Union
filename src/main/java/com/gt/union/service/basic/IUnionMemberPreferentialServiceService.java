package com.gt.union.service.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionMemberPreferentialService;

import java.util.List;
import java.util.Map;

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
     * @param managerId
     * @param verifyStatus
     * @return
     * @throws Exception
     */
	List<Map<String, Object>> listPreferentialServiceByManagerId(Integer managerId, Integer verifyStatus) throws Exception;

	/**
	 * 查询我的优惠服务
	 * @param page
	 * @param unionId	联盟id
	 * @param memberId	盟员id
	 * @return
	 */
	Page listMyByUnionId(Page page, Integer unionId, Integer memberId);

	/**
	 * 保存优惠服务项目
	 * @param unionId
	 * @param busId	商家id
	 * @param serviceName
	 */
	void save(Integer unionId, Integer busId, String serviceName) throws Exception;

	/**
	 * 删除优惠服务项目
	 * @param unionId    联盟id
	 * @param ids    优惠服务项目ids
	 */
	void delete(Integer unionId, String ids) throws Exception;

	/**
	 *	提交优惠服务项目审核
	 * @param unionId	联盟id
	 * @param id	优惠服务项目id
	 */
	void addServiceVerify(Integer unionId, Integer id) throws Exception;

	/**
	 * 审核优惠服务项目
	 * @param unionId	联盟id
	 * @param busId		商家id
	 * @param ids		服务项目ids
	 * @param verifyStatus	审核状态 2：通过 3：不通过
	 */
	void verify(Integer unionId, Integer busId, String ids, Integer verifyStatus) throws Exception;
}
