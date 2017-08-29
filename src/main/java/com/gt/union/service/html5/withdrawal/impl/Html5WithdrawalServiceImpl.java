package com.gt.union.service.html5.withdrawal.impl;

import com.gt.union.amqp.entity.PhoneMessage;
import com.gt.union.amqp.sender.PhoneMessageSender;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ExceptionConstant;
import com.gt.union.common.constant.business.UnionBusinessRecommendConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.service.brokerage.IUnionBrokerageWithdrawalsRecordService;
import com.gt.union.service.brokerage.IUnionIncomeExpenseRecordService;
import com.gt.union.service.business.IUnionBusinessRecommendService;
import com.gt.union.service.card.IUnionCardDivideRecordService;
import com.gt.union.service.html5.withdrawal.IHtml5WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 手机端H5提现 服务实现类
 * Created by Administrator on 2017/8/28 0028.
 */
@Service
public class Html5WithdrawalServiceImpl implements IHtml5WithdrawalService {
    private static final String INDEX = "Html5WithdrawalServiceImpl.index()";

    @Autowired
    private IUnionBusinessRecommendService unionBusinessRecommendService;

    @Autowired
    private IUnionCardDivideRecordService unionCardDivideRecordService;

    @Autowired
    private IUnionBrokerageWithdrawalsRecordService unionBrokerageWithdrawalsRecordService;

    @Autowired
    private IUnionIncomeExpenseRecordService unionIncomeExpenseRecordService;

    @Autowired
    private PhoneMessageSender phoneMessageSender;

    @Override
    public Map<String, Object> index(Integer busId) throws Exception {
        if (busId == null) {
            throw new ParamException(INDEX, "参数busId为空", ExceptionConstant.PARAM_ERROR);
        }
        //已支付的商机佣金总和
        Double confirmRecommendBrokerageSum = this.unionBusinessRecommendService.sumBusinessPriceByFromBusIdAndIsConfirm(busId, UnionBusinessRecommendConstant.IS_CONFIRM_CONFIRM);

        //售卡佣金总和
        Double cardDivideBrokerageSum = this.unionCardDivideRecordService.sumPriceByBusId(busId);

        //历史佣金提现总额
        Double historyWithdrawalSum = this.unionBrokerageWithdrawalsRecordService.sumMoneyByBusId(busId);

        //收支记录表上盈亏金额，即目前可提现的金额
        Double availableWithdrawalMoney = this.unionIncomeExpenseRecordService.getProfitMoneyByBusId(busId);

        //***计算可提现金额=已支付的商机佣金总和+售卡佣金总和-历史佣金提现总额，并判断与该商家的收支记录表上的可提现金额是否相等，如果不等，则发短信通知相关人员进行维护
        BigDecimal availableWithdrawalMoneyInCalculate = BigDecimalUtil.add(confirmRecommendBrokerageSum, cardDivideBrokerageSum);
        availableWithdrawalMoneyInCalculate = BigDecimalUtil.subtract(availableWithdrawalMoneyInCalculate, historyWithdrawalSum);
        if (BigDecimalUtil.subtract(availableWithdrawalMoneyInCalculate, availableWithdrawalMoney).doubleValue() != 0.0) {
            StringBuilder sbContent = new StringBuilder("商家(busId:")
                    .append(busId).append(")的可提现金额出现异常：收支记录表金额(")
                    .append(availableWithdrawalMoney)
                    .append(")，商机佣金+售卡佣金-历史提现金额(")
                    .append(availableWithdrawalMoneyInCalculate)
                    .append(")。");
            PhoneMessage phoneMessage = new PhoneMessage(busId, CommonConstant.DEVELOPER_PHONE, sbContent.toString());
            this.phoneMessageSender.sendMsg(phoneMessage);
        }

        //未支出商机佣金总和
        Double uncheckRecommendBrokerageSum = this.unionBusinessRecommendService.sumBusinessPriceByFromBusIdAndIsConfirm(busId, UnionBusinessRecommendConstant.IS_CONFIRM_UNCHECK);//(含坏账)
        Double badDebtRecommemdBrokerageSum = this.unionBusinessRecommendService.sumBusinessPriceByFromBusIdInBadDebt(busId);//坏账金额
        uncheckRecommendBrokerageSum = BigDecimalUtil.subtract(uncheckRecommendBrokerageSum, badDebtRecommemdBrokerageSum).doubleValue();//(不含坏账)

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("confirmRecommendBrokerageSum", confirmRecommendBrokerageSum);
        resultMap.put("cardDivideBrokerageSum", cardDivideBrokerageSum);
        resultMap.put("historyWithdrawalSum", historyWithdrawalSum);
        resultMap.put("uncheckRecommendBrokerageSum", uncheckRecommendBrokerageSum);
        resultMap.put("availableWithdrawalMoney", availableWithdrawalMoney);

        return resultMap;
    }
}
