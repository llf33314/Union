package com.gt.union.wxapp.card.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.api.bean.session.Member;
import com.gt.api.util.HttpClienUtils;
import com.gt.union.api.client.member.MemberService;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.pay.entity.PayParam;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.card.activity.constant.ActivityConstant;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.activity.service.IUnionCardActivityService;
import com.gt.union.card.consume.service.IUnionConsumeProjectService;
import com.gt.union.card.consume.service.IUnionConsumeService;
import com.gt.union.card.main.constant.CardConstant;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.entity.UnionCardRecord;
import com.gt.union.card.main.service.*;
import com.gt.union.card.project.constant.ProjectConstant;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.card.project.service.IUnionCardProjectItemService;
import com.gt.union.card.project.service.IUnionCardProjectService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.SmsCodeConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.main.vo.UnionPayVO;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import com.gt.union.wxapp.card.constant.WxAppCardConstant;
import com.gt.union.wxapp.card.service.IWxAppCardService;
import com.gt.union.wxapp.card.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author hongjiye
 * @time 2017-12-29 15:07
 **/
@Service
public class WxAppCardServiceImpl implements IWxAppCardService {

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionCardFanService unionCardFanService;

    @Autowired
    private IUnionCardService unionCardService;

    @Autowired
    private IUnionCardActivityService unionCardActivityService;

    @Autowired
    private IUnionCardProjectItemService unionCardProjectItemService;

    @Autowired
    private IUnionCardProjectService unionCardProjectService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private SmsService smsService;

    @Resource(name = "unionWxAppCardApplyService")
    private IUnionCardApplyService unionCardApplyService;

    @Autowired
    private IUnionConsumeService unionConsumeService;

    @Autowired
    private IUnionCardIntegralService unionCardIntegralService;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionCardRecordService unionCardRecordService;

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private IUnionConsumeProjectService unionConsumeProjectService;


    @Override
    public Page listWxAppCardPage(String phone, Integer busId, Page page) throws Exception {
        if (CommonUtil.isEmpty(busId)) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        //查询有效的
        List<UnionMember> members = unionMemberService.listValidReadByBusId(busId);
        List<UnionCardVO> indexCardList = new ArrayList<>();
        if (ListUtil.isNotEmpty(members)) {
            //入盟时间倒序
            Collections.sort(members, new Comparator<UnionMember>() {
                @Override
                public int compare(UnionMember o1, UnionMember o2) {
                    return o2.getCreateTime().compareTo(o1.getCreateTime());
                }
            });
            List<UnionCardVO> discountCardList = new ArrayList<>();
            List<UnionCardVO> activityCardList = new ArrayList<>();
            UnionCardFan fan = unionCardFanService.getValidByPhone(phone);
            long nowTime = DateUtil.getCurrentDate().getTime();
            for (UnionMember member : members) {
                UnionMain unionMain = unionMainService.getValidById(member.getUnionId());
                if (unionMain != null && unionMainService.isUnionValid(unionMain)) {

                    UnionCardVO discountCardVO = getUnionCardVO(unionMain.getName() + "折扣卡", CardConstant.TYPE_DISCOUNT, unionMain.getId(), null, null, member.getId());
                    if (CommonUtil.isNotEmpty(fan) && unionCardService.existValidUnexpiredByUnionIdAndFanIdAndType(unionMain.getId(), fan.getId(), CardConstant.TYPE_DISCOUNT)) {
                        //已办理折扣卡
                        discountCardVO.setStatus(WxAppCardConstant.CARD_DISCOUNT_APPLY);
                    }
                    discountCardVO.setUnionMemberId(member.getId());
                    discountCardVO.setIllustration(unionMain.getIllustration());
                    discountCardList.add(discountCardVO);

                    //活动卡
                    List<UnionCardActivity> list = unionCardActivityService.listValidByUnionId(unionMain.getId());
                    if (ListUtil.isNotEmpty(list)) {
                        for (UnionCardActivity activity : list) {
                            UnionCardVO activityCardVO = getActivityCard(activity, nowTime, unionMain.getId(), member.getId(), CommonUtil.isEmpty(fan) ? null : fan.getId());
                            if(activityCardVO != null){
                                activityCardList.add(activityCardVO);
                            }
                        }
                    }
                }
            }
            indexCardList.addAll(discountCardList);
            indexCardList.addAll(activityCardList);
        }
        page = PageUtil.setRecord(page,indexCardList);
        return page;
    }

