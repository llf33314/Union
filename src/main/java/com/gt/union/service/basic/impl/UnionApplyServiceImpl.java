package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.basic.UnionApplyConstant;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.basic.UnionApply;
import com.gt.union.mapper.basic.UnionApplyMapper;
import com.gt.union.service.basic.IUnionApplyService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 联盟成员申请推荐 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@Service
public class UnionApplyServiceImpl extends ServiceImpl<UnionApplyMapper, UnionApply> implements IUnionApplyService {

    @Override
    public Page listUncheckedApply(Page page, final Integer unionId, final String enterpriseName, final String directorPhone) throws Exception{
        if (page == null) {
            throw new Exception("UnionApplyServiceImpl.pageUnionApplyVO()：参数page不能为空!");
        }
        if (unionId == null) {
            throw new Exception("UnionApplyServiceImpl.pageUnionApplyVO()：参数union不能为空!");
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" a");
                sbSqlSegment.append(" LEFT JOIN t_union_apply_info i ON i.union_apply_id = a.id ")
                            .append(" LEFT JOIN t_union_apply a2 ON a2.apply_bus_id = a.recommend_bus_id ")
                            .append(" LEFT JOIN t_union_apply_info i2 ON i2.union_apply_id = a2.id ")
                            .append(" WHERE ")
                            .append("    (a.recommend_bus_id is null OR (a.recommend_bus_id IS NOT null AND a.bus_confirm_status = ")
                            .append(UnionApplyConstant.BUS_CONFIRM_STATUS_PASS).append(")")
                            .append("     ) ")
                            .append("     AND a.union_id = " + unionId);
                if (StringUtil.isNotEmpty(enterpriseName)) {
                    sbSqlSegment.append(" AND i.enterprise_name LIKE %").append(enterpriseName.trim()).append("% ");
                }
                if (StringUtil.isNotEmpty(directorPhone)) {
                    sbSqlSegment.append(" AND i.director_phone LIKE %").append(directorPhone.trim()).append("% ");
                }
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append(" a.id id, a.union_id unionId, DATE_FORMAT(a.createtime, '%Y-%m-%d %T') createtime, i2.enterprise_name recommendBusName" +
                ", a.apply_status applyStatus, i.enterprise_name enterpriseName, i.director_name directorName" +
                ", i.director_phone directorPhone, i.director_email directorEmail, i.apply_reason applyReason ");
        wrapper.setSqlSelect(sbSqlSelect.toString());

        return this.selectMapsPage(page, wrapper);
    }

}
