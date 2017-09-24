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
import com.gt.union.card.constant.CardConstant;
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
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
        wrapper.setSqlSelect(" t1.id, DATE_FORMAT(t1.createtime, '%Y-%m-%d %T') createtime, t1.type, DATE_FORMAT(t1.validity, '%Y-%m-%d %T') validity, t2.phone, t2.number as cardNo, t2.integral");
        Page result = this.selectMapsPage(page, wrapper);
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
            if (no.length() == 11) {//手机号
                return getUnionCardInfoByPhone(no, busId, unionId);
            } else {//卡号
                return getUnionCardInfoByCardNo(no, busId, unionId);
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
        List<UnionCard> list = this.listByPhoneAndMembers(phone,members);//该手机号没升级过联盟卡
        checkCardList(members,list, phone);
        if(members.size() > 0){
            sendMsg(phone,busId);
        } else if(members.size() <= 0 && list.size() > 0){
            throw new BusinessException("该手机号已办理联盟卡");
        } else if(members.size() <= 0 && list.size() == 0){
            throw new BusinessException("您没有有效的联盟");
        } else {
            throw new BusinessException(CommonConstant.SYS_ERROR);
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

    /**
     * 判断是否还可以升级联盟卡
     * @param members  我的盟员列表
     * @param list
     * @throws Exception
     */
    private void checkCardList(List<UnionMember> members, List<UnionCard> list, String phone) throws Exception{
        Map<Integer,UnionMainCharge> blackMap = new HashMap<Integer,UnionMainCharge>();
        Map<Integer,UnionMainCharge> redMap = new HashMap<Integer,UnionMainCharge>();
        for(UnionMember member : members){
            UnionMainCharge blackCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(member.getUnionId(),MainConstant.CHARGE_TYPE_BLACK, MainConstant.CHARGE_IS_AVAILABLE_YES);
            UnionMainCharge redCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(member.getUnionId(),MainConstant.CHARGE_TYPE_RED, MainConstant.CHARGE_IS_AVAILABLE_YES);
            blackMap.put(member.getUnionId(),blackCharge);
            redMap.put(member.getUnionId(),redCharge);
        }
        Iterator<UnionMember> im = members.iterator();
        while (im.hasNext()){
            UnionMember member = im.next();
            List<UnionMember> memberList = unionMemberService.listWriteByUnionId(member.getUnionId());//我加入该联盟的盟员列表
            List<UnionCard> cards = this.listByPhoneAndMembers(phone,memberList);//该手机号在其他盟员升级的联盟卡列表
            UnionMainCharge blackCharge = blackMap.get(member.getUnionId());
            UnionMainCharge redCharge = redMap.get(member.getUnionId());
            if(ListUtil.isEmpty(cards)){//没有升级过联盟卡，则需要判断售卡佣金比例
                if(blackCharge.getIsCharge() == MainConstant.CHARGE_IS_CHARGE_YES ) {//黑卡收费
                    if(!isUnionCardChargePercent(member.getUnionId())){//是否设置了售卡佣金比例
                        im.remove();
                    }
                }
            }else {//升级过联盟卡
                boolean flag = true;//标识设售卡佣金 false：需要设售卡佣金  true：不需要设售卡佣金
                for(UnionCard card : cards){
                    if(card.getMemberId().equals(member.getId())) {//在本盟员下升级的
                        if(card.getType() == CardConstant.TYPE_BLACK){//黑卡
                            if(CommonUtil.isNotEmpty(card.getIsCharge()) && card.getIsCharge() == CardConstant.IS_CHARGE_YES){//如果是收费升级了黑卡,判断是否开启了红卡，开启了可以升级上
                              if(redCharge == null){//未开启红卡，不可继续升级
                                  flag = true;
                                  im.remove();
                                  break;
                              }else {//如果开启了红卡，则要判断售卡佣金比例
                                  int count = unionCardMapper.countByMemberIdsAndType(memberList, member.getId(), CardConstant.TYPE_RED, MainConstant.CHARGE_IS_CHARGE_YES, phone, MainConstant.CHARGE_IS_AVAILABLE_YES);
                                  if(count == 0){//没有在其他盟员升级红卡
                                      flag = false;//标识需要设售卡佣金
                                  }
                              }
                            } else{//如果升级的是免费卡
                                //判断旧会员不需要收费且没有开启红卡，不可以继续升级
                                if(blackCharge.getIsOldCharge() == MainConstant.CHARGE_OLD_IS_NO && redCharge == null){
                                    flag = true;
                                    im.remove();
                                    break;
                                }else if(blackCharge.getIsOldCharge() == MainConstant.CHARGE_OLD_IS_YES && (redCharge != null || blackCharge.getIsCharge() == MainConstant.CHARGE_IS_CHARGE_YES)){//开启了旧会员收费,如果获取开启了红
                                    //TODO 联盟卡收费
                                    if(blackCharge.getIsCharge() == MainConstant.CHARGE_IS_CHARGE_YES){//开启黑卡收费
                                        int count = unionCardMapper.countByMemberIdsAndType(memberList, member.getId(), CardConstant.TYPE_BLACK, MainConstant.CHARGE_IS_CHARGE_YES, phone, MainConstant.CHARGE_IS_AVAILABLE_YES);
                                        if(count > 0){//在其他盟员升级收费黑卡
                                            flag = true;
                                        }else{
                                            flag = false;//标识需要设售卡佣金
                                        }
                                    }
                                    if(redCharge != null){//开启红卡收费
                                        //在其他盟员升级过红卡
                                        int count = unionCardMapper.countByMemberIdsAndType(memberList, member.getId(), CardConstant.TYPE_RED, MainConstant.CHARGE_IS_CHARGE_YES, phone, MainConstant.CHARGE_IS_AVAILABLE_YES);
                                        if(count > 0){//在其他盟员升级红卡
                                            flag = true;
                                        }else{
                                            if(!flag){
                                                flag = false;//标识需要设售卡佣金
                                            }
                                        }
                                    }
                                }
                            }
                        } else if(card.getType() == CardConstant.TYPE_RED){//红卡 //不可以再升级了
                            flag = true;
                            im.remove();
                            break;
                        }
                    }
                    break;
                }
                if(!flag){//在我的盟员下继续升级，判断需要设置佣金比例
                    Boolean percent = isUnionCardChargePercent(member.getUnionId());
                    if(!percent){//没有设置售卡佣金比例
                        im.remove();
                    }
                }
            }
        }
        if(ListUtil.isEmpty(members)){
            throw new BusinessException("您没有有效的联盟");
        }


    }

    /**
     * 判断收费的联盟卡是否设置了售卡佣金，且和为100
     * @param unionId
     * @return
     * @throws Exception
     */
    private boolean isUnionCardChargePercent(Integer unionId) throws Exception{
        List<UnionMember> memberList = unionMemberService.listWriteByUnionId(unionId);//具有写权限的商家没有
        if(ListUtil.isEmpty(memberList)){
           return false;
        }
        double percent = 0;
        for(UnionMember unionMember : memberList){
            if(CommonUtil.isEmpty(unionMember.getCardDividePercent()) || unionMember.getCardDividePercent() == 0){//没有设置收卡佣金
                return false;
            }
            percent = BigDecimalUtil.add(percent,unionMember.getCardDividePercent()).doubleValue();
        }
        if(percent != 100){
            return false;
        }
        return true;
    }

    @Override
    public List<UnionCard> listByPhoneAndMembers(String phone, List<UnionMember> members) {
        List<UnionCard> list = unionCardMapper.listByPhoneAndMembers(phone,members);
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
        if(unionId != null){
            Iterator<UnionMember> im = members.iterator();
            while (im.hasNext()){
                UnionMember member = im.next();
                if(!member.getUnionId().equals(unionId)){
                    im.remove();
                }
            }
        }
        List<UnionCard> list = this.listByPhoneAndMembers(phone,members);
        Map<String,Object> data = getUnionInfo(members, list, phone, unionId, busId);
        WxPublicUsers users = busUserService.getWxPublicUserByBusId(busId);
        if(users != null){
            data.put("follow",1);
        }else {
            data.put("follow",0);
        }
        return data;

    }

    /**
     *
     * @param members   有效的盟员列表
     * @param list      升级的联盟卡列表
     * @return
     * @throws Exception
     */
    private Map<String, Object> getUnionInfo(List<UnionMember> members, List<UnionCard> list, String phone, Integer unionId, Integer busId) throws Exception{
        checkCardList(members,list,phone);//可以升级的联盟列表

        UnionMember member = members.get(0);//可办卡的联盟
        List<UnionMain> unions = new ArrayList<UnionMain>();//封装联盟列表

        List<UnionMain> mains = unionMainService.listWriteByBusId(busId);
        for(UnionMember unionMember : members){
            for(UnionMain main : mains){
                if(unionMember.getUnionId().equals(main.getId())){
                    unions.add(main);
                    break;
                }
            }
        }
        if(unionId == null){
            unionId = member.getUnionId();
        }
        Map<String, Object> data = new HashMap<String, Object>();
        UnionCard card = this.getByPhoneAndMemberId(phone,member.getId());//该盟员下升级的联盟卡 不一定有效期内
        if(card == null){
            List<UnionMember> memberList = unionMemberService.listWriteByUnionId(member.getUnionId());
            List<UnionCard> cards = this.listByPhoneAndMembers(phone,memberList);//该手机号在其他盟员升级的联盟卡列表
            if(ListUtil.isEmpty(cards)){
                UnionMainCharge redCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(unionId, MainConstant.CHARGE_TYPE_RED, MainConstant.CHARGE_IS_AVAILABLE_YES);
                UnionMainCharge blackCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(unionId, MainConstant.CHARGE_TYPE_BLACK, MainConstant.CHARGE_IS_AVAILABLE_YES);
                if(redCharge != null){
                    Map<String, Object> red = new HashMap<String, Object>();
                    red.put("price",redCharge.getChargePrice());
                    red.put("termTime",DateTimeKit.format(DateTimeKit.addDays(redCharge.getValidityDay()),"yyyy-MM-dd"));
                    data.put("red",red);
                }
                if(blackCharge != null){
                    Map<String, Object> black = new HashMap<String, Object>();
                    black.put("price",blackCharge.getChargePrice());
                    black.put("termTime",blackCharge.getIsCharge() == 1 ? DateTimeKit.format(DateTimeKit.addDays(blackCharge.getValidityDay()),"yyyy-MM-dd") : null);//收费的，有效期
                    data.put("black",black);
                }
            }else {//在其他盟员升级了 且在有效期
                UnionMainCharge redCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(unionId, MainConstant.CHARGE_TYPE_RED, MainConstant.CHARGE_IS_AVAILABLE_YES);
                UnionMainCharge blackCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(unionId, MainConstant.CHARGE_TYPE_BLACK, MainConstant.CHARGE_IS_AVAILABLE_YES);
                if(redCharge != null){
                    Map<String, Object> red = new HashMap<String, Object>();
                    red.put("price",null);
                    red.put("termTime",DateTimeKit.format(DateTimeKit.addDays(redCharge.getValidityDay()),"yyyy-MM-dd"));
                    data.put("red",red);
                }
                if(blackCharge != null){
                    Map<String, Object> black = new HashMap<String, Object>();
                    black.put("price",null);
                    black.put("termTime",blackCharge.getIsCharge() == 1 ? DateTimeKit.format(DateTimeKit.addDays(blackCharge.getValidityDay()),"yyyy-MM-dd") : null);//收费的，有效期
                    data.put("black",black);
                }

            }
        }else {//在该盟员下升级
            if(card.getType() == CardConstant.TYPE_BLACK){//黑卡
                //如果是免费的，则判断旧会员是否需要收费，需要，则可以升级
                if(card.getIsCharge() == CardConstant.IS_CHARGE_NO){
                    UnionMainCharge blackCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(unionId, MainConstant.CHARGE_TYPE_BLACK, MainConstant.CHARGE_IS_AVAILABLE_YES);
                    if(blackCharge.getIsOldCharge() == MainConstant.CHARGE_OLD_IS_YES){//旧会员收费
                        Map<String, Object> black = new HashMap<String, Object>();
                        black.put("price",blackCharge.getChargePrice());
                        black.put("termTime",blackCharge.getValidityDay() == null ? null : DateTimeKit.format(DateTimeKit.addDays(blackCharge.getValidityDay()),"yyyy-MM-dd"));
                        data.put("black",black);
                    }
                }
                //如果是收费的，判断是否有开启红卡，开启了则可以升级
                UnionMainCharge redCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(unionId, MainConstant.CHARGE_TYPE_RED, MainConstant.CHARGE_IS_AVAILABLE_YES);
                if(redCharge != null){//开启了红卡
                    Map<String, Object> red = new HashMap<String, Object>();
                    red.put("price",redCharge.getChargePrice());
                    red.put("termTime",DateTimeKit.format(DateTimeKit.addDays(redCharge.getValidityDay()),"yyyy-MM-dd"));
                    data.put("red",red);
                }
            }
        }
        data.put("unions",unions);//可以升级的联盟列表
        data.put("unionId",unionId);//当前的联盟
        return data;
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
        if(vo.getCardType() == CardConstant.TYPE_BLACK){//黑卡
            if(CommonUtil.isEmpty(charge.getChargePrice())){//不收费，直接办理
                UnionCardRoot root = unionCardRootService.getByPhone(vo.getPhone());
                if(root == null){
                    root = unionCardRootService.createUnionCardRoot(vo.getPhone());
                }
                UnionCard unionCard = createUnionCard(root.getId(), vo.getCardType(), member.getId(), DateTimeKit.parse(CardConstant.CARD_FREE_VALIDITY,"yyyy-MM-dd HH:mm:ss"), 1);
                if(CommonUtil.isNotEmpty(vo.getMemberId())){
                    UnionCardBinding binding = unionCardBindingService.getByCardRootIdAndMemberId(root.getId(),vo.getMemberId());
                    if(binding == null){
                        UnionCardBinding unionCardBinding = unionCardBindingService.createUnionCardBinding(root.getId(), vo.getMemberId());
                    }
                }
            }
            return null;
        }
        if(CommonUtil.isNotEmpty(charge.getChargePrice())){//不收费，直接办理
            //扫码
            //RedisKeyUtil.getBindCardStatusKey()
        }
        return null;
    }

    /**
     * 插入联盟卡
     * @param rootId
     * @param cardType
     * @param memberId
     * @param validity
     * @param isCharge
     * @return
     */
    private UnionCard createUnionCard(Integer rootId, Integer cardType, Integer memberId, Date validity, Integer isCharge){
        UnionCard card = new UnionCard();
        card.setCreatetime(new Date());
        card.setType(cardType);
        card.setDelStatus(CommonConstant.DEL_STATUS_NO);
        card.setIsCharge(isCharge);
        card.setMemberId(memberId);
        card.setValidity(validity);
        card.setRootId(rootId);
        this.insert(card);
        return card;
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

    @Override
    public List<Map<String, Object>> listByUnionId(final Integer unionId, final Integer busId, final String cardNo, final String phone) throws Exception{
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (busId == null) {
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
        wrapper.setSqlSelect(" t1.id, DATE_FORMAT(t1.createtime, '%Y-%m-%d %T') createtime, t1.type, DATE_FORMAT(t1.validity, '%Y-%m-%d %T') validity, t2.phone, t2.number as cardNo, t2.integral");
        List<Map<String, Object>> list = this.selectMaps(wrapper);
        return list;
    }

    @Override
    public HSSFWorkbook exportCardList(String[] titles, String[] contentName, List<Map<String, Object>> list) {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFCellStyle styleCenter = wb.createCellStyle();
        HSSFSheet sheet = createHSSFSheet(titles, wb, styleCenter);
        for (int i=0;i<list.size();i++) {
            Map<String, Object> item = list.get(i);
            HSSFRow row = sheet.createRow(i + 1);
            for(int j=0;j<titles.length;j++){
                String key = contentName[j];
                String c = CommonUtil.isEmpty(item.get(key)) ? "" : item.get(key).toString();
                if("type".equals(key)){//卡类型
                    if("1".equals(c)){
                        c = "黑卡";
                    }else if("2".equals(c)){
                        c = "红卡";
                    }
                }else if("validity".equals(key)){//卡有效期
                    if(StringUtil.isEmpty(c)){
                        c = "无";
                    }else {
                        c = DateTimeKit.format(DateTimeKit.parse(c,DateTimeKit.DEFAULT_DATETIME_FORMAT), "yyyy/MM/dd");
                    }
                }else if("createtime".equals(key)){//升级时间
                    c = DateTimeKit.format(DateTimeKit.parse(c,DateTimeKit.DEFAULT_DATETIME_FORMAT), "yyyy/MM/dd HH:mm");
                }else if("integral".equals(key)){//积分
                    if(StringUtil.isEmpty(c)){
                        c = "0";
                    }
                }
                HSSFCell cell = row.createCell(j);
                cell.setCellValue(c);
                cell.setCellStyle(styleCenter);
            }

        }
        return wb;
    }

    /**
     * 创建sheet
     * @param titles
     * @param wb
     * @param styleCenter
     * @return
     */
    private HSSFSheet createHSSFSheet(String[] titles, HSSFWorkbook wb, HSSFCellStyle styleCenter){
        HSSFSheet sheet = wb.createSheet("sheet1");
        HSSFRow rowTitle = sheet.createRow(0);
        HSSFCellStyle styleTitle = wb.createCellStyle();
        styleTitle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        HSSFFont fontTitle = wb.createFont();
        fontTitle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        fontTitle.setFontName("宋体");
        fontTitle.setFontHeight((short) 200);
        styleTitle.setFont(fontTitle);

        HSSFCell cellTitle = null;
        for(int i=0;i<titles.length;i++){
            cellTitle = rowTitle.createCell(i);
            cellTitle.setCellValue(titles[i]);
            cellTitle.setCellStyle(styleTitle);
        }
        styleCenter.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        return sheet;
    }

}
