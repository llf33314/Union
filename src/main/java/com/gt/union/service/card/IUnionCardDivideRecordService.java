package com.gt.union.service.card;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.card.UnionCardDivideRecord;

/**
 * <p>
 * 商家售卡分成记录 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
public interface IUnionCardDivideRecordService extends IService<UnionCardDivideRecord> {
    /**
     * 根据商家id，分页获取售卡佣金信息
     * @param page
     * @param busId
     * @return
     * @throws Exception
     */
	Page pageMapByBusId(Page page, Integer busId) throws Exception;

    /**
     * 根据联盟id和商家id，分页获取售卡佣金信息
     * @param page
     * @param unionId
     * @param busId
     * @return
     * @throws Exception
     */
    Page pageMapByUnionIdAndBusId(Page page, Integer unionId, Integer busId) throws Exception;

	/**
	 * 根据联盟id和商家id，统计分成金额
	 * @param unionId
     * @param busId
     * @return
	 * @throws Exception
	 */
	Double sumPriceByUnionIdAndBusId(Integer unionId, Integer busId) throws Exception;

    /**
     * 根据商家id统计分成金额
     * @param busId
     * @return
     * @throws Exception
     */
	Double sumPriceByBusId(Integer busId) throws Exception;
}
