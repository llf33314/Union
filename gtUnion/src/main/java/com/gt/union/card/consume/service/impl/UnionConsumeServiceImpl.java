package com.gt.union.card.consume.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.shop.ShopService;
import com.gt.union.card.constant.CardConstant;
import com.gt.union.card.consume.entity.UnionConsume;
import com.gt.union.card.consume.entity.UnionConsumeProject;
import com.gt.union.card.consume.mapper.UnionConsumeMapper;
import com.gt.union.card.consume.service.IUnionConsumeProjectService;
import com.gt.union.card.consume.service.IUnionConsumeService;
import com.gt.union.card.consume.util.UnionConsumeCacheUtil;
import com.gt.union.card.consume.vo.ConsumePayVO;
import com.gt.union.card.consume.vo.ConsumePostVO;
import com.gt.union.card.consume.vo.ConsumeVO;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.entity.UnionCardIntegral;
import com.gt.union.card.main.service.IUnionCardFanService;
import com.gt.union.card.main.service.IUnionCardIntegralService;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.card.project.service.IUnionCardProjectItemService;
import com.gt.union.card.project.service.IUnionCardProjectService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.main.vo.UnionMainVO;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import com.gt.util.entity.result.shop.WsWxShopInfoExtend;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 消费核销 服务实现类
 *
 * @author linweicong
 * @version 2017-11-25 10:51:42
 */
@Service
public class UnionConsumeServiceImpl extends ServiceImpl<UnionConsumeMapper, UnionConsume> implements IUnionConsumeService {

    private Logger logger = Logger.getLogger(UnionConsumeServiceImpl.class);

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionCardFanService unionCardFanService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private IDictService dictService;

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private IUnionCardIntegralService unionCardIntegralService;

    @Autowired
    private IUnionCardProjectItemService unionCardProjectItemService;

    @Autowired
    private IUnionCardProjectService unionCardProjectService;

    @Autowired
    private IUnionConsumeProjectService unionConsumeProjectService;

    //***************************************** Domain Driven Design - get *********************************************

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public List<ConsumeVO> listConsumeVOByBusId(Integer busId, Integer optUnionId, Integer optShopId, String optCardNumber, String optPhone, Date optBeginTime, Date optEndTime) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<ConsumeVO> result = new ArrayList<>();
        // （1）	获取我的所有有效的union
        List<UnionMainVO> validUnionList = unionMainService.listMyValidByBusId(busId);
        List<Integer> validUnionIdList = new ArrayList<>();
        if (ListUtil.isNotEmpty(validUnionList)) {
            for (UnionMainVO validUnion : validUnionList) {
                Integer validUnionId = validUnion.getUnion().getId();
                if (optUnionId != null && !optUnionId.equals(validUnionId)) {
                    continue;
                }
                validUnionIdList.add(validUnionId);
            }
        }
        if (ListUtil.isEmpty(validUnionIdList)) {
            return result;
        }
        // （2）	获取我的所有有效的member
        List<Integer> validMemberIdList = new ArrayList<>();
        for (Integer validUnionId : validUnionIdList) {
            UnionMember validMember = unionMemberService.getReadByBusIdAndUnionId(busId, validUnionId);
            if (validMember != null) {
                validMemberIdList.add(validMember.getId());
            }
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("member_id", validMemberIdList)
                .in("union_id", validUnionIdList)
                .eq(optShopId != null, "shop_id = {0}", optShopId)
                .gt(optBeginTime != null, "create_time >= {0}", optBeginTime)
                .gt(optEndTime != null, "create_time <= {0}", optEndTime)
                .exists(StringUtil.isNotEmpty(optCardNumber) || StringUtil.isNotEmpty(optPhone),
                        " SELECT f.id FROM t_union_card_fan f "
                                + " WHERE f.del_status = " + CommonConstant.DEL_STATUS_NO
                                + " AND f.id = t_union_consume.fan_id "
                                + (StringUtil.isNotEmpty(optCardNumber) ? (" AND f.number LIKE '%" + optCardNumber + "%' ") : "")
                                + (StringUtil.isNotEmpty(optPhone) ? (" AND f.phone LIKE '%" + optPhone + "%' ") : ""));
        List<UnionConsume> consumeList = selectList(entityWrapper);

        if (ListUtil.isNotEmpty(consumeList)) {
            for (UnionConsume consume : consumeList) {
                ConsumeVO vo = new ConsumeVO();
                vo.setConsume(consume);
                vo.setUnion(unionMainService.getById(consume.getUnionId()));
                vo.setFan(unionCardFanService.getById(consume.getFanId()));
                List<Integer> shopIdList = Arrays.asList(consume.getShopId());
                List<WsWxShopInfoExtend> shopList = shopService.listByIds(shopIdList);
                if (ListUtil.isNotEmpty(shopList)) {
                    vo.setShopName(shopList.get(0).getBusinessName());
                }
                List<UnionConsumeProject> consumeProjectList = unionConsumeProjectService.listByConsumeId(consume.getId());
                List<UnionCardProjectItem> itemList = new ArrayList<>();
                if (ListUtil.isNotEmpty(consumeProjectList)) {
                    for (UnionConsumeProject consumeProject : consumeProjectList) {
                        UnionCardProjectItem item = unionCardProjectItemService.getById(consumeProject.getProjectItemId());
                        if (item != null) {
                            itemList.add(item);
                        }
                    }
                }
                vo.setTextList(itemList);

                result.add(vo);
            }
        }
        // （3）	按支付时间倒序排序
        Collections.sort(result, new Comparator<ConsumeVO>() {
            @Override
            public int compare(ConsumeVO o1, ConsumeVO o2) {
                return o1.getConsume().getCreateTime().compareTo(o2.getConsume().getCreateTime());
            }
        });

        return result;
    }

