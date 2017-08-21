package com.gt.union.service.consume.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.UnionApplyConstant;
import com.gt.union.common.constant.card.UnionBusMemberCardConstant;
import com.gt.union.common.constant.card.UnionCardInfoConstant;
import com.gt.union.common.constant.consume.UnionConsumeRecordConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.consume.UnionConsumeRecord;
import com.gt.union.mapper.consume.UnionConsumeRecordMapper;
import com.gt.union.service.consume.IUnionConsumeRecordService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 会员消费 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@Service
public class UnionConsumeRecordServiceImpl extends ServiceImpl<UnionConsumeRecordMapper, UnionConsumeRecord> implements IUnionConsumeRecordService {
    private static final String LIST_MY_UNNIONID = "UnionConsumeRecordServiceImpl.listMyByUnionId()";
    private static final String LIST_OTHER_UNIONID = "UnionConsumeRecordServiceImpl.LIST_OTHER_UNIONID()";

    @Override
    public Page listMyByUnionId(final Page page, final Integer unionId, final Integer busId, final Integer cardBusId
            , final String cardNo, final String phone, final String beginTime, final String endTime) throws Exception {
        if (page == null) {
            throw new ParamException(LIST_MY_UNNIONID, "参数page为空", ExceptionConstant.PARAM_ERROR);
        }
        if (unionId == null) {
            throw new ParamException(LIST_MY_UNNIONID, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(LIST_MY_UNNIONID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" r");
                sbSqlSegment.append(" LEFT JOIN t_union_card_info ci ON ci.union_card_id = r.union_card_id AND ci.union_id = r.union_id ")
                        .append("    LEFT JOIN t_union_bus_member_card c ON c.id = ci.union_card_id ")
                        .append("    LEFT JOIN t_union_apply a ON a.apply_bus_id = c.bus_id AND a.union_id = ci.union_id ")
                        .append("    LEFT JOIN t_union_apply_info ai ON ai.union_apply_id = a.id ")
                        .append("    LEFT JOIN t_union_consume_service_record sr ON sr.recore_id = r.id ")
                        .append("    LEFT JOIN t_union_member_preferential_service ps ON ps.id = sr.service_id ")
                        .append("    WHERE r.del_status = ").append(UnionConsumeRecordConstant.DEL_STATUS_NO)
                        .append("        AND r.union_id = ").append(unionId)
                        .append("        AND r.bus_id = ").append(busId)
                        .append("        AND ci.id IS NOT NULL ")
                        .append("        AND ci.del_status = ").append(UnionCardInfoConstant.DEL_STATUS_NO)
                        .append("        AND c.del_status = ").append(UnionBusMemberCardConstant.DEL_STATUS_NO)
                        .append("        AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("        AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("        AND sr.id IS NOT NULL ");
                if (cardBusId != null) {
                    sbSqlSegment.append(" AND c.bus_id = ").append(cardBusId);
                }
                if (StringUtil.isNotEmpty(cardNo)) {
                    sbSqlSegment.append(" AND c.cardNo LIKE '%").append(cardNo).append("%' ");
                }
                if (StringUtil.isNotEmpty(phone)) {
                    sbSqlSegment.append(" AND c.phone LIKE '%").append(phone).append("%' ");
                }
                if (StringUtil.isNotEmpty(beginTime)) {
                    sbSqlSegment.append(" AND r.createtime >= '").append(beginTime).append("' ");
                }
                if (StringUtil.isNotEmpty(endTime)) {
                    sbSqlSegment.append(" AND r.createtime <= '").append(endTime).append("' ");
                }
                sbSqlSegment.append(" GROUP BY r.id ")
                        .append("    ORDER BY r.createtime ASC,sr.id ASC ");
                return sbSqlSegment.toString();
            }
        };

        StringBuilder sbSqlSelect = new StringBuilder(" ai.enterprise_name enterpriseName ")
                .append(" , c.cardNo cardNo ")
                .append(" , c.member_id memberId ")
                .append(" , c.phone phone ")
                .append(" , r.total_money totalMoney ")
                .append(" , r.pay_money payMoney ")
                .append(" , ps.service_name serviceName ")
                .append(" , GROUP_CONCAT(ps.service_name SEPARATOR '$$') serviceNames ")
                .append(" , r.status status ")
                .append(" , DATE_FORMAT(r.createtime, '%Y-%m-%d %T') createTime ");
        wrapper.setSqlSelect(sbSqlSelect.toString());

        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public Page listOtherByUnionId(Page page, final Integer unionId, final Integer busId, final Integer cardBusId
            , final String cardNo, final String phone, final String beginTime, final String endTime) throws Exception {
        if (page == null) {
            throw new ParamException(LIST_OTHER_UNIONID, "参数page为空", ExceptionConstant.PARAM_ERROR);
        }
        if (unionId == null) {
            throw new ParamException(LIST_OTHER_UNIONID, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(LIST_OTHER_UNIONID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" r");
                sbSqlSegment.append(" LEFT JOIN t_union_card_info ci ON ci.union_card_id = r.union_card_id AND ci.union_id = r.union_id ")
                        .append("    LEFT JOIN t_union_bus_member_card c ON c.id = ci.union_card_id ")
                        .append("    LEFT JOIN t_union_apply a ON a.apply_bus_id = c.bus_id AND a.union_id = ci.union_id ")
                        .append("    LEFT JOIN t_union_apply_info ai ON ai.union_apply_id = a.id ")
                        .append("    LEFT JOIN t_union_consume_service_record sr ON sr.recore_id = r.id ")
                        .append("    LEFT JOIN t_union_member_preferential_service ps ON ps.id = sr.service_id ")
                        .append("    WHERE r.del_status = ").append(UnionConsumeRecordConstant.DEL_STATUS_NO)
                        .append("        AND ci.id IS NOT NULL ")
                        .append("        AND ci.union_card_id IN ( ")
                        .append("            SELECT c2.id ")
                        .append("            FROM t_union_bus_member_card c2 ")
                        .append("            LEFT JOIN t_union_card_info ci2 ON ci2.union_card_id = c2.id ")
                        .append("            WHERE c2.del_status = ").append(UnionBusMemberCardConstant.DEL_STATUS_NO)
                        .append("               AND ci2.del_status = ").append(UnionCardInfoConstant.DEL_STATUS_NO)
                        .append("               AND c2.bus_id = ").append(busId)
                        .append("               AND ci2.union_id = ").append(unionId)
                        .append("            ) ")
                        .append("        AND c.del_status = ").append(UnionBusMemberCardConstant.DEL_STATUS_NO)
                        .append("        AND r.bus_id != ").append(busId)
                        .append("        AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("        AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("        AND sr.id IS NOT NULL ");
                if (cardBusId != null) {
                    sbSqlSegment.append(" AND c.bus_id = ").append(cardBusId);
                }
                if (StringUtil.isNotEmpty(cardNo)) {
                    sbSqlSegment.append(" AND c.cardNo LIKE '%").append(cardNo).append("%' ");
                }
                if (StringUtil.isNotEmpty(phone)) {
                    sbSqlSegment.append(" AND c.phone LIKE '%").append(phone).append("%' ");
                }
                if (StringUtil.isNotEmpty(beginTime)) {
                    sbSqlSegment.append(" AND r.createtime >= '").append(beginTime).append("' ");
                }
                if (StringUtil.isNotEmpty(endTime)) {
                    sbSqlSegment.append(" AND r.createtime <= '").append(endTime).append("' ");
                }

                sbSqlSegment.append(" GROUP BY r.id ")
                        .append("    ORDER BY r.createtime ASC,sr.id ASC ");
                return sbSqlSegment.toString();
            }
        };

        StringBuilder sbSqlSelect = new StringBuilder(" ai.enterprise_name enterpriseName ")
                .append(" , c.cardNo cardNo ")
                .append(" , c.member_id memberId ")
                .append(" , c.phone phone ")
                .append(" , r.total_money totalMoney ")
                .append(" , r.pay_money payMoney ")
                .append(" , ps.service_name serviceName ")
                .append(" , GROUP_CONCAT(ps.service_name SEPARATOR '$$') serviceNames ")
                .append(" , r.status status ");
        wrapper.setSqlSelect(sbSqlSelect.toString());

        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public List<Map<String, Object>> listMyByUnionId(final Integer unionId, final Integer busId, final Integer fromBusId
            , final String cardNo, final String phone, final String beginTime, final String endTime) throws Exception{
        if (unionId == null) {
            throw new ParamException(LIST_MY_UNNIONID, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(LIST_MY_UNNIONID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" r");
                sbSqlSegment.append(" LEFT JOIN t_union_card_info ci ON ci.union_card_id = r.union_card_id AND ci.union_id = r.union_id ")
                        .append("    LEFT JOIN t_union_bus_member_card c ON c.id = ci.union_card_id ")
                        .append("    LEFT JOIN t_union_apply a ON a.apply_bus_id = c.bus_id AND a.union_id = ci.union_id ")
                        .append("    LEFT JOIN t_union_apply_info ai ON ai.union_apply_id = a.id ")
                        .append("    LEFT JOIN t_union_consume_service_record sr ON sr.recore_id = r.id ")
                        .append("    LEFT JOIN t_union_member_preferential_service ps ON ps.id = sr.service_id ")
                        .append("    WHERE r.del_status = ").append(UnionConsumeRecordConstant.DEL_STATUS_NO)
                        .append("        AND r.union_id = ").append(unionId)
                        .append("        AND r.bus_id = ").append(busId)
                        .append("        AND ci.id IS NOT NULL ")
                        .append("        AND ci.del_status = ").append(UnionCardInfoConstant.DEL_STATUS_NO)
                        .append("        AND c.del_status = ").append(UnionBusMemberCardConstant.DEL_STATUS_NO)
                        .append("        AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("        AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("        AND sr.id IS NOT NULL ");
                if (fromBusId != null) {
                    sbSqlSegment.append(" AND c.bus_id = ").append(fromBusId);
                }
                if (StringUtil.isNotEmpty(cardNo)) {
                    sbSqlSegment.append(" AND c.cardNo LIKE '%").append(cardNo).append("%' ");
                }
                if (StringUtil.isNotEmpty(phone)) {
                    sbSqlSegment.append(" AND c.phone LIKE '%").append(phone).append("%' ");
                }
                if (StringUtil.isNotEmpty(beginTime)) {
                    sbSqlSegment.append(" AND r.createtime >= '").append(beginTime).append("' ");
                }
                if (StringUtil.isNotEmpty(endTime)) {
                    sbSqlSegment.append(" AND r.createtime <= '").append(endTime).append("' ");
                }
                sbSqlSegment.append(" GROUP BY r.id ")
                        .append("    ORDER BY r.createtime ASC,sr.id ASC ");
                return sbSqlSegment.toString();
            }
        };

        StringBuilder sbSqlSelect = new StringBuilder(" ai.enterprise_name enterpriseName ")
                .append(" , c.cardNo cardNo ")
                .append(" , c.member_id memberId ")
                .append(" , c.phone phone ")
                .append(" , r.total_money totalMoney ")
                .append(" , r.pay_money payMoney ")
                .append(" , ps.service_name serviceName ")
                .append(" , GROUP_CONCAT(ps.service_name SEPARATOR '$$') serviceNames ")
                .append(" , r.status status ")
                .append(" , DATE_FORMAT(r.createtime, '%Y-%m-%d %T') createTime ");
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMaps(wrapper);
    }

	@Override
	public List<Map<String, Object>> listOtherByUnionId(final Integer unionId, final Integer busId, final Integer toBusId
            , final String cardNo, final String phone, final String beginTime, final String endTime) throws Exception{
        if (unionId == null) {
            throw new ParamException(LIST_OTHER_UNIONID, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(LIST_OTHER_UNIONID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" r");
                sbSqlSegment.append(" LEFT JOIN t_union_card_info ci ON ci.union_card_id = r.union_card_id AND ci.union_id = r.union_id ")
                        .append("    LEFT JOIN t_union_bus_member_card c ON c.id = ci.union_card_id ")
                        .append("    LEFT JOIN t_union_apply a ON a.apply_bus_id = c.bus_id AND a.union_id = ci.union_id ")
                        .append("    LEFT JOIN t_union_apply_info ai ON ai.union_apply_id = a.id ")
                        .append("    LEFT JOIN t_union_consume_service_record sr ON sr.recore_id = r.id ")
                        .append("    LEFT JOIN t_union_member_preferential_service ps ON ps.id = sr.service_id ")
                        .append("    WHERE r.del_status = ").append(UnionConsumeRecordConstant.DEL_STATUS_NO)
                        .append("        AND ci.id IS NOT NULL ")
                        .append("        AND ci.union_card_id IN ( ")
                        .append("            SELECT c2.id ")
                        .append("            FROM t_union_bus_member_card c2 ")
                        .append("            LEFT JOIN t_union_card_info ci2 ON ci2.union_card_id = c2.id ")
                        .append("            WHERE c2.del_status = ").append(UnionBusMemberCardConstant.DEL_STATUS_NO)
                        .append("               AND ci2.del_status = ").append(UnionCardInfoConstant.DEL_STATUS_NO)
                        .append("               AND c2.bus_id = ").append(busId)
                        .append("               AND ci2.union_id = ").append(unionId)
                        .append("            ) ")
                        .append("        AND c.del_status = ").append(UnionBusMemberCardConstant.DEL_STATUS_NO)
                        .append("        AND r.bus_id != ").append(busId)
                        .append("        AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("        AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("        AND sr.id IS NOT NULL ");
                if (toBusId != null) {
                    sbSqlSegment.append(" AND c.bus_id = ").append(toBusId);
                }
                if (StringUtil.isNotEmpty(cardNo)) {
                    sbSqlSegment.append(" AND c.cardNo LIKE '%").append(cardNo).append("%' ");
                }
                if (StringUtil.isNotEmpty(phone)) {
                    sbSqlSegment.append(" AND c.phone LIKE '%").append(phone).append("%' ");
                }
                if (StringUtil.isNotEmpty(beginTime)) {
                    sbSqlSegment.append(" AND r.createtime >= '").append(beginTime).append("' ");
                }
                if (StringUtil.isNotEmpty(endTime)) {
                    sbSqlSegment.append(" AND r.createtime <= '").append(endTime).append("' ");
                }

                sbSqlSegment.append(" GROUP BY r.id ")
                        .append("    ORDER BY r.createtime ASC,sr.id ASC ");
                return sbSqlSegment.toString();
            }
        };

        StringBuilder sbSqlSelect = new StringBuilder(" ai.enterprise_name enterpriseName ")
                .append(" , c.cardNo cardNo ")
                .append(" , c.member_id memberId ")
                .append(" , c.phone phone ")
                .append(" , r.total_money totalMoney ")
                .append(" , r.pay_money payMoney ")
                .append(" , ps.service_name serviceName ")
                .append(" , GROUP_CONCAT(ps.service_name SEPARATOR '$$') serviceNames ")
                .append(" , r.status status ");
        wrapper.setSqlSelect(sbSqlSelect.toString());

        return this.selectMaps(wrapper);
	}
}
