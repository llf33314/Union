package com.gt.union.card.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.card.constant.CardConstant;
import com.gt.union.card.entity.UnionCardIntegral;
import com.gt.union.card.mapper.UnionCardIntegralMapper;
import com.gt.union.card.service.IUnionCardIntegralService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.ListUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * 联盟卡积分 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionCardIntegralServiceImpl extends ServiceImpl<UnionCardIntegralMapper, UnionCardIntegral> implements IUnionCardIntegralService {

    /**
     * 根据联盟id和积分状态，统计总积分数
     *
     * @param unionId {not null} 联盟id
     * @param status  {not null} 积分状态
     * @return
     * @throws Exception
     */
    @Override
    public Double sumCardIntegralByUnionIdAndStatus(final Integer unionId, final Integer status) throws Exception {
        if (unionId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" ci")
                        .append(" LEFT JOIN t_union_card c ON c.id = ci.card_id")
                        .append(" LEFT JOIN t_union_member m ON m.id=c.member_id")
                        .append(" WHERE ci.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND ci.status = ").append(status)
                        .append("  AND c.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND m.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("  AND m.union_id = ").append(unionId);
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" IFNULL(SUM(ci.integral), 0) integralSum");
        wrapper.setSqlSelect(sbSqlSelect.toString());
        Map<String, Object> sqlResultMap = this.selectMap(wrapper);
        Object objIntegralSum = sqlResultMap.get("integralSum");

        return Double.valueOf(objIntegralSum != null ? objIntegralSum.toString() : "0");
    }

    /**
     * 根据联盟id，获取联盟剩余有效的积分数，即收入积分-支出积分
     *
     * @param unionId {not null} 联盟id
     * @return
     * @throws Exception
     */
    @Override
    public Double getCardIntegralProfitByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Double inIntegralSum = this.sumCardIntegralByUnionIdAndStatus(unionId, CardConstant.INTEGRAL_STATUS_IN);
        Double outIntegralSum = this.sumCardIntegralByUnionIdAndStatus(unionId, CardConstant.INTEGRAL_STATUS_OUT);

        return BigDecimalUtil.subtract(inIntegralSum, outIntegralSum).doubleValue();
    }

}
