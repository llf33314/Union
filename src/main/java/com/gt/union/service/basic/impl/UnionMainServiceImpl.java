package com.gt.union.service.basic.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.RedisContant;
import com.gt.union.common.constant.basic.UnionMainConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.DateTimeKit;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.mapper.basic.UnionMainMapper;
import com.gt.union.service.basic.IUnionApplyService;
import com.gt.union.service.basic.IUnionDiscountService;
import com.gt.union.service.basic.IUnionMainService;
import com.gt.union.service.brokerage.IUnionBrokerageWithdrawalsRecordService;
import com.gt.union.service.card.IUnionBusMemberCardService;
import com.gt.union.service.common.IUnionRootService;
import com.gt.union.vo.basic.UnionMainCreateInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	private static final String INDEX_BUSID = "UnionMainServiceImpl.indexByBusId()";
	private static final String INDEX_ID = "UnionMainServiceImpl.indexById()";
	private static final String LIST_MY_UNION = "UnionMainServiceImpl.listMyUnion()";
	private static final String LIST = "UnionMainServiceImpl.list()";
	private static final String UPDATE_ID = "UnionMainServiceImpl.updateById()";
	private static final String GET_ID = "UnionMainServiceImpl.getById()";
	private static final String INSTANCE = "UnionMainServiceImpl.instance()";
	private static final String SAVE = "UnionMainServiceImpl.save()";
	private static final String GET_BUSID = "UnionMainServiceImpl.getByBusId()";

	@Autowired
	private IUnionRootService unionRootService;

	@Autowired
	private IUnionApplyService unionApplyService;

	@Autowired
	private IUnionBusMemberCardService unionBusMemberCardService;

	@Autowired
	private IUnionBrokerageWithdrawalsRecordService unionBrokerageWithdrawalsRecordService;

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Autowired
	private IUnionDiscountService unionDiscountService;

	@Override
	public Map<String, Object> indexByBusId(Integer busId) throws Exception {
		Map<String,Object> data = new HashMap<String,Object>();
	    if (busId == null) {
	        throw new ParamException(INDEX_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        UnionMain createUnion = this.getByBusId(busId);//我创建的联盟
	    UnionMain currentUnion = createUnion;
		List<UnionMain> joins = this.listJoinUnion(busId);
		if(CommonUtil.isEmpty(createUnion)){
			if(joins.size() > 0){
				currentUnion = joins.get(0);
			}
		}
		data.put("createUnion",createUnion);
		data.put("joinUnions", joins);
		return indexUnionMain(currentUnion, busId, data);
	}

	@Override
	public Map<String, Object> indexById(Integer id, Integer busId) throws Exception {
		Map<String,Object> data = new HashMap<String,Object>();
	    if (id == null) {
	        throw new ParamException(INDEX_ID, "参数id为空", ExceptionConstant.PARAM_ERROR);
        }
		if (busId == null) {
			throw new ParamException(INDEX_ID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
		}
		UnionMain createUnion = this.getByBusId(busId);
        UnionMain main = this.getById(id);
		data.put("createUnion",createUnion);
		List<UnionMain> joins = this.listJoinUnion(busId);
		data.put("joinUnions", joins);
		return indexUnionMain(main, busId, data);
	}

	private Map<String, Object> indexUnionMain(UnionMain currentUnion, Integer busId, Map<String,Object> data )throws Exception{
		if(CommonUtil.isEmpty(currentUnion)){
			return data;
		}
		//是否是该盟盟主
		int isUnionOwner = 1;
		data.put("unionId",currentUnion.getId());
		data.put("isIntegral",CommonUtil.isEmpty(currentUnion.getIsIntegral())? 0 : currentUnion.getIsIntegral());
		data.put("unionName",currentUnion.getUnionName());
		data.put("unionIllustration",currentUnion.getUnionIllustration());
		data.put("createtime", DateTimeKit.format(currentUnion.getCreatetime(),DateTimeKit.DEFAULT_DATETIME_FORMAT));
		data.put("unionMemberNum",currentUnion.getUnionMemberNum());
		data.put("unionTotalMember",currentUnion.getUnionTotalMember());
		data.put("surplusMemberNum",currentUnion.getUnionTotalMember() - currentUnion.getUnionMemberNum());
		data.put("isRedCardOpend",CommonUtil.isEmpty(currentUnion.getRedCardOpend()) ? 0 : currentUnion.getRedCardOpend());
		UnionApplyInfo info  = unionApplyService.getUnionApplyInfo(busId,currentUnion.getId());//本商家的
		data.put("enterpriseName",info.getEnterpriseName());
		data.put("ownerEnterpriseName",info.getEnterpriseName());
		data.put("infoId",info.getId());
		if(!busId.equals(currentUnion.getBusId())){//不是盟主
			busId = currentUnion.getBusId();
			UnionApplyInfo mainInfo = unionApplyService.getUnionApplyInfo(busId,currentUnion.getId());
			data.put("ownerEnterpriseName",mainInfo.getEnterpriseName());
			isUnionOwner = 0;
		}
		if(CommonUtil.isNotEmpty(currentUnion.getIsIntegral()) && currentUnion.getIsIntegral() == 1){
			//查询联盟积分
			double integral = unionBusMemberCardService.getUnionMemberIntegral(currentUnion.getId());
			data.put("integral",integral);
		}
		double ableWithDrawalsSum = unionBrokerageWithdrawalsRecordService.getUnionBrokerageAbleToWithdrawalsSum(busId,currentUnion.getId());//联盟可提现佣金总和
		data.put("ableWithDrawalsSum",ableWithDrawalsSum);
		data.put("isUnionOwner",isUnionOwner);
		return data;
	}

	@Override
	public List<UnionMain> listMyUnion(Integer busId) throws Exception {
	    if (busId == null) {
	        throw new ParamException(LIST_MY_UNION, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
		List<UnionMain> list = new ArrayList<UnionMain>();
		UnionMain unionMain = getByBusId(busId);
		if(CommonUtil.isNotEmpty(unionMain)){
			list.add(unionMain);
		}
		List<UnionMain> joinUnions = listJoinUnion(busId);
		if(joinUnions.size() > 0){
			list.addAll(joinUnions);
		}
		return list;
	}


	/**
	 * 我加入的联盟
	 * @param busId
	 * @return
	 */
	private List<UnionMain> listJoinUnion(final Integer busId){
		Wrapper wrapper = new Wrapper() {
			@Override
			public String getSqlSegment() {
				StringBuilder sbSqlSegment = new StringBuilder(" t1");
				sbSqlSegment.append(" LEFT JOIN t_union_member t2 ON t1.id = t2.union_id")
						.append(" WHERE t2.bus_id = ").append(busId)
						.append(" AND t2.del_status = ").append(0)
						.append(" AND t2.is_nuion_owner = ").append(0)
						.append(" ORDER BY t2.id ASC");
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
		List<UnionMain> list = this.selectList(wrapper);
		return list;
	}

	@Override
	public Page list(Page page) throws Exception{
	    if (page == null) {
	        throw new ParamException(LIST, "参数page为空", ExceptionConstant.PARAM_ERROR);
        }
		EntityWrapper entityWrapper = new EntityWrapper<UnionMain>();
		entityWrapper.eq("del_status",0);
		return this.selectPage(page,entityWrapper);
	}

	@Override
	public void updateById(Integer id, Integer busId, UnionMain unionMain) throws Exception {
        if (id == null) {
            throw new ParamException(UPDATE_ID, "参数id为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(UPDATE_ID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        //TODO 校验unionMainInfoVO
        //TODO 更新联盟信息，要求盟主权限
	}


	@Override
	public void instance(Integer busId) throws Exception {
	    if (busId == null) {
	        throw new ParamException(INSTANCE, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        // （1）判断商家是否有盟主权限
        if (!this.unionRootService.hasUnionOwnerAuthority(busId)) {
	        throw new BusinessException(INSTANCE, "busId=" + busId, "当前登录商家帐号不具有盟主权限");
        }

        // （2）判断商家是否创建过联盟
        if (this.unionRootService.hasCreatedUnion(busId)) {
	        throw new BusinessException(INSTANCE, "busId=" + busId, "当前登录商家帐号已创建过一次联盟，无法再次创建");
        }

        // （3）判断商家是否已是盟主
        if (this.unionRootService.isUnionOwner(busId)) {
	        throw new BusinessException(INSTANCE, "busId=" + busId, "当前登录商家已是盟主，无法同时成为多个联盟的盟主");
        }
	}

    @Override
	public void save(Integer busId, UnionMainCreateInfoVO unionMainCreateInfoVO) throws Exception {
        if (busId == null) {
            throw new ParamException(SAVE, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        //TODO 校验unionMainInfoVOList
        //TODO 保存创建联盟的信息
	}

	@Override
	public UnionMain getByBusId(Integer busId) throws Exception {
	    if (busId == null) {
	        throw new ParamException(GET_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

		EntityWrapper entityWrapper = new EntityWrapper<UnionMain>();
		entityWrapper.eq("del_status", UnionMainConstant.DEL_STATUS_NO)
                .eq("bus_id", busId);;
		return this.selectOne(entityWrapper);
	}

    /**
     * 通过id获取对象
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public UnionMain getById(Integer id) throws Exception {
    	if (id == null) {
    	    throw new ParamException(GET_ID, "参数id为空", ExceptionConstant.PARAM_ERROR);
        }

        String idKey = RedisContant.KEY_UNION_MAIN + String.valueOf(id);
		if (this.redisCacheUtil.exists( idKey) ) { // （1）先从缓存中获取，如果存在，则直接返回
			Object obj = this.redisCacheUtil.get(idKey);
            return JSON.parseObject(obj.toString(), UnionMain.class);
		}
		// （2）如果不存在，则从数据库查询
		EntityWrapper<UnionMain> entityWrapper = new EntityWrapper<UnionMain>();
		entityWrapper.eq("del_status", UnionMainConstant.DEL_STATUS_NO)
            .eq("id", id);
        UnionMain unionMain = this.selectOne(entityWrapper);
		// （3）不为空时重新存入缓存
        if (unionMain != null) {
            this.redisCacheUtil.set(idKey, JSON.toJSONString(unionMain) );
        }
		return unionMain;
    }
}
