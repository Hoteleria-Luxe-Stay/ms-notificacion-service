package com.hotel.notificacion.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EVENTS_EXCHANGE = "hotel.events";
    public static final String COMMANDS_EXCHANGE = "hotel.commands";

    public static final String EVENTS_QUEUE = "notification.events.queue";
    public static final String COMMAND_QUEUE = "notification.queue";

    @Value("${app.rabbitmq.contacto.exchange}")
    private String contactoExchangeName;

    @Value("${app.rabbitmq.contacto.queue}")
    private String contactoQueueName;

    @Value("${app.rabbitmq.contacto.routing-key}")
    private String contactoRoutingKeyName;

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EVENTS_EXCHANGE);
    }

    @Bean
    public DirectExchange commandsExchange() {
        return new DirectExchange(COMMANDS_EXCHANGE);
    }

    @Bean
    public Queue eventsQueue() {
        return new Queue(EVENTS_QUEUE, true);
    }

    @Bean
    public Queue commandQueue() {
        return new Queue(COMMAND_QUEUE, true);
    }

    @Bean
    public Binding userRegisteredBinding(TopicExchange eventsExchange, Queue eventsQueue) {
        return BindingBuilder.bind(eventsQueue).to(eventsExchange).with("user.registered");
    }

    @Bean
    public Binding userLoginBinding(TopicExchange eventsExchange, Queue eventsQueue) {
        return BindingBuilder.bind(eventsQueue).to(eventsExchange).with("user.login");
    }

    @Bean
    public Binding userPasswordResetBinding(TopicExchange eventsExchange, Queue eventsQueue) {
        return BindingBuilder.bind(eventsQueue).to(eventsExchange).with("user.password.reset");
    }

    @Bean
    public Binding commandBinding(DirectExchange commandsExchange, Queue commandQueue) {
        return BindingBuilder.bind(commandQueue).to(commandsExchange).with("notification.send");
    }

    @Bean
    public DirectExchange contactoExchange() {
        return new DirectExchange(contactoExchangeName);
    }

    @Bean
    public Queue contactoQueue() {
        return new Queue(contactoQueueName, true);
    }

    @Bean
    public Binding contactoBinding(DirectExchange contactoExchange, Queue contactoQueue) {
        return BindingBuilder.bind(contactoQueue).to(contactoExchange).with(contactoRoutingKeyName);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("com.hotel.notificacion.internal.events", "com.hotel.auth.infrastructure.events");

        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put(
                "com.hotel.auth.infrastructure.events.UserLoginEvent",
                com.hotel.notificacion.internal.events.UserLoginEvent.class
        );
        idClassMapping.put(
                "com.hotel.auth.infrastructure.events.UserRegisteredEvent",
                com.hotel.notificacion.internal.events.UserRegisteredEvent.class
        );
        idClassMapping.put(
                "com.hotel.auth.infrastructure.events.PasswordResetEvent",
                com.hotel.notificacion.internal.events.PasswordResetEvent.class
        );
        typeMapper.setIdClassMapping(idClassMapping);
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
