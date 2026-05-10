package com.hotel.notificacion.core.notificacion.service;

import com.hotel.notificacion.internal.events.ContactoEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactoListenerTest {

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private ContactoListener contactoListener;

    @Test
    void handleContacto_conEmailValido_invocaCrearContactoDesdeEvento() {
        ContactoEvent event = new ContactoEvent("Juan", "juan@test.com", "123456", "Asunto", "Mensaje");

        contactoListener.handleContacto(event);

        verify(notificacionService).crearContactoDesdeEvento(event);
    }

    @Test
    void handleContacto_sinEmail_noInvocaServicio() {
        ContactoEvent event = new ContactoEvent("Juan", null, "123456", "Asunto", "Mensaje");

        contactoListener.handleContacto(event);

        verifyNoInteractions(notificacionService);
    }

    @Test
    void handleContacto_emailVacio_invocaServicio() {
        ContactoEvent event = new ContactoEvent("Juan", "", null, "Asunto", "Mensaje");

        // email no es null, entonces SÍ invoca
        contactoListener.handleContacto(event);

        verify(notificacionService).crearContactoDesdeEvento(event);
    }
}
