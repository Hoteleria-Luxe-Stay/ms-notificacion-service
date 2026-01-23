package com.hotel.notificacion.core.notificacion.repository;

import com.hotel.notificacion.core.notificacion.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByTipo(String tipo);

    List<Notificacion> findByEstado(String estado);

    List<Notificacion> findByFechaCreacionBetween(LocalDateTime desde, LocalDateTime hasta);

    List<Notificacion> findByUserId(Long userId);

    List<Notificacion> findByDestinatario(String destinatario);
}
