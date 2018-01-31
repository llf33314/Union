package com.gt.union.card.consume.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.WxPublicUsers;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.pay.entity.PayParam;
import com.gt.union.api.client.shop.ShopService;
import com.gt.union.api.client.socket.SocketService;
import com.gt.union.api.client.socket.constant.SocketKeyConstant;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.card.consume.constant.ConsumeConstant;
import com.gt.union.card.consume.dao.IUnionConsumeDao;
import com.gt.union.card.consume.entity.UnionConsume;
import com.gt.union.card.consume.entity.UnionConsumeProject;
import com.gt.union.card.consume.service.IUnionConsumeProjectService;
import com.gt.union.card.consume.service.IUnionConsumeService;
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
import com.gt.union.union.main.constant.UnionConstant;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.main.vo.UnionPayVO;
import com.gt.union.union.member.constant.MemberConstant;
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
                .eq("sys_order_no", orderNo);

        return unionConsumeDao.selectOne(entityWrapper);
    }

    @Override
    public UnionConsume getValidByOrderNoAndModel(String orderNo, Integer model) throws Exception {
        if (orderNo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("sys_order_no", orderNo)
                .eq("business_type", model);

        return unionConsumeDao.selectOne(entityWrapper);
    }

    @Override
    public UnionConsume getValidByBusinessOrderIdAndModel(Integer businessOrderId, Integer businessModel) throws Exception {
        if (businessOrderId == null || businessModel == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("business_order_id", businessOrderId)
                .eq("business_type", businessModel);

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

                List<UnionConsumeProject> consumeProjectList = unionConsumeProjectService.listValidByConsumeId(consume.getId());
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

                vo.setShopName(consume.getShopName());

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

        List<Integer> payStatusList = new ArrayList<>();
        payStatusList.add(ConsumeConstant.PAY_STATUS_SUCCESS);
        payStatusList.add(ConsumeConstant.PAY_STATUS_FAIL);
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("member_id", memberIdList)
                .eq(ListUtil.isEmpty(memberList), "member_id", null)
                .eq(optUnionId != null, "union_id", optUnionId)
                .eq(optShopId != null, "shop_id", optShopId)
                .in("pay_status", payStatusList)
                .ge(optBeginTime != null, "create_time", optBeginTime)
                .le(optEndTime != null, "create_time", optEndTime)
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

        List<Integer> payStatusList = new ArrayList<>();
        payStatusList.add(ConsumeConstant.PAY_STATUS_SUCCESS);
        payStatusList.add(ConsumeConstant.PAY_STATUS_FAIL);
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .in("member_id", memberIdList)
                .eq(ListUtil.isEmpty(memberList), "member_id", null)
                .eq(optUnionId != null, "union_id", optUnionId)
                .eq(optShopId != null, "shop_id", optShopId)
                .in("pay_status", payStatusList)
                .ge(optBeginTime != null, "create_time", optBeginTime)
                .le(optEndTime != null, "create_time", optEndTime)
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
        if (ListUtil.isNotEmpty(dataList)) {
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
                Double freeMoney = BigDecimalUtil.add(CommonUtil.isNotEmpty(consume.getDiscountMoney()) ? consume.getDiscountMoney() : 0, CommonUtil.isNotEmpty(consume.getIntegralMoney()) ? consume.getIntegralMoney() : 0).doubleValue();
                vo.setFreeMoney(freeMoney == 0 ? null : freeMoney);
                List<UnionConsumeProject> consumeProjectList = unionConsumeProjectService.listValidByConsumeId(consume.getId());
                if (ListUtil.isNotEmpty(consumeProjectList)) {
                    List<UnionCardProjectItem> items = new ArrayList<>();
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
        logger.info("联盟卡消费核销参数信息：busId == " + busId + ", unionId == " + unionId + ", fanId == " + fanId + ", ConsumePostVO : " +JSONObject.toJSONString(vo));
        // 判断union有效性
        UnionMain union = unionMainService.getValidById(unionId);
        if (!unionMainService.isUnionValid(union)) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        // 判断member读权限
        UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
        if (member == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        // 判断fanId有效性
        UnionCardFan fan = unionCardFanService.getValidById(fanId);
        if (fan == null) {
            throw new BusinessException("找不到联盟卡信息");
        }
        // 校验表单
        UnionConsume saveConsume = new UnionConsume();
        saveConsume.setDelStatus(CommonConstant.DEL_STATUS_NO);
        Date currentDate = DateUtil.getCurrentDate();
        saveConsume.setCreateTime(currentDate);
        saveConsume.setFanId(fanId);
        saveConsume.setMemberId(member.getId());
        saveConsume.setUnionId(unionId);

        Integer shopId = vo.getShopId();
        if (shopId != null) {
            WsWxShopInfoExtend shop = shopService.getById(shopId);
            if (shop == null) {
                throw new BusinessException("找不到门店信息");
            }
            saveConsume.setShopId(shopId);
            saveConsume.setShopName(shop.getBusinessName());
        }

        UnionConsume voConsume = vo.getConsume();
        if (voConsume == null) {
            throw new BusinessException("支付信息为空，请重新支付");
        }
        Double consumeMoney = voConsume.getConsumeMoney();
        if (consumeMoney == null || consumeMoney < 0) {
            throw new BusinessException("消费金额不能为空，且不能小于0");
        }
        if(consumeMoney > ConfigConstant.CONSUME_MAX_MONEY){
            throw new BusinessException("单次消费金额最大为5万元");
        }
        Integer isUseIntegral = vo.getIsUseIntegral();
        if(CommonConstant.COMMON_YES == isUseIntegral && union.getIsIntegral() != UnionConstant.IS_INTEGRAL_YES ){
            throw new BusinessException("不可使用积分抵扣");
        }
        Double discount = member.getDiscount() != null ? member.getDiscount() : 1.0;
        //折扣金额
        BigDecimal discountMoney = BigDecimalUtil.multiply(consumeMoney, discount);
        saveConsume.setDiscount(discount);
        saveConsume.setDiscountMoney(BigDecimalUtil.toDouble(discountMoney));
        //折后价
        BigDecimal payMoney = BigDecimalUtil.subtract(consumeMoney, discountMoney);

        UnionCardIntegral integral = unionCardIntegralService.getValidByUnionIdAndFanId(unionId, fanId);
        //使用积分
        if (CommonConstant.COMMON_YES == isUseIntegral) {
            Double integralExchangeRatio = member.getIntegralExchangeRatio();
            if (integralExchangeRatio == null) {
                throw new BusinessException("未设置积分兑换率");
            }
            if (integral == null || integral.getIntegral() < 1) {
                throw new BusinessException("积分不足，无法使用积分");
            }
            //抵扣的价格
            BigDecimal integralMoney = BigDecimalUtil.multiply(payMoney, integralExchangeRatio);

            // 消费多少积分可兑换1元  1积分抵0.01元
            Double useIntegralPerMoney = dictService.getExchangeIntegral();
            //抵扣最多可抵扣金额需要的积分
            BigDecimal useIntegral = BigDecimalUtil.multiply(integralMoney, useIntegralPerMoney);
            //不够积分
            if (useIntegral.doubleValue() > integral.getIntegral()) {
                //使用整数积分
                useIntegral = BigDecimal.valueOf(integral.getIntegral().intValue());
                integralMoney = BigDecimalUtil.divide(useIntegral, useIntegralPerMoney);
            }
            saveConsume.setUseIntegral(BigDecimalUtil.toDouble(useIntegral));
            saveConsume.setIntegralMoney(BigDecimalUtil.toDouble(integralMoney));

            //折后价减积分价  实付金额
            payMoney = BigDecimalUtil.subtract(payMoney, integralMoney);
        }
        // 支持0.01元精度失算
        if (Math.abs(BigDecimalUtil.subtract(payMoney, voConsume.getPayMoney()).doubleValue()) >= 0.01) {
            throw new BusinessException("实际支付金额错误，请刷新后重试");
        }
        saveConsume.setPayMoney(BigDecimalUtil.toDouble(payMoney));

        if (UnionConstant.IS_INTEGRAL_YES == union.getIsIntegral()) {
            //赠送积分
            Double giveIntegralPerMoney = dictService.getGiveIntegral();
            BigDecimal giveIntegral = BigDecimalUtil.multiply(payMoney, giveIntegralPerMoney);
            saveConsume.setGiveIntegral(BigDecimalUtil.toDouble(giveIntegral));
        }

        Integer voActivityId = vo.getActivityId();
        List<UnionCardProjectItem> voItemList = vo.getTextList();
        List<UnionConsumeProject> saveConsumeProjectList = new ArrayList<>();
        if (voActivityId != null && ListUtil.isNotEmpty(voItemList)) {
            Set<Integer> itemIdSet = new HashSet<>();
            for (UnionCardProjectItem voItem : voItemList) {
                UnionCardProject project = unionCardProjectService.getValidByUnionIdAndMemberIdAndActivityId(unionId, member.getId(), voActivityId);
                if (project == null) {
                    throw new BusinessException("找不到服务项目信息");
                }
                UnionCardProjectItem item = unionCardProjectItemService.getValidByIdAndProjectId(voItem.getId(), project.getId());
                if (item == null) {
                    throw new BusinessException("找不到服务优惠信息");
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
        saveConsume.setConsumeMoney(vo.getConsume().getConsumeMoney());
        saveConsume.setPayStatus(ConsumeConstant.PAY_STATUS_PAYING);
        UnionPayVO result = null;
        UnionCardIntegral saveIntegral = null;
        UnionCardIntegral updateIntegral = null;
        if (ConsumeConstant.VO_PAY_TYPE_CASH == voConsume.getPayType()) {
            saveConsume.setPayType(ConsumeConstant.PAY_TYPE_CASH);
            saveConsume.setPayStatus(ConsumeConstant.PAY_STATUS_SUCCESS);
            if (UnionConstant.IS_INTEGRAL_YES == union.getIsIntegral()) {
                //联盟开启积分
                if (integral == null) {
                    saveIntegral = new UnionCardIntegral();
                    saveIntegral.setDelStatus(CommonConstant.DEL_STATUS_NO);
                    saveIntegral.setCreateTime(currentDate);
                    saveIntegral.setFanId(fanId);
                    saveIntegral.setUnionId(unionId);
                    saveIntegral.setIntegral(CommonUtil.isNotEmpty(saveConsume.getGiveIntegral()) ? saveConsume.getGiveIntegral() : 0);
                } else {
                    updateIntegral = new UnionCardIntegral();
                    updateIntegral.setId(integral.getId());
                    BigDecimal tempIntegral = BigDecimalUtil.subtract(integral.getIntegral(), saveConsume.getUseIntegral());
                    tempIntegral = BigDecimalUtil.add(tempIntegral, saveConsume.getGiveIntegral());
                    updateIntegral.setIntegral(BigDecimalUtil.toDouble(tempIntegral));
                }
            }
        } else {
            result = new UnionPayVO();
            saveConsume.setPayType(ConsumeConstant.PAY_STATUS_PAYING);
            String socketKey = PropertiesUtil.getSocketKey() + SocketKeyConstant.CARD_CONSUME + busId;
            String notifyUrl = PropertiesUtil.getUnionUrl() + "/callBack/79B4DE7C/consume?socketKey=" + socketKey;

            PayParam payParam = new PayParam();
            payParam.setTotalFee(saveConsume.getPayMoney())
                    .setOrderNum(saveConsume.getSysOrderNo())
                    .setIsreturn(CommonConstant.COMMON_NO)
                    .setNotifyUrl(notifyUrl)
                    .setIsSendMessage(CommonConstant.COMMON_NO)
                    .setPayWay(0)
                    .setDesc("消费核销")
                    .setPayDuoFen(false)
                    .setBusId(busId);
            WxPublicUsers publicUsers = busUserService.getWxPublicUserByBusId(busId);
            payParam.setAppid(CommonUtil.isNotEmpty(publicUsers) ? publicUsers.getAppid() : null);

            String payUrl = wxPayService.qrCodePay(payParam);

            result.setOrderNo(orderNo);
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

        if (saveIntegral != null) {
            unionCardIntegralService.save(saveIntegral);
        }
        if (updateIntegral != null) {
            unionCardIntegralService.update(updateIntegral);
        }

        return result;
    }

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateCallbackByOrderNo(String orderNo, String socketKey, String payType, String payOrderNo, Integer isSuccess) {
        logger.info("联盟卡消费支付回调参数：" + "orderNo == " + orderNo + ",socketKey == " + socketKey + ",payType == " + payType + ",payOrderNo == " + payOrderNo + ",isSuccess == " +isSuccess);
        Map<String, Object> result = new HashMap<>(2);
        if (orderNo == null || socketKey == null || payType == null || payOrderNo == null || isSuccess == null) {
            result.put("code", -1);
            result.put("msg", "参数缺少");
            return JSONObject.toJSONString(result);
        }

        // 判断consumeId有效性
        UnionConsume consume;
        try {
            consume = getValidByOrderNo(orderNo);
        } catch (Exception e) {
            logger.error("消费核销支付成功回调错误", e);
            result.put("code", -1);
            result.put("msg", "订单不存在");
            return JSONObject.toJSONString(result);
        }
        if (consume == null) {
            result.put("code", -1);
            result.put("msg", "找不到消费记录");
            return JSONObject.toJSONString(result);
        }
        boolean isIntegral = false;
        try{
            UnionMain main = unionMainService.getValidById(consume.getUnionId());
            isIntegral = CommonUtil.isEmpty(main) ? false : UnionConstant.IS_INTEGRAL_YES == main.getIsIntegral() ? true: false;
        }catch (Exception e){
            logger.error("消费核销支付成功回调错误", e);
            result.put("code", -1);
            result.put("msg", e.getMessage());
            return JSONObject.toJSONString(result);
        }
        // 如果consume是未支付状态，更新consume为支付成功状态，且socket通知，并返回处理成功
        Integer payStatus = consume.getPayStatus();
        if (payStatus == ConsumeConstant.PAY_STATUS_SUCCESS || payStatus == ConsumeConstant.PAY_STATUS_FAIL) {
            result.put("code", -1);
            result.put("msg", "订单已重复处理");
            return JSONObject.toJSONString(result);
        } else {
            //数据库锁
            if(updateConsumeStatusById(consume.getId(), isSuccess == CommonConstant.COMMON_YES ? ConsumeConstant.PAY_STATUS_SUCCESS : ConsumeConstant.PAY_STATUS_FAIL)){
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

                UnionCardIntegral integral;
                try {
                    integral = unionCardIntegralService.getValidByUnionIdAndFanId(consume.getUnionId(), consume.getFanId());
                } catch (Exception e) {
                    logger.error("消费核销支付成功回调错误", e);
                    result.put("code", -1);
                    result.put("msg", e.getMessage());
                    return JSONObject.toJSONString(result);
                }
                UnionCardIntegral saveIntegral = null;
                UnionCardIntegral updateIntegral = null;
                if (integral == null) {
                    saveIntegral = new UnionCardIntegral();
                    saveIntegral.setDelStatus(CommonConstant.DEL_STATUS_NO);
                    saveIntegral.setCreateTime(DateUtil.getCurrentDate());
                    saveIntegral.setFanId(consume.getFanId());
                    saveIntegral.setUnionId(consume.getUnionId());
                    saveIntegral.setIntegral(isIntegral ? (CommonUtil.isNotEmpty(consume.getGiveIntegral()) ? consume.getGiveIntegral(): 0) : 0);
                } else {
                    updateIntegral = new UnionCardIntegral();
                    updateIntegral.setId(integral.getId());
                    Double temIntegral = BigDecimalUtil.subtract(integral.getIntegral(), CommonUtil.isNotEmpty(consume.getUseIntegral()) ?consume.getUseIntegral() : 0).doubleValue();
                    BigDecimal finalIntegral = BigDecimalUtil.add(temIntegral, isIntegral ? (CommonUtil.isNotEmpty(consume.getGiveIntegral()) ? consume.getGiveIntegral(): 0) : 0);
                    updateIntegral.setIntegral(BigDecimalUtil.toDouble(finalIntegral));
                }

                try {
                    update(updateConsume);
                    if (saveIntegral != null) {
                        unionCardIntegralService.save(saveIntegral);
                    }
                    if (updateIntegral != null) {
                        unionCardIntegralService.update(updateIntegral);
                    }
                } catch (Exception e) {
                    logger.error("消费核销支付成功回调错误", e);
                    result.put("code", -1);
                    result.put("msg", e.getMessage());
                    return JSONObject.toJSONString(result);
                }

                // socket通知
                socketService.socketPaySendMessage(socketKey, isSuccess, null, orderNo);
                result.put("code", 0);
                result.put("msg", "成功");
                return JSONObject.toJSONString(result);
            }else {
                result.put("code", -1);
                result.put("msg", "订单已重复处理");
                return JSONObject.toJSONString(result);
            }
        }
    }

    /**
     *  修改消费订单支付状态
     * @param id       消费订单id
     * @param payStatus 支付状态
     */
    private boolean updateConsumeStatusById(Integer id, int payStatus) {
        EntityWrapper<UnionConsume> wrapper = new EntityWrapper<>();
        wrapper.eq("id",id);
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        wrapper.eq("pay_status", ConsumeConstant.PAY_STATUS_PAYING);

        UnionConsume consume = new UnionConsume();
        consume.setPayStatus(payStatus);
        return unionConsumeDao.update(consume, wrapper);
    }

    //********************************************* Base On Business - other *******************************************

    @Override
    public Integer countValidPayByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .eq("fan_id", fanId);

        return unionConsumeDao.selectCount(entityWrapper);
    }

    @Override
    public Integer countValidPayByFanIdAndStatus(Integer fanId, Integer status) throws Exception {
        if (fanId == null || status == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.COMMON_NO)
                .eq("fan_id", fanId)
                .eq("pay_status", status);

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

        return unionConsumeDao.selectById(id);
    }

    @Override
    public UnionConsume getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", id);

        return unionConsumeDao.selectOne(entityWrapper);
    }

    @Override
    public UnionConsume getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("id", id);

        return unionConsumeDao.selectOne(entityWrapper);
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

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId);

        return unionConsumeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsume> listValidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("member_id", memberId);

        return unionConsumeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsume> listInvalidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("member_id", memberId);

        return unionConsumeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsume> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);

        return unionConsumeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsume> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);

        return unionConsumeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsume> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);

        return unionConsumeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsume> listByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("card_id", cardId);

        return unionConsumeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsume> listValidByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("card_id", cardId);

        return unionConsumeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsume> listInvalidByCardId(Integer cardId) throws Exception {
        if (cardId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("card_id", cardId);

        return unionConsumeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsume> listByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("fan_id", fanId);

        return unionConsumeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsume> listValidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("fan_id", fanId);

        return unionConsumeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsume> listInvalidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("fan_id", fanId);

        return unionConsumeDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionConsume> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionConsume> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", idList).eq(ListUtil.isEmpty(idList), "id", null);

        return unionConsumeDao.selectList(entityWrapper);
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
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionConsume> newUnionConsumeList) throws Exception {
        if (newUnionConsumeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionConsumeDao.insertBatch(newUnionConsumeList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

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

        unionConsumeDao.updateById(updateUnionConsume);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionConsume> updateUnionConsumeList) throws Exception {
        if (updateUnionConsumeList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionConsumeDao.updateBatchById(updateUnionConsumeList);
    }

}