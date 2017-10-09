package com.gt.union.verifier.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.verifier.entity.UnionVerifier;

/**
 * <p>
 * 联盟平台管理人员 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
public interface IUnionVerifierService extends IService<UnionVerifier> {
    /**
     * 删除佣金平台管理员
     * @param id
     */
    void deleteById(Integer id) throws Exception;

    /**
     * 保存佣金平台管理员
     * @param unionVerifier
     */
    void save(UnionVerifier unionVerifier) throws Exception;

    /**
     * 查询佣金平台管理员
     * @param page
     * @param busId
     * @return
     */
    Page list(Page<UnionVerifier> page, Integer busId) throws Exception;

    /**
     * 根据手机号查询
     * @param phone
     * @return
     */
	UnionVerifier getByPhone(String phone);

	/**
	 * 根据商家id和名称查询佣金平台管理员个数
	 * @param busId
	 * @param name
	 * @return
	 */
	int selectCountByBusIdAndName(Integer busId, String name);
}
