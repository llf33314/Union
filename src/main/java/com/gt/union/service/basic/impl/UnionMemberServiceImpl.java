package com.gt.union.service.basic.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.*;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.basic.UnionMember;
import com.gt.union.entity.basic.UnionTransferRecord;
import com.gt.union.mapper.basic.UnionMemberMapper;
import com.gt.union.service.basic.*;
import com.gt.union.service.common.IUnionRootService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟成员列表 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@Service
public class UnionMemberServiceImpl extends ServiceImpl<UnionMemberMapper, UnionMember> implements IUnionMemberService {
    private static final String LIST_MAP_UNIONID_PAGE = "UnionMemberServiceImpl.listMapByUnionIdInPage()";
    private static final String LIST_MAP_UNIONID_LIST = "UnionMemberServiceImpl.listMapByUnionIdInList()";
    private static final String LIST_UNIONID_OUTSTATUS = "UnionMemberServiceImpl.listByUnionIdAndOutStatus()";
    private static final String LIST_UNIONID_OUTSTATUS_ISNUIONOWNER = "UnionMemberServiceImpl.listByUnionIdAndOutStatusAndIsNuionOwner()";
    private static final String GET_MAP_ID = "UnionMemberServiceImpl.getById()";
    private static final String TRANSFER_UNION_OWNER = "UnionMemberServiceImpl.transferUnionOwner()";
    private static final String UPDATE_OUTSTATUS_ID = "UnionMemberServiceImpl.updateOutStatusById()";
    private static final String GET_UNIONID_BUSID = "UnionMemberServiceImpl.getByUnionIdAndBusId()";
    private static final String LIST_UNIONID_LIST_ALL = "UnionMemberServiceImpl.listByUnionIdList()";
    private static final String APPLY_OUT_UNION = "UnionMemberServiceImpl.applyOutUnion()";
    private static final String MEMBER_OUT_APPLY_MSG = "UnionMemberServiceImpl.getMemberOutApplyMsgInfo()";
    private static final String CANCEL_UNION_OWNER = "UnionMemberServiceImpl.cancelUnionOwner()";

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionApplyService unionApplyService;

    @Autowired
    private IUnionApplyInfoService unionApplyInfoService;

    @Autowired
    private IUnionRootService unionRootService;

    @Autowired
    private IUnionTransferRecordService unionTransferRecordService;

    @Autowired
    private IUnionMainService unionMainService;