    @Override
    public List<UnionMember> listNearUser(Integer busId, String enterpriseName) throws Exception{
        List<UnionMember> members = unionMemberService.listValidReadByBusId(busId);
        List<UnionMember> allList = new ArrayList<UnionMember>();
        if (ListUtil.isNotEmpty(members)) {
            for (UnionMember member : members) {
                UnionMain unionMain = unionMainService.getValidById(member.getUnionId());
                if (unionMain != null && unionMainService.isUnionValid(unionMain)) {
                    List<UnionMember> memberList = unionMemberService.listValidReadByUnionId(unionMain.getId());
                    if(ListUtil.isNotEmpty(memberList)){
                        if(StringUtil.isNotEmpty(enterpriseName)){
                            memberList = unionMemberService.filterByListEnterpriseName(memberList, enterpriseName);
                        }
                        allList.addAll(memberList);
                    }
                }
            }
        }
        return allList;
    }

    UnionCardVO getUnionCardVO(String cardName, Integer type, Integer unionId, Integer activityId, String color, Integer memberId) {
        UnionCardVO unionCardVO = new UnionCardVO();
        unionCardVO.setCardName(cardName);
        unionCardVO.setCardType(type);
        unionCardVO.setUnionId(unionId);
        unionCardVO.setActivityId(activityId);
        unionCardVO.setUnionMemberId(memberId);
        if (CommonUtil.isNotEmpty(color)) {
            String[] c = color.split(",");
            unionCardVO.setColor1(c[0]);
            unionCardVO.setColor2(c[1]);
        }
        return unionCardVO;
    }


    private UnionCardVO getActivityCard(UnionCardActivity activity, long nowTime, Integer unionId, Integer memberId, Integer fanId) throws Exception{
        UnionCardVO activityCardVO = null;
        if(nowTime > activity.getSellBeginTime().getTime()){
            //售卡开始
            activityCardVO = getUnionCardVO(activity.getName(), CardConstant.TYPE_ACTIVITY, unionId, activity.getId(), activity.getColor(), memberId);
            if(nowTime > activity.getSellBeginTime().getTime() && nowTime < activity.getSellEndTime().getTime()){
                //售卡中
                int count = unionCardService.countValidByUnionIdAndActivityId(unionId,activity.getId());
                if(activity.getAmount() <= count){
                    //售罄
                    activityCardVO.setStatus(WxAppCardConstant.CARD_SELL_OUT);
                }else {
                    if (CommonUtil.isEmpty(fanId) || !unionCardService.existValidUnexpiredByUnionIdAndFanIdAndActivityId(unionId, fanId, activity.getId())) {
                        //没办理过该联盟卡或已过期
                        activityCardVO.setStatus(WxAppCardConstant.CARD_ACTIVITY_APPLY);
                    }
                }
            }else {
                //结束
                activityCardVO.setStatus(WxAppCardConstant.CARD_SELL_OUT);
            }
            activityCardVO.setIllustration(activity.getIllustration());
            activityCardVO.setCardPrice(activity.getPrice());
        }
        return activityCardVO;
    }

