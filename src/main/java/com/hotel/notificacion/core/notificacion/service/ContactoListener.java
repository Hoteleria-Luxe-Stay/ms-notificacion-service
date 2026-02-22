package com.hotel.notificacion.core.notificacion.service;

import com.hotel.notificacion.internal.events.ContactoEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ContactoListener {

    private final NotificacionService notificacionService;

    public ContactoListener(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @RabbitListener(queues = "${app.rabbitmq.contacto.queue}")
    public void handleContacto(ContactoEvent event) {
        if (event.getEmail() == null) {
            return;
        }
        notificacionService.crearContactoDesdeEvento(event);
    }
}
