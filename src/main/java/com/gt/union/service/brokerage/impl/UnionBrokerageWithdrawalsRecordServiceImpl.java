package com.gt.union.service.brokerage.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.brokerage.UnionBrokerageWithdrawalsRecordConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.entity.brokerage.UnionBrokerageWithdrawalsRecord;
import com.gt.union.mapper.brokerage.UnionBrokerageWithdrawalsRecordMapper;
import com.gt.union.service.brokerage.IUnionBrokerageWithdrawalsRecordService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 联盟佣金提现记录 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@Service
public class UnionBrokerageWithdrawalsRecordServiceImpl extends ServiceImpl<UnionBrokerageWithdrawalsRecordMapper, UnionBrokerageWithdrawalsRecord> implements IUnionBrokerageWithdrawalsRecordService {
    private static final String SUM_MONEY_BUSID = "UnionBrokerageWithdrawalsRecordServiceImpl.sumMoneyByBusId()";
    private static final String SUM_MONEY_UNIONID_BUSID = "UnionBrokerageWithdrawalsRecordServiceImpl.sumMoneyByUnionIdAndBusId()";
    private static final String PAGE_MAP_BUSID = "UnionBrokerageWithdrawalsRecordServiceImpl.pageMapByBusId()";
    private static final String PAGE_MAP_UNIONID_BUSID = "UnionBrokerageWithdrawalsRecordServiceImpl.pageMapByUnionIdAndBusId()";

	@Override
	public Double sumMoneyByBusId(final Integer busId) throws Exception {
	    if (busId == null) {
	        throw new ParamException(SUM_MONEY_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                return new StringBuilder(" r")
                        .append(" WHERE r.del_status = ").append(UnionBrokerageWithdrawalsRecordConstant.DEL_STATUS_NO)
                        .append("  AND r.bus_id = ").append(busId)
                        .toString();
            }
        };
	    wrapper.setSqlSelect(" SUM(IFNULL(r.money, 0)) moneySum");
	    Map<String, Object> map = this.selectMap(wrapper);

		return map != null && map.get("moneySum") != null ? Double.valueOf(map.get("moneySum").toString()) : null;
	}

	@Override
	public Double sumMoneyByUnionIdAndBusId(final Integer unionId, final Integer busId) throws Exception {
	    if (unionId == null) {
	        throw new ParamException(SUM_MONEY_UNIONID_BUSID, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(SUM_MONEY_UNIONID_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                return new StringBuilder(" r")
                        .append(" WHERE r.del_status = ").append(UnionBrokerageWithdrawalsRecordConstant.DEL_STATUS_NO)
                        .append("  AND r.union_id = ").append(unionId)
                        .append("  AND r.bus_id = ").append(busId)
                        .toString();
            }
        };
        wrapper.setSqlSelect(" SUM(IFNULL(r.money, 0)) moneySum");
        Map<String, Object> map = this.selectMap(wrapper);

        return map != null && map.get("moneySum") != null ? Double.valueOf(map.get("moneySum").toString()) : null;
	}

	@Override
	public Page pageMapByBusId(Page page, final Integer busId) throws Exception {
	    if (page == null) {
	        throw new ParamException(PAGE_MAP_BUSID, "参数page为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
	        throw new ParamException(PAGE_MAP_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                return new StringBuilder(" r")
                        .append(" WHERE r.del_status = ").append(UnionBrokerageWithdrawalsRecordConstant.DEL_STATUS_NO)
                        .append("  AND r.bus_id = ").append(busId)
                        .append(" ORDER BY r.id DESC")
                        .toString();
            }
        };
	    wrapper.setSqlSelect(new StringBuilder(" r.createtime") //提现时间
                .append(", r.money") //提现金额
                .toString());

	    return this.selectPage(page, wrapper);
	}

	@Override
	public Page pageMapByUnionIdAndBusId(Page page, final Integer unionId, final Integer busId) throws Exception {
        if (page == null) {
            throw new ParamException(PAGE_MAP_UNIONID_BUSID, "参数page为空", ExceptionConstant.PARAM_ERROR);
        }
        if (unionId == null) {
            throw new ParamException(PAGE_MAP_UNIONID_BUSID, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(PAGE_MAP_UNIONID_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                return new StringBuilder(" r")
                        .append(" WHERE r.del_status = ").append(UnionBrokerageWithdrawalsRecordConstant.DEL_STATUS_NO)
                        .append("  AND r.union_id = ").append(unionId)
                        .append("  AND r.bus_id = ").append(busId)
                        .append(" ORDER BY r.id DESC")
                        .toString();
            }
        };
        wrapper.setSqlSelect(new StringBuilder(" r.createtime") //提现时间
                .append(", r.money") //提现金额
                .toString());

        return this.selectPage(page, wrapper);
	}
}
