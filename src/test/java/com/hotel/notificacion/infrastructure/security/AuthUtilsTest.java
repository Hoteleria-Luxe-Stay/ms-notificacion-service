package com.hotel.notificacion.infrastructure.security;

import com.hotel.notificacion.internal.dto.AuthTokenValidationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthUtilsTest {

    // --- getAuth() ---

    @Test
    void getAuth_requestNull_retornaNull() {
        assertThat(AuthUtils.getAuth(null)).isNull();
    }

    @Test
    void getAuth_atributoNoExiste_retornaNull() {
        NativeWebRequest request = mock(NativeWebRequest.class);
        when(request.getAttribute(AuthContextFilter.AUTH_CONTEXT_KEY, RequestAttributes.SCOPE_REQUEST))
                .thenReturn(null);

        assertThat(AuthUtils.getAuth(request)).isNull();
    }

    @Test
    void getAuth_atributoNoEsAuthTokenValidationResponse_retornaNull() {
        NativeWebRequest request = mock(NativeWebRequest.class);
        when(request.getAttribute(AuthContextFilter.AUTH_CONTEXT_KEY, RequestAttributes.SCOPE_REQUEST))
                .thenReturn("string incorrecto");

        assertThat(AuthUtils.getAuth(request)).isNull();
    }

    @Test
    void getAuth_atributoEsAuthTokenValidationResponse_retornaResponse() {
        NativeWebRequest request = mock(NativeWebRequest.class);
        AuthTokenValidationResponse auth = new AuthTokenValidationResponse();
        auth.setUserId(1L);
        auth.setEmail("user@test.com");
        when(request.getAttribute(AuthContextFilter.AUTH_CONTEXT_KEY, RequestAttributes.SCOPE_REQUEST))
                .thenReturn(auth);

        AuthTokenValidationResponse result = AuthUtils.getAuth(request);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
    }

    // --- isAdmin() ---

    @Test
    void isAdmin_authNull_retornaFalse() {
        assertThat(AuthUtils.isAdmin(null)).isFalse();
    }

    @Test
    void isAdmin_roleNull_retornaFalse() {
        AuthTokenValidationResponse auth = new AuthTokenValidationResponse();
        auth.setRole(null);

        assertThat(AuthUtils.isAdmin(auth)).isFalse();
    }

    @Test
    void isAdmin_roleAdmin_retornaTrue() {
        AuthTokenValidationResponse auth = new AuthTokenValidationResponse();
        auth.setRole("ADMIN");

        assertThat(AuthUtils.isAdmin(auth)).isTrue();
    }

    @Test
    void isAdmin_roleAdminLowercase_retornaTrue() {
        AuthTokenValidationResponse auth = new AuthTokenValidationResponse();
        auth.setRole("admin");

        assertThat(AuthUtils.isAdmin(auth)).isTrue();
    }

    @Test
    void isAdmin_roleUser_retornaFalse() {
        AuthTokenValidationResponse auth = new AuthTokenValidationResponse();
        auth.setRole("USER");

        assertThat(AuthUtils.isAdmin(auth)).isFalse();
    }

    // --- constructor privado ---

    @Test
    void constructor_lanzaUnsupportedOperationException() {
        assertThatThrownBy(() -> {
            var constructor = AuthUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).hasCauseInstanceOf(UnsupportedOperationException.class);
    }
}
