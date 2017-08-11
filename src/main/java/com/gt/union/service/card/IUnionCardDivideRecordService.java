package com.gt.union.service.card;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.card.UnionCardDivideRecord;
import com.gt.union.vo.card.UnionCardDivideRecordVO;

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
	 * 查询收卡分成记录列表
	 * @param page
	 * @param vo
	 * @return
	 */
	Page getUnionCardDivideRecordList(Page page, UnionCardDivideRecordVO vo) throws Exception;

	/**
	 * 查询分成记录总和
	 * @param busId	商家id
	 * @param unionId	联盟id 可以为空
	 * @return
	 */
	public double getUnionCardDivideRecordSum(Integer busId, Integer unionId);
}
