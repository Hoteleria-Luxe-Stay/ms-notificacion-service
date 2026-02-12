package com.hotel.notificacion.core.notificacion.service;

import com.hotel.notificacion.infrastructure.client.UserClient;
import com.hotel.notificacion.internal.events.ReservaNotificationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ReservaNotificationKafkaListener {

    private final NotificacionService notificacionService;
    private final UserClient userClient;

    public ReservaNotificationKafkaListener(NotificacionService notificacionService, UserClient userClient) {
        this.notificacionService = notificacionService;
        this.userClient = userClient;
    }

    @KafkaListener(topics = "${app.kafka.topics.reserva-notifications}")
    public void handleReservaNotification(ReservaNotificationEvent event) {
        if (event.getClienteEmail() == null) {
            return;
        }

        String asunto = buildSubject(event);
        String templatePath = getTemplatePath(event);
        String contenido = buildHtmlContent(event, templatePath);

        // Resolver userId desde email del cliente
        Long userId = userClient.getUserIdByEmail(event.getClienteEmail());

        // Crear notificación con userId asignado
        notificacionService.crearDesdeEventoConUserId("EMAIL", event.getClienteEmail(), asunto, contenido, userId);
    }

    private String buildSubject(ReservaNotificationEvent event) {
        if ("CONFIRMED".equalsIgnoreCase(event.getEventType())) {
            return "Reserva confirmada";
        }
        if ("CANCELLED_ADMIN".equalsIgnoreCase(event.getEventType())) {
            return "Reserva cancelada por el administrador";
        }
        if ("CANCELLED".equalsIgnoreCase(event.getEventType())) {
            return "Reserva cancelada";
        }
        return "Reserva creada";
    }

    private String getTemplatePath(ReservaNotificationEvent event) {
        if ("CONFIRMED".equalsIgnoreCase(event.getEventType())) {
            return "templates/reserva-confirmed-email.html";
        }
        if ("CANCELLED_ADMIN".equalsIgnoreCase(event.getEventType()) ||
            "CANCELLED".equalsIgnoreCase(event.getEventType())) {
            return "templates/reserva-cancelled-email.html";
        }
        return "templates/reserva-created-email.html";
    }

    private String buildHtmlContent(ReservaNotificationEvent event, String templatePath) {
        Map<String, String> variables = new HashMap<>();

        // Variables comunes para todos los templates
        variables.put("clienteNombre", safe(event.getClienteNombre()));
        variables.put("reservaId", String.valueOf(event.getReservaId()));
        variables.put("estado", safe(event.getEstado()));
        variables.put("hotelNombre", safe(event.getHotelNombre()));
        variables.put("hotelDireccion", safe(event.getHotelDireccion()));
        variables.put("fechaInicio", safe(event.getFechaInicio()));
        variables.put("fechaFin", safe(event.getFechaFin()));
        variables.put("total", formatTotal(event.getTotal()));
        variables.put("habitaciones", formatHabitaciones(event));

        // Variables específicas para cancelaciones
        if ("CANCELLED_ADMIN".equalsIgnoreCase(event.getEventType()) ||
            "CANCELLED".equalsIgnoreCase(event.getEventType())) {
            variables.put("fechaCancelacion", safe(event.getFechaCancelacion()));
            variables.put("motivoCancelacion", safe(event.getMotivoCancelacion()));
        }

        return notificacionService.renderTemplate(templatePath, variables);
    }

    private String formatHabitaciones(ReservaNotificationEvent event) {
        if (event.getHabitaciones() == null || event.getHabitaciones().isEmpty()) {
            return "-";
        }
        return event.getHabitaciones().stream()
                .map(habitacion -> String.format("#%d (S/. %.2f)",
                        habitacion.getHabitacionId(),
                        habitacion.getPrecioNoche() != null ? habitacion.getPrecioNoche() : 0.0))
                .collect(Collectors.joining(", "));
    }

    private String formatTotal(Double total) {
        if (total == null) {
            return "0.00";
        }
        return String.format("%.2f", total);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
