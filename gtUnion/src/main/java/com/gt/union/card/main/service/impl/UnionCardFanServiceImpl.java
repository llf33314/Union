package com.gt.union.card.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.card.main.constant.CardConstant;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.mapper.UnionCardFanMapper;
import com.gt.union.card.main.service.IUnionCardFanService;
import com.gt.union.card.main.service.IUnionCardIntegralService;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.card.main.util.UnionCardFanCacheUtil;
import com.gt.union.card.main.vo.CardFanDetailVO;
import com.gt.union.card.main.vo.CardFanSearchVO;
import com.gt.union.card.main.vo.CardFanVO;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.ListUtil;
import com.gt.union.common.util.RedisCacheUtil;
import com.gt.union.common.util.StringUtil;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟卡根信息 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:13
 */
@Service
public class UnionCardFanServiceImpl extends ServiceImpl<UnionCardFanMapper, UnionCardFan> implements IUnionCardFanService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionCardService unionCardService;

    @Autowired
    private IUnionCardIntegralService unionCardIntegralService;

    @Autowired
    private IDictService dictService;

    //***************************************** Domain Driven Design - get *********************************************

    @Override
    public CardFanDetailVO getFanDetailVOByBusIdAndIdAndUnionId(Integer busId, Integer fanId, Integer unionId) throws Exception {
        if (fanId == null || busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）	判断union有效性和member读权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_READ_REJECT);
        }
        // （2）	判断fanId有效性
        UnionCardFan fan = getById(fanId);
        if (fan == null) {
            throw new BusinessException("找不到粉丝信息");
        }
        // （3）	获取折扣卡和活动卡，活动卡按时间倒序排序
        List<UnionCard> cardList = unionCardService.listValidByUnionIdAndFanId(unionId, fanId);
        List<UnionCard> discountCardList = unionCardService.filterByType(cardList, CardConstant.TYPE_DISCOUNT);
        CardFanDetailVO result = new CardFanDetailVO();
        if (ListUtil.isNotEmpty(discountCardList)) {
            UnionCard discountCard = discountCardList.get(0);
            result.setDiscountCard(discountCard);
            UnionMember discountCardMember = unionMemberService.getReadByIdAndUnionId(discountCard.getMemberId(), unionId);
            result.setDiscount(discountCardMember != null ? discountCardMember.getDiscount() : null);
        }
        List<UnionCard> activityCardList = unionCardService.filterByType(cardList, CardConstant.TYPE_ACTIVITY);
        result.setActivityCardList(activityCardList);

        return result;
    }

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<CardFanVO> listCardFanVoByBusIdAndUnionId(Integer busId, Integer unionId, String optNumber, String optPhone) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）	判断union有效性和member读权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_READ_REJECT);
        }

        // （2）	获取union下的fan，要求fan在该union下有折扣卡
        List<UnionCardFan> fanList = listWithValidDiscountCardByUnionId(unionId, optNumber, optPhone);

        // （3）	统计联盟积分=指定联盟折扣卡积分+指定联盟活动卡积分
        List<CardFanVO> result = new ArrayList<>();
        for (UnionCardFan fan : fanList) {
            CardFanVO fanVO = new CardFanVO();
            fanVO.setFan(fan);
            fanVO.setIntegral(unionCardIntegralService.countIntegralByUnionIdAndFanId(unionId, fan.getId()));
            result.add(fanVO);
        }

        return result;
    }

    @Override
    public List<UnionCardFan> listWithValidDiscountCardByUnionId(Integer unionId, String optNumber, String optPhone) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardFan> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .eq(StringUtil.isNotEmpty(optNumber), "number", optNumber)
                .eq(StringUtil.isNotEmpty(optPhone), "phone", optPhone)
                .exists(" SELECT c.id FROM t_union_card c "
                        + " WHERE c.fan_id=t_union_card_fan.id "
                        + " AND c.union_id=" + unionId
                        + " AND c.type=" + CardConstant.TYPE_DISCOUNT
                        + " AND c.del_status=" + CommonConstant.COMMON_NO
                        + " AND c.validity >= now() ");
        
        return selectList(entityWrapper);
    }

    //***************************************** Domain Driven Design - save ********************************************

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Object As a Service - get **********************************************

    @Override
    public UnionCardFan getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardFan result;
        // (1)cache
        String idKey = UnionCardFanCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionCardFan.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionCardFan> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    @Override
    public CardFanSearchVO getCardFanSearchVOByBusId(Integer busId, String numberOrPhone, Integer optUnionId) throws Exception {
        if (busId == null || numberOrPhone == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断numberOrPhone有效性
        UnionCardFan fan = getByNumberOrPhone(numberOrPhone);
        if (fan == null) {
            return null;
        }
        // （2）	获取商家所有有效的unionList
        List<UnionMain> busUnionList = unionMainService.listMyValidByBusId(busId);
        List<Integer> busUnionIdList = new ArrayList<>();
        if (ListUtil.isNotEmpty(busUnionList)) {
            for (UnionMain union : busUnionList) {
                busUnionIdList.add(union.getId());
            }
        }
        // （3）	过滤掉一些粉丝没办折扣卡或存在有效活动卡的union
        List<UnionCard> cardList = unionCardService.listValidByFanId(fan.getId());
        List<Integer> fanUnionIdList = new ArrayList<>();
        if (ListUtil.isNotEmpty(cardList)) {
            for (UnionCard card : cardList) {
                fanUnionIdList.add(card.getUnionId());
            }
        }
        List<Integer> validUnionIdList = ListUtil.intersection(busUnionIdList, fanUnionIdList);
        // （4）	如果没有剩余的union，则返回，否则，进行下一步
        if (ListUtil.isEmpty(validUnionIdList)) {
            return null;
        }
        // （5）	判断unionId是否在剩余的union中，如果是，则当前联盟为该union，否则默认为第一个union
        CardFanSearchVO result = new CardFanSearchVO();
        result.setFan(fan);
        List<UnionMain> validUnionList = new ArrayList<>();
        for (Integer validUnionId : validUnionIdList) {
            UnionMain validUnion = unionMainService.getById(validUnionId);
            validUnionList.add(validUnion);
        }
        result.setUnionList(validUnionList);

        Integer currentUnionId = optUnionId != null && validUnionIdList.contains(optUnionId) ? optUnionId : validUnionIdList.get(0);
        UnionMain currentUnion = unionMainService.getById(currentUnionId);
        result.setCurrentUnion(currentUnion);

        // （6）	获取union对应的member，获得折扣信息
        UnionMember currentMember = unionMemberService.getReadByBusIdAndUnionId(busId, currentUnionId);
        result.setCurrentMember(currentMember);

        // （7）	获取粉丝联盟积分
        Double unionIntegral = unionCardIntegralService.countIntegralByUnionIdAndFanId(currentUnionId, fan.getId());
        result.setIntegral(unionIntegral);

        // （8）	如果粉丝在union下的存在有效的活动卡，则优惠项目可用；否则，不可用
        boolean isProjectAvailable = unionCardService.existValidByUnionIdAndFanIdAndType(currentUnionId, fan.getId(), CardConstant.TYPE_ACTIVITY);
        result.setIsProjectAvailable(isProjectAvailable ? CommonConstant.COMMON_YES : CommonConstant.COMMON_NO);

        // （9）	获取消费多少积分可以抵扣1元配置
        Double exchangeIntegral = dictService.getExchangeIntegral();
        result.setExchangeIntegral(exchangeIntegral);

        return result;
    }

    @Override
    public UnionCardFan getByNumberOrPhone(String numberOrPhone) throws Exception {
        if (numberOrPhone == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardFan> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .andNew()
                .eq("number", numberOrPhone)
                .or()
                .eq("phone", numberOrPhone);

        return selectOne(entityWrapper);
    }

    @Override
    public UnionCardFan getByPhone(String phone) throws Exception {
        if (phone == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardFan> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .eq("phone", phone);

        return selectOne(entityWrapper);
    }

    //***************************************** Object As a Service - list *********************************************

    //***************************************** Object As a Service - save *********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardFan newUnionCardRoot) throws Exception {
        if (newUnionCardRoot == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionCardRoot);
        removeCache(newUnionCardRoot);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardFan> newUnionCardRootList) throws Exception {
        if (newUnionCardRootList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionCardRootList);
        removeCache(newUnionCardRootList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionCardFan unionCardRoot = getById(id);
        removeCache(unionCardRoot);
        // (2)remove in db logically
        UnionCardFan removeUnionCardRoot = new UnionCardFan();
        removeUnionCardRoot.setId(id);
        removeUnionCardRoot.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionCardRoot);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardFan> unionCardRootList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardFan unionCardRoot = getById(id);
            unionCardRootList.add(unionCardRoot);
        }
        removeCache(unionCardRootList);
        // (2)remove in db logically
        List<UnionCardFan> removeUnionCardRootList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardFan removeUnionCardRoot = new UnionCardFan();
            removeUnionCardRoot.setId(id);
            removeUnionCardRoot.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardRootList.add(removeUnionCardRoot);
        }
        updateBatchById(removeUnionCardRootList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardFan updateUnionCardRoot) throws Exception {
        if (updateUnionCardRoot == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCardRoot.getId();
        UnionCardFan unionCardRoot = getById(id);
        removeCache(unionCardRoot);
        // (2)update db
        updateById(updateUnionCardRoot);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardFan> updateUnionCardRootList) throws Exception {
        if (updateUnionCardRootList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionCardFan updateUnionCardRoot : updateUnionCardRootList) {
            idList.add(updateUnionCardRoot.getId());
        }
        List<UnionCardFan> unionCardRootList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardFan unionCardRoot = getById(id);
            unionCardRootList.add(unionCardRoot);
        }
        removeCache(unionCardRootList);
        // (2)update db
        updateBatchById(updateUnionCardRootList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionCardFan newUnionCardRoot, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardFanCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCardRoot);
    }

    private void setCache(List<UnionCardFan> newUnionCardRootList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionCardRootList);
        }
    }

    private void removeCache(UnionCardFan unionCardRoot) {
        if (unionCardRoot == null) {
            return;
        }
        Integer id = unionCardRoot.getId();
        String idKey = UnionCardFanCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);
    }

    private void removeCache(List<UnionCardFan> unionCardRootList) {
        if (ListUtil.isEmpty(unionCardRootList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCardFan unionCardRoot : unionCardRootList) {
            idList.add(unionCardRoot.getId());
        }
        List<String> idKeyList = UnionCardFanCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);
    }

    private List<String> getForeignIdKeyList(List<UnionCardFan> unionCardRootList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            default:
                break;
        }
        return result;
    }
}