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
    public Binding reservaCreatedBinding(TopicExchange eventsExchange, Queue eventsQueue) {
        return BindingBuilder.bind(eventsQueue).to(eventsExchange).with("reserva.created");
    }

    @Bean
    public Binding reservaConfirmedBinding(TopicExchange eventsExchange, Queue eventsQueue) {
        return BindingBuilder.bind(eventsQueue).to(eventsExchange).with("reserva.confirmed");
    }

    @Bean
    public Binding reservaCancelledBinding(TopicExchange eventsExchange, Queue eventsQueue) {
        return BindingBuilder.bind(eventsQueue).to(eventsExchange).with("reserva.cancelled");
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
        idClassMapping.put(
                "com.hotel.reserva.infrastructure.events.ReservaCreatedEvent",
                com.hotel.notificacion.internal.events.ReservaCreatedEvent.class
        );
        idClassMapping.put(
                "com.hotel.reserva.infrastructure.events.ReservaConfirmedEvent",
                com.hotel.notificacion.internal.events.ReservaConfirmedEvent.class
        );
        idClassMapping.put(
                "com.hotel.reserva.infrastructure.events.ReservaCancelledEvent",
                com.hotel.notificacion.internal.events.ReservaCancelledEvent.class
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