    //***************************************** Domain Driven Design - save ********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConsumePayVO saveConsumePayVOByBusIdAndUnionIdAndFanId(Integer busId, Integer unionId, Integer fanId, ConsumePostVO vo) throws Exception {
        if (busId == null || unionId == null || fanId == null || vo == null) {
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
        // （3）	校验表单数据
        UnionConsume saveConsume = new UnionConsume();
        saveConsume.setDelStatus(CommonConstant.DEL_STATUS_NO);
        Date currentDate = DateUtil.getCurrentDate();
        saveConsume.setCreateTime(currentDate);
        saveConsume.setFanId(fanId);
        saveConsume.setMemberId(member.getId());
        saveConsume.setUnionId(unionId);

        Integer shopId = vo.getShopId();
        if (shopId == null) {
            throw new BusinessException("找不到门店信息");
        }
        List<Integer> shopIdList = new ArrayList<>();
        shopIdList.add(shopId);
        List<WsWxShopInfoExtend> shopList = shopService.listByIds(shopIdList);
        if (ListUtil.isEmpty(shopList)) {
            throw new BusinessException("门店信息错误");
        }
        saveConsume.setShopId(shopId);

        UnionConsume voConsume = vo.getConsume();
        if (voConsume == null) {
            throw new BusinessException("找不到支付金额信息");
        }
        Double consumeMoney = voConsume.getConsumeMoney();
        if (consumeMoney == null || consumeMoney <= 0) {
            throw new BusinessException("消费金额不能为空，且必须大于0");
        }
        if (member.getDiscount() == null) {
            throw new BusinessException("未设置折扣");
        }
        BigDecimal discount = BigDecimalUtil.divide(member.getDiscount(), BigDecimal.TEN);
        BigDecimal discountMoney = BigDecimalUtil.multiply(consumeMoney, discount);

        BigDecimal payMoney = BigDecimalUtil.subtract(consumeMoney, discountMoney);
        Integer isUserIntegral = vo.getIsUserIntegral();
        UnionCardIntegral integral = unionCardIntegralService.getByFanIdAndUnionId(fanId, unionId);
        if (CommonConstant.COMMON_YES == isUserIntegral) {
            Double integralExchangeRatio = member.getIntegralExchangeRatio();
            if (integralExchangeRatio == null) {
                throw new BusinessException("未设置积分兑换率");
            }
            BigDecimal exchangeRatio = BigDecimalUtil.divide(integralExchangeRatio, Double.valueOf(100));
            BigDecimal exchangeMoney = BigDecimalUtil.multiply(payMoney, exchangeRatio);
            payMoney = BigDecimalUtil.subtract(payMoney, exchangeMoney);

            if (integral == null) {
                throw new BusinessException("不存在积分信息，无法使用积分");
            }
            // 消费多少积分可兑换1元
            Double userIntegralPerMoney = dictService.getExchangeIntegral();
            BigDecimal userIntegral = BigDecimalUtil.multiply(exchangeMoney, userIntegralPerMoney);
            if (BigDecimalUtil.subtract(userIntegral, integral.getIntegral()).doubleValue() > 0) {
                throw new BusinessException("抵扣积分数大于可使用积分数");
            }
            saveConsume.setUseIntegral(userIntegral.doubleValue());

            Double giveIntegralPerMoney = dictService.getGiveIntegral();
            BigDecimal giveIntegral = BigDecimalUtil.multiply(payMoney, giveIntegralPerMoney);
            saveConsume.setGiveIntegral(giveIntegral.doubleValue());

        }
        if (BigDecimalUtil.subtract(payMoney, voConsume.getPayMoney()).intValue() != 0) {
            throw new BusinessException("实际支付金额错误，请刷新后重试");
        }
        saveConsume.setPayMoney(payMoney.doubleValue());
        String orderNo = "Consume_" + busId + "_" + DateUtil.getSerialNumber();
        saveConsume.setOrderNo("LM_" + orderNo);
        saveConsume.setType(CardConstant.CONSUME_TYPE_OFFLINE);
        saveConsume.setBusinessType(CardConstant.CONSUME_BUSINESS_TYPE_OFFLINE);
        ConsumePayVO result = new ConsumePayVO();
        if (CardConstant.CONSUME_VO_PAY_TYPE_CASH == voConsume.getPayType()) {
            saveConsume.setPayType(CardConstant.CONSUME_PAY_TYPE_CASH);
            saveConsume.setPayStatus(CardConstant.CONSUME_PAY_STATUS_SUCCESS);
            save(saveConsume);
        } else {
            saveConsume.setPayType(CardConstant.CONSUME_PAY_STATUS_PAYING);
            save(saveConsume);

            String socketKey = PropertiesUtil.getSocketKey() + orderNo;
            String notifyUrl = PropertiesUtil.getUnionUrl() + "/unionConsume/79B4DE7C/" + saveConsume.getId() + "/callback?socketKey=" + socketKey;
            String payUrl = wxPayService.qrCodePay(saveConsume.getPayMoney(), null, saveConsume.getOrderNo(),
                    null, CommonConstant.COMMON_NO, null, notifyUrl, CommonConstant.COMMON_NO,
                    null, 0, null);
            result.setPayUrl(payUrl);
            result.setSocketKey(socketKey);
        }

        Integer voActivityId = vo.getActivityId();
        List<UnionCardProjectItem> voItemList = vo.getTextList();
        List<UnionConsumeProject> saveConsumeProjectList = new ArrayList<>();
        if (voActivityId != null || ListUtil.isNotEmpty(voItemList)) {
            Set<Integer> itemIdSet = new HashSet<>();
            for (UnionCardProjectItem voItem : voItemList) {
                UnionCardProjectItem item = unionCardProjectItemService.getById(voItem.getId());
                if (item == null) {
                    throw new BusinessException("找不到服务优惠信息");
                }
                UnionCardProject project = unionCardProjectService.getByActivityIdAndMemberIdAndUnionId(voActivityId, member.getId(), unionId);
                if (project == null || !project.getId().equals(item.getProjectId())) {
                    throw new BusinessException("找不到服务项目信息");
                }
                if (itemIdSet.contains(item.getId())) {
                    throw new BusinessException("存在重复使用的服务优惠");
                }
                UnionConsumeProject saveConsumeProject = new UnionConsumeProject();
                saveConsumeProject.setDelStatus(CommonConstant.DEL_STATUS_NO);
                saveConsumeProject.setCreateTime(currentDate);
                saveConsumeProject.setProjectId(project.getId());
                saveConsumeProject.setProjectItemId(item.getId());
                saveConsumeProject.setConsumeId(saveConsume.getId());
                saveConsumeProjectList.add(saveConsumeProject);
            }
        }
        if (ListUtil.isNotEmpty(saveConsumeProjectList)) {
            unionConsumeProjectService.saveBatch(saveConsumeProjectList);
        }

        if (integral == null) {
            UnionCardIntegral saveIntegral = new UnionCardIntegral();
            saveIntegral.setDelStatus(CommonConstant.DEL_STATUS_NO);
            saveIntegral.setCreateTime(currentDate);
            saveIntegral.setFanId(fanId);
            saveIntegral.setUnionId(unionId);
            saveIntegral.setIntegral(saveConsume.getGiveIntegral());
            unionCardIntegralService.save(saveIntegral);
        } else {
            UnionCardIntegral updateIntegral = new UnionCardIntegral();
            updateIntegral.setId(integral.getId());
            updateIntegral.setIntegral(BigDecimalUtil.add(integral.getIntegral(), saveConsume.getGiveIntegral()).doubleValue());
            unionCardIntegralService.update(updateIntegral);
        }

        return result;
    }

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    @Override
    public String updateCallbackByPermitId(Integer consumeId, String socketKey, String payType, String orderNo, Integer isSuccess) {
        Map<String, Object> result = new HashMap<>(2);
        if (consumeId == null || socketKey == null || payType == null || orderNo == null || isSuccess == null) {
            result.put("code", -1);
            result.put("msg", "参数缺少");
            return JSONObject.toJSONString(result);
        }

        // （1）	判断consumeId有效性
        UnionConsume consume;
        try {
            consume = getById(consumeId);
        } catch (Exception e) {
            logger.error("", e);
            result.put("code", -1);
            result.put("msg", "根据参数permitId获取permit对象失败");
            return JSONObject.toJSONString(result);
        }
        if (consume == null) {
            result.put("code", -1);
            result.put("msg", "参数permitId错误");
            return JSONObject.toJSONString(result);
        }
        // （2）	如果permit不是未支付状态，则socket通知，并返回处理成功
        // （3）	否则，更新permit为支付成功状态，且socket通知，并返回处理成功
        Integer payStatus = consume.getPayStatus();
        if (payStatus == CardConstant.CONSUME_PAY_STATUS_SUCCESS || payStatus == CardConstant.CONSUME_PAY_STATUS_FAIL) {
            // TODO socket通知
            result.put("code", 0);
            result.put("msg", "已处理过");
            return JSONObject.toJSONString(result);
        } else {
            UnionConsume updateConsume = new UnionConsume();
            updateConsume.setId(consumeId);
            updateConsume.setPayStatus(isSuccess == CommonConstant.COMMON_YES ? CardConstant.CONSUME_PAY_STATUS_SUCCESS : CardConstant.CONSUME_PAY_STATUS_FAIL);
            if (payType.equals("0")) {
                updateConsume.setWxOrderNo(orderNo);
            } else {
                updateConsume.setAlipayOrderNo(orderNo);
            }

            try {
                update(updateConsume);
            } catch (Exception e) {
                logger.error("", e);
                result.put("code", -1);
                result.put("msg", "更新permit对象失败");
                return JSONObject.toJSONString(result);
            }

            // TODO socket通知
            result.put("code", 0);
            result.put("msg", "成功");
            return JSONObject.toJSONString(result);
        }
    }

