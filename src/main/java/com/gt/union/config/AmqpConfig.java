package com.gt.union.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Created by Administrator on 2017/8/24 0024.
 */
@Configuration
public class AmqpConfig {
    public static String UNION_EXCHANGE;
    public static String UNION_ROUTINGKEY_PHONE_MESSAGE;

    @Value("${exchange.union.exchange}")
    public void setUnionExchange(String exchange) {
        UNION_EXCHANGE = exchange;
    }

    @Value("${exchange.union.exchange}")
    public void setUnionRoutingkeyPhoneMessage(String message) {
        UNION_ROUTINGKEY_PHONE_MESSAGE = message;
    }

    @Value("${mq.uri}")
    private String address;

    @Value("${mq.user}")
    private String userName;

    @Value("${mq.password}")
    private String password;

    @Bean
    public ConnectionFactory amqpConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
//        factory.setAddresses("183.47.242.4:5672");
//        factory.setUsername("guest");
//        factory.setPassword("yf26609632");
        factory.setAddresses(address);
        factory.setUsername(userName);
        factory.setPassword(password);
        factory.setVirtualHost("/");
        factory.setPublisherConfirms(true);//必须设置
        return factory;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) //必须是prototype类型
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(amqpConnectionFactory());
        return template;
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(UNION_EXCHANGE);
    }
}
