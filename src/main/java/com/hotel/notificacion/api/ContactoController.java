package com.hotel.notificacion.api;

import com.hotel.notificacion.api.dto.ContactoRequest;
import com.hotel.notificacion.api.dto.MessageResponse;
import com.hotel.notificacion.internal.events.ContactoEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactoController implements ContactoApi {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.contacto.exchange}")
    private String contactoExchange;

    @Value("${app.rabbitmq.contacto.routing-key}")
    private String contactoRoutingKey;

    public ContactoController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public ResponseEntity<MessageResponse> enviarContacto(ContactoRequest request) {
        ContactoEvent event = new ContactoEvent(
                request.getNombre(),
                request.getEmail(),
                request.getTelefono(),
                request.getAsunto(),
                request.getMensaje()
        );
        rabbitTemplate.convertAndSend(contactoExchange, contactoRoutingKey, event);
        MessageResponse response = new MessageResponse();
        response.setMessage("Mensaje enviado correctamente");
        return ResponseEntity.ok(response);
    }
}
