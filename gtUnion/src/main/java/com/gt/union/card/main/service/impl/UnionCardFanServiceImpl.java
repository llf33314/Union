package com.gt.union.card.main.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.card.main.constant.CardConstant;
import com.gt.union.card.main.dao.IUnionCardFanDao;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.card.main.entity.UnionCardFan;
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
import com.gt.union.common.util.*;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 联盟卡根信息 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:13
 */
@Service
public class UnionCardFanServiceImpl implements IUnionCardFanService {
    @Autowired
    private IUnionCardFanDao unionCardFanDao;

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

    //********************************************* Base On Business - get *********************************************

    @Override
    public CardFanDetailVO getFanDetailVOByBusIdAndIdAndUnionId(Integer busId, Integer fanId, Integer unionId) throws Exception {
        if (fanId == null || busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）	判断union有效性和member读权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // （2）	判断fanId有效性
        UnionCardFan fan = getValidById(fanId);
        if (fan == null) {
            throw new BusinessException("找不到粉丝信息");
        }
        // （3）	获取有效的折扣卡信息
        List<UnionCard> cardList = unionCardService.listValidUnexpiredByUnionIdAndFanId(unionId, fanId);
        List<UnionCard> discountCardList = unionCardService.filterByType(cardList, CardConstant.TYPE_DISCOUNT);
        CardFanDetailVO result = new CardFanDetailVO();
        if (ListUtil.isNotEmpty(discountCardList)) {
            UnionCard discountCard = discountCardList.get(0);
            result.setDiscountCard(discountCard);
            UnionMember discountCardMember = unionMemberService.getValidReadByIdAndUnionId(discountCard.getMemberId(), unionId);
            result.setDiscount(discountCardMember != null ? discountCardMember.getDiscount() : null);
        }
        // （4）获取有效的活动卡信息，并按时间倒序排序
        List<UnionCard> activityCardList = unionCardService.filterByType(cardList, CardConstant.TYPE_ACTIVITY);
        Collections.sort(activityCardList, new Comparator<UnionCard>() {
            @Override
            public int compare(UnionCard o1, UnionCard o2) {
                return o2.getCreateTime().compareTo(o1.getCreateTime());
            }
        });
        result.setActivityCardList(activityCardList);

        return result;
    }

    @Override
    public CardFanSearchVO getCardFanSearchVOByBusId(Integer busId, String numberOrPhone, Integer optUnionId) throws Exception {
        if (busId == null || numberOrPhone == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）	判断numberOrPhone有效性
        UnionCardFan fan = getValidByNumberOrPhone(numberOrPhone);
        if (fan == null) {
            throw new BusinessException("找不到联盟卡号");
        }
        // （2）	获取商家所有有效的unionList
        List<UnionMember> busMemberList = unionMemberService.listValidReadByBusId(busId);
        if (optUnionId != null) {
            busMemberList = unionMemberService.filterByUnionId(busMemberList, optUnionId);
        }
        List<Integer> busUnionIdList = unionMemberService.getUnionIdList(busMemberList);
        // （3）	过滤掉一些粉丝没办折扣卡的union
        List<UnionCard> cardList = unionCardService.listValidUnexpiredByFanIdAndType(fan.getId(), CardConstant.TYPE_DISCOUNT);
        List<Integer> fanUnionIdList = unionCardService.getUnionIdList(cardList);

        List<Integer> tgtUnionIdList = ListUtil.intersection(busUnionIdList, fanUnionIdList);
        // （4）	如果没有剩余的union，则返回，否则，进行下一步
        if (ListUtil.isEmpty(tgtUnionIdList)) {
            return null;
        }
        // （5）	判断unionId是否在剩余的union中，如果是，则当前联盟为该union，否则默认为第一个union
        CardFanSearchVO result = new CardFanSearchVO();
        result.setFan(fan);
        List<UnionMain> tgtUnionList = new ArrayList<>();
        for (Integer tgtUnionId : tgtUnionIdList) {
            UnionMain tgtUnion = unionMainService.getValidById(tgtUnionId);
            tgtUnionList.add(tgtUnion);
        }
        result.setUnionList(tgtUnionList);

        Integer currentUnionId = optUnionId != null && tgtUnionIdList.contains(optUnionId) ? optUnionId : tgtUnionIdList.get(0);
        UnionMain currentUnion = unionMainService.getValidById(currentUnionId);
        result.setCurrentUnion(currentUnion);

        // （6）	获取union对应的member，获得折扣信息
        UnionMember currentMember = unionMemberService.getValidReadByBusIdAndUnionId(busId, currentUnionId);
        result.setCurrentMember(currentMember);

        // （7）	获取粉丝联盟积分
        Double unionIntegral = unionCardIntegralService.sumValidIntegralByUnionIdAndFanId(currentUnionId, fan.getId());
        result.setIntegral(unionIntegral);

        // （8）	如果粉丝在union下的存在有效的活动卡，则优惠项目可用；否则，不可用
        boolean isProjectAvailable = unionCardService.existValidUnexpiredByUnionIdAndFanIdAndType(currentUnionId, fan.getId(), CardConstant.TYPE_ACTIVITY);
        result.setIsProjectAvailable(isProjectAvailable ? CommonConstant.COMMON_YES : CommonConstant.COMMON_NO);

        // （9）	获取消费多少积分可以抵扣1元配置
        Double exchangeIntegral = dictService.getExchangeIntegral();
        result.setExchangeIntegral(exchangeIntegral);

        return result;
    }

    @Override
    public UnionCardFan getValidByNumberOrPhone(String numberOrPhone) throws Exception {
        if (numberOrPhone == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardFan> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .andNew()
                .eq("number", numberOrPhone)
                .or()
                .eq("phone", numberOrPhone);

        return unionCardFanDao.selectOne(entityWrapper);
    }

    @Override
    public UnionCardFan getValidByPhone(String phone) throws Exception {
        if (phone == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCardFan> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .eq("phone", phone);

        return unionCardFanDao.selectOne(entityWrapper);
    }

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<CardFanVO> listCardFanVoByBusIdAndUnionId(Integer busId, Integer unionId, String optNumber, String optPhone) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （1）	判断union有效性和member读权限
        if (!unionMainService.isUnionValid(unionId)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }

        // （2）	获取union下具有有效折扣卡的fan，并根据卡号和手机号进行过滤
        EntityWrapper<UnionCardFan> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .like(StringUtil.isNotEmpty(optNumber), "number", optNumber)
                .like(StringUtil.isNotEmpty(optPhone), "phone", optPhone)
                .exists(" SELECT c.id FROM t_union_card c "
                        + " WHERE c.fan_id=t_union_card_fan.id "
                        + " AND c.union_id=" + unionId
                        + " AND c.type=" + CardConstant.TYPE_DISCOUNT
                        + " AND c.del_status=" + CommonConstant.COMMON_NO
                        + " AND c.validity >= now() ");
        List<UnionCardFan> fanList = unionCardFanDao.selectList(entityWrapper);

        // （3）	统计粉丝联盟积分，即fan在union下所有有效的折扣卡和活动卡的积分之和
        List<CardFanVO> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(fanList)) {
            for (UnionCardFan fan : fanList) {
                CardFanVO vo = new CardFanVO();
                vo.setFan(fan);
                vo.setIntegral(unionCardIntegralService.sumValidIntegralByUnionIdAndFanId(unionId, fan.getId()));
                result.add(vo);
            }
        }

        return result;
    }

    //********************************************* Base On Business - save ********************************************

    @Override
    public UnionCardFan getOrSaveByPhone(String phone) throws Exception {
        String key = RedissonKeyUtil.getUnionCardFanByPhoneKey(phone);
        RedissonLockUtil.lock(key, 5);
        UnionCardFan fan = getValidByPhone(phone);
        if (fan == null) {
            fan = new UnionCardFan();
            fan.setDelStatus(CommonConstant.COMMON_NO);
            fan.setCreateTime(DateUtil.getCurrentDate());
            fan.setPhone(phone);
            fan.setNumber(UnionCardUtil.generateCardNo());
            this.save(fan);
        }
        RedissonLockUtil.unlock(key);
        return fan;
    }

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    //********************************************* Base On Business - other *******************************************

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionCardFan> filterByDelStatus(List<UnionCardFan> unionCardFanList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardFan> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardFanList)) {
            for (UnionCardFan unionCardFan : unionCardFanList) {
                if (delStatus.equals(unionCardFan.getDelStatus())) {
                    result.add(unionCardFan);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

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
        result = unionCardFanDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionCardFan getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardFan result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionCardFan getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionCardFan result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionCardFan> unionCardFanList) throws Exception {
        if (unionCardFanList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardFanList)) {
            for (UnionCardFan unionCardFan : unionCardFanList) {
                result.add(unionCardFan.getId());
            }
        }

        return result;
    }

    @Override
    public List<UnionCardFan> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCardFan> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionCardFan> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardFanDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCardFan newUnionCardFan) throws Exception {
        if (newUnionCardFan == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionCardFanDao.insert(newUnionCardFan);
        removeCache(newUnionCardFan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCardFan> newUnionCardFanList) throws Exception {
        if (newUnionCardFanList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionCardFanDao.insertBatch(newUnionCardFanList);
        removeCache(newUnionCardFanList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionCardFan unionCardFan = getById(id);
        removeCache(unionCardFan);
        // (2)remove in db logically
        UnionCardFan removeUnionCardFan = new UnionCardFan();
        removeUnionCardFan.setId(id);
        removeUnionCardFan.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionCardFanDao.updateById(removeUnionCardFan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionCardFan> unionCardFanList = listByIdList(idList);
        removeCache(unionCardFanList);
        // (2)remove in db logically
        List<UnionCardFan> removeUnionCardFanList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCardFan removeUnionCardFan = new UnionCardFan();
            removeUnionCardFan.setId(id);
            removeUnionCardFan.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardFanList.add(removeUnionCardFan);
        }
        unionCardFanDao.updateBatchById(removeUnionCardFanList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCardFan updateUnionCardFan) throws Exception {
        if (updateUnionCardFan == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionCardFan.getId();
        UnionCardFan unionCardFan = getById(id);
        removeCache(unionCardFan);
        // (2)update db
        unionCardFanDao.updateById(updateUnionCardFan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCardFan> updateUnionCardFanList) throws Exception {
        if (updateUnionCardFanList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionCardFanList);
        List<UnionCardFan> unionCardFanList = listByIdList(idList);
        removeCache(unionCardFanList);
        // (2)update db
        unionCardFanDao.updateBatchById(updateUnionCardFanList);
    }

    //********************************************* Object As a Service - cache support ********************************

    private void setCache(UnionCardFan newUnionCardFan, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionCardFanCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionCardFan);
    }

    private void setCache(List<UnionCardFan> newUnionCardFanList, Integer foreignId, int foreignIdType) {
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
            redisCacheUtil.set(foreignIdKey, newUnionCardFanList);
        }
    }

    private void setValidCache(List<UnionCardFan> newUnionCardFanList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionCardFanList);
        }
    }

    private void setInvalidCache(List<UnionCardFan> newUnionCardFanList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionCardFanList);
        }
    }

    private void removeCache(UnionCardFan unionCardFan) {
        if (unionCardFan == null) {
            return;
        }
        Integer id = unionCardFan.getId();
        String idKey = UnionCardFanCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

    }

    private void removeCache(List<UnionCardFan> unionCardFanList) {
        if (ListUtil.isEmpty(unionCardFanList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionCardFan unionCardFan : unionCardFanList) {
            idList.add(unionCardFan.getId());
        }
        List<String> idKeyList = UnionCardFanCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

    }

    private List<String> getForeignIdKeyList(List<UnionCardFan> unionCardFanList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {

            default:
                break;
        }
        return result;
    }

}