    //***************************************** Domain Driven Design - count *******************************************

    //***************************************** Domain Driven Design - boolean *****************************************

    //***************************************** Object As a Service - get **********************************************

    public UnionConsume getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionConsume result;
        // (1)cache
        String idKey = UnionConsumeCacheUtil.getIdKey(id);
        if (redisCacheUtil.exists(idKey)) {
            String tempStr = redisCacheUtil.get(idKey);
            result = JSONArray.parseObject(tempStr, UnionConsume.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("id", id)
                .eq("del_status", CommonConstant.DEL_STATUS_NO);
        result = selectOne(entityWrapper);
        setCache(result, id);
        return result;
    }

    //***************************************** Object As a Service - list *********************************************

    public List<UnionConsume> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsume> result;
        // (1)cache
        String memberIdKey = UnionConsumeCacheUtil.getMemberIdKey(memberId);
        if (redisCacheUtil.exists(memberIdKey)) {
            String tempStr = redisCacheUtil.get(memberIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsume.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, memberId, UnionConsumeCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    public List<UnionConsume> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsume> result;
        // (1)cache
        String unionIdKey = UnionConsumeCacheUtil.getUnionIdKey(unionId);
        if (redisCacheUtil.exists(unionIdKey)) {
            String tempStr = redisCacheUtil.get(unionIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsume.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, unionId, UnionConsumeCacheUtil.TYPE_UNION_ID);
        return result;
    }

    public List<UnionConsume> listByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsume> result;
        // (1)cache
        String cardIdKey = UnionConsumeCacheUtil.getCardIdKey(cardId);
        if (redisCacheUtil.exists(cardIdKey)) {
            String tempStr = redisCacheUtil.get(cardIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsume.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("card_id", cardId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, cardId, UnionConsumeCacheUtil.TYPE_CARD_ID);
        return result;
    }

    public List<UnionConsume> listByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsume> result;
        // (1)cache
        String fanIdKey = UnionConsumeCacheUtil.getFanIdKey(fanId);
        if (redisCacheUtil.exists(fanIdKey)) {
            String tempStr = redisCacheUtil.get(fanIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsume.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("fan_id", fanId)
                .eq("del_status", CommonConstant.COMMON_NO);
        result = selectList(entityWrapper);
        setCache(result, fanId, UnionConsumeCacheUtil.TYPE_FAN_ID);
        return result;
    }

    //***************************************** Object As a Service - save *********************************************

    @Transactional(rollbackFor = Exception.class)
    public void save(UnionConsume newUnionConsume) throws Exception {
        if (newUnionConsume == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insert(newUnionConsume);
        removeCache(newUnionConsume);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionConsume> newUnionConsumeList) throws Exception {
        if (newUnionConsumeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        insertBatch(newUnionConsumeList);
        removeCache(newUnionConsumeList);
    }

    //***************************************** Object As a Service - remove *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        UnionConsume unionConsume = getById(id);
        removeCache(unionConsume);
        // (2)remove in db logically
        UnionConsume removeUnionConsume = new UnionConsume();
        removeUnionConsume.setId(id);
        removeUnionConsume.setDelStatus(CommonConstant.DEL_STATUS_YES);
        updateById(removeUnionConsume);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionConsume> unionConsumeList = new ArrayList<>();
        for (Integer id : idList) {
            UnionConsume unionConsume = getById(id);
            unionConsumeList.add(unionConsume);
        }
        removeCache(unionConsumeList);
        // (2)remove in db logically
        List<UnionConsume> removeUnionConsumeList = new ArrayList<>();
        for (Integer id : idList) {
            UnionConsume removeUnionConsume = new UnionConsume();
            removeUnionConsume.setId(id);
            removeUnionConsume.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionConsumeList.add(removeUnionConsume);
        }
        updateBatchById(removeUnionConsumeList);
    }

    //***************************************** Object As a Service - update *******************************************

    @Transactional(rollbackFor = Exception.class)
    public void update(UnionConsume updateUnionConsume) throws Exception {
        if (updateUnionConsume == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        Integer id = updateUnionConsume.getId();
        UnionConsume unionConsume = getById(id);
        removeCache(unionConsume);
        // (2)update db
        updateById(updateUnionConsume);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionConsume> updateUnionConsumeList) throws Exception {
        if (updateUnionConsumeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = new ArrayList<>();
        for (UnionConsume updateUnionConsume : updateUnionConsumeList) {
            idList.add(updateUnionConsume.getId());
        }
        List<UnionConsume> unionConsumeList = new ArrayList<>();
        for (Integer id : idList) {
            UnionConsume unionConsume = getById(id);
            unionConsumeList.add(unionConsume);
        }
        removeCache(unionConsumeList);
        // (2)update db
        updateBatchById(updateUnionConsumeList);
    }

    //***************************************** Object As a Service - cache support ************************************

    private void setCache(UnionConsume newUnionConsume, Integer id) {
        if (id == null) {
            //do nothing,just in case
            return;
        }
        String idKey = UnionConsumeCacheUtil.getIdKey(id);
        redisCacheUtil.set(idKey, newUnionConsume);
    }

    private void setCache(List<UnionConsume> newUnionConsumeList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String foreignIdKey = null;
        switch (foreignIdType) {
            case UnionConsumeCacheUtil.TYPE_MEMBER_ID:
                foreignIdKey = UnionConsumeCacheUtil.getMemberIdKey(foreignId);
                break;
            case UnionConsumeCacheUtil.TYPE_UNION_ID:
                foreignIdKey = UnionConsumeCacheUtil.getUnionIdKey(foreignId);
                break;
            case UnionConsumeCacheUtil.TYPE_CARD_ID:
                foreignIdKey = UnionConsumeCacheUtil.getCardIdKey(foreignId);
                break;
            case UnionConsumeCacheUtil.TYPE_FAN_ID:
                foreignIdKey = UnionConsumeCacheUtil.getFanIdKey(foreignId);
                break;
            default:
                break;
        }
        if (foreignIdKey != null) {
            redisCacheUtil.set(foreignIdKey, newUnionConsumeList);
        }
    }

    private void removeCache(UnionConsume unionConsume) {
        if (unionConsume == null) {
            return;
        }
        Integer id = unionConsume.getId();
        String idKey = UnionConsumeCacheUtil.getIdKey(id);
        redisCacheUtil.remove(idKey);

        Integer memberId = unionConsume.getMemberId();
        if (memberId != null) {
            String memberIdKey = UnionConsumeCacheUtil.getMemberIdKey(memberId);
            redisCacheUtil.remove(memberIdKey);
        }

        Integer unionId = unionConsume.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionConsumeCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);
        }

        Integer cardId = unionConsume.getCardId();
        if (cardId != null) {
            String cardIdKey = UnionConsumeCacheUtil.getCardIdKey(cardId);
            redisCacheUtil.remove(cardIdKey);
        }

        Integer fanId = unionConsume.getFanId();
        if (fanId != null) {
            String fanIdKey = UnionConsumeCacheUtil.getFanIdKey(fanId);
            redisCacheUtil.remove(fanIdKey);
        }
    }

    private void removeCache(List<UnionConsume> unionConsumeList) {
        if (ListUtil.isEmpty(unionConsumeList)) {
            return;
        }
        List<Integer> idList = new ArrayList<>();
        for (UnionConsume unionConsume : unionConsumeList) {
            idList.add(unionConsume.getId());
        }
        List<String> idKeyList = UnionConsumeCacheUtil.getIdKey(idList);
        redisCacheUtil.remove(idKeyList);

        List<String> memberIdKeyList = getForeignIdKeyList(unionConsumeList, UnionConsumeCacheUtil.TYPE_MEMBER_ID);
        if (ListUtil.isNotEmpty(memberIdKeyList)) {
            redisCacheUtil.remove(memberIdKeyList);
        }

        List<String> unionIdKeyList = getForeignIdKeyList(unionConsumeList, UnionConsumeCacheUtil.TYPE_UNION_ID);
        if (ListUtil.isNotEmpty(unionIdKeyList)) {
            redisCacheUtil.remove(unionIdKeyList);
        }

        List<String> cardIdKeyList = getForeignIdKeyList(unionConsumeList, UnionConsumeCacheUtil.TYPE_CARD_ID);
        if (ListUtil.isNotEmpty(cardIdKeyList)) {
            redisCacheUtil.remove(cardIdKeyList);
        }

        List<String> fanIdKeyList = getForeignIdKeyList(unionConsumeList, UnionConsumeCacheUtil.TYPE_FAN_ID);
        if (ListUtil.isNotEmpty(fanIdKeyList)) {
            redisCacheUtil.remove(fanIdKeyList);
        }
    }

    private List<String> getForeignIdKeyList(List<UnionConsume> unionConsumeList, int foreignIdType) {
        List<String> result = new ArrayList<>();
        switch (foreignIdType) {
            case UnionConsumeCacheUtil.TYPE_MEMBER_ID:
                for (UnionConsume unionConsume : unionConsumeList) {
                    Integer memberId = unionConsume.getMemberId();
                    if (memberId != null) {
                        String memberIdKey = UnionConsumeCacheUtil.getMemberIdKey(memberId);
                        result.add(memberIdKey);
                    }
                }
                break;
            case UnionConsumeCacheUtil.TYPE_UNION_ID:
                for (UnionConsume unionConsume : unionConsumeList) {
                    Integer unionId = unionConsume.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionConsumeCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);
                    }
                }
                break;
            case UnionConsumeCacheUtil.TYPE_CARD_ID:
                for (UnionConsume unionConsume : unionConsumeList) {
                    Integer cardId = unionConsume.getCardId();
                    if (cardId != null) {
                        String cardIdKey = UnionConsumeCacheUtil.getCardIdKey(cardId);
                        result.add(cardIdKey);
                    }
                }
                break;
            case UnionConsumeCacheUtil.TYPE_FAN_ID:
                for (UnionConsume unionConsume : unionConsumeList) {
                    Integer fanId = unionConsume.getFanId();
                    if (fanId != null) {
                        String fanIdKey = UnionConsumeCacheUtil.getFanIdKey(fanId);
                        result.add(fanIdKey);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }

}