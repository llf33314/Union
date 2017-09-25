package com.gt.union.card.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.api.bean.session.Member;
import com.gt.api.bean.session.WxPublicUsers;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.api.entity.result.UnionDiscountResult;
import com.gt.union.brokerage.constant.BrokerageConstant;
import com.gt.union.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.card.constant.CardConstant;
import com.gt.union.card.entity.*;
import com.gt.union.card.mapper.UnionCardMapper;
import com.gt.union.card.service.*;
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
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
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

    @Value("${union.url}")
    private String unionUrl;

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainChargeService unionMainChargeService;

    @Autowired
    private IBusUserService busUserService;

    @Autowired
    private IUnionCardUpgradePayService unionCardUpgradePayService;

    @Autowired
    private IUnionCardUpgradeService unionCardUpgradeService;

    @Autowired
    private IUnionBrokerageIncomeService unionBrokerageIncomeService;

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
        List<UnionCard> list = this.listByPhoneAndMembers(phone,members);
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
    private List<Map<Integer,List<Map<String,Object>>>> checkCardList(List<UnionMember> members, List<UnionCard> list, String phone) throws Exception{
        List<Map<Integer,List<Map<String,Object>>>> result = new ArrayList<Map<Integer,List<Map<String,Object>>>>();
        //定义收费规则
        Map<Integer,UnionMainCharge> blackMap = new HashMap<Integer,UnionMainCharge>();
        Map<Integer,UnionMainCharge> redMap = new HashMap<Integer,UnionMainCharge>();
        for(UnionMember member : members){
            UnionMainCharge blackCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(member.getUnionId(),MainConstant.CHARGE_TYPE_BLACK, MainConstant.CHARGE_IS_AVAILABLE_YES);
            UnionMainCharge redCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(member.getUnionId(),MainConstant.CHARGE_TYPE_RED, MainConstant.CHARGE_IS_AVAILABLE_YES);
            blackMap.put(member.getUnionId(),blackCharge);
            redMap.put(member.getUnionId(),redCharge);
        }
        Iterator<UnionMember> im = members.iterator();
        int flag = 0;//标志是否升级了联盟卡
        while (im.hasNext()) {
            UnionMember member = im.next();
            Boolean percent = isUnionCardChargePercent(member.getUnionId());
            List<UnionMember> memberList = unionMemberService.listWriteByUnionId(member.getUnionId());//我加入该联盟的盟员列表
            Iterator<UnionMember> itm = memberList.iterator();
            //得到其他盟员列表
            while (itm.hasNext()) {
                UnionMember unionMember = itm.next();
                if (unionMember.getId().equals(member.getId())) {
                    itm.remove();
                    break;
                }
            }
            UnionCard unionCard = this.getByPhoneAndMemberId(phone, member.getId(),true);//本盟员升级的联盟卡
            UnionMainCharge blackCharge = blackMap.get(member.getUnionId());
            UnionMainCharge redCharge = redMap.get(member.getUnionId());
            List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
            if (unionCard == null) {//本盟员下升级了联盟卡
                if(unionCard.getType() == CardConstant.TYPE_BLACK){//黑卡
                    if(unionCard.getIsCharge() == CardConstant.IS_CHARGE_YES){//收费黑卡
                        if (redCharge != null) {//开启红卡
                            int count = unionCardMapper.countByMemberIdsAndType(memberList, CardConstant.TYPE_RED, MainConstant.CHARGE_IS_CHARGE_YES, phone, MainConstant.CHARGE_IS_AVAILABLE_YES);
                            if (count > 0) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                Map<String, Object> red = new HashMap<String, Object>();
                                red.put("price", 0);
                                red.put("termTime", DateTimeKit.format(DateTimeKit.addDays(redCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                                map.put("red", red);
                                dataList.add(map);
                            } else {
                                if (percent) {
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    Map<String, Object> red = new HashMap<String, Object>();
                                    red.put("price", redCharge.getChargePrice());
                                    red.put("termTime", DateTimeKit.format(DateTimeKit.addDays(redCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                                    map.put("red", red);
                                    dataList.add(map);
                                }
                            }
                        }
                    }else {//免费黑卡
                        //黑卡收费
                        if (blackCharge.getIsCharge() == MainConstant.CHARGE_IS_CHARGE_YES) {
                            int count = unionCardMapper.countByMemberIdsAndType(memberList, CardConstant.TYPE_BLACK, MainConstant.CHARGE_IS_CHARGE_YES, phone, MainConstant.CHARGE_IS_AVAILABLE_YES);
                            if(count > 0){
                                Map<String, Object> map = new HashMap<String, Object>();
                                Map<String, Object> black = new HashMap<String, Object>();
                                black.put("price", 0);
                                black.put("termTime", DateTimeKit.format(DateTimeKit.addDays(blackCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                                map.put("black", black);
                                dataList.add(map);
                            }else {
                                if (percent) {
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    Map<String, Object> black = new HashMap<String, Object>();
                                    black.put("price", blackCharge.getChargePrice());
                                    black.put("termTime", DateTimeKit.format(DateTimeKit.addDays(blackCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                                    map.put("black", black);
                                    dataList.add(map);
                                }
                            }
                        }
                        //开启红卡
                        if (redCharge != null) {
                            int count = unionCardMapper.countByMemberIdsAndType(memberList, CardConstant.TYPE_RED, MainConstant.CHARGE_IS_CHARGE_YES, phone, MainConstant.CHARGE_IS_AVAILABLE_YES);
                            if (count > 0) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                Map<String, Object> red = new HashMap<String, Object>();
                                red.put("price", 0);
                                red.put("termTime", DateTimeKit.format(DateTimeKit.addDays(redCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                                map.put("red", red);
                                dataList.add(map);
                            } else {
                                if (percent) {
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    Map<String, Object> red = new HashMap<String, Object>();
                                    red.put("price", redCharge.getChargePrice());
                                    red.put("termTime", DateTimeKit.format(DateTimeKit.addDays(redCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                                    map.put("red", red);
                                    dataList.add(map);
                                }
                            }
                        }
                    }
                }
                if( flag == 0){
                    flag = 1;
                }
            } else {//不在本盟员下升级了联盟卡
                if (blackCharge.getIsCharge() == MainConstant.CHARGE_IS_CHARGE_YES) {//黑卡收费
                    int count = unionCardMapper.countByMemberIdsAndType(memberList, CardConstant.TYPE_BLACK, MainConstant.CHARGE_IS_CHARGE_YES, phone, MainConstant.CHARGE_IS_AVAILABLE_YES);
                    if(count > 0){
                        Map<String, Object> map = new HashMap<String, Object>();
                        Map<String, Object> black = new HashMap<String, Object>();
                        black.put("price", 0);
                        black.put("termTime", DateTimeKit.format(DateTimeKit.addDays(blackCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                        map.put("black", black);
                        dataList.add(map);
                    }else {
                        if (percent) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            Map<String, Object> black = new HashMap<String, Object>();
                            black.put("price", blackCharge.getChargePrice());
                            black.put("termTime", DateTimeKit.format(DateTimeKit.addDays(blackCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                            map.put("black", black);
                            dataList.add(map);
                        }
                    }
                }else {
                    Map<String, Object> map = new HashMap<String, Object>();
                    Map<String, Object> black = new HashMap<String, Object>();
                    black.put("price", blackCharge.getChargePrice());
                    map.put("black", black);
                    dataList.add(map);
                }
                if (redCharge != null) {//开启红卡
                    int count = unionCardMapper.countByMemberIdsAndType(memberList, CardConstant.TYPE_RED, MainConstant.CHARGE_IS_CHARGE_YES, phone, MainConstant.CHARGE_IS_AVAILABLE_YES);
                    if (count > 0) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        Map<String, Object> red = new HashMap<String, Object>();
                        red.put("price", 0);
                        red.put("termTime", DateTimeKit.format(DateTimeKit.addDays(redCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                        map.put("red", red);
                        dataList.add(map);
                    } else {
                        if (percent) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            Map<String, Object> red = new HashMap<String, Object>();
                            red.put("price", redCharge.getChargePrice());
                            red.put("termTime", DateTimeKit.format(DateTimeKit.addDays(redCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                            map.put("red", red);
                            dataList.add(map);
                        }
                    }
                }
            }
            if(ListUtil.isEmpty(dataList)){
                Map<Integer,List<Map<String,Object>>> data = new HashMap<Integer,List<Map<String,Object>>>();
                data.put(member.getId(),dataList);
                result.add(data);
            }
        }
        if(ListUtil.isEmpty(result)){
            if(flag == 1){
                throw new BusinessException("该手机号已办联盟卡");
            }else {
                throw new BusinessException("您没有有效的联盟");
            }
        }
        return result;

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
        List<Map<Integer,List<Map<String,Object>>>> dataList = checkCardList(members,list,phone);//可以升级的联盟列表
        Iterator<UnionMember> it = members.iterator();
        while (it.hasNext()){
            UnionMember member = it.next();
            for(Map<Integer,List<Map<String,Object>>> map : dataList){
                if(!map.containsKey(member.getId())){//不存在
                    it.remove();
                }
            }
        }
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
        UnionMember member = members.get(0);
        if(unionId == null){
            unionId = member.getUnionId();
        }
        Map<String, Object> data = new HashMap<String, Object>();
        for(Map<Integer,List<Map<String,Object>>> arr : dataList){
            if(arr.containsKey(member.getId())){
                List<Map<String,Object>> chargeList = arr.get(member.getId());
                for(Map<String,Object> map : chargeList){
                    if(CommonUtil.isNotEmpty(map.get("red"))){
                        data.put("red", map.get("red"));
                    }
                    if(CommonUtil.isNotEmpty(map.get("black"))){
                        data.put("black", map.get("black"));
                    }
                }
                break;
            }
        }
        data.put("unions",unions);//可以升级的联盟列表
        data.put("unionId",unionId);//当前的联盟
        return data;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> bindCard(UnionCardBindParamVO vo) throws Exception{
        if(CommonUtil.isNotEmpty(vo.getFollow()) && vo.getFollow()){
            if(CommonUtil.isEmpty(vo.getMemberId())){
                throw new BusinessException("请选择粉丝");
            }
        }
        vo.getPhone();
        //检验联盟有效期
        unionMainService.checkUnionMainValid(vo.getUnionId());
        UnionMember member = unionMemberService.getByBusIdAndUnionId(vo.getBusId(),vo.getUnionId());
        if(member == null){
            throw new BusinessException("您没有该联盟权限");
        }
        if(!unionMemberService.hasWriteAuthority(member)){
            throw new BusinessException("您没有该联盟权限");
        }
        UnionCard card = this.getByPhoneAndMemberId(vo.getPhone(),member.getId(), true);
        if(card != null && card.getType().equals(vo.getCardType())){
            UnionMainCharge blackCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(vo.getUnionId(), CardConstant.TYPE_BLACK, MainConstant.CHARGE_IS_AVAILABLE_YES);
            if(!(card.getIsCharge() == CardConstant.IS_CHARGE_NO && blackCharge.getIsOldCharge() == MainConstant.CHARGE_OLD_IS_YES && card.getType() == CardConstant.TYPE_BLACK)){
                throw new BusinessException("该手机号已办理联盟卡");
            }
        }
        UnionMainCharge charge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(vo.getUnionId(), vo.getCardType(), MainConstant.CHARGE_IS_AVAILABLE_YES);
        if(charge == null){
            throw new BusinessException("联盟未开启该联盟卡类型");
        }
        Double price = charge.getChargePrice();//收费价格
        Map<String, Object> data = new HashMap<String, Object>();
        if(CommonUtil.isEmpty(price) || price == 0){//不收费，直接办理
            UnionCard unionCard = this.getByPhoneAndMemberId(vo.getPhone(),member.getId(), false);
            if(unionCard == null){
               UnionCardRoot root = unionCardRootService.getByPhone(vo.getPhone());
               Integer rootId = null;
               if(root == null){
                   UnionCardRoot unionCardRoot = unionCardRootService.createUnionCardRoot(vo.getPhone());
                   rootId = unionCardRoot.getId();
               }else {
                   rootId = root.getId();
               }
                Date time = (charge.getValidityDay() == null || charge.getValidityDay() == 0) ? DateTimeKit.parse(CardConstant.CARD_FREE_VALIDITY,"yyyy-MM-dd HH:mm:ss" ): DateTimeKit.addDate(new Date(),charge.getValidityDay());
                this.createUnionCard(rootId,vo.getCardType(),member.getId(),time,0);
                if(CommonUtil.isNotEmpty(vo.getMemberId())){
                    UnionCardBinding binding = unionCardBindingService.getByCardRootIdAndMemberId(rootId,vo.getMemberId());
                    if(binding == null){
                        unionCardBindingService.createUnionCardBinding(rootId,vo.getMemberId());
                    }
                }
            }else {
                Integer rootId = unionCard.getRootId();
                Date time = (charge.getValidityDay() == null || charge.getValidityDay() == 0) ? DateTimeKit.parse(CardConstant.CARD_FREE_VALIDITY,"yyyy-MM-dd HH:mm:ss" ): DateTimeKit.addDate(new Date(),charge.getValidityDay());
                UnionCard upCard =  new UnionCard();
                upCard.setId(unionCard.getId());
                unionCard.setValidity(time);
                unionCard.setIsCharge(CardConstant.IS_CHARGE_NO);
                this.updateById(upCard);
                if(CommonUtil.isNotEmpty(vo.getMemberId())){
                    UnionCardBinding binding = unionCardBindingService.getByCardRootIdAndMemberId(rootId,vo.getMemberId());
                    if(binding == null){
                        unionCardBindingService.createUnionCardBinding(rootId,vo.getMemberId());
                    }
                }
            }
        }else {//收费
            StringBuilder sb = new StringBuilder("?");
            sb.append("&phone=").append(vo.getPhone())
                    .append("&memberId=").append(vo.getMemberId())
                    .append("&unionId=").append(vo.getUnionId())
                    .append("&cardtype=").append(vo.getCardType());
            data.put("qrurl",unionUrl + "/qrCode"+sb.toString());
        }
        return data;
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
    public UnionCard getByPhoneAndMemberId(String phone, Integer memberId, Boolean isValidity) throws Exception{
        UnionCardRoot root = unionCardRootService.getByPhone(phone);
        if(root == null){
            return null;
        }
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.eq("root_id",root.getId());
        wrapper.eq("del_status",CommonConstant.DEL_STATUS_NO);
        wrapper.eq("member_id",memberId);
        if(isValidity){
            wrapper.gt("validity",new Date());
        }
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
    public Map<String, Object> getUnionCardIndex(Integer busId, Member member) {
        return null;
    }

    @Override
    public Map<String, Object> createQRCode(Integer busId, String phone, Integer memberId, Integer unionId, Integer cardType) throws Exception{
        UnionMainCharge charge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(unionId, cardType, MainConstant.CHARGE_IS_AVAILABLE_YES);
        Double price = charge.getChargePrice();//收费价格
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("totalFee",price);
        data.put("busId", PropertiesUtil.getDuofenBusId());
        data.put("sourceType", 1);//是否墨盒支付
        data.put("payWay",0);//系统判断支付方式
        data.put("isreturn",0);//0：不需要同步跳转
        data.put("model", ConfigConstant.PAY_MODEL);
        String only = String.valueOf(System.currentTimeMillis());
        String orderNo = CardConstant.ORDER_PREFIX + only;
        String encrypt = EncryptUtil.encrypt(PropertiesUtil.getEncryptKey(), orderNo);
        encrypt = URLEncoder.encode(encrypt,"UTF-8");
        WxPublicUsers publicUser = busUserService.getWxPublicUserByBusId(PropertiesUtil.getDuofenBusId());
        data.put("notifyUrl", PropertiesUtil.getUnionUrl() + "/unionCard/79B4DE7C/paymentSuccess/"+encrypt + "/" + only);
        data.put("orderNum", orderNo);//订单号
        data.put("payBusId", busId);//支付的商家id
        data.put("isSendMessage",0);//不推送
        data.put("appid",publicUser.getAppid());//appid
        data.put("desc", "办理联盟卡");
        data.put("appidType",0);//公众号
        data.put("only", only);
        data.put("unionId",unionId);
        data.put("phone",phone);
        data.put("memberId",memberId);
        data.put("cardType",cardType);
        String statusKey = RedisKeyUtil.getBindCardPayStatusKey(only);
        String paramKey = RedisKeyUtil.getBindCardPayParamKey(only);
        redisCacheUtil.set(paramKey, JSON.toJSONString(data), 360l);//5分钟
        redisCacheUtil.set(statusKey,ConfigConstant.USER_ORDER_STATUS_001,300l);//等待扫码状态
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payBindCardSuccess(String encrypt, String only) throws Exception{
        //解密订单号
        String orderNo = EncryptUtil.decrypt(PropertiesUtil.getEncryptKey(),encrypt);
        String statusKey = RedisKeyUtil.getBindCardPayStatusKey(only);
        String paramKey = RedisKeyUtil.getBindCardPayParamKey(only);
        Object obj = redisCacheUtil.get(paramKey);
        Map<String,Object> result = JSONObject.parseObject(obj.toString(),Map.class);
        Double payMoney = CommonUtil.toDouble(result.get("totalFee"));
        Integer unionId = CommonUtil.toInteger(result.get("unionId"));
        Integer cardType = CommonUtil.toInteger(result.get("cardType"));
        Integer payBusId = CommonUtil.toInteger(result.get("payBusId"));
        Integer memberId = CommonUtil.toInteger(CommonUtil.isEmpty(result.get("memberId"))?0 : result.get("memberId"));
        String phone = CommonUtil.toString(result.get("phone"));
        String orderDesc = CommonUtil.toString(result.get("desc"));
        UnionCardUpgradePay pay = unionCardUpgradePayService.createCardUpgreadePay(orderNo, 2, 1, payMoney, orderDesc);
        UnionMember member = unionMemberService.getByBusIdAndUnionId(payBusId,unionId);
        UnionCard unionCard = this.getByPhoneAndMemberId(phone,member.getId(), false);
        UnionMainCharge charge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(unionId, cardType, MainConstant.CHARGE_IS_AVAILABLE_YES);
        Integer rootId = null;
        Integer cardId = null;
        Date validity = null;
        if(unionCard == null){
            UnionCardRoot root = unionCardRootService.getByPhone(phone);
            if(root == null){
                UnionCardRoot unionCardRoot = unionCardRootService.createUnionCardRoot(phone);
                rootId = unionCardRoot.getId();
            }else {
                rootId = root.getId();
            }
            Date time = (charge.getValidityDay() == null || charge.getValidityDay() == 0) ? DateTimeKit.parse(CardConstant.CARD_FREE_VALIDITY,"yyyy-MM-dd HH:mm:ss" ): DateTimeKit.addDate(new Date(),charge.getValidityDay());
            UnionCard card = this.createUnionCard(rootId,cardType,member.getId(),time,0);
            if(memberId != 0){
                UnionCardBinding binding = unionCardBindingService.getByCardRootIdAndMemberId(rootId,memberId);
                if(binding == null){
                    unionCardBindingService.createUnionCardBinding(rootId,memberId);
                }
            }
            cardId = card.getId();
            validity = card.getValidity();
        }else {
            rootId = unionCard.getRootId();
            cardId = unionCard.getId();
            Date time = (charge.getValidityDay() == null || charge.getValidityDay() == 0) ? DateTimeKit.parse(CardConstant.CARD_FREE_VALIDITY,"yyyy-MM-dd HH:mm:ss" ): DateTimeKit.addDate(new Date(),charge.getValidityDay());
            UnionCard upCard =  new UnionCard();
            upCard.setId(unionCard.getId());
            //收费黑卡升红卡
            if(unionCard.getType() == CardConstant.TYPE_BLACK && unionCard.getIsCharge() == CardConstant.IS_CHARGE_YES && cardType == CardConstant.TYPE_RED){
                unionCard.setValidity(DateTimeKit.addDays(unionCard.getValidity(),charge.getValidityDay()));
            }else {
                unionCard.setValidity(time);
            }
            unionCard.setIsCharge(CardConstant.IS_CHARGE_NO);
            this.updateById(upCard);
            validity = upCard.getValidity();
        }
        //绑定联盟卡和粉丝信息
        if(memberId != 0){
            UnionCardBinding binding = unionCardBindingService.getByCardRootIdAndMemberId(rootId,memberId);
            if(binding == null){
                unionCardBindingService.createUnionCardBinding(rootId,memberId);
            }
        }
        UnionCardUpgrade upgrade = unionCardUpgradeService.createCardUpgrade(cardId, member.getId(), cardType, validity, pay.getId());
        List<UnionMember> list = unionMemberService.listWriteByUnionId(member.getUnionId());
        //添加售卡分成
        List<UnionBrokerageIncome> incomes = new ArrayList<UnionBrokerageIncome>();
        for(UnionMember unionMember : list){
            Double money = BigDecimalUtil.multiply(payMoney,unionMember.getCardDividePercent()).doubleValue();
            UnionBrokerageIncome income = new UnionBrokerageIncome();
            income.setType(BrokerageConstant.SOURCE_TYPE_CARD);
            income.setCreatetime(new Date());
            income.setDelStatus(CommonConstant.DEL_STATUS_NO);
            income.setBusId(unionMember.getBusId());
            income.setCardId(cardId);
            income.setMoney(money);
            incomes.add(income);
        }
        unionBrokerageIncomeService.insertBatch(incomes);
        redisCacheUtil.remove(paramKey);
        redisCacheUtil.set(statusKey, ConfigConstant.USER_ORDER_STATUS_003, 60l);//支付成功

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
