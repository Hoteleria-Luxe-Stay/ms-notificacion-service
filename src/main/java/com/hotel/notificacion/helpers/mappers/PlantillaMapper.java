package com.hotel.notificacion.helpers.mappers;

import com.hotel.notificacion.api.dto.PlantillaResponse;
import com.hotel.notificacion.core.plantilla.model.Plantilla;

import java.time.ZoneOffset;

public class PlantillaMapper {

    private PlantillaMapper() {
        throw new UnsupportedOperationException("This class should never be instantiated");
    }

    public static PlantillaResponse toResponse(Plantilla plantilla) {
        if (plantilla == null) {
            return null;
        }

        PlantillaResponse response = new PlantillaResponse();
        response.setId(plantilla.getId());
        response.setCodigo(plantilla.getCodigo());
        response.setNombre(plantilla.getNombre());
        response.setAsunto(plantilla.getAsunto());
        response.setContenido(plantilla.getContenido());
        response.setTipo(plantilla.getTipo());
        response.setActiva(Boolean.TRUE.equals(plantilla.getActiva()));
        if (plantilla.getFechaCreacion() != null) {
            response.setFechaCreacion(plantilla.getFechaCreacion().atOffset(ZoneOffset.UTC));
        }
        if (plantilla.getFechaModificacion() != null) {
            response.setFechaModificacion(plantilla.getFechaModificacion().atOffset(ZoneOffset.UTC));
        }
        return response;
    }
}
