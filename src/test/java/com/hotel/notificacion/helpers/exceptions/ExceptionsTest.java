package com.hotel.notificacion.helpers.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionsTest {

    // --- EntityNotFoundException ---

    @Test
    void entityNotFoundException_mensajeContienEntityNameEId() {
        EntityNotFoundException ex = new EntityNotFoundException("Notificacion", 42L);

        assertThat(ex.getMessage()).contains("Notificacion");
        assertThat(ex.getMessage()).contains("42");
        assertThat(ex.getEntityName()).isEqualTo("Notificacion");
        assertThat(ex.getIdentifier()).isEqualTo(42L);
    }

    @Test
    void entityNotFoundException_identificadorString() {
        EntityNotFoundException ex = new EntityNotFoundException("Plantilla", "codigo-abc");

        assertThat(ex.getMessage()).contains("codigo-abc");
        assertThat(ex.getIdentifier()).isEqualTo("codigo-abc");
    }

    // --- ValidationException ---

    @Test
    void validationException_mensajeYFieldErrors() {
        ValidationException ex = new ValidationException("email", "formato invalido");

        assertThat(ex.getMessage()).isEqualTo("formato invalido");
        assertThat(ex.getFieldErrors()).containsKey("email");
        assertThat(ex.getFieldErrors().get("email")).isEqualTo("formato invalido");
    }

    @Test
    void validationException_fieldErrorsInmutable() {
        ValidationException ex = new ValidationException("campo", "error");

        assertThat(ex.getFieldErrors()).hasSize(1);
    }
}
