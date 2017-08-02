package com.gt.union.service.business;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.business.UnionBusinessRecommend;
import com.gt.union.vo.business.UnionBusinessRecommendFormVO;
import com.gt.union.vo.business.UnionBusinessRecommendVO;

/**
 * <p>
 * 联盟商家商机推荐 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
public interface IUnionBusinessRecommendService extends IService<UnionBusinessRecommend> {

	/**
	 * 获取联盟商机推荐列表
	 * @param page
	 * @param vo
	 * @return
	 */
	Page selectUnionBusinessRecommendList(Page page, UnionBusinessRecommendVO vo) throws Exception;

	/**
	 * 审核商机推荐
	 * @param recommend
	 */
	void updateVerifyRecommend(UnionBusinessRecommend recommend) throws Exception;

	/**
	 * 添加商机推荐
	 * @param vo
	 */
	void saveUnionBusinessRecommend(UnionBusinessRecommendFormVO vo) throws Exception;

	/**
	 * 根据busId查询我的商机信息，并支持根据所属联盟id和受理状态isAcceptance进行过滤
	 * @param page
	 * @param busId
	 * @param unionId
	 * @param isAcceptance
	 * @return
	 * @throws Exception
	 */
	Page listUnionBusinessRecommendToMe(Page page, Integer busId, Integer unionId, String isAcceptance) throws Exception;

	/**
	 * 根据busId查询我推荐的商机信息，并支持根据所属联盟id和受理状态isAcceptance进行过滤
	 * @param page
	 * @param busId
	 * @param unionId
	 * @param isAcceptance
	 * @return
	 * @throws Exception
	 */
	Page listUnionBusinessRecommendFromMe(Page page, Integer busId, Integer unionId, String isAcceptance) throws Exception;
}
