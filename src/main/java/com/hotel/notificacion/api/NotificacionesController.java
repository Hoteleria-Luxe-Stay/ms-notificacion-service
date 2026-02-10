package com.hotel.notificacion.api;

import com.hotel.notificacion.api.dto.EnviarNotificacionRequest;
import com.hotel.notificacion.api.dto.EstadisticasResponse;
import com.hotel.notificacion.api.dto.HealthResponse;
import com.hotel.notificacion.api.dto.MessageResponse;
import com.hotel.notificacion.api.dto.NotificacionResponse;
import com.hotel.notificacion.api.dto.NotificacionUsuarioResponse;
import com.hotel.notificacion.core.notificacion.model.Notificacion;
import com.hotel.notificacion.core.notificacion.service.NotificacionService;
import com.hotel.notificacion.helpers.mappers.NotificacionMapper;
import com.hotel.notificacion.internal.dto.AuthTokenValidationResponse;
import com.hotel.notificacion.infrastructure.security.AuthContextFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class NotificacionesController implements NotificacionesApi {

    private final NotificacionService notificacionService;
    private final NativeWebRequest request;

    public NotificacionesController(NotificacionService notificacionService,
                                    NativeWebRequest request) {
        this.notificacionService = notificacionService;
        this.request = request;
    }

    @Override
    public ResponseEntity<List<NotificacionResponse>> listarNotificaciones(
            String tipo,
            String estado,
            LocalDate fechaDesde,
            LocalDate fechaHasta) {
        AuthTokenValidationResponse auth = getAuth();
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Notificacion> notificaciones = notificacionService.listar(tipo, estado, fechaDesde, fechaHasta);
        return ResponseEntity.ok(notificaciones.stream().map(NotificacionMapper::toResponse).toList());
    }

    @Override
    public ResponseEntity<NotificacionResponse> obtenerNotificacion(Long id) {
        AuthTokenValidationResponse auth = getAuth();
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Notificacion notificacion = notificacionService.buscarPorId(id);
        return ResponseEntity.ok(NotificacionMapper.toResponse(notificacion));
    }

    @Override
    public ResponseEntity<NotificacionResponse> reenviarNotificacion(Long id) {
        AuthTokenValidationResponse auth = getAuth();
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Notificacion notificacion = notificacionService.reenviar(id);
        return ResponseEntity.ok(NotificacionMapper.toResponse(notificacion));
    }

    @Override
    public ResponseEntity<NotificacionResponse> enviarNotificacion(EnviarNotificacionRequest request) {
        AuthTokenValidationResponse auth = getAuth();
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Long userId = auth != null ? auth.getUserId() : null;
        Notificacion notificacion = notificacionService.enviarManual(request, userId);
        return ResponseEntity.ok(NotificacionMapper.toResponse(notificacion));
    }

    @Override
    public ResponseEntity<List<NotificacionUsuarioResponse>> listarMisNotificaciones(Boolean leida, String tipo) {
        AuthTokenValidationResponse auth = getAuth();
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Notificacion> notificaciones = notificacionService.listarPorUsuario(
                auth != null ? auth.getUserId() : null,
                auth != null ? auth.getEmail() : null,
                leida,
                tipo
        );
        return ResponseEntity.ok(notificaciones.stream().map(NotificacionMapper::toUsuarioResponse).toList());
    }

    @Override
    public ResponseEntity<MessageResponse> marcarComoLeida(Long id) {
        AuthTokenValidationResponse auth = getAuth();
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        notificacionService.marcarComoLeida(id,
                auth != null ? auth.getUserId() : null,
                auth != null ? auth.getEmail() : null);

        MessageResponse response = new MessageResponse();
        response.setMessage("Notificacion marcada como leida");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<MessageResponse> marcarTodasComoLeidas() {
        AuthTokenValidationResponse auth = getAuth();
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        notificacionService.marcarTodasComoLeidas(
                auth != null ? auth.getUserId() : null,
                auth != null ? auth.getEmail() : null
        );

        MessageResponse response = new MessageResponse();
        response.setMessage("Todas las notificaciones fueron marcadas como leidas");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<EstadisticasResponse> obtenerEstadisticas(
            LocalDate fechaDesde,
            LocalDate fechaHasta) {
        AuthTokenValidationResponse auth = getAuth();
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Notificacion> notificaciones = notificacionService.listar(null, null, fechaDesde, fechaHasta);
        EstadisticasResponse response = new EstadisticasResponse();

        long totalEnviadas = notificaciones.stream().filter(n -> "ENVIADA".equals(n.getEstado())).count();
        long totalFallidas = notificaciones.stream().filter(n -> "FALLIDA".equals(n.getEstado())).count();
        long totalPendientes = notificaciones.stream().filter(n -> "PENDIENTE".equals(n.getEstado())).count();

        Map<String, Integer> porTipo = new HashMap<>();
        notificaciones.forEach(notificacion -> porTipo.merge(notificacion.getTipo(), 1, Integer::sum));

        response.setTotalEnviadas((int) totalEnviadas);
        response.setTotalFallidas((int) totalFallidas);
        response.setTotalPendientes((int) totalPendientes);
        response.setPorTipo(porTipo);
        if (!notificaciones.isEmpty()) {
            response.setTasaExito((double) totalEnviadas / (double) notificaciones.size());
        }

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<HealthResponse> healthCheck() {
        HealthResponse response = new HealthResponse();
        response.setStatus(HealthResponse.StatusEnum.UP);
        response.setRabbitmq(HealthResponse.RabbitmqEnum.UP);
        response.setDatabase(HealthResponse.DatabaseEnum.UP);
        response.setTimestamp(OffsetDateTime.now());
        return ResponseEntity.ok(response);
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    private boolean isAdmin(AuthTokenValidationResponse auth) {
        return auth.getRole() != null && "ADMIN".equalsIgnoreCase(auth.getRole());
    }

    private AuthTokenValidationResponse getAuth() {
        Optional<NativeWebRequest> request = getRequest();
        if (request.isEmpty()) {
            return null;
        }
        Object value = request.get().getAttribute(AuthContextFilter.AUTH_CONTEXT_KEY, RequestAttributes.SCOPE_REQUEST);
        if (value instanceof AuthTokenValidationResponse response) {
            return response;
        }
        return null;
    }
}
