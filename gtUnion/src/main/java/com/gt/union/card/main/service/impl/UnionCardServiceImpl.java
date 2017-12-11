package com.gt.union.card.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.card.CardConstant;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.activity.service.IUnionCardActivityService;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.mapper.UnionCardMapper;
import com.gt.union.card.main.service.IUnionCardFanService;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.card.main.util.UnionCardCacheUtil;
import com.gt.union.card.main.vo.CardApplyVO;
import com.gt.union.card.main.vo.CardSocketVO;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.service.IUnionCardProjectService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.main.vo.UnionMainVO;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 联盟卡 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Service
public class UnionCardServiceImpl extends ServiceImpl<UnionCardMapper, UnionCard> implements IUnionCardService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionCardFanService unionCardFanService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionCardActivityService unionCardActivityService;

    @Autowired
    private IUnionCardProjectService unionCardProjectService;

    @Autowired
    private WxPayService wxPayService;

    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public CardApplyVO getCardApplyVOByBusIdAndFanId(Integer busId, Integer fanId, Integer optUnionId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断fanId有效性
        UnionCardFan fan = unionCardFanService.getById(fanId);
        if (fan == null) {
            throw new BusinessException("找不到粉丝信息");
        }
        // （2）	获取商家有效的union
        List<UnionMainVO> busUnionList = unionMainService.listMyValidByBusId(busId);
        // （3）	过滤掉粉丝已办卡，且商家没有新活动卡的union
        List<UnionMain> optionUnionList = new ArrayList<>();
        if (ListUtil.isNotEmpty(busUnionList)) {
            for (UnionMainVO unionVO : busUnionList) {
                Integer unionId = unionVO.getUnion().getId();
                UnionMember member = unionMemberService.getReadByBusIdAndUnionId(busId, unionId);
                if (member == null) {
                    throw new BusinessException("找不到盟员信息");
                }
                UnionCard discountCard = getDiscountCardByFanIdAndUnionId(fanId, unionId);
                if (discountCard == null) {
                    optionUnionList.add(unionVO.getUnion());
                } else {
                    List<UnionCardActivity> sellingActivityCardList = unionCardActivityService.listByUnionIdAndStatus(unionId, CardConstant.ACTIVITY_STATUS_SELLING);
                    if (ListUtil.isNotEmpty(sellingActivityCardList)) {
                        for (UnionCardActivity activity : sellingActivityCardList) {
                            UnionCardProject project = unionCardProjectService.getByActivityIdAndMemberIdAndUnionId(activity.getId(), member.getId(), unionId);
                            if (project != null && CardConstant.PROJECT_STATUS_ACCEPT == project.getStatus()) {
                                UnionCard activityCard = getActivityCardByFanIdAndActivityIdAndUnionId(fanId, activity.getId(), unionId);
                                if (activityCard == null) {
                                    optionUnionList.add(unionVO.getUnion());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (ListUtil.isEmpty(optionUnionList)) {
            return null;
        }
        // （4）	如果unionId存在且有效，则当前联盟为对应union；否则，取第一个union
        CardApplyVO result = new CardApplyVO();
        result.setUnionList(optionUnionList);
        UnionMain currentUnion = null;
        if (optUnionId != null) {
            for (UnionMain optionUnion : optionUnionList) {
                if (optUnionId.equals(optionUnion.getId())) {
                    currentUnion = optionUnion;
                    break;
                }
            }
        }
        if (currentUnion == null) {
            return result;
        }

        UnionMember currentMember = unionMemberService.getReadByBusIdAndUnionId(busId, currentUnion.getId());
        result.setCurrentMember(currentMember);

        // （5）	获取当前联盟粉丝的折扣卡和活动卡情况，若已办理，则不显示；否则，要求活动卡在售卖中状态
        UnionCard discountCard = getDiscountCardByFanIdAndUnionId(fanId, currentUnion.getId());
        if (discountCard == null) {
            result.setIsDiscountCard(CommonConstant.COMMON_YES);
        } else {
            List<UnionCardActivity> sellingActivityCardList = unionCardActivityService.listByUnionIdAndStatus(currentUnion.getId(), CardConstant.ACTIVITY_STATUS_SELLING);
            if (ListUtil.isNotEmpty(sellingActivityCardList)) {
                List<UnionCardActivity> activityList = new ArrayList<>();
                for (UnionCardActivity activity : sellingActivityCardList) {
                    UnionCardProject project = unionCardProjectService.getByActivityIdAndMemberIdAndUnionId(activity.getId(), currentMember.getId(), currentUnion.getId());
                    if (project != null && CardConstant.PROJECT_STATUS_ACCEPT == project.getStatus()) {
                        UnionCard activityCard = getActivityCardByFanIdAndActivityIdAndUnionId(fanId, activity.getId(), currentUnion.getId());
                        if (activityCard != null) {
                            continue;
                        }
                        Integer activityCardCount = countByActivityIdAndUnionId(activity.getId(), currentUnion.getId());
                        if (activityCardCount < activity.getAmount()) {
                            activityList.add(activity);
                        }
                    }
                }
                result.setActivityList(activityList);
            }
        }

        return result;
    }

    @Override
    public UnionCard getDiscountCardByFanIdAndUnionId(Integer fanId, Integer unionId) throws Exception {
        if (fanId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> cardList = listValidByFanIdAndUnionIdAndType(fanId, unionId, CardConstant.CARD_TYPE_DISCOUNT);

        return ListUtil.isNotEmpty(cardList) ? cardList.get(0) : null;
    }

    @Override
    public UnionCard getActivityCardByFanIdAndActivityIdAndUnionId(Integer fanId, Integer activityId, Integer unionId) throws Exception {
        if (fanId == null || activityId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> cardList = listByActivityIdAndUnionId(activityId, unionId);
        if (ListUtil.isNotEmpty(cardList)) {
            for (UnionCard card : cardList) {
                if (fanId.equals(card.getFanId())) {
                    return card;
                }
            }
        }

        return null;
    }

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<UnionCard> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.ge("validity", DateUtil.getCurrentDate())
                .eq("union_id", unionId)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);

        return selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidByFanIdAndUnionId(Integer fanId, Integer unionId) throws Exception {
        if (fanId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.ge("validity", DateUtil.getCurrentDate())
                .eq("fan_id", fanId)
                .eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);

        return selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidByFanIdAndUnionIdAndType(Integer fanId, Integer unionId, Integer type) throws Exception {
        if (fanId == null || unionId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = listValidByFanIdAndUnionId(fanId, unionId);
        result = filterByType(result, type);

        return result;
    }

    @Override
    public List<UnionCard> listByActivityIdAndUnionId(Integer activityId, Integer unionId) throws Exception {
        if (activityId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = listByActivityId(activityId);
        result = filterByType(result, CardConstant.CARD_TYPE_ACTIVITY);
        result = filterByUnionId(result, unionId);

        return result;
    }

    @Override
    public List<UnionCard> listValidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .eq("fan_id", fanId)
                .le("validity", DateUtil.getCurrentDate());

        return selectList(entityWrapper);
    }

    //***************************************** Domain Driven Design - save ********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CardSocketVO saveApplyByBusIdAndFanIdAndUnionId(Integer busId, Integer fanId, Integer unionId, List<Integer> activityIdList) throws Exception {
        if (busId == null || fanId == null || unionId == null || activityIdList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断union有效性和member写权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getWriteByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_WRITE_REJECT);
        }
        // （2）	判断fanId有效性
        UnionCardFan fan = unionCardFanService.getById(fanId);
        if (fan == null) {
            throw new BusinessException("找不到粉丝信息");
        }
        // （3）	判断是否已办理过折扣卡，如果没有，则自动办理
        Date currentDate = DateUtil.getCurrentDate();
        UnionCard discountCard = getDiscountCardByFanIdAndUnionId(fanId, unionId);
        UnionCard saveDiscountCard = null;
        if (discountCard == null) {
            saveDiscountCard = new UnionCard();
            saveDiscountCard.setDelStatus(CommonConstant.DEL_STATUS_NO);
            saveDiscountCard.setCreateTime(currentDate);
            saveDiscountCard.setType(CardConstant.CARD_TYPE_DISCOUNT);
            saveDiscountCard.setFanId(fanId);
            saveDiscountCard.setMemberId(member.getId());
            saveDiscountCard.setUnionId(unionId);
            saveDiscountCard.setName(CardConstant.DISCOUNT_CARD_NAME);
            saveDiscountCard.setValidity(DateUtil.addYears(currentDate, 10));
        }
        if (ListUtil.isEmpty(activityIdList)) {
            if (saveDiscountCard != null) {
                save(saveDiscountCard);
                return null;
            }
            throw new BusinessException("请选择活动卡信息");
        }
        // （4）	判断activity有效性
        List<UnionCard> saveActivityCardList = new ArrayList<>();
        BigDecimal payMoneySum = BigDecimal.ZERO;
        for (Integer activityId : activityIdList) {
            UnionCardActivity activity = unionCardActivityService.getByIdAndUnionId(activityId, unionId);
            if (activity == null) {
                throw new BusinessException("找不到活动卡信息");
            }
            if (CardConstant.ACTIVITY_STATUS_SELLING != unionCardActivityService.getStatus(activity)) {
                throw new BusinessException("活动卡不在售卡状态");
            }
            Integer activityCardCount = countByActivityIdAndUnionId(activityId, unionId);
            if (activityCardCount >= activity.getAmount()) {
                throw new BusinessException("售卡量已达活动卡发行量");
            }
            UnionCard saveActivityCard = new UnionCard();
            saveActivityCard.setDelStatus(CommonConstant.DEL_STATUS_NO);
            saveActivityCard.setCreateTime(currentDate);
            saveActivityCard.setType(CardConstant.CARD_TYPE_ACTIVITY);
            saveActivityCard.setFanId(fanId);
            saveActivityCard.setMemberId(member.getId());
            saveActivityCard.setUnionId(unionId);
            saveActivityCard.setActivityId(activityId);
            saveActivityCard.setName(activity.getName());
            saveActivityCard.setValidity(DateUtil.addDays(currentDate, activity.getValidityDay()));
            saveActivityCardList.add(saveActivityCard);

            payMoneySum = BigDecimalUtil.add(payMoneySum, activity.getPrice());
        }
        // （6）	新增未付款的联盟卡购买记录，并返回支付链接
        String orderNo = "Card_" + busId + "_" + DateUtil.getSerialNumber();
        // TODO 新增售卡记录
        // TODO 新增售卡分成

        CardSocketVO result = new CardSocketVO();
        String socketKey = PropertiesUtil.getSocketKey() + orderNo;
        String notifyUrl = PropertiesUtil.getUnionUrl() + "/unionCard/79B4DE7C/callback?socketKey=" + socketKey;
        String payUrl = wxPayService.qrCodePay(payMoneySum.doubleValue(), null, "LM_" + orderNo,
                null, CommonConstant.COMMON_NO, null, notifyUrl, CommonConstant.COMMON_NO,
                null, 0, null);
        result.setPayUrl(payUrl);
        result.setSocketKey(socketKey);

        return result;
    }

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    @Override
    public Integer countByActivityIdAndUnionId(Integer activityId, Integer unionId) throws Exception {
        if (activityId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> cardList = listByActivityIdAndUnionId(activityId, unionId);

        return ListUtil.isNotEmpty(cardList) ? cardList.size() : 0;
    }

    //***************************************** Domain Driven Design - boolean *****************************************

    @Override
    public boolean existValidByFanIdAndUnionIdAndType(Integer fanId, Integer unionId, Integer type) throws Exception {
        if (fanId == null || unionId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> cardList = listValidByFanIdAndUnionIdAndType(fanId, unionId, type);

        return ListUtil.isNotEmpty(cardList);
    }

    //***************************************** Domain Driven Design - filter ******************************************

    @Override
    public List<UnionCard> filterByType(List<UnionCard> cardList, Integer type) throws Exception {
        if (cardList == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = new ArrayList<>();
        for (UnionCard card : cardList) {
            if (type.equals(card.getType())) {
                result.add(card);
            }
        }

        return result;
    }

    @Override
    public List<UnionCard> filterByUnionId(List<UnionCard> cardList, Integer unionId) throws Exception {
        if (cardList == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = new ArrayList<>();
        for (UnionCard card : cardList) {
            if (unionId.equals(card.getUnionId())) {
                result.add(card);
            }
        }

        return result;
    }

    //***************************************** Object As a Service - get **********************************************

    public UnionCard getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCard result;
        // (1)cache
        String idKey = UnionCardCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionCard.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionCard> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCard> result;
        // (1)cache
        String memberIdKey = UnionCardCacheUtil.getMemberIdKey(memberId);
        if (redisCacheUtil.exists(memberIdKey)) {
            String tempStr = redisCacheUtil.get(memberIdKey);
            result = JSONArray.parseArray(tempStr, UnionCard.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, memberId, UnionCardCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    public List<UnionCard> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCard> result;
        // (1)cache
        String unionIdKey = UnionCardCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionCard.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionCardCacheUtil.TYPE_UNION_ID);
        return result;
    }

    public List<UnionCard> listByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCard> result;
        // (1)cache
        String fanIdKey = UnionCardCacheUtil.getFanIdKey(fanId);
        if (redisCacheUtil.exists(fanIdKey)) {
            String tempStr = redisCacheUtil.get(fanIdKey);
            result = JSONArray.parseArray(tempStr, UnionCard.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("fan_id", fanId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, fanId, UnionCardCacheUtil.TYPE_FAN_ID);
        return result;
    }

    public List<UnionCard> listByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionCard> result;
        // (1)cache
        String activityIdKey = UnionCardCacheUtil.getActivityIdKey(activityId);
        if (redisCacheUtil.exists(activityIdKey)) {
            String tempStr = redisCacheUtil.get(activityIdKey);
            result = JSONArray.parseArray(tempStr, UnionCard.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_id", activityId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, activityId, UnionCardCacheUtil.TYPE_ACTIVITY_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCard newUnionCard) throws Exception {
        if (newUnionCard == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionCard);
        removeCache(newUnionCard);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCard> newUnionCardList) throws Exception {
        if (newUnionCardList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionCardList);
        removeCache(newUnionCardList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionCard unionCard = getById(id);
        removeCache(unionCard);
        // (2)remove in db logically
        UnionCard removeUnionCard = new UnionCard();
        removeUnionCard.setId(id);
        removeUnionCard.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionCard);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCard> unionCardList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCard unionCard = getById(id);
            unionCardList.add(unionCard);
        }
        removeCache(unionCardList);
        // (2)remove in db logically
        List<UnionCard> removeUnionCardList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCard removeUnionCard = new UnionCard();
            removeUnionCard.setId(id);
            removeUnionCard.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardList.add(removeUnionCard);
        }
        updateBatchById(removeUnionCardList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCard updateUnionCard) throws Exception {
        if (updateUnionCard == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCard.getId();
        UnionCard unionCard = getById(id);
        removeCache(unionCard);
        // (2)update db
        updateById(updateUnionCard);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCard> updateUnionCardList) throws Exception {
        if (updateUnionCardList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionCard updateUnionCard : updateUnionCardList) {
            idList.add(updateUnionCard.getId());
        }
        List<UnionCard> unionCardList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCard unionCard = getById(id);
            unionCardList.add(unionCard);
        }
        removeCache(unionCardList);
        // (2)update db
        updateBatchById(updateUnionCardList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionCard newUnionCard, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCard);
    }

    private void setCache(List<UnionCard> newUnionCardList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionCardCacheUtil.TYPE_MEMBER_ID:
                foreignIdKey = UnionCardCacheUtil.getMemberIdKey(foreignId);
                break;
            case UnionCardCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionCardCacheUtil.getUnionIdKey(foreignId);
                break;
            case UnionCardCacheUtil.TYPE_FAN_ID:
                foreignIdKey = UnionCardCacheUtil.getFanIdKey(foreignId);
                break;
            case UnionCardCacheUtil.TYPE_ACTIVITY_ID:
                foreignIdKey = UnionCardCacheUtil.getActivityIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionCardList);
        }
    }

    private void removeCache(UnionCard unionCard) {
        if (unionCard == null) {
            return;
        }
        Integer id = unionCard.getId();
        String idKey = UnionCardCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer memberId = unionCard.getMemberId();
        if (memberId != null) {
            String memberIdKey = UnionCardCacheUtil.getMemberIdKey(memberId);
            redisCacheUtil.remove(memberIdKey);
        }

        Integer unionId = unionCard.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionCardCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }

        Integer fanId = unionCard.getFanId();
        if (fanId != null) {
            String fanIdKey = UnionCardCacheUtil.getFanIdKey(fanId);
            redisCacheUtil.remove(fanIdKey);
        }

        Integer activityId = unionCard.getActivityId();
        if (activityId != null) {
            String activityIdKey = UnionCardCacheUtil.getActivityIdKey(activityId);
            redisCacheUtil.remove(activityIdKey);
        }
    }

    private void removeCache(List<UnionCard> unionCardList) {
        if (ListUtil.isEmpty(unionCardList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCard unionCard : unionCardList) {
            idList.add(unionCard.getId());
        }
        List<String> idKeyList = UnionCardCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> memberIdKeyList = getForeignIdKeyList(unionCardList, UnionCardCacheUtil.TYPE_MEMBER_ID);
        if (ListUtil.isNotEmpty(memberIdKeyList)) {
            redisCacheUtil.remove(memberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionCardList, UnionCardCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

        List<String> fanIdKeyList = getForeignIdKeyList(unionCardList, UnionCardCacheUtil.TYPE_FAN_ID);
        if (ListUtil.isNotEmpty(fanIdKeyList)) {
            redisCacheUtil.remove(fanIdKeyList);
        }

        List<String> activityIdKeyList = getForeignIdKeyList(unionCardList, UnionCardCacheUtil.TYPE_ACTIVITY_ID);
        if (ListUtil.isNotEmpty(activityIdKeyList)) {
            redisCacheUtil.remove(activityIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionCard> unionCardList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionCardCacheUtil.TYPE_MEMBER_ID:
                for (UnionCard unionCard : unionCardList) {
                    Integer memberId = unionCard.getMemberId();
                    if (memberId != null) {
                        String memberIdKey = UnionCardCacheUtil.getMemberIdKey(memberId);
                        result.add(memberIdKey);
                    }
                }
                break;
            case UnionCardCacheUtil.TYPE_UNION_ID:
                for (UnionCard unionCard : unionCardList) {
                    Integer unionId = unionCard.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionCardCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            case UnionCardCacheUtil.TYPE_FAN_ID:
                for (UnionCard unionCard : unionCardList) {
                    Integer fanId = unionCard.getFanId();
                    if (fanId != null) {
                        String fanIdKey = UnionCardCacheUtil.getFanIdKey(fanId);
                        result.add(fanIdKey);
                    }
                }
                break;
            case UnionCardCacheUtil.TYPE_ACTIVITY_ID:
                for (UnionCard unionCard : unionCardList) {
                    Integer activityId = unionCard.getActivityId();
                    if (activityId != null) {
                        String activityIdKey = UnionCardCacheUtil.getActivityIdKey(activityId);
                        result.add(activityIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }
}