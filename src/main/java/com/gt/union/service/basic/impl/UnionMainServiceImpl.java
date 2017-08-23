package com.gt.union.service.basic.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.basic.UnionMainConstant;
import com.gt.union.common.constant.basic.UnionMemberConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.entity.basic.*;
import com.gt.union.mapper.basic.UnionMainMapper;
import com.gt.union.service.basic.*;
import com.gt.union.service.brokerage.IUnionBrokerageWithdrawalsRecordService;
import com.gt.union.service.card.IUnionBusMemberCardService;
import com.gt.union.service.common.IUnionRootService;
import com.gt.union.vo.basic.UnionMainCreateInfoVO;
import com.gt.union.vo.basic.UnionMainInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	private IUnionInfoDictService unionInfoDictService;

	@Autowired
	private IUnionApplyInfoService unionApplyInfoService;

	@Autowired
	private IUnionMemberService unionMemberService;

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
			throw new BusinessException(UPDATE_ID, "" , "您不是盟主，不可操作");
		}
		UnionMain main = this.getById(id);
		if(CommonUtil.isNotEmpty(main.getIsIntegral()) && main.getIsIntegral() == 1){
			if(vo.getIsIntegral() == 0){
				throw new BusinessException(UPDATE_ID, "" , "积分开启后不可关闭");
			}
		}
		if(vo.getUnionImg().indexOf("/upload/") > -1){
			main.setEditUnionImg(vo.getUnionImg().split("/upload/")[1]);
		}
		if(StringUtil.isNotEmpty(vo.getUnionWxGroupImg()) && vo.getUnionWxGroupImg().indexOf("/upload/") > -1){
			main.setEditUnionWxGroupImg(vo.getUnionWxGroupImg().split("/upload/")[1]);
		}
		if(StringUtil.isNotEmpty(main.getUnionWxGroupImg()) && main.getUnionWxGroupImg().indexOf("/upload/") > -1){
			main.setEditUnionWxGroupImg(main.getUnionWxGroupImg().split("/upload/")[1]);
		}
		if(vo.getRedCardOpend() == 1){
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
		if(vo.getBlackCardCharge() == 1){
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
		if(vo.getBlackCardCharge() == 1 && vo.getRedCardOpend() == 1){
			if(vo.getBlackCardPrice().doubleValue() > vo.getRedCardPrice()){
				throw new BusinessException(UPDATE_ID, "" , "黑卡价格不可高于红卡价格");
			}
		}
		main.setBlackCardCharge(vo.getBlackCardCharge());
		main.setRedCardOpend(vo.getRedCardOpend());
		main.setUnionName(vo.getUnionName());
		main.setUnionIllustration(vo.getUnionIllustration());
		main.setJoinType(vo.getJoinType());
		main.setOldMemberCharge(vo.getOldMemberCharge());
		main.setIsIntegral(vo.getIsIntegral());
		this.updateById(main);
		EntityWrapper entityWrapper = new EntityWrapper<UnionInfoDict>();
		entityWrapper.eq("union_id",main.getId());
		unionInfoDictService.delete(entityWrapper);
		List<UnionInfoDict> list = vo.getInfos();
		unionInfoDictService.insertBatch(list);
		String infoDictKey = RedisKeyUtil.getUnionInfoDictKey(main.getId());
		redisCacheUtil.set(infoDictKey, JSON.toJSONString(list));
		String unionMainKey = RedisKeyUtil.getUnionMainKey(main.getId());
		this.redisCacheUtil.set(unionMainKey, JSON.toJSONString(main) );
	}


	@Override
	public void instance(Integer busId) throws Exception {
	    if (busId == null) {
	        throw new ParamException(INSTANCE, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }

        checkBeforeInstance(busId);
	}

    /**
     * 创建或保存联盟时校验权限
     * @param busId
     * @throws Exception
     */
	private void checkBeforeInstance(Integer busId) throws Exception {
        // （1）判断商家是否有盟主权限
        if (!this.unionRootService.hasUnionOwnerAuthority(busId)) {
            throw new BusinessException(INSTANCE, "busId=" + busId, "当前登录商家帐号不具有盟主权限");
        }

        // （2）判断商家是否创建过联盟
        if (!this.unionRootService.hasCreatedUnion(busId)) {
            throw new BusinessException(INSTANCE, "busId=" + busId, "不可创建");
        }

        // （3）判断商家是否已是盟主
        if (!this.unionRootService.isUnionOwner(busId)) {
            throw new BusinessException(INSTANCE, "busId=" + busId, "不可创建");
        }
    }

    @Override
	@Transactional(rollbackFor = Exception.class)
	public void save(Integer busId, UnionMainCreateInfoVO vo) throws Exception {
        if (busId == null) {
            throw new ParamException(SAVE, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        checkBeforeInstance(busId);

        //（1）保存联盟信息
        UnionMain main = new UnionMain();
        main.setCreatetime(DateUtil.getCurrentDate());
        main.setDelStatus(UnionMainConstant.DEL_STATUS_NO);
        main.setUnionName(vo.getUnionName());
        main.setBusId(busId);
        main.setUnionImg(vo.getUnionImg());
        main.setJoinType(vo.getJoinType());
        main.setDirectorPhone(vo.getDirectorPphone());
        main.setUnionIllustration(vo.getUnionIllustration());
        main.setUnionWxGroupImg(vo.getUnionWxGroupImg());
        //TODO union_sign? union_total_member?...
        main.setUnionMemberNum(UnionMainConstant.MEMBER_NUM_INIT);
        main.setUnionLevel(UnionMainConstant.LEVEL_INIT);
        this.insert(main);

        // （2）保存盟主信息 //TODO
        UnionMember member = new UnionMember();
        unionMemberService.insert(member);

        // （3）保存申请信息 //TODO
        UnionApply apply = new UnionApply();
        unionApplyService.insert(apply);

        // （4）保存申请关联信息 //TODO
        UnionApplyInfo info = new UnionApplyInfo();
        unionApplyInfoService.insert(info);

        // （5）保存相关缓存信息
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
}
