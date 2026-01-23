package com.hotel.notificacion.api;

import com.hotel.notificacion.api.dto.MessageResponse;
import com.hotel.notificacion.api.dto.PlantillaRequest;
import com.hotel.notificacion.api.dto.PlantillaResponse;
import com.hotel.notificacion.core.plantilla.model.Plantilla;
import com.hotel.notificacion.core.plantilla.service.PlantillaService;
import com.hotel.notificacion.helpers.mappers.PlantillaMapper;
import com.hotel.notificacion.internal.AuthInternalApi;
import com.hotel.notificacion.internal.dto.AuthTokenValidationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;

@RestController
public class PlantillasController implements PlantillasApi {

    private final PlantillaService plantillaService;
    private final AuthInternalApi authInternalApi;
    private final NativeWebRequest request;

    public PlantillasController(PlantillaService plantillaService,
                                AuthInternalApi authInternalApi,
                                NativeWebRequest request) {
        this.plantillaService = plantillaService;
        this.authInternalApi = authInternalApi;
        this.request = request;
    }

    @Override
    public ResponseEntity<List<PlantillaResponse>> listarPlantillas() {
        AuthTokenValidationResponse auth = resolveAuth();
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Plantilla> plantillas = plantillaService.listar();
        return ResponseEntity.ok(plantillas.stream().map(PlantillaMapper::toResponse).toList());
    }

    @Override
    public ResponseEntity<PlantillaResponse> crearPlantilla(PlantillaRequest request) {
        AuthTokenValidationResponse auth = resolveAuth();
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Plantilla plantilla = plantillaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(PlantillaMapper.toResponse(plantilla));
    }

    @Override
    public ResponseEntity<PlantillaResponse> obtenerPlantilla(Long id) {
        AuthTokenValidationResponse auth = resolveAuth();
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Plantilla plantilla = plantillaService.buscarPorId(id);
        return ResponseEntity.ok(PlantillaMapper.toResponse(plantilla));
    }

    @Override
    public ResponseEntity<PlantillaResponse> actualizarPlantilla(Long id, PlantillaRequest request) {
        AuthTokenValidationResponse auth = resolveAuth();
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Plantilla plantilla = plantillaService.actualizar(id, request);
        return ResponseEntity.ok(PlantillaMapper.toResponse(plantilla));
    }

    @Override
    public ResponseEntity<MessageResponse> eliminarPlantilla(Long id) {
        AuthTokenValidationResponse auth = resolveAuth();
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!isAdmin(auth)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        plantillaService.eliminar(id);
        MessageResponse response = new MessageResponse();
        response.setMessage("Plantilla eliminada correctamente");
        return ResponseEntity.ok(response);
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    private AuthTokenValidationResponse resolveAuth() {
        String authorization = resolveAuthorization();
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.substring(7);
        AuthTokenValidationResponse response = authInternalApi.validateToken(token).orElse(null);
        if (response == null || !Boolean.TRUE.equals(response.getValid())) {
            return null;
        }
        return response;
    }

    private String resolveAuthorization() {
        Optional<NativeWebRequest> request = getRequest();
        if (request.isEmpty()) {
            return null;
        }
        return request.get().getHeader("Authorization");
    }

    private boolean isAdmin(AuthTokenValidationResponse auth) {
        return auth.getRole() != null && "ADMIN".equalsIgnoreCase(auth.getRole());
    }
}
