package com.gt.union.service.basic.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.UnionApplyConstant;
import com.gt.union.common.constant.basic.UnionMemberConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.mapper.basic.UnionApplyInfoMapper;
import com.gt.union.service.basic.IUnionApplyInfoService;
import com.gt.union.service.basic.IUnionApplyService;
import com.gt.union.service.basic.IUnionMainService;
import com.gt.union.service.basic.IUnionMemberService;
import com.gt.union.service.common.IUnionRootService;
import com.gt.union.vo.basic.UnionApplyInfoVO;
import com.gt.union.vo.basic.UnionApplyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟成员申请信息 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@Service
public class UnionApplyInfoServiceImpl extends ServiceImpl<UnionApplyInfoMapper, UnionApplyInfo> implements IUnionApplyInfoService {
	private static final String LIST_SELLDIVIDEPROPORTION_PAGE = "UnionApplyInfoServiceImpl.listBySellDivideProportionInPage()";
	private static final String LIST_SELLDIVIDEPROPORTION_LIST = "UnionApplyInfoServiceImpl.listBySellDivideProportionInList()";
	private static final String UPDATE_SELLDIVIDEPROPORTION = "UnionApplyInfoServiceImpl.updateBySellDivideProportion()";
	private static final String UPDATE_ID = "UnionApplyInfoServiceImpl.updateById()";

	@Autowired
	private IUnionApplyService unionApplyService;

	@Autowired
	private IUnionMemberService unionMemberService;

	@Autowired
	private IUnionMainService unionMainService;

	@Autowired
    private IUnionRootService unionRootService;

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateById(UnionApplyInfoVO vo, Integer busId) throws Exception {
		if(!unionRootService.checkUnionMainValid(vo.getUnionId())){
			throw new BusinessException(UPDATE_ID, "" , CommonConstant.UNION_OVERDUE_MSG);
		}
		if(!unionRootService.hasUnionMemberAuthority(vo.getUnionId(),busId)){
			throw new BusinessException(UPDATE_ID, "" , CommonConstant.UNION_MEMBER_NON_AUTHORITY_MSG);
		}
		//TODO 更新盟员信息的地址
		UnionApplyInfo info = unionApplyService.getUnionApplyInfo(busId,vo.getUnionId());
		info.setDirectorName(vo.getDirectorName());
		info.setDirectorPhone(vo.getDirectorPphone());
		info.setDirectorEmail(vo.getDirectorEmail());
		info.setNotifyPhone(vo.getNotifyPhone());
		info.setEnterpriseName(vo.getEnterpriseName());
		info.setIntegralProportion(vo.getIntegralProportion());
		info.setBusAddress(vo.getBusAddress());
		info.setAddressLatitude(vo.getAddressLatitude());
		info.setAddressLongitude(vo.getAddressLongitude());
		this.updateById(info);
		redisCacheUtil.set("unionApplyInfo:" + vo.getUnionId() + ":" + busId, JSON.toJSONString(info));
	}

	@Override
	public Page listBySellDivideProportionInPage(Page page, final Integer unionId) throws Exception {
		if (page == null) {
			throw new ParamException(LIST_SELLDIVIDEPROPORTION_PAGE, "参数page为空", ExceptionConstant.PARAM_ERROR);
		}
		if (unionId == null) {
			throw new ParamException(LIST_SELLDIVIDEPROPORTION_PAGE, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
		}
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" i");
                sbSqlSegment.append(" LEFT JOIN t_union_apply a ON a.id = i.union_apply_id ")
                        .append(" LEFT JOIN t_union_member m ON m.id = a.union_member_id ")
                        .append(" WHERE a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("    AND m.del_status = ").append(UnionMemberConstant.DEL_STATUS_NO)
                        .append("    AND (m.out_staus = ").append(UnionMemberConstant.OUT_STATUS_NORMAL)
                        .append("        OR m.out_staus = ").append(UnionMemberConstant.OUT_STATUS_UNCHECKED)
                        .append("        ) ")
                        .append("    AND a.union_id = ").append(unionId)
                        .append(" ORDER BY m.is_nuion_owner DESC ");
                return sbSqlSegment.toString();
            }
        };
		wrapper.setSqlSelect(" i.enterprise_name enterpriseName, i.sell_divide_proportion sellDivideProportion, m.is_nuion_owner isUnionOwner ");

