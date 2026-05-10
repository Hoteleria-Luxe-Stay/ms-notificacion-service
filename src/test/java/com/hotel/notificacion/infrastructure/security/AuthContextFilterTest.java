package com.hotel.notificacion.infrastructure.security;

import com.hotel.notificacion.internal.dto.AuthTokenValidationResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthContextFilterTest {

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthContextFilter filter;

    @Test
    void doFilterInternal_sinAuthorizationHeader_continuaFiltro() throws Exception {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtDecoder, never()).decode(any());
    }

    @Test
    void doFilterInternal_headerSinBearer_continuaFiltroSinDecode() throws Exception {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic dXNlcjpwYXNz");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtDecoder, never()).decode(any());
    }

    @Test
    void doFilterInternal_tokenValido_setAuthContext() throws Exception {
        String token = "valid.jwt.token";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);

        Jwt jwt = buildJwt(42L, "ROLE_USER");
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        filter.doFilterInternal(request, response, filterChain);

        ArgumentCaptor<AuthTokenValidationResponse> captor =
                ArgumentCaptor.forClass(AuthTokenValidationResponse.class);
        verify(request).setAttribute(eq(AuthContextFilter.AUTH_CONTEXT_KEY), captor.capture());
        AuthTokenValidationResponse auth = captor.getValue();
        assertThat(auth.getValid()).isTrue();
        assertThat(auth.getEmail()).isEqualTo("user@test.com");
        assertThat(auth.getUserId()).isEqualTo(42L);
        assertThat(auth.getRole()).isEqualTo("USER");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_tokenValido_quitaPrefixoROLE() throws Exception {
        String token = "valid.jwt.token";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);

        Jwt jwt = buildJwt(1L, "ROLE_ADMIN");
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        filter.doFilterInternal(request, response, filterChain);

        ArgumentCaptor<AuthTokenValidationResponse> captor =
                ArgumentCaptor.forClass(AuthTokenValidationResponse.class);
        verify(request).setAttribute(eq(AuthContextFilter.AUTH_CONTEXT_KEY), captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo("ADMIN");
    }

    @Test
    void doFilterInternal_tokenInvalido_continuaFiltroSinSetearContexto() throws Exception {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer invalid.token");
        when(jwtDecoder.decode("invalid.token")).thenThrow(new JwtException("bad token"));

        filter.doFilterInternal(request, response, filterChain);

        verify(request, never()).setAttribute(any(), any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_tokenSinUserIdNiRoles_noSetAuthContext() throws Exception {
        String token = "valid.jwt.token";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);

        Jwt jwt = Jwt.withTokenValue(token)
                .header("alg", "RS256")
                .subject("user@test.com")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .claims(c -> {})
                .build();
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        filter.doFilterInternal(request, response, filterChain);

        verify(request, never()).setAttribute(any(), any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_soloUserId_setAuthContextSinRole() throws Exception {
        String token = "valid.jwt.token";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);

        Jwt jwt = Jwt.withTokenValue(token)
                .header("alg", "RS256")
                .subject("user@test.com")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .claims(c -> c.put("userId", 5L))
                .build();
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        filter.doFilterInternal(request, response, filterChain);

        ArgumentCaptor<AuthTokenValidationResponse> captor =
                ArgumentCaptor.forClass(AuthTokenValidationResponse.class);
        verify(request).setAttribute(eq(AuthContextFilter.AUTH_CONTEXT_KEY), captor.capture());
        assertThat(captor.getValue().getRole()).isNull();
    }

    // --- Helper ---

    private Jwt buildJwt(Long userId, String roles) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("user@test.com")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .claims(c -> {
                    c.put("userId", userId);
                    c.put("roles", roles);
                })
                .build();
    }
}
