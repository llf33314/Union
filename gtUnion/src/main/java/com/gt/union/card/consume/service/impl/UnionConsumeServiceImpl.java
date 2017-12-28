package com.gt.union.card.consume.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.WxPublicUsers;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.pay.entity.PayParam;
import com.gt.union.api.client.shop.ShopService;
import com.gt.union.api.client.socket.SocketService;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.card.consume.constant.ConsumeConstant;
import com.gt.union.card.consume.dao.IUnionConsumeDao;
import com.gt.union.card.consume.entity.UnionConsume;
import com.gt.union.card.consume.entity.UnionConsumeProject;
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
public class UnionConsumeServiceImpl implements IUnionConsumeService {
    private Logger logger = Logger.getLogger(UnionConsumeServiceImpl.class);

    @Autowired
    private IUnionConsumeDao unionConsumeDao;

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

    //********************************************* Base On Business - get *********************************************

    @Override
    public UnionConsume getValidByOrderNo(String orderNo) throws Exception {
        if (orderNo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("order_no", orderNo);

        return unionConsumeDao.selectOne(entityWrapper);
    }

    @Override
    public UnionConsume getValidByOrderNoAndModel(String orderNo, Integer model) throws Exception {
        if (orderNo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("order_no", orderNo)
                .eq("business_type", model);

        return unionConsumeDao.selectOne(entityWrapper);
    }

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<ConsumeRecordVO> listConsumeRecordVO(List<UnionConsume> consumeList) throws Exception {
        List<ConsumeRecordVO> result = new ArrayList<>();

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
                        UnionCardProjectItem item = unionCardProjectItemService.getById(consumeProject.getProjectItemId());
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

        return result;
    }

    @Override
    public List<ConsumeRecordVO> listConsumeRecordVOByBusId(
            Integer busId, Integer optUnionId, Integer optShopId, String optCardNumber, String optPhone, Date optBeginTime, Date optEndTime) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("member_id", memberIdList)
                .eq(optUnionId != null, "union_id = {0}", optUnionId)
                .eq(optShopId != null, "shop_id = {0}", optShopId)
                .ge(optBeginTime != null, "create_time >= {0}", optBeginTime)
                .le(optEndTime != null, "create_time <= {0}", optEndTime)
                .exists(StringUtil.isNotEmpty(optCardNumber) || StringUtil.isNotEmpty(optPhone),
                        " SELECT f.id FROM t_union_card_fan f "
                                + " WHERE f.del_status = " + CommonConstant.DEL_STATUS_NO
                                + " AND f.id = t_union_consume.fan_id "
                                + (StringUtil.isNotEmpty(optCardNumber) ? (" AND f.number LIKE '%" + optCardNumber + "%' ") : "")
                                + (StringUtil.isNotEmpty(optPhone) ? (" AND f.phone LIKE '%" + optPhone + "%' ") : ""))
                .orderBy("create_time", false);

        List<UnionConsume> consumeList = unionConsumeDao.selectList(entityWrapper);

        return listConsumeRecordVO(consumeList);
    }

    @Override
    public Page pageConsumeRecordVOByBusId(
            Page page, Integer busId, Integer optUnionId, Integer optShopId, String optCardNumber, String optPhone, Date optBeginTime, Date optEndTime) throws Exception {
        if (page == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> memberList = unionMemberService.listByBusId(busId);
        List<Integer> memberIdList = unionMemberService.getIdList(memberList);

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("member_id", memberIdList)
                .eq(optUnionId != null, "union_id = {0}", optUnionId)
                .eq(optShopId != null, "shop_id = {0}", optShopId)
                .ge(optBeginTime != null, "create_time >= {0}", optBeginTime)
                .le(optEndTime != null, "create_time <= {0}", optEndTime)
                .exists(StringUtil.isNotEmpty(optCardNumber) || StringUtil.isNotEmpty(optPhone),
                        " SELECT f.id FROM t_union_card_fan f "
                                + " WHERE f.del_status = " + CommonConstant.DEL_STATUS_NO
                                + " AND f.id = t_union_consume.fan_id "
                                + (StringUtil.isNotEmpty(optCardNumber) ? (" AND f.number LIKE '%" + optCardNumber + "%' ") : "")
                                + (StringUtil.isNotEmpty(optPhone) ? (" AND f.phone LIKE '%" + optPhone + "%' ") : ""))
                .orderBy("create_time", false);

        Page result = unionConsumeDao.selectPage(page, entityWrapper);
        List<UnionConsume> resultDataList = result.getRecords();
        result.setRecords(listConsumeRecordVO(resultDataList));

        return result;
    }

    @Override
    public Page pageConsumeByFanId(Page page, Integer fanId) throws Exception {
        if (page == null || fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        Page result = pagePayByFanId(page, fanId);
        List<UnionConsume> dataList = result.getRecords();
        List<MyCardConsumeVO> resultDataList = new ArrayList<>();
        if (ListUtil.isNotEmpty(resultDataList)) {
            for (UnionConsume consume : dataList) {
                MyCardConsumeVO vo = new MyCardConsumeVO();
                vo.setShopName(consume.getShopName());
                vo.setConsumeMoney(consume.getConsumeMoney());
                vo.setDiscount(consume.getDiscount());
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
                        UnionCardProjectItem item = unionCardProjectItemService.getById(consumeProject.getProjectItemId());
                        items.add(item);
                    }
                    vo.setItems(items);
                }
                resultDataList.add(vo);
            }
        }
        result.setRecords(resultDataList);

        return result;
    }

    @Override
    public Page pagePayByFanId(Page page, Integer fanId) throws Exception {
        if (page == null || fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("fan_id", fanId)
                .eq("pay_status", ConsumeConstant.PAY_STATUS_SUCCESS)
                .orderBy("create_time", false);

        return unionConsumeDao.selectPage(page, entityWrapper);
    }

    //********************************************* Base On Business - save ********************************************

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
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
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
        UnionCardIntegral integral = unionCardIntegralService.getValidByUnionIdAndFanId(unionId, fanId);
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
                UnionCardProjectItem item = unionCardProjectItemService.getValidById(voItem.getId());
                if (item == null) {
                    throw new BusinessException("找不到服务优惠信息");
                }
                UnionCardProject project = unionCardProjectService.getValidByUnionIdAndMemberIdAndActivityId(unionId, member.getId(), voActivityId);
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

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

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
            consume = getValidByOrderNo(orderNo);
        } catch (Exception e) {
            logger.error("消费核销支付成功回调错误", e);
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
                logger.error("消费核销支付成功回调错误", e);
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

    //********************************************* Base On Business - other *******************************************

    @Override
    public Integer countValidPayByFanId(Integer fanId) throws ParamException {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .eq("fan_id", fanId);

        return unionConsumeDao.selectCount(entityWrapper);
    }

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionConsume> filterByDelStatus(List<UnionConsume> unionConsumeList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionConsume> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionConsumeList)) {
            for (UnionConsume unionConsume : unionConsumeList) {
                if (delStatus.equals(unionConsume.getDelStatus())) {
                    result.add(unionConsume);
                }
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
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
        result = unionConsumeDao.selectById(id);
        setCache(result, id);
        return result;
    }

    @Override
    public UnionConsume getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionConsume result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_NO == result.getDelStatus() ? result : null;
    }

    @Override
    public UnionConsume getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        UnionConsume result = getById(id);

        return result != null && CommonConstant.DEL_STATUS_YES == result.getDelStatus() ? result : null;
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionConsume> unionConsumeList) throws Exception {
        if (unionConsumeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionConsumeList)) {
            for (UnionConsume unionConsume : unionConsumeList) {
                result.add(unionConsume.getId());
            }
        }

