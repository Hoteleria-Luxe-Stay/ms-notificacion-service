package com.hotel.notificacion.internal.events;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EventsTest {

    // --- ContactoEvent ---

    @Test
    void contactoEvent_constructorCompleto_setsCampos() {
        ContactoEvent event = new ContactoEvent("Juan", "juan@test.com", "123456", "Asunto", "Mensaje");

        assertThat(event.getNombre()).isEqualTo("Juan");
        assertThat(event.getEmail()).isEqualTo("juan@test.com");
        assertThat(event.getTelefono()).isEqualTo("123456");
        assertThat(event.getAsunto()).isEqualTo("Asunto");
        assertThat(event.getMensaje()).isEqualTo("Mensaje");
    }

    @Test
    void contactoEvent_setters_actualizanCampos() {
        ContactoEvent event = new ContactoEvent();
        event.setNombre("Pedro");
        event.setEmail("pedro@test.com");
        event.setTelefono("987654321");
        event.setAsunto("Nuevo asunto");
        event.setMensaje("Nuevo mensaje");

        assertThat(event.getNombre()).isEqualTo("Pedro");
        assertThat(event.getEmail()).isEqualTo("pedro@test.com");
        assertThat(event.getTelefono()).isEqualTo("987654321");
        assertThat(event.getAsunto()).isEqualTo("Nuevo asunto");
        assertThat(event.getMensaje()).isEqualTo("Nuevo mensaje");
    }

    // --- PasswordResetEvent ---

    @Test
    void passwordResetEvent_setters_actualizanCampos() {
        PasswordResetEvent event = new PasswordResetEvent();
        event.setUserId(1L);
        event.setUsername("testuser");
        event.setEmail("test@test.com");
        event.setCode("123456");

        assertThat(event.getUserId()).isEqualTo(1L);
        assertThat(event.getUsername()).isEqualTo("testuser");
        assertThat(event.getEmail()).isEqualTo("test@test.com");
        assertThat(event.getCode()).isEqualTo("123456");
    }

    // --- UserLoginEvent ---

    @Test
    void userLoginEvent_setters_actualizanCampos() {
        UserLoginEvent event = new UserLoginEvent();
        event.setUserId(2L);
        event.setUsername("loginuser");
        event.setEmail("login@test.com");
        event.setRole("USER");

        assertThat(event.getUserId()).isEqualTo(2L);
        assertThat(event.getUsername()).isEqualTo("loginuser");
        assertThat(event.getEmail()).isEqualTo("login@test.com");
        assertThat(event.getRole()).isEqualTo("USER");
    }

    // --- UserRegisteredEvent ---

    @Test
    void userRegisteredEvent_setters_actualizanCampos() {
        UserRegisteredEvent event = new UserRegisteredEvent();
        event.setUserId(3L);
        event.setUsername("newuser");
        event.setEmail("new@test.com");
        event.setRole("ADMIN");

        assertThat(event.getUserId()).isEqualTo(3L);
        assertThat(event.getUsername()).isEqualTo("newuser");
        assertThat(event.getEmail()).isEqualTo("new@test.com");
        assertThat(event.getRole()).isEqualTo("ADMIN");
    }

    // --- SendNotificationCommand ---

    @Test
    void sendNotificationCommand_setters_actualizanCampos() {
        SendNotificationCommand cmd = new SendNotificationCommand();
        cmd.setTipo("EMAIL");
        cmd.setDestinatario("dest@test.com");
        cmd.setAsunto("Asunto cmd");
        cmd.setContenido("Contenido cmd");
        cmd.setPlantillaCodigo("PLANTILLA_01");
        cmd.setVariables(Map.of("key", "value"));

        assertThat(cmd.getTipo()).isEqualTo("EMAIL");
        assertThat(cmd.getDestinatario()).isEqualTo("dest@test.com");
        assertThat(cmd.getAsunto()).isEqualTo("Asunto cmd");
        assertThat(cmd.getContenido()).isEqualTo("Contenido cmd");
        assertThat(cmd.getPlantillaCodigo()).isEqualTo("PLANTILLA_01");
        assertThat(cmd.getVariables()).containsKey("key");
    }

    // --- ReservaNotificationEvent ---

    @Test
    void reservaNotificationEvent_setters_actualizanCampos() {
        ReservaNotificationEvent event = new ReservaNotificationEvent();
        event.setEventType("CONFIRMED");
        event.setReservaId(100L);
        event.setUserId(5L);
        event.setClienteNombre("Maria");
        event.setClienteEmail("maria@test.com");
        event.setHotelNombre("Hotel Test");
        event.setHotelDireccion("Calle 123");
        event.setFechaInicio("2024-06-01");
        event.setFechaFin("2024-06-07");
        event.setFechaCancelacion(null);
        event.setTotal(500.0);
        event.setEstado("CONFIRMED");
        event.setMotivoCancelacion(null);

        ReservaNotificationEvent.HabitacionDetalle hab = new ReservaNotificationEvent.HabitacionDetalle();
        hab.setHabitacionId(10L);
        hab.setPrecioNoche(120.0);
        event.setHabitaciones(List.of(hab));

        assertThat(event.getEventType()).isEqualTo("CONFIRMED");
        assertThat(event.getReservaId()).isEqualTo(100L);
        assertThat(event.getUserId()).isEqualTo(5L);
        assertThat(event.getClienteNombre()).isEqualTo("Maria");
        assertThat(event.getClienteEmail()).isEqualTo("maria@test.com");
        assertThat(event.getHotelNombre()).isEqualTo("Hotel Test");
        assertThat(event.getHotelDireccion()).isEqualTo("Calle 123");
        assertThat(event.getFechaInicio()).isEqualTo("2024-06-01");
        assertThat(event.getFechaFin()).isEqualTo("2024-06-07");
        assertThat(event.getTotal()).isEqualTo(500.0);
        assertThat(event.getEstado()).isEqualTo("CONFIRMED");
        assertThat(event.getHabitaciones()).hasSize(1);
        assertThat(event.getHabitaciones().get(0).getHabitacionId()).isEqualTo(10L);
        assertThat(event.getHabitaciones().get(0).getPrecioNoche()).isEqualTo(120.0);
    }
}
