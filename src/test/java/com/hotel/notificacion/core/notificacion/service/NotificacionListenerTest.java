package com.hotel.notificacion.core.notificacion.service;

import com.hotel.notificacion.internal.events.PasswordResetEvent;
import com.hotel.notificacion.internal.events.SendNotificationCommand;
import com.hotel.notificacion.internal.events.UserLoginEvent;
import com.hotel.notificacion.internal.events.UserRegisteredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionListenerTest {

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private NotificacionListener notificacionListener;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificacionListener, "sesionEnabled", true);
    }

    // --- handleUserRegistered ---

    @Test
    void handleUserRegistered_sesionEnabledConEmail_creaNotificacion() {
        UserRegisteredEvent event = new UserRegisteredEvent();
        event.setEmail("user@test.com");
        event.setUsername("testuser");
        event.setRole("USER");

        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html>Welcome</html>");

        notificacionListener.handleUserRegistered(event);

        verify(notificacionService).renderTemplate(eq("templates/welcome-email.html"), anyMap());
        verify(notificacionService).crearDesdeEvento(eq("EMAIL"), eq("user@test.com"), eq("Bienvenido a LuxeStay"), anyString());
    }

    @Test
    void handleUserRegistered_sesionDisabled_noHaceNada() {
        ReflectionTestUtils.setField(notificacionListener, "sesionEnabled", false);
        UserRegisteredEvent event = new UserRegisteredEvent();
        event.setEmail("user@test.com");

        notificacionListener.handleUserRegistered(event);

        verifyNoInteractions(notificacionService);
    }

    @Test
    void handleUserRegistered_emailNull_noHaceNada() {
        UserRegisteredEvent event = new UserRegisteredEvent();
        event.setEmail(null);

        notificacionListener.handleUserRegistered(event);

        verifyNoInteractions(notificacionService);
    }

    @Test
    void handleUserRegistered_camposNulos_usaStringVacio() {
        UserRegisteredEvent event = new UserRegisteredEvent();
        event.setEmail("user@test.com");
        event.setUsername(null);
        event.setRole(null);

        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html></html>");

        notificacionListener.handleUserRegistered(event);

        verify(notificacionService).crearDesdeEvento(any(), any(), any(), any());
    }

    // --- handleUserLogin ---

    @Test
    void handleUserLogin_sesionEnabledConEmail_creaNotificacion() {
        UserLoginEvent event = new UserLoginEvent();
        event.setEmail("user@test.com");
        event.setUsername("testuser");
        event.setUserId(1L);
        event.setRole("USER");

        notificacionListener.handleUserLogin(event);

        verify(notificacionService).crearDesdeEventoConUserIdYEventType(
                eq("LOGIN"), eq("user@test.com"), eq("Bienvenido nuevamente"), anyString(), eq(1L), eq("LOGIN")
        );
    }

    @Test
    void handleUserLogin_sesionDisabled_noHaceNada() {
        ReflectionTestUtils.setField(notificacionListener, "sesionEnabled", false);
        UserLoginEvent event = new UserLoginEvent();
        event.setEmail("user@test.com");

        notificacionListener.handleUserLogin(event);

        verifyNoInteractions(notificacionService);
    }

    @Test
    void handleUserLogin_emailNull_noHaceNada() {
        UserLoginEvent event = new UserLoginEvent();
        event.setEmail(null);

        notificacionListener.handleUserLogin(event);

        verifyNoInteractions(notificacionService);
    }

    @Test
    void handleUserLogin_roleAdmin_noHaceNada() {
        UserLoginEvent event = new UserLoginEvent();
        event.setEmail("admin@test.com");
        event.setRole("ADMIN");

        notificacionListener.handleUserLogin(event);

        verifyNoInteractions(notificacionService);
    }

    @Test
    void handleUserLogin_usernameNull_usaStringVacio() {
        UserLoginEvent event = new UserLoginEvent();
        event.setEmail("user@test.com");
        event.setUsername(null);
        event.setRole("USER");
        event.setUserId(2L);

        notificacionListener.handleUserLogin(event);

        ArgumentCaptor<String> contenidoCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificacionService).crearDesdeEventoConUserIdYEventType(
                any(), any(), any(), contenidoCaptor.capture(), any(), any()
        );
        assertThat(contenidoCaptor.getValue()).contains("Bienvenido nuevamente");
    }

    // --- handlePasswordReset ---

    @Test
    void handlePasswordReset_conEmail_creaNotificacion() {
        PasswordResetEvent event = new PasswordResetEvent();
        event.setEmail("user@test.com");
        event.setUsername("testuser");
        event.setCode("123456");

        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html>Reset</html>");

        notificacionListener.handlePasswordReset(event);

        verify(notificacionService).renderTemplate(eq("templates/password-reset-email.html"), anyMap());
        verify(notificacionService).crearDesdeEvento(eq("EMAIL"), eq("user@test.com"), eq("Restablece tu contraseña"), anyString());
    }

    @Test
    void handlePasswordReset_emailNull_noHaceNada() {
        PasswordResetEvent event = new PasswordResetEvent();
        event.setEmail(null);

        notificacionListener.handlePasswordReset(event);

        verifyNoInteractions(notificacionService);
    }

    @Test
    void handlePasswordReset_camposNulos_usaStringVacio() {
        PasswordResetEvent event = new PasswordResetEvent();
        event.setEmail("user@test.com");
        event.setUsername(null);
        event.setCode(null);

        when(notificacionService.renderTemplate(anyString(), anyMap())).thenReturn("<html></html>");

        notificacionListener.handlePasswordReset(event);

        verify(notificacionService).crearDesdeEvento(any(), any(), any(), any());
    }

    // --- handleSendNotification ---

    @Test
    void handleSendNotification_conDestinatario_creaNotificacion() {
        SendNotificationCommand command = new SendNotificationCommand();
        command.setDestinatario("user@test.com");
        command.setTipo("EMAIL");
        command.setAsunto("Asunto");
        command.setContenido("Contenido");

        notificacionListener.handleSendNotification(command);

        verify(notificacionService).crearDesdeEvento(eq("EMAIL"), eq("user@test.com"), eq("Asunto"), eq("Contenido"));
    }

    @Test
    void handleSendNotification_destinatarioNull_noHaceNada() {
        SendNotificationCommand command = new SendNotificationCommand();
        command.setDestinatario(null);

        notificacionListener.handleSendNotification(command);

        verifyNoInteractions(notificacionService);
    }

    @Test
    void handleSendNotification_asuntoNull_usaDefecto() {
        SendNotificationCommand command = new SendNotificationCommand();
        command.setDestinatario("user@test.com");
        command.setTipo("PUSH");
        command.setAsunto(null);
        command.setContenido("Contenido");

        notificacionListener.handleSendNotification(command);

        verify(notificacionService).crearDesdeEvento(eq("PUSH"), eq("user@test.com"), eq("Notificacion"), eq("Contenido"));
    }

    // --- handleUnknown ---

    @Test
    void handleUnknown_payload_noHaceNada() {
        // Solo verifica que no lanza excepcion
        notificacionListener.handleUnknown("payload desconocido");
        verifyNoInteractions(notificacionService);
    }
}