        return result;
    }

    @Override
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
        entityWrapper.eq("member_id", memberId);
        result = unionConsumeDao.selectList(entityWrapper);
        setCache(result, memberId, UnionConsumeCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionConsume> listValidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsume> result;
        // (1)cache
        String validMemberIdKey = UnionConsumeCacheUtil.getValidMemberIdKey(memberId);
        if (redisCacheUtil.exists(validMemberIdKey)) {
            String tempStr = redisCacheUtil.get(validMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsume.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("member_id", memberId);
        result = unionConsumeDao.selectList(entityWrapper);
        setValidCache(result, memberId, UnionConsumeCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    @Override
    public List<UnionConsume> listInvalidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsume> result;
        // (1)cache
        String invalidMemberIdKey = UnionConsumeCacheUtil.getInvalidMemberIdKey(memberId);
        if (redisCacheUtil.exists(invalidMemberIdKey)) {
            String tempStr = redisCacheUtil.get(invalidMemberIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsume.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("member_id", memberId);
        result = unionConsumeDao.selectList(entityWrapper);
        setInvalidCache(result, memberId, UnionConsumeCacheUtil.TYPE_MEMBER_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("union_id", unionId);
        result = unionConsumeDao.selectList(entityWrapper);
        setCache(result, unionId, UnionConsumeCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionConsume> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsume> result;
        // (1)cache
        String validUnionIdKey = UnionConsumeCacheUtil.getValidUnionIdKey(unionId);
        if (redisCacheUtil.exists(validUnionIdKey)) {
            String tempStr = redisCacheUtil.get(validUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsume.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);
        result = unionConsumeDao.selectList(entityWrapper);
        setValidCache(result, unionId, UnionConsumeCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
    public List<UnionConsume> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsume> result;
        // (1)cache
        String invalidUnionIdKey = UnionConsumeCacheUtil.getInvalidUnionIdKey(unionId);
        if (redisCacheUtil.exists(invalidUnionIdKey)) {
            String tempStr = redisCacheUtil.get(invalidUnionIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsume.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);
        result = unionConsumeDao.selectList(entityWrapper);
        setInvalidCache(result, unionId, UnionConsumeCacheUtil.TYPE_UNION_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("card_id", cardId);
        result = unionConsumeDao.selectList(entityWrapper);
        setCache(result, cardId, UnionConsumeCacheUtil.TYPE_CARD_ID);
        return result;
    }

    @Override
    public List<UnionConsume> listValidByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsume> result;
        // (1)cache
        String validCardIdKey = UnionConsumeCacheUtil.getValidCardIdKey(cardId);
        if (redisCacheUtil.exists(validCardIdKey)) {
            String tempStr = redisCacheUtil.get(validCardIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsume.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("card_id", cardId);
        result = unionConsumeDao.selectList(entityWrapper);
        setValidCache(result, cardId, UnionConsumeCacheUtil.TYPE_CARD_ID);
        return result;
    }

    @Override
    public List<UnionConsume> listInvalidByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsume> result;
        // (1)cache
        String invalidCardIdKey = UnionConsumeCacheUtil.getInvalidCardIdKey(cardId);
        if (redisCacheUtil.exists(invalidCardIdKey)) {
            String tempStr = redisCacheUtil.get(invalidCardIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsume.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("card_id", cardId);
        result = unionConsumeDao.selectList(entityWrapper);
        setInvalidCache(result, cardId, UnionConsumeCacheUtil.TYPE_CARD_ID);
        return result;
    }

    @Override
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
        entityWrapper.eq("fan_id", fanId);
        result = unionConsumeDao.selectList(entityWrapper);
        setCache(result, fanId, UnionConsumeCacheUtil.TYPE_FAN_ID);
        return result;
    }

    @Override
    public List<UnionConsume> listValidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsume> result;
        // (1)cache
        String validFanIdKey = UnionConsumeCacheUtil.getValidFanIdKey(fanId);
        if (redisCacheUtil.exists(validFanIdKey)) {
            String tempStr = redisCacheUtil.get(validFanIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsume.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("fan_id", fanId);
        result = unionConsumeDao.selectList(entityWrapper);
        setValidCache(result, fanId, UnionConsumeCacheUtil.TYPE_FAN_ID);
        return result;
    }

    @Override
    public List<UnionConsume> listInvalidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionConsume> result;
        // (1)cache
        String invalidFanIdKey = UnionConsumeCacheUtil.getInvalidFanIdKey(fanId);
        if (redisCacheUtil.exists(invalidFanIdKey)) {
            String tempStr = redisCacheUtil.get(invalidFanIdKey);
            result = JSONArray.parseArray(tempStr, UnionConsume.class);
            return result;
        }
        // (2)db
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("fan_id", fanId);
        result = unionConsumeDao.selectList(entityWrapper);
        setInvalidCache(result, fanId, UnionConsumeCacheUtil.TYPE_FAN_ID);
        return result;
    }

    @Override
    public List<UnionConsume> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionConsume> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                result.add(getById(id));
            }
        }

        return result;
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionConsume> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionConsumeDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionConsume newUnionConsume) throws Exception {
        if (newUnionConsume == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionConsumeDao.insert(newUnionConsume);
        removeCache(newUnionConsume);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionConsume> newUnionConsumeList) throws Exception {
        if (newUnionConsumeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        unionConsumeDao.insertBatch(newUnionConsumeList);
        removeCache(newUnionConsumeList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
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
        unionConsumeDao.updateById(removeUnionConsume);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<UnionConsume> unionConsumeList = listByIdList(idList);
        removeCache(unionConsumeList);
        // (2)remove in db logically
        List<UnionConsume> removeUnionConsumeList = new ArrayList<>();
        for (Integer id : idList) {
            UnionConsume removeUnionConsume = new UnionConsume();
            removeUnionConsume.setId(id);
            removeUnionConsume.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionConsumeList.add(removeUnionConsume);
        }
        unionConsumeDao.updateBatchById(removeUnionConsumeList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
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
        unionConsumeDao.updateById(updateUnionConsume);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionConsume> updateUnionConsumeList) throws Exception {
        if (updateUnionConsumeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // (1)remove cache
        List<Integer> idList = getIdList(updateUnionConsumeList);
        List<UnionConsume> unionConsumeList = listByIdList(idList);
        removeCache(unionConsumeList);
        // (2)update db
        unionConsumeDao.updateBatchById(updateUnionConsumeList);
    }

    //********************************************* Object As a Service - cache support ********************************

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

    private void setValidCache(List<UnionConsume> newUnionConsumeList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String validForeignIdKey = null;
        switch (foreignIdType) {
            case UnionConsumeCacheUtil.TYPE_MEMBER_ID:
                validForeignIdKey = UnionConsumeCacheUtil.getValidMemberIdKey(foreignId);
                break;
            case UnionConsumeCacheUtil.TYPE_UNION_ID:
                validForeignIdKey = UnionConsumeCacheUtil.getValidUnionIdKey(foreignId);
                break;
            case UnionConsumeCacheUtil.TYPE_CARD_ID:
                validForeignIdKey = UnionConsumeCacheUtil.getValidCardIdKey(foreignId);
                break;
            case UnionConsumeCacheUtil.TYPE_FAN_ID:
                validForeignIdKey = UnionConsumeCacheUtil.getValidFanIdKey(foreignId);
                break;

            default:
                break;
        }
        if (validForeignIdKey != null) {
            redisCacheUtil.set(validForeignIdKey, newUnionConsumeList);
        }
    }

    private void setInvalidCache(List<UnionConsume> newUnionConsumeList, Integer foreignId, int foreignIdType) {
        if (foreignId == null) {
            //do nothing,just in case
            return;
        }
        String invalidForeignIdKey = null;
        switch (foreignIdType) {
            case UnionConsumeCacheUtil.TYPE_MEMBER_ID:
                invalidForeignIdKey = UnionConsumeCacheUtil.getInvalidMemberIdKey(foreignId);
                break;
            case UnionConsumeCacheUtil.TYPE_UNION_ID:
                invalidForeignIdKey = UnionConsumeCacheUtil.getInvalidUnionIdKey(foreignId);
                break;
            case UnionConsumeCacheUtil.TYPE_CARD_ID:
                invalidForeignIdKey = UnionConsumeCacheUtil.getInvalidCardIdKey(foreignId);
                break;
            case UnionConsumeCacheUtil.TYPE_FAN_ID:
                invalidForeignIdKey = UnionConsumeCacheUtil.getInvalidFanIdKey(foreignId);
                break;

            default:
                break;
        }
        if (invalidForeignIdKey != null) {
            redisCacheUtil.set(invalidForeignIdKey, newUnionConsumeList);
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

            String validMemberIdKey = UnionConsumeCacheUtil.getValidMemberIdKey(memberId);
            redisCacheUtil.remove(validMemberIdKey);

            String invalidMemberIdKey = UnionConsumeCacheUtil.getInvalidMemberIdKey(memberId);
            redisCacheUtil.remove(invalidMemberIdKey);
        }

        Integer unionId = unionConsume.getUnionId();
        if (unionId != null) {
            String unionIdKey = UnionConsumeCacheUtil.getUnionIdKey(unionId);
            redisCacheUtil.remove(unionIdKey);

            String validUnionIdKey = UnionConsumeCacheUtil.getValidUnionIdKey(unionId);
            redisCacheUtil.remove(validUnionIdKey);

            String invalidUnionIdKey = UnionConsumeCacheUtil.getInvalidUnionIdKey(unionId);
            redisCacheUtil.remove(invalidUnionIdKey);
        }

        Integer cardId = unionConsume.getCardId();
        if (cardId != null) {
            String cardIdKey = UnionConsumeCacheUtil.getCardIdKey(cardId);
            redisCacheUtil.remove(cardIdKey);

            String validCardIdKey = UnionConsumeCacheUtil.getValidCardIdKey(cardId);
            redisCacheUtil.remove(validCardIdKey);

            String invalidCardIdKey = UnionConsumeCacheUtil.getInvalidCardIdKey(cardId);
            redisCacheUtil.remove(invalidCardIdKey);
        }

        Integer fanId = unionConsume.getFanId();
        if (fanId != null) {
            String fanIdKey = UnionConsumeCacheUtil.getFanIdKey(fanId);
            redisCacheUtil.remove(fanIdKey);

            String validFanIdKey = UnionConsumeCacheUtil.getValidFanIdKey(fanId);
            redisCacheUtil.remove(validFanIdKey);

            String invalidFanIdKey = UnionConsumeCacheUtil.getInvalidFanIdKey(fanId);
            redisCacheUtil.remove(invalidFanIdKey);
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

                        String validMemberIdKey = UnionConsumeCacheUtil.getValidMemberIdKey(memberId);
                        result.add(validMemberIdKey);

                        String invalidMemberIdKey = UnionConsumeCacheUtil.getInvalidMemberIdKey(memberId);
                        result.add(invalidMemberIdKey);
                    }
                }
                break;
            case UnionConsumeCacheUtil.TYPE_UNION_ID:
                for (UnionConsume unionConsume : unionConsumeList) {
                    Integer unionId = unionConsume.getUnionId();
                    if (unionId != null) {
                        String unionIdKey = UnionConsumeCacheUtil.getUnionIdKey(unionId);
                        result.add(unionIdKey);

                        String validUnionIdKey = UnionConsumeCacheUtil.getValidUnionIdKey(unionId);
                        result.add(validUnionIdKey);

                        String invalidUnionIdKey = UnionConsumeCacheUtil.getInvalidUnionIdKey(unionId);
                        result.add(invalidUnionIdKey);
                    }
                }
                break;
            case UnionConsumeCacheUtil.TYPE_CARD_ID:
                for (UnionConsume unionConsume : unionConsumeList) {
                    Integer cardId = unionConsume.getCardId();
                    if (cardId != null) {
                        String cardIdKey = UnionConsumeCacheUtil.getCardIdKey(cardId);
                        result.add(cardIdKey);

                        String validCardIdKey = UnionConsumeCacheUtil.getValidCardIdKey(cardId);
                        result.add(validCardIdKey);

                        String invalidCardIdKey = UnionConsumeCacheUtil.getInvalidCardIdKey(cardId);
                        result.add(invalidCardIdKey);
                    }
                }
                break;
            case UnionConsumeCacheUtil.TYPE_FAN_ID:
                for (UnionConsume unionConsume : unionConsumeList) {
                    Integer fanId = unionConsume.getFanId();
                    if (fanId != null) {
                        String fanIdKey = UnionConsumeCacheUtil.getFanIdKey(fanId);
                        result.add(fanIdKey);

                        String validFanIdKey = UnionConsumeCacheUtil.getValidFanIdKey(fanId);
                        result.add(validFanIdKey);

                        String invalidFanIdKey = UnionConsumeCacheUtil.getInvalidFanIdKey(fanId);
                        result.add(invalidFanIdKey);
                    }
                }
                break;

            default:
                break;
        }
        return result;
    }

}