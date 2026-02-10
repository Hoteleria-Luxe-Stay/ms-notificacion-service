package com.hotel.notificacion.core.notificacion.service;

import com.hotel.notificacion.api.dto.ContactoRequest;
import com.hotel.notificacion.api.dto.EnviarNotificacionRequest;
import com.hotel.notificacion.core.notificacion.model.Notificacion;
import com.hotel.notificacion.core.notificacion.repository.NotificacionRepository;
import com.hotel.notificacion.core.plantilla.model.Plantilla;
import com.hotel.notificacion.core.plantilla.repository.PlantillaRepository;
import com.hotel.notificacion.helpers.exceptions.EntityNotFoundException;
import com.hotel.notificacion.helpers.exceptions.ValidationException;
import com.klab.email.EmailSender;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
public class NotificacionService {

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_ENVIADA = "ENVIADA";
    private static final String ESTADO_FALLIDA = "FALLIDA";

    private final NotificacionRepository notificacionRepository;
    private final PlantillaRepository plantillaRepository;
    private final EmailSender emailSender;

    @Value("${klab.mail.from-email:}")
    private String mailFrom;

    public NotificacionService(NotificacionRepository notificacionRepository,
                               PlantillaRepository plantillaRepository,
                               EmailSender emailSender) {
        this.notificacionRepository = notificacionRepository;
        this.plantillaRepository = plantillaRepository;
        this.emailSender = emailSender;
    }

    public List<Notificacion> listar(String tipo, String estado, LocalDate fechaDesde, LocalDate fechaHasta) {
        if (tipo != null && !tipo.isBlank()) {
            return notificacionRepository.findByTipo(tipo);
        }
        if (estado != null && !estado.isBlank()) {
            return notificacionRepository.findByEstado(estado);
        }
        if (fechaDesde != null && fechaHasta != null) {
            LocalDateTime desde = fechaDesde.atStartOfDay();
            LocalDateTime hasta = LocalDateTime.of(fechaHasta, LocalTime.MAX);
            return notificacionRepository.findByFechaCreacionBetween(desde, hasta);
        }
        return notificacionRepository.findAll();
    }

    public Notificacion buscarPorId(Long id) {
        return notificacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notificacion", id));
    }

    public Notificacion enviarManual(EnviarNotificacionRequest request, Long userId) {
        String contenido = request.getContenido();
        String asunto = request.getAsunto();

        if (request.getPlantillaId() != null) {
            Plantilla plantilla = plantillaRepository.findById(request.getPlantillaId())
                    .orElseThrow(() -> new EntityNotFoundException("Plantilla", request.getPlantillaId()));
            asunto = plantilla.getAsunto();
            contenido = aplicarVariables(plantilla.getContenido(), request.getVariables());
        }

        Notificacion notificacion = crearNotificacionBase(
                String.valueOf(request.getTipo()),
                request.getDestinatario(),
                asunto,
                contenido,
                userId
        );

        notificacion = notificacionRepository.save(notificacion);
        procesarEnvio(notificacion);
        return notificacionRepository.save(notificacion);
    }

    public Notificacion reenviar(Long id) {
        Notificacion notificacion = buscarPorId(id);
        if (!ESTADO_FALLIDA.equals(notificacion.getEstado())) {
            throw new ValidationException("estado", "Solo se pueden reenviar notificaciones fallidas");
        }
        procesarEnvio(notificacion);
        return notificacionRepository.save(notificacion);
    }

    public List<Notificacion> listarPorUsuario(Long userId, String email, Boolean leida, String tipo) {
        List<Notificacion> notificaciones = userId != null
                ? notificacionRepository.findByUserId(userId)
                : notificacionRepository.findByDestinatario(email);

        if (tipo != null && !tipo.isBlank()) {
            notificaciones = notificaciones.stream()
                    .filter(n -> tipo.equalsIgnoreCase(n.getTipo()))
                    .toList();
        }

        if (leida != null) {
            notificaciones = notificaciones.stream()
                    .filter(notificacion -> Boolean.TRUE.equals(notificacion.getLeida()) == leida)
                    .toList();
        }
        return notificaciones;
    }

    public void marcarComoLeida(Long id, Long userId, String email) {
        Notificacion notificacion = buscarPorId(id);
        if (!esNotificacionDelUsuario(notificacion, userId, email)) {
            throw new EntityNotFoundException("Notificacion", id);
        }
        notificacion.setLeida(true);
        notificacionRepository.save(notificacion);
    }