		return this.selectMapsPage(page, wrapper);
	}

	@Override
	public List<Map<String,Object>> listBySellDivideProportionInList(final Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(LIST_SELLDIVIDEPROPORTION_LIST, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" i");
                sbSqlSegment.append(" LEFT JOIN t_union_apply a ON a.id = i.union_apply_id ")
                        .append(" LEFT JOIN t_union_member m ON m.id = a.union_member_id ")
                        .append(" WHERE a.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
                        .append("    AND a.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS)
                        .append("    AND m.del_status = ").append(UnionMemberConstant.DEL_STATUS_NO)
                        .append("    AND (m.out_staus = ").append(UnionMemberConstant.OUT_STATUS_NORMAL)
                        .append("        OR m.out_staus = ").append(UnionMemberConstant.OUT_STATUS_UNCHECKED)
                        .append("        ) ")
                        .append("    AND a.union_id = ").append(unionId)
                        .append(" ORDER BY m.is_nuion_owner DESC ");
                return sbSqlSegment.toString();
            }
        };
        wrapper.setSqlSelect(" i.id infoId, i.enterprise_name enterpriseName, i.sell_divide_proportion sellDivideProportion, m.is_nuion_owner isUnionOwner ");

        return this.selectMaps(wrapper);
	}

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateBySellDivideProportion(List<UnionApplyInfo> unionApplyInfoList) throws Exception {
		//TODO 售卡分成比例权限判断
	    if (ListUtil.isNotEmpty(unionApplyInfoList)) {
	        List<UnionApplyInfo> unionApplyInfos = new LinkedList<>();
            BigDecimal sellDivideProportionSum = BigDecimal.valueOf(0.0);
	        for (int i = 0, size = unionApplyInfoList.size(); i < size; i++) {
	            UnionApplyInfo fromUnionApplyInfo = unionApplyInfoList.get(i);
	            if (fromUnionApplyInfo.getId() == null) {
	                throw new BusinessException(UPDATE_SELLDIVIDEPROPORTION, "更新列表中存在id为空的对象", ExceptionConstant.OPERATE_FAIL);
                }
                if (fromUnionApplyInfo.getSellDivideProportion() == null) {
                    throw new BusinessException(UPDATE_SELLDIVIDEPROPORTION, "更新列表中存在比例为空的对象", ExceptionConstant.OPERATE_FAIL);
                }
                sellDivideProportionSum = BigDecimalUtil.add(sellDivideProportionSum, fromUnionApplyInfo.getSellDivideProportion());
                UnionApplyInfo toUnionApplyInfo = new UnionApplyInfo();
	            toUnionApplyInfo.setId(fromUnionApplyInfo.getId());
	            toUnionApplyInfo.setSellDivideProportion(fromUnionApplyInfo.getSellDivideProportion());
	            unionApplyInfos.add(toUnionApplyInfo);
            }
            if (BigDecimalUtil.subtract(sellDivideProportionSum, BigDecimal.valueOf(100.0)).doubleValue() != 0.0) {
                throw new BusinessException(UPDATE_SELLDIVIDEPROPORTION, "更新列表中的比例之和必须等于100", ExceptionConstant.OPERATE_FAIL);
            }
	        this.updateBatchById(unionApplyInfos);
        }
    }

	@Override
	public Map<String, Object> getById(Integer id, Integer unionId, Integer busId) throws Exception {
		//TODO 判断是否有权限
        // this.unionRootService.hasUnionMemberAuthority(unionId, busId);
		UnionMain main = unionMainService.getById(unionId);
		Map<String,Object> data = new HashMap<String,Object>();
		UnionApplyInfo info = unionApplyService.getUnionApplyInfo(busId,unionId);
		data.put("enterpriseName",info.getEnterpriseName());
		data.put("directorName",info.getDirectorName());
		data.put("directorPhone",info.getDirectorPhone());
		data.put("notifyPhone",info.getNotifyPhone());
		data.put("directorEmail",info.getDirectorEmail());
		data.put("busAddress",info.getBusAddress());
		data.put("integralProportion",info.getIntegralProportion());
		data.put("isIntegralProportion",main.getIsIntegral());
		data.put("address_longitude",info.getAddressLongitude());
		data.put("address_latitude",info.getAddressLatitude());
		//TODO 根据id获取地址名称
		data.put("addressProvience","省");
		data.put("addressCity","市");
		data.put("addressDistrict","区");
		return data;
	}

	@Override
	public UnionApplyInfo saveUnionApplyInfo(UnionApplyVO vo, Integer applyId) {
		UnionApplyInfo info = new UnionApplyInfo();
		info.setUnionApplyId(applyId);
		info.setApplyReason(vo.getApplyReason());
		info.setEnterpriseName(vo.getEnterpriseName());
		info.setDirectorEmail(vo.getDirectorEmail());
		info.setDirectorPhone(vo.getDirectorPhone());
		info.setDirectorName(vo.getDirectorName());
		this.insert(info);
		return info;
	}
}
