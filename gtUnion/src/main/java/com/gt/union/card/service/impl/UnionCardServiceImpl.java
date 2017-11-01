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
import com.gt.union.api.client.member.MemberService;
import com.gt.union.api.client.shop.ShopService;
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
import com.gt.union.common.amqp.entity.PhoneMessage;
import com.gt.union.common.amqp.sender.PhoneMessageSender;
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
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.preferential.constant.PreferentialConstant;
import com.gt.union.preferential.entity.UnionPreferentialItem;
import com.gt.union.preferential.entity.UnionPreferentialProject;
import com.gt.union.preferential.service.IUnionPreferentialItemService;
import com.gt.union.preferential.service.IUnionPreferentialProjectService;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private IDictService dictService;

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

    @Autowired
    private IUnionPreferentialItemService unionPreferentialItemService;

    @Autowired
    private IUnionPreferentialProjectService unionPreferentialProjectService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private PhoneMessageSender phoneMessageSender;

    /**
     * 字母正则校验
     */
    private static Pattern CHARACTER_PATTERN = Pattern.compile("[a-zA-z]");

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
                        .append(" AND t2.del_status = ").append(CommonConstant.DEL_STATUS_NO)
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
        wrapper.setSqlSelect(" t1.id, DATE_FORMAT(t1.createtime, '%Y-%m-%d %T') createtime, t1.type, DATE_FORMAT(t1.validity, '%Y-%m-%d %T') validity, t2.phone, t2.number as cardNo, t2.integral, t1.is_charge");
        wrapper.orderBy("ti.id",false);
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
        if (CHARACTER_PATTERN.matcher(no).find()) {//包含字母--扫码枪扫码所得
            //解密
            try {
                no = EncryptUtil.decrypt(PropertiesUtil.getEncryptKey(), no);//解码后得到联盟卡号
            } catch (Exception e) {
                throw new ParamException(CommonConstant.PARAM_ERROR);
            }
            return getUnionCardInfoByCardNo(no, busId, unionId);
        } else {
            //手机号或联盟卡号
            //卡号使用9位或10开头
            if (no.length() == 11 && !no.startsWith("10")) {//手机号
                return getUnionCardInfoByPhone(no, busId, unionId);
            } else {//卡号
                return getUnionCardInfoByCardNo(no, busId, unionId);
            }
        }
    }

    @Override
    public Map<String, Object> getUnionCardInfoByCardNo(String cardNo, Integer busId, Integer unionId) throws Exception {
        UnionCardRoot root = unionCardRootService.getByCardNo(cardNo);
        if (root == null) {
            throw new BusinessException("该联盟卡不存在");
        }
        List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
        if (ListUtil.isEmpty(members)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        //获取最低折扣信息和联盟列表
        Map<String, Object> result = this.getByMinDiscountByCard(root.getId(), busId, members, unionId);
        return result;
    }

    @Override
    public Map<String, Object> getUnionCardInfoByPhone(String phone, Integer busId, Integer unionId) throws Exception {
        UnionCardRoot root = unionCardRootService.getByPhone(phone);
        if (root == null) {
            throw new BusinessException("该联盟卡不存在");
        }
        List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
        if (ListUtil.isEmpty(members)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }

        Map<String, Object> result = this.getByMinDiscountByCard(root.getId(), busId, members, unionId);
        return result;
    }

    @Override
    public UnionDiscountResult getConsumeUnionDiscount(Integer memberId, String phone, Integer busId) throws Exception {
        UnionDiscountResult result = new UnionDiscountResult();
        if (CommonUtil.isEmpty(memberId) || CommonUtil.isEmpty(busId)) {
            result.setCode(UnionDiscountResult.UNION_DISCOUNT_CODE_NON);
            return result;
        }
        //1、判断商家加入的有效联盟
        List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
        if (ListUtil.isEmpty(members)) {
            result.setCode(UnionDiscountResult.UNION_DISCOUNT_CODE_NON);
            return result;
        }
        //2、判断手机号是否升级过联盟卡
        if (StringUtil.isNotEmpty(phone)) {
            UnionCardRoot unionCardRoot = unionCardRootService.getByPhone(phone);
            if (unionCardRoot == null) {//该手机号没有升级过联盟卡
                result.setCode(UnionDiscountResult.UNION_DISCOUNT_CODE_NON);
                return result;
            } else {//升级过联盟卡，给予使用，返回折扣（有可能在某个联盟中退盟了的，需要进一步判断）
                return getByUnionCardRootAndMembers(busId, unionCardRoot.getId(), members, null);
            }
        }
        //3、判断粉丝是否绑定联盟卡
        UnionCardBinding binding = unionCardBindingService.getByMemberId(memberId);
        if (binding == null) {
            //没有绑定 去绑定  存在两种情况1：根本没有升级过联盟卡  2：升级过，但是不是在商家下升的
            result.setCode(UnionDiscountResult.UNION_DISCOUNT_CODE_BIND);
            return result;
        } else {
            return getByUnionCardRootAndMembers(busId, binding.getRootId(), members, null);
        }
    }

    /**
     * 根据rootId和我的盟员列表
     *
     * @param rootId
     * @param members 我的盟员列表
     * @return
     * @throws Exception
     */
    private UnionDiscountResult getByUnionCardRootAndMembers(Integer busId, Integer rootId, List<UnionMember> members, Integer unionId) throws Exception {
        UnionDiscountResult result = new UnionDiscountResult();
        try {
            Map<String, Object> data = getMinDiscount(rootId, members, unionId);
            if (CommonUtil.isEmpty(data)) {
                result.setCode(UnionDiscountResult.UNION_DISCOUNT_CODE_NON);
                return result;
            }
            result.setUnionId(CommonUtil.toInteger(data.get("unionId")));
            result.setCardId(CommonUtil.toInteger(data.get("cardId")));
            result.setDiscount(CommonUtil.toDouble(data.get("discount")));
            result.setIfDefault(CommonUtil.toInteger(data.get("isdefault")) == 1 ? true : false);
        } catch (Exception e) {
            result.setCode(UnionDiscountResult.UNION_DISCOUNT_CODE_NON);
            return result;
        }
        result.setCode(UnionDiscountResult.UNION_DISCOUNT_CODE_SUCCESS);
        return result;
    }


    @Override
    public List<UnionCard> getByBusMemberIdAndMemberIds(Integer memberId, List<Integer> memberids) {
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.in("member_id", memberids.toArray());
        wrapper.exists(new StringBuilder().append("SELECT t.id from t_union_card_binding t WHERE ")
                .append(" t_union_card.root_id = t.root_id ")
                .append(" AND t.member_id = ").append(memberId)
                .append(" AND t.del_status = ").append(CommonConstant.DEL_STATUS_NO).toString());
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);

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
        wrapper.gt("validity", new Date());
        wrapper.in("member_id", memberIds.toArray());
        return this.selectList(wrapper);
    }

    /**
     * 获取最低折扣
     *
     * @param rootId  升级的主卡id
     * @param members 我的盟员列表
     * @param unionId 联盟id  在哪个联盟
     * @return
     * @throws Exception
     */
    private Map<String, Object> getMinDiscount(Integer rootId, List<UnionMember> members, Integer unionId) throws Exception {
        List<Map<String, Object>> discountList = new ArrayList<Map<String, Object>>();//联盟卡折扣列表
        Map<String, Object> disMap = null;
        List<Integer> unionIds = new ArrayList<Integer>();
        for (UnionMember unionMember : members) {
            if (CommonUtil.isNotEmpty(unionId)) {//选择了联盟
                if (unionMember.getUnionId().equals(unionId)) {
                    Map<String, Object> discountMap = getMinDiscount(unionMember, rootId);//查询本盟员对该rootId盟员的最低折扣
                    if (CommonUtil.isNotEmpty(discountMap)) {
                        discountList.add(discountMap);
                    }
                }
            } else {
                Map<String, Object> discountMap = getMinDiscount(unionMember, rootId);
                if (CommonUtil.isNotEmpty(discountMap)) {
                    discountList.add(discountMap);
                    unionIds.add(CommonUtil.toInteger(discountMap.get("unionId")));
                }
            }
        }
        if (ListUtil.isEmpty(discountList)) {
            throw new BusinessException("没有有效的联盟卡");
        }
        disMap = discountList.get(0);
        if (discountList.size() > 1) {
            for (int i = 0; i < discountList.size(); i++) {
                Map<String, Object> map = discountList.get(i);
                if (CommonUtil.toDouble(disMap.get("discount")) > CommonUtil.toDouble(map.get("discount"))) {
                    disMap = map;
                }
            }
        }
        if (ListUtil.isNotEmpty(unionIds)) {
            disMap.put("unionIds", unionIds);
        }
        return disMap;
    }

    /**
     * 根据盟员和rootId查询该盟员可使用的最低折扣
     *
     * @param unionMember
     * @param rootId
     * @return
     * @throws Exception
     */
    private Map<String, Object> getMinDiscount(UnionMember unionMember, Integer rootId) throws Exception {
        Map<String, Object> data = null;
        List<UnionMember> memberList = unionMemberService.listWriteByUnionId(unionMember.getUnionId());//获取联盟内有写操作权限的盟员列表
        List<Integer> memberIds = new ArrayList<Integer>();
        for (UnionMember member : memberList) {
            memberIds.add(member.getId());
        }
        //得到该联盟下的有效联盟卡
        List<UnionCard> list = this.listByCardRootIdAndMemberIds(rootId, memberIds);
        if (ListUtil.isNotEmpty(list)) {
            Map<String, Object> discountMap = this.baseMapper.getByMinDiscountByCardList(list, unionMember.getId());//获取设置最低折扣
            if (discountMap == null) {
                discountMap = this.baseMapper.getByEarliestByCardList(list);
                discountMap.put("fromMemberId", unionMember.getId());
                discountMap.put("discount", dictService.getDefaultDiscount());
                discountMap.put("unionId", unionMember.getUnionId());
                discountMap.put("isdefault", 1);
            } else {
                discountMap.put("unionId", unionMember.getUnionId());
                discountMap.put("isdefault", 0);
            }
            data = discountMap;
        }
        return data;
    }

    @Override
    public Map<String, Object> getByMinDiscountByCard(Integer rootId, Integer busId, List<UnionMember> members, Integer unionId) throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        LinkedList<UnionMain> unions = new LinkedList<UnionMain>();//封装联盟列表
        Map<String, Object> disMap = getMinDiscount(rootId, members, unionId);//得到最低折扣
        if (CommonUtil.isEmpty(disMap)) {
            throw new BusinessException("没有有效的联盟卡");
        }
        if (CommonUtil.isEmpty(unionId)) {
            if (CommonUtil.isNotEmpty(disMap.get("unionIds"))) {
                List<Integer> unionIds = (List<Integer>) disMap.get("unionIds");
                List<UnionMain> mains = unionMainService.listByIds(unionIds);
                Iterator<UnionMember> it = members.iterator();
                while (it.hasNext()) {
                    UnionMember unionMember = it.next();
                    for (UnionMain main : mains) {
                        if (unionMember.getUnionId().equals(main.getId())) {
                            if(unionMember.getUnionId().equals(CommonUtil.toInteger(disMap.get("unionId")))){
                                unions.addFirst(main);
                            }else {
                                unions.addLast(main);
                            }
                            break;
                        }
                    }
                }
                data.put("unions", unions);
            }
            List<Map<String, Object>> shops = shopService.listByBusId(busId);
            data.put("shops", shops);
        }
        //得到的盟员id
        Integer memberId = CommonUtil.toInteger(disMap.get("fromMemberId"));
        unionId = CommonUtil.toInteger(disMap.get("unionId"));
        UnionMain currentUnionMain = unionMainService.getById(unionId);
        UnionCardRoot root = unionCardRootService.getById(CommonUtil.toInteger(disMap.get("rootId")));
        data.put("enterpriseName", disMap.get("enterpriseName"));
        data.put("discount", disMap.get("discount"));
        data.put("cardNo", root.getNumber());
        data.put("integral", root.getIntegral());
        data.put("validity", CommonUtil.toInteger(disMap.get("isCharge")) == 1 ? DateTimeKit.daysBetween(new Date(), DateTimeKit.parseDate(disMap.get("validity").toString(), "yyyy/MM/dd HH:mm:ss")) : null);
        UnionMainCharge redCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(unionId, MainConstant.CHARGE_TYPE_RED, MainConstant.CHARGE_IS_AVAILABLE_YES);
        if (redCharge != null) {
            UnionPreferentialProject project = unionPreferentialProjectService.getByMemberId(memberId);
            if (project != null) {
                //查询是否在该联盟升级红卡
                int count = this.countCardByTypeAndRootIdAndUnionId(CardConstant.TYPE_RED, root.getId(), unionId);
                if(count > 0){
                    List<UnionPreferentialItem> items = unionPreferentialItemService.listByProjectIdAndStatus(project.getId(), PreferentialConstant.STATUS_PASS);
                    data.put("items", items);
                    data.put("illustration", CommonUtil.isEmpty(redCharge.getIllustration()) ? "" : redCharge.getIllustration());
                }
            }
        }
        data.put("unionId", unionId);
        data.put("cardId", CommonUtil.toInteger(disMap.get("cardId")));
        data.put("memberId", memberId);
        int isIntegral = currentUnionMain.getIsIntegral();
        data.put("isIntegral", isIntegral);
        if (isIntegral == MainConstant.IS_INTEGRAL_YES) {//开启积分
            UnionMember unionMember = unionMemberService.getById(memberId);
            if (CommonUtil.isNotEmpty(unionMember.getIntegralExchangePercent()) && unionMember.getIntegralExchangePercent() > 0) {
                data.put("integralPercent", unionMember.getIntegralExchangePercent());
                data.put("exchangeIntegral", dictService.getExchangeIntegral());
            }else {
                data.put("isIntegral", 0);
            }
        }
        return data;
    }

    /**
     * 检验该手机号在该盟员下可办理联盟卡的信息
     *
     * @param member
     * @param phone
     * @return
     * @throws Exception
     */
    private List<Map<String, Object>> getCardInfoByMemberAndPhone(UnionMember member, String phone) throws Exception {
        List<UnionMember> memberLists = unionMemberService.listWriteByUnionId(member.getUnionId());//我加入该联盟的盟员列表
        Boolean percent = isUnionCardChargePercent(memberLists);
        List<Integer> memberList = new ArrayList<Integer>();//其他的盟员列表
        for (UnionMember unionMember : memberLists) {
            if (!unionMember.getId().equals(member.getId())) {
                memberList.add(unionMember.getId());
            }
        }
        UnionCardRoot root = unionCardRootService.getByPhone(phone);
        UnionCard unionCard = this.getByRootAndMemberId(root, member.getId(), true);//本盟员升级的联盟卡
        UnionMainCharge blackCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(member.getUnionId(), MainConstant.CHARGE_TYPE_BLACK, MainConstant.CHARGE_IS_AVAILABLE_YES);
        UnionMainCharge redCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(member.getUnionId(), MainConstant.CHARGE_TYPE_RED, MainConstant.CHARGE_IS_AVAILABLE_YES);
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        if (unionCard != null) {//本盟员下升级了联盟卡
            if (unionCard.getType() == CardConstant.TYPE_BLACK) {//黑卡
                if (unionCard.getIsCharge() == CardConstant.IS_CHARGE_YES) {//收费黑卡
                    if (redCharge != null) {//开启红卡
                        int count = 0;
                        if(ListUtil.isNotEmpty(memberList)){
                            count = this.baseMapper.countByMemberIdsAndType(memberList, CardConstant.TYPE_RED, MainConstant.CHARGE_IS_CHARGE_YES, phone);//查询是否在其他盟员升级红卡
                        }
                        if (count > 0) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            Map<String, Object> red = new HashMap<String, Object>();
                            red.put("price", 0);
                            red.put("termTime", DateTimeKit.format(DateTimeKit.addDays(redCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                            red.put("illustration", redCharge.getIllustration());
                            map.put("red", red);
                            map.put("unionId",member.getUnionId());
                            dataList.add(map);
                        } else {
                            if (percent) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                Map<String, Object> red = new HashMap<String, Object>();
                                red.put("price", redCharge.getChargePrice());
                                red.put("termTime", DateTimeKit.format(DateTimeKit.addDays(redCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                                red.put("illustration", redCharge.getIllustration());
                                map.put("red", red);
                                map.put("unionId",member.getUnionId());
                                dataList.add(map);
                            }
                        }
                    }
                } else {//免费黑卡
                    //黑卡收费
                    if (blackCharge.getIsCharge() == MainConstant.CHARGE_IS_CHARGE_YES && blackCharge.getIsOldCharge() == MainConstant.CHARGE_OLD_IS_YES) {
                        int count = 0;
                        if(ListUtil.isNotEmpty(memberList)){
                            count = this.baseMapper.countByMemberIdsAndType(memberList, CardConstant.TYPE_BLACK, MainConstant.CHARGE_IS_CHARGE_YES, phone);
                        }
                        if (count > 0) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            Map<String, Object> black = new HashMap<String, Object>();
                            black.put("price", 0);
                            black.put("termTime", DateTimeKit.format(DateTimeKit.addDays(blackCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                            black.put("illustration", blackCharge.getIllustration());
                            map.put("black", black);
                            map.put("unionId",member.getUnionId());
                            dataList.add(map);
                        } else {
                            if (percent) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                Map<String, Object> black = new HashMap<String, Object>();
                                black.put("price", blackCharge.getChargePrice());
                                black.put("termTime", DateTimeKit.format(DateTimeKit.addDays(blackCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                                black.put("illustration", blackCharge.getIllustration());
                                map.put("black", black);
                                map.put("unionId",member.getUnionId());
                                dataList.add(map);
                            }
                        }
                    }
                    //开启红卡
                    if (redCharge != null) {
                        int count = 0;
                        if(ListUtil.isNotEmpty(memberList)){
                            count = this.baseMapper.countByMemberIdsAndType(memberList, CardConstant.TYPE_RED, MainConstant.CHARGE_IS_CHARGE_YES, phone);
                        }
                        if (count > 0) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            Map<String, Object> red = new HashMap<String, Object>();
                            red.put("price", 0);
                            red.put("termTime", DateTimeKit.format(DateTimeKit.addDays(redCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                            red.put("illustration", redCharge.getIllustration());
                            map.put("red", red);
                            map.put("unionId",member.getUnionId());
                            dataList.add(map);
                        } else {
                            if (percent) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                Map<String, Object> red = new HashMap<String, Object>();
                                red.put("price", redCharge.getChargePrice());
                                red.put("termTime", DateTimeKit.format(DateTimeKit.addDays(redCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                                red.put("illustration", redCharge.getIllustration());
                                map.put("red", red);
                                map.put("unionId",member.getUnionId());
                                dataList.add(map);
                            }
                        }
                    }
                }
            }
        } else {//不在本盟员下升级了联盟卡
            if (blackCharge.getIsCharge() == MainConstant.CHARGE_IS_CHARGE_YES) {//黑卡收费
                int count = 0;
                if(ListUtil.isNotEmpty(memberList)){
                    count = this.baseMapper.countByMemberIdsAndType(memberList, CardConstant.TYPE_BLACK, MainConstant.CHARGE_IS_CHARGE_YES, phone);
                }
                if (count > 0) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    Map<String, Object> black = new HashMap<String, Object>();
                    black.put("price", 0);
                    black.put("termTime", DateTimeKit.format(DateTimeKit.addDays(blackCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                    black.put("illustration", blackCharge.getIllustration());
                    map.put("black", black);
                    map.put("unionId",member.getUnionId());
                    dataList.add(map);
                } else {
                    if (percent) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        Map<String, Object> black = new HashMap<String, Object>();
                        black.put("price", blackCharge.getChargePrice());
                        black.put("termTime", DateTimeKit.format(DateTimeKit.addDays(blackCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                        black.put("illustration", blackCharge.getIllustration());
                        map.put("black", black);
                        map.put("unionId",member.getUnionId());
                        dataList.add(map);
                    }
                }
            } else {
                Map<String, Object> map = new HashMap<String, Object>();
                Map<String, Object> black = new HashMap<String, Object>();
                black.put("price", blackCharge.getChargePrice());
                black.put("illustration", blackCharge.getIllustration());
                map.put("black", black);
                map.put("unionId",member.getUnionId());
                dataList.add(map);
            }
            if (redCharge != null) {//开启红卡
                int count = 0;
                if(ListUtil.isNotEmpty(memberList)){
                    count = this.baseMapper.countByMemberIdsAndType(memberList, CardConstant.TYPE_RED, MainConstant.CHARGE_IS_CHARGE_YES, phone);
                }
                if (count > 0) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    Map<String, Object> red = new HashMap<String, Object>();
                    red.put("price", 0);
                    red.put("termTime", DateTimeKit.format(DateTimeKit.addDays(redCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                    red.put("illustration", redCharge.getIllustration());
                    map.put("red", red);
                    map.put("unionId",member.getUnionId());
                    dataList.add(map);
                } else {
                    if (percent) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        Map<String, Object> red = new HashMap<String, Object>();
                        red.put("price", redCharge.getChargePrice());
                        red.put("termTime", DateTimeKit.format(DateTimeKit.addDays(redCharge.getValidityDay()), "yyyy-MM-dd"));//收费的，有效期
                        red.put("illustration", redCharge.getIllustration());
                        map.put("red", red);
                        map.put("unionId",member.getUnionId());
                        dataList.add(map);
                    }
                }
            }
        }
        return dataList;
    }


    @Override
    public void getPhoneCode(Integer busId, String phone) throws Exception {
        if (busId == null || StringUtil.isEmpty(phone)) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
        if (ListUtil.isEmpty(members)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        Iterator<UnionMember> it = members.iterator();
        while (it.hasNext()) {
            UnionMember member = it.next();
            List<Map<String, Object>> dataList = getCardInfoByMemberAndPhone(member, phone);
            if (ListUtil.isEmpty(dataList)) {
                it.remove();
            }
        }
        if (ListUtil.isEmpty(members)) {
            throw new BusinessException("您没有有效的联盟或该手机号已办理联盟卡");
        }
        String code = RandomKit.getRandomString(6, 0);
        PhoneMessage phoneMessage = new PhoneMessage(busId,phone,"办理联盟卡，验证码:" + code);
        this.phoneMessageSender.sendMsg(phoneMessage);
        String phoneKey = RedisKeyUtil.getBindCardPhoneKey(phone);
        redisCacheUtil.set(phoneKey, code, 600L);//5分钟
    }


    /**
     * 判断收费的联盟卡是否设置了售卡佣金，且和为100
     *
     * @param memberList
     * @return
     * @throws Exception
     */
    private boolean isUnionCardChargePercent(List<UnionMember> memberList) throws Exception {
        if (ListUtil.isEmpty(memberList)) {
            return false;
        }
        double percent = 0;
        for (UnionMember unionMember : memberList) {
            if (CommonUtil.isEmpty(unionMember.getCardDividePercent()) || unionMember.getCardDividePercent() == 0) {//没有设置收卡佣金
                return false;
            }
            percent = BigDecimalUtil.add(percent, unionMember.getCardDividePercent()).doubleValue();
        }
        if (percent != 100) {
            return false;
        }
        return true;
    }

    @Override
    public Map<String, Object> getUnionInfoByPhone(Integer busId, String phone, String code, Integer unionId) throws Exception {
        if (busId == null || StringUtil.isEmpty(phone) || StringUtil.isEmpty(code)) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        String phoneKey = RedisKeyUtil.getBindCardPhoneKey(phone);
        String obj = redisCacheUtil.get(phoneKey);
        if (CommonUtil.isEmpty(obj)) {
            throw new BusinessException(CommonConstant.CODE_ERROR_MSG);
        }
        if (!code.equals(JSON.parse(obj))) {
            throw new BusinessException(CommonConstant.CODE_ERROR_MSG);
        }
        List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
        if (ListUtil.isEmpty(members)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        Map<String, Object> data = getUnionInfo(members, phone, unionId, busId);
        WxPublicUsers users = busUserService.getWxPublicUserByBusId(busId);
        if (users != null) {
            data.put("follow", 1);
        } else {
            data.put("follow", 0);
        }
        return data;

    }

    /**
     * @param members 有效的盟员列表
     * @return
     * @throws Exception
     */
    private Map<String, Object> getUnionInfo(List<UnionMember> members, String phone, Integer unionId, Integer busId) throws Exception {
        Iterator<UnionMember> it = members.iterator();
        //得到有效的盟员列表
        Map<Integer, List<Map<String, Object>>> cardInfo = new HashMap<Integer, List<Map<String, Object>>>();
        while (it.hasNext()) {
            UnionMember member = it.next();
            //可以升级的联盟列表
            List<Map<String, Object>> dataList = getCardInfoByMemberAndPhone(member, phone);
            if (ListUtil.isEmpty(dataList)) {
                it.remove();
            } else {
                if (!cardInfo.containsKey(member.getId())) {
                    cardInfo.put(member.getId(), dataList);
                }
            }
        }
        if (ListUtil.isEmpty(members)) {
            throw new BusinessException("您没有有效的联盟或该手机号已办理联盟卡");
        }
        //得到当前的盟员信息
        UnionMember unionMember = members.get(0);
        if (unionId != null) {
            for (UnionMember member : members) {
                if(unionId.equals(member.getUnionId())){
                    unionMember = member;
                }
            }
        }
        //封装联盟卡信息
        Map<String, Object> cards = new HashMap<String,Object>();
        List<Integer> unionIds = new ArrayList<Integer>();
        for (UnionMember member : members) {
            if(cardInfo.containsKey(member.getId())){
                List<Map<String, Object>> list = cardInfo.get(member.getId());
                //选中的
                if(member.getId().equals(unionMember.getId())){
                    for(Map map : list){
                        if(map.containsKey("black")){
                            cards.put("black",map.get("black"));
                        }
                        if(map.containsKey("red")){
                            cards.put("red",map.get("red"));
                        }
                        unionId = CommonUtil.toInteger(map.get("unionId"));
                    }
                }
                //得到可升级的联盟ids
                unionIds.add(member.getUnionId());
            }
        }
        //封装联盟列表
        List<UnionMain> unions = new ArrayList<UnionMain>();
        List<UnionMain> mains = unionMainService.listByIds(unionIds);
        for (UnionMember member : members) {
            for (UnionMain main : mains) {
                if (member.getUnionId().equals(main.getId())) {
                    unions.add(main);
                    break;
                }
            }
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("cards", cards);
        data.put("unions", unions);//可以升级的联盟列表
        data.put("unionId", unionId);//当前的联盟
        return data;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> bindCard(UnionCardBindParamVO vo) throws Exception {
        if (CommonUtil.isNotEmpty(vo.getFollow()) && vo.getFollow()) {
            if (CommonUtil.isEmpty(vo.getMemberId())) {
                throw new BusinessException("请选择粉丝");
            }
        }
        //检验联盟有效期
        unionMainService.checkUnionValid(vo.getUnionId());
        UnionMember member = unionMemberService.getByBusIdAndUnionId(vo.getBusId(), vo.getUnionId());
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        if (!unionMemberService.hasWriteAuthority(member)) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_INVALID);
        }
        UnionCardRoot root = unionCardRootService.getByPhone(vo.getPhone());
        UnionCard card = this.getByRootAndMemberId(root, member.getId(), true);
        if (card != null && card.getType().equals(vo.getCardType())) {
            UnionMainCharge blackCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(vo.getUnionId(), CardConstant.TYPE_BLACK, MainConstant.CHARGE_IS_AVAILABLE_YES);
            if (!(card.getIsCharge() == CardConstant.IS_CHARGE_NO && blackCharge.getIsOldCharge() == MainConstant.CHARGE_OLD_IS_YES && card.getType() == CardConstant.TYPE_BLACK)) {
                throw new BusinessException("该手机号已办理联盟卡");
            }
        }
        //升级的联盟卡信息
        List<Map<String, Object>> list = getCardInfoByMemberAndPhone(member, vo.getPhone());
        UnionMainCharge charge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(vo.getUnionId(), vo.getCardType(), MainConstant.CHARGE_IS_AVAILABLE_YES);
        if (charge == null) {
            throw new BusinessException("联盟未开启该联盟卡类型");
        }
        //黑卡
        Double price = 0D;
        for (Map<String,Object> map : list){
            //黑卡
            if(vo.getCardType() == CardConstant.TYPE_BLACK){
                if (CommonUtil.isNotEmpty(map.get("black"))) {
                    Map<String,Object> black = (Map<String,Object>)map.get("black");
                    price = CommonUtil.toDouble(black.get("price"));//收费价格
                }
            }
            //红卡
            if(vo.getCardType() == CardConstant.TYPE_RED){
                if (CommonUtil.isNotEmpty(map.get("red"))) {
                    Map<String,Object> red = (Map<String,Object>)map.get("red");
                    price = CommonUtil.toDouble(red.get("price"));//收费价格
                }
            }
        }
        Map<String, Object> data = new HashMap<String, Object>();
        if (CommonUtil.isEmpty(price) || price == 0) {//不收费，直接办理
            UnionCard unionCard = this.getByRootAndMemberId(root, member.getId(), false);
            if (unionCard == null) {
                Integer rootId = null;
                if (root == null) {
                    UnionCardRoot unionCardRoot = unionCardRootService.createUnionCardRoot(vo.getPhone());
                    rootId = unionCardRoot.getId();
                } else {
                    rootId = root.getId();
                }
                Date time = (charge.getValidityDay() == null || charge.getValidityDay() == 0) ? DateTimeKit.parse(CardConstant.CARD_FREE_VALIDITY, "yyyy-MM-dd HH:mm:ss") : DateTimeKit.addDate(new Date(), charge.getValidityDay());
                this.createUnionCard(rootId, vo.getCardType(), member.getId(), time, 0);
                if (CommonUtil.isNotEmpty(vo.getMemberId())) {
                    UnionCardBinding binding = unionCardBindingService.getByCardRootIdAndMemberId(rootId, vo.getMemberId());
                    if (binding == null) {
                        unionCardBindingService.createUnionCardBinding(rootId, vo.getMemberId());
                    }
                }
            } else {
                Integer rootId = unionCard.getRootId();
                Date time = (charge.getValidityDay() == null || charge.getValidityDay() == 0) ? DateTimeKit.parse(CardConstant.CARD_FREE_VALIDITY, "yyyy-MM-dd HH:mm:ss") : DateTimeKit.addDate(new Date(), charge.getValidityDay());
                UnionCard upCard = new UnionCard();
                upCard.setId(unionCard.getId());
                upCard.setValidity(time);
                upCard.setIsCharge(CardConstant.IS_CHARGE_NO);
                this.updateById(upCard);
                if (CommonUtil.isNotEmpty(vo.getMemberId())) {
                    UnionCardBinding binding = unionCardBindingService.getByCardRootIdAndMemberId(rootId, vo.getMemberId());
                    if (binding == null) {
                        unionCardBindingService.createUnionCardBinding(rootId, vo.getMemberId());
                    }
                }
            }
        } else {//收费
            StringBuilder sb = new StringBuilder("?");
            sb.append("&phone=").append(vo.getPhone())
                    .append("&memberId=").append(vo.getMemberId())
                    .append("&unionId=").append(vo.getUnionId())
                    .append("&cardtype=").append(vo.getCardType());
            data.put("qrurl", PropertiesUtil.getUnionUrl() + "/qrCode" + sb.toString());
        }
        return data;
    }

    /**
     * 插入联盟卡
     *
     * @param rootId
     * @param cardType
     * @param memberId
     * @param validity
     * @param isCharge
     * @return
     */
    private UnionCard createUnionCard(Integer rootId, Integer cardType, Integer memberId, Date validity, Integer isCharge) {
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
    public UnionCard getByRootAndMemberId(UnionCardRoot root, Integer memberId, Boolean isValidity) throws Exception {
        if (root == null) {
            return null;
        }
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.eq("root_id", root.getId());
        wrapper.eq("member_id", memberId);
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        if (isValidity) {
            wrapper.gt("validity", new Date());
        }
        return this.selectOne(wrapper);
    }

    @Override
    public List<Map<String, Object>> listByUnionId(final Integer unionId, final Integer busId, final String cardNo, final String phone) throws Exception {
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
    public Map<String, Object> getUnionCardIndex(Integer busId, Member busMember) throws Exception {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> unions = new ArrayList<Map<String, Object>>();
        List<UnionMember> members = unionMemberService.listWriteWithValidUnionByBusId(busId);
        if (ListUtil.isNotEmpty(members)) {
            for (UnionMember member : members) {
                UnionMain main = unionMainService.getById(member.getUnionId());
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("unionName", main.getName());
                map.put("unionId", main.getId());
                map.put("memberId", member.getId());
                unions.add(map);
            }
        }
        data.put("unions", unions);
        for(Map<String,Object> map : unions){
            Map<String,Object> memberInfo = this.getUnionInfoCardList(busId, busMember, CommonUtil.toInteger(map.get("unionId")), CommonUtil.toInteger(map.get("memberId")));
            map.put("memberInfo",memberInfo);
        }
        String phone = busMember.getPhone();
        if (StringUtil.isNotEmpty(phone)) {
            UnionCardRoot root = unionCardRootService.getByPhone(phone);
            if (root != null) {
                data.put("cardNo", root.getNumber());
                data.put("integral", root.getIntegral());
            }
        }
        data.put("nickname", busMember.getNickname());
        data.put("headurl", busMember.getHeadimgurl());
        data.put("busId", busId);
        return data;
    }

    @Override
    public Map<String, Object> createQRCode(Integer busId, String phone, Integer memberId, Integer unionId, Integer cardType, Integer isReturn, String returnUrl, Integer userId, Integer isSendSocketMessage) throws Exception {
        UnionMainCharge charge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(unionId, cardType, MainConstant.CHARGE_IS_AVAILABLE_YES);
        Double price = charge.getChargePrice();//收费价格
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("totalFee",price);
        data.put("busId", PropertiesUtil.getDuofenBusId());
        data.put("sourceType", 1);//是否墨盒支付
        data.put("payWay", 1);//微信支付
        data.put("isreturn", isReturn);//0：不需要同步跳转 1:同步跳转
        data.put("model", ConfigConstant.PAY_MODEL);
        String only = String.valueOf(System.currentTimeMillis());
        String orderNo = CardConstant.ORDER_PREFIX + only;
        WxPublicUsers publicUser = busUserService.getWxPublicUserByBusId(PropertiesUtil.getDuofenBusId());
        data.put("notifyUrl", PropertiesUtil.getUnionUrl() + "/unionCard/79B4DE7C/paymentSuccess/" + only);
        if (isReturn == 1) {
            data.put("returnUrl", returnUrl);
        }
        data.put("orderNum", orderNo);//订单号
        data.put("payBusId", busId);//支付的商家id
        data.put("isSendMessage", 0);//不推送
        data.put("appid", publicUser.getAppid());//appid
        data.put("desc", "办理联盟卡");
        data.put("appidType", 0);//公众号
        data.put("only", only);
        data.put("unionId", unionId);
        data.put("phone", phone);
        data.put("memberId", memberId);
        data.put("cardType", cardType);
        data.put("userId", userId);
        data.put("isSendSocketMessage", isSendSocketMessage);
        String statusKey = RedisKeyUtil.getBindCardPayStatusKey(only);
        String paramKey = RedisKeyUtil.getBindCardPayParamKey(only);
        redisCacheUtil.set(paramKey, data, 360L);//5分钟
        redisCacheUtil.set(statusKey, ConfigConstant.USER_ORDER_STATUS_001, 300L);//等待扫码状态
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payBindCardSuccess(String orderNo, String only) throws Exception {
        //解密订单号
        String statusKey = RedisKeyUtil.getBindCardPayStatusKey(only);
        String paramKey = RedisKeyUtil.getBindCardPayParamKey(only);
        String obj = redisCacheUtil.get(paramKey);
        Map<String, Object> result = JSONObject.parseObject(obj, Map.class);
        Double payMoney = CommonUtil.toDouble(result.get("totalFee"));
        Integer unionId = CommonUtil.toInteger(result.get("unionId"));
        Integer cardType = CommonUtil.toInteger(result.get("cardType"));
        Integer payBusId = CommonUtil.toInteger(result.get("payBusId"));
        Integer memberId = CommonUtil.toInteger(CommonUtil.isEmpty(result.get("memberId")) ? 0 : result.get("memberId"));
        String phone = CommonUtil.toString(result.get("phone"));
        String orderDesc = CommonUtil.toString(result.get("desc"));
        UnionCardUpgradePay pay = unionCardUpgradePayService.createCardUpgreadePay(orderNo, 2, 1, payMoney, orderDesc);
        UnionMember member = unionMemberService.getByBusIdAndUnionId(payBusId, unionId);
        UnionCardRoot root = unionCardRootService.getByPhone(phone);
        UnionCard unionCard = this.getByRootAndMemberId(root, member.getId(), false);
        UnionMainCharge charge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(unionId, cardType, MainConstant.CHARGE_IS_AVAILABLE_YES);
        Integer rootId = null;
        Integer cardId = null;
        Date validity = null;
        int isCharge = charge.getIsCharge() == 0 ? CardConstant.IS_CHARGE_NO : CardConstant.IS_CHARGE_YES;
        if (unionCard == null) {
            if (root == null) {
                UnionCardRoot unionCardRoot = unionCardRootService.createUnionCardRoot(phone);
                rootId = unionCardRoot.getId();
            } else {
                rootId = root.getId();
            }
            Date time = (charge.getValidityDay() == null || charge.getValidityDay() == 0) ? DateTimeKit.parse(CardConstant.CARD_FREE_VALIDITY, "yyyy-MM-dd HH:mm:ss") : DateTimeKit.addDate(new Date(), charge.getValidityDay());
            UnionCard card = this.createUnionCard(rootId, cardType, member.getId(), time, isCharge);
            cardId = card.getId();
            validity = card.getValidity();
        } else {
            rootId = unionCard.getRootId();
            Date time = (charge.getValidityDay() == null || charge.getValidityDay() == 0) ? DateTimeKit.parse(CardConstant.CARD_FREE_VALIDITY, "yyyy-MM-dd HH:mm:ss") : DateTimeKit.addDate(new Date(), charge.getValidityDay());
            cardId = unionCard.getId();
            UnionCard upCard = new UnionCard();
            upCard.setId(unionCard.getId());
            //收费黑卡升红卡
            if (unionCard.getType() == CardConstant.TYPE_BLACK && unionCard.getIsCharge() == CardConstant.IS_CHARGE_YES && cardType == CardConstant.TYPE_RED) {
                upCard.setValidity(DateTimeKit.addDays(unionCard.getValidity(), charge.getValidityDay()));
            } else {
                upCard.setValidity(time);
            }
            upCard.setIsCharge(isCharge);
            upCard.setType(cardType);
            this.updateById(upCard);
            validity = upCard.getValidity();

        }
        //绑定联盟卡和粉丝信息
        if (memberId != 0) {
            UnionCardBinding binding = unionCardBindingService.getByCardRootIdAndMemberId(rootId, memberId);
            if (binding == null) {
                unionCardBindingService.createUnionCardBinding(rootId, memberId);
            }
        }
        UnionCardUpgrade upgrade = unionCardUpgradeService.createCardUpgrade(cardId, member.getId(), cardType, validity, pay.getId());
        List<UnionMember> list = unionMemberService.listWriteByUnionId(member.getUnionId());
        //添加售卡分成
        List<UnionBrokerageIncome> incomes = new ArrayList<UnionBrokerageIncome>();
        for (UnionMember unionMember : list) {
            Double money = BigDecimalUtil.multiply(payMoney, BigDecimalUtil.divide(unionMember.getCardDividePercent(),100d)).doubleValue();
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
        redisCacheUtil.set(statusKey, ConfigConstant.USER_ORDER_STATUS_003, 60L);//支付成功

    }

    @Override
    public void bindCardPhone(Member member, Integer busId, String phone, String code) throws Exception {
        if (member == null || busId == null || StringUtil.isEmpty(phone)) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (StringUtil.isEmpty(code)) {
            throw new BusinessException("验证码不能为空");
        }
        String phoneKey = RedisKeyUtil.getCardH5BindPhoneKey(phone);
        String obj = redisCacheUtil.get(phoneKey);
        if (CommonUtil.isEmpty(obj)) {
            throw new BusinessException(CommonConstant.CODE_ERROR_MSG);
        }
        if (!code.equals(JSON.parse(obj))) {
            throw new BusinessException(CommonConstant.CODE_ERROR_MSG);
        }
        int status = memberService.bindMemberPhone(busId, member.getId(), phone);
        if (status == 0) {
            throw new BusinessException("绑定失败");
        }
        redisCacheUtil.remove(phoneKey);
    }

    @Override
    public Map<String, Object> getUnionInfoCardList(Integer busId, Member member, Integer unionId, Integer memberId) throws Exception {
        List<Map<String, Object>> members = unionMemberService.listMapById(memberId);//获取我给联盟内盟员的折扣列表
        Double discount = dictService.getDefaultDiscount();
        for (int i = 0; i < members.size(); i++) {
            Map<String, Object> map = members.get(i);
            if(CommonUtil.toInteger(map.get("memberId")).equals(memberId) && CommonUtil.toInteger(map.get("isUnionOwner")) == MemberConstant.IS_UNION_OWNER_YES){
                //既是盟主又是盟员，不变
            } else if(CommonUtil.toInteger(map.get("memberId")).equals(memberId) && CommonUtil.toInteger(map.get("isUnionOwner")) != MemberConstant.IS_UNION_OWNER_YES){
                //只是盟员
                if(i != 0){
                    Map<String,Object> memberMap = members.get(0);
                    members.set(0,map);
                    members.set(i,memberMap);
                }
            } else if(!CommonUtil.toInteger(map.get("memberId")).equals(memberId) && CommonUtil.toInteger(map.get("isUnionOwner")) == MemberConstant.IS_UNION_OWNER_YES){
                //盟主
                if(i != 1){
                    Map<String,Object> memberMap = members.get(1);
                    members.set(1,map);
                    members.set(i,memberMap);
                }
            }
            if(CommonUtil.isEmpty(map.get("discount"))){
                map.put("discount",discount);
            }
        }
        Map<String, Object> data = new HashMap<String, Object>();
        String phone = member.getPhone();
        if (StringUtil.isNotEmpty(phone)) {
            UnionMember unionMember = unionMemberService.getById(memberId);
            List<Map<String, Object>> dataList = getCardInfoByMemberAndPhone(unionMember, phone);
            if (ListUtil.isNotEmpty(dataList)) {//可以办理
                data.put("cards", dataList);
                data.put("bind", 1);//可办理
            } else {
                data.put("bind", 0);//不可办理
            }
        } else {
            List<Map<String, Object>> cardDataList = new ArrayList<Map<String, Object>>();
            UnionMainCharge blackCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(unionId, MainConstant.CHARGE_TYPE_BLACK, MainConstant.CHARGE_IS_AVAILABLE_YES);
            UnionMainCharge redCharge = unionMainChargeService.getByUnionIdAndTypeAndIsAvailable(unionId, MainConstant.CHARGE_TYPE_RED, MainConstant.CHARGE_IS_AVAILABLE_YES);
            //红卡
            Map<String, Object> black = new HashMap<String, Object>();
            Map<String, Object> blackMap = new HashMap<String, Object>();
            if (blackCharge.getIsCharge() == MainConstant.CHARGE_IS_CHARGE_YES) {
                black.put("price", blackCharge.getChargePrice());
            } else {
                black.put("price", 0);
            }
            black.put("illustration", blackCharge.getIllustration());
            blackMap.put("black", black);
            cardDataList.add(blackMap);
            //黑卡
            if (redCharge != null) {
                Map<String, Object> redMap = new HashMap<String, Object>();
                Map<String, Object> red = new HashMap<String, Object>();
                red.put("price", redCharge.getChargePrice());
                red.put("illustration", redCharge.getIllustration());
                redMap.put("red", red);
                cardDataList.add(redMap);
            }
            data.put("bind", 1);//可办理
            data.put("cards", cardDataList);
        }
        data.put("memberId", memberId);
        data.put("members", members);
        return data;
    }

    @Override
    public int countCardByTypeAndRootIdAndUnionId(int cardType, Integer rootId, Integer unionId) {
        return this.baseMapper.countCardByTypeAndRootIdAndUnionId(cardType, rootId, unionId);
    }


    @Override
    public HSSFWorkbook exportCardList(String[] titles, String[] contentName, List<Map<String, Object>> list) {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFCellStyle styleCenter = wb.createCellStyle();
        HSSFSheet sheet = createHSSFSheet(titles, wb, styleCenter);
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> item = list.get(i);
            HSSFRow row = sheet.createRow(i + 1);
            for (int j = 0; j < titles.length; j++) {
                String key = contentName[j];
                String c = CommonUtil.isEmpty(item.get(key)) ? "" : item.get(key).toString();
                if ("type".equals(key)) {//卡类型
                    if ("1".equals(c)) {
                        c = "黑卡";
                    } else if ("2".equals(c)) {
                        c = "红卡";
                    }
                } else if ("validity".equals(key)) {//卡有效期
                    if (StringUtil.isEmpty(c)) {
                        c = "无";
                    } else {
                        c = DateTimeKit.format(DateTimeKit.parse(c, DateTimeKit.DEFAULT_DATETIME_FORMAT), "yyyy/MM/dd");
                    }
                } else if ("createtime".equals(key)) {//升级时间
                    c = DateTimeKit.format(DateTimeKit.parse(c, DateTimeKit.DEFAULT_DATETIME_FORMAT), "yyyy/MM/dd HH:mm");
                } else if ("integral".equals(key)) {//积分
                    if (StringUtil.isEmpty(c)) {
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
     *
     * @param titles
     * @param wb
     * @param styleCenter
     * @return
     */
    private HSSFSheet createHSSFSheet(String[] titles, HSSFWorkbook wb, HSSFCellStyle styleCenter) {
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
        for (int i = 0; i < titles.length; i++) {
            cellTitle = rowTitle.createCell(i);
            cellTitle.setCellValue(titles[i]);
            cellTitle.setCellStyle(styleTitle);
        }
        styleCenter.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        return sheet;
    }

}
