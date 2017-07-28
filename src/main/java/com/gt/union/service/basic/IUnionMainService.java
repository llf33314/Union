package com.gt.union.service.basic;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.entity.basic.UnionMain;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟主表 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
public interface IUnionMainService extends IService<UnionMain> {

	/**
	 * 根据联盟和商家id获取商家的联盟信息
	 * @param busId 	主账号商家id
	 * @param unionId	联盟id
	 * @return
	 */
	Map<String,Object> getUnionMainMemberInfo(Integer busId, Integer unionId);

	/**
	 * 根据商家id获取商家加入的联盟列表
	 * @param busId 主账号商家id
	 * @return
	 */
	List<UnionMain> getMemberUnionList(Integer busId);

	/**
	 * 根据联盟和商家id获取商家的联盟信息
	 * @param main
	 * @param busId
	 * @return
	 */
	Map<String,Object> getUnionMainMemberInfo(UnionMain main,Integer busId);
}
