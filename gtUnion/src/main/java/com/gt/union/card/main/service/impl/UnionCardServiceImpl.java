package com.gt.union.card.main.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.api.client.socket.SocketService;
import com.gt.union.card.activity.constant.ActivityConstant;
import com.gt.union.card.activity.entity.UnionCardActivity;
import com.gt.union.card.activity.service.IUnionCardActivityService;
import com.gt.union.card.main.constant.CardConstant;
import com.gt.union.card.main.dao.IUnionCardDao;
import com.gt.union.card.main.entity.UnionCard;
import com.gt.union.card.main.entity.UnionCardFan;
import com.gt.union.card.main.entity.UnionCardRecord;
import com.gt.union.card.main.service.IUnionCardApplyService;
import com.gt.union.card.main.service.IUnionCardFanService;
import com.gt.union.card.main.service.IUnionCardRecordService;
import com.gt.union.card.main.service.IUnionCardService;
import com.gt.union.card.main.vo.*;
import com.gt.union.card.project.constant.ProjectConstant;
import com.gt.union.card.project.entity.UnionCardProject;
import com.gt.union.card.project.entity.UnionCardProjectItem;
import com.gt.union.card.project.service.IUnionCardProjectItemService;
import com.gt.union.card.project.service.IUnionCardProjectService;
import com.gt.union.card.sharing.entity.UnionCardSharingRatio;
import com.gt.union.card.sharing.entity.UnionCardSharingRecord;
import com.gt.union.card.sharing.service.IUnionCardSharingRatioService;
import com.gt.union.card.sharing.service.IUnionCardSharingRecordService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.constant.SmsCodeConstant;
import com.gt.union.common.exception.BaseException;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.opportunity.brokerage.constant.BrokerageConstant;
import com.gt.union.opportunity.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.opportunity.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import com.gt.union.union.main.vo.UnionPayVO;
import com.gt.union.union.member.constant.MemberConstant;
import com.gt.union.union.member.entity.UnionMember;
import com.gt.union.union.member.service.IUnionMemberService;
import org.apache.log4j.Logger;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 联盟卡 服务实现类
 *
 * @author linweicong
 * @version 2017-11-23 17:39:04
 */
@Service
public class UnionCardServiceImpl implements IUnionCardService {
    private Logger logger = Logger.getLogger(UnionCardServiceImpl.class);

    @Autowired
    private IUnionCardDao unionCardDao;

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
    private SmsService smsService;

    @Autowired
    private IUnionCardRecordService unionCardRecordService;

    @Autowired
    private IUnionCardSharingRatioService unionCardSharingRatioService;

    @Autowired
    private IUnionCardSharingRecordService unionCardSharingRecordService;

    @Autowired
    private IUnionBrokerageIncomeService unionBrokerageIncomeService;

    @Autowired
    private SocketService socketService;

    @Autowired
    private IUnionCardProjectItemService unionCardProjectItemService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private TransactionTemplate transactionTemplate;

    //********************************************* Base On Business - get *********************************************

    @Override
    public CardApplyVO getCardApplyVOByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        CardApplyVO result = new CardApplyVO();
        List<DiscountCard> discountCardList = new ArrayList<>();
        List<ActivityCard> activityCardList = new ArrayList<>();
        List<UnionMember> memberList = unionMemberService.listValidReadByBusId(busId);
        if (ListUtil.isNotEmpty(memberList)) {
            Collections.sort(memberList, new Comparator<UnionMember>() {
                @Override
                public int compare(UnionMember o1, UnionMember o2) {
                    if (MemberConstant.IS_UNION_OWNER_YES == o1.getIsUnionOwner()) {
                        return -1;
                    }
                    if (MemberConstant.IS_UNION_OWNER_YES == o2.getIsUnionOwner()) {
                        return 1;
                    }
                    return o1.getCreateTime().compareTo(o2.getCreateTime());
                }
            });
            for (UnionMember member : memberList) {
                Integer unionId = member.getUnionId();
                if (!unionMainService.isUnionValid(unionId)) {
                    continue;
                }
                // 折扣卡
                DiscountCard discountCard = new DiscountCard();
                discountCard.setUnion(unionMainService.getById(unionId));

                List<UnionMember> discountCardMemberList = unionMemberService.listValidReadByUnionId(unionId);
                unionMemberService.sortByDiscountAndCreateTime(discountCardMemberList);
                discountCard.setMemberList(discountCardMemberList);

                discountCardList.add(discountCard);

                // 活动卡
                List<UnionCardActivity> activityList = unionCardActivityService.listValidByUnionIdAndStatus(unionId, ActivityConstant.STATUS_SELLING);
                if (ListUtil.isNotEmpty(activityList)) {
                    Integer memberId = member.getId();
                    for (UnionCardActivity activity : activityList) {
                        Integer activityId = activity.getId();
                        UnionCardProject project = unionCardProjectService.getValidByUnionIdAndMemberIdAndActivityIdAndStatus(unionId, memberId, activityId, ProjectConstant.STATUS_ACCEPT);
                        if (project == null) {
                            // 排除没有报名参加或报名参加了但未审核通过的
                            continue;
                        }
                        ActivityCard activityCard = new ActivityCard();
                        activityCard.setActivity(activity);

                        Integer projectItemCount = 0;
                        List<ActivityCardProject> cardProjectList = new ArrayList<>();
                        List<UnionCardProject> projectList = unionCardProjectService.listValidWithoutExpiredMemberByUnionIdAndActivityIdAndStatus(unionId, activityId, ProjectConstant.STATUS_ACCEPT);
                        if (ListUtil.isNotEmpty(projectList)) {
                            for (UnionCardProject project1 : projectList) {
                                ActivityCardProject activityCardProject = new ActivityCardProject();
                                activityCardProject.setProject(project1);

                                UnionMember member1 = unionMemberService.getById(project1.getMemberId());
                                activityCardProject.setMember(member1);

                                List<UnionCardProjectItem> projectItemList = unionCardProjectItemService.listValidByProjectId(project1.getId());
                                activityCardProject.setProjectItemList(projectItemList);
                                projectItemCount += ListUtil.isNotEmpty(projectItemList) ? projectItemList.size() : 0;

                                cardProjectList.add(activityCardProject);
                            }
                        }
                        activityCard.setCardProjectList(cardProjectList);
                        activityCard.setProjectItemCount(projectItemCount);

                        activityCardList.add(activityCard);
                    }
                }
            }
        }
        result.setDiscountCardList(discountCardList);
        result.setActivityCardList(activityCardList);

