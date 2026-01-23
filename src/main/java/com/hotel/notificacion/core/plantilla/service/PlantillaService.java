package com.hotel.notificacion.core.plantilla.service;

import com.hotel.notificacion.api.dto.PlantillaRequest;
import com.hotel.notificacion.core.plantilla.model.Plantilla;
import com.hotel.notificacion.core.plantilla.repository.PlantillaRepository;
import com.hotel.notificacion.helpers.exceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlantillaService {

    private final PlantillaRepository plantillaRepository;

    public PlantillaService(PlantillaRepository plantillaRepository) {
        this.plantillaRepository = plantillaRepository;
    }

    public List<Plantilla> listar() {
        return plantillaRepository.findAll();
    }

    public Plantilla buscarPorId(Long id) {
        return plantillaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plantilla", id));
    }

    public Plantilla crear(PlantillaRequest request) {
        Plantilla plantilla = new Plantilla();
        plantilla.setCodigo(request.getCodigo());
        plantilla.setNombre(request.getNombre());
        plantilla.setAsunto(request.getAsunto());
        plantilla.setContenido(request.getContenido());
        plantilla.setTipo(String.valueOf(request.getTipo()));
        plantilla.setActiva(request.getActiva() == null ? Boolean.TRUE : request.getActiva());
        plantilla.setFechaCreacion(LocalDateTime.now());
        plantilla.setFechaModificacion(LocalDateTime.now());
        return plantillaRepository.save(plantilla);
    }

    public Plantilla actualizar(Long id, PlantillaRequest request) {
        Plantilla plantilla = buscarPorId(id);
        plantilla.setCodigo(request.getCodigo());
        plantilla.setNombre(request.getNombre());
        plantilla.setAsunto(request.getAsunto());
        plantilla.setContenido(request.getContenido());
        plantilla.setTipo(String.valueOf(request.getTipo()));
        plantilla.setActiva(request.getActiva() == null ? plantilla.getActiva() : request.getActiva());
        plantilla.setFechaModificacion(LocalDateTime.now());
        return plantillaRepository.save(plantilla);
    }

    public void eliminar(Long id) {
        if (!plantillaRepository.existsById(id)) {
            throw new EntityNotFoundException("Plantilla", id);
        }
        plantillaRepository.deleteById(id);
    }
}
