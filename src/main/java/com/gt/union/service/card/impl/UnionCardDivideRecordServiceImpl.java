package com.gt.union.service.card.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.entity.card.UnionCardDivideRecord;
import com.gt.union.entity.card.vo.UnionCardDivideRecordVO;
import com.gt.union.mapper.card.UnionCardDivideRecordMapper;
import com.gt.union.service.card.IUnionCardDivideRecordService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 商家售卡分成记录 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@Service
public class UnionCardDivideRecordServiceImpl extends ServiceImpl<UnionCardDivideRecordMapper, UnionCardDivideRecord> implements IUnionCardDivideRecordService {

	@Override
	public Page getUnionCardDivideRecordList(Page page, UnionCardDivideRecordVO vo) {
		return null;
	}

	@Override
	public double getUnionCardDivideRecordSum(final Integer busId, final Integer unionId) {
		Wrapper wrapper = new Wrapper() {
			@Override
			public String getSqlSegment() {
				StringBuilder sbSqlSegment = new StringBuilder();
				sbSqlSegment.append(" WHERE")
						.append(" bus_id = ").append(busId);
				if(CommonUtil.isNotEmpty(unionId)){
					sbSqlSegment.append(" AND union_id = ").append(unionId);
				}
				return sbSqlSegment.toString();
			};
		};
		StringBuilder sbSqlSelect = new StringBuilder();
		sbSqlSelect.append("IFNULL(sum(price),0)AS price");
		wrapper.setSqlSelect(sbSqlSelect.toString());
		Map<String,Object> data = this.selectMap(wrapper);
		return CommonUtil.toDouble(data.get("price"));
	}
}