        return result;
    }

    @Override
    public UnionCard getValidUnexpiredByUnionIdAndFanIdAndType(Integer unionId, Integer fanId, Integer type) throws Exception {
        if (unionId == null || fanId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .gt("validity", DateUtil.getCurrentDate())
                .eq("union_id", unionId)
                .eq("fan_id", fanId)
                .eq("type", type);

        return unionCardDao.selectOne(entityWrapper);
    }

    @Override
    public UnionCard getValidUnexpiredByUnionIdAndFanIdAndActivityId(Integer unionId, Integer fanId, Integer activityId) throws Exception {
        if (unionId == null || fanId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .gt("validity", DateUtil.getCurrentDate())
                .eq("union_id", unionId)
                .eq("fan_id", fanId)
                .eq("activity_id", activityId);

        return unionCardDao.selectOne(entityWrapper);
    }

    //********************************************* Base On Business - list ********************************************

    @Override
    public List<UnionCard> listValidByUnionIdAndTypeAndGECreateTime(Integer unionId, Integer type, Date geCreateTime) throws Exception {
        if (unionId == null || type == null || geCreateTime == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("type", type)
                .ge("create_time", geCreateTime);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidByUnionIdAndFanIdAndType(Integer unionId, Integer fanId, Integer type) throws Exception {
        if (unionId == null || fanId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("fan_id", fanId)
                .eq("type", type);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidByUnionIdAndFanIdAndActivityId(Integer unionId, Integer fanId, Integer activityId) throws Exception {
        if (unionId == null || fanId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("fan_id", fanId)
                .eq("activity_id", activityId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidUnexpiredByUnionIdAndFanId(Integer unionId, Integer fanId) throws Exception {
        if (unionId == null || fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .gt("validity", DateUtil.getCurrentDate())
                .eq("union_id", unionId)
                .eq("fan_id", fanId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidUnexpiredByUnionIdAndFanIdAndType(Integer unionId, Integer fanId, Integer type) throws Exception {
        if (unionId == null || fanId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .gt("validity", DateUtil.getCurrentDate())
                .eq("union_id", unionId)
                .eq("fan_id", fanId)
                .eq("type", type);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidUnexpiredByFanIdAndType(Integer fanId, Integer type) throws Exception {
        if (fanId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .gt("validity", DateUtil.getCurrentDate())
                .eq("fan_id", fanId)
                .eq("type", type);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<DiscountCardStatisticsVO> listDiscountCardStatisticsVOByBusId(Integer busId, Integer statisticsType, Date beginTime, Date endTime) throws Exception {
        if (busId == null || statisticsType == null || beginTime == null || endTime == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<DiscountCardStatisticsVO> result = new ArrayList<>();

        List<Date> dateSpotList = new ArrayList<>();
        Date tempDate = new Date(beginTime.getTime());
        switch (statisticsType) {
            case CardConstant.STATISTICS_TYPE_DAY:
                while (tempDate.compareTo(endTime) < 0) {
                    dateSpotList.add(tempDate);
                    tempDate = DateUtil.addDays(tempDate, 1);
                }
                break;
            case CardConstant.STATISTICS_TYPE_MONTH:
                while (tempDate.compareTo(endTime) < 0) {
                    dateSpotList.add(tempDate);
                    tempDate = DateUtil.addMonths(tempDate, 1);
                }
                if (tempDate.compareTo(DateUtil.addMonths(endTime, 1)) < 0) {
                    dateSpotList.add(tempDate);
                }
                break;
            default:
                break;
        }

        List<UnionMember> memberList = unionMemberService.listReadByBusId(busId);
        List<Integer> unionIdList = unionMemberService.getUnionIdList(memberList);
        unionIdList = ListUtil.unique(unionIdList);
        if (ListUtil.isNotEmpty(unionIdList)) {
            for (Integer unionId : unionIdList) {
                UnionMain union = unionMainService.getById(unionId);

                DiscountCardStatisticsVO discountCardStatisticsVO = new DiscountCardStatisticsVO();
                discountCardStatisticsVO.setUnion(union);

                List<DiscountCardStatisticsSpot> spotList = new ArrayList<>();
                List<UnionCard> discountCardList = listValidByUnionIdAndTypeAndGECreateTime(unionId, CardConstant.TYPE_DISCOUNT, beginTime);
                if (ListUtil.isNotEmpty(dateSpotList)) {
                    switch (statisticsType) {
                        case CardConstant.STATISTICS_TYPE_DAY:
                            for (Date dateSpot : dateSpotList) {
                                DiscountCardStatisticsSpot spot = new DiscountCardStatisticsSpot();
                                String strDate = DateUtil.getDateString(dateSpot, DateUtil.DATE_PATTERN);
                                spot.setTime(strDate);

                                Date spotBeginTime = DateUtil.parseDate(strDate + " 00:00:00", DateUtil.DATETIME_PATTERN);
                                Date spotEndTime = DateUtil.parseDate(strDate + " 23:59:59", DateUtil.DATETIME_PATTERN);
                                List<UnionCard> spotDiscountCardList = filterBetweenTime(discountCardList, spotBeginTime, spotEndTime);
                                spot.setNumber(ListUtil.isNotEmpty(spotDiscountCardList) ? spotDiscountCardList.size() : 0);

                                spotList.add(spot);
                            }
                            break;
                        case CardConstant.STATISTICS_TYPE_MONTH:
                            for (Date dateSpot : dateSpotList) {
                                DiscountCardStatisticsSpot spot = new DiscountCardStatisticsSpot();
                                String strTime = DateUtil.getDateString(dateSpot, DateUtil.YEAR_MONTH_PATTERN);
                                spot.setTime(strTime);

                                String strDate = strTime + "-01";
                                Date spotBeginTime = DateUtil.parseDate(strDate + " 00:00:00", DateUtil.DATETIME_PATTERN);
                                Date spotEndTime = DateUtil.addMonths(spotBeginTime, 1);
                                List<UnionCard> spotDiscountCardList = filterBetweenTime(discountCardList, spotBeginTime, spotEndTime);
                                spot.setNumber(ListUtil.isNotEmpty(spotDiscountCardList) ? spotDiscountCardList.size() : 0);

                                spotList.add(spot);
                            }
                            break;
                        default:
                            break;
                    }
                }
                discountCardStatisticsVO.setSpotList(spotList);

                result.add(discountCardStatisticsVO);
            }
        }

        return result;
    }


    @Override
    public List<ActivityCardStatisticsVO> listActivityCardStatisticsVOByBusId(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<ActivityCardStatisticsVO> result = new ArrayList<>();

        List<UnionMember> memberList = unionMemberService.listReadByBusId(busId);
        List<Integer> unionIdList = unionMemberService.getUnionIdList(memberList);
        unionIdList = ListUtil.unique(unionIdList);
        if (ListUtil.isNotEmpty(unionIdList)) {
            for (Integer unionId : unionIdList) {
                ActivityCardStatisticsVO activityCardStatisticsVO = new ActivityCardStatisticsVO();

                UnionMain union = unionMainService.getById(unionId);
                activityCardStatisticsVO.setUnion(union);

                Integer publishCount = unionCardActivityService.countValidLESellBeginTimeByUnionId(unionId);
                activityCardStatisticsVO.setPublishCount(publishCount);

                Integer sellCount = countValidByUnionIdAndType(unionId, CardConstant.TYPE_ACTIVITY);
                activityCardStatisticsVO.setSellCount(sellCount);

                Double sharingSum = unionCardSharingRecordService.sumValidByUnionId(unionId);
                activityCardStatisticsVO.setSharingSum(sharingSum);

                result.add(activityCardStatisticsVO);
            }
        }

        return result;
    }

    //********************************************* Base On Business - save ********************************************

    @Override
    public UnionPayVO saveApplyByBusId(final Integer busId, CardPhoneResponseVO applyPostVO, final IUnionCardApplyService unionCardApplyService) throws Exception {
        if (busId == null || applyPostVO == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        logger.info("办理联盟卡参数信息：busId == " + busId + ", applyPostVO : " + JSONObject.toJSONString(applyPostVO));
        logger.info("办理联盟卡模块：=====>" + unionCardApplyService.getClass().getName());
        // 判断fanId有效性
        UnionCardFan formFan = applyPostVO.getFan();
        if (formFan == null) {
            throw new BusinessException("提交的表单中没有粉丝id信息");
        }
        final Integer fanId = formFan.getId();
        UnionCardFan fan = unionCardFanService.getValidById(fanId);
        if (fan == null) {
            throw new BusinessException("找不到粉丝信息");
        }
        final List<Integer> unionIdList = applyPostVO.getUnionIdList();
        final List<Integer> activityIdList = applyPostVO.getActivityIdList();
        if (ListUtil.isEmpty(unionIdList) && ListUtil.isEmpty(activityIdList)) {
            throw new BusinessException("请选择要办理的联盟卡");
        }

        //编程式事务
        GtJsonResult result = transactionTemplate.execute(new TransactionCallback<GtJsonResult>(){
            Date currentDate = DateUtil.getCurrentDate();
            List<UnionCard> saveDiscountCardList = new ArrayList<>();
            @Override
            public GtJsonResult doInTransaction(TransactionStatus status) {

                // 	办理折扣卡
                if (ListUtil.isNotEmpty(unionIdList)) {
                    String key = RedissonKeyUtil.getUnionCardByFanIdKey(fanId);
                    RLock rLock = redissonClient.getLock(key);
                    try{
                        rLock.lock(5, TimeUnit.SECONDS);
                        for (Integer unionId : unionIdList) {
                            UnionMain union = unionMainService.getValidById(unionId);
                            if (!unionMainService.isUnionValid(union)) {
                                throw new BusinessException("联盟已过期，无法办理折扣卡");
                            }
                            UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
                            if (member == null) {
                                throw new BusinessException("盟员信息有误，无法办理折扣卡");
                            }
                            if (existValidUnexpiredByUnionIdAndFanIdAndType(unionId, fanId, CardConstant.TYPE_DISCOUNT)) {
                                throw new BusinessException("粉丝已办理过折扣卡，无法重复办理");
                            }
                            UnionCard saveDiscountCard = new UnionCard();
                            saveDiscountCard.setDelStatus(CommonConstant.DEL_STATUS_NO);
                            saveDiscountCard.setCreateTime(currentDate);
                            saveDiscountCard.setType(CardConstant.TYPE_DISCOUNT);
                            saveDiscountCard.setMemberId(member.getId());
                            saveDiscountCard.setUnionId(unionId);
                            saveDiscountCard.setFanId(fanId);
                            saveDiscountCard.setName(union.getName() + "折扣卡");
                            saveDiscountCard.setValidity(DateUtil.addYears(currentDate, 10));
                            saveDiscountCardList.add(saveDiscountCard);
                        }
                        if (ListUtil.isNotEmpty(saveDiscountCardList) && ListUtil.isEmpty(activityIdList)) {
                            saveBatch(saveDiscountCardList);
                        }
                    }catch (BaseException e){
                        return GtJsonResult.instanceErrorMsg(e.getErrorMsg());
                    }catch (Exception e){
                        return GtJsonResult.instanceErrorMsg();
                    }finally {
                        rLock.unlock();
                    }

                }

                // 办理活动卡
                // 新增未付款的联盟卡购买记录，并返回支付链接
                try{
                    if (ListUtil.isNotEmpty(activityIdList)) {
                        List<UnionCardRecord> saveCardRecordList = new ArrayList<>();
                        String orderNo = "LM" + ConfigConstant.PAY_MODEL_CARD + DateUtil.getSerialNumber();
                        BigDecimal payMoneySum = BigDecimal.ZERO;
                        for (Integer activityId : activityIdList) {
                            UnionCardActivity activity = unionCardActivityService.getValidById(activityId);
                            if (activity == null) {
                                throw new BusinessException("找不到活动卡信息");
                            }
                            String activityName = activity.getName();
                            Integer unionId = activity.getUnionId();
                            UnionMain union = unionMainService.getValidById(unionId);
                            if (!unionMainService.isUnionValid(union)) {
                                throw new BusinessException("联盟已过期，无法办理" + activityName);
                            }
                            UnionMember member = unionMemberService.getValidReadByBusIdAndUnionId(busId, unionId);
                            if (member == null) {
                                throw new BusinessException("盟员信息有误，无法办理折扣卡");
                            }
                            Integer memberId = member.getId();
                            if (!unionCardProjectService.existValidByUnionIdAndMemberIdAndActivityIdAndStatus(unionId, memberId, activityId, ProjectConstant.STATUS_ACCEPT)) {
                                throw new BusinessException("盟员未参与活动卡");
                            }
                            if (ActivityConstant.STATUS_SELLING != unionCardActivityService.getStatus(activity)) {
                                throw new BusinessException("活动卡不在售卡状态");
                            }
                            Integer activityCardCount = countValidByUnionIdAndActivityId(unionId, activityId);
                            if (activityCardCount >= activity.getAmount()) {
                                throw new BusinessException("活动卡售卡量已到达设定的发行量");
                            }
                            if (existValidUnexpiredByUnionIdAndFanIdAndActivityId(unionId, fanId, activityId)) {
                                throw new BusinessException("已办理过活动卡");
                            }
                            UnionCardRecord saveCardRecord = new UnionCardRecord();
                            saveCardRecord.setDelStatus(CommonConstant.COMMON_NO);
                            saveCardRecord.setCreateTime(currentDate);
                            saveCardRecord.setFanId(fanId);
                            saveCardRecord.setMemberId(memberId);
                            saveCardRecord.setUnionId(unionId);
                            saveCardRecord.setActivityId(activityId);
                            saveCardRecord.setSysOrderNo(orderNo);
                            saveCardRecord.setPayMoney(activity.getPrice());
                            saveCardRecord.setPayStatus(CardConstant.PAY_STATUS_PAYING);

                            saveCardRecordList.add(saveCardRecord);

                            payMoneySum = BigDecimalUtil.add(payMoneySum, activity.getPrice());
                        }
                        UnionPayVO result = unionCardApplyService.unionCardApply(orderNo, BigDecimalUtil.toDouble(payMoneySum), busId, activityIdList);

                        if (ListUtil.isNotEmpty(saveCardRecordList)) {
                            unionCardRecordService.saveBatch(saveCardRecordList);
                            return GtJsonResult.instanceSuccessMsg(result);
                        }
                    }
                }catch (Exception e){
                    status.setRollbackOnly();
                    if(e instanceof BaseException){
                        return GtJsonResult.instanceErrorMsg(((BaseException) e).getErrorMsg());
                    }
                    return GtJsonResult.instanceErrorMsg();
                }
                return null;
            }
        });

        if(result != null){
            if(result.isSuccess()){
                return (UnionPayVO)result.getData();
            }else {
                if(StringUtil.isNotEmpty(result.getErrorMsg())){
                    throw new BusinessException(result.getErrorMsg());
                }else {
                    throw new Exception();
                }
            }
        }
        return null;
    }

    @Override
    public CardPhoneResponseVO checkCardPhoneVO(CardPhoneVO vo) throws Exception {
        if (vo == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // 判断phone和code有效性
        String phone = vo.getPhone();
        if (StringUtil.isEmpty(phone)) {
            throw new BusinessException("手机号不能为空");
        }
        if (!StringUtil.isPhone(phone)) {
            throw new BusinessException("手机号错误，请输入正确的手机号");
        }
        String code = vo.getCode();
        if (StringUtil.isEmpty(code)) {
            throw new BusinessException("验证码不能为空");
        }
        if (!smsService.checkPhoneCode(SmsCodeConstant.APPLY_UNION_CARD_TYPE, code, phone)) {
            throw new BusinessException("验证码错误");
        }
        CardPhoneResponseVO result = new CardPhoneResponseVO();
        // 根据phone判断fan是否存在，如果已存在，则返回；否则，新增并返回
        UnionCardFan fan = unionCardFanService.getOrSaveByPhone(phone);
        result.setFan(fan);

        Integer fanId = fan.getId();
        List<UnionCard> discountCardList = listValidUnexpiredByFanIdAndType(fanId, CardConstant.TYPE_DISCOUNT);
        if (ListUtil.isNotEmpty(discountCardList)) {
            List<Integer> unionIdList = getUnionIdList(discountCardList);
            result.setUnionIdList(unionIdList);
        }

        List<UnionCard> activityCardList = listValidUnexpiredByFanIdAndType(fanId, CardConstant.TYPE_ACTIVITY);
        if (ListUtil.isNotEmpty(activityCardList)) {
            List<Integer> activityIdList = getActivityIdList(activityCardList);
            result.setActivityIdList(activityIdList);
        }

        return result;
    }

    //********************************************* Base On Business - remove ******************************************

    //********************************************* Base On Business - update ******************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateCallbackByOrderNo(final String orderNo, final String socketKey, final String payType, final String payOrderNo, final Integer isSuccess) {
        Map<String, Object> result = new HashMap<>(2);
        if (orderNo == null || payType == null || payOrderNo == null || isSuccess == null) {
            result.put("code", -1);
            result.put("msg", "参数缺少");
            return JSONObject.toJSONString(result);
        }
        final List<UnionCardRecord> recordList;
        try {
            // 判断recordIds有效性
            recordList = unionCardRecordService.listValidByOrderNo(orderNo);
            if (ListUtil.isEmpty(recordList)) {
                throw new BusinessException("没有支付信息");
            }
        } catch (Exception e) {
            logger.error("办理联盟卡支付成功回调错误", e);
            result.put("code", -1);
            result.put("msg", e.getMessage());
            return JSONObject.toJSONString(result);
        }
        String key = RedissonKeyUtil.getUnionCardOrderKey(orderNo);
        RLock rLock = redissonClient.getLock(key);
        rLock.lock(5, TimeUnit.SECONDS);
        try{

        }catch (Exception e){}
        for (UnionCardRecord record : recordList) {
            Integer payStatus = record.getPayStatus();
            if (CardConstant.PAY_STATUS_SUCCESS == payStatus || CardConstant.PAY_STATUS_FAIL == payStatus || CardConstant.PAY_STATUS_RETURN == payStatus) {
                result.put("code", 0);
                result.put("msg", "重复处理");
                return JSONObject.toJSONString(result);
            }
        }
        GtJsonResult gtJsonResult = transactionTemplate.execute(new TransactionCallback<GtJsonResult>(){
            @Override
            public GtJsonResult doInTransaction(TransactionStatus transactionStatus) {
                List<UnionCardRecord> updateRecordList = new ArrayList<>();
                Date currentDate = DateUtil.getCurrentDate();
                Map<Integer, Integer> unionIds = new HashMap<Integer, Integer>();
                List<UnionCard> saveDiscountCardList = new ArrayList<>();
                try{
                    for (UnionCardRecord record : recordList) {

                        // 根据订单信息生成活动卡
                        UnionCardRecord updateRecord = new UnionCardRecord();
                        updateRecord.setId(record.getId());
                        updateRecord.setPayStatus(CommonConstant.COMMON_YES == isSuccess ? CardConstant.PAY_STATUS_SUCCESS : CardConstant.PAY_STATUS_FAIL);
                        if ("0".equals(payType)) {
                            updateRecord.setPayType(CardConstant.PAY_TYPE_WX);
                            updateRecord.setWxOrderNo(payOrderNo);
                        } else {
                            updateRecord.setPayType(CardConstant.PAY_TYPE_ALIPAY);
                            updateRecord.setAlipayOrderNo(payOrderNo);
                        }

                        UnionCard saveActivityCard = new UnionCard();
                        saveActivityCard.setDelStatus(CommonConstant.DEL_STATUS_NO);
                        saveActivityCard.setCreateTime(currentDate);
                        saveActivityCard.setType(CardConstant.TYPE_ACTIVITY);
                        saveActivityCard.setFanId(record.getFanId());
                        saveActivityCard.setMemberId(record.getMemberId());
                        saveActivityCard.setUnionId(record.getUnionId());
                        UnionCardActivity activity = unionCardActivityService.getById(record.getActivityId());
                        if (activity != null) {
                            saveActivityCard.setActivityId(activity.getId());
                            saveActivityCard.setName(activity.getName());
                            saveActivityCard.setValidity(DateUtil.addDays(currentDate, activity.getValidityDay()));
                        }
                        save(saveActivityCard);

                        updateRecord.setCardId(saveActivityCard.getId());
                        updateRecordList.add(updateRecord);
                        // 由于下面使用recordList进行操作，所以这里需要更新它的值
                        record.setCardId(updateRecord.getCardId());

                        // 自动办理折扣卡
                        if (!unionIds.containsKey(record.getUnionId())) {
                            unionIds.put(record.getUnionId(), record.getUnionId());
                            if (!existValidUnexpiredByUnionIdAndFanIdAndType(record.getUnionId(), record.getFanId(), CardConstant.TYPE_DISCOUNT)) {
                                UnionCard saveDiscountCard = new UnionCard();
                                saveDiscountCard.setDelStatus(CommonConstant.DEL_STATUS_NO);
                                saveDiscountCard.setCreateTime(currentDate);
                                saveDiscountCard.setType(CardConstant.TYPE_DISCOUNT);
                                saveDiscountCard.setFanId(record.getFanId());
                                saveDiscountCard.setMemberId(record.getMemberId());
                                saveDiscountCard.setUnionId(record.getUnionId());
                                UnionMain union = unionMainService.getById(record.getUnionId());
                                saveDiscountCard.setName((union != null ? union.getName() : "") + "折扣卡");
                                saveDiscountCard.setValidity(DateUtil.addYears(currentDate, 10));
                                saveDiscountCardList.add(saveDiscountCard);
                            }
                        }
                    }


                    if (ListUtil.isNotEmpty(saveDiscountCardList)) {
                        saveBatch(saveDiscountCardList);
                    }
                    //售卡分成
                    List<UnionCardSharingRecord> saveSharingRecordList = new ArrayList<>();
                    List<UnionBrokerageIncome> saveIncomeList = new ArrayList<>();
                    if (CommonConstant.COMMON_YES == isSuccess) {
                        for (UnionCardRecord record : recordList) {
                            if (unionCardSharingRatioService.existValidByUnionIdAndActivityId(record.getUnionId(), record.getActivityId())) {
                                saveSharingRecordList.addAll(shareByRatio(record));
                            } else {
                                saveSharingRecordList.addAll(shareByAverage(record));
                            }
                        }
                        saveIncomeList = getSaveIncomeList(saveSharingRecordList);
                    }

                    if (ListUtil.isNotEmpty(updateRecordList)) {
                        unionCardRecordService.updateBatch(updateRecordList);
                    }
                    if (ListUtil.isNotEmpty(saveSharingRecordList)) {
                        unionCardSharingRecordService.saveBatch(saveSharingRecordList);
                    }
                    if (ListUtil.isNotEmpty(saveIncomeList)) {
                        unionBrokerageIncomeService.saveBatch(saveIncomeList);
                    }
                }catch (Exception e){
                    transactionStatus.setRollbackOnly();
                    logger.error("办理联盟卡支付成功回调错误", e);
                    return GtJsonResult.instanceErrorMsg(e.getMessage());
                }
                return GtJsonResult.instanceSuccessMsg();
            }
        });

        if(gtJsonResult.isSuccess()){
            // socket通知
            if (StringUtil.isNotEmpty(socketKey)) {
                socketService.socketPaySendMessage(socketKey, isSuccess, null, orderNo);
            }
            result.put("code", 0);
            result.put("msg", "成功");
            return JSONObject.toJSONString(result);
        }else {
            result.put("code", -1);
            result.put("msg", gtJsonResult.getErrorMsg());
            return JSONObject.toJSONString(result);
        }
    }

    private List<UnionCardSharingRecord> shareByRatio(UnionCardRecord record) throws Exception {
        List<UnionCardSharingRatio> ratioList = unionCardSharingRatioService.listValidByUnionIdAndActivityId(record.getUnionId(), record.getActivityId(), "create_time", true);
        boolean isIncludeUnionOwnerId = unionCardSharingRatioService.existUnionOwnerId(ratioList);
        boolean isIncludeInvalidMemberId = unionCardSharingRatioService.existInvalidMemberId(ratioList);
        ratioList = unionCardSharingRatioService.filterInvalidMemberId(ratioList);

        List<UnionCardSharingRecord> result = new ArrayList<>();
        Double payMoney = record.getPayMoney();
        BigDecimal sharedRatioSum = BigDecimal.ZERO;
        BigDecimal sharedMoneySum = BigDecimal.ZERO;
        Date currentDate = DateUtil.getCurrentDate();
        for (UnionCardSharingRatio ratio : ratioList) {
            Double sharingRatio = ratio.getRatio();
            BigDecimal sharingMoney = BigDecimalUtil.multiply(payMoney, sharingRatio);

            UnionCardSharingRecord saveSharingRecord = new UnionCardSharingRecord();
            saveSharingRecord.setDelStatus(CommonConstant.DEL_STATUS_NO);
            saveSharingRecord.setCreateTime(currentDate);
            saveSharingRecord.setSellPrice(payMoney);
            saveSharingRecord.setSharingRatio(sharingRatio);
            saveSharingRecord.setSharingMoney(BigDecimalUtil.toDouble(sharingMoney));
            saveSharingRecord.setSharingMemberId(ratio.getMemberId());
            saveSharingRecord.setFromMemberId(record.getMemberId());
            saveSharingRecord.setActivityId(record.getActivityId());
            saveSharingRecord.setCardId(record.getCardId());
            saveSharingRecord.setFanId(record.getFanId());
            saveSharingRecord.setUnionId(record.getUnionId());
            result.add(saveSharingRecord);

            sharedRatioSum = BigDecimalUtil.add(sharedRatioSum, sharingRatio);
            sharedMoneySum = BigDecimalUtil.add(sharedMoneySum, sharingMoney);
        }
        BigDecimal surplusSharingRatio = BigDecimalUtil.subtract(1.0, sharedRatioSum);
        BigDecimal surplusSharingMoney = BigDecimalUtil.subtract(payMoney, sharedMoneySum);
        if (surplusSharingRatio.doubleValue() > 0 && surplusSharingMoney.doubleValue() > 0) {
            Integer surplusSharingRatioMemberId = (isIncludeUnionOwnerId || isIncludeInvalidMemberId || ListUtil.isEmpty(ratioList))
                    ? unionMemberService.getValidOwnerByUnionId(record.getUnionId()).getId() : ratioList.get(0).getMemberId();

            UnionCardSharingRecord ownerSaveSharingRecord = new UnionCardSharingRecord();
            ownerSaveSharingRecord.setDelStatus(CommonConstant.DEL_STATUS_NO);
            ownerSaveSharingRecord.setCreateTime(currentDate);
            ownerSaveSharingRecord.setSellPrice(payMoney);
            ownerSaveSharingRecord.setSharingRatio(BigDecimalUtil.toDouble(surplusSharingRatio, 4));
            ownerSaveSharingRecord.setSharingMoney(BigDecimalUtil.toDouble(surplusSharingMoney));
            ownerSaveSharingRecord.setSharingMemberId(surplusSharingRatioMemberId);
            ownerSaveSharingRecord.setFromMemberId(record.getMemberId());
            ownerSaveSharingRecord.setActivityId(record.getActivityId());
            ownerSaveSharingRecord.setCardId(record.getCardId());
            ownerSaveSharingRecord.setFanId(record.getFanId());
            ownerSaveSharingRecord.setUnionId(record.getUnionId());
            result.add(ownerSaveSharingRecord);
        }

        return result;
    }

    private List<UnionCardSharingRecord> shareByAverage(UnionCardRecord record) throws Exception {
        List<UnionCardProject> projectList = unionCardProjectService.listValidByUnionIdAndActivityIdAndStatus(record.getUnionId(), record.getActivityId(), ProjectConstant.STATUS_ACCEPT);
        if (ListUtil.isNotEmpty(projectList)) {
            if (!unionCardSharingRatioService.existValidByUnionIdAndActivityId(record.getUnionId(), record.getActivityId())) {
                unionCardSharingRatioService.autoEqualDivisionRatio(projectList);
            }
            return shareByRatio(record);
        } else {
            List<UnionCardSharingRecord> result = new ArrayList<>();

            UnionCardSharingRecord ownerSaveSharingRecord = new UnionCardSharingRecord();
            ownerSaveSharingRecord.setDelStatus(CommonConstant.DEL_STATUS_NO);
            ownerSaveSharingRecord.setCreateTime(DateUtil.getCurrentDate());
            ownerSaveSharingRecord.setSellPrice(record.getPayMoney());
            ownerSaveSharingRecord.setSharingRatio(1.0);
            ownerSaveSharingRecord.setSharingMoney(record.getPayMoney());
            ownerSaveSharingRecord.setSharingMemberId(unionMemberService.getValidOwnerByUnionId(record.getUnionId()).getId());
            ownerSaveSharingRecord.setFromMemberId(record.getMemberId());
            ownerSaveSharingRecord.setActivityId(record.getActivityId());
            ownerSaveSharingRecord.setCardId(record.getCardId());
            ownerSaveSharingRecord.setFanId(record.getFanId());
            ownerSaveSharingRecord.setUnionId(record.getUnionId());
            result.add(ownerSaveSharingRecord);

            return result;
        }
    }

    private List<UnionBrokerageIncome> getSaveIncomeList(List<UnionCardSharingRecord> saveSharingRecordList) throws Exception {
        List<UnionBrokerageIncome> result = new ArrayList<>();

        if (ListUtil.isNotEmpty(saveSharingRecordList)) {
            Date currentDate = DateUtil.getCurrentDate();
            for (UnionCardSharingRecord saveSharingRecord : saveSharingRecordList) {
                Integer sharingMemberId = saveSharingRecord.getSharingMemberId();
                UnionMember sharingMember = unionMemberService.getById(sharingMemberId);

                UnionBrokerageIncome saveIncome = new UnionBrokerageIncome();
                saveIncome.setDelStatus(CommonConstant.DEL_STATUS_NO);
                saveIncome.setCreateTime(currentDate);
                saveIncome.setType(BrokerageConstant.INCOME_TYPE_CARD);
                saveIncome.setMoney(saveSharingRecord.getSharingMoney());
                saveIncome.setBusId(sharingMember.getBusId());
                saveIncome.setMemberId(sharingMemberId);
                saveIncome.setUnionId(saveSharingRecord.getUnionId());
                saveIncome.setCardId(saveSharingRecord.getCardId());
                result.add(saveIncome);
            }
        }

        return result;
    }

    //********************************************* Base On Business - other *******************************************

    @Override
    public Integer countValidByUnionIdAndType(Integer unionId, Integer type) throws Exception {
        if (unionId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("type", type);

        return unionCardDao.selectCount(entityWrapper);
    }

    @Override
    public Integer countValidByUnionIdAndActivityId(Integer unionId, Integer activityId) throws Exception {
        if (unionId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId)
                .eq("activity_id", activityId);

        return unionCardDao.selectCount(entityWrapper);
    }

    @Override
    public boolean existValidUnexpiredByUnionIdAndFanIdAndType(Integer unionId, Integer fanId, Integer type) throws Exception {
        if (unionId == null || fanId == null || type == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .gt("validity", DateUtil.getCurrentDate())
                .eq("union_id", unionId)
                .eq("fan_id", fanId)
                .eq("type", type);

        return unionCardDao.selectCount(entityWrapper) > 0;
    }

    @Override
    public boolean existValidUnexpiredByUnionIdAndFanIdAndActivityId(Integer unionId, Integer fanId, Integer activityId) throws Exception {
        if (unionId == null || fanId == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .gt("validity", DateUtil.getCurrentDate())
                .eq("union_id", unionId)
                .eq("fan_id", fanId)
                .eq("activity_id", activityId);

        return unionCardDao.selectCount(entityWrapper) > 0;
    }

    //********************************************* Base On Business - filter ******************************************

    @Override
    public List<UnionCard> filterByDelStatus(List<UnionCard> unionCardList, Integer delStatus) throws Exception {
        if (delStatus == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardList)) {
            for (UnionCard unionCard : unionCardList) {
                if (delStatus.equals(unionCard.getDelStatus())) {
                    result.add(unionCard);
                }
            }
        }

        return result;
    }

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

    @Override
    public List<UnionCard> filterByActivityId(List<UnionCard> cardList, Integer activityId) throws Exception {
        if (cardList == null || activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = new ArrayList<>();
        for (UnionCard card : cardList) {
            if (activityId.equals(card.getActivityId())) {
                result.add(card);
            }
        }

        return result;
    }

    @Override
    public List<UnionCard> filterExpired(List<UnionCard> cardList) throws Exception {
        if (cardList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(cardList)) {
            Date currentDate = DateUtil.getCurrentDate();
            for (UnionCard card : cardList) {
                if (currentDate.compareTo(card.getValidity()) < 0) {
                    result.add(card);
                }
            }
        }

        return result;
    }

    @Override
    public List<UnionCard> filterBetweenTime(List<UnionCard> cardList, Date optBeginTime, Date optEndTime) throws Exception {
        if (cardList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(cardList)) {
            for (UnionCard card : cardList) {
                if (optBeginTime != null && optBeginTime.compareTo(card.getCreateTime()) > 0) {
                    continue;
                }
                if (optEndTime != null && optEndTime.compareTo(card.getCreateTime()) < 0) {
                    continue;
                }
                result.add(card);
            }
        }

        return result;
    }

    //********************************************* Object As a Service - get ******************************************

    @Override
    public UnionCard getById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardDao.selectById(id);
    }

    @Override
    public UnionCard getValidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("id", id);

        return unionCardDao.selectOne(entityWrapper);
    }

    @Override
    public UnionCard getInvalidById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("id", id);

        return unionCardDao.selectOne(entityWrapper);
    }

    //********************************************* Object As a Service - list *****************************************

    @Override
    public List<Integer> getIdList(List<UnionCard> unionCardList) throws Exception {
        if (unionCardList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardList)) {
            for (UnionCard unionCard : unionCardList) {
                result.add(unionCard.getId());
            }
        }

        return result;
    }

    @Override
    public List<Integer> getUnionIdList(List<UnionCard> unionCardList) throws Exception {
        if (unionCardList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardList)) {
            for (UnionCard unionCard : unionCardList) {
                result.add(unionCard.getUnionId());
            }
        }

        return result;
    }

    @Override
    public List<Integer> getActivityIdList(List<UnionCard> unionCardList) throws Exception {
        if (unionCardList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<Integer> result = new ArrayList<>();
        if (ListUtil.isNotEmpty(unionCardList)) {
            for (UnionCard unionCard : unionCardList) {
                result.add(unionCard.getActivityId());
            }
        }

        return result;
    }

    @Override
    public List<UnionCard> listByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("member_id", memberId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("member_id", memberId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listInvalidByMemberId(Integer memberId) throws Exception {
        if (memberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("member_id", memberId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("union_id", unionId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("union_id", unionId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listInvalidByUnionId(Integer unionId) throws Exception {
        if (unionId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("union_id", unionId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("fan_id", fanId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("fan_id", fanId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listInvalidByFanId(Integer fanId) throws Exception {
        if (fanId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("fan_id", fanId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("activity_id", activityId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listValidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO)
                .eq("activity_id", activityId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listInvalidByActivityId(Integer activityId) throws Exception {
        if (activityId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_YES)
                .eq("activity_id", activityId);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public List<UnionCard> listByIdList(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        EntityWrapper<UnionCard> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("id", idList).eq(ListUtil.isEmpty(idList), "id", null);

        return unionCardDao.selectList(entityWrapper);
    }

    @Override
    public Page pageSupport(Page page, EntityWrapper<UnionCard> entityWrapper) throws Exception {
        if (page == null || entityWrapper == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        return unionCardDao.selectPage(page, entityWrapper);
    }
    //********************************************* Object As a Service - save *****************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(UnionCard newUnionCard) throws Exception {
        if (newUnionCard == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardDao.insert(newUnionCard);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<UnionCard> newUnionCardList) throws Exception {
        if (newUnionCardList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardDao.insertBatch(newUnionCardList);
    }

    //********************************************* Object As a Service - remove ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Integer id) throws Exception {
        if (id == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        UnionCard removeUnionCard = new UnionCard();
        removeUnionCard.setId(id);
        removeUnionCard.setDelStatus(CommonConstant.DEL_STATUS_YES);
        unionCardDao.updateById(removeUnionCard);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBatchById(List<Integer> idList) throws Exception {
        if (idList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        List<UnionCard> removeUnionCardList = new ArrayList<>();
        for (Integer id : idList) {
            UnionCard removeUnionCard = new UnionCard();
            removeUnionCard.setId(id);
            removeUnionCard.setDelStatus(CommonConstant.DEL_STATUS_YES);
            removeUnionCardList.add(removeUnionCard);
        }
        unionCardDao.updateBatchById(removeUnionCardList);
    }

    //********************************************* Object As a Service - update ***************************************

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UnionCard updateUnionCard) throws Exception {
        if (updateUnionCard == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardDao.updateById(updateUnionCard);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<UnionCard> updateUnionCardList) throws Exception {
        if (updateUnionCardList == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        unionCardDao.updateBatchById(updateUnionCardList);
    }

}