    @Override
    public CardDetailVO getCardDetail(String phone, Integer busId, Integer unionId, Integer activityId, Integer unionMemberId) throws Exception {
        if (CommonUtil.isEmpty(busId) || CommonUtil.isEmpty(unionId)) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionMain union = unionMainService.getById(unionId);
        if (union == null) {
            throw new BusinessException(CommonConstant.UNION_INVALID);
        }
        UnionMember unionMember = unionMemberService.getById(unionMemberId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.MEMBER_NOT_FOUND);
        }
        if(!unionMember.getUnionId().equals(union.getId())){
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        CardDetailVO result = new CardDetailVO();
        result.setIsTransacted(CommonConstant.COMMON_YES);
        result.setUnionId(unionId);
        UnionCardFan fan = unionCardFanService.getValidByPhone(phone);
        if (CommonUtil.isEmpty(activityId)) {
            //折扣卡
            if(!unionMemberService.existValidReadById(unionMember.getId()) || !unionMainService.isUnionValid(union)){
                //联盟或盟员无效
                result.setIsTransacted(CommonConstant.COMMON_NO);
            }else {
                if (fan != null && unionCardService.existValidUnexpiredByUnionIdAndFanIdAndType(unionId, fan.getId(), CardConstant.TYPE_DISCOUNT)) {
                    result.setIsTransacted(CommonConstant.COMMON_NO);
                }
            }
            result.setCardType(CardConstant.TYPE_DISCOUNT);
            result.setCardName(union.getName() + "折扣卡");
            List<UnionMember> members = unionMemberService.listValidReadByUnionId(unionId);
            result.setUserCount(members.size());
        } else {
            //活动卡
            UnionCardActivity activity = unionCardActivityService.getValidByIdAndUnionId(activityId, unionId);
            if (CommonUtil.isEmpty(activity)) {
                throw new BusinessException("找不到该联盟卡");
            }
            if(!unionMemberService.existValidReadById(unionMember.getId()) || !unionMainService.isUnionValid(union)){
                //联盟或盟员无效
                result.setIsTransacted(CommonConstant.COMMON_NO);
            }else {
                if (fan != null) {
                    //办理过该活动卡
                    UnionCard card = unionCardService.getValidUnexpiredByUnionIdAndFanIdAndActivityId(unionId, fan.getId(), activityId);
                    if (card != null) {
                        //有有效的活动卡
                        result.setIsTransacted(CommonConstant.COMMON_NO);
                        result.setIsOverdue(CommonConstant.COMMON_NO);
                        result.setValidityStr(DateTimeKit.format(card.getValidity(), DateTimeKit.DEFAULT_DATE_FORMAT));
                    } else {
                        //没有有效的活动卡
                        if (DateTimeKit.isBetween(activity.getSellBeginTime(), activity.getSellEndTime())) {
                            //有效期 可以卖
                            Integer activityCardCount = unionCardService.countValidByUnionIdAndActivityId(unionId, activityId);
                            if (activityCardCount >= activity.getAmount()) {
                                result.setIsTransacted(CommonConstant.COMMON_NO);
                            }else {
                                result.setValidityDay(activity.getValidityDay());
                            }
                        } else {
                            result.setIsTransacted(CommonConstant.COMMON_NO);
                            //已过期
                            result.setIsOverdue(CommonConstant.COMMON_YES);
                        }
                    }
                } else {
                    result = checkActivity(activity, result);
                }
            }
            result.setActivityIllustration(activity.getIllustration());
            result.setCardName(activity.getName());
            result.setCardType(CardConstant.TYPE_ACTIVITY);
            result.setCardPrice(activity.getPrice());
            if (CommonUtil.isNotEmpty(activity.getColor())) {
                String[] c = activity.getColor().split(",");
                result.setColor1(c[0]);
                result.setColor2(c[1]);
            }
            result.setItemCount(unionCardProjectItemService.countValidCommittedByUnionIdAndActivityId(unionId, activity.getId()));
        }
        return result;
    }

    CardDetailVO checkActivity(UnionCardActivity activity, CardDetailVO result) throws Exception{
        if (DateTimeKit.isBetween(activity.getSellBeginTime(), activity.getSellEndTime())) {
            //有效期 可以卖
            Integer activityCardCount = unionCardService.countValidByUnionIdAndActivityId(activity.getUnionId(), activity.getId());
            if (activityCardCount >= activity.getAmount()) {
                result.setIsTransacted(CommonConstant.COMMON_NO);
            }else {
                result.setValidityDay(activity.getValidityDay());
            }
        } else {
            result.setIsTransacted(CommonConstant.COMMON_NO);
        }
        return result;
    }

    @Override
    public MyCardDetailVO myCardDetail(String phone) throws Exception {
        MyCardDetailVO vo = new MyCardDetailVO();
        UnionCardFan fan = unionCardFanService.getValidByPhone(phone);
        if (fan != null) {
            Double integral = unionCardIntegralService.sumValidIntegralByFanId(fan.getId());
            vo.setIntegral(integral);
            Integer consumeCount = unionConsumeService.countValidPayByFanId(fan.getId());
            vo.setConsumeCount(consumeCount);
            vo.setCardNo(fan.getNumber());
            vo.setCardImg(PropertiesUtil.getUnionUrl() + "/union/wxAppCard/"+PropertiesUtil.getAppVersion()+"/qr/cardNo?cardNo=" + fan.getNumber());
        }
        return vo;
    }

