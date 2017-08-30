package com.gt.union.amqp.receiver;

import com.alibaba.fastjson.JSON;
import com.gt.union.amqp.entity.PhoneMessage;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.config.AmqpConfig;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/24 0024.
 */
@Component
public class PhoneMessageReceiver {
    private Logger logger = LoggerFactory.getLogger(PhoneMessageReceiver.class);

    @Autowired
    private ConnectionFactory amqpConnectionFactory;

    @Autowired
    private DirectExchange directExchange;

    @Autowired
    private SmsService smsService;

    @Bean
    public Queue queuePhoneMessage() {
        return new Queue(AmqpConfig.UNION_ROUTINGKEY_PHONE_MESSAGE, true); //队列持久
    }

    @Bean
    public Binding bindingPhoneMessage() {
        return BindingBuilder.bind(queuePhoneMessage()).to(directExchange).with(AmqpConfig.UNION_ROUTINGKEY_PHONE_MESSAGE);
    }

    @Bean
    public SimpleMessageListenerContainer phoneMessageContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(amqpConnectionFactory);
        container.setQueues(queuePhoneMessage());
        container.setExposeListenerChannel(true);
        container.setMaxConcurrentConsumers(1);
        container.setConcurrentConsumers(1);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL); //设置确认模式手工确认
        container.setMessageListener(new ChannelAwareMessageListener() {

            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                String msg = new String(message.getBody());
                PhoneMessage phoneMessage = JSON.parseObject(msg, PhoneMessage.class);
                Map<String, Object> map = new HashMap<>();
                map.put("reqdata", phoneMessage);
                smsService.sendSms(map);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); //确认消息成功消费
            }
        });
        return container;
    }
}
