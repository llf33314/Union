package com.gt.union.opportunity.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.gt.api.bean.session.WxPublicUsers;
import com.gt.union.api.client.user.IBusUserService;
import com.gt.union.brokerage.constant.BrokerageConstant;
import com.gt.union.brokerage.entity.UnionBrokerageIncome;
import com.gt.union.brokerage.entity.UnionBrokeragePay;
import com.gt.union.brokerage.entity.UnionBrokerageRatio;
import com.gt.union.brokerage.service.IUnionBrokerageIncomeService;
import com.gt.union.brokerage.service.IUnionBrokeragePayService;
import com.gt.union.brokerage.service.IUnionBrokerageRatioService;
import com.gt.union.common.amqp.entity.PhoneMessage;
import com.gt.union.common.amqp.sender.PhoneMessageSender;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.*;
import com.gt.union.main.entity.UnionMain;
import com.gt.union.main.service.IUnionMainService;
import com.gt.union.member.constant.MemberConstant;
import com.gt.union.member.entity.UnionMember;
import com.gt.union.member.service.IUnionMemberService;
import com.gt.union.opportunity.constant.OpportunityConstant;
import com.gt.union.opportunity.entity.UnionOpportunity;
import com.gt.union.opportunity.mapper.UnionOpportunityMapper;
import com.gt.union.opportunity.service.IUnionOpportunityService;
import com.gt.union.opportunity.vo.UnionOpportunityBrokerageVO;
import com.gt.union.opportunity.vo.UnionOpportunityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

/**
 * <p>
 * 商机推荐 服务实现类
 * </p>
 *
 * @author linweicong
 * @since 2017-09-07
 */
@Service
public class UnionOpportunityServiceImpl extends ServiceImpl<UnionOpportunityMapper, UnionOpportunity> implements IUnionOpportunityService {

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private IUnionMemberService unionMemberService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private PhoneMessageSender phoneMessageSender;

    @Autowired
    private IUnionBrokerageRatioService unionBrokerageRatioService;

    @Autowired
    private IBusUserService busUserService;

    @Autowired
    private IUnionBrokerageIncomeService unionBrokerageIncomeService;

    @Autowired
    private IUnionBrokeragePayService unionBrokeragePayService;

    @Autowired
    private UnionOpportunityMapper unionOpportunityMapper;

    @Value("${union.url}")
    private String unionUrl;

    @Value("${wx.duofen.busId}")
    private Integer duofenBusId;

    @Value("${union.encryptKey}")
    private String encryptKey;

