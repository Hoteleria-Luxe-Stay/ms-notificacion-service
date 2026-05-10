package com.hotel.notificacion.core.notificacion.service;

import com.hotel.notificacion.internal.events.ReservaNotificationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaNotificationKafkaListenerTest {

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private ReservaNotificationKafkaListener listener;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(listener, "reservaEnabled", true);
    }

    // --- handleReservaNotification ---

    @Test
    void handleReservaNotification_reservaDisabled_noHaceNada() {
        ReflectionTestUtils.setField(listener, "reservaEnabled", false);
        ReservaNotificationEvent event = buildEvent("CONFIRMED");

        listener.handleReservaNotification(event);

        verifyNoInteractions(notificacionService);
    }

    @Test
    void handleReservaNotification_emailNull_noHaceNada() {
        ReservaNotificationEvent event = buildEvent("CONFIRMED");
        event.setClienteEmail(null);

        listener.handleReservaNotification(event);

        verifyNoInteractions(notificacionService);
    }

    @Test
    void handleReservaNotification_eventTypeConfirmed_creaConAsuntoConfirmada() {
        ReservaNotificationEvent event = buildEvent("CONFIRMED");
        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html>confirmed</html>");

        listener.handleReservaNotification(event);

        ArgumentCaptor<String> asuntoCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificacionService).crearDesdeEventoConUserIdYEventType(
                eq("EMAIL"), eq("cliente@test.com"), asuntoCaptor.capture(), anyString(), eq(1L), eq("CONFIRMED")
        );
        assertThat(asuntoCaptor.getValue()).isEqualTo("Reserva confirmada");
    }

    @Test
    void handleReservaNotification_eventTypeConfirmed_usaTemplateConfirmed() {
        ReservaNotificationEvent event = buildEvent("CONFIRMED");
        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html></html>");

        listener.handleReservaNotification(event);

        verify(notificacionService).renderTemplate(eq("templates/reserva-confirmed-email.html"), anyMap());
    }

    @Test
    void handleReservaNotification_eventTypeCancelled_usaTemplateCancelled() {
        ReservaNotificationEvent event = buildEvent("CANCELLED");
        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html></html>");

        listener.handleReservaNotification(event);

        verify(notificacionService).renderTemplate(eq("templates/reserva-cancelled-email.html"), anyMap());
    }

    @Test
    void handleReservaNotification_eventTypeCancelledAdmin_usaTemplateCancelled() {
        ReservaNotificationEvent event = buildEvent("CANCELLED_ADMIN");
        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html></html>");

        listener.handleReservaNotification(event);

        verify(notificacionService).renderTemplate(eq("templates/reserva-cancelled-email.html"), anyMap());
    }

    @Test
    void handleReservaNotification_eventTypePending_usaTemplateCreated() {
        ReservaNotificationEvent event = buildEvent("PENDING");
        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html></html>");

        listener.handleReservaNotification(event);

        verify(notificacionService).renderTemplate(eq("templates/reserva-created-email.html"), anyMap());
    }

    @Test
    void handleReservaNotification_eventTypeCreated_normalizaAPending() {
        ReservaNotificationEvent event = buildEvent("CREATED");
        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html></html>");

        listener.handleReservaNotification(event);

        ArgumentCaptor<String> eventTypeCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificacionService).crearDesdeEventoConUserIdYEventType(
                any(), any(), any(), any(), any(), eventTypeCaptor.capture()
        );
        assertThat(eventTypeCaptor.getValue()).isEqualTo("PENDING");
    }

    @Test
    void handleReservaNotification_eventTypeNull_normalizaAPending() {
        ReservaNotificationEvent event = buildEvent(null);
        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html></html>");

        listener.handleReservaNotification(event);

        ArgumentCaptor<String> eventTypeCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificacionService).crearDesdeEventoConUserIdYEventType(
                any(), any(), any(), any(), any(), eventTypeCaptor.capture()
        );
        assertThat(eventTypeCaptor.getValue()).isEqualTo("PENDING");
    }

    @Test
    void handleReservaNotification_eventTypeBlank_normalizaAPending() {
        ReservaNotificationEvent event = buildEvent("  ");
        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html></html>");

        listener.handleReservaNotification(event);

        ArgumentCaptor<String> eventTypeCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificacionService).crearDesdeEventoConUserIdYEventType(
                any(), any(), any(), any(), any(), eventTypeCaptor.capture()
        );
        assertThat(eventTypeCaptor.getValue()).isEqualTo("PENDING");
    }

    @Test
    void handleReservaNotification_cancelledAdmin_asuntoCanceladaAdmin() {
        ReservaNotificationEvent event = buildEvent("CANCELLED_ADMIN");
        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html></html>");

        ArgumentCaptor<String> asuntoCaptor = ArgumentCaptor.forClass(String.class);
        listener.handleReservaNotification(event);

        verify(notificacionService).crearDesdeEventoConUserIdYEventType(
                any(), any(), asuntoCaptor.capture(), any(), any(), any()
        );
        assertThat(asuntoCaptor.getValue()).isEqualTo("Reserva cancelada por el administrador");
    }

    @Test
    void handleReservaNotification_cancelled_asuntoCancelada() {
        ReservaNotificationEvent event = buildEvent("CANCELLED");
        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html></html>");

        ArgumentCaptor<String> asuntoCaptor = ArgumentCaptor.forClass(String.class);
        listener.handleReservaNotification(event);

        verify(notificacionService).crearDesdeEventoConUserIdYEventType(
                any(), any(), asuntoCaptor.capture(), any(), any(), any()
        );
        assertThat(asuntoCaptor.getValue()).isEqualTo("Reserva cancelada");
    }

    @Test
    void handleReservaNotification_conHabitaciones_formateaCorrectamente() {
        ReservaNotificationEvent event = buildEvent("CONFIRMED");
        ReservaNotificationEvent.HabitacionDetalle h = new ReservaNotificationEvent.HabitacionDetalle();
        h.setHabitacionId(101L);
        h.setPrecioNoche(150.0);
        event.setHabitaciones(List.of(h));

        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html></html>");

        listener.handleReservaNotification(event);

        verify(notificacionService).crearDesdeEventoConUserIdYEventType(any(), any(), any(), any(), any(), any());
    }

    @Test
    void handleReservaNotification_habitacionesSinPrecio_usaCero() {
        ReservaNotificationEvent event = buildEvent("CONFIRMED");
        ReservaNotificationEvent.HabitacionDetalle h = new ReservaNotificationEvent.HabitacionDetalle();
        h.setHabitacionId(101L);
        h.setPrecioNoche(null);
        event.setHabitaciones(List.of(h));

        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html></html>");

        listener.handleReservaNotification(event);

        verify(notificacionService).crearDesdeEventoConUserIdYEventType(any(), any(), any(), any(), any(), any());
    }

    @Test
    void handleReservaNotification_sinHabitaciones_guion() {
        ReservaNotificationEvent event = buildEvent("CONFIRMED");
        event.setHabitaciones(null);

        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html></html>");

        listener.handleReservaNotification(event);

        verify(notificacionService).crearDesdeEventoConUserIdYEventType(any(), any(), any(), any(), any(), any());
    }

    @Test
    void handleReservaNotification_totalNull_formateaCero() {
        ReservaNotificationEvent event = buildEvent("CONFIRMED");
        event.setTotal(null);

        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html></html>");

        listener.handleReservaNotification(event);

        verify(notificacionService).crearDesdeEventoConUserIdYEventType(any(), any(), any(), any(), any(), any());
    }

    @Test
    void handleReservaNotification_campoCancelacionIncluido_paraCancelledAdmin() {
        ReservaNotificationEvent event = buildEvent("CANCELLED_ADMIN");
        event.setFechaCancelacion("2024-01-01");
        event.setMotivoCancelacion("Solicitud del cliente");

        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html></html>");

        listener.handleReservaNotification(event);

        verify(notificacionService).crearDesdeEventoConUserIdYEventType(any(), any(), any(), any(), any(), any());
    }

    // --- Helper ---

    private ReservaNotificationEvent buildEvent(String eventType) {
        ReservaNotificationEvent event = new ReservaNotificationEvent();
        event.setEventType(eventType);
        event.setReservaId(100L);
        event.setUserId(1L);
        event.setClienteNombre("Cliente Test");
        event.setClienteEmail("cliente@test.com");
        event.setHotelNombre("Hotel Luxe");
        event.setHotelDireccion("Av. Test 123");
        event.setFechaInicio("2024-06-01");
        event.setFechaFin("2024-06-07");
        event.setTotal(350.0);
        event.setEstado("CONFIRMED");
        event.setHabitaciones(List.of());
        return event;
    }
}
