package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.basic.UnionApplyConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParameterException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.mapper.basic.UnionMainMapper;
import com.gt.union.service.basic.IUnionApplyService;
import com.gt.union.service.basic.IUnionMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
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
		return unionMainMapper.selectById(unionId);
	}

	@Override
	public Map<String, Object> getUnionMainMemberInfo(Integer busId, Integer unionId) {
		Map<String,Object> data = new HashMap<String,Object>();
		UnionMain main = getUnionMain(unionId);
		data.put("main",main);
		UnionApplyInfo info  = unionApplyService.getUnionApplyInfo(busId,unionId);//本商家的
		data.put("info",info);
		if(!busId.equals(main.getBusId())){
			busId = main.getBusId();
			UnionApplyInfo mainInfo = unionApplyService.getUnionApplyInfo(busId,unionId);
			data.put("mainInfo",mainInfo);//盟主信息
		}
		return data;
	}

	@Override
	public List<UnionMain> getMemberUnionList(Integer busId) {
		List<UnionMain> list = unionMainMapper.selectMemberUnionList(busId);
		for (int i = 0; i < list.size(); i++) {
			UnionMain main = list.get(i);
			if(busId.equals(main.getBusId())){
				UnionMain main1 = list.get(0);
				UnionMain main2 = list.get(i);
				list.set(i, main1);
				list.set(0, main2);
			}
			//TODO 获取商家加入的联盟列表，返回数据 首先判断商家的有效期，再判断联盟的有效期
		}
		return list;
	}

	@Override
	public Map<String, Object> getUnionMainMemberInfo(UnionMain main, Integer busId) {
		return null;
	}
}