    @Override
    public Member bindCardPhone(Member member, Integer busId, String phone, String code) throws Exception {
        if (busId == null || StringUtil.isEmpty(phone) || StringUtil.isEmpty(code)) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        if (!smsService.checkPhoneCode(SmsCodeConstant.UNION_CARD_PHONE_BIND_TYPE, code, phone)) {
            throw new ParamException(CommonConstant.CODE_ERROR_MSG);
        }
        return memberService.bindMemberPhoneApp(busId, member.getId(), phone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String cardTransaction(String phone, Integer busId, Integer activityId, Integer unionId) throws Exception {
        if (busId == null || unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Map<String, Object> data = new HashMap<String, Object>();
        if (StringUtil.isEmpty(phone)) {
            //没有手机号
            data.put("phone", 0);
            return GtJsonResult.instanceSuccessMsg(data).toString();
        }
        List list = new ArrayList<>();
        if (CommonUtil.isNotEmpty(activityId)) {
            list.add(activityId);
        }
        UnionCardFan fan = unionCardFanService.getOrSaveByPhone(phone);
        UnionPayVO result = unionCardService.saveApplyByBusIdAndUnionIdAndFanId(busId, unionId, fan.getId(), list, unionCardApplyService);
        if (result != null) {
            data.put("pay", true);
            data.put("orderNo", result.getOrderNo());
            data.put("appid", PropertiesUtil.getUnionAppId());
            data.put("busId", PropertiesUtil.getDuofenBusId());
        }
        return GtJsonResult.instanceSuccessMsg(data).toString();
    }

    @Override
    public Page pageConsumeByPhone(Page page, String phone) throws Exception {
        UnionCardFan fan = unionCardFanService.getValidByPhone(phone);
        if (fan != null) {
            return unionConsumeService.pageConsumeByFanId(page, fan.getId());
        }
        return page;
    }


    @Override
    public Page<List<CardDetailListVO>> listCardDetailPage(Integer busId, Integer unionId, Integer activityId, Page page, Integer unionMemberId) throws Exception {
        if (busId == null || unionId == null || page == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        final UnionMember unionMember = unionMemberService.getById(unionMemberId);
        if (unionMember == null) {
            throw new BusinessException(CommonConstant.MEMBER_NOT_FOUND);
        }
        if(!unionMember.getUnionId().equals(unionId)){
            throw new BusinessException(CommonConstant.UNION_MEMBER_ERROR);
        }
        List<CardDetailListVO> list = new ArrayList<>();
        List<UnionMember> members = unionMemberService.listValidReadByUnionId(unionId);
        if (ListUtil.isNotEmpty(members)) {
            //本商家盟员在前面，其他按时间倒序
            Collections.sort(members, new Comparator<UnionMember>() {
                @Override
                public int compare(UnionMember o1, UnionMember o2) {
                    if (o1.getId().equals(unionMember.getId())) {
                        return -1;
                    }
                    if (o2.getId().equals(unionMember.getId())) {
                        return 1;
                    }
                    return o2.getCreateTime().compareTo(o1.getCreateTime());
                }
            });
        }
        if (ListUtil.isNotEmpty(members)) {
            for (UnionMember member : members) {
                CardDetailListVO listVO = new CardDetailListVO();
                listVO.setUnionMember(member);
                if (CommonUtil.isNotEmpty(activityId)) {
                    UnionCardProject project = unionCardProjectService.getValidByUnionIdAndMemberIdAndActivityIdAndStatus(unionId, member.getId(), activityId, ProjectConstant.STATUS_ACCEPT);
                    List<UnionCardProjectItem> textItemList = unionCardProjectItemService.listValidByUnionIdAndMemberIdAndActivityIdAndProjectStatus(unionId, member.getId(), activityId, ProjectConstant.STATUS_ACCEPT);
                    if (ListUtil.isNotEmpty(textItemList)) {
                        for (UnionCardProjectItem textItem : textItemList) {
                            Integer consumeItemCount = unionConsumeProjectService.countValidByProjectIdAndProjectItemId(project.getId(), textItem.getId());
                            Integer textItemNumber = textItem.getNumber() != null ? textItem.getNumber() : 0;
                            Integer surplusItemCount = textItemNumber - consumeItemCount;
                            textItem.setNumber(surplusItemCount);
                            listVO.setUnionCardProjectItems(textItemList);
                        }
                    }
                }
                list.add(listVO);
            }
        }
        page = PageUtil.setRecord(page, list);
        return page;
    }

    @Override
    public Page listMyCardPage(String phone, Page page) throws Exception {
        if (page == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List cardList = new ArrayList<>();
        UnionCardFan fan = unionCardFanService.getValidByPhone(phone);
        if (fan != null) {
            EntityWrapper<UnionCard> wrapper = new EntityWrapper<>();
            wrapper.eq("fan_id", fan.getId())
                    .eq("del_status", CommonConstant.DEL_STATUS_NO)
                    .orderBy("type asc, create_time", false);
            page = unionCardService.pageSupport(page, wrapper);
            List<UnionCard> list = page.getRecords();
            if (ListUtil.isNotEmpty(list)) {
                for (UnionCard card : list) {
                    UnionMain union = unionMainService.getById(card.getUnionId());
                    MyUnionCardDetailVO detailVO = new MyUnionCardDetailVO();
                    detailVO.setCardType(card.getType());
                    detailVO.setUnionId(union.getId());
                    detailVO.setUnionMemberId(card.getMemberId());
                    if (card.getType().equals(CardConstant.TYPE_DISCOUNT)) {
                        //折扣卡
                        detailVO.setCardName(union.getName() + "折扣卡");
                    } else if (card.getType().equals(CardConstant.TYPE_ACTIVITY)) {
                        //活动卡
                        UnionCardActivity activity = unionCardActivityService.getValidByIdAndUnionId(card.getActivityId(), union.getId());
                        detailVO.setCardName(activity.getName());
                        if (CommonUtil.isNotEmpty(activity.getColor())) {
                            String[] c = activity.getColor().split(",");
                            detailVO.setColor1(c[0]);
                            detailVO.setColor2(c[1]);
                        }
                        detailVO.setActivityId(activity.getId());
                        detailVO.setValidityStr(DateTimeKit.format(card.getValidity(), DateTimeKit.DEFAULT_DATE_FORMAT));
                        detailVO.setIsOverdue(DateTimeKit.laterThanNow(card.getValidity()) ? 0 : 1);
                        //优惠项目数
                        int itemCount = unionCardProjectItemService.countValidCommittedByUnionIdAndActivityId(union.getId(), activity.getId());
                        detailVO.setItemCount(itemCount);
                    }
                    cardList.add(detailVO);
                }
            }
        }
        page = PageUtil.setRecord(page, cardList);
        return page;
    }

    @Override
    public String getPayParam(Integer duoFenMemberId, String orderNo, String phone) throws Exception {
        if (duoFenMemberId == null || StringUtil.isEmpty(orderNo) || StringUtil.isEmpty(phone)) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        String notifyUrl = PropertiesUtil.getUnionUrl() + "/callBack/79B4DE7C/card?socketKey=";

        UnionCardRecord record = unionCardRecordService.getValidByOrderNo(orderNo);
        if (record == null || record.getPayStatus() != CardConstant.PAY_STATUS_PAYING) {
            throw new BusinessException("订单不存在");
        }
        PayParam payParam = new PayParam();
        payParam.setTotalFee(record.getPayMoney());
        payParam.setOrderNum(orderNo);
        payParam.setIsreturn(CommonConstant.COMMON_NO);
        payParam.setReturnUrl("");
        payParam.setNotifyUrl(notifyUrl);
        payParam.setIsSendMessage(CommonConstant.COMMON_NO);
        payParam.setPayWay(1);
        payParam.setDesc("办理联盟卡");
        payParam.setPayDuoFen(true);
        payParam.setMemberId(duoFenMemberId);
        String obj = wxPayService.wxAppPay(payParam);
        String payUrl = PropertiesUtil.getWxmpUrl() + "/wxPay/79B4DE7C/commonpayVerApplet2_0.do?obj="+obj;
        Map data = HttpClienUtils.reqGetUTF8(null, payUrl, Map.class);
        data.put("signType", "MD5");
        return GtJsonResult.instanceSuccessMsg(data).toString();
    }
}
