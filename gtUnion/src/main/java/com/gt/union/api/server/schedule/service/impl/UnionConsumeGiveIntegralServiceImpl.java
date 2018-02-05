package com.gt.union.api.server.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.gt.union.api.client.dict.IDictService;
import com.gt.union.api.client.member.impl.MemberServiceImpl;
import com.gt.union.api.server.schedule.service.IUnionConsumeGiveIntegralService;
import com.gt.union.card.consume.constant.ConsumeConstant;
import com.gt.union.card.consume.entity.UnionConsume;
import com.gt.union.card.consume.service.IUnionConsumeService;
import com.gt.union.card.main.entity.UnionCardIntegral;
import com.gt.union.card.main.service.IUnionCardIntegralService;
import com.gt.union.common.constant.CommonConstant;
import com.gt.union.common.exception.ParamException;
import com.gt.union.common.util.BigDecimalUtil;
import com.gt.union.common.util.CommonUtil;
import com.gt.union.union.main.entity.UnionMain;
import com.gt.union.union.main.service.IUnionMainService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author hongjiye
 * @time 2018-01-03 16:58
 **/
@Service
public class UnionConsumeGiveIntegralServiceImpl implements IUnionConsumeGiveIntegralService {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Autowired
    private IUnionConsumeService unionConsumeService;

    @Autowired
    private IUnionMainService unionMainService;

    @Autowired
    private IDictService dictService;

    @Autowired
    private IUnionCardIntegralService unionCardIntegralService;

    @Override
    public void giveConsumeIntegral(Map param) throws Exception {
        logger.info("定时任务赠送积分参数：{}", JSON.toJSONString(param));
        Object model = param.get("model");
        Object orderId = param.get("orderId");
        if (CommonUtil.isEmpty(model) || CommonUtil.isEmpty(orderId)) {
            throw new ParamException(CommonConstant.PARAM_ERROR);
        }
        Integer businessModel = CommonUtil.toInteger(model);
        Integer businessOrderId = CommonUtil.toInteger(orderId);
        UnionConsume consume = unionConsumeService.getValidByBusinessOrderIdAndModel(businessOrderId, businessModel);
        boolean flag = consume != null && consume.getPayStatus() == ConsumeConstant.PAY_STATUS_SUCCESS && (CommonUtil.isEmpty(consume.getGiveIntegral()) || consume.getGiveIntegral() == 0);
        //是否赠送积分
        if (flag) {
            //记录存在且支付成功且没有赠送积分
            if (CommonUtil.isNotEmpty(consume.getUnionId())) {
                UnionMain main = unionMainService.getValidById(consume.getUnionId());
                if (unionMainService.isUnionValid(main)) {
                    if (main.getIsIntegral() == CommonConstant.COMMON_YES) {
                        //开启积分
                        Double giveIntegral = dictService.getGiveIntegral();
                        double integral = BigDecimalUtil.toDouble(BigDecimalUtil.multiply(consume.getConsumeMoney() == null ? Double.valueOf(0) : consume.getConsumeMoney(), giveIntegral));
                        UnionConsume unionConsume = new UnionConsume();
                        unionConsume.setId(consume.getId());
                        unionConsume.setGiveIntegral(integral);
                        unionConsumeService.update(unionConsume);

                        //赠送积分
                        UnionCardIntegral unionCardIntegral = unionCardIntegralService.getValidByUnionIdAndFanId(consume.getUnionId(), consume.getFanId());
                        if (unionCardIntegral == null) {
                            UnionCardIntegral saveIntegral = new UnionCardIntegral();
                            saveIntegral.setDelStatus(CommonConstant.DEL_STATUS_NO);
                            saveIntegral.setCreateTime(new Date());
                            saveIntegral.setFanId(consume.getFanId());
                            saveIntegral.setUnionId(consume.getUnionId());
                            saveIntegral.setIntegral(CommonUtil.isNotEmpty(unionConsume.getGiveIntegral()) ? unionConsume.getGiveIntegral() : 0);
                            unionCardIntegralService.save(saveIntegral);
                        } else {
                            Double cardIntegral = unionCardIntegral.getIntegral() == null ? Double.valueOf(0) : unionCardIntegral.getIntegral();
                            BigDecimal tempIntegral = BigDecimalUtil.add(cardIntegral, unionConsume.getGiveIntegral());

                            UnionCardIntegral updateIntegral = new UnionCardIntegral();
                            updateIntegral.setId(unionCardIntegral.getId());
                            updateIntegral.setIntegral(BigDecimalUtil.toDouble(tempIntegral));
                            unionCardIntegralService.update(updateIntegral);
                        }

                    }
                }
            }

        }

    }
}
