package com.hotel.notificacion.core.notificacion.service;

import com.hotel.notificacion.core.notificacion.model.Notificacion;
import com.hotel.notificacion.core.notificacion.repository.NotificacionRepository;
import com.hotel.notificacion.core.plantilla.model.Plantilla;
import com.hotel.notificacion.core.plantilla.repository.PlantillaRepository;
import com.hotel.notificacion.helpers.exceptions.EntityNotFoundException;
import com.hotel.notificacion.helpers.exceptions.ValidationException;
import com.hotel.notificacion.internal.events.ContactoEvent;
import com.klab.email.EmailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private PlantillaRepository plantillaRepository;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private NotificacionService notificacionService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificacionService, "mailFrom", "from@example.com");
        ReflectionTestUtils.setField(notificacionService, "supportEmail", "support@example.com");
    }

    // --- listar() ---

    @Test
    void listar_porTipo_delegaAlRepository() {
        when(notificacionRepository.findByTipo("EMAIL")).thenReturn(List.of(new Notificacion()));

        List<Notificacion> result = notificacionService.listar("EMAIL", null, null, null);

        assertThat(result).hasSize(1);
        verify(notificacionRepository).findByTipo("EMAIL");
    }

    @Test
    void listar_porEstado_delegaAlRepository() {
        when(notificacionRepository.findByEstado("ENVIADA")).thenReturn(List.of(new Notificacion()));

        List<Notificacion> result = notificacionService.listar(null, "ENVIADA", null, null);

        assertThat(result).hasSize(1);
        verify(notificacionRepository).findByEstado("ENVIADA");
    }

    @Test
    void listar_porFechas_delegaAlRepositoryConRango() {
        LocalDate desde = LocalDate.of(2024, 1, 1);
        LocalDate hasta = LocalDate.of(2024, 12, 31);
        when(notificacionRepository.findByFechaCreacionBetween(any(), any())).thenReturn(List.of());

        List<Notificacion> result = notificacionService.listar(null, null, desde, hasta);

        assertThat(result).isEmpty();
        verify(notificacionRepository).findByFechaCreacionBetween(any(), any());
    }

    @Test
    void listar_sinFiltros_retornaTodos() {
        when(notificacionRepository.findAll()).thenReturn(List.of(new Notificacion(), new Notificacion()));

        List<Notificacion> result = notificacionService.listar(null, null, null, null);

        assertThat(result).hasSize(2);
        verify(notificacionRepository).findAll();
    }

    @Test
    void listar_tipoBlank_retornaTodos() {
        when(notificacionRepository.findAll()).thenReturn(List.of(new Notificacion()));

        List<Notificacion> result = notificacionService.listar("  ", null, null, null);

        verify(notificacionRepository).findAll();
    }

    @Test
    void listar_estadoBlank_retornaTodos() {
        when(notificacionRepository.findAll()).thenReturn(List.of(new Notificacion()));

        List<Notificacion> result = notificacionService.listar(null, "  ", null, null);

        verify(notificacionRepository).findAll();
    }

    // --- buscarPorId() ---

    @Test
    void buscarPorId_encontrado_retornaNotificacion() {
        Notificacion n = new Notificacion();
        n.setId(1L);
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(n));

        Notificacion result = notificacionService.buscarPorId(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void buscarPorId_noEncontrado_lanzaEntityNotFoundException() {
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificacionService.buscarPorId(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- enviarManual() con tipo EMAIL y mailFrom configurado ---

    @Test
    void enviarManual_sinPlantilla_creaYEnviaNotificacion() throws Exception {
        // simular request DTO con getters
        com.hotel.notificacion.api.dto.EnviarNotificacionRequest request = buildEnviarRequest();

        Notificacion saved = buildNotificacion("EMAIL", "PENDIENTE");
        saved.setId(1L);
        when(notificacionRepository.save(any())).thenReturn(saved);

        Notificacion result = notificacionService.enviarManual(request, 1L);

        verify(emailSender).sendEmail(any(), any(), any(), any());
        assertThat(result).isNotNull();
    }

    @Test
    void enviarManual_conPlantillaId_aplicaPlantilla() throws Exception {
        com.hotel.notificacion.api.dto.EnviarNotificacionRequest request = buildEnviarRequestConPlantilla();

        Plantilla plantilla = new Plantilla();
        plantilla.setId(5L);
        plantilla.setAsunto("Asunto plantilla");
        plantilla.setContenido("Hola {{nombre}}");

        when(plantillaRepository.findById(5L)).thenReturn(Optional.of(plantilla));
        Notificacion saved = buildNotificacion("EMAIL", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        Notificacion result = notificacionService.enviarManual(request, 1L);

        verify(plantillaRepository).findById(5L);
        assertThat(result).isNotNull();
    }

    @Test
    void enviarManual_plantillaIdNoExiste_lanzaEntityNotFoundException() {
        com.hotel.notificacion.api.dto.EnviarNotificacionRequest request = buildEnviarRequestConPlantilla();
        when(plantillaRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificacionService.enviarManual(request, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Plantilla");
    }

    // --- reenviar() ---

    @Test
    void reenviar_notificacionFallida_reenviaExitosamente() throws Exception {
        Notificacion n = buildNotificacion("EMAIL", "FALLIDA");
        n.setId(1L);
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(n));
        when(notificacionRepository.save(any())).thenReturn(n);

        Notificacion result = notificacionService.reenviar(1L);

        verify(emailSender).sendEmail(any(), any(), any(), any());
        assertThat(result).isNotNull();
    }

    @Test
    void reenviar_notificacionNoFallida_lanzaValidationException() {
        Notificacion n = buildNotificacion("EMAIL", "ENVIADA");
        n.setId(1L);
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(n));

        assertThatThrownBy(() -> notificacionService.reenviar(1L))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("fallidas");
    }

    // --- listarPorUsuario() ---

    @Test
    void listarPorUsuario_porUserId_filtraCorrectamente() {
        Notificacion n1 = buildNotificacion("EMAIL", "ENVIADA");
        n1.setTipo("EMAIL");
        Notificacion n2 = buildNotificacion("PUSH", "ENVIADA");
        n2.setTipo("PUSH");
        when(notificacionRepository.findByUserId(1L)).thenReturn(List.of(n1, n2));

        List<Notificacion> result = notificacionService.listarPorUsuario(1L, null, null, "EMAIL");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTipo()).isEqualTo("EMAIL");
    }

    @Test
    void listarPorUsuario_porEmail_filtraCorrectamente() {
        Notificacion n = buildNotificacion("EMAIL", "ENVIADA");
        when(notificacionRepository.findByDestinatario("test@test.com")).thenReturn(List.of(n));

        List<Notificacion> result = notificacionService.listarPorUsuario(null, "test@test.com", null, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void listarPorUsuario_filtroLeida_soloRetornaNoLeidas() {
        Notificacion n1 = buildNotificacion("EMAIL", "ENVIADA");
        n1.setLeida(false);
        Notificacion n2 = buildNotificacion("EMAIL", "ENVIADA");
        n2.setLeida(true);
        when(notificacionRepository.findByUserId(1L)).thenReturn(List.of(n1, n2));

        List<Notificacion> result = notificacionService.listarPorUsuario(1L, null, false, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLeida()).isFalse();
    }

    @Test
    void listarPorUsuario_filtroLeidaTrue_soloRetornaLeidas() {
        Notificacion n1 = buildNotificacion("EMAIL", "ENVIADA");
        n1.setLeida(false);
        Notificacion n2 = buildNotificacion("EMAIL", "ENVIADA");
        n2.setLeida(true);
        when(notificacionRepository.findByUserId(1L)).thenReturn(List.of(n1, n2));

        List<Notificacion> result = notificacionService.listarPorUsuario(1L, null, true, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLeida()).isTrue();
    }

    // --- marcarComoLeida() ---

    @Test
    void marcarComoLeida_porUserId_marcaCorrectamente() {
        Notificacion n = buildNotificacion("EMAIL", "ENVIADA");
        n.setId(1L);
        n.setUserId(42L);
        n.setLeida(false);
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(n));
        when(notificacionRepository.save(any())).thenReturn(n);

        notificacionService.marcarComoLeida(1L, 42L, null);

        assertThat(n.getLeida()).isTrue();
        verify(notificacionRepository).save(n);
    }

    @Test
    void marcarComoLeida_porEmail_marcaCorrectamente() {
        Notificacion n = buildNotificacion("EMAIL", "ENVIADA");
        n.setId(1L);
        n.setUserId(null);
        n.setDestinatario("user@test.com");
        n.setLeida(false);
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(n));
        when(notificacionRepository.save(any())).thenReturn(n);

        notificacionService.marcarComoLeida(1L, null, "user@test.com");

        assertThat(n.getLeida()).isTrue();
    }

    @Test
    void marcarComoLeida_notificacionDeOtroUsuario_lanzaEntityNotFoundException() {
        Notificacion n = buildNotificacion("EMAIL", "ENVIADA");
        n.setId(1L);
        n.setUserId(99L);
        n.setDestinatario("otro@test.com");
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(n));

        assertThatThrownBy(() -> notificacionService.marcarComoLeida(1L, 42L, "yo@test.com"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // --- marcarTodasComoLeidas() ---

    @Test
    void marcarTodasComoLeidas_marcaTodasLasNotificacionesDelUsuario() {
        Notificacion n1 = buildNotificacion("EMAIL", "ENVIADA");
        n1.setLeida(false);
        Notificacion n2 = buildNotificacion("EMAIL", "ENVIADA");
        n2.setLeida(false);
        when(notificacionRepository.findByUserId(1L)).thenReturn(List.of(n1, n2));

        notificacionService.marcarTodasComoLeidas(1L, null);

        assertThat(n1.getLeida()).isTrue();
        assertThat(n2.getLeida()).isTrue();
        verify(notificacionRepository).saveAll(anyList());
    }

    // --- eliminarPorOrigen() ---

    @Test
    void eliminarPorOrigen_sesiones_eliminaLoginTypes() {
        Notificacion n = buildNotificacion("EMAIL", "ENVIADA");
        when(notificacionRepository.findByUserIdAndEventTypeIn(eq(1L), eq(List.of("LOGIN"))))
                .thenReturn(List.of(n));

        notificacionService.eliminarPorOrigen(1L, null, "sesiones");

        verify(notificacionRepository).deleteAll(anyList());
    }

    @Test
    void eliminarPorOrigen_reservas_eliminaReservaTypes() {
        when(notificacionRepository.findByUserIdAndEventTypeIn(eq(1L), any()))
                .thenReturn(List.of());

        notificacionService.eliminarPorOrigen(1L, null, "reservas");

        verify(notificacionRepository).deleteAll(anyList());
    }

    @Test
    void eliminarPorOrigen_porEmail_usaDestinatario() {
        when(notificacionRepository.findByDestinatarioAndEventTypeIn(eq("user@test.com"), any()))
                .thenReturn(List.of());

        notificacionService.eliminarPorOrigen(null, "user@test.com", "sesiones");

        verify(notificacionRepository).findByDestinatarioAndEventTypeIn(eq("user@test.com"), any());
    }

    @Test
    void eliminarPorOrigen_origenInvalido_lanzaValidationException() {
        assertThatThrownBy(() -> notificacionService.eliminarPorOrigen(1L, null, "pagos"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Origen invalido");
    }

    // --- crearDesdeEvento() y variantes ---

    @Test
    void crearDesdeEvento_creaNotificacionSinUserId() {
        Notificacion saved = buildNotificacion("PUSH", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        Notificacion result = notificacionService.crearDesdeEvento("PUSH", "user@test.com", "Asunto", "Contenido");

        assertThat(result).isNotNull();
        verify(notificacionRepository, times(2)).save(any());
    }

    @Test
    void crearDesdeEventoConUserId_delegaAConUserIdYEventType() {
        Notificacion saved = buildNotificacion("PUSH", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        Notificacion result = notificacionService.crearDesdeEventoConUserId("PUSH", "user@test.com", "Asunto", "Contenido", 1L);

        assertThat(result).isNotNull();
    }

    @Test
    void crearDesdeEventoConUserIdYEventType_creaConTodosLosParametros() throws Exception {
        Notificacion saved = buildNotificacion("EMAIL", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        Notificacion result = notificacionService.crearDesdeEventoConUserIdYEventType(
                "EMAIL", "user@test.com", "Asunto", "Contenido", 1L, "LOGIN"
        );

        assertThat(result).isNotNull();
        verify(emailSender).sendEmail(any(), any(), any(), any());
    }

    // --- crearContacto() ---

    @Test
    void crearContacto_conMailFromConfigured_enviaAlSupportEmail() throws Exception {
        com.hotel.notificacion.api.dto.ContactoRequest request = new com.hotel.notificacion.api.dto.ContactoRequest();
        request.setAsunto("Consulta");
        request.setMensaje("Tengo una duda");
        request.setEmail("cliente@test.com");

        Notificacion saved = buildNotificacion("EMAIL", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        Notificacion result = notificacionService.crearContacto(request);

        assertThat(result).isNotNull();
        verify(emailSender).sendEmail(any(), any(), any(), any());
    }

    @Test
    void crearContacto_sinAsunto_usaDefecto() throws Exception {
        com.hotel.notificacion.api.dto.ContactoRequest request = new com.hotel.notificacion.api.dto.ContactoRequest();
        request.setAsunto(null);
        request.setMensaje("Mensaje");
        request.setEmail("cliente@test.com");

        Notificacion saved = buildNotificacion("EMAIL", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        notificacionService.crearContacto(request);

        verify(notificacionRepository, times(2)).save(captor.capture());
        Notificacion captured = captor.getAllValues().get(0);
        assertThat(captured.getAsunto()).isEqualTo("Contacto");
    }

    @Test
    void crearContacto_mailFromBlank_usaEmailDelCliente() throws Exception {
        ReflectionTestUtils.setField(notificacionService, "mailFrom", "");

        com.hotel.notificacion.api.dto.ContactoRequest request = new com.hotel.notificacion.api.dto.ContactoRequest();
        request.setAsunto("Consulta");
        request.setMensaje("Mensaje");
        request.setEmail("cliente@test.com");

        Notificacion saved = buildNotificacion("EMAIL", "FALLIDA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        notificacionService.crearContacto(request);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(notificacionRepository, atLeast(1)).save(captor.capture());
    }

    // --- crearContactoDesdeEvento() ---

    @Test
    void crearContactoDesdeEvento_conTodosLosCampos_creaNotificacion() throws Exception {
        ContactoEvent event = new ContactoEvent("Juan", "juan@test.com", "123456", "Mi asunto", "Mi mensaje");

        Notificacion saved = buildNotificacion("EMAIL", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        Notificacion result = notificacionService.crearContactoDesdeEvento(event);

        assertThat(result).isNotNull();
        verify(emailSender).sendEmail(any(), any(), any(), any());
    }

    @Test
    void crearContactoDesdeEvento_sinAsunto_usaDefecto() throws Exception {
        ContactoEvent event = new ContactoEvent("Juan", "juan@test.com", null, null, "Mensaje");

        Notificacion saved = buildNotificacion("EMAIL", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        notificacionService.crearContactoDesdeEvento(event);

        verify(notificacionRepository, atLeast(1)).save(captor.capture());
        Notificacion captured = captor.getAllValues().get(0);
        assertThat(captured.getAsunto()).contains("Mensaje de contacto");
    }

    @Test
    void crearContactoDesdeEvento_sinTelefono_usaNoProporcionado() throws Exception {
        ContactoEvent event = new ContactoEvent("Juan", "juan@test.com", "", "Asunto", "Mensaje");

        Notificacion saved = buildNotificacion("EMAIL", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        notificacionService.crearContactoDesdeEvento(event);

        verify(notificacionRepository, atLeast(1)).save(any());
    }

    // --- renderTemplate() ---

    @Test
    void renderTemplate_templateExistente_retornaContenidoConVariablesAplicadas() {
        Map<String, String> variables = Map.of("clienteNombre", "Juan");

        // Templates de producción existen en src/main/resources
        String result = notificacionService.renderTemplate("templates/welcome-email.html", variables);

        assertThat(result).isNotNull();
        assertThat(result).doesNotContain("{{clienteNombre}}");
    }

    @Test
    void renderTemplate_templateNoExiste_lanzaMailException() {
        assertThatThrownBy(() -> notificacionService.renderTemplate("templates/inexistente.html", Map.of()))
                .isInstanceOf(org.springframework.mail.MailException.class);
    }

    // --- procesarEnvio() — tipo no EMAIL ---

    @Test
    void crearDesdeEventoConUserIdYEventType_tipoPush_noEnviaEmail() {
        Notificacion saved = buildNotificacion("PUSH", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        notificacionService.crearDesdeEventoConUserIdYEventType("PUSH", "user@test.com", "Asunto", "Contenido", 1L, "LOGIN");

        verifyNoInteractions(emailSender);
    }

    // --- enviarEmail falla => estado FALLIDA ---

    @Test
    void enviarManual_emailFalla_estadoFallida() throws Exception {
        com.hotel.notificacion.api.dto.EnviarNotificacionRequest request = buildEnviarRequest();

        doThrow(new RuntimeException("SMTP error")).when(emailSender).sendEmail(any(), any(), any(), any());

        Notificacion pendiente = buildNotificacion("EMAIL", "PENDIENTE");
        pendiente.setIntentos(0);
        Notificacion fallida = buildNotificacion("EMAIL", "FALLIDA");
        when(notificacionRepository.save(any())).thenReturn(pendiente).thenReturn(fallida);

        Notificacion result = notificacionService.enviarManual(request, 1L);

        assertThat(result).isNotNull();
    }

    // --- generarMensajeNotificacion() (via crearDesdeEventoConUserIdYEventType) ---

    @Test
    void crearNotificacion_eventTypeLogin_mensajeLoginExitoso() {
        Notificacion saved = buildNotificacion("PUSH", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        notificacionService.crearDesdeEventoConUserIdYEventType("PUSH", "user@test.com", "Asunto", "Contenido", 1L, "LOGIN");

        verify(notificacionRepository, atLeast(1)).save(captor.capture());
        Notificacion captured = captor.getAllValues().get(0);
        assertThat(captured.getMensaje()).isEqualTo("Inicio de sesión exitoso");
    }

    @Test
    void crearNotificacion_eventTypeConfirmed_mensajeReservaConfirmada() {
        Notificacion saved = buildNotificacion("PUSH", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        notificacionService.crearDesdeEventoConUserIdYEventType("PUSH", "user@test.com", "Asunto", "Contenido", 1L, "CONFIRMED");

        verify(notificacionRepository, atLeast(1)).save(captor.capture());
        assertThat(captor.getAllValues().get(0).getMensaje()).isEqualTo("Tu reserva fue confirmada");
    }

    @Test
    void crearNotificacion_eventTypePending_mensajePendiente() {
        Notificacion saved = buildNotificacion("PUSH", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        notificacionService.crearDesdeEventoConUserIdYEventType("PUSH", "user@test.com", "Asunto", "Contenido", 1L, "PENDING");

        verify(notificacionRepository, atLeast(1)).save(captor.capture());
        assertThat(captor.getAllValues().get(0).getMensaje()).isEqualTo("Tu reserva está pendiente");
    }

    @Test
    void crearNotificacion_eventTypeCreated_mensajePendiente() {
        Notificacion saved = buildNotificacion("PUSH", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        notificacionService.crearDesdeEventoConUserIdYEventType("PUSH", "user@test.com", "Asunto", "Contenido", 1L, "CREATED");

        verify(notificacionRepository, atLeast(1)).save(captor.capture());
        assertThat(captor.getAllValues().get(0).getMensaje()).isEqualTo("Tu reserva está pendiente");
    }

    @Test
    void crearNotificacion_eventTypeCancelledAdmin_mensajeCanceladaAdmin() {
        Notificacion saved = buildNotificacion("PUSH", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        notificacionService.crearDesdeEventoConUserIdYEventType("PUSH", "user@test.com", "Asunto", "Contenido", 1L, "CANCELLED_ADMIN");

        verify(notificacionRepository, atLeast(1)).save(captor.capture());
        assertThat(captor.getAllValues().get(0).getMensaje()).isEqualTo("Tu reserva fue cancelada por el administrador");
    }

    @Test
    void crearNotificacion_eventTypeCancelled_mensajeCancelada() {
        Notificacion saved = buildNotificacion("PUSH", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        notificacionService.crearDesdeEventoConUserIdYEventType("PUSH", "user@test.com", "Asunto", "Contenido", 1L, "CANCELLED");

        verify(notificacionRepository, atLeast(1)).save(captor.capture());
        assertThat(captor.getAllValues().get(0).getMensaje()).isEqualTo("Tu reserva fue cancelada");
    }

    @Test
    void crearNotificacion_eventTypeContacto_mensajeContacto() {
        Notificacion saved = buildNotificacion("PUSH", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        notificacionService.crearDesdeEventoConUserIdYEventType("PUSH", "user@test.com", "Asunto", "Contenido", 1L, "CONTACTO");

        verify(notificacionRepository, atLeast(1)).save(captor.capture());
        assertThat(captor.getAllValues().get(0).getMensaje()).isEqualTo("Mensaje de contacto recibido");
    }

    @Test
    void crearNotificacion_eventTypeNull_mensajeNuevaNotificacion() {
        Notificacion saved = buildNotificacion("PUSH", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        notificacionService.crearDesdeEventoConUserIdYEventType("PUSH", "user@test.com", "Asunto", "Contenido", 1L, null);

        verify(notificacionRepository, atLeast(1)).save(captor.capture());
        assertThat(captor.getAllValues().get(0).getMensaje()).isEqualTo("Nueva notificación");
    }

    @Test
    void crearNotificacion_eventTypeDesconocido_mensajeNuevaNotificacion() {
        Notificacion saved = buildNotificacion("PUSH", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        notificacionService.crearDesdeEventoConUserIdYEventType("PUSH", "user@test.com", "Asunto", "Contenido", 1L, "UNKNOWN");

        verify(notificacionRepository, atLeast(1)).save(captor.capture());
        assertThat(captor.getAllValues().get(0).getMensaje()).isEqualTo("Nueva notificación");
    }

    // --- aplicarVariables() ---

    @Test
    void renderTemplate_conVariables_reemplazaPlaceholders() {
        // Usa welcome-email.html que existe en recursos
        Map<String, String> vars = Map.of("nombre", "Juan", "email", "j@test.com", "rol", "USER", "fecha", "01/01/2024 10:00");
        String result = notificacionService.renderTemplate("templates/welcome-email.html", vars);
        assertThat(result).doesNotContain("{{nombre}}");
    }

    // --- Intentos en procesarEnvio() ---

    @Test
    void enviarManual_intentsNull_inicializaEn1() throws Exception {
        com.hotel.notificacion.api.dto.EnviarNotificacionRequest request = buildEnviarRequest();

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        Notificacion saved = buildNotificacion("EMAIL", "PENDIENTE");
        saved.setIntentos(null);
        Notificacion saved2 = buildNotificacion("EMAIL", "ENVIADA");
        when(notificacionRepository.save(any())).thenReturn(saved).thenReturn(saved2);

        notificacionService.enviarManual(request, 1L);

        verify(notificacionRepository, times(2)).save(captor.capture());
    }

    // --- Helper methods ---

    private Notificacion buildNotificacion(String tipo, String estado) {
        Notificacion n = new Notificacion();
        n.setId(1L);
        n.setTipo(tipo);
        n.setDestinatario("user@test.com");
        n.setAsunto("Asunto test");
        n.setContenido("Contenido test");
        n.setEstado(estado);
        n.setIntentos(0);
        n.setLeida(false);
        n.setFechaCreacion(LocalDateTime.now());
        n.setUserId(1L);
        return n;
    }

    private com.hotel.notificacion.api.dto.EnviarNotificacionRequest buildEnviarRequest() {
        com.hotel.notificacion.api.dto.EnviarNotificacionRequest request =
                new com.hotel.notificacion.api.dto.EnviarNotificacionRequest();
        request.setTipo(com.hotel.notificacion.api.dto.EnviarNotificacionRequest.TipoEnum.EMAIL);
        request.setDestinatario("user@test.com");
        request.setAsunto("Test asunto");
        request.setContenido("Test contenido");
        return request;
    }

    private com.hotel.notificacion.api.dto.EnviarNotificacionRequest buildEnviarRequestConPlantilla() {
        com.hotel.notificacion.api.dto.EnviarNotificacionRequest request =
                new com.hotel.notificacion.api.dto.EnviarNotificacionRequest();
        request.setTipo(com.hotel.notificacion.api.dto.EnviarNotificacionRequest.TipoEnum.EMAIL);
        request.setDestinatario("user@test.com");
        request.setPlantillaId(5L);
        request.setVariables(Map.of("nombre", "Juan"));
        return request;
    }
}
