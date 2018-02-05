package com.gt.union.common.config;

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
 * MQ配置类
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
@Configuration
public class AmqpConfig {
    @Value("${exchange.union.exchange}")
    private String unionExchange;

    @Value("${queueName.union.queueName}")
    private String unionRoutPhoneKey;

    @Value("${mq.uri}")
    private String address;

    @Value("${mq.user}")
    private String userName;

    @Value("${mq.password}")
    private String password;

    @Bean
    public ConnectionFactory amqpConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setAddresses(address);
        factory.setUsername(userName);
        factory.setPassword(password);
        factory.setVirtualHost("/");
        //必须设置
        factory.setPublisherConfirms(true);
        return factory;
    }

    //必须是prototype类型
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(amqpConnectionFactory());
        template.setMandatory(true);
        return template;
    }

    public String getUnionExchange() {
        return unionExchange;
    }

    public void setUnionExchange(String unionExchange) {
        this.unionExchange = unionExchange;
    }

    public String getUnionRoutPhoneKey() {
        return unionRoutPhoneKey;
    }

    public void setUnionRoutPhoneKey(String unionRoutPhoneKey) {
        this.unionRoutPhoneKey = unionRoutPhoneKey;
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(unionExchange);
    }
}
