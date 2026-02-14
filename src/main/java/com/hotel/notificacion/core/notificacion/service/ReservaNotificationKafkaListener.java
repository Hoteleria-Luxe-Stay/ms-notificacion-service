package com.hotel.notificacion.core.notificacion.service;

import com.hotel.notificacion.internal.events.ReservaNotificationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ReservaNotificationKafkaListener {

    private final NotificacionService notificacionService;

    public ReservaNotificationKafkaListener(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @KafkaListener(topics = "${app.kafka.topics.reserva-notifications}")
    public void handleReservaNotification(ReservaNotificationEvent event) {
        if (event.getClienteEmail() == null) {
            return;
        }

        String normalizedEventType = normalizeEventType(event.getEventType());
        String asunto = buildSubject(normalizedEventType);
        String templatePath = getTemplatePath(normalizedEventType);
        String contenido = buildHtmlContent(event, templatePath, normalizedEventType);

        // Usar userId directamente del evento (propagado desde ms-reserva)
        Long userId = event.getUserId();

        // Obtener eventType
        String eventType = normalizedEventType;

        // Crear notificación con userId y eventType asignados
        notificacionService.crearDesdeEventoConUserIdYEventType("EMAIL", event.getClienteEmail(), asunto, contenido, userId, eventType);
    }

    private String buildSubject(String eventType) {
        if ("CONFIRMED".equalsIgnoreCase(eventType)) {
            return "Reserva confirmada";
        }
        if ("CANCELLED_ADMIN".equalsIgnoreCase(eventType)) {
            return "Reserva cancelada por el administrador";
        }
        if ("CANCELLED".equalsIgnoreCase(eventType)) {
            return "Reserva cancelada";
        }
        return "Reserva pendiente";
    }

    private String getTemplatePath(String eventType) {
        if ("CONFIRMED".equalsIgnoreCase(eventType)) {
            return "templates/reserva-confirmed-email.html";
        }
        if ("CANCELLED_ADMIN".equalsIgnoreCase(eventType) ||
            "CANCELLED".equalsIgnoreCase(eventType)) {
            return "templates/reserva-cancelled-email.html";
        }
        return "templates/reserva-created-email.html";
    }

    private String buildHtmlContent(ReservaNotificationEvent event, String templatePath, String eventType) {
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
        if ("CANCELLED_ADMIN".equalsIgnoreCase(eventType) ||
            "CANCELLED".equalsIgnoreCase(eventType)) {
            variables.put("fechaCancelacion", safe(event.getFechaCancelacion()));
            variables.put("motivoCancelacion", safe(event.getMotivoCancelacion()));
        }

        return notificacionService.renderTemplate(templatePath, variables);
    }

    private String normalizeEventType(String eventType) {
        if (eventType == null || eventType.isBlank()) {
            return "PENDING";
        }
        if ("CREATED".equalsIgnoreCase(eventType)) {
            return "PENDING";
        }
        return eventType.toUpperCase();
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
