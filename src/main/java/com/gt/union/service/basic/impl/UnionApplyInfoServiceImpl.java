package com.gt.union.service.basic.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.client.address.AddressService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.UnionApplyConstant;
import com.gt.union.common.constant.basic.UnionMemberConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.basic.vo.UnionApplyInfoVO;
import com.gt.union.entity.basic.vo.UnionApplyVO;
import com.gt.union.mapper.basic.UnionApplyInfoMapper;
import com.gt.union.service.basic.IUnionApplyInfoService;
import com.gt.union.service.basic.IUnionMainService;
import com.gt.union.service.common.IUnionRootService;
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
	private static final String GET_UNIONID_BUSID = "UnionApplyInfoServiceImpl.getByUnionIdAndBusId()";
	private static final String GET_MAP_BY_ID = "UnionApplyInfoServiceImpl.getMapById()";

	@Autowired
	private IUnionMainService unionMainService;

	@Autowired
    private IUnionRootService unionRootService;

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Autowired
	private AddressService addressService;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateById(UnionApplyInfoVO vo, Integer busId) throws Exception {
		if(!unionRootService.checkUnionMainValid(vo.getUnionId())){
			throw new BusinessException(UPDATE_ID, "" , CommonConstant.UNION_OVERDUE_MSG);
		}
		if(!unionRootService.hasUnionMemberAuthority(vo.getUnionId(),busId)){
			throw new BusinessException(UPDATE_ID, "" , CommonConstant.UNION_MEMBER_NON_AUTHORITY_MSG);
		}
		UnionApplyInfo info = new UnionApplyInfo();
		info.setId(vo.getId());
		info.setDirectorName(vo.getDirectorName());
		info.setDirectorPhone(vo.getDirectorPhone());
		info.setDirectorEmail(vo.getDirectorEmail());
		info.setNotifyPhone(vo.getNotifyPhone());
		info.setEnterpriseName(vo.getEnterpriseName());
		info.setIntegralProportion(vo.getIntegralProportion());
		info.setBusAddress(vo.getBusAddress());
		info.setAddressLatitude(vo.getAddressLatitude());
		info.setAddressLongitude(vo.getAddressLongitude());
		String[] p = vo.getProvienceCode().split(",");
		String provience_name = p[0];
		String provience = p[1];
		String[] c = vo.getCityCode().split(",");
		String city_name = c[1];
		String city = c[1];
		String[] d = vo.getDistrictCode().split(",");
		String district_name = d[0];
		String district = d[1];
		String city_codes = provience + "," + city + "," + district;
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("reqdata",city_codes);
		List<Map> list = addressService.getByCityCode(param);
		for(Map map : list){
			if(map.get("city_level").toString().equals(CommonConstant.PROVIENCE_LEVEL) && map.get("city_name").toString().equals(provience_name)){
				info.setAddressProvienceId(CommonUtil.toInteger(map.get("id")));
			}
			if(map.get("city_level").toString().equals(CommonConstant.CITY_LEVEL) && map.get("city_name").toString().equals(city_name)){
				info.setAddressCityId(CommonUtil.toInteger(map.get("id")));
			}
			if(map.get("city_level").toString().equals(CommonConstant.DISTRICT_LEVEL) && map.get("city_name").toString().equals(district_name)){
				info.setAddressDistrictId(CommonUtil.toInteger(map.get("id")));
			}
		}
		this.updateById(info);
		String infoKey = RedisKeyUtil.getUnionApplyInfoKey(vo.getUnionId(),busId);
		redisCacheUtil.remove(infoKey);
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
	public Map<String, Object> getMapById(Integer id, Integer unionId, Integer busId) throws Exception {
		UnionMain main = unionMainService.getById(unionId);
		if(!this.unionRootService.checkUnionMainValid(main)){
			throw new BusinessException(GET_MAP_BY_ID, "", CommonConstant.UNION_OVERDUE_MSG);
		}
		if(!unionRootService.hasUnionMemberAuthority(unionId, busId)){
			throw new BusinessException(GET_MAP_BY_ID, "", CommonConstant.UNION_MEMBER_NON_AUTHORITY_MSG);
		}
		Map<String,Object> data = new HashMap<String,Object>();
		UnionApplyInfo info = this.getByUnionIdAndBusId(unionId, busId);
		data.put("enterpriseName",info.getEnterpriseName());
		data.put("directorName",info.getDirectorName());
		data.put("directorPhone",info.getDirectorPhone());
		data.put("notifyPhone",info.getNotifyPhone());
		data.put("directorEmail",info.getDirectorEmail());
		data.put("integralProportion",info.getIntegralProportion());
		data.put("isIntegralProportion",main.getIsIntegral());
		if(CommonUtil.isNotEmpty(info.getAddressProvienceId())){
			String ids = info.getAddressProvienceId() + "," + info.getAddressCityId() + "," + info.getAddressDistrictId();
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("reqdata",ids);
			List<Map> list = addressService.getByIds(param);
			if(ListUtil.isNotEmpty(list)){
				data.put("busAddress",info.getBusAddress());
				data.put("addressLongitude",info.getAddressLongitude());
				data.put("addressLatitude",info.getAddressLatitude());
				for(Map map : list){
					if(map.get("city_level").toString().equals(CommonConstant.PROVIENCE_LEVEL)){
						data.put("addressProvience",map.get("city_name").toString() + "," + map.get("city_code").toString());
					}
					if(map.get("city_level").toString().equals(CommonConstant.CITY_LEVEL)){
						data.put("addressCity",map.get("city_name").toString() + "," + map.get("city_code").toString());
					}
					if(map.get("city_level").toString().equals(CommonConstant.DISTRICT_LEVEL)){
						data.put("addressDistrict",map.get("city_name").toString() + "," + map.get("city_code").toString());
					}
				}
			}
		}
		data.put("infoId",info.getId());
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

	@Override
	public UnionApplyInfo getByUnionIdAndBusId(Integer unionId, Integer busId) throws Exception {
		if (unionId == null) {
		    throw new ParamException(GET_UNIONID_BUSID, "参数unionId为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
		    throw new ParamException(GET_UNIONID_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

		String unionApplyInfoKey = RedisKeyUtil.getUnionApplyInfoKey(unionId, busId);
		// （1）判断缓存中是否存在，如是，则直接返回
		if ( redisCacheUtil.exists( unionApplyInfoKey) ) {
			Object obj = redisCacheUtil.get(unionApplyInfoKey);
            return JSON.parseObject(obj.toString(),UnionApplyInfo.class);
		}

		// （2）否则，查找数据库
		EntityWrapper entityWrapper = new EntityWrapper<UnionApplyInfo>();
		StringBuilder sqlExists = new StringBuilder();
		sqlExists.append("SELECT t1.id, t1.union_id, t1.apply_bus_id, t1.del_status, t1.apply_status FROM t_union_apply t1 WHERE t1.id = t_union_apply_info.union_apply_id")
				.append(" AND t1.union_id = ").append(unionId)
				.append(" AND t1.apply_bus_id = ").append(busId)
				.append(" AND t1.del_status = ").append(UnionApplyConstant.DEL_STATUS_NO)
				.append(" AND t1.apply_status = ").append(UnionApplyConstant.APPLY_STATUS_PASS);
		entityWrapper.exists(sqlExists.toString());
		UnionApplyInfo unionApplyInfo = this.selectOne(entityWrapper);
		// （3）如果数据库查处的值不为空，则写入缓存中
		if(unionApplyInfo != null){
			redisCacheUtil.set(unionApplyInfoKey, JSON.toJSONString(unionApplyInfo));
		}

		return unionApplyInfo;
	}
}
