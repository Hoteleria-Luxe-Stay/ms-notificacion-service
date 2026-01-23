package com.hotel.notificacion.helpers.mappers;

import com.hotel.notificacion.api.dto.NotificacionResponse;
import com.hotel.notificacion.api.dto.NotificacionUsuarioResponse;
import com.hotel.notificacion.core.notificacion.model.Notificacion;

import java.time.ZoneOffset;

public class NotificacionMapper {

    private NotificacionMapper() {
        throw new UnsupportedOperationException("This class should never be instantiated");
    }

    public static NotificacionResponse toResponse(Notificacion notificacion) {
        if (notificacion == null) {
            return null;
        }

        NotificacionResponse response = new NotificacionResponse();
        response.setId(notificacion.getId());
        response.setTipo(NotificacionResponse.TipoEnum.fromValue(notificacion.getTipo()));
        response.setDestinatario(notificacion.getDestinatario());
        response.setAsunto(notificacion.getAsunto());
        response.setContenido(notificacion.getContenido());
        response.setEstado(NotificacionResponse.EstadoEnum.fromValue(notificacion.getEstado()));
        response.setIntentos(notificacion.getIntentos());
        response.setErrorMensaje(notificacion.getErrorMensaje());
        if (notificacion.getFechaCreacion() != null) {
            response.setFechaCreacion(notificacion.getFechaCreacion().atOffset(ZoneOffset.UTC));
        }
        if (notificacion.getFechaEnvio() != null) {
            response.setFechaEnvio(notificacion.getFechaEnvio().atOffset(ZoneOffset.UTC));
        }
        return response;
    }

    public static NotificacionUsuarioResponse toUsuarioResponse(Notificacion notificacion) {
        if (notificacion == null) {
            return null;
        }

        NotificacionUsuarioResponse response = new NotificacionUsuarioResponse();
        response.setId(notificacion.getId());
        response.setTitulo(notificacion.getAsunto());
        response.setMensaje(notificacion.getContenido());
        response.setTipo(notificacion.getTipo());
        response.setLeida(Boolean.TRUE.equals(notificacion.getLeida()));
        if (notificacion.getFechaCreacion() != null) {
            response.setFechaCreacion(notificacion.getFechaCreacion().atOffset(ZoneOffset.UTC));
        }
        return response;
    }
}
