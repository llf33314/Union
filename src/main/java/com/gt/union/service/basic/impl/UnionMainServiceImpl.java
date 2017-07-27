package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParameterException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.mapper.basic.UnionMainMapper;
import com.gt.union.service.basic.IUnionApplyService;
import com.gt.union.service.basic.IUnionMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 联盟主表 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
@Service
public class UnionMainServiceImpl extends ServiceImpl<UnionMainMapper, UnionMain> implements IUnionMainService {

	@Autowired
	private UnionMainMapper unionMainMapper;

	@Autowired
	private IUnionApplyService unionApplyService;

	public UnionMain getUnionMain(Integer unionId){
		try{
			UnionMain unionMain = unionMainMapper.selectById(unionId);
			return unionMain;
		}catch (Exception e){
			return null;
		}
	}

	@Override
	public Map<String, Object> getUnionMainInfo(Integer busId, Integer unionId) {
		Map<String,Object> data = new HashMap<String,Object>();
		UnionMain main = getUnionMain(unionId);
		data.put("main",main);
		if(CommonUtil.isEmpty(main)){
			data.put("info",null);
			return data;
		}
		UnionApplyInfo info  = unionApplyService.getUnionApplyInfo(busId,unionId);//本商家的
		data.put("info",info);
		if(!busId.equals(main.getBusId())){
			busId = main.getBusId();
			UnionApplyInfo mainInfo = unionApplyService.getUnionApplyInfo(busId,unionId);
			data.put("mainInfo",mainInfo);//盟主信息
		}
		return data;
	}
}
