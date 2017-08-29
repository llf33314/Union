package com.gt.union.service.card.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.card.UnionCardDivideRecordConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.entity.card.UnionCardDivideRecord;
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
	private static final String SUM_PRICE_UNIONID_BUSID = "UnionCardDivideRecordServiceImpl.sumPriceByUnionIdAndBusId()";
	private static final String SUM_PRICE_BUSID = "UnionCardDivideRecordServiceImpl.sumPriceByBusId()";

	@Override
	public Double sumPriceByUnionIdAndBusId(final Integer unionId, final Integer busId) throws Exception {
	    if (unionId == null) {
	        throw new ParamException(SUM_PRICE_UNIONID_BUSID, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
	        throw new ParamException(SUM_PRICE_UNIONID_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                return new StringBuilder(" r")
                        .append(" WHERE r.del_status = ").append(UnionCardDivideRecordConstant.DEL_STATUS_NO)
                        .append("  AND r.union_id = ").append(unionId)
                        .append("  AND r.bus_id = ").append(busId)
                        .toString();
            }
        };
	    wrapper.setSqlSelect(" SUM(IF(r.price, 0)) priceSum");
	    Map<String, Object> map = this.selectMap(wrapper);

		return map != null && map.get("priceSum") != null ? Double.valueOf(map.get("priceSum").toString()) : null;
	}

	@Override
	public Double sumPriceByBusId(final Integer busId) throws Exception {
	    if (busId == null) {
	        throw new ParamException(SUM_PRICE_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                return new StringBuilder(" r")
                        .append(" WHERE r.del_status = ").append(UnionCardDivideRecordConstant.DEL_STATUS_NO)
                        .append("  AND r.bus_id = ").append(busId)
                        .toString();
            }
        };
	    wrapper.setSqlSelect(" SUM(IFNULL(r.price, 0)) priceSum");
        Map<String, Object> map = this.selectMap(wrapper);

		return map != null && map.get("priceSum") != null ? Double.valueOf(map.get("priceSum").toString()) : null;
	}

}
