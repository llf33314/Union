package com.gt.union.card.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.entity.result.UnionDiscountResult;
import com.gt.union.card.entity.UnionCard;
import com.gt.union.card.entity.UnionCardBinding;
import com.gt.union.card.entity.UnionCardRoot;
import com.gt.union.card.mapper.UnionCardMapper;
import com.gt.union.card.service.IUnionCardBindingService;
import com.gt.union.card.service.IUnionCardIntegralService;
import com.gt.union.card.service.IUnionCardRootService;
import com.gt.union.card.service.IUnionCardService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
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


    @Value("${union.encryptKey}")
    private String encryptKey;

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
		if(unionId == null){
            List<UnionMember> members = unionMemberService.listWriteByBusId(busId);
            if(ListUtil.isEmpty(members)){
                throw new BusinessException("您没有有效的联盟");
            }
            List<Integer> unionIds = new ArrayList<Integer>();
            for(UnionMember member : members){
                try{
                    unionMainService.checkUnionMainValid(member.getUnionId());
                    unionIds.add(member.getUnionId());
                }catch (BaseException e){
                }
            }
            if(ListUtil.isEmpty(unionIds)){
                throw new BusinessException("您没有有效的联盟");
            }
            unionId = unionIds.get(0);
        }else {

        }
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
                if (CommonUtil.isNotEmpty(card.getValidity()) && !DateTimeKit.laterThanNow(new Date())) {
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


}
