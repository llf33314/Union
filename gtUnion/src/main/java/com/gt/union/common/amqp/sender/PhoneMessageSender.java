package com.gt.union.common.amqp.sender;

import com.gt.union.common.amqp.entity.PhoneMessage;
import com.gt.union.common.config.AmqpConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 消息队列生产者
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
 */
@Component
public class PhoneMessageSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMsg(PhoneMessage phoneMessage) {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        this.rabbitTemplate.convertAndSend(AmqpConfig.UNION_EXCHANGE, AmqpConfig.UNION_ROUTINGKEY_PHONE_MESSAGE,
                phoneMessage.toString(), correlationData);
    }
}
