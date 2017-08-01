package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.basic.UnionApplyConstant;
import com.gt.union.common.constant.basic.UnionMemberPreferentialManagerConstant;
import com.gt.union.common.constant.basic.UnionMemberPreferentialServiceConstant;
import com.gt.union.entity.basic.UnionMemberPreferentialManager;
import com.gt.union.mapper.basic.UnionMemberPreferentialManagerMapper;
import com.gt.union.mapper.basic.UnionMemberPreferentialServiceMapper;
import com.gt.union.service.basic.IUnionMemberPreferentialManagerService;
import com.gt.union.service.basic.IUnionMemberPreferentialServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 盟员优惠项目管理 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@Service
public class UnionMemberPreferentialManagerServiceImpl extends ServiceImpl<UnionMemberPreferentialManagerMapper, UnionMemberPreferentialManager> implements IUnionMemberPreferentialManagerService {
    @Autowired
    private IUnionMemberPreferentialServiceService unionMemberPreferentialServiceService;

    @Override
    public Page listPreferentialManager(Page page, final Integer unionId, final Integer verifyStatus) throws Exception {
        if (page == null) {
            throw new Exception("UnionMemberPreferentialManagerServiceImpl.listPreferentialManager():参数page不能为空!");
        }
        if (unionId == null) {
            throw new Exception("UnionMemberPreferentialManagerServiceImpl.listPreferentialManager():参数unionId不能为空!");
        }
        if (verifyStatus == null) {
            throw new Exception("UnionMemberPreferentialManagerServiceImpl.listPreferentialManager():参数verifyStatus不能为空!");
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" m");
                sbSqlSegment.append(" LEFT JOIN t_union_apply a ON a.union_member_id = m.member_id ")
                        .append(" LEFT JOIN t_union_apply_info i on i.union_apply_id = a.id ")
                        .append(" WHERE m.del_status=").append(UnionMemberPreferentialManagerConstant.DEL_STATUS_NO)
                        .append("    AND m.union_id = ").append(unionId)
                        .append("    AND EXISTS ( ")
                        .append("        SELECT s.manager_id FROM t_union_member_preferential_service s ")
                        .append("        WHERE s.manager_id = m.id ")
                        .append("           AND s.del_status = ").append(UnionMemberPreferentialServiceConstant.DEL_STATUS_NO)
                        .append("           AND s.verify_status = ").append(verifyStatus)
                        .append("    ) ")
                        .append("    AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS);
                sbSqlSegment.append(" ORDER BY m.createtime DESC ");
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append(" m.id managerId ")
                .append(", i.enterprise_name enterpriseName ")
                .append(", m.preferential_illustration preferentialIllustration ");
        wrapper.setSqlSelect(sbSqlSelect.toString());

        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public Page listMyPreferentialManager(Page page, final Integer unionId, final Integer memberId) throws Exception {
        if (page == null) {
            throw new Exception("UnionMemberPreferentialManagerServiceImpl.listMyPreferentialManager():参数page不能为空!");
        }
        if (unionId == null) {
            throw new Exception("UnionMemberPreferentialManagerServiceImpl.listMyPreferentialManager():参数unionId不能为空!");
        }
        if (memberId == null) {
            throw new Exception("UnionMemberPreferentialManagerServiceImpl.listMyPreferentialManager():参数memberId不能为空!");
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" m");
                sbSqlSegment.append(" LEFT JOIN t_union_apply a ON a.union_member_id = s.union_member_id ")
                        .append(" LEFT JOIN t_union_apply_info i on i.union_apply_id = a.id ")
                        .append(" WHERE m.del_status=").append(UnionMemberPreferentialManagerConstant.DEL_STATUS_NO)
                        .append("    AND s.del_status = ").append(UnionMemberPreferentialServiceConstant.DEL_STATUS_NO)
                        .append("    AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("    AND m.union_id = ").append(unionId)
                        .append("    AND m.member_id = ").append(memberId);
                sbSqlSegment.append(" ORDER BY s.createtime DESC ");
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append(" m.verify_status verifyStatus ")
                .append(" i.enterprise_name enterpriseName ")
                .append(", m.preferential_illustration preferentialIllustration ");
        wrapper.setSqlSelect(sbSqlSelect.toString());

        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public int countPreferentialManager(final Integer unionId, final Integer verifyStatus) throws Exception {
        if (unionId == null) {
            throw new Exception("UnionMemberPreferentialManagerServiceImpl.countPreferentialManager():参数unionId不能为空!");
        }
        if (verifyStatus == null) {
            throw new Exception("UnionMemberPreferentialManagerServiceImpl.countPreferentialManager():参数verifyStatus不能为空!");
        } else if (UnionMemberPreferentialServiceConstant.VERIFY_STATUS_UNCOMMIT != verifyStatus
                && UnionMemberPreferentialServiceConstant.VERIFY_STATUS_UNCHECK != verifyStatus
                && UnionMemberPreferentialServiceConstant.VERIFY_STATUS_PASS != verifyStatus
                && UnionMemberPreferentialServiceConstant.VERIFY_STATUS_FAIL != verifyStatus) {
            throw new Exception("UnionMemberPreferentialManagerServiceImpl.countPreferentialManager():不支持的参数值verifyStatus(value=" + verifyStatus + ")!");
        }
        Wrapper wrapper = null;
        switch (verifyStatus) {
            case UnionMemberPreferentialServiceConstant.VERIFY_STATUS_UNCOMMIT://注意：未提交的逻辑改为不存在优惠服务项
                wrapper = new Wrapper() {
                    @Override
                    public String getSqlSegment() {
                        StringBuilder sbSqlSegment = new StringBuilder(" m");
                        sbSqlSegment.append(" WHERE m.del_status=").append(UnionMemberPreferentialManagerConstant.DEL_STATUS_NO)
                                .append("    AND m.union_id = ").append(unionId)
                                .append("    AND NOT EXISTS( ")
                                .append("        SELECT s.manager_id FROM t_union_member_preferential_service s ")
                                .append("        WHERE s.del_status = ").append(UnionMemberPreferentialServiceConstant.DEL_STATUS_NO)
                                .append("        AND s.manager_id = m.id ")
                                .append("    ) ");
                        return sbSqlSegment.toString();
                    }
                };
                wrapper.setSqlSelect(" m.id ");
                break;
            default:
                wrapper = new Wrapper() {
                    @Override
                    public String getSqlSegment() {
                        StringBuilder sbSqlSegment = new StringBuilder(" m");
                        sbSqlSegment.append(" WHERE m.del_status=").append(UnionMemberPreferentialManagerConstant.DEL_STATUS_NO)
                                .append("    AND m.union_id = ").append(unionId)
                                .append("    AND EXISTS ( ")
                                .append("        SELECT * FROM t_union_member_preferential_service s ")
                                .append("        WHERE s.del_status = ").append(UnionMemberPreferentialServiceConstant.DEL_STATUS_NO)
                                .append("        AND s.manager_id = m.id ")
                                .append("        AND s.verify_status = ").append(verifyStatus)
                                .append("    ) ");
                        return sbSqlSegment.toString();
                    };
                };
                wrapper.setSqlSelect(" m.id managerId ");
        }

        return this.selectCount(wrapper);
    }

    @Override
    public Map<String, Object> detailPreferentialManager(Page page, final Integer id, final Integer verifyStatus) throws Exception {
        if (page == null) {
            throw new Exception("UnionMemberPreferentialManagerServiceImpl.detailPreferentialManager():参数page不能为空!");
        }
        if (id == null) {
            throw new Exception("UnionMemberPreferentialManagerServiceImpl.detailPreferentialManager():参数id不能为空!");
        }
        if (verifyStatus == null) {
            throw new Exception("UnionMemberPreferentialManagerServiceImpl.detailPreferentialManager():参数verifyStatus不能为空!");
        }
        Wrapper managerWrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" m");
                sbSqlSegment.append(" LEFT JOIN t_union_apply a ON a.union_member_id = m.member_id ")
                        .append(" LEFT JOIN t_union_apply_info i on i.union_apply_id = a.id ")
                        .append(" WHERE m.del_status=").append(UnionMemberPreferentialManagerConstant.DEL_STATUS_NO)
                        .append("    AND m.id = ").append(id)
                        .append("    AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS);
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append(" i.enterprise_name enterpriseName ")
                .append(", m.preferential_illustration preferentialIllustration ");
        managerWrapper.setSqlSelect(sbSqlSelect.toString());
        Map<String, Object> resultMap = this.selectMap(managerWrapper);

        resultMap = null == resultMap ? new HashMap<String, Object>() : resultMap; //防止空指针
        resultMap.put("page", this.unionMemberPreferentialServiceService.pagePreferentialServiceByManagerId(page, id, verifyStatus));

        return resultMap;
    }
}
