package com.gt.union.api.amqp.sender;

import com.gt.union.api.amqp.entity.PhoneMessage;
import com.gt.union.api.amqp.entity.SmsMessage;
import com.gt.union.api.amqp.entity.TemplateSmsMessage;
import com.gt.union.common.config.AmqpConfig;
import org.springframework.amqp.core.Message;
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
public class PhoneMessageSender implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback{

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMsg(SmsMessage smsMessage) {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        this.rabbitTemplate.convertAndSend(AmqpConfig.UNION_EXCHANGE, AmqpConfig.UNION_ROUTINGKEY_PHONE_MESSAGE,
                smsMessage.toString(), correlationData);
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }


    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            System.out.println("消息到达交换机");
        } else {
            System.out.println("消息没有到达交换机");
        }
    }

    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        try{
            String msg = new String(message.getBody(), "UTF-8");
            System.out.println(msg);
            System.out.println("消息找不到队列");
        }catch (Exception e){

        }

    }
}
