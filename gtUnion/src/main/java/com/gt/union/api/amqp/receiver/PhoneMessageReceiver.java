package com.gt.union.api.amqp.receiver;

import com.alibaba.fastjson.JSONArray;
import com.gt.union.api.amqp.entity.TemplateSmsMessage;
import com.gt.union.api.client.sms.SmsService;
import com.gt.union.api.amqp.entity.PhoneMessage;
import com.gt.union.common.config.AmqpConfig;
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
 * 消息队列消费者
 *
 * @author linweicong
 * @version 2017-10-19 16:27:37
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

    @Autowired
    private AmqpConfig amqpConfig;

    @Bean
    public Queue queuePhoneMessage() {
        return new Queue(amqpConfig.getUnionRoutPhoneKey(), true); //队列持久
    }

    @Bean
    public Binding bindingPhoneMessage() {
        return BindingBuilder.bind(queuePhoneMessage()).to(directExchange).with(amqpConfig.getUnionRoutPhoneKey());
    }

    @Bean
    public SimpleMessageListenerContainer phoneMessageContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(amqpConnectionFactory);
        container.setQueues(queuePhoneMessage());
        container.setExposeListenerChannel(true);
        container.setMaxConcurrentConsumers(1);
        container.setConcurrentConsumers(1);
        //设置确认模式手工确认
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(new ChannelAwareMessageListener() {

            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                try{
                    String msg = new String(message.getBody(), "UTF-8");
                    logger.info(msg);
                    if(msg.indexOf("tmplId") > -1){
                        TemplateSmsMessage templateSmsMessage = JSONArray.parseObject(msg, TemplateSmsMessage.class);
                        smsService.sendTempSms(templateSmsMessage);
                    }else {
                        PhoneMessage phoneMessage = JSONArray.parseObject(msg, PhoneMessage.class);
                        smsService.sendSms(phoneMessage);
                    }

                }catch (Exception e){
                    logger.error("发送短信错误");
                }finally {
                    //确认消息成功消费
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                }
            }
        });
        return container;
    }
}
