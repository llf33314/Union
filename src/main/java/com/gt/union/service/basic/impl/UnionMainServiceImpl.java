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
	public List<UnionMain> getMemberUnionList(final Integer busId) {
		Wrapper wrapper = new Wrapper() {
			@Override
			public String getSqlSegment() {
				StringBuilder sbSqlSegment = new StringBuilder(" t1");
				sbSqlSegment.append(" LEFT JOIN t_union_member t2 on t1.id = t2.union_id")
						.append(" where t2.bus_id = ").append(busId)
						.append(" and t2.del_status = ").append(0);
				sbSqlSegment.append(" ORDER BY t2.id asc ");
				return sbSqlSegment.toString();
			};

		};
		StringBuilder sbSqlSelect = new StringBuilder("");
		sbSqlSelect.append("t1.id, t1.createtime, t1.del_status, t1.union_name, t1.bus_id, t1.union_img, t1.join_type, t1.director_phone, ")
				.append("t1.union_illustration, t1.union_wx_group_img, t1.union_sign, t1.union_total_member, t1.union_member_num, ")
				.append("t1.union_level, t1.union_verify_status, t1.is_integral, t1.old_member_charge, t1.black_card_charge, ")
				.append("t1.black_card_price, t1.black_card_term, t1.red_card_opend, t1.red_card_price, t1.red_card_term, ")
				.append("t1.black_card_illustration, t1.red_card_illustration, t1.union_validity ");
		wrapper.setSqlSelect(sbSqlSelect.toString());
		List<UnionMain> list = selectList(wrapper);
		for (int i = 0; i < list.size(); i++) {
			UnionMain main = list.get(i);
			if(busId.equals(main.getBusId())){
				UnionMain main1 = list.get(0);
				UnionMain main2 = list.get(i);
				list.set(i, main1);
				list.set(0, main2);
			}
			//TODO 获取商家加入的联盟列表，返回数据 首先判断盟主商家的有效期，再判断盟主商家的等级，最后根据等级判断联盟的有效期
			Integer mainBusId = main.getBusId();//盟主商家主账号id
			//1、判断商家账号有效期
			//2、盟主商家账号等级
			//3、根据商家账号等级 判断联盟的有效期
		}
		return list;
	}

	@Override
	public Map<String, Object> getUnionMainMemberInfo(UnionMain main, Integer busId) {
		return null;
	}

	@Override
	public void updateUnionMain(UnionMain main, Integer busId) throws Exception{
		//TODO  判断是否该联盟盟主
		//判断该商家是否
	}
}