    @Override
    public Page listToMy(Page page, final Integer busId, final Integer unionId, final String isAccept, final String clientName, final String clientPhone) throws Exception {
        if (page == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" o");
                sbSqlSegment.append(" LEFT JOIN t_union_member m ON o.to_member_id = m.id ")
                        .append(" LEFT JOIN t_union_main um ON um.id = m.union_id ")
                        .append(" WHERE m.bus_id = ").append(busId)
                        .append("    AND o.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("    AND m.del_status = ").append(CommonConstant.DEL_STATUS_NO);
                if (unionId != null) {
                    sbSqlSegment.append(" AND m.union_id = ").append(unionId);
                }
                if (StringUtil.isNotEmpty(isAccept)) {
                    String[] isAcceptArray = isAccept.split(";");
                    sbSqlSegment.append(" AND (");
                    for (int i = 0, length = isAcceptArray.length; i < length; i++) {
                        sbSqlSegment.append(i == 0 ? "" : " OR ").append(" o.is_accept = ").append(isAcceptArray[i]);
                    }
                    sbSqlSegment.append(" ) ");
                }
                if (StringUtil.isNotEmpty(clientName)) {
                    sbSqlSegment.append(" AND o.client_name LIKE '%").append(clientName).append("%' ");
                }
                if (StringUtil.isNotEmpty(clientPhone)) {
                    sbSqlSegment.append(" AND o.client_phone LIKE '%").append(clientPhone).append("%' ");
                }
                sbSqlSegment.append(" ORDER BY o.is_accept ASC, o.id DESC ");
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" o.id opportunityId ")
                .append(" , o.client_name clientName ")
                .append(" , o.client_phone clientPhone ")
                .append(" , o.business_msg businessMsg ")
                .append(" , m.enterprise_name enterpriseName ")
                .append(" , um.id unionId ")
                .append(" , um.name name ")
                .append(" , o.is_accept isAccept ");
        wrapper.setSqlSelect(sbSqlSelect.toString());

        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public Page listFromMy(Page page, final Integer busId, final Integer unionId, final String isAccept, final String clientName, final String clientPhone) throws Exception {
        if (page == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Wrapper wrapper = new Wrapper() {
            @Override
            public String getSqlSegment() {
                StringBuilder sbSqlSegment = new StringBuilder(" o");
                sbSqlSegment.append(" LEFT JOIN t_union_member m ON o.from_member_id = m.id ")
                        .append(" LEFT JOIN t_union_main um ON um.id = m.union_id ")
                        .append(" WHERE m.bus_id = ").append(busId)
                        .append("    AND o.del_status = ").append(CommonConstant.DEL_STATUS_NO)
                        .append("    AND m.del_status = ").append(CommonConstant.DEL_STATUS_NO);
                if (unionId != null) {
                    sbSqlSegment.append(" AND m.union_id = ").append(unionId);
                }
                if (StringUtil.isNotEmpty(isAccept)) {
                    String[] isAcceptArray = isAccept.split(";");
                    sbSqlSegment.append(" AND (");
                    for (int i = 0, length = isAcceptArray.length; i < length; i++) {
                        sbSqlSegment.append(i == 0 ? "" : " OR ").append(" o.is_accept = ").append(isAcceptArray[i]);
                    }
                    sbSqlSegment.append(" ) ");
                }
                if (StringUtil.isNotEmpty(clientName)) {
                    sbSqlSegment.append(" AND o.client_name LIKE '%").append(clientName).append("%' ");
                }
                if (StringUtil.isNotEmpty(clientPhone)) {
                    sbSqlSegment.append(" AND o.client_phone LIKE '%").append(clientPhone).append("%' ");
                }
                sbSqlSegment.append(" ORDER BY o.is_accept ASC, o.id DESC ");
                return sbSqlSegment.toString();
            }
        };
        StringBuilder sbSqlSelect = new StringBuilder(" o.id opportunityId ")
                .append(" , o.client_name clientName ")
                .append(" , o.client_phone clientPhone ")
                .append(" , o.business_msg businessMsg ")
                .append(" , m.enterprise_name enterpriseName ")
                .append(" , um.id unionId ")
                .append(" , um.name name ")
                .append(" , o.is_accept isAccept ");
        wrapper.setSqlSelect(sbSqlSelect.toString());

        return this.selectMapsPage(page, wrapper);
    }

    @Override
    public void updateByIdAndIsAccept(Integer busId, Integer id, Integer isAccept, Double acceptPrice) throws Exception {
        if (busId == null || id == null || isAccept == null || acceptPrice == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        // （1）判断审核的商机是否存在
        UnionOpportunity unionOpportunity = this.selectById(id);
        if (unionOpportunity == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        // （2）判断当前商家是否是商机的接收者
        UnionMember member = unionMemberService.getById(unionOpportunity.getToMemberId());
        if (member == null) {
            throw new BusinessException(CommonConstant.OPERATE_ERROR);
        }
        if (!(member.getStatus().equals(MemberConstant.STATUS_IN) || member.getStatus().equals(MemberConstant.STATUS_APPLY_OUT))) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        if (!member.getBusId().equals(busId)) {
            throw new BusinessException(CommonConstant.OPERATE_ERROR);
        }
        // 判断该商机关联的联盟是否有效
        this.unionMainService.checkUnionMainValid(member.getUnionId());

        // （3）判断商机是否是未处理状态
        if (!unionOpportunity.getIsAccept().equals(OpportunityConstant.ACCEPT_CONFIRM_NON)) {
            throw new BusinessException("当前商机已处理");
        }

        switch (isAccept) {
            case OpportunityConstant.ACCEPT_CONFIRM_YES:
                // （4-1）接受商机
                unionOpportunity.setAcceptPrice(acceptPrice);
                this.acceptOpportunity(unionOpportunity);
                break;
            case OpportunityConstant.ACCEPT_CONFIRM_NO:
                // （4-2）拒绝商机
                this.refuseOpportunity(unionOpportunity);
                break;
            default:
                throw new ParamException(CommonConstant.PARAM_ERROR);
        }
    }

    @Transactional
    @Override
    public void save(UnionOpportunityVO vo) throws Exception {
        //判断联盟是否有效
        this.unionMainService.checkUnionMainValid(vo.getUnionId());

        UnionMember fromUnionMember = this.unionMemberService.getByBusIdAndUnionId(vo.getBusId(), vo.getUnionId());
        if (fromUnionMember == null) {
            throw new BusinessException(CommonConstant.UNION_MEMBER_READ_REJECT);
        }
        //判断推荐商家是否是联盟的有效成员
        //if (!this.unionMemberService.hasUnionMemberAuthority(fromUnionMember)) {
        //throw new BusinessException(MemberConstant.UNION_MEMBER_NON_AUTHORITY_MSG);
        //}
        //判断被推荐商家是否是联盟的有效成员
        UnionMember toUnionMember = this.unionMemberService.selectById(vo.getToMemberId());
        //if(toUnionMember == null || this.unionMemberService.hasUnionMemberAuthority(toUnionMember)
        //|| this.busUserService.isBusUserValid(toUnionMember.getBusId())){
        //throw new BusinessException("您推荐的盟员是无效盟员");
        //}
        //保存推荐主表信息
        UnionOpportunity opportunity = new UnionOpportunity();
        opportunity.setCreatetime(new Date());
        opportunity.setDelStatus(CommonConstant.DEL_STATUS_NO);
        opportunity.setIsAccept(OpportunityConstant.ACCEPT_CONFIRM_NON);
        opportunity.setFromMemberId(fromUnionMember.getId());
        opportunity.setToMemberId(toUnionMember.getId());
        opportunity.setBusinessMsg(vo.getBusinessMsg());
        opportunity.setClientName(vo.getClientName());
        opportunity.setClientPhone(vo.getClientPhone());
        opportunity.setType(OpportunityConstant.OPPORTUNITY_TYPE_OFFLINE);//线下
        this.insert(opportunity);
        //发送短信通过
        UnionMain unionMain = this.unionMainService.getById(vo.getUnionId());
        String phone = StringUtil.isNotEmpty(toUnionMember.getNotifyPhone()) ? toUnionMember.getNotifyPhone() : toUnionMember.getDirectorPhone();
        StringBuilder sbContent = new StringBuilder("\"").append(unionMain.getName()).append("\"的盟员\"")
                .append(fromUnionMember.getEnterpriseName()).append("\"为你推荐了客户，请到商机消息处查看。客户信息：")
                .append(opportunity.getClientName()).append("，").append(opportunity.getClientPhone()).append("，").append(opportunity.getBusinessMsg());
        PhoneMessage phoneMessage = new PhoneMessage(vo.getBusId(), phone, sbContent.toString());
        this.phoneMessageSender.sendMsg(phoneMessage);
    }


    /**
     * 拒绝商机
     *
     * @param unionOpportunity
     * @throws Exception
     */
    @Transactional
    private void refuseOpportunity(UnionOpportunity unionOpportunity) throws Exception {
        // （2）判断该商机关联的推荐方是否有效
        Integer fromBusId = unionOpportunity.getFromMemberId();
        //if (!this.unionMemberService.hasUnionMemberAuthority(fromBusId)) {
        //throw new BusinessException("该商机的推荐方信息已无效");
        //}
        // （3）判断该商机的接收方是否对推荐方设置了商机佣金比
        UnionBrokerageRatio unionBrokerageRatio = this.unionBrokerageRatioService.getByFromMemberIdAndToMemberId(unionOpportunity.getFromMemberId(), unionOpportunity.getToMemberId());
        if (unionBrokerageRatio == null) {
            throw new BusinessException("未对该商机的推荐方设置商机佣金比");
        }

        // （4）保存更新信息
        UnionOpportunity opportunity = new UnionOpportunity();
        opportunity.setId(unionOpportunity.getId());
        opportunity.setAcceptPrice(unionOpportunity.getAcceptPrice());
        opportunity.setIsAccept(OpportunityConstant.ACCEPT_CONFIRM_YES);
        opportunity.setBrokeragePrice(BigDecimalUtil.multiply(unionOpportunity.getAcceptPrice(), unionBrokerageRatio.getRatio()).doubleValue());
        this.updateById(opportunity);

        //接受的盟员
        Integer toMemberId = unionOpportunity.getToMemberId();
        UnionMember toMember = unionMemberService.getById(toMemberId);
        //推荐的盟员
        Integer fromMemberId = unionOpportunity.getFromMemberId();
        UnionMember fromMember = unionMemberService.getById(fromMemberId);
        // （5）短信通知
        UnionMain main = this.unionMainService.getById(toMember.getUnionId());
        String phone = StringUtil.isNotEmpty(fromMember.getNotifyPhone()) ? fromMember.getNotifyPhone() : fromMember.getDirectorPhone();
        StringBuilder sbContent = new StringBuilder("\"").append(main.getName()).append("\"的盟员\"")
                .append(toMember.getEnterpriseName()).append("\"已接收您推荐的商机消息。客户信息：")
                .append(unionOpportunity.getClientName()).append("，")
                .append(unionOpportunity.getClientPhone()).append("，")
                .append(unionOpportunity.getBusinessMsg());
        PhoneMessage phoneMessage = new PhoneMessage(fromMember.getBusId(), phone, sbContent.toString());
        this.phoneMessageSender.sendMsg(phoneMessage);
    }


    /**
     * 接收商机
     *
     * @param unionOpportunity
     * @throws Exception
     */
    @Transactional
    private void acceptOpportunity(UnionOpportunity unionOpportunity) throws Exception {

        // （2）判断该商机关联的推荐方是否有效
        Integer fromBusId = unionOpportunity.getFromMemberId();
        //if (!this.unionMemberService.hasUnionMemberAuthority(fromBusId)) {
        //throw new BusinessException("该商机的推荐方信息已无效");
        //}

        // （4）保存更新信息
        UnionOpportunity opportunity = new UnionOpportunity();
        opportunity.setId(unionOpportunity.getId());
        opportunity.setIsAccept(OpportunityConstant.ACCEPT_CONFIRM_NO);
        this.updateById(opportunity);

        //接受的盟员
        Integer toMemberId = unionOpportunity.getToMemberId();
        UnionMember toMember = unionMemberService.getById(toMemberId);
        //推荐的盟员
        Integer fromMemberId = unionOpportunity.getFromMemberId();
        UnionMember fromMember = unionMemberService.getById(fromMemberId);
        // （5）短信通知
        UnionMain main = this.unionMainService.getById(toMember.getUnionId());
        String phone = StringUtil.isNotEmpty(fromMember.getNotifyPhone()) ? fromMember.getNotifyPhone() : fromMember.getDirectorPhone();
        StringBuilder sbContent = new StringBuilder("\"").append(main.getName()).append("\"的盟员\"")
                .append(toMember.getEnterpriseName()).append("\"已拒绝您推荐的商机消息。客户信息：")
                .append(unionOpportunity.getClientName()).append("，")
                .append(unionOpportunity.getClientPhone()).append("，")
                .append(unionOpportunity.getBusinessMsg());
        PhoneMessage phoneMessage = new PhoneMessage(fromMember.getBusId(), phone, sbContent.toString());
        this.phoneMessageSender.sendMsg(phoneMessage);
    }

    @Override
    public Map<String, Object> payOpportunityQRCode(Integer busId, String ids) throws Exception {
        Map<String, Object> data = new HashMap<String, Object>();
        if (StringUtil.isEmpty(ids)) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        String[] arrs = ids.split(",");
        if (arrs.length == 0) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        wrapper.in("id", arrs);
        List<UnionOpportunity> list = this.selectList(wrapper);
        if (ListUtil.isEmpty(list)) {
            throw new BusinessException(CommonConstant.PARAM_ERROR);
        }
        double totalFee = 0;
        WxPublicUsers publicUser = busUserService.getWxPublicUserByBusId(duofenBusId);
        for (UnionOpportunity opportunity : list) {
            if (CommonUtil.isEmpty(opportunity.getIsAccept()) || opportunity.getIsAccept() != OpportunityConstant.ACCEPT_CONFIRM_NON) {
                throw new BusinessException("不可支付未接受的商机");
            }
            UnionBrokerageIncome brokerageIncome = unionBrokerageIncomeService.getByUnionOpportunityId(opportunity.getId());
            if (brokerageIncome != null) {
                throw new BusinessException("该商机已支付");
            }
            totalFee = new BigDecimal(opportunity.getBrokeragePrice()).add(new BigDecimal(totalFee)).doubleValue();
        }
        if (totalFee <= 0) {
            throw new BusinessException("支付金额有误");
        }
        String orderNo = OpportunityConstant.ORDER_PREFIX + System.currentTimeMillis();
        String only = DateTimeKit.getDateTime(new Date(), DateTimeKit.yyyyMMddHHmmss);
        data.put("totalFee", totalFee);
        data.put("busId", duofenBusId);
        data.put("sourceType", 1);//是否墨盒支付
        data.put("payWay", 1);//系统判断支付方式
        data.put("isreturn", 0);//0：不需要同步跳转
        data.put("model", ConfigConstant.PAY_MODEL);
        String encrypt = EncryptUtil.encrypt(encryptKey, ids);
        encrypt = URLEncoder.encode(encrypt, "UTF-8");
        data.put("notifyUrl", unionUrl + "/unionOpportunity/79B4DE7C/paymentSuccess/" + encrypt + "/" + only);
        data.put("orderNum", orderNo);//订单号
        data.put("payBusId", busId);//支付的商家id
        data.put("isSendMessage", 0);//不推送
        data.put("appid", publicUser.getAppid());//appid
        data.put("desc", "联盟商机推荐");
        data.put("appidType", 0);//公众号
        data.put("only", only);
        String paramKey = RedisKeyUtil.getRecommendPayParamKey(only);
        String statusKey = RedisKeyUtil.getRecommendPayStatusKey(only);
        redisCacheUtil.set(paramKey, JSON.toJSONString(data), 360l);//5分钟
        redisCacheUtil.set(statusKey, ConfigConstant.USER_ORDER_STATUS_001, 300l);//等待扫码状态
        return data;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void payOpportunitySuccess(String encrypt, String only) throws Exception {
        //解密参数
        String ids = EncryptUtil.decrypt(encryptKey, encrypt);
        String paramKey = RedisKeyUtil.getRecommendPayParamKey(only);
        Object obj = redisCacheUtil.get(paramKey);
        Map<String, Object> result = JSONObject.parseObject(obj.toString(), Map.class);
        String statusKey = RedisKeyUtil.getRecommendPayStatusKey(only);
        String[] arrs = ids.split(",");
        EntityWrapper wrapper = new EntityWrapper<>();
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        wrapper.in("id", arrs);
        List<UnionOpportunity> list = this.selectList(wrapper);
        String orderNo = result.get("orderNum").toString();
        //插入佣金收入列表
        insertBatchByList(list, orderNo);
        redisCacheUtil.remove(paramKey);
        redisCacheUtil.set(statusKey, ConfigConstant.USER_ORDER_STATUS_003, 60l);//支付成功
    }

    @Override
    public Page listBrokerageFromMy(Page page, final Integer fromBusId, final Integer toMemberId, final Integer unionId, final String status, final String clientName, final String clientPhone) throws Exception {
        if (page == null || fromBusId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> members = unionMemberService.listReadByBusId(fromBusId);
        final List<Integer> memberIds = new ArrayList<Integer>();
        for (UnionMember member : members) {
            memberIds.add(member.getId());
        }
        //结算状态
        Integer incomeStatus = 0;//表示结算、未结算一起
        if (StringUtil.isNotEmpty(status)) {
            String[] arrs = status.split(";");
            if (arrs.length == 1) {
                incomeStatus = Integer.valueOf(arrs[arrs.length]);
            }
        }

        List<UnionOpportunityBrokerageVO> list = unionOpportunityMapper.listBrokerageFromMy(members, toMemberId, unionId, incomeStatus, clientName, clientPhone, page);
        for (UnionOpportunityBrokerageVO vo : list) {
            if (CommonUtil.isEmpty(vo.getIncomeId())) {//未结算
                vo.setStatus(2);
            } else {//结算
                vo.setStatus(1);
            }
        }
        page.setRecords(list);
        return page;
    }

    @Override
    public Page listBrokerageToMy(Page page, Integer toBusId, Integer fromMemberId, Integer unionId, String status, String clientName, String clientPhone) throws Exception {
        if (page == null || toBusId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> members = unionMemberService.listReadByBusId(toBusId);
        if (ListUtil.isEmpty(members)) {
            return page;
        }
        final List<Integer> memberIds = new ArrayList<Integer>();
        for (UnionMember member : members) {
            memberIds.add(member.getId());
        }
        //结算状态
        Integer incomeStatus = 0;//表示结算、未结算一起
        if (StringUtil.isNotEmpty(status)) {
            String[] arrs = status.split(";");
            if (arrs.length == 1) {
                incomeStatus = Integer.valueOf(arrs[arrs.length]);
            }
        }

        List<UnionOpportunityBrokerageVO> list = unionOpportunityMapper.listBrokerageToMy(members, fromMemberId, unionId, incomeStatus, clientName, clientPhone, page);
        for (UnionOpportunityBrokerageVO vo : list) {
            if (CommonUtil.isEmpty(vo.getIncomeId())) {//未结算
                vo.setStatus(2);
            } else {//结算
                vo.setStatus(1);
            }
        }
        page.setRecords(list);
        return page;
    }

    @Override
    public Page listPayDetail(Page page, Integer busId, Integer unionId) throws Exception {
        if (page == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> members = unionMemberService.listReadByBusId(busId);
        if (ListUtil.isEmpty(members)) {
            return page;
        }
        List<Map<String, Object>> list = unionOpportunityMapper.listPayDetail(members, unionId, page);
        page.setRecords(list);
        return page;
    }

    @Override
    public List<Map<String, Object>> listPayDetailParticularByUnionIdAndMemberId(Integer memberId, Integer myMemberId) throws Exception {
        if (memberId == null || myMemberId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<Map<String, Object>> list = unionOpportunityMapper.listPayDetailParticularByUnionIdAndMemberId(memberId, myMemberId);
        return list;
    }

    @Override
    public Map<String, Object> getStatisticData(Integer unionId, Integer busId) throws Exception {
        if (unionId == null || busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Map<String, Object> resultMap = new HashMap<>();
        UnionMember member = unionMemberService.getByBusIdAndUnionId(busId, unionId);
        //未结算佣金收入
        Double uncheckBrokerageIncome = getBrokerageIncome(member.getId(), OpportunityConstant.BROKERAGE_PAY_NO);
        resultMap.put("uncheckBrokerageIncome", uncheckBrokerageIncome);
        //已结算佣金收入
        Double confirmBrokerageIncome = getBrokerageIncome(member.getId(), OpportunityConstant.BROKERAGE_PAY_YES);
        resultMap.put("confirmBrokerageIncome", confirmBrokerageIncome);
        //总佣金收入
        BigDecimal brokerageIncomeSum = BigDecimalUtil.add(uncheckBrokerageIncome, confirmBrokerageIncome);
        resultMap.put("brokerageIncomeSum", brokerageIncomeSum);
        //收入百分比
        if (brokerageIncomeSum.doubleValue() != 0.0D) {
            //未结算佣金收入占总佣金收入的百分比
            BigDecimal bdUncheckBrokerageIncomePercent = BigDecimalUtil.divide(uncheckBrokerageIncome, brokerageIncomeSum, 4);
            String uncheckBrokerageIncomePercent = DoubleUtil.formatPercent(bdUncheckBrokerageIncomePercent.doubleValue());
            resultMap.put("uncheckBrokerageIncomePercent", uncheckBrokerageIncomePercent);
            //已结算佣金收入占总佣金收入的百分比
            BigDecimal bdConfirmBrokerageIncomePercent = BigDecimalUtil.subtract(Double.valueOf(1.00), bdUncheckBrokerageIncomePercent, 4);
            String confirmBrokerageIncomePercent = DoubleUtil.formatPercent(bdConfirmBrokerageIncomePercent.doubleValue());
            resultMap.put("confirmBrokerageIncomePercent", confirmBrokerageIncomePercent);
        }

        //未结算支出佣金
        Double uncheckBrokerageExpense = getBrokerageExpense(member.getId(), OpportunityConstant.BROKERAGE_PAY_NO);
        resultMap.put("uncheckBrokerageExpense", uncheckBrokerageExpense);
        //已结算支出佣金
        Double confirmBrokerageExpense = getBrokerageExpense(member.getId(), OpportunityConstant.BROKERAGE_PAY_YES);
        resultMap.put("confirmBrokerageExpense", confirmBrokerageExpense);
        //总支出佣金
        BigDecimal brokerageExpenseSum = BigDecimalUtil.add(uncheckBrokerageExpense, confirmBrokerageExpense);
        resultMap.put("brokerageExpenseSum", brokerageExpenseSum);
        //支出百分比
        if (brokerageExpenseSum.doubleValue() != 0.0D) {
            //未结算支出佣金占总支出佣金的百分比
            BigDecimal bdUncheckBrokerageExpensePercent = BigDecimalUtil.divide(uncheckBrokerageExpense, brokerageExpenseSum, 4);
            String uncheckBrokerageExpensePercent = DoubleUtil.formatPercent(bdUncheckBrokerageExpensePercent.doubleValue());
            resultMap.put("uncheckBrokerageExpensePercent", uncheckBrokerageExpensePercent);
            //已结算支出佣金占总支出佣金的百分比
            BigDecimal bdConfirmBrokerageExpensePercent = BigDecimalUtil.subtract(Double.valueOf(1.00), bdUncheckBrokerageExpensePercent, 4);
            String confirmBrokerageExpensePercent = DoubleUtil.formatPercent(bdConfirmBrokerageExpensePercent.doubleValue());
            resultMap.put("confirmBrokerageExpensePercent", confirmBrokerageExpensePercent);
        }

        //获取一周统计数据
        List<Map<String, Map<String, Double>>> brokerageInWeek = getBrokerageInWeek(member.getId());
        resultMap.put("brokerageInWeek", brokerageInWeek);
        return resultMap;
    }

    @Override
    public Double sumAcceptFromMy(Integer busId, Integer isAccept) throws Exception {
        if (busId == null || isAccept == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> members = unionMemberService.listReadByBusId(busId);
        if (ListUtil.isEmpty(members)) {
            return 0d;
        }
        List<Integer> memberIds = new ArrayList<Integer>();
        for (UnionMember member : members) {
            memberIds.add(member.getId());
        }
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.in("from_member_id", memberIds.toArray());
        wrapper.eq("is_accept", OpportunityConstant.ACCEPT_CONFIRM_YES);
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        if (isAccept == 0) {//未支付
            wrapper.notExists("select i.id from t_union_brokerage_income i where t_union_opportunity.id = i.opportunity_id");
        } else if (isAccept == 1) {//已支付
            wrapper.exists("select i.id from t_union_brokerage_income i where t_union_opportunity.id = i.opportunity_id");
        } else {
            return 0d;
        }
        wrapper.setSqlSelect("IFNULL(SUM(brokerage_price), 0) money");
        Map<String, Object> data = this.selectMap(wrapper);
        return Double.valueOf(data.get("money").toString());

    }

    @Override
    public Double sumAcceptFromMyByUnionId(Integer unionId, Integer busId, Integer isAccept) throws Exception {
        if (busId == null || isAccept == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }

        //List<UnionMember> members = unionMemberService.getByUnionIdAndBusIdWidthOutDelStatus(unionId,busId);
        List<UnionMember> members = null;
        if (ListUtil.isEmpty(members)) {
            return 0d;
        }
        List<Integer> memberIds = new ArrayList<Integer>();
        for (UnionMember member : members) {
            memberIds.add(member.getId());
        }
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.in("from_member_id", memberIds.toArray());
        wrapper.eq("is_accept", OpportunityConstant.ACCEPT_CONFIRM_YES);
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
        if (isAccept == 0) {//未支付
            wrapper.notExists("select i.id from t_union_brokerage_income i where t_union_opportunity.id = i.opportunity_id");
        } else if (isAccept == 1) {//已支付
            wrapper.exists("select i.id from t_union_brokerage_income i where t_union_opportunity.id = i.opportunity_id");
        } else {
            return 0d;
        }
        wrapper.setSqlSelect("IFNULL(SUM(brokerage_price), 0) money");
        Map<String, Object> data = this.selectMap(wrapper);
        return Double.valueOf(data.get("money").toString());
    }

    @Override
    public Double sumFromMyInBadDebt(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        List<UnionMember> members = unionMemberService.listReadByBusId(busId);
        if (ListUtil.isEmpty(members)) {
            return 0d;
        }
        List<Integer> memberIds = new ArrayList<Integer>();
        for (UnionMember member : members) {
            memberIds.add(member.getId());
        }
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.in("from_member_id", memberIds.toArray());
        wrapper.eq("is_accept", OpportunityConstant.ACCEPT_CONFIRM_YES);
        wrapper.eq("del_status", CommonConstant.DEL_STATUS_YES);
        wrapper.notExists("select i.id from t_union_brokerage_income i where t_union_opportunity.id = i.opportunity_id");
        wrapper.setSqlSelect("IFNULL(SUM(brokerage_price), 0) money");
        Map<String, Object> data = this.selectMap(wrapper);
        return Double.valueOf(data.get("money").toString());
    }

    //获取一周统计数据
    private List<Map<String, Map<String, Double>>> getBrokerageInWeek(Integer memberId) {
        List<Map<String, Map<String, Double>>> resultList = new ArrayList<>();
        int mondayOffset = 0 - ((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2 + 7) % 7);//例如今天是星期四，则mondayOffSet=-3
        Date mondayInThisWeek = DateUtil.addDays(DateUtil.getCurrentDate(), mondayOffset);
        for (; mondayOffset <= 0; mondayOffset++) {
            String strDate = DateUtil.getDateString(mondayInThisWeek, DateUtil.DATE_PATTERN);
            String strDateBegin = strDate + " 00:00:00";
            String strDateEnd = strDate + " 23:59:59";
            Map<String, Double> brokerageInDayMap = getBrokerageInDay(memberId, strDateBegin, strDateEnd);
            String strWeek = DateUtil.getWeek(mondayInThisWeek);
            Map<String, Map<String, Double>> weekMap = new HashMap<>();
            weekMap.put(strWeek, brokerageInDayMap);
            resultList.add(weekMap);
            mondayInThisWeek = DateUtil.addDays(mondayInThisWeek, 1);
        }
        return resultList;
    }

    private Map<String, Double> getBrokerageInDay(Integer memberId, String strDateBegin, String strDateEnd) {
        if (memberId != null || StringUtil.isNotEmpty(strDateBegin) || StringUtil.isNotEmpty(strDateEnd)) {
            Map<String, Double> resultMap = new HashMap<>();
            //已结算的佣金收入
            Double incomeBrokerage = unionOpportunityMapper.getBrokerageComeInDay(memberId, strDateBegin, strDateEnd);
            resultMap.put("incomeBrokerage", incomeBrokerage);
            //已结算的支出佣金
            Double expanseBrokerage = unionOpportunityMapper.getBrokerageExpanseInDay(memberId, strDateBegin, strDateEnd);
            resultMap.put("expanseBrokerage", expanseBrokerage);
            return resultMap;
        }
        return null;
    }


    /**
     * 获取我应付的佣金
     *
     * @param memberId           盟员id
     * @param brokeragePayStatus 结算状态 1：未结算  2：已结算
     * @return
     */
    private Double getBrokerageExpense(Integer memberId, int brokeragePayStatus) {
        Double money = 0d;
        if (memberId != null) {
            switch (brokeragePayStatus) {
                //未结算
                case 1:
                    EntityWrapper wrapper = new EntityWrapper();
                    wrapper.eq("to_member_id", memberId);
                    wrapper.eq("is_accept", OpportunityConstant.ACCEPT_CONFIRM_YES);
                    wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
                    wrapper.notExists("select id from t_union_brokerage_income i where t_union_opportunity.id = i.opportunity_id");
                    wrapper.setSqlSelect("IFNULL(SUM(t_union_opportunity.brokerage_price), 0) money");
                    Map<String, Object> map = this.selectMap(wrapper);
                    if (map == null) {
                        break;
                    }
                    money = Double.valueOf(map.get("money").toString());
                    break;
                //已结算
                case 2:
                    EntityWrapper entityWrapper = new EntityWrapper();
                    entityWrapper.eq("to_member_id", memberId);
                    entityWrapper.eq("is_accept", OpportunityConstant.ACCEPT_CONFIRM_YES);
                    entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
                    entityWrapper.exists("select id from t_union_brokerage_income i where t_union_opportunity.id = i.opportunity_id");
                    entityWrapper.setSqlSelect("IFNULL(SUM(t_union_opportunity.brokerage_price), 0) money");
                    Map<String, Object> data = this.selectMap(entityWrapper);
                    if (data == null) {
                        break;
                    }
                    money = Double.valueOf(data.get("money").toString());
                    break;
                default:
                    break;
            }
        }
        return money;
    }

    /**
     * 获取我应收的佣金
     *
     * @param memberId           盟员id
     * @param brokeragePayStatus 结算状态 1：未结算  2：已结算
     * @return
     */
    private Double getBrokerageIncome(Integer memberId, int brokeragePayStatus) {
        Double money = 0d;
        if (memberId != null) {
            switch (brokeragePayStatus) {
                //未结算
                case 1:
                    EntityWrapper wrapper = new EntityWrapper();
                    wrapper.eq("from_member_id", memberId);
                    wrapper.eq("is_accept", OpportunityConstant.ACCEPT_CONFIRM_YES);
                    wrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
                    wrapper.notExists("select id from t_union_brokerage_income i where t_union_opportunity.id = i.opportunity_id");
                    wrapper.setSqlSelect("IFNULL(SUM(t_union_opportunity.brokerage_price), 0) money");
                    Map<String, Object> map = this.selectMap(wrapper);
                    if (map == null) {
                        break;
                    }
                    money = Double.valueOf(map.get("money").toString());
                    break;
                //已结算
                case 2:
                    EntityWrapper entityWrapper = new EntityWrapper();
                    entityWrapper.eq("from_member_id", memberId);
                    entityWrapper.eq("is_accept", OpportunityConstant.ACCEPT_CONFIRM_YES);
                    entityWrapper.eq("del_status", CommonConstant.DEL_STATUS_NO);
                    entityWrapper.exists("select id from t_union_brokerage_income i where t_union_opportunity.id = i.opportunity_id");
                    entityWrapper.setSqlSelect("IFNULL(SUM(t_union_opportunity.brokerage_price), 0) money");
                    Map<String, Object> data = this.selectMap(entityWrapper);
                    if (data == null) {
                        break;
                    }
                    money = Double.valueOf(data.get("money").toString());
                    break;
                default:
                    break;
            }
        }
        return money;

    }

    /**
     * 插入佣金收入列表
     *
     * @param list
     */
    private void insertBatchByList(List<UnionOpportunity> list, String orderNo) {
        List<UnionBrokerageIncome> incomes = new ArrayList<UnionBrokerageIncome>();
        List<UnionBrokeragePay> pays = new ArrayList<UnionBrokeragePay>();

        for (UnionOpportunity opportunity : list) {
            UnionBrokerageIncome brokerageIncome = new UnionBrokerageIncome();
            brokerageIncome.setCreatetime(new Date());
            brokerageIncome.setDelStatus(CommonConstant.DEL_STATUS_NO);
            brokerageIncome.setMemberId(opportunity.getFromMemberId());
            brokerageIncome.setOpportunityId(opportunity.getId());
            brokerageIncome.setMoney(opportunity.getBrokeragePrice());
            brokerageIncome.setType(BrokerageConstant.TYPE_INCOME);
            incomes.add(brokerageIncome);

            UnionBrokeragePay pay = new UnionBrokeragePay();
            pay.setCreatetime(new Date());
            pay.setDelStatus(CommonConstant.DEL_STATUS_NO);
            pay.setFromMemberId(opportunity.getToMemberId());
            pay.setToMemberId(opportunity.getFromMemberId());
            pay.setMoney(opportunity.getBrokeragePrice());
            pay.setOrderNo(orderNo);
            pay.setOpportunityId(opportunity.getId());
            pay.setStatus(BrokerageConstant.BROKERAGE_PAY_STATUS_YES);
            pay.setType(BrokerageConstant.BROKERAGE_PAY_TYPE_WX);
            pays.add(pay);
        }
        unionBrokerageIncomeService.insertBatch(incomes);
        unionBrokeragePayService.insertBatch(pays);

    }
}
