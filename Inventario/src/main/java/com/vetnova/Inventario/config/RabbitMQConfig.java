package com.vetnova.Inventario.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.MessagingMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String COLA_INVENTARIO = "inventario.descontar.queue";

    @Bean
    public Queue inventarioQueue() {
        return new Queue(COLA_INVENTARIO, true);
    }

      @Bean
    public MessageConverter messageConverter() {
        return new MessagingMessageConverter();
    }
}