    public void marcarTodasComoLeidas(Long userId, String email) {
        List<Notificacion> notificaciones = listarPorUsuario(userId, email, null, null);
        notificaciones.forEach(notificacion -> notificacion.setLeida(true));
        notificacionRepository.saveAll(notificaciones);
    }

    public Notificacion crearDesdeEvento(String tipo, String destinatario, String asunto, String contenido) {
        return crearDesdeEventoConUserId(tipo, destinatario, asunto, contenido, null);
    }

    public Notificacion crearDesdeEventoConUserId(String tipo, String destinatario, String asunto, String contenido, Long userId) {
        Notificacion notificacion = crearNotificacionBase(tipo, destinatario, asunto, contenido, userId);
        notificacion = notificacionRepository.save(notificacion);
        procesarEnvio(notificacion);
        return notificacionRepository.save(notificacion);
    }

    public Notificacion crearContacto(ContactoRequest request) {
        String asunto = request.getAsunto() != null ? request.getAsunto() : "Contacto";
        String contenido = request.getMensaje();
        String destinatario = mailFrom == null || mailFrom.isBlank() ? request.getEmail() : mailFrom;

        Notificacion notificacion = crearNotificacionBase("EMAIL", destinatario, asunto, contenido, null);
        notificacion = notificacionRepository.save(notificacion);
        procesarEnvio(notificacion);
        return notificacionRepository.save(notificacion);
    }

    public String renderTemplate(String templatePath, Map<String, String> variables) {
        try {
            ClassPathResource resource = new ClassPathResource(templatePath);
            String template = new String(resource.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            return aplicarVariables(template, variables);
        } catch (Exception e) {
            throw new MailException("TEMPLATE_NOT_FOUND") {};
        }
    }

    private Notificacion crearNotificacionBase(String tipo, String destinatario, String asunto, String contenido, Long userId) {
        Notificacion notificacion = new Notificacion();
        notificacion.setTipo(tipo);
        notificacion.setDestinatario(destinatario);
        notificacion.setAsunto(asunto);
        notificacion.setContenido(contenido);
        notificacion.setEstado(ESTADO_PENDIENTE);
        notificacion.setFechaCreacion(LocalDateTime.now());
        notificacion.setIntentos(0);
        notificacion.setUserId(userId);
        notificacion.setLeida(false);
        return notificacion;
    }

    private void procesarEnvio(Notificacion notificacion) {
        notificacion.setIntentos(notificacion.getIntentos() == null ? 1 : notificacion.getIntentos() + 1);

        if ("EMAIL".equalsIgnoreCase(notificacion.getTipo())) {
            try {
                enviarEmail(notificacion.getDestinatario(), notificacion.getAsunto(), notificacion.getContenido());
                notificacion.setEstado(ESTADO_ENVIADA);
                notificacion.setFechaEnvio(LocalDateTime.now());
                notificacion.setErrorMensaje(null);
            } catch (Exception e) {
                notificacion.setEstado(ESTADO_FALLIDA);
                notificacion.setErrorMensaje(e.getMessage());
            }
            return;
        }

        notificacion.setEstado(ESTADO_ENVIADA);
        notificacion.setFechaEnvio(LocalDateTime.now());
    }

    private void enviarEmail(String destinatario, String asunto, String contenido) throws MessagingException {
        if (mailFrom == null || mailFrom.isBlank()) {
            throw new MailException("MAIL_CONFIG_MISSING") {};
        }
        try {
            emailSender.sendEmail(contenido, destinatario, asunto, null);
        } catch (Exception e) {
            throw new MailException(e.getMessage()) {};
        }
    }

    private String aplicarVariables(String template, Map<String, String> variables) {
        if (template == null || variables == null || variables.isEmpty()) {
            return template;
        }

        String contenido = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String clave = "{{" + entry.getKey() + "}}";
            contenido = contenido.replace(clave, entry.getValue());
        }
        return contenido;
    }

    private boolean esNotificacionDelUsuario(Notificacion notificacion, Long userId, String email) {
        if (userId != null && userId.equals(notificacion.getUserId())) {
            return true;
        }
        return email != null && email.equalsIgnoreCase(notificacion.getDestinatario());
    }
}
