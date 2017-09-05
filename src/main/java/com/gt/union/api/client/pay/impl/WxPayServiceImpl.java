package com.gt.union.api.client.pay.impl;

import com.alibaba.fastjson.JSONObject;
import com.gt.api.util.HttpClienUtils;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.api.client.user.BusUserService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.brokerage.UnionIncomeExpenseRecordConstant;
import com.gt.union.common.constant.business.UnionBrokeragePayRecordConstant;
import com.gt.union.common.exception.BusinessException;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.entity.brokerage.UnionBrokerageWithdrawalsRecord;
import com.gt.union.entity.brokerage.UnionIncomeExpenseRecord;
import com.gt.union.entity.brokerage.UnionVerifyMember;
import com.gt.union.service.brokerage.IUnionBrokerageWithdrawalsRecordService;
import com.gt.union.service.brokerage.IUnionIncomeExpenseRecordService;
import com.gt.union.service.business.IUnionBrokeragePayRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付服务
 * Created by Administrator on 2017/8/22 0022.
 */
@Service
public class WxPayServiceImpl implements WxPayService {

    private static final String ENTERPRISE_PAYMENT = "WxPayServiceImpl.enterprisePayment()";

    @Autowired
    private IUnionIncomeExpenseRecordService unionIncomeExpenseRecordService;

    @Autowired
    private IUnionBrokeragePayRecordService unionBrokeragePayRecordService;

    @Autowired
    private IUnionBrokerageWithdrawalsRecordService unionBrokerageWithdrawalsRecordService;

    @Autowired
    private BusUserService busUserService;

    @Value("${wx.duofen.busId}")
    private Integer duofenBusId;

    @Value("${wxmp.url}")
    private String wxmpUrl;

    @Override
    public int pay(Map<String,Object> param) {
        String url = wxmpUrl + "/8A5DA52E/payApi/6F6D9AD2/79B4DE7C/payapi.do";
        try {
            Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(param),url, Map.class, CommonConstant.WXMP_SIGN_KEY);
            if(CommonUtil.isEmpty(result)){
                return 0;
            }
            if(CommonUtil.toInteger(result.get("code")) != 0){
                return 0;
            }
        }catch (Exception e){
            return 0;
        }
        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int enterprisePayment(Integer busid, Integer memberId, String openid, Integer unionId, Double fee, UnionVerifyMember unionVerifyMember) throws Exception {
        Double moneySum = unionIncomeExpenseRecordService.getProfitMoneyByBusId(busid);
        if(moneySum <= 0){
            throw new BusinessException(ENTERPRISE_PAYMENT, "可提佣金不足", "佣金不足");
        }
        if(moneySum < fee){
            throw new BusinessException(ENTERPRISE_PAYMENT, "可提佣金小于提现金额", ExceptionConstant.SYS_ERROR);
        }
        String url = wxmpUrl + "/8A5DA52E/payApi/6F6D9AD2/79B4DE7C/enterprisePayment.do";
        Map<String,Object> data = busUserService.getWxPublicUserByBusId(duofenBusId);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appid", data.get("appid"));
        params.put("partner_trade_no", UnionBrokeragePayRecordConstant.orderPrefix + System.currentTimeMillis());
        params.put("openid", openid);
        params.put("desc", "联盟佣金提现");
        params.put("model", CommonConstant.ENTERPRISE_PAY_MODEL);
        params.put("busId", busid);
        params.put("mchid", data.get("mchId"));
        params.put("withdrawalsBusId", busid);
        params.put("memberId", memberId);
        params.put("amount", fee);
        try {
            Map result = HttpClienUtils.reqPostUTF8(JSONObject.toJSONString(params),url, Map.class, CommonConstant.WXMP_SIGN_KEY);
            if(CommonUtil.isEmpty(result)){
                return 0;
            }
            if(CommonUtil.toInteger(result.get("code")) != 0){
                return 0;
            }

            //添加提现记录
            UnionBrokerageWithdrawalsRecord record = new UnionBrokerageWithdrawalsRecord();
            record.setBusId(busid);
            record.setCreatetime(new Date());
            record.setDelStatus(0);
            record.setMemberId(memberId);
            record.setMoney(fee);
            record.setRecordType(0);
            record.setUnionId(unionId);
            if(unionVerifyMember != null){
                record.setUnionVerifyMember(unionVerifyMember.getId());
            }
            unionBrokerageWithdrawalsRecordService.insert(record);
            //添加收支记录
            UnionIncomeExpenseRecord unionIncomeExpenseRecord = new UnionIncomeExpenseRecord();
            unionIncomeExpenseRecord.setBrokerageWithdrawalsRecordId(record.getId());
            unionIncomeExpenseRecord.setBusId(busid);
            unionIncomeExpenseRecord.setDelStatus(UnionIncomeExpenseRecordConstant.DEL_STATUS_NO);
            unionIncomeExpenseRecord.setCreatetime(new Date());
            unionIncomeExpenseRecord.setType(UnionIncomeExpenseRecordConstant.TYPE_EXPENSE);
            unionIncomeExpenseRecord.setSource(UnionIncomeExpenseRecordConstant.SOURCE_WITHDRAWALS);
            unionIncomeExpenseRecord.setMoney(fee);
            unionIncomeExpenseRecord.setUnionId(unionId);
            unionIncomeExpenseRecordService.insert(unionIncomeExpenseRecord);
        }catch (Exception e){
            return 0;
        }
        return 1;
    }


}
