package com.hotel.notificacion.helpers.mappers;

import com.hotel.notificacion.api.dto.NotificacionResponse;
import com.hotel.notificacion.api.dto.NotificacionUsuarioResponse;
import com.hotel.notificacion.core.notificacion.model.Notificacion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NotificacionMapperTest {

    // --- toResponse() ---

    @Test
    void toResponse_conNotificacionCompleta_mapeoCompleto() {
        Notificacion n = buildNotificacion();

        NotificacionResponse response = NotificacionMapper.toResponse(n);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTipo()).isEqualTo(NotificacionResponse.TipoEnum.EMAIL);
        assertThat(response.getDestinatario()).isEqualTo("user@test.com");
        assertThat(response.getAsunto()).isEqualTo("Asunto test");
        assertThat(response.getContenido()).isEqualTo("Contenido test");
        assertThat(response.getEstado()).isEqualTo(NotificacionResponse.EstadoEnum.ENVIADA);
        assertThat(response.getIntentos()).isEqualTo(1);
        assertThat(response.getErrorMensaje()).isNull();
        assertThat(response.getFechaCreacion()).isNotNull();
        assertThat(response.getFechaEnvio()).isNotNull();
    }

    @Test
    void toResponse_notificacionNull_retornaNull() {
        assertThat(NotificacionMapper.toResponse(null)).isNull();
    }

    @Test
    void toResponse_sinFechas_fechasNull() {
        Notificacion n = buildNotificacion();
        n.setFechaCreacion(null);
        n.setFechaEnvio(null);

        NotificacionResponse response = NotificacionMapper.toResponse(n);

        assertThat(response.getFechaCreacion()).isNull();
        assertThat(response.getFechaEnvio()).isNull();
    }

    @Test
    void toResponse_conErrorMensaje_mapeaErrorMensaje() {
        Notificacion n = buildNotificacion();
        n.setEstado("FALLIDA");
        n.setErrorMensaje("SMTP connection refused");

        NotificacionResponse response = NotificacionMapper.toResponse(n);

        assertThat(response.getErrorMensaje()).isEqualTo("SMTP connection refused");
        assertThat(response.getEstado()).isEqualTo(NotificacionResponse.EstadoEnum.FALLIDA);
    }

    // --- toUsuarioResponse() ---

    @Test
    void toUsuarioResponse_conNotificacionCompleta_mapeoCompleto() {
        Notificacion n = buildNotificacion();
        n.setMensaje("Mensaje notificacion");
        n.setEventType("LOGIN");
        n.setLeida(true);

        NotificacionUsuarioResponse response = NotificacionMapper.toUsuarioResponse(n);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitulo()).isEqualTo("Asunto test");
        assertThat(response.getMensaje()).isEqualTo("Mensaje notificacion");
        assertThat(response.getTipo()).isEqualTo("EMAIL");
        assertThat(response.getEventType()).isEqualTo("LOGIN");
        assertThat(response.getLeida()).isTrue();
        assertThat(response.getFechaCreacion()).isNotNull();
    }

    @Test
    void toUsuarioResponse_notificacionNull_retornaNull() {
        assertThat(NotificacionMapper.toUsuarioResponse(null)).isNull();
    }

    @Test
    void toUsuarioResponse_mensajeNull_usaContenido() {
        Notificacion n = buildNotificacion();
        n.setMensaje(null);
        n.setContenido("Contenido fallback");

        NotificacionUsuarioResponse response = NotificacionMapper.toUsuarioResponse(n);

        assertThat(response.getMensaje()).isEqualTo("Contenido fallback");
    }

    @Test
    void toUsuarioResponse_leidaNull_retornaFalse() {
        Notificacion n = buildNotificacion();
        n.setLeida(null);

        NotificacionUsuarioResponse response = NotificacionMapper.toUsuarioResponse(n);

        assertThat(response.getLeida()).isFalse();
    }

    @Test
    void toUsuarioResponse_sinFechaCreacion_fechaNull() {
        Notificacion n = buildNotificacion();
        n.setFechaCreacion(null);

        NotificacionUsuarioResponse response = NotificacionMapper.toUsuarioResponse(n);

        assertThat(response.getFechaCreacion()).isNull();
    }

    // --- constructor privado ---

    @Test
    void constructor_lanzaUnsupportedOperationException() {
        assertThatThrownBy(() -> {
            var constructor = NotificacionMapper.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    // --- Helper ---

    private Notificacion buildNotificacion() {
        Notificacion n = new Notificacion();
        n.setId(1L);
        n.setTipo("EMAIL");
        n.setDestinatario("user@test.com");
        n.setAsunto("Asunto test");
        n.setContenido("Contenido test");
        n.setEstado("ENVIADA");
        n.setIntentos(1);
        n.setLeida(false);
        n.setFechaCreacion(LocalDateTime.now());
        n.setFechaEnvio(LocalDateTime.now());
        return n;
    }
}
