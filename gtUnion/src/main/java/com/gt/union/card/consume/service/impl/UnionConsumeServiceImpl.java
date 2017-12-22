package com.gt.union.card.consume.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.api.bean.session.WxPublicUsers;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.pay.entity.PayParam;
import com.gt.union.api.client.shop.ShopService;
import com.gt.union.api.client.socket.SocketService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.card.consume.constant.ConsumeConstant;
import com.gt.union.card.consume.entity.UnionConsume;
import com.gt.union.card.consume.entity.UnionConsumeProject;
import com.gt.union.card.consume.mapper.UnionConsumeMapper;
import com.gt.union.card.consume.service.IUnionConsumeProjectService;
import com.gt.union.card.consume.service.IUnionConsumeService;
import com.gt.union.card.consume.util.UnionConsumeCacheUtil;
import com.gt.union.card.consume.vo.ConsumePostVO;
import com.gt.union.card.consume.vo.ConsumeRecordVO;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.entity.UnionCardIntegral;
import com.gt.union.card.main.service.IUnionCardFanService;
import com.gt.union.card.main.service.IUnionCardIntegralService;
import com.gt.union.card.project.constant.ProjectConstant;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.card.project.service.IUnionCardProjectItemService;
import com.gt.union.card.project.service.IUnionCardProjectService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.h5.card.vo.MyCardConsumeVO;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.main.vo.UnionPayVO;
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

    @Autowired
    private SocketService socketService;

    @Autowired
    private IBusUserService busUserService;

    //***************************************** Domain Driven Design - get *********************************************

    //***************************************** Domain Driven Design - list ********************************************

    @Override
    public UnionConsume getByOrderNo(String orderNo) throws Exception {
        if (orderNo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .eq("order_no", orderNo);

        return selectOne(entityWrapper);
    }

	@Override
	public Integer countPayByFanId(Integer fanId) throws ParamException {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .eq("fan_id", fanId);

        return selectCount(entityWrapper);
	}

	@Override
    public List<ConsumeRecordVO> listConsumeRecordVOByBusId(Integer busId, Integer optUnionId, Integer optShopId,
                                                            String optCardNumber, String optPhone, Date optBeginTime, Date optEndTime) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<ConsumeRecordVO> result = new ArrayList<>();
        // （1）	获取我的所有有效的union
        List<UnionMain> validUnionList = unionMainService.listMyValidReadByBusId(busId);
        List<Integer> validUnionIdList = new ArrayList<>();
        if (ListUtil.isNotEmpty(validUnionList)) {
            for (UnionMain validUnion : validUnionList) {
                Integer validUnionId = validUnion.getId();
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
                ConsumeRecordVO vo = new ConsumeRecordVO();
                vo.setConsume(consume);

                UnionMain union = unionMainService.getById(consume.getUnionId());
                vo.setUnion(union);

                UnionCardFan fan = unionCardFanService.getById(consume.getFanId());
                vo.setFan(fan);

                List<UnionConsumeProject> consumeProjectList = unionConsumeProjectService.listByConsumeId(consume.getId());
                if (ListUtil.isNotEmpty(consumeProjectList)) {
                    List<UnionCardProjectItem> nonErpTextList = new ArrayList<>();
                    List<UnionCardProjectItem> erpTextList = new ArrayList<>();
                    List<UnionCardProjectItem> eErpGoodsList = new ArrayList<>();
                    for (UnionConsumeProject consumeProject : consumeProjectList) {
                        UnionCardProjectItem item = unionCardProjectItemService.getByIdAndProjectId(consumeProject.getProjectItemId(), consumeProject.getProjectId());
                        if (item != null) {
                            switch (item.getType()) {
                                case ProjectConstant.TYPE_TEXT:
                                    nonErpTextList.add(item);
                                    break;
                                case ProjectConstant.TYPE_ERP_TEXT:
                                    erpTextList.add(item);
                                    break;
                                case ProjectConstant.TYPE_ERP_GOODS:
                                    eErpGoodsList.add(item);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    vo.setNonErpTextList(nonErpTextList);
                    vo.setErpTextList(erpTextList);
                    vo.setErpGoodsList(eErpGoodsList);
                }

                result.add(vo);
            }
        }
        // （3）	按支付时间倒序排序
        Collections.sort(result, new Comparator<ConsumeRecordVO>() {
            @Override
            public int compare(ConsumeRecordVO o1, ConsumeRecordVO o2) {
                return o2.getConsume().getCreateTime().compareTo(o1.getConsume().getCreateTime());
            }
        });

        return result;
    }

    @Override
    public List<MyCardConsumeVO> listConsumeByFanId(Integer fanId, Page page) throws Exception{
        List<MyCardConsumeVO> result = new ArrayList<>();
        List<UnionConsume> list = listPayByFanId(fanId, page);
        if(ListUtil.isNotEmpty(list)){
            for(UnionConsume consume : list){
                MyCardConsumeVO vo = new MyCardConsumeVO();
                vo.setShopName(consume.getShopName());
                vo.setConsumeMoney(consume.getConsumeMoney());
                vo.setDiscount(consume.getDiscount() * 10);
                vo.setConsumeIntegral(consume.getUseIntegral());
                vo.setGiveIntegral(consume.getGiveIntegral());
                vo.setDiscountMoney(consume.getDiscountMoney());
                vo.setIntegralMoney(consume.getIntegralMoney());
                vo.setRecordId(consume.getId());
                vo.setPayMoney(consume.getPayMoney());
                vo.setPayType(consume.getPayType());
                vo.setConsumeTimeStr(DateTimeKit.format(consume.getCreateTime(), DateTimeKit.DEFAULT_DATETIME_FORMAT_YYYYMMDD_HHMM));
                List<UnionConsumeProject> consumeProjectList = unionConsumeProjectService.listByConsumeId(consume.getId());
                if (ListUtil.isNotEmpty(consumeProjectList)) {
                    List items = new ArrayList<>();
                    for (UnionConsumeProject consumeProject : consumeProjectList) {
                        UnionCardProjectItem item = unionCardProjectItemService.getByIdAndProjectId(consumeProject.getProjectItemId(), consumeProject.getProjectId());
                        items.add(item);
                    }
                    vo.setItems(items);
                }
                result.add(vo);
            }
        }
        return result;
    }

    @Override
    public List<UnionConsume> listPayByFanId(Integer fanId, Page page) {
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        entityWrapper.eq("fan_id", fanId);
        entityWrapper.eq("pay_status", ConsumeConstant.PAY_STATUS_SUCCESS);
        entityWrapper.orderBy("create_time",false);
        Page result = this.selectPage(page,entityWrapper);
        return result.getRecords();
    }

    //***************************************** Domain Driven Design - save ********************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UnionPayVO saveConsumePostVOByBusIdAndUnionIdAndFanId(Integer busId, Integer unionId, Integer fanId, ConsumePostVO vo) throws Exception {
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
            throw new BusinessException("门店不能为空");
        }
        WsWxShopInfoExtend shop = shopService.getById(shopId);
        if (shop == null) {
            throw new BusinessException("找不到门店信息");
        }
        saveConsume.setShopId(shopId);
        saveConsume.setShopName(shop.getBusinessName());

        UnionConsume voConsume = vo.getConsume();
        if (voConsume == null) {
            throw new BusinessException("支付信息不能为空");
        }
        Double consumeMoney = voConsume.getConsumeMoney();
        if (consumeMoney == null || consumeMoney <= 0) {
            throw new BusinessException("消费金额不能为空，且必须大于0");
        }
        
        Double discount = member.getDiscount() != null ? member.getDiscount() : 0.0;
        BigDecimal discountMoney = BigDecimalUtil.multiply(consumeMoney, discount);
        saveConsume.setDiscount(discount);
        saveConsume.setDiscountMoney(discountMoney.doubleValue());

        BigDecimal payMoney = BigDecimalUtil.subtract(consumeMoney, discountMoney);
        Integer isUserIntegral = vo.getIsUserIntegral();
        UnionCardIntegral integral = unionCardIntegralService.getByUnionIdAndFanId(unionId, fanId);
        if (CommonConstant.COMMON_YES == isUserIntegral) {
            Double integralExchangeRatio = member.getIntegralExchangeRatio();
            if (integralExchangeRatio == null) {
                throw new BusinessException("未设置积分兑换率");
            }
            BigDecimal integralMoney = BigDecimalUtil.multiply(payMoney, integralExchangeRatio);
            payMoney = BigDecimalUtil.subtract(payMoney, integralMoney);

            if (integral == null) {
                throw new BusinessException("不存在积分信息，无法使用积分");
            }
            // 消费多少积分可兑换1元
            Double useIntegralPerMoney = dictService.getExchangeIntegral();
            BigDecimal useIntegral = BigDecimalUtil.multiply(integralMoney, useIntegralPerMoney);
            if (useIntegral.doubleValue() > integral.getIntegral()) {
                throw new BusinessException("抵扣积分数大于可使用积分数");
            }
            saveConsume.setUseIntegral(useIntegral.doubleValue());
            saveConsume.setIntegralMoney(integralMoney.doubleValue());

            Double giveIntegralPerMoney = dictService.getGiveIntegral();
            BigDecimal giveIntegral = BigDecimalUtil.multiply(payMoney, giveIntegralPerMoney);
            saveConsume.setGiveIntegral(giveIntegral.doubleValue());
        }
        if (BigDecimalUtil.subtract(payMoney, voConsume.getPayMoney()).doubleValue() != 0) {
            throw new BusinessException("实际支付金额错误，请刷新后重试");
        }
        saveConsume.setPayMoney(payMoney.doubleValue());

        Integer voActivityId = vo.getActivityId();
        List<UnionCardProjectItem> voItemList = vo.getTextList();
        List<UnionConsumeProject> saveConsumeProjectList = new ArrayList<>();
        if (voActivityId != null && ListUtil.isNotEmpty(voItemList)) {
            Set<Integer> itemIdSet = new HashSet<>();
            for (UnionCardProjectItem voItem : voItemList) {
                UnionCardProjectItem item = unionCardProjectItemService.getById(voItem.getId());
                if (item == null) {
                    throw new BusinessException("找不到服务优惠信息");
                }
                UnionCardProject project = unionCardProjectService.getByUnionIdAndMemberIdAndActivityId(unionId, member.getId(), voActivityId);
                if (project == null || !project.getId().equals(item.getProjectId())) {
                    throw new BusinessException("找不到服务项目信息");
                }
                if (itemIdSet.contains(item.getId())) {
                    throw new BusinessException("存在重复使用的服务优惠");
                }
                itemIdSet.add(item.getId());
                
                UnionConsumeProject saveConsumeProject = new UnionConsumeProject();
                saveConsumeProject.setDelStatus(CommonConstant.DEL_STATUS_NO);
                saveConsumeProject.setCreateTime(currentDate);
                saveConsumeProject.setProjectId(project.getId());
                saveConsumeProject.setProjectItemId(item.getId());
                saveConsumeProjectList.add(saveConsumeProject);
            }
        }

        String orderNo = "LM" + ConfigConstant.PAY_MODEL_CONSUME + DateUtil.getSerialNumber();
        saveConsume.setSysOrderNo(orderNo);
        saveConsume.setType(ConsumeConstant.TYPE_OFFLINE);
        saveConsume.setBusinessType(ConsumeConstant.BUSINESS_TYPE_OFFLINE);
        UnionPayVO result = null;
        if (ConsumeConstant.VO_PAY_TYPE_CASH == voConsume.getPayType()) {
            saveConsume.setPayType(ConsumeConstant.PAY_TYPE_CASH);
            saveConsume.setPayStatus(ConsumeConstant.PAY_STATUS_SUCCESS);
        } else {
            result = new UnionPayVO();
            saveConsume.setPayType(ConsumeConstant.PAY_STATUS_PAYING);
            String socketKey = PropertiesUtil.getSocketKey() + orderNo;
            String notifyUrl = PropertiesUtil.getUnionUrl() + "/callBack/79B4DE7C/consume/callback?socketKey=" + socketKey;

            PayParam payParam = new PayParam();
            payParam.setTotalFee(saveConsume.getPayMoney());
            payParam.setOrderNum(saveConsume.getSysOrderNo());
            payParam.setIsreturn(CommonConstant.COMMON_NO);
            payParam.setNotifyUrl(notifyUrl);
            payParam.setIsSendMessage(CommonConstant.COMMON_NO);
            payParam.setPayWay(0);
            payParam.setDesc("消费核销");
            payParam.setPayDuoFen(false);
            payParam.setBusId(busId);
            WxPublicUsers publicUsers = busUserService.getWxPublicUserByBusId(busId);
            payParam.setAppid(CommonUtil.isNotEmpty(publicUsers) ? publicUsers.getAppid() : null);

            String payUrl = wxPayService.qrCodePay(payParam);

            result.setPayUrl(payUrl);
            result.setSocketKey(socketKey);
        }

        save(saveConsume);
        if (ListUtil.isNotEmpty(saveConsumeProjectList)) {
            for (UnionConsumeProject saveConsumeProject : saveConsumeProjectList) {
                saveConsumeProject.setConsumeId(saveConsume.getId());
            }
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
            BigDecimal tempIntegral = BigDecimalUtil.subtract(integral.getIntegral(), saveConsume.getUseIntegral());
            tempIntegral = BigDecimalUtil.add(tempIntegral, saveConsume.getGiveIntegral());
            updateIntegral.setIntegral(tempIntegral.doubleValue());
            unionCardIntegralService.update(updateIntegral);
        }

        return result;
    }

    //***************************************** Domain Driven Design - remove ******************************************

    //***************************************** Domain Driven Design - update ******************************************

    @Override
    public String updateCallbackByOrderNo(String orderNo, String socketKey, String payType, String payOrderNo, Integer isSuccess) {
        Map<String, Object> result = new HashMap<>(2);
        if (orderNo == null || socketKey == null || payType == null || payOrderNo == null || isSuccess == null) {
            result.put("code", -1);
            result.put("msg", "参数缺少");
            return JSONObject.toJSONString(result);
        }

        // （1）	判断consumeId有效性
        UnionConsume consume;
        try {
            consume = getByOrderNo(orderNo);
        } catch (Exception e) {
            logger.error("", e);
            result.put("code", -1);
            result.put("msg", e.getMessage());
            return JSONObject.toJSONString(result);
        }
        if (consume == null) {
            result.put("code", -1);
            result.put("msg", "找不到消费记录");
            return JSONObject.toJSONString(result);
        }
        // （2）	如果consume不是未支付状态，则socket通知，并返回处理成功
        // （3）	否则，更新consume为支付成功状态，且socket通知，并返回处理成功
        Integer payStatus = consume.getPayStatus();
        if (payStatus == ConsumeConstant.PAY_STATUS_SUCCESS || payStatus == ConsumeConstant.PAY_STATUS_FAIL) {
            // socket通知
            socketService.socketPaySendMessage(socketKey, isSuccess, null);
            result.put("code", 0);
            result.put("msg", "已处理过");
            return JSONObject.toJSONString(result);
        } else {
            UnionConsume updateConsume = new UnionConsume();
            updateConsume.setId(consume.getId());
            updateConsume.setPayStatus(isSuccess == CommonConstant.COMMON_YES ? ConsumeConstant.PAY_STATUS_SUCCESS : ConsumeConstant.PAY_STATUS_FAIL);
            if (payType.equals("0")) {
                updateConsume.setPayType(ConsumeConstant.PAY_TYPE_WX);
                updateConsume.setWxOrderNo(payOrderNo);
            } else {
                updateConsume.setPayType(ConsumeConstant.PAY_TYPE_ALIPAY);
                updateConsume.setAlipayOrderNo(payOrderNo);
            }

            try {
                update(updateConsume);
            } catch (Exception e) {
                logger.error("", e);
                result.put("code", -1);
                result.put("msg", e.getMessage());
                return JSONObject.toJSONString(result);
            }

            // socket通知
            socketService.socketPaySendMessage(socketKey, isSuccess, null);
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
    @Override
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