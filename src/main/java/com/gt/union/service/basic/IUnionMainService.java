package com.gt.union.service.basic;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.common.BusUser;
import com.gt.union.vo.basic.UnionMainInfoVo;

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

	/**
	 * 更新联盟
	 * @param main
	 * @param busId
	 */
	void updateUnionMain(UnionMain main, Integer busId) throws Exception;

	/**
	 * 创建联盟判断信息
	 * @param user
	 * @return
	 */
	String getCreateUnionInfo(BusUser user) throws Exception;

	/**
	 * 保存创建联盟
	 * @param vo
	 */
	void saveCreateUnion(UnionMainInfoVo vo);
}
