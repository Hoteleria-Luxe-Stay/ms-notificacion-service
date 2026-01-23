package com.hotel.notificacion.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
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
    public Binding commandBinding(DirectExchange commandsExchange, Queue commandQueue) {
        return BindingBuilder.bind(commandQueue).to(commandsExchange).with("notification.send");
    }
}
