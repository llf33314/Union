package com.gt.union.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Created by Administrator on 2017/8/24 0024.
 */
@Configuration
public class AmqpConfig {
    public static final String UNION_EXCHANGE = "union_exchange";
    public static final String UNION_ROUTINGKEY_PHONE_MESSAGE = "union_routingKey_phone_message";

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setAddresses("183.47.242.4:5672");
        factory.setUsername("guest");
        factory.setPassword("yf26609632");
        factory.setVirtualHost("/");
        factory.setPublisherConfirms(true);//必须设置
        return factory;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) //必须是prototype类型
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        return template;
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(UNION_EXCHANGE);
    }
}
