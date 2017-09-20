package com.gt.union.card.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.api.bean.session.WxPublicUsers;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.api.entity.result.UnionDiscountResult;
import com.gt.union.card.entity.UnionCard;
import com.gt.union.card.entity.UnionCardBinding;
import com.gt.union.card.entity.UnionCardRoot;
import com.gt.union.card.mapper.UnionCardMapper;
import com.gt.union.card.service.IUnionCardBindingService;
import com.gt.union.card.service.IUnionCardIntegralService;
import com.gt.union.card.service.IUnionCardRootService;
import com.gt.union.card.service.IUnionCardService;
import com.gt.union.card.vo.UnionCardBindParamVO;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.main.constant.MainConstant;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.entity.UnionMainCharge;
import com.gt.union.main.service.IUnionMainChargeService;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.entity.UnionMemberDiscount;
import com.gt.union.member.service.IUnionMemberDiscountService;
import com.gt.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <p>
 * 联盟卡信息 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionCardServiceImpl extends ServiceImpl<UnionCardMapper, UnionCard> implements IUnionCardService {

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionCardIntegralService unionCardIntegralService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionCardRootService unionCardRootService;

    @Autowired
    private IUnionCardBindingService unionCardBindingService;

    @Autowired
    private IUnionMemberDiscountService unionMemberDiscountService;

    @Autowired
    private IDictService dictService;

    @Autowired
    private UnionCardMapper unionCardMapper;

    @Value("${union.encryptKey}")
    private String encryptKey;

    @Value("${wxmp.company}")
    private String company;

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainChargeService unionMainChargeService;

    @Autowired
    private IBusUserService busUserService;

    @Override
    public Page selectListByUnionId(Page page, final Integer unionId, final Integer busId, final String cardNo, final String phone) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (page == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionMember unionMember = unionMemberService.getByBusIdAndUnionId(busId, unionId);
        final int memberId = unionMember.getId();
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder();
                sbSqlSegment.append(" t1 LEFT JOIN t_union_card_root t2 ON t1.root_id = t2.id")
                        .append(" WHERE")
                        .append(" t1.member_id = ").append(memberId)
                        .append(" AND t1.del_status = ").append(CommonConstant.DEL_STATUS_NO);
                if (StringUtil.isNotEmpty(phone)) {
                    sbSqlSegment.append(" AND t2.phone LIKE '%").append(phone.trim()).append("%' ");
                }
                if (StringUtil.isNotEmpty(cardNo)) {
                    sbSqlSegment.append(" AND t2.number LIKE '%").append(cardNo.trim()).append("%' ");
                }
                sbSqlSegment.append(" ORDER BY t1.id DESC ");
                return sbSqlSegment.toString();
            }

            ;

        };
        wrapper.setSqlSelect(" t1.id, DATE_FORMAT(t1.createtime, '%Y-%m-%d %T') createtime, t1.type, DATE_FORMAT(t1.validity, '%Y-%m-%d %T') validity, t2.phone, t2.number as cardNo");
        Page result = this.selectMapsPage(page, wrapper);
        List<Map<String, Object>> list = result.getRecords();
        List<Integer> cardIds = new ArrayList<Integer>();
        for (Map map : list) {
            cardIds.add(CommonUtil.toInteger(map.get("id")));
        }
        //收入的积分
        List<Map<String, Object>> incomes = unionCardIntegralService.sumByCardIdsAndStatus(cardIds, 1);
        //支出的积分
        List<Map<String, Object>> expenditures = unionCardIntegralService.sumByCardIdsAndStatus(cardIds, 2);
        //计算和
        for (Map<String, Object> map : list) {
            map.put("integral", 0);
            for (Map<String, Object> income : incomes) {
                if (map.get("id").equals(income.get("cardId"))) {
                    map.put("integral", CommonUtil.toDouble(income.get("integral")));
                }
            }
            for (Map<String, Object> expenditure : expenditures) {
                if (map.get("id").equals(expenditure.get("cardId"))) {
                    map.put("integral", new BigDecimal(CommonUtil.toDouble(map.get("integral"))).subtract(new BigDecimal(CommonUtil.toDouble(expenditure.get("integral")))).doubleValue());
                }
            }
        }
        page.setRecords(list);
        return result;
    }

    @Override
    public Map<String, Object> getUnionCardInfo(String no, Integer busId, Integer unionId) throws Exception {
        if (StringUtil.isEmpty(no)) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Pattern p = Pattern.compile("[a-zA-z]");
        if (p.matcher(no).find()) {//包含字母--扫码枪扫码所得
            //解密
            try {
                no = EncryptUtil.decrypt(encryptKey, no);//解码后得到联盟卡号
            } catch (Exception e) {
                throw new ParamException(CommonConstant.PARAM_ERROR);
            }
            return getUnionCardInfoByCardNo(no, busId, unionId);
        } else {
            //手机号或联盟卡号
            //卡号使用8位
            if (no.length() == 8) {//卡号
                return getUnionCardInfoByCardNo(no, busId, unionId);
            } else {//手机号
                return getUnionCardInfoByPhone(no, busId, unionId);
            }
        }
    }

	@Override
	public Map<String, Object> getUnionCardInfoByCardNo(String cardNo, Integer busId, Integer unionId) throws Exception{
		Map<String,Object> data = new HashMap<String,Object>();
		UnionCardRoot root = unionCardRootService.getByCardNo(cardNo);
		if(root == null){
			throw new BusinessException("该联盟卡号不存在");
		}
		List<Integer> unionIds = new ArrayList<Integer>();
		if(unionId == null){
            List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
            if(ListUtil.isEmpty(members)){
                throw new BusinessException("您没有有效的联盟");
            }
            for(UnionMember member : members){
                unionIds.add(member.getUnionId());
            }
        }else {
            unionMainService.checkUnionMainValid(unionId);
            UnionMember member = unionMemberService.getByBusIdAndUnionId(busId,unionId);
            if(unionMemberService.hasWriteAuthority(member)){
                throw new BusinessException("您没有联盟权限");
            }
            unionIds.add(unionId);
        }
        if(ListUtil.isEmpty(unionIds)){
            throw new BusinessException("您没有有效的联盟");
        }
        Double discount = dictService.getDefaultDiscount();
        Map<String,Object> result = this.getByMinDiscountByCard(root.getId(), busId, unionIds);
        if(CommonUtil.isEmpty(result)){
            throw new BusinessException("没有可用的联盟卡");
        }
        if(CommonUtil.isEmpty(result.get("discount"))){//没有设置联盟卡

        }
        //TODO 根据联盟id查询具有写权限的盟员列表
        //unionMemberService.list

		/*
		List<UnionMember> memberList = unionMemberService.listValidByUnionIds(unionIds);
		List<Integer> memberIds = new ArrayList<Integer>();
		for(UnionMember member : memberList){
			memberIds.add(member.getId());
		}
		//查询用该rootId升级的联盟卡
		List<UnionCard> cards = this.listByCardRootIdAndMemberIds(root.getId(),memberIds);
		//得到有效的联盟卡列表
		cards = getValidUnionCards(cards);
		if(ListUtil.isEmpty(cards)){
			throw new BusinessException("没有有效的联盟卡");
		}
		data.put("cardId",);
		data.put("cardNo",cardNo);
		data.put("integral",root.getIntegral());*/

		return data;
	}

    @Override
    public Map<String, Object> getUnionCardInfoByPhone(String phone, Integer busId, Integer unionId) {
        return null;
    }

    @Override
    public UnionDiscountResult getConsumeUnionDiscount(Integer memberId, String phone, Integer busId) throws Exception {
        UnionDiscountResult result = new UnionDiscountResult();
        if (CommonUtil.isEmpty(memberId) || CommonUtil.isEmpty(busId)) {
            result.setCode(UnionDiscountResult.UNION_DISCOUNT_CODE_NON);
            return result;
        }
        //1、判断商家加入的有效联盟
        List<UnionMember> members = unionMemberService.listWriteByBusId(busId);
        if (ListUtil.isEmpty(members)) {
            result.setCode(UnionDiscountResult.UNION_DISCOUNT_CODE_NON);
            return result;
        }
        List<Integer> memberids = new ArrayList<Integer>();
        for (UnionMember member : members) {
            memberids.add(member.getId());
        }

        //2、判断手机号是否升级过联盟卡
        if (StringUtil.isNotEmpty(phone)) {
            UnionCardRoot unionCardRoot = unionCardRootService.getByPhone(phone);
            if (unionCardRoot == null) {//该手机号没有升级过联盟卡
                result.setCode(UnionDiscountResult.UNION_DISCOUNT_CODE_NON);
                return result;
            } else {//升级过联盟卡，给予使用，返回折扣（有可能在某个联盟中退盟了的，需要进一步判断）
                return getByUnionCardRootAndMembers(unionCardRoot.getId(), members);
            }
        }
        //3、判断粉丝是否绑定联盟卡
        UnionCardBinding binding = unionCardBindingService.getByMemberId(memberId);
        if (binding == null) {
            //没有绑定 去绑定  存在两种情况1：根本没有升级过联盟卡  2：升级过，但是不是在商家下升的
            result.setCode(UnionDiscountResult.UNION_DISCOUNT_CODE_BIND);
            return result;
        } else {
            return getByUnionCardRootAndMembers(binding.getRootId(), members);
        }
    }

    /**
     * 根据rootId和我的盟员列表
     *
     * @param rootId
     * @param members
     * @return
     * @throws Exception
     */
    private UnionDiscountResult getByUnionCardRootAndMembers(Integer rootId, List<UnionMember> members) throws Exception {
        UnionDiscountResult result = new UnionDiscountResult();
        //获取根据这个rootId升级的联盟卡列表
        List<UnionCard> list = this.listByCardRootId(rootId);
        if (ListUtil.isEmpty(list)) {
            result.setCode(UnionDiscountResult.UNION_DISCOUNT_CODE_NON);
            return result;
        }

        //返回有效的联盟卡
        list = getValidUnionCards(list);

        if (ListUtil.isEmpty(list)) {//没有有效的联盟卡 不可使用
            result.setCode(UnionDiscountResult.UNION_DISCOUNT_CODE_NON);
            return result;
        }

        //得到通过rootId升级的盟员列表
        List<Integer> memberIds = new ArrayList<Integer>();
        for (UnionCard card : list) {
            memberIds.add(card.getMemberId());
        }

        //当前消费商家加入的联盟列表
        List<Integer> unionIds = new ArrayList<Integer>();
        //我的盟员ids列表
        List<Integer> fromMemberIds = new ArrayList<Integer>();
        for (UnionMember member : members) {
            fromMemberIds.add(member.getId());
            unionIds.add(member.getUnionId());
        }

        //查询当前消费商家所在联盟的其他盟员列表  如商家A加入甲乙联盟，则甲乙联盟的盟员就是和商家A同盟，再通过升级的联盟卡的盟员列表过滤
        //List<UnionMember> memberList = unionMemberService.listByUnionIdsAndUnionMemberIds(unionIds, memberIds);
        List<UnionMember> memberList = null;
        if (ListUtil.isEmpty(memberList)) {
            result.setCode(UnionDiscountResult.UNION_DISCOUNT_CODE_NON);
            return result;
        }
        //用该rootId升级过的盟员列表
        List<Integer> toMemberIds = new ArrayList<Integer>();
        for (UnionMember member : memberList) {
            toMemberIds.add(member.getId());
        }

        //查询我对盟员列表中的盟员最低折扣，如果存在相同的，取对方先加入联盟的，如果不存在最低这里，则取默认的。
        //联盟卡取先加入联盟的
        UnionMemberDiscount minDiscount = unionMemberDiscountService.getMinDiscountByMemberList(fromMemberIds, toMemberIds);
        if (minDiscount == null) {
            Double defaultDiscount = dictService.getDefaultDiscount();
            result.setDiscount(defaultDiscount);
            result.setIfDefault(true);
            Integer minMemberId = memberList.get(0).getId();
            for (UnionMember member : memberList) {
                if (member.getId().intValue() < minMemberId.intValue()) {
                    minMemberId = member.getId();
                }
            }
            UnionCard card = this.getByUnionCardRootIdAndMemberId(rootId, minMemberId);
            UnionMember member = unionMemberService.getById(minMemberId);
            result.setCardId(card.getId());
            result.setUnionId(member.getUnionId());
        } else {
            result.setDiscount(minDiscount.getDiscount());
            result.setIfDefault(false);
            UnionCard card = this.getByUnionCardRootIdAndMemberId(rootId, minDiscount.getToMemberId());
            result.setCardId(card.getId());
            UnionMember member = unionMemberService.getById(minDiscount.getToMemberId());
            result.setUnionId(member.getUnionId());
        }
        result.setCode(UnionDiscountResult.UNION_DISCOUNT_CODE_SUCCESS);
        return result;
    }


    /**
     * 根据这批联盟卡，返回一批有效的联盟卡
     *
     * @param list
     * @return
     */
    List<UnionCard> getValidUnionCards(List<UnionCard> list) {
        if (ListUtil.isNotEmpty(list)) {
            Iterator<UnionCard> it = list.iterator();
            while (it.hasNext()) {
                UnionCard card = it.next();
                //判断联盟卡的有效期是否有效
                if (CommonUtil.isNotEmpty(card.getValidity()) && !DateTimeKit.laterThanNow(card.getValidity())) {
                    it.remove();
                }
            }
        }
        return list;
    }

    @Override
    public List<UnionCard> listByCardRootId(Integer unionCardRootId) throws Exception {
        if (unionCardRootId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.eq("root_id", unionCardRootId);
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        return this.selectList(wrapper);
    }

    @Override
    public List<UnionCard> getByBusMemberIdAndMemberIds(Integer memberId, List<Integer> memberids) {
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.in("member_id", memberids.toArray());
        wrapper.exists(new StringBuilder().append("SELECT t.id from t_union_card_binding t WHERE ")
                .append(" t_union_card.root_id = t.root_id ")
                .append(" AND t.member_id = ").append(memberId)
                .append(" AND t.del_status = ").append(CommonConstant.DEL_STATUS_NO).toString());
        wrapper.eq("del_status = ", CommonConstant.DEL_STATUS_NO);

        return this.selectList(wrapper);
    }

    @Override
    public UnionCard getByUnionCardRootIdAndMemberId(Integer rootId, Integer memberId) {
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.eq("root_id", rootId);
        wrapper.eq("member_id", memberId);
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        return this.selectOne(wrapper);
    }

    @Override
    public UnionCard getById(Integer cardId) {
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.eq("id", cardId);
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        return this.selectOne(wrapper);
    }

	@Override
	public List<UnionCard> listByCardRootIdAndMemberIds(Integer rootId, List<Integer> memberIds) {
		EntityWrapper wrapper = new EntityWrapper<>();
		wrapper.eq("root_id", rootId);
		wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
		wrapper.in("member_id",memberIds.toArray());
		return this.selectList(wrapper);
	}

    @Override
    public Map<String, Object> getByMinDiscountByCard(Integer rootId, final Integer busId, List<Integer> unionIds) throws Exception{
        List<UnionCard> list = unionCardMapper.listByRootIdAndUnionIds(rootId, unionIds);
        if(ListUtil.isEmpty(list)){
            throw new BusinessException("没有可用的联盟卡");
        }
        Map<String,Object> data = unionCardMapper.getByMinDiscountByCardList(list, busId, unionIds);
        if(CommonUtil.isEmpty(data)){
            throw new BusinessException("没有可用的联盟卡");
        }
        if(CommonUtil.isEmpty(data.get("discount"))){//没有设置联盟折扣

        }else {
            return data;
        }
        return null;
    }

    @Override
    public void getPhoneCode(Integer busId, String phone) throws Exception{
        if(busId == null || StringUtil.isEmpty(phone)){
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
        if(ListUtil.isEmpty(members)){
            throw new BusinessException("您没有有效的联盟");
        }
        List<Map<String,Object>> list = this.listByPhoneAndMembers(phone,members);
        if(ListUtil.isEmpty(list)){
            sendMsg(phone,busId);
        }else if(list.size() == members.size()){
            throw new BusinessException("该手机号已办理联盟卡");
        }else {
            sendMsg(phone,busId);
        }
    }

    private void sendMsg(String phone, Integer busId) throws Exception{
        String code = RandomKit.getRandomString(6, 0);
        HashMap<String, Object> smsParams = new HashMap<String,Object>();
        smsParams.put("mobiles", phone);
        smsParams.put("content", "办理联盟卡，验证码:" + code);
        smsParams.put("company", company);
        smsParams.put("busId", busId);
        smsParams.put("model", ConfigConstant.SMS_UNION_MODEL);
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("reqdata",smsParams);
        if(smsService.sendSms(param) == 1){
            String phoneKey = RedisKeyUtil.getBindCardPhoneKey(phone);
            redisCacheUtil.set(phoneKey,code,300l);//5分钟
        } else {
            throw new BusinessException("发送失败");
        }
    }

    @Override
    public List<Map<String, Object>> listByPhoneAndMembers(String phone, List<UnionMember> members) {
        List<Map<String,Object>> list = unionCardMapper.listByPhoneAndMembers(phone,members);
        return list;
    }

    @Override
    public Map<String, Object> getUnionInfoByPhone(Integer busId, String phone, String code, Integer unionId) throws Exception{
        if(busId == null || StringUtil.isEmpty(phone) || StringUtil.isEmpty(code) ){
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        String phoneKey = RedisKeyUtil.getBindCardPhoneKey(phone);
        Object obj = redisCacheUtil.get(phoneKey);
        if(CommonUtil.isEmpty(obj)){
            throw new BusinessException("验证码失效");
        }
        if(!code.equals(obj)){
            throw new BusinessException("验证码有误");
        }
        List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
        if(ListUtil.isEmpty(members)){
            throw new BusinessException("您没有有效的联盟");
        }
        List<Integer> unionIds = new ArrayList<Integer>();
        for(UnionMember member : members){
            unionIds.add(member.getUnionId());
        }
        List<UnionMain> mains = unionMainService.listByIds(unionIds);
        if(ListUtil.isEmpty(mains)){
            throw new BusinessException("您没有有效的联盟");
        }
        if(members.size() != mains.size()){
            throw new BusinessException(CommonConstant.OPERATE_ERROR);
        }
        List<UnionMain> unions = new ArrayList<UnionMain>();
        for(UnionMember member : members){
            for(UnionMain main : mains){
                if(member.getUnionId().equals(main.getId())){
                    unions.add(main);
                }
            }
        }
        if(unionId != null){
            Iterator<UnionMember> it = members.iterator();
            while (it.hasNext()){
                UnionMember member = it.next();
                if(!member.getUnionId().equals(unionId)){
                    it.remove();
                }
            }
        }
        if(ListUtil.isEmpty(members)){
            throw new BusinessException("您没有有效的联盟");
        }
        List<Map<String,Object>> list = this.listByPhoneAndMembers(phone,members);
        if(ListUtil.isEmpty(list)){
            Map<String,Object> data = getUnionInfo(members);
            data.put("unions",unions);
            WxPublicUsers users = busUserService.getWxPublicUserByBusId(busId);
            if(users != null){
                data.put("follow",1);
            }
            return data;
        } else if(list.size() == members.size()){
            throw new BusinessException("该手机号已办理联盟卡");
        } else {
            Map<String,Object> data = getUnionInfo(members);
            data.put("unions",unions);
            WxPublicUsers users = busUserService.getWxPublicUserByBusId(busId);
            if(users != null){
                data.put("follow",1);
            }
            return data;
        }
    }

    @Override
    public Map<String, Object> bindCard(UnionCardBindParamVO vo) throws Exception{
        if(CommonUtil.isNotEmpty(vo.getFollow()) && vo.getFollow()){
            if(CommonUtil.isEmpty(vo.getMemberId())){
                throw new BusinessException("请选择粉丝");
            }
        }
        vo.getPhone();

        UnionMember member = unionMemberService.getByBusIdAndUnionId(vo.getBusId(),vo.getUnionId());
        if(member == null){
            throw new BusinessException("您没有该联盟权限");
        }
        if(!unionMemberService.hasWriteAuthority(member)){
            throw new BusinessException("您没有该联盟权限");
        }
        UnionCard card = this.getByPhoneAndMemberId(vo.getPhone(),member.getId());
        if(card != null && DateTimeKit.laterThanNow(card.getValidity())){
            throw new BusinessException("该手机号已办理联盟卡");
        }
        UnionMainCharge charge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(vo.getUnionId(), vo.getCardType(), MainConstant.CHARGE_IS_AVAILABLE_YES);
        if(charge == null){
            throw new BusinessException("联盟未开启该联盟卡类型");
        }
        //if(charge.getChargePrice())
        return null;
    }

    @Override
    public UnionCard getByPhoneAndMemberId(String phone, Integer memberId) throws Exception{
        UnionCardRoot root = unionCardRootService.getByPhone(phone);
        if(root == null){
            return null;
        }
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.eq("root_id",root.getId());
        wrapper.eq("del_status",CommonConstant.DEL_STATUS_NO);
        wrapper.eq("member_id",memberId);

        return this.selectOne(wrapper);
    }

    private Map<String, Object> getUnionInfo(List<UnionMember> members) throws Exception{
        Map<String, Object> data = new HashMap<String, Object>();
        Integer unionId = members.get(0).getUnionId();
        data.put("unionId",unionId);
        UnionMainCharge redCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(unionId, MainConstant.CHARGE_TYPE_RED, MainConstant.CHARGE_IS_AVAILABLE_YES);
        UnionMainCharge blackCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(unionId, MainConstant.CHARGE_TYPE_BLACK, MainConstant.CHARGE_IS_AVAILABLE_YES);
        if(redCharge != null){
            data.put("red",redCharge);
        }
        if(blackCharge != null){
            data.put("black",blackCharge);
        }
        return data;
    }


}
