package com.hotel.notificacion.core.notificacion.service;

import com.hotel.notificacion.infrastructure.config.RabbitConfig;
import com.hotel.notificacion.internal.events.ReservaCancelledEvent;
import com.hotel.notificacion.internal.events.ReservaConfirmedEvent;
import com.hotel.notificacion.internal.events.ReservaCreatedEvent;
import com.hotel.notificacion.internal.events.SendNotificationCommand;
import com.hotel.notificacion.internal.events.PasswordResetEvent;
import com.hotel.notificacion.internal.events.UserLoginEvent;
import com.hotel.notificacion.internal.events.UserRegisteredEvent;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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

        Map<String, String> variables = new HashMap<>();
        variables.put("clienteNombre", safe(event.getClienteNombre()));
        variables.put("reservaId", String.valueOf(event.getReservaId()));
        variables.put("estado", "PENDIENTE");
        variables.put("hotelNombre", safe(event.getHotelNombre()));
        variables.put("hotelDireccion", "-");
        variables.put("fechaInicio", safe(event.getFechaInicio()));
        variables.put("fechaFin", safe(event.getFechaFin()));
        variables.put("total", "0.00");
        variables.put("habitaciones", "-");

        String contenido = notificacionService.renderTemplate(
                "templates/reserva-created-email.html",
                variables
        );
        notificacionService.crearDesdeEvento("EMAIL", event.getClienteEmail(), asunto, contenido);
    }

    @RabbitHandler
    public void handleReservaConfirmed(ReservaConfirmedEvent event) {
        if (event.getClienteEmail() == null) {
            return;
        }
        String asunto = "Reserva confirmada";

        Map<String, String> variables = new HashMap<>();
        variables.put("clienteNombre", safe(event.getClienteNombre()));
        variables.put("reservaId", String.valueOf(event.getReservaId()));
        variables.put("estado", "CONFIRMADA");
        variables.put("hotelNombre", safe(event.getHotelNombre()));
        variables.put("hotelDireccion", "-");
        variables.put("fechaInicio", safe(event.getFechaInicio()));
        variables.put("fechaFin", safe(event.getFechaFin()));
        variables.put("total", "0.00");
        variables.put("habitaciones", "-");

        String contenido = notificacionService.renderTemplate(
                "templates/reserva-confirmed-email.html",
                variables
        );
        notificacionService.crearDesdeEvento("EMAIL", event.getClienteEmail(), asunto, contenido);
    }

    @RabbitHandler
    public void handleReservaCancelled(ReservaCancelledEvent event) {
        if (event.getClienteEmail() == null) {
            return;
        }
        String asunto = "Reserva cancelada";

        Map<String, String> variables = new HashMap<>();
        variables.put("clienteNombre", safe(event.getClienteNombre()));
        variables.put("reservaId", String.valueOf(event.getReservaId()));
        variables.put("estado", "CANCELADA");
        variables.put("hotelNombre", safe(event.getHotelNombre()));
        variables.put("hotelDireccion", "-");
        variables.put("fechaInicio", "-");
        variables.put("fechaFin", "-");
        variables.put("total", "0.00");
        variables.put("habitaciones", "-");
        variables.put("fechaCancelacion", "-");
        variables.put("motivoCancelacion", safe(event.getMotivo()));

        String contenido = notificacionService.renderTemplate(
                "templates/reserva-cancelled-email.html",
                variables
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

        Map<String, String> variables = new HashMap<>();
        variables.put("nombre", safe(event.getUsername()));
        variables.put("email", safe(event.getEmail()));
        variables.put("rol", safe(event.getRole()));
        variables.put("fecha", fechaActual);

        String contenido = notificacionService.renderTemplate(
                "templates/welcome-email.html",
                variables
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

    @RabbitHandler
    public void handlePasswordReset(PasswordResetEvent event) {
        if (event.getEmail() == null) {
            return;
        }
        String asunto = "Restablece tu contraseña";
        String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        Map<String, String> variables = new HashMap<>();
        variables.put("nombre", safe(event.getUsername()));
        variables.put("email", safe(event.getEmail()));
        variables.put("codigo", safe(event.getCode()));
        variables.put("fecha", fechaActual);

        String contenido = notificacionService.renderTemplate(
                "templates/password-reset-email.html",
                variables
        );
        notificacionService.crearDesdeEvento("EMAIL", event.getEmail(), asunto, contenido);
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

    private String formatDouble(Double value) {
        if (value == null) {
            return "0.00";
        }
        return String.format("%.2f", value);
    }
}
