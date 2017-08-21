package com.gt.union.service.basic.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.UnionApplyConstant;
import com.gt.union.common.constant.basic.UnionMemberPreferentialManagerConstant;
import com.gt.union.common.constant.basic.UnionMemberPreferentialServiceConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.basic.UnionMember;
import com.gt.union.entity.basic.UnionMemberPreferentialManager;
import com.gt.union.mapper.basic.UnionMemberPreferentialManagerMapper;
import com.gt.union.service.basic.IUnionMemberPreferentialManagerService;
import com.gt.union.service.basic.IUnionMemberPreferentialServiceService;
import com.gt.union.service.basic.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
    private static final String LIST_UNIONID_VERIFYSTATUS = "UnionMemberPreferentialManagerServiceImpl.listByUnionIdAndVerifyStatus()";
    private static final String LIST_MY_UNIONID = "UnionMemberPreferentialManagerServiceImpl.listMyByUnionId()";
    private static final String COUNT_PREFERENTIAL_MANAGER = "UnionMemberPreferentialManagerServiceImpl.countPreferentialManager()";
    private static final String GET_ID_VERIFYSTATUS = "UnionMemberPreferentialManagerServiceImpl.getByIdAndVerifyStatus()";
    private static final String GET_UNIONID_MANAGER = "UnionMemberPreferentialManagerServiceImpl.getManagerByUnionId()";
    private static final String SAVE = "UnionMemberPreferentialManagerServiceImpl.save()";

    @Autowired
    private IUnionMemberPreferentialServiceService unionMemberPreferentialServiceService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Override
    public Page listByUnionIdAndVerifyStatus(Page page, final Integer unionId, final Integer verifyStatus) throws Exception {
        if (page == null) {
            throw new ParamException(LIST_UNIONID_VERIFYSTATUS, "参数page为空", ExceptionConstant.PARAM_ERROR);
        }
        if (unionId == null) {
            throw new ParamException(LIST_UNIONID_VERIFYSTATUS, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (verifyStatus == null) {
            throw new ParamException(LIST_UNIONID_VERIFYSTATUS, "参数verifyStatus为空", ExceptionConstant.PARAM_ERROR);
        }
        Page result = null;
        switch (verifyStatus){
            case UnionMemberPreferentialServiceConstant.VERIFY_STATUS_UNCOMMIT:
                result =  unionMemberService.listUncommitByUnionId(page, unionId);
                break;
            default:
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
                result = this.selectMapsPage(page, wrapper);
                break;
        }
        return result;
    }

    @Override
    public Page listMyByUnionId(Page page, final Integer unionId, final Integer busId) throws Exception {
        if (page == null) {
            throw new ParamException(LIST_MY_UNIONID, "参数page为空", ExceptionConstant.PARAM_ERROR);
        }
        if (unionId == null) {
            throw new ParamException(LIST_MY_UNIONID, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(LIST_MY_UNIONID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        UnionMember member = unionMemberService.getByUnionIdAndBusId(unionId, busId);
        return unionMemberPreferentialServiceService.listMyByUnionId(page, unionId, member.getId());
    }

    @Override
    public int countPreferentialManager(final Integer unionId, final Integer verifyStatus) throws Exception {
        if (unionId == null) {
            throw new ParamException(COUNT_PREFERENTIAL_MANAGER, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (verifyStatus == null) {
            throw new ParamException(COUNT_PREFERENTIAL_MANAGER, "参数verifyStatus为空", ExceptionConstant.PARAM_ERROR);
        } else if (UnionMemberPreferentialServiceConstant.VERIFY_STATUS_UNCOMMIT != verifyStatus
                && UnionMemberPreferentialServiceConstant.VERIFY_STATUS_UNCHECK != verifyStatus
                && UnionMemberPreferentialServiceConstant.VERIFY_STATUS_PASS != verifyStatus
                && UnionMemberPreferentialServiceConstant.VERIFY_STATUS_FAIL != verifyStatus) {
            throw new ParamException(COUNT_PREFERENTIAL_MANAGER, "不支持的参数值", ExceptionConstant.PARAM_ERROR);
        }
        int count = 0;
        switch (verifyStatus) {
            case UnionMemberPreferentialServiceConstant.VERIFY_STATUS_UNCOMMIT://注意：未提交的逻辑改为不存在优惠服务项
                count = unionMemberService.countUncommitPreferentialManager(unionId);
                break;
            default:
                Wrapper wrapper = new Wrapper() {
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
                count = this.selectCount(wrapper);
        }
        return count;
    }

    @Override
    public Map<String, Object> getByIdAndVerifyStatus(Page page, final Integer id, final Integer verifyStatus) throws Exception {
        if (page == null) {
            throw new ParamException(GET_ID_VERIFYSTATUS, "参数page为空", ExceptionConstant.PARAM_ERROR);
        }
        if (id == null) {
            throw new ParamException(GET_ID_VERIFYSTATUS, "参数id为空", ExceptionConstant.PARAM_ERROR);
        }
        if (verifyStatus == null) {
            throw new ParamException(GET_ID_VERIFYSTATUS, "参数verifyStatus为空", ExceptionConstant.PARAM_ERROR);
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

    @Override
    public UnionMemberPreferentialManager getManagerByUnionId(Integer unionId, Integer busId) throws Exception{
        if (unionId == null) {
            throw new ParamException(GET_UNIONID_MANAGER, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(GET_UNIONID_MANAGER, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        UnionMemberPreferentialManager manager = null;
        if(redisCacheUtil.exists("manager:" + unionId + ":" + busId)){
            Object obj = redisCacheUtil.get("manager:" + unionId + ":" + busId);
            if (CommonUtil.isNotEmpty(obj)) {
                return JSON.parseObject(obj.toString(),UnionMemberPreferentialManager.class);
            }
        }
        UnionMember member = unionMemberService.getByUnionIdAndBusId(unionId, busId);
        EntityWrapper entityWrapper = new EntityWrapper<UnionMemberPreferentialManager>();
        entityWrapper.eq("del_status",UnionMemberPreferentialManagerConstant.DEL_STATUS_NO);
        entityWrapper.eq("union_id",unionId);
        entityWrapper.eq("member_id",member.getId());
        manager = this.selectOne(entityWrapper);
        if(CommonUtil.isNotEmpty(manager)){
            redisCacheUtil.set("manager:" + unionId + ":" + busId, JSON.toJSONString(manager));
        }
        return manager;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UnionMemberPreferentialManager save(Integer unionId, Integer busId, String preferentialIllustration) throws Exception{
        //TODO 判断联盟有效
        if (unionId == null) {
            throw new ParamException(SAVE, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(SAVE, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        if(StringUtil.isEmpty(preferentialIllustration)){
            throw new ParamException(SAVE, "项目说明为空", "项目说明不能为空");
        }
        if(StringUtil.getStringLength(preferentialIllustration) > 50){
            throw new ParamException(SAVE, "项目说明长度超过50字", "项目说明长度不可超过50字");
        }
        UnionMemberPreferentialManager manager = getManagerByUnionId(unionId,busId);
        if(manager == null){
            UnionMember member = unionMemberService.getByUnionIdAndBusId(unionId, busId);
            manager = new UnionMemberPreferentialManager();
            manager.setDelStatus(0);
            manager.setCreatetime(new Date());
            manager.setLastModifyTime(new Date());
            manager.setMemberId(member.getId());
            manager.setVerifyStatus(2);
            manager.setUnionId(unionId);
            manager.setPreferentialIllustration(preferentialIllustration);
            this.insert(manager);
        }else {
            manager.setPreferentialIllustration(preferentialIllustration);
            manager.setLastModifyTime(new Date());
            this.updateById(manager);
        }
        return manager;
    }
}
