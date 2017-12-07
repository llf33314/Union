package com.gt.union.api.amqp.sender;

import com.gt.union.api.amqp.entity.PhoneMessage;
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

    public void sendMsg(PhoneMessage phoneMessage) {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        System.out.println(correlationData);
        this.rabbitTemplate.convertAndSend(AmqpConfig.UNION_EXCHANGE, AmqpConfig.UNION_ROUTINGKEY_PHONE_MESSAGE,
                phoneMessage.toString(), correlationData);
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //消息成功到达了mq服务器回调
        System.out.println(" 回调id:" + correlationData);
        System.out.println(ack);
        if (ack) {
            System.out.println("消息成功消费" + cause);
        } else {
            System.out.println("消息消费失败:" + cause);
        }
    }

    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        try{
            String msg = new String(message.getBody(), "UTF-8");
            System.out.println(msg);
            System.out.println(i);
            System.out.println(s);
            System.out.println(s1);
            System.out.println(s2);
        }catch (Exception e){

        }

    }
}
