package com.hotel.notificacion.core.plantilla.repository;

import com.hotel.notificacion.core.plantilla.model.Plantilla;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlantillaRepository extends JpaRepository<Plantilla, Long> {

    Optional<Plantilla> findByCodigo(String codigo);
}
