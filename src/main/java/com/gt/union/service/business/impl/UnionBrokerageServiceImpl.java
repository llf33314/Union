package com.gt.union.service.business.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.UnionApplyConstant;
import com.gt.union.common.constant.business.UnionBrokerageConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.DateUtil;
import com.gt.union.entity.business.UnionBrokerage;
import com.gt.union.mapper.business.UnionBrokerageMapper;
import com.gt.union.service.business.IUnionBrokerageService;
import com.gt.union.service.common.IUnionRootService;
import com.gt.union.vo.business.UnionBrokerageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 联盟商家佣金比率 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-24
 */
@Service
public class UnionBrokerageServiceImpl extends ServiceImpl<UnionBrokerageMapper, UnionBrokerage> implements IUnionBrokerageService {
    private static final String UPDATE_OR_SAVE = "UnionBrokerageServiceImpl.updateOrSave()";
	private static final String GET_UNIONID_FROMBUSID_TOBUSID = "UnionBrokerageServiceImpl.getByUnionIdAndFromBusIdAndToBusId()";
	private static final String LISTMAP_UNIONID_FROMBUSID = "UnionBrokerageServiceImpl.listMapByUnionIdAndFromBusId()";

	@Autowired
    private IUnionRootService unionRootService;

    @Override
    public void updateOrSave(UnionBrokerageVO unionBrokerageVO, Integer fromBusId) throws Exception {
        if (unionBrokerageVO == null) {
            throw new ParamException(UPDATE_OR_SAVE, "参数unionBrokerageVO为空", ExceptionConstant.PARAM_ERROR);
        }
        if (fromBusId == null) {
            throw new ParamException(UPDATE_OR_SAVE, "参数fromBusId为空", ExceptionConstant.PARAM_ERROR);
        }

        // （1）判断商机佣金比例被设置方是否有效
        if (!this.unionRootService.checkBusUserValid(unionBrokerageVO.getToBusId())) {
            throw new BusinessException(UPDATE_OR_SAVE, "", "商机佣金比例被设置方已无效");
        }

        // （2）判断联盟是否有效
        if (!this.unionRootService.checkUnionMainValid(unionBrokerageVO.getUnionId())) {
            throw new BusinessException(UPDATE_OR_SAVE, "", "该商机佣金比例设置所在的联盟已无效");
        }

        // （3）判断商机佣金比例被设置方是否是联盟的有效盟员
        if (!this.unionRootService.hasUnionMemberAuthority(unionBrokerageVO.getUnionId(), unionBrokerageVO.getToBusId())) {
            throw new BusinessException(UPDATE_OR_SAVE, "", "商机佣金比例被设置方不是联盟的有效成员");
        }

        // （4）判断商机佣金比例设置方是否是联盟的有效联盟
        if (!this.unionRootService.hasUnionMemberAuthority(unionBrokerageVO.getUnionId(), fromBusId)) {
            throw new BusinessException(UPDATE_OR_SAVE, "", "商机佣金比例设置方不是联盟的有效成员");
        }

        if (unionBrokerageVO.getId() == null) { //新增
            UnionBrokerage insertUnionBrokerage = new UnionBrokerage();
            insertUnionBrokerage.setDelStatus(UnionBrokerageConstant.DEL_STATUS_NO);
            insertUnionBrokerage.setCreatetime(DateUtil.getCurrentDate());
            insertUnionBrokerage.setUnionId(unionBrokerageVO.getUnionId());
            insertUnionBrokerage.setFromBusId(fromBusId);
            insertUnionBrokerage.setToBusId(unionBrokerageVO.getToBusId());
            insertUnionBrokerage.setBrokerageRatio(unionBrokerageVO.getBrokerageRatio());
            this.insert(insertUnionBrokerage);
        } else { //更新
            UnionBrokerage updateUnionBrokerage = new UnionBrokerage();
            updateUnionBrokerage.setId(unionBrokerageVO.getId());
            updateUnionBrokerage.setBrokerageRatio(unionBrokerageVO.getBrokerageRatio());
            updateUnionBrokerage.setModifytime(DateUtil.getCurrentDate());
            this.updateById(updateUnionBrokerage);
        }
    }

    @Override
	public UnionBrokerage getByUnionIdAndFromBusIdAndToBusId(Integer unionId, Integer fromBusId, Integer toBusId) throws Exception {
		if (unionId == null) {
			throw new ParamException(GET_UNIONID_FROMBUSID_TOBUSID, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
		}
		if (fromBusId == null) {
		    throw new ParamException(GET_UNIONID_FROMBUSID_TOBUSID, "参数fromBusId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (toBusId == null) {
		    throw new ParamException(GET_UNIONID_FROMBUSID_TOBUSID, "参数toBusId为空", ExceptionConstant.PARAM_ERROR);
        }

        EntityWrapper entityWrapper = new EntityWrapper();
		entityWrapper.eq("del_status", UnionBrokerageConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("from_bus_id", fromBusId)
                .eq("to_bus_id", toBusId);
		return this.selectOne(entityWrapper);
	}

	@Override
	public Page listMapByUnionIdAndFromBusId(Page page, final Integer unionId, final Integer fromBusId) throws Exception {
	    if (page == null) {
	        throw new ParamException(LISTMAP_UNIONID_FROMBUSID, "参数page为空", ExceptionConstant.PARAM_ERROR);
        }
        if (unionId == null) {
	        throw new ParamException(LISTMAP_UNIONID_FROMBUSID, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (fromBusId == null) {
	        throw new ParamException(LISTMAP_UNIONID_FROMBUSID, "参数fromBusId为空", ExceptionConstant.PARAM_ERROR);
        }

        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" t1 ");
                sbSqlSegment.append(" LEFT JOIN t_union_brokerage t2 ON t2.union_id = t1.union_id ")
                        .append("        AND t2.from_bus_id = t1.to_bus_id ")
                        .append("        AND t2.to_bus_id = t1.from_bus_id ")
                        .append("    LEFT JOIN t_union_apply a ON a.apply_bus_id = t1.to_bus_id ")
                        .append("    LEFT JOIN t_union_apply_info i ON i.union_apply_id = a.id ")
                        .append("    WHERE t1.del_status = ").append(UnionBrokerageConstant.DEL_STATUS_NO)
                        .append("        AND t1.union_id = ").append(unionId)
                        .append("        AND t1.from_bus_id = ").append(fromBusId)
                        .append("        AND t2.del_status = ").append(UnionBrokerageConstant.DEL_STATUS_NO)
                        .append("        AND a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("        AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS);

                return sbSqlSegment.toString();
            }
        };

	    StringBuilder sbSelect = new StringBuilder("");
	    sbSelect.append(" t1.id id ")
                .append(" ,t1.to_bus_id toBusId ")
                .append(" ,i.enterprise_name enterpriseName ")
                .append(" ,t1.brokerage_ratio brokerageRatioFromMe ")
                .append(" ,t2.brokerage_ratio brokerageRationToMe ");
	    wrapper.setSqlSelect(sbSelect.toString());
		return this.selectMapsPage(page, wrapper);
	}
}
