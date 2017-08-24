package com.gt.union.service.business;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.business.UnionBusinessRecommend;
import com.gt.union.entity.business.vo.UnionBusinessRecommendVO;

import java.util.List;
import java.util.Map;

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
	 * @param busId
	 * @param id
	 * @param isAcceptance
     * @param acceptancePrice
	 * @throws Exception
	 */
	void updateByIdAndIsAcceptance(Integer busId, Integer id, Integer isAcceptance, Double acceptancePrice) throws Exception;

	/**
	 * 添加商机推荐
	 * @param vo
	 */
	void save(UnionBusinessRecommendVO vo) throws Exception;

	/**
	 * 根据busId查询我的商机信息，并支持根据所属联盟id和受理状态isAcceptance进行过滤，且对顾客姓名userName、电话userPhone进行模糊匹配
	 * @param page
	 * @param busId
	 * @param unionId
	 * @param isAcceptance
	 * @return
	 * @throws Exception
	 */
	Page listByToBusId(Page page, Integer busId, Integer unionId, String isAcceptance, String userName, String userPhone) throws Exception;

	/**
	 * 根据busId查询我推荐的商机信息，并支持根据所属联盟id和受理状态isAcceptance进行过滤，且对顾客姓名userName、电话userPhone进行模糊匹配
	 * @param page
	 * @param busId
	 * @param unionId
	 * @param isAcceptance
	 * @return
	 * @throws Exception
	 */
	Page listByFromBusId(Page page, Integer busId, Integer unionId, String isAcceptance, String userName, String userPhone) throws Exception;

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
	Page listBrokerageByFromBusId(Page page, Integer fromBusId, Integer toBusId, Integer unionId, String isConfirm, String userName, String userPhone) throws Exception;

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

    Page listBrokerageByToBusId(Page page, Integer toBusId, Integer fromBusId, Integer unionId, String isConfirm, String userName, String userPhone) throws Exception;

    /**
     * 根据fromBusId查询我的支付明细，并可根据unionId进行过滤
     * @param page
     * @param fromBusId
     * @param unionId
     * @return
     * @throws Exception
     */
    Page listPayDetailByFromBusId(Page page, Integer fromBusId, Integer unionId) throws Exception;

    /**
     * 查询我的支付详情
     * @param unionId
     * @param fromBusId
     * @param toBusId
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> listPayDetailParticularByUnionIdAndFromBusIdAndToBusId(Integer unionId, Integer fromBusId, Integer toBusId) throws Exception;

    /**
     * 获取商机统计数据
     * @param unionId
     * @param busId
     * @return
     * @throws Exception
     */
    Map<String, Object> getStatisticData(Integer unionId, Integer busId) throws Exception;

	/**
	 * 推荐商机发送短信
	 * @param redisKey
	 * @return
	 */
	Map<String,Object> getRecommendMsgInfo(String redisKey) throws Exception;
}
