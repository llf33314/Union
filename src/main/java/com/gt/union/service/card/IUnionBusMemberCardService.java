package com.gt.union.service.card;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.card.UnionBusMemberCard;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟商家升级会员联盟卡 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
public interface IUnionBusMemberCardService extends IService<UnionBusMemberCard> {

	/**
	 * 根据联盟id获取联盟积分
	 * @param unionId	联盟id
	 * @return
	 */
	public double getUnionMemberIntegral(Integer unionId);

	/**
	 * 查询盟员的联盟卡列表
	 * @param page	分页
	 * @param unionId	联盟id
	 * @param busId		商家id
	 * @param cardNo	模糊查询的联盟卡号
	 * @param phone		手机号
	 * @return
	 * @throws Exception
	 */
	Page selectUnionBusMemberCardList(Page page, Integer unionId, Integer busId, String cardNo, String phone) throws Exception;

	/**
	 * 查询盟员的联盟卡列表   不分页 导出数据
	 * @param unionId	联盟id
	 * @param busId		商家id
	 * @param cardNo	模糊查询的联盟卡号
	 * @param phone		手机号
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> selectUnionBusMemberCardList(Integer unionId, Integer busId, String cardNo, String phone) throws Exception;
}