    @Override
    public Page listMapByUnionIdInPage(Page page, final Integer unionId, final String enterpriseName) throws Exception {
        if (page == null) {
            throw new ParamException(LIST_MAP_UNIONID_PAGE, "参数page为空", ExceptionConstant.PARAM_ERROR);
        }
        if (unionId == null) {
            throw new ParamException(LIST_MAP_UNIONID_PAGE, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" m");
                sbSqlSegment.append(" LEFT JOIN t_union_discount d4 on d4.from_bus_id = m.bus_id ")
                        .append(" LEFT JOIN t_union_discount d2 on d2.to_bus_id = m.bus_id ")
                        .append(" LEFT JOIN t_union_apply a on a.union_member_id = m.id ")
                        .append(" LEFT JOIN t_union_apply_info i on i.union_apply_id = a.id ")
                        .append(" WHERE m.del_status=").append(UnionMemberConstant.DEL_STATUS_NO)
                        .append("    AND d4.del_status = ").append(UnionDiscountConstant.DEL_STATUS_NO)
                        .append("    AND d2.del_status = ").append(UnionMemberConstant.DEL_STATUS_NO)
                        .append("    AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("    AND m.union_id = ").append(unionId);
                if (StringUtil.isNotEmpty(enterpriseName)) {
                    sbSqlSegment.append(" AND i.enterprise_name LIKE '%").append(enterpriseName).append("%' ");
                }
                sbSqlSegment.append(" ORDER BY m.is_nuion_owner DESC,m.createtime DESC ");
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append(" m.is_nuion_owner isUnionOwner ")
            .append(", m.id id ")
            .append(", m.bus_id busId ")
            .append(", DATE_FORMAT(m.createtime, '%Y-%m-%d %T') createtime ")
            .append(", d4.discount fromDiscount ")
            .append(", d2.discount toDiscount ")
            .append(", i.sell_divide_proportion sellDivideProportion ")
            .append(", i.enterprise_name enterpriseName ");
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public List<Map<String, Object>> listMapByUnionIdInList(final Integer unionId) throws Exception{
        if (unionId == null) {
            throw new ParamException(LIST_MAP_UNIONID_LIST, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" m");
                sbSqlSegment.append(" LEFT JOIN t_union_apply a on a.union_member_id = m.id ")
                        .append(" LEFT JOIN t_union_apply_info i on i.union_apply_id = a.id ")
                        .append(" WHERE m.union_id = ").append(unionId)
                        .append("    AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND m.del_status = ").append(UnionMemberConstant.DEL_STATUS_NO);
                sbSqlSegment.append(" ORDER BY m.id DESC ");
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append(", m.id id ")
                .append(", m.union_id unionId ")
                .append(", m.bus_id busId ")
                .append(", i.enterprise_name enterpriseName ")
                .append(", i.sell_divide_proportion sellDivideProportion ")
                .append(", i.notify_phone notifyPhone ")
                .append(", i.bus_address busAddress ")
                .append(", i.director_phone directorPhone ")
                .append(", i.address_longitude addressLongitude ")
                .append(", i.address_latitude addressLatitude ")
                .append(", i.integral_proportion integralProportion ")
                .append(", i.address_provience_id addressProvienceId ")
                .append(", i.address_city_id addressCityId ")
                .append(", i.address_district_id addressDistrictId ")
                .append(", i.is_member_out_advice isMemberOutAdvice ");
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMaps(wrapper);
    }

    @Override
    public Page listByUnionIdAndOutStatus(Page page, final Integer unionId, final Integer outStatus) throws Exception {
        if (page == null) {
            throw new ParamException(LIST_UNIONID_OUTSTATUS, "参数page为空", ExceptionConstant.PARAM_ERROR);
        }
        if (unionId == null) {
            throw new ParamException(LIST_UNIONID_OUTSTATUS, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (outStatus == null) {
            throw new ParamException(LIST_UNIONID_OUTSTATUS, "参数outStatus为空", ExceptionConstant.PARAM_ERROR);
        }

        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" m");
                sbSqlSegment.append(" LEFT JOIN t_union_apply a on a.union_member_id = m.id ")
                        .append(" LEFT JOIN t_union_apply_info i on i.union_apply_id = a.id ")
                        .append(" WHERE m.del_status=").append(UnionMemberConstant.DEL_STATUS_NO)
                        .append("    AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("    AND m.union_id = ").append(unionId)
                        .append("    AND m.out_staus = ").append(outStatus);
                sbSqlSegment.append(" ORDER BY m.apply_out_time DESC ");
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append(" DATE_FORMAT(m.apply_out_time, '%Y-%m-%d %T') applyOutTime ")
                .append(", m.out_reason outReason ")
                .append(", DATE_FORMAT(m.confirm_out_time, '%Y-%m-%d %T') confirmOutTime ")
                .append(", DATEDIFF(now(), m.confirm_out_time) remainOutTime ")
                .append(", i.enterprise_name enterpriseName ");
        wrapper.setSqlSelect(sbSqlSelect.toString());

        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public Page listByUnionIdAndOutStatusAndIsNuionOwner(Page page, final Integer unionId, final Integer busId
            , final Integer outStatus, final Integer isNuionOwner) throws Exception {
        if (page == null) {
            throw new ParamException(LIST_UNIONID_OUTSTATUS_ISNUIONOWNER, "参数page为空", ExceptionConstant.PARAM_ERROR);
        }
        if (unionId == null) {
            throw new ParamException(LIST_UNIONID_OUTSTATUS_ISNUIONOWNER, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(LIST_UNIONID_OUTSTATUS_ISNUIONOWNER, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        } else {
            if (!this.unionRootService.isUnionOwner(unionId, busId)) {
                throw new BusinessException(LIST_UNIONID_OUTSTATUS_ISNUIONOWNER, "", CommonConstant.UNION_OWNER_NON_AUTHORITY_MSG);
            }
        }
        if (outStatus == null) {
            throw new ParamException(LIST_UNIONID_OUTSTATUS_ISNUIONOWNER, "参数outStatus为空", ExceptionConstant.PARAM_ERROR);
        }
        if (isNuionOwner == null) {
            throw new ParamException(LIST_UNIONID_OUTSTATUS_ISNUIONOWNER, "参数isNuionOwner为空", ExceptionConstant.PARAM_ERROR);
        }

        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" m");
                sbSqlSegment.append(" LEFT JOIN t_union_apply a on a.union_member_id = m.id ")
                        .append(" LEFT JOIN t_union_apply_info i on i.union_apply_id = a.id ")
                        .append(" WHERE m.del_status = ").append(UnionMemberConstant.DEL_STATUS_NO)
                        .append("    AND m.is_nuion_owner = ").append(isNuionOwner)
                        .append("    AND m.out_staus = ").append(outStatus)
                        .append("    AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("    AND m.union_id = ").append(unionId);
                sbSqlSegment.append(" ORDER BY m.apply_out_time DESC ");
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append(" DATE_FORMAT(m.createtime, '%Y-%m-%d %T') createtime ")
                .append(", m.id id ")
                .append(", i.enterprise_name enterpriseName ");
        wrapper.setSqlSelect(sbSqlSelect.toString());

        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public Map<String, Object> getMapById(final Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(GET_MAP_ID, "参数id为空", ExceptionConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" m");
                sbSqlSegment.append(" LEFT JOIN t_union_apply a on a.union_member_id = m.id ")
                        .append(" LEFT JOIN t_union_apply_info i on i.union_apply_id = a.id ")
                        .append(" WHERE m.del_status=").append(UnionMemberConstant.DEL_STATUS_NO)
                        .append("    AND m.id = ").append(id)
                        .append("    AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS);
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append(" DATE_FORMAT(m.createtime, '%Y-%m-%d %T') createtime ")
                .append(", i.enterprise_name enterpriseName ")
                .append(", i.director_name directorName ")
                .append(", i.director_phone directorPhone ")
                .append(", i.director_email directorEmail ")
                .append(", i.bus_address busAddress ");
        wrapper.setSqlSelect(sbSqlSelect.toString());

        return this.selectMap(wrapper);
    }

    @Override
    public void acceptUnionOwner(Integer busId, Integer unionId) throws Exception {
        //TODO  接受盟主权限转移

    }

    @Override
    public void transferUnionOwner(Integer id, Integer unionId, Integer originalOwnerBusId) throws Exception {
        UnionMember unionMember;
        if (id == null) {
            throw new ParamException(TRANSFER_UNION_OWNER, "参数id为空", ExceptionConstant.PARAM_ERROR);
        } else {
            unionMember = this.selectById(id);
            if (unionMember == null) {
                throw new ParamException(TRANSFER_UNION_OWNER, "无法通过id获取对象", ExceptionConstant.PARAM_ERROR);
            }
        }
        if (unionId == null) {
            throw new ParamException(TRANSFER_UNION_OWNER, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (originalOwnerBusId == null) {
            throw new ParamException(TRANSFER_UNION_OWNER, "参数originalOwnerBusId为空", ExceptionConstant.PARAM_ERROR);
        }

        // （1）判断当前用户是否用户盟主的身份
        if (!this.unionRootService.isUnionOwner(unionId, originalOwnerBusId)) {
            throw new BusinessException(TRANSFER_UNION_OWNER, "", CommonConstant.UNION_OWNER_NON_AUTHORITY_MSG);
        }

        // （2）判断盟员是否拥有该联盟权限，即是否仍在该联盟中
        Integer newOwnerBusId = unionMember.getBusId();
        if (!this.unionRootService.hasUnionMemberAuthority(unionMember)) {
            throw new BusinessException(TRANSFER_UNION_OWNER, "", "盟员正处于退盟过渡期");
        }

        // （3）判断盟员是否具有成为盟主的权限
        if (!this.unionRootService.hasUnionOwnerAuthority(newOwnerBusId)) {
            throw new BusinessException(TRANSFER_UNION_OWNER, "", "盟员不具有成为盟主的权限");
        }

        // （4）判断盟员是否已经是盟主
        if (!this.unionRootService.isUnionOwner(newOwnerBusId)) {
            throw new BusinessException(TRANSFER_UNION_OWNER, "", "盟员已经是某联盟的盟主，无法同时成为多个联盟的盟主");
        }

        // （5）判断是否已经存在转移申请，若有，则直接返回操作成功，否则，新增转移申请记录
        if (!this.unionTransferRecordService.existByUnionIdAndBusIdAndConfirmStatus(unionId , newOwnerBusId, UnionTransferRecordConstant.CONFIRM_STATUS_UNCHECK)) {
            UnionTransferRecord unionTransferRecord = new UnionTransferRecord();
            unionTransferRecord.setDelStatus(UnionTransferRecordConstant.DEL_STATUS_NO);
            unionTransferRecord.setCreatetime(DateTimeKit.getNow());
            unionTransferRecord.setUnionId(unionId);
            unionTransferRecord.setFromBusId(originalOwnerBusId);
            unionTransferRecord.setToBusId(newOwnerBusId);
            unionTransferRecord.setConfirmStatus(UnionTransferRecordConstant.CONFIRM_STATUS_UNCHECK);
            this.unionTransferRecordService.save(unionTransferRecord);
        }
    }

    @Override
    public UnionMember getByUnionIdAndBusId(Integer unionId, Integer busId) throws Exception{
        if (unionId == null) {
            throw new ParamException(GET_UNIONID_BUSID, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(GET_UNIONID_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        // （1）判断缓存中是否存在，如存在，则直接返回
        String unionMemberBusIdKey = RedisKeyUtil.getUnionMemberBusIdKey(unionId, busId);
        if (redisCacheUtil.exists(unionMemberBusIdKey)) {
            Object obj = redisCacheUtil.get(unionMemberBusIdKey);
            if(CommonUtil.isNotEmpty(obj)){
                return JSON.parseObject(obj.toString(), UnionMember.class);
            }
        }

        // （2） 如不存在，则从数据库查询
        EntityWrapper<UnionMember> entityWrapper = new EntityWrapper<UnionMember>();
        entityWrapper.eq("del_status",UnionMemberConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("bus_id", busId);
        UnionMember unionMember = this.selectOne(entityWrapper);

        // （3）如查询到有效数据，则存入缓存中
        if (unionMember != null) {
            redisCacheUtil.set(unionMemberBusIdKey, JSON.toJSONString(unionMember));
        }

        return unionMember;
    }

    @Override
    public int getUnionMemberCount(Integer applyBusId) {
        EntityWrapper entityWrapper = new EntityWrapper<UnionMember>();
        entityWrapper.eq("bus_id",applyBusId);
        entityWrapper.eq("del_status",0);
        return this.selectCount(entityWrapper);
    }

    @Override
    public Map<String, Object> updateOutStatusById(Integer id, Integer unionId, Integer busId, Integer verifyStatus) throws Exception{
        Map<String,Object> data = new HashMap<String,Object>();
        if(CommonUtil.isEmpty(id) || CommonUtil.isEmpty(unionId) || CommonUtil.isEmpty(busId) || CommonUtil.isEmpty(verifyStatus)){
            throw new ParamException(UPDATE_OUTSTATUS_ID, "参数...错误", ExceptionConstant.PARAM_ERROR);
        }
        // （1）判断联盟是否有效
        if(!unionRootService.checkUnionMainValid(unionId)){
            throw new BusinessException(APPLY_OUT_UNION, "", CommonConstant.UNION_OVERDUE_MSG);
        }
        if(!this.unionRootService.isUnionOwner(unionId, busId)){
            throw new BusinessException(UPDATE_OUTSTATUS_ID, "", CommonConstant.UNION_OWNER_NON_AUTHORITY_MSG);
        }
        EntityWrapper entityWrapper = new EntityWrapper<UnionMember>();
        entityWrapper.eq("id", id);
        UnionMember unionMember = this.selectOne(entityWrapper);
        if(unionMember == null ){
            throw new BusinessException(UPDATE_OUTSTATUS_ID, "", "盟员不存在");
        }
        if(unionMember.getDelStatus() == 1){
            throw new BusinessException(UPDATE_OUTSTATUS_ID, "", "已退出联盟");
        }
        if(unionMember.getOutStaus() == 0){
            throw new BusinessException(UPDATE_OUTSTATUS_ID, "", "未申请退盟");
        }
        if(unionMember.getOutStaus() == 2){
            throw new BusinessException(UPDATE_OUTSTATUS_ID, "", "已处退盟过渡期");
        }
        if(unionMember.getIsNuionOwner() == 1){
            throw new BusinessException(UPDATE_OUTSTATUS_ID, "", "不可审核盟主");
        }
        UnionApplyInfo unionApplyInfo = null;
        UnionApplyInfo mainApplyInfo = null;
        if(verifyStatus == 1){//同意
            unionApplyInfo = this.unionApplyInfoService.getByUnionIdAndBusId(unionId, unionMember.getBusId());
            mainApplyInfo = this.unionApplyInfoService.getByUnionIdAndBusId(unionId, busId);
            if(CommonUtil.isNotEmpty(unionApplyInfo.getSellDivideProportion()) && unionApplyInfo.getSellDivideProportion() > 0){
                mainApplyInfo.setSellDivideProportion(CommonUtil.isNotEmpty(mainApplyInfo.getSellDivideProportion()) ? BigDecimalUtil.add(mainApplyInfo.getSellDivideProportion(),unionApplyInfo.getSellDivideProportion()).doubleValue() : unionApplyInfo.getSellDivideProportion());
                unionApplyInfoService.updateById(mainApplyInfo);
                unionApplyInfo.setSellDivideProportion(0d);
                unionApplyInfoService.updateById(unionApplyInfo);
            }
            unionMember.setOutStaus(2);
            unionMember.setConfirm(new Date());
            unionMember.setConfirmOutTime(DateTimeKit.addDate(unionMember.getConfirm(), 15));
            this.updateById(unionMember);
            String redisKey = "confirmOut:" + System.currentTimeMillis();
            Map<String,Object> param = new HashMap<String,Object>();
            param.put("busId",unionMember.getBusId());
            param.put("unionId",unionMember.getUnionId());
            redisCacheUtil.set(redisKey, JSON.toJSONString(param),60l);
            data.put("redisKey",redisKey);
        }else if(verifyStatus == 2){//拒绝
            unionMember.setOutStaus(0);
            unionMember.setApplyOutTime(null);
            this.updateAllColumnById(unionMember);
        }
        String memberKey = RedisKeyUtil.getUnionMemberBusIdKey(unionMember.getUnionId(),unionMember.getBusId());
        redisCacheUtil.set(memberKey,JSON.toJSONString(unionMember));
        if(CommonUtil.isNotEmpty(unionApplyInfo)){
            String infoKey = RedisKeyUtil.getUnionApplyInfoKey(unionMember.getUnionId(),unionMember.getBusId());
            redisCacheUtil.set(infoKey,JSON.toJSONString(unionApplyInfo));
        }
        if(CommonUtil.isNotEmpty(mainApplyInfo)){
            String infoKey = RedisKeyUtil.getUnionApplyInfoKey(unionId,busId);
            redisCacheUtil.set(infoKey,JSON.toJSONString(unionApplyInfo));
        }
        return data;
    }
    
    @Override
    public Page listUncommitByUnionId(Page page, final Integer unionId) {
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" t1");
                sbSqlSegment.append(" LEFT JOIN t_union_apply t2 ON t1.id = t2.union_member_id ")
                        .append(" LEFT JOIN t_union_apply_info t3 on t3.union_apply_id = t2.id ")
                        .append(" WHERE t1.union_id = ").append(unionId)
                        .append(" AND t1.del_status=").append(UnionMemberConstant.DEL_STATUS_NO)
                        .append(" AND t2.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append(" AND NOT EXISTS (SELECT id from t_union_member_preferential_manager m ")
                        .append(" WHERE m.member_id = t1.id ")
                        .append(" AND m.del_status = ").append(UnionMemberPreferentialManagerConstant.DEL_STATUS_NO)
                        .append(" )");
                sbSqlSegment.append(" ORDER BY t1.id DESC ");
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append("t1.id")
                .append(", t3.enterprise_name enterpriseName ");
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public int countUncommitPreferentialManager(final Integer unionId) {
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" t1");
                sbSqlSegment.append(" LEFT JOIN t_union_apply t2 ON t1.id = t2.union_member_id ")
                        .append(" LEFT JOIN t_union_apply_info t3 on t3.union_apply_id = t2.id ")
                        .append(" WHERE t1.union_id = ").append(unionId)
                        .append(" AND t1.del_status=").append(UnionMemberConstant.DEL_STATUS_NO)
                        .append(" AND t2.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append(" AND NOT EXISTS (SELECT id from t_union_member_preferential_manager m ")
                        .append(" WHERE m.member_id = t1.id ")
                        .append(" AND m.del_status = ").append(UnionMemberPreferentialManagerConstant.DEL_STATUS_NO)
                        .append(" )");
                sbSqlSegment.append(" ORDER BY t1.id DESC ");
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append("t1.id id");
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectCount(wrapper);
    }

    @Override
    public List<Map<String, Object>> listByUnionIdList(final Integer unionId, final String enterpriseName) throws Exception{
        if (unionId == null) {
            throw new ParamException(LIST_UNIONID_LIST_ALL, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" m");
                sbSqlSegment.append(" LEFT JOIN t_union_discount d4 on d4.from_bus_id = m.bus_id ")
                        .append(" LEFT JOIN t_union_discount d2 on d2.to_bus_id = m.bus_id ")
                        .append(" LEFT JOIN t_union_apply a on a.union_member_id = m.id ")
                        .append(" LEFT JOIN t_union_apply_info i on i.union_apply_id = a.id ")
                        .append(" WHERE m.del_status=").append(UnionMemberConstant.DEL_STATUS_NO)
                        .append("    AND d4.del_status = ").append(UnionDiscountConstant.DEL_STATUS_NO)
                        .append("    AND d2.del_status = ").append(UnionMemberConstant.DEL_STATUS_NO)
                        .append("    AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("    AND m.union_id = ").append(unionId);
                if (StringUtil.isNotEmpty(enterpriseName)) {
                    sbSqlSegment.append(" AND i.enterprise_name LIKE '%").append(enterpriseName).append("%' ");
                }
                sbSqlSegment.append(" ORDER BY m.is_nuion_owner DESC,m.createtime DESC ");
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append(" m.is_nuion_owner isUnionOwner ")
                .append(", m.id id ")
                .append(", m.bus_id busId ")
                .append(", DATE_FORMAT(m.createtime, '%Y-%m-%d %T') createtime ")
                .append(", d4.discount fromDiscount ")
                .append(", d2.discount toDiscount ")
                .append(", i.sell_divide_proportion sellDivideProportion ")
                .append(", i.enterprise_name enterpriseName ");
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMaps(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
	@Override
	public void applyOutUnion(Integer unionId, Integer busId, String outReason) throws Exception{
        if (unionId == null) {
            throw new ParamException(APPLY_OUT_UNION, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(APPLY_OUT_UNION, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        // （1）判断联盟是否有效
        if(!unionRootService.checkUnionMainValid(unionId)){
            throw new BusinessException(APPLY_OUT_UNION, "", CommonConstant.UNION_OVERDUE_MSG);
        }
        if(StringUtil.isEmpty(outReason)){
            throw new ParamException(APPLY_OUT_UNION, "参数outReason为空", "退盟理由内容不能为空");
        }
        if(StringUtil.getStringLength(outReason) > 20){
            throw new ParamException(APPLY_OUT_UNION, "参数outReason为空", "退盟理由内容不可超过20字");
        }
        // （2）判断当前用户是否是联盟的有效盟员
        UnionMember member = this.getByUnionIdAndBusId(unionId,busId);
        if(!unionRootService.hasUnionMemberAuthority(member)){
            throw new BusinessException(APPLY_OUT_UNION, "", CommonConstant.UNION_MEMBER_NON_AUTHORITY_MSG);
        }
        if(member.getOutStaus() == 1){
            throw new BusinessException(APPLY_OUT_UNION, "", "您已申请退盟，请耐心等待盟主审核");
        }
        // （3）判断当前用户是否已申请退盟
        member.setOutReason(outReason);
        member.setOutStaus(1);
        member.setApplyOutTime(new Date());
        this.updateById(member);
        String memberKey = RedisKeyUtil.getUnionMemberBusIdKey(unionId,busId);
        if(redisCacheUtil.exists(memberKey)){
            redisCacheUtil.set(memberKey,JSON.toJSONString(member));
        }
        //TODO 使用消息队列发送消息
        String nowTime = "applyOut:"+System.currentTimeMillis();
        HashMap<String, Object> redisMap = new HashMap<>();
        redisMap.put("unionId",unionId);
        redisMap.put("busId",busId);
        redisCacheUtil.set(nowTime, JSON.toJSONString(redisMap),60l);
    }

    @Override
    public Map<String, Object> getMemberOutApplyMsgInfo(String redisKey) throws Exception{
        if(StringUtil.isEmpty(redisKey)){
            throw new ParamException(MEMBER_OUT_APPLY_MSG, "参数redisKey为空", "参数redisKey为空");
        }
        Object data = redisCacheUtil.get(redisKey);
        if(data == null){
            throw new ParamException(MEMBER_OUT_APPLY_MSG, "", "redis失效");
        }
        Map<String,Object> result = JSON.parseObject(data.toString(), Map.class);
        Integer unionId = CommonUtil.toInteger(result.get("unionId"));//联盟id
        Integer busId = CommonUtil.toInteger(result.get("busId")); //退盟的商家id
        UnionMain main = unionMainService.getById(unionId);
        UnionApplyInfo mainInfo = this.unionApplyInfoService.getByUnionIdAndBusId(main.getId(), main.getBusId());//盟主
        UnionApplyInfo memberInfo = this.unionApplyInfoService.getByUnionIdAndBusId(main.getId(), busId);//盟员
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("mobiles",StringUtil.isEmpty(mainInfo.getNotifyPhone()) ? mainInfo.getDirectorPhone() : mainInfo.getNotifyPhone());
        param.put("company",PropertiesUtil.getWxmpCompany());
        param.put("busId",main.getBusId());
        param.put("model",CommonConstant.SMS_UNION_MODEL);
        param.put("content","\"" + memberInfo.getEnterpriseName() + "\"" + "申请退出" + "\"" + main.getUnionName() + "\"" + "，请到退盟审核处查看并处理");
        Map<String,Object> obj = new HashMap<String,Object>();
        obj.put("reqdata",param);
        return obj;
    }

    @Override
    public void cancelUnionOwner(Integer id, Integer unionId, Integer busId) throws Exception{
        UnionMember unionMember = null;
        if (unionId == null) {
            throw new ParamException(CANCEL_UNION_OWNER, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(CANCEL_UNION_OWNER, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (id == null) {
            throw new ParamException(CANCEL_UNION_OWNER, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }else {
            unionMember = this.selectById(id);
            if (unionMember == null) {
                throw new ParamException(TRANSFER_UNION_OWNER, "无法通过id获取对象", ExceptionConstant.PARAM_ERROR);
            }
        }
        //1、判断联盟是否有效
        if(!unionRootService.checkUnionMainValid(unionId)){
            throw new BusinessException(TRANSFER_UNION_OWNER, "", CommonConstant.UNION_OVERDUE_MSG);
        }

        //2、判断是否盟主
        if(!unionRootService.isUnionOwner(unionId,busId)){
            throw new BusinessException(TRANSFER_UNION_OWNER, "", CommonConstant.UNION_OWNER_NON_AUTHORITY_MSG);
        }

        //3、判断盟员是否有效
        if(!unionRootService.hasUnionMemberAuthority(unionMember)){
            throw new BusinessException(APPLY_OUT_UNION, "", "该盟");
        }
        UnionMain main = unionMainService.getById(unionId);
    }

}
