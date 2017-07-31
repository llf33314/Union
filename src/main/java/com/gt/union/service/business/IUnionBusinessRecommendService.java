package com.gt.union.service.business;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.business.UnionBusinessRecommend;
import com.gt.union.vo.business.UnionBusinessRecommendFormVo;
import com.gt.union.vo.business.UnionBusinessRecommendVo;

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
	Page selectUnionBusinessRecommendList(Page page, UnionBusinessRecommendVo vo) throws Exception;

	/**
	 * 审核商机推荐
	 * @param recommend
	 */
	void updateVerifyRecommend(UnionBusinessRecommend recommend) throws Exception;

	/**
	 * 添加商机推荐
	 * @param vo
	 */
	void saveUnionBusinessRecommend(UnionBusinessRecommendFormVo vo) throws Exception;
}
