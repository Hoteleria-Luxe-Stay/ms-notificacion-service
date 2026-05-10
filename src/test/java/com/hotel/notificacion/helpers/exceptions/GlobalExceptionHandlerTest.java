package com.hotel.notificacion.helpers.exceptions;

import com.hotel.notificacion.api.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/v1/test");
    }

    @Test
    void handleEntityNotFoundException_retorna404() {
        EntityNotFoundException ex = new EntityNotFoundException("Notificacion", 1L);

        ResponseEntity<ErrorResponse> response = handler.handleEntityNotFoundException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/test");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void handleEntityNotFoundException_mensajeContieneEntidadEId() {
        EntityNotFoundException ex = new EntityNotFoundException("Plantilla", 42L);

        ResponseEntity<ErrorResponse> response = handler.handleEntityNotFoundException(ex, request);

        assertThat(response.getBody().getMessage()).contains("Plantilla");
        assertThat(response.getBody().getMessage()).contains("42");
    }

    @Test
    void handleValidationException_retorna400() {
        ValidationException ex = new ValidationException("email", "formato invalido");

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Validation Error");
        assertThat(response.getBody().getMessage()).isEqualTo("formato invalido");
    }

    @Test
    void handleMethodArgumentNotValid_retorna400ConValidationFailed() throws NoSuchMethodException {
        Method method = DummyTarget.class.getMethod("dummy", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "email", "must not be blank"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentNotValid(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError()).isEqualTo("Validation Failed");
        assertThat(response.getBody().getMessage()).contains("validación");
    }

    @Test
    void handleRuntimeException_retorna500() {
        RuntimeException ex = new RuntimeException("error interno");

        ResponseEntity<ErrorResponse> response = handler.handleRuntimeException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage()).isEqualTo("error interno");
    }

    @Test
    void handleRuntimeException_pathSetCorrectamente() {
        RuntimeException ex = new RuntimeException("boom");

        ResponseEntity<ErrorResponse> response = handler.handleRuntimeException(ex, request);

        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/test");
    }

    @Test
    void buildErrorResponse_timestampNoNulo() {
        RuntimeException ex = new RuntimeException("test");

        ResponseEntity<ErrorResponse> response = handler.handleRuntimeException(ex, request);

        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    private static class DummyTarget {
        public void dummy(String arg) { /* no-op */ }
    }
}
