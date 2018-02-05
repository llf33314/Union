package com.gt.union.schedule.refund;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.gt.union.api.amqp.entity.PhoneMessage;
import com.gt.union.api.amqp.sender.PhoneMessageSender;
import com.gt.union.api.client.pay.WxPayService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.constant.ConfigConstant;
import com.gt.union.common.response.GtJsonResult;
import com.gt.union.common.util.*;
import com.gt.union.refund.order.constant.RefundOrderConstant;
import com.gt.union.refund.order.entity.UnionRefundOrder;
import com.gt.union.refund.order.service.IUnionRefundOrderService;
import com.gt.union.union.main.entity.UnionMain;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author hongjiye
 * @time 2018-02-03 16:47
 **/
public class UnionRefundSchedule {

    private Logger logger = Logger.getLogger(UnionRefundSchedule.class);

    @Autowired
    private PhoneMessageSender phoneMessageSender;

    @Autowired
    private IUnionRefundOrderService unionRefundOrderService;

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 商机退款 每两小时执行一次
     */
    @Scheduled(cron = "0 0 0/2 * * *")
    public void refundOpportunity() {
        try {
            Date currentDate = DateUtil.getCurrentDate();
            logger.info(currentDate + " 开始执行<商机退款>定时任务器：商机重复支付退款");
            List<UnionRefundOrder> list = unionRefundOrderService.listValidByStatusAndType(RefundOrderConstant.REFUND_STATUS_APPLYING, RefundOrderConstant.TYPE_OPPORTUNITY);
            if (ListUtil.isNotEmpty(list)) {
                for (UnionRefundOrder order : list) {
                    try {
                        GtJsonResult result = wxPayService.refundOrder(order.getSysOrderNo(), order.getRefundMoney(), order.getTotalMoney());
                        if (result.isSuccess()) {
                            Map data = (Map) result.getData();
                            UnionRefundOrder successRefundOrder = new UnionRefundOrder();
                            successRefundOrder.setId(order.getId());
                            successRefundOrder.setModifyTime(new Date());
                            successRefundOrder.setDesc("退款成功");
                            successRefundOrder.setStatus(RefundOrderConstant.REFUND_STATUS_SUCCESS);
                            successRefundOrder.setRefundOrderNo(CommonUtil.isNotEmpty(data.get("data")) ? data.get("data").toString() : "");
                            unionRefundOrderService.update(successRefundOrder);
                        }
                    } catch (Exception e) {
                        logger.error("执行<商机退款>定时任务器：商机重复支付退款失败", e);
                        phoneMessageSender.sendMsg(new PhoneMessage(PropertiesUtil.getDuofenBusId(), ConfigConstant.DEVELOPER_PHONE,
                            "时间：" + DateTimeKit.format(new Date(), DateTimeKit.DEFAULT_DATETIME_FORMAT) + ",执行<商机退款>定时任务器：商机重复支付退款->出现异常(" + e.getMessage() + ")"));
                    }

                }
            }

        } catch (Exception e) {
            logger.error("执行<商机退款>定时任务器：商机重复支付退款失败", e);
        }
    }
}
