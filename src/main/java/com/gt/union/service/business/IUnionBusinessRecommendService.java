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
	 * 根据busId查询我的商机信息，并支持根据所属联盟id和受理状态isAcceptance进行过滤，且对顾客姓名userName、电话userPhone进行模糊匹配
	 * @param page
	 * @param busId
	 * @param unionId
	 * @param isAcceptance
	 * @return
	 * @throws Exception
	 */
	Page listUnionBusinessRecommendToMe(Page page, Integer busId, Integer unionId, String isAcceptance, String userName, String userPhone) throws Exception;

	/**
	 * 根据busId查询我推荐的商机信息，并支持根据所属联盟id和受理状态isAcceptance进行过滤，且对顾客姓名userName、电话userPhone进行模糊匹配
	 * @param page
	 * @param busId
	 * @param unionId
	 * @param isAcceptance
	 * @return
	 * @throws Exception
	 */
	Page listUnionBusinessRecommendFromMe(Page page, Integer busId, Integer unionId, String isAcceptance, String userName, String userPhone) throws Exception;

    /**
     * 根据fromBusId查询我的佣金收入，并支持根据消费去向toBusId和所属联盟id、结算状态isConfirm进行过滤，且对顾客姓名userName、电话userPhone进行模糊匹配
     * @param page
     * @param fromBusId
     * @param toBusId
     * @param unionId
     * @param isConfirm
     * @return
     * @throws Exception
     */
	Page listBrokerageToMe(Page page, Integer fromBusId, Integer toBusId, Integer unionId, String isConfirm, String userName, String userPhone) throws Exception;

    /**
     * 根据toBusId查询我需要支付佣金，并支持根据消费来源fromBusId和所属联盟id、结算状态isConfirm进行过滤，且对顾客姓名userName、电话userPhone进行模糊匹配
     * @param page
     * @param toBusId
     * @param fromBusId
     * @param unionId
     * @param isConfirm
     * @return
     * @throws Exception
     */
	Page listBrokerageFromMe(Page page, Integer toBusId, Integer fromBusId, Integer unionId, String isConfirm, String userName, String userPhone) throws Exception;
}
