package com.gt.union.service.brokerage;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.common.exception.ParameterException;
import com.gt.union.entity.brokerage.UnionVerifyMember;

/**
 * <p>
 * 联盟佣金平台管理人员 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
public interface IUnionVerifyMemberService extends IService<UnionVerifyMember> {

	/**
	 * 删除佣金平台管理员
	 * @param id
	 */
	void delUnionVerifyMember(Integer id);

	/**
	 * 保存佣金平台管理员
	 * @param unionVerifyMember
	 */
	void saveUnionVerifyMember(UnionVerifyMember unionVerifyMember) throws Exception;
}
