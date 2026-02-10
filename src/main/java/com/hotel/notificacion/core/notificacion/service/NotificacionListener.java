package com.hotel.notificacion.core.notificacion.service;

import com.hotel.notificacion.infrastructure.config.RabbitConfig;
import com.hotel.notificacion.internal.events.ReservaCancelledEvent;
import com.hotel.notificacion.internal.events.ReservaConfirmedEvent;
import com.hotel.notificacion.internal.events.ReservaCreatedEvent;
import com.hotel.notificacion.internal.events.SendNotificationCommand;
import com.hotel.notificacion.internal.events.UserLoginEvent;
import com.hotel.notificacion.internal.events.UserRegisteredEvent;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
@RabbitListener(queues = RabbitConfig.EVENTS_QUEUE)
public class NotificacionListener {

    private final NotificacionService notificacionService;

    public NotificacionListener(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @RabbitHandler
    public void handleReservaCreated(ReservaCreatedEvent event) {
        if (event.getClienteEmail() == null) {
            return;
        }
        String asunto = "Reserva creada";
        String contenido = String.format(
                "Hola %s, tu reserva #%d en %s fue creada para %s - %s.",
                safe(event.getClienteNombre()),
                event.getReservaId(),
                safe(event.getHotelNombre()),
                safe(event.getFechaInicio()),
                safe(event.getFechaFin())
        );
        notificacionService.crearDesdeEvento("EMAIL", event.getClienteEmail(), asunto, contenido);
    }

    @RabbitHandler
    public void handleReservaConfirmed(ReservaConfirmedEvent event) {
        if (event.getClienteEmail() == null) {
            return;
        }
        String asunto = "Reserva confirmada";
        String contenido = String.format(
                "Hola %s, tu reserva #%d en %s fue confirmada. Codigo: %s.",
                safe(event.getClienteNombre()),
                event.getReservaId(),
                safe(event.getHotelNombre()),
                safe(event.getCodigoConfirmacion())
        );
        notificacionService.crearDesdeEvento("EMAIL", event.getClienteEmail(), asunto, contenido);
    }

    @RabbitHandler
    public void handleReservaCancelled(ReservaCancelledEvent event) {
        if (event.getClienteEmail() == null) {
            return;
        }
        String asunto = "Reserva cancelada";
        String contenido = String.format(
                "Hola %s, tu reserva #%d en %s fue cancelada. %s",
                safe(event.getClienteNombre()),
                event.getReservaId(),
                safe(event.getHotelNombre()),
                safe(event.getMotivo())
        );
        notificacionService.crearDesdeEvento("EMAIL", event.getClienteEmail(), asunto, contenido);
    }

    @RabbitHandler
    public void handleUserRegistered(UserRegisteredEvent event) {
        if (event.getEmail() == null) {
            return;
        }
        String asunto = "Bienvenido a LuxeStay";
        String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String contenido = notificacionService.renderTemplate(
                "templates/welcome-email.html",
                Map.of(
                        "nombre", safe(event.getUsername()),
                        "email", safe(event.getEmail()),
                        "rol", safe(event.getRole()),
                        "fecha", fechaActual
                )
        );
        notificacionService.crearDesdeEvento("EMAIL", event.getEmail(), asunto, contenido);
    }

    @RabbitHandler
    public void handleUserLogin(UserLoginEvent event) {
        if (event.getEmail() == null) {
            return;
        }
        if (event.getRole() != null && "ADMIN".equalsIgnoreCase(event.getRole())) {
            return;
        }
        String fechaActual = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String asunto = "Bienvenido nuevamente";
        String contenido = String.format(
                "Bienvenido nuevamente %s - %s",
                safe(event.getUsername()),
                fechaActual
        );
        notificacionService.crearDesdeEventoConUserId("LOGIN", event.getEmail(), asunto, contenido, event.getUserId());
    }

    @RabbitListener(queues = RabbitConfig.COMMAND_QUEUE)
    public void handleSendNotification(SendNotificationCommand command) {
        if (command.getDestinatario() == null) {
            return;
        }
        String asunto = command.getAsunto() == null ? "Notificacion" : command.getAsunto();
        String contenido = command.getContenido();
        notificacionService.crearDesdeEvento(command.getTipo(), command.getDestinatario(), asunto, contenido);
    }

    @RabbitHandler(isDefault = true)
    public void handleUnknown(Object payload) {
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
