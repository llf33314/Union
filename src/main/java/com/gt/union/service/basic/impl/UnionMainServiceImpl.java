package com.gt.union.service.basic.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParameterException;
import com.gt.union.common.response.GTJsonResult;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.DateTimeKit;
import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.common.BusUser;
import com.gt.union.mapper.basic.UnionMainMapper;
import com.gt.union.service.basic.IUnionApplyService;
import com.gt.union.service.basic.IUnionMainService;
import com.gt.union.service.basic.IUnionMemberService;
import com.gt.union.service.brokerage.IUnionBrokerageWithdrawalsRecordService;
import com.gt.union.service.business.IUnionBrokeragePayRecordService;
import com.gt.union.service.card.IUnionBusMemberCardService;
import com.gt.union.service.card.IUnionCardDivideRecordService;
import com.gt.union.vo.basic.UnionMainInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
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
	private IUnionApplyService unionApplyService;

	@Autowired
	private IUnionMemberService unionMemberService;

	@Autowired
	private IUnionBusMemberCardService unionBusMemberCardService;

	@Autowired
	private IUnionCardDivideRecordService unionCardDivideRecordService;

	@Autowired
	private IUnionBrokeragePayRecordService unionBrokeragePayRecordService;

	@Autowired
	private IUnionBrokerageWithdrawalsRecordService unionBrokerageWithdrawalsRecordService;

	public UnionMain getUnionMain(Integer unionId){
		EntityWrapper<UnionMain> entityWrapper = new EntityWrapper<UnionMain>();
		entityWrapper.eq("id",unionId);
		entityWrapper.eq("del_status",0);
		entityWrapper.eq("union_verify_status",1);
		UnionMain main = this.selectOne(entityWrapper);
		return main;
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
		sbSqlSelect.append("t1.id, t1.createtime, t1.del_status delStatus, t1.union_name unionName, ")
				.append("t1.bus_id busId, t1.union_img unionImg, t1.join_type joinType, t1.director_phone directorPhone,")
				.append("t1.union_illustration unionIllustration, t1.union_wx_group_img unionWxGroupImg, t1.union_sign unionSign, ")
				.append("t1.union_total_member uniontotalMember, t1.union_member_num unionMemberNum, t1.union_level unionLevel, ")
				.append("t1.union_verify_status unionVerifyStatus, t1.is_integral isIntegral, t1.old_member_charge oldMemberCharge, ")
				.append("t1.black_card_charge blackCardCcharge, t1.black_card_price blackCardPrice, t1.black_card_term blackCardTerm, ")
				.append(" t1.red_card_opend redCardOpend, t1.red_card_price redCardPrice, t1.red_card_term redCardTerm, ")
				.append("t1.black_card_illustration blackCardIllustration, t1.red_card_illustration redCardIllustration, t1.union_validity unionValidity");
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
	public void updateUnionMain(UnionMain main, Integer busId) throws Exception{
		if(CommonUtil.isEmpty(main.getId())){
			throw new ParameterException("参数错误");
		}
		//TODO  判断是否该联盟盟主
		boolean isUnionOwner =  unionMemberService.isUnionOwner(main.getId(),busId);
		if(!isUnionOwner){
			//判断联盟是否有效
			throw new BusinessException("不是该盟盟主");
		}
		//判断联盟信息
		if(CommonUtil.isEmpty(main.getUnionName())){
			throw new ParameterException("联盟名称不能为空");
		}
		this.updateById(main);
	}

	//TODO 创建联盟
	@Override
	public String getCreateUnionInfo(BusUser user) throws Exception{
		Map<String,Object> data = new HashMap<String,Object>();
		int type = 1;
		if(user.getPid() != null && user.getPid() != 0){
			return GTJsonResult.instanceErrorMsg("请使用主账号创建").toString();
		}
		if(getCreateUnion(user.getId()) != null){
			//1、判断商家等级是否需要收费创建联盟

			//2、如果需收费，判断联盟是否过有效期
			return GTJsonResult.instanceErrorMsg("已创建联盟").toString();
		}
		//判断商家账号是否有创建联盟的权限
		data.put("type",type);
		return GTJsonResult.instanceSuccessMsg(data,null,"去创建联盟").toString();
	}

	@Override
	public void saveCreateUnion(UnionMainInfoVO vo) {

	}

	@Override
	public Map<String, Object> getUnionMainMemberInfo(Integer busId) throws Exception{
		return unionMainMemberInfo(null,busId);
	}

	@Override
	public Map<String, Object> getUnionMainMemberInfo(Integer busId, Integer id) throws Exception{
		UnionMain main = getUnionMain(id);
		return unionMainMemberInfo(main,busId);

	}

	Map<String,Object> unionMainMemberInfo(UnionMain main, Integer busId) throws Exception{
		List<UnionMain> list = getMemberUnionList(busId);
		Map<String,Object> data = new HashMap<String,Object>();
		List<UnionMain> createUnions = new ArrayList<UnionMain>();
		List<UnionMain> joinUnions = new ArrayList<UnionMain>();
		//是否是该盟盟主
		int isUnionOwner = 1;
		if(list.size() > 0){
			if(CommonUtil.isEmpty(main)){
				main = list.get(0);
			}
			data.put("unionName",main.getUnionName());
			data.put("unionIllustration",main.getUnionIllustration());
			data.put("createtime", DateTimeKit.format(main.getCreatetime(),DateTimeKit.DEFAULT_DATETIME_FORMAT));
			data.put("unionMemberNum",main.getUnionMemberNum());
			data.put("unionTotalMember",main.getUnionTotalMember());
			data.put("surplusMemberNum",main.getUnionTotalMember() - main.getUnionMemberNum());
			UnionApplyInfo info  = unionApplyService.getUnionApplyInfo(busId,main.getId());//本商家的
			data.put("enterpriseName",info.getEnterpriseName());
			data.put("ownerEnterpriseName",info.getEnterpriseName());
			if(!busId.equals(main.getBusId())){//不是盟主
				busId = main.getBusId();
				UnionApplyInfo mainInfo = unionApplyService.getUnionApplyInfo(busId,main.getId());
				data.put("ownerEnterpriseName",mainInfo.getEnterpriseName());
				isUnionOwner = 0;
			}
			if(CommonUtil.isNotEmpty(main.getIsIntegral()) && main.getIsIntegral() == 1){
				//查询联盟积分
				double integral = unionBusMemberCardService.getUnionMemberIntegral(main.getId());
				data.put("integral",integral);
			}
			double divideSum = unionCardDivideRecordService.getUnionCardDivideRecordSum(busId,main.getId());//收卡分成收入总和
			double brokerageSum = unionBrokeragePayRecordService.getBrokeragePayRecordSum(busId,main.getId());//佣金收入总和
			double withdrawalsSum = unionBrokerageWithdrawalsRecordService.getUnionBrokerageWithdrawalsSum(busId,main.getId());//提现总和
			//查询该联盟可提现佣金总和
			double ableWithDrawalsSum = new BigDecimal(divideSum).add(new BigDecimal(brokerageSum)).subtract(new BigDecimal(withdrawalsSum)).doubleValue();
			data.put("ableWithDrawalsSum",ableWithDrawalsSum);
			for(UnionMain unionMain : list){
				if(unionMain.getBusId().equals(busId)){
					createUnions.add(unionMain);
				}else {
					joinUnions.add(unionMain);
				}
			}
		}else{
			isUnionOwner = 0;
		}
		data.put("createUnions",createUnions);
		data.put("joinUnions",joinUnions);
		data.put("isUnionOwner",isUnionOwner);
		return data;
	}

	@Override
	public void isUnionValid(UnionMain main) {
		//首先判断盟主的有效期
		//判断盟主的权限
		//判断联盟的有效期
	}

	@Override
	public void isUnionValid(Integer unionId) {
		UnionMain main = getUnionMain(unionId);
		isUnionValid(main);
	}

	/**
	 * 根据商家id获取他创建的联盟
	 * @param busId
	 * @return
	 */
	UnionMain getCreateUnion(Integer busId){
		EntityWrapper<UnionMain> entityWrapper = new EntityWrapper<UnionMain>();
		entityWrapper.eq("bus_id",busId);
		entityWrapper.eq("del_status",0);
		entityWrapper.eq("union_verify_status",1);
		UnionMain main = this.selectOne(entityWrapper);
		return main;
	}


}
