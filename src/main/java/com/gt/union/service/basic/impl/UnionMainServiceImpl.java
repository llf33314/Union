package com.gt.union.service.basic.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.UnionMainConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.entity.basic.UnionApplyInfo;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.mapper.basic.UnionMainMapper;
import com.gt.union.service.basic.*;
import com.gt.union.service.brokerage.IUnionBrokerageWithdrawalsRecordService;
import com.gt.union.service.business.IUnionBrokeragePayRecordService;
import com.gt.union.service.card.IUnionBusMemberCardService;
import com.gt.union.service.card.IUnionCardDivideRecordService;
import com.gt.union.service.common.UnionRootService;
import com.gt.union.vo.basic.UnionMainCreateInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
	private static final String GET_INSTANCE_STEP = "UnionMainServiceImpl.getInstanceStep()";
	private static final String SAVE = "UnionMainServiceImpl.save()";
	private static final String GETID_BUSID = "UnionMainServiceImpl.getIdByBusId()";
	private static final String IS_UNION_MAIN_VALID_DETAIL = "UnionMainServiceImpl.isUnionMainValidDETAIL()";

	@Autowired
	private UnionRootService unionRootService;

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

	@Autowired
	private IUnionInfoDictService unionInfoDictService;

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
        UnionMain createUnion = this.getUnionMainByBusId(busId);//我创建的联盟
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
		UnionMain createUnion = this.getUnionMainByBusId(busId);
        UnionMain main = this.getUnionMainById(id);
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
		UnionMain unionMain = getUnionMainByBusId(busId);
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
	public Page list(Page page) {
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
	public Map<String, Object> getInstanceStep(Integer busId) throws Exception {
	    if (busId == null) {
	        throw new ParamException(GET_INSTANCE_STEP, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        //TODO 获取创建联盟步骤信息
		return null;
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
	public UnionMain getUnionMainByBusId(Integer busId) throws Exception {
	    if (busId == null) {
	        throw new ParamException(GETID_BUSID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
		StringBuilder sqlWhere = new StringBuilder("");
		sqlWhere.append("SELECT t1.id, t1.union_id, t1.bus_id, t1.del_status, t1.is_nuion_owner from t_union_member t1 WHERE t_union_main.id = t1.union_id")
				.append(" AND t1.bus_id = ").append(busId)
				.append(" AND t_union_main.bus_id = ").append(busId)
				.append(" AND t1.del_status = ").append(0)
				.append(" AND t1.is_nuion_owner = ").append(1);
		EntityWrapper entityWrapper = new EntityWrapper<UnionMain>();
		entityWrapper.exists(sqlWhere.toString());
		UnionMain unionMain = this.selectOne(entityWrapper);
		return unionMain;
	}

    /**
     * 通过id获取对象
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public UnionMain getUnionMainById(Integer id){
		UnionMain main = null;
		if ( redisCacheUtil.exists( "unionMain:"+id ) ) {
			Object obj = redisCacheUtil.get("unionMain:"+id);
			if (CommonUtil.isNotEmpty(obj)) {
				return JSON.parseObject(obj.toString(),UnionMain.class);
			}
		}
		// 2. 不存在则从数据库查询
		EntityWrapper<UnionMain> entityWrapper = new EntityWrapper<UnionMain>();
		entityWrapper.eq("id",id);
		entityWrapper.eq("del_status",0);
		main = this.selectOne(entityWrapper);
		// 写入 Redis 操作
		if(CommonUtil.isNotEmpty(main)){
			redisCacheUtil.set( "unionMain:"+id, JSON.toJSONString(main) );
		}
		return main;
    }

    /**
     * ------------ 判断联盟是否有效 ------------
     */
    @Override
    public boolean isUnionMainValid(Integer id) throws Exception {
        return isUnionValid(this.getUnionMainById(id));
    }

    @Override
    public boolean isUnionValid(UnionMain unionMain) throws Exception {
       return this.isUnionMainValidDETAIL(unionMain) == UnionMainConstant.VALID_OK ? true : false;
    }

    @Override
    public Integer isUnionMainValidDETAIL(Integer id) throws Exception {
        return this.isUnionMainValidDETAIL(this.getUnionMainById(id));
    }

    @Override
    public Integer isUnionMainValidDETAIL(UnionMain unionMain) throws Exception {
        if (unionMain == null) {
            throw new ParamException(IS_UNION_MAIN_VALID_DETAIL, "参数unionMain为空", ExceptionConstant.PARAM_ERROR);
        }
        //TODO
        //首先判断盟主的有效期
        //判断盟主的权限
        //判断联盟的有效期
        return null;
    }


/*

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

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateUnionMain(UnionMainInfoVO unionMainInfoVO, Integer busId) throws Exception{
		UnionMain main = unionMainInfoVO.getUnionMain();
		if(CommonUtil.isEmpty(main.getId())){
			throw new ParamException(UPDATE_UNION_MAIN, "参数错误", ExceptionConstant.PARAM_ERROR);
		}
		//TODO  判断是否该联盟盟主
		boolean isUnionOwner =  unionMemberService.isUnionOwner(main.getId(),busId);
		if(!isUnionOwner){
			//判断联盟是否有效
			throw new BusinessException(UPDATE_UNION_MAIN, "", "不是该盟盟主");
		}
		//判断联盟信息
		if(CommonUtil.isEmpty(main.getUnionName())){
			throw new ParamException(UPDATE_UNION_MAIN, "", "联盟名称不能为空");
		}
		EntityWrapper<UnionInfoDict> entityWrapper = new EntityWrapper<UnionInfoDict>();
		entityWrapper.eq("union_id",main.getId());
		unionInfoDictService.delete(entityWrapper);
		unionInfoDictService.insertBatch(unionMainInfoVO.getInfos());
		this.updateById(main);
		redisCacheUtil.set("unionMain:" + main.getId(),JSON.toJSONString(main));
	}

	//TODO 创建联盟
	@Override
	public Map<String, Object> getCreateUnionInfo(BusUser user) throws Exception{
		Map<String,Object> data = new HashMap<String,Object>();
		int type = 1;
		if(getCreateUnion(user.getId()) != null){  //已是盟主
			//1、判断商家等级是否需要收费创建联盟

			//2、如果需收费，判断联盟是否过有效期
			return data;
		}
		//判断商家账号是否有创建联盟的权限
		data.put("type",type);
		return data;
	}

	@Override
	public void saveUnionMainInfo(UnionMainInfoVO unionMainInfo, BusUser user) {

	}

	@Override
	public Map<String, Object> getUnionMainInfo(Integer id) {
		Map<String,Object> data = new HashMap<String,Object>();
		UnionMain main = getUnionMain(id);
		EntityWrapper entityWrapper = new EntityWrapper<UnionInfoDict>();
		entityWrapper.eq("union_id",id);
		entityWrapper.eq("del_status",0);
		List<Map<String,Object>> infos = unionInfoDictService.getUnionInfoDict(id);
		data.put("main",main);
		data.put("infos",infos);
		return data;
	}

	@Override
	public List<UnionMain> getUnionMainList() {
		EntityWrapper entityWrapper = new EntityWrapper<UnionMain>();
		entityWrapper.eq("del_status",0);
		return this.selectList(entityWrapper);
	}


	@Override
	public Map<String, Object> getUnionMainMemberInfo(Integer busId) throws Exception{
		return unionMainMemberInfo(null,busId);
	}

	@Override
	public Map<String, Object> getUnionMainMemberInfo(Integer busId, Integer unionId) throws Exception{
		UnionMain main = getUnionMain(unionId);
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
			data.put("unionId",main.getId());
			data.put("isIntegral",main.getIsIntegral());
			data.put("unionName",main.getUnionName());
			data.put("unionIllustration",main.getUnionIllustration());
			data.put("createtime", DateTimeKit.format(main.getCreatetime(),DateTimeKit.DEFAULT_DATETIME_FORMAT));
			data.put("unionMemberNum",main.getUnionMemberNum());
			data.put("unionTotalMember",main.getUnionTotalMember());
			data.put("surplusMemberNum",main.getUnionTotalMember() - main.getUnionMemberNum());
			UnionApplyInfo info  = unionApplyService.getUnionApplyInfo(busId,main.getId());//本商家的
			data.put("enterpriseName",info.getEnterpriseName());
			data.put("ownerEnterpriseName",info.getEnterpriseName());
			data.put("infoId",info.getId());
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
			double ableWithDrawalsSum = BigDecimalUtil.add(divideSum,brokerageSum).subtract(new BigDecimal(withdrawalsSum)).doubleValue();
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

	*/
/**
	 * 根据商家id获取他创建的联盟
	 * @param busId
	 * @return
	 *//*

	UnionMain getCreateUnion(Integer busId){
		EntityWrapper<UnionMain> entityWrapper = new EntityWrapper<UnionMain>();
		entityWrapper.eq("bus_id",busId);
		entityWrapper.eq("del_status",0);
		UnionMain main = this.selectOne(entityWrapper);
		return main;
	}

*/

}
