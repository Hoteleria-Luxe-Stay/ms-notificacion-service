package com.hotel.notificacion.core.notificacion.service;

import com.hotel.notificacion.infrastructure.client.UserClient;
import com.hotel.notificacion.internal.events.ReservaNotificationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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
        String contenido = buildContent(event);

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

    private String buildContent(ReservaNotificationEvent event) {
        String habitaciones = "";
        if (event.getHabitaciones() != null && !event.getHabitaciones().isEmpty()) {
            habitaciones = event.getHabitaciones().stream()
                    .map(habitacion -> String.format("#%d (S/. %.2f)",
                            habitacion.getHabitacionId(),
                            habitacion.getPrecioNoche() != null ? habitacion.getPrecioNoche() : 0.0))
                    .collect(Collectors.joining(", "));
        }

        String detalleHabitaciones = habitaciones.isBlank() ? "" : " Habitaciones: " + habitaciones + ".";
        String detalleCancelacion = "";
        if (event.getFechaCancelacion() != null || event.getMotivoCancelacion() != null) {
            String fecha = safe(event.getFechaCancelacion());
            String motivo = safe(event.getMotivoCancelacion());
            detalleCancelacion = String.format(" Cancelacion: %s. Motivo: %s.",
                    fecha.isBlank() ? "-" : fecha,
                    motivo.isBlank() ? "-" : motivo);
        }

        return String.format(
                "Hola %s, tu reserva #%d está %s. Hotel: %s. Dirección: %s. Fechas: %s a %s. Total: S/. %.2f.%s%s",
                safe(event.getClienteNombre()),
                event.getReservaId(),
                safe(event.getEstado()),
                safe(event.getHotelNombre()),
                safe(event.getHotelDireccion()),
                safe(event.getFechaInicio()),
                safe(event.getFechaFin()),
                event.getTotal() != null ? event.getTotal() : 0.0,
                detalleHabitaciones,
                detalleCancelacion
        );
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
