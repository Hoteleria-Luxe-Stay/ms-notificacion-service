package com.hotel.notificacion.api;

import com.hotel.notificacion.api.dto.ContactoRequest;
import com.hotel.notificacion.api.dto.MessageResponse;
import com.hotel.notificacion.core.notificacion.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactoController implements ContactoApi {

    private final NotificacionService notificacionService;

    public ContactoController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @Override
    public ResponseEntity<MessageResponse> enviarContacto(ContactoRequest request) {
        notificacionService.crearContacto(request);
        MessageResponse response = new MessageResponse();
        response.setMessage("Mensaje enviado correctamente");
        return ResponseEntity.ok(response);
    }
}
