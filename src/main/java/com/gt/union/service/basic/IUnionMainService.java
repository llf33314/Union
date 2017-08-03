package com.gt.union.service.basic;

import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.common.BusUser;
import com.gt.union.vo.basic.UnionMainInfoVO;

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
	 * 获取联盟信息
	 * @param unionId
	 * @return
	 */
	UnionMain getUnionMain(Integer unionId);

	/**
	 * 判断联盟是否有效
	 * @param main
	 * @return
	 */
	void isUnionValid(UnionMain main);

	/**
	 * 判断联盟是否有效
	 * @param unionId
	 * @return
	 */
	void isUnionValid(Integer unionId);

	/**
	 * 获取我的联盟信息
	 * @param busId	商家id
	 * @return
	 */
	Map<String,Object> getUnionMainMemberInfo(Integer busId) throws Exception;



	/**
	 * 根据联盟id获取联盟信息
	 * @param busId	商家id
	 * @param unionId	联盟id
	 * @return
	 */
	Map<String,Object> getUnionMainMemberInfo(Integer busId, Integer unionId) throws Exception;

	/**
	 * 根据商家id获取商家加入的联盟列表
	 * @param busId 主账号商家id
	 * @return
	 */
	List<UnionMain> getMemberUnionList(Integer busId);

	/**
	 * 更新联盟
	 * @param unionMainInfoVO
	 * @param busId
	 */
	void updateUnionMain(UnionMainInfoVO unionMainInfoVO, Integer busId) throws Exception;

	/**
	 * 创建联盟判断信息
	 * @param user
	 * @return
	 */
	Map<String, Object> getCreateUnionInfo(BusUser user) throws Exception;

	/**
	 * 保存创建联盟的信息
	 * @param unionMainInfo
	 * @param user
	 */
	void saveUnionMainInfo(UnionMainInfoVO unionMainInfo, BusUser user);

	/**
	 * 根据id获取编辑联盟的信息
	 * @param id
	 * @return
	 */
	Map<String, Object> getUnionMainInfo(Integer id);

	/**
	 * 查询联盟列表
	 * @return
	 */
	List<UnionMain> getUnionMainList();
}
