package com.gt.union.service.basic.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.client.address.AddressService;
import com.gt.union.api.client.dict.DictService;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.user.BusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.*;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.entity.basic.*;
import com.gt.union.entity.basic.vo.UnionMainCreateInfoVO;
import com.gt.union.entity.basic.vo.UnionMainInfoVO;
import com.gt.union.entity.common.BusUser;
import com.gt.union.mapper.basic.UnionMainMapper;
import com.gt.union.service.basic.*;
import com.gt.union.service.brokerage.IUnionBrokerageWithdrawalsRecordService;
import com.gt.union.service.brokerage.IUnionIncomeExpenseRecordService;
import com.gt.union.service.card.IUnionBusMemberCardService;
import com.gt.union.service.common.IUnionRootService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
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
	private static final String UPDATE_ID = "UnionMainServiceImpl.updateById()";
	private static final String GET_ID = "UnionMainServiceImpl.getById()";
	private static final String INSTANCE = "UnionMainServiceImpl.instance()";
	private static final String SAVE = "UnionMainServiceImpl.save()";
	private static final String GET_BUSID = "UnionMainServiceImpl.getByBusId()";
	private static final String PAY_CREATE_UNION = "UnionMainServiceImpl.payCreateUnion()";
	private static final String PAY_CREATE_UNION_SUCCESS = "UnionMainServiceImpl.payCreateUnionSuccess()";

	@Autowired
	private IUnionRootService unionRootService;

	@Autowired
	private IUnionApplyService unionApplyService;

	@Autowired
	private IUnionBusMemberCardService unionBusMemberCardService;

	@Autowired
	private IUnionEstablishRecordService unionEstablishRecordService;

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	@Autowired
	private IUnionInfoDictService unionInfoDictService;

	@Autowired
	private IUnionApplyInfoService unionApplyInfoService;

	@Autowired
	private IUnionMemberService unionMemberService;

	@Autowired
	private BusUserService busUserService;

	@Autowired
	private DictService dictService;

	@Autowired
	private IUnionTransferRecordService unionTransferRecordService;

	@Autowired
	private IUnionCreateInfoRecordService unionCreateInfoRecordService;

	@Autowired
	private AddressService addressService;

	@Autowired
    private IUnionIncomeExpenseRecordService unionIncomeExpenseRecordService;

	@Value("${union.url}")
	private String unionUrl;

	@Value("${wx.duofen.busId}")
	private Integer duofenBusId;

	@Value("${union.encryptKey}")
	private String encryptKey;

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
		UnionApplyInfo info  = this.unionApplyInfoService.getByUnionIdAndBusId(currentUnion.getId(), busId);//本商家的
		data.put("enterpriseName",info.getEnterpriseName());
		data.put("ownerEnterpriseName",info.getEnterpriseName());
		data.put("infoId",info.getId());
		if(!busId.equals(currentUnion.getBusId())){//不是盟主
			busId = currentUnion.getBusId();
			UnionApplyInfo mainInfo = this.unionApplyInfoService.getByUnionIdAndBusId(currentUnion.getId(), busId);
			data.put("ownerEnterpriseName",mainInfo.getEnterpriseName());
			isUnionOwner = 0;
		}
		if(CommonUtil.isNotEmpty(currentUnion.getIsIntegral()) && currentUnion.getIsIntegral() == 1){
			//查询联盟积分
			double integral = unionBusMemberCardService.getUnionMemberIntegral(currentUnion.getId());
			data.put("integral",integral);
		}
		double ableWithDrawalsSum = this.unionIncomeExpenseRecordService.getProfitMoneyByUnionIdAndBusId(currentUnion.getId(), busId);//联盟可提现佣金总和
		data.put("ableWithDrawalsSum",ableWithDrawalsSum);
		data.put("isUnionOwner",isUnionOwner);
		UnionTransferRecord record = unionTransferRecordService.get(currentUnion.getId(),busId);//转盟记录
		data.put("transferRecord",record);
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
						.append(" AND t2.del_status = ").append(UnionMemberConstant.DEL_STATUS_NO)
						.append(" AND t2.is_nuion_owner = ").append(UnionMemberConstant.IS_UNION_OWNER_NO)
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
		EntityWrapper entityWrapper = new EntityWrapper<UnionMain>();
		entityWrapper.eq("del_status",UnionMainConstant.DEL_STATUS_NO);
		return this.selectPage(page, entityWrapper);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateById(Integer id, Integer busId, UnionMainInfoVO vo) throws Exception {
        if (id == null) {
            throw new ParamException(UPDATE_ID, "参数id为空", ExceptionConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(UPDATE_ID, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        if(!unionRootService.checkUnionMainValid(id)){
        	throw new BusinessException(UPDATE_ID, "" , CommonConstant.UNION_OVERDUE_MSG);
		}
		if(!unionRootService.isUnionOwner(id, busId)){
			throw new BusinessException(UPDATE_ID, "" , CommonConstant.UNION_OWNER_NON_AUTHORITY_MSG);
		}
		UnionMain main = this.getById(id);
		if(CommonUtil.isNotEmpty(main.getIsIntegral()) && main.getIsIntegral() == UnionMainConstant.IS_INTEGRAL_YES){
			if(vo.getIsIntegral() == UnionMainConstant.IS_INTEGRAL_NO){
				throw new BusinessException(UPDATE_ID, "" , "积分开启后不可关闭");
			}
		}
		UnionMain unionMain = new UnionMain();
		unionMain.setId(main.getId());
		if(vo.getUnionImg().indexOf("/upload/") > -1){
			unionMain.setEditUnionImg(vo.getUnionImg().split("/upload/")[1]);
		}
		if(StringUtil.isNotEmpty(vo.getUnionWxGroupImg()) && vo.getUnionWxGroupImg().indexOf("/upload/") > -1){
			unionMain.setEditUnionWxGroupImg(vo.getUnionWxGroupImg().split("/upload/")[1]);
		}
		if(StringUtil.isNotEmpty(main.getUnionWxGroupImg()) && main.getUnionWxGroupImg().indexOf("/upload/") > -1){
			unionMain.setEditUnionWxGroupImg(main.getUnionWxGroupImg().split("/upload/")[1]);
		}
		if(vo.getRedCardOpend() == UnionMainConstant.RED_CARD_OPEN){
			if(CommonUtil.isEmpty(vo.getRedCardPrice())){
				throw new BusinessException(UPDATE_ID, "" , "请设置红卡价格");
			}
			if(CommonUtil.isEmpty(CommonUtil.toDouble(vo.getRedCardPrice())) || vo.getRedCardPrice() < 0){
				throw new BusinessException(UPDATE_ID, "" , "红卡价格有误");
			}
			if(CommonUtil.isEmpty(vo.getRedCardTerm())){
				throw new BusinessException(UPDATE_ID, "" , "请设置红卡期限");
			}
			if(CommonUtil.isEmpty(CommonUtil.toInteger(vo.getRedCardTerm())) || vo.getRedCardTerm() < 0){
				throw new BusinessException(UPDATE_ID, "" , "红卡期限有误");
			}
			unionMain.setRedCardIllustration(vo.getRedCardIllustration());
			unionMain.setRedCardPrice(vo.getRedCardPrice());
			unionMain.setRedCardTerm(vo.getRedCardTerm());
		}else {
			unionMain.setRedCardPrice(0d);
			unionMain.setRedCardTerm(0);
			unionMain.setRedCardIllustration("");
		}
		if(vo.getBlackCardCharge() == UnionMainConstant.BLACK_CARD_OPEN){
			if(CommonUtil.isEmpty(vo.getRedCardPrice())){
				throw new BusinessException(UPDATE_ID, "" , "请设置黑卡价格");
			}
			if(CommonUtil.isEmpty(CommonUtil.toDouble(vo.getRedCardPrice())) || vo.getRedCardPrice() < 0){
				throw new BusinessException(UPDATE_ID, "" , "黑卡价格有误");
			}
			if(CommonUtil.isEmpty(vo.getBlackCardTerm())){
				throw new BusinessException(UPDATE_ID, "" , "请设置黑卡期限");
			}
			if(CommonUtil.isEmpty(CommonUtil.toInteger(vo.getBlackCardTerm())) || vo.getBlackCardTerm() < 0){
				throw new BusinessException(UPDATE_ID, "" , "黑卡期限有误");
			}
			unionMain.setBlackCardIllustration(vo.getBlackCardIllustration());
			unionMain.setBlackCardPrice(vo.getBlackCardPrice());
			unionMain.setBlackCardTerm(vo.getBlackCardTerm());
		}else {
			unionMain.setBlackCardPrice(0d);
			unionMain.setBlackCardTerm(0);
			unionMain.setBlackCardIllustration("");
		}
		if(vo.getBlackCardCharge() == UnionMainConstant.BLACK_CARD_OPEN && vo.getRedCardOpend() == UnionMainConstant.RED_CARD_OPEN){
			if(vo.getBlackCardPrice().doubleValue() > vo.getRedCardPrice()){
				throw new BusinessException(UPDATE_ID, "" , "黑卡价格不可高于红卡价格");
			}
		}
		unionMain.setBlackCardCharge(vo.getBlackCardCharge());
		unionMain.setRedCardOpend(vo.getRedCardOpend());
		unionMain.setUnionName(vo.getUnionName());
		unionMain.setUnionIllustration(vo.getUnionIllustration());
		unionMain.setJoinType(vo.getJoinType());
		unionMain.setOldMemberCharge(vo.getOldMemberCharge());
		unionMain.setIsIntegral(vo.getIsIntegral());
		this.updateById(unionMain);
		EntityWrapper entityWrapper = new EntityWrapper<UnionInfoDict>();
		entityWrapper.eq("union_id",main.getId());
		unionInfoDictService.delete(entityWrapper);
		List<UnionInfoDict> list = vo.getInfos();
		unionInfoDictService.insertBatch(list);
		String infoDictKey = RedisKeyUtil.getUnionInfoDictKey(main.getId());
		redisCacheUtil.remove(infoDictKey);
		String unionMainKey = RedisKeyUtil.getUnionMainKey(main.getId());
		this.redisCacheUtil.remove(unionMainKey );
	}


	@Override
	public Map<String, Object> instance(Integer busId) throws Exception {
		Map<String,Object> data = new HashMap<String,Object>();
	    if (busId == null) {
	        throw new ParamException(INSTANCE, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

		// 1、判断商家是否已是盟主
		if (this.unionRootService.isUnionOwner(busId)) {
			throw new BusinessException(INSTANCE, "已是盟主", "您已是联盟盟主，不可创建");
		}

		//2、判断是否有创建联盟的权限
		BusUser user = busUserService.getBusUserById(busId);
	    if(user == null){
			throw new BusinessException(INSTANCE, "参数user为空", "账号不存在");
		}
		if(!unionRootService.checkBusUserValid(user)){
			throw new BusinessException(INSTANCE, "参数user为空", CommonConstant.UNION_BUS_OVERDUE_MSG);
		}
		List<Map> createDict = dictService.getCreateUnionDict();//创建联盟的权限
	    boolean flag = false;
	    Map info = null;
		for(Map map : createDict){
			if(CommonUtil.toInteger(map.get("item_key")).equals(user.getLevel())){
				info = map;
				flag = true;
				break;
			}
		}
		if(!flag){
			throw new BusinessException(INSTANCE, "没有创建权限", "您没有创建联盟的权限");
		}
		//3、根据等级判断是否需要付费
		String itemValue = info.get("item_value").toString();//根据等级获取创建联盟的权限
		String[] arrs = itemValue.split(",");
		String isPay = arrs[0];
		if(isPay.equals("0")){//不需要付费
			data.put("save",1);//去创建联盟
		}else{
			//4、需要付费，判断是否已经付费
			UnionCreateInfoRecord record = unionCreateInfoRecordService.getByBusId(busId);
			if(record == null){
				data.put("pay",1);//去支付
				List<Map> list = dictService.getUnionCreatePackage();
				data.put("payItems", list);
			}
			if(DateTimeKit.laterThanNow(record.getPeriodValidity())){//未过期
				if(CommonUtil.isNotEmpty(record.getUnionId())){
					throw new BusinessException(INSTANCE, "联盟未过期", "联盟未过期，不可创建");
				}else{
					data.put("save",1);//去创建联盟
				}
			}else {//过期了
				if(CommonUtil.isNotEmpty(record.getUnionId())){
					data.put("pay",1);//去支付
					List<Map> list = dictService.getUnionCreatePackage();
					data.put("payItems", list);
				}else{
					data.put("save",1);//去创建联盟
				}
			}
		}
		return data;
	}


    @Override
	@Transactional(rollbackFor = Exception.class)
	public void save(Integer busId, UnionMainCreateInfoVO vo) throws Exception {
        if (busId == null) {
            throw new ParamException(SAVE, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
		Map result = instance(busId);
        if(CommonUtil.isEmpty(result.get("save"))){
			throw new BusinessException(SAVE, "", "不可创建联盟");
		}
		if(CommonUtil.isNotEmpty(vo.getIsIntegral()) && vo.getIsIntegral() == UnionMainConstant.IS_INTEGRAL_YES){
        	if(CommonUtil.isEmpty(vo.getIntegralProportion())){
				throw new BusinessException(SAVE, "", "请设置积分抵扣率");
			}
			double integral = new BigDecimal((vo.getIntegralProportion())).doubleValue();
        	if(integral <= 0 || integral > 30){
				throw new BusinessException(SAVE, "", "积分抵扣率有误");
			}
		}
		//（1）保存联盟信息
		UnionMain main = new UnionMain();
		if(vo.getUnionImg().indexOf("/upload/") > -1){
			main.setEditUnionImg(vo.getUnionImg().split("/upload/")[1]);
		}
		if(StringUtil.isNotEmpty(vo.getUnionWxGroupImg()) && vo.getUnionWxGroupImg().indexOf("/upload/") > -1){
			main.setEditUnionWxGroupImg(vo.getUnionWxGroupImg().split("/upload/")[1]);
		}
		if(StringUtil.isNotEmpty(main.getUnionWxGroupImg()) && main.getUnionWxGroupImg().indexOf("/upload/") > -1){
			main.setEditUnionWxGroupImg(main.getUnionWxGroupImg().split("/upload/")[1]);
		}
		if(vo.getRedCardOpend() == UnionMainConstant.RED_CARD_OPEN){
			if(CommonUtil.isEmpty(vo.getRedCardPrice())){
				throw new BusinessException(UPDATE_ID, "" , "请设置红卡价格");
			}
			if(CommonUtil.isEmpty(CommonUtil.toDouble(vo.getRedCardPrice())) || vo.getRedCardPrice() < 0){
				throw new BusinessException(UPDATE_ID, "" , "红卡价格有误");
			}
			if(CommonUtil.isEmpty(vo.getRedCardTerm())){
				throw new BusinessException(UPDATE_ID, "" , "请设置红卡期限");
			}
			if(CommonUtil.isEmpty(CommonUtil.toInteger(vo.getRedCardTerm())) || vo.getRedCardTerm() < 0){
				throw new BusinessException(UPDATE_ID, "" , "红卡期限有误");
			}
			main.setRedCardIllustration(vo.getRedCardIllustration());
			main.setRedCardPrice(vo.getRedCardPrice());
			main.setRedCardTerm(vo.getRedCardTerm());
		}else {
			main.setRedCardPrice(0d);
			main.setRedCardTerm(0);
			main.setRedCardIllustration("");
		}
		if(vo.getBlackCardCharge() == UnionMainConstant.BLACK_CARD_OPEN){
			if(CommonUtil.isEmpty(vo.getRedCardPrice())){
				throw new BusinessException(UPDATE_ID, "" , "请设置黑卡价格");
			}
			if(CommonUtil.isEmpty(CommonUtil.toDouble(vo.getRedCardPrice())) || vo.getRedCardPrice() < 0){
				throw new BusinessException(UPDATE_ID, "" , "黑卡价格有误");
			}
			if(CommonUtil.isEmpty(vo.getBlackCardTerm())){
				throw new BusinessException(UPDATE_ID, "" , "请设置黑卡期限");
			}
			if(CommonUtil.isEmpty(CommonUtil.toInteger(vo.getBlackCardTerm())) || vo.getBlackCardTerm() < 0){
				throw new BusinessException(UPDATE_ID, "" , "黑卡期限有误");
			}
			main.setBlackCardIllustration(vo.getBlackCardIllustration());
			main.setBlackCardPrice(vo.getBlackCardPrice());
			main.setBlackCardTerm(vo.getBlackCardTerm());
		}else {
			main.setBlackCardPrice(0d);
			main.setBlackCardTerm(0);
			main.setBlackCardIllustration("");
		}
		if(vo.getBlackCardCharge() == UnionMainConstant.BLACK_CARD_OPEN && vo.getRedCardOpend() == UnionMainConstant.RED_CARD_OPEN){
			if(vo.getBlackCardPrice().doubleValue() > vo.getRedCardPrice()){
				throw new BusinessException(UPDATE_ID, "" , "黑卡价格不可高于红卡价格");
			}
		}

        main.setCreatetime(DateUtil.getCurrentDate());
        main.setDelStatus(UnionMainConstant.DEL_STATUS_NO);
        main.setUnionName(vo.getUnionName());
        main.setBusId(busId);
        main.setUnionImg(vo.getUnionImg());
        main.setJoinType(vo.getJoinType());
        main.setDirectorPhone(vo.getDirectorPphone());
        main.setUnionIllustration(vo.getUnionIllustration());
		main.setUnionWxGroupImg(vo.getUnionWxGroupImg());
		List<Map> dicts = dictService.getCreateUnionDict();
		BusUser user = busUserService.getBusUserById(busId);
		for(Map map : dicts){
			if(map.get("item_key").equals(user.getLevel())){
				String itemValue = map.get("item_value").toString();
				String unionMember = itemValue.split(",")[1];
				main.setUnionTotalMember(CommonUtil.toInteger(unionMember));
				break;
			}
		}
		main.setUnionSign(RandomKit.getRandomString(8,2));
        main.setUnionMemberNum(UnionMainConstant.MEMBER_NUM_INIT);
        main.setUnionLevel(UnionMainConstant.LEVEL_INIT);
        this.insert(main);

        // （2）保存盟主信息
        UnionMember member = new UnionMember();
		member.setBusId(busId);
		member.setDelStatus(UnionMemberConstant.DEL_STATUS_NO);
		member.setCreatetime(new Date());
		member.setUnionId(main.getId());
		member.setIsNuionOwner(UnionMemberConstant.IS_UNION_OWNER_YES);
		member.setOutStaus(UnionMemberConstant.OUT_STATUS_NORMAL);
		String signID = unionMemberService.createUnionSignByUnionId(main.getId());
		member.setUnionIDSign(signID);
		unionMemberService.insert(member);

        // （3）保存申请信息
        UnionApply apply = new UnionApply();
        apply.setDelStatus(UnionApplyConstant.DEL_STATUS_NO);
        apply.setUnionMemberId(member.getId());
        apply.setApplyBusId(busId);
        apply.setApplyType(UnionApplyConstant.APPLY_TYPE_FREE);
        apply.setCreatetime(new Date());
        apply.setUnionId(main.getId());
        apply.setApplyStatus(UnionApplyConstant.APPLY_STATUS_PASS);
        apply.setBusConfirmStatus(UnionApplyConstant.BUS_CONFIRM_STATUS_PASS);
        unionApplyService.insert(apply);

        // （4）保存申请关联信息
        UnionApplyInfo info = new UnionApplyInfo();
        info.setUnionApplyId(apply.getId());
        info.setEnterpriseName(vo.getEnterpriseName());
        info.setDirectorPhone(vo.getDirectorPphone());
        info.setNotifyPhone(vo.getNotifyPhone());
        info.setDirectorEmail(vo.getDirectorEmail());
        info.setDirectorName(vo.getDirectorName());
        info.setBusAddress(vo.getBusAddress());
        info.setIntegralProportion(CommonUtil.isEmpty(vo.getIntegralProportion()) ? 0 : vo.getIntegralProportion());
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
        unionApplyInfoService.insert(info);
		//（5）修改支付创建联盟记录信息状态
		UnionCreateInfoRecord record = unionCreateInfoRecordService.getByBusId(busId);
		if(CommonUtil.isNotEmpty(record)){
			record.setUnionId(main.getId());
			record.setCreateStatus(UnionCreateInfoRecordConstant.CREATE_STATUS_YES);
			unionCreateInfoRecordService.updateById(record);
		}
        // （6）保存相关缓存信息
		String unionMainKey = RedisKeyUtil.getUnionMainKey(main.getId());
		this.redisCacheUtil.set(unionMainKey, JSON.toJSONString(main) );
		String unionMainValidKey = RedisKeyUtil.getUnionMainValidKey(main.getId());
		this.redisCacheUtil.set(unionMainValidKey, "1");
		String unionMemberBusIdKey = RedisKeyUtil.getUnionMemberBusIdKey(member.getId(), busId);
		this.redisCacheUtil.set(unionMemberBusIdKey, JSON.toJSONString(member) );
		String unionApplyInfoKey = RedisKeyUtil.getUnionApplyInfoKey(main.getId(), busId);
		this.redisCacheUtil.set(unionApplyInfoKey, JSON.toJSONString(info) );
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

        String unionMainKey = RedisKeyUtil.getUnionMainKey(id);
		if (this.redisCacheUtil.exists(unionMainKey) ) { // （1）先从缓存中获取，如果存在，则直接返回
			Object obj = this.redisCacheUtil.get(unionMainKey);
            return JSON.parseObject(obj.toString(), UnionMain.class);
		}
		// （2）如果不存在，则从数据库查询
		EntityWrapper<UnionMain> entityWrapper = new EntityWrapper<UnionMain>();
		entityWrapper.eq("del_status", UnionMainConstant.DEL_STATUS_NO)
            .eq("id", id);
        UnionMain unionMain = this.selectOne(entityWrapper);
		// （3）不为空时重新存入缓存
        if (unionMain != null) {
            this.redisCacheUtil.set(unionMainKey, JSON.toJSONString(unionMain) );
        }
		return unionMain;
    }

	@Override
	public List<UnionMain> getSameUnionBus(List<UnionMain> list, final Integer busId) {
		StringBuilder str = new StringBuilder();
		for(UnionMain main : list){
			str.append(main.getId()).append(",");
		}
		String ids = str.toString();
		final String id = ids.substring(0,ids.length() - 1);
		Wrapper wrapper = new Wrapper() {
			@Override
			public String getSqlSegment() {
				StringBuilder sbSqlSegment = new StringBuilder(" t1");
				sbSqlSegment.append(" LEFT JOIN t_union_member t2 ON t1.id = t2.union_id")
						.append(" WHERE t2.bus_id = ").append(busId)
						.append(" AND t2.del_status = ").append(UnionMemberConstant.DEL_STATUS_NO)
						.append(" AND t2.out_staus != ").append(UnionMemberConstant.OUT_STATUS_PERIOD)
						.append(" AND t2.union_id in (").append(id).append(")");
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
		List<UnionMain> sameUnions = this.selectList(wrapper);
		return sameUnions;
	}

	@Override
	public List<UnionMain> listMyValidUnion(Integer busId) {
    	List<UnionMain> list = new ArrayList<UnionMain>();
		EntityWrapper entityWrapper = new EntityWrapper<UnionMain>();
		StringBuilder existsSql = new StringBuilder();
		existsSql.append("select id from t_union_member m")
				.append(" where m.bus_id = ").append(busId)
				.append(" and m.union_id = t_union_main.id")
				.append(" and m.del_status = ").append(UnionMemberConstant.DEL_STATUS_NO)
				.append(" and m.out_staus != ").append(UnionMemberConstant.OUT_STATUS_PERIOD);
		entityWrapper.exists(existsSql.toString());
		List<UnionMain> unions = this.selectList(entityWrapper);
		if(ListUtil.isNotEmpty(unions)){
			Iterator<UnionMain> it = unions.iterator();
			while (it.hasNext()){
				UnionMain main = it.next();
				try{
					if(unionRootService.checkUnionMainValid(main)){
						list.add(main);
					}
				}catch (Exception e){

				}
			}
		}
		return list;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public Map<String, Object> createUnionQRCode(Integer busId, String infoItemKey) throws Exception{
    	Map<String, Object> data = new HashMap<String, Object>();
		List<Map> list = dictService.getUnionCreatePackage();
		Double pay = 0d;
		boolean flag = false;
		for(Map map : list){
			if(map.get("item_key").equals(infoItemKey)){
				String itemValue = map.get("item_value").toString();
				String value = itemValue.split(",")[0];
				pay = Double.valueOf(value).doubleValue();
				flag = true;
				break;
			}
		}
		if(!flag){
			throw new ParamException(PAY_CREATE_UNION, "支付类型有误", ExceptionConstant.PARAM_ERROR);
		}
		pay = 0.01d;
		String orderNo = CommonConstant.CREATE_UNION_PAY_ORDER_CODE + System.currentTimeMillis();
		String only=DateTimeKit.getDateTime(new Date(), DateTimeKit.yyyyMMddHHmmss);
		Map publicUser = busUserService.getWxPublicUserByBusId(duofenBusId);
		UnionEstablishRecord record = new UnionEstablishRecord();
		record.setBusId(busId);
		record.setCreateUnionStatus(UnionEstablishRecordConstant.CREATE_UNION_STATUS_NO);
		record.setOrderDesc("多粉-创建联盟付款");
		record.setSysOrderNo(orderNo);
		record.setOrderStatus(UnionEstablishRecordConstant.ORDER_UNPAY);
		record.setOrderMoney(pay);
		unionEstablishRecordService.insert(record);
		data.put("totalFee",pay);
		data.put("busId", duofenBusId);
		data.put("sourceType", 1);//是否墨盒支付
		data.put("payWay",0);//系统判断支付方式
		data.put("isreturn",0);//0：不需要同步跳转
		data.put("model", CommonConstant.PAY_MODEL);
		//data.put("notifyUrl", unionUrl + "/unionMain/79B4DE7C/paymentSuccess/"+record.getId());
		String encrypt = EncryptUtil.encrypt(encryptKey, record.getId().toString());
		encrypt = URLEncoder.encode(encrypt,"UTF-8");
		data.put("notifyUrl", "http://union.duofee.com" + "/unionMain/79B4DE7C/paymentSuccess/"+encrypt + "/" + only);
		data.put("orderNum", orderNo);//订单号
		data.put("payBusId",busId);//支付的商家id
		data.put("isSendMessage",0);//不推送
		data.put("appid",publicUser.get("appid"));//appid
		data.put("desc", "多粉-创建联盟");
		data.put("appidType",0);//公众号
		data.put("only", only);
		data.put("infoItemKey",infoItemKey);
		String paramKey = RedisKeyUtil.getCreateUnionPayParamKey(only);
		String statusKey = RedisKeyUtil.getCreateUnionPayStatusKey(only);
		redisCacheUtil.set(paramKey,JSON.toJSONString(data), 360l);//5分钟
		redisCacheUtil.set(statusKey,CommonConstant.USER_ORDER_STATUS_001,300l);//等待扫码状态
		return data;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void payCreateUnionSuccess(String recordEncrypt, String only) throws Exception{
		String data = EncryptUtil.decrypt(encryptKey,recordEncrypt);
		Integer recordId = CommonUtil.toInteger(data);
		UnionEstablishRecord record = unionEstablishRecordService.selectById(recordId);
		if(record == null){
			throw new BusinessException(PAY_CREATE_UNION_SUCCESS, "record==null", "创建联盟支付记录为空");
		}
		//根据订单
		String paramKey = RedisKeyUtil.getCreateUnionPayParamKey(only);
		Object obj = redisCacheUtil.get(paramKey);
		Map<String,Object> result = JSONObject.parseObject(obj.toString(),Map.class);
		String statusKey = RedisKeyUtil.getCreateUnionPayStatusKey(only);
		//修改订单状态
		record.setOrderStatus(UnionEstablishRecordConstant.ORDER_PAY_SUCCESS);
		record.setCreateUnionStatus(UnionEstablishRecordConstant.CREATE_UNION_STATUS_YES);
		record.setFinishTime(new Date());
		unionEstablishRecordService.updateById(record);
		Integer busId = CommonUtil.toInteger(result.get("payBusId"));
		UnionMain main = this.getByBusId(busId);//是否盟主
		//修改之前的创建记录信息（delStatus = 1）
		UnionCreateInfoRecord unionCreateInfoRecord = unionCreateInfoRecordService.getByBusId(busId);
		if(unionCreateInfoRecord != null){
			unionCreateInfoRecord.setDelStatus(UnionCreateInfoRecordConstant.DEL_STATUS_YES);
			unionCreateInfoRecordService.updateById(unionCreateInfoRecord);
		}
		//生成新的创建记录信息
		UnionCreateInfoRecord infoRecord = new UnionCreateInfoRecord();
		infoRecord.setBusId(busId);
		infoRecord.setCreatetime(new Date());
		infoRecord.setDelStatus(UnionCreateInfoRecordConstant.DEL_STATUS_NO);
		if(CommonUtil.isEmpty(main)){
			infoRecord.setCreateStatus(UnionCreateInfoRecordConstant.CREATE_STATUS_NO);
		}else {
			infoRecord.setCreateStatus(UnionCreateInfoRecordConstant.CREATE_STATUS_YES);
			infoRecord.setUnionId(main.getId());
		}
		Object infoItemKey = result.get("infoItemKey");
		List<Map> list = dictService.getUnionCreatePackage();
		Double years = 0d;
		for(Map map : list){
			if(map.get("item_key").equals(infoItemKey)){
				String itemValue = map.get("item_value").toString();
				String value = itemValue.split(",")[1];
				years = Double.valueOf(value).doubleValue();
				break;
			}
		}
		int month = (int)(new BigDecimal(years).multiply(new BigDecimal(12)).doubleValue());
		infoRecord.setPeriodValidity(DateTimeKit.addMonths(month));
		unionCreateInfoRecordService.insert(infoRecord);
		redisCacheUtil.set(statusKey, CommonConstant.USER_ORDER_STATUS_003, 60l);//支付成功
	}


}
