package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.basic.UnionApplyConstant;
import com.gt.union.common.exception.ParameterException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.basic.UnionApply;
import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.mapper.basic.UnionApplyInfoMapper;
import com.gt.union.mapper.basic.UnionApplyMapper;
import com.gt.union.service.basic.IUnionApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    @Autowired
    private UnionApplyInfoMapper unionApplyInfoMapper;

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
                            .append(UnionApplyConstant.BUS_CONFIRM_STATUS_PASS).append(") ")
                            .append("     ) ")
                            .append("     AND a.union_id = " ).append(unionId);
                if (StringUtil.isNotEmpty(enterpriseName)) {
                    sbSqlSegment.append(" AND i.enterprise_name LIKE '%").append(enterpriseName.trim()).append("%' ");
                }
                if (StringUtil.isNotEmpty(directorPhone)) {
                    sbSqlSegment.append(" AND i.director_phone LIKE '%").append(directorPhone.trim()).append("%' ");
                }
                sbSqlSegment.append(" ORDER BY a.createtime DESC ");
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder("");
        sbSqlSelect.append(" a.id id, a.union_id unionId")
                .append(", DATE_FORMAT(a.createtime, '%Y-%m-%d %T') createtime ")
                .append(", a.apply_status applyStatus ")
                .append(", i.enterprise_name enterpriseName ")
                .append(", i.director_name directorName ")
                .append(", i.director_phone directorPhone ")
                .append(", i.director_email directorEmail ")
                .append(", i.apply_reason applyReason ")
                .append(", i2.enterprise_name recommendBusName ");
        wrapper.setSqlSelect(sbSqlSelect.toString());
        return this.selectMapsPage(page, wrapper);
    }



    @Override
    public UnionApplyInfo getUnionApplyInfo(final Integer busId, final Integer unionId) throws Exception{
        if(CommonUtil.isEmpty(busId) || CommonUtil.isEmpty(unionId)){
            throw new ParameterException("参数错误");
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" t1");
                sbSqlSegment.append(" LEFT JOIN t_union_apply_info t2 ON t1.id = t2.union_apply_id ")
                        .append(" WHERE")
                        .append(" t1.union_id = ").append(unionId)
                        .append(" AND t1.apply_bus_id = " ).append(busId)
                        .append(" AND t1.apply_status = " ).append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append(" AND t1.del_status = " ).append(UnionApplyConstant.DEL_STATUS_NO);
                return sbSqlSegment.toString();
            };

        };
        StringBuilder sbSqlSelect = new StringBuilder();
        sbSqlSelect.append(" t2.id , t2.union_apply_id unionApplyId, t2.apply_reason applyReason, t2.enterprise_name enterpriseName, t2.director_name, ")
                .append("t2.director_phone directorPhone, t2.director_email directorEmail, t2.bus_address busAddress, t2.notify_phone notifyPhone, ")
                .append("t2.address_longitude addressLongitude, t2.address_latitude addressLatitude, t2.address_provience_id addressProvienceId, ")
                .append("t2.address_city_id addressCityId, t2.address_district_id addressDistrictId, t2.integral_proportion integralProportion, ")
                .append("t2.is_member_out_advice isMemberOutAdvice");
        wrapper.setSqlSelect(sbSqlSelect.toString());
        Map<String,Object> map = this.selectMap(wrapper);
        UnionApplyInfo info = new UnionApplyInfo();
        if(CommonUtil.isNotEmpty(map)){
            BeanMap beanMap = BeanMap.create(info);
            beanMap.putAll(map);
        }
        return info;
    }

	@Override
	public int getUnionApply(Integer busId, Integer unionId) {
        EntityWrapper<UnionApply> entityWrapper = new EntityWrapper<UnionApply>();
        entityWrapper.eq("union_id",unionId)
                .eq("bus_id",busId)
                .eq("del_status",UnionApplyConstant.DEL_STATUS_NO);
        UnionApply apply = this.selectOne(entityWrapper);
        if(apply == null){
            return 1;
        }
        if(apply.getApplyStatus() == UnionApplyConstant.APPLY_STATUS_UNCHECKED ){
            return 2;//未审核
        }
        if(apply.getApplyStatus() == UnionApplyConstant.APPLY_STATUS_PASS){
            return -2;//盟主审核通过
        }
        if(apply.getApplyStatus() == UnionApplyConstant.APPLY_STATUS_FAIL){
            return -3;//审核不通过
        }
        if(apply.getBusConfirmStatus() == UnionApplyConstant.BUS_CONFIRM_STATUS_APPLY){
            return 3;   //商家申请未审核
        }
        if(apply.getBusConfirmStatus() == UnionApplyConstant.BUS_CONFIRM_STATUS_UNCHECK){
            return 4; //盟员推荐未审核
        }
        if(apply.getBusConfirmStatus() == UnionApplyConstant.BUS_CONFIRM_STATUS_PASS){
            return -13;
        }
        if(apply.getBusConfirmStatus() == UnionApplyConstant.BUS_CONFIRM_STATUS_FAIL){
            return -14;
        }
		return 0;
	}

}
