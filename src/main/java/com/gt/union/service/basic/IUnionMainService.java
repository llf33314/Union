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
	int isUnionValid(UnionMain main);

	/**
	 * 获取我的联盟信息
	 * @param busId	商家id
	 * @return
	 */
	Map<String,Object> getUnionMainMemberInfo(Integer busId) throws Exception;



	/**
	 * 根据联盟id获取联盟信息
	 * @param busId	商家id
	 * @param id	联盟id
	 * @return
	 */
	Map<String,Object> getUnionMainMemberInfo(Integer busId, Integer id) throws Exception;

	/**
	 * 根据商家id获取商家加入的联盟列表
	 * @param busId 主账号商家id
	 * @return
	 */
	List<UnionMain> getMemberUnionList(Integer busId);

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
	void saveCreateUnion(UnionMainInfoVO vo);


}
